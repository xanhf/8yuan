package com.trade.eight.entity.trade;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 2017/5/26.
 * 支行数据
 */

public class BanksBranchData implements Serializable{
    private String bankDepositId	;//	支行ID
    private String name	;//	支行名称
    private String address	;//	地址
    private String tel	;//	支行电话

    public String getBankDepositId() {
        return bankDepositId;
    }

    public void setBankDepositId(String bankDepositId) {
        this.bankDepositId = bankDepositId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
