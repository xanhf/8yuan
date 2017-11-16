package com.trade.eight.entity.trade;

import com.trade.eight.entity.jdpay.RechargeType;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 16/12/16.
 */

public class TradeRechargeTypeAndMoneyData implements Serializable{
    // 支持的充值类型
    List<RechargeType> rechargeType;
    // 充值金额
    List<String> rechargeNumber;

    public List<RechargeType> getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(List<RechargeType> rechargeType) {
        this.rechargeType = rechargeType;
    }

    public List<String> getRechargeNumber() {
        return rechargeNumber;
    }

    public void setRechargeNumber(List<String> rechargeNumber) {
        this.rechargeNumber = rechargeNumber;
    }


}
