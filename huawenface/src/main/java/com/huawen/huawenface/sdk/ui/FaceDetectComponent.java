package com.huawen.huawenface.sdk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
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
import com.huawen.huawenface.sdk.bean.CreateBean;
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
import com.huawen.huawenface.sdk.net.request.FitOneDataUploadRequest;
import com.huawen.huawenface.sdk.net.request.FitOneOpenDeviceRequest;
import com.huawen.huawenface.sdk.net.request.FitOneRegisterRequest;
import com.huawen.huawenface.sdk.utils.ImageUtils;
import com.huawen.huawenface.sdk.utils.ScreenUtils;
import com.huawen.huawenface.sdk.utils.easypermission.EasyPermission;
import com.huawen.huawenface.sdk.utils.easypermission.GrantResult;
import com.huawen.huawenface.sdk.utils.easypermission.NextAction;
import com.huawen.huawenface.sdk.utils.easypermission.NextActionType;
import com.huawen.huawenface.sdk.utils.easypermission.Permission;
import com.huawen.huawenface.sdk.utils.easypermission.PermissionRequestListener;
import com.huawen.huawenface.sdk.utils.easypermission.RequestPermissionRationalListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * <h1>人脸检测组件</h1>
 * <ul>
 * <li>可拖动组件</li>
 * <li>悬浮独立组件</li>
 * <li>自定义组件打下</li>
 * </ul>
 */
public class FaceDetectComponent extends LinearLayout implements SurfaceHolder.Callback, Camera.AutoFocusCallback {

    private int mStartX;
    private int mStartY;
    private Activity mActivity;
    private WindowManager.LayoutParams wmParams;
    private WindowManager wm;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    private IFloatViewClick listener;
    private boolean isAllowTouch = true;

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
    Bitmap cacheBitmap = null;

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
    byte[] mImageNV21Copy = null;
    FRAbsLoop mFRAbsLoop = null;
    AFT_FSDKFace mAFT_FSDKFace = null;

    private OkHttpClient client;
    private FaceRectView faceRectView;
    private DataConfig dataConfig;
    private int sw;
    private int sh;
    private ImageRecogData.ImageRecogItem mPinganPersonInfo;
    private int mFaceViewWidth;
    private int mFaceViewHeight;
    private float mPercent = 0.3f;
    private FaceDetectListener mListener;
    private Bitmap bmp;
    private boolean dontPreView;//如果为true则不会预览识别，resumeDetect可以恢复
    private String imagePath;
    private String imageBase64;
    private Bitmap mPicByCamera;
    private String mHongRuanPic;
    private String imageHrBase64;


    public interface FaceDetectListener {
        /**
         * 回调在UI线程
         */
        void success();

        /**
         * 回调在UI线程
         */
        void failed(String json);
    }

    public FaceDetectComponent(Context context, int startX, int startY, float percent) {
        super(context);
        mActivity = (Activity) context;
        mStartX = startX;
        mStartY = startY;
        mPercent = percent;
        startInit();
        // TODO 自动生成的构造函数存根
        // wmParams=MyApplication.parmas;
    }

    /**
     * 初始化设备
     *
     * @param context 上下文
     * @param percent 人脸检测百分比
     */
    public FaceDetectComponent(Context context, float percent) {
        this(context, 0, 0, percent);
    }

