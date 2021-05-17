package com.dyaco.spiritbike.engineer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.corestar.libs.device.Device;
import com.dyaco.spiritbike.BuildConfig;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MainActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.product_flavors.ModeEnum;
import com.dyaco.spiritbike.product_flavors.InitProduct;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.RelativeRadioGroup;
import com.dyaco.spiritbike.support.RxTimer;
import com.dyaco.spiritbike.support.room.DatabaseCallback;
import com.dyaco.spiritbike.support.room.DatabaseManager;
import com.dyaco.spiritbike.support.room.UserProfileEntity;
import com.dyaco.spiritbike.webapi.BaseApi;
import com.dyaco.spiritbike.webapi.DeleteSyncLinkBean;
import com.dyaco.spiritbike.webapi.IServiceApi;
import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static com.dyaco.spiritbike.MyApplication.MODE;
import static com.dyaco.spiritbike.MyApplication.getInstance;
import static com.dyaco.spiritbike.product_flavors.ModeEnum.XBR55ENT;
import static com.dyaco.spiritbike.product_flavors.ModeEnum.XBU55ENT;
import static com.dyaco.spiritbike.product_flavors.ModeEnum.XE395ENT;
import static com.dyaco.spiritbike.product_flavors.ModeEnum.getMode;
import static com.dyaco.spiritbike.support.CommonUtils.checkStr;
import static java.lang.String.valueOf;

public class ThreeFragment extends Fragment {
    private EditText et_ble_name;
    private Button btn_save;
    private RelativeRadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;

    private Button btn_clear;
    private SegmentedButtonGroup sc_first_launch;
    private DeviceSettingBean deviceSettingBean;

    public ThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_three, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_clear = view.findViewById(R.id.btn_clear);

        btn_save = view.findViewById(R.id.btn_save);

        et_ble_name = view.findViewById(R.id.et_ble_name);

//        textInputLayout.setErrorEnabled(true);
//        textInputLayout.setCounterEnabled(true);
//        textInputLayout.setCounterMaxLength(10);

        btn_clear.setOnClickListener(v -> showDialogAlert());

        btn_save.setOnClickListener(v -> {
            DeviceSettingBean deviceSettingBean = getInstance().getDeviceSettingBean();
            String bleName = TextUtils.isEmpty(et_ble_name.getText().toString()) ? deviceSettingBean.getBle_device_name() : et_ble_name.getText().toString();

            deviceSettingBean.setBle_device_name(bleName);

            if (MMKV.defaultMMKV().encode("DeviceSettingBean", deviceSettingBean)) {
                Toasty.success(getInstance(), "Saved successfully", Toasty.LENGTH_LONG).show();
            } else {
                Toasty.error(getInstance(), "Save failed", Toasty.LENGTH_LONG).show();
            }

            hideSoftKeyboard(view, requireActivity());
        });

        sc_first_launch = view.findViewById(R.id.sc_first_launch);

        sc_first_launch.setOnPositionChangedListener(position -> {
            switch (position) {
                case 0:
                    deviceSettingBean = getInstance().getDeviceSettingBean();
                    deviceSettingBean.setFirst_launch(true);
                    getInstance().setDeviceSettingBean(deviceSettingBean);
                    break;
                case 1:
                    deviceSettingBean = getInstance().getDeviceSettingBean();
                    deviceSettingBean.setFirst_launch(false);
                    getInstance().setDeviceSettingBean(deviceSettingBean);
            }
        });

