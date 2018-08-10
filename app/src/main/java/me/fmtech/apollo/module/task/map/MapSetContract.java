package me.fmtech.apollo.module.task.map;

import com.slamtec.slamware.robot.Location;

import me.fmtech.apollo.base.BasePresenter;
import me.fmtech.apollo.base.BaseView;

public interface MapSetContract {

    interface View extends BaseView {
        void showMapStatus(String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void setMap(String filename, Location loc);
    }
}
