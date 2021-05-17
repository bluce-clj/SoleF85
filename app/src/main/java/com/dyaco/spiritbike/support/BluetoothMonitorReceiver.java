package com.dyaco.spiritbike.support;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.hpplay.sdk.sink.logwriter.LogDispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.dyaco.spiritbike.MyApplication.BT_SOUND_CONNECT;
import static com.dyaco.spiritbike.MyApplication.isSoundConnected;

public class BluetoothMonitorReceiver extends BroadcastReceiver {
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                 //   if (!isAUDIO_VIDEO()) return;
                 //   RxBus.getInstance().post(new MsgEvent(BT_SOUND_CONNECT, isSoundConnected));
                    Log.d("BT_SOUND", "藍牙音訊設備已連接");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                 //   isSoundConnected = false;
                //    RxBus.getInstance().post(new MsgEvent(BT_SOUND_CONNECT, false));
                    Log.d("BT_SOUND", "藍牙音訊設備已斷開");
                    break;
            }

        }
    }


    private boolean isAUDIO_VIDEO() {
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        for(BluetoothDevice bt : pairedDevices) {
            if (bt.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.AUDIO_VIDEO) {
                Log.d("BT_SOUND", "cc: " + bt.getAddress() +","+bt.getName());
                isSoundConnected = true;
            }
        }

//        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
//       // List<BluetoothDevice> devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
//        List<BluetoothDevice> devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
//        Log.d("BT_SOUND", "isAUDIO_VIDEO: " + devices.size());
//        for(BluetoothDevice device : devices) {
//            Log.d("BT_SOUND", "XXX: " + device.getType() +","+ device.getName() +","+ device.getAddress() +","+device.getBluetoothClass().getMajorDeviceClass());
////            if(device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
////                Log.d("BT_SOUND", "cc: " + bt.getAddress() +","+bt.getName());
////            }
//        }

        return isSoundConnected;

    }

    private boolean getBtClass() {
        boolean connectionExists = false;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            int[] profiles = {BluetoothProfile.A2DP};
            for (int profileId : profiles) {
                if (adapter.getProfileConnectionState(profileId) == BluetoothProfile.STATE_CONNECTED) {
                   // Log.d("BT_SOUND@@@", "profileId: " + profileId + ","+ adapter.getAddress());
                    connectionExists = true;
                    break;
                }
            }
            Log.d("BT_SOUND", "connectionExists: " + connectionExists);
        }
        return connectionExists;
    }
}