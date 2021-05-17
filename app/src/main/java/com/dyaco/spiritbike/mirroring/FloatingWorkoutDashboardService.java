package com.dyaco.spiritbike.mirroring;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.corestar.libs.device.Device;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.GlideApp;
import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.RxBus;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.support.room.UserProfileEntity;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static android.view.MotionEvent.ACTION_UP;
import static com.dyaco.spiritbike.MyApplication.BT_SOUND_CONNECT;
import static com.dyaco.spiritbike.MyApplication.FAN_NOTIFY;
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
import static com.dyaco.spiritbike.MyApplication.MIRRORING_SWITCH_DASHBOARD_1;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_SWITCH_DASHBOARD_2;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_SWITCH_DASHBOARD_3;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_SWITCH_DASHBOARD_4;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_SWITCH_DASHBOARD_5;
import static com.dyaco.spiritbike.MyApplication.MODE;
import static com.dyaco.spiritbike.MyApplication.STOP_FLOATING_DASHBOARD;
import static com.dyaco.spiritbike.MyApplication.TIME_EVENT;
import static com.dyaco.spiritbike.MyApplication.VIDEO_BACK_HOME;
import static com.dyaco.spiritbike.MyApplication.WIFI_EVENT;
import static com.dyaco.spiritbike.MyApplication.btnFnaI;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.MyApplication.isInclineErrorShow;
import static com.dyaco.spiritbike.MyApplication.isLevelErrorShow;
import static com.dyaco.spiritbike.MyApplication.isSoundConnected;
import static com.dyaco.spiritbike.product_flavors.ModeEnum.XE395ENT;
import static com.dyaco.spiritbike.support.CommonUtils.incI2F;
import static com.dyaco.spiritbike.support.CommonUtils.setTime;

public class FloatingWorkoutDashboardService extends Service {
    private WindowManager windowManager;
    public static boolean isStarted = false;
    private ImageView iv_sound_connected;
    private ImageView iv_fna_connected;
    private ImageView iv_wifi;

    private TextView tv_distance;
    private TextView tv_distance_unit1; //tv_distance_unit
    private TextView tv_distance_unit2; //tv_total_distance_unit

    private TextView tv_calories;
    private TextView tv_calories_unit1; //tv_calories_text
    private TextView tv_calories_unit2; //tv_unit_kcal

    private TextView tvTotalTime;
    private TextView tv_time_unit1; //tv_time_text
    private TextView tv_3_unit; //tv_time_text

    private TextView tv_hr;
    private TextView tv_hr_unit1; //tv_hr_text
    private TextView tv_hr_unit2; //tv_hr_unit

    private TextView tv_pace;
    private TextView tv_pace_unit1;//tv_pace_text
    private TextView tv_pace_unit2;//tv_pace_unit
    private TextView tv_completed;

    private TextView tvTime_Dashboard;
    private int currentLevel;
    private int maxLevel;
    private int currentIncline;
    private int maxIncline;

    private TextView tvLevelCurrent;
    private TextView tvMaxLevel;
    private Group group_incline;

    private TextView tvInclineCurrent;
    private TextView tvMaxIncline;
    private TextView hideLevelCurrent;
    private TextView hideInclineCurrent;

    private ImageButton btn_level_minus;
    private ImageButton btn_level_plus;
    private ImageButton btn_incline_minus;
    private ImageButton btn_incline_plus;
    private ImageButton btSound_Dashboard;
    private Button btFullScreen_InternetDashboard;
    private Button btHome_InternetDashboard;
    private Button btn_stop;
    private RadioButton rb_dashboard_workout;
    private RadioButton rb_diagram_workout;
    private RadioButton rb_track_workout;
    private RadioButton rb_internet_workout;
    private RadioButton rb_cast_workout;

    //    private ImageView iv_switch_distance;
//    private ImageView iv_switch_calories;
//    private ImageView iv_switch_time;
//    private ImageView iv_switch_hr;
//    private ImageView iv_switch_speed;
    public View switch_2;
    public View switch_3;
    public View switch_4;

