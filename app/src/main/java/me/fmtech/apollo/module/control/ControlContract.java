package me.fmtech.apollo.module.control;

import com.slamtec.slamware.robot.Location;

import me.fmtech.apollo.base.BasePresenter;
import me.fmtech.apollo.base.BaseView;

public interface ControlContract {

    interface View extends BaseView {
        void showLocation(Location location);

        void connected(boolean state);

        void battery(int percent);

        void showVolume(int percent);

        void powerOff(String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void connect();

        void reConnect();

        void disconnect();

        void goHome();

        void getLocation();

        void turnLeft();

        void turnRight();

        void moveForward();

        void moveBack();

        void saveMap(String filePath);

        void getVolume();

        void setVolume(int volume);

        void powerOff();
    }
}