    /**
     * 开始人脸检测
     *
     * @return
     */
    private FaceDetectComponent startInit() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_face_scan, null);
        init(view, mStartX, mStartY);
        initView();

        setFloatViewClickListener(new IFloatViewClick() {
            @Override
            public void onFloatViewClick() {
//                Toast.makeText(mActivity, "facedetect is clicked, will close face detect", Toast.LENGTH_SHORT).show();
//                stopDetect();
            }
        });

        startPreview();
        return this;
    }

    public FaceDetectComponent(Context context, int x, int y, View childView) {
        super(context);
        init(childView, x, y);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
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
                startDelay();
                startDetect(mListener);
//                initFace();
//                FaceDetectActivityPermissionsDispatcher.startPreviewWithPermissionCheck(FaceDetectActivity.this);
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeviceInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.dialog_set_device_info);

        View view = View.inflate(mActivity, R.layout.dialog_device_info_set, null);
        final AppCompatEditText groupIdINputView = view.findViewById(R.id.dialog_input_group_id);
        final AppCompatEditText deviceIdInputView = view.findViewById(R.id.dialog_input_device_id);
        final AppCompatEditText mDelayTimeView = view.findViewById(R.id.dialog_input_delay_time);
        final AppCompatEditText scaleInputView = view.findViewById(R.id.dialog_input_scale);
        builder.setView(view);
        builder.setCancelable(true);
        groupIdINputView.requestFocus();
        builder.setPositiveButton(R.string.confirm, null);
        scaleInputView.setText(String.valueOf(16));
        mDelayTimeView.setText(String.valueOf(2000));
        final AlertDialog dialog = builder.create();

        //为了防止 getButton() 为空,需要在 OnShowListener 里设置事件
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
                //为了避免点击 positive 按钮后直接关闭 dialog,把点击事件拿出来设置
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(new OnClickListener() {
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
                                if (TextUtils.isEmpty(mDelayTimeView.getText().toString())) {
                                    showToast(R.string.hint_device_delay_time);
                                    return;
                                }
                                Global.setSpString(Constants.Sp.SP_DEVICE_ID, deviceIdInputView.getText().toString());
                                Global.setSpString(Constants.Sp.SP_GROUP_ID, groupIdINputView.getText().toString());
                                Global.setSpInteger(Constants.Sp.SP_DELAY_TIME, Integer.valueOf(mDelayTimeView.getText().toString()));
                                Global.setSpInteger(Constants.Sp.SCALE, Integer.valueOf(scaleInputView.getText().toString()));

                                beforeShowChooseDialog();
                                dialog.dismiss();
                            }
                        });
            }
        });

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#c6174e"));

    }

    /**
     * 暂停识别
     */
    public void pauseDetect() {
        if (mFRAbsLoop != null)

            mFRAbsLoop.pauseThread();
    }

    /**
     * 恢复识别，用于识别成功后暂停识别后，需要重新开始识别的情况
     */
    public void resumeDetect() {
        if (mFRAbsLoop != null)
            mFRAbsLoop.resumeThread();
        dontPreView = false;

    }

    protected void onDestroy() {

        if (mFRAbsLoop != null) {
            mFRAbsLoop.resumeThread();
            mFRAbsLoop.shutdown();
            mFRAbsLoop = null;
        }
        AFT_FSDKError err = engine.AFT_FSDK_UninitialFaceEngine();
    }

    private void initView() {
        if (getNumberOfCameras() == 0) {
            showToast(R.string.have_no_camera);
            stopDetect();
        }
        if (!PhoneUtil.hasInternet()) {
            showToast(R.string.have_no_internet_attack);
            stopDetect();
        }

        sw = ScreenUtils.getScreenWidth(mActivity);
        sh = ScreenUtils.getScreenHeight(mActivity);
//        if(sw<sh) {
//
//        }else{
//            mFaceViewWidth = (int) (sh * mPercent);
//            mFaceViewHeight = (int) (sw * mPercent);
//        }
        mFaceViewWidth = (int) (sw * mPercent);
        mFaceViewHeight = (int) (sh * mPercent);
        dataConfig = new DataConfig();
        dataConfig.setGroupId("nsVGj9jCFBUp" + "." + Global.getSpString(Constants.Sp.SP_GROUP_ID, ""));
        dataConfig.setWidth(mFaceViewWidth);
        dataConfig.setHeight(mFaceViewWidth);
        dataConfig.setWaitSecond(1);
        dataConfig.setSensitivity(7);
        dataConfig.setMarginLeft(0);
        dataConfig.setMarginTop(0);
        dataConfig.setScale(Global.getSpInteger(Constants.Sp.SCALE, 16));
        dataConfig.setRecognitionDegree(65);

        initFace();
        startPreview();
//        FaceDetectActivityPermissionsDispatcher.startPreviewWithPermissionCheck(this);
        if (Global.getSpBoolean(Constants.Sp.IS_FIRST_RUN, true)) {
            if (mFRAbsLoop != null)

                mFRAbsLoop.pauseThread();
            showDeviceInputDialog();


        } else {
            startDelay();
        }

    }

    /**
     * 开始延时人脸识别,需要延时开始识别人脸
     */
    private void startDelay() {
        if (mFRAbsLoop != null)

            mFRAbsLoop.pauseThread();
        int count = Global.getSpInteger(Constants.Sp.SP_DELAY_TIME, 1500);
        int sleepTime = 100;
        TimeCounter.getTimeCounter(count / sleepTime, sleepTime).setOnTimeListener(new TimeCounter.OnTimeListener() {
            @Override
            public void onTimesUp() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("即将开始人脸识别");
                        resumeDetect();
                    }
                });
            }

            @Override
            public void onTimeChange(int countRemain) {

            }
        }).start();
    }


    public void showToast(final int textResId) {
        showToast(mActivity.getString(textResId));
    }

    public void showSnak(String message) {
//        Snackbar.make(getWindow().getDecorView(), message, Snackbar.LENGTH_SHORT).show();
    }

    public void showSnak(final int textResId) {
        showSnak(mActivity.getString(textResId));
    }

    public void showToast(String message) {
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }

    private void init(View childView, int x, int y) {
//        wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        //设置你要添加控件的类型，TYPE_ALERT需要申明权限，TOast不需要，在某些定制系统中会禁止悬浮框显示，所以最后用TYPE_TOAST
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//6.0+
//            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//            wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        //设置控件在坐标计算规则，相当于屏幕左上角
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.x = (int) x;
        wmParams.y = (int) y;
        if (childView != null) {
            addView(childView);
        }
        //   wm.addView(this, wmParams);
    }

    /**
     * 更新位置
     *
     * @param x
     * @param y
     */
    public void updateFloatViewPosition(int x, int y) {
        wmParams.x = x;
        wmParams.y = y;
        wm.updateViewLayout(this, wmParams);
    }

    public void setFloatViewClickListener(IFloatViewClick listener) {
        this.listener = listener;
    }

    /**
     * 停止检测人脸
     */
    public void stopDetect() {
        removeFromWindow();
        onDestroy();
    }

    /**
     * 自带权限检测授权并开启人脸检测
     */
    public void startWithPermissionCheck(final FaceDetectListener listener) {
        mListener = listener;
        EasyPermission.with(mActivity)
                .addPermissions(Permission.SYSTEM_ALERT_WINDOW)      //申请定位权限组
                .addPermissions(Permission.WRITE_EXTERNAL_STORAGE)          //申请打电话权限
                .addPermissions(Permission.READ_EXTERNAL_STORAGE)          //申请打电话权限
                .addPermissions(Permission.CAMERA)          //申请打电话权限
                .addRequestPermissionRationaleHandler(Permission.ACCESS_FINE_LOCATION, new RequestPermissionRationalListener() {
                    @Override
                    public void onRequestPermissionRational(String permission, boolean requestPermissionRationaleResult, final NextAction nextAction) {
                        Toast.makeText(mActivity, "请授予权限", Toast.LENGTH_SHORT).show();
                        nextAction.next(NextActionType.NEXT);
                    }
                })
                .addRequestPermissionRationaleHandler(Permission.SYSTEM_ALERT_WINDOW, new RequestPermissionRationalListener() {
                    @Override
                    public void onRequestPermissionRational(String permission, boolean requestPermissionRationaleResult, final NextAction nextAction) {
                        nextAction.next(NextActionType.NEXT);
                    }
                })
                .addRequestPermissionRationaleHandler(Permission.WRITE_EXTERNAL_STORAGE, new RequestPermissionRationalListener() {
                    @Override
                    public void onRequestPermissionRational(String permission, boolean requestPermissionRationaleResult, final NextAction nextAction) {
                        nextAction.next(NextActionType.NEXT);
                    }
                })
                .addRequestPermissionRationaleHandler(Permission.READ_EXTERNAL_STORAGE, new RequestPermissionRationalListener() {
                    @Override
                    public void onRequestPermissionRational(String permission, boolean requestPermissionRationaleResult, final NextAction nextAction) {
                        nextAction.next(NextActionType.NEXT);
                    }
                })
                .addRequestPermissionRationaleHandler(Permission.CAMERA, new RequestPermissionRationalListener() {
                    @Override
                    public void onRequestPermissionRational(String permission, boolean requestPermissionRationaleResult, final NextAction nextAction) {
                        nextAction.next(NextActionType.NEXT);
                    }
                })
                .request(new PermissionRequestListener() {
                    @Override
                    public void onGrant(Map<String, GrantResult> result) {
                        //权限申请返回
                        startDetect(listener);
                    }

                    @Override
                    public void onCancel(String stopPermission) {
                        if (mListener != null) {
                            mListener.failed("授权失败");
                        }
                    }
                });
    }

    /**
     * 开始显示人脸检测
     *
     * @return
     */
    public boolean startDetect(FaceDetectListener listener) {
        mListener = listener;
        boolean isFirst = Global.getSpBoolean(Constants.Sp.IS_FIRST_RUN, true);
        Log.d("is First run:" + isFirst);

        if (isFirst) {
            return false;
        }
        if (wm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (!isAttachedToWindow()) {
                    wm.addView(this, wmParams);
                    return true;
                } else {
                    return false;
                }
            } else {
                try {
                    if (getParent() == null) {
                        wm.addView(this, wmParams);
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }


        } else {
            return false;
        }
    }

    public void setIsAllowTouch(boolean flag) {
        isAllowTouch = flag;
    }

    /**
     * 从窗口移除
     *
     * @return
     */
    public boolean removeFromWindow() {
        if (wm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (isAttachedToWindow()) {
                    wm.removeViewImmediate(this);
                    return true;
                } else {
                    return false;
                }
            } else {
                try {
                    if (getParent() != null) {
                        wm.removeViewImmediate(this);
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }


        } else {
            return false;
        }

    }


    // 此wmParams为获取的全局变量，用以保存悬浮窗口的属性

    // 重写，返回true 拦截触摸事件
//	 @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        return isAllowTouch;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = (int) event.getRawX() - this.getMeasuredWidth() / 2;
                mTouchStartY = (int) event.getRawY() - this.getMeasuredHeight() / 2;

                return true;
            case MotionEvent.ACTION_MOVE:
                wmParams.x = (int) event.getRawX() - this.getMeasuredWidth() / 2;
                // 减25为状态栏的高度
                wmParams.y = (int) event.getRawY() - this.getMeasuredHeight() / 2;
                // 刷新
                if (Math.abs(wmParams.y - mTouchStartY) > 10 || Math.abs(wmParams.x - mTouchStartX) > 10) {
                    wm.updateViewLayout(this, wmParams);
                }
                return true;
            case MotionEvent.ACTION_UP:
                y = (int) event.getRawY() - this.getMeasuredHeight() / 2;
                x = (int) event.getRawX() - this.getMeasuredWidth() / 2;
                if (Math.abs(y - mTouchStartY) > 10 || Math.abs(x - mTouchStartX) > 10) {
                    wm.updateViewLayout(this, wmParams);
                } else {
                    if (listener != null) {
                        listener.onFloatViewClick();
                    }

                }
                return true;
            default:
                break;
        }
        return false;

    }


    public interface IFloatViewClick {
        void onFloatViewClick();
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

                final int rotate = mCameraRotate;

                byte[] data = mImageNV21;
                YuvImage yuv = new YuvImage(data, ImageFormat.NV21, mWidth, mHeight, null);
                ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
                yuv.compressToJpeg(mAFT_FSDKFace.getRect(), 80, ops);
                //获取图片并注册人脸
                mImageNV21 = null;
                final Bitmap bmpTmp = BitmapFactory.decodeByteArray(ops.getByteArray(), 0, ops.getByteArray().length);

                if (bmpTmp != null) {
                    Matrix m = new Matrix();
                    m.setRotate(-90, (float) bmpTmp.getWidth() / 2, (float) bmpTmp.getHeight() / 2);
                    final Bitmap bm1 = Bitmap.createBitmap(bmpTmp, 0, 0, bmpTmp.getWidth(), bmpTmp.getHeight(), m, true);
//                    bytesToImageFile(ImageUtils.Bitmap2Bytes(bm1));
                    saveHongruanPic(bm1);

                    //获取大图
                    Bitmap bigBitMap = ImageUtils.getBitmapImageFromYUV(data, mWidth, mHeight);
                    cacheBitmap = bigBitMap;

                    mActivity.runOnUiThread(new Runnable() {
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

    /**
     * 保存虹软图片
     *
     * @param bm1
     */
    private void saveHongruanPic(final Bitmap bm1) {
        mHongRuanPic = ImageUtils.saveBitmapTofile(bm1, System.currentTimeMillis() + "_hr.jpg");
    }

    //获取摄像头个数
    public int getNumberOfCameras() {
        int cameraCount = Camera.getNumberOfCameras();
        Log.e(TAG, "cameraCount:" + cameraCount);
        return cameraCount;
    }

    //显示预览框(第一次开启的时候调用)
    public void startPreview() {
        String json2 = "{\"log\":\"显示预览框(第一次开启的时候调用)\"}";
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                dataConfig = new Gson().fromJson(json, DataConfig.class);
                mWidth = dataConfig.width;
                mHeight = dataConfig.height;


                mCameraID = getNumberOfCameras() > 1 ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
                mCameraRotate = 270;
                mCameraMirror = true;

                mFormat = ImageFormat.NV21;

                mSurfaceView.setOnCameraListener(new CameraSurfaceView.OnCameraListener() {

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
                            Camera.Size previewSize = sizeList.get(0);
                            for (Camera.Size s : sizeList) {
                                if (previewSize == null) {
                                    previewSize = s;
                                } else {
                                    if (s.width == s.height) {
                                        if (s.width < previewSize.width || s.height < previewSize.height) {
                                            previewSize = s;
                                        }
                                    }

                                }
                            }


                            parameters.setPreviewSize(previewSize.width, previewSize.height);//控制相机分辨率将会影响识别框的大小
//                            parameters.setPreviewSize(mWidth,mHeight);//控制相机分辨率将会影响识别框的大小
//            parameters.setPreviewFormat(mFormat);
                            mCamera2.setParameters(parameters);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (mCamera2 != null) {
                            mWidth = mCamera2.getParameters().getPreviewSize().width;
                            mHeight = mCamera2.getParameters().getPreviewSize().height;
                        }
                        Log.i("previwSize--startPreview", "w: " + mWidth + " h: " + mHeight);

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
                            Rect rect = new Rect();
                            rect.left = (int) (rects[0].left);
                            rect.right = (int) (rects[0].right);
                            rect.bottom = (int) (rects[0].bottom);
                            rect.top = (int) (rects[0].top);
//                            faceRectView.drawFaceRect(rect);
                        } else {
                            faceRectView.clearRect();
                        }

                    }

                });
                mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, mCameraMirror, mCameraRotate);
                mSurfaceView.debug_print_fps(false, false);

                if (dataConfig.getScale() < 2) {
                    dataConfig.setScale(2);
                }
                if (dataConfig.getScale() > 32) {
                    dataConfig.setScale(32);
                }

                AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT, dataConfig.getScale(), 2);
                err = engine.AFT_FSDK_GetVersion(version);

                int width = (int) (dataConfig.width);
                int height = (int) (dataConfig.height);
                faceRectView.setFaceViewSize(width, height);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dataConfig.width, dataConfig.height);
                layoutParams.leftMargin = dataConfig.marginLeft;
                layoutParams.topMargin = dataConfig.marginTop;
                rl_scan_view.setLayoutParams(layoutParams);
                if (mFRAbsLoop == null && !dontPreView) {
                    mFRAbsLoop = new FRAbsLoop();
                    mFRAbsLoop.start();
                } else {
                    if (mFRAbsLoop != null)
                        mFRAbsLoop.resumeThread();
//                    resumeDetect();

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
//                .addHeader("host", "service.image.myqcloud.com")
                .addHeader("host", "recognition.image.myqcloud.com")
                .addHeader("authorization", sign)
                .addHeader("content-typea", "multipart/form-data")
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
        if (mFRAbsLoop != null)

            mFRAbsLoop.pauseThread();
        lastRequestTime = System.currentTimeMillis();


        postImageToWebServer(FaceConfig.URL, dataConfig.groupId, bmp, FaceConfig.USER_ID, FaceConfig.APPID, FaceConfig.SECRETID, FaceConfig.SECRETKEY, FaceConfig.BUCKERNAME, 5 * 60, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e("ZYN", "error:" + e.getMessage());
                final String json = "网络异常，请稍后再试";
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(json);
                    }
                });
                mFRAbsLoop.resumeThread();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                final String result = response.body().string();
                android.util.Log.e("ZYN", result);
                final ResultData resultData = new Gson().fromJson(result, ResultData.class);
                if (resultData.getCode() != 0) {
                    final String json = "请求识别图片失败";
                    saveLogToWeb(json);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(json);

                        }
                    });
                    mFRAbsLoop.resumeThread();
