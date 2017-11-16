package com.trade.eight.moudle.chatroom.viewholder;

import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderAudio;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;

/**
 * Created by zhoujianghua on 2015/8/5.
 */
public class ChatRoomMsgViewHolderAudio extends MsgViewHolderAudio {
    /**
     * 重写一下 显示昵称的方法
     */
    @Override
    public void setNameTextView() {
        nameContainer.setPadding(ScreenUtil.dip2px(6), 0, 0, ScreenUtil.dip2px(5));
        ChatRoomViewHolderHelper.setNameTextView((ChatRoomMessage) message, nameTextView, nameIconView, context);
    }

}
