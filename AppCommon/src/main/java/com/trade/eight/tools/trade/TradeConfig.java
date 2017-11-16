package com.trade.eight.tools.trade;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.text.TextUtils;

import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AppStartUpConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.Exchange;
import com.trade.eight.moudle.baksource.BakSourceInterface;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fangzhu on 16/8/7.
 * 本地配置交易所的切换
 * 使用code对比
 */
public class TradeConfig {
    public static final String SETTING_FILE_NAME = "TradeConfig_config";
    public static final String PARAM_EXCHANGEID = "exchangeId";

    public static final String code_gg = BakSourceInterface.TRUDE_SOURCE_WEIPAN;
    public static final String code_hg = BakSourceInterface.TRUDE_SOURCE_WEIPANP_HG;
    public static final String code_jn = BakSourceInterface.TRUDE_SOURCE_WEIPANP_JN;
    public static final String code_hn = BakSourceInterface.TRUDE_SOURCE_WEIPANP_HN;
    public static final String code_SHFE = BakSourceInterface.TRUDE_SOURCE_QIHUO_SHFE;


    public static final String str_trade_guanggui = "";
    public static final String str_trade_hagui = "";
    public static final String str_trade_jn = "";
    public static final String str_trade_hn = "";

    public static final int exchangeId_gg = 1;
    public static final int exchangeId_hg = 2;
    public static final int exchangeId_jn = 3;
    public static final int exchangeId_hn = 4;

    //备用的本地交易所数据，如果网络获取失败了，使用本地
    //本地默认的交易所
    public static final String DEFAULT_EXCHANGE = BakSourceInterface.TRUDE_SOURCE_WEIPANP_FXBTG;
    //交易所的code对应交易所的，key是code
    public static HashMap<String, Exchange> tradeMap = new LinkedHashMap<>();
    /*备用首页的热门数据，启动接口获取失败，并且本地没有*/
    public static final String QIHUO_CODES = "SHFE|ag,SHFE|au,SHFE|rb,DCE|m,DCE|i,DCE|c,CZCE|MA,CZCE|SR,CZCE|TA,CZCE|RM";

    static {
        //这里农交所优先显示，2017-04-12
        tradeMap.put(code_jn, new Exchange(exchangeId_jn, str_trade_jn, code_jn));
        //添加华凝所,2017-04-19
        tradeMap.put(code_hn, new Exchange(exchangeId_hn, str_trade_hn, code_hn));
        tradeMap.put(code_hg, new Exchange(exchangeId_hg, str_trade_hagui, code_hg));
        /*本地下掉广贵所 2017-03-27*/
        tradeMap.put(code_gg, new Exchange(exchangeId_gg, str_trade_guanggui, code_gg));
    }

    /**
     * 获取网络已经读取的配置
     *
     * @return
     */
    public static HashMap<String, Exchange> getTradeMap() {
        HashMap<String, Exchange> map = ExchangeConfig.getInstance().getExchangeHashMap();
        if (map != null && map.size() > 0)
            return map;
        //返回本地的
        return tradeMap;
    }

