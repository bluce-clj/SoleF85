package com.dyaco.spiritbike.engineer;

import com.dyaco.spiritbike.MyApplication;

public class UpdateUrlBean {
    String baseUrl = "http://ugymota.azurewebsites.net/";
    String brandName;
    String modelName;
    int unitCode;
    int sleepMode;
    int childLock;

    public UpdateUrlBean(String brandName, String modelName, int unitCode, int sleepMode, int childLock) {
        this.brandName = brandName;
        this.modelName = modelName;
        this.unitCode = unitCode;
        this.sleepMode = sleepMode;
        this.childLock = childLock;
        setUpdateURL(baseUrl + "/" + brandName + "/" + modelName + "/" + unitCode + "/" + sleepMode + "/" + childLock);
    }

    public void setUpdateURL(String updateURL) {
        DeviceSettingBean deviceSettingBean = MyApplication.getInstance().getDeviceSettingBean();
        deviceSettingBean.setUpdateUrl(updateURL);
    }
}
