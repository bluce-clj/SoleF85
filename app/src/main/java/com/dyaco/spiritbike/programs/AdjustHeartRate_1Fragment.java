package com.dyaco.spiritbike.programs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.ProgramsEnum;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.support.ruler.RulerView;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.R;
import static com.dyaco.spiritbike.MyApplication.getInstance;


public class AdjustHeartRate_1Fragment extends BaseFragment implements RulerView.OnValueChangeListener {
    private final UserProfileEntity userProfileEntity = getInstance().getUserProfile();
    private RulerView rulerViewAmPmHours;
    private TextView m_tvMale_NewGender;
    private TextView m_tvFemale_NewGender;

    private TextView tv_left_show_num;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public AdjustHeartRate_1Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_adjust_heart_rate_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
        ((DashboardActivity) mActivity).changeSignOutToBack(true, false, R.id.programsDetailsFragment, ProgramsEnum.HEART_RATE.getCode());
     //   ((DashboardActivity) mActivity).invisibleIndicator();

        initView(view);

        Button btNext_SetDate = view.findViewById(R.id.btNext_SetDate);

        btNext_SetDate.setOnClickListener(v -> {
            String targetHeartRate = tv_left_show_num.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString("TargetHeartRate", targetHeartRate);
//            final FragmentManager fm = ((FragmentActivity)mActivity).getSupportFragmentManager();
//            NavHostFragment navHostFragment = (NavHostFragment) fm.findFragmentById(R.id.nhcDashboard);
//            navHostFragment.getNavController().navigate(R.id.adjustHeartRate_2Fragment, bundle);

            ((DashboardActivity)mActivity).navController.navigate(R.id.adjustHeartRate_2Fragment, bundle);
        });


    }

    private void initView(View view) {
        rulerViewAmPmHours = view.findViewById(R.id.rulerViewLeft);
        m_tvMale_NewGender = view.findViewById(R.id.tvMale_NewGender);
        m_tvFemale_NewGender = view.findViewById(R.id.tvFemale_NewGender);
        tv_left_show_num = view.findViewById(R.id.tv_left_show_num);
        ImageButton bt_left_minus = view.findViewById(R.id.bt_left_minus);
        ImageButton bt_left_plus = view.findViewById(R.id.bt_left_plus);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);

        rulerViewAmPmHours.setOnValueChangeListener(this);

        int value80 = (int) ((220 - Integer.parseInt(CommonUtils.getAgeByBirth(userProfileEntity.getBirthday()))) * 0.8);

        rulerViewAmPmHours.setSelectedValue(value80);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbAm_SetTime12) {
                m_tvMale_NewGender.setTextColor(ContextCompat.getColor(mActivity, R.color.colorE4002B));
                m_tvFemale_NewGender.setTextColor(ContextCompat.getColor(mActivity, R.color.color597084));

                int value = (int) ((220 - Integer.parseInt(CommonUtils.getAgeByBirth(userProfileEntity.getBirthday()))) * 0.65);

                rulerViewAmPmHours.setSelectedValue(value);
                //(220 - age) * 65

            } else {
                m_tvMale_NewGender.setTextColor(ContextCompat.getColor(mActivity, R.color.color597084));
                m_tvFemale_NewGender.setTextColor(ContextCompat.getColor(mActivity, R.color.colorE4002B));
                int value = (int) ((220 - Integer.parseInt(CommonUtils.getAgeByBirth(userProfileEntity.getBirthday()))) * 0.8);
                rulerViewAmPmHours.setSelectedValue(value);
                //(220 - age) * 80
            }
        });


        bt_left_minus.setOnClickListener(view1 -> rulerViewAmPmHours.setSelectedValue(Float.parseFloat(tv_left_show_num.getText().toString()) - 1));
        bt_left_plus.setOnClickListener(view12 -> rulerViewAmPmHours.setSelectedValue(Float.parseFloat(tv_left_show_num.getText().toString()) + 1));

        new CommonUtils().addAutoClick(bt_left_minus);
        new CommonUtils().addAutoClick(bt_left_plus);
    }

    @Override
    public void onChange(RulerView view, float value) {
        if (view.getId() == R.id.rulerViewLeft) {
            tv_left_show_num.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
        }
    }
}