package com.dyaco.spiritbike;

import com.corestar.libs.device.Device;
import com.dyaco.spiritbike.uart.isBusEvent;

import java.util.List;

public class KeyBean implements isBusEvent {

    private int eventType;
    private int keyStatus; //0 單  //1 組合
    private boolean isPause; //0 單  //1 組合
    private Device.KEY key;
    List<Device.KEY> keys;

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public int getKeyStatus() {
        return keyStatus;
    }

    public void setKeyStatus(int keyStatus) {
        this.keyStatus = keyStatus;
    }

    public Device.KEY getKey() {
        return key;
    }

    public void setKey(Device.KEY key) {
        this.key = key;
    }

    public List<Device.KEY> getKeys() {
        return keys;
    }

    public void setKeys(List<Device.KEY> keys) {
        this.keys = keys;
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
