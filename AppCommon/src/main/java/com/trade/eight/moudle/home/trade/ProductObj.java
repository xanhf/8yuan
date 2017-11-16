package com.trade.eight.moudle.home.trade;

import com.trade.eight.moudle.baksource.BakSourceInterface;

import java.io.DataOutput;
import java.io.Serializable;

/**
 * Created by dufangzhu on 2017/5/24.
 * 外汇的产品 使用新的实体类
 */

public class ProductObj extends ProfitLossObj {

    /*方向 1.跌 2.涨*/
    public static final int TYPE_BUY_DOWN = 1;
    public static final int TYPE_BUY_UP = 2;

    //是否休市:1=正常，2=休市
    public static final String IS_CLOSE_YES = "2";
    public static final String IS_CLOSE_NO = "1";

    //建仓的参数
    public static final String PARAM_PID = "productId";
    public static final String PARAM_ORDER_NB = "orderNumber";
    public static final String PARAM_PRICE = "price";//建仓价格
    public static final String PARAM_HOLDAVGPRICE = "holdAvgPrice";//持仓均价
    public static final String PARAM_instrumentId = "instrumentId";//合约代码
    public static final String PARAM_TYPE = "type";
    public static final String PARAM_STOPPROFIT = "stopProfit";
    public static final String PARAM_STOPLOSS = "stopLoss";
    public static final String PARAM_TODAYPOSITION = "todayPosition";//是否平今




    /**
     * code : XAUUSD
     * contractNumber : 100
     * downDeferredFee : 3.1
     * isClosed : 1
     * lever : 100
     * margin : 0.61
     * maxSl : 30
     * minSl : 0.01
     * minStopLoss : 0.1
     * minStopProfile : 0.1
     * mp : 0.05%
     * name : 黄金
     * pointDiff : 45
     * productId : 3b76f3c351e6e494
     * sell : 1251.67
     * upDeferredFee : 9.3
     */
//    private String excode = BakSourceInterface.TRUDE_SOURCE_WEIPANP_FXBTG;
//    private String code;
    /*合约提醒*/
    private String contractExpire;
    private double contractNumber;
    /*买跌的手续费*/
    private double downDeferredFee;
    //    private String isClosed;
    private double lever;
    private String margin;
    /*最大购买手数*/
//    private double maxSl;
    /*最小购买手数*/
//    private double minSl;
//    /*最小止损点*/
//    private double minStopLoss;
//    /*最大止盈点*/
//    private double minStopProfile;
    /*价格波动百分比*/
    private String mp;
    private String name;
    /*点差*/
    private double pointDiff;
    //    private String productId;
    /*卖价*/
//    private String sell;
    private double upDeferredFee;
    /*买价*/
//    private String buy;
    /*买涨 比例 "50%的用户买涨"*/
    private String buyRate;
    /*建仓手数的 增加步长*/
    private double slMoveNum = 0.01;
    //休市提示语
    private String closePrompt;

