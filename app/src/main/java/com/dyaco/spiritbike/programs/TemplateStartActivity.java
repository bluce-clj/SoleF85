package com.dyaco.spiritbike.programs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.product_flavors.ModeEnum;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.GlideApp;
import com.dyaco.spiritbike.support.ProgramsEnum;
import com.dyaco.spiritbike.workout.WorkoutBean;
import com.dyaco.spiritbike.support.room.entity.TemplateEntity;
import com.dyaco.spiritbike.support.ruler.RulerView;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.workout.WorkoutDashboardActivity;

import java.util.Arrays;

import static com.dyaco.spiritbike.MyApplication.MODE;
import static com.dyaco.spiritbike.support.CommonUtils.findMaxInt;

public class TemplateStartActivity extends BaseAppCompatActivity implements RulerView.OnValueChangeListener {

    private TextView tvLeftShowNumber;
    private RulerView rulerViewLeft;

  //  private TextView tvRightShowNumber;
   // private RulerView rulerViewRight;
    private String templateName;
    private TemplateEntity templateEntity;
    private int templateParentUid;
    private int maxLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_start);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        templateEntity = (TemplateEntity) bundle.getSerializable("templateEntity");
        templateParentUid = bundle.getInt("templateParentUid", 0);

        templateName = templateEntity.getTemplateName();

        initView();

        int[] arrayLevel = Arrays.stream(templateEntity.getDiagramLevel().split("#", -1))
                .mapToInt(Integer::parseInt)
                .toArray();

        maxLevel = Math.max(findMaxInt(arrayLevel), 5);

     //   rulerViewRight.setSelectedValue(max);
    }

    public void onDelete(View view) {
        MyApplication.SSEB = false;
        Intent intent = new Intent(TemplateStartActivity.this, DeleteProgramConfirmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("templateEntity", templateEntity);
        bundle.putInt("templateParentUid", templateParentUid);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onClose(View view) {
        MyApplication.SSEB = false;
        finish();
    }

    private void initView() {
        TextView titleName = findViewById(R.id.tvTitle_UserName);
        titleName.setText(templateName);
        tvLeftShowNumber = findViewById(R.id.tv_left_show_num);
        ImageButton btLeftPlus = findViewById(R.id.bt_left_plus);
        ImageButton btLeftMinus = findViewById(R.id.bt_left_minus);
        rulerViewLeft = findViewById(R.id.rulerViewLeft);
        rulerViewLeft.setOnValueChangeListener(this);

    //    tvRightShowNumber = findViewById(R.id.tv_right_show_num_cm);
       // Button btRightPlus = findViewById(R.id.bt_right_plus);
//        Button btRightMinus = findViewById(R.id.bt_right_minus);
//        rulerViewRight = findViewById(R.id.rulerViewRight);
//        rulerViewRight.setOnValueChangeListener(this);

        TextView tvBasedOn = findViewById(R.id.tv_based_on);
        tvBasedOn.setText(String.format("Based on: %s", ProgramsEnum.getProgram(templateEntity.getBaseProgramId()).getText()));

        ImageView ivDiagramIncline = findViewById(R.id.iv_diagram_incline);
        if (MODE == ModeEnum.XE395ENT) {
            Log.d("@KKKKKKK", "initView: " + templateEntity.getDiagramIncline());
            Bitmap bitmapIncline = new CommonUtils().getDiagramBitmapSmall(this, templateEntity.getDiagramIncline());
         //   Bitmap bitmapIncline = new CommonUtils().getDiagramBitmapXX(this, templateEntity.getDiagramIncline());
         //   Bitmap bitmapIncline = new CommonUtils().getImageBitmap(this, templateEntity.getDiagramIncline(), 60, 1200);
            GlideApp.with(this)
                    .load(bitmapIncline)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(ivDiagramIncline);
        } else {
            TextView tvIncline = findViewById(R.id.tv_incline);
            ivDiagramIncline.setVisibility(View.INVISIBLE);
            tvIncline.setVisibility(View.INVISIBLE);
        }


        ImageView ivDiagramLevel = findViewById(R.id.iv_diagram_level);
        Bitmap bitmapLevel = new CommonUtils().getImageBitmap(this, templateEntity.getDiagramLevel(), 60, 1200);
     //   Bitmap bitmapLevel = new CommonUtils().getDiagramBitmapXX(this, templateEntity.getDiagramLevel());
        GlideApp.with(this)
                .load(bitmapLevel)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(ivDiagramLevel);

        Button btNext_SetDate = findViewById(R.id.btNext_SetDate);

        btNext_SetDate.setOnClickListener(v -> {

            WorkoutBean workoutBean = new WorkoutBean();
            workoutBean.setProgramName(templateEntity.getTemplateName());
            workoutBean.setProgramId(templateEntity.getUseProgramId());
            workoutBean.setOrgMaxLevel(maxLevel);
            workoutBean.setBaseProgramId(templateEntity.getBaseProgramId());
            workoutBean.setLevelDiagramNum(templateEntity.getDiagramLevel());
            workoutBean.setInclineDiagramNum(templateEntity.getDiagramIncline());
         //   workoutBean.setMaxLevel(Integer.parseInt(tvRightShowNumber.getText().toString()));
            workoutBean.setMaxLevel(maxLevel);
            workoutBean.setTimeSecond(Integer.parseInt(tvLeftShowNumber.getText().toString()) * 60);
            workoutBean.setTemplate(true);

            Intent intent = new Intent(this, WorkoutDashboardActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("WorkoutBean", workoutBean);
            intent.putExtras(bundle);
          //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finishAffinity();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            MyApplication.SSEB = false;
        });

        btLeftMinus.setOnClickListener(v -> rulerViewLeft.setSelectedValue(Float.parseFloat(tvLeftShowNumber.getText().toString()) - 1));

        btLeftPlus.setOnClickListener(view12 -> {
            if ("0".equals(tvLeftShowNumber.getText().toString())) {
                rulerViewLeft.setSelectedValue(10);
            } else {
                rulerViewLeft.setSelectedValue(Float.parseFloat(tvLeftShowNumber.getText().toString()) + 1);
            }
        });
        rulerViewLeft.setSelectedValue(10);


        new CommonUtils().addAutoClick(btLeftMinus);
        new CommonUtils().addAutoClick(btLeftPlus);

      //  btRightMinus.setOnClickListener(view1 -> rulerViewRight.setSelectedValue(Float.parseFloat(tvRightShowNumber.getText().toString()) - 1));
      //  btRightPlus.setOnClickListener(view12 -> rulerViewRight.setSelectedValue(Float.parseFloat(tvRightShowNumber.getText().toString()) + 1));
    }

    @Override
    public void onChange(RulerView view, float value) {
        if (view.getId() == R.id.rulerViewLeft) {
            if (value == 9) {
                value = 0;
            }
            tvLeftShowNumber.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
//        } else {
//            tvRightShowNumber.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
        }
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