package com.dyaco.spiritbike.workout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.corestar.libs.device.Device;
import com.corestar.libs.ftms.FitnessMachine;
import com.corestar.libs.ftms.FitnessMachineControlOperation;
import com.corestar.libs.ftms.FitnessMachineControlResponse;
import com.corestar.libs.ftms.MachineFeature;
import com.corestar.libs.ftms.ProfileFTMS;
import com.corestar.libs.ftms.StopOrPause;
import com.corestar.libs.ftms.TrainingStatus;
import com.dyaco.spiritbike.CommandErrorBean;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.FTMSBean;
import com.dyaco.spiritbike.KeyBean;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.engineer.DeviceSettingBean;
import com.dyaco.spiritbike.mirroring.DashboardDataBean;
import com.dyaco.spiritbike.mirroring.FloatingSoundSettingService;
import com.dyaco.spiritbike.mirroring.FloatingWorkoutDashboardService;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.FloatingWidget;
import com.dyaco.spiritbike.support.GlideApp;
import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.ProgramsEnum;
import com.dyaco.spiritbike.support.RxBus;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.internet.WorkoutInternetFragment;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.uart.DeviceInfoBean;
import com.dyaco.spiritbike.uart.isBusEvent;
import com.dyaco.spiritbike.uart.McuBean;
import com.dyaco.spiritbike.webapi.TrainingProcessBean;
import com.example.librarycalculationuniversal.Elliptical.EllipticalCalculation;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static android.media.AudioManager.FLAG_SHOW_UI;
import static android.media.AudioManager.STREAM_MUSIC;
import static android.view.MotionEvent.ACTION_UP;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.dyaco.spiritbike.MyApplication.BT_SOUND_CONNECT;
import static com.dyaco.spiritbike.MyApplication.COMMAND_KEY;
import static com.dyaco.spiritbike.MyApplication.FAN_NOTIFY;
import static com.dyaco.spiritbike.MyApplication.FTMS_NOTIFY;
import static com.dyaco.spiritbike.MyApplication.FTMS_NOTIFY_SET_INCLINE;
import static com.dyaco.spiritbike.MyApplication.FTMS_NOTIFY_SET_LEVEL;
import static com.dyaco.spiritbike.MyApplication.FTMS_NOTIFY_START_OR_RESUME;
import static com.dyaco.spiritbike.MyApplication.FTMS_NOTIFY_STOP_OR_PAUSE;
import static com.dyaco.spiritbike.MyApplication.FTMS_NOTIFY_TRAINING_STATUS;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_BTN_AUTO_DOWN;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_BTN_AUTO_UP;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_BTN_INCLINE_MINUS;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_BTN_INCLINE_PLUS;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_BTN_LEVEL_MINUS;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_BTN_LEVEL_PLUS;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_DASHBOARD_DATA;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_EXIT_FULL_SCREEN;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_CAST;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_HOME_SCREEN;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_PROFILE;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_PROGRAMS;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_WEB_VIEW;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_SET_CURRENT_INCLINE;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_SET_CURRENT_LEVEL;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_SET_MAX_INCLINE;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_SET_MAX_LEVEL;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_STOP;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_STOP_WORKOUT;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_SWITCH_DASHBOARD_2;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_SWITCH_DASHBOARD_3;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_SWITCH_DASHBOARD_4;
import static com.dyaco.spiritbike.MyApplication.MODE;
import static com.dyaco.spiritbike.MyApplication.ON_BLE_DEVICE_DISCONNECTED;
import static com.dyaco.spiritbike.MyApplication.ON_ERROR;
import static com.dyaco.spiritbike.MyApplication.ON_HEART_RATE_CHANGED;
import static com.dyaco.spiritbike.MyApplication.REMOVE_BUTTON;
import static com.dyaco.spiritbike.MyApplication.STOP_FLOATING_DASHBOARD;
import static com.dyaco.spiritbike.MyApplication.VIDEO_BACK_HOME;
import static com.dyaco.spiritbike.MyApplication.WIFI_EVENT;
import static com.dyaco.spiritbike.MyApplication.btnFnaI;
import static com.dyaco.spiritbike.MyApplication.isFTMSNotify;
import static com.dyaco.spiritbike.MyApplication.isFtmsConnected;
import static com.dyaco.spiritbike.MyApplication.isInclineErrorShow;
import static com.dyaco.spiritbike.MyApplication.isLevelErrorShow;
import static com.dyaco.spiritbike.product_flavors.ModeEnum.XE395ENT;
import static com.dyaco.spiritbike.support.CommonUtils.findMaxInt;
import static com.dyaco.spiritbike.support.CommonUtils.incF2I;
import static com.dyaco.spiritbike.support.CommonUtils.incI2F;
import static com.dyaco.spiritbike.support.CommonUtils.isFastClick;
import static com.dyaco.spiritbike.support.CommonUtils.setTime;
import static com.dyaco.spiritbike.support.ProgramsEnum.HEART_RATE;
import static com.dyaco.spiritbike.MyApplication.COMMAND_SET_CONTROL;
import static com.dyaco.spiritbike.MyApplication.TIME_EVENT;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.ProgramsEnum.MANUAL;
import static com.dyaco.spiritbike.support.UnitEnum.*;

public class WorkoutDashboardActivity extends AppCompatActivity {
    private Group group_incline;
    public boolean isMirrorShow = false; //樂播開啟中
    private ProfileFTMS profileFTMS1;
    private ProfileFTMS profileFTMS_IndoorBike;
    public FloatingWidget floatingWidget = new FloatingWidget(this);
    WorkoutBean workoutBean;
    public final UserProfileEntity userProfileEntity = getInstance().getUserProfile();
    public final DeviceSettingBean deviceSettingBean = getInstance().getDeviceSettingBean();
    public static EllipticalCalculation calculation;
    public int INCLINE_MAX = 10;
    //public final double INCLINE_MAX = 5.0;
    public int LEVEL_MAX; //當前顯示的 max level
    private Button btnStop;
    private TextView tvMaxIncline;
    private TextView tvMaxLevel;
    private TextView tvLevelCurrent;
    private TextView tvInclineCurrent;

    public ImageButton btn_level_plus;
    public ImageButton btn_level_minus;
    public ImageButton btn_incline_plus;
    public ImageButton btn_incline_minus;

    private TextView tv_Speed_Rpm_text; //tv_pace_text
    private TextView tv_pace_unit; //tv_pace_unit
    private TextView tv_CHr_AHR_text; //tv_hr_text
    private TextView tv_hr_unit; //tv_hr_unit
    private TextView tv_Etime_Ltime_text; //tv_time_text
    private TextView tv_calories_watt_text; //tv_calories_text
    private TextView tv_unit_kcal; //tv_unit_kcal
    private TextView tv_total_distance_unit;
    private TextView tv_3_unit;

    public boolean isDistanceSwitch = true;
    public boolean isCaloriesSwitch = true;
    public boolean isTimeSwitch = true;
    public boolean isHrSwitch = true;
    public boolean isSpeedSwitch = true;
    public int isThreeSwitch = 0;

    private Fragment currentFragment = new Fragment();
    public Fragment workoutDashboardFragment, workoutDiagramFragment, workoutTrackFragment, internetFragment, mirroringFragment;
    private FragmentManager manager;
    private ImageButton btn_fan;
    private ImageButton btnSound;
    private View fragmentView;
    private TextView tv_workout_time;
    private FragmentTransaction transaction;

    private TextView tvTotalTime;
    private TextView tvCompleted;
    private long workoutTime;

    private TextView tvDistance;
    private TextView tvCalories;
    private TextView tvPace;
    private TextView tvHeartRate;
    public ProgramsEnum PROGRAM_TYPE;

    private int[] incAdArray;
    private int[] levelAdArray;

    // public ImageView ivSwitchDistance;
    //  public ImageView ivSwitchCalories;
    public View switch_2;
    public View switch_3;
    public View switch_4;
    //    public ImageView ivSwitchTime;
//    public ImageView ivSwitchHR;
    //  public ImageView ivSwitchSpeed;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ImageView iv_wifi;
    private Dialog dialog;

    private ImageView clBaseView;
    private String mWatt;
    private String mCalories;
    private String mCaloriesAvg;
    private String mElapsedTime;
    private String mRemainTime;

    public RadioButton rb_dashboard_workout;
    public RadioButton rb_diagram_workout;
    public RadioButton rb_track_workout;
    public RadioButton rb_internet_workout;
    public RadioButton rb_cast_workout;
    private int mAvgHr;
    private int mTotalHR;

    private TextView tvErrorCurrentIncline;
    private TextView tvErrorCurrentLevel;

    private ImageView iv_sound_connected;
    private ImageView iv_fna_connected;
    private boolean isFtmsUpdateIncline;
    private boolean isFtmsUpdateLevel;
    public boolean levelError;
    public boolean inclineError;
    public boolean onWorkoutStopIng;
    private double mWattAvg;

    public boolean isWebViewFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_dashboard);

        setV();

        MyApplication.SSEB = true;

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        workoutBean = (WorkoutBean) bundle.getSerializable("WorkoutBean");

        PROGRAM_TYPE = ProgramsEnum.getProgram(workoutBean.getProgramId());

        Log.d("WORK_OUT", "PROGRAM_TYPE: " + PROGRAM_TYPE);

        if (workoutBean.isTemplate()) {

            int[] arrayLevel = Arrays.stream(workoutBean.getInclineDiagramNum().split("#", -1))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            int maxIncline = Math.max(findMaxInt(arrayLevel), 5);

            workoutBean.setOrgMaxIncline(maxIncline);
            workoutBean.setMaxIncline(maxIncline);
            INCLINE_MAX = maxIncline;
        } else {
            workoutBean.setOrgMaxIncline((PROGRAM_TYPE == MANUAL || PROGRAM_TYPE == HEART_RATE) ? 40 : INCLINE_MAX);
            workoutBean.setMaxIncline((PROGRAM_TYPE == MANUAL || PROGRAM_TYPE == HEART_RATE) ? 40 : INCLINE_MAX);
            INCLINE_MAX = (PROGRAM_TYPE == MANUAL || PROGRAM_TYPE == HEART_RATE) ? 40 : 10;
        }

        try {
            if (PROGRAM_TYPE == MANUAL) {
                getInstance().mFTMSManager.notifyTrainingStatus(TrainingStatus.MANUAL_MODE);
            } else {
                getInstance().mFTMSManager.notifyTrainingStatus(TrainingStatus.FITNESS_TEST);
            }

            if (isFtmsConnected && isFTMSNotify) getInstance().mFTMSManager.notifyStartOrResume();

        } catch (Exception e) {
            e.printStackTrace();
        }

        getInstance().commandSetRpmCount(Device.RPM_COUNTER_MODE.CLEAR);


        String model = getInstance().getDeviceInfoBean().getModel() != null ? getInstance().getDeviceInfoBean().getModel() : "";
        Log.d("型號", "板子:" + model + ", 設定:" + MODE);

      //  if (model.equals("")) showTypeError();

