package com.dyaco.spiritbike.support;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridViewSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    public GridViewSpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        //不是第一個的格子都設一個左邊和底部的間距
     //   outRect.left = space;
        outRect.bottom = space;
        //由於每行都只有3個，所以第一個都是3的倍數，把左邊距設為0
//        if (parent.getChildLayoutPosition(view) %3==0) {
//            outRect.left = 0;
//        }
    }

}
