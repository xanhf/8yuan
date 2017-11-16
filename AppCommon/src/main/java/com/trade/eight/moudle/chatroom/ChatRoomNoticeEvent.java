package com.trade.eight.moudle.chatroom;

import com.trade.eight.moudle.push.entity.PushExtraObj;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/21.
 * 聊天室公告提醒
 */

public class ChatRoomNoticeEvent {
    public PushExtraObj pushExtraObj;

    public ChatRoomNoticeEvent(PushExtraObj pushExtraObj) {
        this.pushExtraObj = pushExtraObj;
    }
}
