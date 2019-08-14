package com.huawen.huawenface.sdk;

import android.os.Environment;

/**
 * Created by Chenchunpeng on 2015-03-04 上午10点.
 */
public class Config {
    public static final String TEST_BASE = "http://api.ublog.top";//http://47.100.17.125:1010/

    public static final String DEV_BASE = "http://xiangqin.siaslzh.com";
    public static final String REAL_BASE = "http://47.105.236.43/api";
    /**
     * fitone 的app的接口api
     */
    public static final String REAL_FITONE_BASE = "http://app.fitoneapp.com";
    public static final String Base_Url = REAL_BASE;
    public static final String API_URL = Base_Url + "/";

    public static final String API_SECRET = "6ImkpyBqqj7VzARm";
    public static final String packageName = Global.getContext().getPackageName();
    public static final int SERVER_WORK_TIME_START =9 ;
    public static final int SERVER_WORK_TIME_END = 18;
    public static final int MAX_COUNT_OF_PIC_IMAGE = 9;
    public static final String PROTOCOLE_URL = Base_Url+"/register.html";

    public static final String UPLOAD_IMAGE_URL = API_URL + "Danger/ImageUpload";//图片上传url

    public static boolean enableTruslucent = true;
    public static String verfiyCodeUrl = API_URL + "randCodeImage?jsessionid=%s";
    public static String alipayNotifyUrl = API_URL + "pay/alipay/payNotify.do";
    public static final String API_D="http://1.fpaserver.applinzi.com/apiweather/d";
    public static class Database {
        public final static String dataDir = "/data/data/" + packageName + "/databases/";
        public static String cityDbName="city.db";
        public static final String cityDbPath=dataDir+"city.db";

    }

    public static final class File {
        public static final String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        public static final String baseFolder = sdcard + "/huawen";
        private static String locLogPath = baseFolder + "/log/";
        public static String screenShotsPath = baseFolder + "/ScreenShots/";
        public static String savePath = baseFolder + "/save/";
        public static final String shareDir = baseFolder + "/share/";
        public static final String cacheDir = baseFolder + "/cache/";

        public static final String IMAGE_PATH=baseFolder+"/image/";
    }

    public static final class Constants {
    }

}
