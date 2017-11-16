package com.trade.eight.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.tools.Utils;

public class PupupSeekBar extends SeekBar {

    private final int mThumbWidth = 25;
    private PopupWindow mPopupWindow;
    private LayoutInflater mInflater;
    private View mView;
    private int[] mPosition;
    private boolean isClose = false;

    private TextView mTvProgress;

    public PupupSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        mInflater = LayoutInflater.from(context);
        mView = mInflater.inflate(R.layout.seekbar_popup, null);
        mTvProgress = (TextView) mView.findViewById(R.id.tvPop);
        mPopupWindow = new PopupWindow(mView, mView.getWidth(),
                mView.getHeight(), false);
        mPopupWindow.setOutsideTouchable(true);
        mPosition = new int[2];
    }

    public void setSeekBarText(String str) {
        mTvProgress.setText(str);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                this.getLocationOnScreen(mPosition);
                mPopupWindow.showAsDropDown(this);
                break;
            case MotionEvent.ACTION_UP:

                // mPopupWindow.dismiss();

                break;
        }

        return super.onTouchEvent(event);
    }

    private int getViewWidth(View v) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
        return v.getMeasuredWidth();
    }

    private int getViewHeight(View v) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
        return v.getMeasuredHeight();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        int thumb_x = 0;
        if (0 != this.getMax()) {
            thumb_x = this.getProgress() * (this.getWidth() - mThumbWidth)
                    / this.getMax();
        }
        int middle = 0;
        if (isClose)
            middle = this.getHeight() / 2 + Utils.dip2px(getContext(), 272);
        else
            middle = this.getHeight() / 2 + Utils.dip2px(getContext(), 342);

        super.onDraw(canvas);

        if (mPopupWindow != null) {
            try {
                this.getLocationOnScreen(mPosition);
                mPopupWindow.update(thumb_x + mPosition[0]
                                - getViewWidth(mView) / 2 + mThumbWidth / 2, middle,
                        getViewWidth(mView), getViewHeight(mView));

            } catch (Exception e) {
                // TODO: handle exception
            }
        }

    }

    public void setClose(boolean isClose) {
        this.isClose = isClose;
    }

}
