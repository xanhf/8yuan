package com.trade.eight.moudle.push.entity;

import java.io.Serializable;

/**
 * Created by fangzhu on 2017/1/11.
 * 推送接收到的实体对象
 */
public class PushMsgObj implements Serializable {
    /**
     * action : webView
     * body : eeeeeeee
     * sendType : 2
     * title : eeeeeeee
     * type : 0
     * url : http://t.w.8caopan.com/static/html/61.html?articleId=21&sourceId=10&informationId=61&deviceType=1
     */

    private String action;
    private String body;
    //    // 1:系统消息,2:行情提醒,3:直播室公告提醒,4:系统平仓提醒
    private int sendType;
    private String title;
    //    //推送消息类型 对应PushShowMEnum  ALL(0,"所有客户端"),IOS(1,"IOS"),ANDROID(2,"ANDROID");
    private int type;
    private String url;
    private PushExtraObj data;//额外json

    public static final int SYSTEM_NOTICE = 1;// 系统消息
    public static final int PRODUCT_NOTICE = 2;//行情提醒
    public static final int CHATROOM_NOTICE = 3;//聊天室公告提醒
    public static final int CLOSEORDER_NOTICE = 4;//系统平仓提醒
    public static final int AUTH_NOTICE = 5;//实名认证
    public static final int CASHOUT_NOTICE = 6;//出金提醒
    public static final int ACCOUNTFENGXIAN_NOTICE = 7;//账户风险提示(半小时只推送一个次)
    // 期货所属
    public static final int ORDERNOTIFY_NOTICE = 5;//订单反馈

    /**
     * 额外的json对象
     */
//    private PushExtraObj obj;
    public PushExtraObj getData() {
        return data;
    }

    public void setData(PushExtraObj data) {
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getSendType() {
        return sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
