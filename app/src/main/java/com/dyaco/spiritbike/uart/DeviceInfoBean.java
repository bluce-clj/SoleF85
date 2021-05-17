package com.dyaco.spiritbike.uart;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.corestar.libs.device.Device;

public class DeviceInfoBean implements Parcelable {

    private int eventType;
    private String model;
    private String version;
    private String key;
    private String lwrVersion;
    private int hrmStatus;

    public DeviceInfoBean(int eventType, String model, String version, String key, String lwrVersion, int hrmStatus) {
        this.eventType = eventType;
        this.model = model;
        this.version = version;
        this.key = key;
        this.lwrVersion = lwrVersion;
        this.hrmStatus = hrmStatus;
    }

    public DeviceInfoBean() {
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLwrVersion() {
        return lwrVersion;
    }

    public void setLwrVersion(String lwrVersion) {
        this.lwrVersion = lwrVersion;
    }

    public int getHrmStatus() {
        return hrmStatus;
    }

    public void setHrmStatus(int hrmStatus) {
        this.hrmStatus = hrmStatus;
    }

    @NonNull
    @Override
    public String toString() {
        return "DeviceInfoBean{" +
                "eventType=" + eventType +
                ", model=" + model +
                ", version='" + version + '\'' +
                ", key='" + key + '\'' +
                ", lwrVersion='" + lwrVersion + '\'' +
                ", hrmStatus=" + hrmStatus +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(eventType);
        dest.writeString(model);
        dest.writeString(version);
        dest.writeString(key);
        dest.writeString(lwrVersion);
        dest.writeInt(hrmStatus);
    }

    protected DeviceInfoBean(Parcel in) {
        eventType = in.readInt();
        model = in.readString();
        version = in.readString();
        key = in.readString();
        lwrVersion = in.readString();
        hrmStatus = in.readInt();
    }

    public static final Creator<DeviceInfoBean> CREATOR = new Creator<DeviceInfoBean>() {
        @Override
        public DeviceInfoBean createFromParcel(Parcel in) {
            return new DeviceInfoBean(in);
        }

        @Override
        public DeviceInfoBean[] newArray(int size) {
            return new DeviceInfoBean[size];
        }
    };

}
