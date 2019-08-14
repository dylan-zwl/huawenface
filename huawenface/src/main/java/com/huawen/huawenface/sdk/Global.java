package com.huawen.huawenface.sdk;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.fpa.mainsupport.core.android.GlobalApp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huawen.huawenface.R;
import com.huawen.huawenface.sdk.act.FaceDetectActivity;
import com.huawen.huawenface.sdk.bean.DeviceTypeData;
import com.huawen.huawenface.sdk.bean.DeviceTypeItemBean;
import com.huawen.huawenface.sdk.bean.ImageRecogData;
import com.huawen.huawenface.sdk.net.Callback;
import com.huawen.huawenface.sdk.net.FitoneResult;
import com.huawen.huawenface.sdk.net.OkGoNetAccess;
import com.huawen.huawenface.sdk.net.Result;
import com.huawen.huawenface.sdk.net.request.BaseRequest;
import com.huawen.huawenface.sdk.net.request.ImageRecogRequest;
import com.huawen.huawenface.sdk.utils.InternetUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Created by Chenchunpeng on 2014/7/27.
 *
 * @auther Chenchunpeng
 * @email chencp@shengshibotong.com
 */
public class Global extends GlobalApp {
    public static final String TAG = "Global";
    private static String mCurrentChatUserId = "";
    private String tag = Global.class.getSimpleName();
    private boolean mLoadingArea;
    private static Global instance;
    private boolean debug = true;
    public static FaceDB mFaceDB;
    Uri mImage;
    /**
     * 程序运行期间产生的文件，缓存根目录
     */
    public static final String ROOT_DIR_PATH_BASE = "/cics";
    /**
     * 下载图片保存目录
     */
    public static final String PIC_DIR_PATH = ROOT_DIR_PATH_BASE + "/pic";

    public static Global getInstance() {
        return instance;
    }

    public static String getPicRootPath() {
        String picPath = Environment.getExternalStorageDirectory() + PIC_DIR_PATH;
        File file = new File(picPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return picPath;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initGlobal();
        instance = this;
        mFaceDB = new FaceDB(Config.File.cacheDir);
        mImage = null;
    }

    public void setCaptureImage(Uri uri) {
        mImage = uri;
    }

    public Uri getCaptureImage() {
        return mImage;
    }

    /**
     * @param path
     * @return
     */
    public static Bitmap decodeImage(String path) {
        Bitmap res;
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inSampleSize = 1;
            op.inJustDecodeBounds = false;
            //op.inMutable = true;
            res = BitmapFactory.decodeFile(path, op);
            //rotate and scale.
            Matrix matrix = new Matrix();

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            }

            Bitmap temp = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, true);
            Log.d("com.arcsoft", "check target Image:" + temp.getWidth() + "X" + temp.getHeight());

            if (!temp.equals(res)) {
                res.recycle();
            }
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initGlobal() {
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
        com.fpa.mainsupport.core.utils.Log.setOpen(debug);

        initDB();
        initMap();
        initPath();
//        initRongCloud();
        initApp();
        initOkGo();
//        CrashReport.initCrashReport(getApplicationContext(), "bfb341c70d", false);
        try {
            getInitData(null);
        } catch (Exception e) {
            Toast.makeText(this, "初始化失败", Toast.LENGTH_SHORT).show();
        }
    }

