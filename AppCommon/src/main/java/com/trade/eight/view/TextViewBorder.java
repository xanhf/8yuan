package com.trade.eight.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

import com.trade.eight.tools.Utils;

/**
 * 作者：Created by ocean
 * 时间：on 2017/4/7.
 * 边框改变颜色
 */

public class TextViewBorder extends TextView {

    private int strokeWidth;
    private int roundRadius;
    private int borderCol;

    private Paint borderPaint;

    public TextViewBorder(Context context) {
        super(context);
    }


    public TextViewBorder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public TextViewBorder(Context context, AttributeSet attrs) {
        super(context, attrs);
        strokeWidth = Utils.dip2px(context, 0.5f);
        roundRadius = Utils.dip2px(context, 3);

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(strokeWidth);
        borderPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (0 == this.getText().toString().length())
            return;

        borderPaint.setColor(borderCol);


        int w = this.getMeasuredWidth();
        int h = this.getMeasuredHeight();

        RectF r = new RectF(2, 2, w - 2, h - 2);
        canvas.drawRoundRect(r, roundRadius, roundRadius, borderPaint);
        super.onDraw(canvas);
    }

    public int getBordderColor() {
        return borderCol;
    }

    public void setBorderColor(int newColor) {
        borderCol = newColor;
        invalidate();
        requestLayout();
    }
}
