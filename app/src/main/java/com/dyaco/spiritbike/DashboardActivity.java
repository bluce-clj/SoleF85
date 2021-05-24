package com.dyaco.spiritbike;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.corestar.app.BleDevice;
import com.corestar.libs.device.Device;

import com.dyaco.spiritbike.internet.InternetFragment;
import com.dyaco.spiritbike.mirroring.FloatingDashboardService;
import com.dyaco.spiritbike.mirroring.FloatingHeartRateService;
import com.dyaco.spiritbike.mirroring.FloatingSoundSettingService;
import com.dyaco.spiritbike.settings.UpdateBean;
import com.dyaco.spiritbike.settings.UpdateSoftwareActivity;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.FloatingWidget;
import com.dyaco.spiritbike.support.GlideApp;
import com.dyaco.spiritbike.support.LogS;
import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.RxBus;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.support.banner.Banner;
import com.dyaco.spiritbike.support.banner.indicator.RectangleIndicator;
import com.dyaco.spiritbike.support.banner.util.BannerUtils;
import com.dyaco.spiritbike.support.banner.util.LogUtils;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.uart.isBusEvent;
import com.dyaco.spiritbike.webapi.BaseApi;
import com.dyaco.spiritbike.webapi.IServiceApi;
import com.dyaco.spiritbike.workout.WorkoutBean;
import com.dyaco.spiritbike.workout.WorkoutDashboardActivity;
import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static android.media.AudioManager.FLAG_SHOW_UI;
import static android.media.AudioManager.STREAM_MUSIC;
import static com.dyaco.spiritbike.MyApplication.BT_SOUND_CONNECT;
import static com.dyaco.spiritbike.MyApplication.COMMAND_KEY;
import static com.dyaco.spiritbike.MyApplication.COMMAND_SET_CONTROL;
import static com.dyaco.spiritbike.MyApplication.FAN_NOTIFY;
import static com.dyaco.spiritbike.MyApplication.FTMS_NOTIFY;
import static com.dyaco.spiritbike.MyApplication.FTMS_NOTIFY_CONNECTED;
import static com.dyaco.spiritbike.MyApplication.FTMS_NOTIFY_START_OR_RESUME;
import static com.dyaco.spiritbike.MyApplication.GO_SLEEP;
import static com.dyaco.spiritbike.MyApplication.IS_CHILD_LOCKING;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_EXIT_FULL_SCREEN;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_CAST;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_HOME_SCREEN;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_PROFILE;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_PROGRAMS;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_SETTING;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_GO_WEB_VIEW;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_SIGN_OUT;
import static com.dyaco.spiritbike.MyApplication.MODE;
import static com.dyaco.spiritbike.MyApplication.ON_BLE_DEVICE_CONNECTED;
import static com.dyaco.spiritbike.MyApplication.ON_BLE_DEVICE_DISCONNECTED;
import static com.dyaco.spiritbike.MyApplication.ON_ERROR;
import static com.dyaco.spiritbike.MyApplication.REMOVE_BUTTON;
import static com.dyaco.spiritbike.MyApplication.SHOW_EXIT_BUTTON;
import static com.dyaco.spiritbike.MyApplication.STOP_FLOATING_DASHBOARD;
import static com.dyaco.spiritbike.MyApplication.UNIT_E;
import static com.dyaco.spiritbike.MyApplication.VIDEO_BACK_HOME;
import static com.dyaco.spiritbike.MyApplication.WIFI_EVENT;
import static com.dyaco.spiritbike.MyApplication.btnFnaI;
import static com.dyaco.spiritbike.MyApplication.isBlueToothOn;
import static com.dyaco.spiritbike.MyApplication.isFTMSNotify;
import static com.dyaco.spiritbike.MyApplication.isFtmsConnected;
import static com.dyaco.spiritbike.MyApplication.isSoundConnected;
import static com.dyaco.spiritbike.MyApplication.updateNotify;
import static com.dyaco.spiritbike.mirroring.FloatingDashboardService.HIDE_FLOATING_DASHBOARD;
import static com.dyaco.spiritbike.product_flavors.ModeEnum.getMode;
import static com.dyaco.spiritbike.support.CommonUtils.checkStr;
import static com.dyaco.spiritbike.support.CommonUtils.checkSwVersion;
import static com.dyaco.spiritbike.support.CommonUtils.convertSwVersion;
import static com.dyaco.spiritbike.support.CommonUtils.getAvatarHeaderResourceId;
import static com.dyaco.spiritbike.support.CommonUtils.isConnected;
import static com.dyaco.spiritbike.support.CommonUtils.setTime;
import static com.dyaco.spiritbike.MyApplication.TIME_EVENT;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.ProgramsEnum.MANUAL;

public class DashboardActivity extends BaseAppCompatActivity {
    public boolean isMirrorShow = false; //樂播開啟中
    public FloatingWidget floatingWidget;
    private RectangleIndicator indicator;
    // public NavController navController;
    public NavHostController navController;
    public Button m_btSignOut_Dashboard;
    private Button m_btHeartRate_Dashboard;
    private Button m_btSound_Dashboard;
    private Button m_btFan_Dashboard;
    private Button m_btBluetooth_Dashboard;
    private TextView m_tvName_Dashboard;
    public TextView m_tvTime_Dashboard;
    private View m_ivLineV1_Dashboard;
    private View m_ivLineV2_Dashboard;
    private ImageView m_ivWifiIcon_Dashboard;

    private ImageView ivAvatar_Dashboard;
    public RadioButton rbPrograms_Dashboard;
    private boolean dark_bg = true;
    public RadioButton rbHomeScreen_Dashboard;
    private Disposable disposable;
    private int userType; //0 guest
    public ImageView ivUpdateNotify;
    public RadioButton rbProfile_Dashboard;
    //  private ConstraintLayout view;
    public View clDashboard;
    public String userName;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public RadioButton rbInternet_Dashboard;
    public RadioButton rbCast_Dashboard;
    public RadioButton rbSettings_Dashboard;

