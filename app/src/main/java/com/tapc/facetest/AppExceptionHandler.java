package com.tapc.facetest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tapc.facetest.utils.IntentUtils;

import java.lang.Thread.UncaughtExceptionHandler;

public class AppExceptionHandler implements UncaughtExceptionHandler {

    private UncaughtExceptionHandler defaultUEH;
    private Context mContext;
    private Activity mActivity;

    public AppExceptionHandler(Context context) {
        mContext = context;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void setCurrentActivity(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        /**
         * 程序异常�?��处理，停止下�?
         */
        Log.d("AppExceptionHandler", "app error exit");
        if (mActivity != null) {
            mActivity.finish();
        }
        IntentUtils.sendBroadcast(mContext, "action.face.detect.open.device", null);
        Intent intent = new Intent();
        intent.setClassName(mContext.getPackageName(), FaceActivity.class.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
        System.exit(0);
        // defaultUEH.uncaughtException(thread, ex);
    }

}
