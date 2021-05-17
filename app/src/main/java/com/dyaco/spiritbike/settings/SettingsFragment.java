package com.dyaco.spiritbike.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.corestar.libs.device.Device;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.engineer.EngineerActivity;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;

import java.util.concurrent.TimeUnit;

import static com.dyaco.spiritbike.MyApplication.btnFnaI;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.MyApplication.updateNotify;

public class SettingsFragment extends BaseFragment {

    TextView setting_title;
    ImageView iv_update_notify;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
        ((DashboardActivity) mActivity).changeSignOutToBack(false, false, 0, -1);

        ((DashboardActivity) mActivity).ivUpdateNotify.setVisibility(View.INVISIBLE);

        iv_update_notify = view.findViewById(R.id.iv_update_notify);

        Button btSetting01 = view.findViewById(R.id.btSetting01);
        Button btSetting02 = view.findViewById(R.id.btSetting02);
        Button btSetting03 = view.findViewById(R.id.btSetting03);
        Button btSetting04 = view.findViewById(R.id.btSetting04);
        Button btSetting05 = view.findViewById(R.id.btSetting05);
        Button btSetting06 = view.findViewById(R.id.btSetting06);
        Button btSetting07 = view.findViewById(R.id.btSetting07);
        Button btSetting08 = view.findViewById(R.id.btSetting08);
        Button btSetting09 = view.findViewById(R.id.btSetting09);

        btSetting01.setOnClickListener(btSettingsOnClick);
        btSetting02.setOnClickListener(btSettingsOnClick);
        btSetting03.setOnClickListener(btSettingsOnClick);
        btSetting04.setOnClickListener(btSettingsOnClick);
        btSetting05.setOnClickListener(btSettingsOnClick);
        btSetting06.setOnClickListener(btSettingsOnClick);
        btSetting07.setOnClickListener(btSettingsOnClick);
        btSetting08.setOnClickListener(btSettingsOnClick);
        btSetting09.setOnClickListener(btSettingsOnClick);

        setting_title = view.findViewById(R.id.setting_title);

        setting_title.setOnClickListener(v -> onSwitchMonitor());
    }

    private final View.OnClickListener btSettingsOnClick = view -> {

      //  ((DashboardActivity)requireActivity()).startSleepTimer();

        int fragmentid = 0;
        Bundle bundle = new Bundle();

        if (view.getId() == R.id.btSetting01) {
            fragmentid = R.id.action_settingsFragment_to_settingDisplayFragment;
        } else if (view.getId() == R.id.btSetting02) {

            if (!Settings.canDrawOverlays(mActivity)) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mActivity.getPackageName())), 0);
            } else {
                ((DashboardActivity)mActivity).floatingWidget.callSetting(0, DashboardActivity.class);
               // callWIFISetting();
            }
            fragmentid = -1;
            //  fragmentid = R.id.action_settingsFragment_to_settingWifiFragment;
        } else if (view.getId() == R.id.btSetting05) {
            fragmentid = R.id.action_settingsFragment_to_settingDateFragment;
        } else if (view.getId() == R.id.btSetting06) {
            fragmentid = R.id.action_settingsFragment_to_settingTimeFragment;
        } else if (view.getId() == R.id.btSetting04) {
            fragmentid = R.id.action_settingsFragment_to_settingSoftwareFragment;
        } else if (view.getId() == R.id.btSetting03) {
            ((DashboardActivity)mActivity).floatingWidget.callSetting(1, DashboardActivity.class);
           // callBlueToothSetting();
            fragmentid = -1;
        } else if (view.getId() == R.id.btSetting07) {
            fragmentid = R.id.action_settingsFragment_to_settingChildLockFragment;
        } else if (view.getId() == R.id.btSetting08) {
            fragmentid = R.id.action_settingsFragment_to_settingUnitFragment;
        } else if (view.getId() == R.id.btSetting09) {
            fragmentid = R.id.action_settingsFragment_to_settingSleepFragment;
        }

        if (fragmentid != -1)
            ((DashboardActivity) mActivity).navController.navigate(fragmentid, bundle);

        //   Navigation.findNavController(view).navigate(fragmentid, bundle);

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((DashboardActivity) mActivity).floatingWidget.removeFloatingView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((DashboardActivity) mActivity).floatingWidget.removeFloatingView();

        setting_title = null;
        iv_update_notify = null;
    }

    private long[] mHits = null;
    private final long DURATION = TimeUnit.SECONDS.toMillis(4);//規定有效時間

    private void onSwitchMonitor() {

        if (mHits == null) {
            //點擊次數
            int COUNTS = 10;
            mHits = new long[COUNTS];
        }

        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);//把從第二位至最後一位之間的數字複製到第一位至倒數第一位
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();//記錄一個時間

        if (SystemClock.uptimeMillis() - mHits[0] <= DURATION) {//一秒內連續點擊。
            //進來以後需要還原狀態，否則如果點擊過快，第六次，第七次 都會不斷進來觸發該效果。重新開始計數即可

            CommonUtils.closePackage(mActivity);
            mHits = null;
            startActivity(new Intent(mActivity, EngineerActivity.class));
            MyApplication.SSEB = false;

            if(btnFnaI != 0) {
                btnFnaI = 0;
                getInstance().commandSetFan(Device.FAN.STOP);
                ((DashboardActivity)requireActivity()).iv_fna_connected.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        iv_update_notify.setVisibility(updateNotify ? View.VISIBLE : View.INVISIBLE);
    }
}