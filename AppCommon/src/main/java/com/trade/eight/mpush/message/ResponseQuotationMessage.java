package com.trade.eight.mpush.message;

import com.trade.eight.entity.Optional;
import com.trade.eight.mpush.api.connection.Connection;
import com.trade.eight.mpush.api.protocol.Command;
import com.trade.eight.mpush.api.protocol.Packet;
import com.trade.eight.mpush.util.ByteBuf;

import java.nio.ByteBuffer;


/**
 * Created by yu on 2017/6/23.
 */
public class ResponseQuotationMessage extends ByteBufMessage {
    ///合约代码
    private String instrumentID;
    ///交易所代码
    private String exchangeID;
    ///最新价
    private String lastPrice;
    ///昨收盘
    private String preClosePrice;
    ///今开盘
    private String openPrice;
    ///最高价
    private String highestPrice;
    ///最低价
    private String lowestPrice;
    ///数量
    private String volume;
    ///成交金额
    private String turnover;
    ///持仓量
    private String openInterest;
    ///今收盘
    private String closePrice;
    ///本次结算价 结算价
    private String settlementPrice;
    ///涨停板价
    private String upperLimitPrice;
    ///跌停板价
    private String lowerLimitPrice;
    ///申买价一
    private String bidPrice1;
    ///申买量一
    private String bidVolume1;
    ///申卖价一
    private String askPrice1;
    ///申卖量一
    private String askVolume1;
    ///申买价二
    private String bidPrice2;
    ///申买量二
    private String bidVolume2;
    ///申卖价二
    private String askPrice2;
    ///申卖量二
    private String askVolume2;
    ///申买价三
    private String bidPrice3;
    ///申买量三
    private String bidVolume3;
    ///申卖价三
    private String askPrice3;
    ///申卖量三
    private String askVolume3;
    ///申买价四
    private String bidPrice4;
    ///申买量四
    private String bidVolume4;
    ///申卖价四
    private String askPrice4;
    ///申卖量四
    private String askVolume4;
    ///申买价五
    private String bidPrice5;
    ///申买量五
    private String bidVolume5;
    ///申卖价五
    private String askPrice5;
    ///申卖量五
    private String askVolume5;
    //行情时间
    private String quotationDateTime;
    //开始交割日
    private String startDeliveryDate;
    //涨跌
    private String change;
    //涨跌幅
    private String chg;
    //当日均价
    private String averagePrice;
    //昨结
    private String preSettlementPrice;

    public String getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(String averagePrice) {
        this.averagePrice = averagePrice;
    }

    public String getPreSettlementPrice() {
        return preSettlementPrice;
    }

