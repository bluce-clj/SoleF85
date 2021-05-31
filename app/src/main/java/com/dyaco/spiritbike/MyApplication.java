package com.dyaco.spiritbike;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.corestar.app.BleDevice;
import com.corestar.libs.audio.AudioDeviceWatcher;
import com.corestar.libs.device.Device;
import com.corestar.libs.device.DeviceInformationProfile;
import com.corestar.libs.ftms.FitnessMachine;
import com.corestar.libs.ftms.FitnessMachineControlResponse;
import com.corestar.libs.ftms.FitnessMachineManager;
import com.corestar.libs.ftms.MachineFeature;
import com.corestar.libs.ftms.SpinDownControl;
import com.corestar.libs.ftms.StopOrPause;
import com.corestar.libs.ftms.TrainingStatus;
import com.corestar.libs.hrms.HeartRateDeviceManager;
import com.dyaco.spiritbike.engineer.DeviceSettingBean;
import com.dyaco.spiritbike.engineer.RxSettingBean;
import com.dyaco.spiritbike.product_flavors.ModeEnum;

import com.dyaco.spiritbike.product_flavors.InitProduct;
import com.dyaco.spiritbike.settings.appupdate.AppUpdateData;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.ConnectionStateMonitor;
import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.RxBus;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.support.ScreenReceiver;
import com.dyaco.spiritbike.support.StrictModeManager;
import com.dyaco.spiritbike.support.TickReceiver;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.uart.DeviceInfoBean;
import com.dyaco.spiritbike.uart.McuBean;
import com.dyaco.spiritbike.workout.BleHrBean;
import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


import static com.corestar.libs.ftms.FitnessMachine.CROSS_TRAINER;
import static com.corestar.libs.ftms.FitnessMachine.INDOOR_BIKE;
import static com.dyaco.spiritbike.product_flavors.ModeEnum.XE395ENT;
import static com.dyaco.spiritbike.product_flavors.ModeEnum.getMode;


public class MyApplication extends Application {
    public static boolean SSEB = true;//控制在onPause()時，是否要顯示懸浮按鈕
    public static boolean wasScreenOn = true;
    public static int SW_VERSION;
    public static int NEW_SW_VERSION;

    public static long SLEEP_TIME = 60 * 30 * 1000;

    @SuppressLint("StaticFieldLeak")
    private static MyApplication instance;
    public static int btnFnaI = 0; // 0關
    public static int UNIT_E = 0;
    public static int isBlueToothOn = 0;
    public static ModeEnum MODE;

    //第二次回到StartScreenFragment不要鎖螢幕  第二次為 false
    public static boolean isLocked = true;

    public static boolean isSoundConnected = false;
    public static boolean isFtmsConnected = false;

    public static boolean isFTMSNotify;
    public static boolean IS_CHILD_LOCKING = false;
    public static boolean updateNotify = false;
    public static boolean isAutoTest = false;
    public static boolean isUartConPortOpen = false;
    public static boolean isBootUp = false; //開機檢查過

    public static boolean isLevelErrorShow = false;
    public static boolean isInclineErrorShow = false;

    private final McuBean mcuBean = new McuBean();
    private final FTMSBean ftmsBean = new FTMSBean();
    private static OkHttpClient mOkHttpClient;

    private final KeyBean keyBean = new KeyBean();
    public Device mDevice;
    public FitnessMachineManager mFTMSManager;
    public HeartRateDeviceManager mBleEventManager;


    public static int SHOW_EXIT_BUTTON = 432132;

    public static int GO_SLEEP = 555555;
    //UART
    public static final int COMMAND_SET_LWR_MODE = 0x90;
    public static final int COMMAND_DEVICE_INFO = 0x99;
    public static final int COMMAND_SET_SPEED_CONFIG = 0x9A;
    public static final int COMMAND_READ_INCLINE = 0x95;
    public static final int COMMAND_SET_CONTROL = 0x80;
    public static final int COMMAND_SET_EUP = 0x70;
    public static final int COMMAND_FAN = 0x71;
    public static final int COMMAND_SET_BUZZER = 0x73;
    public static final int COMMAND_SET_DEVICE_RESET = 0x90;

    //UART RESPONSE
    public static final int ON_INCLINE_READ = 1401;
    public static final int ON_INCLINE_CALI = 1402;
    public static final int ON_INCLINE_CALI_FAIL = 1422;
    public static final int ON_INCLINE_ADJUST = 1403;
    public static final int ON_LEVEL_ADJUST = 1404;
    public static final int ON_AUTO_TEST = 1405;
    public static final int ON_DEVICE_INFO = 1406;
    public static final int ON_ERROR = 1409;
    public static final int ON_ERROR2 = 14092;
    public static final int ON_LWR_MODE = 1407;

    public static final int ON_USB_MODE_SET = 1900;

    public static int STOP_FLOATING_DASHBOARD = 22223;

    //UART KEY
    public static final int COMMAND_KEY = 3000;
    public static final int COMMAND_KEY_01 = 3001;
    public static final int COMMAND_KEY_02 = 3002;
    public static final int COMMAND_KEY_03 = 3003;
    public static final int COMMAND_KEY_04 = 3004;

    public static final int COMMAND_MULTI_KEY_01 = 3041;
    public static final int COMMAND_MULTI_KEY_02 = 3042;

    public static final int TIME_EVENT = 600;
    public static final int WIFI_EVENT = 610;
    public static final int WORKOUT_TIMER_EVENT = 601;

    public static final int REMOVE_BUTTON = 434343;

    //FTMS
    public static final int FTMS_NOTIFY = 4000;
    public static final int FTMS_NOTIFY_START_OR_RESUME = 4001;
    public static final int FTMS_NOTIFY_STOP_OR_PAUSE = 4002;
    public static final int FTMS_NOTIFY_SET_LEVEL = 4003;
    public static final int FTMS_NOTIFY_SET_INCLINE = 4004;
    public static final int FTMS_NOTIFY_CONNECTED = 4010;
    public static final int FTMS_NOTIFY_TRAINING_STATUS = 4055;
    public static final int FTMS_NOTIFY_MACHINE_STATUS = 4056;
    public static final int FTMS_NOTIFY_DISCONNECTED = 4012;

    public static final int BT_SOUND_CONNECT = 4062;
    public static final int FAN_NOTIFY = 99991;


    //HeartRate Bluetooth Event
    public static final int ON_DISCOVER_DEVICE = 1001;
    public static final int ON_BLE_DEVICE_CONNECTED = 1002;
    public static final int ON_BLE_DEVICE_DISCONNECTED = 1003;
    public static final int ON_HEART_RATE_CHANGED = 1010;
    public static final int ENABLE_BT = 1018;

    public static final int METRIC_MAX_WEIGHT = 180;
    public static final int METRIC_MIN_WEIGHT = 30;

    public static final int METRIC_MAX_HEIGHT = 220;
    public static final int METRIC_MIN_HEIGHT = 100;

    public static final int IMPERIAL_MAX_WEIGHT = 400;
    public static final int IMPERIAL_MIN_WEIGHT = 40;

    public static final int IMPERIAL_MAX_HEIGHT = 100; //inch
    public static final int IMPERIAL_MIN_HEIGHT = 30;

    public static final String QR_CODE_FILE_NAME = "DeviceCode.png";
    public static final String DEFAULT_SEEK_VALUE = "0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0#0";
    public static final String DEFAULT_SEEK_VALUE_LEVEL = "1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1#1";

    public static final int MIRRORING_GO_HOME_SCREEN = 9901;
    public static final int MIRRORING_GO_PROGRAMS = 9902;
    public static final int MIRRORING_GO_PROFILE = 9903;
    public static final int MIRRORING_GO_WEB_VIEW = 9904;
    public static final int MIRRORING_GO_CAST = 9905;
    public static final int VIDEO_BACK_HOME = 9944;

