package com.dyaco.spiritbike;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.corestar.libs.device.Device;
import com.dyaco.spiritbike.engineer.DeviceSettingBean;
import com.dyaco.spiritbike.settings.UpdateBean;
import com.dyaco.spiritbike.settings.UpdateSoftwareActivity;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.FloatingWidget;
import com.dyaco.spiritbike.support.GridViewSpaceItemDecoration;
import com.dyaco.spiritbike.support.LogS;
import com.dyaco.spiritbike.support.MsgEvent;
import com.dyaco.spiritbike.support.RxBus;
import com.dyaco.spiritbike.support.banner.util.BannerUtils;
import com.dyaco.spiritbike.support.kProgressHudUtil;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.uart.isBusEvent;
import com.dyaco.spiritbike.webapi.BaseApi;
import com.dyaco.spiritbike.webapi.BaseCallWebApi;
import com.dyaco.spiritbike.webapi.IServiceApi;
import com.dyaco.spiritbike.webapi.SyncGetUserInfoBean;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.dyaco.spiritbike.MyApplication.COMMAND_KEY;

import static com.dyaco.spiritbike.MyApplication.IS_CHILD_LOCKING;
import static com.dyaco.spiritbike.MyApplication.TIME_EVENT;
import static com.dyaco.spiritbike.MyApplication.WIFI_EVENT;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.MyApplication.isBootUp;
import static com.dyaco.spiritbike.MyApplication.isLocked;
import static com.dyaco.spiritbike.MyApplication.updateNotify;
import static com.dyaco.spiritbike.support.CommonUtils.checkInt;
import static com.dyaco.spiritbike.support.CommonUtils.checkStr;
import static com.dyaco.spiritbike.support.CommonUtils.setTime;
import static com.dyaco.spiritbike.support.CommonUtils.toCeilInt;

public class StartScreenFragment extends BaseFragment {
    StartScreenAdapter startScreenAdapter;
    private ImageView ivStartScreenLogo;
    private RecyclerView recyclerView;

    private TextView tvTime_StartScreen;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private TextView tvVersionName;
    private ImageView ivWifi;
    private KProgressHUD kProgressHUD;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //  getInstance().commandSetDeviceReset();

        isBootUp = true;

        //   //手機的寬度(像素)
//        int mWidth = getResources().getDisplayMetrics().widthPixels;
////手機的高度(像素)
//        int mHeight = getResources().getDisplayMetrics().heightPixels;
////手機的密度
//        float mDensity = getResources().getDisplayMetrics().density;
////手機的DPI
//        float mDpi = getResources().getDisplayMetrics().densityDpi;
//
//        Log.i("SSSSSSS", "onViewCreated: " + mWidth +","+ mHeight +","+ mDensity +","+ mDpi);


//        Button crashButton = new Button(getActivity());
//        crashButton.setText("Crash!");
//        crashButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                throw new RuntimeException("Test Crash"); // Force a crash
//            }
//        });
//
//        getActivity().addContentView(crashButton, new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));


        kProgressHUD = kProgressHudUtil.getInstance().getProgressHud(mActivity);

        initView(view);

        initData();

        Disposable disposable2 = RxBus.getInstance().toObservable(MsgEvent.class).subscribe(msg -> {

            if (msg.getType() == TIME_EVENT) {
                mActivity.runOnUiThread(() -> tvTime_StartScreen.setText(msg.getObj().toString()));
            }

            if (msg.getType() == WIFI_EVENT) {
                int img = CommonUtils.setWifiImage((int) msg.getObj(), true);
                mActivity.runOnUiThread(() -> {
                    if (ivWifi != null)
                        ivWifi.setImageResource(img);
                });
            }
        });
        compositeDisposable.add(disposable2);

        DeviceSettingBean deviceSettingBean = getInstance().getDeviceSettingBean();
        deviceSettingBean.setFirst_launch(false);
        getInstance().setDeviceSettingBean(deviceSettingBean);

        //   MMKV.defaultMMKV().remove("DeviceSettingBean");

        ivWifi.setImageResource(CommonUtils.setWifiImage(new CommonUtils().getWifiLevel(mActivity), true));

        EventBus.getDefault().register(this);

