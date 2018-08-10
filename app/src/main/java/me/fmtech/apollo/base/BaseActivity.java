package me.fmtech.apollo.base;

import android.view.ViewGroup;

import javax.inject.Inject;

import me.fmtech.apollo.app.App;
import me.fmtech.apollo.di.component.ActivityComponent;
import me.fmtech.apollo.di.component.DaggerActivityComponent;
import me.fmtech.apollo.di.module.ActivityModule;
import me.fmtech.apollo.utils.SnackbarUtil;

public abstract class BaseActivity<T extends BasePresenter> extends SimpleActivity implements BaseView {

    @Inject
    protected T mPresenter;

    protected ActivityComponent getActivityComponent() {
        return DaggerActivityComponent.builder()
                .appComponent(App.getAppComponent())
                .activityModule(getActivityModule())
                .build();
    }

    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    @Override
    protected void onViewCreated() {
        super.onViewCreated();
        initInject();
        if (mPresenter != null)
            mPresenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null)
            mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showErrorMsg(String msg) {
        SnackbarUtil.show(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0), msg);
    }

    protected abstract void initInject();
}