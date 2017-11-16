package com.trade.eight.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.me.activity.ShareAct;
import com.trade.eight.moudle.me.activity.WeipanMsgListActivity;
import com.trade.eight.moudle.outterapp.StrategyDetailActivity;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.product.activity.ProductManagerActivity;
import com.trade.eight.moudle.trade.activity.CashInAct;
import com.trade.eight.moudle.trade.activity.CashOutAct;
import com.trade.eight.moudle.trade.activity.FXBTGCashInH5Act;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by fangzhu on 2015/1/7.
 * <p/>
 * 跳转页面的 约定
 *     public static final String ACT_REGISTER = "register";
 * //充值
 *     public static final String ACT_CASHIN = "cashin";
 * //交易
 *     public static final String ACT_TRADE = "trade”;
 * //每周分享
 *     public static final String ACT_SHARE = "share";
 * //跳转到首页直播室list
 * public static final String ACT_LIVE_LIST = "liveList";
 * //网页弹出分享
 * share.openShare(title, shareUrl)
 * title：分享显示的标题
 * shareUrl：分享的链接
 * js方法名字share.openShare 是不变的
 * <p/>
 * 如：
 * <img src="file:///android_asset/icon_btn_add_normal.png"
 * width="200" height="200"
 * onClick="share.openShare('this is title', 'http://www.8caopan.com/static/user/order/share/')"/>
 */
public class OpenActivityUtil {
    //推送 和h5里头的button
    public static final String SCHEME = "futures://";
    //3.4.3以后统一的
    public static final String SCHEME_V2 = "futures://";
    //首页banner
    public static final String SCHEME_SUB = "futures://";
    public static final String KEY_ACTION = "futures";
    public static final String IS_GOHOME = "gohome";//是否返回首页
//     touzile://login?key1=value1&key2=value2
    /**
     * 行情首页
     * 参数 无
     */
    public static final String ACT_MAIN = "main";

    /*bellow string do not change*/
    /**
     * 登录
     * 参数 无
     */
    public static final String ACT_LOGIN = "login";
    /**
     * 注册
     * 参数 无
     */
    public static final String ACT_REGISTER = "register";
    /**
     * 最新活动
     * 或者推送网页html
     * 参数 title  url
     */
    public static final String ACT_WEBVIEW = "webView";
    /**
     * 新闻的推送 包含 实时新闻，全球要闻，原油头条，名家专栏, 黄金头条
     * 1=实时新闻  参数 id type
     * 2=全球要闻  参数 id author
     * 3=原油头条  参数 id author
     * 4=专家策略  参数 id author
     * 5=黄金头条  参数 id author
     * 6=在线活动  参数 title url
     * <p/>
     * 参数
     * id type author title url
     */
    public static final String ACT_NEWS = "news";
    /**
     * 自选编辑页
     */
    public static final String ACT_OPT_MANAGER = "productManager";
    //消息列表
    public static final String ACT_MSGLIST = "weipanMsglist";
    //充值页面
    public static final String ACT_CASHIN = "cashin";
    //提现
    public static final String ACT_CASHOUT = "cashout";
    //首页交易tab
    public static final String ACT_TRADE = "trade";
    //首页行情tab
    public static final String ACT_MARKET = "market";
    //首页直播tab
    public static final String ACT_LIVE_LIST = "liveList";
    //每周分享
    public static final String ACT_SHARE = "share";
    //期货开户
    public static final String ACT_QIHUO_ACCOUNT = "qihuoaccount";
    /**
     * 为需要的activity 配置别名
     * 根据别名找到类全路径
     */
    public static Map<String, String> activityMap = new HashMap<String, String>();

    static {
         /*行情首页*/
        activityMap.put(ACT_MAIN, MainActivity.class.getName());
        /*登录*/
        activityMap.put(ACT_LOGIN, LoginActivity.class.getName());
        /*注册 接入哈贵的登录注册是一起的 */
        activityMap.put(ACT_REGISTER, LoginActivity.class.getName());
        /*webview html5网址 参数 title  url*/
        activityMap.put(ACT_WEBVIEW, WebActivity.class.getName());


        activityMap.put(ACT_NEWS, StrategyDetailActivity.class.getName());

        activityMap.put(ACT_OPT_MANAGER, ProductManagerActivity.class.getName());

        activityMap.put(ACT_MSGLIST, WeipanMsgListActivity.class.getName());
        activityMap.put(ACT_CASHIN, FXBTGCashInH5Act.class.getName());
        activityMap.put(ACT_CASHOUT, CashOutAct.class.getName());
        activityMap.put(ACT_TRADE, MainActivity.class.getName());
        activityMap.put(ACT_MARKET, MainActivity.class.getName());
        activityMap.put(ACT_SHARE, ShareAct.class.getName());
        activityMap.put(ACT_LIVE_LIST, MainActivity.class.getName());

//        activityMap.put(ACT_OPEN_SHARE, MainActivity.class.getName());
    }

