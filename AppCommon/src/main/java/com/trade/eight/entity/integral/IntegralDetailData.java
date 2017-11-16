package com.trade.eight.entity.integral;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 16/12/15.
 * 积分明细
 */

public class IntegralDetailData implements Serializable{
    private String pointSourceName	;//	积分来源描述
    private int pointsValue	;//	积分值
    private String createTimeStr	;//	发生时间

    public String getPointSourceName() {
        return pointSourceName;
    }

    public void setPointSourceName(String pointSourceName) {
        this.pointSourceName = pointSourceName;
    }

    public int getPointsValue() {
        return pointsValue;
    }

    public void setPointsValue(int pointsValue) {
        this.pointsValue = pointsValue;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }
}
