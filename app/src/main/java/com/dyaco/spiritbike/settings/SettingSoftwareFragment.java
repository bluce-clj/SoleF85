package com.dyaco.spiritbike.settings;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.webapi.BaseApi;
import com.dyaco.spiritbike.webapi.IServiceApi;

import static android.content.Context.WINDOW_SERVICE;
import static com.dyaco.spiritbike.MyApplication.updateNotify;
import static com.dyaco.spiritbike.support.CommonUtils.checkSwVersion;
import static com.dyaco.spiritbike.support.CommonUtils.convertSwVersion;

public class SettingSoftwareFragment extends BaseFragment {
    private TextView tv_UpdateText;
    private Button back;
    private Button back2;
    private Button bt_update;
    private WindowManager windowManager;
    private Button button;
    private View view2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_software, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //開啟 ACCESSIBILITY 權限
//        Intent intent2 = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//        startActivity(intent2);

        //   kProgressHUD = kProgressHudUtil.getInstance().getProgressHud(mActivity);
        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
        ((DashboardActivity) mActivity).changeSignOutToBack(true, false, 0, -1);

        TextView version_text = view.findViewById(R.id.version_text);
        //  version_text.setText(String.format(Locale.getDefault(),"%s.%d", new CommonUtils().getLocalVersionName(mActivity), CommonUtils.checkSwVersion()));
        version_text.setText(new CommonUtils().getLocalVersionName(mActivity));

        tv_UpdateText = view.findViewById(R.id.tv_UpdateText);
        bt_update = view.findViewById(R.id.bt_update);
        back = view.findViewById(R.id.back);
        back2 = view.findViewById(R.id.back2);

        //  checkUpdate();


//        if (!updateNotify) {
//
//            bt_update.setVisibility(View.VISIBLE);
//            back.setVisibility(View.INVISIBLE);
//            tv_UpdateText.setText("There is a newer version available");
//        }

        back.setOnClickListener(v -> mActivity.onBackPressed());
        back2.setOnClickListener(v -> mActivity.onBackPressed());

//        LongClickUtils.setLongClick(new Handler(), back, 10000, v -> {
//            Log.d("KKKKKKKKK", "onLongClick: ");
//            MyApplication.SSEB = false;
//            start_redstone_app();
//            return false;
//        });
//
//        LongClickUtils.setLongClick(new Handler(), bt_update, 10000, v -> {
//            Log.d("KKKKKKKKK", "onLongClick: ");
//            MyApplication.SSEB = false;
//            start_redstone_app();
//            return false;
//        });

        bt_update.setOnClickListener(v -> {
            MyApplication.SSEB = false;
            start_redstone_app();
//            Intent intent = new Intent(getActivity(), UpdateSoftwareActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("fileUrl", fileUrl);
//            bundle.putString("md5", md5);
//            bundle.putBoolean("isForce", isForce);
//            intent.putExtras(bundle);
//            startActivity(intent);
        });

        if (!requireActivity().getPackageManager().canRequestPackageInstalls()) {
            startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + requireActivity().getPackageName())));
        }
    }

    String fileUrl;
    String md5;
    boolean isForce;

    private void checkUpdate() {

        BaseApi.request(BaseApi.createApi2(IServiceApi.class).apiCheckUpdate(),
                new BaseApi.IResponseListener<UpdateBean>() {
                    @Override
                    public void onSuccess(UpdateBean data) {
                        try {
                            if (!isAdded()) return;

                            if (convertSwVersion(data.getOS_Version()) > checkSwVersion()) {
                                updateNotify = true;
                                ((DashboardActivity) requireActivity()).ivUpdateNotify.setVisibility(View.VISIBLE);
                                bt_update.setVisibility(View.VISIBLE);
                                back2.setVisibility(View.VISIBLE);
                                back.setVisibility(View.INVISIBLE);
                                tv_UpdateText.setText("There is a newer version available");

                            } else {
                                updateNotify = false;
                                ((DashboardActivity) requireActivity()).ivUpdateNotify.setVisibility(View.INVISIBLE);
                                back.setVisibility(View.VISIBLE);
                                bt_update.setVisibility(View.INVISIBLE);
                                back2.setVisibility(View.INVISIBLE);
                                tv_UpdateText.setText("Your software version is the latest");
                                if (data.getVersionCode() > new CommonUtils().getLocalVersionCode()) {
                                    MyApplication.SSEB = false;
                                    Intent intent = new Intent(requireActivity(), UpdateSoftwareActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("fileUrl", data.getDownloadURL());
                                    bundle.putString("md5", data.getMD5());
                                    bundle.putBoolean("isForce", true);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            }


//                            if (data.getVersionCode() > new CommonUtils().getLocalVersionCode()) {
//                                fileUrl = data.getDownloadURL();
//                                md5 = data.getMD5();
//                                isForce = "YES".equalsIgnoreCase(data.getForceUpdate());
//
//                                if (fileUrl != null && !"".equals(fileUrl)) {
//                                    bt_update.setVisibility(View.VISIBLE);
//                                    back.setVisibility(View.INVISIBLE);
//                                    tv_UpdateText.setText("There is a newer version available");
//                                } else {
//                                    back.setVisibility(View.VISIBLE);
//                                    bt_update.setVisibility(View.INVISIBLE);
//                                    tv_UpdateText.setText("Your software version is the latest");
//                                }
//                            }
//                            Log.d("WEB_API-checkUpdate", "Response: " + new Gson().toJson(data));
                            //    BaseApi.clearDispose();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail() {
                        Log.d("WEB_API-checkUpdate", "失敗");
                        //   BaseApi.clearDispose();
                    }
                });
    }

    public void start_redstone_app() {
        windowManager = (WindowManager) requireActivity().getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        //   layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 184;
        layoutParams.height = 80;
        layoutParams.x = 500;
        layoutParams.y = 700;

        Typeface typeface = ResourcesCompat.getFont(requireActivity(), R.font.hanzel_bold);
        button = new Button(requireActivity());
        button.setText("BACK");
        button.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorFFFFFF));
        button.setTextSize(26);
        button.setTypeface(typeface);
        button.setGravity(Gravity.CENTER);
        button.setBackgroundResource(R.drawable.btn_rrect_9d2227_184);
        // button.setOnTouchListener(new SettingsFragment.FloatingOnTouchListener());
        windowManager.addView(button, layoutParams);

        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams();
        layoutParams2.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams2.gravity = Gravity.TOP | Gravity.START;
        layoutParams2.format = PixelFormat.RGBA_8888;
        layoutParams2.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        layoutParams2.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams2.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams2.height = 60;
        view2 = new View(requireActivity());
        view2.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.colorFFFFFF));
        windowManager.addView(view2, layoutParams2);

        button.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(requireActivity(), DashboardActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(0, 0);
            windowManager.removeViewImmediate(button);
            windowManager.removeViewImmediate(view2);
        });

        ComponentName mComponentName = new ComponentName("com.redstone.ota.ui", "com.redstone.ota.ui.activity.RsMainActivity");
        Intent mIntent = new Intent();
        mIntent.setComponent(mComponentName);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        requireActivity().startActivity(mIntent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeFloatingView();
    }

    private void removeFloatingView() {
        try {
            if (windowManager != null) {
                if (button != null && button.getWindowToken() != null) {
                    windowManager.removeViewImmediate(button);
                }
                if (view2 != null && view2.getWindowToken() != null) {
                    windowManager.removeViewImmediate(view2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUpdate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tv_UpdateText = null;
        back = null;
        back2 = null;
        bt_update = null;

        windowManager = null;
        button = null;
        view2 = null;
    }
}