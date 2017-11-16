package com.trade.eight.moudle.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.trade.eight.moudle.netty.NettyClient;
import com.trade.eight.tools.Log;


/**
 * Created by fangzhu
 * <p/>
 * app全局的网络变换监听
 * 行情的socket 网络变化后 重新连接
 */
public class AppNetReceiver extends BroadcastReceiver {
    static final String TAG = "AppNetReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                //有网络
                String name = info.getTypeName();
                Log.v(TAG, "connect net");
                NettyClient.getInstance(context).reStart();

            } else {
                //没有网络
                Log.v(TAG, "disconnect net");
            }
        }
    }
}
