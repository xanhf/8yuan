package com.trade.eight.entity.live;


import java.io.Serializable;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 16/12/29.
 * 新版直播室列表
 */

public class LiveRoomNew implements Serializable{
    /**
     * NO_INPUT(0, "无输入流"),
     * LIVING(1, "直播中"),
     * EXCEPTION(2, "异常"),
     * CLOSED(3, "关闭");
     */
    public static final int STATUS_ON = 1;
    public static final int STATUS_OFF = 0;

    //    0 聊天室允许发图片;   1表示不允许
    public static final int sendPicStatus_ENABLE = 0;
    public static final int sendPicStatus_DIS_ENABLE = 1;

    //直播室类型（1.文字直播，2视频）
    public static final int CHANNELTYPE_WORD = 1;
    public static final int CHANNELTYPE_VIDEO = 2;

    //是否为付费观看的（1免费，2付费）
    public static final int ISPAY_NO = 1;
    public static final int ISPAY_YES = 2;

    //是否为隐藏（0不隐藏，1隐藏）
    public static final int ISHIDDEN_NO = 0;
    public static final int ISHIDDEN_YES = 1;


    //直播室状态 除了1为直播中 其他均为非直播状态
    public static final int CT_STATUS_NORESOURE = 0;// 为0为无流输入
    public static final int CT_STATUS_PALY = 1;// 为1为正在直播
    public static final int CT_STATUS_EXCEPTION = 2;// 异常
    public static final int CT_STATUS_CLOSED = 3;// 关闭

    private String activityName;// "EIA数据",
    private int authorId;
    private String authorName;
    private String channelDescribe;// "分析师直播室2",
    private String channelId;// "16093425727655221997",
    private String channelName;// "行情直播室",
    private int channelStatus;// 0,
    private String channelStatusName;// "",
    private int channelType;//直播室类型（1.文字直播，2视频）
    private String chatRoomId;// "55698",
    private String color;// "#FFFFFF",
    private int hidden;// 1,
    private String hlsDownstreamAddress;// "http;////2446.liveplay.myqcloud.com/2446_10228d103b8911e6a2cba4dcbef5e35a.m3u8",
    private String image;// "http;////t.m.8caopan.com/images/live/1482984735573.png",
    private int isPay;//是否为付费观看的（1免费，2付费）
    private String label;//标签
    private String liveTime;// "21;//00~01;//00",
    private List<LiveTeacherTimeData> liveTimeList;// [],
    private String onlineNumber;// 1389,
    private String onlineNumberSimpleName;// "1389",
    private String password;// "",
    private String rtmpDownstreamAddress;// "rtmp;////2446.liveplay.myqcloud.com/live/2446_10228d103b8911e6a2cba4dcbef5e35a",
    private String cardId;

    private LiveTeacherData segmentModel;// {},
    private int sendPicStatus;// 0 聊天室允许发图片;   1表示不允许

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getChannelDescribe() {
        return channelDescribe;
    }

    public void setChannelDescribe(String channelDescribe) {
        this.channelDescribe = channelDescribe;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getChannelStatus() {
        return channelStatus;
    }

    public void setChannelStatus(int channelStatus) {
        this.channelStatus = channelStatus;
    }

    public String getChannelStatusName() {
        return channelStatusName;
    }

    public void setChannelStatusName(String channelStatusName) {
        this.channelStatusName = channelStatusName;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getHidden() {
        return hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }

    public String getHlsDownstreamAddress() {
        return hlsDownstreamAddress;
    }

    public void setHlsDownstreamAddress(String hlsDownstreamAddress) {
        this.hlsDownstreamAddress = hlsDownstreamAddress;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(String liveTime) {
        this.liveTime = liveTime;
    }

    public List<LiveTeacherTimeData> getLiveTimeList() {
        return liveTimeList;
    }

    public void setLiveTimeList(List<LiveTeacherTimeData> liveTimeList) {
        this.liveTimeList = liveTimeList;
    }

    public String getOnlineNumber() {
        return onlineNumber;
    }

    public void setOnlineNumber(String onlineNumber) {
        this.onlineNumber = onlineNumber;
    }

    public String getOnlineNumberSimpleName() {
        return onlineNumberSimpleName;
    }

    public void setOnlineNumberSimpleName(String onlineNumberSimpleName) {
        this.onlineNumberSimpleName = onlineNumberSimpleName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRtmpDownstreamAddress() {
        return rtmpDownstreamAddress;
    }

    public void setRtmpDownstreamAddress(String rtmpDownstreamAddress) {
        this.rtmpDownstreamAddress = rtmpDownstreamAddress;
    }

    public LiveTeacherData getSegmentModel() {
        return segmentModel;
    }

    public void setSegmentModel(LiveTeacherData segmentModel) {
        this.segmentModel = segmentModel;
    }

    public int getSendPicStatus() {
        return sendPicStatus;
    }

    public void setSendPicStatus(int sendPicStatus) {
        this.sendPicStatus = sendPicStatus;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getChannelType() {
        return channelType;
    }

    public void setChannelType(int channelType) {
        this.channelType = channelType;
    }

    public int getIsPay() {
        return isPay;
    }

    public void setIsPay(int isPay) {
        this.isPay = isPay;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
