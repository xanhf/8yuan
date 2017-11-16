package com.trade.eight.moudle.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;

import com.easylife.ten.lib.R;
import com.trade.eight.app.MyApplication;
import com.trade.eight.moudle.chatroom.ChatRoomNoticeEvent;
import com.trade.eight.moudle.chatroom.OrderNotifyEvent;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.push.activity.CashOutNoticeActivity;
import com.trade.eight.moudle.push.activity.OrderAndTradeNotifyActivity;
import com.trade.eight.moudle.push.activity.ProductNoticeNotifyActivity;
import com.trade.eight.moudle.push.activity.TradeFXNoticeActivity;
import com.trade.eight.moudle.push.entity.PushExtraObj;
import com.trade.eight.moudle.push.entity.PushMsgObj;
import com.trade.eight.moudle.push.activity.CloseOrderNotifyActivity;
import com.trade.eight.mpush.android.MPushService;
import com.trade.eight.mpush.entity.OrderNotifyData;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.OpenActivityUtil;
import com.trade.eight.tools.StringUtil;

import java.util.Random;

import de.greenrobot.event.EventBus;

/**
 * Created by fangzhu on 2017/1/12.
 * 解析个推的推送的打开动作
 */
public class PushActionUtil {
    public static final String TAG = "PushActionUtil";
    //h5活动链接
    public static final String ACT_WEBVIEW = "webView";
    //打开首页的公告，交易机会等url
    public static final String ACT_NEWS = "news";
    //打开首页 没有参数
    public static final String ACT_MAIN = "main";


    public static void open(Context context, PushMsgObj pushMsgObj) {

        if (pushMsgObj != null && pushMsgObj.getSendType() == PushMsgObj.PRODUCT_NOTICE) {//为行情提醒
            PushExtraObj productNoticeNotify = pushMsgObj.getData();
            if (productNoticeNotify != null) {
                if (MyApplication.getInstance().isApplicationBroughtToBackground(context)) {//app在后台运行
                    Log.e(TAG, "在后台运行");
                    sendDefaultNotice(context, null, pushMsgObj.getBody(), 0, productNoticeNotify);
                } else {
                    ProductNoticeNotifyActivity.startProductNoticeActivity(context, productNoticeNotify);
                }
            }

        } else if (pushMsgObj != null && pushMsgObj.getSendType() == PushMsgObj.CLOSEORDER_NOTICE) {// 系统平仓提醒

            PushExtraObj closeOrderNotify = pushMsgObj.getData();
            if (closeOrderNotify != null) {
                if (MyApplication.getInstance().isApplicationBroughtToBackground(context)) {//app在后台运行
                    Log.e(TAG, "在后台运行");
                    sendDefaultNotice4ClosrOrder(context, pushMsgObj.getTitle(), pushMsgObj.getBody(), 0, closeOrderNotify);
                } else {
                    CloseOrderNotifyActivity.startAct(context, closeOrderNotify);
                }
            }

        } else if (pushMsgObj != null && pushMsgObj.getSendType() == PushMsgObj.CHATROOM_NOTICE) {// 聊天室公告
            PushExtraObj pushExtraObj = pushMsgObj.getData();
            EventBus.getDefault().post(new ChatRoomNoticeEvent(pushExtraObj));

        }
//        else if (pushMsgObj != null && pushMsgObj.getSendType() == PushMsgObj.AUTH_NOTICE) {// 实名认证
//            PushExtraObj pushExtraObj = pushMsgObj.getData();
//            if (pushExtraObj != null) {
//                if (MyApplication.getInstance().isApplicationBroughtToBackground(context)) {//app在后台运行
//                    Log.e(TAG, "在后台运行");
//                } else {
//
//                }
//            }
//
//        }
        else if (pushMsgObj != null && pushMsgObj.getSendType() == PushMsgObj.CASHOUT_NOTICE) { // 出金提示
            PushExtraObj pushExtraObj = pushMsgObj.getData();
            if (pushExtraObj != null) {
                if (MyApplication.getInstance().isApplicationBroughtToBackground(context)) {//app在后台运行
                    Log.e(TAG, "在后台运行");
                    sendDefaultNotice4CashOut(context, pushMsgObj.getTitle(), pushMsgObj.getBody() + pushExtraObj.getMark(), 0, pushMsgObj);
                } else {
                    CashOutNoticeActivity.startCashOutNoticeActivity(context, pushMsgObj);
                }
            }

        } else if (pushMsgObj != null && pushMsgObj.getSendType() == PushMsgObj.ACCOUNTFENGXIAN_NOTICE) {// 用户风险提示
            if (MyApplication.getInstance().isApplicationBroughtToBackground(context)) {//app在后台运行
                Log.e(TAG, "在后台运行");
                PushExtraObj pushExtraObj = pushMsgObj.getData();
                if (pushExtraObj != null) {
                    sendDefaultNotice4BCJG(context, pushMsgObj.getTitle(), pushMsgObj.getBody() + pushExtraObj.getTimeOutTxt(), 0, pushMsgObj);
                } else {
                    sendDefaultNotice4BCJG(context, pushMsgObj.getTitle(), pushMsgObj.getBody(), 0, pushMsgObj);
                }
            } else {
                TradeFXNoticeActivity.startAct(context, pushMsgObj);
            }
        } else if (pushMsgObj != null && pushMsgObj.getSendType() == PushMsgObj.ORDERNOTIFY_NOTICE) {// 订单反馈
            if (MyApplication.getInstance().isApplicationBroughtToBackground(context)) {//app在后台运行
                Log.e(TAG, "在后台运行");
                PushExtraObj pushExtraObj = pushMsgObj.getData();
                if (pushExtraObj != null) {
                    sendDefaultNotice4OrderNotify(context, 0, pushExtraObj);
                }
            } else {
                PushExtraObj pushExtraObj = pushMsgObj.getData();
                if (pushExtraObj != null) {
                    EventBus.getDefault().post(new OrderNotifyEvent());

                    OrderNotifyData orderNotifyData = OrderNotifyData.pushExtraObjToData(pushExtraObj);
                    Log.e(TAG, orderNotifyData.toString());
                    OrderAndTradeNotifyActivity.startOrderNotifyAct(context, orderNotifyData);
                }
            }

        } else {// 默认为系统消息
            context.startActivity(getIntent(context, pushMsgObj));
        }

    }

