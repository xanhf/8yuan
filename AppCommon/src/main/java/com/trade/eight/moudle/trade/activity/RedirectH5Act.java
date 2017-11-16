package com.trade.eight.moudle.trade.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.RegData;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.trade.CashInEvent;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.NetWorkUtils;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.OpenActivityUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.nav.UNavConfig;
import com.trade.eight.tools.trade.TradeConfig;

import java.util.LinkedHashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by fangzhu on 2015/4/28.
 * 当前act 做的处理逻辑
 * 1、交易所的登录注册  先用url检测是登录还是注册
 * regData 为null
 * <p>
 * 2、充值h5跳转;充值直接传入的是 regData
 * type : getdata 的type  注册 还是登录需要
 * regData  作为传入的判断
 */
public class RedirectH5Act extends BaseActivity {
    WebView webView;
    RedirectH5Act context = null;
    String phone = null, code = null, title;
    public static final String TAG = "RedirectH5Act";
    // 返回h5的data结果
    RegData regData;
    //注册
    public static final int TYPE_REG = RegData.TYPE_REG;
    //登录
    public static final int TYPE_LOGIN = RegData.TYPE_LOGIN;
    //充值
    public static final int TYPE_CASH = 9527;

    //getdata 获取输入H5 的action
    int getDataType = TYPE_REG;
    //重定向的action
    int redirectType = TYPE_REG;

    private static int CODE_REQUEST = 100;


    /**
     * Token过期errorCode
     * 交易所的登录还是注册检测
     * 先用url检测是登录还是注册，然后设置标题，load url
     *
     * @param mContext
     */
    public static void startTokenCheck(Context mContext) {
        Intent intent = new Intent();
        intent.setClass(mContext, RedirectH5Act.class);
        intent.putExtra("type", RedirectH5Act.TYPE_LOGIN);
        intent.putExtra("redirectType", RedirectH5Act.TYPE_LOGIN);

        mContext.startActivity(intent);
    }

    /**
     * 输入登录密码
     *
     * @param mContext
     * @param data
     */
    public static void startTokenLogin(Context mContext, RegData data) {
        Intent intent = new Intent();
        intent.setClass(mContext, RedirectH5Act.class);
        intent.putExtra("object", data);
        intent.putExtra("title", ConvertUtil.NVL(data.getTypeName(), "登录"));
        intent.putExtra("redirectType", RedirectH5Act.TYPE_LOGIN);
        mContext.startActivity(intent);
    }

    /**
     * 第一次设置交易密码
     *
     * @param mContext
     * @param data
     */
    public static void startTokenReg(Context mContext, RegData data) {
        Intent intent = new Intent();
        intent.setClass(mContext, RedirectH5Act.class);
        intent.putExtra("object", data);
        intent.putExtra("title", ConvertUtil.NVL(data.getTypeName(), "设置交易密码"));
        intent.putExtra("redirectType", RedirectH5Act.TYPE_REG);
        mContext.startActivity(intent);
    }

    /**
     * 网银充值网页url callback处理
     *
     * @param mContext
     * @param data
     */
    public static void startCashin(Context mContext, RegData data) {
        Intent intent = new Intent();
        intent.setClass(mContext, RedirectH5Act.class);
        intent.putExtra("object", data);
        intent.putExtra("title", "充值");
        intent.putExtra("redirectType", RedirectH5Act.TYPE_CASH);
        mContext.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_redirect_html);

        if (!new UserInfoDao(context).isLogin()) {
            showCusToast("请登录后重试");
            startActivity(new Intent(context, LoginActivity.class));
            finish();
            return;
        }
        if (new UserInfoDao(context).isLogin()) {
            phone = new UserInfoDao(context).queryUserInfo().getMobileNum();
        }

        code = getIntent().getStringExtra("code");
        title = ConvertUtil.NVL(getIntent().getStringExtra("title"), "");
        setAppCommonTitle(title);

