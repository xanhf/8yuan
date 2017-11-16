package com.trade.eight.tools;

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
public class WeiPanPushUtils {

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

    /**
     * locl msg id
     *
     * @param context
     * @return
     */
    public static long getLocalLatestMsgId(Context context) {
        UserInfoDao userInfoDao = new UserInfoDao(context);
        if (!userInfoDao.isLogin()) {
            return -1;
        }
        UserInfo userInfo = userInfoDao.queryUserInfo();
        if (userInfo == null)
            return -1;
        String uid = userInfo.getUserId();
        return PreferenceSetting.getSharedPreferences(context, "push_weipan", uid, -1);
    }

    public static void setLocalLatestMsgId(Context context, long id) {
        UserInfoDao userInfoDao = new UserInfoDao(context);
        if (!userInfoDao.isLogin()) {
            return;
        }
        UserInfo userInfo = userInfoDao.queryUserInfo();
        if (userInfo == null)
            return;
        String uid = userInfo.getUserId();
        PreferenceSetting.setSharedPreferences(context, "push_weipan", uid, id);
    }

    /**
     * 是否显示new icon、
     *
     * @param context
     * @return
     */
    public static boolean isShowIconNew(Context context) {
        UserInfoDao userInfoDao = new UserInfoDao(context);
        if (!userInfoDao.isLogin()) {
            return false;
        }
        UserInfo userInfo = userInfoDao.queryUserInfo();
        if (userInfo == null)
            return false;
        String uid = userInfo.getUserId();
        return context.getSharedPreferences("push_weipan", 0).getBoolean(uid + "_isnewIcon", false);
    }

    public static void setShowIconNew(Context context, boolean isShow) {
        UserInfoDao userInfoDao = new UserInfoDao(context);
        if (!userInfoDao.isLogin()) {
            return;
        }
        UserInfo userInfo = userInfoDao.queryUserInfo();
        if (userInfo == null)
            return;
        String uid = userInfo.getUserId();
        context.getSharedPreferences("push_weipan", 0).edit().putBoolean(uid + "_isnewIcon", isShow).commit();
    }


    public static CommonResponse4List<WeipanMsg> getLastestMsgList(Context context) {
        try {
            //没有进入过页面消息列表
            if (getLocalLatestMsgId(context) == -1)
                return null;
            UserInfoDao userInfoDao = new UserInfoDao(context);
            if (!userInfoDao.isLogin()) {
                return null;
            }
            UserInfo userInfo = userInfoDao.queryUserInfo();
            if (userInfo == null)
                return null;
            String uid = userInfo.getUserId();
            LinkedHashMap<String, String> map = (LinkedHashMap) ApiConfig.getCommonMap(context);
            map.put("mnId", getLocalLatestMsgId(context) + "");
            map.put(UserInfo.UID, uid);
            map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, map));
            String url = AndroidAPIConfig.URL_WEIPAN_NEW_MSG_BYMNID;
            String res = HttpClientHelper.getStringFromPost(context, url, map);
            CommonResponse4List<WeipanMsg> list = CommonResponse4List.fromJson(res, WeipanMsg.class);
//            if (list!= null && list.isSuccess()) {
//                if (list.getData() != null && list.getData().size() > 0)
//                    setLocalLatestMsgId(context, list.getData().get(0).getMnId());
//            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
