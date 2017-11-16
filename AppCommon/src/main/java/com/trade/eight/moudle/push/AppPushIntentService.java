package com.trade.eight.moudle.push;

import android.content.Context;
import android.os.Message;

import com.google.gson.Gson;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;
import com.trade.eight.app.MyApplication;
import com.trade.eight.moudle.push.entity.PushMsgObj;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.StringUtil;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class AppPushIntentService extends GTIntentService {

    private static final String TAG = "AppPushIntentService";

    /**
     * 为了观察透传数据变化.
     */
    private static int cnt;

    public AppPushIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.v(TAG, "onReceiveServicePid -> " + pid);
    }

    /**
     * 注意这个方法 不是在ui线程中的
     * 如果使用toast，必须发送到ui中去再调用
     * 1、调用这个方法的时候，通知已经显示
     * 2、这个方法其实是点击了通知才调用的<发送通知>
     * 3、纯透传消息直接会进入到这个方法        EventBus.getDefault().register(this);

     *
     * @param context
     * @param msg     消息对象，额外的透传消息在msg.getPayload()中
     */
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        Log.v(TAG, "onReceiveMessageData");
        /**
         * 这里是在线程中使用的
         * 不能直接toast等ui操作
         */
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        Log.v(TAG, "cid===="+cid);

        try {
            //额外字段
            if (payload == null) {
                Log.e(TAG, "receiver payload = null");
                return;
            } else {
                String data = new String(payload);
                if (StringUtil.isEmpty(data))
                    return;
                Log.v(TAG, "receiver payload = " + data);

                PushMsgObj pushMsgObj = new Gson().fromJson(data, PushMsgObj.class);
                Log.v(TAG, "pushMsgObj action=" + ConvertUtil.NVL(pushMsgObj.getAction(), "is empty"));

                Message message = new Message();
                message.obj = pushMsgObj;
                MyApplication.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);

//        sendMessage(clientid, 1);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.v(TAG, "onReceiveOnlineState -> " + (online ? "online" : "offline"));
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.v(TAG, "onReceiveCommandResult -> " + cmdMessage);

        int action = cmdMessage.getAction();

        if (action == PushConsts.SET_TAG_RESULT) {
            setTagResult((SetTagCmdMessage) cmdMessage);
        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
            feedbackResult((FeedbackCmdMessage) cmdMessage);
        }
    }

    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
        String sn = setTagCmdMsg.getSn();
        String code = setTagCmdMsg.getCode();

        String text = "设置标签失败, 未知异常";
        switch (Integer.valueOf(code)) {
            case PushConsts.SETTAG_SUCCESS:
                text = "设置标签成功";
                break;

            case PushConsts.SETTAG_ERROR_COUNT:
                text = "设置标签失败, tag数量过大, 最大不能超过200个";
                break;

            case PushConsts.SETTAG_ERROR_FREQUENCY:
                text = "设置标签失败, 频率过快, 两次间隔应大于1s且一天只能成功调用一次";
                break;

            case PushConsts.SETTAG_ERROR_REPEAT:
                text = "设置标签失败, 标签重复";
                break;

            case PushConsts.SETTAG_ERROR_UNBIND:
                text = "设置标签失败, 服务未初始化成功";
                break;

            case PushConsts.SETTAG_ERROR_EXCEPTION:
                text = "设置标签失败, 未知异常";
                break;

            case PushConsts.SETTAG_ERROR_NULL:
                text = "设置标签失败, tag 为空";
                break;

            case PushConsts.SETTAG_NOTONLINE:
                text = "还未登陆成功";
                break;

            case PushConsts.SETTAG_IN_BLACKLIST:
                text = "该应用已经在黑名单中,请联系售后支持!";
                break;

            case PushConsts.SETTAG_NUM_EXCEED:
                text = "已存 tag 超过限制";
                break;

            default:
                break;
        }

        Log.v(TAG, "settag result sn = " + sn + ", code = " + code + ", text = " + text);
    }

    private void feedbackResult(FeedbackCmdMessage feedbackCmdMsg) {
        String appid = feedbackCmdMsg.getAppid();
        String taskid = feedbackCmdMsg.getTaskId();
        String actionid = feedbackCmdMsg.getActionId();
        String result = feedbackCmdMsg.getResult();
        long timestamp = feedbackCmdMsg.getTimeStamp();
        String cid = feedbackCmdMsg.getClientId();

        Log.v(TAG, "onReceiveCommandResult -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nactionid = " + actionid + "\nresult = " + result
                + "\ncid = " + cid + "\ntimestamp = " + timestamp);
    }

//    private void sendMessage(String data, int what) {
//        Message msg = Message.obtain();
//        msg.what = what;
//        msg.obj = data;
//        DemoApplication.sendMessage(msg);
//    }
}