    public ImageView ivOptionBg;

    private ImageView iv_hr_connected;
    private ImageView iv_bt_connected;
    private ImageView iv_sound_connected;
    public ImageView iv_fna_connected;
    public boolean canSleep = true;
    private RxTimer checkUpdateRxTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initDelay();

        initNav();

        if (getInstance().getUserProfile().getSleepMode() == 1) {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 2147483647);
        } else {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 60 * 30 * 1000);
        }

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

    private void initNav() {
        Navigation.findNavController(this,R.id.nhcDashboard).addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable @org.jetbrains.annotations.Nullable Bundle arguments) {
                LogUtils.d(getClassName() +"onDestinationChanged() destination: " + destination.getLabel());
            }
        });
    }

    /**
     * 檢查app更新
     */
    public void checkUpdate() {

        BaseApi.request(BaseApi.createApi2(IServiceApi.class).apiCheckUpdate(),
                new BaseApi.IResponseListener<UpdateBean>() {
                    @Override
                    public void onSuccess(UpdateBean data) {
                       // Log.d("更新", "檢查更新: " + new Gson().toJson(data));
                        LogS.printJson("RRRRREEEE",new Gson().toJson(data),"");
                        try {
                            if (convertSwVersion(data.getOS_Version()) > checkSwVersion()) {
                                updateNotify = true;
                                ivUpdateNotify.setVisibility(View.VISIBLE);
                                return;
                            } else {
                                updateNotify = false;
                                ivUpdateNotify.setVisibility(View.INVISIBLE);
                            }

                            if (data.getVersionCode() > new CommonUtils().getLocalVersionCode()) {
                                Log.d("更新", "需要更新");
                                if (checkUpdateRxTimer != null) {
                                    checkUpdateRxTimer.cancel();
                                    checkUpdateRxTimer = null;
                                }
                                MyApplication.SSEB = false;
                                Intent intent = new Intent(DashboardActivity.this, UpdateSoftwareActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("fileUrl", data.getDownloadURL());
                                bundle.putString("md5", data.getMD5());
                                bundle.putBoolean("isForce", true);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail() {
                        Log.d("WEB_API-checkUpdate", "失敗");
                    }
                });
    }

    private void initDelay() {

        //  Looper.myQueue().addIdleHandler(() -> {

        initView();

        UserProfileEntity userProfileEntity = getInstance().getUserProfile();
        userType = userProfileEntity.getUserType();
        UNIT_E = userProfileEntity.getUnit();
        initUserData(userProfileEntity);

        //進入Dashboard後的起始畫面
        initIntoScreen();

        initEvent();

        m_tvTime_Dashboard.setText(setTime());

        EventBus.getDefault().register(this);


//        checkUpdateRxTimer = new RxTimer();
//        checkUpdateRxTimer.interval(1000 * 10, n -> checkUpdate());
        checkUpdate();
//            return false;
//        });


        Log.d("PPPPPPP", "initDelay: " + userProfileEntity.getUnit() +","+ userProfileEntity.getUserName() +","+MyApplication.UNIT_E);
    }

    /**
     * 進入Dashboard後的起始畫面
     */
    private void initIntoScreen() {

        int openSummary = getIntent().getIntExtra("openSummary", -1);
        //WorkOut結束 從WorkoutPause開Summary
        if (openSummary > -1) {
            //  WorkoutBean workoutBean = (WorkoutBean) getIntent().getExtras().getSerializable("WorkoutBean");
            WorkoutBean workoutBean = (WorkoutBean) getIntent().getSerializableExtra("WorkoutBean");
            Bundle bundleSend = new Bundle();
            rbPrograms_Dashboard.setChecked(true);
            //  bundleSend.putInt("item", openSummary);
            bundleSend.putSerializable("WorkoutBean", workoutBean);
            navController.navigate(R.id.programsSummaryFragment, bundleSend);
        } else {

            rbHomeScreen_Dashboard.setChecked(true);
            //Guest
            if (userType == 0) {
                long guestUid = getIntent().getLongExtra("GuestUid", 0);
                Bundle bundle = new Bundle();
                bundle.putLong("GuestUid", guestUid);
                navController.navigate(R.id.homeScreenGuestFragment, bundle);
            } else {

                boolean openProfile = getIntent().getBooleanExtra("OpenProfile", false);
                if (openProfile) rbProfile_Dashboard.setChecked(true);
                navController.navigate(openProfile ? R.id.profileFragment : R.id.homeScreenFragment);
            }
        }
    }

    private void initView() {

        floatingWidget = new FloatingWidget(this);

        //  navController = Navigation.findNavController(this, R.id.nhcDashboard);
        navController = (NavHostController) Navigation.findNavController(this, R.id.nhcDashboard);

        m_btSignOut_Dashboard = findViewById(R.id.btSignout_Dashboard);
        m_btHeartRate_Dashboard = findViewById(R.id.btHeartRate_Dashboard);
        m_btSound_Dashboard = findViewById(R.id.btSound_Dashboard);
        m_btFan_Dashboard = findViewById(R.id.btFan_Dashboard);
        m_btBluetooth_Dashboard = findViewById(R.id.btBlueTooth_Dashboard);

        m_tvTime_Dashboard = findViewById(R.id.tvTime_Dashboard);
        m_ivLineV1_Dashboard = findViewById(R.id.ivLineV1_Dashboard);
        m_ivLineV2_Dashboard = findViewById(R.id.ivLineV2_Dashboard);
        m_ivWifiIcon_Dashboard = findViewById(R.id.ivWifiIcon_Dashboard);

        m_ivWifiIcon_Dashboard.setOnClickListener(v -> {
            floatingWidget.callSetting(0, DashboardActivity.class);
            //   startSleepTimer();
        });

        ivAvatar_Dashboard = findViewById(R.id.ivAvatar_Dashboard);
        m_tvName_Dashboard = findViewById(R.id.tvName_Dashboard);

        ivUpdateNotify = findViewById(R.id.iv_update_notify);
        // ivUpdateNotify.setVisibility(updateNotify ? View.VISIBLE : View.INVISIBLE);
        ivUpdateNotify.setOnClickListener(v -> ivUpdateNotify.setVisibility(View.GONE));

        clDashboard = findViewById(R.id.clDashboard);
        rbHomeScreen_Dashboard = findViewById(R.id.rbHomeScreen_Dashboard);
        rbPrograms_Dashboard = findViewById(R.id.rbPrograms_Dashboard);
        rbProfile_Dashboard = findViewById(R.id.rbProfile_Dashboard);
        rbInternet_Dashboard = findViewById(R.id.rbInternet_Dashboard);
        rbCast_Dashboard = findViewById(R.id.rbCast_Dashboard);
        rbSettings_Dashboard = findViewById(R.id.rbSettings_Dashboard);
        iv_hr_connected = findViewById(R.id.iv_hr_connected);
        iv_bt_connected = findViewById(R.id.iv_bt_connected);
        iv_sound_connected = findViewById(R.id.iv_sound_connected);
        iv_fna_connected = findViewById(R.id.iv_fna_connected);

        indicator = findViewById(R.id.indicator);

        ivOptionBg = findViewById(R.id.ivOptionBg);

        m_btSound_Dashboard.setOnClickListener(v -> {
//            Intent intent2 = new Intent(DashboardActivity.this, SoundActivity.class);
//            Bundle bundle2 = new Bundle();
//            bundle2.putBoolean("isWorkout", false);
//            intent2.putExtras(bundle2);
//            startActivity(intent2);

            if (!Settings.canDrawOverlays(this)) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            } else {
                if (!FloatingSoundSettingService.isStarted) {
                    Intent serviceIntent = new Intent(this, FloatingSoundSettingService.class);
                    serviceIntent.putExtra("isWorkout", false);
                    startService(serviceIntent);
                    //    startSleepTimer();
                }
            }
        });

        m_btBluetooth_Dashboard.setOnClickListener(v -> {
            floatingWidget.callSetting(1, DashboardActivity.class);
            //  startSleepTimer();
        });

        m_btHeartRate_Dashboard.setOnClickListener(v -> {
//            Intent intent = new Intent(DashboardActivity.this, HeartRateActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putBoolean("isWorkout", false);
//            intent.putExtras(bundle);
//            startActivity(intent);

            if (!FloatingHeartRateService.isStarted) {
                Intent serviceIntent = new Intent(this, FloatingHeartRateService.class);
                serviceIntent.putExtra("isWorkout", false);
                startService(serviceIntent);
            }
        });

        //風扇
        m_btFan_Dashboard.setOnClickListener(v -> {
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
//        m_btFan_Dashboard.setOnClickListener(v -> {
//            if (btnFnaI == 0) {
//                m_btFan_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_fan1_9b9b9b_64 : R.drawable.btn_round_fan1_e6e6e6_64);
//                btnFnaI = 1;
//                getInstance().commandSetFan(Device.FAN.WEAK);
//            } else if (btnFnaI == 1) {
//                m_btFan_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_fan2_9b9b9b_64 : R.drawable.btn_round_fan2_e6e6e6_64);
//                btnFnaI = 2;
//                getInstance().commandSetFan(Device.FAN.MIDDLE);
//            } else if (btnFnaI == 2) {
//                m_btFan_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_fan3_9b9b9b_64 : R.drawable.btn_round_fan3_e6e6e6_64);
//                btnFnaI = 3;
//                getInstance().commandSetFan(Device.FAN.STRONG);
//            } else {
//                m_btFan_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_fan0_9b9b9b_64 : R.drawable.btn_round_fan0_e6e6e6_64);
//                btnFnaI = 0;
//                getInstance().commandSetFan(Device.FAN.STOP);
//            }
//        });

        //背景圖
        clDashboard.setBackgroundResource(R.drawable.bg_homescreen);

        rbHomeScreen_Dashboard.setOnClickListener(rbDashboardOnClick);
        rbPrograms_Dashboard.setOnClickListener(rbDashboardOnClick);
        rbProfile_Dashboard.setOnClickListener(rbDashboardOnClick);
        rbInternet_Dashboard.setOnClickListener(rbDashboardOnClick);
        rbCast_Dashboard.setOnClickListener(rbDashboardOnClick);
        rbSettings_Dashboard.setOnClickListener(rbDashboardOnClick);

        m_tvTime_Dashboard.setText(setTime());


        clDashboard.setOnTouchListener((v, event) -> {

            //  startSleepTimer();
            //   Log.d("SLEEP", "onTouch: ");
            v.performClick();
            return false;
        });
    }

    public void setIndicator(Banner banner) {
        indicator.setVisibility(View.VISIBLE);
        banner.setIndicator(indicator, false);
        banner.setIndicatorSelectedWidth((int) BannerUtils.dp2px(4));
        banner.setIndicatorWidth((int) BannerUtils.dp2px(4), (int) BannerUtils.dp2px(4));
        banner.setIndicatorHeight((int) BannerUtils.dp2px(4));
        banner.setIndicatorNormalColorRes(R.color.colorB4BEC7);
        banner.setIndicatorSelectedColorRes(R.color.colorE4002B);
        banner.setIndicatorSpace((int) BannerUtils.dp2px(24));
        banner.setIndicatorRadius(0);
    }

    /**
     * 隱藏分頁指示器
     */
    public void invisibleIndicator() {
        if (indicator != null) indicator.setVisibility(View.INVISIBLE);
        //   startSleepTimer();
    }


    /**
     * @param change    true 返回圖標， false 登出圖標
     * @param isDark    是否為黑色圖標
     * @param actionId  fragmentId， 0 - 返回
     * @param programId programId
     */
    public void changeSignOutToBack(boolean change, boolean isDark, int actionId, int programId) {

        invisibleIndicator();

        if (change) {
            //上一頁
            m_btSignOut_Dashboard.setBackgroundResource(R.drawable.btn_icon_back);
            m_btSignOut_Dashboard.setOnClickListener(v -> {
                if (actionId == 0) {
                    navController.navigateUp();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("item", programId);
                    navController.navigate(actionId, bundle);
                }
            });
        } else {
            //登出
            m_btSignOut_Dashboard.setBackgroundResource(isDark ? R.drawable.btn_round_signout_9b9b9b_64 : R.drawable.btn_round_signout_e6e6e6_64);
            m_btSignOut_Dashboard.setOnClickListener(v -> {


                if (userType == 0) {
                    DatabaseManager.getInstance(MyApplication.getInstance()).
                            updateUserProfile(getInstance().getUserProfile(), new DatabaseCallback<UserProfileEntity>() {
                                @Override
                                public void onUpdated() {
                                    super.onUpdated();
                                    MyApplication.SSEB = false;
                                    Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                    overridePendingTransition(0, android.R.anim.slide_out_right);
                                    CommonUtils.closePackage(DashboardActivity.this);
                                }

                                @Override
                                public void onError(String err) {
                                    super.onError(err);

                                    Toast.makeText(getInstance(), "Failure:" + err, Toast.LENGTH_LONG).show();
                                }
                            });
                } else {

                    MyApplication.SSEB = false;

                    Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                    // intent.putExtra("fragment_id", R.id.startScreenFragment);
                    startActivity(intent);
                    finishAffinity();
//                System.exit(0);
                    //   android.os.Process.killProcess(android.os.Process.myPid());
                    overridePendingTransition(0, android.R.anim.slide_out_right);

                    CommonUtils.closePackage(this);
                }
            });
        }
    }

    public void makeFullScreen(boolean fullScreen) {
        ConstraintLayout clPanelDashboard = findViewById(R.id.clPanelDashboard);
        clPanelDashboard.setVisibility(fullScreen ? View.INVISIBLE : View.VISIBLE);
    }

    public void changeTopWidgetStyle(boolean dark_bg) {
        try {

            this.dark_bg = dark_bg;

//            m_ivLineV1_Dashboard.setImageResource(dark_bg ? R.drawable.line_inversion : R.drawable.line);
//            m_ivLineV2_Dashboard.setImageResource(dark_bg ? R.drawable.line_inversion : R.drawable.line);

            m_ivLineV1_Dashboard.setBackgroundColor(dark_bg ? ContextCompat.getColor(this, R.color.color33FFFFFF) : ContextCompat.getColor(this, R.color.colorDEE2E6));
            m_ivLineV2_Dashboard.setBackgroundColor(dark_bg ? ContextCompat.getColor(this, R.color.color33FFFFFF) : ContextCompat.getColor(this, R.color.colorDEE2E6));

            m_ivWifiIcon_Dashboard.setImageResource(CommonUtils.setWifiImage(new CommonUtils().getWifiLevel(this), dark_bg));

            //    m_ivWifiIcon_Dashboard.setImageResource(dark_bg ? R.drawable.icon_wifi_inversion_lv4 : R.drawable.icon_wifi_lv4);

            m_tvName_Dashboard.setTextColor(dark_bg ? ContextCompat.getColor(this, R.color.colorFFFFFF) :
                    ContextCompat.getColor(this, R.color.color597084));

            m_tvTime_Dashboard.setTextColor(dark_bg ? ContextCompat.getColor(this, R.color.colorFFFFFF) :
                    ContextCompat.getColor(this, R.color.color597084));

            m_btHeartRate_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_heartrate_9b9b9b_64 : R.drawable.btn_round_heartrate_e6e6e6_64);
            m_btSound_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_sound_9b9b9b_64 : R.drawable.btn_round_sound_e6e6e6_64);

            m_btBluetooth_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_bluetooth_9b9b9b_64 : R.drawable.btn_round_bluetooth_e6e6e6_64);

            m_btSignOut_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_signout_9b9b9b_64 : R.drawable.btn_round_signout_e6e6e6_64);


            //  m_btFan_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_fan0_9b9b9b_64 : R.drawable.btn_round_fan0_e6e6e6_64);

            if (btnFnaI == 0) {
                m_btFan_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_fan0_9b9b9b_64 : R.drawable.btn_round_fan0_e6e6e6_64);
                iv_fna_connected.setVisibility(View.INVISIBLE);
                //  } else if (btnFnaI == 1) {
                //   m_btFan_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_fan1_9b9b9b_64 : R.drawable.btn_round_fan1_e6e6e6_64);
                //  } else if (btnFnaI == 2) {
                //      m_btFan_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_fan2_9b9b9b_64 : R.drawable.btn_round_fan2_e6e6e6_64);
            } else {
                m_btFan_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_fan0_9b9b9b_64 : R.drawable.btn_round_fan0_e6e6e6_64);
                iv_fna_connected.setVisibility(View.VISIBLE);
                //  m_btFan_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_fan3_9b9b9b_64 : R.drawable.btn_round_fan3_e6e6e6_64);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private final RadioButton.OnClickListener rbDashboardOnClick = new RadioButton.OnClickListener() {
        @Override
        public void onClick(View view) {

            //  startSleepTimer();

            int changeFragmentId = 0;
            int rbId = view.getId();

            ivOptionBg.setAlpha(1f);
            int currentId = 0;
            if (navController.getCurrentDestination() != null)
                currentId = navController.getCurrentDestination().getId();

            if (rbId == R.id.rbHomeScreen_Dashboard) {
                changeFragmentId = userType == 0 ? R.id.homeScreenGuestFragment : R.id.homeScreenFragment;
            } else if (rbId == R.id.rbPrograms_Dashboard) {
                changeFragmentId = R.id.programsFragment;
            } else if (rbId == R.id.rbProfile_Dashboard) {
                changeFragmentId = userType == 0 ? R.id.guestProfileFragment : R.id.profileFragment;
            } else if (rbId == R.id.rbInternet_Dashboard) {
                ivOptionBg.setAlpha(0.9f);
                changeFragmentId = R.id.internetFragment;
            } else if (rbId == R.id.rbCast_Dashboard) {
                changeFragmentId = R.id.castFragment;
            } else if (rbId == R.id.rbSettings_Dashboard) {
                changeFragmentId = R.id.settingsFragment;
            }

            if (currentId == changeFragmentId) return;

            try {
                navController.navigate(changeFragmentId, null);
                invisibleIndicator();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    };

    public void mirroringBack(int go) {

        //   if(isMirrorShow) return;
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = new Bundle();
        bundle.putInt("GO", go);
        intent.putExtras(bundle);

        intent.setClass(this, DashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);

    }

    //FLAG_ACTIVITY_SINGLE_TOP  single task
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        overridePendingTransition(0, 0);
        if (intent.getExtras() != null) {
            boolean Open_Templates = intent.getExtras().getBoolean("Open_Templates", false);
            //from SaveProgramSuccessActivity , DeleteProgramsConfirmActivity
            if (Open_Templates) {
                //開啟Program頁籤，再開啟Templates子頁籤
                Bundle bundleSend = new Bundle();
                bundleSend.putBoolean("Open_Templates", true);
                rbPrograms_Dashboard.setChecked(true);
                navController.navigate(R.id.programsFragment, bundleSend);

                return;
            }

            Log.d("CCCCC", "onNewIntent: " + Open_Templates);

            // from floatDashBoard
            int go = intent.getExtras().getInt("GO", 0);

            if (go <= 0) {
                rbHomeScreen_Dashboard.setChecked(true);
                return;
            }

//            int currentFragment = 0;
//            if (navController.getCurrentDestination() != null) {
//                currentFragment = navController.getCurrentDestination().getId();
//            }

            switch (go) {
                case MIRRORING_GO_HOME_SCREEN:
                    navController.navigate(userType == 0 ? R.id.homeScreenGuestFragment : R.id.homeScreenFragment);
                    rbHomeScreen_Dashboard.setChecked(true);
                    break;
                case MIRRORING_GO_PROGRAMS:
                    navController.navigate(R.id.programsFragment);
                    rbPrograms_Dashboard.setChecked(true);
                    break;
                case MIRRORING_GO_PROFILE:
                    navController.navigate(userType == 0 ? R.id.guestProfileFragment : R.id.profileFragment);
                    rbProfile_Dashboard.setChecked(true);
                    break;
                case MIRRORING_GO_WEB_VIEW:
                    navController.navigate(R.id.internetFragment);
                    rbInternet_Dashboard.setChecked(true);
                    break;
                case MIRRORING_GO_CAST:
                    navController.navigate(R.id.castFragment);
                    rbCast_Dashboard.setChecked(true);
                    break;
                case MIRRORING_GO_SETTING:
                    //   if (currentFragment != R.id.settingsFragment) {
                    navController.navigate(R.id.settingsFragment);
                    rbSettings_Dashboard.setChecked(true);
                    Log.d("ASDFG", "settingsFragment settingsFragment.setChecked(true): ");

                    //  }
                    break;
                case MIRRORING_SIGN_OUT:
                    m_btSignOut_Dashboard.callOnClick();
                    break;
            }
            //關閉 FLOATING_DASHBOARD
            RxBus.getInstance().post(new MsgEvent(STOP_FLOATING_DASHBOARD, true));
        }
    }

    public void initUserData(UserProfileEntity userProfileEntity) {
        userName = userProfileEntity.getUserName();
        m_tvName_Dashboard.setText(userName);

        int avatar = getAvatarHeaderResourceId(userProfileEntity.getUserImage());
        if (checkStr(userProfileEntity.getSoleHeaderImgUrl()) && isConnected(this)) {
            //已連接Sole帳號
            GlideApp.with(this)
                    .load(userProfileEntity.getSoleHeaderImgUrl())
                    // .placeholder(R.drawable.avatar_start_no)
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
    }

    private void initEvent() {

        Disposable d = RxBus.getInstance().toObservable(MsgEvent.class).subscribe(msg -> {

            if (msg.getType() == TIME_EVENT) {
                runOnUiThread(() -> m_tvTime_Dashboard.setText(msg.getObj().toString()));
            }

            if (msg.getType() == WIFI_EVENT) {
                int img = CommonUtils.setWifiImage((int) msg.getObj(), dark_bg);
                runOnUiThread(() -> m_ivWifiIcon_Dashboard.setImageResource(img));
            }

            //FTMS CONNECT
            if (msg.getType() == FTMS_NOTIFY_CONNECTED) {
                runOnUiThread(this::showFtmsConnectedIcon);
            }

            //SOUND CONNECT
            if (msg.getType() == BT_SOUND_CONNECT) {
                runOnUiThread(this::showSoundConnectedIcon);
            }

            if (msg.getType() == ON_ERROR) {
                showErrorDialogAlert((CommandErrorBean) msg.getObj());
            }

            if (msg.getType() == 101010) {
                RxBus.getInstance().post(new MsgEvent(HIDE_FLOATING_DASHBOARD, false));

                floatingWidget.callSetting((int) msg.getObj(), DashboardActivity.class);
            }

            if (msg.getType() == MIRRORING_GO_HOME_SCREEN) {
                //  RxBus.getInstance().post(new MsgEvent(MIRRORING_STOP,true));
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
                Log.d("HHHHHH", "VIDEO_BACK_HOME: ");
                mirroringBack(MIRRORING_GO_WEB_VIEW);
                return;
            }

            if (msg.getType() == MIRRORING_GO_CAST) {
                mirroringBack(MIRRORING_GO_CAST);
                return;
            }

            if (msg.getType() == MIRRORING_GO_SETTING) {
                mirroringBack(MIRRORING_GO_SETTING);
                return;
            }

            if (msg.getType() == MIRRORING_SIGN_OUT) {
                mirroringBack(MIRRORING_SIGN_OUT);
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

            if (msg.getType() == GO_SLEEP) {

                Log.d("休眠", "GO_SLEEP - 回登入頁111111: ");
                new RxTimer().timer(2000, number -> {

//                    MyApplication.SSEB = false;
//                    Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
//                    intent.putExtra("fragment_id", R.id.startScreenFragment); //不加會跑checkUARTConnect
//                    startActivity(intent);
//                    finishAffinity();
//                    updateNotify = true;
                    Log.d("休眠", "GO_SLEEP - @@@@@@回登入頁22222222: ");
//
//                  //  freeMemory();
//                    System.exit(0);
//                    android.os.Process.killProcess(android.os.Process.myPid());

                    try {
                        MyApplication.SSEB = false;
                        CommonUtils.closePackage(getApplicationContext());
                        Intent intent = new Intent(getInstance(), MainActivity.class);
                        PendingIntent restartIntent = PendingIntent.getActivity(getInstance(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mAlarmManager = (AlarmManager) getInstance().getSystemService(Context.ALARM_SERVICE);
                        mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, restartIntent); // 2秒鐘後重啟應用
                        android.os.Process.killProcess(android.os.Process.myPid());  //結束當前程序
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return;
            }

//            if (msg.getType() == SHOW_EXIT_BUTTON) {
//
//            }

            //顯示離開全螢幕的浮動按鈕
            if (msg.getType() == MIRRORING_EXIT_FULL_SCREEN) {
                int type = (int) msg.getObj();
                Log.d("多媒體", "收到MIRRORING_EXIT_FULL_SCREEN: type:" + type);
                FloatingDashboardService.isStarted = false;
                stopService(new Intent(getInstance(), FloatingDashboardService.class));
                showBtnFullScreenExit(type);
                return;
            }

            if (msg.getType() == REMOVE_BUTTON) {
                try {
                    ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
                    Method method = activityManager.getClass().getMethod("forceStopPackage", String.class);
                    method.setAccessible(true);
                    method.invoke(activityManager, "com.netflix.mediaclient");
                    method.invoke(activityManager, "com.hulu.plus");
                    method.invoke(activityManager, "com.abc.abcnews");
                    method.invoke(activityManager, "com.zumobi.msnbc");
                    method.invoke(activityManager, "com.cnn.mobile.android.phone");
                    method.invoke(activityManager, "com.foxnews.android");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                removeFloatView();
            }
        });
        compositeDisposable.add(d);
    }

    public void freeMemory() {

        try {
            System.runFinalization();
            Runtime.getRuntime().gc();
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
        DatabaseManager.getInstance(MyApplication.getInstance()).roomClear();
        BaseApi.apiClear();
        MMKV.defaultMMKV().encode("DashboardActivity", false);
        EventBus.getDefault().unregister(this);

        if (errorDialog != null) errorDialog.dismiss();
        //  if (rxSleepTimer != null) rxSleepTimer.cancel();

        removeFloatView();
        stopService(new Intent(this, FloatingSoundSettingService.class));

        if (checkUpdateRxTimer != null) {
            checkUpdateRxTimer.cancel();
            checkUpdateRxTimer = null;
        }

        try {
            floatingWidget.removeFloatingView();
            floatingWidget = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        //    MyApplication.SSEB = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //  if (rxSleepTimer != null) rxSleepTimer.cancel();

        Log.d("JJJJJJ", " onPause: show exit button:" + MyApplication.SSEB);
        if (MyApplication.SSEB) showBtnFullScreenExit(4);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //   if (MyApplication.SSEB) showBtnFullScreenExit(4);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        MODE = getMode(getInstance().getDeviceSettingBean().getModel_code());

        getInstance().commandSetUsbMode(Device.USB_MODE.CHARGER);
        getInstance().commandSetHeartRateMode(Device.HEART_RATE_MODE.NORMAL);
        getInstance().commandSetLwrMode(Device.LWR_MODE.NORMAL);
        getInstance().commandSetEchoMode(Device.ECHO_MODE.AA);

        MMKV.defaultMMKV().encode("DashboardActivity", true);
        MMKV.defaultMMKV().encode("CheckUpdateIng", false);

        checkBtDevice();
        showHrConnectedIcon();
        showSoundConnectedIcon();
        showFtmsConnectedIcon();
        showFanIcon();

        //睡眠關機計時
        //   startSleepTimer();
        MyApplication.SSEB = true;
        //  Log.d("浮動按鈕", "onResume DashboardActivity: " + MyApplication.SSEB);

        //  canSleep = true;

        try {
            // checkUpdate();
            updateNotify = MyApplication.NEW_SW_VERSION > checkSwVersion();
            ivUpdateNotify.setVisibility(updateNotify ? View.VISIBLE : View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        removeFloatView();

        try {
            floatingWidget.removeFloatingView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ii() {
        List<AccessibilityWindowInfo> windows = new AccessibilityService() {
            @Override
            public void onAccessibilityEvent(AccessibilityEvent event) {
                AccessibilityNodeInfo mSource = event.getSource();
                int child = mSource.getChildCount();

                Log.d("PIP", "onAccessibilityEvent: " + child);
//                // iterate through all child of parent view
//                for (int i=0; i<child; i++){
//                    AccessibilityNodeInfo childNodeView = mParent.getChild(i);
//                    // Do something with this window content
//                }
            }

            @Override
            public void onInterrupt() {

            }
        }.getWindows();

        for (AccessibilityWindowInfo accessibilityWindowInfo : windows) {
            Log.d("PIP", ": " + accessibilityWindowInfo.toString());
        }
    }

    //睡眠關機計時
    public void startSleepTimer() {
//        if (rxSleepTimer != null) {
//            rxSleepTimer.cancel();
//            rxSleepTimer = null;
//        }
//        //睡眠關機計時
//        if (1 == getInstance().getUserProfile().getSleepMode()) {
//            Log.d("SLEEP", "睡眠計時開始: " + SLEEP_TIME);
//            rxSleepTimer = new RxTimer();
//            rxSleepTimer.timer(SLEEP_TIME, this::onSleep);
//        }
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

//    private void showHrConnectedIcon() {
//        //hr連結圖示
//        try {
//            if (getInstance().mBleEventManager.getConnectedBleDevices() != null &&
//                    getInstance().mBleEventManager.getConnectedBleDevices().size() > 0 && isBlueToothOn == 0) {
//                if (iv_hr_connected != null) iv_hr_connected.setVisibility(View.VISIBLE);
//            } else {
//                if (iv_hr_connected != null) iv_hr_connected.setVisibility(View.INVISIBLE);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void showHrConnectedIcon() {
        //hr連結圖示
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

    private void showFanIcon() {
        if (btnFnaI == 0) {
            m_btFan_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_fan0_9b9b9b_64 : R.drawable.btn_round_fan0_e6e6e6_64);
            iv_fna_connected.setVisibility(View.INVISIBLE);
            //  } else if (btnFnaI == 1) {
            //   m_btFan_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_fan1_9b9b9b_64 : R.drawable.btn_round_fan1_e6e6e6_64);
            //  } else if (btnFnaI == 2) {
            //      m_btFan_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_fan2_9b9b9b_64 : R.drawable.btn_round_fan2_e6e6e6_64);
        } else {
            m_btFan_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_fan0_9b9b9b_64 : R.drawable.btn_round_fan0_e6e6e6_64);
            iv_fna_connected.setVisibility(View.VISIBLE);
            //  m_btFan_Dashboard.setBackgroundResource(dark_bg ? R.drawable.btn_round_fan3_9b9b9b_64 : R.drawable.btn_round_fan3_e6e6e6_64);
        }
    }

    private void showFtmsConnectedIcon() {
        //ftms連結圖示
//        try {
//            if (isFtmsConnected) {
//                if (iv_bt_connected != null) iv_bt_connected.setVisibility(View.VISIBLE);
//            } else {
//                if (iv_bt_connected != null) iv_bt_connected.setVisibility(View.INVISIBLE);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    //  private RxTimer rxSleepTimer;

    private void onSleep(long sec) {
        Log.d("SLEEP", "canSleep: " + canSleep);
        if (getInstance().getUserProfile().getSleepMode() == 1) {
            if (canSleep) {
                Log.d("SLEEP", "sleep..... ");
                Toast.makeText(this, "Sleeping..", Toast.LENGTH_LONG).show();
                getInstance().commandSetEup(2);
            }
        }
    }

    Dialog lockedDialog;

    public void showDialogAlert() {
//        if (!MMKV.defaultMMKV().decodeBool(IS_LOCK, false)) {
//            return;
//        }

        if (getInstance().getDeviceSettingBean().getChild_lock() == 0 || IS_CHILD_LOCKING) {
            return;
        }

        IS_CHILD_LOCKING = true;
        lockedDialog = new Dialog(DashboardActivity.this, android.R.style.ThemeOverlay);
        lockedDialog.setCanceledOnTouchOutside(false);
        lockedDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        //   lockedDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        lockedDialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        lockedDialog.setContentView(R.layout.dialog_console_locked);
        lockedDialog.show();
    }

    private Dialog errorDialog;

    public void showErrorDialogAlert(CommandErrorBean commandErrorBean) {
        Log.d("KKKKKK", "showErrorDialogAlert: " + commandErrorBean.toString());

        runOnUiThread(() -> {
            if (errorDialog == null || !errorDialog.isShowing()) {
                errorDialog = new Dialog(DashboardActivity.this, android.R.style.ThemeOverlay);
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
                    textView.setText("ERROR");
                    textView2.setText(commandErrorBean.getErrorMessage() == null ? "" : commandErrorBean.getErrorMessage());
                } else {

                    textView.setText(commandErrorBean.getCommand() == null ? "" : commandErrorBean.getCommand().toString());
                    textView2.setText(commandErrorBean.getCommandError() == null ? "" : commandErrorBean.getCommandError().toString());
                }
            }
        });
    }

    /**
     * EventBus Receiver
     * MAIN Android主線程，阻塞主線程的發布 進入主線程的隊列
     *
     * @param isBusEvent EventEntity
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommandEvent(isBusEvent isBusEvent) {

        switch (isBusEvent.getEventType()) {
            case COMMAND_SET_CONTROL:
                // Log.i("UART_CONN_EVENT_BUS", "COMMAND_SET_CONTROL: " + ((McuBean) isBusEvent).toString() + ",@@@RPM:" + mRPM);
                break;
            case COMMAND_KEY: //KEYBOARD

                KeyBean keyBean = ((KeyBean) isBusEvent);
                if (keyBean.getKeyStatus() == 0) {

                    if (IS_CHILD_LOCKING) {
                        return;
                    }

                    switch (keyBean.getKey()) {
                        case KEY01:

                            m_btFan_Dashboard.callOnClick();

                            break;
                        case KEY05:
                            startWorkout();
                            break;
                        case KEY03://stop

                            if (navController.getCurrentDestination() != null) {
                                if (navController.getCurrentDestination().getId() == R.id.programsSummaryFragment) {
                                    if (rbPrograms_Dashboard.isChecked()) {
                                        getInstance().commandSetBuzzer(Device.BEEP.SHORT, 1);
                                        rbHomeScreen_Dashboard.setChecked(true);
                                        int changeFragmentId = userType == 0 ? R.id.homeScreenGuestFragment : R.id.homeScreenFragment;
                                        navController.navigate(changeFragmentId, null);
                                    }
                                }
                            }
                            break;
                    }
                } else {
                    //多重點擊 > 解鎖
                    if (lockedDialog != null) {
                        lockedDialog.dismiss();
                        lockedDialog = null;
                        IS_CHILD_LOCKING = false;
                    }
                }

                break;

            case FTMS_NOTIFY: //FTMS

//                if (getInstance().getDeviceSettingBean().getChild_lock() == 0) {
//                    return;
//                }

                if (IS_CHILD_LOCKING) {
                    return;
                }

                switch (((FTMSBean) isBusEvent).getFtmsNotifyType()) {
                    case FTMS_NOTIFY_START_OR_RESUME:
                        if (((FTMSBean) isBusEvent).getStartOrResume() == 1) { //START
                            startWorkout();
                        }
                }
        }
    }

    private boolean isStartWorkout = false;

    private void startWorkout() {

        //呼叫子fragment的method
//        NavHostFragment fragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nhcDashboard);
//        InternetFragment frag = (InternetFragment) fragment.getChildFragmentManager().getFragments().get(0);
//        frag.xxxx();

        if (!isStartWorkout) {

            isStartWorkout = true;

            if (isFtmsConnected && isFTMSNotify) {
                try {
                    getInstance().mFTMSManager.notifyStartOrResume();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            getInstance().commandSetBuzzer(Device.BEEP.SHORT, 1);

            WorkoutBean workoutBean = new WorkoutBean();
            workoutBean.setProgramName(MANUAL.getText());
            workoutBean.setProgramId(MANUAL.getCode());
            workoutBean.setBaseProgramId(MANUAL.getCode());
            workoutBean.setOrgMaxLevel(20);
            workoutBean.setInclineDiagramNum(MANUAL.getInclineNum());
            workoutBean.setLevelDiagramNum(MANUAL.getLevelNum());
            workoutBean.setMaxLevel(20);
            workoutBean.setTimeSecond(0);

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("WorkoutBean", workoutBean);
            intent.putExtras(bundle);
            intent.setClass(this, WorkoutDashboardActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            MyApplication.SSEB = false;
        }
    }

    @SuppressLint("MissingPermission")
    private void checkBtDevice() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            int[] profiles = {BluetoothProfile.A2DP};
            boolean connectionExists = false;
            for (int profileId : profiles) {
                if (adapter.getProfileConnectionState(profileId) == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("BT_SOUND", "profileId: " + profileId);
                    connectionExists = true;
                    break;
                }
            }
            isSoundConnected = connectionExists;
            Log.d("BT_SOUND", "connectionExists: " + connectionExists);
        }
    }


    private Button btnFullScreenExit;
    private WindowManager windowManager_exitButton;
    private WindowManager.LayoutParams layoutParams;
    public boolean floatButtonIsStarted;
    private boolean m_bOnClick;
    private long m_lStartTime;

    @SuppressLint("ClickableViewAccessibility")
    public void showBtnFullScreenExit(int type) {

        if (floatButtonIsStarted) return;
        try {
            FloatingDashboardService.isStarted = false;
            stopService(new Intent(getInstance(), FloatingDashboardService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("HHHHHH", "showBtnFullScreenExit: " + type);
        floatButtonIsStarted = true;
        windowManager_exitButton = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 172;
        layoutParams.height = 172;
        layoutParams.x = 1000;
        layoutParams.y = 0;

        btnFullScreenExit = new Button(getApplicationContext());
        btnFullScreenExit.setAlpha(0.9f);
        btnFullScreenExit.setBackgroundResource(R.drawable.btn_icon_screenfull_exit);
        windowManager_exitButton.addView(btnFullScreenExit, layoutParams);
        btnFullScreenExit.setOnTouchListener(new FloatingOnTouchListener());
        btnFullScreenExit.setOnClickListener(v -> {
            //點擊離開全螢幕的float按鈕
            if (CommonUtils.isFastClick()) return;

            try {

                startFloatingDashboard(type);

                new RxTimer().timer(200, number -> {
                    try {
                        windowManager_exitButton.removeView(btnFullScreenExit);
                        floatButtonIsStarted = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

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

                    try {
                        windowManager_exitButton.updateViewLayout(view, layoutParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    long m_lEndTime = System.currentTimeMillis();
                    m_bOnClick = (m_lEndTime - m_lStartTime) > 0.1 * 5000L;
                    break;
                default:
                    break;
            }
            return m_bOnClick;
        }
    }

    public void startFloatingDashboard(int type) {
        if (!FloatingDashboardService.isStarted) { //FloatingDashboardService 尚未開啟，重建

            int currentFragment = 0;
            if (navController.getCurrentDestination() != null) {
                currentFragment = navController.getCurrentDestination().getId();
            }

            if (type == 4) {
                if (currentFragment != R.id.internetFragment) {
                    rbInternet_Dashboard.setChecked(true);
                    Log.d("ASDFG", "startFloatingDashboard _rbInternet_Dashboard.setChecked(true): ");
                    navController.navigate(R.id.internetFragment);
                }
            } else {
                if (currentFragment != R.id.castFragment) {
                    rbCast_Dashboard.setChecked(true);
                    navController.navigate(R.id.castFragment);
                }
            }

            Intent serviceIntent = new Intent(this, FloatingDashboardService.class);
            serviceIntent.putExtra("TYPE", type);
            startService(serviceIntent);

        }
    }

    public void removeFloatView() {

        try {
            FloatingDashboardService.isStarted = false;
            stopService(new Intent(getInstance(), FloatingDashboardService.class));


            //關閉 離開全螢幕的按鈕
            if (windowManager_exitButton != null && btnFullScreenExit != null) {
                floatButtonIsStarted = false;
                windowManager_exitButton.removeView(btnFullScreenExit);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            windowManager_exitButton = null;
            btnFullScreenExit = null;
        }
    }
}