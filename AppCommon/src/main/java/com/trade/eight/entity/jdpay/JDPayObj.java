package com.trade.eight.entity.jdpay;

import java.io.Serializable;

/**
 * Created by fangzhu on 2017/2/9.
 * 京东支付需要的字段
 */

public class JDPayObj implements Serializable {
    private String orderId;
    private String bankName;
    private String bankCard;


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }
}
