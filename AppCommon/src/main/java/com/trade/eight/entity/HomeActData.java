package com.trade.eight.entity;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 2017/4/12.
 * 首页活动
 */

public class HomeActData implements Serializable {
    private String bntName;// "",
    private String imgUrl;// "",
    private boolean show;// false,
    private String url;// ""

    public String getBntName() {
        return bntName;
    }

    public void setBntName(String bntName) {
        this.bntName = bntName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
