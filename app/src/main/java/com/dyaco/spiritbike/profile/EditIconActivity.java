package com.dyaco.spiritbike.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.R;

import static com.dyaco.spiritbike.MyApplication.getInstance;

public class EditIconActivity extends BaseAppCompatActivity {

    private final UserProfileEntity userProfileEntity = getInstance().getUserProfile();
    private String m_strAction;
    private RadioGroup m_rgUp_AvatarIcon;
    private RadioGroup m_rgDown_AvatarIcon;
    private boolean m_bChecked = false;
    private int mAvatarIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_icon);

        Button btn_close = findViewById(R.id.btClose_AvatarIcon);

        Button btNo_DialogDataLost = findViewById(R.id.btNo_DialogDataLost);
        Button btYes_DialogDataLost = findViewById(R.id.btYes_DialogDataLost);

        btn_close.setOnClickListener(view -> {
            MyApplication.SSEB = false;
            finish();
        });

        btNo_DialogDataLost.setOnClickListener(v -> {
            MyApplication.SSEB = false;
            finish();
        });

        btYes_DialogDataLost.setOnClickListener(v -> {

            if (m_rgUp_AvatarIcon.getCheckedRadioButtonId() != -1) {
                mAvatarIcon = m_rgUp_AvatarIcon.getCheckedRadioButtonId();
            }

            if (m_rgDown_AvatarIcon.getCheckedRadioButtonId() != -1) {
                mAvatarIcon = m_rgDown_AvatarIcon.getCheckedRadioButtonId();
            }

            if (mAvatarIcon != -1) {
                mAvatarIcon = CommonUtils.getAvatarResourceId(mAvatarIcon);
                updateData();
            } else {
                Toast.makeText(getInstance(), "Please Choose an Avatar.", Toast.LENGTH_SHORT).show();
            }
                }
        );
        m_rgUp_AvatarIcon = findViewById(R.id.rgUp_AvatarIcon);
        m_rgDown_AvatarIcon = findViewById(R.id.rgDown_AvatarIcon);

        m_rgUp_AvatarIcon.setOnCheckedChangeListener(rgAvatarIconOnCheckedChange);
        m_rgDown_AvatarIcon.setOnCheckedChangeListener(rgAvatarIconOnCheckedChange);

        setSelected();


    }

    private final RadioGroup.OnCheckedChangeListener rgAvatarIconOnCheckedChange = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if (m_bChecked)
                return;

            if (radioGroup.getId() == R.id.rgUp_AvatarIcon) {
                m_bChecked = true;
                m_rgDown_AvatarIcon.clearCheck();
                m_bChecked = false;
            } else if (radioGroup.getId() == R.id.rgDown_AvatarIcon) {
                m_bChecked = true;
                m_rgUp_AvatarIcon.clearCheck();
                m_bChecked = false;
            }
        }
    };

    private void setSelected() {

        int i = userProfileEntity.getUserImage();
        if (i == R.drawable.avatar_create_1_inactive) {
            ((RadioButton)findViewById(R.id.rbAvatar01_AvatarIcon)).setChecked(true);
        } else if (i == R.drawable.avatar_create_2_inactive){
            ((RadioButton)findViewById(R.id.rbAvatar02_AvatarIcon)).setChecked(true);
        }else if (i == R.drawable.avatar_create_3_inactive){
            ((RadioButton)findViewById(R.id.rbAvatar03_AvatarIcon)).setChecked(true);
        }else if (i == R.drawable.avatar_create_4_inactive){
            ((RadioButton)findViewById(R.id.rbAvatar04_AvatarIcon)).setChecked(true);
        }else if (i == R.drawable.avatar_create_5_inactive){
            ((RadioButton)findViewById(R.id.rbAvatar05_AvatarIcon)).setChecked(true);
        }else if (i == R.drawable.avatar_create_6_inactive){
            ((RadioButton)findViewById(R.id.rbAvatar06_AvatarIcon)).setChecked(true);
        }else if (i == R.drawable.avatar_create_7_inactive){
            ((RadioButton)findViewById(R.id.rbAvatar07_AvatarIcon)).setChecked(true);
        }else if (i == R.drawable.avatar_create_8_inactive){
            ((RadioButton)findViewById(R.id.rbAvatar08_AvatarIcon)).setChecked(true);
        }else if (i == R.drawable.avatar_create_9_inactive){
            ((RadioButton)findViewById(R.id.rbAvatar09_AvatarIcon)).setChecked(true);
        }else {
            ((RadioButton)findViewById(R.id.rbAvatar10_AvatarIcon)).setChecked(true);
        }
    }

    private void updateData() {
        userProfileEntity.setUserImage(mAvatarIcon);
        DatabaseManager.getInstance(getApplicationContext()).updateUserProfile(userProfileEntity, new DatabaseCallback<UserProfileEntity>() {
            @Override
            public void onUpdated() {
                super.onUpdated();

            //    setUserProfile(userProfileEntity);

                MyApplication.SSEB = false;
                finish();
            }

            @Override
            public void onError(String err) {
                super.onError(err);


             //   Toast.makeText(EditIconActivity.this, "Failure:" + err, Toast.LENGTH_LONG).show();
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