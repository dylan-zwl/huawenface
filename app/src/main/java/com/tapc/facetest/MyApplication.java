package com.tapc.facetest;

import com.huawen.huawenface.sdk.Global;

public class MyApplication extends Global {

    public static AppExceptionHandler sAppExceptionHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppExceptionHandler = new AppExceptionHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(sAppExceptionHandler);
    }
}
