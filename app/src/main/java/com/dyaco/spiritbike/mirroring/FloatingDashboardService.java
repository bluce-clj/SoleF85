package com.dyaco.spiritbike.mirroring;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.corestar.app.BleDevice;
import com.corestar.libs.device.Device;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.GlideApp;
import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.RxBus;
import com.dyaco.spiritbike.support.banner.util.LogUtils;
import com.dyaco.spiritbike.support.room.UserProfileEntity;

import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.dyaco.spiritbike.MyApplication.BT_SOUND_CONNECT;
import static com.dyaco.spiritbike.MyApplication.FAN_NOTIFY;
import static com.dyaco.spiritbike.MyApplication.FTMS_NOTIFY_CONNECTED;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_EXIT_FULL_SCREEN;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_CAST;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_HOME_SCREEN;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_PROFILE;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_PROGRAMS;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_SETTING;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_WEB_VIEW;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_SIGN_OUT;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_STOP;
import static com.dyaco.spiritbike.MyApplication.ON_BLE_DEVICE_CONNECTED;
import static com.dyaco.spiritbike.MyApplication.STOP_FLOATING_DASHBOARD;
import static com.dyaco.spiritbike.MyApplication.TIME_EVENT;
import static com.dyaco.spiritbike.MyApplication.VIDEO_BACK_HOME;
import static com.dyaco.spiritbike.MyApplication.WIFI_EVENT;
import static com.dyaco.spiritbike.MyApplication.btnFnaI;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.MyApplication.isBlueToothOn;
import static com.dyaco.spiritbike.MyApplication.isFtmsConnected;
import static com.dyaco.spiritbike.MyApplication.isSoundConnected;
import static com.dyaco.spiritbike.support.CommonUtils.checkStr;
import static com.dyaco.spiritbike.support.CommonUtils.getAvatarHeaderResourceId;
import static com.dyaco.spiritbike.support.CommonUtils.isConnected;
import static com.dyaco.spiritbike.support.CommonUtils.setTime;

