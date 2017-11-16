package com.trade.eight.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 16/10/28.
 * 盈利榜最新排名数据
 */

public class RankMostNewData implements Serializable {
    private List<String> avatars;//头像数组，最新嗮单用户头像
    private int isClose;//	是否休市，2=休市，1=没有休市
    private String myAvatar;//	当前用户头像
    private int myRanking;//	当前用户排名，0=没有排名
    private int showOrderNum;//嗮单人数

//    private int isLogin;// 冗余字段 0未登录 1登录
//
//    public int getIsLogin() {
//        return isLogin;
//    }
//
//    public void setIsLogin(int isLogin) {
//        this.isLogin = isLogin;
//    }

    public List<String> getAvatars() {
        return avatars;
    }

    public void setAvatars(List<String> avatars) {
        this.avatars = avatars;
    }

    public int getIsClose() {
        return isClose;
    }

    public void setIsClose(int isClose) {
        this.isClose = isClose;
    }

    public String getMyAvatar() {
        return myAvatar;
    }

    public void setMyAvatar(String myAvatar) {
        this.myAvatar = myAvatar;
    }

    public int getMyRanking() {
        return myRanking;
    }

    public void setMyRanking(int myRanking) {
        this.myRanking = myRanking;
    }

    public int getShowOrderNum() {
        return showOrderNum;
    }

    public void setShowOrderNum(int showOrderNum) {
        this.showOrderNum = showOrderNum;
    }
}
