package com.fpa.mainsupport.core.android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.support.multidex.MultiDex;
//import android.support.multidex.MultiDexApplication;
import android.view.WindowManager;


/**
 *
 */
public class GlobalApp extends Application {
    private static final String SP_DATA = "huawen";
    private static Context context;
    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    /**
     * 分包
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }
    public WindowManager.LayoutParams getWmParams() {
        return wmParams;
    }

    public static Context getContext() {
        return context;
    }

    public static void setSpString(String key, String value) {
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(SP_DATA, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getSpString(String key, String def) {
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(SP_DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, def);
    }

    public static long getSpLong(String key, long def) {
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(SP_DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, def);
    }

    public static void setSpLong(String key, long value) {
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(SP_DATA, Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(key, value).apply();
    }

    public static int getSpInteger(String key, int def) {
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(SP_DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, def);
    }

    public static void setSpInteger(String key, int value) {
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(SP_DATA, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public static void setSpBoolean(String key, boolean value) {
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(SP_DATA, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static boolean getSpBoolean(String key, boolean def) {
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(SP_DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, def);
    }

    public static void setSpFloat(String key, float value) {
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(SP_DATA, Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat(key, value).apply();
    }
    public static float getSpFloat(String key, float def) {
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences(SP_DATA, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(key, def);
    }
    public static void sendCustomBroadcast(String action, Bundle extras) {
        Intent intent = new Intent(action);
        if (null != extras)
            intent.putExtras(extras);
        sendCustomBroadcast(intent);
    }

    public static void sendCustomBroadcast(Intent intent) {
        getContext().sendBroadcast(intent);
    }


}
