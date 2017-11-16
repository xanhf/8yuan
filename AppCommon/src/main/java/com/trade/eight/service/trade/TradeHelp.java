package com.trade.eight.service.trade;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.easylife.ten.lib.R;
import com.google.gson.Gson;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.Exchange;
import com.trade.eight.entity.OrderDeferred;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.response.CommonResponseBase;
import com.trade.eight.entity.trade.Banks;
import com.trade.eight.entity.trade.CashOutFee;
import com.trade.eight.entity.trade.TradeCashOut;
import com.trade.eight.entity.trade.TradeDetail;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.entity.trade.TradeOrderAndUserInfoData;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.entity.trade.TradeRechargeHistory;
import com.trade.eight.entity.trade.TradeRedPacket;
import com.trade.eight.entity.trade.TradeVoucher;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.baksource.BakSourceInterface;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.HttpsVerifyUtil;
import com.trade.eight.net.NetWorkUtils;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.net.okhttp.OkHttpFactory;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trude.UserService;
import com.trade.eight.tools.AppDataCache;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.trade.TradeConfig;
import com.trade.eight.tools.trade.TradeSortUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by developer on 16/1/11.
 * <p>
 * 交易数据网络获取类
 * <p>
 * 交易相关的一定得登录了得
 * 所以userId 直接不作为参数传入了
 */
public class TradeHelp {
    public static final int COUNT_MAX_BUY = 10;
    public static final String TAG = "TradeHelp";

    /**
     * 本地检测token是否过期 这里已经分交易所的token了
     * getRefreshTimeWPToken
     *
     * @param context
     * @return
     */
    public static boolean isTokenEnable(BaseActivity context) {
        if (context == null)
            return true;

        UserInfoDao dao = new UserInfoDao(context);
        if (!dao.isLogin())
            return false;
        long t = AppSetting.TIME_WP_TOKEN;
        if (TradeConfig.isCurrentHG(context))
            t = AppSetting.TIME_WP_TOKEN_HG;

        if (System.currentTimeMillis() - AppSetting.getInstance(context).getRefreshTimeWPToken(context) > t) {
            return false;
        }
        return true;
    }

    /**
     * 本地检测token是否过期 按交易所
     *
     * @param context
     * @param excode
     * @return
     */
    public static boolean isTokenEnable(BaseActivity context, String excode) {
        if (context == null)
            return true;

        UserInfoDao dao = new UserInfoDao(context);
        if (!dao.isLogin())
            return false;
        long t = AppSetting.TIME_WP_TOKEN;
        if (excode.equals(BakSourceInterface.TRUDE_SOURCE_WEIPANP_HG)) {
            t = AppSetting.TIME_WP_TOKEN_HG;
        }

        if (System.currentTimeMillis() - AppSetting.getInstance(context).getRefreshTimeWPToken(context, excode) > t) {
            return false;
        }
        return true;
    }

