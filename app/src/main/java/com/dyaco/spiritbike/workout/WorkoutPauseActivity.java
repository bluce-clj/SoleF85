package com.dyaco.spiritbike.workout;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.corestar.app.BleDevice;
import com.corestar.libs.device.Device;
import com.corestar.libs.ftms.FitnessMachine;
import com.corestar.libs.ftms.MachineFeature;
import com.corestar.libs.ftms.ProfileFTMS;
import com.corestar.libs.ftms.StopOrPause;
import com.dyaco.spiritbike.FTMSBean;

import com.dyaco.spiritbike.KeyBean;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.mirroring.FloatingHeartRateService;
import com.dyaco.spiritbike.mirroring.FloatingSoundSettingService;
import com.dyaco.spiritbike.product_flavors.ModeEnum;
import com.dyaco.spiritbike.settings.SettingsFragment;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.FloatingWidget;
import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.ProgramsEnum;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.RxBus;
import com.dyaco.spiritbike.uart.McuBean;
import com.dyaco.spiritbike.uart.isBusEvent;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.dyaco.spiritbike.MyApplication.BT_SOUND_CONNECT;
import static com.dyaco.spiritbike.MyApplication.COMMAND_KEY;
import static com.dyaco.spiritbike.MyApplication.COMMAND_SET_CONTROL;
import static com.dyaco.spiritbike.MyApplication.ENABLE_BT;
import static com.dyaco.spiritbike.MyApplication.FTMS_NOTIFY;
import static com.dyaco.spiritbike.MyApplication.FTMS_NOTIFY_SET_INCLINE;
import static com.dyaco.spiritbike.MyApplication.FTMS_NOTIFY_SET_LEVEL;
import static com.dyaco.spiritbike.MyApplication.FTMS_NOTIFY_START_OR_RESUME;
import static com.dyaco.spiritbike.MyApplication.FTMS_NOTIFY_STOP_OR_PAUSE;
import static com.dyaco.spiritbike.MyApplication.MODE;
import static com.dyaco.spiritbike.MyApplication.ON_BLE_DEVICE_CONNECTED;
import static com.dyaco.spiritbike.MyApplication.ON_BLE_DEVICE_DISCONNECTED;
import static com.dyaco.spiritbike.MyApplication.ON_HEART_RATE_CHANGED;
import static com.dyaco.spiritbike.MyApplication.btnFnaI;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.MyApplication.isBlueToothOn;
import static com.dyaco.spiritbike.MyApplication.isFTMSNotify;
import static com.dyaco.spiritbike.MyApplication.isFtmsConnected;
import static com.dyaco.spiritbike.MyApplication.isSoundConnected;
import static com.dyaco.spiritbike.product_flavors.ModeEnum.XE395ENT;
import static com.dyaco.spiritbike.support.CommonUtils.findMaxInt;
import static com.dyaco.spiritbike.support.CommonUtils.isFastClick;
import static com.dyaco.spiritbike.support.CommonUtils.setTime;
import static com.dyaco.spiritbike.workout.WorkoutDashboardActivity.calculation;
import static com.dyaco.spiritbike.workout.WorkoutDashboardActivity.tmpWorkoutBean;

