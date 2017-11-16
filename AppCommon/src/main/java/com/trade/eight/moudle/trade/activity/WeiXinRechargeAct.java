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

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.RegData;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.trade.CashInEvent;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.Log;

import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * regData  作为传入的判断
 * 微信充值
 */
public class WeiXinRechargeAct extends BaseActivity {
    WebView webView;
    WeiXinRechargeAct context = null;
    String code = null, title;
    public static final String TAG = "WeiXinRechargeAct";
    // 返回h5的data结果
    RegData regData;
    //充值
    public static final int TYPE_CASH = 9527;
    private static int CODE_REQUEST = 100;


    /**
     * 提现网页url callback处理
     *
     * @param mContext
     * @param data
     */
    public static void startCashin(Context mContext, RegData data) {
        Intent intent = new Intent();
        intent.setClass(mContext, WeiXinRechargeAct.class);
        intent.putExtra("object", data);
        intent.putExtra("title", "充值");
        mContext.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_weixin_pay);

        code = getIntent().getStringExtra("code");
        title = ConvertUtil.NVL(getIntent().getStringExtra("title"), "");
        setAppCommonTitle(title);

        initViews();
        initListener();

    }

    void initViews() {
        initWebView();

        regData = (RegData) getIntent().getSerializableExtra("object");

        //处理 重定向
        webView.loadUrl(regData.getUrl());


    }


    void initWebView() {
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        //不使用缓存
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(false);//不缓存

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
                if (url.startsWith("weixin://wap/pay?")) {
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
                } else if (regData != null
                        && url.startsWith(regData.getRedirectUrl())) {
                    //哈贵需要拦截 redirectUrl 才知道是成功
                    if (regData.getCashInType() == RegData.CASHIN_WEIXIN_HG) {
                        //充值成功  不需要在http访问
                        //返回到个人页面
                        showCusToast("充值成功");
                        finish();
                        EventBus.getDefault().post(new CashInEvent(true));
                        return true;
                    }

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
                    if (url.startsWith(regData.getRedirectUrl())) {
                        Log.e(TAG, "onPageStarted" + " url=" + url);
                        finish();
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
        super.onDestroy();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "requestCode=" + requestCode + " resultCode=" + resultCode);
        //requestCode=100 resultCode=0
        //微信支付回来的 resultCode 都是0，微信没有作Result处理，
        // 做法在这里使用 接口自己检测，如果成功，返回到资金页面去 只有农交所微信才需要验证
        if (requestCode == CODE_REQUEST && regData.getCashInType() == 1) {
            checkWXPAY();
        }
    }

    /* *
          * 检测支付结果
         */
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
    }

}
