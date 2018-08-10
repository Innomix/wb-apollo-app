package me.fmtech.apollo.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import me.fmtech.apollo.R;
import me.fmtech.apollo.app.App;
import me.fmtech.apollo.di.component.DaggerFragmentComponent;
import me.fmtech.apollo.di.component.FragmentComponent;
import me.fmtech.apollo.di.module.FragmentModule;
import me.fmtech.apollo.utils.SnackbarUtil;

public abstract class BaseFragment<T extends BasePresenter> extends SimpleFragment implements BaseView {

    @Inject
    protected T mPresenter;

    protected FragmentComponent getFragmentComponent() {
        return DaggerFragmentComponent.builder()
                .appComponent(App.getAppComponent())
                .fragmentModule(getFragmentModule())
                .build();
    }

    protected FragmentModule getFragmentModule() {
        return new FragmentModule(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initInject();
        mPresenter.attachView(this);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        if (mPresenter != null) mPresenter.detachView();
        super.onDestroyView();
    }

    @Override
    public void showErrorMsg(String msg) {
        SnackbarUtil.show(((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0), msg);
    }

    protected abstract void initInject();


    protected ProgressDialog mProgressDialog;

    protected ProgressDialog showProgressDialog() {
        return showProgressDialog(getString(R.string.set_waiting));
    }

    protected ProgressDialog showProgressDialog(String msg) {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if (null != mActivity && mActivity.isFinishing()) {
            return null;
        }

        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        return mProgressDialog;
    }

    protected void cancelDialog() {
        if (null != mProgressDialog) {
            mProgressDialog.cancel();
        }
    }

    public static Dialog showMsgDialog(Activity activity, String msg) {
        if (activity.isFinishing()) {
            return null;
        }

        AlertDialog mDialog = new AlertDialog.Builder(activity)
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override


                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                }).create();
        mDialog.show();
        return mDialog;
    }
}