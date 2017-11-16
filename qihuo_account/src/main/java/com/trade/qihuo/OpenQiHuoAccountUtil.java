package com.trade.qihuo;

import android.content.Context;
import android.content.Intent;

/**
 * 作者：Created by ocean
 * 时间：on 2017/6/28.
 * 开户操作
 */

public class OpenQiHuoAccountUtil {

    public static final String BROKERID = "0101";
    public static final String CHANNEL = "@200$088";
    public static final String PACKNAME = "com.trade.qihuo";


    /**
     * 执行开户
     * @param context
     */
    public static void openQiHuoAccount(Context context){
        Intent intent = new Intent(context, com.cfmmc.app.sjkh.MainActivity.class);
        intent.putExtra("brokerId", BROKERID);
        intent.putExtra("channel", CHANNEL);
        intent.putExtra("packName", PACKNAME);
        context.startActivity(intent);
    }
}
