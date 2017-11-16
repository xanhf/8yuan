package com.trade.eight.config;

import android.content.Context;

import com.trade.eight.tools.trade.TradeConfig;

import java.util.HashMap;

public class AndroidAPIConfig {

    private static final String TAG = "AndroidAPIConfig";

    //    https://q-futures.8caopan.com
//    https://m-futures.8caopan.com
//    https://c-futures.8caopan.com 
//    https://static-futures.8caopan.com
//
    public static final String HOST_API = "https://m-futures.8caopan.com";//主域名
    public static final String HOST_TRADE = "https://m-futures.8caopan.com";//交易
    public static final String HOST_TRADE_HG = "https://m-futures.8caopan.com";//交易hg
    public static final String HOST_TRADE_JN = "https://m-futures.8caopan.com";//交易所农交所jn
    public static final String HOST_TRADE_HN = "https://m-futures.8caopan.com";//交易所华凝所hn
    public static final String HOST_WEIPAN_MARKET = "https://q-futures.8caopan.com";//行情,如果测试环境没有，就使用正式的
    public static final String HOST_API_RANK = "https://m-futures.8caopan.com";//首页排行榜,测试环境没有，使用正式的
    public static final String HOST_API_HTML = "https://static-futures.8caopan.com";//静态页面 测试环境没有，使用正式的
    public static final String HOST_API_HTML_STATIC = "https://static-futures.8caopan.com";//静态页面(static) 测试环境没有，使用正式的
    public static final String HOST_API_HTML_HTTPS = "https://static-futures.8caopan.com";//静态页面
    public static final String HOST_TRADE_QIHUO = "https://m-futures.8caopan.com";//交易
    public static final String HOST_QUOTATION = "http://106.14.183.203:30003/";//交易


    //test host
//    public static final String HOST_API = "https://futures-m.8caopan.com";//主域名
//    public static final String HOST_TRADE = "https://futures-m.8caopan.com";//交易
//    public static final String HOST_TRADE_HG = "https://fxbtg-m.8caopan.com";//交易hg
//    public static final String HOST_TRADE_JN = "https://fxbtg-m.8caopan.com";//交易所农交所jn
//    public static final String HOST_TRADE_HN = "https://fxbtg-m.8caopan.com";//交易所华凝所hn
//    public static final String HOST_WEIPAN_MARKET = "https://futures-m.8caopan.com";//行情,如果测试环境没有，就使用正式的
//    public static final String HOST_API_RANK = "https://fxbtg-m.8caopan.com";//首页排行榜,测试环境没有，使用正式的
//    public static final String HOST_API_HTML = "https://fxbtg-static.8caopan.com";//静态页面 测试环境没有，使用正式的
//    public static final String HOST_API_HTML_STATIC = "https://fxbtg-static.8caopan.com";//静态页面(static) 测试环境没有，使用正式的
//    public static final String HOST_API_HTML_HTTPS = "https://fxbtg-static.8caopan.com";//静态页面
//    public static final String HOST_TRADE_QIHUO = "https://futures-m.8caopan.com";//交易


    //期货开户
    public static final String URL_OPENQIHUOACCOUNTWELCOME = HOST_API_HTML_STATIC+"/beginners/openaccount.html";
    //首页新手学堂
    public static final String URL_HOME_XSXT = HOST_API_HTML + "/beginners/home.html";
    /*关于我们*/
    public static final String URL_ABOUT_US = HOST_API_HTML + "/beginners/about.html";
    /* 交易大厅  顶部规则*/
    public static final String URL_TRADE_RULE = HOST_API_HTML + "/beginners/rule.html";
    /*出入金规则规则*/
    public static final String URL_CASHINOUT_RULE = HOST_API_HTML + "/beginners/money.html";
    /* 如何添加银行卡  如何开通银期*/
    public static final String URL_HOWTO_ADDCARD = HOST_API_HTML + "/beginners/bind.html";
    //注册  用户协议
    public static final String URL_FENGXIANTISHI = HOST_API_HTML + "/beginners/xieyi.html";

    //开启页的广告图 sourceId
    public static final String URL_ADS_APP = HOST_API + "/news/enable/startup/image/android";
    //收集android收集的imei；userId=2&imei=12313
    public static final String URL_COLLECT_IMEI = HOST_API + "/user/info/device/startup/imei";
    //个推推送需要保存的信息version cid deviceInformation
    public static final String URL_COLLECT_GETUI_INFO = HOST_API + "/app/getui/user/version/information";
    //百度渠道的开启咨询开关
    public static final String URL_SWITCH_SHOWNEWS = HOST_API + "/user/news/enable";
    //3.1之后登录 mobileNum  sourceId password
    public static final String URL_USER_LOGIN = HOST_API + "/user/info/login";
    //初始化密码发送短信 mobileNum sourceId
    public static final String URL_USER_INIT_PWD_SMS = HOST_API + "/user/info/initPassport/send/sms";
    //初始化密码 mobileNum sourceId password code
    public static final String URL_USER_INIT_PWD = HOST_API + "/user/info/initPassport";
    //3.1之后注册
    public static final String URL_USER_REGISTER = HOST_API + "/user/info/register";
    //注册获取短信 mobileNum sourceId
    public static final String URL_USER_REGISTER_SMS = HOST_API + "/user/info/register/send/sms";
    //找回密码
    public static final String URL_USER_RESET_PWD = HOST_API + "/user/info/retrieve/password";
    //找回密码短信
    public static final String URL_USER_RESET_PWD_SMS = HOST_API + "/user/info/retrieve/password/send/sms";
    //检测手机是否初始化过密码 mobileNum
    public static final String URL_USER_CHECK_MOBILE = HOST_API + "/user/info/check/mobile";
    //更新用户昵称  userId nickName
    public static final String URL_USER_UPDATE_NICKNAME = HOST_API + "/user/info/update/nickName";
    //更新用户头像  userId file
    public static final String URL_USER_UPDATE_AVATAR = HOST_API + "/user/info/update/avatar";

    //注册获取短信v2 语音
//	public static final String URL_GET_VALICODE = HOST_API + "/gg/user/v2/register/send/sms";
    //注册获取短信第一个版本的
    public static final String URL_GET_VALICODE = HOST_API + "/gg/user/register/send/sms";
    //修改密码
    public static final String URL_RESET_PWD = HOST_API + "/gg/user/update/password";

    /*8元短信验证码验证  mobile，code*/
    public static final String URL_CHECK_SMS = HOST_API + "/gg/user/register/sms/check";

    //首页滚动广告
    public static final String URL_HOME_ADS = HOST_API + "/news/activity/list?sourceId={sourceId}";
    // 首页、直播室轮播
    public static final String URL_ADS_4HOMEANDLIVE = HOST_API + "/news/api/appcontent/activity/list";
    //重要信息v2
    public static final String URL_IMPORT_NEWS = HOST_API + "/news/portal/important/list/v2?startPos=0&count=9";
    //重要信息详情页
    public static final String URL_IMPORT_NEWS_DETAIL = HOST_API + "/news/portal/important/{id}/v2";
    //重要信息详情页的文字广告
    public static final String URL_IMPORT_NEWS_DETAIL_ADS = HOST_API + "/news/app/important/detail/ad";
    //财经日历
    public static final String URL_CALENDER = HOST_API + "/news/predict/list?date={date}&startPos=0&count=9";//?date=2015-08-13
    //财经日历详情
    public static final String URL_CALENDER_DETAIL = HOST_API + "/news/predict/detail?date={date}&id={id}";
    //首页盈利排行榜
    public static final String URL_PROFIT_RANK = HOST_API_RANK + "/news/board/profit/rate/v2/list";
    //首页盈利排行榜  新版首页
    public static final String URL_PROFIT_RANK_V1 = HOST_API_RANK + "/news/api/billboard/show/order/list";
    //盈利排行榜详情  orderId 必须使用正式环境才能看见数据  v2
    public static final String URL_PROFIT_RANK_DETAIL = HOST_API_RANK + "/news/board/profit/rate/v2/order";
    //盈利榜之最新用户晒单 我的排名
    public static final String URL_PROFIT_MOSTNEWRANK = HOST_API_RANK + "/news/api/billboard/me/rank";
    //盈利榜之我的盈利榜
    public static final String URL_PROFIT_MYPROFITORDER = HOST_API_RANK + "/news/api/billboard/profit/order/list";
    //根据 mnId 返回最新的通知
    public static final String URL_WEIPAN_NEW_MSG_BYMNID = HOST_API + "/user/micro/notify/lastList";//userId  mnId
    //消息列表  分页 page count
    public static final String URL_WEIPAN_MSG_LIST = HOST_API + "/user/micro/notify/list";//userId page count

