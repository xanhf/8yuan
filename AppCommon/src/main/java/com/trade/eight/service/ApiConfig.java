package com.trade.eight.service;

import android.content.Context;

import com.easylife.ten.lib.R;
import com.trade.eight.config.AppSetting;
import com.trade.eight.config.AppStartUpConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.startup.StartupObj;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.tools.AESUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyBase64;
import com.trade.eight.tools.StringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fangzhu
 * 接口访问参数配置
 */
public class ApiConfig {
    public static final String TAG = "ApiConfig";
    //common param
    public static final String PARAM_TIME = "t";//时间毫秒
    public static final String PARAM_KEY = "key";//参数key
    /*不加密之前的真实值*/
//    public static final String PARAM_KEY_VALUE = "bjfC4NLG0PtAkcqoiKYfZv";
    public static final String PARAM_KEY_VALUE = "Ea[nMz]Jve4@uN{_Ipg8yAk^H`OKtL7.";

    /*不将key直接暴露，这里先用base64加密，代码中用的时候再去解密*/
//    public static final String PARAM_KEY_VALUE = "YmpmQzROTEcwUHRBa2Nxb2lLWWZadg==";

    public static final String PARAM_DEVICEID = "deviceId";//设备唯一标示
    public static final String PARAM_SOURCEID = "sourceId";//app source
    public static final String PARAM_DEVICETYPE = "device";//设备类型：0=PC，1=安卓，2=ISO，3=微信
//    public static final String PARAM_MARKET = "market";//市场标示  getResources().getString(R.string.string_lable_market)
    /*v 表示app版本号*/
    public static final String PARAM_APP_VERSION = "v";

    //must can
    public static final String PARAM_CODE = "code";//短信返回字段
    public static final String PARAM_CODEKEY = "codeKey";//短信返回字段
    public static final String PARAM_AUTH = "auth";//auth认证

    //trade 交易的参数 str  Set-Cookie: token=170d64778d3d489668047632c22ebf72
    public static final String PARAM_TRADE_REPONSE_HEADER = "Set-Cookie";
    public static final String PARAM_TRADE_REQUEST_HEADER = "Cookie";
    public static final String PARAM_TRADE_TOKEN = "token";


    // 解析失败  解析字段为空
    public static final String CODE_NET_ERROR = "404";
    //交易token过期
    public static final String ERROR_CODE_TOEKN = "00007";
    //接口没有输入token
    public static final String ERROR_CODE_TOEKN_EMPTY = "30005";
    //直播房间 第一次没有输入过昵称
    public static final String ERROR_CODE_NOT_REG = "40000";
    //新版3.1之前的用户需要初始化密码
    public static final String ERROR_CODE_NOT_INIT_PWD = "10028";
    //请绑定京东快捷支付 30011
    public static final String ERROR_CODE_BIND_JD_IDCARD = "30011";
    /*直播室送礼物积分不足*/
    public static final String ERROR_CODE_POINE_LESS = "250006";
    /*提现实名认证*/
    public static final String ERROR_CODE_SHIMING = "10032";
    /*单点登录*/
    public static final String ERROR_CODE_RELOGIN = "00013";
    /*需要确认账单*/
    public static final String ERROR_CODE_CONFIRMBALANCE = "410005";
    /*没有绑定银期*/
    public static final String ERROR_CODE_NOYINQI = "10801";
    /*非登录时间*/
    public static final String ERROR_CODE_NOTLOGINTIME = "10034";



    /*设备类型deviceType  1:android   2:ios;打开网页的时候需要加上参数 用来响应caopan8的规则 */
    public static final String PARAM_DEVICE_TYPE = "deviceType";
    public static final String PARAM_DEVICE_TYPE_ANDROID = "1";


    //需要重新登录的返回errorCode
    public static List<String> tokenDisableList = new ArrayList<>();

