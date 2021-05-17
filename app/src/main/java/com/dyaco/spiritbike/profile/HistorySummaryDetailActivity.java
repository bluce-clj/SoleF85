package com.dyaco.spiritbike.profile;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyaco.spiritbike.CommandErrorBean;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.product_flavors.ModeEnum;
import com.dyaco.spiritbike.programs.TemplateFullActivity;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.GlideApp;
import com.dyaco.spiritbike.support.UnitEnum;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.entity.HistoryEntity;
import com.dyaco.spiritbike.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.dyaco.spiritbike.MyApplication.MODE;
import static com.dyaco.spiritbike.support.CommonUtils.convertUnit;
import static com.dyaco.spiritbike.support.CommonUtils.formatSec2H;


public class HistorySummaryDetailActivity extends BaseAppCompatActivity {
    private View cl_incline;
    private View v_incline;
    private TextView datetime_title;
    private String title;
    private HistoryEntity historyEntity;

    private TextView tvTotalDistance;
    private TextView tvTime;
    private TextView tvAvgSpeed;
    private TextView tvCalories;
    private TextView tvMaxPower;
    private TextView tvAvgMET;
    private TextView tvAvgPower;

    private TextView avgSpeedUnit;
    private TextView tvTotalDistanceUnit;
    private TextView tvAvgLevelUnit;
    private TextView tvAvgLevel;
    private TextView tvAvgIncline;
    private TextView tvMaxIncline;
    private TextView tvMaxLevel;

    private TextView tv_v4_avg_hr;
    private TextView tv_v4_max_hr;

