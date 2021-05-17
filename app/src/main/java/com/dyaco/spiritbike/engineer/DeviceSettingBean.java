package com.dyaco.spiritbike.engineer;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DeviceSettingBean implements Parcelable {

    String baseUrl = "http://ugymota.azurewebsites.net/";
    /**
     * 0 --> Metric
     * 1 --> Imperial
     */
    private int unit_code; //0公 1英
    private double ODO_time = 0;
    private double ODO_distance = 0;

//	0 --> off (會休眠)
//	1 --> on (不休眠)
    private int display_mode;

    private int beep_sound;
    private String dsInclineAd;
    private String dsLevelAd;
    private boolean first_launch = true;
    /**
     * 0 --> XE395ENT
     * 1 --> XBR55ENT
     * 2 --> XBU55ENT
     */
    private int model_code = 0;
    private String model_name;
    private String unit = "Metric";
    private int child_lock;
    private int display_brightness = 85;
    /**
     * XE395_FTMS
     * XBR55_FTMS
     * XBU55_FTMS
     */
    private String ble_device_name;

    private int time_unit = 12;
    private String timeAM_PM = "AM";

    private String updateUrl;

    /**
     * category_code
     * 0=Treadmill,
     * 1=Bike,
     * 2=Elliptical
     * 3=Stepper
     * 4=Rower
     */
    private String categoryCode;

    private int maxRpm;
    private int minRpm;
    private int maxLevel;
    private int minLevel;
    private String brand_name;

    //100/010
    private String pModelName;

    private String updateURL;

//    public String getUpdateUrl() {
//        String url = baseUrl + brand_name + "/" + modelName + "/" + unitCode + "/" + sleepMode + "/" + childLock + "/debug/";
//        Log.d("XXXXXXXXX", "getUpdateUrl: " + url);
//        return url;
//    }


//    public String getUpdateUrl() {
//        String url = baseUrl + brand_name + "/debug/";
//        Log.d("XXXXXXXXX", "getUpdateUrl: " + url);
//        return url;
//    }


    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public int getMaxRpm() {
        return maxRpm;
    }

    public void setMaxRpm(int maxRpm) {
        this.maxRpm = maxRpm;
    }

    public int getMinRpm() {
        return minRpm;
    }

    public void setMinRpm(int minRpm) {
        this.minRpm = minRpm;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getChild_lock() {
        return child_lock;
    }

    public void setChild_lock(int child_lock) {
        this.child_lock = child_lock;
    }

    public int getDisplay_brightness() {
        return display_brightness;
    }

    public void setDisplay_brightness(int display_brightness) {
        this.display_brightness = display_brightness;
    }

    public String getBle_device_name() {

        return ble_device_name;
    }

    public void setBle_device_name(String ble_device_name) {
        this.ble_device_name = ble_device_name;
    }

    public int getTime_unit() {
        return time_unit;
    }

    public void setTime_unit(int time_unit) {
        this.time_unit = time_unit;
    }

    public String getTimeAM_PM() {
        return timeAM_PM;
    }

    public void setTimeAM_PM(String timeAM_PM) {
        this.timeAM_PM = timeAM_PM;
    }

    public int getUnit_code() {
        return unit_code;
    }

    public void setUnit_code(int unit_code) {
        this.unit_code = unit_code;
    }

    public double getODO_time() {
        return ODO_time;
    }

    public void setODO_time(double ODO_time) {
        this.ODO_time = ODO_time;
    }

    public double getODO_distance() {
        return ODO_distance;
    }

    public void setODO_distance(double ODO_distance) {
        this.ODO_distance = ODO_distance;
    }

    public int getDisplay_mode() {
        return display_mode;
    }

    public void setDisplay_mode(int display_mode) {
        this.display_mode = display_mode;
    }

    public int getBeep_sound() {
        return beep_sound;
    }

    public void setBeep_sound(int beep_sound) {
        this.beep_sound = beep_sound;
    }

    public String getDsInclineAd() {
        return dsInclineAd;
    }

    public void setDsInclineAd(String dsInclineAd) {
        this.dsInclineAd = dsInclineAd;
    }

    public String getDsLevelAd() {
        return dsLevelAd;
    }

    public void setDsLevelAd(String dsLevelAd) {
        this.dsLevelAd = dsLevelAd;
    }

    public boolean isFirst_launch() {
        return first_launch;
    }

    public void setFirst_launch(boolean first_launch) {
        this.first_launch = first_launch;
    }

    public int getModel_code() {
        return model_code;
    }

    public void setModel_code(int model_code) {
        this.model_code = model_code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public DeviceSettingBean() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(unit_code);
        dest.writeDouble(ODO_time);
        dest.writeDouble(ODO_distance);
        dest.writeInt(display_mode);
        dest.writeInt(beep_sound);
        dest.writeString(dsInclineAd);
        dest.writeString(dsLevelAd);
        dest.writeByte((byte) (first_launch ? 1 : 0));
        dest.writeInt(model_code);
        dest.writeInt(time_unit);
        dest.writeString(timeAM_PM);
        dest.writeString(ble_device_name);
        dest.writeString(model_name);
        dest.writeString(unit);
        dest.writeInt(child_lock);
        dest.writeInt(display_brightness);
        dest.writeString(categoryCode);
        dest.writeInt(maxRpm);
        dest.writeInt(minRpm);
        dest.writeInt(maxLevel);
        dest.writeInt(minLevel);
        dest.writeString(brand_name);
        dest.writeString(updateUrl);
        dest.writeString(pModelName);
    }

    protected DeviceSettingBean(Parcel in) {
        unit_code = in.readInt();
        ODO_time = in.readDouble();
        ODO_distance = in.readDouble();
        display_mode = in.readInt();
        beep_sound = in.readInt();
        dsInclineAd = in.readString();
        dsLevelAd = in.readString();
        first_launch = in.readByte() != 0;
        model_code = in.readInt();
        time_unit = in.readInt();
        timeAM_PM = in.readString();
        ble_device_name = in.readString();
        model_name = in.readString();
        unit = in.readString();
        child_lock = in.readInt();
        display_brightness = in.readInt();
        categoryCode = in.readString();

        maxRpm = in.readInt();
        minRpm = in.readInt();
        maxLevel = in.readInt();
        minLevel = in.readInt();
        brand_name = in.readString();
        updateUrl = in.readString();
        pModelName = in.readString();
    }

    public static final Creator<DeviceSettingBean> CREATOR = new Creator<DeviceSettingBean>() {
        @Override
        public DeviceSettingBean createFromParcel(Parcel in) {
            return new DeviceSettingBean(in);
        }

        @Override
        public DeviceSettingBean[] newArray(int size) {
            return new DeviceSettingBean[size];
        }
    };


    @NonNull
    @Override
    public String toString() {
        return "DeviceSettingBean{" +
                ", unit_code=" + unit_code +
                ", ODO_time=" + ODO_time +
                ", ODO_distance=" + ODO_distance +
                ", sleep_mode=" + display_mode +
                ", beep_sound=" + beep_sound +
                ", dsInclineAd='" + dsInclineAd + '\'' +
                ", dsLevelAd='" + dsLevelAd + '\'' +
                ", first_launch=" + first_launch +
                ", model_code=" + model_code +
                ", model_name='" + model_name + '\'' +
                ", unit='" + unit + '\'' +
                ", child_lock=" + child_lock +
                ", display_brightness=" + display_brightness +
                ", ble_device_name='" + ble_device_name + '\'' +
                ", time_unit='" + time_unit + '\'' +
                ", timeAM_PM='" + timeAM_PM + '\'' +
                ", updateUrl='" + updateUrl + '\'' +
                ", categoryCode='" + categoryCode + '\'' +
                ", maxRpm=" + maxRpm +
                ", minRpm=" + minRpm +
                ", maxLevel=" + maxLevel +
                ", minLevel=" + minLevel +
                ", brand_name='" + brand_name + '\'' +
                ", pModelName='" + pModelName + '\'' +
                ", updateURL='" + updateURL + '\'' +
                '}';
    }
}
