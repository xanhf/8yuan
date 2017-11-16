package com.trade.eight.entity.integral;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/31.
 * 礼物详情
 */

public class GoodsActGiftDetailData implements Serializable{

    private String buyStatus;// 0,
    private String currTime;// 1490931040222,
    private String giftAuthDesc;// "权限说明：r&n1.19999积分（积分可更改）兑换一次，不同等级享受相应折扣；r&n2.满足条件后，可参与抢购。",
    private String giftBigPic;// 大图片
    private String giftDesc;// 有效期等说明
    private String giftId;// 12,
    private String giftLimitNum;// 4,
    private String giftName;// "特权-亏损包赔",
    private String giftRemark;// "注：每人只能抢购1种特权卡",
    private String giftRuleDesc;// 规则
    private String takeNum;// 0

    // 兑换历史页面冗余参数
    private int levelNum	;//	兑换时用户等级
    private String levelName;//	兑换时用户等级
    private String rebateRate	;//	兑换折扣(等于1时表示无折扣)
    private int giftPoins	;//	商品所需积分（折扣前积分）

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    public String getRebateRate() {
        if(TextUtils.isEmpty(rebateRate)){
            return "1";
        }
        return rebateRate;
    }

    public void setRebateRate(String rebateRate) {
        this.rebateRate = rebateRate;
    }

    public int getGiftPoins() {
        return giftPoins;
    }

    public void setGiftPoins(int giftPoins) {
        this.giftPoins = giftPoins;
    }

    public String getBuyStatus() {
        return buyStatus;
    }

    public void setBuyStatus(String buyStatus) {
        this.buyStatus = buyStatus;
    }

    public String getCurrTime() {
        return currTime;
    }

    public void setCurrTime(String currTime) {
        this.currTime = currTime;
    }

    public String getGiftAuthDesc() {
        return giftAuthDesc;
    }

    public void setGiftAuthDesc(String giftAuthDesc) {
        this.giftAuthDesc = giftAuthDesc;
    }

    public String getGiftBigPic() {
        return giftBigPic;
    }

    public void setGiftBigPic(String giftBigPic) {
        this.giftBigPic = giftBigPic;
    }

    public String getGiftDesc() {
        return giftDesc;
    }

    public void setGiftDesc(String giftDesc) {
        this.giftDesc = giftDesc;
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getGiftLimitNum() {
        return giftLimitNum;
    }

    public void setGiftLimitNum(String giftLimitNum) {
        this.giftLimitNum = giftLimitNum;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getGiftRemark() {
        return giftRemark;
    }

    public void setGiftRemark(String giftRemark) {
        this.giftRemark = giftRemark;
    }

    public String getGiftRuleDesc() {
        return giftRuleDesc;
    }

    public void setGiftRuleDesc(String giftRuleDesc) {
        this.giftRuleDesc = giftRuleDesc;
    }

    public String getTakeNum() {
        return takeNum;
    }

    public void setTakeNum(String takeNum) {
        this.takeNum = takeNum;
    }
}
