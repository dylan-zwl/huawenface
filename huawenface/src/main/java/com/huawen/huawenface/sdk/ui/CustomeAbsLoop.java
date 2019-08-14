package com.huawen.huawenface.sdk.ui;

/**
 * Created by ZengYinan.
 * Date: 2018/9/11 23:00
 * Email: 498338021@qq.com
 * Desc:
 */
public abstract class CustomeAbsLoop extends Thread {
    volatile Thread mBlinker = this;

    abstract public void setup();

    abstract public void loop();

    abstract public void over();

    private final Object lock = new Object();
    private boolean pause = false;

    /**
     * 调用这个方法实现暂停线程
     */
    public void pauseThread() {
        pause = true;
    }

    /**
     * 调用这个方法实现恢复线程的运行
     */
    public void resumeThread() {
        pause = false;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**
     * 注意：这个方法只能在run方法里调用，不然会阻塞主线程，导致页面无响应
     */
    public void onPause() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        Thread thisThread = Thread.currentThread();
        setup();
        while (mBlinker == thisThread) {
            while (pause) {
                onPause();
            }
            loop();
        }
        over();
    }

    public void break_loop() {
        mBlinker = null;
    }

    public void shutdown() {
        break_loop();
        try {
            if (this != Thread.currentThread()) {
                synchronized (this) {
                    this.notifyAll();
                }
                this.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
