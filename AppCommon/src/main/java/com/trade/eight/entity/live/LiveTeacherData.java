package com.trade.eight.entity.live;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 16/12/29.
 */

public class LiveTeacherData implements Serializable {
    private String authorAvatar;// "http;////t.m.8caopan.com/images/appContent/author/0/0/11/20160401135003622.png",
    private String authorName;// "分析师;//一休老师(更多策略和互动可进直播室)",
    private String endTime;// "23;//00",
    private String introduction;// "精准 | 幽默 | 专注贵金属投资经验",
    private String liveDescription;// "视频（文字）直播",
    private String liveTime;// "18;//00~23;//00",
    private String professionalTitle;// "高级分析师",
    private String startTime;// "18;//00",
    private String uuid;// "6b244886-9398-449d-866f-7b2a43018ff6"
    private int status;//阶段直播状态（0，未直播，1直播中，2完成直播）
    private String label;//阶段直播标签（多个用逗号分隔）

    //分析师详细数据
    private long createTime;// 1490856643000,
    private int giftCount;// 1,
    private int integralCount;// 88,
    private String integralCountSimpleName;// "0.01万",
    private int liveTimeCount;// 0,
    private String liveTimeCountSimpleName;// "0万",
    private int reminderCount;// 0,
    private String reminderCountSimpleName;// "0万",
    private int watchCount;// 0,
    private String watchCountSimpleName;// "0.01万"

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getGiftCount() {
        return giftCount;
    }

    public void setGiftCount(int giftCount) {
        this.giftCount = giftCount;
    }

    public int getIntegralCount() {
        return integralCount;
    }

    public void setIntegralCount(int integralCount) {
        this.integralCount = integralCount;
    }

    public String getIntegralCountSimpleName() {
        return integralCountSimpleName;
    }

    public void setIntegralCountSimpleName(String integralCountSimpleName) {
        this.integralCountSimpleName = integralCountSimpleName;
    }

    public int getLiveTimeCount() {
        return liveTimeCount;
    }

    public void setLiveTimeCount(int liveTimeCount) {
        this.liveTimeCount = liveTimeCount;
    }

    public String getLiveTimeCountSimpleName() {
        return liveTimeCountSimpleName;
    }

    public void setLiveTimeCountSimpleName(String liveTimeCountSimpleName) {
        this.liveTimeCountSimpleName = liveTimeCountSimpleName;
    }

    public int getReminderCount() {
        return reminderCount;
    }

    public void setReminderCount(int reminderCount) {
        this.reminderCount = reminderCount;
    }

    public String getReminderCountSimpleName() {
        return reminderCountSimpleName;
    }

    public void setReminderCountSimpleName(String reminderCountSimpleName) {
        this.reminderCountSimpleName = reminderCountSimpleName;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    public String getWatchCountSimpleName() {
        return watchCountSimpleName;
    }

    public void setWatchCountSimpleName(String watchCountSimpleName) {
        this.watchCountSimpleName = watchCountSimpleName;
    }

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
