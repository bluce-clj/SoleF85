package com.dyaco.spiritbike.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastReceiverHide extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      //  Log.d("@@@@@@@","intent = " + intent.getStringExtra("name"));
        Log.d("@@@@@@@","intent = @@@@@@@@@@@");
    }
}