    public static final int MIRRORING_GO_SETTING = 9906;
    public static final int MIRRORING_EXIT_FULL_SCREEN = 9911;
    public static final int MIRRORING_SIGN_OUT = 9912;
    public static final int MIRRORING_SHOW_DASH_BOARD = 9913;
    public static final int MIRRORING_STOP = 9920;
    public static final int MIRRORING_STOP_WORKOUT = 9921;

    public static final int MIRRORING_DASHBOARD_DATA = 9924;

    public static final int MIRRORING_BTN_LEVEL_MINUS = 9931;
    public static final int MIRRORING_BTN_LEVEL_PLUS = 9932;
    public static final int MIRRORING_BTN_INCLINE_MINUS = 9933;
    public static final int MIRRORING_BTN_INCLINE_PLUS = 9934;

    public static final int MIRRORING_BTN_AUTO_UP = 99301;
    public static final int MIRRORING_BTN_AUTO_DOWN = 99302;

    public static final int MIRRORING_SET_CURRENT_LEVEL = 9935;
    public static final int MIRRORING_SET_CURRENT_INCLINE = 9936;
    public static final int MIRRORING_SET_MAX_LEVEL = 9937;
    public static final int MIRRORING_SET_MAX_INCLINE = 9938;

    public static final int MIRRORING_SWITCH_DASHBOARD_1 = 9951;
    public static final int MIRRORING_SWITCH_DASHBOARD_2 = 9952;
    public static final int MIRRORING_SWITCH_DASHBOARD_3 = 9953;
    public static final int MIRRORING_SWITCH_DASHBOARD_4 = 9954;
    public static final int MIRRORING_SWITCH_DASHBOARD_5 = 9955;

    DeviceSettingBean deviceSettingBean;
    AppUpdateData appUpdateData;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        initData();

//        Intent intentService = new Intent(this, LauncherService.class);
//        Integer integerTimeSet =  123 ; //使用者輸入的剩餘時間參數
//        intentService.putExtra("TimeValue",integerTimeSet);
//        startForegroundService(intentService);


        //crash 重啟
    //    Thread.setDefaultUncaughtExceptionHandler(restartHandler);


        ConnectionStateMonitor networkCallback = new ConnectionStateMonitor();
        networkCallback.enable(getApplicationContext());

      //  StrictModeManager.init();

      //  RxDisposableWatcher.INSTANCE.init();

       // List<ProbeEntry> result = RxDisposableWatcher.INSTANCE.probe(); // Collect info: stacktrace, number of calls, type
      //  val report = HtmlReportBuilder(result).build() // Generate HTML report


        // Normal app init code...
    }

    private void initData() {

        MMKV.initialize(instance);

        initSetting();


        mDevice = new Device(instance);

        //時鐘 Broadcast
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(new TickReceiver(), filter);

        //WIFI Broadcast
//        IntentFilter intentFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        registerReceiver(new WifiChangeBroadcastReceiver(), intentFilter);

        final IntentFilter filter2 = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter2.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new ScreenReceiver(), filter2);

//        final IntentFilter filter3 = new IntentFilter(Intent.ACTION_MY_PACKAGE_REPLACED);
//        registerReceiver(new MyReceiver(), filter3);

        //BlueTooth Broadcast
        // 監聽藍芽設備與APP連接的狀態
//        IntentFilter btIntentFilter = new IntentFilter();
//        btIntentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//        btIntentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
//        registerReceiver(new BluetoothMonitorReceiver(), btIntentFilter);

        //接收隱藏下拉的廣播
