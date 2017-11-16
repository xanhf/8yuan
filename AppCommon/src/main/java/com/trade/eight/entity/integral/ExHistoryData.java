package com.trade.eight.entity.integral;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 16/12/15.
 * 兑换历史
 */

public class ExHistoryData implements Serializable {
    private long giftId;//	商品ID
    private String giftName;//	商品名称
    private int giftType;//	商品类型：1=代金券
    private String giftPic;//	商品图片
    private String giftSmallPic;// 商品小图片
    private int giftNum;//	兑换的数量
    private int totalPoins;//	总共消费的积分
    private String createTimeStr;//	兑换时间
    private long historyRecId;//
    private String fullName;// 全程  不需要拼接文案
    private String specDesc;//实物兑换规格

    public String getSpecDesc() {
        return specDesc;
    }

    public void setSpecDesc(String specDesc) {
        this.specDesc = specDesc;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public long getHistoryRecId() {
        return historyRecId;
    }

    public void setHistoryRecId(long historyRecId) {
        this.historyRecId = historyRecId;
    }

    public String getGiftSmallPic() {
        return giftSmallPic;
    }

    public void setGiftSmallPic(String giftSmallPic) {
        this.giftSmallPic = giftSmallPic;
    }

    public long getGiftId() {
        return giftId;
    }

    public void setGiftId(long giftId) {
        this.giftId = giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public int getGiftType() {
        return giftType;
    }

    public void setGiftType(int giftType) {
        this.giftType = giftType;
    }

    public String getGiftPic() {
        return giftPic;
    }

    public void setGiftPic(String giftPic) {
        this.giftPic = giftPic;
    }

    public int getGiftNum() {
        return giftNum;
    }

    public void setGiftNum(int giftNum) {
        this.giftNum = giftNum;
    }

    public int getTotalPoins() {
        return totalPoins;
    }

    public void setTotalPoins(int totalPoins) {
        this.totalPoins = totalPoins;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }
}
