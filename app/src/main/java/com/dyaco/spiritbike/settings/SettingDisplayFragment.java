package com.dyaco.spiritbike.settings;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.ruler.RulerView;
import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.R;

public class SettingDisplayFragment extends BaseFragment implements RulerView.OnValueChangeListener {

    private TextView display_text;
    private RulerView rulerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_display, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((DashboardActivity) mActivity).changeTopWidgetStyle(false);
        ((DashboardActivity) mActivity).changeSignOutToBack(true, false, 0, -1);

        //  autoBrightness(getActivity(), false);
        checkSystemWritePermission();


        ImageButton btnM = view.findViewById(R.id.bt_left);
        ImageButton btnP = view.findViewById(R.id.bt_right);

        new CommonUtils().addAutoClick(btnM);
        new CommonUtils().addAutoClick(btnP);

        display_text = view.findViewById(R.id.display_text);
        rulerView = view.findViewById(R.id.height_ruler);

        rulerView.setOnValueChangeListener(this);

        btnM.setOnClickListener(view1 -> {
            rulerView.setSelectedValue(Float.parseFloat(display_text.getText().toString()) - 1);
            //   rulerView.onScroll(null, null, 0, -23.0f);
            // xx(0f, MotionEvent.ACTION_UP);
        });

        btnP.setOnClickListener(view12 -> {
            rulerView.setSelectedValue(Float.parseFloat(display_text.getText().toString()) + 1);

        });

        rulerView.setSelectedValue(getBrightnessPresent(getScreenBrightness(mActivity)));
    }

    @Override
    public void onChange(RulerView view, float value) {
        if (view.getId() == R.id.height_ruler) {
            display_text.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));

            float brightnessPresent = getBrightness255(value);

            setBrightness(mActivity, brightnessPresent);

            if (Settings.System.canWrite(mActivity)) {
                saveBrightness(mActivity, (int) brightnessPresent);
            }
        }
    }


    /**
     * 判斷是否開啟了自動亮度調節
     */
    public static boolean isAutoBrightness(Context context) {
        ContentResolver resolver = context.getContentResolver();
        boolean automaticBrightness = false;
        try {
            automaticBrightness = Settings.System.getInt(resolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automaticBrightness;
    }

    /**
     * 獲取螢幕的亮度
     */
    public int getScreenBrightness(Context context) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = context.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 設定當前Activity顯示時的亮度
     * 螢幕亮度最大數值一般為255，各款手機有所不同
     * screenBrightness 的取值範圍在[0,1]之間
     */
    public void setBrightness(Activity activity, float brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        //  lp.screenBrightness = (float) brightness * (1f / 255f);
        lp.screenBrightness = brightness;
        activity.getWindow().setAttributes(lp);

        //  Log.i("BBBBB", "setBrightness: " + brightness);
    }

    /**
     * 開啟關閉自動亮度調節
     */
    public boolean autoBrightness(Context activity, boolean flag) {
        int value;
        if (flag) {
            value = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC; //開啟
        } else {
            value = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;//關閉
        }
        return Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                value);
    }

    /**
     * 儲存亮度設定狀態，退出app也能保持設定狀態
     */
    public void saveBrightness(Context context, int brightness) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = android.provider.Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        android.provider.Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        resolver.notifyChange(uri, null);
    }

    /**
     * 系統權限要手動開
     */
    private void checkSystemWritePermission() {
        boolean retVal;
        retVal = Settings.System.canWrite(mActivity);
        if (!retVal) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
            startActivity(intent);
        }
    }


    public float getBrightness255(float value) {
        return (value * 0.01f) * 255f;
    }

    private int getBrightnessPresent(float value) {
        return (Math.round((value / 255) * 100));
    }


//    private void xx(float x, int MotionEventAction) {
//        long downTime = SystemClock.uptimeMillis();
//        long eventTime = SystemClock.uptimeMillis() + 100;
//        //  float x = 263.0f;
//        float y = 147.0f;
//// List of meta states found here:     developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
//        int metaState = 0;
//        MotionEvent motionEvent = MotionEvent.obtain(
//                downTime,
//                eventTime,
//                MotionEventAction,
//                x,
//                y,
//                metaState
//        );
//
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        display_text = null;
        rulerView = null;
    }
}