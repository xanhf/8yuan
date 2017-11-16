package com.trade.eight.tools.trade;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppStartUpConfig;
import com.trade.eight.entity.Exchange;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.startup.StartupConfigObj;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.StringUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by fangzhu on 2017/1/17.
 * 交易所列表的配置
 * 读取网络配置  关联TradeConfig 使用
 */
public class ExchangeConfig {
    public static final int API_versionNo = 2;

    private static ExchangeConfig ourInstance = new ExchangeConfig();

    public static ExchangeConfig getInstance() {
        return ourInstance;
    }

    private ExchangeConfig() {
    }

    //交易所集合
    public HashMap<String, Exchange> exchangeHashMap = new LinkedHashMap<>();

    public HashMap<String, Exchange> getExchangeHashMap() {
        return exchangeHashMap;
    }

    public void setExchangeHashMap(HashMap<String, Exchange> exchangeHashMap) {
        this.exchangeHashMap = exchangeHashMap;
    }

    /**
     * 初始化配置信息
     *
     * @param callback 可以设置回调
     */
    public void init(final Context context, final Handler.Callback callback) {
        if (AppStartUpConfig.getInstance(context).getStartupConfigObj() == null)
            return;
        List<Exchange> list = AppStartUpConfig.getInstance(context).getStartupConfigObj().getExchangeList();
        if (list == null)
            return;

        clear();
        for (int i = 0; i < list.size(); i ++) {
            Exchange exchange = list.get(i);
            //第一次设置默认交易所
            if (StringUtil.isEmpty(TradeConfig.getCurrentTradeCode(context))) {
                TradeConfig.setCurrentTradeCode(context, exchange.getExcode());
            }
//            if(!exchange.getExcode().equals(BakSourceInterface.TRUDE_SOURCE_WEIPAN)){
                exchangeHashMap.put(exchange.getExcode(), exchange);
//            }
        }
        //处理回调
        if (callback != null) {
            Message message = new Message();
            message.obj = list;
            callback.handleMessage(message);
        }

    }

    /**
     * 检查初始化状态
     */
    public boolean isInited(Context context) {
        if (exchangeHashMap.size() == 0)
            return false;
        return true;
    }

    /**
     * 清除单例模式的缓存
     */
    public void clear() {
        if (exchangeHashMap != null)
            exchangeHashMap.clear();
    }

    /**
     * 网络获取交易所
     *
     * @return
     */
    public synchronized CommonResponse4List<Exchange> loadFromNet(Context context) {
        try {
            Map<String, String> map = ApiConfig.getCommonMap(context);
            map.put("versionNo", API_versionNo + "");
            map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, map));

            String url = AndroidAPIConfig.URL_EXCHANGE_LIST;
            String res = HttpClientHelper.getStringFromPost(context, url, map);
            return CommonResponse4List.fromJson(res, Exchange.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 检查是否初始化过交易所列表
     */
    public void check4TabTrade(Context context) {
        if (context == null)
            return;
        if (!AppStartUpConfig.getInstance(context).isInited()) {
            //还没有获取到数据
            AppStartUpConfig.getInstance(context).init(new AppStartUpConfig.OnConfigLoad() {
                @Override
                public void onConfigSuccess(StartupConfigObj obj) {
                    if (obj == null)
                        return;
                    if (obj.getExchangeList() == null)
                        return;
                    if (obj.getExchangeList().size() == 0)
                        return;
                    //发送event 修改交易所顺序
                    EventBus.getDefault().post(new Exchange());
                }
            });
        }
    }

}
