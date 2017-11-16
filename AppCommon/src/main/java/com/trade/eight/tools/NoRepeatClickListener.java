package com.trade.eight.tools;

import android.view.View;

import java.util.Calendar;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/24.
 * 防止重复点击
 */

public abstract class NoRepeatClickListener implements View.OnClickListener {
    public static final int MIN_CLICK_DELAY_TIME = 1000;//
    private long lastClickTime = 0;
    public abstract void onNoRepeatClick(View v);

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoRepeatClick(v);
        }
    }
}
