package com.huawen.huawenface.sdk.ui;

public class TimeCounter extends Thread {
    private  int sleepTime;
    private int count;

    private TimeCounter(){

    }
    private TimeCounter(int count) {
        this(count, 1000);
    }
    private TimeCounter(int count,int sleepTime){
        mCount=count;
        this.sleepTime = sleepTime;
    }

    private  int mCount = 60;
    private OnTimeListener mListener;
    public static TimeCounter getTimeCounter(int count){
        return new TimeCounter(count);
    }
    public void setCount(int count){
        mCount=count;
    }
    /**
     * a time counter instance
     * @param count the count of sleep
     * @param sleepTime
     * @return
     */
    public static TimeCounter getTimeCounter(int count, int sleepTime){
        return new TimeCounter(count,sleepTime);
    }

    public int getRemainCount() {
        return count;
    }

    public interface OnTimeListener {
        public void onTimesUp();

        public void onTimeChange(int countRemain);
    }

    public TimeCounter setOnTimeListener(OnTimeListener listener) {
        this.mListener = listener;
        return this;
    }

    @Override
    public void run() {
        super.run();
        count = 0;
        if (mListener != null)
            mListener.onTimeChange(mCount - count);
        while (count < mCount) {
            try {
                Thread.sleep(sleepTime);//每次sleep 1s
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            if (mListener != null)
                mListener.onTimeChange(mCount - count);
        }
        if (mListener != null)
            mListener.onTimesUp();
    }
}