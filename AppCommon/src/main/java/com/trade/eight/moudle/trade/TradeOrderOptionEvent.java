package com.trade.eight.moudle.trade;

/**
 * 作者：Created by ocean
 * 时间：on 2017/2/9.
 * 建仓成功 平仓成功  主要用于产品详情页"平仓"按钮的显示以及持仓辅助线的绘制
 */

public class TradeOrderOptionEvent {
    public int option_type = -1;
    public static final int OPTION_CREATESUCCESS = 1;
    public static final int OPTION_CLOSESUCCESS = 2;

    public TradeOrderOptionEvent(int option_type) {
        this.option_type = option_type;
    }
}

