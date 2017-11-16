package com.trade.eight.moudle.trade.alipay;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.trade.eight.base.BaseActivity;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.trade.TradeConfig;

import java.util.Map;


/**
 * Created by dufangzhu on 2017/3/29.
 * 阿里支付的帮助类
 */

public class AliPayUtil {

    /**
     * 支付宝的扫码支付
     *
     * @param cashInAct
     * @param url           api地址
     * @param money         金额
     * @param errorCallBack token过期 或者错误处理
     */
    public static void alipayScan(final BaseActivity cashInAct, final String url, final String money, final Handler.Callback errorCallBack) {
        if (!new UserInfoDao(cashInAct).isLogin())
            return;
        new AsyncTask<Void, Void, CommonResponse<AliPayObj>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                cashInAct.showNetLoadingProgressDialog(null);
            }

            @Override
            protected CommonResponse<AliPayObj> doInBackground(Void... params) {
                try {
                    UserInfoDao dao = new UserInfoDao(cashInAct);
                    if (!dao.isLogin())
                        return null;

                    Map<String, String> map = ApiConfig.getCommonMap(cashInAct);
                    map.put(TradeConfig.PARAM_EXCHANGEID, TradeConfig.getExchangeId(cashInAct, TradeConfig.getCurrentTradeCode(cashInAct)) + "");

                    map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
                    map.put("amount", money);
                    map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(cashInAct, map));

                    String res = TradeHelp.post(cashInAct, url, map);

                    return CommonResponse.fromJson(res, AliPayObj.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<AliPayObj> response) {
                super.onPostExecute(response);
                try {
                    if (cashInAct.isFinishing())
                        return;
                    cashInAct.hideNetLoadingProgressDialog();
                    if (response != null) {
                        AliPayObj data = response.getData();
                        if (response.isSuccess() && data != null) {
                            //掉用webview 处理打开
//                            AliPayWebAct.start(cashInAct, "跳转中", data.getCodeUrl());
                            doAliPay(cashInAct,data);

                        } else {
                            if (ApiConfig.isNeedLogin(response.getErrorCode())) {
                                //重新登录
                                Message msg = new Message();
                                msg.obj = response;
                                //1表示token过期
                                msg.what = 1;
                                if (errorCallBack != null)
                                    errorCallBack.handleMessage(msg);
                                return;
                            }
                            DialogUtil.showMsgDialog(cashInAct, ConvertUtil.NVL(response.getErrorInfo(), "提交失败"), "确定");
                        }
                    } else {
                        cashInAct.showCusToast("网络异常");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }


    public static void doAliPay(final BaseActivity cashInAct,AliPayObj data){
        if (data.getCodeUrl() == null)
            return;
        String url = null;
        if (data.getCodeUrl().startsWith("alipays://")) {
            url = data.getCodeUrl();
        } else {
                                /*
                              * 直接转换成最后的scheme
                             * */
//                            https%3A%2F%2Fqr.alipay.com%2Fbax009874zpa1gyqe9cj006e%3F_s%3Dweb-other&_t=1490866874370#Intent;scheme=alipays;package=com.eg.android.AlipayGphone;end
            String urlReg = "alipays://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode={qrcode}";
            url = urlReg.replace("{qrcode}", data.getCodeUrl());
        }
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            cashInAct.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            cashInAct.showCusToast("请下载最新版支付宝支付");
        }
    }
}
