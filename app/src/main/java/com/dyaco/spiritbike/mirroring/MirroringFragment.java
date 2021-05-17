package com.dyaco.spiritbike.mirroring;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.engineer.DeviceSettingBean;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.RxBus;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.hpplay.sdk.sink.api.CastInfo;
import com.hpplay.sdk.sink.api.ClientInfo;
import com.hpplay.sdk.sink.api.IAPI;
import com.hpplay.sdk.sink.api.IServerListener;
import com.hpplay.sdk.sink.api.ServerInfo;


import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.dyaco.spiritbike.MyApplication.MIRRORING_STOP;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.CommonUtils.isConnected;
import static com.dyaco.spiritbike.support.CommonUtils.showToastAlert;

public class MirroringFragment extends BaseFragment {
    private AllCast mAllCast;
    private ConstraintLayout m_cl_ios;
    private ConstraintLayout m_cl_android;
    private TextView m_tvPrograms_Programs;
    private TextView m_tvFitnessTests_Programs;
    private boolean isWorkout;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private TextView ios_bottom_text;
    private TextView android_bottom_text;
    private TextView android_top_text;
    private TextView ios_top_text;

    private String wifiName = "";
    private String deviceName = "";
    private String bmName;
    public boolean mirrorRun;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((DashboardActivity) mActivity).isMirrorShow = true;
        if (getArguments() != null) {
            isWorkout = getArguments().getBoolean("isWorkout", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int layout = isWorkout ? R.layout.fragment_mirroring_workout : R.layout.fragment_mirroring;

        return inflater.inflate(layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       // CommonUtils.closePackage(mActivity);

        if (!isConnected(mActivity)) {
            showToastAlert(getString(R.string.no_internet_toast_text), mActivity);
        }
        DeviceSettingBean deviceSettingBean = getInstance().getDeviceSettingBean();
        int selectedColor = R.color.colorE4002B;

        View cl_base = view.findViewById(R.id.clPrograms);
        if (isWorkout) {
            cl_base.setBackgroundResource(R.drawable.background_black);
            //   selectedColor = R.color.colorFFFFFF;
        } else {
            ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
            ((DashboardActivity) mActivity).changeSignOutToBack(false, false, 0, -1);

            //   selectedColor = R.color.colorE4002B;
        }

        bmName = deviceSettingBean.getBrand_name() + " " + deviceSettingBean.getModel_name();

        //  Log.d("QQQQQQQQQ", "onViewCreated: " + deviceSettingBean.getModel_name());

        m_cl_ios = view.findViewById(R.id.cl_ios);
        m_cl_android = view.findViewById(R.id.cl_android);

        RadioGroup rgOption_Profile = view.findViewById(R.id.rgOption_Programs);

        m_tvPrograms_Programs = view.findViewById(R.id.tvPrograms_Programs);
        m_tvFitnessTests_Programs = view.findViewById(R.id.tvFitnessTests_Programs);

        ios_top_text = view.findViewById(R.id.ios_top_text);
        ios_bottom_text = view.findViewById(R.id.ios_bottom_text);
        android_top_text = view.findViewById(R.id.android_top_text);
        android_bottom_text = view.findViewById(R.id.android_bottom_text);

        //  showWifi_DeviceName();

        rgOption_Profile.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbPrograms_Programs) {

                m_tvPrograms_Programs.setTextColor(ContextCompat.getColor(mActivity, selectedColor));
                m_tvFitnessTests_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorB4BEC7));

                m_cl_ios.setVisibility(View.VISIBLE);
                m_cl_android.setVisibility(View.INVISIBLE);

            } else if (checkedId == R.id.rbFitnessTests_Programs) {

                m_tvPrograms_Programs.setTextColor(ContextCompat.getColor(mActivity, R.color.colorB4BEC7));
                m_tvFitnessTests_Programs.setTextColor(ContextCompat.getColor(mActivity, selectedColor));

                m_cl_ios.setVisibility(View.INVISIBLE);
                m_cl_android.setVisibility(View.VISIBLE);
            }
        });

        //  initLelink();

        initEvent();


        initDelay(view);

    }

    private void initDelay(View view) {

        Looper.myQueue().addIdleHandler(() -> {
            showWifi_DeviceName();
            initLelink();
            return false;
        });
    }

    private void showWifi_DeviceName() {
        try {

            wifiName = new CommonUtils().getSSID(requireActivity());

         //   ios_top_text.setText(getString(isWorkout ? R.string.ios_top_text2 : R.string.ios_top_text, bmName));
         //   android_top_text.setText(getString(isWorkout ? R.string.android_top_text2 : R.string.android_top_text, bmName));

            ios_bottom_text.setText(getString(isWorkout ? R.string.ios_bottom_text2 : R.string.ios_bottom_text, wifiName, deviceName));
            android_bottom_text.setText(getString(isWorkout ? R.string.ios_bottom_text2 : R.string.ios_bottom_text, wifiName, deviceName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initLelink() {

        mAllCast = new AllCast(MyApplication.getInstance(), getString(R.string.app_id), getString(R.string.app_secret));
        mAllCast.setDeviceName(bmName);
        mAllCast.setDisplayFrameRateSwitch(false);

        //    mAllCast.setLanguage(IAPI.LANG_EN);

        //  mAllCast.setSurfaceType(IAPI.SURFACE_SURFACEVIEW);
        //   mAllCast.setSurfaceType(IAPI.SURFACE_TEXTUREVIEW);

        //   mAllCast.resetPlayerWhenMirror(IAPI.MIRROR_RESET_CLOSE);
        //   mAllCast.setDisplayMode(0);
        mAllCast.setServerListener(mServerListener);

        mAllCast.startServer();
    }

    private static final String TAG = "MirroringFragment";
    private IServerListener mServerListener = new IServerListener() {
        @Override
        public void onStart(int id, ServerInfo info) {
            String deviceName2 = "onStart service: " + id + " deviceName: " + info.deviceName;
            Log.d(TAG, "服務發佈成功 deviceName: " + deviceName2);
            deviceName = info.deviceName;
            showWifi_DeviceName();
            // ios_bottom_text.setText(getString(isWorkout ? R.string.ios_bottom_text2 : R.string.ios_bottom_text, wifiName, deviceName));
        }

        @Override
        public void onStop(int id) {
            String info = "onStop service: " + id;
            Log.i(TAG, "停止服務 info: " + info);
        }

        @Override
        public void onError(int id, int what, int extra) {
            String info = "onError service: " + id + " what: " + what + " extra: " + extra;
            Log.i(TAG, "服務發佈失敗 info: " + info);
        }

        @Override
        public void onAuthSDK(int id, int status) {
            Log.i(TAG, "onAuthSDK status: " + status);
            switch (status) {
                case IServerListener.SDK_AUTH_SUCCESS:
                    Log.i(TAG, "認證成功");
                    break;
                case IServerListener.SDK_AUTH_FAILED:
                    Log.i(TAG, "認證失败");
                    break;
                case IServerListener.SDK_AUTH_SERVER_FAILED:
                    Log.i(TAG, "連接認證服務器失敗");
                    break;
            }
        }

        @Override
        public void onCast(int id, CastInfo info) {
            if (!mirrorRun) {
                CommonUtils.closePackage(mActivity);
                mirrorRun = true;
                MyApplication.SSEB = false;
            }
            //會跑好幾次
            mActivity.overridePendingTransition(0, 0);
            Log.d("樂播", "onCast: 開小按鈕");

            ((DashboardActivity) mActivity).showBtnFullScreenExit(5);
        }

        @Override
        public void onAuthConnect(int id, String authCode, int expiry) {
            // 若开启随机投屏码功能，在发送端连接的时候，SDK会将生成的随机投屏码回调到这里
            // authCode 为生成的投屏码，expiry为投屏码的有限时间，单位秒
            String info = "onAuth service: " + id + " authCode: " + authCode + "\nexpiry: " + expiry;
            Log.i("MMMMMM", "投屏碼連接 info: " + info);
        }

        @Override
        public void onConnect(int id, ClientInfo clientInfo) {
            // Log.i("MMMMMM", "新用户連接 " + "onConnect id: " + id + " info:" + clientInfo);
        }

        @Override
        public void onDisconnect(int i, ClientInfo clientInfo) {
            //预留接口，暂未实现
        }
    };

    private void initEvent() {

        Disposable d1 = RxBus.getInstance().toObservable(MsgEvent.class).subscribe(msg -> {

            if (msg.getType() == MIRRORING_STOP) {
                mAllCast.getIPlayer().stop();
                ((DashboardActivity)mActivity).removeFloatView();
            }
        });

        compositeDisposable.add(d1);
    }

    public void mirroringStop() {
        mAllCast.getIPlayer().stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAllCast != null) mAllCast.stopServer();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((DashboardActivity) mActivity).isMirrorShow = false;

        if (mAllCast != null) mAllCast.stopServer();
        mServerListener = null;
        compositeDisposable.clear();

        ((DashboardActivity) mActivity).removeFloatView();
        mirrorRun = false;

        m_cl_android = null;
        m_cl_ios = null;

        m_tvPrograms_Programs = null;
        m_tvFitnessTests_Programs = null;
        ios_bottom_text = null;
        android_bottom_text = null;


        MyApplication.SSEB = true;
    }

    @Override
    public void onResume() {
        super.onResume();

        mActivity.overridePendingTransition(0, 0);
        ((DashboardActivity) mActivity).removeFloatView();

    }
}


