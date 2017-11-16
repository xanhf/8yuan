package com.trade.eight.moudle.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;

import com.easylife.ten.lib.R;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.WeipanMsg;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.moudle.me.activity.WeipanMsgListActivity;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.WeiPanPushUtils;

/**
 * Created by Administrator on 2015/9/9.
 */
public class AlarmReceiver extends BroadcastReceiver {
    String TAG = "AlarmReceiver";
    public static final String ACTION_WEIPAN_MSG = "weipan_msg";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive");
        if (ACTION_WEIPAN_MSG.equals(intent.getAction())) {

            getResult(context, intent);
        }
    }

    void getResult (final Context context, final Intent intent) {

        UserInfoDao userInfoDao = new UserInfoDao(context);
        if (!userInfoDao.isLogin()) {
            return;
        }
        new AsyncTask<Void, Void, CommonResponse4List<WeipanMsg>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected CommonResponse4List<WeipanMsg> doInBackground(Void... params) {
                return WeiPanPushUtils.getLastestMsgList(context);
            }

            @Override
            protected void onPostExecute(CommonResponse4List<WeipanMsg> list) {
                super.onPostExecute(list);
                if (list != null && list.isSuccess()) {
                    WeipanMsg msg = null;
                    if (list.getData() != null && list.getData().size() > 0) {
                        msg = list.getData().get(0);
                        WeiPanPushUtils.setLocalLatestMsgId(context, msg.getMnId());
                        WeiPanPushUtils.setShowIconNew(context, true);
                        context.sendBroadcast(new Intent(WeiPanPushUtils.ICON_RECEIVED_ACTION));

                        //进入消息列表
                        Intent intent = new Intent(context, WeipanMsgListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        showNot(context, intent, "最新消息", msg.getMessage(), (int) msg.getMnId());
                    }
                }
            }
        }.execute();
    }

    private static void showNot(Context context, Intent intent, String mTitle, String mMessage, int id) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//

        String title = ConvertUtil.NVL(mTitle, "");
        String text = ConvertUtil.NVL(mMessage, "");

        title = StringUtil.moveCenterTag(title);
        title = Html.fromHtml(title).toString();

        text = StringUtil.moveCenterTag(text);
        text = Html.fromHtml(text).toString();

        PendingIntent pendingIntent = PendingIntent.getActivity(context, mTitle.hashCode(), intent
                , PendingIntent.FLAG_UPDATE_CURRENT);

        //不同版本的写法   http://www.cnblogs.com/Arture/p/5523695.html
        //考虑到云信的sdk build 是23  没有2.3的方法了， 这里直接编译的sdk 大于11 显示通知，umeng后台还有2.3的用户
        if (Build.VERSION.SDK_INT < 11) {
            return;
        }
        //高于API Level 11，低于API Level 16 (Android 4.1.2)版本
        Notification.Builder builder = new Notification.Builder(context)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.app_icon)
                .setWhen(System.currentTimeMillis())
                .setOngoing(false);//为ture时，不可以被侧滑消失。
//        直接可以builder设置 notification.defaults notification.flags
//        builder.setDefaults(Notification.DEFAULT_SOUND);
        Notification notification = builder.getNotification();

        //低于api 11的写法，
//        Notification notification = new Notification(R.drawable.app_icon,
//                title, System.currentTimeMillis());
//        notification.setLatestEventInfo(context,
//                title, text,
//                pendingIntent);

        //设置参数信息
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.priority = Notification.PRIORITY_MAX;

        //false 不覆盖  notify id 唯一
        mNotificationManager.notify(id, notification);

    }
}
