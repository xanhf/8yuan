package com.trade.eight.entity.startup;

import com.trade.eight.entity.AdsObj;
import com.trade.eight.entity.Exchange;
import com.trade.eight.entity.HomeActData;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fangzhu on 16/10/9.
 * 开启app的配置信息
 * 1、交易所列表
 * 2、首页热门产品
 * 3、tcp连接(首页弹窗广告，android应用推荐换量)
 * 4、app开启图片
 * 5、市场屏蔽开关
 */
public class StartupConfigObj implements Serializable {
    //交易所列表
    private List<Exchange> exchangeList;
    //首页热门产品
    private String productList;
    //tcp连接(首页弹窗广告，android应用推荐换量)
    private StartupObj config;
    //开启图片
    private AdsObj android_startup_image;
    //市场屏蔽开关
    private MarketVersionObj andriod_version;
    //首页活动
    private HomeActData indexActivity;

    public HomeActData getIndexActivity() {
        return indexActivity;
    }

    public void setIndexActivity(HomeActData indexActivity) {
        this.indexActivity = indexActivity;
    }

    public List<Exchange> getExchangeList() {
        return exchangeList;
    }

    public void setExchangeList(List<Exchange> exchangeList) {
        this.exchangeList = exchangeList;
    }

    public String getProductList() {
        return productList;
    }

    public void setProductList(String productList) {
        this.productList = productList;
    }

    public StartupObj getConfig() {
        return config;
    }

    public void setConfig(StartupObj config) {
        this.config = config;
    }

    public AdsObj getAndroid_startup_image() {
        return android_startup_image;
    }

    public void setAndroid_startup_image(AdsObj android_startup_image) {
        this.android_startup_image = android_startup_image;
    }

    public MarketVersionObj getAndriod_version() {
        return andriod_version;
    }

    public void setAndriod_version(MarketVersionObj andriod_version) {
        this.andriod_version = andriod_version;
    }
}
