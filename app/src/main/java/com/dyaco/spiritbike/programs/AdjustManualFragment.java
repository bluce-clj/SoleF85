package com.dyaco.spiritbike.programs;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.workout.WorkoutBean;
import com.dyaco.spiritbike.support.ruler.RulerView;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.workout.WorkoutDashboardActivity;

import static com.dyaco.spiritbike.support.ProgramsEnum.MANUAL;

public class AdjustManualFragment extends BaseFragment implements RulerView.OnValueChangeListener {

    private TextView display_text;
    private RulerView rulerView;

    public AdjustManualFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adjust_manual, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
        ((DashboardActivity) mActivity).changeSignOutToBack(true, false, R.id.programsDetailsFragment, MANUAL.getCode());

//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            m_nItem = bundle.getInt("item");
//        }


        //開始Workout
        Button btNext_SetDate = view.findViewById(R.id.btNext_SetDate);
        btNext_SetDate.setOnClickListener(v -> {

            if (CommonUtils.isFastClick()) {
                return;
            }

            WorkoutBean workoutBean = new WorkoutBean();
            workoutBean.setProgramName(MANUAL.getText());
            workoutBean.setProgramId(MANUAL.getCode());
            workoutBean.setOrgMaxLevel(20);
            workoutBean.setBaseProgramId(MANUAL.getCode());
            workoutBean.setInclineDiagramNum(MANUAL.getInclineNum());
            workoutBean.setLevelDiagramNum(MANUAL.getLevelNum());
            workoutBean.setMaxLevel(20);
            workoutBean.setTimeSecond(Integer.parseInt(display_text.getText().toString()) * 60);

            Intent intent = new Intent(mActivity, WorkoutDashboardActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("WorkoutBean", workoutBean);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            mActivity.finish();
            mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            MyApplication.SSEB = false;
        });

        ImageButton btnM = view.findViewById(R.id.bt_left);
        ImageButton btnP = view.findViewById(R.id.bt_right);
        display_text = view.findViewById(R.id.display_text);
        rulerView = view.findViewById(R.id.height_ruler);

        rulerView.setOnValueChangeListener(this);

        btnM.setOnClickListener(view1 -> rulerView.setSelectedValue(Float.parseFloat(display_text.getText().toString()) - 1));

        btnP.setOnClickListener(view12 -> {
            if ("0".equals(display_text.getText().toString())) {
                rulerView.setSelectedValue(10);
            } else {
                rulerView.setSelectedValue(Float.parseFloat(display_text.getText().toString()) + 1);
            }
        });

        new CommonUtils().addAutoClick(btnM);
        new CommonUtils().addAutoClick(btnP);

        rulerView.setSelectedValue(10);
    }

    @Override
    public void onChange(RulerView view, float value) {
        if (view.getId() == R.id.height_ruler) {

            if (value == 9) {
                value = 0;
            }
            display_text.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        display_text = null;
        rulerView = null;
    }
}