    /**
     * 解析推送intent
     * {"action":"webView","body":"新春将至，为了答谢广大fxbtg用户春节红包雨不停。","sendType":2,"title":"新春送不停","type":0,"url":"http://t.w.8caopan.com/static/html/61.html?articleId=21&sourceId=10&informationId=61&deviceType=1"}
     *
     * @param pushMsgObj 推送收到的action 解析的额外字段对象
     */
    public static Intent getIntent(Context context, PushMsgObj pushMsgObj) {
        Intent intent = new Intent();
        intent.putExtra(OpenActivityUtil.IS_GOHOME, true);//返回首页
        //必须设置是新启动
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        String clsNameDefault = MainActivity.class.getName();
        try {
            if (pushMsgObj == null) {
                Log.v(TAG, "pushMsgObj == null");
                //直接打开
                intent.setClassName(context, clsNameDefault);
                return intent;
            }
            //action 没有解析到，也是打开首页
            String action = pushMsgObj.getAction();
            if (StringUtil.isEmpty(action)) {
                Log.v(TAG, "action is empty");
                //直接打开
                intent.setClassName(context, clsNameDefault);
                return intent;
            }
            action = action.trim();
            Log.v(TAG, "action=" + action);
            if (action.equals(ACT_WEBVIEW)) {
                intent.setClassName(context, WebActivity.class.getName());
                intent.putExtra("title", pushMsgObj.getTitle());
                intent.putExtra("url", pushMsgObj.getUrl());
            } else if (action.equals(ACT_NEWS)) {
                intent.setClassName(context, WebActivity.class.getName());
                intent.putExtra("title", pushMsgObj.getTitle());
                intent.putExtra("url", pushMsgObj.getUrl());
            } else if (action.equals(ACT_MAIN)) {
                intent.setClassName(context, clsNameDefault);
            } else {
                intent.setClassName(context, clsNameDefault);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    /**
     * 发送通知栏消息(行情提醒)
     *
     * @param context
     * @param title
     * @param content
     * @param res
     */
    public static void sendDefaultNotice(Context context, String title, String content, @DrawableRes int res, PushExtraObj productNoticeNotify) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setOngoing(false);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);//点击消失
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);//默认的声音和震动
        Notification notification = builder
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(content)
                .setSmallIcon(R.drawable.app_icon)
                .setSmallIcon(R.drawable.app_icon)
                .build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("productNoticeNotify", productNoticeNotify);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 指定内容意图
        notification.contentIntent = contentIntent;
        manager.notify(productNoticeNotify.getPid().hashCode(), notification);
    }

