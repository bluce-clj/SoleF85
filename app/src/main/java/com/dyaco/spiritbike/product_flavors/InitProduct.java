package com.dyaco.spiritbike.product_flavors;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import com.dyaco.spiritbike.BuildConfig;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.engineer.DeviceSettingBean;
import com.google.gson.Gson;

import static com.dyaco.spiritbike.MyApplication.MODE;

public class InitProduct {
    private final Context context;

    public InitProduct(Context context) {
        this.context = context;
    }

    public void setProductDefault(String settingText) {

        DeviceSettingBean deviceSettingBean = new Gson().fromJson(settingText, DeviceSettingBean.class);

        MODE = ModeEnum.getMode(deviceSettingBean.getModel_code());

        switch (MODE) {
            case XE395ENT:
                deviceSettingBean.setCategoryCode("2");
                deviceSettingBean.setMaxRpm(120);
                deviceSettingBean.setMinRpm(20);
                deviceSettingBean.setMaxLevel(20);
                deviceSettingBean.setMinLevel(1);
                break;
            case XBR55ENT:
            case XBU55ENT:
                deviceSettingBean.setCategoryCode("1");
                deviceSettingBean.setMaxRpm(120);
                deviceSettingBean.setMinRpm(15);
                deviceSettingBean.setMaxLevel(20);
                deviceSettingBean.setMinLevel(1);
                break;
        }

        MyApplication.getInstance().setDeviceSettingBean(deviceSettingBean);


        int time;
        if (deviceSettingBean.getDisplay_mode() == 1) {
            time = 2147483647; //不休眠
        } else {
            time = 60 * 30 * 1000; //30分鐘
        }
        setTimeOut(time);

        saveBrightness(getBrightness255(deviceSettingBean.getDisplay_brightness()));

      //  setBrightness((Activity) context, getBrightness255(deviceSettingBean.getDisplay_brightness()));
      //  Log.d("FFFFFF", "setProductDefault: " + deviceSettingBean.toString());
    }

    public void setProductDefault(ModeEnum modeEnum) {
        //Brand/Model_Name/unit code/sleep mode/child lock

        DeviceSettingBean deviceSettingBean = new DeviceSettingBean();
        // String modelName = context.getString(R.string.model_name);
        deviceSettingBean.setBrand_name("SPIRIT");
        deviceSettingBean.setODO_distance(0d);
        deviceSettingBean.setODO_time(0d);
        deviceSettingBean.setUpdateUrl(BuildConfig.UPDATE_URL);
        switch (modeEnum) {
            case XE395ENT:
                deviceSettingBean.setModel_name("XE395ENT");
                deviceSettingBean.setUnit_code(1);
                deviceSettingBean.setDisplay_mode(1);
                deviceSettingBean.setChild_lock(0);
                deviceSettingBean.setModel_code(0);
                deviceSettingBean.setUnit("Imperial");
                deviceSettingBean.setBeep_sound(1);
                deviceSettingBean.setDisplay_brightness(85);
                deviceSettingBean.setTime_unit(12);
                deviceSettingBean.setBle_device_name("XE395ENT");
                deviceSettingBean.setFirst_launch(true);
                deviceSettingBean.setCategoryCode("2");
                deviceSettingBean.setMaxRpm(120);
                deviceSettingBean.setMinRpm(20);
                deviceSettingBean.setMaxLevel(20);
                deviceSettingBean.setMinLevel(1);
                break;
            case XBR55ENT:
                deviceSettingBean.setModel_name("XBR55ENT");
                deviceSettingBean.setUnit_code(1);
                deviceSettingBean.setDisplay_mode(1);
                deviceSettingBean.setChild_lock(0);
                deviceSettingBean.setModel_code(1);
                deviceSettingBean.setUnit("Imperial");
                deviceSettingBean.setBeep_sound(1);
                deviceSettingBean.setDisplay_brightness(85);
                deviceSettingBean.setTime_unit(12);
                deviceSettingBean.setBle_device_name("XBR55ENT");
                deviceSettingBean.setFirst_launch(true);
                deviceSettingBean.setCategoryCode("1");
                deviceSettingBean.setMaxRpm(120);
                deviceSettingBean.setMinRpm(15);
                deviceSettingBean.setMaxLevel(20);
                deviceSettingBean.setMinLevel(1);
                break;
            case XBU55ENT:
                deviceSettingBean.setModel_name("XBU55ENT");
                deviceSettingBean.setUnit_code(1);
                deviceSettingBean.setChild_lock(0);
                deviceSettingBean.setDisplay_mode(1);
                deviceSettingBean.setModel_code(2);
                deviceSettingBean.setUnit("Imperial");
                deviceSettingBean.setBeep_sound(1);
                deviceSettingBean.setDisplay_brightness(85);
                deviceSettingBean.setTime_unit(12);
                deviceSettingBean.setBle_device_name("XBU55ENT");
                deviceSettingBean.setFirst_launch(true);
                deviceSettingBean.setCategoryCode("1");
                deviceSettingBean.setMaxRpm(120);
                deviceSettingBean.setMinRpm(15);
                deviceSettingBean.setMaxLevel(20);
                deviceSettingBean.setMinLevel(1);
                break;
            default:
        }

        MODE = ModeEnum.getMode(deviceSettingBean.getModel_code());
        MyApplication.getInstance().setDeviceSettingBean(deviceSettingBean);

        int time;
        if (deviceSettingBean.getDisplay_mode() == 1) {
            time = 60 * 30 * 1000;
        } else {
            time = 2147483647;
        }
        setTimeOut(time);


        saveBrightness(getBrightness255(deviceSettingBean.getDisplay_brightness()));
        //    setBrightness((Activity) context, getBrightness255(deviceSettingBean.getDisplay_brightness()));
    }

    private void setTimeOut(int time) {
        try {
            String t = (time == 2147483647 ? "不休眠" : String.valueOf(time));
            Log.d("SETTING_FILE", "休眠時間: "+ t);
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getChannel() {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("CHANNEL");
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return "NO_NAME000";
    }


    public void saveBrightness(int brightness) {
        try {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = android.provider.Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
            android.provider.Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
            resolver.notifyChange(uri, null);
            Log.d("SETTING_FILE", "亮度: "+ brightness);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int getBrightness255(float value) {
        return (int)((value * 0.01f) * 255f);
    }
}