//                    resumeDetect();

                    lastRequestTime = System.currentTimeMillis() - (dataConfig.sensitivity + 2) * 1000;

                } else {
                    //调用js方法
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast("人脸检测通过");
                        }
                    });

                    try {
                        Matrix m = new Matrix();
                        m.setRotate(-90, (float) cacheBitmap.getWidth() / 2, (float) cacheBitmap.getHeight() / 2);
                        mPicByCamera = Bitmap.createBitmap(cacheBitmap, 0, 0, cacheBitmap.getWidth(), cacheBitmap.getHeight(), m, true);
                        imagePath = ImageUtils.saveBitmapTofile(mPicByCamera, System.currentTimeMillis() + "_com.jpg");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (TextUtils.isEmpty(imagePath)) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("人脸图片保存失败");
                            }
                        });
                    }
                    final String picStr = Base64.encodeToString(File2byte(imagePath), Base64.NO_WRAP);
                    final String imageFormat = "jpeg";
                    imageBase64 = "data:image/" + imageFormat + ";base64," + picStr;

                    showRecogResultDialog("111", resultData, imageBase64, bmp);
                }
            }
        });
    }

    public void createImageToWebServer(String url, String group_id, Bitmap bitmap, String userId, long appId, String secretId, String secretKey, String bucketName, long expired, okhttp3.Callback callback) {

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
                .addFormDataPart("group_ids[0]", group_id)
                .addFormDataPart("person_id", System.currentTimeMillis() + "")
                .addFormDataPart("image", new Date().getTime() + ".jpg", RequestBody.create(MEDIA_TYPE_PNG, ImageUtils.Bitmap2Bytes(bitmap)));
//                .addFormDataPart("url", "http://i9.taou.com/maimai/p/4425/6762_49_62QooXqTEXCLj2-a160");
        MultipartBody requestBody = builder.build();
        //构建请求
        okhttp3.Request request = new okhttp3.Request.Builder()
//                .addHeader("host", "service.image.myqcloud.com")
                .addHeader("host", "recognition.image.myqcloud.com")
                .addHeader("authorization", sign)
                .addHeader("content-typea", "multipart/form-data")
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void reDetect() {
        stopDetect();
        dontPreView = false;
        startDetect(mListener);
        startPreview();

    }

    private void imageCallback(final byte[] data, Camera camera, final String json, final ResultData resultData) {

        if (data == null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {

                    final String picStr = Base64.encodeToString(File2byte(mHongRuanPic), Base64.NO_WRAP);
                    final String imageFormat = "jpeg";
                    imageHrBase64 = "data:image/" + imageFormat + ";base64," + picStr;
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    showRecogResultDialog(json, resultData, imageHrBase64, mPicByCamera);


                    //mWebViewMain.loadUrl("https://www.baidu.com");
                    android.util.Log.e("ZYN", "SUCCESS");

                }
            }.execute();

        } else {
            stopDetect();
            dontPreView = true;
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        Bitmap bmpTmp = ImageUtils.saveFileToBmp(data, System.currentTimeMillis() + ".jpg");
                        Matrix m = new Matrix();
                        m.setRotate(-90, (float) bmpTmp.getWidth() / 2, (float) bmpTmp.getHeight() / 2);
                        mPicByCamera = Bitmap.createBitmap(bmpTmp, 0, 0, bmpTmp.getWidth(), bmpTmp.getHeight(), m, true);
                        imagePath = ImageUtils.saveBitmapTofile(mPicByCamera, System.currentTimeMillis() + "_com.jpg");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (TextUtils.isEmpty(imagePath)) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("人脸图片保存失败");
                            }
                        });
                        return null;
                    }
                    final String picStr = Base64.encodeToString(File2byte(imagePath), Base64.NO_WRAP);
                    final String imageFormat = "jpeg";
                    imageBase64 = "data:image/" + imageFormat + ";base64," + picStr;

                    final String picStr1 = Base64.encodeToString(File2byte(mHongRuanPic), Base64.NO_WRAP);
                    final String imageFormat1 = "jpeg";
                    imageHrBase64 = "data:image/" + imageFormat1 + ";base64," + picStr1;
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
//                showToast("处理图片完毕");

                    showRecogResultDialog(json, resultData, imageHrBase64, mPicByCamera);

                    //mWebViewMain.loadUrl("https://www.baidu.com");
                    android.util.Log.e("ZYN", "SUCCESS");

                    startDetect(mListener);

                    startPreview();

                }
            }.execute();
        }


