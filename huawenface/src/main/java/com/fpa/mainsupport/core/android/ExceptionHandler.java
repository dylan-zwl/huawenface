package com.fpa.mainsupport.core.android;


import com.fpa.mainsupport.core.utils.Log;

/**
 * Created by Chenchunpeng
 *
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler handler;
    private static final String tag = ExceptionHandler.class.getSimpleName();
    public ExceptionHandler() {
        handler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
       Log.e(tag,ex.toString());
        if (null != handler) {
            handler.uncaughtException(thread, ex);
        }
    }

}
