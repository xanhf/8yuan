package com.trade.eight.mpush.entity;

import com.trade.eight.moudle.push.entity.PushExtraObj;
import com.trade.eight.mpush.message.PushOrderNotifyMessage;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/12.
 * 订单反馈数据
 */

public class OrderNotifyData implements Serializable{
    /**
     * 买跌，买涨
     */
    private int type;
    /**
     * 建仓平仓
     */
    private int orderType;// 0  建仓 1 平仓
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

    public int getType() {
        return type;
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

    /**
     * 消息转为实体
     * @param pushOrderNotifyMessage
     * @return
     */
    public static OrderNotifyData messageToData(PushOrderNotifyMessage pushOrderNotifyMessage){
        OrderNotifyData orderNotifyData = new OrderNotifyData();

        orderNotifyData.setType(pushOrderNotifyMessage.getType());
        orderNotifyData.setOrderType(pushOrderNotifyMessage.getOrderType());
        orderNotifyData.setInstrumentId(pushOrderNotifyMessage.getInstrumentId());
        orderNotifyData.setInstrumentName(pushOrderNotifyMessage.getInstrumentName());
        orderNotifyData.setStatus(pushOrderNotifyMessage.getStatus());
        orderNotifyData.setAllCount(pushOrderNotifyMessage.getAllCount());
        orderNotifyData.setSuccessCount(pushOrderNotifyMessage.getSuccessCount());
        orderNotifyData.setFailCount(pushOrderNotifyMessage.getFailCount());
        orderNotifyData.setText(pushOrderNotifyMessage.getText());

        return orderNotifyData;
    }

    public static OrderNotifyData pushExtraObjToData(PushExtraObj pushExtraObj){
        OrderNotifyData orderNotifyData = new OrderNotifyData();

        orderNotifyData.setType(pushExtraObj.getType());
        orderNotifyData.setOrderType(pushExtraObj.getOrderType());
        orderNotifyData.setInstrumentId(pushExtraObj.getInstrumentId());
        orderNotifyData.setInstrumentName(pushExtraObj.getInstrumentName());
        orderNotifyData.setStatus(pushExtraObj.getStatus());
        orderNotifyData.setAllCount(pushExtraObj.getAllCount());
        orderNotifyData.setSuccessCount(pushExtraObj.getSuccessCount());
        orderNotifyData.setFailCount(pushExtraObj.getFailCount());
        orderNotifyData.setText(pushExtraObj.getText());

        return orderNotifyData;
    }

    @Override
    public String toString() {
        return
                "{type:" + type +
                        "{orderType:" + orderType +
                        "{instrumentId:" + type +
                        "{instrumentName:" + instrumentName +
                        "{status:" + status +
                        "{allCount:" + allCount +
                        "{successCount:" + successCount +
                        "{failCount:" + failCount +
                        "{text:" + text +
                        "}";
    }
}