    public void setPreSettlementPrice(String preSettlementPrice) {
        this.preSettlementPrice = preSettlementPrice;
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


    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getPreClosePrice() {
        return preClosePrice;
    }

    public void setPreClosePrice(String preClosePrice) {
        this.preClosePrice = preClosePrice;
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

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
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

    public ResponseQuotationMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    public ResponseQuotationMessage(Connection connection) {
        super(new Packet(Command.Quotation), connection);
    }


    @Override
    public void decode(ByteBuffer byteBuf) {
        instrumentID = decodeString(byteBuf);
        exchangeID = decodeString(byteBuf);
        lastPrice = decodeString(byteBuf);
        preClosePrice = decodeString(byteBuf);
        openPrice = decodeString(byteBuf);
        highestPrice = decodeString(byteBuf);
        lowestPrice = decodeString(byteBuf);
        volume = decodeString(byteBuf);
        turnover = decodeString(byteBuf);
        openInterest = decodeString(byteBuf);
        closePrice = decodeString(byteBuf);
        settlementPrice = decodeString(byteBuf);
        upperLimitPrice = decodeString(byteBuf);
        lowerLimitPrice = decodeString(byteBuf);
        bidPrice1 = decodeString(byteBuf);
        bidVolume1 = decodeString(byteBuf);
        askPrice1 = decodeString(byteBuf);
        askVolume1 = decodeString(byteBuf);
        bidPrice2 = decodeString(byteBuf);
        bidVolume2 = decodeString(byteBuf);
        askPrice2 = decodeString(byteBuf);
        askVolume2 = decodeString(byteBuf);
        bidPrice3 = decodeString(byteBuf);
        bidVolume3 = decodeString(byteBuf);
        askPrice3 = decodeString(byteBuf);
        askVolume3 = decodeString(byteBuf);
        bidPrice4 = decodeString(byteBuf);
        bidVolume4 = decodeString(byteBuf);
        askPrice4 = decodeString(byteBuf);
        askVolume4 = decodeString(byteBuf);
        bidPrice5 = decodeString(byteBuf);
        bidVolume5 = decodeString(byteBuf);
        askPrice5 = decodeString(byteBuf);
        askVolume5 = decodeString(byteBuf);
        quotationDateTime = decodeString(byteBuf);
        startDeliveryDate = decodeString(byteBuf);
        change = decodeString(byteBuf);
        chg = decodeString(byteBuf);
        averagePrice = decodeString(byteBuf);
        preSettlementPrice = decodeString(byteBuf);
    }

    @Override
    public void encode(ByteBuf byteBuf) {
        encodeString(byteBuf, instrumentID);
        encodeString(byteBuf, exchangeID);
        encodeString(byteBuf, lastPrice);
        encodeString(byteBuf, preClosePrice);
        encodeString(byteBuf, openPrice);
        encodeString(byteBuf, highestPrice);
        encodeString(byteBuf, lowestPrice);
        encodeString(byteBuf, volume);
        encodeString(byteBuf, turnover);
        encodeString(byteBuf, openInterest);
        encodeString(byteBuf, closePrice);
        encodeString(byteBuf, settlementPrice);
        encodeString(byteBuf, upperLimitPrice);
        encodeString(byteBuf, lowerLimitPrice);
        encodeString(byteBuf, bidPrice1);
        encodeString(byteBuf, bidVolume1);
        encodeString(byteBuf, askPrice1);
        encodeString(byteBuf, askVolume1);
        encodeString(byteBuf, bidPrice2);
        encodeString(byteBuf, bidVolume2);
        encodeString(byteBuf, askPrice2);
        encodeString(byteBuf, askVolume2);
        encodeString(byteBuf, bidPrice3);
        encodeString(byteBuf, bidVolume3);
        encodeString(byteBuf, askPrice3);
        encodeString(byteBuf, askVolume3);
        encodeString(byteBuf, bidPrice4);
        encodeString(byteBuf, bidVolume4);
        encodeString(byteBuf, askPrice4);
        encodeString(byteBuf, askVolume4);
        encodeString(byteBuf, bidPrice5);
        encodeString(byteBuf, bidVolume5);
        encodeString(byteBuf, askPrice5);
        encodeString(byteBuf, askVolume5);
        encodeString(byteBuf, quotationDateTime);
        encodeString(byteBuf, startDeliveryDate);
        encodeString(byteBuf, change);
        encodeString(byteBuf, chg);
        encodeString(byteBuf, averagePrice);
        encodeString(byteBuf, preSettlementPrice);
    }

    public Optional messageToOptional() {
        Optional optional = new Optional();
        optional.setInstrumentID(instrumentID);
        optional.setExchangeID(exchangeID);
        optional.setLastPrice(lastPrice);
        optional.setPreClosePrice(preClosePrice);
        optional.setOpenPrice(openPrice);
        optional.setHighestPrice(highestPrice);
        optional.setLowestPrice(lowestPrice);
        optional.setVolume(volume);
        optional.setTurnover(turnover);
        optional.setOpenInterest(openInterest);
        optional.setClosePrice(closePrice);
        optional.setSettlementPrice(settlementPrice);
        optional.setUpperLimitPrice(upperLimitPrice);
        optional.setLowerLimitPrice(lowerLimitPrice);
        optional.setBidPrice1(bidPrice1);
        optional.setBidVolume1(bidVolume1);
        optional.setAskPrice1(askPrice1);
        optional.setAskVolume1(askVolume2);
        optional.setBidPrice2(bidPrice2);
        optional.setBidVolume2(bidVolume2);
        optional.setAskPrice2(askPrice2);
        optional.setAskVolume2(askVolume2);
        optional.setBidPrice3(bidPrice3);
        optional.setBidVolume3(bidVolume3);
        optional.setAskPrice3(askPrice3);
        optional.setAskVolume3(askVolume3);
        optional.setBidPrice4(bidPrice4);
        optional.setBidVolume4(bidVolume4);
        optional.setAskPrice4(askPrice4);
        optional.setAskVolume4(askVolume4);
        optional.setBidPrice5(bidPrice5);
        optional.setBidVolume5(bidVolume5);
        optional.setAskPrice5(askPrice5);
        optional.setAskVolume5(askVolume5);
        optional.setQuotationDateTime(quotationDateTime);
        optional.setStartDeliveryDate(startDeliveryDate);
        optional.setChange(change);
        optional.setChg(chg);
        optional.setAveragePrice(averagePrice);
        optional.setPreSettlementPrice(preSettlementPrice);
        return optional;
    }


    @Override
    public String toString() {
        return "quotationDateTime:" + quotationDateTime+
                "instrumentID:" + instrumentID+
                "lastPrice:" + lastPrice;
    }
}
