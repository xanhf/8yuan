package com.trade.eight.config;

import android.content.Context;
import android.os.AsyncTask;

import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.startup.StartupConfigObj;
import com.trade.eight.entity.startup.StartupObj;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.netty.NettyClient;
import com.trade.eight.moudle.trade.fragment.CashInChooseMoneyFrag;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.trade.ExchangeConfig;
import com.trade.eight.tools.trade.TradeConfig;

import java.util.Map;

/**
 * Created by dufangzhu on 2017/2/14.
 * app启动获取的配置信息
 * 使用合并接口
 * 1、交易所列表
 * 2、首页热门产品
 * 3、tcp连接(首页弹窗广告，android应用推荐换量)
 * 4、app开启图片
 * 5、市场屏蔽开关
 */

public class AppStartUpConfig {
    public static final String TAG = "AppStartUpConfig";
    protected Context context;
    private static AppStartUpConfig ourInstance;

    public static AppStartUpConfig getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new AppStartUpConfig(context);
        return ourInstance;
    }

    private AppStartUpConfig(Context context) {
        this.context = context;
    }

    private StartupConfigObj startupConfigObj;

    /**
     * 获取接口信息成功
     */
    public interface OnConfigLoad {
        void onConfigSuccess(StartupConfigObj obj);
    }

    /**
     * 请求接口获取
     * 服务端的配置信息
     *
     * @param context
     * @param callback
     */
    public void loadConfig(final Context context, final OnConfigLoad callback) {
        new AsyncTask<Void, Void, CommonResponse<StartupConfigObj>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected CommonResponse<StartupConfigObj> doInBackground(Void... params) {
                try {
                    Log.v(TAG, "get config");
                    String url = AndroidAPIConfig.URL_STARTUP_CONFIG;
                    Map<String, String> map = ApiConfig.getCommonMap(context);
                    /**
                     * add at 2017-03-21
                     * 加了一个versionNo，userId 的参数
                     versionNo 是支付方式列表接口的versionNo，根据这个值来觉得交易所列表是否有微信支付的标识。
                     userId  用来判断这个用户是否需要显示广贵所的入口。
                     */
                    if (new UserInfoDao(context).isLogin()) {
                        map.put(UserInfo.UID, new UserInfoDao(context).queryUserInfo().getUserId());
                    }
                    map.put(CashInChooseMoneyFrag.P_API_VERSION_NO, CashInChooseMoneyFrag.VALUE_VERSION_NO + "");
                    map = ApiConfig.getParamMap(context, map);

                    String res = HttpClientHelper.getStringFromGet(context, url, map);
                    return CommonResponse.fromJson(res, StartupConfigObj.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<StartupConfigObj> response) {
                try {
                    super.onPostExecute(response);
                    if (response == null)
                        return;
                    if (response != null && response.isSuccess()) {
                        startupConfigObj = response.getData();
                        //保存热门产品
                        if (startupConfigObj != null) {
                            TradeConfig.setProductCodes(context, startupConfigObj.getProductList());
                        }
                        try {
                            //防止key获取不到 本地存一分
                            if (startupConfigObj != null) {
                                StartupObj.setLocalKey(context, startupConfigObj.getConfig().getK());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (callback != null) {
                            callback.onConfigSuccess(response.getData());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    /**
     * 初始化配置信息
     *
     * @param callbackExtr 额外的回调
     */
    public void init(final AppStartUpConfig.OnConfigLoad callbackExtr) {
        loadConfig(context, new AppStartUpConfig.OnConfigLoad() {
            @Override
            public void onConfigSuccess(StartupConfigObj obj) {
                if (obj == null)
                    return;
                if (obj.getConfig() == null)
                    return;

                //需要的回调处理
                //开启tcp连接
                NettyClient.getInstance(context).init();
                //设置交易所
                ExchangeConfig.getInstance().init(context, null);

                if (callbackExtr != null) {
                    callbackExtr.onConfigSuccess(obj);
                }
            }
        });
    }

    public boolean isInited() {
        if (startupConfigObj == null)
            return false;
        return true;
    }

    public void init() {
        init(null);
    }

    public StartupConfigObj getStartupConfigObj() {
        return startupConfigObj;
    }

    public void setStartupConfigObj(StartupConfigObj startupConfigObj) {
        this.startupConfigObj = startupConfigObj;
    }
}
