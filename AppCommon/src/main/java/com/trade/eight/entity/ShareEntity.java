package com.trade.eight.entity;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by fangzhu on 16/4/4.
 */
public class ShareEntity implements Serializable {

    /**
     * content : 一个人赚钱好孤单，送你红包，快来和我一起嗨！
     * pic : http://t.m.8caopan.com/static/images/invter_logo.png
     * title : 一个人赚钱好孤单，送你红包，快来和我一起嗨！
     * url : https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxad256bd251e77b0e&redirect_uri=http://t.webchat.8caopan.com/lqhb/?inviterId=8&sourceId=10&response_type=code&scope=snsapi_userinfo&state=1#wechat_redirect
     */

    /**
     * shareUrl: "http://www.8caopan.com/share/index.html?activityCode=6129",
     * content: "注册送24元操盘本金，国家电子商务试点，8元起投，盈利可提现。"
     */

    private String content;
    private String pic;
    private String title;
    private String url;// (分享链接)此字段在分享赚积分活动版本添加
    private String shareUrl;//(分享链接)之前版本所需 ps:容错机制,在getUrl做判断返回
    private String callback;// 分享成功回调

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        if(TextUtils.isEmpty(url)){// 容错机制
            return shareUrl;
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
