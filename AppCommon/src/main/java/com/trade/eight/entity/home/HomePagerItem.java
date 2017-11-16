package com.trade.eight.entity.home;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/8/15.
 */
public class HomePagerItem implements Serializable {

    public static final String REQUEST_HOME = "1";// 请求类型(首页)
    public static final String REQUEST_LIVE = "2";// 请求类型(直播室)

    private long contentId;
    private String title;
    private String image_url;
    private String url;
    private String summary;
    private String add_time;

    public HomePagerItem() {

    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }
}
