package com.dyaco.spiritbike.profile;

import android.Manifest;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyaco.spiritbike.BuildConfig;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.BtnExitFullScreen;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.GlideApp;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.support.download.FileUtils;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.webapi.BaseApi;
import com.dyaco.spiritbike.webapi.BaseCallWebApi;
import com.dyaco.spiritbike.webapi.IServiceApi;
import com.dyaco.spiritbike.webapi.SyncDeviceSyncUserBean;
import com.google.gson.Gson;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.disposables.Disposable;

import static com.dyaco.spiritbike.MyApplication.QR_CODE_FILE_NAME;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.CommonUtils.checkInt;
import static com.dyaco.spiritbike.support.CommonUtils.checkStr;
import static com.dyaco.spiritbike.support.CommonUtils.toCeilInt;
import static com.dyaco.spiritbike.support.download.DownloadUtil.PATH_CHALLENGE_VIDEO;

public class LinkSoleAppActivity extends BaseAppCompatActivity {
    private String identity;
    private RxTimer rxTimer;
    private UserProfileEntity userProfileEntity;
    private Disposable disposable;
    private boolean isCreated = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_soleapp);

        identity = getInstance().getIdentity();

        userProfileEntity = getInstance().getUserProfile();

        getPermission();
    }

    private void createQrCodeFile() {
        File file = new File(PATH_CHALLENGE_VIDEO + File.separator + QR_CODE_FILE_NAME);
        if (!FileUtils.isFileExists(file)) {
            new FileUtils().persistImage(new CommonUtils().generateQRCode(identity, this), QR_CODE_FILE_NAME);
        }
        initDelay(file);
    }

    private void getPermission() {
        disposable = new RxPermissions(this)
                .requestEachCombined(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .subscribe(permission -> {
                    if (permission.granted) {
                        createQrCodeFile();
                    }
                });
    }

    private void initDelay(File file) {
        Looper.myQueue().addIdleHandler(() -> {
            ImageView ivQRCode = findViewById(R.id.img_QRCode);

//            GlideApp.with(LinkSoleAppActivity.this)
//                    .load(file)
//                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                    .into(ivQRCode);

            GlideApp.with(LinkSoleAppActivity.this)
                  //  .load(CommonUtils.generateQRCode(identity, LinkSoleAppActivity.this))
                    .load(new CommonUtils().createQRCode(identity, 250, 0))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(ivQRCode);

            rxTimer = new RxTimer();
            rxTimer.interval(2000, number -> apiSyncDeviceSyncUser());

            return false;
        });
    }

    /**
     * 同步確認
     */
    private void apiSyncDeviceSyncUser() {

        Map<String, Object> param = new HashMap<>();
        param.put("api_token", BuildConfig.API_TOKEN);
        param.put("device_id", identity);

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiSyncDeviceSyncUser(param),
                new BaseApi.IResponseListener<SyncDeviceSyncUserBean>() {
                    @Override
                    public void onSuccess(SyncDeviceSyncUserBean data) {
                        if (data.getSys_response_message().getCode().equals("1")) {
                            rxTimer.cancel();

                            if (isCreated) return;
                            isCreated = true;


                            checkSyncAccount(data.getSys_response_data().getAccount_no(), data);

                         //   updateUserProfile(data.getSys_response_data());

                        }

                        Log.d("WEB_API-同步確認", "Response: " + new Gson().toJson(data));
                      //  BaseApi.clearDispose();
                    }

                    @Override
                    public void onFail() {
                        Log.d("WEB_API-同步確認", "失敗");
                      //  BaseApi.clearDispose();
                    }
                });
    }

    /**
     * 同步使用者資料
     * @param data UserData
     */
    private void updateUserProfile(SyncDeviceSyncUserBean.SysResponseDataBean data) {

        userProfileEntity.setSoleAccount(data.getAccount());
        userProfileEntity.setSoleAccountNo(data.getAccount_no());
        userProfileEntity.setSoleEmail(data.getEmail());
        userProfileEntity.setSolePassword(data.getPassword());
        userProfileEntity.setSoleSyncPassword(data.getSynce_password());

        if (checkStr(data.getName())) userProfileEntity.setUserName(data.getName());
        if (checkStr(data.getBirthday())) userProfileEntity.setBirthday(data.getBirthday());

        if (checkInt(toCeilInt(data.getWeight()))) userProfileEntity.setWeight_imperial(CommonUtils.kg2lb(toCeilInt(data.getWeight())));
        if (checkInt(toCeilInt(data.getWeight()))) userProfileEntity.setWeight_metric(toCeilInt(data.getWeight()));
        if (checkInt(toCeilInt(data.getHeight()))) userProfileEntity.setHeight_imperial(CommonUtils.cm2in(toCeilInt(data.getHeight())));
        if (checkInt(toCeilInt(data.getHeight()))) userProfileEntity.setHeight_metric(toCeilInt(data.getHeight()));

        if (checkStr(data.getSex())) userProfileEntity.setGender("F".equals(data.getSex()) ? 0 : 1);
        if (checkStr(data.getHead_photo_url()))
            userProfileEntity.setSoleHeaderImgUrl(data.getHead_photo_url());

        DatabaseManager.getInstance(MyApplication.getInstance()).updateUserProfile(userProfileEntity,
                new DatabaseCallback<UserProfileEntity>() {
                    @Override
                    public void onUpdated() {
                        super.onUpdated();

                        //將尚未上傳的資料傳送至雲端
                        new BaseCallWebApi().getNotUploadHistoryList(userProfileEntity);

                        done();
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);
                        Toast.makeText(getInstance(), "Failure:" + err, Toast.LENGTH_LONG).show();
                    }
                });
    }


    public void onClose(View view) {
        done();
    }

    private void done() {
        if (rxTimer != null) rxTimer.cancel();

        MyApplication.SSEB = false;
        finish();
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
        if (disposable != null) disposable.dispose();
        if (rxTimer != null) rxTimer.cancel();
        BaseApi.apiClear();
        btnExitFullScreen.removeFloatView();
        btnExitFullScreen = null;
    }


    private void checkSyncAccount(String accountNo, SyncDeviceSyncUserBean syncDeviceSyncUserBean) {
        DatabaseManager.getInstance(MyApplication.getInstance()).checkSyncAccount(accountNo,
                new DatabaseCallback<Integer>() {
                    @Override
                    public void onCount(Integer i) {
                        super.onCount(i);

                        if (i <= 0) {
                            updateUserProfile(syncDeviceSyncUserBean.getSys_response_data());
                        } else {
                            Log.d("WEB_API-檢查使用者同步", "已重複: ");
                            isCreated = false;
                            showErrorDialog();
                        }
                    }
                });
    }

    public void showErrorDialog() {
        Dialog errorDialog2 = new Dialog(this, android.R.style.ThemeOverlay);
        errorDialog2.setCanceledOnTouchOutside(false);
        errorDialog2.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        errorDialog2.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        errorDialog2.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        errorDialog2.setContentView(R.layout.account_sync_error);
        Button btn_close = errorDialog2.findViewById(R.id.btn_support);
        Button btn_tryAgain = errorDialog2.findViewById(R.id.btn_try_again);
        errorDialog2.show();
        errorDialog2.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        btn_close.setOnClickListener(v -> {
            errorDialog2.dismiss();
            try {
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btn_tryAgain.setOnClickListener(v -> {
            errorDialog2.dismiss();
            rxTimer = new RxTimer();
            rxTimer.interval(2000, number -> apiSyncDeviceSyncUser());
        });
    }
}