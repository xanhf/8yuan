package com.trade.eight.moudle.trade;

import com.trade.eight.entity.trade.TradeInfoData;

/**
 * 作者：Created by ocean
 * 时间：on 2017/2/9.
 * 用户交易信息以及持仓单信息刷新
 */

public class UpdateTradeUserInfoEvent {

    public TradeInfoData tradeInfoData;
    public UpdateTradeUserInfoEvent(TradeInfoData tradeInfoData) {
        this.tradeInfoData = tradeInfoData;
    }
}

