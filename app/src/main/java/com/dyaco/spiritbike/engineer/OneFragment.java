package com.dyaco.spiritbike.engineer;


import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.corestar.libs.device.Device;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.settings.UsbUpdateActivity;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.RxTimer;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.WINDOW_SERVICE;
import static com.dyaco.spiritbike.MyApplication.UNIT_E;
import static com.dyaco.spiritbike.MyApplication.getInstance;


public class OneFragment extends Fragment {
    DeviceSettingBean deviceSettingBean;
    private Button btn_update;
    private Button btn_odo_reset;
    private SegmentedButtonGroup sc_unit;
    private SegmentedButtonGroup sc_sleep;
    private SegmentedButtonGroup sc_beep;
    private TextView tvTime;
    private TextView tvDistance;

    private Button btn_osOTA;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private Button button;
    private View view2;

    private RadioGroup radioGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private Button btn_to_command;


    public OneFragment() {
    }

    private static final String TAG = "OneFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_one, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: ");
        radioGroup = view.findViewById(R.id.rgggg);
        RadioButton radioButton1 = view.findViewById(R.id.rb1);
        RadioButton radioButton4 = view.findViewById(R.id.rb4);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb1) {

                setTimeOut(8 * 1 * 1000);
                MyApplication.SLEEP_TIME = 8 * 1 * 1000;

//            } else if (checkedId == R.id.rb2) {
//                MyApplication.SLEEP_TIME = 60 * 2 * 1000;
//
//            } else if (checkedId == R.id.rb3) {
//                MyApplication.SLEEP_TIME = 60 * 20 * 1000;
            } else if (checkedId == R.id.rb2){
                setTimeOut(60 * 30 * 1000);
                MyApplication.SLEEP_TIME = 60 * 30 * 1000;
            }
        });

        float result = 0;
        try {
            result = Settings.System.getInt(requireActivity().getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (result == (30 * 1 * 1000)){
            radioButton1.setChecked(true);
        } else if(result ==  (60 * 30 * 1000)) {
            radioButton4.setChecked(true);
        } else {
            radioButton1.setChecked(false);
            radioButton4.setChecked(false);
        }

//        radioButton1.setChecked();
//        radioButton2.setChecked();


        btn_osOTA = view.findViewById(R.id.btn_osOTA);
        btn_update = view.findViewById(R.id.btn_update);
        btn_odo_reset = view.findViewById(R.id.btn_odo_reset);
        sc_unit = view.findViewById(R.id.sc_unit);
        sc_sleep = view.findViewById(R.id.sc_sleep);
        sc_beep = view.findViewById(R.id.sc_beep);
        tvTime = view.findViewById(R.id.tv_time);
        tvDistance = view.findViewById(R.id.tv_distance);

        btn_to_command = view.findViewById(R.id.btn_to_command);
        btn_to_command.setOnClickListener(v -> {
            getInstance().commandSetUsbMode(Device.USB_MODE.DATA);
            Intent intent = new Intent();
            intent.setClass(requireContext(), UsbUpdateCommandActivity.class);
            startActivityForResult(intent, 1);

            MyApplication.SSEB = false;
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                //   bundle.putString("name",etUserName_UserName.getText().toString());
                intent.putExtras(bundle);
                intent.setClass(requireContext(), UsbUpdateActivity.class);
                startActivity(intent);

                MyApplication.SSEB = false;
            }
        });

        btn_osOTA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.SSEB = false;
                start_redstone_app();
            }
        });


        btn_odo_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceSettingBean deviceSettingBean = getInstance().getDeviceSettingBean();
                deviceSettingBean.setODO_time(0);
                deviceSettingBean.setODO_distance(0);
                getInstance().setDeviceSettingBean(deviceSettingBean);

                tvDistance.setText("0");
                tvTime.setText("0");

            }
        });

        sc_unit.setOnPositionChangedListener(position -> {
            switch (position) {
                case 0:
                    deviceSettingBean = getInstance().getDeviceSettingBean();
                    deviceSettingBean.setUnit_code(1);
                    getInstance().setDeviceSettingBean(deviceSettingBean);
                    UNIT_E = 1;
                    break;
                case 1:
                    deviceSettingBean = getInstance().getDeviceSettingBean();
                    deviceSettingBean.setUnit_code(0);
                    getInstance().setDeviceSettingBean(deviceSettingBean);
                    UNIT_E = 0;
            }
        });

        sc_sleep.setOnPositionChangedListener(position -> {

            switch (position) {
                case 0: //on
                    deviceSettingBean = getInstance().getDeviceSettingBean();
                    deviceSettingBean.setDisplay_mode(1);//	1 --> on (不休眠)
                    getInstance().setDeviceSettingBean(deviceSettingBean);
                    break;
                case 1://off
                    deviceSettingBean = getInstance().getDeviceSettingBean();
                    deviceSettingBean.setDisplay_mode(0); //	0 --> off (會休眠)
                    getInstance().setDeviceSettingBean(deviceSettingBean);
            }
        });

        sc_beep.setOnPositionChangedListener(position -> {
            switch (position) {
                case 0:
                    deviceSettingBean = getInstance().getDeviceSettingBean();
                    deviceSettingBean.setBeep_sound(1);
                    getInstance().setDeviceSettingBean(deviceSettingBean);
                    if (!isFirst)
                        getInstance().commandSetBuzzer(Device.BEEP.SHORT, 1);
                    break;
                case 1:
                    deviceSettingBean = getInstance().getDeviceSettingBean();
                    deviceSettingBean.setBeep_sound(0);
                    getInstance().setDeviceSettingBean(deviceSettingBean);
            }
            isFirst = false;
        });

        initData();
    }

    public void start_redstone_app() {


        windowManager = (WindowManager) requireActivity().getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        //   layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 184;
        layoutParams.height = 80;
        layoutParams.x = 500;
        layoutParams.y = 700;

        Typeface typeface = ResourcesCompat.getFont(requireActivity(), R.font.hanzel_bold);
        button = new Button(requireActivity());
        button.setText("BACK");
        button.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorFFFFFF));
        button.setTextSize(26);
        button.setTypeface(typeface);
        button.setGravity(Gravity.CENTER);
        button.setBackgroundResource(R.drawable.btn_rrect_9d2227_184);
        // button.setOnTouchListener(new SettingsFragment.FloatingOnTouchListener());
        windowManager.addView(button, layoutParams);

        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams();
        layoutParams2.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams2.gravity = Gravity.TOP | Gravity.START;
        layoutParams2.format = PixelFormat.RGBA_8888;
        layoutParams2.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        layoutParams2.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams2.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams2.height = 60;
        view2 = new View(requireActivity());
        view2.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.colorFFFFFF));
        windowManager.addView(view2, layoutParams2);

        button.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(requireActivity(), DashboardActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(0, 0);
            windowManager.removeViewImmediate(button);
            windowManager.removeViewImmediate(view2);
        });

        ComponentName mComponentName = new ComponentName("com.redstone.ota.ui", "com.redstone.ota.ui.activity.RsMainActivity");
        Intent mIntent = new Intent();
        mIntent.setComponent(mComponentName);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        requireActivity().startActivity(mIntent);
    }

    boolean isFirst = true;

    private void initData() {
        deviceSettingBean = getInstance().getDeviceSettingBean();
        sc_unit.setPosition(deviceSettingBean.getUnit_code() == 1 ? 0 : 1, false);

        //	0 --> off (會休眠)
        //	1 --> on (不休眠)
        sc_sleep.setPosition(deviceSettingBean.getDisplay_mode() == 1 ? 0 : 1, false);

        sc_beep.setPosition(deviceSettingBean.getBeep_sound() == 1 ? 0 : 1, false);
        tvDistance.setText(CommonUtils.formatDecimal(deviceSettingBean.getODO_distance(), 1));
        tvTime.setText(CommonUtils.formatDecimal(deviceSettingBean.getODO_time(), 1));

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("FFFFFFF", "onResume:1 ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeFloatingView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeFloatingView();
    }

    private void removeFloatingView() {
        try {
            if (windowManager != null) {
                if (button != null && button.getWindowToken() != null) {
                    windowManager.removeViewImmediate(button);
                }
                if (view2 != null && view2.getWindowToken() != null) {
                    windowManager.removeViewImmediate(view2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    /*
                    回傳值 0 = 失敗
                    回傳值 1 = 成功
                     */
                    boolean returnedData = data.getBooleanExtra("updateState", true);
                    Log.d("#########", "UsbUpdateCommandActivity = " + returnedData);
                    if (returnedData) {
                        Log.d("#########", "returnedData = true ");
                    }
                    getInstance().mDevice.connect();

                    new RxTimer().timer(700, number ->
                            getInstance().commandSetUsbMode(Device.USB_MODE.CHARGER));
                }
                break;
            default:

        }
    }


    private void setTimeOut(int time) {

        Log.d("KKKKKKK", "setTimeOut: " + time);

        Settings.System.putInt(requireActivity().getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, time);
    }

    private void xx() {
        float result = 0;
//        try {
//            result = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
//        } catch (Settings.SettingNotFoundException e) {
//            e.printStackTrace();
//        }
//        Log.i("PPPPP", "111result = " + result);
//
//
//        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 10 * 60 * 1000);
//
//        try {
//            result = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
//        } catch (Settings.SettingNotFoundException e) {
//            e.printStackTrace();
//        }
//        Log.i("PPPPP", "222result = " + result);

        //Settings.System.putInt(requireActivity().getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 1000);

//        try {
//            result = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
//        } catch (Settings.SettingNotFoundException e) {
//            e.printStackTrace();
//        }
//        Log.i("PPPPP", "222result = " + result);


//        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 2147483647);
//
//        try {
//            result = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
//        } catch (Settings.SettingNotFoundException e) {
//            e.printStackTrace();
//        }
//        Log.i("PPPPP", "222result = " + result);
    }
}
