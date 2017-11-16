package com.trade.eight.entity.integral;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 16/12/13.
 * 积分商品---代金券
 */

public class IntegralProductData implements Serializable {
    private long giftId;//商品ID
    private String giftName;//商品名称
    private String giftType;//商品类型：1=代金券
    private String giftPic;//商品图片
    private String moneys;//商品面额（元）
    private int excode;//交易所：1=广贵所，2=哈贵所，3=农交所，4=华凝所
    private int poins;//商品所需积分
    private int takeNum;//
    private String giftSmallPic;

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

    public String getGiftType() {
        return giftType;
    }

    public void setGiftType(String giftType) {
        this.giftType = giftType;
    }

    public String getGiftPic() {
        return giftPic;
    }

    public void setGiftPic(String giftPic) {
        this.giftPic = giftPic;
    }

    public String getMoneys() {
        return moneys;
    }

    public void setMoneys(String moneys) {
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
}
