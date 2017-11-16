package com.trade.eight.entity.trade;

import java.io.Serializable;

/**
 * Created by developer on 16/1/17.
 * 交易明细
 */
public class TradeDetail implements Serializable {
    private String amount;//金额
    private int type;
    private String typeName;
    private String createTime;
    private String remark;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
