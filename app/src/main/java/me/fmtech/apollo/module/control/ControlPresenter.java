package me.fmtech.apollo.module.control;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;

import com.google.gson.Gson;
import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.action.MoveDirection;
import com.slamtec.slamware.geometry.Size;
import com.slamtec.slamware.robot.CompositeMap;
import com.slamtec.slamware.robot.Location;
import com.slamtec.slamware.robot.Map;
import com.slamtec.slamware.robot.MapKind;
import com.slamtec.slamware.robot.MapType;
import com.slamtec.slamware.sdp.CompositeMapHelper;

import org.reactivestreams.Publisher;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.subscribers.ResourceSubscriber;
import me.fmtech.apollo.R;
import me.fmtech.apollo.app.App;
import me.fmtech.apollo.app.Constants;
import me.fmtech.apollo.base.RxPresenter;
import me.fmtech.apollo.component.PriorityScheduler;
import me.fmtech.apollo.model.bean.BaseHttpResponse;
import me.fmtech.apollo.model.bean.VolumeBean;
import me.fmtech.apollo.model.http.HttpHelper;
import me.fmtech.apollo.model.http.RetrofitHelper;
import me.fmtech.apollo.module.control.ControlContract.Presenter;
import me.fmtech.apollo.module.control.ControlContract.View;
import me.fmtech.apollo.utils.RxUtil;
import retrofit2.Response;

import static me.fmtech.apollo.module.task.list.TaskListPresenter.TASK_CANCEL_URL;

public class ControlPresenter extends RxPresenter<View> implements Presenter {
    private static final String TAG = ControlPresenter.class.getName();
    private AbstractSlamwarePlatform mPlatform;
    private HttpHelper httpHelper;
    private static final String VOLUME_URL = Constants.BASE_URL + "volume";
    private static final String POWER_OFF_URL = Constants.BASE_URL + "exec/poweroff";

    @Inject
    ControlPresenter(RetrofitHelper httpHelper) {
        this.httpHelper = httpHelper;
        connect();
        getBattery();
    }

    @Override
    public void connect() {
        addSubscribe(
                Flowable.just(1)
                        .subscribeOn(PriorityScheduler.get().priority(5))
                        .flatMap(new Function<Integer, Publisher<Integer>>() {
                            @Override
                            public Publisher<Integer> apply(Integer integer) throws Exception {
                                mPlatform = App.getSlamwarePlatform();
                                return Flowable.just(integer);
                            }
                        }).compose(RxUtil.<Integer>rxSchedulerHelper())
                        .subscribeWith(new ResourceSubscriber<Object>() {
                            @Override
                            public void onNext(Object o) {
                            }

                            @Override
                            public void onError(Throwable t) {
                                t.printStackTrace();
                                if (mView != null) {
                                    mView.connected(mPlatform != null);
                                }
                            }

                            @Override
                            public void onComplete() {
                                if (mView != null) {
                                    mView.connected(mPlatform != null);
                                }
                            }
                        })
        );
    }

