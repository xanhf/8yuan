package com.trade.eight.tools.trade;

import com.trade.eight.entity.Optional;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.moudle.trade.TradeOrderOptionEvent;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by fangzhu
 * 下单弹窗
 */
public abstract class TradeCreateUtil {
    //自己内部刷新
    boolean isRefreshSelf = false;
    public abstract boolean isShowingDialog();
    public abstract void showDialog(int aniStyle);
    public abstract void updatePrice(Optional o);
    public abstract void updatePrice(List<List<TradeProduct>> list);
    public abstract void setGobackViewVisible(boolean isVisible);//是否显示返回键
    public abstract void isGotoTradeOrder(boolean isGotoTradeOrder);//是否展示查看持仓dialog

    public boolean isRefreshSelf() {
        return isRefreshSelf;
    }

    public void setRefreshSelf(boolean refreshSelf) {
        isRefreshSelf = refreshSelf;
    }

    /**
     * 建仓成功
     * 1  平仓按钮的状态显示
     * 2  绘制持仓辅助线
     */
    public void createOrderSuccess(){
        EventBus.getDefault().post(new TradeOrderOptionEvent(TradeOrderOptionEvent.OPTION_CREATESUCCESS));
    }
}