        radioGroup = view.findViewById(R.id.radioGroup);
        radioButton1 = view.findViewById(R.id.radioButton1);
        radioButton2 = view.findViewById(R.id.radioButton2);
        radioButton3 = view.findViewById(R.id.radioButton3);

//        radioButton1.setOnClickListener(rbGenderOnClick);
//        radioButton2.setOnClickListener(rbGenderOnClick);
//        radioButton3.setOnClickListener(rbGenderOnClick);
    }

    private final View.OnClickListener rbGenderOnClick = view -> initEvent();

    private void initEvent() {

        deviceSettingBean = getInstance().getDeviceSettingBean();

        if (radioButton1.isChecked()) {
            //  MODE = XBU55ENT;
            radioButton1.setVisibility(View.VISIBLE);
            radioButton1.setPadding(97, 0, 45, 0);
            radioButton1.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_wifi_done), null, null, null);
            radioButton1.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorE4002B));
        } else {
            radioButton1.setVisibility(View.INVISIBLE);
            radioButton1.setPadding(0, 0, 0, 0);
            radioButton1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            radioButton1.setTextColor(ContextCompat.getColor(requireActivity(), R.color.color597084));
        }

        if (radioButton2.isChecked()) {
            //  MODE = XBR55ENT;
            radioButton2.setVisibility(View.VISIBLE);
            radioButton2.setPadding(77, 0, 25, 0);
            radioButton2.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_wifi_done), null, null, null);
            radioButton2.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorE4002B));
        } else {
            radioButton2.setVisibility(View.INVISIBLE);
            radioButton2.setPadding(0, 0, 0, 0);
            radioButton2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            radioButton2.setTextColor(ContextCompat.getColor(requireActivity(), R.color.color597084));
        }

        if (radioButton3.isChecked()) {
            //   MODE = XE395ENT;
            radioButton3.setVisibility(View.VISIBLE);
            radioButton3.setPadding(97, 0, 45, 0);
            radioButton3.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_wifi_done), null, null, null);
            radioButton3.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorE4002B));
        } else {
            radioButton3.setVisibility(View.INVISIBLE);
            radioButton3.setPadding(0, 0, 0, 0);
            radioButton3.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            radioButton3.setTextColor(ContextCompat.getColor(requireActivity(), R.color.color597084));

            //ECB-INC
        }

//        if (MODE.getModeCode() != getInstance().getDeviceSettingBean().getModel_code()) {
//            new InitProduct(requireActivity()).setProductDefault(MODE);
//            Toasty.success(requireActivity(), "切換機型請重新啟動", Toasty.LENGTH_LONG).show();
//            initData();
//        }

    }

    private void initData() {

//        deviceSettingBean = getInstance().getDeviceSettingBean();
//        int modelCode = deviceSettingBean.getModel_code();

        switch (MODE) {
            case XE395ENT:
                radioButton3.setChecked(true);
                radioButton3.setPadding(97, 0, 45, 0);
                radioButton3.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_wifi_done), null, null, null);
                radioButton3.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorE4002B));
                break;
            case XBR55ENT:
                radioButton2.setChecked(true);
                radioButton2.setPadding(77, 0, 25, 0);
                radioButton2.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_wifi_done), null, null, null);
                radioButton2.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorE4002B));
                break;
            case XBU55ENT:
                radioButton1.setChecked(true);
                radioButton1.setPadding(97, 0, 45, 0);
                radioButton1.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireActivity(), R.drawable.icon_wifi_done), null, null, null);
                radioButton1.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorE4002B));
                break;
        }

