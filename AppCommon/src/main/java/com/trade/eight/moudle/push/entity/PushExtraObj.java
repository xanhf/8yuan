package com.trade.eight.moudle.push.entity;

import android.text.TextUtils;

import com.trade.eight.config.ProFormatConfig;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.tools.trade.TradeConfig;

import java.io.Serializable;

/**
 * Created by fangzhu on 2017/1/16.
 * 推送的额外数据结构，json对象
 * 供扩展使用
 */

public class PushExtraObj implements Serializable {
    /*行情提醒字段 start******************/
    private long time;// 行情到达时间戳 1487231040029,
    private String excode;//  JCCE ,
    private String customizedProfit;// 定制点位 4025.0 ,
    private String mq;//  涨幅百分比-0.25% ,
    private String name;//  尿素 ,
    private String margin;//  涨幅点-10 ,
    private String code;//  URP ,
    private String reminderProfit;// 当前点位 4026.0
    private String pid;
    /*行情提醒字段 end******************/

    /*聊天室提醒字段 start******************/
    private String content;//公告内容
    private String roomId;//房间号
    private int times;//倒计时时间
    /*聊天室提醒字段 end******************/

    /*出金提醒字段 start******************/
    private String amountUSD;//30.00,
    private String charge;//20.00,
    private String factMoney;//205.13,
    private String logNo;//14969897325549801518,
    private String mark;//通过,
    public final static  int CASHOUT_SHOULI =1;
    public final static  int CASHOUT_SUCCESS =2;
    public final static  int CASHOUT_REFUSE =3;
    public final static  int CASHOUT_CHEXIAO =4;
//    type;//3  拒绝用户出金 4  撤销 1 受理 2 通过
    /*出金提醒字段 end******************/

    /*爆仓警告提醒字段 start******************/
    private String timeOutTxt;//:"六小时内不会再次提醒"
    /*爆仓警告字段 end******************/

    /*系统平仓提醒 start******************/
    private long userId;

    private String orderId; //订单ID

    private String productId;//产品ID

    private String productName;//productName

    private String closeTime;//平仓时间

    private String createTime;//建仓时间

    private String fee;//手续费

//    private int type;//买涨买跌

    private String orderNumber;//购买手数

    private String profitLoss;//盈亏

    private String createPrice;//建仓价格

    private String closePrice;//平仓价格

    private int closeType;//平仓类型

    private String amount;//建仓消费金额

    private String deferred;//过夜费


    private String stopProfit; //止盈

    private String stopLoss; //止损

    /*系统平仓提醒 end******************/


    /*订单反馈 start*************/
    /**
     * 买跌，买涨
     */
    private int type;
    /**
     * 建仓平仓
     */
    private int orderType;
    /**
     * 合约代码
     */
    private String instrumentId;
    /**
     * 合约名称
     */
    private String instrumentName;
    /**
     * 1、全部成功，2、部分成功，3：全部失败
     */
    private int status;
    /**
     * 全部数量
     */
    private int allCount;
    /**
     * 成功数量
     */
    private int successCount;
    /**
     * 失败数量
     */
    private int failCount;

    private String text;
    /*订单反馈 end***************/

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCloseType(int closeType) {
        this.closeType = closeType;
    }

    public String getTimeOutTxt() {
        return timeOutTxt;
    }

    public void setTimeOutTxt(String timeOutTxt) {
        this.timeOutTxt = timeOutTxt;
    }

    public String getAmountUSD() {
        return amountUSD;
    }

    public void setAmountUSD(String amountUSD) {
        this.amountUSD = amountUSD;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getFactMoney() {
        return factMoney;
    }

    public void setFactMoney(String factMoney) {
        this.factMoney = factMoney;
    }

    public String getLogNo() {
        return logNo;
    }

    public void setLogNo(String logNo) {
        this.logNo = logNo;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(String profitLoss) {
        this.profitLoss = profitLoss;
    }

    public String getCreatePrice() {
        return createPrice;
    }

    public void setCreatePrice(String createPrice) {
        this.createPrice = createPrice;
    }

    public String getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(String closePrice) {
        this.closePrice = closePrice;
    }

    public Integer getCloseType() {
        return closeType;
    }

    public void setCloseType(Integer closeType) {
        this.closeType = closeType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDeferred() {
        return deferred;
    }

    public void setDeferred(String deferred) {
        this.deferred = deferred;
    }

    public String getStopProfit() {
        if (TextUtils.isEmpty(stopProfit) || Double.parseDouble(stopProfit) == 0)
            return TradeOrder.LABLE_ZERO;
        return ProFormatConfig.formatByCodes((TextUtils.isEmpty(excode) ? TradeConfig.DEFAULT_EXCHANGE : excode) + "|" +code, stopProfit);
    }

    public void setStopProfit(String stopProfit) {
        this.stopProfit = stopProfit;
    }

    public String getStopLoss() {
        if (TextUtils.isEmpty(stopLoss) || Double.parseDouble(stopLoss) == 0)
            return TradeOrder.LABLE_ZERO;
        return ProFormatConfig.formatByCodes((TextUtils.isEmpty(excode) ? TradeConfig.DEFAULT_EXCHANGE : excode) + "|" +code, stopLoss);
    }

    public void setStopLoss(String stopLoss) {
        this.stopLoss = stopLoss;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getExcode() {
        return excode;
    }

    public void setExcode(String excode) {
        this.excode = excode;
    }

    public String getCustomizedProfit() {
        return customizedProfit;
    }

    public void setCustomizedProfit(String customizedProfit) {
        this.customizedProfit = customizedProfit;
    }

    public String getMq() {
        return mq;
    }

    public void setMq(String mq) {
        this.mq = mq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMargin() {
        return margin;
    }

    public void setMargin(String margin) {
        this.margin = margin;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getReminderProfit() {
        return reminderProfit;
    }

    public void setReminderProfit(String reminderProfit) {
        this.reminderProfit = reminderProfit;
    }
}
