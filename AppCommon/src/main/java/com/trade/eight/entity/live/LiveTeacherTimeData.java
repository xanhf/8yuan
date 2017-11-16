package com.trade.eight.entity.live;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 16/12/29.
 */

public class LiveTeacherTimeData implements Serializable {

    public static final int STATUS_NOLIVE  =0;
    public static final int STATUS_INLIVE  =1;
    public static final int STATUS_COMPLETELIVE  =2;



    private String authorAvatar;// "http;////t.m.8caopan.com/images/appContent/author/0/0/19/20160415160330869.jpg",
    private String authorName;// "蓝翔老技师",
    private String endTime;// "18;//00",
    private String introduction;// "精准 | 人气 | 多年贵金属投资经验",
    private String liveDescription;// "视频(文字)直播",
    private String liveTime;// "13;//00~18;//00",
    private String professionalTitle;// "特邀分析师",
    private String startTime;// "13;//00",
    private String uuid;// "5f7afd40-9943-4220-88ca-1c5601c69ac0"
    private int status;//阶段直播状态（0，未直播，1直播中，2完成直播）
    private String label;//阶段直播标签（多个用逗号分隔）

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getLiveDescription() {
        return liveDescription;
    }

    public void setLiveDescription(String liveDescription) {
        this.liveDescription = liveDescription;
    }

    public String getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(String liveTime) {
        this.liveTime = liveTime;
    }

    public String getProfessionalTitle() {
        return professionalTitle;
    }

    public void setProfessionalTitle(String professionalTitle) {
        this.professionalTitle = professionalTitle;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