    private int type;

    private ConstraintLayout viewTop;
    private ConstraintLayout viewBottom;
    private ConstraintLayout viewLeft;
    private ConstraintLayout viewRight;


    //  private String userName;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        currentLevel = intent.getIntExtra("currentLevel", 1);
        maxLevel = intent.getIntExtra("maxLevel", 1);
        currentIncline = intent.getIntExtra("currentIncline", 1);
        maxIncline = intent.getIntExtra("maxIncline", 1);
        type = intent.getIntExtra("TYPE", 5);

        initViewP();

        initView();
        initData();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initViewP() {
//        view = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.activity_floating_workout_dashboard, null);
//        view.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                        View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        windowManager.addView(view, layoutParams);


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

//        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;


        viewTop = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.activity_floating_workout_dashboard_top, null);
        viewBottom = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.activity_floating_workout_dashboard_bottom, null);
        viewLeft = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.activity_floating_workout_dashboard_left, null);
        viewRight = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.activity_floating_workout_dashboard_right, null);

        int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        viewTop.setSystemUiVisibility(visibility);
        viewBottom.setSystemUiVisibility(visibility);
        viewLeft.setSystemUiVisibility(visibility);
        viewRight.setSystemUiVisibility(visibility);

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.TOP;
        windowManager.addView(viewTop, layoutParams);

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.BOTTOM;
        windowManager.addView(viewBottom, layoutParams);

        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.START;
        windowManager.addView(viewLeft, layoutParams);

        layoutParams.gravity = Gravity.END;
        windowManager.addView(viewRight, layoutParams);
    }

    private void initView() {

        tvLevelCurrent = viewRight.findViewById(R.id.tvLevelCurrent);
        tvMaxLevel = viewRight.findViewById(R.id.tvMaxLevel);
        tvInclineCurrent = viewRight.findViewById(R.id.tvInclineCurrent);
        hideInclineCurrent = viewRight.findViewById(R.id.hideInclineCurrent);
        hideLevelCurrent = viewRight.findViewById(R.id.hideLevelCurrent);

        tvMaxIncline = viewRight.findViewById(R.id.tvMaxIncline);
        group_incline = viewRight.findViewById(R.id.group_incline);
        btn_level_minus = viewRight.findViewById(R.id.btn_level_plus);
        btn_level_plus = viewRight.findViewById(R.id.btn_level_minus);
        btn_incline_minus = viewRight.findViewById(R.id.btn_incline_plus);
        btn_incline_plus = viewRight.findViewById(R.id.btn_incline_minus);


        iv_sound_connected = viewTop.findViewById(R.id.iv_sound_connected);
        iv_wifi = viewTop.findViewById(R.id.iv_wifi);
        iv_fna_connected = viewTop.findViewById(R.id.iv_fna_connected);
        btSound_Dashboard = viewTop.findViewById(R.id.btn_sound);
        btFullScreen_InternetDashboard = viewTop.findViewById(R.id.btFullScreen_InternetDashboard);
        btHome_InternetDashboard = viewTop.findViewById(R.id.btHome_InternetDashboard);
        tvTime_Dashboard = viewTop.findViewById(R.id.tv_workout_time);

        btn_stop = viewBottom.findViewById(R.id.btn_stop);
        rb_dashboard_workout = viewBottom.findViewById(R.id.rb_dashboard_workout);
        rb_diagram_workout = viewBottom.findViewById(R.id.rb_diagram_workout);
        rb_track_workout = viewBottom.findViewById(R.id.rb_track_workout);
        rb_internet_workout = viewBottom.findViewById(R.id.rb_internet_workout);
        rb_cast_workout = viewBottom.findViewById(R.id.rb_cast_workout);

//        iv_switch_distance = viewLeft.findViewById(R.id.iv_switch_distance);
//        iv_switch_calories = viewLeft.findViewById(R.id.iv_switch_calories);
//        iv_switch_time = viewLeft.findViewById(R.id.iv_switch_time);
//        iv_switch_hr = viewLeft.findViewById(R.id.iv_switch_hr);
//        iv_switch_speed = viewLeft.findViewById(R.id.iv_switch_speed);

        switch_2 = viewLeft.findViewById(R.id.switch_2);
        switch_3 = viewLeft.findViewById(R.id.switch_3);
        switch_4 = viewLeft.findViewById(R.id.switch_4);

        tv_distance = viewLeft.findViewById(R.id.tv_distance);
        tv_distance_unit1 = viewLeft.findViewById(R.id.tv_distance_unit);
        tv_distance_unit2 = viewLeft.findViewById(R.id.tv_total_distance_unit);

        tv_calories = viewLeft.findViewById(R.id.tv_calories);
        tv_calories_unit1 = viewLeft.findViewById(R.id.tv_calories_text);
        tv_calories_unit2 = viewLeft.findViewById(R.id.tv_unit_kcal);

        tvTotalTime = viewLeft.findViewById(R.id.tv_total_time);
        tv_time_unit1 = viewLeft.findViewById(R.id.tv_time_text);
        tv_3_unit = viewLeft.findViewById(R.id.tv_3_unit);

        tv_hr = viewLeft.findViewById(R.id.tv_hr);
        tv_hr_unit1 = viewLeft.findViewById(R.id.tv_hr_text);
        tv_hr_unit2 = viewLeft.findViewById(R.id.tv_hr_unit);

        tv_pace = viewLeft.findViewById(R.id.tv_pace);
        tv_pace_unit1 = viewLeft.findViewById(R.id.tv_pace_text);
        tv_pace_unit2 = viewLeft.findViewById(R.id.tv_pace_unit);

        tv_completed = viewBottom.findViewById(R.id.tv_completed);

        //  showSoundConnectedIcon();

        initMode();
    }

    private void initData() {

        //set level/incline num
        tvLevelCurrent.setText(String.valueOf(currentLevel));
        tvMaxLevel.setText(String.valueOf(maxLevel));
//        tvInclineCurrent.setText(String.valueOf(currentIncline));
//        tvMaxIncline.setText(String.valueOf(maxIncline));

        tvInclineCurrent.setText(String.valueOf(incI2F(currentIncline)));
        tvMaxIncline.setText(String.valueOf(incI2F(maxIncline)));

        if (isLevelErrorShow) {
            tvLevelCurrent.setVisibility(View.INVISIBLE);
            hideLevelCurrent.setVisibility(View.VISIBLE);
            btn_level_minus.setEnabled(false);
            btn_level_plus.setEnabled(false);
        }

        if (XE395ENT == MODE) {
            if (isInclineErrorShow) {
                tvInclineCurrent.setVisibility(View.INVISIBLE);
                tvInclineCurrent.setText("");

                hideInclineCurrent.setVisibility(View.VISIBLE);
                btn_incline_minus.setEnabled(false);
                btn_incline_plus.setEnabled(false);
            }
        }

        //update level/incline
        btn_level_minus.setOnClickListener(v ->
                RxBus.getInstance().post(new MsgEvent(MIRRORING_BTN_LEVEL_MINUS, true)));

        btn_level_plus.setOnClickListener(v ->
                RxBus.getInstance().post(new MsgEvent(MIRRORING_BTN_LEVEL_PLUS, true)));

        btn_incline_minus.setOnClickListener(v ->
                RxBus.getInstance().post(new MsgEvent(MIRRORING_BTN_INCLINE_MINUS, true)));

        btn_incline_plus.setOnClickListener(v ->
                RxBus.getInstance().post(new MsgEvent(MIRRORING_BTN_INCLINE_PLUS, true)));

        addAutoClick(btn_level_plus, 1);
        addAutoClick(btn_level_minus, 2);
        addAutoClick(btn_incline_plus, 3);
        addAutoClick(btn_incline_minus, 4);


//        //switch
//        iv_switch_distance.setOnClickListener(v ->
//                RxBus.getInstance().post(new MsgEvent(MIRRORING_SWITCH_DASHBOARD_1, true)));

        switch_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_calories.setText("");
                tv_calories_unit1.setText("");
                tv_calories_unit2.setText("");
                RxBus.getInstance().post(new MsgEvent(MIRRORING_SWITCH_DASHBOARD_2, true));
            }
        });