//        if ((model.equals("ECB") && MODE == XE395ENT) || (model.equals("ECB_AND_INC") && MODE != XE395ENT)) {
//            showTypeError();
//        } else {
            showStartDialog();

            initData();

            initView();

            initMode();

            showBackground();

            initEvent();

            //  setUnitText();

            initDelay();
  //      }

    }

    private void initDelay() {

        Looper.myQueue().addIdleHandler(() -> {

            initFragment();

            transaction = manager.beginTransaction();
            //  showFragment(internetFragment);
            showFragment(workoutTrackFragment);
            showFragment(workoutDiagramFragment);
            showFragment(workoutDashboardFragment);

            new Handler().postDelayed(() -> {

                if (HEART_RATE != PROGRAM_TYPE) {
                    //除了HR，其他Programs 在DashBoard顯示時間
                    ((WorkoutDashboardFragment) workoutDashboardFragment).setDashBoardTimer(workoutTime);
                    ((WorkoutDashboardFragment) workoutDashboardFragment).setTimeCenter();
                }
                btnStop.setEnabled(true);
                ((WorkoutDashboardFragment) workoutDashboardFragment).tvNormalText.setText(workoutTime == 0 ? R.string.time_elapsed : R.string.time_left);

                initTimer(false);

                startDialog.dismiss();
                startDialog = null;

            }, 300);

            return false;
        });
    }

    private int mUnit;
    private int mWeight;

    private void initData() {

        //    getInstance().commandSetLwrMode(Device.LWR_MODE.NORMAL);

        workoutTime = workoutBean.getTimeSecond() * 1000;

        //倒數時間
        reciprocal = workoutTime;

        LEVEL_MAX = workoutBean.getMaxLevel();

        receiverNotify();

        mUnit = userProfileEntity.getUnit();
        workoutBean.setUnit(mUnit);
        mWeight = mUnit == 0 ? userProfileEntity.getWeight_metric() : userProfileEntity.getWeight_imperial();

        //初始化 TrainingProcess
        TrainingProcessBean trainingProcessBean = new TrainingProcessBean();
        List<TrainingProcessBean.SysResponseDataBean> list = new ArrayList<>();
        trainingProcessBean.setSys_response_data(list);
        workoutBean.setTrainingProcessBean(trainingProcessBean);
//        sysResponseDataBean = new TrainingProcessBean.SysResponseDataBean();

        //初始化AD
        initAD();

        initCalculation();


        try {
//            if (MODE == XE395ENT) {
//                profileFTMS = getInstance().mFTMSManager.getProfile(FitnessMachine.CROSS_TRAINER);
//            } else {
//                profileFTMS = getInstance().mFTMSManager.getProfile(FitnessMachine.INDOOR_BIKE);
//            }

            profileFTMS1 = getInstance().mFTMSManager.getProfile(FitnessMachine.CROSS_TRAINER);
            profileFTMS_IndoorBike = getInstance().mFTMSManager.getProfile(FitnessMachine.INDOOR_BIKE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化AD
     */
    private void initAD() {
        String incAdStr = getInstance().getDeviceSettingBean().getDsInclineAd();
        if (!TextUtils.isEmpty(incAdStr)) {
            incAdArray = Arrays.stream(incAdStr.split("#"))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        } else {
            incAdArray = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        }

        String levelAdStr = getInstance().getDeviceSettingBean().getDsLevelAd();
        if (!TextUtils.isEmpty(levelAdStr)) {
            levelAdArray = Arrays.stream(levelAdStr.split("#"))
                    .mapToInt(Integer::parseInt)
                    .toArray();
        } else {
            levelAdArray = MODE.getLevelAD();
        }

        Log.d("WORKOUT_AD", "LEVEL: " + Arrays.toString(levelAdArray) + ",INCLINE:" + Arrays.toString(incAdArray));
    }

    private void initEvent() {

        btn_level_plus.setOnClickListener(view -> {
            if (PROGRAM_TYPE == HEART_RATE) {
                ((WorkoutDashboardFragment) workoutDashboardFragment).updateTargetHeartRate(true);
            } else {
                // Log.d(">>>>>>>>", "btn_level_plus ccc: ");
                callUpdateLevel(1, true);
            }
        });
        btn_level_minus.setOnClickListener(view -> {
            if (PROGRAM_TYPE == HEART_RATE) {
                ((WorkoutDashboardFragment) workoutDashboardFragment).updateTargetHeartRate(false);
            } else {
                //  Log.d(">>>>>>>>", "btn_level_minus ccc: ");
                callUpdateLevel(-1, true);
            }
        });

        btn_incline_plus.setOnClickListener(view -> {
            callUpdateIncline(1, true);
        });

        btn_incline_minus.setOnClickListener(view -> {
            callUpdateIncline(-1, true);
        });

        //風扇
        btn_fan.setOnClickListener(v -> {
            if (btnFnaI == 0) {
                btnFnaI = 3;
                getInstance().commandSetFan(Device.FAN.STRONG);
                iv_fna_connected.setVisibility(View.VISIBLE);
                RxBus.getInstance().post(new MsgEvent(FAN_NOTIFY, true));
            } else {
                btnFnaI = 0;
                getInstance().commandSetFan(Device.FAN.STOP);
                iv_fna_connected.setVisibility(View.INVISIBLE);
                RxBus.getInstance().post(new MsgEvent(FAN_NOTIFY, false));
            }
        });
//        //風扇
//        btn_fan.setOnClickListener(v -> {
//
//            if (btnFnaI == 0) {
//                btn_fan.setBackgroundResource(R.drawable.btn_round_fan1_9b9b9b_64);
//                btnFnaI = 1;
//                getInstance().commandSetFan(Device.FAN.WEAK);
//            } else if (btnFnaI == 1) {
//                btn_fan.setBackgroundResource(R.drawable.btn_round_fan2_9b9b9b_64);
//                btnFnaI = 2;
//                getInstance().commandSetFan(Device.FAN.MIDDLE);
//            } else if (btnFnaI == 2) {
//                btn_fan.setBackgroundResource(R.drawable.btn_round_fan3_9b9b9b_64);
//                btnFnaI = 3;
//                getInstance().commandSetFan(Device.FAN.STRONG);
//            } else {
//                btn_fan.setBackgroundResource(R.drawable.btn_round_fan0_9b9b9b_64);
//                btnFnaI = 0;
//                getInstance().commandSetFan(Device.FAN.STOP);
//            }
//        });

        //音量
        btnSound.setOnClickListener(v -> {

//            Intent intent1 = new Intent(WorkoutDashboardActivity.this, SoundActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putBoolean("isWorkout", true);
//            intent1.putExtras(bundle);
//            startActivity(intent1);

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

        //暫停
        btnStop.setOnClickListener((View v) -> onWorkOutStop(true));

        //-Speed unit:RPM
//        ivSwitchDistance.setOnClickListener(v -> {
//            // tv_total_distance_unit.setText(isSpeedSwitch ? "CALORIES" : "CALORIES");
//            isDistanceSwitch = !isDistanceSwitch;
//        });

        //-Calories / (Calories / Min)
//        ivSwitchCalories.setOnClickListener(v -> {
//            isCaloriesSwitch = !isCaloriesSwitch;
//            tvCalories.setText(isCaloriesSwitch ? mCalories : mCaloriesAvg);
//            tv_calories_watt_text.setText(isCaloriesSwitch ? "CALORIES" : "CALORIES/MIN");
//            // tv_unit_kcal.setText(isCaloriesSwitch ? "KCAL" : "");
//        });
        switch_2.setOnClickListener(v -> {
            isCaloriesSwitch = !isCaloriesSwitch;
            tvCalories.setText(isCaloriesSwitch ? mCalories : mCaloriesAvg);
            tv_calories_watt_text.setText(isCaloriesSwitch ? "CALORIES" : "CALORIES/MIN");
        });


        //-Total Time / Distance / Time Left
//        ivSwitchTime.setOnClickListener(v -> {
//            if (isThreeSwitch == 0) {
//                isThreeSwitch = 1;
//                tvTotalTime.setText(distanceAccmulate);
//                tv_Etime_Ltime_text.setText("DISTANCE");
//                tv_3_unit.setText(getUnit(DISTANCE));
//            } else if (isThreeSwitch == 1) {
//                isThreeSwitch = 2;
//                tvTotalTime.setText(workoutTime != 0 ? mRemainTime : "-:-");
//                tv_Etime_Ltime_text.setText("TIME LEFT");
//                tv_3_unit.setText("");
//            } else if (isThreeSwitch == 2) {
//                isThreeSwitch = 0;
//                tvTotalTime.setText(mElapsedTime);
//                tv_Etime_Ltime_text.setText("TOTAL TIME");
//                tv_3_unit.setText("");
//            }
//        });

        switch_3.setOnClickListener(v -> {
            if (isThreeSwitch == 0) {
                isThreeSwitch = 1;
                tvTotalTime.setText(distanceAccmulate);
                tv_Etime_Ltime_text.setText("DISTANCE");
                tv_3_unit.setText(getUnit(DISTANCE));
            } else if (isThreeSwitch == 1) {
                isThreeSwitch = 2;
                tvTotalTime.setText(workoutTime != 0 ? mRemainTime : "-:-");
                tv_Etime_Ltime_text.setText("TIME LEFT");
                tv_3_unit.setText("");
            } else if (isThreeSwitch == 2) {
                isThreeSwitch = 0;
                tvTotalTime.setText(mElapsedTime);
                tv_Etime_Ltime_text.setText("TOTAL TIME");
                tv_3_unit.setText("");
            }
        });

        //-Current HR/ Avg HR
//        ivSwitchHR.setOnClickListener(v -> {
//            isHrSwitch = !isHrSwitch;
//            tvHeartRate.setText(isHrSwitch ? String.valueOf(mCurrentHR) : String.valueOf(mAvgHr));
//            tv_CHr_AHR_text.setText(isHrSwitch ? "CURRENT HR" : "AVG HR");
//            tv_hr_unit.setText("bpm");
//        });

        switch_4.setOnClickListener(v -> {
            isHrSwitch = !isHrSwitch;
            tvHeartRate.setText(isHrSwitch ? String.valueOf(mCurrentHR) : String.valueOf(mAvgHr));
            tv_CHr_AHR_text.setText(isHrSwitch ? "CURRENT HR" : "AVG HR");
            tv_hr_unit.setText("bpm");
        });

//        //-POWER unit:w
//        ivSwitchSpeed.setOnClickListener(v -> {
//            isSpeedSwitch = !isSpeedSwitch;
//            tvPace.setText(isSpeedSwitch ? mSpeed : String.valueOf(mRPM));
//            tv_Speed_Rpm_text.setText(isSpeedSwitch ? "SPEED" : "RPM");
//            tv_pace_unit.setText(isSpeedSwitch ? "KM/H" : "");
//        });
    }

    public void callUpdateIncline(int updateIncline, boolean beep) {
        if (onInclinePlusListener != null)
            onInclinePlusListener.onInclineUpdate(updateIncline, beep);
    }

    public void callUpdateLevel(int updateLevel, boolean beep) {
        if (onLevelUpdateListener != null) {
            if (isWorkoutTimerRunning) //workout是否在計時
                onLevelUpdateListener.onLevelUpdate(updateLevel, beep);
        }
    }

    private void initFan() {

        try {
            if (btnFnaI == 0) {
                iv_fna_connected.setVisibility(View.INVISIBLE);
            } else {
                iv_fna_connected.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (btnFnaI == 0) {
//            btn_fan.setBackgroundResource(R.drawable.btn_round_fan0_9b9b9b_64);
//        } else if (btnFnaI == 1) {
//            btn_fan.setBackgroundResource(R.drawable.btn_round_fan1_9b9b9b_64);
//        } else if (btnFnaI == 2) {
//            btn_fan.setBackgroundResource(R.drawable.btn_round_fan2_9b9b9b_64);
//        } else {
//            btn_fan.setBackgroundResource(R.drawable.btn_round_fan3_9b9b9b_64);
//        }
    }

    /**
     * WorkoutDiagramFragment取得當前 Level/Incline
     *
     * @return currentLevel/currentIncline
     */
    public int getCurrentLevel() {
        int l = 0;
        try {
            l = CommonUtils.null2Zero(tvLevelCurrent.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public int getCurrentIncline() {

        //  return CommonUtils.null2Zero(tvInclineCurrent.getText().toString());
        int i = 0;
        try {
            i = incF2I(Double.parseDouble(tvInclineCurrent.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * WorkoutDiagramFragment更新當前 Level/Incline
     *
     * @param currentIncline 當前 Level/Incline
     */
    public void setCurrentIncline(int currentIncline, boolean beep) {
        //  tvInclineCurrent.setText(String.valueOf(currentIncline));
        //  tvInclineCurrent.setText(String.valueOf((currentIncline - 1) / 2d));

        tvInclineCurrent.setText(String.valueOf(incI2F(currentIncline)));

        sendCommandSetControl(getCurrentLevel(), getCurrentIncline(), beep, 2);

        if (FloatingWorkoutDashboardService.isStarted) {
            RxBus.getInstance().post(new MsgEvent(MIRRORING_SET_CURRENT_INCLINE, String.valueOf(incI2F(currentIncline))));
        }

        inclineFloat();
    }

    public void setCurrentLevel(int currentLevel, boolean beep) {

        tvLevelCurrent.setText(String.valueOf(currentLevel));
        sendCommandSetControl(getCurrentLevel(), getCurrentIncline(), beep, 1);

        if (FloatingWorkoutDashboardService.isStarted) {
            RxBus.getInstance().post(new MsgEvent(MIRRORING_SET_CURRENT_LEVEL, String.valueOf(currentLevel)));
        }


        levelFloat();

//        if (windowManager_level != null && levelView != null) {
//            try {
//                xxcl.setText(String.valueOf(currentLevel));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            if (floatButtonIsStarted) {
//                levelFloat();
//            }
//        }
    }

    /**
     * WorkoutDiagramFragment 更新 MaxLevel
     *
     * @param maxLevel level
     */
    public void setMaxLevel(int maxLevel) {
        tvMaxLevel.setText(String.valueOf(maxLevel));
        if (FloatingWorkoutDashboardService.isStarted) {
            RxBus.getInstance().post(new MsgEvent(MIRRORING_SET_MAX_LEVEL, String.valueOf(maxLevel)));
        }

        if (windowManager_level != null && levelView != null && xxml != null) {
            try {
                xxml.setText(String.valueOf(maxLevel));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setMaxIncline(int maxIncline) {
        tvMaxIncline.setText(String.valueOf(incI2F(maxIncline)));
        //   tvMaxIncline.setText(String.valueOf(maxIncline));
        if (FloatingWorkoutDashboardService.isStarted) {
            RxBus.getInstance().post(new MsgEvent(MIRRORING_SET_MAX_INCLINE, String.valueOf(incI2F(maxIncline))));
        }

        if (inclineView != null && windowManager_incline != null && xxmi != null) {
            try {
                xxmi.setText(tvMaxIncline.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initView() {

        iv_sound_connected = findViewById(R.id.iv_sound_connected);
        iv_fna_connected = findViewById(R.id.iv_fna_connected);
        tvErrorCurrentIncline = findViewById(R.id.hideInclineCurrent);
        tvErrorCurrentLevel = findViewById(R.id.hideLevelCurrent);
        tv_total_distance_unit = findViewById(R.id.tv_total_distance_unit);
        tv_3_unit = findViewById(R.id.tv_3_unit);

        tv_Speed_Rpm_text = findViewById(R.id.tv_pace_text);
        tv_pace_unit = findViewById(R.id.tv_pace_unit);

        tv_CHr_AHR_text = findViewById(R.id.tv_hr_text);
        tv_hr_unit = findViewById(R.id.tv_hr_unit);

        tv_Etime_Ltime_text = findViewById(R.id.tv_time_text);

        tv_calories_watt_text = findViewById(R.id.tv_calories_text);
        tv_unit_kcal = findViewById(R.id.tv_unit_kcal);

        iv_wifi = findViewById(R.id.iv_wifi);
        iv_wifi.setImageResource(CommonUtils.setWifiImage(new CommonUtils().getWifiLevel(this), true));
        iv_wifi.setOnClickListener(v ->
                floatingWidget.callSetting(0, WorkoutDashboardActivity.class));

        clBaseView = findViewById(R.id.cl_base);

        btnStop = findViewById(R.id.btn_stop);

        tvTotalTime = findViewById(R.id.tv_total_time);
        tvCompleted = findViewById(R.id.tv_completed);

        tvMaxIncline = findViewById(R.id.tvMaxIncline);
        // tvMaxIncline.setText(String.valueOf(INCLINE_MAX));


        group_incline = findViewById(R.id.group_incline);

        tvMaxLevel = findViewById(R.id.tvMaxLevel);

        tvMaxLevel.setText(String.valueOf(LEVEL_MAX));
        setMaxIncline(INCLINE_MAX);

        tv_workout_time = findViewById(R.id.tv_workout_time);
        tv_workout_time.setText(setTime());
        tvLevelCurrent = findViewById(R.id.tvLevelCurrent);
        tvInclineCurrent = findViewById(R.id.tvInclineCurrent);

        btn_level_minus = findViewById(R.id.btn_level_plus);
        btn_level_plus = findViewById(R.id.btn_level_minus);
        btn_incline_minus = findViewById(R.id.btn_incline_plus);
        btn_incline_plus = findViewById(R.id.btn_incline_minus);

        addAutoClick(btn_level_plus);
        addAutoClick(btn_level_minus);
        addAutoClick(btn_incline_plus);
        addAutoClick(btn_incline_minus);

        tvDistance = findViewById(R.id.tv_distance);
        tvCalories = findViewById(R.id.tv_calories);
        tvPace = findViewById(R.id.tv_pace);
        tvHeartRate = findViewById(R.id.tv_hr);

        fragmentView = findViewById(R.id.f_workout);

        rb_dashboard_workout = findViewById(R.id.rb_dashboard_workout);
        rb_diagram_workout = findViewById(R.id.rb_diagram_workout);
        rb_track_workout = findViewById(R.id.rb_track_workout);
        rb_internet_workout = findViewById(R.id.rb_internet_workout);
        rb_cast_workout = findViewById(R.id.rb_cast_workout);

        //MENU
        rb_dashboard_workout.setOnClickListener(rbDashboardOnClick);
        rb_diagram_workout.setOnClickListener(rbDashboardOnClick);
        rb_track_workout.setOnClickListener(rbDashboardOnClick);
        rb_internet_workout.setOnClickListener(rbDashboardOnClick);
        rb_cast_workout.setOnClickListener(rbDashboardOnClick);

        btnSound = findViewById(R.id.btn_sound);

        btn_fan = findViewById(R.id.btn_fan);

        //    ivSwitchDistance = findViewById(R.id.iv_switch_distance);
        //   ivSwitchCalories = findViewById(R.id.iv_switch_calories);
        switch_2 = findViewById(R.id.switch_2);
        switch_3 = findViewById(R.id.switch_3);
        switch_4 = findViewById(R.id.switch_4);
//        ivSwitchTime = findViewById(R.id.iv_switch_time);
//        ivSwitchHR = findViewById(R.id.iv_switch_hr);
//        ivSwitchSpeed = findViewById(R.id.iv_switch_speed);

        showSoundConnectedIcon();
    }

    /**
     * 切換Fragment
     */
    private final RadioButton.OnClickListener rbDashboardOnClick = new RadioButton.OnClickListener() {
        @Override
        public void onClick(View view) {

            int rbId = view.getId();

            if (rbId == R.id.rb_dashboard_workout) {
                showFragment(workoutDashboardFragment);
            } else if (rbId == R.id.rb_diagram_workout) {
                showFragment(workoutDiagramFragment);
            } else if (rbId == R.id.rb_track_workout) {
                showFragment(workoutTrackFragment);
            } else if (rbId == R.id.rb_internet_workout) {
                showFragment(internetFragment);
            } else if (rbId == R.id.rb_cast_workout) {
                showFragment(mirroringFragment);
            }
        }
    };

    public static WorkoutBean tmpWorkoutBean;

    /**
     * Go to WorkoutPauseActivity
     *
     * @param isPause 是否為暫停
     */
    public void onWorkOutStop(boolean isPause) {

        isWebViewFull = false;
        onWorkoutStopIng = true;

        removeFloatView();

        ((WorkoutInternetFragment) internetFragment).webViewExitRemove();

        //   if (isFtmsConnected && isFTMSNotify) getInstance().mFTMSManager.notifyStartOrResume();
        if (isPause) {
            if (isFtmsConnected && isFTMSNotify) {
                try {
                    getInstance().mFTMSManager.notifyStoppedOrPaused(StopOrPause.PAUSE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (workoutTime != 0) {
            //不要超過 workout設定的時間
            timerTotalTimeSec = Math.min(timerTotalTimeSec, workoutTime);
        }

        workoutBean.setRunTime(timerTotalTimeSec);
        workoutBean.setAvgHR(String.valueOf(mAvgHr));

        //停止所有計時
        isWorkoutTimerRunning = false;

        ((WorkoutTrackFragment) workoutTrackFragment).stopAnimation();
//        if (PROGRAM_TYPE == HEART_RATE)
//            ((WorkoutDashboardFragment) workoutDashboardFragment).pauseMaintainingTimer();

        //SeekBar無法序列化
        tmpWorkoutBean = workoutBean;

        //level incline 回到第1階
        sendCommandSetControl(1, 0, false, 0);

        //取消ECHO
        getInstance().commandSetEchoMode(Device.ECHO_MODE.AA);

        MyApplication.SSEB = false;


        workoutBean.setCurrentSegment(((WorkoutDiagramFragment) workoutDiagramFragment).currentSegment + 1);
        workoutBean.setReCount(((WorkoutDiagramFragment) workoutDiagramFragment).reCount);

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isPause", isPause);
        intent.putExtras(bundle);
        intent.setClass(WorkoutDashboardActivity.this, WorkoutPauseActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            tmpWorkoutBean = null;

            initTimer(true);

            initFan();

            MyApplication.SSEB = true;
            onWorkoutStopIng = false;
        }
    }

    /**
     * 隱藏/顯示 Fragment
     *
     * @param fragment fragment
     */
    public void showFragment(Fragment fragment) {
        MyApplication.SSEB = true;
        try {
            if (currentFragment != fragment) {
                transaction = manager.beginTransaction();
                transaction.hide(currentFragment);
                currentFragment = fragment;
                if (!fragment.isAdded()) {
                    transaction.add(R.id.f_workout, fragment).show(fragment).commit();
                } else {
                    transaction.show(fragment).commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        manager = getSupportFragmentManager();
        workoutDashboardFragment = new WorkoutDashboardFragment();
        workoutDiagramFragment = new WorkoutDiagramFragment();
        workoutTrackFragment = new WorkoutTrackFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean("isWorkout", true);
        internetFragment = new WorkoutInternetFragment();
        internetFragment.setArguments(bundle);
        //   mirroringFragment = new MirroringFragment();
        mirroringFragment = new WorkoutMirroringFragment();
        mirroringFragment.setArguments(bundle);
    }

    /**
     * 把fragment 擺到整個 layout 的最前面
     *
     * @param isFullScreen 是否全螢幕
     */
    public void makeFullScreen(boolean isFullScreen) {
        if (isFullScreen) {
            fragmentView.bringToFront();
            btnStop.setVisibility(View.INVISIBLE);
        } else {
            sendViewToBack(fragmentView);
            sendViewToBack(clBaseView);
            btnStop.setVisibility(View.VISIBLE);
        }
    }

    public void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup) child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }

    /**
     * @param isResume 是否暫停後重啟
     */
    private void initTimer(boolean isResume) {

        //設為ECHO模式
        getInstance().commandSetEchoMode(Device.ECHO_MODE.SECOND);

        getInstance().commandSetRpmCount(Device.RPM_COUNTER_MODE.RESUME);

        if (isResume) {
            //發送setControl 取得rpm
            sendCommandSetControl(getCurrentLevel(), getCurrentIncline(), false, 0);
        }

        isWorkoutTimerRunning = true;
        if (!isResume) {
            //第一次啟動
            rxTimer = new RxTimer();
            rxTimer.interval(1000, this::runTime);
        } else {
            //暫停重啟
            //track重新啟動
            ((WorkoutTrackFragment) workoutTrackFragment).startAnimation();
            isKeyPauseWorkout = false;
            //HeartRate重新啟動
//            if (PROGRAM_TYPE == HEART_RATE)
//                ((WorkoutDashboardFragment) workoutDashboardFragment).startMaintainingTimer();
        }
    }

    private RxTimer rxTimer;
    public boolean isWorkoutTimerRunning = false; //workout是否在計時
    private long timerTotalTimeSec;
    private long completedPresent;
    long reciprocal;
    long timeCount; //ms
    long xxTime;
    long xxCount;

    /**
     * 計時
     *
     * @param milliSeconds milliSeconds
     */
    private void runTime(long milliSeconds) {

        if (isWorkoutTimerRunning) { //是否啟動

            timerTotalTimeSec++; //經過的秒數

            //經過的毫秒
            long millisUntilFinished = timerTotalTimeSec * 1000;

            //計算
            calculation.setData(mRPM, getCurrentLevel(), getCurrentIncline());

            if (workoutTime == 0) {
                //正數
                timeCount = millisUntilFinished;

            } else {
                //倒數，剩餘時間
                reciprocal -= 1000;
                timeCount = reciprocal;
            }

            //Update Diagram
            ((WorkoutDiagramFragment) workoutDiagramFragment).segmentFlow(millisUntilFinished);

//            String mm = String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
//            String ss = String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

//            if (workoutTime == 0) {//上數
//                if ("00".equals(ss)) {
//                    //完成的百分比
//                    completedPresent++;
//                }
//            } else {//下數
//                //完成的百分比
//                double l = (((double) millisUntilFinished) / ((double) workoutTime)) * 100;
//                completedPresent = (long) Math.floor(l);
//            }

            if (workoutTime != 0) {
                //完成的百分比
                double l = (((double) millisUntilFinished) / ((double) workoutTime)) * 100;
                completedPresent = (long) Math.floor(l);
            }


            //計算時間
            xxTime = millisUntilFinished - xxCount;
            if (xxTime == (60 * 1000 * 100)) {
                xxCount += (60 * 1000 * 100);
                xxTime = 0;
            }

            if (HEART_RATE != PROGRAM_TYPE) {
                //除了HR，其他Programs 在DashBoard顯示時間 倒數or正數
                if (workoutTime == 0) {
                    ((WorkoutDashboardFragment) workoutDashboardFragment).setDashBoardTimer(xxTime);
                } else {
                    //倒數
                    ((WorkoutDashboardFragment) workoutDashboardFragment).setDashBoardTimer(timeCount);
                }
            }

            String mm1 = String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toMinutes(xxTime));
            String ss1 = String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toSeconds(xxTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(xxTime)));
            mElapsedTime = String.format("%s:%s", mm1, ss1);//正數

            String mm2 = String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toMinutes(timeCount));
            String ss2 = String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toSeconds(timeCount) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeCount)));
            mRemainTime = String.format("%s:%s", mm2, ss2);//倒數

//            if (HEART_RATE != PROGRAM_TYPE) {
//                //除了HR，其他Programs 在DashBoard顯示時間 倒數or正數
//                ((WorkoutDashboardFragment) workoutDashboardFragment).setDashBoardTimer(timeCount);
//            }
//            String mm2 = String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toMinutes(timeCount));
//            String ss2 = String.format(Locale.getDefault(), "%02d", TimeUnit.MILLISECONDS.toSeconds(timeCount) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeCount)));
//            mElapsedTime = String.format("%s:%s", mm, ss);//正數
//            mRemainTime = String.format("%s:%s", mm2, ss2);//倒數

            //計算
            setCalResult();

            //FTMS
            try {
                sendFTMSData();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //HR
            if (PROGRAM_TYPE == HEART_RATE) {
                updateHeartRate(timerTotalTimeSec, mCurrentHR);
            }

//            //換背景圖
//            if (timerTotalTimeSec % 60 == 0) {
//                showBackground();
//            }

            //倒數結束，離開Workout
            if (workoutTime != 0 && reciprocal <= 0) {
                runOnUiThread(() -> onWorkOutStop(false));
            }
        }
    }

    int[] drawableRs = new int[]{R.drawable.background_workout_3, R.drawable.bg_homescreen};
    int n = 0;

    /**
     * 換背景圖
     */
    private void showBackground() {

        try {
            GlideApp.with(this)
                    .load(drawableRs[n])
                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(clBaseView);

            n = n == 0 ? 1 : 0;
        } catch (Exception e) {
            n = 0;
            e.printStackTrace();
        }

//                DrawableCrossFadeFactory factory =
//                        new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();

//                GlideApp.with(this)
//                       // .asDrawable()
//                        .load(R.drawable.background_cast)
//                       // .transition(DrawableTransitionOptions.withCrossFade(1000))
//                       //  .transition(withCrossFade(factory))
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .into(new CustomTarget<Drawable>() {
//                            @Override
//                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                                clBaseView.setBackground(resource);
//                            }
//                            @Override
//                            public void onLoadCleared(@Nullable Drawable placeholder) { }
//                        });
    }

    /**
     * 初始化Calculation
     */
    private void initCalculation() {

        EllipticalCalculation.TYPE calcType = EllipticalCalculation.TYPE.EBCINC;
        DeviceInfoBean deviceInfoBean = getInstance().getDeviceInfoBean();
        String model = deviceInfoBean.getModel() != null ? deviceInfoBean.getModel() : "";
        switch (model) {
            case "ECB_AND_INC":
                calcType = EllipticalCalculation.TYPE.EBCINC;
                break;
            case "ECB":
                calcType = EllipticalCalculation.TYPE.ECB;
                break;
        }
        EllipticalCalculation.UNIT unit = mUnit == 0 ? EllipticalCalculation.UNIT.METRIC : EllipticalCalculation.UNIT.IMPERIAL;
        calculation = new EllipticalCalculation(unit, calcType, (double) mWeight);

        //   Log.d("@@@@@@@@", "initCalculation: " + Arrays.deepToString(new InitProduct(this).getWattTable()));
        Log.d("@@@@@@@@", "initCalculation: " + Arrays.deepToString(MODE.getWattTable()) + "," + deviceSettingBean.getMaxRpm() + "," + deviceSettingBean.getMinRpm() + "," + deviceSettingBean.getMaxLevel() + "," + deviceSettingBean.getMinLevel());
        calculation.changeWattTable(MODE.getWattTable(), deviceSettingBean.getMaxRpm(), deviceSettingBean.getMinRpm(), deviceSettingBean.getMaxLevel(), deviceSettingBean.getMinLevel());

    }

    String distanceAccmulate;
    int hrCount = 0;

    /**
     * 計算
     */
    private void setCalResult() {

        //WP 無線心跳
        //HP 手握
        //HR 優先權  : HRS --> WP --> HP
        // AVG HEART RATE
        // >240 < 40
        if (mCurrentHR > 40 && mCurrentHR < 240) {
            mTotalHR += mCurrentHR;
            hrCount++;
            mAvgHr = mTotalHR / hrCount;
        }

        //   Log.d("WorkoutDashboardFragmen", "setCalResult: " + );

        //dashboard1 -Speed unit:RPM
        tvDistance.setText(String.valueOf(mRPM));

        //dashboard2 -Calories / (Calories / Min)
        mCalories = CommonUtils.formatDecimal(calculation.getKcalAccumulate(), 0);
        mCaloriesAvg = CommonUtils.formatDecimal((calculation.getKcalHr() / 60), 0);
        tvCalories.setText(isCaloriesSwitch ? mCalories : mCaloriesAvg);

        //dashboard3 -Total Time / Distance / Time Left
        distanceAccmulate = CommonUtils.formatDecimal(calculation.getDistanceAccumulate(), 2);
        if (isThreeSwitch == 1) {
            //Distance
            tvTotalTime.setText(distanceAccmulate);
            tv_3_unit.setText(getUnit(DISTANCE));
        } else if (isThreeSwitch == 2) {
            //Time Left
            tvTotalTime.setText(workoutTime != 0 ? mRemainTime : "-:-");
            tv_3_unit.setText("");
        } else if (isThreeSwitch == 0) {
            //Total Time
            tvTotalTime.setText(mElapsedTime);
            tv_3_unit.setText("");
        }

//        if (workoutTime != 0) {
//            tvTotalTime.setText(isTimeSwitch ? mElapsedTime : mRemainTime);
//        } else {
//            tvTotalTime.setText(isTimeSwitch ? mElapsedTime : "  -:-");
//        }

        //dashboard4  -Current HR/ Avg HR
        tvHeartRate.setText(isHrSwitch ? String.valueOf(mCurrentHR) : String.valueOf(mAvgHr));

        //dashboard5  -POWER unit:W
        mWatt = String.valueOf(calculation.getWatt());
        tvPace.setText(mWatt);

        mWattAvg = calculation.getWattAverage();

        //dashboard6
        tvCompleted.setText(workoutTime == 0 ? "--" : String.valueOf(completedPresent));

        //Track跑速
        //   double trackSpeed = mUnit == 0 ? calculation.getPace() * 0.4 : calculation.getPace() * 0.25;
        //  updateTrackSpeed(Math.round(trackSpeed * 1000));
        double trackSpeed = mUnit == 0 ? calculation.getDistanceAccumulate() * 1000 : calculation.getDistanceAccumulate() * 5280;
        updateTrackSpeed(trackSpeed);

        //  Log.d("TRAAAAA", "updateLapSpeed: MS:" + Math.round(trackSpeed * 1000) + ",trackSpeed:" + trackSpeed + "," + mRPM);

        //傳給投影
        send2Mirror();

        //TrainingProcessData 每10秒存一次
        if (timerTotalTimeSec % 10 == 0) {

            String timeLeft = "0";
            //   String totalTime = "0";
//            if (workoutTime == 0) {
//                timeLeft = String.valueOf(((99 * 60 * 1000) - timeCount) / 1000);
//            } else {
//                timeLeft = String.valueOf(timeCount / 1000);
//                totalTime = String.valueOf(workoutTime / 1000);
//            }

            if (workoutTime != 0) {
                timeLeft = String.valueOf(timeCount / 1000);
                //  totalTime = String.valueOf(workoutTime / 1000);
            }

            //  Log.d("WEB_API", "setCalResult: " + timeLeft +","+ (workoutTime / 1000));

            TrainingProcessBean.SysResponseDataBean sysResponseDataBean = new TrainingProcessBean.SysResponseDataBean();
            sysResponseDataBean.setTotal_workout_time(String.valueOf(timerTotalTimeSec)); //string 運動進行時間[單位:秒]
            //  sysResponseDataBean.setTotal_workout_time(totalTime); //string 運動進行時間[單位:秒]
            sysResponseDataBean.setTotal_timeleft(timeLeft); //string 運動剩下時間[單位:秒]
            sysResponseDataBean.setNow_hr(mCurrentHR); //Double 目前心律**
            sysResponseDataBean.setTotal_distance(Double.parseDouble(distanceAccmulate)); //Double 目前跑步累計距離
            sysResponseDataBean.setNow_pace(String.valueOf(calculation.getPace())); //string 目前配速**
            sysResponseDataBean.setTotal_calorie(Double.parseDouble(mCalories)); //Double 目前累計消耗卡路里
            sysResponseDataBean.setNow_speed(calculation.getSpeedHr()); //Double 目前速度**
            sysResponseDataBean.setNow_incline(getCurrentIncline()); //Double 目前incline**
            sysResponseDataBean.setNow_level(getCurrentLevel()); //Double 目前等級 for elliptical
            sysResponseDataBean.setNow_watt((double) calculation.getWatt()); //Double 目前瓦特 for elliptical
            sysResponseDataBean.setTotal_min_spm(0); //Double 每分鐘划幾次 for rower
            sysResponseDataBean.setTotal_spm(0); //Double 總共划幾次 for rower
            sysResponseDataBean.setAvg_rpm(calculation.getRpmAverage()); //Double RPM for bike
            sysResponseDataBean.setTotal_floor(0); //Double 樓層數 for stepper
            sysResponseDataBean.setTotal_elevation(0); //Double 爬升高度 for stepper
            sysResponseDataBean.setTotal_steps(0); //Double 步數 for stepper
            sysResponseDataBean.setTotal_cur_spm(0); //Double 目前步速 for stepper
            sysResponseDataBean.setTotal_avg_spm(0); //Double 平均步速 for stepper

            workoutBean.getTrainingProcessBean().getSys_response_data().add(sysResponseDataBean);
        }
    }

    /**
     * 調整 WorkoutDiagramFragment 的 Level/Incline
     */
    public interface OnInclinePlusListener {
        void onInclineUpdate(int updateIncline, boolean beep);
    }

    private OnInclinePlusListener onInclinePlusListener;

    public void setOnInclinePlusListener(OnInclinePlusListener onInclinePlusListener) {
        this.onInclinePlusListener = onInclinePlusListener;
    }

    public interface OnLevelUpdateListener {
        void onLevelUpdate(int updateLevel, boolean beep);
    }

    private OnLevelUpdateListener onLevelUpdateListener;

    public void setOnLevelUpdateListener(OnLevelUpdateListener onLevelUpdateListener) {
        this.onLevelUpdateListener = onLevelUpdateListener;
    }

    /**
     * 更新 HeartRate
     *
     * @param timerTotalTimeSec 經過的秒數
     */
    private void updateHeartRate(long timerTotalTimeSec, int currentHR) {
        ((WorkoutDashboardFragment) workoutDashboardFragment).updateHeartRateStatus(timerTotalTimeSec, currentHR);
    }

    /**
     * 發送 0x80
     *
     * @param level   level 階
     * @param incline incline 階
     * @param beep    有無beep 可判斷 false為flow來的
     * @param from    0 全部,1 level, 2 incline
     */
    private void sendCommandSetControl(int level, int incline, boolean beep, int from) {

        try {
            if (level <= 0) level = 1;
            if (incline < 0) incline = 0;

            int levelAd = levelAdArray[level - 1];
            //  int inclineAd = incAdArray[incline - 1];
            int inclineAd = incAdArray[incline];

            Log.d("WORKOUT_發送SetControl", "發送SetControl: LevelAd" + levelAd + "," + "inclineAD" + inclineAd);
            //   Log.d("WORKOUT_發送SetControl", "發送SetControl: LevelAd" + levelAd + "," + levelAction +", InclineAD:"+inclineAd +","+ inclineAction);

            // if (!isLevelErrorShow)
            if (beep) { //不是flow來的

                if (!isLongClickIng) { //不是long click
                    getInstance().commandSetBuzzer(Device.BEEP.SHORT, 1);
                } else {
                    isLongBeep = true;
                    //  Log.d("WWWWWWW", "isLongBeep: " + isLongBeep);
                }

                if (isFtmsConnected && isFTMSNotify && from == 1)
                    getInstance().mFTMSManager.notifyTargetResistanceLevelChanged(level);
                if (isFtmsConnected && isFTMSNotify && from == 2)
                    getInstance().mFTMSManager.notifyTargetInclineChanged(incline * 5);
            }

            //回覆 FTMS
            if (isFtmsConnected) {
                responseFtmsUpdateLevel(true);
                responseFtmsUpdateIncline(true);
            }

            getInstance().commandSetControl(0, Device.ACTION_MODE.NORMAL, levelAd, Device.ACTION_MODE.NORMAL, inclineAd, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendFTMSData() {

        if (!isFtmsConnected) return;

        double d = mUnit == 1 ? CommonUtils.mi2km(calculation.getDistanceAccumulate()) : calculation.getDistanceAccumulate();
        int distance = (int) (d * 1000);

        double s = mUnit == 1 ? CommonUtils.mi2km(calculation.getSpeedHr()) : calculation.getSpeedHr();
        int speed = (int) (s * 100);

        int watt = calculation.getWatt();
        int kcal = (int) calculation.getKcalAccumulate();

        if (MODE == XE395ENT) {
            // 更新機器的即時速度到FTMS服務方法
            // 單位：0.01km/h, 實際的速度如果是 4km/h, 更新時要乘上100，speed = 4 * 100
            profileFTMS1.setFeatureValue(MachineFeature.INSTANTANEOUS_SPEED_PRESENT, speed);
            // 更新機器的總里程到FTMS服務方法
            // 單位：m, 實際的總里程如果是 5km, 更新的時要乘上1000, distance = 5 * 1000
            profileFTMS1.setFeatureValue(MachineFeature.TOTAL_DISTANCE_PRESENT, distance);
            // 更新機器的揚升資訊到FTMS服務方法
            // 單位：inclination: 0.1%, 實際揚升值如果是15，更新時要乘上10, inclination = 15 * 10
            //       rampAngle: 0.1°，實際角度如果是15，更新時要乘上10，rampAngle = 15 * 10, 目前不需要提供，填0即可。
            profileFTMS1.setFeatureValue(MachineFeature.INCLINATION_AND_RAMP_ANGLE_PRESENT, getCurrentIncline() * 5, 0); // INCLINATION_AND_RAMP_ANGLE 包含inclination, rampAngle
            // 更新機器的阻力資訊到FTMS服務方法
            // 單位：無
            profileFTMS1.setFeatureValue(MachineFeature.RESISTANCE_LEVEL_PRESENT, getCurrentLevel());
            // 更新機器的即時功率資訊到FTMS服務方法
            // 單位：瓦
            profileFTMS1.setFeatureValue(MachineFeature.INSTANTANEOUS_POWER_PRESENT, watt);

            profileFTMS1.setFeatureValue(MachineFeature.ELEVATION_GAIN_PRESENT, (int) calculation.getAltitude(), 0);
            profileFTMS1.setFeatureValue(MachineFeature.STRIDE_COUNT_PRESENT, mRpmCount);
            // 更新機器的步數計算到FTMS服務方法
            // 單位：step per minute: 無，avgStepRate: steps/min
            profileFTMS1.setFeatureValue(MachineFeature.STEP_COUNT_PRESENT, mRPM * 2, 0); // STEP_COUNT 包含兩個資料stepPerMinute, avgStepRate


            // 更新機器的消耗熱量資訊到FTMS服務方法
            // 單位：totalEnergy: 千卡，
            //       energyPerHour: 千卡，目前不需要提供
            //       energyPerMinute: 千卡，目前不需要提供
            profileFTMS1.setFeatureValue(MachineFeature.EXPENDED_ENERGY_PRESENT, kcal, 0, 0); // EXPENDED_ENERGY包含totalEnergy, energyPerHour, energyPerMinute

            // 更新機器的心跳資訊到FTMS服務方法
            // 單位：bpm
            profileFTMS1.setFeatureValue(MachineFeature.HEART_RATE_PRESENT, mCurrentHR);

            // 更新機器的經過時間到FTMS服務方法
            // 單位：秒
            profileFTMS1.setFeatureValue(MachineFeature.ELAPSED_TIME_PRESENT, (int) timerTotalTimeSec);
        }
        profileFTMS_IndoorBike.setFeatureValue(MachineFeature.INSTANTANEOUS_SPEED_PRESENT, speed);
        profileFTMS_IndoorBike.setFeatureValue(MachineFeature.TOTAL_DISTANCE_PRESENT, distance);
        profileFTMS_IndoorBike.setFeatureValue(MachineFeature.RESISTANCE_LEVEL_PRESENT, getCurrentLevel());
        profileFTMS_IndoorBike.setFeatureValue(MachineFeature.INSTANTANEOUS_POWER_PRESENT, watt);
        profileFTMS_IndoorBike.setFeatureValue(MachineFeature.EXPENDED_ENERGY_PRESENT, kcal, 0, 0); // EXPENDED_ENERGY包含totalEnergy, energyPerHour, energyPerMinute
        profileFTMS_IndoorBike.setFeatureValue(MachineFeature.HEART_RATE_PRESENT, mCurrentHR);
        profileFTMS_IndoorBike.setFeatureValue(MachineFeature.ELAPSED_TIME_PRESENT, (int) timerTotalTimeSec);
        profileFTMS_IndoorBike.setFeatureValue(MachineFeature.INSTANTANEOUS_CADENCE_PRESENT, mRPM * 2);
        profileFTMS_IndoorBike.setFeatureValue(MachineFeature.AVERAGE_CADENCE_PRESENT, (int) calculation.getRpmAverage() * 2);
        profileFTMS_IndoorBike.setFeatureValue(MachineFeature.AVERAGE_POWER_PRESENT, (int) mWattAvg);
    }

    /**
     * 接收各種通知
     */
    private void receiverNotify() {

        EventBus.getDefault().register(this);

        Disposable d = RxBus.getInstance().toObservable(MsgEvent.class).subscribe(msg -> {
            if (msg.getType() == WIFI_EVENT) {
                int img = CommonUtils.setWifiImage((int) msg.getObj(), true);
                runOnUiThread(() -> iv_wifi.setImageResource(img));
                return;
            }

            if (msg.getType() == ON_ERROR) {
                runOnUiThread(() -> {
                    tvErrorCurrentLevel.setVisibility(View.VISIBLE);

                    if (MODE == XE395ENT)
                        tvErrorCurrentIncline.setVisibility(View.VISIBLE);

                    tvInclineCurrent.setVisibility(View.INVISIBLE);
                    tvLevelCurrent.setVisibility(View.INVISIBLE);

                    btn_incline_plus.setClickable(false);
                    btn_incline_minus.setClickable(false);

                    btn_level_plus.setClickable(false);
                    btn_level_minus.setClickable(false);


                    btn_level_minus.setEnabled(false);
                    btn_level_plus.setEnabled(false);
                    btn_incline_plus.setEnabled(false);
                    btn_incline_minus.setEnabled(false);

                    isLevelErrorShow = true;
                    isInclineErrorShow = true;
                });


                showErrorDialogAlert((CommandErrorBean) msg.getObj(), "ERROR");
            }

            //時鐘
            if (msg.getType() == TIME_EVENT) {
                runOnUiThread(() -> tv_workout_time.setText(msg.getObj().toString()));
                return;
            }

            if (msg.getType() == ON_BLE_DEVICE_DISCONNECTED) {
                runOnUiThread(() -> {
                    mHRS = 0;
                    tvHeartRate.setText(String.valueOf(mHRS));
                    Log.d("心跳設備", "斷線通知: ");
                });
                return;
            }

            //SOUND CONNECT
            if (msg.getType() == BT_SOUND_CONNECT) {
                runOnUiThread(this::showSoundConnectedIcon);
                return;
            }

            if (msg.getType() == MIRRORING_GO_HOME_SCREEN) {
                mirroringBack(MIRRORING_GO_HOME_SCREEN);
                return;
            }

            if (msg.getType() == MIRRORING_GO_PROGRAMS) {
                mirroringBack(MIRRORING_GO_PROGRAMS);
                return;
            }

            if (msg.getType() == MIRRORING_GO_PROFILE) {
                mirroringBack(MIRRORING_GO_PROFILE);
                return;
            }

            if (msg.getType() == MIRRORING_GO_WEB_VIEW) {
                mirroringBack(MIRRORING_GO_WEB_VIEW);
                return;
            }

            if (msg.getType() == VIDEO_BACK_HOME) {
                mirroringBack(MIRRORING_GO_WEB_VIEW);
                return;
            }

            if (msg.getType() == MIRRORING_GO_CAST) {
                mirroringBack(MIRRORING_GO_CAST);
                return;
            }

            if (msg.getType() == MIRRORING_STOP_WORKOUT) {
                mirroringBack(MIRRORING_STOP_WORKOUT);
                RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP, true));

                //   runOnUiThread(() -> onWorkOutStop(true));
                return;
            }

            if (msg.getType() == MIRRORING_BTN_LEVEL_MINUS) {
                btn_level_minus.callOnClick();
                return;
            }

            if (msg.getType() == MIRRORING_BTN_LEVEL_PLUS) {
                btn_level_plus.callOnClick();
                return;
            }

            if (msg.getType() == MIRRORING_BTN_INCLINE_MINUS) {
                btn_incline_minus.callOnClick();
                return;
            }

            if (msg.getType() == MIRRORING_BTN_INCLINE_PLUS) {
                btn_incline_plus.callOnClick();
                return;
            }

//            //
//            if (msg.getType() == MIRRORING_SWITCH_DASHBOARD_1) {
//                parent.ivSwitchDistance.callOnClick();
//            }

            if (msg.getType() == MIRRORING_SWITCH_DASHBOARD_2) {
                //  ivSwitchCalories.callOnClick();
                switch_2.callOnClick();
                return;
            }

            if (msg.getType() == MIRRORING_SWITCH_DASHBOARD_3) {
                //  ivSwitchTime.callOnClick();
                switch_3.callOnClick();
                return;
            }

            if (msg.getType() == MIRRORING_SWITCH_DASHBOARD_4) {
                //  ivSwitchHR.callOnClick();
                switch_4.callOnClick();
                return;
            }

//            if (msg.getType() == MIRRORING_SWITCH_DASHBOARD_5) {
//                parent.ivSwitchSpeed.callOnClick();
//            }

            if (msg.getType() == MIRRORING_BTN_AUTO_DOWN) {
                ImageButton imageButton;
                if ((int) msg.getObj() == 1) {
                    imageButton = btn_level_plus;
                } else if ((int) msg.getObj() == 2) {
                    imageButton = btn_level_minus;
                } else if ((int) msg.getObj() == 3) {
                    imageButton = btn_incline_plus;
                } else {
                    imageButton = btn_incline_minus;
                }
                longDown(imageButton);
                return;
            }

            if (msg.getType() == MIRRORING_BTN_AUTO_UP) {
                longUp();
                return;
            }

            if (msg.getType() == FTMS_NOTIFY_TRAINING_STATUS) {
                TrainingStatus trainingStatus;
                if (PROGRAM_TYPE == MANUAL) {
                    trainingStatus = TrainingStatus.MANUAL_MODE;
                } else {
                    trainingStatus = TrainingStatus.FITNESS_TEST;
                }
                try {
                    getInstance().mFTMSManager.notifyTrainingStatus(trainingStatus);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return;
            }

            //顯示離開全螢幕的浮動按鈕
            if (msg.getType() == MIRRORING_EXIT_FULL_SCREEN) {
                //  if (!parent.isMirrorShow) return;
                runOnUiThread(() -> {
                    int type = (int) msg.getObj();
                    FloatingWorkoutDashboardService.isStarted = false;
                    stopService(new Intent(getApplicationContext(), FloatingWorkoutDashboardService.class));
                    Log.d("fufufu", "@@@@@@111111receiverNotify: ");
                    showBtnFullScreenExit(type);
                });

                return;
            }

            if (msg.getType() == REMOVE_BUTTON) {
                CommonUtils.closePackage(this);
                removeFloatView();
            }

        });
        compositeDisposable.add(d);
    }

    private void setUnitText() {

        tv_3_unit.setText(getUnit(DISTANCE));
        //   tv_pace_unit.setText(String.format("%s", getUnit(SPEED)));
    }

    private void showSoundConnectedIcon() {
//        //音訊連結圖示
//        try {
//            if (isSoundConnected) {
//                if (iv_sound_connected != null) iv_sound_connected.setVisibility(View.VISIBLE);
//            } else {
//                if (iv_sound_connected != null) iv_sound_connected.setVisibility(View.INVISIBLE);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 更新Track跑速
     *
     * @param milliSecond 毫秒
     */
    //   public void updateTrackSpeed(long milliSecond) {
    public void updateTrackSpeed(double milliSecond) {
//        if (milliSecond == 0 || mRPM == 0) {
//            milliSecond = 999999;
//        }
        ((WorkoutTrackFragment) workoutTrackFragment).updateLapSpeed(milliSecond, mUnit);
    }

    private Dialog errorDialog;

    public void showErrorDialogAlert(CommandErrorBean commandErrorBean, String errorMsg) {
        runOnUiThread(() -> {
            if (errorDialog == null || !errorDialog.isShowing()) {
                errorDialog = new Dialog(WorkoutDashboardActivity.this, android.R.style.ThemeOverlay);
                errorDialog.setCanceledOnTouchOutside(false);
                errorDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                errorDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                errorDialog.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                errorDialog.setContentView(R.layout.dialog_error_alert);
                TextView textView = errorDialog.findViewById(R.id.tv_ShowText);
                TextView textView2 = errorDialog.findViewById(R.id.tv_ShowText2);
                ImageView btn_close = errorDialog.findViewById(R.id.btn_close);
                Button btn_support = errorDialog.findViewById(R.id.btn_support);
                errorDialog.show();
                errorDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                btn_close.setOnClickListener(v -> errorDialog.dismiss());
                btn_support.setOnClickListener(v -> errorDialog.dismiss());

                if (commandErrorBean.getErrorType() == 1) {
                    textView.setText(errorMsg);
                    textView2.setText(commandErrorBean.getErrorMessage());
                } else {
                    // Mode Error command error
                    textView.setText(commandErrorBean.getCommand().toString());
                    textView2.setText(commandErrorBean.getCommandError().toString());
                }
            }
        });
    }


    private View hrHintView;
    private WindowManager hrHintDialogWindow;
    private WindowManager.LayoutParams hrHintDialogLayoutParams;
    public int hrMsgType = -1;
    public String hrNo = "";

    /**
     * @param type 0: No heart rate detected Connect HR device or finish the program   (finish)
     *             1: Your heart rate is too high. Program is ended (Got it,red)
     *             2: Your heart rate is not high enough!Increasing level…(Got it,w)
     *             3: Your heart rate is too high!Decreasing level…(Got it,w)
     */
    public void showHrDialogAlert(int type, String no) {

        if (!isWorkoutTimerRunning) return;

        Log.d("HRRRRRR", "@@@@@@@@@@showDialogAlert: " + type + "," + hrMsgType + "," + no + "," + hrNo);
        //1強制結束
        if (hrMsgType == 1) return;

        if (hrMsgType == 0) return;

        if (hrMsgType == type && no.equals(hrNo)) return;

        hrNo = no;
        hrMsgType = type;


//        runOnUiThread(() -> {
//            if (dialog != null) {
//                dialog.dismiss();
//                dialog = null;
//            }
//
//            String text;
//            String btText;
//
//            switch (type) {
//                case 0:
//                    text = "No heart rate detected. \nConnect HR device or finish the program.";
//                    btText = "FINISH";
//                    break;
//                case 1:
//                    text = "Your heart rate is too high. \nProgram is ended";
//                    btText = "GOT IT";
//                    break;
//                case 2:
//                    text = getString(R.string.hr_hint_not_high);
//                    btText = "GOT IT";
//                    break;
//                case 3:
//                    text = getString(R.string.hr_hint_too_high);
//                    btText = "GOT IT";
//                    break;
//                default:
//                    text = "";
//                    btText = "";
//
//            }
//
//         //   if (dialog == null || !dialog.isShowing()) {
//                dialog = new Dialog(WorkoutDashboardActivity.this, android.R.style.ThemeOverlay);
//                dialog.setCanceledOnTouchOutside(false);
//                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                dialog.getWindow().getDecorView().setSystemUiVisibility(
//                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//                dialog.setContentView(R.layout.dialog_workout_alert);
//                TextView textView = dialog.findViewById(R.id.tv_ShowText);
//                TextView nummm = dialog.findViewById(R.id.nummm);
//                Button button = dialog.findViewById(R.id.btn_bt);
//                dialog.show();
//                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//                button.setOnClickListener(v -> {
//                    dialog.dismiss();
//                    if (type == 0 || type == 1) {
//                        onWorkOutStop(false);
//                    }
//                });
//                button.setText(btText);
//                textView.setText(text);
//                if (type == 2 || type == 3) {
//                    button.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.btn_rrect_e6e6e6_184));
//                    button.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color597084));
//                    nummm.setText(no);
//                }
//         //   }
//        });


        runOnUiThread(() -> {

            try {
                if (hrHintDialogWindow != null && hrHintView != null) {
                    hrHintDialogWindow.removeView(hrHintView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            hrHintDialogWindow = (WindowManager) getSystemService(WINDOW_SERVICE);
            hrHintDialogLayoutParams = new WindowManager.LayoutParams();
            hrHintDialogLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            hrHintDialogLayoutParams.format = PixelFormat.RGBA_8888;
            hrHintDialogLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            hrHintDialogLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            hrHintDialogLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

            hrHintView = LayoutInflater.from(this).inflate(R.layout.dialog_workout_alert, null);

            hrHintDialogWindow.addView(hrHintView, hrHintDialogLayoutParams);
            TextView textView = hrHintView.findViewById(R.id.tv_ShowText);
            TextView nummm = hrHintView.findViewById(R.id.nummm);
            Button button = hrHintView.findViewById(R.id.btn_bt);
            button.setOnClickListener(v -> {
                try {
                    hrHintDialogWindow.removeView(hrHintView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (type == 0 || type == 1) {
                    onWorkOutStop(false);
                }
            });

            String text;
            String btText;

            switch (type) {
                case 0:
                    text = "No heart rate detected. \nConnect HR device or finish the program.";
                    btText = "FINISH";
                    break;
                case 1:
                    text = "Your heart rate is too high. \nProgram is ended";
                    btText = "GOT IT";
                    isWorkoutTimerRunning = false;
                    button.setVisibility(View.INVISIBLE);
                    new RxTimer().timer(3000, n -> {
                        try {
                            onWorkOutStop(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    break;
                case 2:
                    text = getString(R.string.hr_hint_not_high);
                    btText = "GOT IT";
                    button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rrect_e6e6e6_184));
                    button.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color597084));
                    nummm.setText(no);
                    break;
                case 3:
                    text = getString(R.string.hr_hint_too_high);
                    btText = "GOT IT";
                    button.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.btn_rrect_e6e6e6_184));
                    button.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color597084));
                    nummm.setText(no);
                    break;
                default:
                    text = "";
                    btText = "";

            }
            button.setText(btText);
            textView.setText(text);
        });
    }


    /**
     * 心跳已出現，清除警告視窗
     */
    public void dismissDialog() {
        try {
            if (hrMsgType == 0) {
                Log.d("HRRRRRR", "@@@@@@@@@@dismissDialog: ");
                if (hrHintDialogWindow != null && hrHintView != null) {
                    hrHintDialogWindow.removeViewImmediate(hrHintView);
                    hrHintDialogWindow = null;
                    hrHintView = null;
                }
                hrMsgType = -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showTypeError() {
        Dialog errorDialog2 = new Dialog(WorkoutDashboardActivity.this, android.R.style.ThemeOverlay);
        errorDialog2.setCanceledOnTouchOutside(false);
        errorDialog2.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        errorDialog2.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        errorDialog2.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        errorDialog2.setContentView(R.layout.dialog_error_alert);
        TextView textView = errorDialog2.findViewById(R.id.tv_ShowText);
        TextView textView2 = errorDialog2.findViewById(R.id.tv_ShowText2);
        ImageView btn_close = errorDialog2.findViewById(R.id.btn_close);
        Button btn_support = errorDialog2.findViewById(R.id.btn_support);
        errorDialog2.show();
        errorDialog2.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        btn_close.setVisibility(View.INVISIBLE);
        btn_support.setText("DONE");
        btn_support.setOnClickListener(v -> {
            errorDialog2.dismiss();


            Intent intent = new Intent(this, DashboardActivity.class);
            Bundle bundle = new Bundle();
            intent.putExtras(bundle);
            startActivity(intent);
            finishAffinity();
        });

        textView.setText("LWR TYPE ERROR");
        textView2.setText("");

    }

    public int mCurrentHR = 0;
    public int mRPM = 0;
    public int mHP = 0;
    public int mWP = 0;
    public int mHRS = 0;
    public int mRpmCount = 0;

    /**
     * EventBus Receiver
     *
     * @param isBusEvent EventEntity
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommandEvent(isBusEvent isBusEvent) {
        if (startDialog != null && startDialog.isShowing()) return;
        switch (isBusEvent.getEventType()) {
            case COMMAND_SET_CONTROL:

                mRPM = ((McuBean) isBusEvent).getRpm();
                mWP = ((McuBean) isBusEvent).getWp();
                mHP = ((McuBean) isBusEvent).getHp();
                mRpmCount = ((McuBean) isBusEvent).getRpmCount();
                //HR 優先權  : HRS --> WP(無線心跳) --> HP(手握)

                if (mHRS <= 0) {
                    if (mWP > 0) {
                        mCurrentHR = mWP;
                    } else if (mHP > 0) {
                        mCurrentHR = mHP;
                    } else {
                        mCurrentHR = 0;
                    }
                }

                if (((McuBean) isBusEvent).getErrors().size() == 0) return;
                levelError = ((McuBean) isBusEvent).getErrors().contains(Device.MCU_ERROR.RES);
                inclineError = ((McuBean) isBusEvent).getErrors().contains(Device.MCU_ERROR.INC);

                String errorMsg1 = ((McuBean) isBusEvent).getErrors().toString();
                String errorMsg2 = "ERROR" + (levelError ? " E2" : "") + (inclineError ? " E3" : "");

                runOnUiThread(() -> {
                    //if (!isLevelErrorShow) {
                    if (levelError) {
                        tvErrorCurrentLevel.setVisibility(View.VISIBLE);
                        tvLevelCurrent.setVisibility(View.INVISIBLE);

//                        btn_level_plus.setClickable(false);
//                        btn_level_minus.setClickable(false);

                        btn_level_plus.setEnabled(false);
                        btn_level_minus.setEnabled(false);


                        CommandErrorBean commandErrorBean = new CommandErrorBean();
                        commandErrorBean.setErrorType(1);
                        commandErrorBean.setErrorMessage(errorMsg1);
                        if (!isLevelErrorShow) {
                            isLevelErrorShow = true;
                            showErrorDialogAlert(commandErrorBean, errorMsg2);
                        }
                        //       }
                    }

                    // if (!isInclineErrorShow) {

                    if (inclineError) {
                        if (MODE == XE395ENT)
                            tvErrorCurrentIncline.setVisibility(View.VISIBLE);
                        tvInclineCurrent.setVisibility(View.INVISIBLE);

//                        btn_incline_plus.setClickable(false);
//                        btn_incline_minus.setClickable(false);

                        btn_incline_plus.setEnabled(false);
                        btn_incline_minus.setEnabled(false);

                        CommandErrorBean commandErrorBean = new CommandErrorBean();
                        commandErrorBean.setErrorType(1);
                        commandErrorBean.setErrorMessage(errorMsg1);
                        if (!isInclineErrorShow) {
                            isInclineErrorShow = true;
                            showErrorDialogAlert(commandErrorBean, errorMsg2);
                        }
                    }
                    //     }
                });

                //e2 res //e3 inc [RES, INC]
                //   Log.d("UART_CONN_EVENT_BUS", "COMMAND_SET_CONTROL: " + isBusEvent.toString() + ",@@@RPM:" + mRPM);
                break;
            case ON_HEART_RATE_CHANGED:
                if (isBusEvent instanceof BleHrBean) {
                    //    mCurrentHR = ((BleHrBean) isBusEvent).getHr();
                    mHRS = ((BleHrBean) isBusEvent).getHr();
                    mCurrentHR = mHRS;

                    //    Log.d("心跳設備", "ON_HEART_RATE_CHANGED: " + "," + mHRS + "," + mCurrentHR);
                }
                break;

            case FTMS_NOTIFY:

                switch (((FTMSBean) isBusEvent).getFtmsNotifyType()) {

                    case FTMS_NOTIFY_SET_INCLINE:
                        if (MODE == XE395ENT) {
                            int i = ((FTMSBean) isBusEvent).getSetIncline() / 5;
                            int uIncline = (new BigDecimal(getCurrentIncline() - i).negate()).intValue();
                            isFtmsUpdateIncline = true;
                            callUpdateIncline(uIncline, true);
                            Log.d("FTMSSS", "WORKOUT。callUpdateIncline:" + uIncline);
                        }

                        break;
                    case FTMS_NOTIFY_SET_LEVEL:
                        int l = ((FTMSBean) isBusEvent).getSetLevel() / 10;
                        int uLevel = (new BigDecimal(getCurrentLevel() - l).negate()).intValue();
                        isFtmsUpdateLevel = true;
                        callUpdateLevel(uLevel, true);
                        Log.d("FTMSSS", "WORKOUT。callUpdateLevel:" + uLevel);
                        break;
                    case FTMS_NOTIFY_START_OR_RESUME:
                        break;
                    case FTMS_NOTIFY_STOP_OR_PAUSE:
                        if (!getInstance().checkPauseRunning()) {
                            //  if (startDialog != null && startDialog.isShowing()) return;
                            if (isFastClick()) return;

                            CommonUtils.closePackage(this);
                            //FTMS 暫停 WorkOut
                            onWorkOutStop(hrMsgType != 1);
                            getInstance().commandSetBuzzer(Device.BEEP.SHORT, 1);
                        }
//                        if (!getInstance().checkPauseRunning()) {
//                            if (((FTMSBean) isBusEvent).getStopOrPause() == 1) { //stop
//
//                                if (!isKeyStopWorkout) {
//                                    if (startDialog != null && startDialog.isShowing()) return;
//                                    onWorkOutStop(false);
//                                    isKeyStopWorkout = true;
//                                }
//
//                                // onWorkOutStop(false);
//                            } else if (((FTMSBean) isBusEvent).getStopOrPause() == 2) { //pause
//
//                                if (startDialog != null && startDialog.isShowing()) return;
//                                if (isFastClick()) return;
//                                onWorkOutStop(true);
//                                //    getInstance().mFTMSManager.responseFitnessMachineControl(FitnessMachineControlOperation.START_OR_RESUME, FitnessMachineControlResponse.SUCCESS);
//                            }
//                        }
                        break;
                }
                break;

            case COMMAND_KEY:
                KeyBean keyBean = ((KeyBean) isBusEvent);

                if (keyBean.isPause()) return;

                //單按鍵
                if (keyBean.getKeyStatus() == 0) {
                    switch (keyBean.getKey()) {
                        case KEY_UNKNOWN: //autokey取消
                            longUp();
                            break;
                        case KEY01://FAN
                            //FAN
                            btn_fan.callOnClick();

                            break;
                        case KEY03://PAUSE/STOP
                            if (!isKeyPauseWorkout) {

                                //   if (startDialog != null && startDialog.isShowing()) return;

                                CommonUtils.closePackage(this);
                                //實體按鍵 暫停Workout
                                onWorkOutStop(hrMsgType != 1);
                                isKeyPauseWorkout = true;
                                getInstance().commandSetBuzzer(Device.BEEP.SHORT, 1);

                            }
                            break;
                        case KEY02://LEVEL DOWN
                            commandButton(btn_level_minus);
                            break;
                        case KEY11: //扶手
                            commandButton(MODE == XE395ENT ? btn_level_minus : btn_level_plus);

                            break;
                        case KEY06://LEVEL UP
                            commandButton(btn_level_plus);
                            break;
                        case KEY10: //扶手
                            commandButton(MODE == XE395ENT ? btn_level_plus : btn_level_minus);
                            break;
                        case KEY08://INCLINE UP
                            if (MODE == XE395ENT) commandButton(btn_incline_plus);

                            break;
                        case KEY09://INCLINE DOWN
                            if (MODE == XE395ENT) commandButton(btn_incline_minus);
                            break;
                    }
                } else {
                    //組合
                    if (isLongClickIng) return;

                    if (keyBean.getKeys().contains(Device.KEY.KEY11)) {
                        //扶手 run key
                        if (MODE == XE395ENT) {
                            longDown(btn_level_plus);
                        } else {
                            longDown(btn_level_minus);
                        }

                    } else if (keyBean.getKeys().contains(Device.KEY.KEY12)) {
                        //扶手 run key
                        if (MODE == XE395ENT) {
                            longDown(btn_level_minus);
                        } else {
                            longDown(btn_level_plus);
                        }
                    } else if (keyBean.getKeys().contains(Device.KEY.KEY07)) {
                        longDown(btn_level_plus);
                    } else if (keyBean.getKeys().contains(Device.KEY.KEY03)) {
                        longDown(btn_level_minus);

//                    if (keyBean.getKeys().contains(Device.KEY.KEY11) || keyBean.getKeys().contains(Device.KEY.KEY07)) {
//                        //LEVEL auto +
//                        // btn_level_plus.performClick();
//                        //   Log.d("KKKKKK", "long: ");
//                        longDown(btn_level_plus);
//
//                    } else if (keyBean.getKeys().contains(Device.KEY.KEY12) || keyBean.getKeys().contains(Device.KEY.KEY03)) {
//                        //LEVEL auto -
//                        // btn_level_minus.callOnClick();
//                        longDown(btn_level_minus);

                    } else if (keyBean.getKeys().contains(Device.KEY.KEY09)) {
                        //INC auto +
                        //  btn_incline_plus.callOnClick();
                        longDown(btn_incline_plus);
                    } else if (keyBean.getKeys().contains(Device.KEY.KEY10)) {
                        //INC auto -
                        //  btn_incline_minus.callOnClick();
                        longDown(btn_incline_minus);
                    }
                }

                break;
        }
    }

    private boolean isKeyStopWorkout = false;
    private boolean isKeyPauseWorkout = false;

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MMKV.defaultMMKV().encode("WorkoutDashboardActivity", true);

        try {
            iv_wifi.setImageResource(CommonUtils.setWifiImage(new CommonUtils().getWifiLevel(this), true));
        } catch (Exception e) {
            e.printStackTrace();
        }

        initFan();


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        onWorkoutStopIng = false;


        removeFloatView();

        try {
            floatingWidget.removeFloatingView();
        } catch (Exception e) {
            e.printStackTrace();
        }

        isWorkoutInBackground = false;
    }

    public boolean isWorkoutInBackground;

    @Override
    protected void onPause() {
        super.onPause();

        if (floatLevelTimer != null) {
            floatLevelTimer.cancel();
            floatLevelTimer = null;
        }

        if (floatInclineTimer != null) {
            floatInclineTimer.cancel();
            floatInclineTimer = null;
        }

        isWebViewFull = false;

        if (MyApplication.SSEB) {
            showBtnFullScreenExit(4);
        }
        isWorkoutInBackground = true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (floatLevelTimer != null) {
            floatLevelTimer.cancel();
            floatLevelTimer = null;
        }

        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
        if (rxTimer != null) {
            rxTimer.cancel();
            rxTimer = null;
        }

        if (longTimer != null) {
            longTimer.cancel();
            longTimer = null;
        }

        if (errorDialog != null) {
            errorDialog.dismiss();
            errorDialog = null;
        }
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

        isWebViewFull = false;

        EventBus.getDefault().unregister(this);
        MMKV.defaultMMKV().encode("WorkoutDashboardActivity", false);

        removeFloatView();

        stopService(new Intent(this, FloatingSoundSettingService.class));

        try {
            floatingWidget.removeFloatingView();
            floatingWidget = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Dialog startDialog;

    /**
     *
     */
    public void showStartDialog() {
        startDialog = new Dialog(this, R.style.DialogProgressTheme);
        startDialog.setCanceledOnTouchOutside(false);
        startDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        startDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startDialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        startDialog.setContentView(R.layout.dialog_progress);
        startDialog.show();
        startDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    DashboardDataBean dashboardDataBean = new DashboardDataBean();

    private void send2Mirror() {

        if (FloatingWorkoutDashboardService.isStarted) {

            dashboardDataBean.setDashboard1(tvDistance.getText().toString());
            //  dashboardDataBean.setDashboard1_unit2(tv_total_distance_unit.getText().toString());

            dashboardDataBean.setDashboard2(tvCalories.getText().toString());
            dashboardDataBean.setDashboard2_unit1(tv_calories_watt_text.getText().toString());
            //   dashboardDataBean.setDashboard2_unit2(tv_unit_kcal.getText().toString());

            dashboardDataBean.setDashboard3(tvTotalTime.getText().toString());
            dashboardDataBean.setDashboard3_unit1(tv_Etime_Ltime_text.getText().toString());
            dashboardDataBean.setDashboard3_unit2(tv_3_unit.getText().toString());

            dashboardDataBean.setDashboard4(tvHeartRate.getText().toString());
            dashboardDataBean.setDashboard4_unit1(tv_CHr_AHR_text.getText().toString());
            //   dashboardDataBean.setDashboard4_unit2(tv_hr_unit.getText().toString());

            dashboardDataBean.setDashboard5(tvPace.getText().toString());
            //    dashboardDataBean.setDashboard5_unit1(tv_Speed_Rpm_text.getText().toString());
            //    dashboardDataBean.setDashboard5_unit2(tv_pace_unit.getText().toString());

            dashboardDataBean.setDashboard6(tvCompleted.getText().toString());

            RxBus.getInstance().post(new MsgEvent(MIRRORING_DASHBOARD_DATA, dashboardDataBean));
        }
    }

    public void responseFtmsUpdateLevel(boolean isOk) {
        if (isFtmsUpdateLevel) { //判斷是否為FTMS來的
            if (isOk) {
                getInstance().mFTMSManager.responseFitnessMachineControl(FitnessMachineControlOperation.SET_TARGET_RESISTANCE_LEVEL, FitnessMachineControlResponse.SUCCESS);
                getInstance().mFTMSManager.responseFitnessMachineControl(FitnessMachineControlOperation.SET_INDOOR_BIKE_SIMULATION_PARAMETERS, FitnessMachineControlResponse.SUCCESS);
            } else {
                getInstance().mFTMSManager.responseFitnessMachineControl(FitnessMachineControlOperation.SET_TARGET_RESISTANCE_LEVEL, FitnessMachineControlResponse.INVALID_PARAMETER);
                getInstance().mFTMSManager.responseFitnessMachineControl(FitnessMachineControlOperation.SET_INDOOR_BIKE_SIMULATION_PARAMETERS, FitnessMachineControlResponse.INVALID_PARAMETER);

            }
            isFtmsUpdateLevel = false;
        }
    }

    public void responseFtmsUpdateIncline(boolean isOk) {
        if (isFtmsUpdateIncline) { //判斷是否為FTMS來的
            if (isOk) {
                getInstance().mFTMSManager.responseFitnessMachineControl(FitnessMachineControlOperation.SET_TARGET_INCLINATION, FitnessMachineControlResponse.SUCCESS);
            } else {
                getInstance().mFTMSManager.responseFitnessMachineControl(FitnessMachineControlOperation.SET_TARGET_INCLINATION, FitnessMachineControlResponse.INVALID_PARAMETER);
            }
            isFtmsUpdateIncline = false;
        }
    }

    private void initMode() {
        if (MODE != XE395ENT) group_incline.setVisibility(View.INVISIBLE);
    }

    private void mirroringBack(int go) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = new Bundle();
        bundle.putInt("GO", go);
        intent.putExtras(bundle);

        intent.setClass(this, WorkoutDashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(0, 0);
        if (intent.getExtras() != null) {

            int go = intent.getExtras().getInt("GO", 0);
            if (go <= 0) return;
            switch (go) {
                case MIRRORING_GO_HOME_SCREEN:
                    rb_dashboard_workout.setChecked(true);
                    showFragment(workoutDashboardFragment);
                    break;
                case MIRRORING_GO_PROGRAMS:
                    rb_diagram_workout.setChecked(true);
                    showFragment(workoutDiagramFragment);
                    break;
                case MIRRORING_GO_PROFILE:
                    rb_track_workout.setChecked(true);
                    showFragment(workoutTrackFragment);
                    break;
                case MIRRORING_GO_WEB_VIEW:
                    rb_internet_workout.setChecked(true);
                    showFragment(internetFragment);
                    break;
                case MIRRORING_GO_CAST:
                    rb_cast_workout.setChecked(true);
                    showFragment(mirroringFragment);
                    break;
                case MIRRORING_STOP_WORKOUT:
                    onWorkOutStop(true);
                    break;
            }

            RxBus.getInstance().post(new MsgEvent(STOP_FLOATING_DASHBOARD, true));
        }
    }


    RxTimer longTimer;
    boolean isLongClickIng = false;
    boolean isLongBeep = false;

    @SuppressLint("ClickableViewAccessibility")
    public void addAutoClick(ImageButton button) {
        button.setOnLongClickListener(v -> {
            longDown(button);
            return true;
        });

        button.setOnTouchListener((v, event) -> {
            if (iButton != null) {
                if (event.getAction() == ACTION_UP) {
                    longUp();
                }
            }
            return false;
        });
    }

    ImageButton iButton;

    private void longDown(ImageButton button) {
        Log.d(">>>>>>>>", "longDown: ");
        if (longTimer != null) longTimer.cancel();
        isLongClickIng = true;
        isLongBeep = false;
        iButton = button;
        //  button.setPressed(true);
        longTimer = new RxTimer();
        longTimer.interval3(200, n -> button.callOnClick());
    }

    private void longUp() {
        if (longTimer != null) longTimer.cancel();
        if (isLongBeep) getInstance().commandSetBuzzer(Device.BEEP.SHORT, 1);
        if (iButton != null) iButton.setPressed(false);
        iButton = null;
        isLongClickIng = false;
        isLongBeep = false;
        Log.d(">>>>>>>>", "longUp: ");
    }

    private void commandButton(ImageButton imageButton) {
        imageButton.performClick();
        //   imageButton.setPressed(true);
        //   new RxTimer().timer(100, n -> imageButton.setPressed(false));
    }

    private Button btnFullScreenExit;
    private WindowManager windowManager_exitButton;
    private WindowManager.LayoutParams layoutParams;
    private boolean floatButtonIsStarted;

    @SuppressLint("ClickableViewAccessibility")
    public void showBtnFullScreenExit(int type) {

//        if (onWorkoutStopIng) {
//            onWorkoutStopIng = false;
//            return;
//        }

        if (floatButtonIsStarted) return;

        FloatingWorkoutDashboardService.isStarted = false;
        stopService(new Intent(getApplicationContext(), FloatingWorkoutDashboardService.class));

        floatButtonIsStarted = true;
        windowManager_exitButton = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 114;
        layoutParams.height = 114;
        layoutParams.x = 1000;
        layoutParams.y = 0;
        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;



        btnFullScreenExit = new Button(getApplicationContext());
        btnFullScreenExit.setAlpha(0.9f);
        btnFullScreenExit.setBackgroundResource(R.drawable.btn_icon_inversion_screenfull_exit);
        windowManager_exitButton.addView(btnFullScreenExit, layoutParams);

        // btnFullScreenExit.setOnTouchListener(new FloatingOnTouchListener());
        btnFullScreenExit.setOnTouchListener(new FloatingOnTouchListener(windowManager_exitButton, layoutParams));

        btnFullScreenExit.setOnClickListener(v -> {

            if (CommonUtils.isFastClick()) return;

            startFloatingDashboard(type);
        });

        levelFloat();
        inclineFloat();
    }

    private ConstraintLayout levelView;
    private WindowManager windowManager_level;
    private TextView xxcl;
    private TextView xxml;
    private RxTimer floatLevelTimer;

    @SuppressLint("ClickableViewAccessibility")
    public void levelFloat() {

        //  if (windowManager_level != null && levelView != null && !floatButtonIsStarted) return;

        if (!floatButtonIsStarted && !isWebViewFull){
            return;
        }

        if (windowManager_level == null || levelView == null) {

            windowManager_level = (WindowManager) getSystemService(WINDOW_SERVICE);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.x = 500;
            layoutParams.y = 650;
            layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

            levelView = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.work_layout, null);
            levelView.setAlpha(0.9f);
            xxcl = levelView.findViewById(R.id.xxcl);
            xxml = levelView.findViewById(R.id.xxml);

            try {
                xxcl.setText(String.valueOf(getCurrentLevel()));
                xxml.setText(tvMaxLevel.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            windowManager_level.addView(levelView, layoutParams);

            levelView.setOnTouchListener(new FloatingOnTouchListener(windowManager_level, layoutParams));

        }

        try {
            if (floatLevelTimer != null) {
                floatLevelTimer.cancel();
                floatLevelTimer = null;
            }
            floatLevelTimer = new RxTimer();

            levelView.setVisibility(View.VISIBLE);
            xxcl.setText(String.valueOf(getCurrentLevel()));
            //  xxml.setText(tvMaxLevel.getText().toString());

            floatLevelTimer.timer(3000, n -> {
                try {
                    if (windowManager_level != null && levelView != null) {
                        levelView.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
//            floatLevelTimer.timer(3000, n -> removeLevelView());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeLevelView() {
        try {
            if (windowManager_level != null && levelView != null)
                windowManager_level.removeView(levelView);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (floatLevelTimer != null) {
                floatLevelTimer.cancel();
                floatLevelTimer = null;
            }
            windowManager_level = null;
            levelView = null;
            xxcl = null;
            xxml = null;
        }
    }

    private ConstraintLayout inclineView;
    private WindowManager windowManager_incline;
    private TextView xxci;
    private TextView xxmi;
    private RxTimer floatInclineTimer;

    @SuppressLint("ClickableViewAccessibility")
    public void inclineFloat() {

        if (MODE != XE395ENT) return;
        if (!floatButtonIsStarted && !isWebViewFull){
            return;
        }

        if (windowManager_incline == null || inclineView == null) {

            windowManager_incline = (WindowManager) getSystemService(WINDOW_SERVICE);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.x = 700;
            layoutParams.y = 650;
            layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

            inclineView = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.work_layout_incline, null);
            inclineView.setAlpha(0.9f);
            xxci = inclineView.findViewById(R.id.xxcl);
            xxmi = inclineView.findViewById(R.id.xxml);

            try {
                xxci.setText(String.valueOf(incI2F(getCurrentIncline())));
                xxmi.setText(tvMaxIncline.getText().toString());
                //   xxmi.setText(String.valueOf(incI2F(INCLINE_MAX)));

                Log.d("EEEEEEE", "inclineFloat: " + INCLINE_MAX);
            } catch (Exception e) {
                e.printStackTrace();
            }

            windowManager_incline.addView(inclineView, layoutParams);

            inclineView.setOnTouchListener(new FloatingOnTouchListener(windowManager_incline, layoutParams));
        }

        try {
            if (floatInclineTimer != null) {
                floatInclineTimer.cancel();
                floatInclineTimer = null;
            }
            floatInclineTimer = new RxTimer();

            inclineView.setVisibility(View.VISIBLE);
            xxci.setText(String.valueOf(incI2F(getCurrentIncline())));
            //   xxmi.setText(String.valueOf(incI2F(INCLINE_MAX)));

            floatInclineTimer.timer(3000, n -> {
                try {
                    if (windowManager_incline != null && inclineView != null) {
                        inclineView.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void removeInclineView() {
        if (MODE != XE395ENT) return;
        try {
            if (windowManager_incline != null && inclineView != null)
                windowManager_incline.removeView(inclineView);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (floatInclineTimer != null) {
                floatInclineTimer.cancel();
                floatInclineTimer = null;
            }
            windowManager_incline = null;
            inclineView = null;
            xxci = null;
            xxmi = null;
        }
    }

    public void startFloatingDashboard(int type) {

        removeInclineView();
        removeLevelView();
        try {
            if (windowManager_exitButton != null && btnFullScreenExit != null) {
                windowManager_exitButton.removeView(btnFullScreenExit);
            }
            floatButtonIsStarted = false;
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (!FloatingWorkoutDashboardService.isStarted) {
//            if (type == 4) {
//                rb_internet_workout.setChecked(true);
//                showFragment(internetFragment);
//            } else {
//                rb_cast_workout.setChecked(true);
//                showFragment(mirroringFragment);
//            }

            Intent serviceIntent = new Intent(getApplicationContext(), FloatingWorkoutDashboardService.class);
            serviceIntent.putExtra("currentLevel", getCurrentLevel());
            serviceIntent.putExtra("maxLevel", LEVEL_MAX);
            serviceIntent.putExtra("currentIncline", getCurrentIncline());
            serviceIntent.putExtra("maxIncline", INCLINE_MAX);
            serviceIntent.putExtra("TYPE", type);
            startService(serviceIntent);

        }
    }

//    boolean m_bOnClick = false;
//    private long m_lStartTime;

    private static class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        boolean m_bOnClick = false;
        private long m_lStartTime;

        private final WindowManager viewB;
        private final WindowManager.LayoutParams layoutParams;

        public FloatingOnTouchListener(WindowManager view, WindowManager.LayoutParams layoutParams) {
            this.viewB = view;
            this.layoutParams = layoutParams;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    m_bOnClick = false;
                    m_lStartTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    m_bOnClick = true;
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    //  windowManager_exitButton.updateViewLayout(view, layoutParams);
                  //  viewB.updateViewLayout(view, layoutParams);

                    try {
                        viewB.updateViewLayout(view, layoutParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MotionEvent.ACTION_UP:
//                    view.performClick();
                    long m_lEndTime = System.currentTimeMillis();
                    m_bOnClick = (m_lEndTime - m_lStartTime) > 0.1 * 5000L;
                    break;
                default:
                    break;
            }
            return m_bOnClick;
        }
    }

    public void removeFloatView() {
        try {
            FloatingWorkoutDashboardService.isStarted = false;
            stopService(new Intent(getApplicationContext(), FloatingWorkoutDashboardService.class));

            Log.d("QQQQQQQQQQ", "removeFloatView: ");
            //關閉 離開全螢幕的按鈕
            if (windowManager_exitButton != null && btnFullScreenExit != null) {
                floatButtonIsStarted = false;
                windowManager_exitButton.removeView(btnFullScreenExit);
                Log.d("QQQQQQQQQQ", "removeFloatView:2 ");
            }

            removeInclineView();
            removeLevelView();

            if (hrHintDialogWindow != null) {
                hrHintDialogWindow.removeViewImmediate(hrHintView);
            }
            hrMsgType = -1;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            windowManager_exitButton = null;
            btnFullScreenExit = null;
            hrHintDialogWindow = null;
            hrHintView = null;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void setV() {
        try {
            AudioManager audioManager;
            audioManager = (AudioManager) getApplicationContext().getSystemService(AUDIO_SERVICE);

            Log.d("音量", "系統before: " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

//            float vvv = MMKV.defaultMMKV().decodeFloat("ooxxoo",0) == 0 ? audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) : MMKV.defaultMMKV().decodeFloat("ooxxoo",0);
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) vvv, 0);

            //    Log.d("音量", "after: " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

            int newIndex = (int) (MMKV.defaultMMKV().decodeFloat("ooxxoo", 0) == 0 ? audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) : MMKV.defaultMMKV().decodeFloat("ooxxoo", 0));

            Log.d("音量", "存的音量: " + (MMKV.defaultMMKV().decodeFloat("ooxxoo", 0)));
            if (newIndex == 15) newIndex = 14;
            audioManager.setStreamVolume(STREAM_MUSIC, newIndex, 0);
            if (audioManager.getStreamVolume(STREAM_MUSIC) < newIndex) {
                //音量過大警示
                audioManager.setStreamVolume(STREAM_MUSIC, newIndex, FLAG_SHOW_UI);
            }
            Log.d("音量", "系統after: " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}