package com.dyaco.spiritbike.settings;
//
//import android.annotation.SuppressLint;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.wifi.ScanResult;
//import android.net.wifi.WifiConfiguration;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.ProgressBar;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//class WifiReceiver extends BroadcastReceiver {
//   WifiManager wifiManager;
//   public static StringBuilder sb;
//   RecyclerView wifiDeviceList;
//   WifiAdapter wifiAdapter;
//   ProgressBar progressBar;
//   public WifiReceiver(WifiManager wifiManager, RecyclerView wifiDeviceList, WifiAdapter wifiAdapter, ProgressBar progressBar) {
//      this.wifiManager = wifiManager;
//      this.wifiDeviceList = wifiDeviceList;
//      this.wifiAdapter = wifiAdapter;
//      this.progressBar = progressBar;
//   }
//   public void onReceive(Context context, Intent intent) {
//      String action = intent.getAction();
//      if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
//         sb = new StringBuilder();
//         List<ScanResult> wifiList = wifiManager.getScanResults();
//
//         List<WifiBean> deviceList = new ArrayList<>();
//         for (ScanResult scanResult : wifiList) {
//            WifiBean wifiBean = new WifiBean();
//            wifiBean.setWifiName(scanResult.SSID);
//            wifiBean.setCapabilities(scanResult.capabilities);
//            wifiBean.setNeedPassword(checkIsCurrentWifiHasPassword(scanResult.SSID));
//            deviceList.add(wifiBean);
//         }
//
//         wifiDeviceList.setLayoutManager(new LinearLayoutManager(context));
//         wifiDeviceList.setHasFixedSize(true);
//
//         wifiDeviceList.setAdapter(wifiAdapter);
//      //   wifiAdapter.setData2View(deviceList);
//
//         progressBar.setVisibility(View.INVISIBLE);
//      }
//   }
//
//   /**
//    * 利用WifiConfiguration.KeyMgmt的管理機制,來判斷當前wifi是否需要連線密碼
//    *
//    * @return true:需要密碼連線,false:不需要密碼連線
//    */
//   public boolean checkIsCurrentWifiHasPassword(String currentWifiSSID) {
//      try {
//
//         // 得到當前連線的wifi熱點的資訊
//         WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//
//         // 得到當前WifiConfiguration列表,此列表包含所有已經連過的wifi的熱點資訊,未連過的熱點不包含在此表中
//         @SuppressLint("MissingPermission")
//         List<WifiConfiguration> wifiConfiguration = wifiManager.getConfiguredNetworks();
//
//         String currentSSID = wifiInfo.getSSID();
//         if (currentSSID != null && currentSSID.length() > 2) {
//            if (currentSSID.startsWith("\"") && currentSSID.endsWith("\"")) {
//               currentSSID = currentSSID.substring(1, currentSSID.length() - 1);
//            }
//
//            if (wifiConfiguration != null && wifiConfiguration.size() > 0) {
//               for (WifiConfiguration configuration : wifiConfiguration) {
//                  if (configuration != null && configuration.status == WifiConfiguration.Status.CURRENT) {
//                     String ssid = null;
//                     if (!TextUtils.isEmpty(configuration.SSID)) {
//                        ssid = configuration.SSID;
//                        if (configuration.SSID.startsWith("\"") && configuration.SSID.endsWith("\"")) {
//                           ssid = configuration.SSID.substring(1, configuration.SSID.length() - 1);
//                        }
//                     }
//                     if (TextUtils.isEmpty(currentSSID) || currentSSID.equalsIgnoreCase(ssid)) {
//                        //KeyMgmt.NONE表示無需密碼
//                        return (!configuration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE));
//                     }
//                  }
//               }
//            }
//         }
//
//
//      } catch (Exception e) {
//         e.printStackTrace();
//      }
//      return true;
//   }
//}