public class WorkoutPauseActivity extends BaseAppCompatActivity {
    public FloatingWidget floatingWidget = new FloatingWidget(this);
    private TextView tvTimeout;
    private WorkoutBean workoutBean;
    private ImageView iv_hr_connected;
    private boolean isKeyStopWorkout = false;
    private ImageButton btn_fan;
    private ImageButton bt_bt;
    private boolean isKeyPauseWorkout;
    private ImageView iv_sound_connected;
    private ImageView iv_fna_connected;
    private ImageView iv_wifi;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private boolean isPauseIng = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_pause);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        boolean isPause = bundle.getBoolean("isPause", true);

        getInstance().commandSetRpmCount(Device.RPM_COUNTER_MODE.PAUSE);

        if (!isPause) {
            //蜂鳴器
            MyApplication.getInstance().commandSetBuzzer(Device.BEEP.SHORT, 3);
            done();
        } else {
            EventBus.getDefault().register(this);

            iv_sound_connected = findViewById(R.id.iv_sound_connected);
            iv_fna_connected = findViewById(R.id.iv_fna_connected);
            iv_wifi = findViewById(R.id.iv_wifi);
            iv_wifi.setImageResource(CommonUtils.setWifiImage(new CommonUtils().getWifiLevel(this), true));

            btn_fan = findViewById(R.id.btn_fan);

            bt_bt = findViewById(R.id.bt_bt);

            bt_bt.setOnClickListener(v ->
                    floatingWidget.callSetting(1, WorkoutPauseActivity.class));

            iv_wifi.setOnClickListener(v ->
                    floatingWidget.callSetting(0, WorkoutPauseActivity.class));

//            if (btnFnaI == 0) {
//                btn_fan.setBackgroundResource(R.drawable.btn_round_fan0_9b9b9b_64);
//            } else if (btnFnaI == 1) {
//                btn_fan.setBackgroundResource(R.drawable.btn_round_fan1_9b9b9b_64);
//            } else if (btnFnaI == 2) {
//                btn_fan.setBackgroundResource(R.drawable.btn_round_fan2_9b9b9b_64);
//            } else {
//                btn_fan.setBackgroundResource(R.drawable.btn_round_fan3_9b9b9b_64);
//            }

            if (btnFnaI == 0) {
                iv_fna_connected.setVisibility(View.INVISIBLE);
            } else {
                iv_fna_connected.setVisibility(View.VISIBLE);
            }

            //風扇
            btn_fan.setOnClickListener(v -> {
                if (btnFnaI == 0) {
                    btnFnaI = 3;
                    getInstance().commandSetFan(Device.FAN.STRONG);
                    iv_fna_connected.setVisibility(View.VISIBLE);
                } else {
                    btnFnaI = 0;
                    getInstance().commandSetFan(Device.FAN.STOP);
                    iv_fna_connected.setVisibility(View.INVISIBLE);
                }
            });

//            //風扇
//            btn_fan.setOnClickListener(v -> {
//                if (btnFnaI == 0) {
//                    btn_fan.setBackgroundResource(R.drawable.btn_round_fan1_9b9b9b_64);
//                    btnFnaI = 1;
//                    getInstance().commandSetFan(Device.FAN.WEAK);
//                } else if (btnFnaI == 1) {
//                    btn_fan.setBackgroundResource(R.drawable.btn_round_fan2_9b9b9b_64);
//                    btnFnaI = 2;
//                    getInstance().commandSetFan(Device.FAN.MIDDLE);
//                } else if (btnFnaI == 2) {
//                    btn_fan.setBackgroundResource(R.drawable.btn_round_fan3_9b9b9b_64);
//                    btnFnaI = 3;
//                    getInstance().commandSetFan(Device.FAN.STRONG);
//                } else {
//                    btn_fan.setBackgroundResource(R.drawable.btn_round_fan0_9b9b9b_64);
//                    btnFnaI = 0;
//                    getInstance().commandSetFan(Device.FAN.STOP);
//                }
//            });

            ImageButton btnSound = findViewById(R.id.btn_sound);
            //音量
            btnSound.setOnClickListener(v -> {
//                Intent intent2 = new Intent(WorkoutPauseActivity.this, SoundActivity.class);
//                Bundle bundle2 = new Bundle();
//                bundle2.putBoolean("isWorkout", true);
//                intent2.putExtras(bundle2);
//                startActivity(intent2);

                if (!Settings.canDrawOverlays(this)) {
                    startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
                } else {
                    if (!FloatingSoundSettingService.isStarted) {
                        Intent serviceIntent = new Intent(this, FloatingSoundSettingService.class);
                        serviceIntent.putExtra("isWorkout", true);
                        startService(serviceIntent);
                    }
                }
            });

            iv_hr_connected = findViewById(R.id.iv_hr_connected);
            TextView tv_workout_time = findViewById(R.id.tv_workout_time);
            tv_workout_time.setText(setTime());
            tvTimeout = findViewById(R.id.tv_time_out);
            Button btNo_DialogDataLost = findViewById(R.id.btNo_DialogDataLost);
            Button btYes_DialogDataLost = findViewById(R.id.btYes_DialogDataLost);

            //Resume
            btNo_DialogDataLost.setOnClickListener(v -> {

                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                        if (isFtmsConnected && isFTMSNotify)
                            getInstance().mFTMSManager.notifyStartOrResume();
                        setResult(RESULT_OK, null);
                        finish();
                        MyApplication.SSEB = false;
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
            );

            //結束
            btYes_DialogDataLost.setOnClickListener(v -> done());

            ImageButton ibHR = findViewById(R.id.btHeartRate_Dashboard);

            ibHR.setOnClickListener(v -> {

                if (!FloatingHeartRateService.isStarted) {
                    Intent serviceIntent = new Intent(this, FloatingHeartRateService.class);
                    serviceIntent.putExtra("isWorkout", true);
                    startService(serviceIntent);
                }

//                Intent intent1 = new Intent(WorkoutPauseActivity.this, HeartRateActivity.class);
//                Bundle bundle1 = new Bundle();
//                bundle1.putBoolean("isWorkout", true);
//                intent1.putExtras(bundle1);
//                startActivity(intent1);
            });

            initEvent();

            countDownTimer.start();
        }
    }

    private void initEvent() {
        Disposable d = RxBus.getInstance().toObservable(MsgEvent.class).subscribe(msg -> {
            //SOUND CONNECT
            if (msg.getType() == BT_SOUND_CONNECT) {
                runOnUiThread(this::showSoundConnectedIcon);
                return;
            }

            if (msg.getType() == ON_BLE_DEVICE_CONNECTED) {
                runOnUiThread(this::showHrConnectedIcon);
                return;
            }

            if (msg.getType() == ON_BLE_DEVICE_DISCONNECTED) {
                runOnUiThread(this::showHrConnectedIcon);
                return;
            }

//            if (msg.getType() == ENABLE_BT) {
//                enableBT();
//                return;
//            }
        });

        compositeDisposable.add(d);
    }

