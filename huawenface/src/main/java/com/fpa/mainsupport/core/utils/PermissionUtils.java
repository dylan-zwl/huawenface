package com.fpa.mainsupport.core.utils;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchunpeng on 2018/9/29.
 */
public class PermissionUtils {
    private static PermissionUtils mInstance;
    private final AppCompatActivity mContext;

    private PermissionUtils(AppCompatActivity context){
        this.mContext=context;
    }
    public static PermissionUtils getInstance(AppCompatActivity context){
        if(mInstance==null){
            mInstance=new PermissionUtils(context);
        }
        return mInstance;
    }

    private PermissionListener mPermissionListener;

    /**
     * 申请运行时权限
     */
    public void requestRuntimePermission(String[] permissions, PermissionListener permissionListener) {
        mPermissionListener = permissionListener;
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(mContext, permissionList.toArray(new String[permissionList.size()]), 1);
        } else {
            permissionListener.onGranted();
        }
    }

    /**
     * @description: 权限申请回调的接口
     */
    public interface PermissionListener {

        void onGranted();

        void onDenied(List<String> deniedPermissions);
    }

}
