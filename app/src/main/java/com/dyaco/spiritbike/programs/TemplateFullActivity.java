package com.dyaco.spiritbike.programs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.ProgramsEnum;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.entity.TemplateEntity;
import com.dyaco.spiritbike.support.room.entity.UserProfileAndTemplates;

import java.util.ArrayList;
import java.util.List;

import static com.dyaco.spiritbike.MyApplication.getInstance;

public class TemplateFullActivity extends BaseAppCompatActivity {
    List<TemplateEntity> templateEntityList = new ArrayList<>();
    private ProgramsTemplatesAdapter profileHistoryAdapter;
    private TemplateEntity templateEntity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_full);

        initView();
    }

    private void initView() {

        findViewById(R.id.btClose).setOnClickListener(v -> {
            MyApplication.SSEB = false;
            finish();
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        //  recyclerView.addItemDecoration(new GridSpaceItemDecoration(4,13));
//  recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        profileHistoryAdapter = new ProgramsTemplatesAdapter(this, gridLayoutManager);
        recyclerView.setAdapter(profileHistoryAdapter);

        profileHistoryAdapter.setData2View(templateEntityList);

        profileHistoryAdapter.setOnItemClickListener(templateEntity -> {
            this.templateEntity = templateEntity;
            MyApplication.SSEB = false;
            Intent intent = new Intent(this, DeleteProgramAskActivity.class);
            startActivityForResult(intent, 0);
            // deleteTemplate(templateEntity);
        });

        getData();
    }

    private void getData() {
        DatabaseManager.getInstance(MyApplication.getInstance()).getTemplateFromUserProfile(MyApplication.getInstance().getUserProfile().getUid(), new DatabaseCallback<UserProfileAndTemplates>() {
            @Override
            public void onDataLoadedList(List<UserProfileAndTemplates> userProfileAndTemplatesList) {
                super.onDataLoadedList(userProfileAndTemplatesList);

                for (UserProfileAndTemplates userProfileAndTemplates : userProfileAndTemplatesList) {
                    templateEntityList.addAll(userProfileAndTemplates.templateEntityList);
                }
                profileHistoryAdapter.setData2View(templateEntityList);
            }
        });
    }

    private void deleteTemplate(TemplateEntity templateEntity) {
        DatabaseManager.getInstance(MyApplication.getInstance()).
                deleteTemplate(templateEntity.getTemplateParentUid(), templateEntity.getTemplateName(),
                        new DatabaseCallback<TemplateEntity>() {
                            @Override
                            public void onDeleted() {
                                super.onDeleted();
                                profileHistoryAdapter.deleteMessage(templateEntity.getTemplateName());
                            }

                            @Override
                            public void onError(String err) {
                                super.onError(err);
                                Toast.makeText(getInstance(), "Failure:" + err, Toast.LENGTH_LONG).show();
                            }
                        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                boolean isDelete = bundle.getBoolean("isDelete", false);

                if (isDelete) {
                    deleteTemplate(templateEntity);
                } else {
                    templateEntity = null;
                }
            }
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