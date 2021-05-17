package com.dyaco.spiritbike;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;

import com.corestar.app.BleDevice;
import com.corestar.app.BleEventManager;
import com.corestar.app.BluetoothLowEnergyService;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.RxBus;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.dyaco.spiritbike.MyApplication.ON_BLE_DEVICE_CONNECTED;
import static com.dyaco.spiritbike.MyApplication.ON_BLE_DEVICE_DISCONNECTED;
import static com.dyaco.spiritbike.MyApplication.ON_DISCOVER_DEVICE;
import static com.dyaco.spiritbike.MyApplication.ON_HEART_RATE_CHANGED;
import static com.dyaco.spiritbike.MyApplication.TIME_EVENT;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.MyApplication.isBlueToothOn;


public class HeartRateActivity extends BaseAppCompatActivity {
    public static final UUID UUID_SERVICE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");

    private ParcelUuid parcelUuid = ParcelUuid.fromString("0000180d-0000-1000-8000-00805f9b34fb");

    private static final int REQUEST_ENABLE_BT = 111;
    private SegmentedButtonGroup segmentedButtonGroup;

    private RecyclerView bleList;

    private TextView tvDeviceCount;

    private List<HeartRateBean> heartRateBeanList = new ArrayList<>();

    HeartRateAdapter heartRateAdapter;

    private static final String TAG = "HeartRate";

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private BluetoothAdapter adapter;
    private boolean isWorkout;
    private ImageView reScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_heart_rate);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        isWorkout = bundle.getBoolean("isWorkout");

        if (isWorkout) {
            setContentView(R.layout.activity_heart_rate_workout);
        } else {
            setContentView(R.layout.activity_heart_rate);
        }


        initView();

        //getPermission();

        initEvent();

    }

    @SuppressLint("MissingPermission")
    private void initView() {

        reScan = findViewById(R.id.reScan);
        reScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInstance().mBleEventManager.startScan(true);
            }
        });

        Button btClose = findViewById(R.id.btClose);
        btClose.setOnClickListener(v -> finish());

        tvDeviceCount = findViewById(R.id.tv_device_count);
        bleList = findViewById(R.id.ble_list);
        heartRateAdapter = new HeartRateAdapter(this, isWorkout);
        bleList.setLayoutManager(new LinearLayoutManager(this));
        bleList.setHasFixedSize(true);
        bleList.setAdapter(heartRateAdapter);

        heartRateAdapter.setOnItemClickListener(bleDevice -> {
            if (bleDevice.isConnect()) {
                getInstance().mBleEventManager.disconnectBleDevice(bleDevice);
            } else {
                getInstance().mBleEventManager.connectBleDevice(bleDevice);
            }
        });

        segmentedButtonGroup = findViewById(R.id.sc_unit);
        initBLE();
        segmentedButtonGroup.setOnPositionChangedListener(position -> {
            isBlueToothOn = position;
            switch (position) {
                case 0:

                    if (adapter.isEnabled()) {
                        if (getInstance().mBleEventManager.getConnectedBleDevices() != null)
                            heartRateAdapter.setData2View(getInstance().mBleEventManager.getConnectedBleDevices());

                        getInstance().mBleEventManager.startScan(true);
                        reScan.setEnabled(true);
                        updateConnectedDevicesCount();
                    } else {
                        //開啟藍芽
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }

                    break;
                case 1:

                 //   adapter.disable();

                    reScan.setEnabled(false);
                    if (getInstance().mBleEventManager.getConnectedBleDevices() != null &&
                            getInstance().mBleEventManager.getConnectedBleDevices().size() > 0) {

                        for (BleDevice bleDevice : getInstance().mBleEventManager.getConnectedBleDevices()) {
                            getInstance().mBleEventManager.disconnectBleDevice(bleDevice);
                        }
                    //    getInstance().mBleEventManager.disconnectBleDevice(getInstance().mBleEventManager.getConnectedBleDevices().get(0));
                    }

                    List<BleDevice> bleDevicesList = new ArrayList<>();
                    heartRateAdapter.setData2View(bleDevicesList);
                    String data = String.format("%s DEVICES FOUND", 0);
                    if (adapter.isEnabled())
                        getInstance().mBleEventManager.startScan(false);
                    display(data);
            }
        });

        if (adapter.isEnabled() && isBlueToothOn == 0) {
            List<BleDevice> totalList = getInstance().mBleEventManager.getPeripherals();
            heartRateAdapter.setData2View(totalList);
            Log.d("心跳設備", "一開始取得已連線及已掃瞄裝置: " + totalList.size());
        } else {
            isBlueToothOn = 1;
            segmentedButtonGroup.setPosition(isBlueToothOn,false);
        }
    }

    @SuppressLint("MissingPermission")
    private void initBLE() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        segmentedButtonGroup.setPosition(isBlueToothOn, false);
        if (adapter.isEnabled() && isBlueToothOn == 0) {
            Log.d("心跳設備", "開始掃描");
            getInstance().mBleEventManager.startScan(true);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // scanBleDevices();
            if (adapter.isEnabled()) {
                segmentedButtonGroup.setPosition(0, false);
                getInstance().mBleEventManager.startScan(true);
                reScan.setEnabled(true);

            } else {
                segmentedButtonGroup.setPosition(1, false);
            }
        }
    }

