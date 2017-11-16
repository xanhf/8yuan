package com.trade.eight.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * 作者：Created by ocean
 * 时间：on 17/1/6.
 * 子view没有点击事件
 */

public class ChildNoTouchRelativeLayout extends RelativeLayout{
    public ChildNoTouchRelativeLayout(Context context) {
        super(context);
    }

    public ChildNoTouchRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildNoTouchRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
