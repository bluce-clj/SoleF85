package com.dyaco.spiritbike.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import static com.dyaco.spiritbike.MyApplication.TIME_EVENT;
import static com.dyaco.spiritbike.MyApplication.WIFI_EVENT;
import static com.dyaco.spiritbike.support.CommonUtils.isConnected;
import static com.dyaco.spiritbike.support.CommonUtils.setTime;

public class WifiChangeBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        getWifiInfo(context);
     //   new RxTimer().timer(5000, number -> getWifiInfo(context));
    }

    private void getWifiInfo(Context context) {

        int signalLevel;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getBSSID() != null) {
            //wifi名稱
         //   String ssid = wifiInfo.getSSID();
            //wifi信號強度
            signalLevel = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4);
            //wifi速度 
          //  int speed = wifiInfo.getLinkSpeed();
            //wifi速度單位
         //   String units = WifiInfo.LINK_SPEED_UNITS;
        } else {
            signalLevel = -1;
        }
      //  if (!isConnected(context)) signalLevel = -1;

        Log.d("getWifiInfo", "LEVEL: " + signalLevel);

        RxBus.getInstance().post(new MsgEvent(WIFI_EVENT, signalLevel));


    }

}
