package com.dyaco.spiritbike.support;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class NoTouchView extends ConstraintLayout {

    public NoTouchView(@NonNull Context context) {
        super(context);
    }

    public NoTouchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NoTouchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NoTouchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    //    return super.onTouchEvent(event);
        performClick();
     //   Log.i("TTTTTTTTT", "onTouchEvent: ");

        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
     //   Log.i("TTTTTTTTT", "dispatchTouchEvent: " + ev.get);
       // return false;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return false;
    }

}
