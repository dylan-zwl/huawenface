package com.huawen.huawenface.sdk.act;

import android.Manifest;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Base64;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.fpa.mainsupport.core.utils.Log;
import com.fpa.mainsupport.core.utils.PhoneUtil;
import com.google.gson.Gson;
import com.guo.android_extend.java.ExtByteArrayOutputStream;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;
import com.huawen.huawenface.R;
import com.huawen.huawenface.sdk.Config;
import com.huawen.huawenface.sdk.Constants;
import com.huawen.huawenface.sdk.FaceDB;
import com.huawen.huawenface.sdk.Global;
import com.huawen.huawenface.sdk.bean.DataConfig;
import com.huawen.huawenface.sdk.bean.DeviceTypeItemBean;
import com.huawen.huawenface.sdk.bean.FaceConfig;
import com.huawen.huawenface.sdk.bean.ImageRecogData;
import com.huawen.huawenface.sdk.bean.ResultData;
import com.huawen.huawenface.sdk.bean.UserInfoData;
import com.huawen.huawenface.sdk.net.Callback;
import com.huawen.huawenface.sdk.net.OkGoNetAccess;
import com.huawen.huawenface.sdk.net.Result;
import com.huawen.huawenface.sdk.net.Sign;
import com.huawen.huawenface.sdk.net.request.FitOneOpenDeviceRequest;
import com.huawen.huawenface.sdk.net.request.FitOneRegisterRequest;
import com.huawen.huawenface.sdk.ui.CustomeAbsLoop;
import com.huawen.huawenface.sdk.ui.FaceRectView;
import com.huawen.huawenface.sdk.utils.ImageUtils;
import com.huawen.huawenface.sdk.utils.ScreenUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class FaceDetectActivity extends BaseActivity implements SurfaceHolder.Callback, CameraSurfaceView.OnCameraListener, Camera.AutoFocusCallback {
    private static final String TAG = "HuawenFace";//FaceDetectActivity.class.getSimpleName();
    ViewGroup mLayoutMain = null;
    SurfaceView mSurfaceViewCamera = null;
    SurfaceHolder mSurfaceHolderCamera = null;
    Camera mCamera1 = null;//摄像头
    RelativeLayout mLayoutRealMain = null;
    private long mHeartBeatTimer = 30;
    int mCameraPreviewLeft = 0;
    int mCameraPreviewTop = 0;
    int mCameraPreviewWidth = 0;
    int mCameraPictureWidth = 0;
    private String _mHardwareVersion = "";

    FileInputStream mInputStream;


    Handler handler = new Handler();

    private RelativeLayout rl_scan_view;
    private CameraSurfaceView mSurfaceView;
    private CameraGLSurfaceView mGLSurfaceView;
    private Camera mCamera2;
    private int mWidth, mHeight, mFormat;

//    private RelativeLayout rl_loading;

    AFT_FSDKVersion version = new AFT_FSDKVersion();
    AFT_FSDKEngine engine = new AFT_FSDKEngine();
    List<AFT_FSDKFace> result = new ArrayList<>();

    int mCameraID;
    int mCameraRotate;
    boolean mCameraMirror;
    byte[] mImageNV21 = null;
    FRAbsLoop mFRAbsLoop = null;
    AFT_FSDKFace mAFT_FSDKFace = null;

    private OkHttpClient client;
    private FaceRectView faceRectView;
    private DataConfig dataConfig;
    private int sw;
    private int sh;
    private ImageRecogData.ImageRecogItem mPinganPersonInfo;


    @Override
    protected void onDestroy() {
        if (mFRAbsLoop != null) {
            mFRAbsLoop.resumeThread();
            mFRAbsLoop.shutdown();
        }
        AFT_FSDKError err = engine.AFT_FSDK_UninitialFaceEngine();
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if (getNumberOfCameras() == 0) {
            showToast(R.string.have_no_camera);
            finish();
        }
        if (!PhoneUtil.hasInternet()) {
            showToast(R.string.have_no_internet_attack);
            finish();
        }
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_face_scan);
        sw = ScreenUtils.getScreenWidth(getApplicationContext());
        sh = ScreenUtils.getScreenHeight(getApplicationContext());
        dataConfig = new DataConfig();
        dataConfig.setGroupId("nsVGj9jCFBUp.1735");
        dataConfig.setWidth(sw);
        dataConfig.setHeight(sh);
        dataConfig.setWaitSecond(1);
        dataConfig.setSensitivity(7);
        dataConfig.setMarginLeft(0);
        dataConfig.setMarginTop(0);
        dataConfig.setScale(16);
        dataConfig.setRecognitionDegree(65);

        initFace();
        FaceDetectActivityPermissionsDispatcher.startPreviewWithPermissionCheck(FaceDetectActivity.this);
        if (Global.getSpBoolean(Constants.Sp.IS_FIRST_RUN, true)) {

            showDeviceInputDialog();


            mFRAbsLoop.pauseThread();
        } else {
        }

    }

    private void beforeShowChooseDialog() {
        List<DeviceTypeItemBean> deviceTypeItemBeans = Global.getInstance().getDeviceTypeList();

        if (deviceTypeItemBeans != null) {
            showChooseDialog(deviceTypeItemBeans);
        } else {
            Global.getInstance().getInitData(new com.fpa.mainsupport.core.Callback() {
                @Override
                public void call(Object[] values) {
                    boolean result = (boolean) values[0];
                    if (result) {
                        showChooseDialog(Global.getInstance().getDeviceTypeList());
                    }
                }
            });
        }
    }

    private void showChooseDialog(final List<DeviceTypeItemBean> deviceTypeItemBeans) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.dialog_choose_device_title);
        final String[] Items = new String[deviceTypeItemBeans.size()];

        int index = 0;
        for (DeviceTypeItemBean item : deviceTypeItemBeans) {
            Items[index++] = item.getContent();
        }
        builder.setItems(Items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Global.setSpString(Constants.Sp.DEVICE_TYPE, deviceTypeItemBeans.get(i).getContent());
                Global.setSpString(Constants.Sp.SP_DEVICE_KEY, deviceTypeItemBeans.get(i).getKey());
                Global.setSpBoolean(Constants.Sp.IS_FIRST_RUN, false);
                mFRAbsLoop.resumeThread();
//                initFace();
//                FaceDetectActivityPermissionsDispatcher.startPreviewWithPermissionCheck(FaceDetectActivity.this);
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeviceInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.dialog_set_device_info);

        View view = View.inflate(this, R.layout.dialog_device_info_set, null);
        final AppCompatEditText groupIdINputView = view.findViewById(R.id.dialog_input_group_id);
        final AppCompatEditText deviceIdInputView = view.findViewById(R.id.dialog_input_device_id);
        builder.setView(view);
        builder.setCancelable(true);
        groupIdINputView.requestFocus();
        builder.setPositiveButton(R.string.confirm, null);

        final AlertDialog dialog = builder.create();

        //为了防止 getButton() 为空,需要在 OnShowListener 里设置事件
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
                //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (TextUtils.isEmpty(groupIdINputView.getText().toString())) {
                                    showToast(R.string.hint_group_id_input);
                                    return;
                                }
                                if (TextUtils.isEmpty(deviceIdInputView.getText().toString())) {
                                    showToast(R.string.hint_device_id_input);
                                    return;
                                }
                                Global.setSpString(Constants.Sp.SP_DEVICE_ID, deviceIdInputView.getText().toString());
                                Global.setSpString(Constants.Sp.SP_GROUP_ID, groupIdINputView.getText().toString());

                                beforeShowChooseDialog();
                                dialog.dismiss();
                            }
                        });
            }
        });

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#c6174e"));

    }

    class FRAbsLoop extends CustomeAbsLoop {

        AFR_FSDKVersion version = new AFR_FSDKVersion();
        AFR_FSDKEngine engine = new AFR_FSDKEngine();

        @Override
        public void setup() {
            AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
            error = engine.AFR_FSDK_GetVersion(version);
            saveLogToWeb("setup error:" + error.getCode());
        }

        @Override
        public void loop() {
            SystemClock.sleep(dataConfig.waitSecond * 1000);
            if (mImageNV21 != null) {
                byte[] data = mImageNV21;
                YuvImage yuv = new YuvImage(data, ImageFormat.NV21, mWidth, mHeight, null);
                ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
                yuv.compressToJpeg(mAFT_FSDKFace.getRect(), 80, ops);
                //获取图片并注册人脸
                final Bitmap bmp = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);
                mImageNV21 = null;
                if (bmp != null) {
                    Matrix m = new Matrix();
                    m.setRotate(-90, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
                    final Bitmap bm1 = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
//                    bytesToImageFile(ImageUtils.Bitmap2Bytes(bm1));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            compareWithWebServer(bm1);
                        }
                    });
                }
            }
        }

        @Override
        public void over() {
            AFR_FSDKError error = engine.AFR_FSDK_UninitialEngine();
        }
    }


    //获取摄像头个数
    public int getNumberOfCameras() {
        int cameraCount = Camera.getNumberOfCameras();
        Log.e(TAG, "cameraCount:" + cameraCount);
        return cameraCount;
    }

    //显示预览框(第一次开启的时候调用)
    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    public void startPreview() {
        String json2 = "{\"log\":\"显示预览框(第一次开启的时候调用)\"}";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                dataConfig = new Gson().fromJson(json, DataConfig.class);
                mWidth = dataConfig.width;
                mHeight = dataConfig.height;


                mCameraID = getNumberOfCameras() > 1 ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
                mCameraRotate = 270;
                mCameraMirror = true;

                mFormat = ImageFormat.NV21;

                mSurfaceView.setOnCameraListener(FaceDetectActivity.this);
                mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, mCameraMirror, mCameraRotate);
                mSurfaceView.debug_print_fps(false, false);

                if (dataConfig.getScale() < 2) {
                    dataConfig.setScale(2);
                }
                if (dataConfig.getScale() > 32) {
                    dataConfig.setScale(32);
                }

                AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, dataConfig.getScale(), 5);
                err = engine.AFT_FSDK_GetVersion(version);

                faceRectView.setFaceViewSize(dataConfig.width, dataConfig.height);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dataConfig.width, dataConfig.height);
                layoutParams.leftMargin = dataConfig.marginLeft;
                layoutParams.topMargin = dataConfig.marginTop;
                rl_scan_view.setLayoutParams(layoutParams);
                if (mFRAbsLoop == null) {
                    mFRAbsLoop = new FRAbsLoop();
                    mFRAbsLoop.start();
                } else {
                    mFRAbsLoop.resumeThread();
                }
                rl_scan_view.setVisibility(View.VISIBLE);
            }
        });
    }


    private long lastRequestTime = 0;

    private static byte[] File2byte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public void postImageToWebServer(String url, String group_id, Bitmap bitmap, String userId, long appId, String secretId, String secretKey, String bucketName, long expired, okhttp3.Callback callback) {

        String sign = null;
        try {
            sign = Sign.appSign(appId, secretId, secretKey, bucketName, expired).replace("\n", "");
        } catch (Exception e) {
            e.printStackTrace();
            sign = "";
        }
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("appid", appId + "")
                .addFormDataPart("group_id", group_id)
                .addFormDataPart("image", new Date().getTime() + ".jpg", RequestBody.create(MEDIA_TYPE_PNG, ImageUtils.Bitmap2Bytes(bitmap)));
//                .addFormDataPart("url", "http://i9.taou.com/maimai/p/4425/6762_49_62QooXqTEXCLj2-a160");
        MultipartBody requestBody = builder.build();
        //构建请求
        okhttp3.Request request = new okhttp3.Request.Builder()
                .addHeader("host", "service.image.myqcloud.com")
                .addHeader("authorization", sign)
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }


    private void compareWithWebServer(final Bitmap bmp) {
        android.util.Log.e("ZYN", "------" + (System.currentTimeMillis() - lastRequestTime) / 1000);
        if (lastRequestTime != 0 && (System.currentTimeMillis() - lastRequestTime) / 1000 < dataConfig.sensitivity) {
            return;
        }
        mFRAbsLoop.pauseThread();
        lastRequestTime = System.currentTimeMillis();


        postImageToWebServer(FaceConfig.URL, dataConfig.groupId, bmp, FaceConfig.USER_ID, FaceConfig.APPID, FaceConfig.SECRETID, FaceConfig.SECRETKEY, FaceConfig.BUCKERNAME, 5 * 60, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e("ZYN", "error:" + e.getMessage());
                final String json = "{\"log\":\"识别出错：" + e.getMessage() + "\"}";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(json);
                    }
                });
                saveLogToWeb(json);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                final String result = response.body().string();
                android.util.Log.e("ZYN", result);
                final ResultData resultData = new Gson().fromJson(result, ResultData.class);
                if (resultData == null || resultData.getCode() != 0 || resultData.getData().getCandidates().size() <= 0 || resultData.getData().getCandidates().get(0).getConfidence() < dataConfig.getRecognitionDegree()) {
                    final String json = "识别成功，识别率小于设定阈值";
                    saveLogToWeb(json);
                    mFRAbsLoop.resumeThread();
                    lastRequestTime = System.currentTimeMillis() - (dataConfig.sensitivity + 2) * 1000;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            showToast(json);
                            showRecogResultDialog(json, resultData, "");

                        }
                    });
                } else {
                    //调用js方法
                    final String json = "{\"log\":\"识别成功，识别率大于设定阈值\"}";
                    saveLogToWeb(json);
//                    mFRAbsLoop.resumeThread();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            hidePreview();
//                            showToast(json);
                            final String imagePath = ImageUtils.saveBitmapTofile(bmp, System.currentTimeMillis() + ".jpg");
                            final String picStr = Base64.encodeToString(File2byte(imagePath), Base64.NO_WRAP);
                            final String imageFormat = "jpeg";
                            String imageBase64 = "data:image/" + imageFormat + ";base64," + picStr;
                            showRecogResultDialog(json, resultData, imageBase64);

                            //mWebViewMain.loadUrl("https://www.baidu.com");
                            android.util.Log.e("ZYN", "SUCCESS");
                        }
                    });
                }
            }
        });
    }

    private void showRecogResultDialog(String json, ResultData resultData, final String imageBase64) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(FaceDetectActivity.this);
