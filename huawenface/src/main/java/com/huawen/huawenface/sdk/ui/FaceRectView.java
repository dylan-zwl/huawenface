package com.huawen.huawenface.sdk.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.huawen.huawenface.R;


/**
 * Created by ZengYinan.
 * Date: 2018/9/6 14:32
 * Email: 498338021@qq.com
 * Desc:
 */
public class FaceRectView extends View {
    private Rect rect;
    private int screenWidth;
    private int screenHeight;

    public FaceRectView(Context context) {
        this(context, null);
    }

    public FaceRectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceRectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        screenWidth = wm.getDefaultDisplay().getWidth();
//        screenHeight = wm.getDefaultDisplay().getHeight();
        initPaint(context);
    }

    public void setFaceViewSize(int width, int height){
        screenHeight = height;
        screenWidth = width;
    }

    private void initPaint(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(8);
        mPaint.setColor(context.getResources().getColor(R.color.color_face_rect));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(new CornerPathEffect(30));
    }

    private Paint mPaint;

    /**
     * 开始画矩形框
     *
     * @param rect1
     */
    public void drawFaceRect(Rect rect1) {
        this.rect = new Rect();
        //将屏幕人脸框转换为视频区域的人脸框
        this.rect.bottom = screenHeight - rect1.left;//OK
        this.rect.right = screenWidth - rect1.top;//OK
        this.rect.top = screenHeight - rect1.right;
        this.rect.left = screenWidth  - rect1.bottom;
        //在主线程发起绘制请求
        postInvalidate();
    }

    public void clearRect() {
        rect = null;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rect != null) {

            Path leftTop = new Path();
            leftTop.moveTo(rect.left, rect.top+60);
            leftTop.lineTo(rect.left, rect.top);
            leftTop.lineTo(rect.left + 60, rect.top);
            canvas.drawPath(leftTop, mPaint);

//            /**
//             * 左上角的竖线
//             */
//            canvas.drawLine(rect.left, rect.top, rect.left, rect.top + 60, mPaint);
//            /**
//             * 左上角的横线
//             */
//            canvas.drawLine(rect.left, rect.top, rect.left + 60, rect.top, mPaint);
//
            Path rightTop = new Path();
            rightTop.moveTo(rect.right - 60, rect.top);
            rightTop.lineTo(rect.right, rect.top);
            rightTop.lineTo(rect.right, rect.top + 60);
            canvas.drawPath(rightTop, mPaint);

//            /**
//             * 右上角的竖线
//             */
//            canvas.drawLine(rect.right, rect.top, rect.right - 60, rect.top, mPaint);
//            /**
//             * 右上角的横线
//             */
//            canvas.drawLine(rect.right, rect.top, rect.right, rect.top + 60, mPaint);

            Path leftBottom = new Path();
            leftBottom.moveTo(rect.left, rect.bottom - 60);
            leftBottom.lineTo(rect.left, rect.bottom);
            leftBottom.lineTo(rect.left + 60, rect.bottom);
            canvas.drawPath(leftBottom, mPaint);

//            /**
//             * 左下角的竖线
//             */
//            canvas.drawLine(rect.left, rect.bottom, rect.left, rect.bottom - 60, mPaint);
//            /**
//             * 左下角的横线
//             */
//            canvas.drawLine(rect.left, rect.bottom, rect.left + 60, rect.bottom, mPaint);

            Path rightBottom = new Path();
            rightBottom.moveTo(rect.right, rect.bottom-60);
            rightBottom.lineTo(rect.right, rect.bottom);
            rightBottom.lineTo(rect.right-60, rect.bottom);
            canvas.drawPath(rightBottom, mPaint);

//            /**
//             * 右下角的竖线
//             */
//            canvas.drawLine(rect.right, rect.bottom, rect.right, rect.bottom-60, mPaint);
//            /**
//             * 右下角的横线
//             */
//            canvas.drawLine(rect.right, rect.bottom, rect.right-60, rect.bottom , mPaint);
        }
    }
}
