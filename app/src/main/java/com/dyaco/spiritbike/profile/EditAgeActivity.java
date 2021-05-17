package com.dyaco.spiritbike.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.support.ruler.RulerView;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.webapi.BaseCallWebApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.CommonUtils.addZero;
import static com.dyaco.spiritbike.support.CommonUtils.checkStr;
import static com.dyaco.spiritbike.support.ruler.RulerView.daysOfMonth;


public class EditAgeActivity extends BaseAppCompatActivity {

    private final UserProfileEntity userProfileEntity = getInstance().getUserProfile();
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

    private int targetYear;
    private int targetMonth;
    private int targetDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_age);

        Button btNo_DialogDataLost = findViewById(R.id.btNo_DialogDataLost);
        Button btYes_DialogDataLost = findViewById(R.id.btYes_DialogDataLost);

        btNo_DialogDataLost.setOnClickListener(v -> {
            MyApplication.SSEB = false;
            finish();
        });

        //SAVE
        btYes_DialogDataLost.setOnClickListener(v -> updateData());

        initDefaultAge();

        initView();

        initEvent();

        int maxYear = Calendar.getInstance().get(Calendar.YEAR) - 10;
        int minYear = Calendar.getInstance().get(Calendar.YEAR) - 99;
        rulerViewYear.setMaxValue(maxYear);
        rulerViewYear.setMinValue(minYear);

        rulerViewYear.setSelectedValue(targetYear);
        rulerViewMonth.setSelectedValue(targetMonth);
        rulerViewDay.setSelectedValue(targetDay);

        findViewById(R.id.btClose).setOnClickListener(v -> {

            MyApplication.SSEB = false;
            finish();
        });
    }

    private void initDefaultAge() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(userProfileEntity.getBirthday(), formatter);

        targetYear = date.getYear();
        targetMonth = date.getMonthValue();
        targetDay = date.getDayOfMonth();

        Log.d("AAAAAA", "initDefaultAge: " + targetYear + "," + targetMonth + "," + targetDay);

    }

    private void initView() {

        tvDay = findViewById(R.id.tvDay_SetDate);
        tvDay.setText(String.valueOf(targetDay));
        rulerViewDay = findViewById(R.id.rulerViewDay);
        rulerViewDay.setMaxAndMinValue(daysOfMonth(targetMonth, targetYear), 1);
        rulerViewDay.setOnValueChangeListener((view12, value) -> {
            targetDay = (int) value;
          //  tvDay.setText(String.valueOf(targetDay));
            tvDay.setText(addZero(String.valueOf(targetDay)));
        });
        btnDayUp = findViewById(R.id.btUpDay_SetDate);
        btnDayDown = findViewById(R.id.btDownDay_SetDate);

        tvMonth = findViewById(R.id.tvMonth_SetDate);
        tvMonth.setText(String.valueOf(targetMonth));
        rulerViewMonth = findViewById(R.id.rulerViewMonth);
        rulerViewMonth.setOnValueChangeListener((view1, value) -> {
            targetMonth = (int) value;
          //  tvMonth.setText(String.valueOf(targetMonth));
            tvMonth.setText(addZero(String.valueOf(targetMonth)));
            rulerViewDay.setMaxValue(daysOfMonth(targetMonth, targetYear));
            rulerViewDay.setSelectedValue(targetDay);
        });
        btnMonthUp = findViewById(R.id.btUpMonth_SetDate);
        btnMonthDown = findViewById(R.id.btDownMonth_SetDate);

        tvYear = findViewById(R.id.tvYear_SetDate);
        tvYear.setText(String.valueOf(targetYear));
        rulerViewYear = findViewById(R.id.rulerViewYear);
        rulerViewYear.setOnValueChangeListener((view, value) -> {
            targetYear = (int) value;
            tvYear.setText(String.valueOf(targetYear));
            rulerViewDay.setMaxValue(daysOfMonth(targetMonth, targetYear));
            rulerViewDay.setSelectedValue(targetDay);
        });

        btnYearUp = findViewById(R.id.btUpYear_SetDate);
        btnYearDown = findViewById(R.id.btDownYear_SetDate);
    }

    private void initEvent() {

        //日
        btnDayUp.setOnClickListener(view ->
                rulerViewDay.setSelectedValue(Float.parseFloat(tvDay.getText().toString()) + 1));

        btnDayDown.setOnClickListener(view ->
                rulerViewDay.setSelectedValue(Float.parseFloat(tvDay.getText().toString()) - 1));

        //月
        btnMonthUp.setOnClickListener(view ->
                rulerViewMonth.setSelectedValue(Float.parseFloat(tvMonth.getText().toString()) + 1));

        btnMonthDown.setOnClickListener(view ->
                rulerViewMonth.setSelectedValue(Float.parseFloat(tvMonth.getText().toString()) - 1));

        //年
        btnYearUp.setOnClickListener(view ->
                rulerViewYear.setSelectedValue(Float.parseFloat(tvYear.getText().toString()) + 1));

        btnYearDown.setOnClickListener(view ->
                rulerViewYear.setSelectedValue(Float.parseFloat(tvYear.getText().toString()) - 1));

        new CommonUtils().addAutoClick(btnDayUp);
        new CommonUtils().addAutoClick(btnDayDown);
        new CommonUtils().addAutoClick(btnMonthUp);
        new CommonUtils().addAutoClick(btnMonthDown);
        new CommonUtils().addAutoClick(btnYearUp);
        new CommonUtils().addAutoClick(btnYearDown);
    }

    private void updateData() {
        String day = tvDay.getText().toString();
        String month = tvMonth.getText().toString();
        String year = tvYear.getText().toString();
        String birthDay = year + "-" + String.format(Locale.getDefault(), "%02d", Long.parseLong(month)) + "-" + String.format(Locale.getDefault(), "%02d", Long.parseLong(day));


        Log.d("AAAAAAAA", "updateData: " + birthDay);

        userProfileEntity.setBirthday(birthDay);
        DatabaseManager.getInstance(MyApplication.getInstance()).updateUserProfile(userProfileEntity,
                new DatabaseCallback<UserProfileEntity>() {
                    @Override
                    public void onUpdated() {
                        super.onUpdated();

                        if (checkStr(userProfileEntity.getSoleAccountNo()))
                            new BaseCallWebApi().callSyncUserDataApi(userProfileEntity);


                        MyApplication.SSEB = false;
                        finish();
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);

                        Toast.makeText(getInstance(), "Failure:" + err, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private BtnExitFullScreen btnExitFullScreen = new BtnExitFullScreen(this);

    @Override
    protected void onPause() {
        super.onPause();
        btnExitFullScreen.showBtnFullScreenExit(DashboardActivity.class, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnExitFullScreen.removeFloatView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btnExitFullScreen.removeFloatView();
        btnExitFullScreen = null;
    }
}