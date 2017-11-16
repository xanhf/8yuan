package com.trade.eight.moudle.home.trade;

import com.trade.eight.moudle.baksource.BakSourceInterface;
import com.trade.eight.service.NumberUtil;

import java.io.Serializable;

/**
 * Created by dufangzhu on 2017/5/26.
 * 建仓设置止盈止损和持仓设置止盈止损的共有参数
 */

public class ProfitLossObj implements Serializable {

    protected String excode = BakSourceInterface.TRUDE_SOURCE_WEIPANP_FXBTG;
    protected String code;


    /*买入一手的预付款*/
    private double buyMargin;
    /*卖出一手的预付款*/
    private double sellMargin;
    /*止盈止损的增长步长*/
    private double minMovePoint;

    /*最小单位的浮动盈亏*/
    private double floatingPl;
    /*最小单位波动的单位 最小波动0.01个点;算点位的金钱：点位/calculatePoint*floatingPl */
    private double calculatePoint = 1;

    /*最小止损点*/
    private double minStopLoss;

    /*============注意这里的两个单词不一样==============*/
    /*产品列表盈点字段  最大止盈点*/
    private double minStopProfile;
    /*持仓列表盈点字段  最大止盈点*/
    private double minStopProfit;

    /*卖价*/
    private String sell;
    /*买价*/
    private String buy;

    /*=========================持仓的参数=========================*/
    /*建仓行情价格*/
    protected String createPrice;
    /*止损点数*/
    private double stopLossPoint;
    /*止盈点数*/
    private double stopProfitPoint;
    /*购买手数*/
    private String orderNumber;

    public double getFloatingPl() {
        return floatingPl;
    }

    public void setFloatingPl(double floatingPl) {
        this.floatingPl = floatingPl;
    }

    public double getBuyMargin() {
        return buyMargin;
    }

    public void setBuyMargin(double buyMargin) {
        this.buyMargin = buyMargin;
    }

    public double getSellMargin() {
        return sellMargin;
    }

    public void setSellMargin(double sellMargin) {
        this.sellMargin = sellMargin;
    }

    public double getMinMovePoint() {
        return minMovePoint;
    }

    public void setMinMovePoint(double minMovePoint) {
        this.minMovePoint = minMovePoint;
    }

    public double getCalculatePoint() {
        return calculatePoint;
    }

    public void setCalculatePoint(double calculatePoint) {
        this.calculatePoint = calculatePoint;
    }

    public double getMinStopLoss() {
        return minStopLoss;
    }

    public void setMinStopLoss(double minStopLoss) {
        this.minStopLoss = minStopLoss;
    }

    public double getMinStopProfile() {
        //如果是持仓get这个字段，minStopProfile为0 所以直接使用持仓时候的字段minStopProfit
        if (minStopProfile == 0)
            return minStopProfit;
        return minStopProfile;
    }

    public void setMinStopProfile(double minStopProfile) {
        this.minStopProfile = minStopProfile;
    }
    public String getSell() {
        return sell;
    }

    public void setSell(String sell) {
        this.sell = sell;
    }
    public String getBuy() {
        return buy;
    }

    public void setBuy(String buy) {
        this.buy = buy;
    }

    public String getCreatePrice() {
        return createPrice;
    }

    public void setCreatePrice(String createPrice) {
        this.createPrice = createPrice;
    }

    public double getStopLossPoint() {
        return stopLossPoint;
    }

    public void setStopLossPoint(double stopLossPoint) {
        this.stopLossPoint = stopLossPoint;
    }

    public double getStopProfitPoint() {
        return stopProfitPoint;
    }

    public void setStopProfitPoint(double stopProfitPoint) {
        this.stopProfitPoint = stopProfitPoint;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }


    /**
     *
     * @param point 波动的点数
     * @param countSize 手数
     * @return
     */
    public double getMoneyByPoint(double point,double countSize) {
        try {
            //getCalculatePoint  最小单位
            //getYkDouble()最小单位表示的金额
            double money = NumberUtil.multiply(NumberUtil.multiply(NumberUtil.divide(point, getCalculatePoint()),
                    getFloatingPl()),countSize);
            return money;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getMinStopProfit() {
        return minStopProfit;
    }

    public void setMinStopProfit(double minStopProfit) {
        this.minStopProfit = minStopProfit;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExcode() {
        return excode;
    }

    public void setExcode(String excode) {
        this.excode = excode;
    }

}
