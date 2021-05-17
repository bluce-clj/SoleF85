package com.dyaco.spiritbike.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.ProgramsEnum;
import com.dyaco.spiritbike.support.room.entity.TemplateEntity;
import com.dyaco.spiritbike.workout.WorkoutBean;
import com.dyaco.spiritbike.workout.WorkoutDashboardActivity;

public class SaveProgramSuccessActivity extends BaseAppCompatActivity {


    private TemplateEntity templateEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_program_success);


        templateEntity = (TemplateEntity) getIntent().getExtras().getSerializable("TemplateEntity");

        Button btNo_DialogDataLost = findViewById(R.id.btNo_DialogDataLost);
        Button btYes_DialogDataLost = findViewById(R.id.btYes_DialogDataLost);

        btNo_DialogDataLost.setOnClickListener(v -> {
                    MyApplication.SSEB = false;
                    //OPEN TEMPLATES
                    //   Intent intent = new Intent(this, DashboardActivity.class);
                    /// Bundle bundle = new Bundle();
                    //  bundle.putBoolean("Open_Templates", true);
                    //   intent.putExtras(bundle);
                    //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    //   startActivity(intent);


                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    Bundle bundle = new Bundle();
                    bundle.putBoolean("Open_Templates", true);
                    intent.putExtras(bundle);

                    intent.setClass(this, DashboardActivity.class);
                    startActivity(intent);
                    finish();

                }
        );

        btYes_DialogDataLost.setOnClickListener(v -> {

                    MyApplication.SSEB = false;
//                    Intent intent = new Intent(this, DashboardActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putBoolean("Open_Templates", true);
//                    intent.putExtras(bundle);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    startActivity(intent);


                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("Open_Templates", true);
                    intent.putExtras(bundle);

                    intent.setClass(this, DashboardActivity.class);
                    startActivity(intent);


//                    WorkoutBean workoutBean = new WorkoutBean();
//                    workoutBean.setProgramName(templateEntity.getTemplateName());
//                    workoutBean.setProgramId(templateEntity.getBaseProgramId());
//                    workoutBean.setLevelDiagramNum(templateEntity.getDiagramLevel());
//                    workoutBean.setInclineDiagramNum(templateEntity.getDiagramIncline());
//                    workoutBean.setMaxLevel(Integer.parseInt(tvRightShowNumber.getText().toString()));
//                    workoutBean.setTimeSecond(Integer.parseInt(tvLeftShowNumber.getText().toString()) * 60);
//
//                    Intent intent = new Intent(SaveProgramSuccessActivity.this, WorkoutDashboardActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("WorkoutBean", workoutBean);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                    finish();
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
        );

    }

    public void onClose(View view) {
        MyApplication.SSEB = false;
        finish();
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