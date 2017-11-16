package com.trade.eight.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import com.easylife.ten.lib.R;
import com.trade.eight.tools.Utils;

/**
 * 作者：Created by ocean
 * 时间：on 16/10/17.
 * 圆形背景框span
 */

public class CircleBlueBackgroundSpan extends ReplacementSpan {

    int width;
    int height;
    private float mTextSize;
    Context context;
    private float MAGIC_NUMBER = 0;

    public CircleBlueBackgroundSpan(Context context,float textSize) {
        this.width = Utils.dip2px(context,24);
        this.height = Utils.dip2px(context,24);
        this.mTextSize = textSize;
        this.context = context;

        MAGIC_NUMBER = Utils.dip2px(context, 2);
    }

    private float measureText(Paint paint, CharSequence text, int start, int end) {
        return paint.measureText(text, start, end);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return Math.round(measureText(paint, text, start, end));
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        RectF rect = new RectF(x, top, x + width, top + height);
        paint.setColor(context.getResources().getColor(R.color.sub_blue));
//        canvas.drawRoundRect(rect, 10, 10, paint);
        canvas.drawCircle((x + width)/2,(top + height)/2,width/2,paint);
        paint.setColor(context.getResources().getColor(R.color.white));
        int y_coordinate = (int) (mTextSize + (height - mTextSize) / 2 - MAGIC_NUMBER);
        canvas.drawText(text, start, end, x + (width - measureText(paint, text, start, end)) / 2, y_coordinate, paint);
    }
}
