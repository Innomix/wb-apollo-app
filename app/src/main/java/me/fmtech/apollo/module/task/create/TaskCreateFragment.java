package me.fmtech.apollo.module.task.create;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.leon.lfilepickerlibrary.LFilePicker;

import java.util.List;

import butterknife.BindView;
import me.fmtech.apollo.R;
import me.fmtech.apollo.base.BaseFragment;
import me.fmtech.apollo.model.bean.LocationBean;
import me.fmtech.apollo.model.bean.TaskBean;

public class TaskCreateFragment extends BaseFragment<TaskCreatePresenter> implements TaskCreateContract.View {
    private static final String TAG = TaskCreateFragment.class.getName();

    @BindView(R.id.route_list)
    protected RecyclerView mRouteListView;
    @BindView(R.id.route_plan)
    protected RecyclerView mRoutePlanView;
    @BindView(R.id.task_name)
    protected EditText mTaskName;
    private TaskAdapter mRouteListAdapter;
    private TaskAdapter mRoutePlanAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_route_create;
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected void initEventAndData() {
        mRouteListAdapter = new TaskAdapter(mContext);
        mRouteListAdapter.setOnItemClickListener(routeListener);
        mRouteListView.setLayoutManager(new LinearLayoutManager(mContext));
        mRouteListView.setAdapter(mRouteListAdapter);

        mRoutePlanAdapter = new TaskAdapter(mContext);
        mRoutePlanView.setLayoutManager(new LinearLayoutManager(mContext));
        mRoutePlanView.setAdapter(mRoutePlanAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mRoutePlanAdapter.getTouchHelpCallback());
        itemTouchHelper.attachToRecyclerView(mRoutePlanView);

        mPresenter.getLocations();
    }

    @Override
    public void showLocations(List<LocationBean> list) {
        mRouteListAdapter.update(list);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_route_create, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_route:
                List<LocationBean> data = mRoutePlanAdapter.getList();
                if (data.size() > 0) {
                    getFile(REQUEST_SAVE_TASK_FILE, false);
                } else {
                    showMsgDialog(mActivity, getString(R.string.not_edit_route));
                }
                return true;
            case R.id.action_load_route:
                getFile(REQUEST_LOAD_TASK_FILE, true);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final int REQUEST_SAVE_TASK_FILE = 100;
    private static final int REQUEST_LOAD_TASK_FILE = 101;

    private void getFile(int requestCode, boolean pickFiles) {
        new LFilePicker()
                .withSupportFragment(this)
                .withRequestCode(requestCode)
                .withTitle(pickFiles ? "选择任务文件" : "选择保存位置")
                .withStartPath(Environment.getExternalStorageDirectory().getPath())
                .withMutilyMode(false)
                .withChooseMode(pickFiles)
                .start();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (REQUEST_SAVE_TASK_FILE == requestCode) {
                String file = intent.getStringExtra("path");
                String task = mTaskName.getText().toString();
                file += "/" + task + ".txt";
                List<LocationBean> data = mRoutePlanAdapter.getList();
                mPresenter.saveTask(file, new TaskBean(task, data));
            } else if (REQUEST_LOAD_TASK_FILE == requestCode) {
                List<String> list = intent.getStringArrayListExtra("paths");
                if (null != list && list.size() == 1) {
                    mPresenter.loadTask(list.get(0));
                }
            }
        }
    }

    protected OnClickListener routeListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            int pos = (Integer) view.getTag();
            LocationBean l = (LocationBean) mRouteListAdapter.get(pos);
            if (l != null) {
                mRoutePlanAdapter.add(l);
            }
        }
    };

}
