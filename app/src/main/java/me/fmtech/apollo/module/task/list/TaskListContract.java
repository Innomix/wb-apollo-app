package me.fmtech.apollo.module.task.list;

import java.util.List;

import me.fmtech.apollo.base.BasePresenter;
import me.fmtech.apollo.base.BaseView;
import me.fmtech.apollo.model.bean.TaskBean;

public interface TaskListContract {

    interface View extends BaseView {
        void showTasks(List<TaskBean> task);
    }

    interface Presenter extends BasePresenter<View> {
        void getTasks();

        void deleteTask(int id);

        void execTask(int id);

        void cancelTask();
    }
}
