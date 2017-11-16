package com.trade.eight.entity.trade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/6.
 */

public class CheckBalanceData implements Serializable{

    private String creationDate;//	制表时间
    private String clientId;//	客户号
    private String date;//	时间
    private String clientName;//	客户名称
    private String balanceBF;//	初期结存
    private String initialMargin;//	基础保证金
    private String depositWithdrawal;//	出入金
    private String balanceCF;//	期末结存
    private String realizedPL;//	平仓盈亏
    private String pledgeAmount;//	质押金
    private String mtmPL;//	持仓盯市盈亏
    private String clientEquity;//	客户权益
    private String commission;//	手续费
    private String marginOccupied;//	保证金占用
    private String exercisePL;//	期权执行盈亏
    private String fx_pledge_occ;//	货币质押保证金占用
    private String exerciseFee;//	行权手续费
    private String deliveryMargin;//	交割保证金
    private String deliveryFee;//	交割手续费
    private String marketValueLong;//	多头期权市值
    private String newFXPledge;//	货币质入
    private String marketValueSHort;//	空头期权市值
    private String fxRedemption;//	货币质出
    private String marketValueEquity;//	市值权益
    private String chgInPledgeAmt;//	质押变化金额
    private String fundAvail;//	可用资金
    private String premiumReceive;//	权利金收入
    private String riskDegree;//	风 险 度
    private String premiumPaid;//	权利金支出
    private String marginCall;//	应追加资金
    private String currency;//	币种
    private List<TransactionRecord> transactionList;// 成交记录
    private List<PositionClose> closeList;
    private List<PositionsDetail> positionsDetailList;// 持仓明细记录
    private List<PositionsList> positionsList;// 持仓总汇
    private List<Deposit> depositList;//出入金明细


    // 成交记录
    public class TransactionRecord implements Serializable{
        private String date;//	成交日期
        private String exchange;//	交易所
        private String product;//	品种
        private String instrument;//	合约
        private String bs;//	买卖
        private String sh;//	投保
        private String price;//	成交价格
        private String lots;//	手数
        private String turnover;//	成交额
        private String oc;//	开平
        private String fee;//	手续费
        private String realizedPL;//	平仓盈亏
        private String premiumReceivedPaid;//	权力收支金
        private String transNo;//	成交序号

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public String getInstrument() {
            return instrument;
        }

        public void setInstrument(String instrument) {
            this.instrument = instrument;
        }

        public String getBs() {
            return bs;
        }

        public void setBs(String bs) {
            this.bs = bs;
        }

        public String getSh() {
            return sh;
        }

