package com.trade.eight.entity;

import java.io.Serializable;

/**
 * Created by fangzhu on 16/6/26.
 * 广告
 */
public class AdsObj implements Serializable {


    /**
     * sourceId : 10
     * imageUrl : http://m.8caopan.com/images/startup/pic/10/20160626171947655.jpg
     * text :
     * link :
     * modular :
     */

    private int sourceId;
    private String imageUrl;
    private String text;
    private String link;
    private String modular;

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getModular() {
        return modular;
    }

    public void setModular(String modular) {
        this.modular = modular;
    }
}
