package com.trade.eight.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    public static String TAG = "NetworkUtil";

    public static boolean checkNetwork(final Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isConnected();
        } else {

        }

        return false;
    }

}
