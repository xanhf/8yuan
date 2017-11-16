package com.trade.eight.moudle.trade;

import android.view.View;

import com.trade.eight.entity.trade.TradeOrder;

/**
 * 作者：Created by ocean
 * 时间：on 2017/2/9.
 * 平仓面板操作
 */

public class TradeCloseOptionEvent {
    public int option_type = -1;
    public static final int OPTION_EXPANDMENU = 1;
    public static final int OPTION_ADDORREDUCE = 2;
    public boolean isExpandMenu;
    public View optionView;// 被操作的view
    public TradeOrder optionTradeOrder;//被操作的订单
    public boolean isAddOrreduce;// 加还是减

    public TradeCloseOptionEvent(int option_type,boolean isExpandMenu,View optionView,TradeOrder optionTradeOrder) {
        this.option_type = option_type;
        this.isExpandMenu = isExpandMenu;
        this.optionView = optionView;
        this.optionTradeOrder = optionTradeOrder;
    }

    public TradeCloseOptionEvent(int option_type,boolean isAddOrreduce ){
        this.option_type = option_type;
        this.isAddOrreduce = isAddOrreduce;
    }
}

