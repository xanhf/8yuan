package com.trade.eight.tools.trade;


import com.trade.eight.entity.trade.TradeProduct;

import java.util.List;

/**
 * Created by fangzhu 大部分代码来自TradeCreateUtil做了修改
 * 快速建仓 直播室里头
 */
public abstract class QuickTradeUtil {
    //自己内部刷新
    boolean isRefreshSelf = false;

    public abstract boolean isShowingDialog();
    public abstract void showDialog();

    public abstract void updatePrice(List<List<TradeProduct>> list);

    public abstract void dismissDlg();

    public boolean isRefreshSelf() {
        return isRefreshSelf;
    }

    public void setRefreshSelf(boolean refreshSelf) {
        isRefreshSelf = refreshSelf;
    }
}


