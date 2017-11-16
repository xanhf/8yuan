package com.trade.eight.entity;

import java.io.Serializable;

/**
 * Created by fangzhu
 */
public class CallList implements Serializable {
    public static final int CHECK_IN = 1;
    public static final int CHECK_OUT = 2;

    private long outcryId;
    private String title;//标题
    private String limitation;//有效期
    private int operation;//操作方向：1=买入 2=卖出
    private String operationName;//操作方向 str
    private String operationPrice;//操作价格
    private String stopProfitPrice;//止盈价格
    private String stopLossPrice;//止损价格
    private String profitPoints;//止盈点数
    private String lossPoints;//止损点数
    private long createTime;//时间
    private String remark;//备注
    private String styleContent;// 带样式的内容，
    private int  top ;//(大于0的代表置顶。),
    private String label;//(多个标签逗号分隔)
//    private String metalName;//类型金属名称
//    private String v2Title;//V2版本的列表标题


    public String getStyleContent() {
        return styleContent;
    }

    public void setStyleContent(String styleContent) {
        this.styleContent = styleContent;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getOutcryId() {
        return outcryId;
    }

    public void setOutcryId(long outcryId) {
        this.outcryId = outcryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLimitation() {
        return limitation;
    }

    public void setLimitation(String limitation) {
        this.limitation = limitation;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public String getOperationPrice() {
        return operationPrice;
    }

    public void setOperationPrice(String operationPrice) {
        this.operationPrice = operationPrice;
    }

    public String getStopProfitPrice() {
        return stopProfitPrice;
    }

    public void setStopProfitPrice(String stopProfitPrice) {
        this.stopProfitPrice = stopProfitPrice;
    }

    public String getStopLossPrice() {
        return stopLossPrice;
    }

    public void setStopLossPrice(String stopLossPrice) {
        this.stopLossPrice = stopLossPrice;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getLossPoints() {
        return lossPoints;
    }

    public void setLossPoints(String lossPoints) {
        this.lossPoints = lossPoints;
    }

    public String getProfitPoints() {
        return profitPoints;
    }

    public void setProfitPoints(String profitPoints) {
        this.profitPoints = profitPoints;
    }
}