    /***
     * 期货字段 start
     ******/
    private String productId;//	产品ID
    private String productName;//	产品名称
    private int exchangeId;//	交易所ID
    //    private String excode	;//	交易所代码
    private String exchangeName;//	交易所名称
    private int volumeMultiple;//	合约数量乘数
    private String priceTick;//	最小变动价位（对应金额为pirceTick*volumeMultiple）
    private int maxSl;//	最大手数
    private int minSl;//	最小手数
    private String instrumentId;//	合约代码（建仓接口用这个）
    private String instrumentName;//	合约名称
    private int deliveryYear;//	交割年份
    private int deliveryMonth;//	交割月份
    private String expireDate;//	到期日(格式yyyy-MM-dd)
    private String startDelivDate;//	开始交割日(格式yyyy-MM-dd)
    private String endDelivDate;//	结束交割日(格式yyyy-MM-dd)
    private String delivDateStr;//	交割日描述（如4-30 21:00前）
    private int isTrading;//	是否可以交易，1：是，0：否
    private String longMarginRatioByMoney;//	多头保证金率(买涨保证金率)
    private String shortMarginRatioByMoney;//	空头保证金率（买跌保证金率）
    private String maxMarginSideAlgorithm;//	是否使用大额单边保证金算法,1:是,0:否
    private String openRatioByMoney;//	建仓手续费率（根据行情）
    private String openRatioByVolume;//	建仓手续费率（根据手数）
    private String closeRatioByMoney;//	平仓手续费率（根据行情）
    private String closeRatioByVolume;//	平仓手续费率（根据手数）
    private String closeTodayRatioByMoney;//	平今手续费率（根据行情）
    private String closeTodayRatioByVolume;//	平均手续费率（根据手数）
    private String isClosed;//	是否休市，1：未休市，2：已休市
    private String lastPrice;//	行情最新价
    private String bidPrice1;//	申买价一
    private String askPrice1;//	申卖价一
    private String tradeVolume;//	成交量
    private String longRate;//	多头持仓比例（如50，为50%）
    private String shortRate;//	空头持仓比例
    private String longMarginRatioByVolume;//	多头保证金（按手）
    private String shortMarginRatioByVolume;//	空头保证金（按手）
    private String exchangeTimePrompt;//	交易时间段（逗号分隔）

    /***
     * 期货字段 end
     ******/

    public String getLongMarginRatioByMoney() {
        return longMarginRatioByMoney;
    }

    public void setLongMarginRatioByMoney(String longMarginRatioByMoney) {
        this.longMarginRatioByMoney = longMarginRatioByMoney;
    }

    public String getShortMarginRatioByMoney() {
        return shortMarginRatioByMoney;
    }

    public void setShortMarginRatioByMoney(String shortMarginRatioByMoney) {
        this.shortMarginRatioByMoney = shortMarginRatioByMoney;
    }

    public String getLongMarginRatioByVolume() {
        return longMarginRatioByVolume;
    }

    public void setLongMarginRatioByVolume(String longMarginRatioByVolume) {
        this.longMarginRatioByVolume = longMarginRatioByVolume;
    }

    public String getShortMarginRatioByVolume() {
        return shortMarginRatioByVolume;
    }

    public void setShortMarginRatioByVolume(String shortMarginRatioByVolume) {
        this.shortMarginRatioByVolume = shortMarginRatioByVolume;
    }

    public String getExchangeTimePrompt() {
        return exchangeTimePrompt;
    }

