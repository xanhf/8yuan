package com.trade.eight.moudle.trade;

import android.app.Dialog;
import android.content.Intent;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.entity.trade.TradeOrderAndUserInfoData;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.push.activity.OrderAndTradeNotifyActivity;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.refresh.RefreshUtil;
import com.trade.eight.tools.trade.TradeConfig;

import java.math.BigDecimal;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 2017/5/31.
 * 全局的用户交易信息以及持仓单信息
 */

public class TradeUserInfoData4Situation {

    String TAG = "TradeUserInfoData4Situation";

    protected BaseActivity context;
//    protected BaseFragment baseFragment;

    private static TradeUserInfoData4Situation tradeUserInfoData4Situation;

    // 用户的交易信息以及持仓单信息
    private TradeInfoData tradeInfoData;
    // 刷新线程
    RefreshUtil refreshUtil;

    TradeLoginDlg tradeLoginDlg;


    private TradeUserInfoData4Situation(BaseActivity context, BaseFragment baseFragment) {
        this.context = context;
//        this.baseFragment = baseFragment;
        initRefresh();
    }

    public static TradeUserInfoData4Situation getInstance(BaseActivity context, BaseFragment baseFragment) {
        if (tradeUserInfoData4Situation == null) {
            tradeUserInfoData4Situation = new TradeUserInfoData4Situation(context, baseFragment);
        }
        return tradeUserInfoData4Situation;
    }

    // 自定义回调接口
    public interface GetDataCallback {
        void onFailure(String resultCode, String resultMsg);

        void onResponse(TradeInfoData tradeInfoData);
    }

    /**
     * 初始化刷新
     */
    void initRefresh() {
        refreshUtil = new RefreshUtil(context);
        refreshUtil.setRefreshTime(AppSetting.getInstance(context).getRefreshTimeTradeInfo());
        refreshUtil.setOnRefreshListener(new RefreshUtil.OnRefreshListener() {
            @Override
            public Object doInBackground() {
                return getData();
            }

            @Override
            public void onUpdate(Object result) {
//                if (baseFragment.isDetached())
//                    return;
//                if (!baseFragment.isAdded())
//                    return;
                if (context.isFinishing())
                    return;

//                if (tradeInfoData != null) {
                    updateValue();
//                }
            }
        });
    }

    /**
     * 更新界面事件
     */
    private void updateValue() {
        if (tradeInfoData != null) {
            int safeOrDangerous = tradeInfoData.getTradeInfoSafeOrDangerous();
            if (safeOrDangerous != 0) {
                long current = System.currentTimeMillis();
                long before = 0;
                switch (safeOrDangerous) {
                    case TradeInfoData.TRADEINFO_DANGEROUS:
                        before = PreferenceSetting.getLong(context, TradeInfoData.KEY_DANGEROUS);
                        break;
                    case TradeInfoData.TRADEINFO_DANGEROUS_MOST_1:
                        before = PreferenceSetting.getLong(context, TradeInfoData.KEY_DANGEROUS_MOST_1);
                        break;
                    case TradeInfoData.TRADEINFO_DANGEROUS_MOST_2:
                        before = PreferenceSetting.getLong(context, TradeInfoData.KEY_DANGEROUS_MOST_2);
                        break;
                    case TradeInfoData.TRADEINFO_DANGEROUS_MOST_3:
                        before = PreferenceSetting.getLong(context, TradeInfoData.KEY_DANGEROUS_MOST_3);
                        break;
                }
                if (current - before > 5 * 60 * 1000) {// 超过5分钟就显示
                    OrderAndTradeNotifyActivity.startTradeNotifyAct(context, safeOrDangerous);
                }
            }
        }
        EventBus.getDefault().post(new UpdateTradeUserInfoEvent(tradeInfoData));
    }

    /**
     * 获取数据
     */
    TradeInfoData getData() {
        try {
            final CommonResponse<TradeInfoData> response4List = TradeHelp.getTradeInfo(context);
            if (response4List != null && response4List.isSuccess()) {
                tradeInfoData = response4List.getData();
                return tradeInfoData;
            } else {

                tradeInfoData = null;

                //token过期 避免重复弹框
                if (response4List != null && ApiConfig.isNeedLogin(response4List.getErrorCode())) {
                    //刷新过程中token过期
//                    if (tradeLoginDlg == null) {
//                        tradeLoginDlg = new TradeLoginDlg(context);
//                    }
//                    if (!tradeLoginDlg.isShowingDialog()) {
//                        tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
//                    }

                    AppSetting appSetting = AppSetting.getInstance(context);
                    //清理的token
                    appSetting.clearWPToken();
                    stopRefresh();
                }
                stopRefresh();
                if (response4List != null && ApiConfig.ERROR_CODE_RELOGIN.equals(response4List.getErrorCode())) {
                    stopRefresh();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化用户信息以及持仓单数据
     *
     * @param getDataCallback
     */
    public void loadTradeOrderAndUserInfoData(final GetDataCallback getDataCallback, boolean isShowLoading) {
        String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADEINFO);
        HttpClientHelper.doPostOption(context,
                url,
                null,
                null,
                new NetCallback(context) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
//                        if (ApiConfig.isNeedLogin(resultCode)) {
                            //刷新过程中token过期
//                            context.runOnUiThread(
//                                    new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            //重新登录
////                                            showDialog();
//                                            if (tradeLoginDlg == null) {
//                                                tradeLoginDlg = new TradeLoginDlg(context);
//                                            }
//                                            if (!tradeLoginDlg.isShowingDialog()) {
//                                                tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
//                                            }
//                                            stopRefresh();
//                                        }
//                                    }
//                            );
//                        } else {
                            resultMsg = ConvertUtil.NVL(resultMsg, context.getResources().getString(R.string.network_problem));
                            if (getDataCallback != null) {
                                getDataCallback.onFailure(resultCode, resultMsg);
                            }
//                        }
                        stopRefresh();
                    }

                    @Override
                    public void onResponse(String response) {
                        CommonResponse<TradeInfoData> responseForTrade = CommonResponse.fromJson(response, TradeInfoData.class);
                        if (responseForTrade != null
                                && responseForTrade.isSuccess()) {
                            tradeInfoData = responseForTrade.getData();
                            if (getDataCallback != null) {
                                getDataCallback.onResponse(tradeInfoData);
                            }

                            /*有持仓单才刷新*/
                            if (tradeInfoData != null) {
                                //开启刷新操作
                                startRefresh();
                            } else {
                                //重新load的时候没有持仓了就停止
                                stopRefresh();
                            }
                        }
                    }
                },
                isShowLoading);
    }

    /**
     * 开启刷新
     * 这里每次都是在初始化之后再检查是否调用
     */
    public void startRefresh() {
        if (refreshUtil != null)
            refreshUtil.startDelay(AppSetting.getInstance(context).getRefreshTimeTradeInfo());
    }

    /**
     * 停止刷新
     * 离开交易持仓页面 调用
     */
    public void stopRefresh() {
        if (refreshUtil != null)
            refreshUtil.stop();
    }


    public TradeInfoData getTradeInfoData() {
        return tradeInfoData;
    }

    public void setTradeInfoData(TradeInfoData tradeInfoData) {
        this.tradeInfoData = tradeInfoData;
    }
}
