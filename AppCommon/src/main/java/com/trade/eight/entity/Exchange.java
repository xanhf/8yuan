package com.trade.eight.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangzhu on 16/8/14.
 * 交易所 id name code
 */
public class Exchange implements Serializable {

    private int exchangeId;
    private String exchangeName;
    private String excode;
    //顺序
    private int sort;

    //没有网络加载成功
    public static final int WX_PAY_NOT_LOAD= -1;
    public static final int WX_PAY_TRUE = 1;
    public static final int WX_PAY_FALSE = 0;

    //是否支持微信支付
    private int wxPay = WX_PAY_NOT_LOAD;

    private List<Optional> quotationDataList;

    private List<Optional> optionalsList;

    public Exchange() {
    }

    public List<Optional> getOptionalsList() {

        if(optionalsList==null){
            optionalsList = new ArrayList<Optional>();
        }
        optionalsList.clear();
        if(quotationDataList!=null){
//            for(FXBTGProductData fxbtgProductData :codeList){
//                Optional optional = new Optional(fxbtgProductData);
//                optionalsList.add(optional);
//            }
            optionalsList.addAll(quotationDataList);
        }
        return optionalsList;
    }

    public void setOptionalsList(List<Optional> optionalsList) {
        this.optionalsList = optionalsList;
    }

    public List<Optional> getQuotationDataList() {
        return quotationDataList;
    }

    public void setQuotationDataList(List<Optional> quotationDataList) {
        this.quotationDataList = quotationDataList;
    }

    public Exchange(int exchangeId, String exchangeName, String exchangeCode) {
        this.exchangeId = exchangeId;
        this.exchangeName = exchangeName;
        this.excode = exchangeCode;
    }

    public int getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(int exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getExcode() {
        return excode;
    }

    public void setExcode(String excode) {
        this.excode = excode;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getWxPay() {
        return wxPay;
    }

    public void setWxPay(int wxPay) {
        this.wxPay = wxPay;
    }
}
