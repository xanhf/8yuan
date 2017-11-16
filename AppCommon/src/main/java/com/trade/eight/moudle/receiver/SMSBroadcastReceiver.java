package com.trade.eight.moudle.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

/**
 * Created by fangzhu on 2015/5/26.
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {

    private static MessageListener mMessageListener;
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    public SMSBroadcastReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
                Object[] pdus = (Object[]) intent.getExtras().get("pdus");
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
//                    String sender = smsMessage.getDisplayOriginatingAddress();
                    //短信内容
                    String content = smsMessage.getDisplayMessageBody();
//                    long date = smsMessage.getTimestampMillis();

                    mMessageListener.onReceived(content);
                    abortBroadcast();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //回调接口
    public interface MessageListener {
        public void onReceived(String message);
    }

    public void setOnReceivedMessageListener(MessageListener messageListener) {
        this.mMessageListener = messageListener;
    }
}