//stopSelf();  > onDestroy
public class FloatingDashboardService extends Service {

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    public static boolean isStarted = false;
    private ConstraintLayout viewTop;
    private ConstraintLayout viewBottom;
    private ImageView iv_bt_connected;
    private ImageView iv_sound_connected;
    private ImageView iv_fna_connected;
    private ImageView iv_hr_connected;
    private Button m_btFan_Dashboard;
    private int type;
    private ImageView m_ivWifiIcon_Dashboard;
    private TextView tvTime_Dashboard;
    public static int HIDE_FLOATING_DASHBOARD = 22222;
    private RadioButton rbInternet_Dashboard;
    private RadioButton rbCast_Dashboard;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

//        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;


        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        initView();

    }

    private void initView() {
        viewTop = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.activity_floating_dashboard_top, null);
        viewBottom = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.activity_floating_dashboard_bottom, null);

        viewTop.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        viewBottom.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        layoutParams.gravity = Gravity.TOP;
        windowManager.addView(viewTop, layoutParams);

        layoutParams.gravity = Gravity.BOTTOM;
        windowManager.addView(viewBottom, layoutParams);

        LogUtils.d("FloatingDashboardService ->" + "initView()");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        type = intent.getIntExtra("TYPE", 5);

        LogUtils.d("FloatingDashboardService ->" + "onStartCommand()" + "type:" +type );
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    private void showFloatingWindow() {
        // ViewGroup rootView = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        //    view = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.activity_floating_dashboard, null);

        iv_hr_connected = viewTop.findViewById(R.id.iv_hr_connected);
        showHrConnectedIcon();
        //音量設定
        Button btSound_Dashboard = viewTop.findViewById(R.id.btSound_Dashboard);
        btSound_Dashboard.setOnClickListener(v -> {
            if (!FloatingSoundSettingService.isStarted) {
                Intent serviceIntent = new Intent(this, FloatingSoundSettingService.class);
                serviceIntent.putExtra("isWorkout", false);
                startService(serviceIntent);
            }
        });

        Button btHeartRate_Dashboard = viewTop.findViewById(R.id.btHeartRate_Dashboard);
        btHeartRate_Dashboard.setOnClickListener(v -> {
            if (!FloatingSoundSettingService.isStarted) {
                Intent serviceIntent = new Intent(this, FloatingHeartRateService.class);
                serviceIntent.putExtra("isWorkout", false);
                startService(serviceIntent);
            }
        });

        //隱藏Dashboard，顯示離開全螢幕的浮動按鈕
        Button btFullScreen_InternetDashboard = viewTop.findViewById(R.id.btFullScreen_InternetDashboard);
        btFullScreen_InternetDashboard.setOnClickListener(v -> {
        //    if (CommonUtils.isFastClick()) return;
            stopSelf();
            isStarted = false;
            RxBus.getInstance().post(new MsgEvent(MIRRORING_EXIT_FULL_SCREEN, type));
        });

        iv_bt_connected = viewTop.findViewById(R.id.iv_bt_connected);
        iv_sound_connected = viewTop.findViewById(R.id.iv_sound_connected);
        iv_fna_connected = viewTop.findViewById(R.id.iv_fna_connected);

        //時鐘
        tvTime_Dashboard = viewTop.findViewById(R.id.tvTime_Dashboard);
        tvTime_Dashboard.setText(setTime());

        initNotify();

        UserProfileEntity userProfileEntity = getInstance().getUserProfile();
        //UserName
        TextView m_tvName_Dashboard = viewTop.findViewById(R.id.tvName_Dashboard);
        m_tvName_Dashboard.setText(userProfileEntity.getUserName());

        //Avatar
        ImageView ivAvatar_Dashboard = viewTop.findViewById(R.id.ivAvatar_Dashboard);
        int avatar = getAvatarHeaderResourceId(userProfileEntity.getUserImage());
        if (checkStr(userProfileEntity.getSoleHeaderImgUrl()) && isConnected(this)) {
            //已連接Sole帳號
            GlideApp.with(this)
                    .load(userProfileEntity.getSoleHeaderImgUrl())
                    .placeholder(R.drawable.avatar_start_no)
                    .centerInside()
                    .error(avatar)
                    .circleCrop()
                    // .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(ivAvatar_Dashboard);
        } else {
            //尚未連接Sole帳號
            GlideApp.with(this)
                    .load(avatar)
                    .centerInside()
                    .into(ivAvatar_Dashboard);
        }

        //HOME 關閉
        Button btHome_InternetDashboard = viewTop.findViewById(R.id.btHome_InternetDashboard);
        btHome_InternetDashboard.setOnClickListener(v -> {
            if (CommonUtils.isFastClick()) return;

            Log.d("HHHHHH", "showFloatingWindow: " + type);
            if (type == 4) {
                RxBus.getInstance().post(new MsgEvent(VIDEO_BACK_HOME, true));
            } else {
                RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP, true));
                stopSelf();
            }
        });

        //BottomMenu 切換
        RadioButton rbHomeScreen_Dashboard = viewBottom.findViewById(R.id.rbHomeScreen_Dashboard);
        rbHomeScreen_Dashboard.setOnClickListener(v -> {
            RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP, true));
            RxBus.getInstance().post(new MsgEvent(MIRRORING_GO_HOME_SCREEN, true));
        });

        RadioButton rbPrograms_Dashboard = viewBottom.findViewById(R.id.rbPrograms_Dashboard);
        rbPrograms_Dashboard.setOnClickListener(v -> {
            RxBus.getInstance().post(new MsgEvent(MIRRORING_GO_PROGRAMS, true));
            RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP, true));
        });

        RadioButton rbProfile_Dashboard = viewBottom.findViewById(R.id.rbProfile_Dashboard);
        rbProfile_Dashboard.setOnClickListener(v -> {
            RxBus.getInstance().post(new MsgEvent(MIRRORING_GO_PROFILE, true));
            RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP, true));
        });

        RadioButton rbInternet_Dashboard = viewBottom.findViewById(R.id.rbInternet_Dashboard);
        rbInternet_Dashboard.setOnClickListener(v -> {
            if (type == 4) return;
            RxBus.getInstance().post(new MsgEvent(MIRRORING_GO_WEB_VIEW, true));
            RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP, true));
        });

        RadioButton rbCast_Dashboard = viewBottom.findViewById(R.id.rbCast_Dashboard);
        rbCast_Dashboard.setOnClickListener(v -> {
            if (type == 5) return;
            RxBus.getInstance().post(new MsgEvent(MIRRORING_GO_CAST, true));
        });

        RadioButton rbSettings_Dashboard = viewBottom.findViewById(R.id.rbSettings_Dashboard);
        rbSettings_Dashboard.setOnClickListener(v -> {
            RxBus.getInstance().post(new MsgEvent(MIRRORING_GO_SETTING, true));
            RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP, true));
        });

        if (type == 5) {
            rbCast_Dashboard.setChecked(true);
        } else {
            rbInternet_Dashboard.setChecked(true);
        }

        m_btFan_Dashboard = viewTop.findViewById(R.id.btFan_Dashboard);

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
//        //風扇
//        m_btFan_Dashboard.setOnClickListener(v -> {
//            if (btnFnaI == 0) {  //btn_round_fan0_9b9b9b_64
//                m_btFan_Dashboard.setBackgroundResource(R.drawable.btn_round_fan1_e6e6e6_64);
//                btnFnaI = 1;
//                getInstance().commandSetFan(Device.FAN.WEAK);
//            } else if (btnFnaI == 1) {
//                m_btFan_Dashboard.setBackgroundResource(R.drawable.btn_round_fan2_e6e6e6_64);
//                btnFnaI = 2;
//                getInstance().commandSetFan(Device.FAN.MIDDLE);
//            } else if (btnFnaI == 2) {
//                m_btFan_Dashboard.setBackgroundResource(R.drawable.btn_round_fan3_e6e6e6_64);
//                btnFnaI = 3;
//                getInstance().commandSetFan(Device.FAN.STRONG);
//            } else {
//                m_btFan_Dashboard.setBackgroundResource(R.drawable.btn_round_fan0_e6e6e6_64);
//                btnFnaI = 0;
//                getInstance().commandSetFan(Device.FAN.STOP);
//            }
//        });

        //風扇初始狀態