    /**
     * map转换成list
     *
     * @param context
     * @return
     */
    public static List<Exchange> getExchangeList(Context context) {
        List<Exchange> list = new ArrayList<>();
        Iterator<String> iterator = getTradeMap().keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            list.add(getTradeMap().get(key));
        }
        return list;
    }

    public static Exchange getExchange(Context context, String code) {
        if (getTradeMap().containsKey(code))
            return getTradeMap().get(code);
        //没有找到返回 默认
        return tradeMap.get(DEFAULT_EXCHANGE);
    }


    public static String getName(Context context, String code) {
        return getExchange(context, code).getExchangeName();
    }


    /**
     * 获取本地存入的当前交易所名字
     * 先取code 然后nameSourceMap取name
     *
     * @param context
     * @return
     */
    public static String getCurrentTradeName(Context context) {
        String key = getCurrentTradeCode(context);
        return getExchange(context, key).getExchangeName();
    }

    /**
     * @param context
     * @param code    对应的交易所code
     * @return
     */
    public static int getExchangeId(Context context, String code) {
        return getExchange(context, code).getExchangeId();
    }

    /**
     * 存入code
     *
     * @param context
     * @return
     */
    public static String getCurrentTradeCode(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        String key = sp.getString("currentCode", code_gg);
        return key;
    }

    public static void setCurrentTradeCode(Context context, String content) {
        SharedPreferences sp = context.getSharedPreferences(
                SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        sp.edit().putString("currentCode", content).commit();
    }

    /**
     * 获取没有被选中的交易所
     *
     * @param context
     * @return
     */
    public static List<Exchange> getNotSelectedCode(Context context) {
        List<Exchange> list = new ArrayList<>();
        Iterator<String> iterator = getTradeMap().keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (!key.equals(getCurrentTradeCode(context))) {
                list.add(getTradeMap().get(key));
            }
        }
        return list;
    }

    /**
     * 当前选中的是否是广贵所
     *
     * @param context
     * @return
     */
    public static boolean isCurrentGG(Context context) {
        if (code_gg.equals(getCurrentTradeCode(context)))
            return true;
        return false;
    }

    /**
     * 当前选中的是否是哈贵所
     *
     * @param context
     * @return
     */
    public static boolean isCurrentHG(Context context) {
        if (code_hg.equals(getCurrentTradeCode(context)))
            return true;
        return false;
    }

    /**
     * 当前是否是农交所
     *
     * @param context
     * @return
     */
    public static boolean isCurrentJN(Context context) {
        if (code_jn.equals(getCurrentTradeCode(context)))
            return true;
        return false;
    }

    /**
     * 当前是否是华凝所
     *
     * @param context
     * @return
     */
    public static boolean isCurrentHN(Context context) {
        if (code_hn.equals(getCurrentTradeCode(context)))
            return true;
        return false;
    }

    //token 过期的errorCode 1、没有设置过交易所密码  2、toekn 过期时间已到===start

    /**
     * 本读缓存 交易所的密码 分交易所
     * 判断当前用户 有没有初始化过交易所的密码
     * 避免每次都需要调用check接口
     *
     * @param context
     * @param sourceName
     * @return
     */
    public static boolean isInitPwdLocal(Context context, String sourceName) {
        UserInfoDao dao = new UserInfoDao(context);
        if (!dao.isLogin())
            return false;
        //第一次默认
        SharedPreferences sp = context.getSharedPreferences(
                SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        return sp.getBoolean(sourceName + dao.queryUserInfo().getMobileNum(), false);
    }

    public static void setInitPwdLocal(Context context, String sourceName, boolean value) {
        UserInfoDao dao = new UserInfoDao(context);
        if (!dao.isLogin())
            return;
        //第一次默认
        SharedPreferences sp = context.getSharedPreferences(
                SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        sp.edit().putBoolean(sourceName + dao.queryUserInfo().getMobileNum(), value).commit();
    }
    //token 过期的errorCode 1、没有设置过交易所密码  2、toekn 过期时间已到===end


    // 获取用户是否统一过密码
    public static boolean getAccountIsUnifyPWD(Context context, String userID) {
        SharedPreferences sp = context.getSharedPreferences(
                SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        boolean key = sp.getBoolean(userID, false);
        return key;
    }

    // 设置是否不在提醒统一弹框
    public static void setAccountIsAllowedNotify(Context context, boolean isChecked) {
        SharedPreferences sp = context.getSharedPreferences(
                SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        sp.edit().putBoolean(new UserInfoDao(context).queryUserInfo().getUserId() + PreferenceSetting.NOT_ALLOW_UNIFYPWDDLG, isChecked).commit();
    }

    // 得到是否不在提醒统一弹框
    public static boolean getAccountIsAllowedNotify(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        boolean key = sp.getBoolean(new UserInfoDao(context).queryUserInfo().getUserId() + PreferenceSetting.NOT_ALLOW_UNIFYPWDDLG, false);
        return key;
    }

    /**
     * 设置  建仓是否展示风险提示
     * @param context
     * @param isShow
     */
    public static void setIsShowTradeCreateFengXian(Context context,boolean isShow) {
        SharedPreferences sp = context.getSharedPreferences(
                SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        sp.edit().putBoolean(new UserInfoDao(context).queryUserInfo().getUserId() + PreferenceSetting.ISSHOWTRADECREATE_FENGXIAN, isShow).commit();
    }

    /**
     * 获取  建仓是否展示风险提示
     * @param context
     * @return
     */
    public static boolean getIsShowTradeCreateFengXian(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        boolean key = sp.getBoolean(new UserInfoDao(context).queryUserInfo().getUserId() + PreferenceSetting.ISSHOWTRADECREATE_FENGXIAN, true);
        return key;
    }

    // 设置用户是否统一过密码
    public static void setAccountIsUnifyPWD(Context context, String userID) {
        SharedPreferences sp = context.getSharedPreferences(
                SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        sp.edit().putBoolean(userID, true).commit();
    }

    // 设置用户是否开启辅助线
    public static void setAccountIsOpenHelpLine(Context context, String userID, boolean isOpen) {
        SharedPreferences sp = context.getSharedPreferences(
                SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        sp.edit().putBoolean(userID + "isOpenHelpLine", isOpen).commit();
    }

    // 获取用户是否开启辅助线
    public static boolean getAccountIsOpenHelpLine(Context context, String userID) {
        SharedPreferences sp = context.getSharedPreferences(
                SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        boolean key = sp.getBoolean(userID + "isOpenHelpLine", true);
        return key;
    }

    /**
     * 热门产品 保存
     *
     * @param context
     * @param codes
     */
    public static void setProductCodes(Context context, String codes) {
        SharedPreferences sp = context.getSharedPreferences(
                SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        sp.edit().putString("productcodes", codes).commit();
    }

    /**
     * 热门产品 获取
     * @param context
     * @return
     */
    public static String getProductCodes(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        return sp.getString("productcodes", "");
    }

    /**
     * 获取当前所有产品的code
     *
     * @param context
     * @return
     */
    public static String getCurrentCodes(BaseActivity context) {
        String codes = "";

        // 先从内存中获取
        if (AppStartUpConfig.getInstance(context).getStartupConfigObj() != null) {
            String productList = AppStartUpConfig.getInstance(context).getStartupConfigObj().getProductList();
            if (!StringUtil.isEmpty(productList))
                codes = productList;
            return codes;
        }
        // 从本地文件中获取
        if(!TextUtils.isEmpty(getProductCodes(context))){
            return  getProductCodes(context);
        }

        return QIHUO_CODES;
    }
}
