package com.trade.eight.service.trude;

import android.content.Context;

import com.trade.eight.app.ServiceException;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppCollectUtil;
import com.trade.eight.config.AppSetting;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.response.AliApiResult;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.chatroom.LogoutHelper;
import com.trade.eight.mpush.coreoption.CoreOptionUtil;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.JSONObjectUtil;
import com.trade.eight.tools.AESUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.UploadFileUtil;
import com.trade.eight.tools.nav.UNavConfig;
import com.trade.eight.tools.trade.TradeConfig;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class UserService  {
    public static final String TAG = "UserService";
    Context context;

    public UserService(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
    }


    /**
     * type 1=注册短信，2=修改密码短信
     */
    public AliApiResult getCode(String mobile, String type) throws ServiceException {
        Map<String, String> paraMap = new LinkedHashMap<String, String>();
//        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UMOBLE, mobile);
        //8元 不需要
//        paraMap.put("type", type);
        paraMap.put(ApiConfig.PARAM_TIME, System.currentTimeMillis() + "");
        paraMap.put(ApiConfig.PARAM_SOURCEID, AppSetting.getInstance(context).getAppSource() + "");

        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

        String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_GET_VALICODE, paraMap);
        Log.v(TAG, "result=" + result);
        if (result == null)
            return null;
        AliApiResult response = AliApiResult.fromJson(result, JSONObject.class);
        return response;
    }


    /**
     * （验证码）String code,
     * （codeKey）String codeKey,
     * （unix时间）t，
     * auth= MD5(code + codeKey +t+KEY)
     * 20160129  使用新的 验证
     * code＝验证码  mobile＝手机号码
     *
     * @return
     */
    public AliApiResult checkSMS(String mCode, final String mobile, String url) {
        try {
            Map<String, String> map = new LinkedHashMap<String, String>();
            map.put(ApiConfig.PARAM_CODE, mCode);
//            map.put(ApiConfig.PARAM_CODEKEY, codeKey);
            map.put(UserInfo.UMOBLE, mobile);
            map.put(ApiConfig.PARAM_TIME, System.currentTimeMillis() + "");
            map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, map));

            String jsonStr = HttpClientHelper.getStringFromPost(context,  url,map);
            if (jsonStr == null)
                return null;
            JSONObject object = new JSONObject(jsonStr);
            AliApiResult aliApiResult = new AliApiResult();
            aliApiResult.setSuccess(JSONObjectUtil.getBoolean(object, "success", false));
            aliApiResult.setErrorInfo(JSONObjectUtil.getString(object, "errorInfo", ""));
            aliApiResult.setErrorCode(JSONObjectUtil.getString(object, "errorCode", ""));
            return aliApiResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检测手机号是否已经初始化过密码
     *
     * @param context
     * @param mobileNum
     * @return
     * @throws Exception
     */
    public CommonResponse<TempObject> checkInitPWD(Context context, String mobileNum) throws Exception {
        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UMOBLE_NUMBER, mobileNum);

        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

        String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_USER_CHECK_MOBILE,paraMap);
        Log.v(TAG, "result=" + result);
        if (result == null)
            return null;
        CommonResponse<TempObject> response = CommonResponse.fromJson(result, TempObject.class);
        return response;

    }


    /**
     * 昵称
     *
     * @param nickName
     * @return
     * @throws Exception
     */
    public CommonResponse<String> updateNickName(String nickName) throws Exception {
        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UID, new UserInfoDao(context).queryUserInfo().getUserId());
        paraMap.put(UserInfo.UNICKNAME, nickName);

        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

        String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_USER_UPDATE_NICKNAME,paraMap);
        Log.v(TAG, "result=" + result);
        if (result == null)
            return null;
        CommonResponse response = CommonResponse.fromJson(result, String.class);
        if (response != null && response.isSuccess()) {
            UserInfoDao dao = new UserInfoDao(context);
            UserInfo userInfo = dao.queryUserInfo();
            if (userInfo != null) {
                userInfo.setNickName(nickName);
                dao.addOrUpdate(userInfo);
            }
        }
        return response;

    }

    /**
     * 登录
     *
     * @param context
     * @param mobileNum
     * @param password
     * @return data is UserInfo
     * @throws Exception
     */
    public CommonResponse<UserInfo> login(Context context, String mobileNum, String password) throws Exception {
        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UMOBLE_NUMBER, mobileNum);
        paraMap.put(UserInfo.UPASSWORD, AESUtil.encrypt(password));

        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

        String result = HttpClientHelper.getStringFromPost(context,AndroidAPIConfig.URL_USER_LOGIN,paraMap);
        Log.v(TAG, "result=" + result);
        if (result == null)
            return null;
        CommonResponse<UserInfo> response = CommonResponse.fromJson(result, UserInfo.class);
        if (response != null
                && response.isSuccess()
                && response.getData() != null) {
            UserInfoDao dao = new UserInfoDao(context);
            dao.addOrUpdate(response.getData());
            CoreOptionUtil.getCoreOptionUtil(context.getApplicationContext()).bindUser(response.getData().getUserId());
            //更新本地的token
//            AppSetting.getInstance(context).updateToken(response.getData().getToken());
        }
        return response;

    }

    /**
     * 注册 返回是否成功不返回用户信息
     *
     * @param context
     * @param mobileNum
     * @param password
     * @param code
     * @return true or false data is null
     * @throws Exception
     */
    public CommonResponse<UserInfo> reg(Context context, String mobileNum, String password, String code) throws Exception {
        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UMOBLE_NUMBER, mobileNum);
        paraMap.put(UserInfo.UPASSWORD, AESUtil.encrypt(password));
        paraMap.put(UserInfo.UCODE, code);
        //3.3.3开始收集imei  字段使用idfa
        String imei = StringUtil.getIMEI(context);
        if (!StringUtil.isEmpty(imei))
            paraMap.put(AppCollectUtil.P_idfa, imei);

        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

        String result = HttpClientHelper.getStringFromPost(context,AndroidAPIConfig.URL_USER_REGISTER,paraMap);
        Log.v(TAG, "result=" + result);
        if (result == null)
            return null;
        CommonResponse<UserInfo> response = CommonResponse.fromJson(result, UserInfo.class);
        if (response != null
                && response.isSuccess()
                && response.getData() != null) {
            UserInfoDao dao = new UserInfoDao(context);
            dao.addOrUpdate(response.getData());
            //更新本地的token
//            AppSetting.getInstance(context).updateToken(response.getData().getToken());
            CoreOptionUtil.getCoreOptionUtil(context.getApplicationContext()).bindUser(response.getData().getUserId());
        }
        return response;

    }

    /**
     * 重新设置登录密码 返回是否成功不返回用户信息
     *
     * @param context
     * @param mobileNum
     * @param password
     * @param code
     * @return true or false data is null
     * @throws Exception
     */
    public CommonResponse<UserInfo> resetPWD(Context context, String mobileNum, String password, String code) throws Exception {
        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UMOBLE_NUMBER, mobileNum);
        paraMap.put(UserInfo.UPASSWORD, AESUtil.encrypt(password));
        paraMap.put(UserInfo.UCODE, code);

        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

        String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_USER_RESET_PWD,paraMap);
        Log.v(TAG, "result=" + result);
        if (result == null)
            return null;
        CommonResponse<UserInfo> response = CommonResponse.fromJson(result, UserInfo.class);

        return response;

    }

    /**
     * 获取重新设置的登录密码的短信
     *
     * @param context
     * @param mobileNum
     * @return true or false data is null
     * @throws Exception
     */
    public CommonResponse<UserInfo> getResetPWDSMS(Context context, String mobileNum) throws Exception {
        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UMOBLE_NUMBER, mobileNum);

        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

        String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_USER_RESET_PWD_SMS, paraMap);
        Log.v(TAG, "result=" + result);
        if (result == null)
            return null;
        CommonResponse<UserInfo> response = CommonResponse.fromJson(result, UserInfo.class);

        return response;

    }

    /**
     * 获取注册的短信
     *
     * @param context
     * @param mobileNum
     * @return true or false data is null
     * @throws Exception
     */
    public CommonResponse<UserInfo> getRegSMS(Context context, String mobileNum) throws Exception {
        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UMOBLE_NUMBER, mobileNum);

        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

        String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_USER_REGISTER_SMS, paraMap);
        Log.v(TAG, "result=" + result);
        if (result == null)
            return null;
        CommonResponse<UserInfo> response = CommonResponse.fromJson(result, UserInfo.class);

        return response;

    }

    /**
     * 初始化密码
     *
     * @param context
     * @param mobileNum
     * @param password
     * @param code
     * @return true or false data is null
     * @throws Exception
     */
    public CommonResponse<UserInfo> initPWD(Context context, String mobileNum, String password, String code) throws Exception {
        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UMOBLE_NUMBER, mobileNum);
        paraMap.put(UserInfo.UPASSWORD, AESUtil.encrypt(password));
        paraMap.put(UserInfo.UCODE, code);

        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

        String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_USER_INIT_PWD, paraMap);
        Log.v(TAG, "result=" + result);
        if (result == null)
            return null;
        CommonResponse<UserInfo> response = CommonResponse.fromJson(result, UserInfo.class);

        //默认登陆
        if (response != null && response.isSuccess()
                && response.getData() != null) {
            UserInfoDao dao = new UserInfoDao(context);
            UserInfo userInfo = response.getData();
            userInfo.setPassword(password);
            dao.addOrUpdate(userInfo);
        }
        return response;

    }

    /**
     * 初始化密码获取短信
     *
     * @param context
     * @param mobileNum
     * @return true or false data is null
     * @throws Exception
     */
    public CommonResponse<UserInfo> initPWDSMS(Context context, String mobileNum) throws Exception {
        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UMOBLE_NUMBER, mobileNum);

        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

        String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_USER_INIT_PWD_SMS, paraMap);
        Log.v(TAG, "result=" + result);
        if (result == null)
            return null;
        CommonResponse<UserInfo> response = CommonResponse.fromJson(result, UserInfo.class);

        return response;

    }

    /**
     * 更新头像
     *
     * @param file
     * @return
     */
    public CommonResponse<String> updateAvatar(File file) {
        UserInfoDao dao = new UserInfoDao(context);
        UserInfo userInfo = dao.queryUserInfo();
        String baseUrl = AndroidAPIConfig.URL_USER_UPDATE_AVATAR;
//        Map<String, String> reqHeader = ApiConfig.getCommonMap(context);
        Map<String, String> reqHeader = new LinkedHashMap<String, String>();
        reqHeader.put(ApiConfig.PARAM_TIME, System.currentTimeMillis() + "");

        reqHeader.put(UserInfo.UID, userInfo.getUserId());
        reqHeader.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, reqHeader));
        try {
            FileInputStream inputStream = new FileInputStream(file);
            UploadFileUtil util = new UploadFileUtil();
            String result = util.toUploadFile(context, file, baseUrl, reqHeader);

            if (result == null)
                return null;
            CommonResponse response = CommonResponse.fromJson(result, String.class);
            if (response != null && response.isSuccess()) {
                if (userInfo != null) {
                    userInfo.setAvatar(response.getData().toString());
                    dao.addOrUpdate(userInfo);
                }
            }
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 交易所所注册
     *
     * @param userId
     * @param password
     * @return
     * @throws Exception
     */
    public CommonResponse<UserInfo> tradeReg(String userId, String password) throws Exception {
        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UID, userId);
        paraMap.put(UserInfo.UPASSWORD, AESUtil.encrypt(password));
        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

        String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_USER_REG), paraMap);
        Log.v(TAG, "result=" + result);
        if (result == null)
            return null;
        CommonResponse<UserInfo> response = CommonResponse.fromJson(result, UserInfo.class);

        return response;

    }

    /**
     * 交易所登录
     *
     * @param userId
     * @param password
     * @return
     * @throws Exception
     */
    public CommonResponse<UserInfo> tradeLogin(String userId, String password) throws Exception {
        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UID, userId);
        paraMap.put(UserInfo.UPASSWORD, AESUtil.encrypt(password));
        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

        String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_USER_LOGIN), paraMap);
        Log.v(TAG, "result=" + result);
        if (result == null)
            return null;
        CommonResponse<UserInfo> response = CommonResponse.fromJson(result, UserInfo.class);

        return response;

    }

    /**
     * 交易所重置交易密码
     *
     * @param context
     * @param userId
     * @param password
     * @param code
     * @return
     * @throws Exception
     */
    public CommonResponse<UserInfo> tradeResetPWD(Context context, String userId, String password, String code) throws Exception {
        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UID, userId);
        paraMap.put(UserInfo.UPASSWORD, AESUtil.encrypt(password));
        paraMap.put(UserInfo.UCODE, code);

        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

        String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_USER_RESET_PWD_TRADE), paraMap);
        Log.v(TAG, "result=" + result);
        if (result == null)
            return null;
        CommonResponse<UserInfo> response = CommonResponse.fromJson(result, UserInfo.class);

        return response;

    }

    /**
     * 交易所重置交易密码 获取短信
     *
     * @param context
     * @param userId
     * @return
     * @throws Exception
     */
    public CommonResponse<UserInfo> getTradeResetPWDSMS(Context context, String userId) throws Exception {
        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UID, userId);

        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

        String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_USER_RESET_PWD_TRADE_SMS), paraMap);
        Log.v(TAG, "result=" + result);
        if (result == null)
            return null;
        CommonResponse<UserInfo> response = CommonResponse.fromJson(result, UserInfo.class);

        return response;

    }

    /**
     * 交易所重置交易密码(分交易所)
     *
     * @param context
     * @param userId
     * @param password
     * @param code
     * @param exchangeCode(交易所code)
     * @return
     * @throws Exception
     */
    public CommonResponse<UserInfo> tradeResetPWD(Context context, String userId, String password, String code, String exchangeCode) throws Exception {
        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UID, userId);
        paraMap.put(UserInfo.UPASSWORD, AESUtil.encrypt(password));
        paraMap.put(UserInfo.UCODE, code);
        paraMap.put(TradeConfig.PARAM_EXCHANGEID, TradeConfig.getExchangeId(context, exchangeCode) + "");
        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

        String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_USER_RESET_PWD_TRADE, exchangeCode), paraMap);
        Log.v(TAG, "result=" + result);
        if (result == null)
            return null;
        CommonResponse<UserInfo> response = CommonResponse.fromJson(result, UserInfo.class);

        return response;

    }

    /* 交易所重置交易密码 获取短信 (分交易所)
    * @param context
    * @param userId
    * @param exchangeCode(交易所code)
    * @return
            * @throws Exception
    */
    public CommonResponse<UserInfo> getTradeResetPWDSMS(Context context, String userId, String exchangeCode) throws Exception {
        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UID, userId);
        paraMap.put(TradeConfig.PARAM_EXCHANGEID, TradeConfig.getExchangeId(context, exchangeCode) + "");
        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));
        String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_USER_RESET_PWD_TRADE_SMS, exchangeCode), paraMap);
        Log.v(TAG, "result=" + result);
        if (result == null)
            return null;
        CommonResponse<UserInfo> response = CommonResponse.fromJson(result, UserInfo.class);

        return response;

    }


    /**
     * 退出登录
     */
    public void loginOut() {



        new UserInfoDao(context).deleteUserInfo();
        AppSetting appSetting = AppSetting.getInstance(context);
        //清理的token
        appSetting.clearWPToken();
        //清理本地记录的初始化过密码的手机号
//        appSetting.setUserName("");

        //同时需要清理掉 云信用户的账户xinxi
        LogoutHelper.logout();
//        NIMClient.getService(AuthService.class).logout();
//        DemoCache.clear();

        //有退出操作就认为是有登录过账号了，新手引导就不显示
        UNavConfig.setLogined(context, true);
        //有退出操作了就不再显示新手步骤
        UNavConfig.setSkipStep(context, true);


    }
}
