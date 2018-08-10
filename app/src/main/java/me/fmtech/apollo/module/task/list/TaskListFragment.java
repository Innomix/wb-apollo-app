package me.fmtech.apollo.module.task.list;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.fmtech.apollo.R;
import me.fmtech.apollo.app.Constants;
import me.fmtech.apollo.base.BaseFragment;
import me.fmtech.apollo.model.bean.TaskBean;
import me.fmtech.apollo.module.base.DispatcherActivity;
import me.fmtech.apollo.module.task.display.TaskDisplayFragment;
import me.fmtech.apollo.module.task.upload.TaskUploadFragment;
import me.fmtech.apollo.widget.DefaultItemTouchHelpCallback;

public class TaskListFragment extends BaseFragment<TaskListPresenter> implements TaskListContract.View {
    private static final String TAG = TaskListFragment.class.getName();

    @BindView(R.id.swipe_refresh)
    protected SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.task_list)
    protected RecyclerView mTaskListView;
    private TaskListAdapter mTaskListAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_task_list;
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected void initEventAndData() {
        mTaskListAdapter = new TaskListAdapter(mContext);
        mTaskListAdapter.setOnItemClickListener(taskListener);
        mTaskListView.setLayoutManager(new LinearLayoutManager(mContext));
        mTaskListView.setAdapter(mTaskListAdapter);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getTasks();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mTaskListAdapter.getTouchHelpCallback());
        itemTouchHelper.attachToRecyclerView(mTaskListView);
        mTaskListAdapter.setmCallback(new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
            @Override
            public void onSwiped(int pos) {
                if (pos >= mTaskListAdapter.getItemCount()) {
                    return;
                }

                TaskBean task = (TaskBean) mTaskListAdapter.get(pos);
                mPresenter.deleteTask(task.getId());
            }

            @Override
            public boolean onMove(int srcPosition, int targetPosition) {
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getTasks();
    }

    protected View.OnClickListener taskListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int pos = (Integer) view.getTag();
            if (pos >= mTaskListAdapter.getItemCount()) {
                return;
            }

            TaskBean task = (TaskBean) mTaskListAdapter.get(pos);
            showTask(task);
        }
    };

    @Override
    public void showTasks(List<TaskBean> task) {
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }

        mTaskListAdapter.update(task);
    }

    @OnClick(R.id.stop_task)
    protected void stopTask() {
        mPresenter.cancelTask();
    }

    @OnClick(R.id.create_task)
    protected void createTask() {
        Bundle data = new Bundle(1);
        data.putString(Constants.FRAGMENT_TITLE, getString(R.string.task_upload));
        DispatcherActivity.launch(mActivity, TaskUploadFragment.class.getName(), data);
    }

    protected void showTask(TaskBean task) {
        Bundle data = new Bundle(1);
        data.putString(Constants.FRAGMENT_TITLE, getString(R.string.task_detail));
        data.putInt(Constants.KEY_TASK_ID, task.getId());
        DispatcherActivity.launch(mActivity, TaskDisplayFragment.class.getName(), data);
    }

    @Override
    public void showErrorMsg(String msg) {
        super.showErrorMsg(msg);

        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }
}
