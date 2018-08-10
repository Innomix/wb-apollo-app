package me.fmtech.apollo.module.task.display;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.reactivestreams.Publisher;

import java.util.HashMap;

import javax.inject.Inject;

import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subscribers.ResourceSubscriber;
import me.fmtech.apollo.R;
import me.fmtech.apollo.app.App;
import me.fmtech.apollo.base.RxPresenter;
import me.fmtech.apollo.model.bean.BaseHttpResponse;
import me.fmtech.apollo.model.bean.TaskBean;
import me.fmtech.apollo.model.http.HttpHelper;
import me.fmtech.apollo.model.http.RetrofitHelper;
import me.fmtech.apollo.module.task.display.TaskDisplayContract.Presenter;
import me.fmtech.apollo.module.task.display.TaskDisplayContract.View;
import me.fmtech.apollo.utils.RxUtil;
import retrofit2.Response;

import static me.fmtech.apollo.module.task.list.TaskListPresenter.TASK_CANCEL_URL;
import static me.fmtech.apollo.module.task.list.TaskListPresenter.TASK_URL;

public class TaskDisplayPresenter extends RxPresenter<View> implements Presenter {
    private static final String TAG = TaskDisplayPresenter.class.getName();
    private HttpHelper httpHelper;

    @Inject
    TaskDisplayPresenter(RetrofitHelper httpHelper) {
        this.httpHelper = httpHelper;
    }

    public void loadTask(int taskid) {
        addSubscribe(
                httpHelper.get(TASK_URL + "/" + taskid, new HashMap<String, Object>())
                        .compose(RxUtil.<Response<String>>rxSchedulerHelper())
                        .map(new Function<Response<String>, TaskBean>() {
                            @Override
                            public TaskBean apply(Response<String> r) throws Exception {
                                if (null != r) {
                                    JSONObject j = new JSONObject(r.body());
                                    return new Gson().fromJson(j.getJSONObject("task").toString(), TaskBean.class);
                                }

                                return null;
                            }
                        }).subscribeWith(new ResourceSubscriber<TaskBean>() {
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
    public void execTask(final int id) {
        addSubscribe(
                httpHelper.post(TASK_CANCEL_URL, new HashMap<String, Object>(), new HashMap<String, Object>())
                        .compose(RxUtil.<Response<String>>rxSchedulerHelper())
                        .map(new Function<Response<String>, BaseHttpResponse>() {
                            @Override
                            public BaseHttpResponse apply(Response<String> r) throws Exception {
                                if (r != null) {
                                    return new Gson().fromJson(r.body(), BaseHttpResponse.class);
                                }

                                return new BaseHttpResponse();
                            }
                        }).filter(new Predicate<BaseHttpResponse>() {
                    @Override
                    public boolean test(BaseHttpResponse r) throws Exception {
                        return null != r && r.isSuccess();
                    }
                }).flatMap(new Function<BaseHttpResponse, Publisher<Response<String>>>() {
                    @Override
                    public Publisher<Response<String>> apply(BaseHttpResponse baseHttpResponse) throws Exception {
                        return httpHelper.post(TASK_URL + "/" + id, new HashMap<String, Object>(), new HashMap<String, Object>())
                                .compose(RxUtil.<Response<String>>rxSchedulerHelper());
                    }
                }).subscribeWith(new ResourceSubscriber<Response<String>>() {
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
                                mView.showErrorMsg(App.getInstance().getString(R.string.exec_success));
                            } else {
                                mView.showErrorMsg(App.getInstance().getString(R.string.exec_error));
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
