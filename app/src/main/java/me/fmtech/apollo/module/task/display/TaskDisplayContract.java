package me.fmtech.apollo.module.task.display;

import me.fmtech.apollo.base.BasePresenter;
import me.fmtech.apollo.base.BaseView;
import me.fmtech.apollo.model.bean.TaskBean;

public interface TaskDisplayContract {

    interface View extends BaseView {
        void showTask(TaskBean task);
    }

    interface Presenter extends BasePresenter<View> {
        void loadTask(int taskid);

        void execTask(int id);
    }
}
