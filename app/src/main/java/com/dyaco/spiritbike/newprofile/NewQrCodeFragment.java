package com.dyaco.spiritbike.newprofile;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyaco.spiritbike.BuildConfig;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.GlideApp;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.support.download.FileUtils;
import com.dyaco.spiritbike.support.kProgressHudUtil;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.webapi.BaseApi;
import com.dyaco.spiritbike.webapi.IServiceApi;
import com.dyaco.spiritbike.webapi.SyncDeviceSyncUserBean;
import com.dyaco.spiritbike.workout.WorkoutDashboardActivity;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.tbruyelle.rxpermissions3.RxPermissions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.disposables.Disposable;

import static com.dyaco.spiritbike.MyApplication.QR_CODE_FILE_NAME;
import static com.dyaco.spiritbike.MyApplication.UNIT_E;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.support.CommonUtils.checkInt;
import static com.dyaco.spiritbike.support.CommonUtils.checkStr;
import static com.dyaco.spiritbike.support.CommonUtils.toCeilInt;
import static com.dyaco.spiritbike.support.download.DownloadUtil.PATH_CHALLENGE_VIDEO;

public class NewQrCodeFragment extends BaseFragment {
    private Disposable disposable;
    ImageView ivQrCode_NewQrCode;
    private String identity;
    private RxTimer rxTimer;
    private View mView;
    private Button btIdont_NewQrCode;
    //  private ProgressBar progressBar;
    private KProgressHUD kProgressHUD;

    private TextView tv_skip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_qr_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mView = view;

        kProgressHUD = kProgressHudUtil.getInstance().getProgressHud(mActivity);

        ivQrCode_NewQrCode = view.findViewById(R.id.ivQrCode_NewQrCode);

        btIdont_NewQrCode = view.findViewById(R.id.btIdont_NewQrCode);
        tv_skip = view.findViewById(R.id.tv_skip);
      //  Button btTestSoleAccount = view.findViewById(R.id.btTestSoleAccount);

        btIdont_NewQrCode.setOnClickListener(btNewQrCodeOnClick);
        tv_skip.setOnClickListener(btNewQrCodeOnClick);
      //  btClose_NewQrCode.setOnClickListener(btNewQrCodeOnClick);
      //  btTestSoleAccount.setOnClickListener(btNewQrCodeOnClick);

        identity = getInstance().getIdentity();

