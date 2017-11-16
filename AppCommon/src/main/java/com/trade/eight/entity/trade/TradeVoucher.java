package com.trade.eight.entity.trade;

import java.io.Serializable;

/**
 * Created by developer on 16/1/17.
 * 代金券
 */
public class TradeVoucher implements Serializable {
    //1=未使用，2=已使用
    public static final int TYPE_USED = 2;
    public static final int TYPE_NOT_USED = 1;

    private int type;
    private String amount;
    private String limitTime;
    //对应产品的代金券格式
    //data":[{"amount":200,"number":0}]
    private int number;

    //建仓页面查询当前交易所 持有的代金券
    //    {"exchangeId":1,"exchangeName":null,"number":9,"couponId":2,"price":"80元"}
    private int exchangeId;
    private String exchangeName;
    //    private int number;
    private int couponId;
    private String price;

    //农交所只有这一个字段，按照产品返回的
    private int count;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(String limitTime) {
        this.limitTime = limitTime;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(int exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