//        builder.setIcon(R.drawable.ic_launcher);
//        builder.setTitle(R.string.dialog_title_recog_result);
//        builder.setPositiveButton(R.string.confirm, null);
        ResultData.DataBean data = resultData.getData();
        List<ResultData.DataBean.CandidatesBean> cos = data.getCandidates();

        String resultStr = "";
        boolean isNewUser;
        String personId = "";
        if (cos != null && cos.size() > 0) {
//            resultStr="最大识别率："+cos.get(0).getConfidence()+",faceId:"+cos.get(0).getFace_id()+",personId:"+cos.get(0).getPerson_id();
//            resultStr+="最低识别率："+cos.get(cos.size()-1).getConfidence()+",faceId:"+cos.get(cos.size()-1).getFace_id()+",personId:"+cos.get(cos.size()-1).getPerson_id();
            isNewUser = cos.get(0).getConfidence() < 85;
            if (isNewUser) {
                resultStr += "用户库不存在，即将注册新用户";
                postPingAnData(imageBase64);

            } else {
                String personIdStr = cos.get(0).getPerson_id();
                String[] personIdStrs = personIdStr.split("\\.");
                if (personIdStrs.length == 3) {
                    getUserInfo(personIdStrs[2]);
                }
                return;
            }
        } else {
            resultStr = "没有识别结果";
        }
        showToast(resultStr);