//    private void getPermission() {
//        d = new RxPermissions(this)
//                .requestEachCombined(
//                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
//                )
//                .subscribe(permission -> {
//                    if (permission.granted) {
//                        initBLE();
//                    }
//                });
//    }


    @Override
    protected void onResume() {
        super.onResume();

        // registerReceiver(mBleEventManager, BleEventManager.getBleEventFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeDisposable.dispose();

        if (getInstance().mBleEventManager != null) getInstance().mBleEventManager.startScan(false);

        //   unbindService(mBleEventManager);
    }

    private void initEvent() {

        Disposable disposable1 = RxBus.getInstance().toObservable(MsgEvent.class).subscribe((MsgEvent msg) -> {
            if (msg.getType() == ON_DISCOVER_DEVICE) {
                runOnUiThread(() -> {

                  //  onBleDeviceDisconnected, onDiscoverDevice 通知

                    List<BleDevice> bleDeviceList = getInstance().mBleEventManager.getPeripherals();

//                    //把已連線的加進去
//                    if (getInstance().mBleEventManager.getConnectedBleDevices() != null)
//                        bleDeviceList.addAll(getInstance().mBleEventManager.getConnectedBleDevices());

                    Log.d("心跳設備", "取得裝置:" + getInstance().mBleEventManager.getPeripherals());

                    heartRateAdapter.setData2View(bleDeviceList);
                    updateConnectedDevicesCount();
                });
                return;
            }

            if (msg.getType() == ON_BLE_DEVICE_CONNECTED) {
                runOnUiThread(() -> {

                    Log.d("心跳設備", "裝置連線通知: " + ((BleDevice) msg.getObj()).getDeviceName());
                    List<BleDevice> bleDeviceList = getInstance().mBleEventManager.getPeripherals();
                    heartRateAdapter.setData2View(bleDeviceList);
                  //  heartRateAdapter.setData2View((BleDevice) msg.getObj());
                    updateConnectedDevicesCount();
                });
                return;
            }

//            if (msg.getType() == ON_BLE_DEVICE_DISCONNECTED) {
//                runOnUiThread(() -> {
//                    Log.d("心跳設備", "斷線通知: " + ((BleDevice) msg.getObj()).getDeviceName());
//                    heartRateAdapter.setData2View((BleDevice) msg.getObj());
//                    updateConnectedDevicesCount();
//                });
//                return;
//            }
        });
        compositeDisposable.add(disposable1);
    }

    private void updateConnectedDevicesCount() {
        int count = getInstance().mBleEventManager.getPeripherals().size();
//        display.setText(String.format("Connected device count: %s", count));
        String data = String.format("%s DEVICES FOUND", count);
        display(data);
    }

    private void display(final String data) {
            runOnUiThread(() -> {
                if (isBlueToothOn == 0) {
                    tvDeviceCount.setText(data);
                } else {
                    tvDeviceCount.setText(String.format("%s DEVICES FOUND", 0));
                }
            });
    }

//    @Override
//    public void onDiscoverDevice() {
//        heartRateAdapter.setData2View(mBleEventManager.getScannedBleDevices());
//        updateConnectedDevicesCount();
//
//        Log.d(TAG, "onDiscoverDevice: " + mBleEventManager.getScannedBleDevices());
//        Log.i(TAG, "updateConnectedDevicesCount: " + mBleEventManager.getConnectedBleDevicesCount());
//    }
//
//    @Override
//    public void onBleDeviceConnected(BleDevice bleDevice) {
//        heartRateAdapter.setData2View(bleDevice);
//        updateConnectedDevicesCount();
//    }
//
//    @Override
//    public void onBleDeviceDisconnected(BleDevice bleDevice) {
//        heartRateAdapter.setData2View(bleDevice);
//        updateConnectedDevicesCount();
//    }
//
//    @Override
//    public void onBleAdvertisingStart(int i) {
//        Log.d(TAG, "onBleAdvertisingStart:" );
//    }
//
//    @Override
//    public void onBleAdvertisingStop() {
//        Log.d(TAG, "onBleAdvertisingStop:" );
//    }
//
//    @Override
//    public void onHeartRateChanged(BleDevice bleDevice, int hrv) {
//      //  Log.d(TAG, "on heart rate changed callback.");
//
//        String result = String.format(Locale.getDefault(),"Heart rate value: %d from %s", hrv, bleDevice.getDeviceAddress());
//        Log.d(TAG, "onHeartRateChanged:" + result);
//    }
//


//    private RxBleClient rxBleClient;
//    private Disposable scanDisposable;

//    private void scanBleDevices() {
//        scanDisposable = rxBleClient.scanBleDevices(
//                new ScanSettings.Builder()
//                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
//                        .build(),
//                new ScanFilter.Builder()
//                        .setServiceUuid(parcelUuid)
////                            .setDeviceAddress("B4:99:4C:34:DC:8B")
//                        // add custom filters if needed
//                        .build()
//        )
//                .observeOn(AndroidSchedulers.mainThread())
//                .doFinally(this::dispose)
//                .subscribe(scanResult -> {
//                    RxBleDevice c = scanResult.getBleDevice();
//                    ScanRecord r = scanResult.getScanRecord();
//                    heartRateBeanList.add(new HeartRateBean("", c.getName(), c.getMacAddress()));
//                    heartRateAdapter.setData2View(heartRateBeanList);
//                    scanDisposable.dispose();
//                    Log.i(TAG, "initBLE: " + c.getMacAddress() + "," + r.getServiceUuids());
//                });
//        //  .subscribe(resultsAdapter::addScanResult, this::onScanFailure);
//    }

//    private void dispose() {
//        scanDisposable = null;
//        //   heartRateAdapter.clearScanResults();
//        //  updateButtonUIState();
//    }
//    public void onReScan(View view) {
//
//        heartRateAdapter.clearScanResults();
//        scanBleDevices();
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (scanDisposable != null)
//            scanDisposable.dispose();
//    }
}