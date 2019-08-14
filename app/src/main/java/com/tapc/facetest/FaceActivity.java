package com.tapc.facetest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huawen.huawenface.sdk.Constants;
import com.huawen.huawenface.sdk.Global;
import com.huawen.huawenface.sdk.act.FaceSettingActivity;
import com.huawen.huawenface.sdk.ui.FaceDetectComponent;
import com.huawen.huawenface.sdk.utils.ScreenUtils;
import com.tapc.facetest.utils.ClickModel;
import com.tapc.facetest.utils.IntentUtils;
import com.tapc.facetest.utils.NetUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.functions.Consumer;

public class FaceActivity extends Activity {
    private static final String TAG = FaceActivity.class.getSimpleName();
    private Handler mHandler;
    private ClickModel mClickModel;
    private Button mFaceLogin;
    private TextView mFaceDetectErrorTv;
    private FaceDetectComponent mFaceDetectComponent;
    private Timer mFaceDetectTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication.sAppExceptionHandler.setCurrentActivity(this);
        IntentUtils.sendBroadcast(this, "action.face.detect.started", null);

        requestPermissions();

        initType();

        initButton();

        IntentUtils.registerReceiver(this, mCloseBroadcastReceiver, "action.face.detect.close");
    }

    private void initType() {
        String clubId = "";
        String extDeviceId = "";
        String deviceType = "";
        String deviceKey = "";
//        String clubId = "1086";
//        String extDeviceId = "HWCBCA9JH";
//        String deviceType = "力量器械";
//        String deviceKey = "power";

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String extDeviceIdTemp = bundle.getString("EXT_DEVICE_ID");
            String clubIdTemp = bundle.getString("CLUB_ID");
            String deviceTypeTemp = bundle.getString("DEVICE_TYPE");
            String deviceKeyTemp = bundle.getString("DEVICE_KEY");
            if (!TextUtils.isEmpty(clubIdTemp) && !TextUtils.isEmpty(extDeviceIdTemp)) {
                clubId = clubIdTemp;
                extDeviceId = extDeviceIdTemp;
            }
            if (!TextUtils.isEmpty(deviceTypeTemp) && !TextUtils.isEmpty(deviceKeyTemp)) {
                deviceType = deviceTypeTemp;
                deviceKey = deviceKeyTemp;
            }

            Log.d(TAG, "CLUB_ID = " + clubId + " EXT_DEVICE_ID = " + extDeviceId +
                    " TYPE = " + deviceType + " KEY = " + deviceKey);

            //俱乐部ID：1735，  设备ID：HWCBZWG62  设备类型选择跑步机
            Global.setSpString(Constants.Sp.SP_GROUP_ID, clubId);
            Global.setSpString(Constants.Sp.SP_DEVICE_ID, extDeviceId);
            Global.setSpInteger(Constants.Sp.SP_DELAY_TIME, Integer.valueOf("500"));

            Global.setSpString(Constants.Sp.DEVICE_TYPE, deviceType);
            Global.setSpString(Constants.Sp.SP_DEVICE_KEY, deviceKey);

            Global.setSpBoolean(Constants.Sp.IS_FIRST_RUN, false);
        }
    }

    final Runnable mDetectRunnable = new Runnable() {
        @Override
        public void run() {
            if (NetUtils.isConnected(FaceActivity.this)) {
                startDetect(FaceActivity.this);
            } else {
                mHandler.postDelayed(mDetectRunnable, 500);
            }
        }
    };

    private void startDetect(Activity activity) {
        if (!NetUtils.isConnected(FaceActivity.this)) {
            stopDetect("网络未连接！");
            return;
        }
        if (mFaceDetectComponent == null) {
            mFaceDetectErrorTv.setText("");
            mFaceDetectErrorTv.setVisibility(View.GONE);
            mFaceLogin.setVisibility(View.GONE);

            int scrW = ScreenUtils.getScreenWidth(activity);
            int startX = (int) (scrW / 2 - scrW * 0.5 / 2);
            mFaceDetectComponent = new FaceDetectComponent(activity,
                    startX + 30, 100, 0.4f);
            mFaceDetectComponent.setIsAllowTouch(false);
            mFaceDetectComponent.startDetect(new FaceDetectComponent.FaceDetectListener() {
                @Override
                public void success() {
                    Log.d(TAG, "face detect success");
                    mFaceDetectComponent.resumeDetect();
//                exitApp();
                }

                @Override
                public void failed(String json) {
                    Log.d(TAG, "face detect failed : " + json);
                    String error = "人脸识别失败\n\n\n识别未成功原因：\n" + json;
                    stopDetect(error);
                }
            });

            if (mFaceDetectTimer != null) {
                mFaceDetectTimer.cancel();
            }
            mFaceDetectTimer = new Timer();
            mFaceDetectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String error = "人脸识别失败\n\n识别未成功原因：\n" + "识别超时";
                    stopDetect(error);
                }
            }, 20000);
        }
    }

    private void stopDetect(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mFaceDetectComponent != null) {
                    try {
                        mFaceDetectComponent.stopDetect();
                    } catch (Exception e) {

                    }
                    mFaceDetectComponent = null;
                }

                mFaceDetectErrorTv.setText(error);
                mFaceDetectErrorTv.setVisibility(View.VISIBLE);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFaceDetectErrorTv.setText("");
                        mFaceDetectErrorTv.setVisibility(View.GONE);
                    }
                }, 10000);
                mFaceLogin.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initButton() {
        mHandler = new Handler();
//        mHandler.postDelayed(mDetectRunnable, 500);

        mClickModel = new ClickModel();
        mClickModel.setListener(new ClickModel.Listener() {
            @Override
            public void onClickCompleted() {
                exitApp();
            }
        });

        TextView exitBtn = findViewById(R.id.exit);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickModel.click();
            }
        });

        TextView settingBtn = findViewById(R.id.setting);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.startActivity(FaceActivity.this, FaceSettingActivity.class);
            }
        });

        mFaceDetectErrorTv = findViewById(R.id.face_detect_error);
        mFaceLogin = findViewById(R.id.face_login);
        mFaceLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetect(FaceActivity.this);
            }
        });
    }

    private BroadcastReceiver mCloseBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            exitApp();
        }
    };

    private void exitApp() {
        IntentUtils.sendBroadcast(this, "action.face.detect.open.device", null);
        System.exit(0);
    }

    @SuppressLint("CheckResult")
    private void requestPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(Permission permission) throws Exception {
                if (permission.granted) {
                    //权限被允许
                } else {
                    //权限未被允许
                }
                if (permission.shouldShowRequestPermissionRationale) {
                    //权限被拒绝没有选择不再询问
                } else {
                    //权限被允许选择了不再询问
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mClickModel != null) {
            mClickModel.cancel();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mCloseBroadcastReceiver != null) {
            unregisterReceiver(mCloseBroadcastReceiver);
        }
        if (mFaceDetectComponent != null) {
            try {
                mFaceDetectComponent.stopDetect();
            } catch (Exception e) {

            }
        }
    }
}
