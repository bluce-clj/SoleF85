package com.dyaco.spiritbike;

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
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.workout.WorkoutBean;
import com.dyaco.spiritbike.support.ruler.RulerView;
import com.dyaco.spiritbike.workout.WorkoutDashboardActivity;

import java.util.Calendar;

import static com.dyaco.spiritbike.MyApplication.IMPERIAL_MAX_WEIGHT;
import static com.dyaco.spiritbike.MyApplication.IMPERIAL_MIN_WEIGHT;
import static com.dyaco.spiritbike.MyApplication.METRIC_MAX_WEIGHT;
import static com.dyaco.spiritbike.MyApplication.METRIC_MIN_WEIGHT;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.MyApplication.isFTMSNotify;
import static com.dyaco.spiritbike.MyApplication.isFtmsConnected;
import static com.dyaco.spiritbike.support.CommonUtils.kg2lb;
import static com.dyaco.spiritbike.support.CommonUtils.lb2kg;
import static com.dyaco.spiritbike.support.ProgramsEnum.MANUAL;


public class HomeScreenGuestFragment extends BaseFragment implements RulerView.OnValueChangeListener {

    private TextView tv_show_age;
    private TextView tv_show_weight;

    private ImageButton bt_age_up;
    private ImageButton bt_age_down;

    private ImageButton bt_weight_up;
    private ImageButton bt_weight_down;

    private RulerView age_Animation;
    private RulerView weight_Animation;
    private long guestUid;

    private Button btnChooseProgram;
    private Button btnChooseSave;
    private View mView;

    private TextView tvWeightUnit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            guestUid = getArguments().getLong("GuestUid", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home_screen_guest, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view;
        ((DashboardActivity) mActivity).changeTopWidgetStyle(true);
        ((DashboardActivity) mActivity).changeSignOutToBack(false, true, 0, -1);

        if (guestUid != 0) {
            //從登入頁來的
            getGuestData();
        } else {
            //切換頁面
            userProfileEntity = getInstance().getUserProfile();
            initView(view);
            initEvent();
        }
    }

    public UserProfileEntity userProfileEntity;

    private void getGuestData() {

        DatabaseManager.getInstance(MyApplication.getInstance()).getUserProfileById(guestUid,
                new DatabaseCallback<UserProfileEntity>() {
                    @Override
                    public void onDataLoadedBean(UserProfileEntity userProfile) {
                        super.onDataLoadedBean(userProfile);
                        getInstance().setUserProfile(userProfile);
                        userProfileEntity = userProfile;

                        initView(mView);
                        initEvent();
                    }
                });
    }

