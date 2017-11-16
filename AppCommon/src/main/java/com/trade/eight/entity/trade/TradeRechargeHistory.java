package com.trade.eight.entity.trade;

import java.io.Serializable;

/**
 * 充值记录
 */
public class TradeRechargeHistory implements Serializable {
    private long id;
    private String amount;//充值金额
    private String balance;//余额
    private String createTime;//充值时间,格式yyyy-MM-dd HH:mm:ss
    private String status;//充值状态文本，处理中、成功、失败
    private String typeName;//支付方式名称，如 点芯支付
    //fxbtg
    private String state;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