//        pauseDetect();
//        mCamera2.startPreview();
//        mCamera1.startPreview();
//        startPreviewDisplay(camera,mSurfaceHolderCamera);
    }

    /**
     * 开始预览
     *
     * @param holder
     */
    public void startPreviewDisplay(Camera camera, SurfaceHolder holder) {
        if (camera == null) {
            throw new IllegalStateException("Camera must be set when start preview");
        }
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showRecogResultDialog(String json, ResultData resultData, final String imageBase64, Bitmap mPicByCamera) {
        if (TextUtils.isEmpty(imageBase64)) {
            showToast("识别成功，但是人脸图片为空");
            return;
        }
        ResultData.DataBean data = resultData.getData();
        final List<ResultData.DataBean.CandidatesBean> cos = data.getCandidates();

        String resultStr = "";
        String personId = "";
        boolean isNewUser;

        if (cos != null && cos.size() > 0) {
            isNewUser = cos.get(0).getConfidence() < 85;
        }else {
            isNewUser = true;
        }

        if (cos != null && !isNewUser) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("识别成功，获取用户信息");
                }
            });

            //调用js方法
            getUserInfo(cos.get(0).getFace_id(), imageBase64);
        } else {
            //识别失败  添加用户到腾讯云
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast("识别失败，添加用户信息");
                }
            });
            createImageToWebServer(FaceConfig.CREATE_URL, dataConfig.groupId, mPicByCamera, FaceConfig.USER_ID, FaceConfig.APPID, FaceConfig.SECRETID, FaceConfig.SECRETKEY, FaceConfig.BUCKERNAME, 5 * 60, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    android.util.Log.e("ZYN", "error:" + e.getMessage());
                    final String json = "添加出错" + e.getMessage();
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(json);
                        }
                    });
                    mFRAbsLoop.resumeThread();
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    final String result = response.body().string();

                    final CreateBean resultData = new Gson().fromJson(result, CreateBean.class);
                    if (resultData.getCode() != 0) {
                        final String json = "添加用户失败";
                        saveLogToWeb(json);
                        mFRAbsLoop.resumeThread();

                    } else {
                        //添加用户成功
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("添加用户信息成功");
                            }
                        });
                        //添加成功 添加数据到服务器
                        postPingAnData(imageBase64, resultData.getData().getFace_id());
                    }
                }
            });
        }

    }


    /**
     * 提供给硬件外部的上传数据接口，只提供data参数字段，默认数据json格式传递过来，最终给接口形式如：  {"data":"{"keyA":"I am keyA value"},{"keyB":"I am keuB"}"}
     *
     * @param data
     */
    public void uploadDeviceData(String data) {
        final FitOneDataUploadRequest request = new FitOneDataUploadRequest();
        request.setData(data);
        OkGoNetAccess.post(Config.REAL_FITONE_BASE + "/app/cgi/uploadData", request.toParams(FitOneDataUploadRequest.class), Result.class, new Callback() {
            @Override
            public void callback(Result result) {
//                hideProgressDialog();
                if (result.isSuccess()) {
                    showToast("用户数据上传成功");
                } else {
                    showToast(result.getMessage());

                }
            }
        });
    }

    /**
     * 上传数据到平安银行
     *
     * @param data
     */
    private void postPingAnData(String data, final String faceId) {
//        showProgressDialog(R.string.loading, true, null);
        final FitOneRegisterRequest request = new FitOneRegisterRequest();
        request.setFile(data);
        request.setFaceId(faceId);
        OkGoNetAccess.post(Config.REAL_FITONE_BASE + "/app/clubs/registerVipByFace", request.toParams(FitOneRegisterRequest.class), UserInfoData.class, new Callback() {
            @Override
            public void callback(Result result) {
//                hideProgressDialog();
                if (result.isSuccess()) {
                    showToast("即将开启设备");
                    //开启设备
                    UserInfoData userData = (UserInfoData) result;
                    openDevice(faceId);
                } else {
                    showToast(result.getMessage());
//                    if(mListener!=null){
//                        mListener.failed(result.getMessage());
//                    }
                }
            }
        });
    }

    private void getUserInfo(String personIdStr, final String imageStr) {
        final FitOneOpenDeviceRequest request = new FitOneOpenDeviceRequest();
        request.setDev_id(Global.getSpString(Constants.Sp.SP_DEVICE_ID, ""));
        request.setFaceId(personIdStr);
        OkGoNetAccess.post(Config.REAL_FITONE_BASE + "/cgi/orientGetUser", request.toParams(FitOneOpenDeviceRequest.class), UserInfoData.class, new Callback() {
            @Override
            public void callback(Result result) {
                if (result.isSuccess()) {
                    UserInfoData data = (UserInfoData) result;
                    showToast("获取用户信息成功,准备开启设备");
                    //开启设备
                    openDevice(data.getData().getFaceId());
                } else {
                    showToast("用户不存在");
//                    postPingAnData(imageStr, "123");
                    //xyf-todo
                    mFRAbsLoop.resumeThread();
                }
            }
        });
    }

    private void openDevice(final String userId) {
//        showProgressDialog(R.string.loading, true, null);
        if (TextUtils.isEmpty(userId) || "".equals(userId)) {
            return;
        }
//        showToast("准备开启设备");

        final FitOneOpenDeviceRequest request = new FitOneOpenDeviceRequest();
        request.setDev_id(Global.getSpString(Constants.Sp.SP_DEVICE_ID, ""));
        request.setDev_type(Global.getSpString(Constants.Sp.SP_DEVICE_KEY, ""));
        request.setFaceId(userId);
        request.setBig_pic(imageBase64);//相机的截取大图
        request.setSmall_pic(imageHrBase64);//虹软的图片
        OkGoNetAccess.post(Config.REAL_FITONE_BASE + "/cgi/orientOpenDevice", request.toParams(FitOneOpenDeviceRequest.class), UserInfoData.class, new Callback() {
            @Override
            public void callback(Result result) {
                if (result.isSuccess()) {
                    showToast("设备开启成功");
                    if (mListener != null) {
                        mListener.success();
                    }
                    //开启设备
//                    openDevice();
                } else {
                    showToast("设备开启失败");
                    if (mListener != null) {
                        mListener.failed(result.getMessage());
                    }
//                    showToast(result.getMessage() + Global.getSpString(Constants.Sp.SP_DEVICE_ID, ""));

                }
            }
        });
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
    }

    //显示预览框
    public void showPreview() {
        String json = "{\"log\":\"显示预览框\"}";
        saveLogToWeb(json);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mFRAbsLoop != null)
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
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mFRAbsLoop != null)

                    mFRAbsLoop.pauseThread();
                rl_scan_view.setVisibility(View.GONE);
            }
        });
    }

    //暂停识别
    private void pauseCheck() {
        String json = "{\"log\":\"暂停识别\"}";
        saveLogToWeb(json);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mFRAbsLoop != null)

                    mFRAbsLoop.pauseThread();
            }
        });
    }

    //启动识别
    private void startCheck() {
        String json = "{\"log\":\"启动识别\"}";
        saveLogToWeb(json);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rl_scan_view.getVisibility() == View.GONE) {
                    rl_scan_view.setVisibility(View.VISIBLE);
                }
                if (mFRAbsLoop == null) {
                    mFRAbsLoop = new FRAbsLoop();
                    mFRAbsLoop.start();
                } else {
                    if (mFRAbsLoop != null)

                        mFRAbsLoop.resumeThread();
                }
            }
        });
    }

    //初始化人脸识别相关
    public void initFace() {
        String json = "{\"log\":\"初始化人脸识别相关\"}";
        saveLogToWeb(json);
        ScreenUtils.keepScreenLongLight(mActivity);
        rl_scan_view = findViewById(R.id.rl_scan_view);
        faceRectView = findViewById(R.id.face_view);
//        rl_loading = findViewById(R.id.rl_loading);
        mGLSurfaceView = findViewById(R.id.glsurfaceView);
        mSurfaceView = findViewById(R.id.surfaceView);
        client = new OkHttpClient();
    }

