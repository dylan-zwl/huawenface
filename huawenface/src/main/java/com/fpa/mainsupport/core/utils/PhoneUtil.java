package com.fpa.mainsupport.core.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.fpa.mainsupport.core.android.GlobalApp;

/**
 * class for collect information of the phone
 */
public final class PhoneUtil {
    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }
    // 检查是否有网络
    public static boolean hasInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) GlobalApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {

            return true;
        } else {
            return false;
        }
    }
    public static String getDeviceId(final Context context) {
        String deviceId = "";

        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return StringUtils.md5(deviceId + androidId);
    }

    /**
     * open the app of phone call
     * <br>PhoneUtil.tel(Uri.parse("tel:"+123456)));
     *
     * @param uri
     */
    public static void tel(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        GlobalApp.getContext().startActivity(intent);
    }

    /**
     * PhoneUtil.message(Uri.parse("smsto:"+"111111")
     *
     * @param uri
     */
    public static void message(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        GlobalApp.getContext().startActivity(intent);
    }

    /**
     * open the app of phone call with the number and call the number<br/>
     * <b>Attention:<i>need use-permission  android:name="android.permission.CALL_PHONE"   </b>
     *
     * @param context
     * @param uri     the uri of this call's number .<b>example:<i>(Uri.parse("tel:"+number))</i></b>
     */
    public static void tel(Context context, Uri uri) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        boolean telephonyEnabled = tm != null && tm.getSimState() == TelephonyManager.SIM_STATE_READY;
        if (!telephonyEnabled) {
            Toast.makeText(context,"have no sim card", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

    public static String getImei(final Context context) {
        final String[] imei = {""};


//        PermissionUtils.getInstance((BaseActivity) context).requestRuntimePermission(new String[]{Manifest.permission.READ_PHONE_STATE}, new PermissionUtils.PermissionListener() {
//            @Override
//            public void onGranted() {
//                TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//                imei[0] =mTelephonyManager.getDeviceId();
//            }
//
//            @Override
//            public void onDenied(List<String> deniedPermissions) {
//
//            }
//        });
////
        if ( Build.VERSION.SDK_INT< Build.VERSION_CODES.LOLLIPOP ) {
            TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                imei[0] = mTelephonyManager.getDeviceId();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(imei[0])) {
            imei[0] = Settings.Secure.getString(GlobalApp.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        }
        if (TextUtils.isEmpty(imei[0])) {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            imei[0] = wm.getConnectionInfo().getMacAddress();
            imei[0] = imei[0].replace(":", "");
        }

        return imei[0];
    }

    /**
     * get the device id
     *
     * @return device id
     */
    public static String getIMEI() {
        TelephonyManager tm = (TelephonyManager) GlobalApp.getContext().getSystemService(Context.TELEPHONY_SERVICE);
//        String line1Number = tm.getLine1Number();  //手机号码
//        String simSerialNumber = tm.getSimSerialNumber();//sim卡号
//        String subscriberId = tm.getSubscriberId();//IMSI号
        return tm.getDeviceId();
    }

    /**
     * get the mobile's number
     *
     * @return mobile's number
     */
    public static String getMobileNumber() {
        TelephonyManager tm = (TelephonyManager) GlobalApp.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getLine1Number();
    }

    /**
     * get the mobile model
     *
     * @return mobile model
     */
    public static String getMobileModel() {
        return Build.MODEL; // 手机型号
    }

    /**
     * get the mobile's release system version
     *
     * @return
     */
    public static String getMobileVersionRelease() {
        return Build.VERSION.RELEASE;//系统版本
    }

    /**
     * get the mobile's brand
     *
     * @return
     */
    public static String getMobileBrand() {
        return Build.BRAND;//手机品牌
    }

    /**
     * get the mobile's sdk version
     *
     * @return
     */
    public static String getMobileVersionSdk() {
        return Build.VERSION.SDK; //SDK版本
    }

    /**
     * make the phone vibrate
     *
     * @param activity the activity called the function
     * @param repeat   need repeat
     * @param pattern
     */
    public static void vibrator(Activity activity, boolean repeat, long... pattern) {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, repeat ? 2 : -1);
    }

    public static void toggleInputMethod(Context context, boolean toggle) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
        if (!toggle) {
            if (imm.isActive())  //一直是true
                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                        InputMethodManager.HIDE_NOT_ALWAYS);
        } else imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_FORCED);
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void hideInputMethod(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen) {
            View focusView = activity.getCurrentFocus();
            if (focusView == null)
                return;
            IBinder token = focusView.getWindowToken();
            if (token != null)
                imm.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public static void showInputMethod(Activity activity, View view) {
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view, 0);
    }


}
