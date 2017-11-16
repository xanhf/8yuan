package com.trade.eight.service.trade;

import android.content.Context;
import android.text.TextUtils;

import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.entity.unifypwd.AccountCheckBindAndRegData;
import com.trade.eight.entity.unifypwd.AccountCheckExchangePasswordData;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一交易密码
 */
public class UnifyTradePwdHelp {
    public static final String TAG = UnifyTradePwdHelp.class.getSimpleName();

    /**
     * 用户交易所注册、绑定情况(包含是否统一过交易密码)
     *
     * @param context
     * @return
     */
    public static CommonResponse<AccountCheckBindAndRegData> checkAccountBind(Context context) {
        try {
            if (context == null)
                return null;
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.URL_ACCOUNT_EXCHANGELIST;
            Map<String, String> map = new HashMap<String, String>();
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map = ApiConfig.getParamMap(context,map);
            String res = HttpClientHelper.getStringFromPost(context, url, map);
            CommonResponse<AccountCheckBindAndRegData> response = CommonResponse.fromJson(res, AccountCheckBindAndRegData.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 批量检查交易所密码是否正确
     *
     * @param context
     * @return
     */
    public static CommonResponse4List<AccountCheckExchangePasswordData> checkAccountExchangedPWDBatch(Context context, String exjson) {
        try {
            if (context == null)
                return null;
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.URL_ACCOUNT_CHECKEXCHANGEPWD_BATCH;

            Map<String, String> map = new HashMap<String, String>();
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map.put("exjson", exjson);
            map = ApiConfig.getParamMap(context, map);
            String res = HttpClientHelper.getStringFromPost(context, url, map);
            CommonResponse4List<AccountCheckExchangePasswordData> response = CommonResponse4List.fromJson(res, AccountCheckExchangePasswordData.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 单个检查交易所密码是否正确
     *
     * @param context
     * @return
     */
    public static CommonResponse<AccountCheckExchangePasswordData> checkAccountExchangedPWD(Context context, String password, int exchangeID) {
        try {
            if (context == null)
                return null;
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.URL_ACCOUNT_CHECKEXCHANGEPWD;

            Map<String, String> map = new HashMap<String, String>();
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map.put("password", password);
            map.put("exchangeId", exchangeID + "");
            map = ApiConfig.getParamMap(context, map);
            String res = HttpClientHelper.getStringFromPost(context, url, map);
            CommonResponse<AccountCheckExchangePasswordData> response = CommonResponse.fromJson(res, AccountCheckExchangePasswordData.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置统一交易密码
     * @param context
     * @param exjson
     * @param password
     * @return
     */
    public static CommonResponse4List<AccountCheckExchangePasswordData> setUnifyPWD(Context context, String exjson, String password) {
        try {
            if (context == null)
                return null;
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.URL_ACCOUNT_UNIFYPWD_SET;

            Map<String, String> map = new HashMap<String, String>();
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            if (!TextUtils.isEmpty(exjson)) {// 老用户才需要此字段
                map.put("exjson", exjson);
            }
            map.put("password", password);
            map = ApiConfig.getParamMap(context, map);
            String res = HttpClientHelper.getStringFromPost(context, url, map);
            CommonResponse4List<AccountCheckExchangePasswordData> response = CommonResponse4List.fromJson(res, AccountCheckExchangePasswordData.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 统一交易密码登陆
     * @param context
     * @param password
     * @return
     */
    public static CommonResponse4List<AccountCheckExchangePasswordData> loginByUnifyPWD(Context context, String password) {
        try {
            if (context == null)
                return null;
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.URL_ACCOUNT_UNIFYPWD_LOGIN;

            Map<String, String> map = new HashMap<String, String>();
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map.put("password", password);
            map = ApiConfig.getParamMap(context, map);
            String res = HttpClientHelper.getStringFromPost(context, url, map);
            CommonResponse4List<AccountCheckExchangePasswordData> response = CommonResponse4List.fromJson(res, AccountCheckExchangePasswordData.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 重置统一密码 获取短信验证码
     * @param context
     * @return
     */
    public static CommonResponse<AccountCheckExchangePasswordData> resetUnifyPWdGetSmsCode(Context context) {
        try {
            if (context == null)
                return null;
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.URL_ACCOUNT_UNIFYPWD_SMSCODE;

            Map<String, String> map = new HashMap<String, String>();
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map = ApiConfig.getParamMap(context, map);
            String res = HttpClientHelper.getStringFromPost(context, url, map);
            CommonResponse<AccountCheckExchangePasswordData> response = CommonResponse.fromJson(res, AccountCheckExchangePasswordData.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 重置统一密码
     * @param context
     * @return
     */
    public static CommonResponse<AccountCheckExchangePasswordData> resetUnifyPWd(Context context,String password,String code) {
        try {
            if (context == null)
                return null;
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return null;
            String url = AndroidAPIConfig.URL_ACCOUNT_UNIFYPWD_RSET;

            Map<String, String> map = new HashMap<String, String>();
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
            map.put("password", password);
            map.put("code", code);
            map = ApiConfig.getParamMap(context, map);
            String res = HttpClientHelper.getStringFromPost(context, url, map);
            CommonResponse<AccountCheckExchangePasswordData> response = CommonResponse.fromJson(res,AccountCheckExchangePasswordData.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
