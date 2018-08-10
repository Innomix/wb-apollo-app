package me.fmtech.apollo.module.task.upload;

import me.fmtech.apollo.base.BasePresenter;
import me.fmtech.apollo.base.BaseView;
import me.fmtech.apollo.model.bean.TaskBean;

public interface TaskUploadContract {

    interface View extends BaseView {
        void showTask(TaskBean task);

        void setTaskStatus(String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void loadTask(String filename);

        void setTask(TaskBean task, String musicFilepath);
    }
}