    public void setExchangeTimePrompt(String exchangeTimePrompt) {
        this.exchangeTimePrompt = exchangeTimePrompt;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(int exchangeId) {
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

    public int getVolumeMultiple() {
        return volumeMultiple;
    }

    public void setVolumeMultiple(int volumeMultiple) {
        this.volumeMultiple = volumeMultiple;
    }

    public String getPriceTick() {
        return priceTick;
    }

    public void setPriceTick(String priceTick) {
        this.priceTick = priceTick;
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

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getStartDelivDate() {
        return startDelivDate;
    }

    public void setStartDelivDate(String startDelivDate) {
        this.startDelivDate = startDelivDate;
    }

    public String getEndDelivDate() {
        return endDelivDate;
    }

    public void setEndDelivDate(String endDelivDate) {
        this.endDelivDate = endDelivDate;
    }

    public String getDelivDateStr() {
        return delivDateStr;
    }

    public void setDelivDateStr(String delivDateStr) {
        this.delivDateStr = delivDateStr;
    }

    public int getIsTrading() {
        return isTrading;
    }

    public void setIsTrading(int isTrading) {
        this.isTrading = isTrading;
    }

    public String getMaxMarginSideAlgorithm() {
        return maxMarginSideAlgorithm;
    }

    public void setMaxMarginSideAlgorithm(String maxMarginSideAlgorithm) {
        this.maxMarginSideAlgorithm = maxMarginSideAlgorithm;
    }

    public String getOpenRatioByMoney() {
        return openRatioByMoney;
    }

    public void setOpenRatioByMoney(String openRatioByMoney) {
        this.openRatioByMoney = openRatioByMoney;
    }

    public String getOpenRatioByVolume() {
        return openRatioByVolume;
    }

    public void setOpenRatioByVolume(String openRatioByVolume) {
        this.openRatioByVolume = openRatioByVolume;
    }

    public String getCloseRatioByMoney() {
        return closeRatioByMoney;
    }

    public void setCloseRatioByMoney(String closeRatioByMoney) {
        this.closeRatioByMoney = closeRatioByMoney;
    }

    public String getCloseRatioByVolume() {
        return closeRatioByVolume;
    }

    public void setCloseRatioByVolume(String closeRatioByVolume) {
        this.closeRatioByVolume = closeRatioByVolume;
    }

    public String getCloseTodayRatioByMoney() {
        return closeTodayRatioByMoney;
    }

    public void setCloseTodayRatioByMoney(String closeTodayRatioByMoney) {
        this.closeTodayRatioByMoney = closeTodayRatioByMoney;
    }

    public String getCloseTodayRatioByVolume() {
        return closeTodayRatioByVolume;
    }

    public void setCloseTodayRatioByVolume(String closeTodayRatioByVolume) {
        this.closeTodayRatioByVolume = closeTodayRatioByVolume;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getBidPrice1() {
        return bidPrice1;
    }

    public void setBidPrice1(String bidPrice1) {
        this.bidPrice1 = bidPrice1;
    }

    public String getAskPrice1() {
        return askPrice1;
    }

    public void setAskPrice1(String askPrice1) {
        this.askPrice1 = askPrice1;
    }

    public String getTradeVolume() {
        return tradeVolume;
    }

    public void setTradeVolume(String tradeVolume) {
        this.tradeVolume = tradeVolume;
    }

    public String getLongRate() {
        return longRate;
    }

    public void setLongRate(String longRate) {
        this.longRate = longRate;
    }

    public String getShortRate() {
        return shortRate;
    }

    public void setShortRate(String shortRate) {
        this.shortRate = shortRate;
    }

    public double getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(double contractNumber) {
        this.contractNumber = contractNumber;
    }

    public double getDownDeferredFee() {
        return downDeferredFee;
    }

    public void setDownDeferredFee(double downDeferredFee) {
        this.downDeferredFee = downDeferredFee;
    }

    public String getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(String isClosed) {
        this.isClosed = isClosed;
    }

    public double getLever() {
        return lever;
    }

    public void setLever(double lever) {
        this.lever = lever;
    }

    public String getMargin() {
        return margin;
    }

    public void setMargin(String margin) {
        this.margin = margin;
    }

    public int getMaxSl() {
        return maxSl;
    }

    public void setMaxSl(int maxSl) {
        this.maxSl = maxSl;
    }

    public int getMinSl() {
        return minSl;
    }

    public void setMinSl(int minSl) {
        this.minSl = minSl;
    }


    public String getMp() {
        return mp;
    }

    public void setMp(String mp) {
        this.mp = mp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPointDiff() {
        return pointDiff;
    }

    public void setPointDiff(double pointDiff) {
        this.pointDiff = pointDiff;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getUpDeferredFee() {
        return upDeferredFee;
    }

    public void setUpDeferredFee(double upDeferredFee) {
        this.upDeferredFee = upDeferredFee;
    }

    public String getBuyRate() {
        return buyRate;
    }

    public void setBuyRate(String buyRate) {
        this.buyRate = buyRate;
    }

    public String getClosePrompt() {
        return closePrompt;
    }

    public void setClosePrompt(String closePrompt) {
        this.closePrompt = closePrompt;
    }

    public String getContractExpire() {
        return contractExpire;
    }

    public void setContractExpire(String contractExpire) {
        this.contractExpire = contractExpire;
    }

    public double getSlMoveNum() {
        return slMoveNum;
    }

    public void setSlMoveNum(double slMoveNum) {
        this.slMoveNum = slMoveNum;
    }


}
