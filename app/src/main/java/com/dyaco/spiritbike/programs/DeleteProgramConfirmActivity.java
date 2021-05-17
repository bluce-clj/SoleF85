package com.dyaco.spiritbike.programs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.entity.TemplateEntity;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.R;

public class DeleteProgramConfirmActivity extends BaseAppCompatActivity {
    private TemplateEntity templateEntity;
    private int templateParentUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_program_confirm);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        templateEntity = (TemplateEntity) bundle.getSerializable("templateEntity");
        templateParentUid = bundle.getInt("templateParentUid", 0);

        Button btNo_DialogDataLost = findViewById(R.id.btNo_DialogDataLost);
        Button btYes_DialogDataLost = findViewById(R.id.btYes_DialogDataLost);

        btNo_DialogDataLost.setOnClickListener(v -> {
            MyApplication.SSEB = false;
            finish();
        });

        btYes_DialogDataLost.setOnClickListener(v ->
                deleteTemplate(templateParentUid, templateEntity.getTemplateName()));
    }

    private void deleteTemplate(int templateParentUid, String templateName) {
        DatabaseManager.getInstance(MyApplication.getInstance()).
                deleteTemplate(templateParentUid, templateName,
                        new DatabaseCallback<TemplateEntity>() {
                            @Override
                            public void onDeleted() {
                                super.onDeleted();

                                MyApplication.SSEB = false;
                                Intent intent = new Intent(DeleteProgramConfirmActivity.this, DashboardActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("Open_Templates", true);
                                intent.putExtras(bundle);
                                //   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); //不會觸發 NewIntent
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onError(String err) {
                                super.onError(err);
                                Toast.makeText(DeleteProgramConfirmActivity.this, "Failure:" + err, Toast.LENGTH_LONG).show();
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