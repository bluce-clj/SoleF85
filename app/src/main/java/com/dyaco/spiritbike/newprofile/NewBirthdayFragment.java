package com.dyaco.spiritbike.newprofile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.ruler.RulerView;
import com.dyaco.spiritbike.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

import static com.dyaco.spiritbike.support.CommonUtils.addZero;
import static com.dyaco.spiritbike.support.ruler.RulerView.daysOfMonth;


public class NewBirthdayFragment extends Fragment {

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
    private String mUserName;

    private int targetYear = 2000;
    private int targetMonth = 1;
    private int targetDay = 1;
    private String mBirthDay;
    private int mGender;
    private int mWeightMetric;
    private int mHeightMetric;
    private int mWeightImperial;
    private int mHeightImperial;
    private int mUnit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserName = getArguments().getString("userName","");
            mBirthDay = getArguments().getString("birthDay","");
            mGender = getArguments().getInt("gender",1);
            mWeightMetric = getArguments().getInt("weight_metric",60);
            mHeightMetric = getArguments().getInt("height_metric",170);
            mWeightImperial = getArguments().getInt("weight_imperial",200);
            mHeightImperial = getArguments().getInt("height_imperial",70);
            mUnit = getArguments().getInt("unit",0);
        }
        Log.d("@@@@@@", "onCreate2222222: " + mBirthDay);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_birthday, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btBack_NewBirthday = view.findViewById(R.id.btBack_NewBirthday);
        Button btNext_NewBirthday = view.findViewById(R.id.btNext_NewBirthday);
        Button btClose_NewBirthday = view.findViewById(R.id.btClose_NewBirthday);

        btBack_NewBirthday.setOnClickListener(btNewBirthdayOnClick);
        btNext_NewBirthday.setOnClickListener(btNewBirthdayOnClick);
        btClose_NewBirthday.setOnClickListener(btNewBirthdayOnClick);


        initDate();

        initView(view);

        initEvent();

        int maxYear = Calendar.getInstance().get(Calendar.YEAR) - 10;
        int minYear = Calendar.getInstance().get(Calendar.YEAR) - 99;
        rulerViewYear.setMaxValue(maxYear);
        rulerViewYear.setMinValue(minYear);

        rulerViewYear.setSelectedValue(targetYear);
        rulerViewMonth.setSelectedValue(targetMonth);
        rulerViewDay.setSelectedValue(targetDay);

    }

    private void initDate() {
        if ("".equals(mBirthDay)) {
            targetYear = 2000;
            targetMonth = 1;
            targetDay = 1;
        } else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(mBirthDay, formatter);
                targetYear = date.getYear();
                targetMonth = date.getMonthValue();
                targetDay = date.getDayOfMonth();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        Calendar mCurrentDate = Calendar.getInstance();
//        targetYear = mCurrentDate.get(Calendar.YEAR);
//        targetMonth = mCurrentDate.get(Calendar.MONTH) + 1;
//        targetDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);
    }

    private final View.OnClickListener btNewBirthdayOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            String day = tvDay.getText().toString();
            String month = tvMonth.getText().toString();
            String year = tvYear.getText().toString();
           // String birthDay = year + String.format(Locale.getDefault(), "%02d", Long.parseLong(month)) + String.format(Locale.getDefault(), "%02d", Long.parseLong(day));
            String birthDay = year + "-" + String.format(Locale.getDefault(), "%02d", Long.parseLong(month)) + "-" + String.format(Locale.getDefault(), "%02d", Long.parseLong(day));

            bundle.putString("userName", mUserName);
            bundle.putString("birthDay", birthDay);
            bundle.putInt("gender", mGender);
            bundle.putInt("unit", mUnit);
            bundle.putInt("weight_metric", mWeightMetric);
            bundle.putInt("height_metric", mHeightMetric);
            bundle.putInt("weight_imperial", mWeightImperial);
            bundle.putInt("height_imperial", mHeightImperial);

            switch (view.getId()) {
                case R.id.btBack_NewBirthday:
                    bundle.putString("action", "action_newBirthdayFragment_to_userNameFragment");
                    Navigation.findNavController(view).navigate(R.id.action_newBirthdayFragment_to_userNameFragment, bundle);
                    break;
                case R.id.btNext_NewBirthday:

                    Navigation.findNavController(view).navigate(R.id.action_newBirthdayFragment_to_newGenderFragment, bundle);
                    break;
                case R.id.btClose_NewBirthday:
                    Navigation.findNavController(view).navigate(R.id.action_newBirthdayFragment_to_dialogDataLostFragment);
                    break;
            }
        }
    };

    private void initView(View view) {

        tvDay = view.findViewById(R.id.tvDay_SetDate);
        tvDay.setText(String.valueOf(targetDay));
        rulerViewDay = view.findViewById(R.id.rulerViewDay);
        rulerViewDay.setMaxAndMinValue(daysOfMonth(targetMonth, targetYear), 1);
        rulerViewDay.setOnValueChangeListener((view12, value) -> {
            targetDay = (int) value;
         //   tvDay.setText(String.valueOf(targetDay));
            tvDay.setText(addZero(String.valueOf(targetDay)));
        });
        btnDayUp = view.findViewById(R.id.btUpDay_SetDate);
        btnDayDown = view.findViewById(R.id.btDownDay_SetDate);

        tvMonth = view.findViewById(R.id.tvMonth_SetDate);
        tvMonth.setText(String.valueOf(targetMonth));
        rulerViewMonth = view.findViewById(R.id.rulerViewMonth);
        rulerViewMonth.setOnValueChangeListener((view1, value) -> {
            targetMonth = (int) value;
          //  tvMonth.setText(String.valueOf(targetMonth));
            tvMonth.setText(addZero(String.valueOf(targetMonth)));
            rulerViewDay.setMaxValue(daysOfMonth(targetMonth, targetYear));
            rulerViewDay.setSelectedValue(targetDay);
        });
        btnMonthUp = view.findViewById(R.id.btUpMonth_SetDate);
        btnMonthDown = view.findViewById(R.id.btDownMonth_SetDate);

        tvYear = view.findViewById(R.id.tvYear_SetDate);
        tvYear.setText(String.valueOf(targetYear));
        rulerViewYear = view.findViewById(R.id.rulerViewYear);
        rulerViewYear.setOnValueChangeListener((view3, value) -> {
            targetYear = (int) value;
            tvYear.setText(String.valueOf(targetYear));
            rulerViewDay.setMaxValue(daysOfMonth(targetMonth, targetYear));
            rulerViewDay.setSelectedValue(targetDay);
        });

        btnYearUp = view.findViewById(R.id.btUpYear_SetDate);
        btnYearDown = view.findViewById(R.id.btDownYear_SetDate);
    }

    private void initEvent() {

        //日
        btnDayUp.setOnClickListener(view ->
                rulerViewDay.setSelectedValue(Float.parseFloat(tvDay.getText().toString()) + 1));

        btnDayDown.setOnClickListener(view -> {
            rulerViewDay.setSelectedValue(Float.parseFloat(tvDay.getText().toString()) - 1);
//            rulerViewDay.onScroll(null, null, 0, -23.0f);
//            xx(0f, MotionEvent.ACTION_UP);
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
//            rulerViewYear.onScroll(null, null, 0, -23.0f);
//            xx(0f, MotionEvent.ACTION_UP);
        });


        new CommonUtils().addAutoClick(btnDayUp);
        new CommonUtils().addAutoClick(btnDayDown);
        new CommonUtils().addAutoClick(btnMonthUp);
        new CommonUtils().addAutoClick(btnMonthDown);
        new CommonUtils().addAutoClick(btnYearUp);
        new CommonUtils().addAutoClick(btnYearDown);
    }




    private void xx(float x, int MotionEventAction) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        //  float x = 263.0f;
        float y = 147.0f;
// List of meta states found here:     developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEventAction,
                x,
                y,
                metaState
        );

    }

}