package com.trade.eight.entity.home;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.trade.eight.moudle.home.HomeInformationTimeDownEvent;
import com.trade.eight.moudle.home.adapter.HomeInformationAdapter;
import com.trade.eight.tools.DateUtil;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 16/10/17.
 * 首页资讯 含定時器
 */

public class HomeInformationTimer {

    HomeInformation homeInformation;
    View contentView;
    public HomeInformationTimer(HomeInformation homeInformation,View contentView) {
        this.homeInformation = homeInformation;
        this.contentView = contentView;
    }



    /********************
     * 倒计时部分
     **********************************/

    private MyCountDownTimer mc;
    private long timeCount;
    private TextView[] textView;

    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    表示以毫秒为单位 倒计时的总数
         *                          <p>
         *                          例如 millisInFuture=1000 表示1秒
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick 方法
         *                          <p>
         *                          例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            EventBus.getDefault().post(new HomeInformationTimeDownEvent());// 发送刷新数据event
            removeCallBacks();
        }

        @Override
        public void onTick(long millisUntilFinished) {


//            timeCount -= 60;//原逻辑
//            if (timeCount <= 60) {////原逻辑小于一分钟就刷新数据
            timeCount -= 1;//每秒
            if (timeCount <= 0) {////现逻辑小于一秒就刷新数据
                Log.e("onTick", millisUntilFinished + "======" + timeCount);
                EventBus.getDefault().post(new HomeInformationTimeDownEvent());// 发送刷新数据event
                removeCallBacks();
                return;
            }
            displayTextview();
        }
    }

    public void removeCallBacks() {
        if (mc != null) {
            mc.cancel();
        }
    }

    public void setDisplayTextview(TextView[] textView, long second) {
        timeCount = (second - System.currentTimeMillis()) / 1000L;// 倒计时的秒数
        this.textView = textView;
        displayTextview();

//        if (timeCount >= 60) {//原逻辑
        if (timeCount >= 10) {

            if (mc != null) {
                mc.cancel();
            }
//            mc = new MyCountDownTimer(timeCount * 1000, 1000 * 60);//原逻辑每隔一分钟ontick  之前日时分
            mc = new MyCountDownTimer(timeCount * 1000, 1000);//每隔一秒ontick 现在时分秒
            mc.start();
        }
    }

    void displayTextview() {
        HomeInformation homeInformationTag = (HomeInformation) textView[0].getTag();
        if (homeInformationTag.getInformactionId().equals(homeInformation.getInformactionId())) {

            String[] date = DateUtil.formatDateDecentHMS(timeCount);
            HomeInformationAdapter.ViewHolderType_3 viewHolderType_3 = (HomeInformationAdapter.ViewHolderType_3) contentView.getTag();
            viewHolderType_3.tv_homeinformation_ri.setText(date[0]);
            viewHolderType_3.tv_homeinformation_shi.setText(date[1]);
            viewHolderType_3.tv_homeinformation_fen.setText(date[2]);

//            textView[0].setText(date[0]);
//            textView[1].setText(date[1]);
//            textView[2].setText(date[2]);
        }

    }
    public HomeInformation getHomeInformation() {
        return homeInformation;
    }

    public void setHomeInformation(HomeInformation homeInformation) {
        this.homeInformation = homeInformation;
    }


    public MyCountDownTimer getMc() {
        return mc;
    }

    public void setMc(MyCountDownTimer mc) {
        this.mc = mc;
    }

    public long getTimeCount() {
        return timeCount;
    }

    public void setTimeCount(long timeCount) {
        this.timeCount = timeCount;
    }

    public TextView[] getTextView() {
        return textView;
    }

    public void setTextView(TextView[] textView) {
        this.textView = textView;
    }
}
