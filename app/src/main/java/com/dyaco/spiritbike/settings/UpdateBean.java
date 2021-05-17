package com.dyaco.spiritbike.settings;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateBean {
    /**
     * #取得 APK 的 MD5 SHA1
     * keytool -printcert -jarfile /Users/mac/GoogleDrive/AndroidStudioProjects/SoleF85/app/build/outputs/apk/production/release/SpiritV1.1.0-0106A_\[production\].apk
     *
     * #apk 是否簽名
     * jarsigner -verify /Users/mac/Desktop/app-dev-release.aab
     */

    @SerializedName("Version Code")
    private int VersionCode;
    private String Version;
    private String Territory;
    private String Brand;
    private String Model;
    @SerializedName("Model Name")
    private String modelName;
    private String MD5;
    @SerializedName("Download URL")
    private String downloadURL;

    @SerializedName("Force updates")
    private String forceUpdate;

    //APK更新檔檔名與路徑(For USB upgrades Only)
    private String PATH;

    private String OS_Version;

    public String getOS_Version() {
        return OS_Version;
    }

    public void setOS_Version(String OS_Version) {
        this.OS_Version = OS_Version;
    }

    public String getPATH() {
        return PATH;
    }

    public void setPATH(String PATH) {
        this.PATH = PATH;
    }

    public String getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(String forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    private List<String> Modifications;

    public List<String> getModifications() {
        return Modifications;
    }

    public void setModifications(List<String> Modifications) {
        this.Modifications = Modifications;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String Version) {
        this.Version = Version;
    }

    public String getTerritory() {
        return Territory;
    }

    public void setTerritory(String Territory) {
        this.Territory = Territory;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String Brand) {
        this.Brand = Brand;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String Model) {
        this.Model = Model;
    }



    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public int getVersionCode() {
        return VersionCode;
    }

    public void setVersionCode(int versionCode) {
        VersionCode = versionCode;
    }

    @NonNull
    @Override
    public String toString() {
        return "UpdateBean{" +
                "VersionCode=" + VersionCode +
                ", Version='" + Version + '\'' +
                ", Territory='" + Territory + '\'' +
                ", Brand='" + Brand + '\'' +
                ", Model='" + Model + '\'' +
                ", modelName='" + modelName + '\'' +
                ", MD5='" + MD5 + '\'' +
                ", downloadURL='" + downloadURL + '\'' +
                ", forceUpdate='" + forceUpdate + '\'' +
                ", PATH='" + PATH + '\'' +
                ", OS_Version='" + OS_Version + '\'' +
                ", Modifications=" + Modifications +
                '}';
    }
}
