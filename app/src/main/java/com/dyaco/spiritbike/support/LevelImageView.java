package com.dyaco.spiritbike.support;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LevelImageView extends androidx.appcompat.widget.AppCompatImageView {
    public LevelImageView(Context context) {
        super(context);
    }

    public LevelImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LevelImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int imageLevel = 0;
    public void setImageLevel(int level) {
        if (this.imageLevel == level)
            return;
        super.setImageLevel(level);
        this.imageLevel = level;
    }
    public int getImageLevel() {
        return imageLevel;
    }
    // 下一level介面。
    public void nextLevel() {
        setImageLevel(imageLevel++ % maxLevel);
    }
    private int maxLevel = 25;

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

}
