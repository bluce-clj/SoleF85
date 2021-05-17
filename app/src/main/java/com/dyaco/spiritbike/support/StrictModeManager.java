package com.dyaco.spiritbike.support;

import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import java.io.File;

public class StrictModeManager {
    private static final String STRICT_MODE_CFG = "ovo.top.stictmode.cfg/enable";
    private static final String ACTIVITY_LEAKS_DETECT_CFG = "ovo.top.stictmode.cfg/enable/activity_leaks_detect";
    private static final boolean DEBUG = false;
    private static boolean sIsInStrictMode = false;
    private static boolean sEnableActivityLeaksDetect = false;
    private static boolean sIsFlgChecked = false;

    public static void init() {
     //   checkStrictModeCfg();
        sIsInStrictMode = true;
      //  sEnableActivityLeaksDetect = true;
        if (sIsInStrictMode) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            if (sEnableActivityLeaksDetect) {
                builder.detectAll();
            } else {
                builder.detectActivityLeaks();
                builder.detectLeakedRegistrationObjects();
                builder.detectLeakedClosableObjects();
                builder.detectLeakedRegistrationObjects();
                builder.detectFileUriExposure();
                builder.detectLeakedSqlLiteObjects();
                builder.detectNonSdkApiUsage();
            }

            builder.penaltyLog().penaltyDropBox().penaltyDeath();
            StrictMode.setVmPolicy(builder.build());

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectNetwork()
                    .penaltyLog()
                    .penaltyDropBox()
                    .penaltyDialog()
                    .build());
        }
    }

    public static boolean isInStrictMode() {
        checkStrictModeCfg();
        return sIsInStrictMode;
    }

    /**
     * 有些情况下我们希望立刻发现错误，有任何异常都曝光出来，而不是带着错误继续执行。这样可以尽快发现错误。
     * 这种情况下将刚捕获的异常放到handleException就可以了。
     * 严格模式下，这些被处理的Exception 仍会立刻抛出。而非严格的模式下，会写日志。
     * @param e e
     * @param msg msg
     */
    public static void handleException(Exception e, String msg) {
        if (isInStrictMode()) {
            throw new RuntimeException(msg, e);
        } else if (DEBUG) {
            Log.e("Exception", msg, e);
        }
    }

    private static void checkStrictModeCfg() {
        if (!sIsFlgChecked) {
            File strictModeCfgFile = new File(Environment.getExternalStorageDirectory(), STRICT_MODE_CFG);
            sIsInStrictMode = strictModeCfgFile.exists();

            File activityLeaksCfgFile = new File(Environment.getExternalStorageDirectory(), ACTIVITY_LEAKS_DETECT_CFG);
            sEnableActivityLeaksDetect = activityLeaksCfgFile.exists();

            sIsFlgChecked = true;
        }
    }
}