    //特殊渠道 开关
    public static final String URL_WEIPAN_SWITCH = HOST_API + "/news/enable/ios/version";
    //客服qq
    public static final String URL_GET_SERVICE_NUMBER = HOST_API + "/news/v2/serviceNumber/select";
    //在线客服的配置信息  请求参数:userId  uid  sourceId  market
//    public static final String URL_GET_ONLINE_HELP = HOST_API + "/news/api/helpline/click";
    public static final String URL_GET_ONLINE_HELP = HOST_API + "/news/api/helpline/clickV2";

    //获取交易所列表  versionNo必须加上
    public static final String URL_EXCHANGE_LIST = HOST_API + "/app/exchange/list/v2";

    //获取分享url userId
    public static final String URL_SHARE_GET_URL = HOST_API + "/user/activity/get/share/url";

    //分享成功url回调  userId
//	public static final String URL_SHARE_SUCCESS = HOST_API + "/user/activity/v2/share/success";
    //分享成功url回调  userId 哈贵送券
    public static final String URL_SHARE_SUCCESS = HOST_API + "/user/activity/hg/share/success";

    //获取晒单分享链接 userId orderid exchangeId
    public static final String URL_SHARE_ORDER = HOST_API + "/user/activity/order/share/show";
    //获取晒单分享链接 分享成功请求 userId orderid exchangeId
    public static final String URL_SHARE_ORDER_SUCCESS = HOST_API + "/user/activity/order/share/show/success";

    //邀请好友拿红包start
    //获取入口信息
    public static final String URL_SHARE_JOIN_GETACTS = HOST_API + "/user/point/invite/getActivityInfo";
    //获取红包数量接口
    public static final String URL_SHARE_JOIN_GET_VOCHE = HOST_API + "/user/point/invite/getVoucherNum";

    //邀请好友拿红包end

    //获取行情tab 显示的所有交易所
    public static final String URL_TAB_EX_LIST = HOST_API + "/news/api/quotation/exchange/list/v2";

    // 获取行情tab 期货所有产品
    public static final String URL_TAB_GETALL_PRODUCT = HOST_WEIPAN_MARKET + "/quotation/realTime/main";


    //=============直播室接口start
    //获取聊天室房间号  userId
    public static final String URL_CHATROOM_ROOMID = HOST_API + "/app/user/netease/user/get/roomId";
    //输入昵称注册云信ID userId  name
    public static final String URL_CHATROOM_UPDATE_NAME = HOST_API + "/app/user/netease/user/update/name";
    //聊天室公告
    public static final String URL_CHATROOM_GONGGAO = HOST_API + "/thirdParty/netEase/notice";
    //喊单 操作建议 roomId
//    public static final String URL_CHATROOM_ADVICE = HOST_API + "/news/v2/outcry/list";
    public static final String URL_CHATROOM_ADVICE = HOST_API + "/news/api/qutcry/v3/list";// v3接口 增加喊单置顶
    //直播房间频道
//	public static final String URL_LIVE_LIST = HOST_API + "/thirdParty/qcloud/liveChannel/list";
    public static final String URL_LIVE_LIST = HOST_API + "/news/live/list";
    //直播房间频道v4 update at 17/03/29 by 海洋
    public static final String URL_LIVE_LIST_V4 = HOST_API + "/news/api/v4/liveVideo/list";
    //直播室是否允许发图片
    public static final String URL_LIVE_SENDPIC = HOST_API + "/news/api/message/v4/sendPicStatus";
    //直播时间安排
    public static final String URL_LIVE_PLAN = HOST_API_HTML + "/static/live/dispatch.html";
    //直播室敏感词接口 userId
    public static final String URL_LIVE_KEYWORD = HOST_API + "/app/user/netease/user/keyword/list";
    //直播室禁言 remark userId
    public static final String URL_LIVE_DISABLE_USER = HOST_API + "/app/user/netease/user/disable";
    //直播室礼物列表接口 userId,page, pageSize
    public static final String URL_LIVE_GIFT_LIST = HOST_API + "/user/gift/liveGift/list";
    //直播室礼物兑换赠送
    public static final String URL_LIVE_GIFT_SEND = HOST_API + "/user/gift/liveGift/exchange";


    //直播室获取老师信息
    public static final String URL_LIVE_AUTHORINFO = HOST_API + "/news/api/live/author/info";
    //直播室观看记录添加
    public static final String URL_LIVE_WATCHADD = HOST_API + "/news/api/live/watch/user/add";
    //===========直播室接口end

    //休市时间说明
    public static final String URL_RULES_QA = HOST_API_HTML + "/static/rules/jywt/";
    //获取省份
    public static final String URL_GET_PROVICE = HOST_API + "/app/fxbtg/dict/province/list";
    //获取省份下的城市  provinceId
    public static final String URL_GET_CITY = HOST_API + "/app/fxbtg/dict/city/list";
    //获取支行
    public static final String URL_GET_BANK_BRANCH = HOST_API + "/app/fxbtg/dict/branchBank/list";

    //实名认证，开户协议
    public static final String URL_AGRE_OPEN_ACCOUNT = HOST_API_HTML + "/product/agreement_openacc.html";

    // 常见问题
    public static final String URL_FQ = HOST_API_HTML + "/static/faq/";
    //介绍 一分钟了解
//    public static final String URL_WEIPAN_DES = HOST_API_HTML + "/static/know_weipan/";
    public static final String URL_WEIPAN_DES = HOST_API_HTML + "/static/product/xsxt/";
    // 版本检测
    public static final String URL_UPGRADEINFO = HOST_API + "/news/api/version/check/android";
    //获取配置信息
//    public static final String URL_STARTUP_CONFIG = HOST_API + "/news/app/startup/v3/config";
    //合并接口 1、交易所列表  2、首页热门产品 3、tcp连接(首页弹窗广告，android应用推荐换量)  4、开启图片 5、市场屏蔽开关
    public static final String URL_STARTUP_CONFIG = HOST_API + "/news/api/startup/init";
    //首页资讯列表
    public static final String URL_HOME_INFOEMATION_LIST = HOST_API + "/news/api/transaction/opportunity/list/";
    //首页资讯利多利空操作
    public static final String URL_HOME_INFOEMATION_OPERATIONINSERT = HOST_API + "/news/api/transaction/operation/insert";
   //代金券使用规则  是从新手学堂里面拷贝的地址  地址不会变动
    public static final String URL_VOUCHER_RULE = HOST_API_HTML + "/static/product/xsxt/djq.html";

    /*统一交易密码(start) add by haiyang*****************************/
    //检查用户是否设置统一密码
    public static final String URL_ACCOUNT_CHECKBIND = HOST_API + "/app/user/account/check/bind";
    //用户交易所注册、绑定情况(包含是否统一过交易密码)
    public static final String URL_ACCOUNT_EXCHANGELIST = HOST_API + "/app/user/account/exchange/list";
    //批量检查交易所密码是否正确
    public static final String URL_ACCOUNT_CHECKEXCHANGEPWD_BATCH = HOST_API + "/app/user/account/check/exchange/password/batch";
    //单个检查交易所密码是否正确
    public static final String URL_ACCOUNT_CHECKEXCHANGEPWD = HOST_API + "/app/user/account/check/exchange/password";
    //设置统一交易密码
    public static final String URL_ACCOUNT_UNIFYPWD_SET = HOST_API + "/app/user/account/unify/pwd/set";
    //统一交易密码登陆
    public static final String URL_ACCOUNT_UNIFYPWD_LOGIN = HOST_API + "/app/user/account/unify/login";
    //重置统一交易密码获取验证码
    public static final String URL_ACCOUNT_UNIFYPWD_SMSCODE = HOST_API + "/app/user/account/unify/resetPwd/sms";
    //重置统一交易密码
    public static final String URL_ACCOUNT_UNIFYPWD_RSET = HOST_API + "/app/user/account/unify/resetPwd";
    // 各项条款
    public static final String URL_ACCOUNT_UNIFYPWD_PROTOCOL = HOST_API_HTML + "/static/protocol/tymm_mz_protocol.html";
    /*统一交易密码(end) add by haiyang*****************************/

    /*积分(start)************************/
    // 查看当前积分和等级
    public static final String URL_ACCOUNT_INTEGRALINFO = HOST_API + "/user/point/select";
    //等级列表
    public static final String URL_INTEGRAL_LEVELLIST = HOST_API + "/user/point/level/list";
    //积分商品列表 17/03/30 add by 海洋
    public static final String URL_INTEGRAL_PRODUCTLIST = HOST_API + "/user/gift/list/v2";
    //查看积分明细
    public static final String URL_ACCOUNT_INTEGRALDETAIL = HOST_API + "/user/point/rec/list";
    //积分兑换
    public static final String URL_ACCOUNT_INTEGRAL_EXCHANGE = HOST_API + "/user/gift/exchange";
    //积分兑换历史
    public static final String URL_ACCOUNT_INTEGRAL_EXCHANGEHISTORY = HOST_API + "/user/gift/exchange/list";
    //积分规则介绍
    public static final String URL_INTEGRAL_INTRO = HOST_API_HTML + "/static/product/jifen/";
    //礼物详情页
    public static final String URL_ACTGIFT_DETAIL = HOST_API + "/user/gift/detail";
    //礼物详情页--兑换历史
    public static final String URL_ACTGIFT_DETAIL_HISTORY = HOST_API + "/user/gift/history/detail";
    /*积分(end) add by haiyang*****************************/