//        iv_switch_calories.setOnClickListener(v -> {
//            tv_calories.setText("");
//            tv_calories_unit1.setText("");
//            tv_calories_unit2.setText("");
//            RxBus.getInstance().post(new MsgEvent(MIRRORING_SWITCH_DASHBOARD_2, true));
//        });

        switch_3.setOnClickListener(v -> {
            tvTotalTime.setText("");
            tv_time_unit1.setText("");
            tv_3_unit.setText("");
            RxBus.getInstance().post(new MsgEvent(MIRRORING_SWITCH_DASHBOARD_3, true));
        });

        switch_4.setOnClickListener(v -> {
            tv_hr.setText("");
            tv_hr_unit1.setText("");
            tv_hr_unit2.setText("bpm");
            RxBus.getInstance().post(new MsgEvent(MIRRORING_SWITCH_DASHBOARD_4, true));
        });

//        iv_switch_speed.setOnClickListener(v -> {
//            tv_pace.setText("");
//            tv_pace_unit1.setText("");
//            tv_pace_unit2.setText("");
//            RxBus.getInstance().post(new MsgEvent(MIRRORING_SWITCH_DASHBOARD_5, true));
//        });

        //音量設定
        btSound_Dashboard.setOnClickListener(v -> {
            if (!FloatingSoundSettingService.isStarted) {
                Intent serviceIntent = new Intent(this, FloatingSoundSettingService.class);
                serviceIntent.putExtra("isWorkout", true);
                startService(serviceIntent);
            }
        });

        //隱藏Dashboard，顯示離開全螢幕的浮動按鈕
        btFullScreen_InternetDashboard.setOnClickListener(v -> {
            //  if (CommonUtils.isFastClick()) return;
            stopSelf();
            isStarted = false;
            RxBus.getInstance().post(new MsgEvent(MIRRORING_EXIT_FULL_SCREEN, type));
        });

        //關閉
        btHome_InternetDashboard.setOnClickListener(v -> {
            if (CommonUtils.isFastClick()) return;
            if (type == 4) {
                RxBus.getInstance().post(new MsgEvent(VIDEO_BACK_HOME, true));
            } else {
                RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP, true));
            }
            stopSelf();
        });

        //時鐘
        tvTime_Dashboard.setText(setTime());

        //STOP WORKOUT
        btn_stop.setOnClickListener(v -> {
            RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP_WORKOUT, true));
        });


        //BottomMenu 切換
        rb_dashboard_workout.setOnClickListener(v -> {
            if (type == 5) RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP, true));
            RxBus.getInstance().post(new MsgEvent(MIRRORING_GO_HOME_SCREEN, true));
        });
        rb_diagram_workout.setOnClickListener(v -> {
            if (type == 5) RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP, true));
            RxBus.getInstance().post(new MsgEvent(MIRRORING_GO_PROGRAMS, true));
        });
        rb_track_workout.setOnClickListener(v -> {
            if (type == 5) RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP, true));
            RxBus.getInstance().post(new MsgEvent(MIRRORING_GO_PROFILE, true));
        });
        rb_internet_workout.setOnClickListener(v -> {
            if (type == 5) RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP, true));
            if (type == 4) return;
            RxBus.getInstance().post(new MsgEvent(MIRRORING_GO_WEB_VIEW, true));
        });

        rb_cast_workout.setOnClickListener(v -> {
            if (type == 5) return;
            RxBus.getInstance().post(new MsgEvent(MIRRORING_GO_CAST, true));
        });

        if (type == 5) {
            rb_cast_workout.setChecked(true);
        } else {
            rb_internet_workout.setChecked(true);
        }

        ImageButton m_btFan_Dashboard = viewTop.findViewById(R.id.btn_fan);
        //風扇
        m_btFan_Dashboard.setOnClickListener(v -> {
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
        //風扇初始狀態
        if (btnFnaI == 0) {
            iv_fna_connected.setVisibility(View.INVISIBLE);
        } else {
            iv_fna_connected.setVisibility(View.VISIBLE);
        }

        iv_wifi.setImageResource(CommonUtils.setWifiImage(new CommonUtils().getWifiLevel(this), true));


//        //風扇
//        m_btFan_Dashboard.setOnClickListener(v -> {
//            if (btnFnaI == 0) {  //btn_round_fan0_9b9b9b_64
//                m_btFan_Dashboard.setBackgroundResource(R.drawable.btn_round_fan1_9b9b9b_64);
//                btnFnaI = 1;
//                getInstance().commandSetFan(Device.FAN.WEAK);
//            } else if (btnFnaI == 1) {
//                m_btFan_Dashboard.setBackgroundResource(R.drawable.btn_round_fan2_9b9b9b_64);
//                btnFnaI = 2;
//                getInstance().commandSetFan(Device.FAN.MIDDLE);
//            } else if (btnFnaI == 2) {
//                m_btFan_Dashboard.setBackgroundResource(R.drawable.btn_round_fan3_9b9b9b_64);
//                btnFnaI = 3;
//                getInstance().commandSetFan(Device.FAN.STRONG);
//            } else {
//                m_btFan_Dashboard.setBackgroundResource(R.drawable.btn_round_fan0_9b9b9b_64);
//                btnFnaI = 0;
//                getInstance().commandSetFan(Device.FAN.STOP);
//            }
//        });
//
//        //風扇初始狀態
//        if (btnFnaI == 0) {
//            m_btFan_Dashboard.setBackgroundResource(R.drawable.btn_round_fan0_9b9b9b_64);
//        } else if (btnFnaI == 1) {
//            m_btFan_Dashboard.setBackgroundResource(R.drawable.btn_round_fan1_9b9b9b_64);
//        } else if (btnFnaI == 2) {
//            m_btFan_Dashboard.setBackgroundResource(R.drawable.btn_round_fan2_9b9b9b_64);
//        } else {
//            m_btFan_Dashboard.setBackgroundResource(R.drawable.btn_round_fan3_9b9b9b_64);
//        }

        initEvent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exitDashboard();
    }

    private void exitDashboard() {
        try {
            isStarted = false;
            windowManager.removeView(viewTop);
            windowManager.removeView(viewBottom);
            windowManager.removeView(viewLeft);
            windowManager.removeView(viewRight);
            stopService(new Intent(this, FloatingSoundSettingService.class));
            stopService(new Intent(this, FloatingHeartRateService.class));
            compositeDisposable.dispose();
            windowManager = null;
            viewTop = null;
            viewBottom = null;
            viewLeft = null;
            viewRight = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    DashboardDataBean dashboardDataBean = new DashboardDataBean();

    private void initEvent() {

        Handler mainHandler = new Handler(Looper.getMainLooper());
        Disposable disposable = RxBus.getInstance().toObservable(MsgEvent.class).subscribe(msg -> {

            if (msg.getType() == MIRRORING_DASHBOARD_DATA) {
                Runnable myRunnable = () -> {
                    dashboardDataBean = (DashboardDataBean) msg.getObj();

                    tv_distance.setText(dashboardDataBean.getDashboard1());
                    // tv_distance_unit2.setText(dashboardDataBean.getDashboard1_unit2());

                    tv_calories.setText(dashboardDataBean.getDashboard2());
                    tv_calories_unit1.setText(dashboardDataBean.getDashboard2_unit1());
                    //   tv_calories_unit2.setText(dashboardDataBean.getDashboard2_unit2());

                    tvTotalTime.setText(dashboardDataBean.getDashboard3());
                    tv_time_unit1.setText(dashboardDataBean.getDashboard3_unit1());
                    tv_3_unit.setText(dashboardDataBean.getDashboard3_unit2());

                    tv_hr.setText(dashboardDataBean.getDashboard4());
                    tv_hr_unit1.setText(dashboardDataBean.getDashboard4_unit1());
                    //  tv_hr_unit2.setText(dashboardDataBean.getDashboard4_unit2());

                    tv_pace.setText(dashboardDataBean.getDashboard5());
                    //   tv_pace_unit1.setText(dashboardDataBean.getDashboard5_unit1());
                    //  tv_pace_unit2.setText(dashboardDataBean.getDashboard5_unit2());

                    tv_completed.setText(dashboardDataBean.getDashboard6());
                };
                mainHandler.post(myRunnable);
                return;
            }

            if (msg.getType() == TIME_EVENT) {
                Runnable myRunnable = () -> tvTime_Dashboard.setText(msg.getObj().toString());
                mainHandler.post(myRunnable);
                return;
            }

            if (msg.getType() == MIRRORING_SET_CURRENT_LEVEL) {
                Runnable myRunnable = () -> tvLevelCurrent.setText(msg.getObj().toString());
                mainHandler.post(myRunnable);
                return;
            }

            if (msg.getType() == MIRRORING_SET_CURRENT_INCLINE) {
                Runnable myRunnable = () -> {
                    if (!isInclineErrorShow)
                        tvInclineCurrent.setText(msg.getObj().toString());
                };
                mainHandler.post(myRunnable);
                return;
            }

            if (msg.getType() == MIRRORING_SET_MAX_LEVEL) {
                Runnable myRunnable = () -> tvMaxLevel.setText(msg.getObj().toString());
                mainHandler.post(myRunnable);
                return;
            }

            if (msg.getType() == MIRRORING_SET_MAX_INCLINE) {
                Runnable myRunnable = () -> tvMaxIncline.setText(msg.getObj().toString());
                mainHandler.post(myRunnable);
                return;
            }

            //SOUND CONNECT
//            if (msg.getType() == BT_SOUND_CONNECT) {
//                Runnable myRunnable = this::showSoundConnectedIcon;
//                mainHandler.post(myRunnable);
//                return;
//            }

            if (msg.getType() == FAN_NOTIFY) {
                Runnable myRunnable = () ->
                        iv_fna_connected.setVisibility((boolean) msg.getObj() ? View.VISIBLE : View.INVISIBLE);
                mainHandler.post(myRunnable);
                return;
            }

            if (msg.getType() == 10044) {
                hideView((boolean) msg.getObj());
                return;
            }

            if (msg.getType() == WIFI_EVENT) {
                int img = CommonUtils.setWifiImage((int) msg.getObj(), true);
                Runnable myRunnable = () ->
                        iv_wifi.setImageResource(img);
                mainHandler.post(myRunnable);
                return;
            }

            if (msg.getType() == STOP_FLOATING_DASHBOARD) {
                stopSelf();
            }
        });
        compositeDisposable.add(disposable);
    }

    private void hideView(boolean isShow) {
        viewTop.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        viewBottom.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        viewLeft.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        viewRight.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
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

    private void initMode() {
        if (MODE != XE395ENT) group_incline.setVisibility(View.INVISIBLE);
    }

    ImageButton iButton;

    @SuppressLint("ClickableViewAccessibility")
    public void addAutoClick(ImageButton button, int type) {
        button.setOnLongClickListener(v -> {
            RxBus.getInstance().post(new MsgEvent(MIRRORING_BTN_AUTO_DOWN, type));
            iButton = button;
            //    button.setPressed(true);
            return true;
        });
        button.setOnTouchListener((v, event) -> {
            if (iButton != null) {
                if (event.getAction() == ACTION_UP) {
                    RxBus.getInstance().post(new MsgEvent(MIRRORING_BTN_AUTO_UP, type));
                    if (iButton != null) iButton.setPressed(false);
                    iButton = null;
                }
            }
            return false;
        });
    }

    private void commandButton(ImageButton imageButton) {
        imageButton.performClick();
        //  imageButton.setPressed(true);
        new RxTimer().timer(100, n -> imageButton.setPressed(false));
    }
}