    private void initView(View view) {

        tvWeightUnit = view.findViewById(R.id.tv_kg);

        btnChooseProgram = view.findViewById(R.id.guest_b_text_1);
        btnChooseSave = view.findViewById(R.id.guest_b_text_3);

        tv_show_age = view.findViewById(R.id.tv_show_age);
        tv_show_weight = view.findViewById(R.id.tv_show_weight);

        bt_age_up = view.findViewById(R.id.bt_age_up);
        bt_age_down = view.findViewById(R.id.bt_age_down);
        bt_weight_up = view.findViewById(R.id.bt_weight_up);
        bt_weight_down = view.findViewById(R.id.bt_weight_down);

        age_Animation = view.findViewById(R.id.age_Animation);
        weight_Animation = view.findViewById(R.id.weight_Animation);


        age_Animation.setOnValueChangeListener(this);
        weight_Animation.setOnValueChangeListener(this);

        try {
            int age = Integer.parseInt(CommonUtils.getAgeByBirth(userProfileEntity.getBirthday()));
            age_Animation.setSelectedValue(age);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //    weight_Animation.setSelectedValue(70.5f);
        // weight_Animation.setSelectedValue(70);

        int weight;
        if (userProfileEntity.getUnit() == 0) {
            weight = userProfileEntity.getWeight_metric();
            tvWeightUnit.setText(R.string.kg);
            weight_Animation.setMaxValue(METRIC_MAX_WEIGHT);
            weight_Animation.setMinValue(METRIC_MIN_WEIGHT);
        } else {
            weight = userProfileEntity.getWeight_imperial();
            tvWeightUnit.setText(R.string.lb);
            weight_Animation.setMaxValue(IMPERIAL_MAX_WEIGHT);
            weight_Animation.setMinValue(IMPERIAL_MIN_WEIGHT);
        }
        //預設
        weight_Animation.setSelectedValue(weight);

        Button btQuickStart_HomeScreen = view.findViewById(R.id.btQuickStart_HomeScreen);

        btQuickStart_HomeScreen.setOnClickListener(v -> {

            WorkoutBean workoutBean = new WorkoutBean();
            workoutBean.setProgramName(MANUAL.getText());
            workoutBean.setProgramId(MANUAL.getCode());
            workoutBean.setOrgMaxLevel(20);
            workoutBean.setBaseProgramId(MANUAL.getCode());
            workoutBean.setInclineDiagramNum(MANUAL.getInclineNum());
            workoutBean.setLevelDiagramNum(MANUAL.getLevelNum());
            workoutBean.setMaxLevel(20);
            workoutBean.setTimeSecond(0);

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("WorkoutBean", workoutBean);
            intent.putExtras(bundle);
            intent.setClass(mActivity, WorkoutDashboardActivity.class);
            startActivity(intent);
            mActivity.finish();
            mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            MyApplication.SSEB = false;
        });
    }

    private void initEvent() {
        btnChooseProgram.setOnClickListener(v -> {

            ((DashboardActivity) mActivity).rbPrograms_Dashboard.setChecked(true);
            ((DashboardActivity) mActivity).navController.navigate(R.id.programsFragment);
        });

        btnChooseSave.setOnClickListener(v -> {

            ((DashboardActivity) mActivity).rbPrograms_Dashboard.setChecked(true);
            Bundle bundleSend = new Bundle();
            bundleSend.putBoolean("Open_Templates", true);
            ((DashboardActivity) mActivity).navController.navigate(R.id.programsFragment, bundleSend);
        });

        bt_age_up.setOnClickListener(view -> {
            age_Animation.setSelectedValue(Float.parseFloat(tv_show_age.getText().toString()) + 1);
        });

        bt_age_down.setOnClickListener(view ->
                age_Animation.setSelectedValue(Float.parseFloat(tv_show_age.getText().toString()) - 1));

        bt_weight_up.setOnClickListener(view -> weight_Animation.setSelectedValue(Float.parseFloat(tv_show_weight.getText().toString()) + 1));

        bt_weight_down.setOnClickListener(view ->
                weight_Animation.setSelectedValue(Float.parseFloat(tv_show_weight.getText().toString()) - 1));


        new CommonUtils().addAutoClick(bt_age_up);
        new CommonUtils().addAutoClick(bt_age_down);
        new CommonUtils().addAutoClick(bt_weight_up);
        new CommonUtils().addAutoClick(bt_weight_down);

//        bt_weight_up.setOnClickListener(view -> weight_Animation.setSelectedValue(Float.parseFloat(tv_show_weight.getText().toString()) + 0.1f));
//
//        bt_weight_down.setOnClickListener(view ->
//                weight_Animation.setSelectedValue(Float.parseFloat(tv_show_weight.getText().toString()) - 0.1f));
    }

    @Override
    public void onChange(RulerView view, float value) {

        if (view.getId() == R.id.age_Animation) {
            tv_show_age.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
            int year = (Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(tv_show_age.getText().toString()));

            //   Log.d("BBBB", "onChange: " + Calendar.getInstance().get(Calendar.YEAR) +","+tv_show_age.getText().toString());
            userProfileEntity.setBirthday(year + "-01-01");
        } else {
            tv_show_weight.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
            //   tv_show_weight.setText(String.valueOf(value));

            int weight = Integer.parseInt(tv_show_weight.getText().toString());
            //  userProfileEntity.setWeight_metric(Integer.parseInt(tv_show_weight.getText().toString()));
            userProfileEntity.setWeight_metric(userProfileEntity.getUnit() == 0 ? weight : lb2kg(weight));
            userProfileEntity.setWeight_imperial(userProfileEntity.getUnit() == 1 ? weight : kg2lb(weight));
        }
        getInstance().setUserProfile(userProfileEntity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        tv_show_age = null;
        tv_show_weight = null;

        bt_age_up = null;
        bt_age_down = null;

        bt_weight_up = null;
        bt_weight_down = null;

        age_Animation = null;
        weight_Animation = null;

        btnChooseProgram = null;
        btnChooseSave = null;
        mView = null;
        tvWeightUnit = null;
    }
}