    //充值方式列表接口2017-01-17
    public static final String URL_RECHARGE_MONEYANDTYPE = HOST_API + "/app/exchange/recharge/type/list/v3";

    /*行情定制  2017-02-13*****************************/
    //行情定制-产品信息接口
    public static final String URL_PRODUCRTNOTICE_INFO = HOST_API + "/news/api/quotes/reminder/product/setting";
    //行情定制-添加操作
    public static final String URL_PRODUCRTNOTICE_ADD = HOST_API + "/news/api/quotes/reminder/add";
    //行情定制-获取列表
    public static final String URL_PRODUCRTNOTICE_LIST = HOST_API + "/news/api/quotes/reminder/list";
    //行情定制-删除操作
    public static final String URL_PRODUCRTNOTICE_DELETE = HOST_API + "/news/api/quotes/reminder/delete";
    //行情定制-编辑操作
    public static final String URL_PRODUCRTNOTICE_EDIT = HOST_API + "/news/api/quotes/reminder/update";
    //行情定制-功能说明
    public static final String URL_PRODUCRTNOTICE_INTRO = HOST_API_HTML_STATIC + "/product/quotationReminder/";

    /*行情定制 add by haiyang*****************************/

    /*任务中心 start********************/
    //获取任务中心列表
    public static final String URL_MISSIONCENTER_LIST = HOST_API + "/user/task/select";
    //获取题目列表
    public static final String URL_MISSIONCENTER_QUESTION_LIST = HOST_API + "/user/task/question/list";
    //完成答题提交接口
    public static final String URL_MISSIONCENTER_QUESTIONSAVE = HOST_API + "/user/task/question/save";
    /*任务中心 add by haiyang*****************************/

    /*消息中心 start********************/
    //消息中心列表
    public static final String URL_MESSAGECENTER_LIST = HOST_API + "/news/api/user/message/list";
    //消息中心读取
    public static final String URL_MESSAGECENTER_READ = HOST_API + "/news/api/user/message/read";
    //消息中心读取全部
    public static final String URL_MESSAGECENTER_READALL = HOST_API + "/news/api/user/message/all/read";
    // 消息中心未读条数
    public static final String URL_MESSAGECENTER_UNREAD = HOST_API + "/news/api/user/message/unread/count";

    /*消息中心 end add by haiyang*****************************/


    /*与交易系统相关的url start==================================================================*/
    // 期货

    //登录
    public static final String URL_TRADE_USER_LOGIN_GG = HOST_TRADE + "/app/future/exchange/user/login";
    // 退出登录
    public static final String URL_TRADE_USER_LOGIN_OUT = HOST_TRADE + "/app/future/exchange/user/login/out";

    //交易大厅产品listv2
    public static final String URL_TRADE_PRODUCT_LIST_GG = HOST_TRADE_QIHUO + "/app/future/exchange/trade/product/list";
    //产品详情
    public static final String URL_TRADE_PRODUCT_DETAIL = HOST_TRADE_QIHUO + "/app/future/exchange/trade/product/detail";
    //用户交易信息
    public static final String URL_TRADEINFO_GG = HOST_TRADE + "/app/future/exchange/user/account/info";
    //建仓
    public static final String URL_TRADE_ORDER_CREATE_GG = HOST_TRADE + "/app/future/exchange/trade/create/order";
    //持仓列表 userId v2
    public static final String URL_TRADE_ORDER_HOLD_LIST_GG = HOST_TRADE + "/app/future/exchange/trade/holdPosition/list";
    // 持仓详情
    public static final String URL_TRADE_ORDER_DETAIL = HOST_TRADE + "/app/future/exchange/trade/holdPosition/detail";
    //平仓userId orderId
    public static final String URL_TRADE_ORDER_CLOSE_GG = HOST_TRADE + "/app/future/exchange/trade/close/order";
    // 账单确认明细
    public static final String URL_CHECKBALANCE_DETAIL = HOST_TRADE + "/app/future/exchange/user/settlement/info";
//    public static final String URL_CHECKBALANCE_DETAIL = HOST_TRADE + "/app/future/exchange/user/settlement/base/info";


    // 账单确认操作
    public static final String URL_CHECKBALANCE_OPTION = HOST_TRADE + "/app/future/exchange/user/settlement/info/confirm";
    // 已平仓订单
    public static final String URL_ORDERCLOSE_LIST = HOST_TRADE + "/app/future/exchange/orders/close/list";
    // 已平仓订单详情
    public static final String URL_ORDERCLOSE_DETAIL = HOST_TRADE + "/app/future/exchange/orders/close/details";
    // 历史订单-建/平 盈利统计
    public static final String URL_ORDERHISTORY_TOTAL = HOST_TRADE + "/app/future/exchange/orders/history/stat";
    // 历史订单-建仓历史
    public static final String URL_ORDERHISTORY_CREATELIST = HOST_TRADE + "/app/future/exchange/orders/history/open/list";
    // 历史订单-平仓历史
    public static final String URL_ORDERHISTORY_CLOSELIST = HOST_TRADE + "/app/future/exchange/orders/history/close/list";
    //用户持有银行卡 userId
    public static final String URL_TRADE_GET_USER_BANKS_GG = HOST_TRADE + "/app/future/exchange/transfer/bank/list";
    // 用户银行卡余额
    public static final String URL_TRADE_GET_USER_BANKMONEY = HOST_TRADE + "/app/future/exchange/transfer/select/balance";
    // 用户充值
    public static final String URL_TRADE_USER_CASHIN = HOST_TRADE + "/app/future/exchange/transfer/recharge";
    // 用户提现
    public static final String URL_TRADE_USER_CASHOUT = HOST_TRADE + "/app/future/exchange/transfer/cashout";
    // 用户出入金明细
    public static final String URL_TRADE_USER_CASHHISTORY = HOST_TRADE + "/app/future/exchange/transfer/detail";
    // 用户修改交易密码
    public static final String URL_TRADE_USER_UPDATEPWD = HOST_TRADE + "/app/future/exchange/user/update/password";
    // 用户修改资金密码
    public static final String URL_TRADE_USER_UPDATEPWD_MONEY = HOST_TRADE + "/app/future/exchange/user/update/trade/password";




    //注册接口
    public static final String URL_TRADE_USER_REG_GG = HOST_TRADE + "/gg/user/v3/register";
    //交易所登录 注册  userId，检测返回地址，如果注册过了返回交易所的登录；没有注册返回交易所的注册页面
    public static final String URL_TRADE_USER_CHECK_GG = HOST_TRADE + "/gg/user/v3/check/exchange";
    //重置密码
    public static final String URL_USER_RESET_PWD_TRADE_GG = HOST_TRADE + "/gg/user/v3/resetPwd";
    //接收短信重置密码
    public static final String URL_USER_RESET_PWD_TRADE_SMS_GG = HOST_TRADE + "/gg/user/v3/resetPwd/sms";
    /*获取单个品种*/
    public static final String URL_TRADE_GET_PRODUCT_OBJ = HOST_TRADE + "/app/fxbtg/exchange/trade/product/get";


    //用户产品代金劵数量  当前产品  userId productId
    public static final String URL_TRADE_PID_VOUCHER_GG = HOST_TRADE + "/gg/exchange/v3/product/voucher/all";
    //订单设置止盈、止损 userId orderId stopProfit stopLoss v2
    public static final String URL_TRADE_ORDER_SET_PROFIT_GG = HOST_TRADE + "/app/fxbtg/exchange/trade/order/update";
    //用户余额 userId
    public static final String URL_TRADE_GET_USERINFO_GG = HOST_TRADE + "/app/fxbtg/exchange/user/account/info";
    //交易记录 userId page pageSize v2
    public static final String URL_TRADE_GET_HISTORY_LIST_GG = HOST_TRADE + "/app/fxbtg/exchange/trade/history/list";
    //提现记录列表
    public static final String URL_CASHOUT_LIST_GG = HOST_TRADE + "/app/fxbtg/exchange/cashOut/list";
    //充值记录列表
    public static final String URL_RECHARGE_LIST_GG = HOST_TRADE + "/app/fxbtg/exchange/recharge/list";

