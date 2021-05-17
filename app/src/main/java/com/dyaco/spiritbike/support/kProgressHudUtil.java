package com.dyaco.spiritbike.support;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.dyaco.spiritbike.R;
import com.kaopiz.kprogresshud.KProgressHUD;
import java.io.Serializable;

public class kProgressHudUtil implements Serializable {

    private static volatile kProgressHudUtil sSoleInstance;


    //private constructor.
    private kProgressHudUtil() {

        //Prevent form the reflection api.
        if (sSoleInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static kProgressHudUtil getInstance() {
        //Double check locking pattern
        if (sSoleInstance == null) { //Check for the first time

            synchronized (kProgressHudUtil.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (sSoleInstance == null) sSoleInstance = new kProgressHudUtil();
            }
        }

        return sSoleInstance;
    }

    //Make singleton from serialize and deserialize operation.
    protected kProgressHudUtil readResolve() {
        return getInstance();
    }


    public KProgressHUD getProgressHud(Context context) {

     //   ImageView imageView = new ImageView(context);
    //    imageView.setBackgroundResource(R.drawable.progress_round2);
//        AnimationDrawable drawable = (AnimationDrawable) imageView.getBackground();
//        drawable.start();

        ImageView imageView = new ImageView(context);
        imageView.setBackgroundResource(R.drawable.icon_loading);
     //   imageView.setBackgroundResource(R.drawable.progress_red);
        RotateAnimation am = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        // 動畫開始到結束的執行時間 (1000 = 1 秒)
        am.setDuration(800);
        LinearInterpolator lir = new LinearInterpolator();
        am.setInterpolator(lir);
        // 動畫重複次數 (Animation.INFINITE表示一直重複)
      //  am.setRepeatCount(Animation.INFINITE);
        am.setRepeatCount(Animation.INFINITE);
        // 圖片配置動畫
        imageView.setAnimation(am);
        // 動畫開始
        am.startNow();

        return KProgressHUD.create(context)
                .setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
                .setCancellable(false)
                .setDimAmount(0.5f)
                .setCustomView(imageView);

    }
}