//        if (btnFnaI == 0) {
//            m_btFan_Dashboard.setBackgroundResource(R.drawable.btn_round_fan0_e6e6e6_64);
//        } else if (btnFnaI == 1) {
//            m_btFan_Dashboard.setBackgroundResource(R.drawable.btn_round_fan1_e6e6e6_64);
//        } else if (btnFnaI == 2) {
//            m_btFan_Dashboard.setBackgroundResource(R.drawable.btn_round_fan2_e6e6e6_64);
//        } else {
//            m_btFan_Dashboard.setBackgroundResource(R.drawable.btn_round_fan3_e6e6e6_64);
//        }
        if (btnFnaI == 0) {
            iv_fna_connected.setVisibility(View.INVISIBLE);
        } else {
            iv_fna_connected.setVisibility(View.VISIBLE);
        }

        Button btSignout_Dashboard = viewTop.findViewById(R.id.btSignout_Dashboard);
        btSignout_Dashboard.setOnClickListener(v -> {
            RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP, true));
            RxBus.getInstance().post(new MsgEvent(MIRRORING_SIGN_OUT, true));
        });

        Button btBlueTooth_Dashboard = viewTop.findViewById(R.id.btBlueTooth_Dashboard);
        btBlueTooth_Dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  RxBus.getInstance().post(new MsgEvent(101010, 1));
            }
        });

        m_ivWifiIcon_Dashboard = viewTop.findViewById(R.id.ivWifiIcon_Dashboard);
        m_ivWifiIcon_Dashboard.setImageResource(CommonUtils.setWifiImage(new CommonUtils().getWifiLevel(this), false));

        m_ivWifiIcon_Dashboard.setOnClickListener(v -> {
            //  RxBus.getInstance().post(new MsgEvent(101010, 0));
        });

        iv_hr_connected = viewTop.findViewById(R.id.iv_hr_connected);

        showHrConnectedIcon();

        //   showFtmsConnectedIcon();
        showSoundConnectedIcon();
        showFanIcon();
    }

    private void initNotify() {

        Handler mainHandler = new Handler(Looper.getMainLooper());
        Disposable disposable = RxBus.getInstance().toObservable(MsgEvent.class).subscribe(msg -> {
            if (msg.getType() == TIME_EVENT) {
                Runnable myRunnable = () -> tvTime_Dashboard.setText(msg.getObj().toString());
                mainHandler.post(myRunnable);
            }

            if (msg.getType() == FTMS_NOTIFY_CONNECTED) {
                Runnable myRunnable = this::showFtmsConnectedIcon;
                mainHandler.post(myRunnable);
                return;
            }

            //SOUND CONNECT
            if (msg.getType() == BT_SOUND_CONNECT) {
                Runnable myRunnable = this::showSoundConnectedIcon;
                mainHandler.post(myRunnable);
                return;
            }

            if (msg.getType() == WIFI_EVENT) {
                int img = CommonUtils.setWifiImage((int) msg.getObj(), false);
                Runnable myRunnable = () ->
                        m_ivWifiIcon_Dashboard.setImageResource(img);
                mainHandler.post(myRunnable);
                return;
            }

            //FAN
            if (msg.getType() == FAN_NOTIFY) {
                Runnable myRunnable = () ->
                        iv_fna_connected.setVisibility((boolean) msg.getObj() ? View.VISIBLE : View.INVISIBLE);
                mainHandler.post(myRunnable);
                return;
            }

            if (msg.getType() == ON_BLE_DEVICE_CONNECTED) {
                Runnable myRunnable = this::showHrConnectedIcon;
                mainHandler.post(myRunnable);
                return;
            }

            if (msg.getType() == STOP_FLOATING_DASHBOARD) {
                stopSelf();
                return;
            }

            if (msg.getType() == HIDE_FLOATING_DASHBOARD) {
                Runnable myRunnable = () -> {
                    boolean b = (boolean) msg.getObj();
                    showView(b);
//                    if (b) setCheck(msg.getP());
//                    type = msg.getP();
                };
                mainHandler.post(myRunnable);
            }
        });
        compositeDisposable.add(disposable);
    }

    private void setCheck(int p) {
        if (p == 5) {
            rbCast_Dashboard.setChecked(true);
        } else {
            rbInternet_Dashboard.setChecked(true);
        }
    }

    private void showView(boolean isShow) {

        viewTop.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        viewBottom.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exitDashboard();

        viewTop = null;
        viewBottom = null;
    }

    private void exitDashboard() {
        try {
            isStarted = false;
            windowManager.removeView(viewTop);
            windowManager.removeView(viewBottom);
            stopService(new Intent(this, FloatingSoundSettingService.class));
            stopService(new Intent(this, FloatingHeartRateService.class));
            if (compositeDisposable != null) {
                compositeDisposable.dispose();
                compositeDisposable = null;
            }
            windowManager = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private void showFanIcon() {
        if (btnFnaI == 0) {
            iv_fna_connected.setVisibility(View.INVISIBLE);
        } else {
            iv_fna_connected.setVisibility(View.VISIBLE);
        }
    }

    private void showFtmsConnectedIcon() {
        //ftms連結圖示
        try {
            if (isFtmsConnected) {
                if (iv_bt_connected != null) iv_bt_connected.setVisibility(View.VISIBLE);
            } else {
                if (iv_bt_connected != null) iv_bt_connected.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void showHrConnectedIcon() {
//        try {
//            if (getInstance().mBleEventManager.getConnectedBleDevices() != null
//                    && getInstance().mBleEventManager.getConnectedBleDevices().size() > 0 && isBlueToothOn == 0) {
//                if (iv_hr_connected != null) iv_hr_connected.setVisibility(View.VISIBLE);
//            } else {
//                if (iv_hr_connected != null) iv_hr_connected.setVisibility(View.INVISIBLE);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void showHrConnectedIcon() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
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
}