    public void getInitData(final com.fpa.mainsupport.core.Callback callback) {
        if (!InternetUtils.hasInternet(this)) {
            return;
        }
        OkGoNetAccess.get(Config.REAL_FITONE_BASE + "/cgi/getDeviceType", new BaseRequest().toParams(BaseRequest.class), DeviceTypeData.class, new Callback() {
            @Override
            public void callback(Result result) {
                if (result instanceof DeviceTypeData) {

                    DeviceTypeData fitoneResult = (DeviceTypeData) result;
                    if (fitoneResult.isSuccess()) {
                        Global.setSpString(Constants.Sp.DEVICE_TYPE, new Gson().toJson(fitoneResult.getData()));
                        if (callback != null) {
                            callback.call(true);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                        if (callback != null) {
                            callback.call(false);
                        }
                    }

                }
            }
        });
    }

    public List<DeviceTypeItemBean> getDeviceTypeList() {
        String deviceType = Global.getSpString(Constants.Sp.DEVICE_TYPE, "");
        if (TextUtils.isEmpty(deviceType)) {
            return null;
        } else return new Gson().fromJson(deviceType, new TypeToken<List<DeviceTypeItemBean>>() {
        }.getType());
    }

    private void initOkGo() {
        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
        HttpHeaders headers = new HttpHeaders();
//        headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
//        headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
//        params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
//        params.put("commonParamsKey2", "这里支持中文参数");
        //----------------------------------------------------------------------------------------//

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志
        //第三方的开源库，使用通知显示当前请求的log，不过在做文件下载的时候，这个库好像有问题，对文件判断不准确
        //builder.addInterceptor(new ChuckInterceptor(this));

        //超时时间设置，默认60秒
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //全局的连接超时时间

        //自动管理cookie（或者叫session的保持），以下几种任选其一就行
        //builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));              //使用数据库保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保持cookie，app退出后，cookie消失

        //https相关设置，以下几种方案根据需要自己设置
        //方法一：信任所有证书,不安全有风险
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        //方法二：自定义信任规则，校验服务端证书
        HttpsUtils.SSLParams sslParams2 = HttpsUtils.getSslSocketFactory(new SafeTrustManager());
        //方法三：使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
        //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams4 = HttpsUtils.getSslSocketFactory(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"));
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
        builder.hostnameVerifier(new SafeHostnameVerifier());

        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/
        OkGo.getInstance().init(this)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                .addCommonHeaders(headers)                      //全局公共头
                .addCommonParams(params);                       //全局公共参数
    }

    /**
     * 这里只是我谁便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
     * 这里只是我谁便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
     * 这里只是我谁便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
     * 重要的事情说三遍，以下代码不要直接使用
     */
    private class SafeTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                for (X509Certificate certificate : chain) {
                    certificate.checkValidity(); //检查证书是否过期，签名是否通过等
                }
            } catch (Exception e) {
                throw new CertificateException(e);
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * 这里只是我谁便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
     * 这里只是我谁便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
     * 这里只是我谁便写的认证规则，具体每个业务是否需要验证，以及验证规则是什么，请与服务端或者leader确定
     * 重要的事情说三遍，以下代码不要直接使用
     */
    private class SafeHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            //验证主机名是否匹配
            //return hostname.equals("server.jeasonlzy.com");
            return true;
        }
    }


    private void initApp() {
//        MobclickAgent.setCatchUncaughtExceptions(true);
//        sendCustomBroadcast(com.fpa.wxpay.Constants.APPREGISTER_ACTION,null);
//        if (!mLoadingArea && !Global.getSpBoolean(Constants.Sp.isLoadedArea, false))
//            initChinaAreaNeeded();
    }


    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    private void initPath() {
        File file;
        String[] dirs = new String[]{
                Config.File.savePath, Config.File.shareDir,
                Config.File.cacheDir,
                Config.File.IMAGE_PATH,
        };
        for (String dir : dirs) {
            file = new File(dir);
            if (Config.File.cacheDir.equals(dir))
                clearCache(file);
            if (!file.exists())
                file.mkdirs();
        }
    }

    private void clearCache(final File cacheDir) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File[] cacheFiles = cacheDir.listFiles();
                if (cacheFiles != null && cacheFiles.length > 0)
                    for (File cacheFile : cacheFiles) {
                        cacheFile.delete();
                    }
            }
        }).start();

    }

    private void initMap() {
//        SDKInitializer.initialize(this);
    }


    private void initDB() {
//        StoreRegister.registerD/omains(LoginModel.class, ChinaArea.class, ConversationStatus.class, QYTipsModel.class);
//        try {
////            checkDB();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public boolean checkDB() throws IOException {
        File file = new File(Config.Database.cityDbPath);
        boolean isE;
        isE = file.exists();
        if (!isE)
            copyDatabase();
        return isE;
    }

    /**
     * @throws IOException
     */
    private void copyDatabase() throws IOException {

        File file = new File(Config.Database.dataDir);
        if (!file.exists())
            file.mkdir();
        file = new File(Config.Database.cityDbPath);
        if (!file.exists()) {
            file.createNewFile();
        }
        byte[] buffer = new byte[1024 * 172];
        FileOutputStream out = new FileOutputStream(Config.Database.cityDbPath);
        InputStream is = getResources().getAssets().open(Config.Database.cityDbName);
        is.read(buffer);//
        out.write(buffer);
        out.close();
        is.close();
    }


    public static void setCurrentChatUserId(String chatUserId) {
        mCurrentChatUserId = chatUserId;
    }

    public static String getCurrentChatUserId() {
        return mCurrentChatUserId;
    }


    protected void initPicasso() {
//        if (mPicasso == null)
//            mPicasso = Picasso.with(this);
    }


    public static String getCacheCompressPath(String filePath) {
        return Config.File.cacheDir + "compress_" + new File(filePath).getName();
    }


}
