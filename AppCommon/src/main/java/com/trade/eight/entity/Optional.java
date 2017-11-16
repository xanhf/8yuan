package com.trade.eight.entity;

import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;
import com.trade.eight.kchart.entity.KCandleObj;
import com.trade.eight.mpush.message.ResponseQuotationMessage;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;

public class Optional implements Serializable {
    public static final String ISINITDATA = "isInitData";
    /*涨跌值保留的最长有效小数点位*/
    public static final int NUM_SCAL_RATE = 5;
    /*涨跌幅保留的百分比点位*/
    public static final int NUM_SCAL_RATE_CHANGE = 2;

    @DatabaseField(generatedId = true)
    private int localId;
    @DatabaseField
    private int drag;
    @DatabaseField
    private int top;
    @DatabaseField
    private boolean optional; //是否是自选股
    @DatabaseField
    private boolean isHostory; //
    @DatabaseField
    private String type; //  sg  tg //交易所code
    @DatabaseField
    private String treaty;    //品种代码
    @DatabaseField
    private String title;       // 名称
    @DatabaseField
    private String date;
    @DatabaseField
    private String time;
    @DatabaseField
    private String opening;
    @DatabaseField
    private String highest;
    @DatabaseField
    private String lowest;
    @DatabaseField
    private String newest;
    @DatabaseField
    private String buyone;
    @DatabaseField
    private String sellone;
    @DatabaseField
    private String buyquantity;
    @DatabaseField
    private String sellquantity;
    //    @DatabaseField
//    private String volume;
    @DatabaseField
    private String price;
    @DatabaseField
    private String position;
    @DatabaseField
    private String lastsettle;
    @DatabaseField
    private String closed;
    @DatabaseField
    private String add_time;
    @DatabaseField
    private String unit;
    @DatabaseField(defaultValue = "0.1f")
    private String ratio;

    //code在客户端显示的品种名称 code标示，treaty才是真实的代码值
    /**
     * type == source
     * treaty  == customCode
     * title == name
     */
    @DatabaseField
    private String customCode;

    @DatabaseField
    private int goodsid;

    @DatabaseField
    private String productCode;//code

    @DatabaseField
    private boolean isInitData;

    /*开盘时间 如09:00*/
    @DatabaseField
    private String baksourceStart;
    /*休盘时间 如15：00*/
    @DatabaseField
    private String baksourceEnd;
    /*中间休盘时间断 如11：30/13:00*/
    @DatabaseField
    private String baksourceMiddle;

    @DatabaseField
    private int tradeFlag;//`0=不能交易，1=能交易


    /* 期货行情数据 start***********/
    @DatabaseField
    private String tradingDay;//20170717",
    @DatabaseField
    private String instrumentID;//ag",
    @DatabaseField
    private String exchangeID;//SHFE",
    @DatabaseField
    private String productId;//ag",
    @DatabaseField
    private String lastPrice;//3844",
    @DatabaseField
    private String preSettlementPrice;//3777",
    @DatabaseField
    private String preClosePrice;//3769",
    @DatabaseField
    private String preOpenInterest;//704370",
    @DatabaseField
    private String openPrice;//3840",
    @DatabaseField
    private String highestPrice;//3867",
    @DatabaseField
    private String lowestPrice;//3820",
    @DatabaseField
    private String volume;//551538",
    @DatabaseField
    private String turnover;//31774403580",
    @DatabaseField
    private String openInterest;//669956",
    @DatabaseField
    private String closePrice;//0",
    @DatabaseField
    private String settlementPrice;//0",
    @DatabaseField
    private String upperLimitPrice;//3965",
    @DatabaseField
    private String lowerLimitPrice;//3588",
    @DatabaseField
    private String preDelta;//0",
    @DatabaseField
    private String currDelta;//0",
    @DatabaseField
    private String updateTime;//15:00:00",
    @DatabaseField
    private String updateMillisecond;//0",
    @DatabaseField
    private String bidPrice1;//3843",
    @DatabaseField
    private String bidVolume1;//229",
    @DatabaseField
    private String askPrice1;//3844",
    @DatabaseField
    private String askVolume1;//37",
    private String bidPrice2;//0",
    private String bidVolume2;//0",
    private String askPrice2;//0",
    private String askVolume2;//0",
    private String bidPrice3;//0",
    private String bidVolume3;//0",
    private String askPrice3;//0",
    private String askVolume3;//0",
    private String bidPrice4;//0",
    private String bidVolume4;//0",
    private String askPrice4;//0",
    private String askVolume4;//0",
    private String bidPrice5;//0",
    private String bidVolume5;//0",
    private String askPrice5;//0",
    private String askVolume5;//0",
    @DatabaseField
    private String averagePrice;//57610",
    @DatabaseField
    private String actionDay;//20170717",
    @DatabaseField
    private String quotationDateTime;//2017-07-17 15:00:00",
    @DatabaseField
    private String startDeliveryDate;// null,
    @DatabaseField
    private String decimalPrecision;//
    @DatabaseField
    private String change;//75",
    @DatabaseField
    private String chg;//1.99%",
    @DatabaseField
    private String name;//白银主连"
//    @DatabaseField
//    private String  averagePrice;
    //昨结
//    @DatabaseField
//    private String preSettlementPrice;
    /*期货行情数据 end***********/


