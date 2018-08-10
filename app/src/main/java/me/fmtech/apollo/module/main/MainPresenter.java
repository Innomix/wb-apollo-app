package me.fmtech.apollo.module.main;

import android.Manifest;

import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import me.fmtech.apollo.base.RxPresenter;
import me.fmtech.apollo.model.http.HttpHelper;
import me.fmtech.apollo.model.http.RetrofitHelper;
import me.fmtech.apollo.module.main.MainContract.Presenter;
import me.fmtech.apollo.module.main.MainContract.View;

public class MainPresenter extends RxPresenter<View> implements Presenter {
    HttpHelper httpHelper;

    @Inject
    public MainPresenter(RetrofitHelper helper) {
        httpHelper = helper;
    }

    @Override
    public void attachView(View view) {
        super.attachView(view);
    }

    @Override
    public void checkPermissions(RxPermissions rxPermissions) {
        addSubscribe(
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean granted) {
                                if (granted) {
                                } else {
                                    mView.showErrorMsg("需要文件读写权限哦~");
                                }
                            }
                        })
        );
    }
}
