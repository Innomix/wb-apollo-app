package me.fmtech.apollo.module.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import butterknife.BindView;
import me.fmtech.apollo.R;
import me.fmtech.apollo.app.Constants;
import me.fmtech.apollo.base.SimpleActivity;
import me.fmtech.apollo.base.SimpleFragment;

public class DispatcherActivity extends SimpleActivity {

    @BindView(R.id.tool_bar)
    Toolbar mToolBar;
    boolean notitle, nostatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        notitle = data.getBoolean(Constants.FRAGMENT_NO_ACTIONBAR);
        nostatus = data.getBoolean(Constants.FRAGMENT_NO_STATUSBAR);

        if (notitle) this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (nostatus) this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_dispatcher;
    }

    @Override
    protected void initEventAndData() {
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        String name = data.getString(Constants.FRAGMENT_TITLE);
        String clazz = data.getString(Constants.FRAGMENT_CLASS);

        if (!notitle) {
            setToolBar(mToolBar, name);
        } else {
            hideToolBar();
        }
        SimpleFragment fragment = null;
        try {
            fragment = (SimpleFragment) Class.forName(clazz).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
            return;
        }

        fragment.setArguments(intent.getExtras());
        loadRootFragment(R.id.fl_content, fragment);
    }

    public void hideToolBar() {
        mToolBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (null != getTopFragment()) {
            getTopFragment().onCreateOptionsMenu(menu, getMenuInflater());
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (null != getTopFragment()) {
            getTopFragment().onOptionsItemSelected(item);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void launch(Activity context, String clazz, Bundle data) {
        Intent intent = new Intent();
        intent.setClass(context, DispatcherActivity.class);
        if (null == data) {
            data = new Bundle(2);
        }
        data.putString(Constants.FRAGMENT_CLASS, clazz);
        intent.putExtras(data);

        context.startActivity(intent);
    }

    public static void launchForResult(Activity context, String clazz, Bundle data, int requestid) {
        Intent intent = new Intent();
        intent.setClass(context, DispatcherActivity.class);
        if (null == data) {
            data = new Bundle(2);
        }
        data.putString(Constants.FRAGMENT_CLASS, clazz);
        intent.putExtras(data);

        context.startActivityForResult(intent, requestid);
    }
}
