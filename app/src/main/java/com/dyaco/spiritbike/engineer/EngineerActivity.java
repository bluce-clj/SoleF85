package com.dyaco.spiritbike.engineer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.corestar.libs.device.Device;
import com.dyaco.spiritbike.CommandErrorBean;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MainActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.mirroring.FloatingDashboardService;
import com.dyaco.spiritbike.product_flavors.ModeEnum;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.LoadingDialog;
import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.RxBus;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.workout.WorkoutDashboardActivity;
import com.google.android.material.tabs.TabLayout;
import com.tencent.mmkv.MMKV;

import es.dmoral.toasty.Toasty;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.dyaco.spiritbike.MyApplication.MODE;
import static com.dyaco.spiritbike.MyApplication.ON_ERROR;
import static com.dyaco.spiritbike.MyApplication.ON_ERROR2;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.MyApplication.isAutoTest;
import static com.dyaco.spiritbike.product_flavors.ModeEnum.XE395ENT;

public class EngineerActivity extends BaseAppCompatActivity {
    public ViewPager viewPager;
    TabLayout tabLayout;
    public Button btBack;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private DeviceSettingBean deviceSettingBean;
    public LoadingDialog loadingDialog;

    private BtnExitFullScreen btnExitFullScreen = new BtnExitFullScreen(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engineer);
        loadingDialog = new LoadingDialog(EngineerActivity.this);
        setTabLayout();
        setViewPager();
        setListener();

        btBack = findViewById(R.id.btBack);

        btBack.setOnClickListener(v -> {
            MyApplication.SSEB = false;
            finish();
        });

        initEvent();
    }

    private void setListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    public void setTabLayout() {
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("FUNCTION"));
        tabLayout.addTab(tabLayout.newTab().setText("SERVICE"));
        tabLayout.addTab(tabLayout.newTab().setText("FACTORY SETTING"));
        if (MODE == XE395ENT) tabLayout.addTab(tabLayout.newTab().setText("CALIBRATION"));
    }

    private void setViewPager() {
        viewPager = findViewById(R.id.viewPager);
        CustomAdapter customAdapter = new CustomAdapter(getSupportFragmentManager());
        viewPager.setAdapter(customAdapter);
        viewPager.setOffscreenPageLimit(4);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInstance().commandSetHeartRateMode(Device.HEART_RATE_MODE.ENGINEERING);
        MMKV.defaultMMKV().encode("EngineerActivity", true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        btnExitFullScreen.removeFloatView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAutoTest = false;
     //   getInstance().commandSetLwrMode(Device.LWR_MODE.NORMAL);
     //   getInstance().commandSetHeartRateMode(Device.HEART_RATE_MODE.NORMAL);

        MMKV.defaultMMKV().encode("EngineerActivity", false);

        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }

        btnExitFullScreen.removeFloatView();
        btnExitFullScreen = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void initEvent() {
        Disposable d = RxBus.getInstance().toObservable(MsgEvent.class).subscribe(msg -> {

            if (msg.getType() == ON_ERROR2) {
                runOnUiThread(() -> {
                    String s ;
                    CommandErrorBean commandErrorBean = (CommandErrorBean) msg.getObj();
                    if (commandErrorBean.getErrorType() == 1) {
                        s = "ERROR:" + commandErrorBean.getErrorMessage();
                    } else {
                        s = commandErrorBean.getCommand().toString() + ":" + commandErrorBean.getCommandError().toString();
                    }

                    Toasty.error(getInstance(), s, Toasty.LENGTH_SHORT).show();
                });
            }

        });

        compositeDisposable.add(d);
    }

    @Override
    protected void onPause() {
        super.onPause();
        btnExitFullScreen.showBtnFullScreenExit(DashboardActivity.class, false);
    }
}