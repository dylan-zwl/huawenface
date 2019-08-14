package com.huawen.huawenface.sdk.act;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.CompoundButton;

import com.huawen.huawenface.R;
import com.huawen.huawenface.sdk.Constants;
import com.huawen.huawenface.sdk.Global;
import com.huawen.huawenface.sdk.bean.DeviceTypeItemBean;

import java.util.List;

public class FaceSettingActivity extends BaseActivity {
    private AppCompatEditText mClubeInputView;
    private AppCompatEditText mDeviceInputView;
    private AppCompatEditText mDelayTimeInputView;
    private AppCompatEditText mScaleInputView;
    private AppCompatCheckBox mBigPicMode;
    private boolean lastB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_setting);
        mClubeInputView = (AppCompatEditText) findViewById(R.id.dialog_input_group_id);
        mDeviceInputView = (AppCompatEditText) findViewById(R.id.dialog_input_device_id);
        mDelayTimeInputView = (AppCompatEditText) findViewById(R.id.dialog_input_delay_time);
        mScaleInputView = (AppCompatEditText) findViewById(R.id.dialog_input_scale);
        mBigPicMode = (AppCompatCheckBox) findViewById(R.id.setting_big_mode);
        String clubeId = Global.getSpString(Constants.Sp.SP_GROUP_ID, "");
        String deviceId = Global.getSpString(Constants.Sp.SP_DEVICE_ID, "");
        int delayTime = Global.getSpInteger(Constants.Sp.SP_DELAY_TIME, 1500);
        int scale = Global.getSpInteger(Constants.Sp.SCALE, 16);
        mClubeInputView.setText(clubeId);
        mDeviceInputView.setText(deviceId);
        mDelayTimeInputView.setText(String.valueOf(delayTime));
        mScaleInputView.setText(String.valueOf(scale));
        lastB= Global.getSpBoolean(Constants.Sp.BIG_PIC,false);
        mBigPicMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==lastB){
                    return;
                }
                lastB=b;
                Global.setSpBoolean(Constants.Sp.BIG_PIC,b);

            }
        });
        mBigPicMode.setChecked(lastB);
        findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.setSpString(Constants.Sp.SP_GROUP_ID,mClubeInputView.getText().toString());
                Global.setSpString(Constants.Sp.SP_DEVICE_ID,mDeviceInputView.getText().toString());
                Global.setSpInteger(Constants.Sp.SP_DELAY_TIME,Integer.valueOf(mDelayTimeInputView.getText().toString()));
                Global.setSpInteger(Constants.Sp.SCALE,Integer.valueOf(mScaleInputView.getText().toString()));
                finish();
            }
        });
        findViewById(R.id.setting_device_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.getInstance().getInitData(new com.fpa.mainsupport.core.Callback() {
                    @Override
                    public void call(Object[] values) {
                        boolean result = (boolean) values[0];
                        if (result) {
                            showChooseDialog(Global.getInstance().getDeviceTypeList());
                        }
                    }
                });
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void showChooseDialog(final List<DeviceTypeItemBean> deviceTypeItemBeans) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.dialog_choose_device_title);
        final String[] Items = new String[deviceTypeItemBeans.size()];

        int index = 0;
        for (DeviceTypeItemBean item : deviceTypeItemBeans) {
            Items[index++] = item.getContent();
        }
        builder.setItems(Items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Global.setSpString(Constants.Sp.DEVICE_TYPE, deviceTypeItemBeans.get(i).getContent());
                Global.setSpString(Constants.Sp.SP_DEVICE_KEY, deviceTypeItemBeans.get(i).getKey());
                Global.setSpBoolean(Constants.Sp.IS_FIRST_RUN, false);
//                initFace();
//                FaceDetectActivityPermissionsDispatcher.startPreviewWithPermissionCheck(FaceDetectActivity.this);
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
