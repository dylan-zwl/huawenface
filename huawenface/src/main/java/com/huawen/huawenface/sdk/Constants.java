package com.huawen.huawenface.sdk;

import android.os.Environment;

/***
 * Created by Chenchunpeng on 2015-03-04 上午10点.
 **/
public class Constants {
     static final String FILEPATH = Environment
            .getExternalStorageDirectory().getAbsolutePath() +"/huawen"+"/";
    //apk文件名
     String APKNAME = "cics.apk";
     float MONEY_NEED_PAY_TAKE_PART_IN_ACTIVITY = 2.9f;
    /***
     * 消息体带上附加参数leaveMessage表示留言，不需要自动回复
     **/
     String LEAVE_MESSAGE = "leaveMessage";



    public interface Sp {

        String IS_FIRST_RUN = "sp_is_first";
        /**
         * 设备type名字
         */
        String DEVICE_TYPE = "device_type_name";
        /**
         * 设备type的key
         */
        String SP_DEVICE_KEY = "device_type_key";
        String SP_DEVICE_ID = "device_id";//设备id
        String SP_GROUP_ID = "group_id";
        String SP_DELAY_TIME = "delay_time";//延时启动人脸识别的时间
        String SCALE = "scale";//识别比例，越小，头像越大
        String BIG_PIC = "big_pic";
    }

    public interface  Action {
    }

    public class Key {
         String LANGUAGE = "language";

    }

    public static class Resources {
    }

    public class UserInfo {

         String USER_NAME = "user_name";
    }



}
