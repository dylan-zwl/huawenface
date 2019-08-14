package com.huawen.huawenface.sdk.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Base64;
import android.view.View;


import com.huawen.huawenface.sdk.Config;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ZengYinan.
 * Date: 2018/9/7 11:53
 * Email: 498338021@qq.com
 * Desc:
 */
public class ImageUtils {

    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;

    }
    /**
     * 截取控件截图
     *
     * @param view
     * @return
     */
    public static Bitmap viewShot(Activity activity, View v) throws IOException {
        Bitmap bitmap;
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        bitmap = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        try {
            bitmap = Bitmap.createBitmap(bitmap, location[0], location[1], v.getWidth(), v.getHeight());
          return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // 清理缓存
            view.destroyDrawingCache();
        }
        return bitmap;
    }

    public static Bitmap getPicFromBytes(byte[] bytes,
                                         BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return newBmp;
    }

    /**
     * 把Bitmap转Byte
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 把字节数组保存为一个文件
     */
    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    public static Bitmap getBitmapImageFromYUV(byte[] data, int width, int height) {
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
        byte[] jdata = baos.toByteArray();
        BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);
        return bmp;
    }

    private static String bitmap2base64(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT).trim().replaceAll("\n", "").replaceAll("\r", "");
    }


    /**
     * compress image bitmap by "quality compress"
     *
     * @param image
     * @param targetSize the target size of bitmap need compress to
     * @return
     */
    public static Bitmap compressBitmap(Bitmap image, int targetSize) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//quality compress
        int options = 100;
        while (baos.toByteArray().length / 1024 > targetSize) {    //if the size of bitmap is still > targetSize kb,compress
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }


    /**
     * compress image bitmap by "size scale compress
     *
     * @param srcPath    the bitmap 's file path
     * @param reqWidth   the width which requested to compress
     * @param reqHeight  the height which requested to compress
     * @param targetSize the target size of bitmap need compress to
     * @return
     */
    public static Bitmap compressBitmap(String srcPath, int reqWidth, int reqHeight, int targetSize) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = calculateInSampleSize(newOpts, reqWidth, reqHeight);//set the scale
        //read bitmap again
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressBitmap(bitmap, targetSize);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * compress image bitmap by "size scale compress
     *
     * @param srcPath   the bitmap 's file path
     * @param reqWidth  the width which requested to compress
     * @param reqHeight the height which requested to compress
     * @return
     */
    public static Bitmap compressBitmap(String srcPath, int reqWidth, int reqHeight) {
        return compressBitmap(srcPath, reqWidth, reqHeight, 100);
    }

    /**
     * compress a scaled bitmap & the size of bitmap is < 1M
     *
     * @param image
     * @param targetSize the target size of bitmap need compress to
     * @return
     */
    public static Bitmap compressScaledBitmap(Bitmap image, int targetSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//if the size of bitmap > 1M ,compress to avoid oom
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int reqHeight = 320;
        int reqWidth =480;
        newOpts.inSampleSize = calculateInSampleSize(newOpts, reqWidth, reqHeight);
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressBitmap(bitmap, targetSize);
    }

    /**
     * 保存图片到指定文件夹
     *
     * @param bmp
     * @param filename
     * @return
     */
    public static String saveBitmapTofile(Bitmap bmp, String filename) {
        bmp=compressScaledBitmap(bmp,20);
        if (bmp == null || filename == null)
            return null;
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        String path = Config.File.IMAGE_PATH;
        File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
        }
        File imageFile=new File(path+filename);
        if(imageFile.exists()){
            try {
                imageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            stream = new FileOutputStream(imageFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        boolean result=bmp.compress(format, quality, stream);
        return result?imageFile.getAbsolutePath():null;
    }

    public static String saveByteToFile(byte[] data,String filename) throws IOException {

        if (data == null || filename == null)
            return null;
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        String path = Config.File.IMAGE_PATH;
        File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
        }
        File imageFile=new File(path+filename);
        if(imageFile.exists()){
            try {
                imageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            stream = new FileOutputStream(imageFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        FileOutputStream fos = new FileOutputStream(imageFile);
        fos.write(data);
        fos.close();

        return imageFile.getAbsolutePath();
    }

    public static Bitmap saveFileToBmp(byte[] data, String filename) {
        String file= null;
        try {
            file = saveByteToFile(data,filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeFile(file);
    }
}