//        builder.setMessage(json + ",data:" + resultStr);
//        final AlertDialog dialog = builder.create();
//
//        //为了防止 getButton() 为空,需要在 OnShowListener 里设置事件
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(final DialogInterface dialogInterface) {
//                //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
//                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
//                        .setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                mFRAbsLoop.resumeThread();
//                                dialog.dismiss();
//                                postPingAnData(imageBase64);
//                            }
//                        });
//            }
//        });
//        dialog.show();
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#c6174e"));
    }


//    private void compareWithWebServer(Bitmap bmp) {
//        Log.e(TAG, "------" + (System.currentTimeMillis() - lastRequestTime) / 1000);
//        if (lastRequestTime != 0 && (System.currentTimeMillis() - lastRequestTime) / 1000 < dataConfig.sensitivity) {
//            return;
//        }
//        mFRAbsLoop.pauseThread();
//        lastRequestTime = System.currentTimeMillis();
//
//
//        final String imagePath = ImageUtils.saveBitmapTofile(bmp, System.currentTimeMillis() + ".jpg");
//        final String picStr = Base64.encodeToString(File2byte(imagePath), Base64.NO_WRAP);
//        final String imageFormat = "jpeg";
//        String imageBase64 = "data:image/" + imageFormat + ";base64," + picStr;
//        ImageRecogRequest recogRequest = new ImageRecogRequest();
////        recogRequest.setImage(imageBase64);
//        List<String> list = new ArrayList();
//        list.add("addfd");
//        list.add("bbada");
//        recogRequest.setRepositoryIds(list);
////        showProgressDialog(R.string.loading, true, null);
//        OkGoNetAccess.get("http://api.huayuexh.com/huawen.php?act=imageRecog", recogRequest.toParams(ImageRecogRequest.class), ImageRecogData.class, new Callback() {
//            @Override
//            public void callback(Result result) {
////                hideProgressDialog();
//                if (result.isSuccess()) {
//                    ImageRecogData data = (ImageRecogData) result;
//                    List<ImageRecogData.ImageRecogItem> pinganDataList = data.getData();
//                    if (pinganDataList.size() > 0) {
//                        mPinganPersonInfo = pinganDataList.get(0);
//                    }
//                    postPingAnData(new Gson().toJson(mPinganPersonInfo));
//                } else {
//                    showToast(result.getMessage());
//
//                }
//            }
//        });
//        if (imagePath != null) {
//
//        } else {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    showToast("save pic  failed:");
//                    return;
//
//                }
//            });
//        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                showToast("识别成功");
//
//
//            }
//        });
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mFRAbsLoop.resumeThread();
//                    }
//                });
//            }
//        }).start();
//
////        postImageToWebServer(Config.URL, dataConfig.groupId, bmp, Config.USER_ID, Config.APPID, Config.SECRETID, Config.SECRETKEY, Config.BUCKETNAME, 5 * 60, new Callback() {
////            @Override
////            public void onFailure(Call call, IOException e) {
////                Log.e(TAG, "error:" + e.getMessage());
////                String json = "{\"log\":\"识别出错：" + e.getMessage() + "\"}";
////                saveLogToWeb(json);
////            }
////
////            @Override
////            public void onResponse(Call call, okhttp3.Response response) throws IOException {
////                final String result = response.body().string();
////                Log.e(TAG, result);
////                ResultData resultData = new Gson().fromJson(result, ResultData.class);
////                if (resultData == null || resultData.getCode() != 0 || resultData.getData().getCandidates().size() <= 0 || resultData.getData().getCandidates().get(0).getConfidence() < dataConfig.getRecognitionDegree()) {
////                    String json = "{\"log\":\"识别成功，识别率小于设定阈值\"}";
////                    saveLogToWeb(json);
////                    mFRAbsLoop.resumeThread();
////                    lastRequestTime = System.currentTimeMillis() - (dataConfig.sensitivity + 2) * 1000;
////                } else {
////                    //调用js方法
////                    String json = "{\"log\":\"识别成功，识别率大于设定阈值\"}";
////                    saveLogToWeb(json);
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
//////                            hidePreview();
//////                            saveLogToWeb("识别结果：" + result);
////                            //mWebViewMain.loadUrl("https://www.baidu.com");
////                            Log.e(TAG, "SUCCESS,result:" + result);
//////                            takePicture();
////                            Intent intent= new Intent(FaceDetectActivity.this, ResultActivity.class);
////                            intent.putExtra("result",result);
////                            startActivity(intent);
////                        }
////                    });
////                }
////            }
////        });
//    }
//

    /**
     * 上传数据到平安银行
     *
     * @param data
     */
    private void postPingAnData(String data) {
//        showProgressDialog(R.string.loading, true, null);
        final FitOneRegisterRequest request = new FitOneRegisterRequest();
        request.setFile(data);
        OkGoNetAccess.post(Config.REAL_FITONE_BASE + "/app/clubs/registerVipByFace", request.toParams(FitOneRegisterRequest.class), UserInfoData.class, new Callback() {
            @Override
            public void callback(Result result) {
//                hideProgressDialog();
                if (result.isSuccess()) {
                    showToast("用户注册成功即将开启设备");
                    //开启设备
                    UserInfoData userData = (UserInfoData) result;
                    openDevice(userData.getData().getFaceId());
                } else {
                    showToast(result.getMessage());

                }
            }
        });
    }

    private void getUserInfo(String personIdStr) {
        final FitOneOpenDeviceRequest request = new FitOneOpenDeviceRequest();
        request.setDev_id(Global.getSpString(Constants.Sp.SP_DEVICE_ID, ""));
        request.setPersonId(personIdStr);
        OkGoNetAccess.post(Config.REAL_FITONE_BASE + "/cgi/orientGetUser", request.toParams(FitOneOpenDeviceRequest.class), UserInfoData.class, new Callback() {
            @Override
            public void callback(Result result) {
                hideProgressDialog();
                if (result.isSuccess()) {
                    UserInfoData data = (UserInfoData) result;
//                    showToast(result.getMessage());
                    //开启设备
                    openDevice(data.getData().getFaceId());
                } else {
                    showToast(result.getMessage());

                }
            }
        });
    }

    private void openDevice(final String userId) {
//        showProgressDialog(R.string.loading, true, null);
        if(TextUtils.isEmpty(userId)||"".equals(userId)){
            return;
        }
        final FitOneOpenDeviceRequest request = new FitOneOpenDeviceRequest();
        request.setDev_id(Global.getSpString(Constants.Sp.SP_DEVICE_ID, ""));
        request.setDev_type(Global.getSpString(Constants.Sp.SP_DEVICE_KEY, ""));
        request.setFaceId(userId);
        OkGoNetAccess.post(Config.REAL_FITONE_BASE + "/cgi/orientOpenDevice", request.toParams(FitOneOpenDeviceRequest.class), UserInfoData.class, new Callback() {
            @Override
            public void callback(Result result) {
                hideProgressDialog();
                if (result.isSuccess()) {
                    showToast("设备开启成功:"+userId);
                    //开启设备
//                    openDevice();
                } else {
                    showToast(result.getMessage());

                }
            }
        });
    }

    @Override
    public Camera setupCamera() {
        // TODO Auto-generated method stub
        mCamera2 = Camera.open(mCameraID);
        try {
            Camera.Parameters parameters = mCamera2.getParameters();

            //获取相机所有支持的显示大小列表
            List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
            // 如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
            int PreviewWidth = 0, PreviewHeight = 0;
            Camera.Size previewSize = null;
            for (Camera.Size s : sizeList) {
//                Log.i("previwSize", "w: " + s.width + " h: " + s.height);
                if (previewSize == null) {
                    previewSize = s;
                } else {
//                    if (Math.abs(previewSize.width - mCameraPreviewWidth) > Math.abs(s.width - mCameraPreviewWidth)) {
//                        previewSize = s;
//                    }
                    if (s.width > previewSize.width || s.height > previewSize.height) {
                        previewSize = s;
                    }
                }
            }

            parameters.setPreviewSize(previewSize.width, previewSize.height);
//            parameters.setPreviewFormat(mFormat);
            mCamera2.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCamera2 != null) {
            mWidth = mCamera2.getParameters().getPreviewSize().width;
            mHeight = mCamera2.getParameters().getPreviewSize().height;
        }
        return mCamera2;
    }

    @Override
    public void setupChanged(int format, int width, int height) {
    }

    @Override
    public boolean startPreviewImmediately() {
        return true;
    }

    @Override
    public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {
        AFT_FSDKError err = engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, result);
        if (mImageNV21 == null) {
            if (!result.isEmpty()) {
                mAFT_FSDKFace = result.get(0).clone();
                mImageNV21 = data.clone();
            }
        }
        Rect[] rects = new Rect[result.size()];
        for (int i = 0; i < result.size(); i++) {
            rects[i] = new Rect(result.get(i).getRect());
        }
        result.clear();
        return rects;
    }

    @Override
    public void onBeforeRender(CameraFrameData data) {

    }

    @Override
    public void onAfterRender(CameraFrameData data) {
        Rect[] rects = (Rect[]) data.getParams();
        //mGLSurfaceView.getGLES2Render().draw_rect(rects, Color.GREEN, 2);
        if (rects.length > 0) {
            faceRectView.drawFaceRect(rects[0]);
        } else {
            faceRectView.clearRect();
        }

    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
    }

    //显示预览框
    public void showPreview() {
        String json = "{\"log\":\"显示预览框\"}";
        saveLogToWeb(json);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFRAbsLoop.resumeThread();
                rl_scan_view.setVisibility(View.VISIBLE);
            }
        });
    }

    private void saveLogToWeb(String json) {
        Log.e(TAG, json);
    }

    //隐藏预览框
    public void hidePreview() {
        String json = "{\"log\":\"隐藏预览框\"}";
        saveLogToWeb(json);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFRAbsLoop.pauseThread();
                rl_scan_view.setVisibility(View.GONE);
            }
        });
    }

    //暂停识别
    public void pauseCheck() {
        String json = "{\"log\":\"暂停识别\"}";
        saveLogToWeb(json);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFRAbsLoop.pauseThread();
            }
        });
    }

    //启动识别
    public void startCheck() {
        String json = "{\"log\":\"启动识别\"}";
        saveLogToWeb(json);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rl_scan_view.getVisibility() == View.GONE) {
                    rl_scan_view.setVisibility(View.VISIBLE);
                }
                if (mFRAbsLoop == null) {
                    mFRAbsLoop = new FRAbsLoop();
                    mFRAbsLoop.start();
                } else {
                    mFRAbsLoop.resumeThread();
                }
            }
        });
    }

    //初始化人脸识别相关
    public void initFace() {
        String json = "{\"log\":\"初始化人脸识别相关\"}";
        saveLogToWeb(json);
        ScreenUtils.keepScreenLongLight(this);
        rl_scan_view = findViewById(R.id.rl_scan_view);
        faceRectView = findViewById(R.id.face_view);
//        rl_loading = findViewById(R.id.rl_loading);
        mGLSurfaceView = findViewById(R.id.glsurfaceView);
        mSurfaceView = findViewById(R.id.surfaceView);
        client = new OkHttpClient();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        FaceDetectActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);

    }

    protected void stopPreview() {
        if (null != mCamera1) {
            mCamera1.stopPreview();
            mCamera1.release();
            mCamera1 = null;
        }
        if (null != mSurfaceHolderCamera) {
            mSurfaceHolderCamera.removeCallback(this);
            mSurfaceHolderCamera = null;
        }
        if (null != mSurfaceViewCamera) {
            mLayoutMain.removeView(mSurfaceViewCamera);
            mSurfaceViewCamera = null;
        }
    }

    private void startPreView() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (null != mSurfaceViewCamera) {
                    if (null != mCamera2) {
                        mCamera2.startPreview();
                    }
                    Toast.makeText(FaceDetectActivity.this, "ok success", Toast.LENGTH_SHORT).show();
                } else {
                    mSurfaceViewCamera = new SurfaceView(FaceDetectActivity.this);

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(1, 1);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                    mLayoutMain.addView(mSurfaceViewCamera, mLayoutMain.getChildCount(), layoutParams);
                    mSurfaceHolderCamera = mSurfaceViewCamera.getHolder();
                    mSurfaceHolderCamera.addCallback(FaceDetectActivity.this);
                }
            }
        });
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int defaultId = -1;
        // Find the total number of cameras available
        int cameraNum = getNumberOfCameras();
        // Find the ID of the default camera
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < cameraNum; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                defaultId = i;
                break;
            }
        }
        try {
            mCamera1 = Camera.open(defaultId >= 0 ? defaultId : 0);
            Camera.Parameters params = mCamera1.getParameters();

            List<Camera.Size> previewSizeList = params.getSupportedPreviewSizes();
            Camera.Size previewSize = null;
            for (Camera.Size s : previewSizeList) {
                Log.i("previwSize", "w: " + s.width + " h: " + s.height);
                if (previewSize == null) {
                    previewSize = s;
                } else {
                    if (Math.abs(previewSize.width - mCameraPreviewWidth) > Math.abs(s.width - mCameraPreviewWidth)) {
                        previewSize = s;
                    }
                }
            }
            params.setPreviewSize(previewSize.width, previewSize.height);

            Configuration newConfig = getResources().getConfiguration();
            RelativeLayout.MarginLayoutParams mp = null;

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.hardKeyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {
                mp = new RelativeLayout.LayoutParams(previewSize.width, previewSize.height);

                mCamera1.setDisplayOrientation(270);
            } else {
                mp = new RelativeLayout.LayoutParams(previewSize.width, previewSize.height);
            }
            mp.leftMargin = mCameraPreviewLeft;
            mp.topMargin = mCameraPreviewTop;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mp);

            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            mSurfaceViewCamera.setLayoutParams(layoutParams);
            mCamera1.setPreviewDisplay(holder);
            mCamera1.setParameters(params);

            mCamera1.startPreview();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(FaceDetectActivity.this, "Ok", Toast.LENGTH_SHORT).show();
//
//                }
//            });
        } catch (Exception e) {
            if (null != mCamera1) {
                mCamera1.release();
                mCamera1 = null;
            }
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera1 != null) {
            mCamera1.startPreview();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != mCamera1) {
            holder.removeCallback(this);
            mCamera1.setPreviewCallback(null);
            mCamera1.stopPreview();
            mCamera1.release();
            mCamera1 = null;
        }
    }


}
