package com.trade.eight.moudle.me.tradeinfo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.WeipanMsg;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.receiver.AlarmReceiver;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.PreferenceSetting;

import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2015/9/9.
 * WeiPanPushUtils 开启轮询的刷新闹钟，每次启动都重新开启闹钟
 * 消息列表的 循环获取最新数据
 * 1.进入消息列表之后 记录最新的msgid
 * 2.根据最新的msgid，获取最新的数据，然后发送广播到 mainAct AlarmReceiver
 * 3.如果有最新数据，显示我的页面的红点 共两处红点，显示最新msg的通知
 * 4.点击了我的消息列表之后，隐藏红点
 */
public class TradeUserInfoRefresh {

    public static final String TAG = "WeiPanPushUtils";
    public static final boolean DEDUG = false;
    public static final String ICON_RECEIVED_ACTION = "com.ixit.icon.visible";
    public static int TIME_REPET_MIN = 5;//5分钟

    public static void startAlarm(Context context, boolean isDelay) {
        try {
            UserInfoDao userInfoDao = new UserInfoDao(context);
            if (!userInfoDao.isLogin()) {
                return;
            }
            Log.v(TAG, "startAlarm");
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction(AlarmReceiver.ACTION_WEIPAN_MSG);

            if (!isDelay) {
                //马上执行一次 不延时
                context.sendBroadcast(intent);
            }
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
            AlarmManager alarm = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

            alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + TIME_REPET_MIN * 60 * 1000, TIME_REPET_MIN * 60 * 1000, sender);

            //test
//            if (DEDUG)
//                alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5 * 1000, sender);
//            else
//                alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + TIME_REPET_MIN * 60 * 1000,  TIME_REPET_MIN * 60 * 1000, sender);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cancleAlarm(Context context) {
        try {
            UserInfoDao userInfoDao = new UserInfoDao(context);
            if (!userInfoDao.isLogin()) {
                return;
            }
            Log.v(TAG, "cancleAlarm");
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction(AlarmReceiver.ACTION_WEIPAN_MSG);

            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
            AlarmManager alarm = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            alarm.cancel(sender);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
