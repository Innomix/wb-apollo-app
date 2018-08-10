package me.fmtech.apollo.module.task.display;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import me.fmtech.apollo.R;
import me.fmtech.apollo.app.Constants;
import me.fmtech.apollo.base.BaseFragment;
import me.fmtech.apollo.model.bean.TaskBean;
import me.fmtech.apollo.module.task.create.TaskAdapter;

public class TaskDisplayFragment extends BaseFragment<TaskDisplayPresenter> implements TaskDisplayContract.View {
    private static final String TAG = TaskDisplayFragment.class.getName();

    @BindView(R.id.route_list)
    protected RecyclerView mRouteListView;
    private TaskAdapter mRouteListAdapter;

    @BindView(R.id.set_task)
    protected Button mExecTaskTv;
    @BindView(R.id.task_name)
    protected EditText mTaskNameEt;
    @BindView(R.id.task_name_pre)
    protected TextView mTaskNametV;
    @BindView(R.id.audio_file_path)
    protected TextView mAudioFilePathTv;

    private TaskBean mTaskBean;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_route_edit;
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected void initEventAndData() {
        mExecTaskTv.setText(R.string.exec_task);
        mTaskNameEt.setVisibility(View.GONE);
        mRouteListAdapter = new TaskAdapter(mContext);
        mRouteListView.setLayoutManager(new LinearLayoutManager(mContext));
        mRouteListView.setAdapter(mRouteListAdapter);

        int taskid = getArguments().getInt(Constants.KEY_TASK_ID);
        mPresenter.loadTask(taskid);
    }

    @Override
    public void showTask(TaskBean task) {
        mTaskBean = task;
        mTaskNametV.setText(getString(R.string.task_name) + task.getName());
        mAudioFilePathTv.setText(getString(R.string.audio_file_path) + mTaskBean.getAudio1());
        mRouteListAdapter.update(task.getMilestones());
    }

    @OnClick({R.id.set_task})
    protected void click(View v) {
        switch (v.getId()) {
            case R.id.set_task:
                if (mTaskBean != null) {
                    mPresenter.execTask(mTaskBean.getId());
                }
                break;
            default:
                break;
        }
    }

}
