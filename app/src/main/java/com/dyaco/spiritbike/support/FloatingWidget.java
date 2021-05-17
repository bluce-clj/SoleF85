package com.dyaco.spiritbike.support;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.settings.SettingsFragment;

import static android.content.Context.WINDOW_SERVICE;
import static com.dyaco.spiritbike.mirroring.FloatingDashboardService.HIDE_FLOATING_DASHBOARD;

public class FloatingWidget {
    private final Context mContext;
    private Button button;
    private View view2;

    public FloatingWidget(Context context) {
        this.mContext = context;
    }

//    private static FloatingWidget INSTANCE = null;
//    public FloatingWidget(Context Context) {
//        this.mContext = Context;
//    }
//    public static FloatingWidget getSingleton(Activity activity){
//        if (null == INSTANCE){
//            INSTANCE = new FloatingWidget(activity);
//        }
//        return INSTANCE;
//    }

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    boolean m_bOnClick = false;
    private long m_lStartTime;


    /**
     *
     * @param type 0 wifi, 1 bt
     */
    @SuppressLint("ClickableViewAccessibility")
    public void callSetting(int type, Class backCls) {
        MyApplication.SSEB = false;
        windowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        //   layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 184;
        layoutParams.height = 80;
        layoutParams.x = 500;
        layoutParams.y = 700;

        Typeface typeface = ResourcesCompat.getFont(mContext, R.font.hanzel_bold);
        button = new Button(mContext);
        button.setText("BACK");
        button.setTextColor(ContextCompat.getColor(mContext, R.color.colorFFFFFF));
        button.setTextSize(26);
        button.setTypeface(typeface);
        button.setGravity(Gravity.CENTER);
        button.setBackgroundResource(R.drawable.btn_rrect_9d2227_184);
        button.setOnTouchListener(btInternetOnTouch);
        windowManager.addView(button, layoutParams);

        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams();
        layoutParams2.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams2.gravity = Gravity.TOP | Gravity.START;
        layoutParams2.format = PixelFormat.RGBA_8888;
        layoutParams2.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams2.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams2.height = 60;
        view2 = new View(mContext);
        view2.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorf5f5f5));
        windowManager.addView(view2, layoutParams2);

        button.setOnClickListener(v -> {
            try {
                MyApplication.SSEB = true;
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(mContext, backCls);
                mContext.startActivity(intent);
                windowManager.removeViewImmediate(button);
                windowManager.removeViewImmediate(view2);
                ((Activity) mContext).overridePendingTransition(0, 0);
            //    RxBus.getInstance().post(new MsgEvent(HIDE_FLOATING_DASHBOARD, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        String action;
        if (type == 0) {
            action = Settings.ACTION_WIFI_SETTINGS;
        } else if (type == 1) {
            action = Settings.ACTION_BLUETOOTH_SETTINGS;
        } else {
            action = Settings.ACTION_WIFI_SETTINGS;
        }

//        action = Settings.ACTION_NFC_SETTINGS;

        Intent intent = new Intent();
        intent.setAction(action);
      //  intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(0, 0);
    }

    private final Button.OnTouchListener btInternetOnTouch = new Button.OnTouchListener() {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    m_bOnClick = false;
                    m_lStartTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    m_bOnClick = true;
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                  //  windowManager.updateViewLayout(view, layoutParams);

                    try {
                        windowManager.updateViewLayout(view, layoutParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    long m_lEndTime = System.currentTimeMillis();
                    m_bOnClick = (m_lEndTime - m_lStartTime) > 0.1 * 5000L;
                    break;
                default:
                    break;
            }
            return m_bOnClick;
        }
    };

    public void removeFloatingView() {
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
}