//    private void enableBT() {
//        //開啟藍芽
//        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        startActivityForResult(enableBtIntent, 123);
//    }

    CountDownTimer countDownTimer = new CountDownTimer(1000 * 60 * 5, 1000) {
        public void onTick(long millisUntilFinished) {
            String mm = String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
            String ss = String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

            // runOnUiThread(() -> tvTimeout.setText(String.format(Locale.getDefault(), "00:%02d", millisUntilFinished / 1000L)));
            runOnUiThread(() -> tvTimeout.setText(String.format("%s:%s", mm, ss)));
        }

        public void onFinish() {
            runOnUiThread(() -> done());
        }
    };

    private void done() {

        MyApplication.SSEB = false;

//        if (CommonUtils.isFastClick()) {
//            return;
//        }
        //   btYes_DialogDataLost.setEnabled(false);

        try {

            if (isFtmsConnected && isFTMSNotify)
                getInstance().mFTMSManager.notifyStoppedOrPaused(StopOrPause.STOP);

            workoutBean = tmpWorkoutBean;
            saveDataToWorkOutBean();

            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            //   ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);

            // > WorkoutResultSummaryFragment
            Intent intent = new Intent(this, DashboardActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("openSummary", ProgramsEnum.MANUAL.getCode());
            bundle.putSerializable("WorkoutBean", workoutBean);
            intent.putExtras(bundle);
            //   startActivity(intent, options.toBundle());
            //   startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this,btYes_DialogDataLost, "sharedView").toBundle());
            startActivity(intent);
            finishAffinity();

            overridePendingTransition(0, android.R.anim.slide_out_right);

            calculation.clear();
            calculation = null;
            tmpWorkoutBean = null;

        } catch (Exception e) {

            e.printStackTrace();
            setResult(RESULT_OK, null);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        //   System.exit(0);
    }

    private void saveDataToWorkOutBean() {

        //跑到第幾階
        int runSegment = workoutBean.getCurrentSegment();

        Date date = Calendar.getInstance().getTime();
        workoutBean.setUpdateTime(date);

        //存 Level / Incline 的值
        int totalLevel = 0;
        int totalIncline = 0;
        //    int totalHR = 0;
        StringBuilder levelNum = new StringBuilder(); //全部段
        StringBuilder inclineNum = new StringBuilder();
        StringBuilder hrNum = new StringBuilder();

        StringBuilder levelNumReal = new StringBuilder(); //跑到第幾個段
        StringBuilder inclineNumReal = new StringBuilder();

        for (int i = 0; i < 20; i++) {
     //   for (int i = 0; i < runSegment; i++) {

            if (MODE == XE395ENT) {
                int incline = workoutBean.getDiagramInclineList().get(i).getProgressIncline();
                inclineNum.append(incline).append("#");

                //只加跑過的，或已重複的
                if ((i <= (runSegment - 1)) || workoutBean.getReCount() > 1){
                    totalIncline += incline;
                    inclineNumReal.append(incline).append("#");
                }
            }

            int level = workoutBean.getDiagramLevelList().get(i).getProgressLevel();
            int hr = workoutBean.getDiagramHRList().get(i).getHr();

            levelNum.append(level).append("#");
            hrNum.append(hr).append("#");

            //只加跑過的，或已重複的
            if ((i <= (runSegment -1)) || workoutBean.getReCount() > 1){
                totalLevel += level;
                levelNumReal.append(level).append("#");
            }
        }
        levelNum = levelNum.deleteCharAt(levelNum.length() - 1);

        levelNumReal = levelNumReal.deleteCharAt(levelNumReal.length() - 1);

        hrNum = hrNum.deleteCharAt(hrNum.length() - 1);
        workoutBean.setLevelDiagramNum(levelNum.toString());
        workoutBean.setHrDiagramNum(hrNum.toString());

        if (MODE == XE395ENT) {
            inclineNum = inclineNum.deleteCharAt(inclineNum.length() - 1);
            workoutBean.setInclineDiagramNum(inclineNum.toString());

            inclineNumReal = inclineNumReal.deleteCharAt(inclineNumReal.length() - 1);
        }

        String avgSpeed = String.valueOf(calculation.getSpeedHrAverage());
        String calories = String.valueOf(calculation.getKcalAccumulate());
        String avgRPM = String.valueOf(calculation.getRpmAverage());
        String avgWATT = String.valueOf(calculation.getWattAverage());
        String maxWatt = String.valueOf(calculation.getWattMax());
        String avgMET = String.valueOf(calculation.getMetsAverage());
        String totalDistance = String.valueOf(calculation.getDistanceAccumulate());
        String pace = String.valueOf(calculation.getPace());
        String avgPace = String.valueOf(calculation.getPaceAverage());

        workoutBean.setAvgSpeed(avgSpeed);
        workoutBean.setCalories(calories);
        workoutBean.setAvgRPM(avgRPM);
        workoutBean.setAvgWATT(avgWATT);
        workoutBean.setAvgMET(avgMET);
        workoutBean.setTotalDistance(totalDistance);

        workoutBean.setWorkoutMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
        workoutBean.setWattAccumulate(calculation.getWattAccumulate());
        workoutBean.setWattFrequency(calculation.getWattFrequency());

        //LEVEL 跑過的平均
        String avgLevel = String.valueOf(totalLevel / runSegment);
        workoutBean.setAvgLevel(avgLevel);

        Log.d("OOOWOWOWOWO", "saveDataToWorkOutBean: " + totalLevel +","+ runSegment +","+ avgLevel);

        //LEVEL 跑過的最大LEVEL
        int[] arrayLevel = Arrays.stream(levelNumReal.toString().split("#", -1))
                .mapToInt(Integer::parseInt)
                .toArray();

        if (MODE == XE395ENT) {
            //INCLINE 跑過的平均
            String avgIncline = String.valueOf(totalIncline / runSegment);
            workoutBean.setAvgIncline(avgIncline);

            //INCLINE 跑過的最大INCLINE
            int[] arrayIncline = Arrays.stream(inclineNumReal.toString().split("#", -1))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            workoutBean.setMaxIncline(CommonUtils.findMaxInt(arrayIncline));
        }

        Log.d("KL:KL:", "totalLevel: "+totalLevel +",totalIncline:"+ totalIncline+",runSegment:"+  runSegment);

        workoutBean.setMaxLevel(CommonUtils.findMaxInt(arrayLevel));
        workoutBean.setMaxWATT(maxWatt);

        //   String avgHR = String.valueOf(totalHR / 20);

        int[] arrayHR = Arrays.stream(hrNum.toString().split("#", -1))
                .mapToInt(Integer::parseInt)
                .toArray();
        String maxHR = String.valueOf(findMaxInt(arrayHR));


        workoutBean.setMaxHR(maxHR);
        //少於一分鐘 不紀錄
        if (workoutBean.getRunTime() < 60) {
            workoutBean.setAvgHR("0");
            workoutBean.setMaxHR("0");
        }

        workoutBean.setAvgPace(avgPace);
        workoutBean.setPace(pace);

        workoutBean.setDiagramInclineList(null);
        workoutBean.setDiagramLevelList(null);

        try {
            clearFTMSData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFTMSData() {
        if (MODE == XE395ENT) {
            ProfileFTMS profileFTMS = getInstance().mFTMSManager.getProfile(FitnessMachine.CROSS_TRAINER);
            profileFTMS.setFeatureValue(MachineFeature.INSTANTANEOUS_SPEED_PRESENT, 0);
            profileFTMS.setFeatureValue(MachineFeature.TOTAL_DISTANCE_PRESENT, 0);
            profileFTMS.setFeatureValue(MachineFeature.INCLINATION_AND_RAMP_ANGLE_PRESENT, 0, 0);
            profileFTMS.setFeatureValue(MachineFeature.RESISTANCE_LEVEL_PRESENT, 0);
            profileFTMS.setFeatureValue(MachineFeature.INSTANTANEOUS_POWER_PRESENT, 0);
            profileFTMS.setFeatureValue(MachineFeature.STEP_COUNT_PRESENT, 0, 0);
            profileFTMS.setFeatureValue(MachineFeature.EXPENDED_ENERGY_PRESENT, 0, 0, 0);
            profileFTMS.setFeatureValue(MachineFeature.HEART_RATE_PRESENT, 0);
            profileFTMS.setFeatureValue(MachineFeature.ELAPSED_TIME_PRESENT, 0);
        }

        ProfileFTMS profileFTMS2 = getInstance().mFTMSManager.getProfile(FitnessMachine.INDOOR_BIKE);
        profileFTMS2.setFeatureValue(MachineFeature.INSTANTANEOUS_SPEED_PRESENT, 0);
        profileFTMS2.setFeatureValue(MachineFeature.TOTAL_DISTANCE_PRESENT, 0);
        profileFTMS2.setFeatureValue(MachineFeature.RESISTANCE_LEVEL_PRESENT, 0);
        profileFTMS2.setFeatureValue(MachineFeature.INSTANTANEOUS_POWER_PRESENT, 0);
        profileFTMS2.setFeatureValue(MachineFeature.EXPENDED_ENERGY_PRESENT, 0, 0, 0);
        profileFTMS2.setFeatureValue(MachineFeature.HEART_RATE_PRESENT, 0);
        profileFTMS2.setFeatureValue(MachineFeature.ELAPSED_TIME_PRESENT, 0);
        profileFTMS2.setFeatureValue(MachineFeature.INSTANTANEOUS_CADENCE_PRESENT, 0);

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            btnExitFullScreen.removeFloatView();
        } catch (Exception e) {
            e.printStackTrace();
        }

        showHrConnectedIcon();

        showSoundConnectedIcon();

        MMKV.defaultMMKV().encode("WorkoutPauseActivity", true);

        isPauseIng = false;

        try {
            floatingWidget.removeFloatingView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * EventBus Receiver
     *
     * @param isBusEvent EventEntity
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommandEvent(isBusEvent isBusEvent) {

        if (isBusEvent.getEventType() == FTMS_NOTIFY) {
            switch (((FTMSBean) isBusEvent).getFtmsNotifyType()) {
                case FTMS_NOTIFY_START_OR_RESUME:

                    callResume();

//                    if (isFastClick()) return;
//
//                    //resume
//                    if (countDownTimer != null) {
//                        countDownTimer.cancel();
//                        countDownTimer = null;
//                    }
//                    setResult(RESULT_OK, null);
//                    finish();
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    break;
                case FTMS_NOTIFY_STOP_OR_PAUSE:
                    callStop();
//                    //stop
//                    if (((FTMSBean) isBusEvent).getStopOrPause() == 1) { //stop
//                        callStop();
//                    }
                    break;
            }
        } else if (isBusEvent.getEventType() == COMMAND_KEY) {

            KeyBean keyBean = ((KeyBean) isBusEvent);
            if (keyBean.getKeyStatus() == 0) {
                switch (keyBean.getKey()) {
                    case KEY01:
                        //FAN
                        btn_fan.callOnClick();
                        break;
                    case KEY03:
                        //stop
                        callStop();
                        getInstance().commandSetBuzzer(Device.BEEP.SHORT, 1);
                        break;
                    case KEY05:
                        //resume
                        callResume();
                        getInstance().commandSetBuzzer(Device.BEEP.SHORT, 1);
                        break;
                }
            }

        }
    }

    private void callStop() {
        if (!isKeyStopWorkout) {
            isKeyStopWorkout = true;
            done();
            getInstance().commandSetBuzzer(Device.BEEP.SHORT, 1);
        }
    }

    private void callResume() {
        if (!isKeyPauseWorkout) {
            MyApplication.SSEB = false;
            if (isPauseIng) CommonUtils.closePackage(this);
            isKeyPauseWorkout = true;
            if (isFtmsConnected && isFTMSNotify) getInstance().mFTMSManager.notifyStartOrResume();
            setResult(RESULT_OK, null);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            getInstance().commandSetBuzzer(Device.BEEP.SHORT, 1);

        }
    }

//    private void showHrConnectedIcon() {
//        if (getInstance().mBleEventManager.getConnectedBleDevices() != null
//                && getInstance().mBleEventManager.getConnectedBleDevices().size() > 0 && isBlueToothOn == 0) {
//            if (iv_hr_connected != null) iv_hr_connected.setVisibility(View.VISIBLE);
//        } else {
//            if (iv_hr_connected != null) iv_hr_connected.setVisibility(View.INVISIBLE);
//        }
//    }

    private void showHrConnectedIcon() {
        boolean xx = false;
        if (getInstance().mBleEventManager.getPeripherals() != null
                && getInstance().mBleEventManager.getPeripherals().size() > 0 && isBlueToothOn == 0) {

            for (BleDevice bleDevice : getInstance().mBleEventManager.getPeripherals()) {
                if (bleDevice.isConnect()) xx = true;
            }
            if (iv_hr_connected != null)
                iv_hr_connected.setVisibility(xx ? View.VISIBLE : View.INVISIBLE);
        } else {
            if (iv_hr_connected != null) iv_hr_connected.setVisibility(View.INVISIBLE);
        }
    }

    private void showSoundConnectedIcon() {
        //音訊連結圖示
        try {
            if (isSoundConnected) {
                if (iv_sound_connected != null) iv_sound_connected.setVisibility(View.VISIBLE);
            } else {
                if (iv_sound_connected != null) iv_sound_connected.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BtnExitFullScreen btnExitFullScreen = new BtnExitFullScreen(this);

    @Override
    protected void onPause() {
        super.onPause();
        btnExitFullScreen.showBtnFullScreenExit(WorkoutPauseActivity.class, true);
        isPauseIng = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            btnExitFullScreen.removeFloatView();
            btnExitFullScreen = null;

            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            if (compositeDisposable != null) {
                compositeDisposable.dispose();
                compositeDisposable = null;
            }
            MMKV.defaultMMKV().encode("WorkoutPauseActivity", false);
            EventBus.getDefault().unregister(this);

            floatingWidget.removeFloatingView();
            floatingWidget = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}