    /**
     * 发送通知栏消息(平仓提醒)
     *
     * @param context
     * @param title
     * @param content
     * @param res
     */
    public static void sendDefaultNotice4ClosrOrder(Context context, String title, String content, @DrawableRes int res, PushExtraObj closeOrderNotify) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setOngoing(false);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);//点击消失
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);//默认的声音和震动
        Notification notification = builder
                .setContentTitle(context.getResources().getString(R.string.app_name) + title)
                .setContentText(context.getResources().getString(R.string.trade_order_notification, content, closeOrderNotify.getClosePrice(), closeOrderNotify.getProfitLoss()))
                .setSmallIcon(R.drawable.app_icon)
                .setSmallIcon(R.drawable.app_icon)
                .build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("closeOrderNotify", closeOrderNotify);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 指定内容意图
        notification.contentIntent = contentIntent;
        manager.notify(closeOrderNotify.getOrderId().hashCode(), notification);
    }

    /**
     * 发送通知栏消息(爆仓警告提醒)
     *
     * @param context
     * @param title
     * @param content
     * @param res
     */
    public static void sendDefaultNotice4BCJG(Context context, String title, String content, @DrawableRes int res, PushMsgObj pushMsgObj) {
        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setOngoing(false);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);//点击消失
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);//默认的声音和震动
        Notification notification = builder
                .setContentTitle(context.getResources().getString(R.string.app_name) + title)
                .setContentText(content)
                .setSmallIcon(R.drawable.app_icon)
                .setSmallIcon(R.drawable.app_icon)
                .build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("bcjg", pushMsgObj);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 指定内容意图
        notification.contentIntent = contentIntent;
        manager.notify(3, notification);*/

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setOngoing(false);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);//点击消失
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);//默认的声音和震动
        Notification notification = builder
                .setContentTitle(context.getResources().getString(R.string.app_name) + title)
                .setContentText(content)
                .setSmallIcon(R.drawable.app_icon)
                .setSmallIcon(R.drawable.app_icon)
                .build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("bcjg", pushMsgObj);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 指定内容意图
        notification.contentIntent = contentIntent;
        manager.notify(new String("14969897325549801518").hashCode(), notification);
    }

    /**
     * 发送通知栏消息(出金提醒)
     *
     * @param context
     * @param title
     * @param content
     * @param res
     */
    public static void sendDefaultNotice4CashOut(Context context, String title, String content, @DrawableRes int res, PushMsgObj pushMsgObj) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setOngoing(false);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);//点击消失
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);//默认的声音和震动
        Notification notification = builder
                .setContentTitle(context.getResources().getString(R.string.app_name) + title)
                .setContentText(content)
                .setSmallIcon(R.drawable.app_icon)
                .setSmallIcon(R.drawable.app_icon)
                .build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("cashout", pushMsgObj);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 指定内容意图
        notification.contentIntent = contentIntent;
        manager.notify(pushMsgObj.getData().getLogNo().hashCode(), notification);
    }

    /**
     * 发送通知栏消息(出金提醒)
     *
     * @param context
     * @param res
     */
    public static void sendDefaultNotice4OrderNotify(Context context, @DrawableRes int res, PushExtraObj pushExtraObj) {

        String displaytitle = "";
        String content = "";

        String title = "";
        String buyType = "";

        switch (pushExtraObj.getType()) {
            case ProductObj.TYPE_BUY_UP:
                buyType = context.getResources().getString(R.string.trade_buy_up);
                break;
            case ProductObj.TYPE_BUY_DOWN:
                buyType = context.getResources().getString(R.string.trade_buy_down);
                break;
        }
        switch (pushExtraObj.getOrderType()) {
            case 0:
                title = context.getResources().getString(R.string.lable_create);
                break;
            case 1:
                title = context.getResources().getString(R.string.trade_close);
                break;

        }
        switch (pushExtraObj.getStatus()) {
            case 1:
                displaytitle = context.getResources().getString(R.string.ordernotify_1, title);
                content = context.getResources().getString(R.string.ordernotify_6, pushExtraObj.getInstrumentName(), buyType, pushExtraObj.getAllCount());
                break;
            case 2:
                displaytitle = context.getResources().getString(R.string.ordernotify_3, title);
                if (pushExtraObj.getOrderType() == 0) {
                    content = context.getResources().getString(R.string.ordernotify_4, pushExtraObj.getInstrumentName(), buyType, pushExtraObj.getSuccessCount(), pushExtraObj.getFailCount());
                } else {
                    content = context.getResources().getString(R.string.ordernotify_5, pushExtraObj.getInstrumentName(), buyType, pushExtraObj.getSuccessCount(), pushExtraObj.getFailCount());
                }
                break;
            case 3:
                displaytitle = context.getResources().getString(R.string.ordernotify_2, title);
                content = context.getResources().getString(R.string.ordernotify_6, pushExtraObj.getInstrumentName(), buyType, pushExtraObj.getAllCount());
                break;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setOngoing(false);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);//点击消失
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);//默认的声音和震动
        Notification notification = builder
                .setContentTitle(displaytitle)
                .setContentText(content)
                .setSmallIcon(R.drawable.app_icon)
                .setSmallIcon(R.drawable.app_icon)
                .build();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("orderNotify", OrderNotifyData.pushExtraObjToData(pushExtraObj));
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 指定内容意图
        notification.contentIntent = contentIntent;
        manager.notify(pushExtraObj.getAllCount(), notification);
    }
}
