package com.trade.eight.entity.trade;

import java.io.Serializable;

/**
 * 作者：Created by changhaiyang
 * 时间：on 16/10/10.
 * 提现手续费
 */

public class CashOutFee implements Serializable {
    private double fee;
    private String arrival;
    private String arrivalRule;// 到账规则
    private String feeRule;// 手续费规则

    public String getFeeRule() {
        return feeRule;
    }

    public void setFeeRule(String feeRule) {
        this.feeRule = feeRule;
    }

    public String getArrivalRule() {
        return arrivalRule;
    }

    public void setArrivalRule(String arrivalRule) {
        this.arrivalRule = arrivalRule;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}