    private Button bt_del_as;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_pop);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        historyEntity = (HistoryEntity) bundle.getSerializable("historyEntity");

        title = historyEntity.getHistoryName();

        initView();

        iniData();
    }

    private void iniData() {


        String date = new SimpleDateFormat("hh:mm a, MMM dd, yyyy", Locale.ENGLISH).format(historyEntity.getUpdateTime());
        datetime_title.setText(date);

        //  avgSpeedUnit.setText(UnitEnum.getUnit(UnitEnum.SPEED));
        tvTotalDistanceUnit.setText(UnitEnum.getUnit(UnitEnum.DISTANCE));
        tvAvgLevelUnit.setText(UnitEnum.getUnit(UnitEnum.SPEED));

        // tvTotalDistance.setText(historyEntity.getTotalDistance());

        tvTotalDistance.setText(String.format(Locale.getDefault(), "%.2f", convertUnit(UnitEnum.DISTANCE, historyEntity.getUnit(), Double.parseDouble(historyEntity.getTotalDistance()))));

       // tvTime.setText(formatSecToM(historyEntity.getRunTime()));
        tvTime.setText(formatSec2H(historyEntity.getRunTime()));
        tvAvgSpeed.setText(CommonUtils.formatDecimal(Double.parseDouble(historyEntity.getAvgSpeed()), 1));
        tvCalories.setText(CommonUtils.formatDecimal(Double.parseDouble(historyEntity.getCalories()), 1));

        tvAvgMET.setText(CommonUtils.formatDecimal(Double.parseDouble(historyEntity.getAvgMET()), 1));
        tvAvgPower.setText(historyEntity.getAvgWATT());
        tvMaxPower.setText(historyEntity.getMaxWATT());

        tvAvgLevel.setText(historyEntity.getAvgLevel());
        //    tvAvgIncline.setText(historyEntity.getAvgIncline());

        tvMaxLevel.setText(historyEntity.getMaxLevel());
        //    tvMaxIncline.setText(historyEntity.getMaxIncline());

        tv_v4_avg_hr.setText(historyEntity.getAvgHR());
        tv_v4_max_hr.setText(historyEntity.getMaxHR());

        if (MODE != ModeEnum.XE395ENT) {
            cl_incline.setVisibility(View.GONE);
            v_incline.setVisibility(View.GONE);
        } else {
            try {
                if (historyEntity.getAvgIncline() != null)
                    tvAvgIncline.setText(String.valueOf(CommonUtils.incI2F(Integer.parseInt(historyEntity.getAvgIncline()))));

                if (historyEntity.getMaxIncline() != null)
                    tvMaxIncline.setText(String.valueOf(CommonUtils.incI2F(Integer.parseInt(historyEntity.getMaxIncline()))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        bt_del_as.setOnClickListener(v -> {
            showDeleteDialog();
        });
    }

    private void initView() {
        bt_del_as = findViewById(R.id.bt_del_as);
        datetime_title = findViewById(R.id.datetime_title);
        cl_incline = findViewById(R.id.cl_incline);
        v_incline = findViewById(R.id.v_incline);

        tvAvgLevel = findViewById(R.id.tv_m_22_avg_level);
        tvAvgIncline = findViewById(R.id.tv_v3_avg_incline);

        tvMaxLevel = findViewById(R.id.tv_m_25_max_level);
        tvMaxIncline = findViewById(R.id.tv_v3_max_incline);

        avgSpeedUnit = findViewById(R.id.tv_m_8);
        tvTotalDistanceUnit = findViewById(R.id.tv_m_2);
        tvAvgLevelUnit = findViewById(R.id.tv_m_23);

        tvTotalDistance = findViewById(R.id.tv_m_1);
        tvTime = findViewById(R.id.tv_m_4);
        tvAvgSpeed = findViewById(R.id.tv_m_6);
        tvCalories = findViewById(R.id.tv_m_11);
        tvMaxPower = findViewById(R.id.tv_m_14);
        tvAvgMET = findViewById(R.id.tv_m_16);
        tvAvgPower = findViewById(R.id.tv_m_9);

        tv_v4_avg_hr = findViewById(R.id.tv_v4_avg_hr);
        tv_v4_max_hr = findViewById(R.id.tv_v4_max_hr);

        ImageView ivLevelDiagram = findViewById(R.id.iv_level_diagram);
        ImageView ivInclineDiagram = findViewById(R.id.iv_incline_diagram);
        ImageView iv_hr_diagram = findViewById(R.id.iv_hr_diagram);

        //  Bitmap bitmapLevel = new CommonUtils().getImageBitmap(this, historyEntity.getDiagramLevel(), 60, 1200);
        Bitmap bitmapLevel = new CommonUtils().getDiagramBitmapXX(this, historyEntity.getDiagramLevel());
        GlideApp.with(this)
                .load(bitmapLevel)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(ivLevelDiagram);

        //  Bitmap bitmapIncline = new CommonUtils().getImageBitmap(this, historyEntity.getDiagramIncline(), 60, 1200);
        Bitmap bitmapIncline = new CommonUtils().getDiagramBitmapXX(this, historyEntity.getDiagramIncline());
        GlideApp.with(this)
                .load(bitmapIncline)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(ivInclineDiagram);

        Bitmap bitmapHR = new CommonUtils().getDiagramBitmapXX(this, historyEntity.getDiagramHR());
        GlideApp.with(this)
                .load(bitmapHR)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(iv_hr_diagram);

        Button btClose_UserName = findViewById(R.id.btClose);
        TextView bt_save_as = findViewById(R.id.bt_save_as);
        TextView tvTitleText = findViewById(R.id.title_text);
        tvTitleText.setText(title);
        btClose_UserName.setOnClickListener(v -> {
            MyApplication.SSEB = false;
            finish();
        });

        bt_save_as.setOnClickListener(v -> {
            checkTemplateSum();
        });
    }

    private void checkTemplateSum() {

        DatabaseManager.getInstance(MyApplication.getInstance()).checkTemplateSum(MyApplication.getInstance().getUserProfile().getUid(),
                new DatabaseCallback<Integer>() {
                    @Override
                    public void onCount(Integer i) {
                        super.onCount(i);
                        Log.i("@@@@@@@@@@", "onCount: " + i);

                        //大於等於12筆 要刪除
                        if (i >= 12) {
                            MyApplication.SSEB = false;
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            intent.putExtras(bundle);
                            intent.setClass(HistorySummaryDetailActivity.this, TemplateFullActivity.class);
                            startActivity(intent);
                        } else {
                            goSave();
                        }
                    }
                });
    }

    private void goSave() {
        MyApplication.SSEB = false;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("historyEntity", historyEntity);
        intent.putExtras(bundle);
        intent.setClass(HistorySummaryDetailActivity.this, SaveProgramAsTemplateActivity.class);
        startActivity(intent);
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

    private void deleteHistory(int historyParentUid, int historyId) {
        DatabaseManager.getInstance(MyApplication.getInstance()).
                deleteHistory(historyParentUid, historyId,
                        new DatabaseCallback<HistoryEntity>() {
                            @Override
                            public void onDeleted() {
                                super.onDeleted();


                                if (dialog != null) dialog.dismiss();

                                MyApplication.SSEB = false;
                                finish();
                            }

                            @Override
                            public void onError(String err) {
                                super.onError(err);
                                if (dialog != null) dialog.dismiss();
                                Toast.makeText(HistorySummaryDetailActivity.this, "Failure:" + err, Toast.LENGTH_LONG).show();
                            }
                        });
    }


    Dialog dialog;
    public void showDeleteDialog() {

        dialog = new Dialog(HistorySummaryDetailActivity.this, android.R.style.ThemeOverlay);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        dialog.setContentView(R.layout.dialog_delete_history);
        Button btn_yes = dialog.findViewById(R.id.btYes_DialogDataLost);
        Button btn_no = dialog.findViewById(R.id.btNo_DialogDataLost);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        btn_no.setOnClickListener(v -> dialog.dismiss());

        btn_yes.setOnClickListener(v -> {
            deleteHistory(historyEntity.getHistoryParentUid(), historyEntity.getUid());
        });
    }

}