    //收支明细 userId page pageSize
    public static final String URL_TRADE_GET_DETAIL_LIST_GG = HOST_TRADE + "/gg/user/v3/balance/list";
    //用户代金券 userId page pageSize
    public static final String URL_TRADE_GET_VOUCHER_LIST_GG = HOST_TRADE + "/gg/user/v3/voucher/list";
    //用户充值选择金额列表 userId
//    public static final String URL_TRADE_GET_CASH_IN_AMOUNT_LIST_GG = HOST_TRADE + "/gg/user/v3/recharge/amount/list";
    //用户充值 userId
    public static final String URL_TRADE_CASH_IN_GG = HOST_TRADE + "/gg/user/v3/recharge/cash";
    //提现 参数较多 见接口使用
    public static final String URL_TRADE_GET_CASH_OUT_GG = HOST_TRADE + "/app/fxbtg/exchange/cashOut/insert";
    //提现短信
    public static final String URL_TRADE_GET_CASH_OUT_SMS_GG = HOST_TRADE + "/gg/user/v3/cash/out/sms";
    //提现可用的银行名称列表
    public static final String URL_TRADE_BANK_LIST_GG = HOST_TRADE + "/app/fxbtg/dict/bank/list";
    //交易规则
    public static final String URL_TRADE_RULE_GG = HOST_API_HTML + "/static/rules/rules-gg.html";

    //京东快捷支付-检测绑定，并发短信
    public static final String URL_TRADE_JDPAY_CHECK_BIND_GG = HOST_TRADE + "/gg/user/v3/jdPay/check/bind";
    //京东快捷支付-绑定银行卡，并返回订单号
    public static final String URL_TRADE_JDPAY_SIGN_GG = HOST_TRADE + "/gg/user/v3/jdPay/sign";
    //京东快捷支付-支付
    public static final String URL_TRADE_JDPAY_PAY_GG = HOST_TRADE + "/gg/user/v3/jdPay";
    //京东快捷支付-银行列表
    public static final String URL_TRADE_JDPAY_BANK_LIST_GG = HOST_TRADE + "/gg/user/v3/jdPay/bankName/list";

    //微信支付 使用微信原生sdk支付
    public static final String URL_IWXPAY_WX_PAY_GG = HOST_TRADE + "/gg/user/v4/wechatPay/minsheng";

    //----------------------

    //哈贵 hg
    //登录
    public static final String URL_TRADE_USER_LOGIN_HG = HOST_TRADE_HG + "/hg/user/v3/login";
    //注册接口
    public static final String URL_TRADE_USER_REG_HG = HOST_TRADE_HG + "/hg/user/v3/register";
    //交易所登录 注册  userId，检测返回地址，如果注册过了返回交易所的登录；没有注册返回交易所的注册页面
    public static final String URL_TRADE_USER_CHECK_HG = HOST_TRADE_HG + "/hg/user/v3/check/exchange";
    //重置密码
    public static final String URL_USER_RESET_PWD_TRADE_HG = HOST_TRADE_HG + "/hg/user/v3/resetPwd";
    //接收短信重置密码
    public static final String URL_USER_RESET_PWD_TRADE_SMS_HG = HOST_TRADE_HG + "/hg/user/v3/resetPwd/sms";
    //交易大厅产品
    public static final String URL_TRADE_PRODUCT_LIST_HG = HOST_TRADE_HG + "  /hg/exchange/v4/product/list";
    //建仓
    public static final String URL_TRADE_ORDER_CREATE_HG = HOST_TRADE_HG + "/hg/exchange/v4/order/create";
    //平仓userId orderId
    public static final String URL_TRADE_ORDER_CLOSE_HG = HOST_TRADE_HG + "/hg/exchange/v3/order/close/position";
    //用户产品代金劵数量  当前产品  userId productId
    public static final String URL_TRADE_PID_VOUCHER_HG = HOST_TRADE_HG + "/hg/exchange/v3/product/voucher/all";
    //持仓列表 userId
    public static final String URL_TRADE_ORDER_HOLD_LIST_HG = HOST_TRADE_HG + "/hg/exchange/v4/order/holdPosition/list";
    //订单设置止盈、止损 userId orderId stopProfit stopLoss v2
    public static final String URL_TRADE_ORDER_SET_PROFIT_HG = HOST_TRADE_HG + "/hg/exchange/v4/order/set/profitLoss";
    //用户余额 userId
    public static final String URL_TRADE_GET_USERINFO_HG = HOST_TRADE_HG + "/hg/user/v3/info";
    //交易记录 userId page pageSize v2
    public static final String URL_TRADE_GET_HISTORY_LIST_HG = HOST_TRADE_HG + "/hg/user/v3/exchanged/list";
    //收支明细 userId page pageSize
    public static final String URL_TRADE_GET_DETAIL_LIST_HG = HOST_TRADE_HG + "/hg/user/v3/balance/list";
    //用户代金券 userId page pageSize
    public static final String URL_TRADE_GET_VOUCHER_LIST_HG = HOST_TRADE_HG + "/hg/user/v3/voucher/list";
    //用户持有银行卡 userId
    public static final String URL_TRADE_GET_USER_BANKS_HG = HOST_TRADE_HG + "/hg/user/v3/bank/list";
    //用户充值选择金额列表 userId
//    public static final String URL_TRADE_GET_CASH_IN_AMOUNT_LIST_HG = HOST_TRADE_HG + "/hg/user/v3/recharge/amount/list";
    //用户充值 userId
    public static final String URL_TRADE_CASH_IN_HG = HOST_TRADE_HG + "/hg/user/v3/recharge/cash";
    //提现 参数较多 见接口使用
    public static final String URL_TRADE_GET_CASH_OUT_HG = HOST_TRADE_HG + "/hg/user/v3/cash/out";
    //提现短信
    public static final String URL_TRADE_GET_CASH_OUT_SMS_HG = HOST_TRADE_HG + "/hg/user/v3/cash/out/sms";
    //提现可用的银行名称列表
    public static final String URL_TRADE_BANK_LIST_HG = HOST_TRADE_HG + "/hg/user/v3/bank/name/list";
    //哈贵的微信支付 userId amount
//    public static final String URL_WX_PAY_HG = HOST_TRADE_HG + "/hg/user/v3/wxt";// 点心支付
//    public static final String URL_WX_PAY_HG = HOST_TRADE_HG + "/hg/user/v3/wxp";// 现在支付
    public static final String URL_WX_PAY_HG = HOST_TRADE_HG + "/hg/user/v3/wxp/type";//统一微信支付接口

    //哈贵-微信支付结果检查，检查是否成功 id{"success":true,"errorCode":null,"errorInfo":null,"pagerManager":null,"data":{"exchangeId":2,"exchangeName":null,"url":"http://www.8caopan.com/recharge/hg_index.html?tk=b1445df6c6984fa987628b2e2ff02b53_606&amount=10.0&userId=6129&cardNo=6217001210015143603","redirectUrl":"http://hgwp.8caopan.com/hg/user/v3/recharge/cash/redirect"}}

    public static final String URL_WX_PAY_CHECK_HG = HOST_TRADE_HG + "/hg/user/v3/wx/checkResult";
    //交易规则
    public static final String URL_TRADE_RULE_HG = HOST_API_HTML + "/static/rules/rules-hg.html";
    // 哈贵支付宝充值
    public static final String URL_TRADE_CASHIN_ALIPAY_HG = HOST_TRADE_HG + "/hg/user/v4/recharge/alipay";
    //微信支付 使用微信原生sdk支付
    public static final String URL_IWXPAY_WX_PAY_HG = HOST_TRADE_HG + "/hg/user/v4/wechatPay/minsheng";
    //京东快捷支付-检测绑定，并发短信
    public static final String URL_TRADE_JDPAY_CHECK_BIND_HG = HOST_TRADE_HG + "/hg/user/v4/jdPay/check/bind";
    //京东快捷支付-绑定银行卡，并返回订单号
    public static final String URL_TRADE_JDPAY_SIGN_HG = HOST_TRADE_HG + "/hg/user/v4/jdPay/sign";
    //京东快捷支付-支付
    public static final String URL_TRADE_JDPAY_PAY_HG = HOST_TRADE_HG + "/hg/user/v4/jdPay";
    //京东快捷支付-银行列表
    public static final String URL_TRADE_JDPAY_BANK_LIST_HG = HOST_TRADE_HG + "/hg/user/v4/jdPay/bankName/list";
    //获取交易信息(余额 代金券 特权卡等)
    public static final String URL_TRADE_INFO_HG = HOST_TRADE_HG + "/hg/user/v4/info";


    //农交所
    //比哈贵广贵多的接口-----start
    //持仓过夜手续费 userId exchangeId orderId
    public static final String URL_TRADE_DEFERRED_JN = HOST_TRADE_JN + "/app/jn/exchange/trade/deferred/list";
    //比哈贵广贵多的接口-----end

