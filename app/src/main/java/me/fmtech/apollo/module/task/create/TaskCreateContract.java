package me.fmtech.apollo.module.task.create;

import java.util.List;

import me.fmtech.apollo.base.BasePresenter;
import me.fmtech.apollo.base.BaseView;
import me.fmtech.apollo.model.bean.LocationBean;

public interface TaskCreateContract {

    interface View extends BaseView {
        void showLocations(List<LocationBean> list);
    }

    interface Presenter extends BasePresenter<View> {
        void getLocations();
    }
}