    static {
        tokenDisableList.add(ERROR_CODE_TOEKN);
        tokenDisableList.add(ERROR_CODE_TOEKN_EMPTY);
    }


    /**
     * 是否需要重新登录交易所
     * token过期， 为null
     *
     * @param errorCode
     * @return
     */
    public static boolean isNeedLogin(String errorCode) {
        if (errorCode == null)
            return false;
        if (tokenDisableList.contains(errorCode))
            return true;
        return false;
    }

    /**
     * map 顺序MD5
     *
     * @param context
     * @param map
     * @return
     */
    public static String getAuth(Context context, Map<String, String> map) {
        Map<String, String> authMap = new LinkedHashMap<String, String>();
        authMap.putAll(map);
        try {
            //用base64解密得到真实的值
//            authMap.put(PARAM_KEY, new String(MyBase64.decode(PARAM_KEY_VALUE)))
            authMap.put(PARAM_KEY, PARAM_KEY_VALUE);

            // 以下是加密key后台获取逻辑
           /* String realK = "";
            AppStartUpConfig appStartUpConfig = AppStartUpConfig.getInstance(context);
            if (appStartUpConfig != null) {
                if (appStartUpConfig.getStartupConfigObj() != null) {
                    String k = appStartUpConfig.getStartupConfigObj().getConfig().getK();
                    realK = AESUtil.decrypt(new String(MyBase64.decode(k)));
                }
            }
            //防止启动接口没有获取到，这里使用上一次启动接口的key
            if (StringUtil.isEmpty(realK)) {
                //取本地的key
                realK = AESUtil.decrypt(new String(MyBase64.decode(StartupObj.getLocalKey(context))));
            }
            if (realK == null)
                realK = "";
            authMap.put(PARAM_KEY, realK);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuffer stringBuffer = new StringBuffer();
        Iterator<String> iterator = authMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = authMap.get(key);
            if (!StringUtil.isEmpty(value))
                stringBuffer.append(value);
        }
        return StringUtil.md5(stringBuffer.toString());
    }


    /**
     * @param context
     * @return
     * @deprecated repleace by getParamMap
     */
    public static Map<String, String> getCommonMap(Context context) {
        //map必须要有顺序
        Map<String, String> map = new LinkedHashMap<String, String>();
        /*
        * mark：有些手机时间对不上，不是13位的；
        * 如酷派8090 获取到的时间戳是：955762384468
        * */
        map.put(PARAM_TIME, System.currentTimeMillis() + "");
        map.put(PARAM_DEVICEID, StringUtil.getDeviceId(context));
        map.put(PARAM_SOURCEID, AppSetting.getInstance(context).getAppSource() + "");
        map.put(PARAM_APP_VERSION, AppSetting.getInstance(context).getAppVersion() + "");
        map.put(PARAM_DEVICETYPE, PARAM_DEVICE_TYPE_ANDROID);
        map.put(context.getResources().getString(R.string.string_lable_market), AppSetting.getInstance(context).getAppMarket());

        if (new UserInfoDao(context).isLogin()) {
            UserInfo userInfo = new UserInfoDao(context).queryUserInfo();
//            map.put(UserInfo.UID, "1");
            map.put(UserInfo.UID, userInfo.getUserId());
        }
        String token = AppSetting.getInstance(context).getWPToken(context);
        if (!StringUtil.isEmpty(token))
            map.put(PARAM_TRADE_TOKEN, token);

        return map;
    }

    /**
     * 获取接口处参数
     *
     * @param context
     * @param parmMap 接口需要额外传入的参数
     */
    public static Map<String, String> getParamMap(Context context, Map<String, String> parmMap) {
        //最开始需要公共的参数,map必须要有顺
        Map<String, String> map = getCommonMap(context);
        //接口额外的参数
        if (parmMap != null && parmMap.size() > 0) {
            map.putAll(parmMap);
        }
        //最后加上auth 验证
        map.put(PARAM_AUTH, getAuth(context, map));
        return map;
    }

}
