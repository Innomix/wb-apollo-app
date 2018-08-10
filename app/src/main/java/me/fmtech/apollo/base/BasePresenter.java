package me.fmtech.apollo.base;

public interface BasePresenter<T extends BaseView> {

    void attachView(T view);

    void detachView();
}
