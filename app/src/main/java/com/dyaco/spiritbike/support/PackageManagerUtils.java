package com.dyaco.spiritbike.support;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.dyaco.spiritbike.MainActivity;
import com.dyaco.spiritbike.support.banner.util.LogUtils;

import java.util.List;

public class PackageManagerUtils {
    public static PackageManagerUtils instance;
    private Activity mActivity;
    private PackageManager mPackageManager;


    public static PackageManagerUtils instance(Activity activity) {
        if (instance == null) {
            new PackageManagerUtils(activity);
            return instance;
        }
        return instance;
    }

    public PackageManagerUtils(Activity activity) {
        this.mActivity = activity;
        this.mPackageManager = this.mActivity.getPackageManager();
    }

    public void getPackageSystemDataLog() {
        if (this.mPackageManager != null) {
            List<ApplicationInfo> packages = this.mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo packageInfo : packages) {
                if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                    LogUtils.d("Installed package (System) :" + packageInfo.packageName);
                else
                    LogUtils.d("Installed package (User) :" + packageInfo.packageName);
            }
        } else {
            LogUtils.d("mPackageManager != null");
        }


    }

}
