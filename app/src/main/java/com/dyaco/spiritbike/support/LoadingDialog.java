package com.dyaco.spiritbike.support;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.dyaco.spiritbike.R;

public class LoadingDialog extends Dialog {


    private RotateAnimation am;
    private ImageView imageView;


    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.DialogProgressTheme);

        initDialog();
    }

    private void initDialog() {
      //  dialog = new Dialog(context, R.style.DialogProgressTheme);
        setCanceledOnTouchOutside(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setContentView(R.layout.dialog_progress);
        imageView = findViewById(R.id.image);
        imageView.setBackgroundResource(R.drawable.icon_loading);
        am = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        am.setDuration(800);
        LinearInterpolator lir = new LinearInterpolator();
        am.setInterpolator(lir);
        am.setRepeatCount(Animation.INFINITE);
        am.setRepeatMode(Animation.RESTART);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    private void setAnimation() {
        imageView.setAnimation(am);
        am.startNow();
    }

    public void startDialog() {
        setAnimation();
        show();
    }

    public void stopDialog() {
        am.cancel();
        dismiss();
    }

}
