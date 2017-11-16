package com.trade.eight.entity;

import java.io.Serializable;

/**
 * Created by fangzhu on 16/4/22.
 */
public class RankItem implements Serializable {

    /**
     * nickName : 好运连连
     * orderId : 3141435
     * profitRate : 50.00%
     * giveVoucher : 168
     * closeDate : 2016-05-05
     * status : 1
     * <p/>
     * status
     * 1＝参与成功
     * 2=已发券
     */

    private String nickName;
    private int orderId;
    private int exchangeId;
    private String profitRate;
    private String giveVoucher;
    private String closeDate;
    private int status;
    private int index;//当日的排名  需要自己算出来
    private String avatar;
    private String uod;//  确定是否高亮

    public String getUod() {
        return uod;
    }

    public void setUod(String uod) {
        this.uod = uod;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(String profitRate) {
        this.profitRate = profitRate;
    }

    public String getGiveVoucher() {
        return giveVoucher;
    }

    public void setGiveVoucher(String giveVoucher) {
        this.giveVoucher = giveVoucher;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(int exchangeId) {
        this.exchangeId = exchangeId;
    }
}
