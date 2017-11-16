package com.trade.eight.entity.trade;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 作者：Created by changhaiyang
 * 时间：on 16/9/26.
 */

public class TradeRedPacket implements Serializable{
    private double amount;//红包总额
    private double balance;//红包余额
    private String couponValidDate;//红包有效期，格式yyyy-MM-dd
    private String lastIn;//最后收入红包，根据这个来判断要不要飘
    private ArrayList<TradeRedPacketDetail> flows;//流水列表

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCouponValidDate() {
        return couponValidDate;
    }

    public void setCouponValidDate(String couponValidDate) {
        this.couponValidDate = couponValidDate;
    }

    public String getLastIn() {
        return lastIn;
    }

    public void setLastIn(String lastIn) {
        this.lastIn = lastIn;
    }

    public ArrayList<TradeRedPacketDetail> getFlows() {
        return flows;
    }

    public void setFlows(ArrayList<TradeRedPacketDetail> flows) {
        this.flows = flows;
    }
}
