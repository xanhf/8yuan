package com.trade.eight.moudle.upgradeversion.entity;

import java.io.Serializable;

/**
 * 作者：Created by changhaiyang
 * 时间：on 16/9/20.
 * 版本更新
 */

public class UpgradeInfo implements Serializable {
    private String market;// 市场
    private String url;// 下载地址
    private String content;// 更新内容
    private String version;// 最新版本

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