        getDataType = getIntent().getIntExtra("type", TYPE_REG);
        redirectType = getIntent().getIntExtra("redirectType", TYPE_REG);
        initViews();
        initListener();

    }

    void initViews() {
        initWebView();

        regData = (RegData) getIntent().getSerializableExtra("object");
        if (regData == null) {
            //获取 h5 网页地址信息
            getData();
        } else {
            //处理 重定向
            webView.loadUrl(regData.getUrl());

        }

    }

    /**
     * 获取 h5 网页地址信息
     * 必须登录后
     */
    void getData() {
        //获取输入密码的h5 网页
        Map<String, String> paraMap = ApiConfig.getCommonMap(context);
        paraMap.put(UserInfo.UID, new UserInfoDao(context).queryUserInfo().getUserId());
        paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

        new GetDataTask(paraMap).execute();
    }

    void initWebView() {
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        //不使用缓存
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(false);//不缓存
//        webView.getSettings().setAppCachePath();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                // TODO Auto-generated method stub
                Log.e(TAG, "onJsAlert  url" + url + "   message" + message);

                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress < 100 && !isProgressDialogShowing()) {
                    showNetLoadingProgressDialog("请稍等");//or do it onCreate

                }
                if (newProgress == 100) {
                    Log.v(TAG, "newProgress == 100");
                    hideNetLoadingProgressDialog();
                }

            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                Log.e(TAG, "onJsPrompt  url" + url + "   message" + message + "    defaultValue" + defaultValue);
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
//                Log.v(TAG, "onLoadResource=" + url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                try {
                    //handler.cancel(); // Android默认的处理方式
                    //添加下面代码来忽略SSL验证
                    handler.proceed();  // 接受所有网站的证书
                    //handleMessage(Message msg); // 进行其他处理
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null)
                    return true;
                Log.v(TAG, "shouldOverrideUrlLoading=" + url);
                if (url.trim().startsWith("tel:")) {
                    String phoneStr = url.replace("tel:", "");
                    try {
                        //打电话
                        //拨号盘界面
                        String tel = StringUtil.replace(phoneStr, "-", "");
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if (url.startsWith("mqqwpa://")) {
                    //打开QQ
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if (regData != null && url.startsWith(regData.getRedirectUrl())) {
                    if (redirectType == TYPE_CASH) {
                        //充值成功  不需要在http访问
                        hideNetLoadingProgressDialog();
                        //返回到个人页面
                        showCusToast("充值成功");
                        finish();
                        EventBus.getDefault().post(new CashInEvent(true));
                        return true;
                    }
                    //h5跳转RedirectUrl 表示操作成功
                    String regUrl = url;//自己带有参数
                    //设置参数  加密，暂时只需要mobile
                    Map<String, String> paraMap = ApiConfig.getCommonMap(context);
                    paraMap.put(UserInfo.UMOBLE, phone);
//                    paraMap.put(ApiConfig.PARAM_SOURCEID, AppSetting.getInstance(context).getAppSource() + "");
//                    paraMap.put(ApiConfig.PARAM_CODE, code);
//                    paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));
                    //封装成get 请求
                    regUrl = NetWorkUtils.setParam4get(regUrl, paraMap);
                    //返回token，放在Cookie中。
                    //解析token，和返回的用户信息
                    //新的map用于auth验证，post  api  param
                    String api = "";
                    Map<String, String> finalParaMap = new LinkedHashMap<String, String>();
                    try {
                        String[] strs = regUrl.split("\\?");
                        api = strs[0];
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
//                                if (key.equalsIgnoreCase("url") && value.length() > 0)
//                                    value = URLDecoder.decode(value, "utf-8");
                                if (key != null && key.trim().length() > 0)
                                    finalParaMap.put(key, value);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //auth 验证
                    finalParaMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, finalParaMap));
                    //POST
                    new RedirectTypeTask(api, finalParaMap).execute();
                    return true;

                }
                if (url.startsWith("alipays://")) {
                    Log.v(TAG, "alipays://");
                    //目前有响应支付宝支付
                    startAlipayActivity(url);
                    return true;
                }
                if (url.startsWith("weixin://wap/pay?")) {
                    //微信支付
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivityForResult(intent, CODE_REQUEST);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showCusToast("您安装微信最新版支付");
                    }
                    return true;
                }

                /*else if (url.startsWith("weixin://wap/pay?")) {
                    //微信支付
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivityForResult(intent, CODE_REQUEST);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showCusToast("您还没有安装微信");
                    }
                    return true;
                }
                else if(regData != null && regData.getCashInType() == 2 && url.startsWith(regData.getRedirectUrl())){
                    if (redirectType == TYPE_CASH) {
                        //充值成功  不需要在http访问
                        //返回到个人页面
                        showCusToast("充值成功");
                        finish();
                        return true;
                    }
                }*/
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
                //onPageStarted will call many times progressDialog can not dismiss
                //do it onProgressChanged, byfangzhu
//				showNetLoadingProgressDialog("加载中...");
                try {
                    if (regData == null)
                        return;
                    if (regData.getRedirectUrl() == null)
                        return;
                    if (url == null)
                        return;
                    if (url.startsWith(regData.getRedirectUrl())) {
                        Log.e(TAG, "onPageStarted" + " url=" + url);
                        if (redirectType == TYPE_CASH) {
                            //充值成功  不需要在http访问
                            hideNetLoadingProgressDialog();
                            //返回到个人页面
                            showCusToast("充值成功");
                            finish();
                            EventBus.getDefault().post(new CashInEvent(true));
                        }else{
                            hideNetLoadingProgressDialog();
                            finish();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//				hideNetLoadingProgressDialog();
                Log.e(TAG, "onPageFinished");

            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // TODO Auto-generated method stub
                super.onReceivedError(view, errorCode, description, failingUrl);
                //do it onProgressChanged, byfangzhu
//				hideNetLoadingProgressDialog();
                Log.e(TAG, "onReceivedError");
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    void initListener() {
        View gobackView = findViewById(R.id.gobackView);
        if (gobackView != null) {
            gobackView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


    }

    /**
     * 传入的RegData为null
     * 首先获取RegData
     */
    class GetDataTask extends AsyncTask<String, Void, CommonResponse<RegData>> {

        Map<String, String> paraMap = null;

        public GetDataTask(Map<String, String> paraMap) {
            this.paraMap = paraMap;
        }


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            showNetLoadingProgressDialog(null);
        }

        @Override
        protected CommonResponse<RegData> doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_USER_CHECK);
                String res = HttpClientHelper.getStringFromPost(context, url, paraMap);
                if (res != null)
                    return CommonResponse.fromJson(res, RegData.class);
            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(CommonResponse<RegData> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
//            hideNetLoadingProgressDialog();
            try {
                if (result != null) {
                    if (result.isSuccess()) {
                        if (result.getData() == null)
                            return;
                        regData = result.getData();

                        setAppCommonTitle(ConvertUtil.NVL(regData.getTypeName(), title));
                        redirectType = regData.getType();

                        if (StringUtil.isEmpty(regData.getUrl()))
                            return;
                        //开启网页 输入密码，在webview中load  url，当在webview中load regData.getRedirectUrl()  表示callack成功，拦截 app自己处理
                        webView.loadUrl(regData.getUrl());

                    } else {
                        showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "操作失败，请重试"));
                    }

                } else {
                    showCusToast("网络异常，请重试！");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据RegData，对h5重定向
     */
    class RedirectTypeTask extends AsyncTask<String, Void, CommonResponse<UserInfo>> {
        String url = null;
        Map<String, String> paraMap = null;

        public RedirectTypeTask(String url, Map<String, String> paraMap) {
            this.url = url;
            this.paraMap = paraMap;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
//            showNetLoadingProgressDialog("请稍等");
        }

        @Override
        protected CommonResponse<UserInfo> doInBackground(String... params) {
            try {
                return TradeHelp.redirect(context, url, paraMap);
            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(CommonResponse<UserInfo> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
//            hideNetLoadingProgressDialog();
            if (result != null) {
                if (result.isSuccess()) {
                    if (redirectType == TYPE_REG) {
//                        showCusToast("注册成功！");
                        //本地记录已经初始化过密码
                        TradeConfig.setInitPwdLocal(context, TradeConfig.getCurrentTradeCode(context), true);

                        UNavConfig.setShowStep01(context, false);
                        UNavConfig.setShowStep02(context, true);

//                        //设置哈贵密码成功之后，设置本地标示，显示新手引导第二步
//                        if (TradeConfig.isCurrentHG(context))
//                            UNavConfig.setShowStep02(context, true);
                        //设置完交易密码之后就不显示小红包
                        UNavConfig.setShowSmallDlg(context, false);

                    } else if (redirectType == TYPE_LOGIN) {
//                        showCusToast("登录成功！");
                        //本地记录已经初始化过密码 登录成功表示肯定已经是设置过交易所的密码了
                        TradeConfig.setInitPwdLocal(context, TradeConfig.getCurrentTradeCode(context), true);

                        UNavConfig.setShowSmallDlg(context, false);
                    } else {
//                        showCusToast("操作成功！");

                    }


                    //处理成功之后的跳转
                    String action = getIntent().getStringExtra(OpenActivityUtil.KEY_ACTION);
                    if (!StringUtil.isEmpty(action)) {
                        Intent intent = OpenActivityUtil.getIntent(context, action);
                        if (intent != null) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        }
                    }
//                    else if (getIntent().getIntExtra("startActivityForResult", 0) != 0) {
//                        setResult(RESULT_OK);
//                    }
                    //逻辑跳转
                    doMyfinish();
//                    Intent intent = new Intent();
//                    intent.setClass(context, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);

                } else {
                    if (redirectType == TYPE_REG)
                        showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "注册失败"));
                    else if (redirectType == TYPE_LOGIN)
                        showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "登录失败"));
                    else
                        showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "操作失败"));

                }

            } else {
                showCusToast("网络异常，请重试！");
            }

        }
    }

    // 重写返回键，让网页页面可以“后退”
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                doMyfinish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        hideNetLoadingProgressDialog();
        try {
            if (webView != null) {
                ViewGroup parent = (ViewGroup) webView.getParent();
                if (parent != null) {
                    parent.removeView(webView);
                }
                webView.removeAllViews();
                webView.clearCache(true);
                webView.clearHistory();
                webView.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "requestCode=" + requestCode + " resultCode=" + resultCode);
        //requestCode=100 resultCode=0
        //微信支付回来的 resultCode 都是0，微信没有作Result处理，
        // 做法在这里使用 接口自己检测，如果成功，返回到资金页面去 只有农交所微信才需要验证
        if (requestCode == CODE_REQUEST&&regData.getCashInType() == 1) {
            checkWXPAY();
        }
    }

    *//* *
          * 检测支付结果
         *//*
    void checkWXPAY() {
        new AsyncTask<Void, Void, CommonResponse<TempObject>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showNetLoadingProgressDialog(null);
            }

            @Override
            protected CommonResponse<TempObject> doInBackground(Void... params) {
                try {
                    UserInfoDao dao = new UserInfoDao(context);
                    if (!dao.isLogin())
                        return null;
                    String url = AndroidAPIConfig.URL_WX_PAY_CHECK_JN;
                    Map<String, String> map = ApiConfig.getCommonMap(context);
                    map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
                    map.put("orderId", regData.getOrderId());
                    map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, map));

                    String res = TradeHelp.post(context, url, map);

                    return CommonResponse.fromJson(res, RegData.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<TempObject> response) {
                super.onPostExecute(response);

                regData = null;
                context.hideNetLoadingProgressDialog();
                if (response != null) {
                    if (response.isSuccess()) {
                        doMyfinish();//关闭当前页面
                        EventBus.getDefault().post(new CashInEvent(true));
                    } else {
                        doMyfinish();//关闭当前页面
                        EventBus.getDefault().post(new CashInEvent(false, ConvertUtil.NVL(response.getErrorInfo(), "提交失败")));
                    }

                } else showCusToast("网络异常");
            }
        }.execute();
    }*/
    /**
     * 调起支付宝并跳转到指定页面
     *
     * @param url intent://xxx
     */
    private void startAlipayActivity(String url) {
        try {
            Intent intent = Intent.parseUri(url,
                    Intent.URI_INTENT_SCHEME);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setComponent(null);
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "请下载最新版支付宝支付", Toast.LENGTH_LONG).show();
        }
    }
}
