package me.fmtech.apollo.module.task.map;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.slamtec.slamware.robot.Location;

import org.reactivestreams.Publisher;

import java.io.File;
import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subscribers.ResourceSubscriber;
import me.fmtech.apollo.R;
import me.fmtech.apollo.app.App;
import me.fmtech.apollo.app.Constants;
import me.fmtech.apollo.base.RxPresenter;
import me.fmtech.apollo.model.bean.BaseHttpResponse;
import me.fmtech.apollo.model.http.HttpHelper;
import me.fmtech.apollo.model.http.RetrofitHelper;
import me.fmtech.apollo.module.task.map.MapSetContract.Presenter;
import me.fmtech.apollo.module.task.map.MapSetContract.View;
import me.fmtech.apollo.utils.RxUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody.Part;
import okhttp3.RequestBody;
import retrofit2.Response;

public class MapSetPresenter extends RxPresenter<View> implements Presenter {
    private static final String TAG = MapSetPresenter.class.getName();
    private HttpHelper httpHelper;
    private static final String SET_HOME_URL = Constants.BASE_URL + "home";
    private static final String UPLOAD_URL = Constants.BASE_URL + "map";

    @Inject
    MapSetPresenter(RetrofitHelper httpHelper) {
        this.httpHelper = httpHelper;
    }

    @Override
    public void setMap(String filename, Location loc) {
        if (TextUtils.isEmpty(filename) || !filename.endsWith(".stcm")) {
            if (mView != null) {
                mView.showMapStatus(App.getInstance().getString(R.string.map_set_error));
            }
            return;
        }

        File file = new File(filename);
        if (!file.exists()) {
            if (mView != null) {
                mView.showMapStatus(App.getInstance().getString(R.string.map_set_error));
            }
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        final Part postFile = Part.createFormData("file", file.getName(), requestFile);

        addSubscribe(
                setLocationFlowable(loc).filter(new Predicate<BaseHttpResponse>() {
                    @Override
                    public boolean test(BaseHttpResponse r) throws Exception {
                        return null != r && r.isSuccess();
                    }
                }).flatMap(new Function<BaseHttpResponse, Publisher<BaseHttpResponse>>() {
                    @Override
                    public Publisher<BaseHttpResponse> apply(BaseHttpResponse r) throws Exception {
                        return httpHelper.postFile(UPLOAD_URL, new HashMap<String, Object>(), postFile)
                                .compose(RxUtil.<Response<String>>rxSchedulerHelper())
                                .map(new Function<Response<String>, BaseHttpResponse>() {
                                    @Override
                                    public BaseHttpResponse apply(Response<String> r) throws Exception {
                                        if (null != r) {
                                            return new Gson().fromJson(r.body(), BaseHttpResponse.class);
                                        }
                                        return null;
                                    }
                                });
                    }
                }).subscribeWith(new ResourceSubscriber<BaseHttpResponse>() {
                    BaseHttpResponse response;

                    @Override
                    public void onNext(BaseHttpResponse r) {
                        response = r;
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                        showError();
                    }


                    @Override
                    public void onComplete() {
                        if (null != response && response.isSuccess()) {
                            showSuccess();
                            return;
                        }

                        showError();
                    }
                })
        );
    }

    private void showSuccess() {
        if (mView != null) {
            mView.showMapStatus("设置成功");
        }
    }

    private void showError() {
        if (mView != null) {
            mView.showMapStatus("设置失败");
        }
    }

    public Flowable<BaseHttpResponse> setLocationFlowable(Location loc) {
        String url = SET_HOME_URL + "?x=" + loc.getX() + "&y=" + loc.getY() + "&z=" + loc.getZ();

        return httpHelper.put(url, new HashMap<String, Object>())
                .compose(RxUtil.<Response<String>>rxSchedulerHelper())
                .map(new Function<Response<String>, BaseHttpResponse>() {
                    @Override
                    public BaseHttpResponse apply(Response<String> r) throws Exception {
                        if (null != r) {
                            return new Gson().fromJson(r.body(), BaseHttpResponse.class);
                        }
                        return null;
                    }
                });
    }

}
