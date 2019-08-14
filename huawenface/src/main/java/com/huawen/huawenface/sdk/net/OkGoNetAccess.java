package com.huawen.huawenface.sdk.net;

import android.text.TextUtils;

import com.fpa.mainsupport.core.utils.StringUtils;
import com.google.gson.Gson;
import com.huawen.huawenface.sdk.bean.BaseBean;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

public class OkGoNetAccess {


    public static final void post(String url, HttpParams params, final Class clazz,final Callback callback) {
        OkGo.<String>post(url)//
                .tag(StringUtils.md5(url))//
                .params(params)
//                .headers("header1", "headerValue1")//
//                .params("uid", "paramValue1")//
                .isMultipart(true)         //强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //注意这里已经是在主线程了
                        String data = response.body();//这个就是返回来的结果
                        if (!TextUtils.isEmpty(data)) {
                            Result result=new Result();
                            try{
                               result = (Result) new Gson().fromJson(data, clazz);
                            }catch (Exception e){

                            }
                            if (callback != null)
                                callback.callback(result);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Result result = new Result("-1", response.body());
                        if (callback != null)
                            callback.callback(result);
                    }
                });

    }

    private static boolean isPingan(String url) {
        return true;//!TextUtils.isEmpty(url)&&url.contains("pingan");
    }

    public static final void get(final String url, HttpParams params, final Class clazz,final Callback callback) {
        OkGo.<String>get(url)//
                .tag(StringUtils.md5(url))//
//                .headers("header1", "headerValue1")//
                .params(params)
//                .params("param1", "paramValue1")//
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //注意这里已经是在主线程了
                        String data = response.body();//这个就是返回来的结果
                        if (!TextUtils.isEmpty(data)) {
                            Result result = (Result) new Gson().fromJson(data, clazz);
                            if (callback != null)

                                callback.callback(result);
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Result result = new Result("-1", response.body());
                        if (callback != null)

                            callback.callback(result);
                    }
                });
    }
}
