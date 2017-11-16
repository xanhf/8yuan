package com.trade.eight.entity.sharejoin;

import java.io.Serializable;

/**
 * Created by fangzhu on 2017/1/20.
 */

public class ShJoinObj implements Serializable {
    //type字段的 活动入口  1、首页banner 2、我的页面 3、积分商城
    public static final int TYPE_PAGE_HOME_BANNER = 1;
    public static final int TYPE_PAGE_ME = 2;
    public static final int TYPE_PAGE_INTE = 3;

    //活动状态开启
    public static final int STATUS_ON = 1;
    //活动状态关闭
    public static final int STATUS_OFF = 0;

    /**
     * link : http://t.w.8caopan.com/activity/hyyq/?userId=8&sourceId=10
     * linkTitle : 邀请好友
     * pic : http://t.m.8caopan.com/static/images/hyyq.png
     * status : 1
     * subject : 800万现金,送完为止
     * title : 邀请好友赚积分
     */

    private String link;
    private String linkTitle;
    private String pic;
    private int status;
    private String subject;
    private String title;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