//        BroadcastReceiverHide mCloseBroadcastMyReceiver = new BroadcastReceiverHide();
//        IntentFilter closeBroadcastFilter = new IntentFilter("com.roco.hide.bar");
//        registerReceiver(mCloseBroadcastMyReceiver, closeBroadcastFilter);

        //發送隱藏下拉的廣播
        Intent intent = new Intent("com.roco.hide.bar"); //com.roco.show.bar
        sendBroadcast(intent);


       // IntentFilter filter = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);


        AudioDeviceWatcher mAudioDeviceWatcher = new AudioDeviceWatcher(this);
        mAudioDeviceWatcher.setDeviceStateListener(new AudioDeviceWatcher.DeviceStateListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onDeviceConnectionStateChanged(BluetoothDevice device, AudioDeviceWatcher.CONNECTION_STATE state) {
                Log.d("BT_SOUND", "on device connection state changed. Device: " + device.getName() + ", address: " + device.getAddress() + ", connection state: " + state);
                switch (state) {
                    case CONNECTED:
                        isSoundConnected = true;
                        RxBus.getInstance().post(new MsgEvent(BT_SOUND_CONNECT, isSoundConnected));
                        Log.d("BT_SOUND", "藍牙音訊設備已連接");
                        break;
                    case DISCONNECTED:
                        isSoundConnected = false;
                        RxBus.getInstance().post(new MsgEvent(BT_SOUND_CONNECT, false));
                        Log.d("BT_SOUND", "藍牙音訊設備已斷開");
                        break;
                }
            }
        });

        initUartConnect();

        initBle();

        initFTMS();

        initCheckActive();

        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
    }


    private void initFTMS() {

        mFTMSManager = new FitnessMachineManager(instance);

        /**
         * FTMS manager的基本回應介面，使用manager之前先初始化，未來可能會新增其他事件如藍牙狀態。
         */
        // 當客戶端開啟或關閉監聽機器資訊時通知，可以在開啟通知時再更新機器資訊
        // 當客戶端連線時通知
        // 當客戶端離線時通知
        // 當系統藍牙狀態改變時通知
        // 當客戶端開啟或關閉監聽目標狀態時通知，如果是開啟狀態，需要在目標設定完成後，主動通知客戶端。
        // 也就是下面的FitnessMachineControlListener通知時，設定完成後再使用函式通知客戶端。
        // 儲存目前是否有客戶端是否正在監聽設定目標狀態的旗標
        FitnessMachineManager.FitnessMachineEventListener ftmsEventListener = new FitnessMachineManager.FitnessMachineEventListener() {

            /**
             * 11 = BOND_BONDING 開
             * 12 = BOND_BONDED 關
             * 10 = BOND_NONE 關
             * -2147483648 = BluetoothAdapter.ERROR
             */
            @Override
            public void onBluetoothBondStateChanged(int i) {
                switch (i) {
                    case 11:
                     //   RxBus.getInstance().post(new MsgEvent(REMOVE_BUTTON, true));
                      //  RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP, true));
                        SSEB = false;
                        break;
                    case 12:
                    case 10:
                     //   RxBus.getInstance().post(new MsgEvent(REMOVE_BUTTON, true));
                     //   RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP, true));
                        SSEB = true;
                }

                Log.d("FTMSSS", "onBluetoothBondStateChanged: "+ i);
            }

            // 當客戶端開啟或關閉監聽機器資訊時通知，可以在開啟通知時再更新機器資訊
            @Override
            public void onFitnessMachineNotify(BluetoothDevice bluetoothDevice, FitnessMachine fitnessMachine, boolean b) {
                Log.d("FTMSSS", "onFitnessMachineNotify: " + bluetoothDevice.getAddress() + "," + fitnessMachine.getSystemId() + "," + b);
            }

            // 當客戶端連線時通知
            @Override
            public void onClientConnected(BluetoothDevice bluetoothDevice) {
                Log.d("FTMSSS", "onClientConnected: " + bluetoothDevice.getAddress());
                isFtmsConnected = true;
                RxBus.getInstance().post(new MsgEvent(FTMS_NOTIFY_CONNECTED, true));

            }

            // 當客戶端離線時通知
            @Override
            public void onClientDisconnected(BluetoothDevice bluetoothDevice) {
                Log.d("FTMSSS", "onClientDisconnected: " + bluetoothDevice.getAddress());
                isFtmsConnected = false;
                RxBus.getInstance().post(new MsgEvent(FTMS_NOTIFY_CONNECTED, false));
            }

            // 當系統藍牙狀態改變時通知
            @Override
            public void onBluetoothStateChanged(FitnessMachineManager.BLUETOOTH_STATE bluetooth_state) {
                Log.d("FTMSSS", "onBluetoothStateChanged: " + bluetooth_state);
                if (FitnessMachineManager.BLUETOOTH_STATE.ON == bluetooth_state) {
                    initFTMS();
                } else if (FitnessMachineManager.BLUETOOTH_STATE.OFF == bluetooth_state) {
                    mFTMSManager = null;
                }
            }

            @Override
            public void onStartAdvertising() {
                Log.d("FTMSSS", "on start ftms.");
            }

            @Override
            public void onStopAdvertising() {
                Log.d("FTMSSS", "on stop ftms.");
            }

            @Override
            public void onTrainingStatusNotify(BluetoothDevice bluetoothDevice, boolean b) {

                if (b) {
                    if (checkWorkoutRunning()) {
                        RxBus.getInstance().post(new MsgEvent(FTMS_NOTIFY_TRAINING_STATUS, true));
                    } else {
                        try {
                            mFTMSManager.notifyTrainingStatus(TrainingStatus.IDLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.d("FTMSSS", "onTrainingStatusNotify: " + b);
            }

            @Override
            public void onFitnessMachineStatusNotify(BluetoothDevice bluetoothDevice, boolean b) {
                // 當客戶端開啟或關閉監聽目標狀態時通知，如果是開啟狀態，需要在目標設定完成後，主動通知客戶端。
                // 也就是下面的FitnessMachineControlListener通知時，設定完成後再使用函式通知客戶端。

                isFTMSNotify = b; // 儲存目前是否有客戶端是否正在監聽設定目標狀態的旗標
                Log.d("FTMSSS", "當客戶端開啟或關閉監聽目標狀態時通知:" + b);

                if (b) {
                    try {
                        if (checkPauseRunning()) {
                            mFTMSManager.notifyStoppedOrPaused(StopOrPause.STOP);
                        } else if (checkWorkoutRunning() && !checkPauseRunning()) {
                            mFTMSManager.notifyStartOrResume();
                        } else {
                            mFTMSManager.notifyStoppedOrPaused(StopOrPause.STOP);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        /**
         * Fitness Machine Control Point, client端傳入控制指令的回應介面
         * 回應型別：FitnessMachineControlResponse
         */
        FitnessMachineManager.FitnessMachineControlListener ftmsControlListener = new FitnessMachineManager.FitnessMachineControlListener() {
            @Override
            public FitnessMachineControlResponse onRequestControl() {
                // client端要求進行控制時通知。
                Log.d("FTMSSS", "client端要求進行控制時通知");
                return FitnessMachineControlResponse.SUCCESS;
            }

            @Override
            public FitnessMachineControlResponse onReset() {
                // 此機型暫無設定
                return null;
            }

            @Override
            public FitnessMachineControlResponse onSetTargetSpeed(int i) {
                // 此機型暫無設定
                return null;
            }

            @Override
            public FitnessMachineControlResponse onSetTargetInclination(int i) {
                // client 端要求設定目標INCLINE揚升值時通知。
                Log.d("FTMSSS", "client 端要求設定目標INCLINE揚升值時通知。INCLINE:" + i);

                if (MODE != XE395ENT) return FitnessMachineControlResponse.OP_CODE_NOT_SUPPORTED;

                if (checkWorkoutRunning() && !checkPauseRunning()) {
                    if (isFTMSNotify) { //目前是否有客戶端是否正在監聽設定目標狀態的旗標
                        mFTMSManager.notifyTargetInclineChanged(i); // 通知方法
                    }

                    ftmsBean.setEventType(FTMS_NOTIFY);
                    ftmsBean.setFtmsNotifyType(FTMS_NOTIFY_SET_INCLINE);
                    ftmsBean.setSetIncline(i);
                    EventBus.getDefault().post(ftmsBean);
                    Log.d("FTMSSS", "成功送出：client 端要求設定目標INCLINE揚升值時通知。INCLINE:" + i);
                    //   new WorkoutDashboardActivity().getLifecycle().getCurrentState().isAtLeast(STARTED);
                    return null;
                } else {
                    return FitnessMachineControlResponse.INVALID_PARAMETER;
                }
            }

            /**
             * 0x04[D2] d2 = 20 = 2
             * @param i
             * @return
             */
            @Override
            public FitnessMachineControlResponse onSetTargetResistanceLevel(int i) {
                // 客戶端要求設定目標LEVEL阻力值時通知。
                // 需設定，當目標阻力已設定後，如果客戶端有監聽目標狀態的話，通知客戶端目標阻力已改變。

                Log.d("FTMSSS", "客戶端要求設定目標LEVEL阻力值時通知。LEVEL:" + i);

                if (checkWorkoutRunning() && !checkPauseRunning()) {

                    if (isFTMSNotify) { //目前是否有客戶端是否正在監聽設定目標狀態的旗標
                        mFTMSManager.notifyTargetResistanceLevelChanged(i); // 通知方法
                    }
                    ftmsBean.setEventType(FTMS_NOTIFY);
                    ftmsBean.setFtmsNotifyType(FTMS_NOTIFY_SET_LEVEL);
                    ftmsBean.setSetLevel(i);
                    EventBus.getDefault().post(ftmsBean);
                    Log.d("FTMSSS", "成功送出：客戶端要求設定目標LEVEL阻力值時通知。LEVEL:" + i);

                    //    FitnessMachineControlResponse.INVALID_PARAMETER
                    return null;
                } else {
                    return FitnessMachineControlResponse.INVALID_PARAMETER;
                }
            }

            @Override
            public FitnessMachineControlResponse onSetTargetPower(int i) {
                // 此機型暫無設定
                return null;
            }

            @Override
            public FitnessMachineControlResponse onSetTargetHeartRate(int i) {
                // 此機型暫無設定
                return null;
            }

            @Override
            public FitnessMachineControlResponse onStartOrResume() {
                // 當客戶端要求開始或繼續Workout時通知。
                // 需設定，當Workout成功啟動或繼續時，如果客戶端有監聽目標狀態的話，通知客戶端機器已啟動。

                Log.d("FTMSSS", "當客戶端要求開始或繼續Workout時通知。");

                if(checkUpdateIng()) return FitnessMachineControlResponse.INVALID_PARAMETER;

                int status = 0;
                // Workout正在跑
                if (checkWorkoutRunning() && !checkPauseRunning())
                    return FitnessMachineControlResponse.INVALID_PARAMETER;

                // 2 RESUME
                if (checkPauseRunning()) status = 2;

                // 1 START
                if (checkDashboardRunning()) status = 1;

                if (status == 0) return null;

                //  if (isFTMSNotify) mFTMSManager.notifyStartOrResume(); // 通知方法

                Log.d("FTMSSS", "當客戶端要求開始或繼續Workout時通知。" + status);
                ftmsBean.setEventType(FTMS_NOTIFY);
                ftmsBean.setFtmsNotifyType(FTMS_NOTIFY_START_OR_RESUME);
                ftmsBean.setStartOrResume(status);

                EventBus.getDefault().post(ftmsBean);

                return FitnessMachineControlResponse.SUCCESS;
            }

            @Override
            public FitnessMachineControlResponse onStopOrPause(StopOrPause stopOrPause) {
                // 當客戶端要求停止或暫停Workout時通知。
                // 需設定，當Workout成功停止或暫停時，如果客戶端有監聽目標狀態的話，通知客戶端機器已停止或暫停。

//                if (stopOrPause == StopOrPause.PAUSE)
//                    return FitnessMachineControlResponse.INVALID_PARAMETER;


//                //Workout已在Pause中
//                if (stopOrPause == StopOrPause.PAUSE) {
//                    if (checkPauseRunning()) return FitnessMachineControlResponse.INVALID_PARAMETER;
//                }
//
//                if (stopOrPause == StopOrPause.STOP) {
//
//                }

                Log.d("FTMSSS", "當客戶端要求停止或暫停Workout時通知。stopOrPause:" + stopOrPause);

//                if (isFTMSNotify) {
//                    mFTMSManager.notifyStoppedOrPaused(stopOrPause); // 通知方法
//                }

                // Workout沒在跑
                if (checkDashboardRunning()) return FitnessMachineControlResponse.INVALID_PARAMETER;

                ftmsBean.setEventType(FTMS_NOTIFY);
                ftmsBean.setFtmsNotifyType(FTMS_NOTIFY_STOP_OR_PAUSE);
                ftmsBean.setStopOrPause(stopOrPause.getCode());
                EventBus.getDefault().post(ftmsBean);
                return FitnessMachineControlResponse.SUCCESS;
            }

            @Override
            public FitnessMachineControlResponse onSetTargetedExpendedEnergy(int i) {
                // 此機型暫無設定
                return null;
            }

            @Override
            public FitnessMachineControlResponse onSetTargetedNumberOfSteps(int i) {
                // 此機型暫無設定
                return null;
            }

            @Override
            public FitnessMachineControlResponse onSetTargetedNumberOfStrides(int i) {
                // 此機型暫無設定
                return null;
            }

            @Override
            public FitnessMachineControlResponse onSetTargetedDistance(int i) {
                // 此機型暫無設定
                return null;
            }

            @Override
            public FitnessMachineControlResponse onSetTargetedTrainingTime(int i) {
                // 此機型暫無設定
                return null;
            }

            @Override
            public FitnessMachineControlResponse onSetTargetedTimeInTwoHeartRateZones(int i, int i1) {
                // 此機型暫無設定
                return null;
            }

            @Override
            public FitnessMachineControlResponse onSetTargetedTimeInThreeHeartRateZones(int i, int i1, int i2) {
                // 此機型暫無設定
                return null;
            }

            @Override
            public FitnessMachineControlResponse onSetTargetedTimeInFiveHeartRateZones(int i, int i1, int i2, int i3, int i4) {
                // 此機型暫無設定
                return null;
            }

            @Override
            public FitnessMachineControlResponse onSetIndoorBikeSimulationParameters(int i, int i1, int i2, int i3) {
             //   int target = i1 <= 0 ? 1 : (i1 / 100); // for cross trainer and bike
                int target = i1 <= 0 ? 1 : (i1 / 100) * 10; // for cross trainer and bike

                Log.d(TAG, "on set indoor bike simulation parameters: wind speed: " + i + ", grade: " + i1 + ", crr: " + i2 + ", cw: " + i3 + ", target: " + target);

                // response
              //  mFTMSManager.responseFitnessMachineControl(FitnessMachineControlOperation.SET_INDOOR_BIKE_SIMULATION_PARAMETERS, FitnessMachineControlResponse.SUCCESS);

                if (checkWorkoutRunning() && !checkPauseRunning()) {
                    if (isFTMSNotify) { //目前是否有客戶端是否正在監聽設定目標狀態的旗標
                        mFTMSManager.notifyTargetResistanceLevelChanged(target); // 通知方法
                    }
                    ftmsBean.setEventType(FTMS_NOTIFY);
                    ftmsBean.setFtmsNotifyType(FTMS_NOTIFY_SET_LEVEL);
                    ftmsBean.setSetLevel(target);
                    EventBus.getDefault().post(ftmsBean);
                    Log.d("FTMSSS", "成功送出：客戶端要求設定目標LEVEL阻力值時通知。LEVEL:" + target +","+i1);
                    return null;
                } else {
                    return FitnessMachineControlResponse.INVALID_PARAMETER;
                }
            }

            @Override
            public FitnessMachineControlResponse onSetWheelCircumference(int i) {
                // 此機型暫無設定
                return null;
            }

            @Override
            public FitnessMachineControlResponse onSpinDownControl(SpinDownControl spinDownControl) {
                // 此機型暫無設定
                return null;
            }

            @Override
            public int getTargetSpeedLow() {
                // 此機型暫無設定
                return 0;
            }

            @Override
            public int getTargetSpeedHigh() {
                // 此機型暫無設定
                return 0;
            }

            @Override
            public FitnessMachineControlResponse onSetTargetedCadence(int i) {
                // 此機型暫無設定
                return null;
            }
        };

        /**
         * Device information service 設定介面，需要在啟動ftms前設定。
         */
        // 設定機型
        DeviceInformationProfile.DataProvider dataProvider = new DeviceInformationProfile.DataProvider() {
            @Override
            public String getManufacturer() {
                return null;
            }

            @Override
            public String getModelNumber() {
                return null;
            }

            @Override
            public String getSerialNumber() {
                return null;
            }

            @Override
            public String getHardwareRevision() {
                return null;
            }

            @Override
            public String getFirmwareRevision() {
                return null;
            }

            @Override
            public String getSoftwareRevision() {
                return null;
            }

            @Override
            public FitnessMachine getSystemId() {
                if (MODE == XE395ENT) {
                    //開兩個
                    Log.d("FTMSSS", "設定機型:" + CROSS_TRAINER);
                    return CROSS_TRAINER;
                } else {
                    Log.d("FTMSSS", "設定機型:" + INDOOR_BIKE);
                    return INDOOR_BIKE;
                }
            }
        };

        // 初始化 FitnessMachineManager
        // 設定FTMS廣播時的機器名稱
        mFTMSManager.setDeviceName(CommonUtils.textCheckNull(getDeviceSettingBean().getBle_device_name()));

        // 設定Device information data provider
        mFTMSManager.setDeviceInfoDataProvider(dataProvider);

        // 設定基本回應介面及控制回應介面
        mFTMSManager.registerListener(ftmsEventListener, ftmsControlListener);

        if (MODE == XE395ENT) {
            // 建立機型資訊項目清單
            MachineFeature[] machineFeatures = new MachineFeature[]{
                    MachineFeature.INSTANTANEOUS_SPEED_PRESENT,
                    MachineFeature.TOTAL_DISTANCE_PRESENT,
                    MachineFeature.STEP_COUNT_PRESENT,
                    MachineFeature.STRIDE_COUNT_PRESENT,
                    MachineFeature.ELEVATION_GAIN_PRESENT, // add for Gym kit at 03/17
                    MachineFeature.INCLINATION_AND_RAMP_ANGLE_PRESENT,
                    MachineFeature.RESISTANCE_LEVEL_PRESENT,
                    MachineFeature.INSTANTANEOUS_POWER_PRESENT,
                    MachineFeature.EXPENDED_ENERGY_PRESENT,
                    MachineFeature.HEART_RATE_PRESENT,
                    MachineFeature.ELAPSED_TIME_PRESENT};

            // 設定FTMS所支援的機型及其資訊項目
            mFTMSManager.addMachine(CROSS_TRAINER, machineFeatures);

            // 建立機型資訊項目清單 for Indoor Bike
            MachineFeature[] machineFeaturesForIndoorBike = new MachineFeature[]{
                    MachineFeature.INSTANTANEOUS_SPEED_PRESENT,
                    MachineFeature.INSTANTANEOUS_CADENCE_PRESENT,
                    MachineFeature.AVERAGE_CADENCE_PRESENT, // add for Gym kit at 03/17
                    MachineFeature.TOTAL_DISTANCE_PRESENT,
                    MachineFeature.RESISTANCE_LEVEL_PRESENT,
                    MachineFeature.INSTANTANEOUS_POWER_PRESENT,
                    MachineFeature.AVERAGE_POWER_PRESENT, // add for Gym kit at 03-/17
                    MachineFeature.EXPENDED_ENERGY_PRESENT,
                    MachineFeature.HEART_RATE_PRESENT,
                    MachineFeature.ELAPSED_TIME_PRESENT
            };

            // 設定FTMS所支援的機型及其資訊項目 Indoor Bike
            mFTMSManager.addMachine(INDOOR_BIKE, machineFeaturesForIndoorBike);
        } else {
            // 建立機型資訊項目清單 for Indoor Bike
            MachineFeature[] machineFeaturesForIndoorBike = new MachineFeature[]{
                    MachineFeature.INSTANTANEOUS_SPEED_PRESENT,
                    MachineFeature.INSTANTANEOUS_CADENCE_PRESENT,
                    MachineFeature.AVERAGE_CADENCE_PRESENT, // add for Gym kit at 03/17
                    MachineFeature.TOTAL_DISTANCE_PRESENT,
                    MachineFeature.RESISTANCE_LEVEL_PRESENT,
                    MachineFeature.INSTANTANEOUS_POWER_PRESENT,
                    MachineFeature.AVERAGE_POWER_PRESENT, // add for Gym kit at 03-/17
                    MachineFeature.EXPENDED_ENERGY_PRESENT,
                    MachineFeature.HEART_RATE_PRESENT,
                    MachineFeature.ELAPSED_TIME_PRESENT
            };

            // 設定FTMS所支援的機型及其資訊項目 Indoor Bike
            mFTMSManager.addMachine(INDOOR_BIKE, machineFeaturesForIndoorBike);
        }
        try {
            mFTMSManager.startFTMS();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MyApplication getInstance() {
        if (instance == null) {
            synchronized (MyApplication.class) {
                if (instance == null)
                    instance = new MyApplication();
            }
        }
        return instance;
    }

    /**
     * OkHttp初始化
     *
     * @return OkHttpClient
     */
    public OkHttpClient genericClient() {

        if (mOkHttpClient != null)
            return mOkHttpClient;

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.HEADERS;
        logInterceptor.setLevel(level);

        return mOkHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(6, TimeUnit.SECONDS)
                // 設置讀寫時間
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(logInterceptor)
                .build();
    }

    /**
     * 取得 UserProfileEntity
     *
     * @return UserProfileEntity
     */
    public UserProfileEntity getUserProfile() {
        return MMKV.defaultMMKV().decodeParcelable("UserProfileEntity", UserProfileEntity.class, new UserProfileEntity());
    }

    public void setUserProfile(UserProfileEntity userProfileEntity) {
        MMKV.defaultMMKV().encode("UserProfileEntity", userProfileEntity);
    }

    public DeviceInfoBean getDeviceInfoBean() {

        return MMKV.defaultMMKV().decodeParcelable("DeviceInfoBean", DeviceInfoBean.class, new DeviceInfoBean());
    }

//    public int[] getInclineAdAllStep() {
//        return MMKV.defaultMMKV().decodeInt("InclineAdAllStep", 0);
//    }

    /**
     * 取得GUID
     *
     * @return GUID
     */
    public String getIdentity() {
        return MMKV.defaultMMKV().decodeString("GUID", "");
    }


    //Fitness Machine Service PROTOCOL
    // public FitnessMachineManager getFTMSManager() {
    //      return mFTMSManager;
    //  }

    private static final String TAG = "UART_CONN";

    /**
     * init #UART
     */
    private void initUartConnect() {

        Device.DeviceEventListener mDeviceEventListener = new Device.DeviceEventListener() {
            @Override
            public void onRpmCounterMode(Device.RPM_COUNTER_MODE rpm_counter_mode) {

            }

            @Override
            public void onHeartRateMode(Device.HEART_RATE_MODE heart_rate_mode) {

              //  Log.d("CCCCCC", "onHeartRateMode: " + heart_rate_mode);
            }

            @Override
            public void onEEPRomWrite(Device.MCU_SET mcu_set) {

            }

            @Override
            public void onEEPRomRead(Device.MCU_GET mcu_get, byte[] bytes, byte[] bytes1) {

            }

            @Override
            public void onUsbModeSet(Device.MCU_SET mcu_set) {

                Log.d("USBBBB", "onUsbModeSet: " + mcu_set);

                RxBus.getInstance().post(new MsgEvent(ON_USB_MODE_SET, mcu_set));
            }

            @Override
            public void onConnectFail() {
                isUartConPortOpen = false;
                Log.d("START_DEVICE", "onConnectFail: ");
            }

            @Override
            public void onConnected() {
                isUartConPortOpen = true;
                Log.d("START_DEVICE", "UART on connected.");
            }

            @Override
            public void onDisconnected() {
                isUartConPortOpen = false;
                Log.d("START_DEVICE", "UART on disconnected.");
            }

            @Override
            public void onDataSend(String message) {
                //     Log.d("GGGGGGGG", "onDataSend:" + message);
            }

            @Override
            public void onDataReceive(String s) {
                //  Log.d("GGGGGGGG", "onDataReceive: " + s);
            }

            @Override
            public void onErrorMessage(String s) {
                //  if (checkEngineerRunning()) return;

                Log.d("START_DEVICE", "UART onErrorMessage: " + s);

                if(checkUpdateIng()) return;

                CommandErrorBean commandErrorBean = new CommandErrorBean();
                commandErrorBean.setErrorType(1);
                commandErrorBean.setErrorMessage(s);

                //incorrect mode

                if (checkEngineerRunning()) {
                    RxBus.getInstance().post(new MsgEvent(ON_ERROR2, commandErrorBean));
                } else {
                    RxBus.getInstance().post(new MsgEvent(ON_ERROR, commandErrorBean));
                }
            }

            /**主動通知?
             * CMD 0x60 error code
             * 00 Cmd NG (No this Cmd)
             * 01 Para err
             * 02 Cksum Err
             * 03 Len Err
             * 04 Mode Error
             * 05 No Support
             * 06 Busy
             * 07 LWR timeout
             * @param command
             * @param command_error
             */
            @Override
            public void onCommandError(Device.COMMAND command, Device.COMMAND_ERROR command_error) {

                String msg = "UART command error: command: " + command + ", error: " + command_error;
                Log.d("START_DEVICE", "onCommandError: " + msg);

                if(checkUpdateIng()) return;

                CommandErrorBean commandErrorBean = new CommandErrorBean();
                commandErrorBean.setErrorType(2);
                commandErrorBean.setCommand(command);
                commandErrorBean.setCommandError(command_error);

                if (checkEngineerRunning()) {
                    RxBus.getInstance().post(new MsgEvent(ON_ERROR2, commandErrorBean));
                } else {
                    RxBus.getInstance().post(new MsgEvent(ON_ERROR, commandErrorBean));
                }
            }

            @Override
            public void onKeyTrigger(Device.KEY keyNumber) {

                if (checkEngineerRunning()) {

                    keyBean.setEventType(COMMAND_KEY);
                    keyBean.setKeyStatus(0);
                    keyBean.setKey(keyNumber);
                    keyBean.setKeys(null);

                    RxBus.getInstance().post(new MsgEvent(COMMAND_KEY, keyBean));
                    return;
                }


                if (!checkWorkoutRunning() || !checkPauseRunning()) wakeUpScreen();


                if(checkUpdateIng()) return;


//                if (checkWorkoutRunning() && keyNumber == KEY_UNKNOWN) {
//
//                    keyBean.setEventType(COMMAND_KEY);
//                    keyBean.setKeyStatus(0);
//                    keyBean.setKey(keyNumber);
//                    keyBean.setPause(checkPauseRunning());
//                    keyBean.setKeys(null);
//                    EventBus.getDefault().post(keyBean);
//                    return;
//                }

                keyBean.setEventType(COMMAND_KEY);
                keyBean.setKeyStatus(0);
                keyBean.setKey(keyNumber);
                keyBean.setPause(checkPauseRunning());
                keyBean.setKeys(null);
                EventBus.getDefault().post(keyBean);

//                CommandErrorBean commandErrorBean = new CommandErrorBean();
//                commandErrorBean.setErrorType(2);
//                commandErrorBean.setCommand(Device.COMMAND.FAN_CONTROL);
//                commandErrorBean.setCommandError(Device.COMMAND_ERROR.NO_SUPPORT);
//                RxBus.getInstance().post(new MsgEvent(ON_ERROR, commandErrorBean));


                Log.d("BBBBB", "onKeyTrigger: " + keyNumber);
            }

            @Override
            public void onMultiKey(int second, List<Device.KEY> keys) {

                if (checkEngineerRunning()) {

                    keyBean.setEventType(COMMAND_KEY);
                    keyBean.setKeyStatus(1);
                    keyBean.setKey(null);
                    keyBean.setKeys(keys);

                    RxBus.getInstance().post(new MsgEvent(COMMAND_KEY, keyBean));
                    return;
                }

                boolean key06 = keys.contains(Device.KEY.KEY06);
                boolean key07 = keys.contains(Device.KEY.KEY07);
                boolean sec30 = second >= 30 && second < 60;

                Log.d("BBBBB", "onMultiKey:" + keys.toString() + ",秒數:" + second);
                //  Log.d("BBBBB", "MultiKey:" + key06 +","+ key07 +",秒數:"+second);

                if (checkWorkoutRunning() && keys.size() == 1) {
                    //長按 autoKey
                    keyBean.setEventType(COMMAND_KEY);
                    keyBean.setKeyStatus(1);
                    keyBean.setKey(null);
                    keyBean.setKeys(keys);
                    EventBus.getDefault().post(keyBean);

                } else if (keys.size() == 2) {
                    if (key06 && key07 && sec30) {
                        Log.d("BBBBB", "解鎖:");
                        keyBean.setEventType(COMMAND_KEY);
                        keyBean.setKeyStatus(1);
                        keyBean.setKey(null);
                        keyBean.setKeys(keys);
                        EventBus.getDefault().post(keyBean);
                    }
                }
            }

            @Override
            public void onEupNotify(Device.EUP eup) {
                Log.d("休眠", "onEupNotify: " + eup);
            }

            //切換模式回傳
            @Override
            public void onLwrMode(Device.LWR_MODE lwr_mode) {
                switch (lwr_mode) {
                    case NORMAL:
                        RxBus.getInstance().post(new MsgEvent(ON_LWR_MODE, lwr_mode));
                        break;
                    case SPEED_AND_INCLINE_CALI:
                        break;
                    case SPEED_CALI:
                        break;
                    case RESISTANCE_ADJ:
                        break;
                    case INCLINE_ADJ:
                        break;
                    case INCLINE_CALI:
                        //0x96 自動校正
                        new RxTimer().timer(1000, number -> commandSetInclineCalibration(Device.INCLINE_CALI_STEP.START));
                        break;
                    case RESET:
                        break;
                }
            }

            @Override
            public void onSpeedCali(Device.SPEED_CALI_STEP speed_cali_step, Device.SPEED_CALI_STATUS speed_cali_status, List<Device.MCU_ERROR> list, int rpm, int speed) {
            }

            //0x92
            @Override
            public void onEcbAdjust(Device.ACTION_STATUS action_status, int ad) {
                Log.d("JJJJJJ", "onEcbAdjust: " + action_status);
                RxBus.getInstance().post(new MsgEvent(ON_LEVEL_ADJUST, ad));
//                switch (action_status) {
//                    case STOP:
//                        RxBus.getInstance().post(new MsgEvent(ON_LEVEL_ADJUST, ad));
//                        break;
//                    case UP:
//                    case DOWN:
//                }
            }

            //0x93 setInclineAdjust
            @Override
            public void onInclineAdjust(Device.ACTION_STATUS action_status, int ad) {

                RxBus.getInstance().post(new MsgEvent(ON_INCLINE_ADJUST, ad));

//                switch (action_status) {
//                    case STOP:
//                    //    RxBus.getInstance().post(new MsgEvent(ON_INCLINE_ADJUST, ad));
//                        break;
//                    case UP:
//                    case DOWN:
//                }
            }

            @Override
            public void onInclineRead(int maxAd, int minAd, Device.INCLINE_READ_STATUS status) {
                //0x95 commandReadIncline

                // NEGATIVE ad表大到小
                // POSITIVE ad表小到大
                // NEW 沒校正
                Log.d(TAG, "onInclineRead: " + status + "," + maxAd + "," + minAd);

//                maxAd -= 15;
//                minAd += 15;

                //  int inclineStepAd = getStepAd(maxAd, minAd, 19);
                int inclineStepAd = getStepAd(maxAd, minAd, 40);

                StringBuilder incAdStr = new StringBuilder();

                if (status == Device.INCLINE_READ_STATUS.POSITIVE) {
                    for (int i = 0; i < 41; i++) {
                        incAdStr.append(i == 40 ? (maxAd - 5) : minAd + (inclineStepAd * i) + "#");
                    }
                } else if (status == Device.INCLINE_READ_STATUS.NEGATIVE) {

                    for (int i = 0; i < 41; i++) {
                        //  incAdStr.append(i == 40 ? minAd : maxAd - (inclineStepAd * i) + "#");
                        incAdStr.append(i == 40 ? (minAd + 5) : maxAd - (inclineStepAd * i) + "#");
                    }

                } else {
                    for (int i = 0; i < 41; i++) {
                        incAdStr.append("0#");
                    }
                    incAdStr.setLength(incAdStr.length() - 1);
                    Toasty.warning(MyApplication.this, "This device is not Calibrated.", Toasty.LENGTH_LONG).show();
                }

                deviceSettingBean = getDeviceSettingBean();
                deviceSettingBean.setDsInclineAd(incAdStr.toString());
                setDeviceSettingBean(deviceSettingBean);

                RxSettingBean rxSettingBean = new RxSettingBean();
                rxSettingBean.setInclineMaxAd(maxAd);
                rxSettingBean.setInclineMinAd(minAd);
                rxSettingBean.setStatus(status.toString());
                RxBus.getInstance().post(new MsgEvent(ON_INCLINE_READ, rxSettingBean));
            }

            //自動校正
            @Override
            public void onInclineCali(Device.INCLINE_CALI_STATUS incline_cali_status, int ad) {
                String msg = "incline cali : status: " + incline_cali_status + ", ad: " + ad;
                Log.d("ZZZZZZZZZ", "自動校正: " + msg);

                //傳ad
                RxBus.getInstance().post(new MsgEvent(ON_INCLINE_CALI, ad));

                switch (incline_cali_status) {
                    case SUCCESS:
                        commandReadIncline();
                        break;
                    case FAIL:
                        RxBus.getInstance().post(new MsgEvent(ON_INCLINE_CALI_FAIL, false));
                }
            }

            @Override
            public void onDeviceInfo(Device.MODEL model, String version, String key, String lwrVersion, int hrmStatus) {
                DeviceInfoBean deviceInfoBean = new DeviceInfoBean(COMMAND_DEVICE_INFO, model.name(), version, key, lwrVersion, hrmStatus);
                MMKV.defaultMMKV().encode("DeviceInfoBean", deviceInfoBean);

                RxBus.getInstance().post(new MsgEvent(ON_DEVICE_INFO, true));
                Log.d("休眠", "onDeviceInfo: " + "," + deviceInfoBean.toString());
            }

            @Override
            public void onMcuSetting(Device.MODEL model, Device.MCU_SET mcu_set) {
                String msg = "Mcu setting : model: " + model + ", status: " + mcu_set;
                Log.d("onDeviceInfo", "onMcuSetting: " + msg);
            }

            @Override
            public void onMCuControl(Device.MODEL model, List<Device.MCU_ERROR> errors, int hp, int wp, Device.ACTION_STATUS inclineStatus, int incline, Device.SAFE_KEY saveKeyStatus, int speed, int stepCount, Device.DIRECTION directStatus, int rpm, Device.ACTION_STATUS resStatus, int res, int rpm1, int rpm2, int rpmCount) {
                //setControl
                mcuBean.setEventType(COMMAND_SET_CONTROL);
                mcuBean.setModel(model);
                mcuBean.setErrors(errors);
                mcuBean.setHp(hp);
                mcuBean.setWp(wp);
                mcuBean.setInclineStatus(inclineStatus);
                mcuBean.setIncline(incline);
                mcuBean.setSaveKeyStatus(saveKeyStatus);
                mcuBean.setSpeed(speed);
                mcuBean.setStepCount(stepCount);
                mcuBean.setDirectStatus(directStatus);
                mcuBean.setLevelStatus(resStatus);
                mcuBean.setLevel(res);
                mcuBean.setRpm(rpm);
                mcuBean.setRpm1(rpm1);
                mcuBean.setRpm2(rpm2);
                mcuBean.setRpmCount(rpmCount);

                EventBus.getDefault().post(mcuBean);

                if (isAutoTest)
                    RxBus.getInstance().post(new MsgEvent(ON_AUTO_TEST, mcuBean));


                //  Log.d(TAG, "onMCuControl: " + mcuBean.toString());
            }

            @Override
            public void onEchoMode(Device.ECHO_MODE echo_mode) {
                String msg = "echo mode: " + echo_mode;

                Log.d(TAG, "onEchoMode: " + msg);
            }
        };

        try {
            mDevice.registerListener(mDeviceEventListener);
            mDevice.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /***************************************************************************************************
     * COMMAND                     *** START ***
     **************************************************************************************************/

    /**
     * 0x99 COMMAND_DEVICE_INFO
     * 裝置資訊
     * onDeviceInfo
     */
    public void commandDeviceInfo() {
        mDevice.getDeviceInfo();
    }

    /**
     * 0x71 COMMAND_FAN
     * 風扇控制
     *
     * @param mDeviceFan 風扇狀態
     */
    public void commandSetFan(Device.FAN mDeviceFan) {
        commandSetBuzzer(Device.BEEP.SHORT, 1);
        mDevice.setFan(mDeviceFan);
    }

    /**
     * 0x70 COMMAND_SET_EUP
     * EUP控制
     *
     * @param eupTime 斷電時間
     */
    public void commandSetEup(int eupTime) {
        mDevice.setEUP(eupTime);
    }

    /**
     * 0X90 COMMAND_SET_LWR_MODE
     *
     * @param mode mode
     */
    public void commandSetLwrMode(Device.LWR_MODE mode) { //Device.LWR_MODE.INCLINE_ADJ
        mDevice.setLwrMode(mode);
    }

    /**
     * 0x80 COMMAND_SET_CONTROL
     *
     * @param speed   0
     * @param resMode Device.ACTION_MODE.NORMAL
     * @param resAd   level ad
     * @param incMode Device.ACTION_MODE.NORMAL
     * @param incAd   incline ad
     * @param pwm     0
     */
    public void commandSetControl(int speed, Device.ACTION_MODE resMode, int resAd, Device.ACTION_MODE incMode, int incAd, int pwm) {
        //onMCuControl
        mDevice.setControl(speed, resMode, resAd, incMode, incAd, pwm);
    }

    /**
     * @param mode Device.ECHO_MODE.AA
     */
    public void commandSetEchoMode(Device.ECHO_MODE mode) {
        mDevice.setEchoMode(mode);
    }

    /**
     * @param mode NORMAL(0),ENGINEERING(1);
     */
    public void commandSetHeartRateMode(Device.HEART_RATE_MODE mode) {
        mDevice.setHeartRateMode(mode);
    }

    /**
     * 0x92
     * 校正 取 Level AD ，最大最小AD
     *
     * @param tremor          Device.TREMOR.SEEAD
     * @param tenMilliSeconds 執行時長
     */
    public void commandSetResistanceAdjust(Device.TREMOR tremor, int tenMilliSeconds) {
        mDevice.setResistanceAdjust(tremor, tenMilliSeconds);
    }

    /**
     * 0x93
     * 校正  取 Incline AD ，最大最小AD
     *
     * @param tremor
     * @param tenMilliSeconds 執行時長
     */
    public void commandSetInclineAdjust(Device.TREMOR tremor, int tenMilliSeconds) {
        mDevice.setInclineAdjust(tremor, tenMilliSeconds);
    }

    /**
     * 0x96
     * 自動校正 Incline
     *
     * @param step Device.INCLINE_CALI_STEP.START
     */
    public void commandSetInclineCalibration(Device.INCLINE_CALI_STEP step) {
        mDevice.setInclineCali(step);
    }

    /**
     * 0x95 COMMAND_READ_INCLINE
     * 自動校正 讀取Incline AD
     * <p>
     * 回傳 onInclineRead
     */
    public void commandReadIncline() {
        mDevice.readIncline();
    }

    /**
     * 0x9A COMMAND_SET_SPEED_CONFIG
     *
     * @param minSpeed  minSpeed
     * @param maxSpeed  maxSpeed
     * @param wheelSize wheelSize
     * @param cutSpeed  cutSpeed
     */
    public void commandSetSpeedConfig(int minSpeed, int maxSpeed, int wheelSize, int cutSpeed) {
        mDevice.setSpeedConfig(minSpeed, maxSpeed, wheelSize, cutSpeed);
    }


    /**
     * DATA(0),
     * CHARGER(1);
     *
     * @param mode
     */
    public void commandSetUsbMode(Device.USB_MODE mode) {
        mDevice.setUsbMode(mode);
    }

    /**
     * @param model model
     */
    public void commandSetCurrentModel(Device.MODEL model) {
        mDevice.setCurrentModel(model);
    }

    /**
     *
     */
    public void commandSetDeviceReset() {
        mDevice.setDeviceReset();
    }

    /**
     * @param pwm
     */
    public void commandSetPwm(int pwm) {
        mDevice.setPwm(pwm);
    }

    /**
     * @param millisecond
     */
    public void commandSetPeriod(int millisecond) {
        mDevice.setPeriod(millisecond);
    }

    /**
     * Device.RPM_COUNTER_MODE
     *
     * @param mode
     */
    public void commandSetRpmCount(Device.RPM_COUNTER_MODE mode) {
        mDevice.setRPMCounterMode(mode);
    }

    /**
     * @param order
     */
    public void commandSetAdOrderConfig(Device.ORDER order) {
        mDevice.setAdOrderConfig(order);
    }

    /**
     * 0x73 COMMAND_SET_BUZZER
     * 蜂鳴器設定
     *
     * @param beep
     * @param second
     */
    public void commandSetBuzzer(Device.BEEP beep, int second) {

        if (1 == getDeviceSettingBean().getBeep_sound())
            mDevice.setBuzzer(beep, second);
    }

    /**************************************************************************************************
     * COMMAND                   *** END ***
     **************************************************************************************************/


    /**
     * Step ad value calculate function
     */
    private int getStepAd(int minAd, int maxAd, int step) {
        return (Math.abs(maxAd - minAd)) / step;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //  System.exit(0);
        //  System.gc();
        Log.i("PPPPPPPPPPPPPP", "onLowMemory: ");
    }

    private void initBle() {

        mBleEventManager = new HeartRateDeviceManager(getInstance(), new HeartRateDeviceManager.HeartRateDeviceEventListener() {
            @Override
            public void onDiscoverDevice() {
                Log.d("心跳設備", "onDiscoverDevice: ");
                if (isBlueToothOn == 0)
                    RxBus.getInstance().post(new MsgEvent(ON_DISCOVER_DEVICE, true));
            }

            @Override
            public void onBleDeviceConnected(BleDevice bleDevice) {
                Log.d("心跳設備", "onBleDeviceConnected: " + bleDevice.getDeviceAddress());//FA:32:0F:AE:62:63
                if (isBlueToothOn == 0)
                    RxBus.getInstance().post(new MsgEvent(ON_BLE_DEVICE_CONNECTED, bleDevice));
            }

            @Override
            public void onBleDeviceDisconnected(String s) {
                Log.d("心跳設備", "onBleDeviceDisconnected: " + s); //FA:32:0F:AE:62:63
                if (isBlueToothOn == 0)
                    RxBus.getInstance().post(new MsgEvent(ON_DISCOVER_DEVICE, true));

                //   if (checkWorkoutRunning()) {
                RxBus.getInstance().post(new MsgEvent(ON_BLE_DEVICE_DISCONNECTED, true));
                //    }
            }

            @Override
            public void onHeartRateChanged(BleDevice bleDevice, int hrv) {
                //   Log.d("心跳設備", "onBleDeviceDisconnected: " + bleDevice.getDeviceName() +",HRV:"+ hrv);
                EventBus.getDefault().post(new BleHrBean(ON_HEART_RATE_CHANGED, hrv));

                if (checkEngineerRunning()) {
                    RxBus.getInstance().post(new MsgEvent(ON_HEART_RATE_CHANGED, hrv));
                }
            }
        });
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public boolean checkWorkoutRunning() {

        return MMKV.defaultMMKV().decodeBool("WorkoutDashboardActivity", false);
    }

    public boolean checkDashboardRunning() {
        return MMKV.defaultMMKV().decodeBool("DashboardActivity", false);
    }

    public boolean checkPauseRunning() {
        return MMKV.defaultMMKV().decodeBool("WorkoutPauseActivity", false);
    }

    public boolean checkEngineerRunning() {
        return MMKV.defaultMMKV().decodeBool("EngineerActivity", false);
    }

    public boolean checkUpdateIng() {
        return MMKV.defaultMMKV().decodeBool("CheckUpdateIng", false);
    }

    private void initCheckActive() {
        MMKV.defaultMMKV().encode("DashboardActivity", false);
        MMKV.defaultMMKV().encode("WorkoutDashboardActivity", false);
        MMKV.defaultMMKV().encode("WorkoutPauseActivity", false);
        MMKV.defaultMMKV().encode("EngineerActivity", false);
        MMKV.defaultMMKV().encode("CheckUpdateIng", false);
    }

    public DeviceSettingBean getDeviceSettingBean() {
        return deviceSettingBean = MMKV.defaultMMKV().decodeParcelable("DeviceSettingBean", DeviceSettingBean.class, new DeviceSettingBean());
    }

    public void setDeviceSettingBean(DeviceSettingBean deviceSettingBean) {
        MMKV.defaultMMKV().encode("DeviceSettingBean", deviceSettingBean);
    }

    public AppUpdateData getAppUpdateData() {
        return appUpdateData = MMKV.defaultMMKV().decodeParcelable("AppUpdateData", AppUpdateData.class, new AppUpdateData());
    }

    public void setAppUpdateData(AppUpdateData appUpdateData) {
        MMKV.defaultMMKV().encode("AppUpdateData", appUpdateData);
    }

    //    private void initSetting() {
//
//        //  MMKV.defaultMMKV().remove("DeviceSettingBean");
//
//        DeviceSettingBean deviceSettingBean = getDeviceSettingBean();
//        if (TextUtils.isEmpty(deviceSettingBean.getModel_name())) { //DeviceSettingBean 無值 > 讀設定檔
//            Log.d("SETTING_FILE", "DeviceSettingBean無值，第一次進入");
//            String settingFile = new CommonUtils().getSettingFile(this); //若無設定檔 > 取預設值產生設定檔
//            if (null != settingFile) {
//                Log.d("SETTING_FILE", "取得設定檔資料成功");
//
//                new InitProduct(this).setProductDefault(settingFile);//將設定檔的值存入 DeviceSettingBean
//            } else {
//                Log.d("SETTING_FILE", "取得設定檔資料及建立設定檔失敗，直接取程式內取預設值");
//                new InitProduct(this).setProductDefault(XE395ENT); //產生設定檔失敗，直接取程式內預設值 存入 DeviceSettingBean
//            }
//        } else {
//            //DeviceSettingBean已有值，設定當前 Model
//            MODE = getMode(deviceSettingBean.getModel_code());
//            //  new InitProduct(this).setProductDefault(XE395ENT);
//
//            Log.d("SETTING_FILE", "DeviceSettingBean已有值，設定當前 Model:" + MODE);
//        }
//    }

    private void initSetting() {

        //  MMKV.defaultMMKV().remove("DeviceSettingBean");

        String modelName = CommonUtils.textCheckNull(getDeviceSettingBean().getModel_name());
        String settingFile = new CommonUtils().getSettingFile(getApplicationContext()); //若無設定檔 > 取預設值產生設定檔
        if (null != settingFile) {
            Log.d("SETTING_FILE", "讀取設定檔資料成功");
            DeviceSettingBean deviceSettingBean = new Gson().fromJson(settingFile, DeviceSettingBean.class);
            if (modelName.equals(deviceSettingBean.getModel_name())) {
                //只是檢查 設定檔 的 ModelName 不需要覆蓋 DeviceSettingBean
                MODE = getMode(deviceSettingBean.getModel_code());
                Log.d("SETTING_FILE", "設定檔的機型(VersionName&VersionCode)與裝置內存放的機型(VersionName&VersionCode)相同");
            } else {
                new InitProduct(getApplicationContext()).setProductDefault(settingFile);//將設定檔的值存入 DeviceSettingBean
                Log.d("SETTING_FILE", "設定檔的機型(VersionName&VersionCode)與裝置內存放的不同，取設定檔資料 重新寫入裝置資料庫:" + MODE);
            }
            Log.d("SETTING_FILE", "當前機型為：" + MODE + " 開機完成 ");
        } else {
            Log.d("SETTING_FILE", "讀取設定檔資料或建立設定檔失敗，直接取程式內機型預設值");
            new InitProduct(getApplicationContext()).setProductDefault(XE395ENT); //產生設定檔失敗，直接取程式內預設值 存入 DeviceSettingBean
        }
    }


    @SuppressLint("InvalidWakeLockTag")
    private void wakeUpScreen() {

        if (checkWorkoutRunning() || checkPauseRunning()) return;

        Log.d("BBBBB", "開啟螢幕: ");
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

//        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
//                | PowerManager.ACQUIRE_CAUSES_WAKEUP
//                | PowerManager.ON_AFTER_RELEASE, "WAKE");
//FLAG_KEEP_SCREEN_ON
        PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "WAKE");

        mWakeLock.acquire(5000);
    }
    
    private final Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
            CommonUtils.closePackage(getApplicationContext());
            Intent intent = new Intent(instance, MainActivity.class);
            //重啟應用，得使用PendingIntent
            PendingIntent restartIntent = PendingIntent.getActivity(instance, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mAlarmManager = (AlarmManager) instance.getSystemService(Context.ALARM_SERVICE);
            mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, restartIntent); // 2秒鐘後重啟應用
            android.os.Process.killProcess(android.os.Process.myPid());  //結束當前程序
        }
    };
}
