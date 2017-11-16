package com.trade.eight.entity.trade;

import java.io.Serializable;

/**
 * Created by developer on 16/1/18.
 * 用户－银行列表
 */
public class Banks implements Serializable {

    public static final int IS_TOP_YES = 1;
    public static final int IS_TOP_NO = 0;
    private String bankName;//银行
    private String bankCard;//银行开户卡号
    private String bankBranch;//支行名称
    private String city;//城市
    private String province;//省份
    private String realName;//开户名字
    //v2 接口使用
//    private long bankId;
    //    private String bankName;//银行
    // 农交所提现cardID专用
    private long exchangeBankId;
    /**
     * 置顶（是否是热门银行）
     * 1=是
     * 0=不是
     */
    private int top;

    // fxbtg
    private String bankNo	;//	银行卡号
    private String icon;//图标

    private String code;
    private String name;

    private String bankId	;//	银行编码
    private String bankBranchId	;//	银行分支机构编码
    private String bankAccount	;//	银行帐号
    private String brokerBranchId	;//	brokerBranchId
    private String bankIcon	;//	bankIcon
    private int rechargeFlag	;//	充值需要密码 1代表需要2不需要
    private int cashoutFlag	;//	提现需要密码 1代表需要2不需要
    private int balanceFlag;// 查询余额需要密码  1代表需要2不需要

    public int getBalanceFlag() {
        return balanceFlag;
    }

    public void setBalanceFlag(int balanceFlag) {
        this.balanceFlag = balanceFlag;
    }

    public String getBankBranchId() {
        return bankBranchId;
    }

    public void setBankBranchId(String bankBranchId) {
        this.bankBranchId = bankBranchId;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBrokerBranchId() {
        return brokerBranchId;
    }

    public void setBrokerBranchId(String brokerBranchId) {
        this.brokerBranchId = brokerBranchId;
    }

    public int getRechargeFlag() {
        return rechargeFlag;
    }

    public void setRechargeFlag(int rechargeFlag) {
        this.rechargeFlag = rechargeFlag;
    }

    public int getCashoutFlag() {
        return cashoutFlag;
    }

    public void setCashoutFlag(int cashoutFlag) {
        this.cashoutFlag = cashoutFlag;
    }

    public String getBankId() {
        return bankId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public long getExchangeBankId() {
        return exchangeBankId;
    }

    public void setExchangeBankId(long exchangeBankId) {
        this.exchangeBankId = exchangeBankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

//    public long getBankId() {
//        return bankId;
//    }
//
//    public void setBankId(long bankId) {
//        this.bankId = bankId;
//    }

    public String getBankIcon() {
        return bankIcon;
    }

    public void setBankIcon(String bankIcon) {
        this.bankIcon = bankIcon;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }
}
