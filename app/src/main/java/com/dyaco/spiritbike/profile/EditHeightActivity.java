package com.dyaco.spiritbike.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.Group;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.support.ruler.RulerView;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.webapi.BaseCallWebApi;

import static com.dyaco.spiritbike.support.CommonUtils.checkStr;
import static com.dyaco.spiritbike.support.CommonUtils.cm2in;
import static com.dyaco.spiritbike.support.CommonUtils.in2cm;
import static com.dyaco.spiritbike.MyApplication.IMPERIAL_MAX_HEIGHT;
import static com.dyaco.spiritbike.MyApplication.IMPERIAL_MIN_HEIGHT;
import static com.dyaco.spiritbike.MyApplication.METRIC_MAX_HEIGHT;
import static com.dyaco.spiritbike.MyApplication.METRIC_MIN_HEIGHT;
import static com.dyaco.spiritbike.MyApplication.getInstance;

public class EditHeightActivity extends BaseAppCompatActivity implements RulerView.OnValueChangeListener {
    private final UserProfileEntity userProfileEntity = getInstance().getUserProfile();
    private TextView tv_sound_text;
    private RulerView rulerView;

    private TextView tv_height_ft;
    private TextView tv_height_in;
    private int unit;

    ImageButton bt_left;
    ImageButton bt_right;

    RxTimer longTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_height);

        Button btNo_DialogDataLost = findViewById(R.id.btNo_DialogDataLost);
        Button btYes_DialogDataLost = findViewById(R.id.btYes_DialogDataLost);

        btNo_DialogDataLost.setOnClickListener(v -> {
            MyApplication.SSEB = false;
            finish();
        });

        btYes_DialogDataLost.setOnClickListener(v -> updateData());
        initView();
    }

    private void initView() {
        findViewById(R.id.btClose).setOnClickListener(view -> {
            MyApplication.SSEB = false;
            finish();
        });

        unit = userProfileEntity.getUnit();

        Group imperial_group = findViewById(R.id.imperial_group);
        Group metric_group = findViewById(R.id.metric_group);

        tv_height_ft = findViewById(R.id.tv_height_ft);
        tv_height_in = findViewById(R.id.tv_height_in);

        bt_right = findViewById(R.id.bt_right);
        bt_left = findViewById(R.id.bt_left);

        tv_sound_text = findViewById(R.id.tv_sound_text);
        rulerView = findViewById(R.id.height_ruler);

        rulerView.setOnValueChangeListener(this);

        int height;
        if (unit == 0) {
            imperial_group.setVisibility(View.INVISIBLE);
            metric_group.setVisibility(View.VISIBLE);
            height = userProfileEntity.getHeight_metric();
            rulerView.setMaxValue(METRIC_MAX_HEIGHT);
            rulerView.setMinValue(METRIC_MIN_HEIGHT);

        } else {
            imperial_group.setVisibility(View.VISIBLE);
            metric_group.setVisibility(View.INVISIBLE);
            height = userProfileEntity.getHeight_imperial();
            rulerView.setMaxValue(IMPERIAL_MAX_HEIGHT);
            rulerView.setMinValue(IMPERIAL_MIN_HEIGHT);
        }

        //預設
        rulerView.setSelectedValue(height);

        new CommonUtils().addAutoClick(bt_right);
        new CommonUtils().addAutoClick(bt_left);
    }

    @Override
    public void onChange(RulerView view, float value) {

        if (view.getId() == R.id.height_ruler) {
            // tv_sound_text.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));

            if (unit != 0) {
                tv_height_ft.setText(String.valueOf(CommonUtils.in2ft((int) value)));
                tv_height_in.setText(String.valueOf(CommonUtils.in2sin((int) value)));
            }
            tv_sound_text.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));

        }
    }

    public void onM(View view) {
        rulerView.setSelectedValue(Float.parseFloat(tv_sound_text.getText().toString()) - 1);
    }


    public void onP(View view) {
        rulerView.setSelectedValue(Float.parseFloat(tv_sound_text.getText().toString()) + 1);
    }

    private void updateData() {

        int height = Integer.parseInt(tv_sound_text.getText().toString());

        userProfileEntity.setHeight_metric(unit == 0 ? height : in2cm(height));
        userProfileEntity.setHeight_imperial(unit == 1 ? height : cm2in(height));

        DatabaseManager.getInstance(MyApplication.getInstance()).
                updateUserProfile(userProfileEntity, new DatabaseCallback<UserProfileEntity>() {
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