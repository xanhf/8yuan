package com.trade.eight.entity.trade;

import android.content.Context;
import android.text.TextUtils;

import com.easylife.ten.lib.R;
import com.trade.eight.config.ProFormatConfig;
import com.trade.eight.moudle.baksource.BakSourceService;
import com.trade.eight.moudle.home.trade.ProfitLossObj;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.trade.TradeConfig;

/**
 * Created by developer on 16/1/13.
 * <p/>
 * 持仓订单对象
 */
public class TradeOrder extends ProfitLossObj {
    public static final String LABLE_ZERO = "-";

    public static final int BUY_DOWN = 1;//买跌
    public static final int BUY_UP = 2;//买涨

    //平仓类型，1：手动平仓，2：强制平仓
    public static final int CLOSE_BYHAND = 1;
    public static final int CLOSE_BYSYS = 2;


    //    "quotationId":1,"code":"XAG1"
//    String excode;
//    String code;
    private long orderId;
    //    private String createPrice;//建仓行情价格
    private String createTime;
    private String realTimeProfitLoss;//订单实时浮动盈亏金额
    private String fee;//手续费
    private String currentPrice;//实时行情价格
    //    private String orderNumber;//购买手数
    private String stopProfit;//止盈：0-90
    private String stopLoss;//止损：0-90
    private String buyMoney;//总钱
    private String unitPrice;//每手价格
    private String unit;
    private String weight;
    private String productPrice;//每手价格


    //用户－交易记录 需要字段

    private String amount;//建仓消费金额
    private String closeTime;
    private String isJuan;
    private String profitRate;

    private String mp;//行情浮动比列
    private String margin;//行情浮动点数
    //盈亏的详情
//    private String amount;
    private String name;

    //农交所持仓
    private String productDeferred;
    private String deferred;
    private String isDeferred;
    private int days;

    private String contractExpire;//合约到期


    public static final String CODE_XAG = "XAG1";
    public static final String CODE_OIL = "OIL";
    public static final String CODE_HGNI = "HGNI";

    /**期货所需字段 start*********/
    private String instrumentId	;//	合约ID
    private String instrumentName	;//	合约名称
    private String productId	;//	产品ID
    private String productName	;//	产品名称
    private String exchangeId	;//	交易所ID
//    private String excode	;//	交易所编号
    private String exchangeName	;//	交易所名称
    private int deliveryYear	;//	交割年份
    private int deliveryMonth	;//	交割月份
    private String startDelivDate	;//	开始交割日(格式yyyy-MM-dd)
    private String delivDateStr	;//	交割日描述，如4-30 21:00前
    private String endDelivDate	;//	结束交割日(格式yyyy-MM-dd)
    private String useMargin	;//	占用的保证金
    private String todayProfit	;//	当日持仓盈亏
    private String totalProfit	;//	累计盈亏
    private String preSettlementPrice	;//	上次结算价
    private String settlementPrice	;//	本次结算价(当前行情价)
    private String holdAvgPrice	;//	持仓均价
    private int ydPosition	;//	昨日仓位数
    private int position	;//	总仓位数  已平仓里面为平仓手数
    private int todayPosition	;//	今日持仓数
    private int type	;//	购买方向，1：跌，2：涨
    private String sxf	;//	建仓手续费
    private String updateTime	;//	仓位最后修改时间
    private String openAvgPrice;
    // 以下是持仓单详情页所需 冗余字段
//    private String createPrice;
    private int count	;//	建仓手数
    private String openDate	;//	开仓日期（格式:yyyy-MM-dd）
    private String openTime	;//	开仓时间,格式yyyy-MM-dd HH:mm:ss（这个是从我们自己这边获取，不一定有数据）
    // 已平仓里面需要字段 冗余字段
    private long  id	;//	平仓ID
    private String  price	;//	建仓价格
    private String  closePrice	;//	平仓价格
    private String  profitLoss	;//	实际盈亏盈亏
    private String  closeSxf	;//	平仓手续费
    private String  orderSysId	;//	报单ID
    private String  tradeId	;//	成交ID
    private String  tradeTime	;//	成交时间
    private int  closeType	;//	平仓类型，1：手动平仓，2：强制平仓
    private String closeProfitLoss;// 平仓盈亏
//    private String holdAvgPrice	;//	持仓均价
    // 已平仓详情需要字段 冗余字段
    private String openPrice	;//	建仓价格
    private String openSxf	;//	建仓手续费
    /**期货所需字段 end*********/



    public String getNameByCode(Context context) {
        if (StringUtil.isEmpty(getCode()))
            return productName;

        return productName;
    }

    public String getCloseProfitLoss() {
        return closeProfitLoss;
    }

    public void setCloseProfitLoss(String closeProfitLoss) {
        this.closeProfitLoss = closeProfitLoss;
    }

    public String getOpenAvgPrice() {
        return openAvgPrice;
    }

    public void setOpenAvgPrice(String openAvgPrice) {
        this.openAvgPrice = openAvgPrice;
    }

    public String getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(String openPrice) {
        this.openPrice = openPrice;
    }

    public String getOpenSxf() {
        return openSxf;
    }

