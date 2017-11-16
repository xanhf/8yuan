package com.trade.eight.moudle.home;

import android.view.View;

import com.trade.eight.entity.home.HomeInformation;

/**
 * 作者：Created by ocean
 * 时间：on 16/10/19.
 * 行情预演  利多利空支持点击事件
 */

public class HomeInformationUpOrDownClickEvent {

    public View contentView;
    public HomeInformation homeInformation;
    public int clickType;
    public static final int MORE = 1;
    public static final int LESS = 2;
    public int idnex;

    public HomeInformationUpOrDownClickEvent(View contentView, HomeInformation homeInformation, int clickType,int idnex) {
        this.contentView = contentView;
        this.homeInformation = homeInformation;
        this.clickType = clickType;
        this.idnex = idnex;
    }
}
