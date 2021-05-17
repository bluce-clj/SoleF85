package com.dyaco.spiritbike.workout;

import com.dyaco.spiritbike.uart.isBusEvent;

public class BleHrBean implements isBusEvent {

    private int eventType;
    private int hr;

    public BleHrBean(int eventType, int hr) {
        this.eventType = eventType;
        this.hr = hr;
    }

    public int getHr() {
        return hr;
    }

    public void setHr(int hr) {
        this.hr = hr;
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
