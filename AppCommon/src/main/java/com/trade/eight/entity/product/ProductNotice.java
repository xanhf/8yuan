package com.trade.eight.entity.product;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 2017/2/13.
 * 行情提醒
 */

public class ProductNotice implements Serializable{

    private long createTime	;//	创建时间（时间戳）
    private String productCode	;//	产品编码
    private String productExcode	;//	交易所编码
    private String exchangeName	;//	交易所名称
    private String productName	;//	产品名称
    private String customizedProfit	;//	定制点位
    private String floatUpProfit	;//	浮动点位
    private int status	;//	状态（1.定制中，未发送）
    private int cycleType	;//	周期类型（1，一天，2一周）
    private long expirationTime	;//	过期时间（时间戳）
    private String userName	;//	用户名称
    private long pid;//id
    private int buyType;

    public int getBuyType() {
        return buyType;
    }

    public void setBuyType(int buyType) {
        this.buyType = buyType;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductExcode() {
        return productExcode;
    }

    public void setProductExcode(String productExcode) {
        this.productExcode = productExcode;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCustomizedProfit() {
        return customizedProfit;
    }

    public void setCustomizedProfit(String customizedProfit) {
        this.customizedProfit = customizedProfit;
    }

    public String getFloatUpProfit() {
        return floatUpProfit;
    }

    public void setFloatUpProfit(String floatUpProfit) {
        this.floatUpProfit = floatUpProfit;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCycleType() {
        return cycleType;
    }

    public void setCycleType(int cycleType) {
        this.cycleType = cycleType;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
