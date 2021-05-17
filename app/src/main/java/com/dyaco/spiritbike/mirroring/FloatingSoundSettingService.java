package com.dyaco.spiritbike.mirroring;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.dyaco.spiritbike.R;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.ruler.RulerView;
import com.tencent.mmkv.MMKV;

import static android.media.AudioManager.FLAG_SHOW_UI;
import static android.media.AudioManager.STREAM_MUSIC;

public class FloatingSoundSettingService extends Service implements RulerView.OnValueChangeListener {
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    public static boolean isStarted = false;
    private ConstraintLayout view;
    private TextView tv_sound_text;
    private RulerView rulerView;
    private AudioManager audioManager;
    private boolean isWorkout;
    private SegmentedButtonGroup segmentedButtonGroup;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isWorkout = intent.getBooleanExtra("isWorkout", false);

        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    private void showFloatingWindow() {

        int layout = isWorkout ? R.layout.activity_sound_workout : R.layout.activity_floating_sound;

        view = (ConstraintLayout) LayoutInflater.from(this).inflate(layout, null);
        windowManager.addView(view, layoutParams);

        tv_sound_text = view.findViewById(R.id.tv_sound_text);
        rulerView = view.findViewById(R.id.height_ruler);

        rulerView.setOnValueChangeListener(this);

        Group group = view.findViewById(R.id.group);

        segmentedButtonGroup = view.findViewById(R.id.sc_unit);
        segmentedButtonGroup.setOnPositionChangedListener(position -> {
            switch (position) {
                case 0:
                    group.setVisibility(View.VISIBLE);

                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Integer.parseInt(tv_sound_text.getText().toString()), 0);
                    break;
                case 1:
                    group.setVisibility(View.INVISIBLE);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    MMKV.defaultMMKV().encode("ooxxoo", 0);
                 //   audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
            }
        });


      //  MMKV.defaultMMKV().remove("ooxxoo");

        volumeSetting();

        //預設
      //  rulerView.setSelectedValue(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        Log.d("音量", "getStreamVolume: " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        Log.d("音量", "decodeFloat: " + MMKV.defaultMMKV().decodeFloat("ooxxoo"));

        rulerView.setSelectedValue(MMKV.defaultMMKV().decodeFloat("ooxxoo",0) == 0 ? audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) : MMKV.defaultMMKV().decodeFloat("ooxxoo",0));

       if(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
           segmentedButtonGroup.setPosition(1,false);
       }

        initEvent();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        try {
            if (windowManager != null) {
                isStarted = false;
                windowManager.removeView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEvent() {
        //關閉
        view.findViewById(R.id.btClose).setOnClickListener(v -> {
            try {
                isStarted = false;
                windowManager.removeView(view);
                windowManager = null;
                view = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            stopSelf();
        });

        ImageButton btnM = view.findViewById(R.id.bt_left);
        ImageButton btnP = view.findViewById(R.id.bt_right);
        btnM.setOnClickListener(v -> rulerView.setSelectedValue(Float.parseFloat(tv_sound_text.getText().toString()) - 1));
        btnP.setOnClickListener(v -> rulerView.setSelectedValue(Float.parseFloat(tv_sound_text.getText().toString()) + 1));
        new CommonUtils().addAutoClick(btnM);
        new CommonUtils().addAutoClick(btnP);
    }

    @Override
    public void onChange(RulerView view, float value) {
        if (view.getId() == R.id.height_ruler) {
            tv_sound_text.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));
            MMKV.defaultMMKV().encode("ooxxoo", value);
            int newIndex = (int) value;
            if (newIndex == 15) newIndex = 14;
            audioManager.setStreamVolume(STREAM_MUSIC, newIndex, 0);
            if (audioManager.getStreamVolume(STREAM_MUSIC) < newIndex) {
                //音量過大警示
                audioManager.setStreamVolume(STREAM_MUSIC, newIndex, FLAG_SHOW_UI);
            }

            Log.d("音量", "newIndex: " + newIndex);
        }
    }

    private void volumeSetting() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    }

}
