package com.dyaco.spiritbike;

public class HeartRateBean {

    public HeartRateBean(String uuid, String deviceName, String mac) {
        this.uuid = uuid;
        this.deviceName = deviceName;
        this.mac = mac;
    }

    private String uuid;
    private String deviceName;
    private String mac;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
