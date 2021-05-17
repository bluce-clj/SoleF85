package com.dyaco.spiritbike.settings;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.ruler.RulerView;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.dyaco.spiritbike.support.CommonUtils.addZero;
import static com.dyaco.spiritbike.support.ruler.RulerView.daysOfMonth;

public class SettingDateFragment extends BaseFragment {
    private static final String TAG = "SettingDate";

    private Long mBroadcastTime;
    Button btNext_SetDate;

    //日
    private TextView tvDay;
    private RulerView rulerViewDay;
    private ImageButton btnDayUp;
    private ImageButton btnDayDown;

    //月
    private TextView tvMonth;
    private RulerView rulerViewMonth;
    private ImageButton btnMonthUp;
    private ImageButton btnMonthDown;

    //年
    private TextView tvYear;
    private RulerView rulerViewYear;
    private ImageButton btnYearUp;
    private ImageButton btnYearDown;

    private int targetYear;
    private int targetMonth;
    private int targetDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_date, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
        ((DashboardActivity) mActivity).changeSignOutToBack(true, false, 0, -1);

        btNext_SetDate = view.findViewById(R.id.btNext_SetDate);

        btNext_SetDate.setOnClickListener(v -> mActivity.onBackPressed());

        initDate();

        initView(view);

        initEvent();


        rulerViewYear.setSelectedValue(targetYear);
        rulerViewMonth.setSelectedValue(targetMonth);
        rulerViewDay.setSelectedValue(targetDay);
    }

    private void initDate() {
        Calendar mCurrentDate = Calendar.getInstance();
        targetYear = mCurrentDate.get(Calendar.YEAR);
        targetMonth = mCurrentDate.get(Calendar.MONTH) + 1;
        targetDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);

        Log.d("WWWWWWW", "year: " + targetYear + ", month: " + targetMonth + ", day: " + targetDay);
    }

    private void initView(View view) {

        tvDay = view.findViewById(R.id.tvDay_SetDate);
        tvDay.setText(String.valueOf(targetDay));
        rulerViewDay = view.findViewById(R.id.rulerViewDay);
        rulerViewDay.setMaxAndMinValue(daysOfMonth(targetMonth, targetYear), 1);
        rulerViewDay.setOnValueChangeListener((view12, value) -> {
            targetDay = (int) value;
          //  tvDay.setText(String.valueOf(targetDay));
            tvDay.setText(addZero(String.valueOf(targetDay)));
            setTimeDate(targetYear, targetMonth, targetDay);
        });

        btnDayUp = view.findViewById(R.id.btUpDay_SetDate);
        btnDayDown = view.findViewById(R.id.btDownDay_SetDate);

        tvMonth = view.findViewById(R.id.tvMonth_SetDate);
        tvMonth.setText(String.valueOf(targetMonth));

        rulerViewMonth = view.findViewById(R.id.rulerViewMonth);
        rulerViewMonth.setOnValueChangeListener((view1, value) -> {
            targetMonth = (int) value;
         //   tvMonth.setText(String.valueOf(targetMonth));
            tvMonth.setText(addZero(String.valueOf(targetMonth)));
            rulerViewDay.setMaxValue(daysOfMonth(targetMonth, targetYear));
            rulerViewDay.setSelectedValue(targetDay);
            setTimeDate(targetYear, targetMonth, targetDay);
        });
        btnMonthUp = view.findViewById(R.id.btUpMonth_SetDate);
        btnMonthDown = view.findViewById(R.id.btDownMonth_SetDate);

        tvYear = view.findViewById(R.id.tvYear_SetDate);
        tvYear.setText(String.valueOf(targetYear));

        rulerViewYear = view.findViewById(R.id.rulerViewYear);
        rulerViewYear.setOnValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onChange(RulerView view, float value) {
                targetYear = (int) value;
                tvYear.setText(String.valueOf(targetYear));

                rulerViewDay.setMaxValue(daysOfMonth(targetMonth, targetYear));
                rulerViewDay.setSelectedValue(targetDay);

                setTimeDate(targetYear, targetMonth, targetDay);

            }
        });

        btnYearUp = view.findViewById(R.id.btUpYear_SetDate);
        btnYearDown = view.findViewById(R.id.btDownYear_SetDate);
    }

    private void initEvent() {

        //日
        btnDayUp.setOnClickListener(view -> {
            rulerViewDay.setSelectedValue(Float.parseFloat(tvDay.getText().toString()) + 1);
        });

        btnDayDown.setOnClickListener(view -> {
            rulerViewDay.setSelectedValue(Float.parseFloat(tvDay.getText().toString()) - 1);
        });

        //月
        btnMonthUp.setOnClickListener(view ->
                rulerViewMonth.setSelectedValue(Float.parseFloat(tvMonth.getText().toString()) + 1));

        btnMonthDown.setOnClickListener(view -> {
            rulerViewMonth.setSelectedValue(Float.parseFloat(tvMonth.getText().toString()) - 1);
        });

        //年
        btnYearUp.setOnClickListener(view ->
                rulerViewYear.setSelectedValue(Float.parseFloat(tvYear.getText().toString()) + 1));

        btnYearDown.setOnClickListener(view -> {
            rulerViewYear.setSelectedValue(Float.parseFloat(tvYear.getText().toString()) - 1);
        });


        new CommonUtils().addAutoClick(btnDayUp);
        new CommonUtils().addAutoClick(btnDayDown);
        new CommonUtils().addAutoClick(btnMonthUp);
        new CommonUtils().addAutoClick(btnMonthDown);
        new CommonUtils().addAutoClick(btnYearUp);
        new CommonUtils().addAutoClick(btnYearDown);
    }

    public void setTimeDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        year = year - 1900;
        month = month - 1;
        int hr = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        Date date = new Date(year, month, day, hr, min);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String time = String.valueOf(simpleFormatter.format(date));
        try {
            date = simpleFormatter.parse(time);
            mBroadcastTime = date != null ? date.getTime() : 0;
            sendB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendB() {
        Intent intent3 = new Intent("com.roco.action.UPDATE_SYSTEM_TIME");
        intent3.setComponent(new ComponentName("com.android.settings", "com.android.settings.UpdateSystemTimeRecevier"));
        intent3.setClassName("com.android.settings", "com.android.settings.UpdateSystemTimeRecevier");
        Log.d("WWWWWWW", "mBroadcastTime = " + mBroadcastTime);
        Long CURRENT_TIME = mBroadcastTime;
        intent3.putExtra("CURRENT_TIME", CURRENT_TIME);
        requireActivity().sendBroadcast(intent3);
        requireActivity().sendOrderedBroadcast(intent3, null);   //有序廣播發送
    }
}