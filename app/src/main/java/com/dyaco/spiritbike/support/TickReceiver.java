package com.dyaco.spiritbike.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.dyaco.spiritbike.support.CommonUtils.setTime;
import static com.dyaco.spiritbike.MyApplication.TIME_EVENT;

public class TickReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        RxBus.getInstance().post(new MsgEvent(TIME_EVENT, setTime()));
    }
}