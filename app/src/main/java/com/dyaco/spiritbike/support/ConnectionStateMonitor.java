package com.dyaco.spiritbike.support;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.dyaco.spiritbike.webapi.BaseApi;
import com.dyaco.spiritbike.webapi.IServiceApi;

import static com.dyaco.spiritbike.MyApplication.WIFI_EVENT;

@SuppressLint("MissingPermission")
public class ConnectionStateMonitor extends ConnectivityManager.NetworkCallback {
    private static final String TAG = "ConnectionStateMonitor";
    final NetworkRequest networkRequest;
    private Context context;

    public ConnectionStateMonitor() {
        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
    }

    public void enable(Context context) {
        this.context = context;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerNetworkCallback(networkRequest, this);
    }

    // Likewise, you can have a disable method that simply calls ConnectivityManager.unregisterNetworkCallback(NetworkCallback) too.

    /**
     * 網絡可用的回調
     */
    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
      //  Log.i(TAG, "onAvailable: " + network);
    }

    /**
     * 在網絡失去連接的時候回調，但是如果是一個生硬的斷開，他可能不回調
     */
    @Override
    public void onLosing(Network network, int maxMsToLive) {
        super.onLosing(network, maxMsToLive);
     //   Log.i(TAG, "onLosing: " + network);
    }

    /**
     * 網絡丟失的回調
     */
    @Override
    public void onLost(Network network) {
        super.onLost(network);

        RxBus.getInstance().post(new MsgEvent(WIFI_EVENT, -1));
     //   Log.i(TAG, "onLost: " + network);
    }

    /**
     * 按照官方註釋的解釋，是指如果在超時時間內都沒有找到可用的網絡時進行回調
     */
    @Override
    public void onUnavailable() {
        super.onUnavailable();
    //    Log.i(TAG, "onUnavailable: ");
    }

    /**
     * 網絡的某個能力發生了變化回調
     * @param network network
     * @param networkCapabilities networkCapabilities
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        try {
          //  Log.d(TAG, "onCapabilitiesChanged: " + WifiManager.calculateSignalLevel(networkCapabilities.getSignalStrength(), 4));
            RxBus.getInstance().post(new MsgEvent(WIFI_EVENT, WifiManager.calculateSignalLevel(networkCapabilities.getSignalStrength(), 4)));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
//            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                Log.d(TAG, "onCapabilitiesChanged: 网络类型为wifi");
//                post(NetType.WIFI);
//            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                Log.d(TAG, "onCapabilitiesChanged: 蜂窝网络");
//                post(NetType.CMWAP);
//            } else {
//                Log.d(TAG, "onCapabilitiesChanged: 其他网络");
//                post(NetType.AUTO);
//            }
//        }
    }

    /**
     * 當建立網絡連接時，回調連接的屬性
     */
    @Override
    public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
        super.onLinkPropertiesChanged(network, linkProperties);

        if (CommonUtils.isFastClick()) return;
        checkTimeZone();
    }

    private void checkTimeZone() {
        BaseApi.request(BaseApi.createApi3(IServiceApi.class).apiGetTimeZone(),
                new BaseApi.IResponseListener<TimeZoneBean>() {
                    @Override
                    public void onSuccess(TimeZoneBean data) {

                        try {
                            Log.d("TIME_ZONE", "ConnectionStateMonitor: " + data.getGeoplugin_timezone());
                            setTimeZone(data.getGeoplugin_timezone());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail() {

                    }
                });
    }

    private void setTimeZone(String timeZone) {
        AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        am.setTimeZone(timeZone);
    }

    //        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//     //   am.setTimeZone("America/Los_Angeles");
//        am.setTimeZone("Asia/Taipei");
//
//        String[] idArray = TimeZone.getAvailableIDs();
//
//        Log.d("MMMMMMM", "當前時區: " + TimeZone.getDefault());
//        for (String zone : idArray) {
//            Log.d("MMMMMMM", "時區: " + zone);
//        }
}