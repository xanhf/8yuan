package com.trade.eight.entity.integral;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 16/12/13.
 * 等级信息
 */

public class IntegralLevelInfo implements Serializable{
    private int levelNum	;//	等级
    private String levelName	;//	等级名称
    private String rebateRate	;//	享受的折扣率
    private int minExp	;//	等级经验范围-最小值
    private int maxExp	;//	等级经验范围-最大值

    public int getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getRebateRate() {
        return rebateRate;
    }

    public void setRebateRate(String rebateRate) {
        this.rebateRate = rebateRate;
    }

    public int getMinExp() {
        return minExp;
    }

    public void setMinExp(int minExp) {
        this.minExp = minExp;
    }

    public int getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(int maxExp) {
        this.maxExp = maxExp;
    }
}
