package com.trade.eight.entity;

import java.io.Serializable;

/**
 * Created by fangzhu on 16/9/24.
 * 过夜费
 */
public class OrderDeferred implements Serializable{

    /**
     * amount : 0.5
     * date : 2016-09-24
     */
    private String amount;
    private String date;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
