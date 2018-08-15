package me.fmtech.apollo.module.task.upload;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.FileReader;
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
import me.fmtech.apollo.component.PriorityScheduler;
import me.fmtech.apollo.model.bean.BaseHttpResponse;
import me.fmtech.apollo.model.bean.TaskBean;
import me.fmtech.apollo.model.http.HttpHelper;
import me.fmtech.apollo.model.http.RetrofitHelper;
import me.fmtech.apollo.module.task.upload.TaskUploadContract.Presenter;
import me.fmtech.apollo.module.task.upload.TaskUploadContract.View;
import me.fmtech.apollo.utils.RxUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class TaskUploadPresenter extends RxPresenter<View> implements Presenter {
    private static final String TAG = TaskUploadPresenter.class.getName();
    private HttpHelper httpHelper;
    private static final String TASK_URL = Constants.BASE_URL + "tasks";
    private static final String UPLOAD_URL = Constants.BASE_URL + "upload";

    @Inject
    TaskUploadPresenter(RetrofitHelper httpHelper) {
        this.httpHelper = httpHelper;
    }

    public void loadTask(final String filename) {
        if (TextUtils.isEmpty(filename)) {
            return;
        }

        File file = new File(filename);
        if (!file.exists()) {
            return;
        }

        addSubscribe(
                Flowable.just(1).subscribeOn(PriorityScheduler.get().priority(4))
                        .flatMap(new Function<Integer, Publisher<TaskBean>>() {
                            @Override
                            public Publisher<TaskBean> apply(Integer integer) throws Exception {
                                return Flowable.just(new Gson().fromJson(new FileReader(filename), TaskBean.class));
                            }
                        }).compose(RxUtil.<TaskBean>rxSchedulerHelper())
                        .subscribeWith(new ResourceSubscriber<TaskBean>() {
                            @Override
                            public void onNext(TaskBean taskBean) {
                                mView.showTask(taskBean);
                            }

                            @Override
                            public void onError(Throwable t) {
                                if (mView != null) {
                                    mView.showErrorMsg(App.getInstance().getString(R.string.load_task_error));
                                }
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

    @Override
    public void setTask(final TaskBean task, final String musicFilepath) {
        addSubscribe(
                Flowable.just(1).flatMap(new Function<Integer, Publisher<BaseHttpResponse>>() {
                    @Override
                    public Publisher<BaseHttpResponse> apply(Integer integer) throws Exception {
                        if (!TextUtils.isEmpty(musicFilepath)) {
                            return getFileFlowable(musicFilepath)
                                    .compose(RxUtil.<Response<String>>rxSchedulerHelper())
                                    .map(new Function<Response<String>, BaseHttpResponse>() {
                                        @Override
                                        public BaseHttpResponse apply(Response<String> r) throws Exception {
                                            if (null != r) {
                                                return new Gson().fromJson(r.body(), BaseHttpResponse.class);
                                            }
                                            return null;
                                        }
                                    }).filter(new Predicate<BaseHttpResponse>() {
                                        @Override
                                        public boolean test(BaseHttpResponse r) throws Exception {
                                            return r != null && r.isSuccess();
                                        }
                                    });
                        }

                        return Flowable.just(new BaseHttpResponse());
                    }
                }).flatMap(new Function<BaseHttpResponse, Publisher<Response<String>>>() {
                    @Override
                    public Publisher<Response<String>> apply(BaseHttpResponse r) throws Exception {
                        return getTaskFlowable(task)
                                .compose(RxUtil.<Response<String>>rxSchedulerHelper());
                    }
                }).subscribeWith(new ResourceSubscriber<Response<String>>() {
                    BaseHttpResponse response;

                    @Override
                    public void onNext(Response<String> r) {
                        if (null != r) {
                            response = new Gson().fromJson(r.body(), BaseHttpResponse.class);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                        if (mView != null) {
                            mView.setTaskStatus(App.getInstance().getString(R.string.set_error));
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (mView != null) {
                            if (null != response && response.isSuccess()) {
                                mView.setTaskStatus(App.getInstance().getString(R.string.set_success));
                            } else {
                                mView.setTaskStatus(App.getInstance().getString(R.string.set_error));
                            }
                        }
                    }
                })
        );
    }

    private Flowable<Response<String>> getFileFlowable(String filePath) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        final MultipartBody.Part postFile = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        return httpHelper.postFile(UPLOAD_URL, new HashMap<String, Object>(), postFile);
    }

    private Flowable<Response<String>> getTaskFlowable(TaskBean task) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), task.toString());

        if (task.getId() > 0) {//edit
            return httpHelper.put(TASK_URL + "/" + task.getId(), new HashMap<String, Object>(), body);
        } else {//create
            return httpHelper.postBody(TASK_URL, new HashMap<String, Object>(), body);
        }
    }
}
