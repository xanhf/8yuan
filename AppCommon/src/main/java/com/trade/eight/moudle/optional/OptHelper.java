package com.trade.eight.moudle.optional;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.entity.Exchange;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;

import java.util.Map;

/**
 * Created by dufangzhu on 2017/4/13.
 * 自选行情  数据请求类
 */

public class OptHelper {
    /**
     * 获取交易所列表
     *
     * @param context
     * @return
     */
    public static CommonResponse4List<Exchange> getOptExChanges(Context context) {
        try {
            Map<String, String> map = ApiConfig.getParamMap(context, null);
            String res = HttpClientHelper.getStringFromGet(context, AndroidAPIConfig.URL_TAB_EX_LIST, map);
            if (res == null)
                return null;
            return CommonResponse4List.fromJson(res, Exchange.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取交易所列表并回调处理
     *
     * @param context
     * @param callback 回调处理
     */
    public static void getOptExChanges(final Context context, final Handler.Callback callback) {
        new AsyncTask<Void, Void, CommonResponse4List<Exchange>>() {
            @Override
            protected CommonResponse4List<Exchange> doInBackground(Void... params) {
                return getOptExChanges(context);
            }

            @Override
            protected void onPostExecute(CommonResponse4List<Exchange> response4List) {
                super.onPostExecute(response4List);
                if (callback != null) {
                    //不管是否成功都先回调
                    Message m = new Message();
                    m.obj = response4List;
                    callback.handleMessage(m);
                }

            }
        }.execute();
    }


}

