package com.tutorials.camera.decorations;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CardDecoration extends RecyclerView.ItemDecoration
{
    private int space;
    public CardDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
    {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = space;
        outRect.right = space;
        outRect.left = space;
        outRect.bottom = space;
    }
}
