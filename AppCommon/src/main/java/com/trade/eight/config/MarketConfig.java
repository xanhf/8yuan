package com.trade.eight.config;

import android.content.Context;

import com.easylife.ten.lib.R;

/**
 * Created by dufangzhu on 2017/4/19.
 * 市场配置代码
 */

public class MarketConfig {

    /**
     * 当前市场是否使用导航页面
     * 目前小米  samsung
     *
     * @param context
     * @return 默认使用导航页
     */
    public static boolean isMktUseNav(Context context) {
        String mktCode = AppSetting.getInstance(context).getAppMarket();
        if (mktCode == null)
            return true;
        if (mktCode.contains(context.getResources().getString(R.string.market_xiaomi))) {
            return false;
        }
        if (mktCode.contains(context.getResources().getString(R.string.market_samsung))) {
            return false;
        }
        return true;
    }
}
