package com.trade.eight.entity.trade;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/5/25.
 */

public class TradeOrderAndUserInfoData extends TradeInfoData{
    private List<TradeOrder> orders;

    public List<TradeOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<TradeOrder> orders) {
        this.orders = orders;
    }
}
