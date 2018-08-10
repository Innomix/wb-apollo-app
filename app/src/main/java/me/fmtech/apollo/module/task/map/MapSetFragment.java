package me.fmtech.apollo.module.task.map;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.slamtec.slamware.robot.Location;

import butterknife.BindView;
import butterknife.OnClick;
import lib.folderpicker.FolderPicker;
import me.fmtech.apollo.R;
import me.fmtech.apollo.base.BaseFragment;

public class MapSetFragment extends BaseFragment<MapSetPresenter> implements MapSetContract.View {
    private static final String TAG = MapSetFragment.class.getName();

    @BindView(R.id.map_file_path)
    protected TextView mMapFilePathTv;

    @BindView(R.id.axis_x)
    protected EditText mAxisXTv;
    @BindView(R.id.axis_y)
    protected EditText mAxisYTv;
    @BindView(R.id.axis_z)
    protected EditText mAxisZTv;

    private String mMapFilePath;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map_set;
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected void initEventAndData() {
    }

    @Override
    public void showMapStatus(String msg) {
        cancelDialog();
        showMsgDialog(mActivity, msg);
    }

    @OnClick({R.id.set_map, R.id.map_file_path_layout})
    protected void click(View v) {
        switch (v.getId()) {
            case R.id.map_file_path_layout:
                getFile(REQUEST_MAP_FILE);
                break;
            case R.id.set_map:
                showProgressDialog();
                float x, y, z;
                try {
                    x = Float.valueOf(mAxisXTv.getText().toString());
                    y = Float.valueOf(mAxisYTv.getText().toString());
                    z = Float.valueOf(mAxisZTv.getText().toString());

                    mPresenter.setMap(mMapFilePath, new Location(x, y, z));
                } catch (Exception e) {
                    showMsgDialog(mActivity, getString(R.string.error_axis));
                }
                break;
            default:
                break;
        }
    }

    private static final int REQUEST_MAP_FILE = 101;

    private void getFile(int requestCode) {
        Intent intent = new Intent(mActivity, FolderPicker.class);
        intent.putExtra("pickFiles", true);
        startActivityForResult(intent, requestCode);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            String file = intent.getExtras().getString("data");
            if (REQUEST_MAP_FILE == requestCode) {
                mMapFilePath = file;
                mMapFilePathTv.setText(getText(R.string.map_file_path) + file);
            }
        }
    }
}