        public void setSh(String sh) {
            this.sh = sh;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getLots() {
            return lots;
        }

        public void setLots(String lots) {
            this.lots = lots;
        }

        public String getTurnover() {
            return turnover;
        }

        public void setTurnover(String turnover) {
            this.turnover = turnover;
        }

        public String getOc() {
            return oc;
        }

        public void setOc(String oc) {
            this.oc = oc;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getRealizedPL() {
            return realizedPL;
        }

        public void setRealizedPL(String realizedPL) {
            this.realizedPL = realizedPL;
        }

        public String getPremiumReceivedPaid() {
            return premiumReceivedPaid;
        }

        public void setPremiumReceivedPaid(String premiumReceivedPaid) {
            this.premiumReceivedPaid = premiumReceivedPaid;
        }

        public String getTransNo() {
            return transNo;
        }

        public void setTransNo(String transNo) {
            this.transNo = transNo;
        }
    }

    // 平仓明细
    public class PositionClose implements Serializable{
        private String exchange	;//	交易所
        private String product	;//	品种
        private String instrument	;//	合约
        private String openDate	;//	开仓日期
        private String bs	;//	买卖
        private String lots	;//	手数
        private String posOpenPrice	;//	开仓价
        private String prevSttl	;//	昨结算
        private String transPrice	;//	成交价格
        private String realizedPL	;//	平仓盈亏
        private String premiumReceivedPaid	;//	权利金收支

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public String getInstrument() {
            return instrument;
        }

        public void setInstrument(String instrument) {
            this.instrument = instrument;
        }

        public String getOpenDate() {
            return openDate;
        }

        public void setOpenDate(String openDate) {
            this.openDate = openDate;
        }

        public String getBs() {
            return bs;
        }

        public void setBs(String bs) {
            this.bs = bs;
        }

        public String getLots() {
            return lots;
        }

        public void setLots(String lots) {
            this.lots = lots;
        }

        public String getPosOpenPrice() {
            return posOpenPrice;
        }

        public void setPosOpenPrice(String posOpenPrice) {
            this.posOpenPrice = posOpenPrice;
        }

        public String getPrevSttl() {
            return prevSttl;
        }

        public void setPrevSttl(String prevSttl) {
            this.prevSttl = prevSttl;
        }

        public String getTransPrice() {
            return transPrice;
        }

        public void setTransPrice(String transPrice) {
            this.transPrice = transPrice;
        }

        public String getRealizedPL() {
            return realizedPL;
        }

        public void setRealizedPL(String realizedPL) {
            this.realizedPL = realizedPL;
        }

        public String getPremiumReceivedPaid() {
            return premiumReceivedPaid;
        }

        public void setPremiumReceivedPaid(String premiumReceivedPaid) {
            this.premiumReceivedPaid = premiumReceivedPaid;
        }
    }

    // 持仓明细
    public class PositionsDetail implements Serializable{
        private String exchange;//	交易所
        private String product;//	产品
        private String instrument;//	合约
        private String openDate;//	开仓日期
        private String sh;//	投保
        private String bs;//	买卖
        private String positon;//	持仓量
        private String posOpenPrice;//	开仓价
        private String privSttl;//	昨结算
        private String settlementPrice;//	结算价
        private String accumPL;//	浮动盈亏
        private String mtmPl;//	盯市盈亏
        private String margin;//	保证金
        private String marketValueOptions;//	期权市值

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public String getInstrument() {
            return instrument;
        }

        public void setInstrument(String instrument) {
            this.instrument = instrument;
        }

        public String getOpenDate() {
            return openDate;
        }

        public void setOpenDate(String openDate) {
            this.openDate = openDate;
        }

        public String getSh() {
            return sh;
        }

        public void setSh(String sh) {
            this.sh = sh;
        }

        public String getBs() {
            return bs;
        }

        public void setBs(String bs) {
            this.bs = bs;
        }

        public String getPositon() {
            return positon;
        }

        public void setPositon(String positon) {
            this.positon = positon;
        }

        public String getPosOpenPrice() {
            return posOpenPrice;
        }

        public void setPosOpenPrice(String posOpenPrice) {
            this.posOpenPrice = posOpenPrice;
        }

        public String getPrivSttl() {
            return privSttl;
        }

        public void setPrivSttl(String privSttl) {
            this.privSttl = privSttl;
        }

        public String getSettlementPrice() {
            return settlementPrice;
        }

        public void setSettlementPrice(String settlementPrice) {
            this.settlementPrice = settlementPrice;
        }

        public String getAccumPL() {
            return accumPL;
        }

        public void setAccumPL(String accumPL) {
            this.accumPL = accumPL;
        }

        public String getMtmPl() {
            return mtmPl;
        }

        public void setMtmPl(String mtmPl) {
            this.mtmPl = mtmPl;
        }

        public String getMargin() {
            return margin;
        }

        public void setMargin(String margin) {
            this.margin = margin;
        }

        public String getMarketValueOptions() {
            return marketValueOptions;
        }

        public void setMarketValueOptions(String marketValueOptions) {
            this.marketValueOptions = marketValueOptions;
        }
    }

    // 持仓总汇
    public class PositionsList implements Serializable{
        private String product;//	品种
        private String intrument;//	合约
        private String longPos;//	买持
        private String avgBuyPrice;//	买均
        private String shortPos;//	卖持
        private String avgSellPrice;//	卖均
        private String prevSttl;//	昨结算
        private String sttlToday;//	今结算
        private String mtmPL;//	持仓盯市盈亏
        private String margiOccupied;//	保证金占用
        private String sh;//	投保
        private String markeValueLong;//	多头期权市值
        private String marketValueShort;//	空头期权市值

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public String getIntrument() {
            return intrument;
        }

        public void setIntrument(String intrument) {
            this.intrument = intrument;
        }

        public String getLongPos() {
            return longPos;
        }

        public void setLongPos(String longPos) {
            this.longPos = longPos;
        }

        public String getAvgBuyPrice() {
            return avgBuyPrice;
        }

        public void setAvgBuyPrice(String avgBuyPrice) {
            this.avgBuyPrice = avgBuyPrice;
        }

        public String getShortPos() {
            return shortPos;
        }

        public void setShortPos(String shortPos) {
            this.shortPos = shortPos;
        }

        public String getAvgSellPrice() {
            return avgSellPrice;
        }

        public void setAvgSellPrice(String avgSellPrice) {
            this.avgSellPrice = avgSellPrice;
        }

        public String getPrevSttl() {
            return prevSttl;
        }

        public void setPrevSttl(String prevSttl) {
            this.prevSttl = prevSttl;
        }

        public String getSttlToday() {
            return sttlToday;
        }

        public void setSttlToday(String sttlToday) {
            this.sttlToday = sttlToday;
        }

        public String getMtmPL() {
            return mtmPL;
        }

        public void setMtmPL(String mtmPL) {
            this.mtmPL = mtmPL;
        }

        public String getMargiOccupied() {
            return margiOccupied;
        }

        public void setMargiOccupied(String margiOccupied) {
            this.margiOccupied = margiOccupied;
        }

        public String getSh() {
            return sh;
        }

        public void setSh(String sh) {
            this.sh = sh;
        }

        public String getMarkeValueLong() {
            return markeValueLong;
        }

        public void setMarkeValueLong(String markeValueLong) {
            this.markeValueLong = markeValueLong;
        }

        public String getMarketValueShort() {
            return marketValueShort;
        }

        public void setMarketValueShort(String marketValueShort) {
            this.marketValueShort = marketValueShort;
        }
    }
    //出入金明细
    public class Deposit implements Serializable{
        private String date	;//	发生时间
        private String type	;//	出入金类型
        private String deposit	;//	入金金额
        private String whthdrawal	;//	出金金额
        private String note	;//	说明

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDeposit() {
            return deposit;
        }

        public void setDeposit(String deposit) {
            this.deposit = deposit;
        }

        public String getWhthdrawal() {
            return whthdrawal;
        }

        public void setWhthdrawal(String whthdrawal) {
            this.whthdrawal = whthdrawal;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }

    public List<PositionClose> getCloseList() {
        return closeList;
    }

    public void setCloseList(List<PositionClose> closeList) {
        this.closeList = closeList;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getBalanceBF() {
        return balanceBF;
    }

    public void setBalanceBF(String balanceBF) {
        this.balanceBF = balanceBF;
    }

    public String getInitialMargin() {
        return initialMargin;
    }

    public void setInitialMargin(String initialMargin) {
        this.initialMargin = initialMargin;
    }

    public String getDepositWithdrawal() {
        return depositWithdrawal;
    }

    public void setDepositWithdrawal(String depositWithdrawal) {
        this.depositWithdrawal = depositWithdrawal;
    }

    public String getBalanceCF() {
        return balanceCF;
    }

    public void setBalanceCF(String balanceCF) {
        this.balanceCF = balanceCF;
    }

    public String getRealizedPL() {
        return realizedPL;
    }

    public void setRealizedPL(String realizedPL) {
        this.realizedPL = realizedPL;
    }

    public String getPledgeAmount() {
        return pledgeAmount;
    }

    public void setPledgeAmount(String pledgeAmount) {
        this.pledgeAmount = pledgeAmount;
    }

    public String getMtmPL() {
        return mtmPL;
    }

    public void setMtmPL(String mtmPL) {
        this.mtmPL = mtmPL;
    }

    public String getClientEquity() {
        return clientEquity;
    }

    public void setClientEquity(String clientEquity) {
        this.clientEquity = clientEquity;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getMarginOccupied() {
        return marginOccupied;
    }

    public void setMarginOccupied(String marginOccupied) {
        this.marginOccupied = marginOccupied;
    }

    public String getExercisePL() {
        return exercisePL;
    }

    public void setExercisePL(String exercisePL) {
        this.exercisePL = exercisePL;
    }

    public String getFx_pledge_occ() {
        return fx_pledge_occ;
    }

    public void setFx_pledge_occ(String fx_pledge_occ) {
        this.fx_pledge_occ = fx_pledge_occ;
    }

    public String getExerciseFee() {
        return exerciseFee;
    }

    public void setExerciseFee(String exerciseFee) {
        this.exerciseFee = exerciseFee;
    }

    public String getDeliveryMargin() {
        return deliveryMargin;
    }

    public void setDeliveryMargin(String deliveryMargin) {
        this.deliveryMargin = deliveryMargin;
    }

    public String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public String getMarketValueLong() {
        return marketValueLong;
    }

    public void setMarketValueLong(String marketValueLong) {
        this.marketValueLong = marketValueLong;
    }

    public String getNewFXPledge() {
        return newFXPledge;
    }

    public void setNewFXPledge(String newFXPledge) {
        this.newFXPledge = newFXPledge;
    }

    public String getMarketValueSHort() {
        return marketValueSHort;
    }

    public void setMarketValueSHort(String marketValueSHort) {
        this.marketValueSHort = marketValueSHort;
    }

    public String getFxRedemption() {
        return fxRedemption;
    }

    public void setFxRedemption(String fxRedemption) {
        this.fxRedemption = fxRedemption;
    }

    public String getMarketValueEquity() {
        return marketValueEquity;
    }

    public void setMarketValueEquity(String marketValueEquity) {
        this.marketValueEquity = marketValueEquity;
    }

    public String getChgInPledgeAmt() {
        return chgInPledgeAmt;
    }

    public void setChgInPledgeAmt(String chgInPledgeAmt) {
        this.chgInPledgeAmt = chgInPledgeAmt;
    }

    public String getFundAvail() {
        return fundAvail;
    }

    public void setFundAvail(String fundAvail) {
        this.fundAvail = fundAvail;
    }

    public String getPremiumReceive() {
        return premiumReceive;
    }

    public void setPremiumReceive(String premiumReceive) {
        this.premiumReceive = premiumReceive;
    }

    public String getRiskDegree() {
        return riskDegree;
    }

    public void setRiskDegree(String riskDegree) {
        this.riskDegree = riskDegree;
    }

    public String getPremiumPaid() {
        return premiumPaid;
    }

    public void setPremiumPaid(String premiumPaid) {
        this.premiumPaid = premiumPaid;
    }

    public String getMarginCall() {
        return marginCall;
    }

    public void setMarginCall(String marginCall) {
        this.marginCall = marginCall;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * 建仓单
     * @return*/

    public List<TransactionRecord> getTransactionList() {
        if(transactionList!=null&&transactionList.size()>0){
            List<TransactionRecord> list =  new ArrayList<>();
            for(TransactionRecord transactionRecord : transactionList){
                if(transactionRecord.getOc().equals("开")){
                    list.add(transactionRecord);
                }
            }
            return list;
        }
        return transactionList;
    }

    public void setTransactionList(List<TransactionRecord> transactionList) {
        this.transactionList = transactionList;
    }

    public List<PositionsDetail> getPositionsDetailList() {
        return positionsDetailList;
    }

    public void setPositionsDetailList(List<PositionsDetail> positionsDetailList) {
        this.positionsDetailList = positionsDetailList;
    }

    public List<PositionsList> getPositionsList() {
        return positionsList;
    }

    public void setPositionsList(List<PositionsList> positionsList) {
        this.positionsList = positionsList;
    }

    public List<Deposit> getDepositList() {
        return depositList;
    }

    public void setDepositList(List<Deposit> depositList) {
        this.depositList = depositList;
    }
}