    //登录
    public static final String URL_TRADE_USER_LOGIN_JN = HOST_TRADE_JN + "/app/jn/exchange/user/login";
    //注册接口
    public static final String URL_TRADE_USER_REG_JN = HOST_TRADE_JN + "/app/jn/exchange/user/register";
    //check交易所登录 注册  userId，检测返回地址，如果注册过了返回交易所的登录；没有注册返回交易所的注册页面
    public static final String URL_TRADE_USER_CHECK_JN = HOST_TRADE_JN + "/app/jn/exchange/user/register/check";
    //农交所重置密码
    public static final String URL_USER_RESET_PWD_TRADE_JN = HOST_TRADE_JN + "/app/jn/exchange/user/resetPwd";
    //接收短信农交所重置密码
    public static final String URL_USER_RESET_PWD_TRADE_SMS_JN = HOST_TRADE_JN + "/app/jn/exchange/user/resetPwd/sms";
    //交易大厅产品listv2
    public static final String URL_TRADE_PRODUCT_LIST_JN = HOST_TRADE_JN + "/app/jn/exchange/trade/product/list";
    //建仓
    public static final String URL_TRADE_ORDER_CREATE_JN = HOST_TRADE_JN + "/app/jn/exchange/trade/order/create";
    //平仓userId orderId
    public static final String URL_TRADE_ORDER_CLOSE_JN = HOST_TRADE_JN + "/app/jn/exchange/trade/order/close";
    //用户产品代金劵数量  当前产品  userId productId 2017-01-17
    public static final String URL_TRADE_PID_VOUCHER_JN = HOST_TRADE_JN + "/app/jn/exchange/trade/voucher/count";
    //持仓列表 userId v2
    public static final String URL_TRADE_ORDER_HOLD_LIST_JN = HOST_TRADE_JN + "/app/jn/exchange/trade/holdPosition/list/v2";
    //订单设置止盈、止损
    public static final String URL_TRADE_ORDER_SET_PROFIT_JN = HOST_TRADE_JN + "/app/jn/exchange/trade/order/update";
    //用户余额 userId
    public static final String URL_TRADE_GET_USERINFO_JN = HOST_TRADE_JN + "/app/jn/exchange/user/account/info";
    //交易记录 userId page pageSize v2
    public static final String URL_TRADE_GET_HISTORY_LIST_JN = HOST_TRADE_JN + "/app/jn/exchange/trade/history/list";
    //收支明细 userId page pageSize
//    public static final String URL_TRADE_GET_DETAIL_LIST_JN = HOST_TRADE_JN + "";
    //用户代金券(农交所里称之为红包) userId page pageSize
//    public static final String URL_TRADE_GET_VOUCHER_LIST_JN = HOST_TRADE_JN + "/app/jn/exchange/trade/coupon/list";
    //代金券2017-01-17
    public static final String URL_TRADE_GET_VOUCHER_LIST_JN = HOST_TRADE_JN + "/app/jn/exchange/trade/voucher/list";
    //用户领取红包
    public static final String URL_TRADE_GET_RECEIVE_COUPON_JN = HOST_TRADE_JN + "/app/jn/exchange/trade/coupon/receive";
    //用户持有银行卡 userId
    public static final String URL_TRADE_GET_USER_BANKS_JN = HOST_TRADE_JN + "/app/jn/exchange/user/bankList";
    //用户充值选择金额列表 userId
//    public static final String URL_TRADE_GET_CASH_IN_AMOUNT_LIST_JN = HOST_TRADE_JN + "/app/jn/exchange/recharge/amount/list";
    //用户充值 银联支付
    public static final String URL_TRADE_CASH_IN_JN = HOST_TRADE_JN + "/app/jn/exchange/recharge/nowUnion/pay";
    //提现 参数较多 见接口使用
    public static final String URL_TRADE_GET_CASH_OUT_JN = HOST_TRADE_JN + "/app/jn/exchange/cashOut/insert";
    //提现短信
//    public static final String URL_TRADE_GET_CASH_OUT_SMS_JN = HOST_TRADE_JN + "";
    //提现可用的银行名称列表(农交所 沿用哈贵)
    public static final String URL_TRADE_BANK_LIST_JN = HOST_TRADE_HG + "/hg/user/v3/bank/name/list";
    //微信支付 (点芯支付)userId amount
//    public static final String URL_WX_PAY_JN = HOST_TRADE_JN + "/app/jn/exchange/recharge/dotPay/pay";
    public static final String URL_WX_PAY_JN = HOST_TRADE_JN + "/app/jn/exchange/recharge/dotPay/pay/v2";
    //农交所-微信支付结果检查，检查是否成功 id
    public static final String URL_WX_PAY_CHECK_JN = HOST_TRADE_JN + "/app/jn/exchange/recharge/dotPay/verify";
    //提现记录列表
    public static final String URL_CASHOUT_LIST_JN = HOST_TRADE_JN + "/app/jn/exchange/cashOut/list";
    //充值记录列表
    public static final String URL_RECHARGE_LIST_JN = HOST_TRADE_JN + "/app/jn/exchange/recharge/list";
    //农交所交易规则
    public static final String URL_TRADE_RULE_JN = HOST_API_HTML + "/static/know_weipan/jn.html";
    //红包流水
    public static final String URL_REDPACKET_LIST_JN = HOST_TRADE_JN + "/app/jn/exchange/trade/coupon/flows";
    //提现手续费
    public static final String URL_CASHOUT_FEE_JN = HOST_TRADE_JN + "/app/jn/exchange/cashOut/fee";

    //京东快捷支付-检测绑定，并发短信
    public static final String URL_TRADE_JDPAY_CHECK_BIND_JN = HOST_TRADE_JN + "/app/jn/exchange/recharge/jdPay/check/bind";
    //京东快捷支付-绑定银行卡，并返回订单号
    public static final String URL_TRADE_JDPAY_SIGN_JN = HOST_TRADE_JN + "/app/jn/exchange/recharge/jdPay/sign";
    //京东快捷支付-支付
    public static final String URL_TRADE_JDPAY_PAY_JN = HOST_TRADE_JN + "/app/jn/exchange/recharge/jdPay";
    //京东快捷支付-银行列表
    public static final String URL_TRADE_JDPAY_BANK_LIST_JN = HOST_TRADE_JN + "/app/jn/exchange/recharge/jdPay/bankName/list";
    //支付宝支付扫码支付
    public static final String URL_TRADE_ZHIFUBAO_SCAN_JN = HOST_TRADE_JN + "/app/jn/exchange/recharge/alipay/scan";
    //获取交易信息(余额 代金券 特权卡等)
    public static final String URL_TRADE_INFO_JN = HOST_TRADE_JN + "/app/jn/exchange/user/v2/account/info";
    //吉农提现实名制
    public static final String URL_CASHSHIMING_JN = HOST_API_HTML_HTTPS + "/static/auth/jn_auth.html";


    //华凝所
    //持仓过夜手续费 userId exchangeId orderId
    public static final String URL_TRADE_DEFERRED_HN = HOST_TRADE_HN + "/app/hn/exchange/trade/deferred/list";
    //登录
    public static final String URL_TRADE_USER_LOGIN_HN = HOST_TRADE_HN + "/app/hn/exchange/user/login";
    //注册接口
    public static final String URL_TRADE_USER_REG_HN = HOST_TRADE_HN + "/app/hn/exchange/user/register";
    //check交易所登录 注册  userId，检测返回地址，如果注册过了返回交易所的登录；没有注册返回交易所的注册页面
    public static final String URL_TRADE_USER_CHECK_HN = HOST_TRADE_HN + "/app/hn/exchange/user/register/check";
    //农交所重置密码
    public static final String URL_USER_RESET_PWD_TRADE_HN = HOST_TRADE_HN + "/app/hn/exchange/user/resetPwd";
    //接收短信农交所重置密码
    public static final String URL_USER_RESET_PWD_TRADE_SMS_HN = HOST_TRADE_HN + "/app/hn/exchange/user/resetPwd/sms";
    //交易大厅产品listv2
    public static final String URL_TRADE_PRODUCT_LIST_HN = HOST_TRADE_HN + "/app/hn/exchange/trade/product/list";
    //建仓
    public static final String URL_TRADE_ORDER_CREATE_HN = HOST_TRADE_HN + "/app/hn/exchange/trade/order/create";
    //平仓userId orderId
    public static final String URL_TRADE_ORDER_CLOSE_HN = HOST_TRADE_HN + "/app/hn/exchange/trade/order/close";
    //用户产品代金劵数量  当前产品  userId productId 2017-01-17
    public static final String URL_TRADE_PID_VOUCHER_HN = HOST_TRADE_HN + "/app/hn/exchange/trade/voucher/count";
    //持仓列表 userId v2
    public static final String URL_TRADE_ORDER_HOLD_LIST_HN = HOST_TRADE_HN + "/app/hn/exchange/trade/holdPosition/list/v2";
    //订单设置止盈、止损
    public static final String URL_TRADE_ORDER_SET_PROFIT_HN = HOST_TRADE_HN + "/app/hn/exchange/trade/order/update";
    //用户余额 userId
    public static final String URL_TRADE_GET_USERINFO_HN = HOST_TRADE_HN + "/app/hn/exchange/user/account/info";
    //交易记录 userId page pageSize v2
    public static final String URL_TRADE_GET_HISTORY_LIST_HN = HOST_TRADE_HN + "/app/hn/exchange/trade/history/list";
    //代金券2017-01-17
    public static final String URL_TRADE_GET_VOUCHER_LIST_HN = HOST_TRADE_HN + "/app/hn/exchange/trade/voucher/list";
    //用户领取红包
    public static final String URL_TRADE_GET_RECEIVE_COUPON_HN = HOST_TRADE_HN + "/app/hn/exchange/trade/coupon/receive";
    //用户持有银行卡 userId
    public static final String URL_TRADE_GET_USER_BANKS_HN = HOST_TRADE_HN + "/app/hn/exchange/user/bankList";
    //用户充值 银联支付
    public static final String URL_TRADE_CASH_IN_HN = HOST_TRADE_HN + "/app/hn/exchange/recharge/nowUnion/pay";
    //提现 参数较多 见接口使用
    public static final String URL_TRADE_GET_CASH_OUT_HN = HOST_TRADE_HN + "/app/hn/exchange/cashOut/insert";
    //提现可用的银行名称列表(农交所 沿用哈贵)
    public static final String URL_TRADE_BANK_LIST_HN = HOST_TRADE_HG + "/hg/user/v3/bank/name/list";
    public static final String URL_WX_PAY_HN = HOST_TRADE_HN + "/app/hn/exchange/recharge/dotPay/pay/v2";
    //农交所-微信支付结果检查，检查是否成功 id
    public static final String URL_WX_PAY_CHECK_HN = HOST_TRADE_HN + "/app/hn/exchange/recharge/dotPay/verify";
    //提现记录列表
    public static final String URL_CASHOUT_LIST_HN = HOST_TRADE_HN + "/app/hn/exchange/cashOut/list";
    //充值记录列表
    public static final String URL_RECHARGE_LIST_HN = HOST_TRADE_HN + "/app/hn/exchange/recharge/list";
    //华凝所交易规则
    public static final String URL_TRADE_RULE_HN = HOST_API_HTML + "/static/rules/rules-hn.html";
    //红包流水
    public static final String URL_REDPACKET_LIST_HN = HOST_TRADE_HN + "/app/hn/exchange/trade/coupon/flows";
    //提现手续费
    public static final String URL_CASHOUT_FEE_HN = HOST_TRADE_HN + "/app/hn/exchange/cashOut/fee";

