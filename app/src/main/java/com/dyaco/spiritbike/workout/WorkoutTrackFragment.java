package com.dyaco.spiritbike.workout;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyaco.spiritbike.support.BaseFragment;
import com.dyaco.spiritbike.support.GlideApp;
import com.dyaco.spiritbike.support.LevelImageView;
import com.dyaco.spiritbike.R;


public class WorkoutTrackFragment extends BaseFragment {
    public TextView tvLapCount;
    //  int lap;
    double lap;
    private ObjectAnimator objectAnimator;
    private long currentPlayTime;
    int speed;
    private double remainder;
    private int schedule;

    public WorkoutTrackFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_track, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        speed = 1130000;

        tvLapCount = view.findViewById(R.id.tv_lap_count);
        LevelImageView ivLap = view.findViewById(R.id.iv_lap_animation);

//        GlideApp.with(mActivity)
//                .load(R.drawable.lap_animation2)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .into(ivLap);

        ivLap.setImageResource(R.drawable.lap_animation2);
        //   ivLap.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        //新建動畫.屬性值從1-10的變化
        //   objectAnimator = ObjectAnimator.ofInt(ivLap, "imageLevel", 1, 25);
        objectAnimator = ObjectAnimator.ofInt(ivLap, "imageLevel", 1, 24);
        //設定動畫的播放數量為一直播放.
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        //設定一個速度加速器.讓動畫看起來可以更貼近現實效果.
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        objectAnimator.setDuration(speed);
        objectAnimator.start();

        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                mActivity.runOnUiThread(() -> tvLapCount.setText(String.valueOf(++lap)));
            }
        });
    }

    public void stopAnimation() {
        currentPlayTime = objectAnimator.getCurrentPlayTime();
        objectAnimator.cancel();
    }

    public void startAnimation() {
        if (objectAnimator != null) {
            objectAnimator.start();
            objectAnimator.setCurrentPlayTime(currentPlayTime);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (objectAnimator != null) objectAnimator.cancel();

        objectAnimator = null;
    }

    private static final String TAG = "WorkoutTrackFragment";

    /**
     * 變更速度
     *
     * @param milliSecond 跑完一圈需要的 milliSecond
     */
    public void updateLapSpeed(double milliSecond, int unit) {
        final int METRIC = 400;
        final int IMPERIAL = 1320;
        int playground = unit == 0 ? METRIC : IMPERIAL;
        lap = Math.floor(milliSecond / playground);
        remainder = (milliSecond % playground);
        schedule = (int) Math.ceil((remainder / ((double) playground / 24)));
        mActivity.runOnUiThread(() -> tvLapCount.setText(String.valueOf((int) lap)));
        objectAnimator.setIntValues(schedule, schedule);
    }
//    public void updateLapSpeed(long milliSecond) {
//      //  Log.d("TRAAAAA", "updateLapSpeed: " + milliSecond / 1000 + "," + objectAnimator.getAnimatedValue().toString());
//        if (objectAnimator != null) {
//            i++;
//            if (i == 25) i = 1;
//            objectAnimator.setIntValues(i, 25);
//            //  objectAnimator.setDuration(milliSecond);
//            //   objectAnimator.setObjectValues(i++);
//            //   objectAnimator.setCurrentFraction(i++);
//        }
//    }
}