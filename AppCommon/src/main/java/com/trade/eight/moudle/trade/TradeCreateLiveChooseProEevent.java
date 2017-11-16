package com.trade.eight.moudle.trade;

import com.trade.eight.moudle.home.trade.ProductObj;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/19.
 * 直播室建仓面板  选择产品
 */

public class TradeCreateLiveChooseProEevent {
    public  ProductObj productObj;
    public int position;

    public TradeCreateLiveChooseProEevent(ProductObj productObj,int position) {
        this.productObj = productObj;
        this.position = position;
    }
}
