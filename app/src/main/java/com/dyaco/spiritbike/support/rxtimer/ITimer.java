package com.dyaco.spiritbike.support.rxtimer;

import com.dyaco.spiritbike.support.rxtimer.TimerSupport.*;
public interface ITimer {
    /**
     * start timer
     */
    void start();

    /**
     * @param bySecond true, start timer with interval alignment; false, start timer immediately
     */
    void start(boolean bySecond);

    /**
     * pause timer
     */
    void pause();


    /**
     * restart timer
     */
    void restart();

    /**
     * stop timer
     */
    void stop();

    /**
     * clear listeners
     */
    void clear();

    /**
     * cancel timer
     */
    void cancel();

    /**
     * @return timer status
     */
    HandlerTimer.TimerStatus getStatus();

    void register(int interval, OnTickListener onTickListener, boolean immediate);

    void unregister(OnTickListener onTickListener);

    boolean isRegistered(OnTickListener onTickListener);
}