package com.trade.eight.entity.jdpay;

import java.io.Serializable;

/**
 * Created by fangzhu on 2017/2/10.
 * 充值方式
 *
 YINLIAN(1, "银联"),
 //2016-07-18
 WEI_XIN(2, "微信"),
 BEST_PAY(3, "翼支付"),
 DIAN_XIN(4, "点芯微信WAP"),
 DIAN_XIN_APP(5,"点芯微信APP");
 6 支付宝
 */

public class RechargeType implements Serializable {
    public static final int TYPE_YINLIAN = 1;
    //网页中的scheme   weixin://wap/pay?
    public static final int TYPE_WX_SCHEME = 2;
    //翼支付 和银联方式是一样的
    public static final int TYPE_YINLIAN_BEST_PAY = 3;
    //点芯微信WAP
    public static final int TYPE_WX_DIAN_XIN_WAP = 4;
    //点芯微信APP 打开微信app支付
    public static final int TYPE_WX_DIAN_XIN_APP = 5;
    //支付宝
    public static final int TYPE_ZHIFUBAO = 6;
    //京东快捷支付
    public static final int TYPE_JD_KJZF = 7;
    //微信原生sdk支付
    public static final int TYPE_WX_IWXAPI = 8;
    //支付宝扫码
    public static final int TYPE_ZHIFUBAO_SCAN = 9;


    //默认的支付方式
    public static final int IS_DEFAULT_YES = 1;
    //不是默认的
    public static final int IS_DEFAULT_FALSE = 0;

    private int isDefault ;// 1,
    private String payName ;//  微信支付 ,
    private String paySubTitle ;//  仅支持微信6.0.2以上版本 ,
    private int payType ;// 2,
    private int sort ;// 1

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public String getPaySubTitle() {
        return paySubTitle;
    }

    public void setPaySubTitle(String paySubTitle) {
        this.paySubTitle = paySubTitle;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
