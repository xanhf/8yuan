package com.trade.eight.tools;


import android.app.Dialog;

/**
 * 作者：Created by ocean
 * 时间：on 2017/6/23.
 * 数据缓存类
 */

public class AppDataCache {
    static AppDataCache appDataCache;

    Dialog reLoginDialog;

    private AppDataCache(){

    }

    public static AppDataCache getDataCache(){
        if(appDataCache==null){
            appDataCache = new AppDataCache();
        }
        return  appDataCache;
    }

    public Dialog getReLoginDialog() {
        return reLoginDialog;
    }

    public void setReLoginDialog(Dialog reLoginDialog) {
        this.reLoginDialog = reLoginDialog;
    }
}