        getPermission();
    }

    private void createQrCodeFile() {
        File file = new File(PATH_CHALLENGE_VIDEO + File.separator + QR_CODE_FILE_NAME);
        if (!FileUtils.isFileExists(file)) {
            new FileUtils().persistImage(new CommonUtils().generateQRCode(identity, mActivity), QR_CODE_FILE_NAME);
        }
        initDelay(file);
    }

    private void initDelay(File file) {

        Looper.myQueue().addIdleHandler(() -> {

            GlideApp.with(mActivity)
                    .load(new CommonUtils().createQRCode(identity, 250, 0))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(ivQrCode_NewQrCode);

            rxTimer = new RxTimer();
            rxTimer.interval(2000, number -> apiSyncDeviceSyncUser());

            return false;
        });
    }

    private boolean isCreated = false;
    /**
     * 同步
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

                            try {
                                //if (!isDestroyed())
                                if (isCreated) return;
                                isCreated = true;

                                kProgressHUD.show();
                                rxTimer.cancel();

                                btIdont_NewQrCode.setEnabled(false);
                                //    btClose_NewQrCode.setEnabled(false);

                               checkSyncAccount(data.getSys_response_data().getAccount_no(), data);
//                                updateUserProfile(data.getSys_response_data());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Log.d("WEB_API-檢查使用者同步",  new Gson().toJson(data));
                     //   BaseApi.clearDispose();
                    }

                    @Override
                    public void onFail() {
                        Log.d("WEB_API-檢查使用者同步", "失敗");
                      //  BaseApi.clearDispose();
                    }
                });
    }

    /**
     * 同步使用者資料
     *
     * @param data UserData
     */
    private void updateUserProfile(SyncDeviceSyncUserBean.SysResponseDataBean data) {
        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userProfileEntity.setSoleAccount(data.getAccount());
        userProfileEntity.setSoleAccountNo(data.getAccount_no());
        userProfileEntity.setSoleEmail(data.getEmail());
        userProfileEntity.setSolePassword(data.getPassword());
        userProfileEntity.setSoleSyncPassword(data.getSynce_password());

        if (checkStr(data.getName())) userProfileEntity.setUserName(data.getName());
        if (checkStr(data.getBirthday())) userProfileEntity.setBirthday(data.getBirthday());
        if (checkInt(toCeilInt(data.getWeight())))
            userProfileEntity.setWeight_imperial(CommonUtils.cm2in(toCeilInt(data.getWeight())));
        if (checkInt(toCeilInt(data.getWeight()))) userProfileEntity.setWeight_metric(toCeilInt(data.getWeight()));
        if (checkInt(toCeilInt(data.getHeight())))
            userProfileEntity.setHeight_imperial(CommonUtils.kg2lb(toCeilInt(data.getHeight())));
        if (checkInt(toCeilInt(data.getHeight()))) userProfileEntity.setHeight_metric(toCeilInt(data.getHeight()));
        if (checkStr(data.getSex())) userProfileEntity.setGender("F".equals(data.getSex()) ? 0 : 1);
        if (checkStr(data.getHead_photo_url()))
            userProfileEntity.setSoleHeaderImgUrl(data.getHead_photo_url());
        userProfileEntity.setUserImage(R.drawable.avatar_create_1_inactive);
        userProfileEntity.setUserType(1);
        userProfileEntity.setUnit(0);
        //user sleep mode on  不sleep
        userProfileEntity.setSleepMode(getInstance().getDeviceSettingBean().getDisplay_mode());
        UNIT_E = 0;

        DatabaseManager.getInstance(MyApplication.getInstance()).updateUserProfile(userProfileEntity,
                new DatabaseCallback<UserProfileEntity>() {
                    @Override
                    public void onUpdated() {
                        super.onUpdated();
                        saveData(userProfileEntity);
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);
                        Toast.makeText(mActivity, "Failure:" + err, Toast.LENGTH_LONG).show();
                        btIdont_NewQrCode.setEnabled(true);
                        kProgressHUD.dismiss();
                        isCreated = false;
                    }
                });
    }

    private final View.OnClickListener btNewQrCodeOnClick = view -> {
        Bundle bundle = new Bundle();
        if (rxTimer != null) rxTimer.cancel();

        if (view.getId() == R.id.tv_skip) { //跳過e
            bundle.putString("action", "action_newQrCodeFragment_to_newUserNameFragment");
            Navigation.findNavController(view).navigate(R.id.action_newQrCodeFragment_to_userNameFragment, bundle);
        } else {
            bundle.putString("action", "action_userNameFragment_to_dialogDataLostFragment");
            Navigation.findNavController(view).navigate(R.id.action_newQrCodeFragment_to_dialogDataLostFragment, bundle);
        }
//        switch (view.getId()) {
//            case R.id.btIdont_NewQrCode:
//                bundle.putString("action", "action_newQrCodeFragment_to_newUserNameFragment");
//                Navigation.findNavController(view).navigate(R.id.action_newQrCodeFragment_to_userNameFragment, bundle);
//                break;
//            case R.id.btIdont_NewQrCode:
//                bundle.putString("action", "action_userNameFragment_to_dialogDataLostFragment");
//                Navigation.findNavController(view).navigate(R.id.action_userNameFragment_to_dialogDataLostFragment, bundle);
//                break;
////            case R.id.btTestSoleAccount:
////                bundle.putString("action", "action_newQrCodeFragment_to_soleUserNameFragment");
////                Navigation.findNavController(view).navigate(R.id.action_newQrCodeFragment_to_userNameFragment, bundle);
//        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rxTimer != null) rxTimer.cancel();
        if (disposable != null) disposable.dispose();
        if (kProgressHUD != null && kProgressHUD.isShowing()) kProgressHUD.dismiss();
        BaseApi.apiClear();

        mView = null;
    }

    private void saveData(UserProfileEntity userProfileEntity) {

        DatabaseManager.getInstance(MyApplication.getInstance()).insertUserProfile(userProfileEntity,
                new DatabaseCallback<UserProfileEntity>() {
                    @Override
                    public void onAdded(long rowId) {
                        super.onAdded(rowId);
                        userProfileEntity.setUid((int) rowId);
                        getInstance().setUserProfile(userProfileEntity);
                        try {
                            Navigation.findNavController(mView).navigate(R.id.action_newQrCodeFragment_to_dialogProfileCreatedFragment);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        kProgressHUD.dismiss();
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);
                        kProgressHUD.dismiss();
                        btIdont_NewQrCode.setEnabled(true);
                        isCreated = false;

                    }
                });
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

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.SSEB = true;
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
                            kProgressHUD.dismiss();
                            btIdont_NewQrCode.setEnabled(true);
                            isCreated = false;
                            showErrorDialog();
                        }
                    }
                });
    }

    public void showErrorDialog() {
        Dialog errorDialog2 = new Dialog(requireActivity(), android.R.style.ThemeOverlay);
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
                Navigation.findNavController(mView).navigate(R.id.action_newQrCodeFragment_to_startScreenFragment);
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