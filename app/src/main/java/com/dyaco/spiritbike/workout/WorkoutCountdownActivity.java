package com.dyaco.spiritbike.workout;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.dyaco.spiritbike.support.BaseAppCompatActivity;
import com.dyaco.spiritbike.R;

public class WorkoutCountdownActivity extends BaseAppCompatActivity {

    private TextView tvCountDownNumber;
    private TextView tvCountdownStarting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_countdown);

        tvCountDownNumber = findViewById(R.id.countdown_number);
        tvCountdownStarting = findViewById(R.id.tv_countdown_starting);

        ImageView iv = this.findViewById(R.id.iv_rotate_ball);
        // 動畫設定 (指定旋轉動畫)
        Animation am = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // 動畫開始到結束的執行時間 (1000 = 1 秒)
        am.setDuration(4000);
        // 動畫重複次數 (Animation.INFINITE表示一直重複)
        am.setRepeatCount(Animation.INFINITE);
        // 圖片配置動畫
        iv.setAnimation(am);
        // 動畫開始
        am.startNow();

        new CountDownTimer(4000 + 20, 1000) {

            public void onTick(long millisUntilFinished) {
                long l = millisUntilFinished / 1000;
                if (l == 0) {
                    tvCountDownNumber.setText("Go!");
                    tvCountdownStarting.setVisibility(View.INVISIBLE);
                } else {
                    tvCountDownNumber.setText(l + "");
                }
            }

            public void onFinish() {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                //   bundle.putString("name",etUserName_UserName.getText().toString());
                intent.putExtras(bundle);
                intent.setClass(WorkoutCountdownActivity.this, WorkoutDashboardActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();

    }
}