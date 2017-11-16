package com.trade.eight.entity.integral;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/30.
 * 积分商城商品------活动(特权卡等)
 */

public class GoodsActData implements Serializable{
    //是否已兑换：0=否 ； 1=是
    public static final int CHANGESTATUS_NO =0;
    public static final int CHANGESTATUS_YES =1;

    private String actTitle	;//	活动标题
    private String actDesc	;//	活动说明
    private long actStartTime	;//	活动开始时间
    private long actEndTime	;//	活动结束时间
    private long currTime	;//	系统当前时间
    private int status	;//	是否已兑换：0=否 ； 1=是
    private int giftType	;//	商品类型：1=代金券，2=直播室礼物，3=特权卡
    private List<GoodsActGiftData> giftList;//特权卡等列表

    public String getActTitle() {
        return actTitle;
    }

    public void setActTitle(String actTitle) {
        this.actTitle = actTitle;
    }

    public String getActDesc() {
        return actDesc;
    }

    public void setActDesc(String actDesc) {
        this.actDesc = actDesc;
    }

    public long getActStartTime() {
        return actStartTime;
    }

    public void setActStartTime(long actStartTime) {
        this.actStartTime = actStartTime;
    }

    public long getActEndTime() {
        return actEndTime;
    }

    public void setActEndTime(long actEndTime) {
        this.actEndTime = actEndTime;
    }

    public long getCurrTime() {
        return currTime;
    }

    public void setCurrTime(long currTime) {
        this.currTime = currTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getGiftType() {
        return giftType;
    }

    public void setGiftType(int giftType) {
        this.giftType = giftType;
    }

    public List<GoodsActGiftData> getGiftList() {
        return giftList;
    }

    public void setGiftList(List<GoodsActGiftData> giftList) {
        this.giftList = giftList;
    }
}
