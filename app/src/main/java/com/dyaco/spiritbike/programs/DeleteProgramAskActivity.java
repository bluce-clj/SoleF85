package com.dyaco.spiritbike.programs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.entity.TemplateEntity;

public class DeleteProgramAskActivity extends BaseAppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_program_confirm);

        Button btNo_DialogDataLost = findViewById(R.id.btNo_DialogDataLost);
        Button btYes_DialogDataLost = findViewById(R.id.btYes_DialogDataLost);


        btNo_DialogDataLost.setOnClickListener(v -> {
            MyApplication.SSEB = false;
            Intent replyIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isDelete", false);
            replyIntent.putExtras(bundle);
            setResult(RESULT_OK, replyIntent);
            finish();
        });


        btYes_DialogDataLost.setOnClickListener(v -> {
            MyApplication.SSEB = false;
            Intent replyIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isDelete", true);
            replyIntent.putExtras(bundle);
            setResult(RESULT_OK, replyIntent);
            finish();
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