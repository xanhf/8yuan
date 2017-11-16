package com.netease.nim.uikit.session.emoji;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * Created by dufangzhu on 2017/3/23.
 * 设置图文剧中
 *
 * @link http://blog.csdn.net/gaoyucindy/article/details/39473135
 *
 * 发现换行的时候不能剧中 使用了linespace
 */

public class MyImageSpan extends ImageSpan {
    /*新增剧中处理方式*/
    public static final int ALIGN_CENTER = 1234;
    int align = ALIGN_CENTER;

    public MyImageSpan(Drawable d, int verticalAlignment) {
        super(d, verticalAlignment);
        this.align = verticalAlignment;
    }


    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fm) {
        if (align != ALIGN_CENTER)
            super.getSize(paint, text, start, end, fm);
        try {
            Drawable d = getDrawable();
            Rect rect = d.getBounds();
            if (fm != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.bottom - fmPaint.top;
                int drHeight = rect.bottom - rect.top;

                int top = drHeight / 2 - fontHeight / 4;
                int bottom = drHeight / 2 + fontHeight / 4;

                fm.ascent = -bottom;
                fm.top = -bottom;
                fm.bottom = top;
                fm.descent = top;
            }
            return rect.right;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.getSize(paint, text, start, end, fm);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        if (align != ALIGN_CENTER)
            super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        try {
//            // image to draw
//            Drawable b = getDrawable();
//            // font metrics of text to be replaced
//            Paint.FontMetricsInt fm = paint.getFontMetricsInt();
//            int transY = (y + fm.descent + y + fm.ascent) / 2
//                    - b.getBounds().bottom / 2;
//
//            canvas.save();
//            canvas.translate(x, transY);
//            b.draw(canvas);
//            canvas.restore();

            Drawable b = getDrawable();
            canvas.save();
            int transY = 0;
            transY = ((bottom - top) - b.getBounds().bottom) / 2 + top;
            canvas.translate(x, transY);
            b.draw(canvas);
            canvas.restore();
        } catch (Exception e) {
            e.printStackTrace();
            super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        }
    }
}
