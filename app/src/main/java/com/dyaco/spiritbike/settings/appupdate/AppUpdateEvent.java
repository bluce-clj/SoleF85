package com.dyaco.spiritbike.settings.appupdate;

import com.dyaco.spiritbike.uart.isBusEvent;

public class AppUpdateEvent implements isBusEvent {
    private int evenType;



    @Override
    public int getEventType() {
        return evenType;
    }

    @Override
    public void setEventType(int eventType) {
        this.evenType = eventType;
    }

    public AppUpdateEvent(int evenType) {
        this.evenType = evenType;
    }


    public void setEvenType(int evenType) {
        this.evenType = evenType;
    }
}
