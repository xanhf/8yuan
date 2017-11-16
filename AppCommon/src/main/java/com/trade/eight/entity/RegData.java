package com.trade.eight.entity;

import java.io.Serializable;

/**
 * Created by developer
 * 注册成功 的data解析数据
 * 与交易所那边 callback 的redirectUrl
 * 充值  登录 注册
 */
public class RegData implements Serializable {
    public static final int TYPE_REG = 1;
    public static final int TYPE_LOGIN = 2;
    private String url;
    private String redirectUrl;
    private String echangeId;
    private String orderId;// 农交所微信支付 订单号
    private String payUrl;// 农交所微信支付 跳转url
    private String payPage;// 农交所微信支付 跳转网页url
    private String services;//支持的支付类型，多个以"|"连接（app支付）
    private String tokenId;//支付授权码（app支付）

    //2017-02-20
    private String appId;// 微信app支付的appId从后台获取

    private int cashInType = 0;// 避免银联网页支付拦截微信支付
    public static final int CASHIN_WEIXIN_JN = 1;
    public static final int CASHIN_WEIXIN_HG = 2;
    //    1＝注册、2=登录  当前为微信支付时  1=现在支付、2=点芯支付 (ps:支付方式（4：wap支付，5：app支付）)
    private int type;
    private String typeName;
    private String id;
    private String scheme;// 微信支付scheme  只针对哈贵

    // fxbtg充值
    private String rechargeUrl;
    private String callBackUrl;

    public String getRechargeUrl() {
        return rechargeUrl;
    }

    public void setRechargeUrl(String rechargeUrl) {
        this.rechargeUrl = rechargeUrl;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public void setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public int getCashInType() {
        return cashInType;
    }

    public void setCashInType(int cashInType) {
        this.cashInType = cashInType;
    }

    public String getPayPage() {
        return payPage;
    }

    public void setPayPage(String payPage) {
        this.payPage = payPage;
    }

    /**
     * {
     * "success": true,
     * "errorCode": "",
     * "errorInfo": "",
     * "pagerManager": null,
     * "data": {
     * "isReg": false
     * }
     * }
     * success 只是接口返回成功
     * 农交所需
     */

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    private boolean isReg;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }


    public String getEchangeId() {
        return echangeId;
    }

    public void setEchangeId(String echangeId) {
        this.echangeId = echangeId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isReg() {
        return isReg;
    }

    public void setReg(boolean reg) {
        isReg = reg;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
