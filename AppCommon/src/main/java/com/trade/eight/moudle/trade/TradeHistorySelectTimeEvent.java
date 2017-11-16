package com.trade.eight.moudle.trade;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/28.
 * 交易记录时间选择
 */

public class TradeHistorySelectTimeEvent {
    public String startTime;
    public String endTime;

    public TradeHistorySelectTimeEvent(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
