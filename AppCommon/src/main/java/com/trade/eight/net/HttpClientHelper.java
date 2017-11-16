package com.trade.eight.net;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.trade.eight.base.BaseActivity;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.net.okhttp.OkHttpFactory;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.BaseInterface;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class HttpClientHelper {
    public static final boolean DEBUG = BaseInterface.isDubug;
    static final int CONNECTTIMEOUT = 10 * 1000;
    static final int READTIMEOUT = 10 * 1000;
    static String sCookie; //设置cookie服务端当成是同一个请求
    private static String TAG = "HttpClientHelper";

    public static final int REQUEST_SUCCESS =1;// 访问成功
    public static final int REQUEST_FAILED =2;// 访问失败

    private HttpClientHelper() {
    }

    public static InputStream getStreamFromGet(Context context, String url) {
        try {
            if (url == null || url.trim().length() == 0) {
                return null;
            }
            URL u = new URL(url);
            return (InputStream) u.getContent();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getStringFromGet(Context context, String path) {
        return OkHttpFactory.getInstance().getSync(NetWorkUtils.setParam4get(path, null)).toString();
    }

    public static String getStringFromGet(Context context, String api, Map<String, String> params) {
        return getStringFromGet(context, NetWorkUtils.setParam4get(api, params));
    }

    public static String getStringFromPost(Context context, String apiUrl, Map<String, String> params) {
        return getStringFromPost(context, apiUrl, params, null);
    }

    public static String getStringFromPost(Context context, String apiUrl, Map<String, String> params, Map<String, String> header) {
        return OkHttpFactory.getInstance().postSync(apiUrl, NetWorkUtils.getRequestData(params).toString(), header).toString();
    }

    /**ok网络访问  start add by ocean************************/
    /**
     * 最新的网络访问操作 get
     * @param baseActivity
     * @param url
     * @param requestMap
     * @param callback
     * @param isShowDialog
     */
    public static void doGetOption(BaseActivity baseActivity, String url, Map requestMap, NetCallback callback,boolean isShowDialog) {
        if (isShowDialog) {
            baseActivity.showNetLoadingProgressDialog("加载中");
        }
        OkHttpFactory.getInstance().getAsync(NetWorkUtils.setParam4get(url, ApiConfig.getParamMap(baseActivity, requestMap)), callback);
    }

    /**
     * 最新的网络访问操作 post
     * @param baseActivity
     * @param url
     * @param requestMap
     * @param header
     * @param callback
     * @param isShowDialog
     */
    public static void doPostOption(BaseActivity baseActivity, String url, Map requestMap, Map<String, String> header,NetCallback callback,boolean isShowDialog) {
        if (isShowDialog) {
            baseActivity.showNetLoadingProgressDialog("加载中");
        }
        OkHttpFactory.getInstance().postAsync(url, addCommonFields(baseActivity, requestMap), header, callback);
    }
    /**ok网络访问  end************************/


    /**
     * 此处拼接固定参数 并且转为str
     *
     * @param context
     * @param jsonMap // 请求map
     * @return
     */
    public static String addCommonFields(Context context, Map<String, String> jsonMap) {
        if (jsonMap == null) {
            jsonMap = new HashMap<String, String>();
        }
        jsonMap = ApiConfig.getParamMap(context, jsonMap);
        return NetWorkUtils.getRequestData(jsonMap).toString();
    }

    public static void setCookie(String cookie) {
        OkHttpFactory.getInstance().setCookie(cookie);
    }



}