    //京东快捷支付-检测绑定，并发短信
    public static final String URL_TRADE_JDPAY_CHECK_BIND_HN = HOST_TRADE_HN + "/app/hn/exchange/recharge/jdPay/check/bind";
    //京东快捷支付-绑定银行卡，并返回订单号
    public static final String URL_TRADE_JDPAY_SIGN_HN = HOST_TRADE_HN + "/app/hn/exchange/recharge/jdPay/sign";
    //京东快捷支付-支付
    public static final String URL_TRADE_JDPAY_PAY_HN = HOST_TRADE_HN + "/app/hn/exchange/recharge/jdPay";
    //京东快捷支付-银行列表
    public static final String URL_TRADE_JDPAY_BANK_LIST_HN = HOST_TRADE_HN + "/app/hn/exchange/recharge/jdPay/bankName/list";
    //支付宝支付扫码支付
    public static final String URL_TRADE_ZHIFUBAO_SCAN_HN = HOST_TRADE_HN + "/app/hn/exchange/recharge/alipay/scan";
    //获取交易信息(余额 代金券 特权卡等)
    public static final String URL_TRADE_INFO_HN = HOST_TRADE_HN + "/app/hn/exchange/user/v2/account/info";
    //吉农提现实名制
    public static final String URL_CASHSHIMING_HN = HOST_API_HTML_HTTPS + "/static/auth/jn_auth.html";
    /*与交易系统相关的url ==================================================================*/

    //定义key
    public static final String KEY_URL_TRADE_USER_CHECK = "URL_TRADE_USER_CHECK";
    public static final String KEY_URL_TRADE_PRODUCT_LIST = "URL_TRADE_PRODUCT_LIST";
    public static final String KEY_URL_TRADE_ORDER_CREATE = "URL_TRADE_ORDER_CREATE";
    public static final String KEY_URL_TRADE_ORDER_CLOSE = "URL_TRADE_ORDER_CLOSE";
    public static final String KEY_URL_TRADE_PID_VOUCHER = "URL_TRADE_PID_VOUCHER";
    public static final String KEY_URL_TRADE_ORDER_HOLD_LIST = "URL_TRADE_ORDER_HOLD_LIST";
    public static final String KEY_URL_TRADE_ORDER_SET_PROFIT = "URL_TRADE_ORDER_SET_PROFIT";
    public static final String KEY_URL_TRADE_GET_USERINFO = "URL_TRADE_GET_USERINFO";
    public static final String KEY_URL_TRADE_GET_HISTORY_LIST = "URL_TRADE_GET_HISTORY_LIST";
    public static final String KEY_URL_TRADE_GET_DETAIL_LIST = "URL_TRADE_GET_DETAIL_LIST";
    public static final String KEY_URL_TRADE_GET_VOUCHER_LIST = "URL_TRADE_GET_VOUCHER_LIST";
    public static final String KEY_URL_TRADE_RECEIVE_COUPON_JN = "KEY_URL_TRADE_RECEIVE_COUPON_JN";// 领取红包
    public static final String KEY_URL_TRADE_GET_USER_BANKS = "URL_TRADE_GET_USER_BANKS";
    public static final String KEY_URL_TRADE_GET_CASH_IN_AMOUNT_LIST = "URL_TRADE_GET_CASH_IN_AMOUNT_LIST";
    public static final String KEY_URL_TRADE_CASH_IN = "URL_TRADE_CASH_IN";
    public static final String KEY_URL_TRADE_GET_CASH_OUT = "URL_TRADE_GET_CASH_OUT";
    public static final String KEY_URL_TRADE_GET_CASH_OUT_SMS = "URL_TRADE_GET_CASH_OUT_SMS";
    public static final String KEY_URL_TRADE_BANK_LIST = "URL_TRADE_BANK_LIST";
    public static final String KEY_URL_TRADE_CASH_IN_WX = "URL_TRADE_CASH_IN_WX";
    public static final String KEY_URL_TRADE_CASH_IN_WXCHECK = "KEY_URL_TRADE_CASH_IN_WXCHECK";
    public static final String KEY_URL_TRADE_RULE = "URL_TRADE_RULE";
    public static final String KEY_URL_TRADE_USER_REG = "URL_TRADE_USER_REG";
    public static final String KEY_URL_TRADE_USER_LOGIN = "KEY_URL_TRADE_USER_LOGIN";
    public static final String KEY_URL_USER_RESET_PWD_TRADE = "URL_USER_RESET_PWD_TRADE";
    public static final String KEY_URL_USER_RESET_PWD_TRADE_SMS = "URL_USER_RESET_PWD_TRADE_SMS";
    public static final String KEY_URL_USER_CASHIN_ALIPAY = "KEY_URL_USER_CASHIN_ALIPAY";
    public static final String KEY_URL_TRADE_JDPAY_CHECK_BIND = "URL_TRADE_JDPAY_CHECK_BIND";
    public static final String KEY_URL_TRADE_JDPAY_SIGN = "URL_TRADE_JDPAY_SIGN";
    public static final String KEY_URL_TRADE_JDPAY_PAY = "URL_TRADE_JDPAY_PAY";
    public static final String KEY_URL_TRADE_JDPAY_BANK_LIST = "URL_TRADE_JDPAY_BANK_LIST";
    public static final String KEY_URL_IWXPAY_WX_PAY = "URL_MS_WX_PAY";
    public static final String KEY_URL_TRADEINFO = "URL_TRADEINFO";
    public static final String KEY_URL_TRADE_CASHIN_HISTORY = "URL_TRADE_CASHIN_HISTORY";//充值记录
    public static final String KEY_URL_TRADE_CASHOUT_HISTORY = "URL_TRADE_CASHOUT_HISTORY";//提现记录
    public static final String KEY_URL_TRADE_CASHOUT_FEE = "URL_TRADE_CASHOUT_FEE";//提现手续费
    public static final String KEY_URL_TRADE_DEFERRED = "URL_TRADE_DEFERRED";//持仓过夜费


    /*与交易系统相关的url 使用map获取  使用key get url*/
    //广贵所
    protected static HashMap<String, String> ggApi = new HashMap<String, String>();
    //哈贵所
    protected static HashMap<String, String> hgApi = new HashMap<String, String>();
    //农交所
    protected static HashMap<String, String> jnApi = new HashMap<String, String>();
    //华凝所
    protected static HashMap<String, String> hnApi = new HashMap<String, String>();

