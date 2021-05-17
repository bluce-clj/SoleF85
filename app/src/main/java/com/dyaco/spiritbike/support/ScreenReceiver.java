package com.dyaco.spiritbike.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.corestar.libs.device.Device;

import static com.dyaco.spiritbike.MyApplication.GO_SLEEP;
import static com.dyaco.spiritbike.MyApplication.IS_CHILD_LOCKING;
import static com.dyaco.spiritbike.MyApplication.WIFI_EVENT;
import static com.dyaco.spiritbike.MyApplication.btnFnaI;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.MyApplication.isLocked;
import static com.dyaco.spiritbike.MyApplication.wasScreenOn;

public class ScreenReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // do whatever you need to do here
            Log.d("休眠", "ACTION_SCREEN_OFF: ");

            IS_CHILD_LOCKING = false;
            isLocked = true;
            wasScreenOn = false;

            btnFnaI = 0;
            getInstance().mDevice.setFan(Device.FAN.STOP);
            getInstance().commandSetEup(1);

            RxBus.getInstance().post(new MsgEvent(GO_SLEEP, true));

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // and do whatever you need to do here
            wasScreenOn = true;
            Log.d("休眠", "ACTION_SCREEN_ON: ");
            getInstance().commandDeviceInfo();
        }
    }

}