    /**
     * @param context
     * @param action  touzile://action?key1=value1&key2=value2
     */
    public static void open(Context context, String action) {
        context.startActivity(getIntent(context, action));
    }

    /**
     * 使用场景
     * 登录成功 需要跳转到指定activity
     *
     * @param context
     * @param clazz       目的activity
     * @param callBackAct 目的activity执行完后 跳转的activity 自定义的缩写名称
     * @param stringMap   intent参数
     * @return
     */
    public static Intent initAction(Context context, Class clazz, String callBackAct, Map<String, String> stringMap) {
        StringBuffer stringBuffer = null;
        try {
            if (stringMap == null)
                stringMap = new HashMap<String, String>();
            //     touzile://action?key1=value1&key2=value2
            stringBuffer = new StringBuffer();
            stringBuffer.append(SCHEME);
            stringBuffer.append(callBackAct);
            stringBuffer.append("?");
            Iterator<String> keys = stringMap.keySet().iterator();
            int i = 0;
            while (keys.hasNext()) {
                if (i > 0)
                    stringBuffer.append("&");
                String key = keys.next();
                stringBuffer.append(key);
                stringBuffer.append("=");
                stringBuffer.append(stringMap.get(key));
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Intent intent = new Intent();
        intent.setClass(context, clazz);
        intent.putExtra(KEY_ACTION, stringBuffer.toString());
        return intent;
    }

    /**
     * 得到action后面拼接的所有参数
     *
     * @param action
     * @return
     */
    public static Map<String, String> getActionParam(String action) {
        try {
            if (action == null)
                return null;
            if (action.trim().length() == 0)
                return null;
            action = action.trim();
            if (!action.startsWith(SCHEME)
                    && !action.startsWith(SCHEME_SUB)
                    && !action.startsWith(SCHEME_V2))
                return null;
            Map<String, String> map = new LinkedHashMap<>();
            action = StringUtil.replace(action, SCHEME, "");
            action = StringUtil.replace(action, SCHEME_V2, "");
            action = StringUtil.replace(action, SCHEME_SUB, "");
            if (action.contains("?")) {
                String[] strs = action.split("\\?");
                String clsAction = strs[0].trim();

                if (strs.length > 1) {
                    String[] params = strs[1].split("&");
                    for (int i = 0; i < params.length; i++) {
                        String str = params[i].trim();
                        String[] temp = str.split("=");
                        String key = "";
                        if (temp.length > 0)
                            key = temp[0].trim();
                        String value = "";
                        if (temp.length > 1)
                            value = temp[1].trim();
                        value = ConvertUtil.NVL(value, "");
                        if (key.equalsIgnoreCase("url") && value.length() > 0)
                            value = URLDecoder.decode(value, "utf-8");
                        if (key != null && key.trim().length() > 0)
                            map.put(key, value);
                    }
                    return map;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * if (clsAction.equals(ACT_MAIN))
     * intent.putExtra(IS_GOHOME, false);//首页不再需要返回首页
     * else
     * intent.putExtra(IS_GOHOME, true);/de/返回首页
     *
     * @param context
     * @param action
     * @return
     */
    public static Intent getIntent(Context context, String action) {
        try {
//     touzile://action?key1=value1&key2=value2
            if (action == null)
                return null;
            if (action.trim().length() == 0)
                return null;
            action = action.trim();

            if (!action.startsWith(SCHEME)
                    && !action.startsWith(SCHEME_SUB)
                    && !action.startsWith(SCHEME_V2))
                return null;
            action = StringUtil.replace(action, SCHEME, "");
            action = StringUtil.replace(action, SCHEME_V2, "");
            action = StringUtil.replace(action, SCHEME_SUB, "");

            if (action.contains("?")) {
                String[] strs = action.split("\\?");
                Intent intent = new Intent();
                String clsAction = strs[0].trim();

                if (activityMap.containsKey(clsAction)) {
                    //首页交易tab
                    if (clsAction.equals(ACT_TRADE)) {
                        //交易tab
                        intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.WEIPAN);
                        //产品列表
                        intent.putExtra(BaseInterface.TAB_PARAME_INDEX, 0);
                    }
                    //直播室列表
                    if (clsAction.equals(ACT_LIVE_LIST)) {
                        //首页直播室列表tab
                        intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.LIVING);
                    }
                    //首页行情tab
                    if (clsAction.equals(ACT_MARKET)) {
                        //首页行情tab
                        intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.MARKET);
                    }
                    //注册
                    if (clsAction.equals(ACT_REGISTER)) {
                        intent.putExtra("tab", LoginActivity.TAB_REG);
                    }
//                    if (clsAction.equals(ACT_MAIN))
//                        intent.putExtra(IS_GOHOME, false);//首页不再需要返回首页
//                    else
//                        intent.putExtra(IS_GOHOME, true);//返回首页
                    intent.setClassName(context, activityMap.get(clsAction));
                } else {
                    return null;
                }

                if (strs.length > 1) {
                    String[] params = strs[1].split("&");
                    for (int i = 0; i < params.length; i++) {
                        String str = params[i].trim();
                        String[] temp = str.split("=");
                        String key = "";
                        if (temp.length > 0)
                            key = temp[0].trim();
                        String value = "";
                        if (temp.length > 1)
                            value = temp[1].trim();
                        value = ConvertUtil.NVL(value, "");
                        if (key.equalsIgnoreCase("url") && value.length() > 0)
                            value = URLDecoder.decode(value, "utf-8");
                        if (key != null && key.trim().length() > 0)
                            intent.putExtra(key, value);
                    }
                }

                return intent;
            } else {
                Intent intent = new Intent();
                if (activityMap.containsKey(action)) {
                    if (action.equals(ACT_TRADE)) {
                        intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.WEIPAN);
                        intent.putExtra(BaseInterface.TAB_PARAME_INDEX, 0);
                    }
                    //直播室列表
                    if (action.equals(ACT_LIVE_LIST)) {
                        //首页直播室列表tab
                        intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.LIVING);
                    }
                    //直播室列表
                    if (action.equals(ACT_MARKET)) {
                        //首页直播室列表tab
                        intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.MARKET);
                    }
                    intent.setClassName(context, activityMap.get(action));
                }
                return intent;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isGoHome(Context context, Intent intent) {
        return intent.getBooleanExtra(IS_GOHOME, false);
    }

    public static String getShortAction(Context context, String action) {
        try {
//     schme://action?key1=value1&key2=value2
            if (action == null)
                return null;
            if (action.trim().length() == 0)
                return null;
            action = action.trim();
            if (!action.startsWith(SCHEME)
                    && !action.startsWith(SCHEME_SUB)
                    && !action.startsWith(SCHEME_V2))
                return null;
            action = StringUtil.replace(action, SCHEME, "");
            action = StringUtil.replace(action, SCHEME_V2, "");
            action = StringUtil.replace(action, SCHEME_SUB, "");

            if (action.contains("?")) {
                String[] strs = action.split("\\?");
                String clsAction = strs[0].trim();
                return clsAction;
            } else {
                return action;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return action;
    }

    /**
     * 打电话
     * @param context
     * @param phone
     */
    public static void callPhone(Context context,String phone){
        if(TextUtils.isEmpty(phone)){
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+phone));
        context.startActivity(intent);
    }

    /**
     * 期货开户
     * @param context
     */
    public static void openQiHuoAccountWelcome(BaseActivity context){
//        Intent intent = new Intent(context, com.cfmmc.app.sjkh.MainActivity.class);
//        intent.putExtra("brokerId", "0101");
//        intent.putExtra("channel", "@200$088");
//        intent.putExtra("packName", "com.trade.qihuo");
//        context.startActivity(intent);
        //http://static.8caopan.com
        WebActivity.start(context, "期货一键开户", AndroidAPIConfig.URL_OPENQIHUOACCOUNTWELCOME,true);
    }

    /**
     * 期货开户
     * @param
     */
    public static void openQiHuoAccount(BaseActivity context){
        Intent intent = new Intent(context, com.cfmmc.app.sjkh.MainActivity.class);
        intent.putExtra("brokerId", "0101");
        intent.putExtra("channel", "@200$088");
        intent.putExtra("packName", "com.trade.qihuo");
        context.startActivity(intent);
    }
}
