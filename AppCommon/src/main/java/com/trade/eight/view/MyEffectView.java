package com.trade.eight.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by fangzhu on 2015/4/15.
 * 虚线
 */
public class MyEffectView extends TextView {
    float mStrokeWidth = 4;

    int mLineColor = Color.parseColor("#80848999");

    PathEffect effects = new DashPathEffect(new float[] { 8, 10, 8, 10}, 0);

    /**
     * 默认是竖屏
     */
    boolean isHorinational = false;

    public MyEffectView(Context context) {
        super(context);
    }

    public MyEffectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEffectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeWidth(mStrokeWidth);
        mPaintLine.setColor(mLineColor);
        mPaintLine.setPathEffect(effects);
        //横向
        if (isHorinational)
            canvas.drawLine(0, 0, getWidth(), 0, mPaintLine);
        else
            canvas.drawLine(0, 0, 0, getHeight(), mPaintLine);
    }

    public float getmStrokeWidth() {
        return mStrokeWidth;
    }

    public void setmStrokeWidth(float mStrokeWidth) {
        this.mStrokeWidth = mStrokeWidth;
    }

    public int getmLineColor() {
        return mLineColor;
    }

    public void setmLineColor(int mLineColor) {
        this.mLineColor = mLineColor;
    }

    public PathEffect getEffects() {
        return effects;
    }

    public void setEffects(PathEffect effects) {
        this.effects = effects;
    }

    public boolean isHorinational() {
        return isHorinational;
    }

    public void setHorinational(boolean isHorinational) {
        this.isHorinational = isHorinational;
    }
}
