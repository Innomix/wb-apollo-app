package me.fmtech.apollo.module.task.list;

import com.google.gson.Gson;

import org.reactivestreams.Publisher;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subscribers.ResourceSubscriber;
import me.fmtech.apollo.R;
import me.fmtech.apollo.app.App;
import me.fmtech.apollo.app.Constants;
import me.fmtech.apollo.base.RxPresenter;
import me.fmtech.apollo.model.bean.BaseHttpResponse;
import me.fmtech.apollo.model.bean.TaskBean;
import me.fmtech.apollo.model.bean.TaskList;
import me.fmtech.apollo.model.http.HttpHelper;
import me.fmtech.apollo.model.http.RetrofitHelper;
import me.fmtech.apollo.module.task.list.TaskListContract.Presenter;
import me.fmtech.apollo.module.task.list.TaskListContract.View;
import me.fmtech.apollo.utils.RxUtil;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class TaskListPresenter extends RxPresenter<View> implements Presenter {
    private static final String TAG = TaskListPresenter.class.getName();
    private HttpHelper httpHelper;
    public static final String TASK_URL = Constants.BASE_URL + "tasks";
    public static final String TASK_CANCEL_URL = Constants.BASE_URL + "exec/cancel";

    @Inject
    TaskListPresenter(RetrofitHelper httpHelper) {
        this.httpHelper = httpHelper;
    }

    @Override
    public void getTasks() {
        addSubscribe(
                httpHelper.get(TASK_URL)
                        .compose(RxUtil.<ResponseBody>rxSchedulerHelper())
                        .subscribeWith(new ResourceSubscriber<ResponseBody>() {
                            @Override
                            public void onNext(ResponseBody r) {
                                try {
                                    TaskList list = new Gson().fromJson(r.string(), TaskList.class);
                                    if (null == list) {
                                        showError();
                                        return;
                                    }

                                    List<TaskBean> tasks = list.getTasks();
                                    if (mView != null) {
                                        mView.showTasks(tasks);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    showError();
                                }
                            }

                            @Override
                            public void onError(Throwable t) {
                                t.printStackTrace();
                                showError();
                            }

                            @Override
                            public void onComplete() {

                            }

                            private void showError() {
                                if (mView != null) {
                                    mView.showErrorMsg(App.getInstance().getString(R.string.get_task_error));
                                }
                            }
                        })
        );
    }

    @Override
    public void deleteTask(int id) {
        addSubscribe(
                httpHelper.delete(TASK_URL + "/" + id, new HashMap<String, Object>())
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
                                        mView.showErrorMsg(App.getInstance().getString(R.string.delete_success));
                                    } else {
                                        mView.showErrorMsg(App.getInstance().getString(R.string.delete_error));
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

    @Override
    public void cancelTask() {
        addSubscribe(
                httpHelper.post(TASK_CANCEL_URL, new HashMap<String, Object>(), new HashMap<String, Object>())
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
                                        mView.showErrorMsg(App.getInstance().getString(R.string.cancel_task_success));
                                    } else {
                                        mView.showErrorMsg(App.getInstance().getString(R.string.cancel_task_error));
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
