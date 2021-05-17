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

import static com.dyaco.spiritbike.support.CommonUtils.checkStr;
import static com.dyaco.spiritbike.support.CommonUtils.kg2lb;
import static com.dyaco.spiritbike.support.CommonUtils.lb2kg;
import static com.dyaco.spiritbike.MyApplication.IMPERIAL_MAX_WEIGHT;
import static com.dyaco.spiritbike.MyApplication.IMPERIAL_MIN_WEIGHT;
import static com.dyaco.spiritbike.MyApplication.METRIC_MAX_WEIGHT;
import static com.dyaco.spiritbike.MyApplication.METRIC_MIN_WEIGHT;
import static com.dyaco.spiritbike.MyApplication.getInstance;

public class EditWeightActivity extends BaseAppCompatActivity implements RulerView.OnValueChangeListener {
    private TextView tvWeight;
    private RulerView rulerView;
    private ImageButton bt_left;
    private ImageButton bt_right;
    private final UserProfileEntity userProfileEntity = getInstance().getUserProfile();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_weight);


        initView();

        Button btNo_DialogDataLost = findViewById(R.id.btNo_DialogDataLost);
        Button btYes_DialogDataLost = findViewById(R.id.btYes_DialogDataLost);

        btNo_DialogDataLost.setOnClickListener((View v) -> {
            MyApplication.SSEB = false;
            finish();
        });

        btYes_DialogDataLost.setOnClickListener(v -> updateData());
    }

    private void initView() {

        TextView tvWeightUnit = findViewById(R.id.weight_unit);

        findViewById(R.id.btClose).setOnClickListener(view -> {
            MyApplication.SSEB = false;
            finish();
        });

        tvWeight = findViewById(R.id.tv_sound_text);
        rulerView = findViewById(R.id.height_ruler);

        rulerView.setOnValueChangeListener(this);


        int weight;
        if (userProfileEntity.getUnit() == 0) {
            weight = userProfileEntity.getWeight_metric();
            tvWeightUnit.setText(R.string.kg);
            rulerView.setMaxValue(METRIC_MAX_WEIGHT);
            rulerView.setMinValue(METRIC_MIN_WEIGHT);
        } else {
            weight = userProfileEntity.getWeight_imperial();
            tvWeightUnit.setText(R.string.lb);
            rulerView.setMaxValue(IMPERIAL_MAX_WEIGHT);
            rulerView.setMinValue(IMPERIAL_MIN_WEIGHT);
        }

        //預設
        rulerView.setSelectedValue(weight);

        bt_left = findViewById(R.id.bt_left);
        bt_right = findViewById(R.id.bt_right);
        new CommonUtils().addAutoClick(bt_left);
        new CommonUtils().addAutoClick(bt_right);
    }

    @Override
    public void onChange(RulerView view, float value) {

        if (view.getId() == R.id.height_ruler) {
            tvWeight.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
        }
    }

    public void onM(View view) {
        rulerView.setSelectedValue(Float.parseFloat(tvWeight.getText().toString()) - 1);
    }

    public void onP(View view) {
        rulerView.setSelectedValue(Float.parseFloat(tvWeight.getText().toString()) + 1);
    }

    private void updateData() {

        int weight = Integer.parseInt(tvWeight.getText().toString());

        userProfileEntity.setWeight_metric(userProfileEntity.getUnit() == 0 ? weight : lb2kg(weight));
        userProfileEntity.setWeight_imperial(userProfileEntity.getUnit() == 1 ? weight : kg2lb(weight));

        DatabaseManager.getInstance(MyApplication.getInstance()).updateUserProfile(userProfileEntity, new DatabaseCallback<UserProfileEntity>() {
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