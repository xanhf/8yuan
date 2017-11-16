package com.trade.eight.entity.integral;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/30.
 * 积分商城商品------(特权卡等)
 */

public class GoodsActGiftData implements Serializable {
    //购买状态：0=未购买 1=已购买 2=已抢光
    public static final int BUYSTATUS_NO = 0;
    public static final int BUYSTATUS_YES = 1;
    public static final int BUYSTATUS_EMPTY = 2;

    private long giftId	;//	商品ID
    private String giftName	;//	商品名称
    private int giftType	;//	商品类型：1=代金券，2=直播室礼物，3=特权卡
    private String giftPic	;//	商品图片
    private String giftSmallPic	;//	商品图片小尺寸
    private int moneys	;//	商品面额（元）
    private int excode;//	交易所：1=广贵所，2=哈贵所，3=农交所，4=华凝所
    private int poins	;//	商品所需积分
    private int takeNum	;//	已兑换的人数
    private int giftLimitNum	;//	商品剩余数量
    private int buyStatus	;//	购买状态：0=未购买 1=已购买 2=已抢光
    private String btnTextColor	;//	文字颜色
    private String subTextColorOpacity	;//	透明度

    // 建仓面板冗余字段
    private long giftStartTime	;//	特权卡有效期开始时间
    private long giftEndTime	;//	特权卡有效期结束时间
    private long currTime	;//	系统当前时间
    private String giftOrderRemark;//特权卡备注说明

    public String getBtnTextColor() {
        return btnTextColor;
    }

    public void setBtnTextColor(String btnTextColor) {
        this.btnTextColor = btnTextColor;
    }

    public String getSubTextColorOpacity() {
        return subTextColorOpacity;
    }

    public void setSubTextColorOpacity(String subTextColorOpacity) {
        this.subTextColorOpacity = subTextColorOpacity;
    }

    public String getGiftOrderRemark() {
        return giftOrderRemark;
    }

    public void setGiftOrderRemark(String giftOrderRemark) {
        this.giftOrderRemark = giftOrderRemark;
    }

    public long getGiftStartTime() {
        return giftStartTime;
    }

    public void setGiftStartTime(long giftStartTime) {
        this.giftStartTime = giftStartTime;
    }

    public long getGiftEndTime() {
        return giftEndTime;
    }

    public void setGiftEndTime(long giftEndTime) {
        this.giftEndTime = giftEndTime;
    }

    public long getCurrTime() {
        return currTime;
    }

    public void setCurrTime(long currTime) {
        this.currTime = currTime;
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

    public String getGiftSmallPic() {
        return giftSmallPic;
    }

    public void setGiftSmallPic(String giftSmallPic) {
        this.giftSmallPic = giftSmallPic;
    }

    public int getMoneys() {
        return moneys;
    }

    public void setMoneys(int moneys) {
        this.moneys = moneys;
    }

    public int getExcode() {
        return excode;
    }

    public void setExcode(int excode) {
        this.excode = excode;
    }

    public int getPoins() {
        return poins;
    }

    public void setPoins(int poins) {
        this.poins = poins;
    }

    public int getTakeNum() {
        return takeNum;
    }

    public void setTakeNum(int takeNum) {
        this.takeNum = takeNum;
    }

    public int getGiftLimitNum() {
        return giftLimitNum;
    }

    public void setGiftLimitNum(int giftLimitNum) {
        this.giftLimitNum = giftLimitNum;
    }

    public int getBuyStatus() {
        return buyStatus;
    }

    public void setBuyStatus(int buyStatus) {
        this.buyStatus = buyStatus;
    }
}
