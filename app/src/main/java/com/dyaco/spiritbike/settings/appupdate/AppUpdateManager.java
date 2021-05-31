package com.dyaco.spiritbike.settings.appupdate;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.dyaco.spiritbike.support.rxtimer.PackageManagerUtils;

import java.util.List;

public class AppUpdateManager {
    public static final String PACKAGE_NAME_NETFLIX = "com.netflix.mediaclient";
    public static final String PACKAGE_NAME_HULU = "com.hulu.plus";
    public static final String PACKAGE_NAME_ABC_NEWS = "com.abc.abcnews";
    public static final String PACKAGE_NAME_MSNBC = "com.zumobi.msnbc";
    public static final String PACKAGE_NAME_CNN = "com.cnn.mobile.android.phone";
    public static final String PACKAGE_NAME_FOX_NEWS = "com.foxnews.android";

    public static final int REPLACED_STATUS_NETFLIX = 000;
    public static final int REPLACED_STATUS_HULU = 111;
    public static final int REPLACED_STATUS_ABC_NEWS = 222;
    public static final int REPLACED_STATUS_MSNBC = 333;
    public static final int REPLACED_STATUS_CNN = 444;
    public static final int REPLACED_STATUS_FOX_NEWS = 555;

    public static AppUpdateManager instance;

    private Context mContext;
    private PackageManagerUtils mPackageManagerUtils;

    public static AppUpdateManager getInstance(Context context) {
        if (instance == null) instance = new AppUpdateManager(context);
        return instance;
    }

    public AppUpdateManager(Context context) {
        this.mContext = context;
        this.mPackageManagerUtils = new PackageManagerUtils();
    }

    public PackageInfo getAppUpdateInfo(String packageName) {
        return mPackageManagerUtils.getPackageInfo(this.mContext, packageName);
    }


    public PackageInfo getNetflixInfo() {
        return getAppUpdateInfo(PACKAGE_NAME_NETFLIX);
    }

    public PackageInfo getHuluInfo() {
        return getAppUpdateInfo(PACKAGE_NAME_HULU);
    }

    public PackageInfo getAbcNewsInfo() {
        return getAppUpdateInfo(PACKAGE_NAME_ABC_NEWS);
    }

    public PackageInfo getNbcInfo() {
        return getAppUpdateInfo(PACKAGE_NAME_MSNBC);
    }

    public PackageInfo getCnnInfo() {
        return getAppUpdateInfo(PACKAGE_NAME_CNN);
    }

    public PackageInfo getFoxNewsInfo() {
        return getAppUpdateInfo(PACKAGE_NAME_FOX_NEWS);
    }

    public String getAppUpdateVersion(List<AppUpdateData.AppUpdateBean> appUpdateBeans, String appUpdateName) {
        if (appUpdateBeans == null) return null;
        String version = "";

        for (int i = 0; i < appUpdateBeans.size(); i++) {
            if (appUpdateBeans.get(i).getPackageName().equals(appUpdateName)) {
                version = appUpdateBeans.get(i).getVersion();
                break;
            }
        }
        return version;
    }

    public String getAppUpdateDownloadUrl(List<AppUpdateData.AppUpdateBean> appUpdateBeans, String appUpdateName) {
        if (appUpdateBeans == null) return null;
        String DownloadUrl = "";

        for (int i = 0; i < appUpdateBeans.size(); i++) {
            if (appUpdateBeans.get(i).getPackageName().equals(appUpdateName)) {
                DownloadUrl = appUpdateBeans.get(i).getDownloadURL();
                break;
            }
        }
        return DownloadUrl;
    }

    public boolean isForceUpdates(List<AppUpdateData.AppUpdateBean> appUpdateBeans, String appUpdateName) {
        boolean isForceUpdates = false;

        for (int i = 0; i < appUpdateBeans.size(); i++) {
            if (appUpdateBeans.get(i).getPackageName().equals(appUpdateName)) {
                if (appUpdateBeans.get(i).getForceUpdate().equals("YES"))
                    isForceUpdates = true;
                break;
            }
        }
        return isForceUpdates;
    }


}
