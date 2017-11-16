package com.trade.eight.entity.trade;

import java.io.Serializable;

/**
 * 提现记录
 */
public class TradeCashOut implements Serializable {

    private String amount;//充值金额
    private String charge;//提现手续费
    private String withdrawTime;//提现时间
    private String handleTime;//处理时间
    private String bank;
    private String branchBank;
    private String province;
    private String city;
    private String cardNo;
    private int status;//  状态 1-处理中，2-成功，3-失败
    public static final int STATUS_DEALING = 1;
    public static final int STATUS_SUCCESS = 2;
    public static final int STATUS_FAIL = 3;

    private long id;//提现记录ID
    private String remark;//提现状态
    //fxbtg
    private String logNo;//流水号
    private String bankName	;//	银行名称
    private String bankDepositName	;//	银行名称
    private String bankNo	;//	银行卡号
    private String balance	;//	出金额
    private String lastHandleTime	;//	处理时间
    private String state	;//	处理状态-3.审批撤销,-2.审批拒绝,-1.审批不通过,0.正在审批,1.审批通过,2.已出金
    private String mark	;//	备注
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogNo() {
        return logNo;
    }

    public void setLogNo(String logNo) {
        this.logNo = logNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankDepositName() {
        return bankDepositName;
    }

    public void setBankDepositName(String bankDepositName) {
        this.bankDepositName = bankDepositName;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getLastHandleTime() {
        return lastHandleTime;
    }

    public void setLastHandleTime(String lastHandleTime) {
        this.lastHandleTime = lastHandleTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getWithdrawTime() {
        return withdrawTime;
    }

    public void setWithdrawTime(String withdrawTime) {
        this.withdrawTime = withdrawTime;
    }

    public String getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(String handleTime) {
        this.handleTime = handleTime;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBranchBank() {
        return branchBank;
    }

    public void setBranchBank(String branchBank) {
        this.branchBank = branchBank;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
