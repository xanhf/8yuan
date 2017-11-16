package com.trade.eight.moudle.chatroom.viewholder;

import android.view.View;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.trade.eight.moudle.chatroom.helper.ChatRoomNotificationHelper;

/**
 * 直播室通知消息显示内
 */
public class ChatRoomMsgViewHolderNotification extends MsgViewHolderBase {
    //是否显示system 通知
    boolean isShow = false;
    protected TextView notificationTextView;

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_notification;
    }

    @Override
    protected void inflateContentView() {
        notificationTextView = (TextView) view.findViewById(R.id.message_item_notification_label);

        if (!isShow) {
            View rootView = view.findViewById(R.id.rootView);
            if(rootView !=null) {
                rootView.setVisibility(View.GONE);
            }

        }
    }

    @Override
    protected void bindContentView() {
        notificationTextView.setText(ChatRoomNotificationHelper.getNotificationText((ChatRoomNotificationAttachment) message.getAttachment()));
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
    }
}

