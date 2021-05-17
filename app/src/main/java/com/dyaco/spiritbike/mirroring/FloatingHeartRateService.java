package com.dyaco.spiritbike.mirroring;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.corestar.app.BleDevice;
import com.corestar.app.BleEventManager;
import com.dyaco.spiritbike.HeartRateAdapter;
import com.dyaco.spiritbike.HeartRateBean;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.RxBus;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.support.ruler.RulerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.dyaco.spiritbike.MyApplication.ENABLE_BT;
import static com.dyaco.spiritbike.MyApplication.ON_BLE_DEVICE_CONNECTED;
import static com.dyaco.spiritbike.MyApplication.ON_BLE_DEVICE_DISCONNECTED;
import static com.dyaco.spiritbike.MyApplication.ON_DISCOVER_DEVICE;
import static com.dyaco.spiritbike.MyApplication.TIME_EVENT;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.MyApplication.isBlueToothOn;
import static com.dyaco.spiritbike.MyApplication.isLocked;
import static com.dyaco.spiritbike.support.CommonUtils.setTime;

public class FloatingHeartRateService extends Service {
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    public static boolean isStarted = false;
    private ConstraintLayout view;

    Handler mainHandler = new Handler(Looper.getMainLooper());

    private SegmentedButtonGroup segmentedButtonGroup;

    private RecyclerView bleList;

    private TextView tvDeviceCount;

    HeartRateAdapter heartRateAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BluetoothAdapter adapter;
    private ImageView reScan;
    private boolean isWorkout;
    private RotateAnimation am;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isWorkout = intent.getBooleanExtra("isWorkout", false);
        initEvent();
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("MissingPermission")
    private void showFloatingWindow() {

        if (isWorkout) {
            view = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.activity_heart_rate_workout, null);
        } else {
            view = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.activity_heart_rate, null);
        }

        windowManager.addView(view, layoutParams);

        tvDeviceCount = view.findViewById(R.id.tv_device_count);
        bleList = view.findViewById(R.id.ble_list);
        heartRateAdapter = new HeartRateAdapter(this, isWorkout);
        bleList.setLayoutManager(new LinearLayoutManager(this));
        bleList.setHasFixedSize(true);
        bleList.setAdapter(heartRateAdapter);

        reScan = view.findViewById(R.id.reScan);

        reScan.setOnClickListener(v -> startScan());

        am = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        am.setDuration(2000);
        am.setRepeatCount(Animation.INFINITE);
        LinearInterpolator lir = new LinearInterpolator();
        am.setInterpolator(lir);

        Button btClose = view.findViewById(R.id.btClose);
        btClose.setOnClickListener(v -> {
            stopSelf();
        });

        heartRateAdapter.setOnItemClickListener(bleDevice -> {

            if (bleDevice.isConnect()) {
                getInstance().mBleEventManager.disconnectBleDevice(bleDevice);
            } else {
                getInstance().mBleEventManager.connectBleDevice(bleDevice);
            }
        });

        segmentedButtonGroup = view.findViewById(R.id.sc_unit);
        initBLE();
        segmentedButtonGroup.setOnPositionChangedListener(position -> {
            isBlueToothOn = position;
            switch (position) {
                case 0:

                    if (adapter.isEnabled()) {
                        if (getInstance().mBleEventManager.getConnectedBleDevices() != null)
                            heartRateAdapter.setData2View(getInstance().mBleEventManager.getConnectedBleDevices());

                        try {
                            startScan();
                            updateConnectedDevicesCount();
                        } catch (Exception e) {
                            segmentedButtonGroup.setPosition(1,false);
                        }

                    } else {

                        if (adapter.enable()) {
                            if (getInstance().mBleEventManager.getConnectedBleDevices() != null)
                                heartRateAdapter.setData2View(getInstance().mBleEventManager.getConnectedBleDevices());

                            new RxTimer().timer(1000, number -> {
                                try {
                                    startScan();
                                    updateConnectedDevicesCount();
                                } catch (Exception e) {
                                    segmentedButtonGroup.setPosition(1,false);
                                }
                            });
                        } else {
                            segmentedButtonGroup.setPosition(1,false);
                        }
                      //  RxBus.getInstance().post(new MsgEvent(ENABLE_BT, true));
                    }

                    break;
                case 1:

                    if (getInstance().mBleEventManager.getConnectedBleDevices() != null &&
                            getInstance().mBleEventManager.getConnectedBleDevices().size() > 0) {
                        for (BleDevice bleDevice : getInstance().mBleEventManager.getConnectedBleDevices()) {
                            getInstance().mBleEventManager.disconnectBleDevice(bleDevice);
                        }
                    }

                    List<BleDevice> bleDevicesList = new ArrayList<>();
                    heartRateAdapter.setData2View(bleDevicesList);
                    String data = String.format("%s DEVICES FOUND", 0);

                    try {
                        getInstance().mBleEventManager.startScan(false);
                        if (am != null) am.cancel();
                        reScan.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
            startScan();
        }
    }

    private void initEvent() {
        Disposable disposable1 = RxBus.getInstance().toObservable(MsgEvent.class).subscribe((MsgEvent msg) -> {
            if (msg.getType() == ON_DISCOVER_DEVICE) {

                Runnable myRunnable = () -> {

                    List<BleDevice> bleDeviceList = getInstance().mBleEventManager.getPeripherals();
                    heartRateAdapter.setData2View(bleDeviceList);
                    updateConnectedDevicesCount();
                };
                mainHandler.post(myRunnable);

                return;
            }

            if (msg.getType() == ON_BLE_DEVICE_CONNECTED) {

                Runnable myRunnable = () -> {
                    List<BleDevice> bleDeviceList = getInstance().mBleEventManager.getPeripherals();
                    heartRateAdapter.setData2View(bleDeviceList);
                    updateConnectedDevicesCount();
                };
                mainHandler.post(myRunnable);
                return;
            }

//            if (msg.getType() == ON_BLE_DEVICE_DISCONNECTED) {
//                Runnable myRunnable = () -> {
//                    heartRateAdapter.setData2View((BleDevice) msg.getObj());
//                    updateConnectedDevicesCount();
//                };
//                mainHandler.post(myRunnable);
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

        Runnable myRunnable = () -> {
            if (isBlueToothOn == 0) {
                tvDeviceCount.setText(data);
            } else {
                tvDeviceCount.setText(String.format("%s DEVICES FOUND", 0));
            }
        };
        mainHandler.post(myRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeL();
    }

    private void removeL() {
        try {
            windowManager.removeView(view);
            isStarted = false;
            if (compositeDisposable != null) {
                compositeDisposable.dispose();
                compositeDisposable = null;
            }

            windowManager = null;
            view = null;

            if (getInstance().mBleEventManager != null) getInstance().mBleEventManager.startScan(false);
            if (am != null) am.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startScan() {
        Log.d("心跳設備", "開始掃描");
        try {
            reScan.setAnimation(am);
            am.startNow();
            getInstance().mBleEventManager.startScan(true);
            reScan.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
