package me.fmtech.apollo.module.main;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationBar.OnTabSelectedListener;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.tbruyelle.rxpermissions2.RxPermissions;

import me.fmtech.apollo.R;
import me.fmtech.apollo.base.BaseActivity;
import me.fmtech.apollo.module.control.ControlFragment;
import me.fmtech.apollo.module.task.list.TaskListFragment;
import me.fmtech.apollo.module.task.map.MapSetFragment;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View, OnTabSelectedListener {
    private static final String TAG = MainActivity.class.getName();

    //导航栏
    private BottomNavigationBar mBottomNavigationBar;
    private FragmentManager fragmentManager;
    //当前fragment
    private Fragment currentFragment;

    //手动控制
    private Fragment mControlFragment;
    //任务列表
    private Fragment mTaskListFragment;
    //设置地图
    private Fragment mMapSetFragment;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initEventAndData() {
        requestPermission();
        initFragment();
        initBottomNavigationBar();
    }

    //申请权限
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            mPresenter.checkPermissions(new RxPermissions(this));
        }
    }

    private void initBottomNavigationBar() {
        mBottomNavigationBar = findViewById(R.id.bottom_navigation);
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        mBottomNavigationBar.setBarBackgroundColor(android.R.color.white);

        mBottomNavigationBar.addItem(
                new BottomNavigationItem(R.drawable.ic_manual, R.string.manual_control)
                        .setActiveColorResource(android.R.color.holo_orange_dark));
        mBottomNavigationBar.addItem(
                new BottomNavigationItem(R.drawable.ic_list, R.string.tasks)
                        .setActiveColorResource(android.R.color.holo_green_dark));
        mBottomNavigationBar.addItem(
                new BottomNavigationItem(R.drawable.ic_map, R.string.map_set)
                        .setActiveColorResource(R.color.colorPrimaryDark));

        mBottomNavigationBar.setFirstSelectedPosition(0);
        mBottomNavigationBar.initialise();
        mBottomNavigationBar.setTabSelectedListener(this);
        mBottomNavigationBar.selectTab(0);
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }
        currentFragment = fragment;
        if (!fragment.isAdded()) {
            fragmentTransaction.add(R.id.layout_container, fragment).show(fragment).commit();
        } else {
            fragmentTransaction.show(fragment).commit();
        }
    }

    //标签被选中
    @Override
    public void onTabSelected(int position) {
        switch (position) {
            case 0:
                if (mControlFragment == null) {
                    mControlFragment = new ControlFragment();
                }
                showFragment(mControlFragment);
                break;
            case 1:
                if (mTaskListFragment == null) {
                    mTaskListFragment = new TaskListFragment();
                }
                showFragment(mTaskListFragment);
                break;
            case 2:
                if (mMapSetFragment == null) {
                    mMapSetFragment = new MapSetFragment();
                }
                showFragment(mMapSetFragment);
                break;
        }
    }

    //标签未被选中
    @Override
    public void onTabUnselected(int position) {

    }

    //标签在选中状态下再次点击
    @Override
    public void onTabReselected(int position) {

    }

    @Override
    protected void initInject() {
        getActivityComponent().inject(this);
    }
}
