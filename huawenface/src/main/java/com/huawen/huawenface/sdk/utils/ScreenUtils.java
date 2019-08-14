package com.huawen.huawenface.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by ZengYinan.
 * Date: 2018/9/13 9:38
 * Email: 498338021@qq.com
 * Desc:
 */
public class ScreenUtils {
    /**
     * 是否使屏幕常亮
     *
     * @param activity
     */
    public static void keepScreenLongLight(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    public static int[] getScreen(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dms = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dms);
        return new int[]{dms.widthPixels, dms.heightPixels};
    }


    public static int getScreenWidth(Context context) {

        return getScreen(context)[0];
    }

    public static int getScreenHeight(Context context) {
        return getScreen(context)[1];
    }


}
