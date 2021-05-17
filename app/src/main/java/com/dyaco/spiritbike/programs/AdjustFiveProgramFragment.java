package com.dyaco.spiritbike.programs;

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

import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.product_flavors.BaseFragment2;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.ProgramsEnum;
import com.dyaco.spiritbike.workout.WorkoutBean;
import com.dyaco.spiritbike.support.ruler.RulerView;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.workout.WorkoutDashboardActivity;

import java.util.Arrays;

import static com.dyaco.spiritbike.support.CommonUtils.findMaxInt;


public class AdjustFiveProgramFragment extends BaseFragment2 implements RulerView.OnValueChangeListener {

    private TextView tvLeftShowNumber;
    private RulerView rulerViewLeft;

    private TextView tvRightShowNumber;
    private RulerView rulerViewRight;

    private int programId;

    public AdjustFiveProgramFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            programId = getArguments().getInt("ProgramId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adjust_hill, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
        //   ((DashboardActivity) mActivity).changeSignOutToBack(true, false, 0);
        ((DashboardActivity) mActivity).changeSignOutToBack(true, false, R.id.programsDetailsFragment, programId);

        initView(view);
    }

    private void initView(View view) {
        TextView textTitle = view.findViewById(R.id.display_title);
        textTitle.setText(ProgramsEnum.getProgram(programId).getText());

        tvLeftShowNumber = view.findViewById(R.id.tv_left_show_num);
        ImageButton btLeftPlus = view.findViewById(R.id.bt_left_plus);
        ImageButton btLeftMinus = view.findViewById(R.id.bt_left_minus);
        rulerViewLeft = view.findViewById(R.id.rulerViewLeft);
        rulerViewLeft.setOnValueChangeListener(this);

        tvRightShowNumber = view.findViewById(R.id.tv_right_show_num_cm);
        ImageButton btRightPlus = view.findViewById(R.id.bt_right_plus);
        ImageButton btRightMinus = view.findViewById(R.id.bt_right_minus);
        rulerViewRight = view.findViewById(R.id.rulerViewRight);
        rulerViewRight.setOnValueChangeListener(this);

        int[] arrayLevel = Arrays.stream(ProgramsEnum.getProgram(programId).getLevelNum().split("#", -1))
                .mapToInt(Integer::parseInt)
                .toArray();

        // int maxLevel = findMaxInt(arrayLevel);
        int maxLevel = Math.max(findMaxInt(arrayLevel), 5);
        rulerViewRight.setMinValue(maxLevel);
        rulerViewRight.setSelectedValue(maxLevel);

        //開始Workout
        Button btNext_SetDate = view.findViewById(R.id.btNext_SetDate);
        btNext_SetDate.setOnClickListener(v -> {

//            setResult("我是返回數據");
//            back();


            if (CommonUtils.isFastClick()) {
                return;
            }

            WorkoutBean workoutBean = new WorkoutBean();
            workoutBean.setProgramName(ProgramsEnum.getProgram(programId).getText());
            workoutBean.setProgramId(programId);
            workoutBean.setOrgMaxLevel(maxLevel);
            workoutBean.setBaseProgramId(programId);
            workoutBean.setInclineDiagramNum(ProgramsEnum.getProgram(programId).getInclineNum());
            workoutBean.setLevelDiagramNum(ProgramsEnum.getProgram(programId).getLevelNum());
            workoutBean.setMaxLevel(Integer.parseInt(tvRightShowNumber.getText().toString()));
            workoutBean.setTimeSecond(Integer.parseInt(tvLeftShowNumber.getText().toString()) * 60);

            Intent intent = new Intent(mActivity, WorkoutDashboardActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("WorkoutBean", workoutBean);
            intent.putExtras(bundle);
            //    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            mActivity.finish();
            mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            MyApplication.SSEB = false;
        });


        btLeftMinus.setOnClickListener(view1 -> rulerViewLeft.setSelectedValue(Float.parseFloat(tvLeftShowNumber.getText().toString()) - 1));
        //    btLeftPlus.setOnClickListener(view12 -> rulerViewLeft.setSelectedValue(Float.parseFloat(tvLeftShowNumber.getText().toString()) + 1));

        btLeftPlus.setOnClickListener(view12 -> {
//            if ("0".equals(tvLeftShowNumber.getText().toString())) {
//                rulerViewLeft.setSelectedValue(10);
//            } else {
            rulerViewLeft.setSelectedValue(Float.parseFloat(tvLeftShowNumber.getText().toString()) + 1);
            //   }
        });
        rulerViewLeft.setSelectedValue(10);

        btRightMinus.setOnClickListener(view1 -> rulerViewRight.setSelectedValue(Float.parseFloat(tvRightShowNumber.getText().toString()) - 1));
        btRightPlus.setOnClickListener(view12 -> rulerViewRight.setSelectedValue(Float.parseFloat(tvRightShowNumber.getText().toString()) + 1));

        new CommonUtils().addAutoClick(btRightMinus);
        new CommonUtils().addAutoClick(btRightPlus);
        new CommonUtils().addAutoClick(btLeftMinus);
        new CommonUtils().addAutoClick(btLeftPlus);
    }

    @Override
    public void onChange(RulerView view, float value) {
        if (view.getId() == R.id.rulerViewLeft) {
//            if (value == 9) {
//                value = 0;
//            }
            tvLeftShowNumber.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
        } else {
            tvRightShowNumber.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
        }
    }


    @Override
    protected int requireLayoutId() {
        return 0;
    }

    @Override
    protected void onFindView(View rootView) {

    }

    @Override
    protected void onBindListener() {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        tvLeftShowNumber = null;
        rulerViewLeft = null;
        tvRightShowNumber = null;
        rulerViewRight = null;
    }
}