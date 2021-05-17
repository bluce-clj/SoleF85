package com.dyaco.spiritbike.support.rxtimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.dyaco.spiritbike.support.rxtimer.TimerSupport.*;

public class HandlerTimer implements Runnable, ITimer {
    private final Handler mHandler;
    private final long mInterval;
    private TimerStatus mStatus;

    private long mStartTS = 0L;

    private final Map<OnTickListener, IntervalTickListener> mListeners = new HashMap<>();

    private final List<IntervalTickListener> mCopyListeners = new ArrayList<>();

    public HandlerTimer(long interval) {
        this(interval, new Handler(Looper.getMainLooper()));
    }

    public HandlerTimer(long interval, Handler handler) {
        if (handler == null) {
            throw new NullPointerException("handler or task must not be null");
        }
        this.mInterval = interval;
        this.mHandler = handler;
        this.mStatus = TimerStatus.Waiting;
    }

    @Override
    public final void run() {
        if (mStatus == TimerStatus.Waiting
                || mStatus == TimerStatus.Paused
                || mStatus == TimerStatus.Stopped) {
            return;
        }

        runTask();

        long delay = mInterval - (System.currentTimeMillis() - mStartTS) % mInterval;
        mHandler.postDelayed(this, delay == 0 ? mInterval : delay);
    }

    /**
     * start timer immediately
     */
    @Override
    public void start() {
        start(false);
    }

    /**
     * @param bySecond true, start timer with interval alignment; false, start timer immediately
     */
    @Override
    public void start(boolean bySecond) {
        if (this.mStatus != TimerStatus.Running) {
            this.mStartTS = bySecond ? 0 : System.currentTimeMillis();
            mHandler.removeCallbacks(this);
            this.mStatus = TimerStatus.Running;

            long delay = mInterval - (System.currentTimeMillis() - mStartTS) % mInterval;
            mHandler.postDelayed(this, delay);
        }
    }

    /**
     * pause timer
     */
    @Override
    public void pause() {
        this.mStatus = TimerStatus.Paused;
        mHandler.removeCallbacks(this);
    }

    /**
     * resume timer
     */
    @Override
    public void restart() {
        mHandler.removeCallbacks(this);
        this.mStatus = TimerStatus.Running;
        mHandler.postDelayed(this, mInterval);
    }

    /**
     * stop timer
     */
    @Override
    public void stop() {
        mStatus = TimerStatus.Stopped;
        mHandler.removeCallbacks(this);
    }

    @Override
    public void clear() {
        mListeners.clear();
    }

    /**
     * cancel timer
     */
    @Override
    public void cancel() {
        mStatus = TimerStatus.Stopped;
        mHandler.removeCallbacks(this);
    }

    /**
     * @return current status
     */
    @Override
    public TimerStatus getStatus() {
        return mStatus;
    }

    @Override
    public void register(int interval, OnTickListener onTickListener, boolean intermediate) {
        mListeners.put(onTickListener, new IntervalTickListener(interval, onTickListener, intermediate));
        start(false);
    }

    @Override
    public void unregister(OnTickListener onTickListener) {
        mListeners.remove(onTickListener);
    }

    @Override
    public boolean isRegistered(OnTickListener onTickListener) {
        return mListeners.containsKey(onTickListener);
    }

    public void runTask() {
        mCopyListeners.clear();
        mCopyListeners.addAll(mListeners.values());
        for (int i = 0, size = mCopyListeners.size(); i < size; i++) {
            IntervalTickListener listener = mCopyListeners.get(i);
            listener.onTick();
        }
        if (mListeners.isEmpty()) {
            stop();
        }
    }

    /**
     * Timer status
     */
    @Keep
    public enum TimerStatus {

        /**
         * Waiting
         */
        Waiting(0, "Waiting"),
        /**
         * Running
         */
        Running(1, "Running"),
        /**
         * Paused
         */
        Paused(-1, "Paused"),
        /**
         * Stopped
         */
        Stopped(-2, "Stopped");

        private int code;
        private String desc;

        TimerStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    static final class IntervalTickListener {
        private final int interval;
        private int count;

        private final OnTickListener mListener;

        IntervalTickListener(int interval, @NonNull OnTickListener onTickListener, boolean intermediate) {
            this.interval = interval;
            this.count = 0;
            this.mListener = onTickListener;

            if (intermediate) {
                onTickListener.onTick(0);
            }
        }

        void onTick() {
            count = (count + 1) % interval;
            if (count == 0) {
                if (mListener != null) {
                    mListener.onTick(0);
                }
            }
        }

    }

}