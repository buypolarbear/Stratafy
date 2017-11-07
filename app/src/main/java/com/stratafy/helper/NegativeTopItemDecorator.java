package com.stratafy.helper;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class NegativeTopItemDecorator extends RecyclerView.ItemDecoration{
    private final int mTopMargin;

    public NegativeTopItemDecorator(int topMargin) {
        this.mTopMargin = topMargin;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = mTopMargin;
    }
}