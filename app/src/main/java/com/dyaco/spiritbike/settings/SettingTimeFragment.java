package com.dyaco.spiritbike.settings;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.dyaco.spiritbike.engineer.DeviceSettingBean;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.support.ruler.RulerView;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.R;

import java.util.Calendar;
import java.util.Date;

import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.CommonUtils.addZero;
import static com.dyaco.spiritbike.support.CommonUtils.setTime;

public class SettingTimeFragment extends BaseFragment implements RulerView.OnValueChangeListener {
    private View viewAMPM;
    private View view24;
    private TextView m_tvMale_NewGender;
    private TextView m_tvFemale_NewGender;

    //ampm 時分
    private TextView tv_ampm_hours_text;
    private TextView tv_ampm_min_text;
    private RulerView rulerViewAmPmHours;
    private RulerView rulerViewAmPmMins;
    private ImageButton bt_ampm_hours_left;
    private ImageButton bt_ampm_hours_right;
    private ImageButton bt_ampm_mins_left;
    private ImageButton bt_ampm_mins_right;

    //24 時分
    private TextView tv_24_hours_text;
    private TextView tv_24_mins_text;
    private RulerView rulerView24Hours;
    private RulerView rulerView24Mins;
    private ImageButton bt_24_hours_left;
    private ImageButton bt_24_hours_right;
    private ImageButton bt_24_mins_left;
    private ImageButton bt_24_mins_right;

    private Button btGetStarted_SetTime12;

    private RadioGroup radioGroup;
    private RadioButton rbAm_SetTime12;
    private RadioButton rbPm_SetTime12;

    private CheckBox set24;
    private Long mBroadcastTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_time, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        initEvent();

