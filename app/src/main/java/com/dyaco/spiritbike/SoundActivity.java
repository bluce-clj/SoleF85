package com.dyaco.spiritbike;

import androidx.constraintlayout.widget.Group;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.support.CommonUtils;
import com.dyaco.spiritbike.support.ruler.RulerView;

import static android.media.AudioManager.FLAG_SHOW_UI;
import static android.media.AudioManager.STREAM_MUSIC;

public class SoundActivity extends BaseAppCompatActivity implements RulerView.OnValueChangeListener {

    private TextView tv_sound_text;
    private RulerView rulerView;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        boolean isWorkout = bundle.getBoolean("isWorkout", false);

        volumeSetting();

        if (isWorkout) {
            setContentView(R.layout.activity_sound_workout);
//            View cl_base = findViewById(R.id.cl_base);
//            cl_base.setBackgroundResource(R.drawable.background_black);
        } else {
            setContentView(R.layout.activity_sound);
        }

        // ((RulerView)findViewById(R.id.height_ruler)).setOnValueChangeListener(this);

        tv_sound_text = findViewById(R.id.tv_sound_text);
        rulerView = findViewById(R.id.height_ruler);

        rulerView.setOnValueChangeListener(this);

        Group group = findViewById(R.id.group);

        SegmentedButtonGroup segmentedButtonGroup = findViewById(R.id.sc_unit);
        segmentedButtonGroup.setOnPositionChangedListener(position -> {
            switch (position) {
                case 0:
                    group.setVisibility(View.VISIBLE);
                    try {
                        audioManager.setStreamVolume(STREAM_MUSIC, Integer.parseInt(tv_sound_text.getText().toString()), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case 1:
                    group.setVisibility(View.INVISIBLE);
                    audioManager.setStreamVolume(STREAM_MUSIC, 0, 0);
                    rulerView.setSelectedValue(0);
            }
        });

//        audioManager.setStreamVolume(STREAM_MUSIC, newIndex, 0);
//        if (audioManager.getStreamVolume(STREAM_MUSIC) < newIndex) {
//            audioManager.setStreamVolume(STREAM_MUSIC, newIndex, FLAG_SHOW_UI);
//        }

        //  Log.i("SOUND", "getStreamMaxVolume: " + audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)); // 15
        //   Log.i("SOUND", "getStreamMinVolume: " + audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC)); // 0
        //  Log.i("SOUND", "getStreamVolume: " + getVolumePresent(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))); // 0

        //預設
      //  rulerView.setSelectedValue(getVolumePresent(audioManager.getStreamVolume(STREAM_MUSIC)));
        rulerView.setSelectedValue(audioManager.getStreamVolume(STREAM_MUSIC));

    }

    public void onClose(View view) {
        finish();
    }


    @Override
    public void onChange(RulerView view, float value) {
        if (view.getId() == R.id.height_ruler) {

            tv_sound_text.setText(CommonUtils.subZeroAndDot(String.valueOf(value)));

          //  int newIndex = (int) getVolume15(value);
            int newIndex = (int) value;
            audioManager.setStreamVolume(STREAM_MUSIC, newIndex, 0);
            if (audioManager.getStreamVolume(STREAM_MUSIC) < newIndex) {
                audioManager.setStreamVolume(STREAM_MUSIC, newIndex, FLAG_SHOW_UI);
            }

            Log.d("@@@@@", "onCreate: " + audioManager.getStreamVolume(STREAM_MUSIC) +","+ newIndex);
        }
    }

    public void onM(View view) {
        //  rulerView.setSelectedValue(Float.parseFloat(tv_sound_text.getText().toString()) - 1);

        //   rulerView.onScroll(null, null, 50.0f, 0);

        rulerView.setSelectedValue(Float.parseFloat(tv_sound_text.getText().toString()) - 1);

        //  xx(0f, MotionEvent.ACTION_UP);
    }


    public void onP(View view) {
        rulerView.setSelectedValue(Float.parseFloat(tv_sound_text.getText().toString()) + 1);
    }

    private void volumeSetting() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    }


    private float getVolume15(float value) {
        return (Math.round((value * 0.1f) * 15f));
    }

    private int getVolumePresent(float value) {
        return (int) Math.floor((value / 15) * 10);
    }

}