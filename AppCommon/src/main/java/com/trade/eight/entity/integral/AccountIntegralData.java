package com.trade.eight.entity.integral;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 16/12/13.
 */

public class AccountIntegralData implements Serializable {
    private int validPoints;//	可用积分
    private int totalPoints;//	累计积分
    private String pointsRanking;//	积分排名（百分比）
    private int totalExp;//	累计经验
    private int levelNum;//	当前等级
    private String levelName;//	当前等级名称
    private String rebateRate;//	当前等级享受的折扣率
    private int minExp;//	当前等级经验范围 最小值
    private int maxExp;//	当前等级经验范围 最大值
    private String nextLevelRate;//	达到下个等级经验的比率
    private int versionNo;//	版本号

    private List<IntegralLevelInfo> levelList;

    public List<IntegralLevelInfo> getLevelList() {
        return levelList;
    }

    public void setLevelList(List<IntegralLevelInfo> levelList) {
        this.levelList = levelList;
    }

    public int getValidPoints() {
        return validPoints;
    }

    public void setValidPoints(int validPoints) {
        this.validPoints = validPoints;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getPointsRanking() {
        return pointsRanking;
    }

    public void setPointsRanking(String pointsRanking) {
        this.pointsRanking = pointsRanking;
    }

    public int getTotalExp() {
        return totalExp;
    }

    public void setTotalExp(int totalExp) {
        this.totalExp = totalExp;
    }

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

    public String getNextLevelRate() {
        return nextLevelRate;
    }

    public void setNextLevelRate(String nextLevelRate) {
        this.nextLevelRate = nextLevelRate;
    }

    public int getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }
}