        initDate();
    }

    private void initDate() {

        DeviceSettingBean deviceSettingBean = getInstance().getDeviceSettingBean();
     //   set24.setChecked("24".equals(deviceSettingBean.getTime_unit()));
        set24.setChecked(24 == deviceSettingBean.getTime_unit());

        Calendar calendar = Calendar.getInstance();
        int a = calendar.get(Calendar.AM_PM);
        if (a == Calendar.AM) {
            rbAm_SetTime12.setChecked(true);
        } else {
            rbPm_SetTime12.setChecked(true);
        }

//        rbAm_SetTime12.setChecked("AM".equals(deviceSettingBean.getTimeAM_PM()));
//        rbPm_SetTime12.setChecked("PM".equals(deviceSettingBean.getTimeAM_PM()));


        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        Log.d("@@@@@@", "initDate: " + hours + "," + minutes);

        rulerView24Hours.setSelectedValue(hours);
        rulerView24Mins.setSelectedValue(minutes);

        if (rbPm_SetTime12.isChecked()) {
            int h = hours;
            if (hours > 12) {
                h = hours - 12;
            }

            rulerViewAmPmHours.setSelectedValue(h);
        } else {
            if (hours == 0) {
                hours = 12;
            }
            rulerViewAmPmHours.setSelectedValue(hours);
        }
        rulerViewAmPmMins.setSelectedValue(minutes);
    }

    private void initEvent() {

        set24.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {//24

                set24.setText(R.string.hours_24_format);
                viewAMPM.setVisibility(View.INVISIBLE);
                view24.setVisibility(View.VISIBLE);

//                    DeviceSettingBean deviceSettingBean = getInstance().getDeviceSettingBean();
//                    deviceSettingBean.setTime24_12("24");
//                    getInstance().setDeviceSettingBean(deviceSettingBean);

                int h = Integer.parseInt("".equals(tv_ampm_hours_text.getText().toString()) ? "0" : tv_ampm_hours_text.getText().toString());

                if (rbPm_SetTime12.isChecked()) {

                    if (h != 12) {
                        h = h + 12;
                    }
                } else {
                    if (h == 12) {
                        h = 0;
                    }
                }
                int m = Integer.parseInt("".equals(tv_ampm_min_text.getText().toString()) ? "1" : tv_ampm_min_text.getText().toString());
                rulerView24Hours.setSelectedValue(h);
                rulerView24Mins.setSelectedValue(m);

            } else {//am pm

                set24.setText(R.string.am_pm_format);
                viewAMPM.setVisibility(View.VISIBLE);
                view24.setVisibility(View.INVISIBLE);

                int h = 1;
                if (!"".equals(tv_24_hours_text.getText().toString())) {
                    h = Integer.parseInt(tv_24_hours_text.getText().toString());
                }

                Log.d("@@@@@@", "onCheckedChanged: " + h);

                if (h >= 12) { // PM
                    rbPm_SetTime12.setChecked(true);
                    rbAm_SetTime12.setChecked(false);
                    if (h != 12)
                        h = h - 12;
                } else { //AM

                    if (h == 0) {
                        h = 12;
                    }
                    rbAm_SetTime12.setChecked(true);
                    rbPm_SetTime12.setChecked(false);
                }

//                    DeviceSettingBean deviceSettingBean = getInstance().getDeviceSettingBean();
//                    deviceSettingBean.setTime24_12("12");
//                    getInstance().setDeviceSettingBean(deviceSettingBean);


                //  int h = Integer.parseInt("".equals(tv_24_hours_text.getText().toString()) ? "1" : tv_24_hours_text.getText().toString());
                int m = Integer.parseInt("".equals(tv_24_mins_text.getText().toString()) ? "1" : tv_24_mins_text.getText().toString());
                rulerViewAmPmHours.setSelectedValue(h);
                rulerViewAmPmMins.setSelectedValue(m);
            }
        });

        //SAVE
        btGetStarted_SetTime12.setOnClickListener(v -> {
            DeviceSettingBean deviceSettingBean = getInstance().getDeviceSettingBean();
          //  deviceSettingBean.setTime_unit(set24.isChecked() ? "24" : "12");
            deviceSettingBean.setTime_unit(set24.isChecked() ? 24 : 12);
            deviceSettingBean.setTimeAM_PM(rbAm_SetTime12.isChecked() ? "AM" : "PM");
            getInstance().setDeviceSettingBean(deviceSettingBean);

            setT();
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbAm_SetTime12) {
                m_tvMale_NewGender.setTextColor(ContextCompat.getColor(mActivity, R.color.colorE4002B));
                m_tvFemale_NewGender.setTextColor(ContextCompat.getColor(mActivity, R.color.color597084));

            } else {
                m_tvMale_NewGender.setTextColor(ContextCompat.getColor(mActivity, R.color.color597084));
                m_tvFemale_NewGender.setTextColor(ContextCompat.getColor(mActivity, R.color.colorE4002B));

            }
        });

        bt_ampm_hours_left.setOnClickListener(view1 -> rulerViewAmPmHours.setSelectedValue(Float.parseFloat(tv_ampm_hours_text.getText().toString()) - 1));
        bt_ampm_hours_right.setOnClickListener(view12 -> rulerViewAmPmHours.setSelectedValue(Float.parseFloat(tv_ampm_hours_text.getText().toString()) + 1));

        bt_ampm_mins_left.setOnClickListener(view1 -> rulerViewAmPmMins.setSelectedValue(Float.parseFloat(tv_ampm_min_text.getText().toString()) - 1));
        bt_ampm_mins_right.setOnClickListener(view12 -> rulerViewAmPmMins.setSelectedValue(Float.parseFloat(tv_ampm_min_text.getText().toString()) + 1));

        bt_24_hours_left.setOnClickListener(view1 -> rulerView24Hours.setSelectedValue(Float.parseFloat(tv_24_hours_text.getText().toString()) - 1));
        bt_24_hours_right.setOnClickListener(view12 -> rulerView24Hours.setSelectedValue(Float.parseFloat(tv_24_hours_text.getText().toString()) + 1));

        bt_24_mins_left.setOnClickListener(view1 -> rulerView24Mins.setSelectedValue(Float.parseFloat(tv_24_mins_text.getText().toString()) - 1));
        bt_24_mins_right.setOnClickListener(view12 -> rulerView24Mins.setSelectedValue(Float.parseFloat(tv_24_mins_text.getText().toString()) + 1));

        new CommonUtils().addAutoClick(bt_ampm_hours_left);
        new CommonUtils().addAutoClick(bt_ampm_hours_right);
        new CommonUtils().addAutoClick(bt_ampm_mins_left);
        new CommonUtils().addAutoClick(bt_ampm_mins_right);
        new CommonUtils().addAutoClick(bt_24_hours_left);
        new CommonUtils().addAutoClick(bt_24_hours_right);
        new CommonUtils().addAutoClick(bt_24_mins_left);
        new CommonUtils().addAutoClick(bt_24_mins_right);
    }

    private void initView(View view) {

        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
        ((DashboardActivity) mActivity).changeSignOutToBack(true, false, 0, -1);

        m_tvMale_NewGender = view.findViewById(R.id.tvMale_NewGender);
        m_tvFemale_NewGender = view.findViewById(R.id.tvFemale_NewGender);

        btGetStarted_SetTime12 = view.findViewById(R.id.btGetStarted_SetTime12);
        radioGroup = view.findViewById(R.id.radioGroup);
        set24 = view.findViewById(R.id.btFormat_SetTime12);

        tv_ampm_hours_text = view.findViewById(R.id.tv_ampm_hours_text);
        tv_ampm_min_text = view.findViewById(R.id.tv_ampm_min_text);
        rulerViewAmPmHours = view.findViewById(R.id.rulerViewAmPmHours);
        rulerViewAmPmMins = view.findViewById(R.id.rulerViewAmPmMins);
        bt_ampm_hours_left = view.findViewById(R.id.bt_ampm_hours_left);
        bt_ampm_hours_right = view.findViewById(R.id.bt_ampm_hours_right);
        bt_ampm_mins_left = view.findViewById(R.id.bt_ampm_min_left);
        bt_ampm_mins_right = view.findViewById(R.id.bt_ampm_min_right);
        rulerViewAmPmHours.setOnValueChangeListener(this);
        rulerViewAmPmMins.setOnValueChangeListener(this);


        tv_24_hours_text = view.findViewById(R.id.tv_24_hours_text);
        tv_24_mins_text = view.findViewById(R.id.tv_24_mins_text);
        rulerView24Hours = view.findViewById(R.id.rulerView24Hours);
        rulerView24Mins = view.findViewById(R.id.rulerView24Mins);
        bt_24_hours_left = view.findViewById(R.id.bt_24_hours_left);
        bt_24_hours_right = view.findViewById(R.id.bt_24_hours_right);
        bt_24_mins_left = view.findViewById(R.id.bt_24_min_left);
        bt_24_mins_right = view.findViewById(R.id.bt_24_min_right);
        rulerView24Hours.setOnValueChangeListener(this);
        rulerView24Mins.setOnValueChangeListener(this);

        viewAMPM = view.findViewById(R.id.cl_ampm);
        view24 = view.findViewById(R.id.cl_24);

        rbAm_SetTime12 = view.findViewById(R.id.rbAm_SetTime12);
        rbPm_SetTime12 = view.findViewById(R.id.rbPm_SetTime12);
    }

    @Override
    public void onChange(RulerView view, float value) {

        if (view.getId() == R.id.rulerViewAmPmHours) {
          //  tv_ampm_hours_text.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
            tv_ampm_hours_text.setText(addZero(String.valueOf(value)));
        } else if (view.getId() == R.id.rulerViewAmPmMins) {
          //  tv_ampm_min_text.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
            tv_ampm_min_text.setText(addZero(String.valueOf(value)));
        } else if (view.getId() == R.id.rulerView24Hours) {
         //   tv_24_hours_text.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
            tv_24_hours_text.setText(addZero(String.valueOf(value)));
        } else if (view.getId() == R.id.rulerView24Mins) {
          //  tv_24_mins_text.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
            tv_24_mins_text.setText(addZero(String.valueOf(value)));
        }
    }

    private void setT() {
        int h, m;
        try {
            if (set24.isChecked()) {
                //24
                h = Integer.parseInt(tv_24_hours_text.getText().toString());
                m = Integer.parseInt(tv_24_mins_text.getText().toString());
            } else {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                h = Integer.parseInt(tv_ampm_hours_text.getText().toString());
                m = Integer.parseInt(tv_ampm_min_text.getText().toString());
                if (selectedId == R.id.rbPm_SetTime12) {
                    if (h != 12) {
                        h = h + 12;
                    }
                } else {
                    if (h == 12) {
                        h = 0;
                    }
                }
            }

//            Log.d("@@@@@@", "onChange: " + h +","+ m);
            setTimeDate(h, m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTimeDate(int hr, int min) {
        Calendar calendar = Calendar.getInstance();
        int yearDate = calendar.get(Calendar.YEAR);
        int monthDate = calendar.get(Calendar.MONTH);
        int dayDate = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(yearDate, monthDate, dayDate, hr, min);
        //  Date date = new Date(yearDate, monthDate, dayDate, hrDate, minDate);

        // SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());
        Date date = calendar.getTime();
        mBroadcastTime = date.getTime();

        sendB();

//        String time = simpleFormatter.format(date);
//        try {
//            date = simpleFormatter.parse(time);
//            mBroadcastTime = date != null ? date.getTime() : 0;
//            sendB();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }

    private void sendB() {
        Intent intent3 = new Intent("com.roco.action.UPDATE_SYSTEM_TIME");
        intent3.setComponent(new ComponentName("com.android.settings", "com.android.settings.UpdateSystemTimeRecevier"));
        intent3.setClassName("com.android.settings", "com.android.settings.UpdateSystemTimeRecevier");
        Long CURRENT_TIME = mBroadcastTime;
        intent3.putExtra("CURRENT_TIME", CURRENT_TIME);
        requireActivity().sendBroadcast(intent3);
        requireActivity().sendOrderedBroadcast(intent3, null);   //有序廣播發送

        new RxTimer().timer(500, n -> {
            try {
                ((DashboardActivity) mActivity).m_tvTime_Dashboard.setText(setTime());
                requireActivity().onBackPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}