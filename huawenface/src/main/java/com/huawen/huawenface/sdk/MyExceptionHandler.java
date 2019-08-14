package com.huawen.huawenface.sdk;

import com.fpa.mainsupport.core.android.ExceptionHandler;
import com.fpa.mainsupport.core.utils.Log;

/**
 * Created by chenchunpeng on 2015/3/30.
 * email:fpa@shubaobao.com
 */
public class MyExceptionHandler extends ExceptionHandler {

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        super.uncaughtException(thread, ex);
        ex.printStackTrace();
        Log.e("MyExceptionHandler","thread:"+thread.getName()+",ex:"+ex.toString());
    }
}
