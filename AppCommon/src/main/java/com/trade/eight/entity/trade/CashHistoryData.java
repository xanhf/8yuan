package com.trade.eight.entity.trade;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/15.
 * 资金记录
 */

public class CashHistoryData implements Serializable {

    private String type;//	类型（充值，提现 ）
    private String date;//	日期
    private String amount;//	金额
    private String status;//	状态
    private String bankName;//	银行名称
    private String bankAccount;//	银行卡号

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