    static {
        ggApi.put(KEY_URL_TRADE_USER_CHECK, URL_TRADE_USER_CHECK_GG);
        ggApi.put(KEY_URL_TRADE_PRODUCT_LIST, URL_TRADE_PRODUCT_LIST_GG);
        ggApi.put(KEY_URL_TRADE_ORDER_CREATE, URL_TRADE_ORDER_CREATE_GG);
        ggApi.put(KEY_URL_TRADE_ORDER_CLOSE, URL_TRADE_ORDER_CLOSE_GG);
        ggApi.put(KEY_URL_TRADE_PID_VOUCHER, URL_TRADE_PID_VOUCHER_GG);
        ggApi.put(KEY_URL_TRADE_ORDER_HOLD_LIST, URL_TRADE_ORDER_HOLD_LIST_GG);
        ggApi.put(KEY_URL_TRADE_ORDER_SET_PROFIT, URL_TRADE_ORDER_SET_PROFIT_GG);
        ggApi.put(KEY_URL_TRADE_GET_USERINFO, URL_TRADE_GET_USERINFO_GG);
        ggApi.put(KEY_URL_TRADE_GET_HISTORY_LIST, URL_TRADE_GET_HISTORY_LIST_GG);
        ggApi.put(KEY_URL_TRADE_GET_DETAIL_LIST, URL_TRADE_GET_DETAIL_LIST_GG);
        ggApi.put(KEY_URL_TRADE_GET_VOUCHER_LIST, URL_TRADE_GET_VOUCHER_LIST_GG);
        ggApi.put(KEY_URL_TRADE_GET_USER_BANKS, URL_TRADE_GET_USER_BANKS_GG);
        ggApi.put(KEY_URL_TRADE_CASH_IN, URL_TRADE_CASH_IN_GG);
        ggApi.put(KEY_URL_TRADE_GET_CASH_OUT, URL_TRADE_GET_CASH_OUT_GG);
        ggApi.put(KEY_URL_TRADE_GET_CASH_OUT_SMS, URL_TRADE_GET_CASH_OUT_SMS_GG);
        ggApi.put(KEY_URL_TRADE_BANK_LIST, URL_TRADE_BANK_LIST_GG);
        ggApi.put(KEY_URL_TRADE_RULE, URL_TRADE_RULE_GG);
        ggApi.put(KEY_URL_TRADE_USER_REG, URL_TRADE_USER_REG_GG);
        ggApi.put(KEY_URL_TRADE_USER_LOGIN, URL_TRADE_USER_LOGIN_GG);
        ggApi.put(KEY_URL_USER_RESET_PWD_TRADE, URL_USER_RESET_PWD_TRADE_GG);
        ggApi.put(KEY_URL_USER_RESET_PWD_TRADE_SMS, URL_USER_RESET_PWD_TRADE_SMS_GG);

        ggApi.put(KEY_URL_TRADE_JDPAY_CHECK_BIND, URL_TRADE_JDPAY_CHECK_BIND_GG);
        ggApi.put(KEY_URL_TRADE_JDPAY_SIGN, URL_TRADE_JDPAY_SIGN_GG);
        ggApi.put(KEY_URL_TRADE_JDPAY_PAY, URL_TRADE_JDPAY_PAY_GG);
        ggApi.put(KEY_URL_TRADE_JDPAY_BANK_LIST, URL_TRADE_JDPAY_BANK_LIST_GG);

        ggApi.put(KEY_URL_IWXPAY_WX_PAY, URL_IWXPAY_WX_PAY_GG);
        ggApi.put(KEY_URL_TRADE_CASHIN_HISTORY, URL_RECHARGE_LIST_GG);
        ggApi.put(KEY_URL_TRADE_CASHOUT_HISTORY, URL_CASHOUT_LIST_GG);
        ggApi.put(KEY_URL_TRADEINFO,URL_TRADEINFO_GG );

        //==============
        hgApi.put(KEY_URL_TRADE_USER_CHECK, URL_TRADE_USER_CHECK_HG);
        hgApi.put(KEY_URL_TRADE_PRODUCT_LIST, URL_TRADE_PRODUCT_LIST_HG);
        hgApi.put(KEY_URL_TRADE_ORDER_CREATE, URL_TRADE_ORDER_CREATE_HG);
        hgApi.put(KEY_URL_TRADE_ORDER_CLOSE, URL_TRADE_ORDER_CLOSE_HG);
        hgApi.put(KEY_URL_TRADE_PID_VOUCHER, URL_TRADE_PID_VOUCHER_HG);
        hgApi.put(KEY_URL_TRADE_ORDER_HOLD_LIST, URL_TRADE_ORDER_HOLD_LIST_HG);
        hgApi.put(KEY_URL_TRADE_ORDER_SET_PROFIT, URL_TRADE_ORDER_SET_PROFIT_HG);
        hgApi.put(KEY_URL_TRADE_GET_USERINFO, URL_TRADE_GET_USERINFO_HG);
        hgApi.put(KEY_URL_TRADE_GET_HISTORY_LIST, URL_TRADE_GET_HISTORY_LIST_HG);
        hgApi.put(KEY_URL_TRADE_GET_DETAIL_LIST, URL_TRADE_GET_DETAIL_LIST_HG);
        hgApi.put(KEY_URL_TRADE_GET_VOUCHER_LIST, URL_TRADE_GET_VOUCHER_LIST_HG);
        hgApi.put(KEY_URL_TRADE_GET_USER_BANKS, URL_TRADE_GET_USER_BANKS_HG);
        hgApi.put(KEY_URL_TRADE_CASH_IN, URL_TRADE_CASH_IN_HG);
        hgApi.put(KEY_URL_TRADE_GET_CASH_OUT, URL_TRADE_GET_CASH_OUT_HG);
        hgApi.put(KEY_URL_TRADE_GET_CASH_OUT_SMS, URL_TRADE_GET_CASH_OUT_SMS_HG);
        hgApi.put(KEY_URL_TRADE_BANK_LIST, URL_TRADE_BANK_LIST_HG);
        hgApi.put(KEY_URL_TRADE_CASH_IN_WX, URL_WX_PAY_HG);
        hgApi.put(KEY_URL_TRADE_CASH_IN_WXCHECK, URL_WX_PAY_CHECK_HG);
        hgApi.put(KEY_URL_TRADE_RULE, URL_TRADE_RULE_HG);
        hgApi.put(KEY_URL_TRADE_USER_REG, URL_TRADE_USER_REG_HG);
        hgApi.put(KEY_URL_TRADE_USER_LOGIN, URL_TRADE_USER_LOGIN_HG);
        hgApi.put(KEY_URL_USER_RESET_PWD_TRADE, URL_USER_RESET_PWD_TRADE_HG);
        hgApi.put(KEY_URL_USER_RESET_PWD_TRADE_SMS, URL_USER_RESET_PWD_TRADE_SMS_HG);
        hgApi.put(KEY_URL_USER_CASHIN_ALIPAY, URL_TRADE_CASHIN_ALIPAY_HG);

        hgApi.put(KEY_URL_TRADE_JDPAY_CHECK_BIND, URL_TRADE_JDPAY_CHECK_BIND_HG);
        hgApi.put(KEY_URL_TRADE_JDPAY_SIGN, URL_TRADE_JDPAY_SIGN_HG);
        hgApi.put(KEY_URL_TRADE_JDPAY_PAY, URL_TRADE_JDPAY_PAY_HG);
        hgApi.put(KEY_URL_TRADE_JDPAY_BANK_LIST, URL_TRADE_JDPAY_BANK_LIST_HG);

        hgApi.put(KEY_URL_IWXPAY_WX_PAY, URL_IWXPAY_WX_PAY_HG);
        hgApi.put(KEY_URL_TRADEINFO, URL_TRADE_INFO_HG);

        //==============
        jnApi.put(KEY_URL_TRADE_USER_CHECK, URL_TRADE_USER_CHECK_JN);
        jnApi.put(KEY_URL_TRADE_PRODUCT_LIST, URL_TRADE_PRODUCT_LIST_JN);
        jnApi.put(KEY_URL_TRADE_ORDER_CREATE, URL_TRADE_ORDER_CREATE_JN);
        jnApi.put(KEY_URL_TRADE_ORDER_CLOSE, URL_TRADE_ORDER_CLOSE_JN);
        jnApi.put(KEY_URL_TRADE_PID_VOUCHER, URL_TRADE_PID_VOUCHER_JN);
        jnApi.put(KEY_URL_TRADE_ORDER_HOLD_LIST, URL_TRADE_ORDER_HOLD_LIST_JN);
        jnApi.put(KEY_URL_TRADE_ORDER_SET_PROFIT, URL_TRADE_ORDER_SET_PROFIT_JN);
        jnApi.put(KEY_URL_TRADE_GET_USERINFO, URL_TRADE_GET_USERINFO_JN);
        jnApi.put(KEY_URL_TRADE_GET_HISTORY_LIST, URL_TRADE_GET_HISTORY_LIST_JN);
        jnApi.put(KEY_URL_TRADE_GET_VOUCHER_LIST, URL_TRADE_GET_VOUCHER_LIST_JN);
        jnApi.put(KEY_URL_TRADE_RECEIVE_COUPON_JN, URL_TRADE_GET_RECEIVE_COUPON_JN);// 领取红包
        jnApi.put(KEY_URL_TRADE_GET_USER_BANKS, URL_TRADE_GET_USER_BANKS_JN);
        jnApi.put(KEY_URL_TRADE_CASH_IN, URL_TRADE_CASH_IN_JN);
        jnApi.put(KEY_URL_TRADE_GET_CASH_OUT, URL_TRADE_GET_CASH_OUT_JN);
        jnApi.put(KEY_URL_TRADE_BANK_LIST, URL_TRADE_BANK_LIST_JN);
        jnApi.put(KEY_URL_TRADE_CASH_IN_WX, URL_WX_PAY_JN);
        jnApi.put(KEY_URL_TRADE_CASH_IN_WXCHECK, URL_WX_PAY_CHECK_JN);
        jnApi.put(KEY_URL_TRADE_RULE, URL_TRADE_RULE_JN);
        jnApi.put(KEY_URL_TRADE_USER_REG, URL_TRADE_USER_REG_JN);
        jnApi.put(KEY_URL_TRADE_USER_LOGIN, URL_TRADE_USER_LOGIN_JN);
        jnApi.put(KEY_URL_USER_RESET_PWD_TRADE, URL_USER_RESET_PWD_TRADE_JN);
        jnApi.put(KEY_URL_USER_RESET_PWD_TRADE_SMS, URL_USER_RESET_PWD_TRADE_SMS_JN);

        jnApi.put(KEY_URL_TRADE_JDPAY_CHECK_BIND, URL_TRADE_JDPAY_CHECK_BIND_JN);
        jnApi.put(KEY_URL_TRADE_JDPAY_SIGN, URL_TRADE_JDPAY_SIGN_JN);
        jnApi.put(KEY_URL_TRADE_JDPAY_PAY, URL_TRADE_JDPAY_PAY_JN);
        jnApi.put(KEY_URL_TRADE_JDPAY_BANK_LIST, URL_TRADE_JDPAY_BANK_LIST_JN);
        jnApi.put(KEY_URL_TRADEINFO, URL_TRADE_INFO_JN);
        jnApi.put(KEY_URL_TRADE_CASHIN_HISTORY, URL_RECHARGE_LIST_JN);
        jnApi.put(KEY_URL_TRADE_CASHOUT_HISTORY, URL_CASHOUT_LIST_JN);
        jnApi.put(KEY_URL_TRADE_CASHOUT_FEE, URL_CASHOUT_FEE_JN);
        jnApi.put(KEY_URL_TRADE_DEFERRED, URL_TRADE_DEFERRED_JN);


        //==============
        hnApi.put(KEY_URL_TRADE_USER_CHECK, URL_TRADE_USER_CHECK_HN);
        hnApi.put(KEY_URL_TRADE_PRODUCT_LIST, URL_TRADE_PRODUCT_LIST_HN);
        hnApi.put(KEY_URL_TRADE_ORDER_CREATE, URL_TRADE_ORDER_CREATE_HN);
        hnApi.put(KEY_URL_TRADE_ORDER_CLOSE, URL_TRADE_ORDER_CLOSE_HN);
        hnApi.put(KEY_URL_TRADE_PID_VOUCHER, URL_TRADE_PID_VOUCHER_HN);
        hnApi.put(KEY_URL_TRADE_ORDER_HOLD_LIST, URL_TRADE_ORDER_HOLD_LIST_HN);
        hnApi.put(KEY_URL_TRADE_ORDER_SET_PROFIT, URL_TRADE_ORDER_SET_PROFIT_HN);
        hnApi.put(KEY_URL_TRADE_GET_USERINFO, URL_TRADE_GET_USERINFO_HN);
        hnApi.put(KEY_URL_TRADE_GET_HISTORY_LIST, URL_TRADE_GET_HISTORY_LIST_HN);
        hnApi.put(KEY_URL_TRADE_GET_VOUCHER_LIST, URL_TRADE_GET_VOUCHER_LIST_HN);
        hnApi.put(KEY_URL_TRADE_RECEIVE_COUPON_JN, URL_TRADE_GET_RECEIVE_COUPON_HN);// 领取红包
        hnApi.put(KEY_URL_TRADE_GET_USER_BANKS, URL_TRADE_GET_USER_BANKS_HN);
        hnApi.put(KEY_URL_TRADE_CASH_IN, URL_TRADE_CASH_IN_HN);
        hnApi.put(KEY_URL_TRADE_GET_CASH_OUT, URL_TRADE_GET_CASH_OUT_HN);
        hnApi.put(KEY_URL_TRADE_BANK_LIST, URL_TRADE_BANK_LIST_HN);
        hnApi.put(KEY_URL_TRADE_CASH_IN_WX, URL_WX_PAY_HN);
        hnApi.put(KEY_URL_TRADE_CASH_IN_WXCHECK, URL_WX_PAY_CHECK_HN);
        hnApi.put(KEY_URL_TRADE_RULE, URL_TRADE_RULE_HN);
        hnApi.put(KEY_URL_TRADE_USER_REG, URL_TRADE_USER_REG_HN);
        hnApi.put(KEY_URL_TRADE_USER_LOGIN, URL_TRADE_USER_LOGIN_HN);
        hnApi.put(KEY_URL_USER_RESET_PWD_TRADE, URL_USER_RESET_PWD_TRADE_HN);
        hnApi.put(KEY_URL_USER_RESET_PWD_TRADE_SMS, URL_USER_RESET_PWD_TRADE_SMS_HN);

        hnApi.put(KEY_URL_TRADE_JDPAY_CHECK_BIND, URL_TRADE_JDPAY_CHECK_BIND_HN);
        hnApi.put(KEY_URL_TRADE_JDPAY_SIGN, URL_TRADE_JDPAY_SIGN_HN);
        hnApi.put(KEY_URL_TRADE_JDPAY_PAY, URL_TRADE_JDPAY_PAY_HN);
        hnApi.put(KEY_URL_TRADE_JDPAY_BANK_LIST, URL_TRADE_JDPAY_BANK_LIST_HN);
        hnApi.put(KEY_URL_TRADEINFO, URL_TRADE_INFO_HN);
        hnApi.put(KEY_URL_TRADE_CASHIN_HISTORY, URL_RECHARGE_LIST_HN);
        hnApi.put(KEY_URL_TRADE_CASHOUT_HISTORY, URL_CASHOUT_LIST_HN);
        hnApi.put(KEY_URL_TRADE_CASHOUT_FEE, URL_CASHOUT_FEE_HN);
        hnApi.put(KEY_URL_TRADE_DEFERRED, URL_TRADE_DEFERRED_HN);
    }

