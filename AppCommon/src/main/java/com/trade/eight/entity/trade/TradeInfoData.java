package com.trade.eight.entity.trade;

import android.text.TextUtils;

import com.trade.eight.entity.integral.GoodsActGiftData;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/4/5.
 * 获取余额 代金券 特权卡合并接口
 * <p>
 * ① 风险率达到50%-70%，顶部通知栏仅提醒1次；②达到70%-80%，提醒一次，达到80%-90%提醒1次，90%-100%提醒1次
 * 每5分钟一次
 */

public class TradeInfoData implements Serializable {

    // 资金风险率测试
    public static final int TRADEINFO_SAFE = 0;
    public static final int TRADEINFO_DANGEROUS = 1;//风险率达到50%-70%
    public static final int TRADEINFO_DANGEROUS_MOST_1 = 2;// 达到70%-80%
    public static final int TRADEINFO_DANGEROUS_MOST_2 = 3;//，达到80%-90%
    public static final int TRADEINFO_DANGEROUS_MOST_3 = 4;//90%-100%

    public static final String KEY_DANGEROUS = "dangerous";
    public static final String KEY_DANGEROUS_MOST_1 = "dangerous_most_1";
    public static final String KEY_DANGEROUS_MOST_2 = "dangerous_most_2";
    public static final String KEY_DANGEROUS_MOST_3 = "dangerous_most_3";


    public String balance;//余额
    public String equity;//净值
    public String freeMargin;//	可用预付款
    public String margin;    //已用预付款
    public String marginLevel;//	预付款比例(%)
    //    public double profitLoss;//浮动盈亏、
    public String rechargeMinAmountDesc;//最低入金300美金
    public String cashOutMinAmountDesc;// 出金的提示


    /****
     * 期货账户信息 start
     ************/
    private String settlementId;//	结算ID
    private String accountId;//	账户ID
    private String currMargin;//	保证金总额
    private String positionProfit;//	持仓盈亏
    private String closeProfit;//	平仓盈亏
    private String available;//	可用资金
    private String rightsInterests;//	权益
    private String capitalProportion;//	资金占用比例
    private String withdrawQuota;//	可取资金
    private String deposit;//	入金金额
    private String withdraw;//	出金金额
    private String frozenCash;//	冻结的资金
    private String frozenCommission;//	冻结的手续费
    private String commission;//	手续费
    private String profit;//浮动盈亏
    private String exchangeName;//、	期货交易所名称
    private String customerServicephone;//	客服电话
    // 历史订单 建/平 盈利统计
    private int open;//	建仓数量
    private int close;//int	平仓数量
    private String profitLoss;//	盈亏
    /****
     * 期货账户信息 end
     ************/
    private String amount;//银行卡余额 冗余字段


    private GoodsActGiftData specialCard;//特权卡

    private List<TradeVoucher> voucherList;//代金券

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getClose() {
        return close;
    }

    public void setClose(int close) {
        this.close = close;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getCustomerServicephone() {
        return customerServicephone;
    }

    public void setCustomerServicephone(String customerServicephone) {
        this.customerServicephone = customerServicephone;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public String getCashOutMinAmountDesc() {
        return cashOutMinAmountDesc;
    }

    public String getSettlementId() {
        return settlementId;
    }

    public void setSettlementId(String settlementId) {
        this.settlementId = settlementId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCurrMargin() {
        return currMargin;
    }

    public void setCurrMargin(String currMargin) {
        this.currMargin = currMargin;
    }

    public String getPositionProfit() {
        return positionProfit;
    }

    public void setPositionProfit(String positionProfit) {
        this.positionProfit = positionProfit;
    }

    public String getCloseProfit() {
        return closeProfit;
    }

    public void setCloseProfit(String closeProfit) {
        this.closeProfit = closeProfit;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getRightsInterests() {
        return rightsInterests;
    }

    public void setRightsInterests(String rightsInterests) {
        this.rightsInterests = rightsInterests;
    }

    public String getCapitalProportion() {
        return capitalProportion;
    }

    public void setCapitalProportion(String capitalProportion) {
        this.capitalProportion = capitalProportion;
    }

    public String getWithdrawQuota() {
        return withdrawQuota;
    }

    public void setWithdrawQuota(String withdrawQuota) {
        this.withdrawQuota = withdrawQuota;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(String withdraw) {
        this.withdraw = withdraw;
    }

    public String getFrozenCash() {
        return frozenCash;
    }

    public void setFrozenCash(String frozenCash) {
        this.frozenCash = frozenCash;
    }

    public String getFrozenCommission() {
        return frozenCommission;
    }

    public void setFrozenCommission(String frozenCommission) {
        this.frozenCommission = frozenCommission;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public void setCashOutMinAmountDesc(String cashOutMinAmountDesc) {
        this.cashOutMinAmountDesc = cashOutMinAmountDesc;
    }

    public String getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(String profitLoss) {
        this.profitLoss = profitLoss;
    }

    public String getRechargeMinAmountDesc() {
        return rechargeMinAmountDesc;
    }

    public void setRechargeMinAmountDesc(String rechargeMinAmountDesc) {
        this.rechargeMinAmountDesc = rechargeMinAmountDesc;
    }

    public String getEquity() {
        return equity;
    }

    public void setEquity(String equity) {
        this.equity = equity;
    }

    public String getFreeMargin() {
        return freeMargin;
    }

    public void setFreeMargin(String freeMargin) {
        this.freeMargin = freeMargin;
    }

    public String getMargin() {
        return margin;
    }

    public void setMargin(String margin) {
        this.margin = margin;
    }

    public String getMarginLevel() {
        return marginLevel;
    }

    public void setMarginLevel(String marginLevel) {
        this.marginLevel = marginLevel;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public GoodsActGiftData getSpecialCard() {
        return specialCard;
    }

    public void setSpecialCard(GoodsActGiftData specialCard) {
        this.specialCard = specialCard;
    }

    public List<TradeVoucher> getVoucherList() {
        return voucherList;
    }

    public void setVoucherList(List<TradeVoucher> voucherList) {
        this.voucherList = voucherList;
    }

    /**
     * 得到用户的危险或者安全等级
     *
     * @return
     */
    public int getTradeInfoSafeOrDangerous() {
        if (TextUtils.isEmpty(capitalProportion)) {
            return TRADEINFO_SAFE;
        }
        String percent = StringUtil.matcherNumber(capitalProportion);
        if (new BigDecimal(ConvertUtil.NVL(percent, 0)).doubleValue() >= 50 && new BigDecimal(ConvertUtil.NVL(percent, 0)).doubleValue() < 70) {

            return TRADEINFO_DANGEROUS;

        } else if (new BigDecimal(ConvertUtil.NVL(percent, 0)).doubleValue() >= 70 && new BigDecimal(ConvertUtil.NVL(percent, 0)).doubleValue() < 80) {

            return TRADEINFO_DANGEROUS_MOST_1;

        } else if (new BigDecimal(ConvertUtil.NVL(percent, 0)).doubleValue() >= 80 && new BigDecimal(ConvertUtil.NVL(percent, 0)).doubleValue() < 90) {

            return TRADEINFO_DANGEROUS_MOST_2;

        } else if (new BigDecimal(ConvertUtil.NVL(percent, 0)).doubleValue() >= 90) {

            return TRADEINFO_DANGEROUS_MOST_3;

        }

        return TRADEINFO_SAFE;
    }
}