        showDialogAlert();
    }

    private void initData() {
        DatabaseManager.getInstance(MyApplication.getInstance()).getUserProfiles(new DatabaseCallback<UserProfileEntity>() {
            @Override
            public void onDataLoadedList(List<UserProfileEntity> userProfileEntityList) {
                super.onDataLoadedList(userProfileEntityList);

                //if (!isDestroyed())

                startScreenAdapter.setData2View(userProfileEntityList);
                setGuestView(recyclerView);

                if (userProfileEntityList != null && userProfileEntityList.size() < 9) {
                    setAddUserView(recyclerView);
                }

                if (userProfileEntityList != null && userProfileEntityList.size() < 8) {
                    setPlaceHolderView(recyclerView);
                }
            }

            @Override
            public void onError(String err) {
                super.onError(err);
            }
        });

        DatabaseManager.getInstance(MyApplication.getInstance()).checkGuest(new DatabaseCallback<Integer>() {
            @Override
            public void onCount(Integer i) {
                super.onCount(i);
                //本機Guest帳號已建立
                guestUid = i.longValue();
            }

            @Override
            public void onNoData() {
                super.onNoData();
                //本機Guest帳號尚未建立
                insertGuest();
            }
        });
    }

    private long guestUid = -1;

    private void insertGuest() {

        int year = Calendar.getInstance().get(Calendar.YEAR) - 30;

        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userProfileEntity.setUserType(0);
        userProfileEntity.setBirthday(year + "-01-01");
        // userProfileEntity.setBirthday("19001111");
        userProfileEntity.setUserName("GUEST");
        userProfileEntity.setGender(1);
        userProfileEntity.setUserImage(R.drawable.avatar_start_guest);
        userProfileEntity.setHeight_metric(178);
        userProfileEntity.setWeight_metric(70);
        userProfileEntity.setHeight_imperial(10);
        userProfileEntity.setWeight_imperial(10);
        userProfileEntity.setUserType(0);
        userProfileEntity.setUnit(getInstance().getDeviceSettingBean().getUnit_code());

        //user sleep mode on  不sleep
        userProfileEntity.setSleepMode(getInstance().getDeviceSettingBean().getDisplay_mode());

        DatabaseManager.getInstance(MyApplication.getInstance()).insertUserProfile(userProfileEntity,
                new DatabaseCallback<UserProfileEntity>() {

                    @Override
                    public void onAdded(long rowId) {
                        super.onAdded(rowId);
                        Log.i("CCCCC", "rowId: " + rowId);
                        guestUid = rowId;
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);
                        Log.i("CCCCC", "onError: " + err);
                    }
                });
    }

    private void initView(View view) {

        ivWifi = view.findViewById(R.id.ivWifiIcon_StartScreen);
        ivWifi.setOnClickListener(v -> new FloatingWidget(requireActivity()).callSetting(0, MainActivity.class));

        tvVersionName = view.findViewById(R.id.tvVersionName);
   //     tvVersionName.setText(new CommonUtils().getLocalVersionCode());
   //     tvVersionName.setText(new CommonUtils().getLocalVersionName(requireActivity().getApplicationContext()));


        tvTime_StartScreen = view.findViewById(R.id.tvTime_StartScreen);

        ivStartScreenLogo = view.findViewById(R.id.ivStartScreenLogo);
        ivStartScreenLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwitchMonitor();
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 5) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridViewSpaceItemDecoration((int) BannerUtils.dp2px(56)));
        startScreenAdapter = new StartScreenAdapter(mActivity, gridLayoutManager);
        recyclerView.setAdapter(startScreenAdapter);

        startScreenAdapter.setOnItemClickListener((bean, type) -> {

            MyApplication.SSEB = false;

            if (CommonUtils.isFastClick()) {
                return;
            }

            ((MainActivity) requireActivity()).isNext = true;

            Intent intent = new Intent(mActivity, DashboardActivity.class);
            Bundle bundle = new Bundle();
            if (type == 0) {

                DatabaseManager.getInstance(MyApplication.getInstance()).getUserProfilesGuest(new DatabaseCallback<UserProfileEntity>() {
                    @Override
                    public void onDataLoadedList(List<UserProfileEntity> userProfileEntityList) {
                        super.onDataLoadedList(userProfileEntityList);

                        if (userProfileEntityList != null && userProfileEntityList.size() > 0) {
                            //GUEST
                            getInstance().setUserProfile(userProfileEntityList.get(0));
                        } else {
                            getInstance().setUserProfile(bean);
                        }

                        bundle.putLong("GuestUid", guestUid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        mActivity.finish();
                        mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);
                    }
                });


//                //GUEST
//                getInstance().setUserProfile(bean);
//                bundle.putLong("GuestUid", guestUid);
//                intent.putExtras(bundle);
//                startActivity(intent);
//                mActivity.finish();
//                mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else if (type == 1) {
                //Normal User

                getInstance().setUserProfile(bean);

                //已連結Sole帳號，同步使用者資料
                if (checkStr(bean.getSoleAccountNo())) {
                    apiSyncGetUserInfo(bean);
                } else {
                    goToDashboard();
                }

                //  goToDashboard();

            } else {

//                NavOptions navOptions = new NavOptions.Builder()
//                        .setEnterAnim(R.anim.slide_bottom_to_top) //进入动画
//                        .setExitAnim(R.anim.slide_top_to_bottom)    //退出动画
////                        .setPopEnterAnim(R.anim.slide_bottom_to_top)    //弹出进入动画
////                        .setPopExitAnim(R.anim.slide_top_to_bottom)  //弹出退出动画
//                        .build();

                // New User

                try {
                    Navigation.findNavController(view).navigate(R.id.action_startScreenFragment_to_newQrCodeFragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //guest view
    private void setGuestView(RecyclerView view) {
        try {
            View header = LayoutInflater.from(requireActivity()).inflate(R.layout.items_start_screen_profile_list, view, false);
            startScreenAdapter.setGuestView(header);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // add user view
    private void setAddUserView(RecyclerView view) {
        View footer = LayoutInflater.from(mActivity).inflate(R.layout.items_start_screen_profile_list, view, false);
        startScreenAdapter.setAddUserView(footer);
    }

    //    // add placeholder view
    private void setPlaceHolderView(RecyclerView view) {
        View footer = LayoutInflater.from(mActivity).inflate(R.layout.items_start_screen_profile_list, view, false);
        startScreenAdapter.setPlaceHolderView(footer);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tvTime_StartScreen != null)
            tvTime_StartScreen.setText(setTime());

        //   removeFloatingView();
        try {
            requireActivity().sendBroadcast(new Intent("com.roco.hide.bar"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (kProgressHUD != null) kProgressHUD.dismiss();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
        EventBus.getDefault().unregister(this);
        if (lockedDialog != null) {
            lockedDialog.dismiss();
            lockedDialog = null;
            IS_CHILD_LOCKING = false;
        }

        try {
            requireActivity().sendBroadcast(new Intent("com.roco.hide.bar"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        startScreenAdapter = null;
        ivStartScreenLogo = null;
        recyclerView = null;
        tvTime_StartScreen = null;
        tvVersionName = null ;
        ivWifi = null;
        kProgressHUD = null;
    }

    /**
     * 取得雲端使用者資料
     *
     * @param userProfileEntity 本機使用者資料
     */
    private void apiSyncGetUserInfo(UserProfileEntity userProfileEntity) {

        kProgressHUD.show();

        Map<String, Object> param = new HashMap<>();
        param.put("api_token", BuildConfig.API_TOKEN);
        param.put("account_no", userProfileEntity.getSoleAccountNo());
        param.put("synce_password", userProfileEntity.getSoleSyncPassword());
        param.put("device_id", getInstance().getIdentity());
      //  Log.d("WEB_API-取得雲端使用者資料", "param: "+ param.toString());
        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiSyncGetUserInfo(param),
                new BaseApi.IResponseListener<SyncGetUserInfoBean>() {
                    @Override
                    public void onSuccess(SyncGetUserInfoBean data) {
                     //   BaseApi.apiClear();
                        if (data.getSys_response_message().getCode().equals("1")) {
                            //  updateData(userProfileEntity, data.getSys_response_data(), true);
                            //更新雲端使用者資料到本機
                            updateData(userProfileEntity, data.getSys_response_data(), false);
                        } else {
//                            -3：清除本機該帳號所有資料；
//                            -5：本機與該帳號關聯斷開，即所有資料不再上傳 / 同步雲端；但本機該帳號資料不刪除； Device and user disconnected
                            if ("-5".equals(data.getSys_response_message().getCode())) {
                                updateData(userProfileEntity, data.getSys_response_data(), true);
                            } else if ("-3".equals(data.getSys_response_message().getCode())) {
                                //刪除帳號
                                deleteACC(userProfileEntity);
                            } else {
                                goToDashboard();
                            }

                            Toasty.warning(getInstance(), data.getSys_response_message().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                      //  Log.d("WEB_API-取得雲端使用者資料", "Response: " + new Gson().toJson(data));
                        LogS.printJson("WEB_API-取得雲端使用者資料",new Gson().toJson(data),"");
                    }

                    @Override
                    public void onFail() {
                    //    BaseApi.apiClear();
                        Log.d("WEB_API-取得雲端使用者資料", "失敗");
                        Toasty.error(getInstance(), "SyncUserInfo Failed", Toasty.LENGTH_LONG).show();
                        goToDashboard();
                    }
                });
    }

    /**
     * 更新雲端使用者資料到本機
     *
     * @param userProfileEntity 本機使用者資料
     * @param data              雲端使用者的資料
     * @param isUnbind          是否綁定Sole帳號
     */
    private void updateData(UserProfileEntity userProfileEntity, SyncGetUserInfoBean.SysResponseDataBean data, boolean isUnbind) {

        if (isUnbind) {//清除同步資料
            userProfileEntity.setSoleAccount("");
            userProfileEntity.setSoleAccountNo(null);
            userProfileEntity.setSoleEmail("");
            userProfileEntity.setSolePassword("");
            userProfileEntity.setSoleSyncPassword("");
            userProfileEntity.setSoleHeaderImgUrl("");
        } else {
            if (checkStr(data.getEmail())) userProfileEntity.setSoleEmail(data.getEmail());
            if (checkStr(data.getName())) userProfileEntity.setUserName(data.getName());
            if (checkStr(data.getBirthday())) userProfileEntity.setBirthday(data.getBirthday());

            if (checkInt(toCeilInt(data.getWeight())))
                userProfileEntity.setWeight_imperial(CommonUtils.kg2lb(toCeilInt(data.getWeight())));
            if (checkInt(toCeilInt(data.getWeight()))) userProfileEntity.setWeight_metric(toCeilInt(data.getWeight()));
            if (checkInt(toCeilInt(data.getHeight())))
                userProfileEntity.setHeight_imperial(CommonUtils.cm2in(toCeilInt(data.getHeight())));
            if (checkInt(toCeilInt(data.getHeight()))) userProfileEntity.setHeight_metric(toCeilInt(data.getHeight()));

            if (checkStr(data.getSex()))
                userProfileEntity.setGender("F".equals(data.getSex()) ? 0 : 1);

            //  if (checkStr(data.getHead_photo_url()) && (!data.getHead_photo_url().equals(userProfileEntity.getSoleHeaderImgUrl()))) {
            if (checkStr(data.getHead_photo_url())) {
                userProfileEntity.setSoleHeaderImgUrl(data.getHead_photo_url());
            }
        }

        DatabaseManager.getInstance(getInstance().getApplicationContext()).updateUserProfile(userProfileEntity,
                new DatabaseCallback<UserProfileEntity>() {
                    @Override
                    public void onUpdated() {
                        super.onUpdated();

                        goToDashboard();
                        if (isUnbind) return;
                        //將尚未上傳的資料傳送至雲端
                        new BaseCallWebApi().getNotUploadHistoryList(userProfileEntity);
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);
                        goToDashboard();
                    }
                });
    }

    private void goToDashboard() {
        try {
            ((MainActivity) requireActivity()).isNext = true;

            if (kProgressHUD != null) kProgressHUD.dismiss();
            Intent intent = new Intent(mActivity, DashboardActivity.class);
            Bundle bundle = new Bundle();
            intent.putExtras(bundle);
            startActivity(intent);
            mActivity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommandEvent(isBusEvent isBusEvent) {
        if (isBusEvent.getEventType() == COMMAND_KEY) { //KEYBOARD
            KeyBean keyBean = ((KeyBean) isBusEvent);
            if (keyBean.getKeyStatus() != 0) {
                //   MMKV.defaultMMKV().encode(IS_LOCK, false);
                if (lockedDialog != null) {
                    lockedDialog.dismiss();
                    lockedDialog = null;
                    IS_CHILD_LOCKING = false;
                    getInstance().commandSetBuzzer(Device.BEEP.SHORT, 1);
                }
            }
        }
    }

    Dialog lockedDialog;

    public void showDialogAlert() {
        //  Log.d("休眠", "showLockedDialogAlert: " + isLocked);
        if (getInstance().getDeviceSettingBean().getChild_lock() == 0 || IS_CHILD_LOCKING) {
            return;
        }

        //第二次進來不鎖
        if (!isLocked) return;

        isLocked = false;

        IS_CHILD_LOCKING = true;

        if (lockedDialog == null || !lockedDialog.isShowing()) {
            lockedDialog = new Dialog(requireActivity(), android.R.style.ThemeOverlay);
            lockedDialog.setCanceledOnTouchOutside(false);
            lockedDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            lockedDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            lockedDialog.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            lockedDialog.setContentView(R.layout.dialog_console_locked);
            lockedDialog.show();
        }
    }


    private void onSwitchMonitor() {

        if (mHits == null) {
            mHits = new long[COUNTS];
        }
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);//把從第二位至最後一位之間的數字複製到第一位至倒數第一位
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();//記錄一個時間

        if (SystemClock.uptimeMillis() - mHits[0] <= DURATION) {//一秒內連續點擊。
            //進來以後需要還原狀態，否則如果點擊過快，第六次，第七次 都會不斷進來觸發該效果。重新開始計數即可
            mHits = null;
            start_redstone_app();
        }
    }

    //    private WindowManager windowManager;
//    private Button button;
    private long[] mHits = null;
    private final int COUNTS = 10;//點擊次數
    private final long DURATION = TimeUnit.SECONDS.toMillis(3);//規定有效時間

    public void start_redstone_app() {
        MyApplication.SSEB = false;
        requireActivity().sendBroadcast(new Intent("com.roco.show.bar"));
//        windowManager = (WindowManager) requireActivity().getSystemService(WINDOW_SERVICE);
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        //   layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
//        layoutParams.format = PixelFormat.RGBA_8888;
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        layoutParams.width = 184;
//        layoutParams.height = 80;
//        layoutParams.x = 500;
//        layoutParams.y = 700;
//
//        Typeface typeface = ResourcesCompat.getFont(requireActivity(), R.font.hanzel_bold);
//        button = new Button(requireActivity());
//        button.setText("BACK");
//        button.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorFFFFFF));
//        button.setTextSize(26);
//        button.setTypeface(typeface);
//        button.setGravity(Gravity.CENTER);
//        button.setBackgroundResource(R.drawable.btn_rrect_9d2227_184);
//        // button.setOnTouchListener(new SettingsFragment.FloatingOnTouchListener());
//        windowManager.addView(button, layoutParams);
//
//        button.setOnClickListener(v -> {
//            Intent intent = new Intent();
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setClass(requireActivity(), MainActivity.class);
//            startActivity(intent);
//            requireActivity().overridePendingTransition(0, 0);
//            windowManager.removeViewImmediate(button);
//
//            requireActivity().sendBroadcast(new Intent("com.roco.hide.bar"));
//        });

        try {
            ComponentName mComponentName = new ComponentName("com.mediatek.factorymode", "com.mediatek.factorymode.MainActivity");
            Intent mIntent = new Intent();
            mIntent.setComponent(mComponentName);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            requireActivity().startActivity(mIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deleteACC(UserProfileEntity userProfileEntity) {
        DatabaseManager.getInstance(MyApplication.getInstance()).deleteUserProfile(userProfileEntity.getUid(),
                new DatabaseCallback<UserProfileEntity>() {
                    @Override
                    public void onDeleted() {
                        super.onDeleted();

                        MMKV.defaultMMKV().remove("UserProfileEntity");

                        kProgressHUD.dismiss();
                        Intent intent = new Intent(mActivity, MainActivity.class);
                        startActivity(intent);

                        Toasty.warning(getInstance(), "User data has been deleted", Toasty.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);
                    }
                });
    }
}