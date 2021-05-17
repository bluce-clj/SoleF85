package com.dyaco.spiritbike.support;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class MyItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        // only for the last one
        if (parent.getChildAdapterPosition(view) == Objects.requireNonNull(parent.getAdapter()).getItemCount() - 1) {
            outRect.right = 28/* set your margin here */;
        }
    }
}