package com.dyaco.spiritbike.mirroring;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.dyaco.spiritbike.R;

public class FloatingButtonService extends Service {
    private Button btnFullScreenExit;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    public static boolean isStarted = false;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 150;
        layoutParams.height = 150;
        layoutParams.x = 300;
        layoutParams.y = 300;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showFloatingWindow() {

        if (Settings.canDrawOverlays(this)) {
            btnFullScreenExit = new Button(getApplicationContext());
            btnFullScreenExit.setAlpha(0.9f);
            btnFullScreenExit.setBackgroundResource(R.drawable.btn_icon_screenfull_exit);
            windowManager.addView(btnFullScreenExit, layoutParams);

            btnFullScreenExit.setOnTouchListener(new FloatingOnTouchListener());

            btnFullScreenExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("MirroringFragment", "onClick: ");
                }
            });
        }
    }

    boolean m_bOnClick = false;
    private long m_lStartTime;

    private class FloatingOnTouchListener implements View.OnTouchListener {
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

    }
}