    /**
     * 根据定义的key 获取 不同交易所的api
     *
     * @param context
     * @param key
     * @return
     */
    public static String getAPI(Context context, String key) {
        if (TradeConfig.code_jn.equals(TradeConfig.getCurrentTradeCode(context)))
            return jnApi.get(key);
        if (TradeConfig.code_hg.equals(TradeConfig.getCurrentTradeCode(context)))
            return hgApi.get(key);
        if (TradeConfig.code_hn.equals(TradeConfig.getCurrentTradeCode(context)))
            return hnApi.get(key);
        return ggApi.get(key);
    }

    /**
     * 根据定义的key 获取 不同交易所的api
     *
     * @param context
     * @param key
     * @param exchangeCode// 交易code
     * @return
     */

    public static String getAPI(Context context, String key, String exchangeCode) {
        if (TradeConfig.code_jn.equals(exchangeCode))
            return jnApi.get(key);
        if (TradeConfig.code_hg.equals(exchangeCode))
            return hgApi.get(key);
        if (TradeConfig.code_hn.equals(exchangeCode))
            return hnApi.get(key);
        return ggApi.get(key);
    }

    /*实名制认证状态信息 userId token*/
    public static final String URL_AUTH_STATUS = HOST_TRADE + "/app/fxbtg/exchange/user/auth/status";
    /*实名制认身份证正面信息*/
    public static final String URL_AUTH_FRONT_CARD = HOST_TRADE + "/app/fxbtg/exchange/user/idCard/frontDist";
    /*实名制认身份证反面信息*/
    public static final String URL_AUTH_BACK_CARD = HOST_TRADE + "/app/fxbtg/exchange/user/idCard/backDist";
    /*实名制认证提交信息*/
    public static final String URL_AUTH_SUBMIT = HOST_TRADE + "/app/fxbtg/exchange/user/auth/submit";
    /*提现绑定银行卡*/
    public static final String URL_CASHOUT_BINDCARD = HOST_TRADE + "/app/fxbtg/exchange/user/bind/card";
    /*充值地址获取*/
    public static final String URL_CASHIN = HOST_TRADE + "/app/fxbtg/exchange/recharge/url";

}
