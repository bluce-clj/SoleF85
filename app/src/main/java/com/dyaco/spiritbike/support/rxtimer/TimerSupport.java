package com.dyaco.spiritbike.support.rxtimer;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;
import com.dyaco.spiritbike.support.rxtimer.HandlerTimer.*;

import io.reactivex.Observable;

public class TimerSupport {

    private static final int MILLISECOND = 1000;

    private ITimer mDefaultTimer = new HandlerTimer(MILLISECOND);

    public void setSupportRx(boolean supportRx) {
        if (supportRx) {
            if (!(mDefaultTimer instanceof RxTimer)) {
                mDefaultTimer = new RxTimer(MILLISECOND);
            }
        } else {
            if (!(mDefaultTimer instanceof HandlerTimer)) {
                mDefaultTimer = new HandlerTimer(MILLISECOND);
            }
        }
    }

    public void register(int interval, @NonNull OnTickListener onTickListener) {
        register(interval, onTickListener, false);
    }

    /**
     * onTickListener will store as weak reference, don't use anonymous class here
     *
     * @param interval       how many seconds of interval that the listener will be called in
     * @param onTickListener listener
     * @param intermediate   whether execute onTick intermediately
     */
    public void register(int interval, @NonNull OnTickListener onTickListener, boolean intermediate) {
        mDefaultTimer.register(interval, onTickListener, intermediate);
    }

    /**
     * unregister a onTickListener
     * @param onTickListener
     */
    public void unregister(@NonNull OnTickListener onTickListener) {
        mDefaultTimer.unregister(onTickListener);
    }

    /**
     * @return current timer status
     */
    @Keep
    public TimerStatus getStatus() {
        return mDefaultTimer.getStatus();
    }

    /**
     * restart timer
     */
    public void restart() {
        mDefaultTimer.restart();
    }

    /**
     * pause timer
     */
    public void pause() {
        mDefaultTimer.pause();
    }

    /**
     * clear timer listener and stop timer
     */
    public void clear() {
        mDefaultTimer.stop();
        mDefaultTimer.clear();
    }

    public boolean isRegistered(@NonNull OnTickListener onTickListener) {
        return mDefaultTimer.isRegistered(onTickListener);
    }


    /**
     * tick lister, called at every registered interval time passed
     */
    public interface OnTickListener {
        void onTick(long tick);
    }

    /**
     * return tick observable for each user, user should handle the observable with cell's lifecycle
     * @param interval timer interval, in TimeUnit.SECOND
     * @param intermediate true, get event immediately
     * @return
     * @since 3.0.0
     */
    public Observable<Long> getTickObservable(int interval, boolean intermediate) {
        return Observable.interval(intermediate ? 0 : interval, interval, TimeUnit.SECONDS);
    }

    /**
     * return tick observable for each user, user should handle the observable with cell's lifecycle
     * @param interval timer interval, in TimeUnit.SECOND
     * @param total total event count
     * @param intermediate true, get event immediately
     * @return
     * @since 3.0.0
     */
    public Observable<Long> getTickObservable(int interval, int total, boolean intermediate) {
        return Observable.intervalRange(0, total, intermediate ? 0 : interval, interval, TimeUnit.SECONDS);
    }

}