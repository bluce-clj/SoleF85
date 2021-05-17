package com.dyaco.spiritbike.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.dyaco.spiritbike.BuildConfig;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.MainActivity;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.webapi.BaseApi;
import com.dyaco.spiritbike.webapi.DeleteSyncLinkBean;
import com.dyaco.spiritbike.webapi.IServiceApi;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.CommonUtils.checkStr;

public class DeleteProfileActivity extends BaseAppCompatActivity {
    UserProfileEntity userProfileEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);
        userProfileEntity = getInstance().getUserProfile();

        Button btNo_DialogDataLost = findViewById(R.id.btNo_DialogDataLost);
        Button btYes_DialogDataLost = findViewById(R.id.btYes_DialogDataLost);

        btNo_DialogDataLost.setOnClickListener(v -> {
            MyApplication.SSEB = false;
            finish();
        });

        btYes_DialogDataLost.setOnClickListener(v ->
                DatabaseManager.getInstance(MyApplication.getInstance()).deleteUserProfile(userProfileEntity.getUid(),
                        new DatabaseCallback<UserProfileEntity>() {
                            @Override
                            public void onDeleted() {
                                super.onDeleted();
                                if (checkStr(userProfileEntity.getSoleAccountNo())) {
                                    apiDeleteSyncLink();
                                }
                                backD();

                            }

                            @Override
                            public void onError(String err) {
                                super.onError(err);
                                Toast.makeText(getInstance(), "Failure:" + err, Toast.LENGTH_LONG).show();
                            }
                        })
        );

    }


    /**
     * 刪除Sole帳號連結
     */
    private void apiDeleteSyncLink() {

        Map<String, Object> param = new HashMap<>();
        param.put("api_token", BuildConfig.API_TOKEN);
        param.put("account_no", userProfileEntity.getSoleAccountNo());
        param.put("synce_password", userProfileEntity.getSoleSyncPassword());
        param.put("device_id", getInstance().getIdentity());

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiDeleteSyncLink(param),
                new BaseApi.IResponseListener<DeleteSyncLinkBean>() {
                    @Override
                    public void onSuccess(DeleteSyncLinkBean data) {
                        Log.d("WEB_API-刪除Sole帳號連結", "Response: " + new Gson().toJson(data));
                     //   BaseApi.clearDispose();
                    }

                    @Override
                    public void onFail() {
                        Log.d("WEB_API-刪除Sole帳號連結", "失敗");
                    //    BaseApi.clearDispose();
                    }
                });
    }

    private void backD() {

        MyApplication.SSEB = false;
        Intent intent = new Intent(DeleteProfileActivity.this, MainActivity.class);
        intent.putExtra("fragment_id", R.id.startScreenFragment);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finishAffinity();
        overridePendingTransition(0, android.R.anim.slide_out_right);
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