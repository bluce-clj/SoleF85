package com.dyaco.spiritbike.settings.appupdate;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.loader.content.Loader;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppUpdateData implements Parcelable {
    private List<AppUpdateBean> appUpdateBeans;

    public AppUpdateData() {
    }

    public AppUpdateData(List<AppUpdateBean> appUpdateBeans) {
        this.appUpdateBeans = appUpdateBeans;
    }

    protected AppUpdateData(Parcel in) {
    }

    public static final Creator<AppUpdateData> CREATOR = new Creator<AppUpdateData>() {
        @Override
        public AppUpdateData createFromParcel(Parcel in) {
            return new AppUpdateData(in);
        }

        @Override
        public AppUpdateData[] newArray(int size) {
            return new AppUpdateData[size];
        }
    };

    public List<AppUpdateBean> getAppUpdateBeans() {
        return appUpdateBeans;
    }



    public void setAppUpdateBeans(List<AppUpdateBean> appUpdateBeans) {
        this.appUpdateBeans = appUpdateBeans;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public static class AppUpdateBean implements Parcelable{
        @SerializedName("Version")
        private String version;
        @SerializedName("Version Code")
        private int versionCode;
        @SerializedName("Package Name")
        private String packageName;
        private String MD5;
        @SerializedName("Download URL")
        private String downloadURL;
        @SerializedName("Force updates")
        private String forceUpdate;
        @SerializedName("Path")
        private String path;


        public AppUpdateBean(String version, int versionCode, String packageName, String MD5, String downloadURL, String forceUpdate, String path) {
            this.version = version;
            this.versionCode = versionCode;
            this.packageName = packageName;
            this.MD5 = MD5;
            this.downloadURL = downloadURL;
            this.forceUpdate = forceUpdate;
            this.path = path;
        }

        protected AppUpdateBean(Parcel in) {
            version = in.readString();
            versionCode = in.readInt();
            packageName = in.readString();
            MD5 = in.readString();
            downloadURL = in.readString();
            forceUpdate = in.readString();
            path = in.readString();
        }

        public static final Creator<AppUpdateBean> CREATOR = new Creator<AppUpdateBean>() {
            @Override
            public AppUpdateBean createFromParcel(Parcel in) {
                return new AppUpdateBean(in);
            }

            @Override
            public AppUpdateBean[] newArray(int size) {
                return new AppUpdateBean[size];
            }
        };

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getMD5() {
            return MD5;
        }

        public void setMD5(String MD5) {
            this.MD5 = MD5;
        }

        public String getDownloadURL() {
            return downloadURL;
        }

        public void setDownloadURL(String downloadURL) {
            this.downloadURL = downloadURL;
        }

        public String getForceUpdate() {
            return forceUpdate;
        }

        public void setForceUpdate(String forceUpdate) {
            this.forceUpdate = forceUpdate;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(version);
            dest.writeInt(versionCode);
            dest.writeString(packageName);
            dest.writeString(MD5);
            dest.writeString(downloadURL);
            dest.writeString(forceUpdate);
            dest.writeString(path);
        }
    }
}
