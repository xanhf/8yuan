package com.trade.eight.moudle.trade.alipay;

import java.io.Serializable;

/**
 * Created by dufangzhu on 2017/3/29.
 * 支付宝支付需要的解析实体类
 */

public class AliPayObj implements Serializable {
    private String orderId;
    /*二维码图片地址*/
    private String codeImgUrl;
    /*支付网页地址，用webview或者默认浏览器打开*/
    private String codeUrl;

    public String getCodeUrl() {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCodeImgUrl() {
        return codeImgUrl;
    }

    public void setCodeImgUrl(String codeImgUrl) {
        this.codeImgUrl = codeImgUrl;
    }
}
