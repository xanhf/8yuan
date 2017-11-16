package com.trade.eight.moudle.chatroom;

import com.trade.eight.entity.live.LiveRoomNew;

/**
 * 作者：Created by ocean
 * 时间：on 16/12/29.
 * 直播室点击事件传递
 */

public class IndexLiveItemClickEvent {
    public LiveRoomNew liveRoomNew;

    public IndexLiveItemClickEvent(LiveRoomNew liveRoomNew) {
        this.liveRoomNew = liveRoomNew;
    }
}