//        radioButton1.setChecked(modelCode == 2);
//        radioButton2.setChecked(modelCode == 1);
//        radioButton3.setChecked(modelCode == 0);

        initEvent();

        sc_first_launch.setPosition(deviceSettingBean.isFirst_launch() ? 0 : 1, false);

        String bleName2 = TextUtils.isEmpty(getInstance().getDeviceSettingBean().getBle_device_name()) ? "_FTMS" : getInstance().getDeviceSettingBean().getBle_device_name();
        et_ble_name.setText(bleName2);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();

        Log.d("FFFFFFF", "onResume:3 ");
    }

    Dialog dialog;

    public void showDialogAlert() {

        if (dialog == null || !dialog.isShowing()) {
            dialog = new Dialog(requireActivity(), android.R.style.ThemeOverlay);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            dialog.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            dialog.setContentView(R.layout.activity_delete_program_confirm);
            TextView textView = dialog.findViewById(R.id.tv_version);
            Button button = dialog.findViewById(R.id.btYes_DialogDataLost);
            Button buttonNo = dialog.findViewById(R.id.btNo_DialogDataLost);
            dialog.show();
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            button.setOnClickListener(v -> {
                dialog.dismiss();

                MyApplication.SSEB = false;
                ((EngineerActivity) requireActivity()).loadingDialog.startDialog();
                Toasty.warning(getInstance(), "RESTORE FACTORY.....", Toasty.LENGTH_LONG).show();
                getSyncUser();

                //  MMKV.defaultMMKV().remove("DeviceSettingBean");

            });

            buttonNo.setOnClickListener(v -> dialog.dismiss());

            textView.setText("RESTORE FACTORY");
        }
    }


    public static void hideSoftKeyboard(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 取得裝置內的文字檔 設定檔
     */
    private void initSetting() {
        String settingFile = new CommonUtils().getSettingFile(requireActivity());
        if (null != settingFile) {
            new InitProduct(requireActivity()).setProductDefault(settingFile);
        } else {
            new InitProduct(requireActivity()).setProductDefault(MODE);
        }
    }

    private int times;
    private int syncSize;

    private void getSyncUser() {
        DatabaseManager.getInstance(MyApplication.getInstance())
                .getSyncUserProfiles(new DatabaseCallback<UserProfileEntity>() {
                    @Override
                    public void onDataLoadedList(List<UserProfileEntity> userProfileEntityList) {
                        super.onDataLoadedList(userProfileEntityList);

                        syncSize = userProfileEntityList.size();

                        if (syncSize == 0) {
                            cccc();
                        }

                        Log.d("XXXXXX", "@@@@onDataLoadedList: " + syncSize);

                        for (UserProfileEntity userProfileEntity : userProfileEntityList) {
                            apiDeleteSyncLink(userProfileEntity);
                        }

                    }

                    @Override
                    public void onError(String err) {
                        super.onError(err);
                        cccc();
                    }
                });
    }

    /**
     * 刪除Sole帳號連結
     */
    private void apiDeleteSyncLink(UserProfileEntity userProfileEntity) {

        Map<String, Object> param = new HashMap<>();
        param.put("api_token", BuildConfig.API_TOKEN);
        param.put("account_no", userProfileEntity.getSoleAccountNo());
        param.put("synce_password", userProfileEntity.getSoleSyncPassword());
        param.put("device_id", getInstance().getIdentity());

        BaseApi.request(BaseApi.createApi(IServiceApi.class).apiDeleteSyncLink(param),
                new BaseApi.IResponseListener<DeleteSyncLinkBean>() {
                    @Override
                    public void onSuccess(DeleteSyncLinkBean data) {
                        Log.d("XXXXXX", "Response: " + new Gson().toJson(data));
                        ook();
                    }

                    @Override
                    public void onFail() {
                        Log.d("XXXXXX", "失敗");
                        ook();
                    }
                });
    }

    private void ook() {
        times++;
        if (times >= syncSize) {
            cccc();
        }
    }

    private void cccc() {
        Log.d("XXXXXX", "CLEAR: ");

        new Thread(() ->
                DatabaseManager.getInstance(getInstance()).clearTable()).start();

        initSetting();

        try {
            getInstance().mFTMSManager.removeBondedDevices();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toasty.warning(getInstance(), "重新啟動中.....", Toasty.LENGTH_LONG).show();
        new RxTimer().timer(5000, number -> {
            try {
                ((EngineerActivity) requireActivity()).loadingDialog.stopDialog();

                Intent intent = new Intent(requireContext(), MainActivity.class);
                startActivity(intent);
                requireActivity().finishAffinity();
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
            } catch (Exception e) {
                e.printStackTrace();
            }

//            requireActivity().runOnUiThread(() -> {
//
//                ((EngineerActivity) requireActivity()).btBack.setEnabled(false);
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
//                        .setTitle("")
//                        .setMessage("請重新開機")
//                        .setPositiveButton("OK", null);
//
//                AlertDialog alertDialog = builder.show();
//                TextView textView = alertDialog.findViewById(android.R.id.message);
//                if (textView != null) {
//                    textView.setTextSize(48.0f);
//                    textView.setTextColor(Color.rgb(255, 0, 0));
//                }
//
////                requireActivity().finishAffinity();
////                Intent intent = requireActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage(requireActivity().getBaseContext().getPackageName());
////                intent.putExtra("REBOOT","reboot");
////                PendingIntent restartIntent = PendingIntent.getActivity(requireActivity().getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
////                AlarmManager mgr = (AlarmManager)requireActivity().getSystemService(Context.ALARM_SERVICE);
////                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
//                //android.os.Process.killProcess(android.os.Process.myPid());
//            });
        });
    }
}
