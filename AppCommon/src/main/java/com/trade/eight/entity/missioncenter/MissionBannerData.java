package com.trade.eight.entity.missioncenter;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/3.
 * 任务中心  banner数据
 */

public class MissionBannerData implements Serializable{
    private String pic	;//	banner图片
    private String link	;//	点击banner跳转链接
    private String linkTitle	;//	点击banner跳转链接标题
    private int linkType;// banner 跳转类型

    public int getLinkType() {
        return linkType;
    }

    public void setLinkType(int linkType) {
        this.linkType = linkType;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

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
}
