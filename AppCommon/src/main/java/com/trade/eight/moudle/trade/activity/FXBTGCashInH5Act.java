package com.trade.eight.moudle.trade.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.trade.eight.moudle.auth.AuthUploadIdCardAct;
import com.trade.eight.moudle.auth.data.AuthDataHelp;
import com.trade.eight.moudle.auth.entity.CardAuthObj;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.trade.CashInEvent;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.trade.TradeConfig;

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
public class FXBTGCashInH5Act extends BaseActivity {
    public static final String TAG = "RedirectH5Act";

    WebView webView;
    FXBTGCashInH5Act context = null;
    String phone = null, code = null, title;
    // 返回h5的data结果
    RegData regData;
    private static Dialog tokenDlg = null;

    /**
     * 网银充值网页url callback处理
     *
     * @param mContext
     */
    public static void startCashin(Context mContext) {
        Intent intent = new Intent();
        intent.setClass(mContext, FXBTGCashInH5Act.class);
        intent.putExtra("title", mContext.getResources().getString(R.string.trade_order_cashin));
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

        title = ConvertUtil.NVL(getIntent().getStringExtra("title"), getResources().getString(R.string.trade_order_cashin));
        setAppCommonTitle(title);

        initViews();
        initListener();

    }

    void initViews() {
        initWebView();
    }

    @Override
    public void onResume() {
        super.onResume();
//        doRecharge();
        if (regData == null) {
            checkAuthStatus();
        }
    }

    void initWebView() {
        webView = (WebView) findViewById(R.id.webview);
        //自适配高度和宽度
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
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
                } else if (regData != null && url.startsWith(regData.getCallBackUrl())) {
                    //充值成功  不需要在http访问
                    hideNetLoadingProgressDialog();
                    //返回到个人页面
//                    showCusToast("充值成功");
                    finish();
                    EventBus.getDefault().post(new CashInEvent(true));
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
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showCusToast("您安装微信最新版支付");
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
                try {
                    if (regData == null)
                        return;
                    if (regData.getRedirectUrl() == null)
                        return;
                    if (url == null)
                        return;
                    if (url.startsWith(regData.getCallBackUrl())) {
                        Log.e(TAG, "onPageStarted" + " url=" + url);
                        //充值成功  不需要在http访问
                        hideNetLoadingProgressDialog();
                        //返回到个人页面
//                        showCusToast("充值成功");
                        finish();
                        EventBus.getDefault().post(new CashInEvent(true));

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

    /**
     * 检查实名认证状态
     */
    private void checkAuthStatus() {

        UserInfoDao userInfoDao = new UserInfoDao(FXBTGCashInH5Act.this);
        UserInfo userInfo = userInfoDao.queryUserInfo();
        if (userInfo.getAuthStatus() == CardAuthObj.STATUS_SUCCESS) {
            doRecharge();
        } else {
            AuthDataHelp.checkStatus(FXBTGCashInH5Act.this, new NetCallback(FXBTGCashInH5Act.this) {
                @Override
                public void onFailure(String resultCode, String resultMsg) {

                    //token过期 避免重复弹框
                    if (ApiConfig.isNeedLogin(resultCode)) {
                        //刷新过程中token过期
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        //重新登录
                                        showDialog();
                                    }
                                }
                        );
                    } else {
                        showCusToast(ConvertUtil.NVL(resultMsg, getResources().getString(R.string.network_problem)));
                    }
                }

                @Override
                public void onResponse(String response) {
                    CommonResponse<CardAuthObj> commonResponse = CommonResponse.fromJson(response,CardAuthObj.class);
                    if (commonResponse != null
                            && commonResponse.isSuccess()
                            && commonResponse.getData() != null) {
                        int authStatus = commonResponse.getData().getStatus();
                        if (authStatus == CardAuthObj.STATUS_SUCCESS) {
                            doRecharge();
                        } else if (authStatus == CardAuthObj.STATUS_CHECKING) {
                            showCusToast(getResources().getString(R.string.card_status_checking_1));
                            doMyfinish();
                        } else {
                            showCusToast(getResources().getString(R.string.card_status_tips));
                            AuthUploadIdCardAct.start(FXBTGCashInH5Act.this);
                            doMyfinish();
                        }
                    }
                }
            });
        }
    }

    /**
     * 进行充值操作
     */
    void doRecharge() {
        UserInfoDao dao = new UserInfoDao(this);
        if (!dao.isLogin()) {
            DialogUtil.showConfirmDlg(this, null, null, null, true, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    doMyfinish();
                    return false;
                }
            }, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    startActivity(new Intent(FXBTGCashInH5Act.this, LoginActivity.class));
                    return false;
                }
            });
            return;
        }
        HttpClientHelper.doPostOption(FXBTGCashInH5Act.this,
                AndroidAPIConfig.URL_CASHIN,
                null,
                null,
                new NetCallback(FXBTGCashInH5Act.this) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        //token过期 避免重复弹框
                        if (ApiConfig.isNeedLogin(resultCode)) {
                            //刷新过程中token过期
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            //重新登录
                                            showDialog();
                                        }
                                    }
                            );
                        } else {
                            showCusToast(resultMsg);
                            doMyfinish();
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        CommonResponse<RegData> commonResponse = CommonResponse.fromJson(response, RegData.class);
                        regData = commonResponse.getData();
                        if (regData != null) {
                            webView.loadUrl(regData.getRechargeUrl());
                        }
                    }
                },
                true);
    }

    /**
     * dialog的处理
     */
    void showDialog() {

        if (tokenDlg != null) {
            if (tokenDlg.isShowing())
                return;
        }


        DialogUtil.showTokenDialog(this, TradeConfig.getCurrentTradeCode(this), new DialogUtil.AuthTokenDlgShow() {
            @Override
            public void onDlgShow(Dialog dlg) {
                if (tokenDlg != null) {
                    if (tokenDlg.isShowing())
                        return;
                }
                tokenDlg = dlg;
            }
        }, new DialogUtil.AuthTokenCallBack() {
            @Override
            public void onPostClick(Object obj) {

            }

            @Override
            public void onNegClick() {
                Intent intent = new Intent(FXBTGCashInH5Act.this, MainActivity.class);
                intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//跳转到main  必须要有
                startActivity(intent);
            }
        });
    }


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
