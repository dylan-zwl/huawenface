package com.tapc.facetest.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.List;

/**
 * Created by Administrator on 2016/11/13.
 */

public class IntentUtils {

    private IntentUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void startActivity(Context context, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class<?> cls, Bundle bundle, int flag) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setFlags(flag);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    public static void startActivityForResult(Activity activity, Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    public static void registerReceiver(Context context, BroadcastReceiver receiver, String... actionList) {
        IntentFilter filter = new IntentFilter();
        for (String action : actionList) {
            filter.addAction(action);
        }
        context.registerReceiver(receiver, filter);
    }

    public static void sendBroadcast(Context context, String action, Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.sendBroadcast(intent);
    }

    public static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    public static void startApp(Context context, String packageName) {
        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(LaunchIntent);
    }

    /**
     * 判断当前应用程序处于前台还是后台
     */
    public static boolean isApplicationBroughtToBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTopActivity(Context context, Class cls) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName app = am.getRunningTasks(1).get(0).topActivity;
        return app.getClassName().equals(cls.getName());
    }

}