    public void getBattery() {
        addSubscribe(
                Flowable.interval(1, 10, TimeUnit.SECONDS)
                        .subscribeOn(PriorityScheduler.get().priority(5))
                        .flatMap(new Function<Long, Publisher<Integer>>() {
                            @Override
                            public Publisher<Integer> apply(Long integer) throws Exception {
                                if (mPlatform != null) {
                                    return Flowable.just(mPlatform.getBatteryPercentage());
                                }

                                return Flowable.just(0);
                            }
                        }).compose(RxUtil.<Integer>rxSchedulerHelper())
                        .onErrorReturnItem(0)
                        .subscribeWith(new ResourceSubscriber<Integer>() {
                            @Override
                            public void onNext(Integer i) {
                                if (mView != null) {
                                    mView.battery(i);
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                t.printStackTrace();
                            }

                            @Override
                            public void onComplete() {
                            }
                        })
        );
    }

    public void getMap() {
        addSubscribe(
                Flowable.interval(1, 10, TimeUnit.SECONDS)
                        .subscribeOn(PriorityScheduler.get().priority(5))
                        .flatMap(new Function<Long, Publisher<Bitmap>>() {
                            @Override
                            public Publisher<Bitmap> apply(Long integer) throws Exception {
                                if (null != mPlatform) {
                                    try {
                                        RectF rectF = mPlatform.getKnownArea(MapType.BITMAP_8BIT, MapKind.EXPLORE_MAP);
                                        Map map = mPlatform.getMap(MapType.BITMAP_8BIT, MapKind.EXPLORE_MAP, rectF);

                                        return Flowable.just(create(map.getDimension(), map.getData()));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                return Flowable.empty();
                            }
                        }).compose(RxUtil.<Bitmap>rxSchedulerHelper())
                        .subscribeWith(new ResourceSubscriber<Bitmap>() {
                            @Override
                            public void onNext(Bitmap bitmap) {
                                if (mView != null) {
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                t.printStackTrace();
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

    private Bitmap create(Size size, byte[] d) {
        int w = size.getWidth();
        int h = size.getHeight();
        Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        if (null == d) {
            return b;
        }

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int index = i * w + j;
                if (index >= d.length) {
                    break;
                }

                int p = d[index] + 127;
                int pixel = p;
                pixel += p << 8;
                pixel += p << 16;
                b.setPixel(i, j, pixel | 0xFF000000);
            }
        }

        return rotateBitmap(b, 270);
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void reConnect() {
        addSubscribe(
                Flowable.just(1)
                        .subscribeOn(PriorityScheduler.get().priority(5))
                        .flatMap(new Function<Integer, Publisher<Integer>>() {
                            @Override
                            public Publisher<Integer> apply(Integer integer) throws Exception {
                                mPlatform = App.reConnect();
                                return Flowable.empty();
                            }
                        }).compose(RxUtil.<Integer>rxSchedulerHelper())
                        .subscribeWith(new ResourceSubscriber<Integer>() {
                            @Override
                            public void onNext(Integer integer) {

                            }

                            @Override
                            public void onError(Throwable t) {
                                if (mView != null) {
                                    mView.connected(mPlatform != null);
                                }
                            }

                            @Override
                            public void onComplete() {
                                if (mView != null) {
                                    mView.connected(mPlatform != null);
                                }
                            }
                        })
        );
    }

    @Override
    public void disconnect() {
        addSubscribe(
                Flowable.just(1)
                        .subscribeOn(PriorityScheduler.get().priority(5))
                        .flatMap(new Function<Integer, Publisher<Integer>>() {
                            @Override
                            public Publisher<Integer> apply(Integer integer) throws Exception {
                                if (null != mPlatform) {
                                    mPlatform.disconnect();
                                }

                                return Flowable.empty();
                            }
                        }).subscribeWith(new ResourceSubscriber<Integer>() {
                    @Override
                    public void onNext(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
    }

    @Override
    public void goHome() {
        addSubscribe(
                httpHelper.post(TASK_CANCEL_URL, new HashMap<String, Object>(), new HashMap<String, Object>())
                        .compose(RxUtil.<Response<String>>rxSchedulerHelper())
                        .flatMap(new Function<Response<String>, Publisher<Integer>>() {
                            @Override
                            public Publisher<Integer> apply(Response<String> r) throws Exception {
                                if (null != mPlatform) {
                                    mPlatform.goHome();
                                }

                                return Flowable.empty();
                            }
                        }).subscribeWith(new ResourceSubscriber<Integer>() {
                    @Override
                    public void onNext(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable t) {
                        if (mView != null) {
                            mView.showErrorMsg(App.getInstance().getString(R.string.cmd_error));
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
    }

    @Override
    public void getLocation() {
        addSubscribe(
                Flowable.just(1)
                        .subscribeOn(PriorityScheduler.get().priority(5))
                        .flatMap(new Function<Integer, Publisher<Location>>() {
                            @Override
                            public Publisher<Location> apply(Integer integer) throws Exception {
                                if (null != mPlatform) {
                                    return Flowable.just(mPlatform.getLocation());
                                }

                                return Flowable.empty();
                            }
                        }).compose(RxUtil.<Location>rxSchedulerHelper())
                        .subscribeWith(new ResourceSubscriber<Location>() {
                            @Override
                            public void onNext(Location location) {
                                if (mView != null) {
                                    mView.showLocation(location);
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                t.printStackTrace();
                                if (mView != null) {
                                    mView.showErrorMsg(App.getInstance().getString(R.string.get_location_error));
                                }
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

    @Override
    public void turnLeft() {
        moveBy(MoveDirection.TURN_LEFT);
    }

    @Override
    public void turnRight() {
        moveBy(MoveDirection.TURN_RIGHT);
    }

    @Override
    public void moveForward() {
        moveBy(MoveDirection.FORWARD);
    }

    @Override
    public void moveBack() {
        moveBy(MoveDirection.BACKWARD);
    }

    private Disposable disposable;

    private void moveBy(final MoveDirection action) {
        cancelMove();
        disposable = Flowable.interval(0, 200, TimeUnit.MILLISECONDS)
                .subscribeOn(PriorityScheduler.get().priority(5))
                .flatMap(new Function<Long, Publisher<Long>>() {
                    @Override
                    public Publisher<Long> apply(Long num) throws Exception {
                        if (null != mPlatform) {
                            mPlatform.moveBy(action);
                        }

                        return Flowable.just(num);
                    }
                }).compose(RxUtil.<Long>rxSchedulerHelper())
                .subscribeWith(new ResourceSubscriber<Long>() {
                    @Override
                    public void onNext(Long o) {
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                        if (mView != null) {
                            mView.showErrorMsg(App.getInstance().getString(R.string.cmd_error));
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        addSubscribe(disposable);
    }

    public void cancelMove() {
        if (null != disposable && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

    public void saveMap(final String filePath) {
        addSubscribe(
                Flowable.just(1)
                        .subscribeOn(PriorityScheduler.get().priority(5))
                        .flatMap(new Function<Integer, Publisher<Integer>>() {
                            @Override
                            public Publisher<Integer> apply(Integer integer) throws Exception {
                                String name = filePath + "/fm.stcm";
                                CompositeMap compositeMap = mPlatform.getCompositeMap();
                                CompositeMapHelper compositeMapHelper = new CompositeMapHelper();
                                compositeMapHelper.saveFile(name, compositeMap);

                                return Flowable.just(integer);
                            }
                        }).subscribeWith(new ResourceSubscriber<Integer>() {
                    @Override
                    public void onNext(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                        if (mView != null) {
                            mView.showErrorMsg(App.getInstance().getString(R.string.save_map_error));
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mView != null) {
                            mView.showErrorMsg(App.getInstance().getString(R.string.save_map_success));
                        }
                    }
                })
        );
    }

    @Override
    public void getVolume() {
        addSubscribe(
                httpHelper.get(VOLUME_URL, new HashMap<String, Object>())
                        .compose(RxUtil.<Response<String>>rxSchedulerHelper())
                        .subscribeWith(new ResourceSubscriber<Response<String>>() {
                            @Override
                            public void onNext(Response<String> r) {
                                VolumeBean volume = new Gson().fromJson(r.body(), VolumeBean.class);
                                if (volume != null && volume.isSuccess()) {
                                    if (mView != null) {
                                        mView.showVolume(volume.getVolume());
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                t.printStackTrace();
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

    @Override
    public void setVolume(int volume) {
        String url = VOLUME_URL + "?volume=" + volume;
        addSubscribe(
                httpHelper.put(url, new HashMap<String, Object>())
                        .compose(RxUtil.<Response<String>>rxSchedulerHelper())
                        .subscribeWith(new ResourceSubscriber<Response<String>>() {
                            @Override
                            public void onNext(Response<String> stringResponse) {

                            }

                            @Override
                            public void onError(Throwable t) {
                                t.printStackTrace();
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

    @Override
    public void powerOff() {
        addSubscribe(
                httpHelper.post(POWER_OFF_URL, new HashMap<String, Object>(), new HashMap<String, Object>())
                        .compose(RxUtil.<Response<String>>rxSchedulerHelper())
                        .flatMap(new Function<Response<String>, Publisher<Response<String>>>() {
                            @Override
                            public Publisher<Response<String>> apply(Response<String> r) throws Exception {
                                return Flowable.just(r).delay(5, TimeUnit.SECONDS);
                            }
                        })
                        .compose(RxUtil.<Response<String>>rxSchedulerHelper())
                        .subscribeWith(new ResourceSubscriber<Response<String>>() {
                            BaseHttpResponse response;

                            @Override
                            public void onNext(Response<String> r) {
                                if (r != null) {
                                    response = new Gson().fromJson(r.body(), BaseHttpResponse.class);
                                }
                            }

                            private void end() {
                                if (mView != null) {
                                    if (response != null && response.isSuccess()) {
                                        mView.powerOff(App.getInstance().getString(R.string.power_off_success));
                                    } else {
                                        mView.powerOff(App.getInstance().getString(R.string.power_off_error));
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                t.printStackTrace();
                                end();
                            }

                            @Override
                            public void onComplete() {
                                end();
                            }
                        })
        );
    }
}
