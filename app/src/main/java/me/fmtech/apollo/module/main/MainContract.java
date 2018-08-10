package me.fmtech.apollo.module.main;

import com.tbruyelle.rxpermissions2.RxPermissions;

import me.fmtech.apollo.base.BasePresenter;
import me.fmtech.apollo.base.BaseView;

public interface MainContract {

    interface View extends BaseView {

    }

    interface Presenter extends BasePresenter<View> {
        void checkPermissions(RxPermissions rxPermissions);
    }
}
