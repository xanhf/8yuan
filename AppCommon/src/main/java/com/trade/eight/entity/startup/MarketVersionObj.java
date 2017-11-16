package com.trade.eight.entity.startup;

import java.io.Serializable;

/**
 * Created by dufangzhu on 2017/2/14.
 * 市场屏蔽开关
 */

public class MarketVersionObj implements Serializable {
    private String androidMarket;
    private boolean enable;
    private String version;

    public String getAndroidMarket() {
        return androidMarket;
    }

    public void setAndroidMarket(String androidMarket) {
        this.androidMarket = androidMarket;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


}