    public void setOpenSxf(String openSxf) {
        this.openSxf = openSxf;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCloseSxf() {
        return closeSxf;
    }

    public void setCloseSxf(String closeSxf) {
        this.closeSxf = closeSxf;
    }

    public String getOrderSysId() {
        return orderSysId;
    }

    public void setOrderSysId(String orderSysId) {
        this.orderSysId = orderSysId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public void setCloseType(int closeType) {
        this.closeType = closeType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }


    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    @Override
    public String getExcode() {
        return excode;
    }

    @Override
    public void setExcode(String excode) {
        this.excode = excode;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public int getDeliveryYear() {
        return deliveryYear;
    }

    public void setDeliveryYear(int deliveryYear) {
        this.deliveryYear = deliveryYear;
    }

    public int getDeliveryMonth() {
        return deliveryMonth;
    }

    public void setDeliveryMonth(int deliveryMonth) {
        this.deliveryMonth = deliveryMonth;
    }

    public String getStartDelivDate() {
        return startDelivDate;
    }

    public void setStartDelivDate(String startDelivDate) {
        this.startDelivDate = startDelivDate;
    }

    public String getDelivDateStr() {
        return delivDateStr;
    }

    public void setDelivDateStr(String delivDateStr) {
        this.delivDateStr = delivDateStr;
    }

    public String getEndDelivDate() {
        return endDelivDate;
    }

    public void setEndDelivDate(String endDelivDate) {
        this.endDelivDate = endDelivDate;
    }

    public String getUseMargin() {
        return useMargin;
    }

    public void setUseMargin(String useMargin) {
        this.useMargin = useMargin;
    }

    public String getTodayProfit() {
        return todayProfit;
    }

    public void setTodayProfit(String todayProfit) {
        this.todayProfit = todayProfit;
    }

    public String getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(String totalProfit) {
        this.totalProfit = totalProfit;
    }

    public String getPreSettlementPrice() {
        return preSettlementPrice;
    }

    public void setPreSettlementPrice(String preSettlementPrice) {
        this.preSettlementPrice = preSettlementPrice;
    }

    public String getSettlementPrice() {
        return settlementPrice;
    }

    public void setSettlementPrice(String settlementPrice) {
        this.settlementPrice = settlementPrice;
    }

    public String getHoldAvgPrice() {
        return holdAvgPrice;
    }

    public void setHoldAvgPrice(String holdAvgPrice) {
        this.holdAvgPrice = holdAvgPrice;
    }

    public int getYdPosition() {
        return ydPosition;
    }

    public void setYdPosition(int ydPosition) {
        this.ydPosition = ydPosition;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTodayPosition() {
        return todayPosition;
    }

    public void setTodayPosition(int todayPosition) {
        this.todayPosition = todayPosition;
    }

    public String getSxf() {
        return sxf;
    }

    public void setSxf(String sxf) {
        this.sxf = sxf;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getContractExpire() {
        return contractExpire;
    }

    public void setContractExpire(String contractExpire) {
        this.contractExpire = contractExpire;
    }

    public String getDeferred() {
        return deferred;
    }

    public void setDeferred(String deferred) {
        this.deferred = deferred;
    }

    public String getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(String profitRate) {
        this.profitRate = profitRate;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getRealTimeProfitLoss() {
        return realTimeProfitLoss;
    }

    public void setRealTimeProfitLoss(String realTimeProfitLoss) {
        this.realTimeProfitLoss = realTimeProfitLoss;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getStopProfit() {
        if (TextUtils.isEmpty(stopProfit) || Double.parseDouble(stopProfit) == 0)
            return LABLE_ZERO;
        return ProFormatConfig.formatByCodes((TextUtils.isEmpty(excode) ? TradeConfig.DEFAULT_EXCHANGE : excode) + "|" + code, stopProfit);
    }

    public void setStopProfit(String stopProfit) {
        this.stopProfit = stopProfit;
    }

    public String getStopProfitPercent() {
        if (StringUtil.isEmpty(stopProfit))
            return LABLE_ZERO;
        if (stopProfit.contains("%"))
            return stopProfit;
        return stopProfit + "%";
    }

    public String getStopProfitPointStr() {
        if (getStopProfitPoint() == 0)
            return LABLE_ZERO;
        return ProFormatConfig.formatByCodes(excode + "|" + code, getStopProfitPoint());
    }

    public String getStopLoss() {
        if (TextUtils.isEmpty(stopLoss) || Double.parseDouble(stopLoss) == 0)
            return LABLE_ZERO;
        return ProFormatConfig.formatByCodes((TextUtils.isEmpty(excode) ? TradeConfig.DEFAULT_EXCHANGE : excode) + "|" + code, stopLoss);
    }

    public void setStopLoss(String stopLoss) {
        this.stopLoss = stopLoss;
    }

    public String getStopLossPercent() {
        if (StringUtil.isEmpty(stopLoss))
            return LABLE_ZERO;
        if (stopLoss.contains("%"))
            return stopLoss;
        return stopLoss + "%";
    }

    public String getStopLossPointStr() {
        if (getStopLossPoint() == 0)
            return LABLE_ZERO;
        return ProFormatConfig.formatByCodes(excode + "|" + code, getStopLossPoint());
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(String closePrice) {
        this.closePrice = closePrice;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getIsJuan() {
        return isJuan;
    }

    public void setIsJuan(String isJuan) {
        this.isJuan = isJuan;
    }

    public int getCloseType() {
        return closeType;
    }

    public String getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(String profitLoss) {
        this.profitLoss = profitLoss;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getBuyMoney() {
        return buyMoney;
    }

    public void setBuyMoney(String buyMoney) {
        this.buyMoney = buyMoney;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getMp() {
        return mp;
    }

    public void setMp(String mp) {
        this.mp = mp;
    }

    public String getMargin() {
        return margin;
    }

    public void setMargin(String margin) {
        this.margin = margin;
    }

    public String getProductDeferred() {
        return productDeferred;
    }

    public void setProductDeferred(String productDeferred) {
        this.productDeferred = productDeferred;
    }

    public String getIsDeferred() {
        return isDeferred;
    }

    public void setIsDeferred(String isDeferred) {
        this.isDeferred = isDeferred;
    }

    public String getIsDeferredStr() {
        if (TradeProduct.IS_DEFERRED_YES.equals(isDeferred)) {
            return "开启";
        }
        return "关闭";
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }


}