    public static final int TRADEFLAG_YES = 1;
    public static final int TRADEFLAG_NO = 0;


    public Optional() {
    }

    public Optional(Goods goods) {
        this.type = goods.getSource();
        this.treaty = goods.getCustomCode();
        this.goodsid = goods.getGoodsId();
        this.title = goods.getName();
        this.productCode = goods.getCode();
        this.customCode = goods.getCustomCode();
        this.isInitData = false;
    }

    public Optional(FXBTGProductData fxbtgProductData) {
        this.type = fxbtgProductData.getExcode();
        this.treaty = fxbtgProductData.getCode();
        this.productCode = fxbtgProductData.getCode();
        this.customCode = fxbtgProductData.getCode();
        this.title = fxbtgProductData.getName();
    }

    public int getTradeFlag() {
        return tradeFlag;
    }

    public void setTradeFlag(int tradeFlag) {
        this.tradeFlag = tradeFlag;
    }

    public String getCustomCode() {
        if (customCode == null || customCode.trim().length() == 0)
            return treaty;
        return customCode;
    }

    public void setCustomCode(String customCode) {
        this.customCode = customCode;
    }

    public int getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(int goodsid) {
        this.goodsid = goodsid;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public int getDrag() {
        return drag;
    }

    public void setDrag(int drag) {
        this.drag = drag;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isHostory() {
        return isHostory;
    }

    public void setHostory(boolean isHostory) {
        this.isHostory = isHostory;
    }

    public String getTreaty() {
        return treaty;
    }

    public void setTreaty(String treaty) {
        this.treaty = treaty;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOpening() {
        return opening;
    }

    public void setOpening(String opening) {
        this.opening = opening;
    }

    public String getHighest() {
        return highest;
    }

    public void setHighest(String highest) {
        this.highest = highest;
    }

    public String getLowest() {
        return lowest;
    }

    public void setLowest(String lowest) {
        this.lowest = lowest;
    }

    public String getNewest() {
        return newest;
    }

    public void setNewest(String newest) {
        this.newest = newest;
    }

    public String getBuyone() {
        return buyone;
    }

    public void setBuyone(String buyone) {
        this.buyone = buyone;
    }

    public String getSellone() {
        return sellone;
    }

    public void setSellone(String sellone) {
        this.sellone = sellone;
    }

    public String getBuyquantity() {
        return buyquantity;
    }

    public void setBuyquantity(String buyquantity) {
        this.buyquantity = buyquantity;
    }

    public String getSellquantity() {
        return sellquantity;
    }

    public void setSellquantity(String sellquantity) {
        this.sellquantity = sellquantity;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLastsettle() {
        return lastsettle;
    }

    public void setLastsettle(String lastsettle) {
        this.lastsettle = lastsettle;
    }

    public String getClosed() {
        return closed;
    }

    public void setClosed(String closed) {
        this.closed = closed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getUnit() {
        if (unit == null || "".equals(unit)) {
            return "10";
        }
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isInitData() {
        return isInitData;
    }

    public void setInitData(boolean isInitData) {
        this.isInitData = isInitData;
    }

    public String getBaksourceStart() {
        return baksourceStart;
    }

    public void setBaksourceStart(String baksourceStart) {
        this.baksourceStart = baksourceStart;
    }

    public String getBaksourceEnd() {
        return baksourceEnd;
    }

    public void setBaksourceEnd(String baksourceEnd) {
        this.baksourceEnd = baksourceEnd;
    }

    public String getBaksourceMiddle() {
        return baksourceMiddle;
    }

    public void setBaksourceMiddle(String baksourceMiddle) {
        this.baksourceMiddle = baksourceMiddle;
    }

    public String getRatio() {
        return ratio;
    }

    public String getTradingDay() {
        return tradingDay;
    }

    public void setTradingDay(String tradingDay) {
        this.tradingDay = tradingDay;
    }

    public String getInstrumentID() {
        return instrumentID;
    }

    public void setInstrumentID(String instrumentID) {
        this.instrumentID = instrumentID;
    }

    public String getExchangeID() {
        return exchangeID;
    }

    public void setExchangeID(String exchangeID) {
        this.exchangeID = exchangeID;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getPreSettlementPrice() {
        return preSettlementPrice;
    }

    public void setPreSettlementPrice(String preSettlementPrice) {
        this.preSettlementPrice = preSettlementPrice;
    }

    public String getPreClosePrice() {
        return preClosePrice;
    }

    public void setPreClosePrice(String preClosePrice) {
        this.preClosePrice = preClosePrice;
    }

    public String getPreOpenInterest() {
        return preOpenInterest;
    }

    public void setPreOpenInterest(String preOpenInterest) {
        this.preOpenInterest = preOpenInterest;
    }

    public String getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(String openPrice) {
        this.openPrice = openPrice;
    }

    public String getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(String highestPrice) {
        this.highestPrice = highestPrice;
    }

    public String getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(String lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public String getTurnover() {
        return turnover;
    }

    public void setTurnover(String turnover) {
        this.turnover = turnover;
    }

    public String getOpenInterest() {
        return openInterest;
    }

    public void setOpenInterest(String openInterest) {
        this.openInterest = openInterest;
    }

    public String getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(String closePrice) {
        this.closePrice = closePrice;
    }

    public String getSettlementPrice() {
        return settlementPrice;
    }

    public void setSettlementPrice(String settlementPrice) {
        this.settlementPrice = settlementPrice;
    }

    public String getUpperLimitPrice() {
        return upperLimitPrice;
    }

    public void setUpperLimitPrice(String upperLimitPrice) {
        this.upperLimitPrice = upperLimitPrice;
    }

    public String getLowerLimitPrice() {
        return lowerLimitPrice;
    }

    public void setLowerLimitPrice(String lowerLimitPrice) {
        this.lowerLimitPrice = lowerLimitPrice;
    }

    public String getPreDelta() {
        return preDelta;
    }

    public void setPreDelta(String preDelta) {
        this.preDelta = preDelta;
    }

    public String getCurrDelta() {
        return currDelta;
    }

    public void setCurrDelta(String currDelta) {
        this.currDelta = currDelta;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateMillisecond() {
        return updateMillisecond;
    }

    public void setUpdateMillisecond(String updateMillisecond) {
        this.updateMillisecond = updateMillisecond;
    }

    public String getBidPrice1() {
        return bidPrice1;
    }

    public void setBidPrice1(String bidPrice1) {
        this.bidPrice1 = bidPrice1;
    }

    public String getBidVolume1() {
        return bidVolume1;
    }

    public void setBidVolume1(String bidVolume1) {
        this.bidVolume1 = bidVolume1;
    }

    public String getAskPrice1() {
        return askPrice1;
    }

    public void setAskPrice1(String askPrice1) {
        this.askPrice1 = askPrice1;
    }

    public String getAskVolume1() {
        return askVolume1;
    }

    public void setAskVolume1(String askVolume1) {
        this.askVolume1 = askVolume1;
    }

    public String getBidPrice2() {
        return bidPrice2;
    }

    public void setBidPrice2(String bidPrice2) {
        this.bidPrice2 = bidPrice2;
    }

    public String getBidVolume2() {
        return bidVolume2;
    }

    public void setBidVolume2(String bidVolume2) {
        this.bidVolume2 = bidVolume2;
    }

    public String getAskPrice2() {
        return askPrice2;
    }

    public void setAskPrice2(String askPrice2) {
        this.askPrice2 = askPrice2;
    }

    public String getAskVolume2() {
        return askVolume2;
    }

    public void setAskVolume2(String askVolume2) {
        this.askVolume2 = askVolume2;
    }

    public String getBidPrice3() {
        return bidPrice3;
    }

    public void setBidPrice3(String bidPrice3) {
        this.bidPrice3 = bidPrice3;
    }

    public String getBidVolume3() {
        return bidVolume3;
    }

    public void setBidVolume3(String bidVolume3) {
        this.bidVolume3 = bidVolume3;
    }

    public String getAskPrice3() {
        return askPrice3;
    }

    public void setAskPrice3(String askPrice3) {
        this.askPrice3 = askPrice3;
    }

    public String getAskVolume3() {
        return askVolume3;
    }

    public void setAskVolume3(String askVolume3) {
        this.askVolume3 = askVolume3;
    }

    public String getBidPrice4() {
        return bidPrice4;
    }

    public void setBidPrice4(String bidPrice4) {
        this.bidPrice4 = bidPrice4;
    }

    public String getBidVolume4() {
        return bidVolume4;
    }

    public void setBidVolume4(String bidVolume4) {
        this.bidVolume4 = bidVolume4;
    }

    public String getAskPrice4() {
        return askPrice4;
    }

    public void setAskPrice4(String askPrice4) {
        this.askPrice4 = askPrice4;
    }

    public String getAskVolume4() {
        return askVolume4;
    }

    public void setAskVolume4(String askVolume4) {
        this.askVolume4 = askVolume4;
    }

    public String getBidPrice5() {
        return bidPrice5;
    }

    public void setBidPrice5(String bidPrice5) {
        this.bidPrice5 = bidPrice5;
    }

    public String getBidVolume5() {
        return bidVolume5;
    }

    public void setBidVolume5(String bidVolume5) {
        this.bidVolume5 = bidVolume5;
    }

    public String getAskPrice5() {
        return askPrice5;
    }

    public void setAskPrice5(String askPrice5) {
        this.askPrice5 = askPrice5;
    }

    public String getAskVolume5() {
        return askVolume5;
    }

    public void setAskVolume5(String askVolume5) {
        this.askVolume5 = askVolume5;
    }

    public String getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(String averagePrice) {
        this.averagePrice = averagePrice;
    }

    public String getActionDay() {
        return actionDay;
    }

    public void setActionDay(String actionDay) {
        this.actionDay = actionDay;
    }

    public String getQuotationDateTime() {
        return quotationDateTime;
    }

    public void setQuotationDateTime(String quotationDateTime) {
        this.quotationDateTime = quotationDateTime;
    }

    public String getStartDeliveryDate() {
        return startDeliveryDate;
    }

    public void setStartDeliveryDate(String startDeliveryDate) {
        this.startDeliveryDate = startDeliveryDate;
    }

    public String getDecimalPrecision() {
        return decimalPrecision;
    }

    public void setDecimalPrecision(String decimalPrecision) {
        this.decimalPrecision = decimalPrecision;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getChg() {
        return chg;
    }

    public void setChg(String chg) {
        this.chg = chg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取产品 excode|code
     *
     * @return
     */
    public String getCode() {
        return exchangeID + "|" + instrumentID;
    }

    /**
     * 封装算出 涨跌值
     * 使用BigDecimal相除 防止出现精度问题
     *
     * @return
     */
    public double getRate() {
        double diff = 0;
        if (!StringUtil.isNull(getSellone())
                && !StringUtil.isNull(getClosed())) {
            try {
                diff = NumberUtil.subtract(Double.parseDouble(getSellone()), Double.parseDouble(getClosed()));
                BigDecimal bigDecimal = new BigDecimal(diff);
                return bigDecimal.setScale(NUM_SCAL_RATE,
                        BigDecimal.ROUND_HALF_UP).doubleValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return diff;
    }

    /**
     * 封装算出 涨跌幅
     * 使用BigDecimal相除 防止出现精度问题
     *
     * @return x100之后的涨跌幅
     */
    public double getRateChange() {
        try {
            if (StringUtil.isNull(getClosed()))
                return 0;
            double rateChange = NumberUtil.divide(NumberUtil.multiply(getRate(), 100),
                    Double.parseDouble(getClosed()));
            BigDecimal bigDecimal = new BigDecimal(rateChange);
            return bigDecimal.setScale(NUM_SCAL_RATE_CHANGE,
                    BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 将实时的行情转换成一个 k线对象
     *
     * @return
     */
    public KCandleObj obj2KCandleObj() {
        try {
            KCandleObj kCandleObj = new KCandleObj();
//            kCandleObj.setOpen(Double.parseDouble(opening));
//            kCandleObj.setHigh(Double.parseDouble(highest));
//            kCandleObj.setLow(Double.parseDouble(lowest));
//            kCandleObj.setClose(Double.parseDouble(sellone));
            kCandleObj.setOpen(Double.parseDouble(openPrice));
            kCandleObj.setHigh(Double.parseDouble(highestPrice));
            kCandleObj.setLow(Double.parseDouble(lowestPrice));
            kCandleObj.setClose(Double.parseDouble(lastPrice));
            //2016-10-14 17:21:50
            kCandleObj.setTime(quotationDateTime);
            Log.v("Optional", "time=" + time);
            kCandleObj.setTimeLong(DateUtil.parser(quotationDateTime, "yyyy-MM-dd HH:mm:ss").getTime());
            return kCandleObj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }



}
