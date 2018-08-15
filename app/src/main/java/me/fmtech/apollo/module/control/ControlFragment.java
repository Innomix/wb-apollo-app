package me.fmtech.apollo.module.control;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.slamtec.slamware.robot.Location;

import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.OnClick;
import lib.folderpicker.FolderPicker;
import me.fmtech.apollo.R;
import me.fmtech.apollo.app.Constants;
import me.fmtech.apollo.base.BaseFragment;
import me.fmtech.apollo.module.base.DispatcherActivity;
import me.fmtech.apollo.module.task.create.TaskCreateFragment;
import me.fmtech.apollo.utils.PrefUtil;

public class ControlFragment extends BaseFragment<ControlPresenter> implements ControlContract.View {
    private static final String TAG = ControlFragment.class.getName();

    @BindView(R.id.connect)
    protected Button mConnectState;
    @BindView(R.id.battery)
    protected Button mBattery;
    public static final String PREF_LOC = "PREF_LOC";
    private Map<String, Location> mPositions = new TreeMap<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_control;
    }

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected void initEventAndData() {
        String s = PrefUtil.getString(mContext, PREF_LOC);
        if (!TextUtils.isEmpty(s)) {
            Map<String, Location> m = new Gson().fromJson(s, new TypeToken<Map<String, Location>>() {
            }.getType());
            mPositions.putAll(m);
        }
        mView.findViewById(R.id.left).setOnTouchListener(onTouchListener);
        mView.findViewById(R.id.right).setOnTouchListener(onTouchListener);
        mView.findViewById(R.id.top).setOnTouchListener(onTouchListener);
        mView.findViewById(R.id.down).setOnTouchListener(onTouchListener);
    }

    @OnClick({R.id.add, R.id.save_map, R.id.power_off, R.id.create_task, R.id.volume, R.id.connect, R.id.go_home})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.add:
                mPresenter.getLocation();
                break;
            case R.id.save_map:
                getFile(REQUEST_SAVE_MAP_FILE);
                break;
            case R.id.power_off:
                showProgressDialog(getString(R.string.power_off_waiting));
                mPresenter.powerOff();
                break;
            case R.id.create_task:
                Bundle data = new Bundle(1);
                data.putString(Constants.FRAGMENT_TITLE, getString(R.string.task_create));
                DispatcherActivity.launch(mActivity, TaskCreateFragment.class.getName(), data);
                break;
            case R.id.connect:
                mPresenter.reConnect();
                break;
            case R.id.volume:
                getVolumeDialog().show();
                mPresenter.getVolume();
                break;
            case R.id.go_home:
                mPresenter.goHome();
                break;
            default:
                break;
        }
    }

    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                switch (v.getId()) {
                    case R.id.left:
                        mPresenter.turnLeft();
                        break;
                    case R.id.right:
                        mPresenter.turnRight();
                        break;
                    case R.id.top:
                        mPresenter.moveForward();
                        break;
                    case R.id.down:
                        mPresenter.moveBack();
                        break;
                    default:
                        break;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mPresenter.cancelMove();
            }

            return true;
        }
    };

    private AlertDialog dialog;
    private SeekBar seekBar;

    private Dialog getVolumeDialog() {
        if (null != dialog) {
            return dialog;
        }

        android.view.View view = getLayoutInflater().inflate(R.layout.view_volume_set, null);
        seekBar = view.findViewById(R.id.volume);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPresenter.setVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.volume)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
    }

    @Override
    public void showLocation(final Location location) {
        View v = getLayoutInflater().inflate(R.layout.view_edit_loc, null);
        final EditText text = v.findViewById(R.id.edittext);
        text.setText(String.format(getString(R.string.route), mPositions.size()));

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.save_name)
                .setView(v)
                .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String code = text.getText().toString().trim().replace("\n", "");
                if (!TextUtils.isEmpty(code)) {
                    mPositions.put(code, location);
                }
            }
        })
                .create()
                .show();
    }

    @Override
    public void connected(boolean state) {
        String text = this.getResources().getString(state ? R.string.connect : R.string.not_connect);
        mConnectState.setText(text);
        if (!state) {
            showErrorMsg(getString(R.string.connect_error));
        } else {
            mPresenter.getBattery();
        }
    }

    @Override
    public void battery(int percent) {
        mBattery.setText(percent + "%");
    }

    @Override
    public void showVolume(int percent) {
        if (seekBar != null) {
            seekBar.setProgress(percent);
        }
    }

    @Override
    public void powerOff(String msg) {
        cancelDialog();
        showMsgDialog(mActivity, msg);
    }

    @Override
    public void onPause() {
        super.onPause();

        String s = new Gson().toJson(mPositions, new TypeToken<Map<String, Location>>() {
        }.getType());
        PrefUtil.put(mContext, PREF_LOC, s);
    }

    private static final int REQUEST_SAVE_MAP_FILE = 100;

    private void getFile(int requestCode) {
        Intent intent = new Intent(mActivity, FolderPicker.class);
        intent.putExtra("pickFiles", false);
        startActivityForResult(intent, requestCode);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            String file = intent.getExtras().getString("data");
            if (REQUEST_SAVE_MAP_FILE == requestCode) {
                mPresenter.saveMap(file);
            }
        }
    }
}
