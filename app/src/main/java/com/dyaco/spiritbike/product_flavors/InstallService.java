package com.dyaco.spiritbike.product_flavors;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class InstallService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d("InstallService", event.toString());
        checkInstall(event);
    }


    private void checkInstall(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();
        if (source != null) {
            boolean installPage = event.getPackageName().equals("com.android.packageinstaller");
            if (installPage) {
                installAPK(event);
            }
        }
    }

    private void installAPK(AccessibilityEvent event) {
        AccessibilityNodeInfo source = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nextInfos = source.findAccessibilityNodeInfosByText("INSTALL");
        nextClick(nextInfos);
//        List<AccessibilityNodeInfo> installInfos = source.findAccessibilityNodeInfosByText("INSTALL");
//        nextClick(installInfos);
//        List<AccessibilityNodeInfo> openInfos = source.findAccessibilityNodeInfosByText("OPEN");
//        nextClick(openInfos);

        runInBack(event);

    }

    private void runInBack(AccessibilityEvent event) {
        event.getSource().performAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    private void nextClick(List<AccessibilityNodeInfo> infos) {
        if (infos != null)
            for (AccessibilityNodeInfo info : infos) {
                if (info.isEnabled() && info.isClickable())
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
    }


    private boolean checkTitle(AccessibilityNodeInfo source) {
        List<AccessibilityNodeInfo> infos = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("@id/app_name");
        for (AccessibilityNodeInfo nodeInfo : infos) {
            if (nodeInfo.getClassName().equals("android.widget.TextView")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        Log.d("InstallService", "auto install apk");
    }
}