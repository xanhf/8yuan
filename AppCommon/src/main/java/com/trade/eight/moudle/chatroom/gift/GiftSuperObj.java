package com.trade.eight.moudle.chatroom.gift;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dufangzhu on 2017/4/5.
 * 直播室礼物接受的顶级对象
 */

public class GiftSuperObj implements Serializable {
    List<GiftObj> giftList;
    /*等级*/
    private int levelNum;
    /*折扣率*/
    private double rebateRate;
    /*可用积分*/
    private int validPoints;

    public List<GiftObj> getGiftList() {
        return giftList;
    }

    public void setGiftList(List<GiftObj> giftList) {
        this.giftList = giftList;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    public double getRebateRate() {
        return rebateRate;
    }

    public void setRebateRate(double rebateRate) {
        this.rebateRate = rebateRate;
    }

    public int getValidPoints() {
        return validPoints;
    }

    public void setValidPoints(int validPoints) {
        this.validPoints = validPoints;
    }
}