    /**
     * 判断所有交易所token是否均过期
     *
     * @return
     */
    public static boolean judgeAllTokenEnable(BaseActivity context) {
        HashMap<String, Exchange> tradeMap = TradeConfig.getTradeMap();
        Set<String> trades = tradeMap.keySet();
        Iterator<String> iterator = trades.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            if (TradeHelp.isTokenEnable(context, iterator.next())) {
                i++;
            }
        }
        if (i == trades.size()) {
            return true;
        }
        return false;
    }


    public static CommonResponse4List<List<TradeProduct>> getTypeProductList(BaseActivity context) {
        String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_PRODUCT_LIST);
        return getTypeProductList(context, url);
    }

    /**
     * 封装弄成油 银  相应类别对应的多个品种
     * 油 8 80 200
     *
     * @param context
     * @return
     */
    public static CommonResponse4List<List<TradeProduct>> getTypeProductList(BaseActivity context, String url) {
        try {
            if (context == null)
                return null;

            Map<String, String> map = ApiConfig.getParamMap(context, null);
//            String res = HttpClientHelper.getStringFromPost(context, url, map);
            String res = post(context, url, map);
            CommonResponse4List<TradeProduct> response4List = CommonResponse4List.fromJson(res, TradeProduct.class);
            if (response4List == null)
                return null;
            List<TradeProduct> list = response4List.getData();

            //最外层的list
            List<List<TradeProduct>> mainList = TradeSortUtil.sortProducts(list);

            CommonResponse4List commonResponse4List = new CommonResponse4List();
            commonResponse4List.setData(mainList);
            commonResponse4List.setSuccess(response4List.isSuccess());
            commonResponse4List.setErrorCode(response4List.getErrorCode());
            commonResponse4List.setErrorInfo(response4List.getErrorInfo());

            return commonResponse4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * post 请求 可用于  注册重定向  登录的重定向  找回密码的重定向
     * 注意 这里的方法有公用  不能直接简单修改
     * 获取head里头的token 和返回的string res
     *
     * @param context
     * @param api     api
     * @param map     map
     * @return
     */
    public static CommonResponse<UserInfo> redirect(Context context, String api, Map<String, String> map) {
        try {
            if (context == null)
                return null;
            UserInfoDao dao = new UserInfoDao(context);
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = getHttpURLConnection(context, api, map, false);
                if (httpURLConnection == null)
                    return null;
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String token = httpURLConnection.getHeaderField(ApiConfig.PARAM_TRADE_REPONSE_HEADER);
                    if (!StringUtil.isEmpty(token)) {
                        token = token.replaceFirst("token=", "");
                        //本地设置token
                        AppSetting.getInstance(context).setWPToken(context, token);
                        //本地设置token获取时间
                        AppSetting.getInstance(context).setRefreshTimeWPToken(context, System.currentTimeMillis());
                    }

                    String str = StringUtil.convertStreamToString(httpURLConnection.getInputStream());
                    CommonResponse<UserInfo> commonResponse = CommonResponse.fromJson(str, UserInfo.class);
                    if (commonResponse != null && commonResponse.isSuccess()) {
                        //存入本地的 用户信息  包括 token的值
                        UserInfo userInfo = commonResponse.getData();
                        if (userInfo != null) {
                            if (!dao.isLogin()) {
                                userInfo.setToken(token);
                                dao.addOrUpdate(userInfo);
                            } else {
                                //先查到本地的的userinfo  然后替换网络重新登录的信息
                                UserInfo userLocal = dao.queryUserInfo();
                                userLocal.setToken(token);
                                userLocal.setNickName(userInfo.getNickName());
                                userLocal.setMobileNum(userInfo.getMobileNum());
                                dao.addOrUpdate(userLocal);
                            }

                        }
                    }
                    return commonResponse;

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取持仓list
     *
     * @param context
     * @return
     */
    public static CommonResponse4List<TradeOrder> getTradeOrderList(BaseActivity context) {
        try {
            if (context == null)
                return null;
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_ORDER_HOLD_LIST);
            Map<String, String> map = new LinkedHashMap<>();

            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map = ApiConfig.getParamMap(context, map);
            long t = System.currentTimeMillis();
            String res = post(context, url, map);
            Log.v(TAG, TradeConfig.getCurrentTradeCode(context) + " t=" + (System.currentTimeMillis() - t));
            CommonResponse4List<TradeOrder> response4List = CommonResponse4List.fromJson(res, TradeOrder.class);

            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 交易记录
     *
     * @param context
     * @return
     */
    public static CommonResponse4List<TradeOrder> getTradeHistoryList(BaseActivity context, int page) {
        try {
            if (context == null)
                return null;

            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_GET_HISTORY_LIST);
//            String url = AndroidAPIConfig.URL_TRADE_GET_HISTORY_LIST;
            Map<String, String> map = new LinkedHashMap<>();
            map.put("page", "" + page);
            map.put("pageSize", "20");
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());

            map = ApiConfig.getParamMap(context, map);

            String res = post(context, url, map);
            CommonResponse4List<TradeOrder> response4List = CommonResponse4List.fromJson(res, TradeOrder.class);
            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用户持有的银行卡
     *
     * @param context
     * @return
     */
    public static CommonResponse4List<Banks> getBankList(BaseActivity context) {
        try {
            if (context == null)
                return null;

            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_GET_USER_BANKS);
            Map<String, String> map = new LinkedHashMap<>();

            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map = ApiConfig.getParamMap(context, map);

            String res = post(context, url, map);
            CommonResponse4List<Banks> response4List = CommonResponse4List.fromJson(res, Banks.class);
            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 资金明细页面
     * 收支明细list
     *
     * @param context
     * @return
     */
    public static CommonResponse4List<TradeDetail> getTradeDetailList(BaseActivity context, int page) {
        try {
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_GET_DETAIL_LIST);
//            String url = AndroidAPIConfig.URL_TRADE_GET_DETAIL_LIST;
            Map<String, String> map = new LinkedHashMap<>();
            map.put("page", "" + page);
            map.put("pageSize", "10");
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());

            map = ApiConfig.getParamMap(context, map);

            String res = post(context, url, map);
            CommonResponse4List<TradeDetail> response4List = CommonResponse4List.fromJson(res, TradeDetail.class);
            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 资金明细页面
     * 充值list
     *
     * @param context
     * @return
     */
    public static CommonResponse4List<TradeRechargeHistory> getRechargeList(BaseActivity context, int page) {
        try {
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_CASHIN_HISTORY);
            Map<String, String> map = new LinkedHashMap<>();
            map.put("page", "" + page);
            map.put("pageSize", "10");
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());

            map = ApiConfig.getParamMap(context, map);

            String res = post(context, url, map);
            CommonResponse4List<TradeRechargeHistory> response4List = CommonResponse4List.fromJson(res, TradeRechargeHistory.class);
            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 资金明细页面
     * 提现list
     *
     * @param context
     * @return
     */
    public static CommonResponse4List<TradeCashOut> getCashOutList(BaseActivity context, int page) {
        try {
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_CASHOUT_HISTORY);
            Map<String, String> map = new LinkedHashMap<>();
            map.put("page", "" + page);
            map.put("pageSize", "10");
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());

            map = ApiConfig.getParamMap(context, map);

            String res = post(context, url, map);
            CommonResponse4List<TradeCashOut> response4List = CommonResponse4List.fromJson(res, TradeCashOut.class);
            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 资金代金券页面
     * 代金券list
     *
     * @param context
     * @return
     */
    public static CommonResponse4List<TradeVoucher> getTradeVoucherList(BaseActivity context, int page) {
        try {
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_GET_VOUCHER_LIST);
            Map<String, String> map = new LinkedHashMap<>();
            map.put("page", "" + page);
            map.put("pageSize", "20");
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map = ApiConfig.getParamMap(context, map);

            String res = post(context, url, map);
            CommonResponse4List<TradeVoucher> response4List = CommonResponse4List.fromJson(res, TradeVoucher.class);
            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 资金代金券页面  按交易所
     * 代金券list
     *
     * @param context
     * @return
     */
    public static CommonResponse4List<TradeVoucher> getTradeVoucherListByExchange(BaseActivity context, int page, int exchangeId) {
        try {
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = "";
            switch (exchangeId) {
                case 1:
                    url = AndroidAPIConfig.URL_TRADE_GET_VOUCHER_LIST_GG;
                    break;
                case 2:
                    url = AndroidAPIConfig.URL_TRADE_GET_VOUCHER_LIST_HG;
                    break;
                case 3:
                    url = AndroidAPIConfig.URL_TRADE_GET_VOUCHER_LIST_JN;
                    break;
            }
            Map<String, String> map = new LinkedHashMap<>();
            map.put("page", "" + page);
            map.put("pageSize", "20");
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map = ApiConfig.getParamMap(context, map);

            String res = post(context, url, map);
            CommonResponse4List<TradeVoucher> response4List = CommonResponse4List.fromJson(res, TradeVoucher.class);
            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 我的红包页面
     * 红包明细
     *
     * @param context
     * @return
     */
    public static CommonResponse<TradeRedPacket> getRedPacketList(BaseActivity context, int page) {
        try {
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.URL_REDPACKET_LIST_JN;
            Map<String, String> map = new LinkedHashMap<>();
            map.put("page", "" + page);
            map.put("pageSize", "20");
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map = ApiConfig.getParamMap(context, map);

            String res = post(context, url, map);
            CommonResponse<TradeRedPacket> response = CommonResponse.fromJson(res, TradeRedPacket.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 过夜费
     *
     * @param context
     * @param orderId
     * @return
     */
    public static CommonResponse4List<OrderDeferred> getDeferredList(BaseActivity context, String url, String orderId) {
        try {
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            Map<String, String> map = new LinkedHashMap<>();
            map.put("orderId", orderId);
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map = ApiConfig.getParamMap(context, map);

            String res = post(context, url, map);
            CommonResponse4List<OrderDeferred> response4List = CommonResponse4List.fromJson(res, OrderDeferred.class);
            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 资金代金券页面
     * 可领取红包list
     *
     * @param context
     * @return
     */
    public static CommonResponse4List<TradeVoucher> getTradeCouponList(BaseActivity context) {
        try {
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_GET_VOUCHER_LIST);
            Map<String, String> map = new LinkedHashMap<>();
//            map.put("page", "" + page);
//            map.put("pageSize", "20");
//            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
//            map = ApiConfig.getParamMap(context, map);

            String res = post(context, url, map);
            CommonResponse4List<TradeVoucher> response4List = CommonResponse4List.fromJson(res, TradeVoucher.class);
            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 领取红包
     *
     * @return
     */
    public static CommonResponse receiveCoupon() {
        return null;
    }

    /**
     * 获取当前产品的 代金券
     *
     * @param context
     * @param productId 产品id
     * @return
     */
    public static CommonResponse4List<TradeVoucher> getTradeVoucherByPid(BaseActivity context, String productId, String couponId) {
        try {
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_PID_VOUCHER);
            Map<String, String> map = new LinkedHashMap<>();
            map.put(TradeProduct.PARAM_PID, productId);
            map.put(TradeProduct.PARAM_COUPONID, couponId);
//            map.put("pageSize", "20");
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map = ApiConfig.getParamMap(context, map);

            String res = post(context, url, map);
            CommonResponse4List<TradeVoucher> response4List = CommonResponse4List.fromJson(res, TradeVoucher.class);
            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static CommonResponse4List<TradeVoucher> getTradeVoucher(BaseActivity context) {
        try {
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_PID_VOUCHER);
            Map<String, String> map = ApiConfig.getCommonMap(context);
//            map.put(TradeProduct.PARAM_PID, productId);
//            map.put(TradeProduct.PARAM_COUPONID, couponId);
//            map.put("pageSize", "20");
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, map));

            String res = post(context, url, map);
            CommonResponse4List<TradeVoucher> response4List = CommonResponse4List.fromJson(res, TradeVoucher.class);
            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 农交所的对应产品的代金券
     * 农交所是按照单个产品查的，其他交易所是查的所有的
     *
     * @param context
     * @return
     */
    public synchronized static CommonResponse<TradeVoucher> getPVoucherJN(BaseActivity context, String productId) {
        try {
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_PID_VOUCHER);
            Map<String, String> map = ApiConfig.getCommonMap(context);
//            map.put(TradeProduct.PARAM_PID, productId);
//            map.put(TradeProduct.PARAM_COUPONID, couponId);
//            map.put("pageSize", "20");
            map.put("productId", productId);
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, map));

            String res = post(context, url, map);
            CommonResponse<TradeVoucher> response4List = CommonResponse.fromJson(res, TradeVoucher.class);
            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 平仓
     *
     * @param context
     * @param orderId 订单号
     * @return
     */
    public static CommonResponse<TempObject> closeOrder(BaseActivity context, String orderId) {
        try {
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_ORDER_CLOSE);
            Map<String, String> map = ApiConfig.getCommonMap(context);

            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map.put("orderId", orderId);
            map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, map));

            String res = post(context, url, map);
            CommonResponse<TempObject> response = CommonResponse.fromJson(res, TempObject.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 平仓
     *
     * @param context
     * @param map     type	true	普通参数	String		平仓仓位方向，1：跌，2：涨
     *                price	true	普通参数	String		平仓行情价格
     *                instrumentId	true	普通参数	String		合约代码
     *                todayPosition	true	普通参数	String		用来做上期所的平今判断
     * @return
     */
    public static CommonResponse<TempObject> closeOrder(BaseActivity context, HashMap<String, String> map) {
        try {
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_ORDER_CLOSE);
            Map<String, String> reqMap = ApiConfig.getParamMap(context, map);
            String res = post(context, url, reqMap);
            CommonResponse<TempObject> response = CommonResponse.fromJson(res, TempObject.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 登录情况下 获取用户余额
     *
     * @param context
     * @return
     */
    public static CommonResponse<UserInfo> getUserBalance(BaseActivity context) {
        try {
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_GET_USERINFO);
            Map<String, String> map = ApiConfig.getCommonMap(context);

            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, map));

            String res = post(context, url, map);
            CommonResponse<UserInfo> response = CommonResponse.fromJson(res, UserInfo.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 建仓 字段太多不封装了
     * userId	Long(数字)	用户id
     * productId	Integer (数字)	产品id
     orderNumber	Integer (数字)	购买数量，1-50之内
     type	Integer (数字)	购买方向：1＝跌 2＝涨
     isJuan	Integer (数字)	使用代金劵: 1=使用0=不使用
     stopProfit	Integer (数字)	止盈，值：10-90
     stopLoss	Integer (数字)	止损，值：10-90

     */
//    public static String createOrder(){
//
//    }

    /**
     * post  请求
     * 封装了header
     *
     * @param params
     * @param apiUrl
     * @return
     * @throws Exception
     */
    public static String post(final BaseActivity context, String apiUrl, Map<String, String> params) throws Exception {


        Map<String, String> header = new HashMap<String, String>();
        String token = AppSetting.getInstance(context).getWPToken(context);
        header.put(ApiConfig.PARAM_TRADE_REQUEST_HEADER,
                ApiConfig.PARAM_TRADE_TOKEN + "=" + token);

        String reponse = OkHttpFactory.getInstance().postSync(apiUrl, NetWorkUtils.getRequestData(params).toString(), header).toString();
        CommonResponseBase commonResponse = new Gson().fromJson(reponse, CommonResponseBase.class);
        if (commonResponse != null && ApiConfig.ERROR_CODE_RELOGIN.equals(commonResponse.getErrorCode())) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialog reLoginDialog = AppDataCache.getDataCache().getReLoginDialog();
                    if (reLoginDialog != null && reLoginDialog.isShowing()) {
                        return;
                    }
                    Dialog dialog = DialogUtil.showTitleAndContentDialog(context,
                            context.getResources().getString(R.string.singlelogin_title),
                            context.getResources().getString(R.string.singlelogin_content),
                            context.getResources().getString(R.string.cancel),
                            context.getResources().getString(R.string.singlelogin_relogin),
                            null,
                            new Handler.Callback() {
                                @Override
                                public boolean handleMessage(Message message) {

                                    new UserService(context).loginOut();
                                    //直接跳转到登录页面
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    context.startActivity(intent);
                                    return false;
                                }
                            });
                    AppDataCache.getDataCache().setReLoginDialog(dialog);
                }
            });

        }
        return reponse;
    }

    /**
     * post  请求
     * 封装了header
     *
     * @param params
     * @param apiUrl
     * @return
     * @throws Exception
     */
    public static HttpURLConnection getHttpURLConnection(Context context, String apiUrl, Map<String, String> params, boolean showToken) throws Exception {

        HttpURLConnection httpURLConnection = null;
        byte[] data = NetWorkUtils.getRequestData(params).toString().getBytes();// 获得请求体
        try {
            Log.v(TAG, "url=" + apiUrl);

            URL url = new URL(apiUrl);
            //跳过https的验证
            HttpsVerifyUtil.verifySSL(apiUrl);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10 * 000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);
            // 设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            // 设置请求体的长度
            httpURLConnection.setRequestProperty("Content-Length",
                    String.valueOf(data.length));

            httpURLConnection.setRequestProperty("User-Agent",
                    "Android");
            //token
            if (showToken) {
                String token = AppSetting.getInstance(context).getWPToken(context);
                Log.v(TAG, "token=" + token);
                httpURLConnection.setRequestProperty(ApiConfig.PARAM_TRADE_REQUEST_HEADER,
                        ApiConfig.PARAM_TRADE_TOKEN + "=" + token);
            }


            // 获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

            int response = httpURLConnection.getResponseCode(); // 获得服务器的响应码
            if (response == HttpURLConnection.HTTP_OK) {
                return httpURLConnection;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally{
//            if(httpURLConnection != null) {
//                httpURLConnection.disconnect();
//            }
//        }
        return httpURLConnection;
    }

    /**
     * 农交所提现手续费
     *
     * @param context
     * @param cashOutMoney
     * @return
     */
    public static CommonResponse<CashOutFee> getCashOutFee(BaseActivity context, String cashOutMoney) {
        try {
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_CASHOUT_FEE);
            Map<String, String> map = ApiConfig.getCommonMap(context);

            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map.put("amount", cashOutMoney);
            map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, map));

            String res = post(context, url, map);
            CommonResponse<CashOutFee> response = CommonResponse.fromJson(res, CashOutFee.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 建仓面板获取交易信息(余额  代金券 特权卡)
     *
     * @param context
     * @return
     */
    public static CommonResponse<TradeInfoData> getTradeInfo(BaseActivity context) {
        try {
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADEINFO);
            Map<String, String> map = ApiConfig.getCommonMap(context);
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, map));

            String res = post(context, url, map);
            CommonResponse<TradeInfoData> response = CommonResponse.fromJson(res, TradeInfoData.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//==================================
//===================================fxbtg  新的接口

    /**
     * 获取大厅交易产品 list
     *
     * @param context
     * @return
     */
    public static CommonResponse4List<ProductObj> getFxPList(BaseActivity context) {
        try {
            if (context == null)
                return null;
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_PRODUCT_LIST);
            Map<String, String> map = ApiConfig.getParamMap(context, null);
//            String res = HttpClientHelper.getStringFromPost(context, url, map);
            String res = post(context, url, map);
            CommonResponse4List<ProductObj> response4List = CommonResponse4List.fromJson(res, ProductObj.class);
            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取单个产品
     *
     * @param context
     * @return
     */
    public static CommonResponse<ProductObj> getFxProduct(BaseActivity context, String exCode, String code) {
        try {
            if (context == null)
                return null;
            String url = AndroidAPIConfig.URL_TRADE_PRODUCT_DETAIL;
            Map<String, String> map = new LinkedHashMap<>();
//            map.put("excode", exCode);
//            map.put("code", code);
            map.put("instrumentId", code);
            map = ApiConfig.getParamMap(context, map);
//            String res = HttpClientHelper.getStringFromPost(context, url, map);
            String res = post(context, url, map);
            CommonResponse<ProductObj> response4List = CommonResponse.fromJson(res, ProductObj.class);
            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 建仓
     *
     * @param context
     * @param map
     * @param callback
     */
    public static void fxCreate(BaseActivity context, Map<String, String> map, NetCallback callback) {
        HttpClientHelper.doPostOption(context, AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_ORDER_CREATE), map, null, callback, false);
    }

    /**
     * 持仓和资金信息  TradeOrderAndUserInfoData
     *
     * @param context
     * @param map
     * @param callback
     */
    public static void fxHoldOrder(BaseActivity context, Map<String, String> map, NetCallback callback) {
        HttpClientHelper.doPostOption(context, AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_ORDER_HOLD_LIST), map, null, callback, false);
    }

}
