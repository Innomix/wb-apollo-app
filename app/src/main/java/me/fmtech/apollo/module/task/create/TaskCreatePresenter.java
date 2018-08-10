package me.fmtech.apollo.module.task.create;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.slamtec.slamware.robot.Location;

import org.apache.commons.io.FileUtils;
import org.reactivestreams.Publisher;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.subscribers.ResourceSubscriber;
import me.fmtech.apollo.app.App;
import me.fmtech.apollo.base.RxPresenter;
import me.fmtech.apollo.component.PriorityScheduler;
import me.fmtech.apollo.model.bean.LocationBean;
import me.fmtech.apollo.model.bean.TaskBean;
import me.fmtech.apollo.module.task.create.TaskCreateContract.Presenter;
import me.fmtech.apollo.module.task.create.TaskCreateContract.View;
import me.fmtech.apollo.utils.PrefUtil;
import me.fmtech.apollo.utils.RxUtil;

import static me.fmtech.apollo.module.control.ControlFragment.PREF_LOC;

public class TaskCreatePresenter extends RxPresenter<View> implements Presenter {
    private static final String TAG = TaskCreatePresenter.class.getName();

    @Inject
    TaskCreatePresenter() {
    }

    @Override
    public void getLocations() {
        if (mView != null) {
            mView.showLocations(test());
        }
    }

    private List<LocationBean> test() {
        String s = PrefUtil.getString(App.getInstance(), PREF_LOC);
        if (!TextUtils.isEmpty(s)) {
            Map<String, Location> m = new Gson().fromJson(s, new TypeToken<Map<String, Location>>() {
            }.getType());
            List<LocationBean> data = new ArrayList<>(m.size());

            for (String k : m.keySet()) {
                LocationBean l = new LocationBean(m.get(k));
                l.setName(k);
                data.add(l);
            }

            return data;
        }

        return new ArrayList<>();
    }

    public void saveTask(final String filename, final TaskBean task) {
        Flowable.just(1).subscribeOn(PriorityScheduler.get().priority(4))
                .flatMap(new Function<Integer, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Integer integer) throws Exception {
                        FileUtils.writeStringToFile(new File(filename), task.toString());
                        return Flowable.empty();
                    }
                }).subscribe();
    }

    public void loadTask(final String filename) {
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
                                mView.showLocations(taskBean.getMilestones());
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
}
