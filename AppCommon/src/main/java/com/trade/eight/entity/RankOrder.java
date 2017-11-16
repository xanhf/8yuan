package com.trade.eight.entity;

import java.io.Serializable;

/**
 * Created by fangzhu on 16/4/22.
 */
public class RankOrder implements Serializable {


    /**
     * productName : 油
     * closeTime : 2016-05-05 20:31:40
     * createTime : 2016-05-05 15:10:43
     * fee : 0.60
     * type : 买涨
     * orderNumber : 1
     * profitLoss : 4.02
     * createPrice : 2242.2
     * closePrice : 2282.4
     * stopProfit :
     * stopLoss :
     * closeType : 手动平仓
     * amount : 8.00
     * unit : 吨
     * weight : 0.1
     * avatar : http://m.8caopan.com/images/avatar/3/0/0/14/20160505195639759.jpg
     */

    private String productName;
    private String closeTime;
    private String createTime;
    private String fee;
    private String type;
    private String orderNumber;
    private String profitLoss;
    private String createPrice;
    private String closePrice;
    private String stopProfit;
    private String stopLoss;
    private String closeType;
    private String amount;
    private String unit;
    private String weight;
    private String avatar;
    private String productPrice;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(String profitLoss) {
        this.profitLoss = profitLoss;
    }

    public String getCreatePrice() {
        return createPrice;
    }

    public void setCreatePrice(String createPrice) {
        this.createPrice = createPrice;
    }

    public String getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(String closePrice) {
        this.closePrice = closePrice;
    }

    public String getStopProfit() {
        return stopProfit;
    }

    public void setStopProfit(String stopProfit) {
        this.stopProfit = stopProfit;
    }

    public String getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(String stopLoss) {
        this.stopLoss = stopLoss;
    }

    public String getCloseType() {
        return closeType;
    }

    public void setCloseType(String closeType) {
        this.closeType = closeType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}
