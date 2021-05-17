package com.dyaco.spiritbike;

import com.corestar.libs.ftms.StopOrPause;
import com.dyaco.spiritbike.uart.isBusEvent;

public class FTMSBean implements isBusEvent {

    private int ftmsNotifyType;
    private int stopOrPause; // stop 1  pause 2
    private int startOrResume; //start 1 resume 2
    private int setLevel;
    private int setIncline;
    private int eventType;

    public int getFtmsNotifyType() {
        return ftmsNotifyType;
    }

    public void setFtmsNotifyType(int ftmsNotifyType) {
        this.ftmsNotifyType = ftmsNotifyType;
    }

    public int getStopOrPause() {
        return stopOrPause;
    }

    public void setStopOrPause(int stopOrPause) {
        this.stopOrPause = stopOrPause;
    }

    public int getStartOrResume() {
        return startOrResume;
    }

    public void setStartOrResume(int startOrResume) {
        this.startOrResume = startOrResume;
    }

    public int getSetLevel() {
        return setLevel;
    }

    public void setSetLevel(int setLevel) {
        this.setLevel = setLevel;
    }

    public int getSetIncline() {
        return setIncline;
    }

    public void setSetIncline(int setIncline) {
        this.setIncline = setIncline;
    }

    @Override
    public int getEventType() {
        return eventType;
    }

    @Override
    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
}
