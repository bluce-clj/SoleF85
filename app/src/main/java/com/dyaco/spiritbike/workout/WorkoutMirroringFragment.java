package com.dyaco.spiritbike.workout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
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
import com.dyaco.spiritbike.mirroring.AllCast;

import com.dyaco.spiritbike.mirroring.FloatingWorkoutDashboardService;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.RxBus;
import com.dyaco.spiritbike.support.RxTimer;
import com.hpplay.sdk.sink.api.CastInfo;
import com.hpplay.sdk.sink.api.ClientInfo;
import com.hpplay.sdk.sink.api.IAPI;
import com.hpplay.sdk.sink.api.IServerListener;
import com.hpplay.sdk.sink.api.ServerInfo;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static android.content.Context.WINDOW_SERVICE;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_EXIT_FULL_SCREEN;
import static com.dyaco.spiritbike.MyApplication.MIRRORING_STOP;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.CommonUtils.isConnected;
import static com.dyaco.spiritbike.support.CommonUtils.showToastAlert;

public class WorkoutMirroringFragment extends BaseFragment {
    private AllCast mAllCast;
    private ConstraintLayout m_cl_ios;
    private ConstraintLayout m_cl_android;
    private TextView m_tvPrograms_Programs;
    private TextView m_tvFitnessTests_Programs;
    private boolean isWorkout;
    private CompositeDisposable compositeDisposable;
    private TextView ios_bottom_text;
    private TextView android_bottom_text;

    private String wifiName = "";
    private String deviceName = "";
    private WorkoutDashboardActivity parent;
    private String bmName;
    private TextView android_top_text;
    private TextView ios_top_text;
    private boolean mirrorRun;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent = (WorkoutDashboardActivity) mActivity;
        parent.isMirrorShow = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_mirroring_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!isConnected(mActivity)) {
            showToastAlert(getString(R.string.no_internet_toast_text), mActivity);
        }

        DeviceSettingBean deviceSettingBean = getInstance().getDeviceSettingBean();
        bmName = deviceSettingBean.getBrand_name() + " " + deviceSettingBean.getModel_name();
        Log.d(TAG, "onViewCreated: ");
        compositeDisposable = new CompositeDisposable();
        int selectedColor = R.color.colorE4002B;

        View cl_base = view.findViewById(R.id.clPrograms);
        cl_base.setBackgroundResource(R.drawable.background_black);

        m_cl_ios = view.findViewById(R.id.cl_ios);
        m_cl_android = view.findViewById(R.id.cl_android);

        RadioGroup rgOption_Profile = view.findViewById(R.id.rgOption_Programs);

        m_tvPrograms_Programs = view.findViewById(R.id.tvPrograms_Programs);
        m_tvFitnessTests_Programs = view.findViewById(R.id.tvFitnessTests_Programs);

        ios_top_text = view.findViewById(R.id.ios_top_text);
        ios_bottom_text = view.findViewById(R.id.ios_bottom_text);
        android_top_text = view.findViewById(R.id.android_top_text);
        android_bottom_text = view.findViewById(R.id.android_bottom_text);

        showWifi_DeviceName();

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

        initLelink();

        try {
            initEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showWifi_DeviceName() {

        try {

            wifiName = new CommonUtils().getSSID(requireActivity());

//            ios_top_text.setText(getString(R.string.ios_top_text2, bmName));
//            android_top_text.setText(getString(R.string.android_top_text2, bmName));

            ios_bottom_text.setText(getString(R.string.ios_bottom_text2, wifiName, deviceName));
            android_bottom_text.setText(getString(R.string.ios_bottom_text2, wifiName, deviceName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initLelink() {
        // TODO: 2/18/21  AllCast mAllCast = new AllCast(MyApplication.getInstance(), getString(R.string.app_id), getString(R.string.app_secret));
        mAllCast = new AllCast(MyApplication.getInstance(), getString(R.string.app_id), getString(R.string.app_secret));
        mAllCast.setDeviceName(bmName);
        mAllCast.setSurfaceType(IAPI.SURFACE_SURFACEVIEW);
        mAllCast.setDisplayFrameRateSwitch(false);
        mAllCast.setLanguage(IAPI.LANG_EN);
        mAllCast.resetPlayerWhenMirror(IAPI.MIRROR_RESET_CLOSE);
        mAllCast.setServerListener(mServerListener);
        mAllCast.startServer();
    }

    private static final String TAG = "MirroringFragment";
    private final IServerListener mServerListener = new IServerListener() {
        @Override
        public void onStart(int id, ServerInfo info) {
            String deviceName2 = "onStart service: " + id + " deviceName: " + info.deviceName;
            Log.d(TAG, "服務發佈成功 deviceName: " + deviceName2);
            deviceName = info.deviceName;
            showWifi_DeviceName();
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

            mActivity.overridePendingTransition(0, 0);

            if (!((WorkoutDashboardActivity) mActivity).onWorkoutStopIng)
                ((WorkoutDashboardActivity) mActivity).showBtnFullScreenExit(5);
        }

        @Override
        public void onAuthConnect(int id, String authCode, int expiry) {
        }

        @Override
        public void onConnect(int id, ClientInfo clientInfo) {
        }

        @Override
        public void onDisconnect(int i, ClientInfo clientInfo) {
        }
    };


    private void initEvent() {

        Disposable d1 = RxBus.getInstance().toObservable(MsgEvent.class).subscribe(msg -> {
            if (msg.getType() == MIRRORING_STOP) {
                if (!parent.isMirrorShow) return;
                if (mAllCast != null) mAllCast.getIPlayer().stop();
            }
        });

        compositeDisposable.add(d1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAllCast != null) mAllCast.stopServer();
        mAllCast = null;
        if (compositeDisposable != null) compositeDisposable.dispose();

        ((WorkoutDashboardActivity) mActivity).removeFloatView();
        mActivity.stopService(new Intent(mActivity, FloatingWorkoutDashboardService.class));


        m_cl_ios = null;
        m_cl_android = null;
        m_tvPrograms_Programs = null;
        m_tvFitnessTests_Programs = null;
        ios_bottom_text = null;
        android_bottom_text = null;
        parent = null;

    }

    @Override
    public void onResume() {
        super.onResume();

        mActivity.overridePendingTransition(0, 0);
        ((WorkoutDashboardActivity) mActivity).removeFloatView();
        mirrorRun = false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        parent.isMirrorShow = !hidden;

        if (hidden) {
            Log.d(TAG, "onHiddenChanged:stop ");
            if (mAllCast != null) mAllCast.stopServer();
            ios_bottom_text.setText(getString(R.string.ios_bottom_text2, wifiName, ""));
            android_bottom_text.setText(getString(R.string.ios_bottom_text2, wifiName, ""));
            MyApplication.SSEB = true;
            Log.d("fufufu", "onHiddenChanged: " + MyApplication.SSEB);
            mirrorRun = false;
        } else {
            Log.d(TAG, "onHiddenChanged:start ");
            initLelink();
        }
    }
}


