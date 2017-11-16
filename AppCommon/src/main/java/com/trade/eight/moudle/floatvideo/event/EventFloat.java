package com.trade.eight.moudle.floatvideo.event;

import com.trade.eight.entity.live.LiveRoomNew;

import java.io.Serializable;

import de.greenrobot.event.EventBus;

/**
 * Created by dufangzhu on 2017/3/14.
 * EventBus 通知act 显示视屏悬浮窗
 */

public class EventFloat implements Serializable {
    LiveRoomNew data;
    boolean hideFloat = false;

    public boolean isHideFloat() {
        return hideFloat;
    }

    public void setHideFloat(boolean hideFloat) {
        this.hideFloat = hideFloat;
    }

    public EventFloat(LiveRoomNew data) {
        this.data = data;
    }

    public LiveRoomNew getData() {
        return data;
    }

    public void setData(LiveRoomNew data) {
        this.data = data;
    }

    /**
     * 发送隐藏小窗口事件event
     */
    public static void postHideEvent() {
         /*退出隐藏视频*/
        EventFloat eventFloat = new EventFloat(null);
        eventFloat.setHideFloat(true);
        EventBus.getDefault().post(eventFloat);
    }
}
