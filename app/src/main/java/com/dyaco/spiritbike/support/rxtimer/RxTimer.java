package com.dyaco.spiritbike.support.rxtimer;

import android.util.Log;

import androidx.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import com.dyaco.spiritbike.support.rxtimer.HandlerTimer.*;
import com.dyaco.spiritbike.support.rxtimer.TimerSupport.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RxTimer implements ITimer {

    private final long mInterval;

    private TimerStatus mStatus;

    private boolean pause;

    private final Observable<Long> mIntervalObservable;

    private final ConcurrentHashMap<OnTickListener, Disposable> tickCache = new ConcurrentHashMap<>();

    public RxTimer(long interval) {
        this.mInterval = interval;
        this.mStatus = TimerStatus.Waiting;
        this.mIntervalObservable = Observable
                .interval(0, this.mInterval, TimeUnit.MILLISECONDS)
                .doOnSubscribe(disposable -> {
                    mStatus = TimerStatus.Running;
                    Log.d("RxTimerSupportTest", "accept " + disposable + " status " + mStatus);
                })
                .doOnDispose(() -> {
                    mStatus = TimerStatus.Paused;
                    Log.d("RxTimerSupportTest", "on dispose " + " status " + mStatus);
                })
                .doOnTerminate(() -> {
                    mStatus = TimerStatus.Stopped;
                    Log.d("RxTimerSupportTest", "on terminate " + " status " + mStatus);
                })
                .share();
    }

    public void register(final int interval, @NonNull final OnTickListener onTickListener, boolean intermediate) {
        Disposable disposable = this.mIntervalObservable.delaySubscription(intermediate ? 0 : interval * mInterval, TimeUnit.MILLISECONDS).skipWhile(
                aLong -> pause).observeOn(
                AndroidSchedulers.mainThread()).subscribe(count -> {
                    Log.d("RxTimerSupportTest", "accept " + " value " + count);
                    if (count % interval == 0) {
                        onTickListener.onTick(count);
                    }
                });
        tickCache.put(onTickListener, disposable);
    }

    public void unregister(OnTickListener onTickListener) {
        Disposable disposable = tickCache.get(onTickListener);
        if (disposable != null) {
            disposable.dispose();
            tickCache.remove(onTickListener);
        }
    }

    public boolean isRegistered(@NonNull OnTickListener onTickListener) {
        return tickCache.containsKey(onTickListener);
    }

    @Override
    public void start() {
        //no need
    }

    @Override
    public void start(boolean bySecond) {
        //no need
    }

    @Override
    public void pause() {
        pause = true;
        mStatus = TimerStatus.Paused;
    }

    @Override
    public void restart() {
        pause = false;
        if (mStatus == TimerStatus.Paused) {
            mStatus = TimerStatus.Running;
        }
    }

    @Override
    public void stop() {
        for (Map.Entry<OnTickListener, Disposable> entry : tickCache.entrySet()) {
            Disposable disposable = entry.getValue();
            disposable.dispose();
        }
        tickCache.clear();
    }

    @Override
    public void cancel() {
        //no need
    }

    @Override
    public void clear() {
        tickCache.clear();
    }

    @Override
    public TimerStatus getStatus() {
        return mStatus;
    }
}