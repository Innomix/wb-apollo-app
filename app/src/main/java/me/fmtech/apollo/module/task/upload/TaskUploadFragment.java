package me.fmtech.apollo.module.task.upload;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.leon.lfilepickerlibrary.LFilePicker;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.fmtech.apollo.R;
import me.fmtech.apollo.app.Constants;
import me.fmtech.apollo.base.BaseFragment;
import me.fmtech.apollo.model.bean.TaskBean;
import me.fmtech.apollo.module.task.create.TaskAdapter;

public class TaskUploadFragment extends BaseFragment<TaskUploadPresenter> implements TaskUploadContract.View {
    private static final String TAG = TaskUploadFragment.class.getName();

    @BindView(R.id.route_list)
    protected RecyclerView mRouteListView;
    private TaskAdapter mRouteListAdapter;

    @BindView(R.id.task_name)
    protected EditText mTaskNameEt;
    @BindView(R.id.audio_file_path)
    protected TextView mAudioFilePathTv;

    private String mAudioFilePath;
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
        mRouteListAdapter = new TaskAdapter(mContext);
        mRouteListView.setLayoutManager(new LinearLayoutManager(mContext));
        mRouteListView.setAdapter(mRouteListAdapter);

        String path = getArguments().getString(Constants.KEY_FILE_PATH);
        if (!TextUtils.isEmpty(path)) {
            mPresenter.loadTask(path);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_route_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_load_route:
                getFile(REQUEST_TASK_FILE);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showTask(TaskBean task) {
        mTaskBean = task;
        mTaskNameEt.setText(task.getName());
        mRouteListAdapter.update(task.getMilestones());
    }

    @Override
    public void setTaskStatus(String msg) {
        cancelDialog();
        showMsgDialog(mActivity, msg);
    }

    @OnClick({R.id.set_task, R.id.audio_file_path_layout})
    protected void click(View v) {
        switch (v.getId()) {
            case R.id.audio_file_path_layout:
                getFile(REQUEST_AUDIO_FILE);
                break;
            case R.id.set_task:
                if (null == mTaskBean || mTaskBean.getMilestones().size() == 0) {
                    showMsgDialog(mActivity, getString(R.string.empty_task));
                    return;
                }

                if (mAudioFilePath != null) {
                    mTaskBean.setAudio1(new File(mAudioFilePath).getName());
                }
                mTaskBean.setName(mTaskNameEt.getText().toString());
                showProgressDialog();
                mPresenter.setTask(mTaskBean, mAudioFilePath);
                break;
            default:
                break;
        }
    }

    private static final int REQUEST_TASK_FILE = 100;
    private static final int REQUEST_AUDIO_FILE = 101;

    private void getFile(int requestCode) {
        new LFilePicker()
                .withSupportFragment(this)
                .withRequestCode(requestCode)
                .withTitle("选择任务文件")
                .withStartPath(Environment.getExternalStorageDirectory().getPath())
                .withMutilyMode(false)
                .withChooseMode(true)
                .start();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            String file = "";
            List<String> list = intent.getStringArrayListExtra("paths");
            if (null != list && list.size() == 1) {
                file = list.get(0);
            } else {
                return;
            }
            if (REQUEST_TASK_FILE == requestCode) {
                mPresenter.loadTask(file);
            } else if (REQUEST_AUDIO_FILE == requestCode) {
                mAudioFilePath = file;
                mAudioFilePathTv.setText(getText(R.string.audio_file_path) + file);
            }
        }
    }
}