//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        FloatDiy.onRequestPermissionsResult(this, requestCode, grantResults);
//
//    }

//    protected void stopPreview() {
//        if (null != mCamera1) {
//            mCamera1.stopPreview();
//            mCamera1.release();
//            mCamera1 = null;
//        }
//        if (null != mSurfaceHolderCamera) {
//            mSurfaceHolderCamera.removeCallback(this);
//            mSurfaceHolderCamera = null;
//        }
//        if (null != mSurfaceViewCamera) {
//            mLayoutMain.removeView(mSurfaceViewCamera);
//            mSurfaceViewCamera = null;
//        }
//    }

//    private void startPreView1() {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (null != mSurfaceViewCamera) {
//                    if (null != mCamera2) {
//                        mCamera2.startPreview();
//                    }
//                } else {
//                    mSurfaceViewCamera = new SurfaceView(mActivity);
//
//                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(1, 1);
//                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//
//                    mLayoutMain.addView(mSurfaceViewCamera, mLayoutMain.getChildCount(), layoutParams);
//                    mSurfaceHolderCamera = mSurfaceViewCamera.getHolder();
//                    mSurfaceHolderCamera.addCallback(FaceDetectComponent.this);
//                }
//            }
//        });
//    }


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
            MarginLayoutParams mp = null;

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
            Log.i("previwSize--surfaceCreated", "w: " + previewSize.width + " h: " + previewSize.height);

            mCamera1.setParameters(params);

            mCamera1.startPreview();
//            mActivity.runOnUiThread(new Runnable() {
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
