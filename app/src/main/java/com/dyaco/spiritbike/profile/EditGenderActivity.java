package com.dyaco.spiritbike.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.webapi.BaseCallWebApi;

import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.CommonUtils.checkStr;

public class EditGenderActivity extends BaseAppCompatActivity {

    private final UserProfileEntity userProfileEntity = getInstance().getUserProfile();

    private RadioButton m_rbMale_NewGender;
    private RadioButton m_rbFemale_NewGender;
    private TextView m_tvMale_NewGender;
    private TextView m_tvFemale_NewGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gender);

        Button btNo_DialogDataLost = findViewById(R.id.btNo_DialogDataLost);
        Button btYes_DialogDataLost = findViewById(R.id.btYes_DialogDataLost);

        m_tvMale_NewGender = findViewById(R.id.tvMale_NewGender);
        m_tvFemale_NewGender = findViewById(R.id.tvFemale_NewGender);

        m_rbMale_NewGender = findViewById(R.id.rbMale_NewGender);
        m_rbFemale_NewGender = findViewById(R.id.rbFemale_NewGender);

        m_rbMale_NewGender.setOnClickListener(rbGenderOnClick);
        m_rbFemale_NewGender.setOnClickListener(rbGenderOnClick);
        // m_rbMale_NewGender.setChecked(true);

        Button btnClose = findViewById(R.id.btClose_NewGender);

        btnClose.setOnClickListener(view -> {
            MyApplication.SSEB = false;
            finish();
        });

        btNo_DialogDataLost.setOnClickListener(v -> updateData());

        btYes_DialogDataLost.setOnClickListener(v -> {
            MyApplication.SSEB = false;
            finish();
        });
        RadioGroup rgGender_NewGender = findViewById(R.id.rgGender_NewGender);
        int check = userProfileEntity.getGender() == 0 ? R.id.rbFemale_NewGender : R.id.rbMale_NewGender;
        rgGender_NewGender.check(check);
        initData();
    }


    private final View.OnClickListener rbGenderOnClick = view -> initData();

    private void initData() {
        m_tvMale_NewGender.setTextColor(m_rbMale_NewGender.isChecked() ?
                ContextCompat.getColor(EditGenderActivity.this, R.color.colorE4002B) : ContextCompat.getColor(EditGenderActivity.this, R.color.color597084));
        m_tvFemale_NewGender.setTextColor(m_rbFemale_NewGender.isChecked() ?
                ContextCompat.getColor(EditGenderActivity.this, R.color.colorE4002B) : ContextCompat.getColor(EditGenderActivity.this, R.color.color597084));
    }

    private void updateData() {
        int i = -1;

        if (m_rbMale_NewGender.isChecked()) {
            i = 1;
        }

        if (m_rbFemale_NewGender.isChecked()) {
            i = 0;
        }

        if (i != -1) {

            userProfileEntity.setGender(i);
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
        } else {
            Toast.makeText(EditGenderActivity.this, "SELECT:", Toast.LENGTH_LONG).show();
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