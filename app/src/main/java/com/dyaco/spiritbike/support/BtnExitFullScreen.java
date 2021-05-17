package com.dyaco.spiritbike.support;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.dyaco.spiritbike.DashboardActivity;
import com.dyaco.spiritbike.MyApplication;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.engineer.EngineerActivity;
import com.dyaco.spiritbike.mirroring.FloatingDashboardService;

import static android.content.Context.WINDOW_SERVICE;
import static com.dyaco.spiritbike.MyApplication.getInstance;

public class BtnExitFullScreen {

    private Button btnFullScreenExit;
    private WindowManager windowManager_exitButton;
    private WindowManager.LayoutParams layoutParams;
    public boolean floatButtonIsStarted;
    private boolean m_bOnClick;
    private long m_lStartTime;
    private Context context;

    public BtnExitFullScreen(Context context) {
        this.context = context;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showBtnFullScreenExit(Class cls, boolean isWorkout) {

      //  Log.d("JJJJJJ", "showBtnFullScreenExit: " + MyApplication.SSEB);
        if (!MyApplication.SSEB) return;

        if (floatButtonIsStarted) return;

        floatButtonIsStarted = true;
        windowManager_exitButton = (WindowManager) context.getApplicationContext().getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = isWorkout ? 114 : 172;
        layoutParams.height = isWorkout ? 114 : 172;
        layoutParams.x = 1000;
        layoutParams.y = 0;

        btnFullScreenExit = new Button(context.getApplicationContext());
        btnFullScreenExit.setAlpha(0.9f);
        btnFullScreenExit.setBackgroundResource(isWorkout ? R.drawable.btn_icon_inversion_screenfull_exit : R.drawable.btn_icon_screenfull_exit);
        windowManager_exitButton.addView(btnFullScreenExit, layoutParams);
        btnFullScreenExit.setOnTouchListener(new FloatingOnTouchListener());
        btnFullScreenExit.setOnClickListener(v -> {
            //點擊離開全螢幕的float按鈕
            if (CommonUtils.isFastClick()) return;

            try {

                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(context.getApplicationContext(), cls);
                context.getApplicationContext().startActivity(intent);

                windowManager_exitButton.removeView(btnFullScreenExit);
                floatButtonIsStarted = false;

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @SuppressLint("ClickableViewAccessibility")
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
                  //  windowManager_exitButton.updateViewLayout(view, layoutParams);

                    try {
                        windowManager_exitButton.updateViewLayout(view, layoutParams);
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
    }

    public void removeFloatView() {

        try {
            //關閉 離開全螢幕的按鈕
            if (windowManager_exitButton != null && btnFullScreenExit != null) {
                floatButtonIsStarted = false;
                windowManager_exitButton.removeView(btnFullScreenExit);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            windowManager_exitButton = null;
            btnFullScreenExit = null;
        }
    }
}
