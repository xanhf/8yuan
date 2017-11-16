package com.trade.eight.moudle.outterapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.google.gson.Gson;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.ShareEntity;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.trade.alipay.AliPayObj;
import com.trade.eight.moudle.trade.alipay.AliPayUtil;
import com.trade.eight.net.NetWorkUtils;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.OpenActivityUtil;
import com.trade.eight.tools.ShareTools;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.view.AppTitleView;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 显示网页的act
 * 1、首页详情页也有用到，修改的时候注意
 */
public class WebActivity extends BaseActivity {

    public AppTitleView title_view;
    protected WebView mWebView;
    private String html_url = "";
    //点击返回后不再重复显示dlg
    boolean isToShowDlg = true;
    //是否直接返回关闭
    boolean isGoBack = false;
    //    private TextView titleView;
    WebActivity context = this;
    public static final String TAG = "WebActivity";
    ShareTools shareTools = null;//html_url = "file:///android_asset/test.html";

    public static void start(Context context, String title, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    public static void start(Context context, String title, String url, boolean isGoBack) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        intent.putExtra("isGoBack", isGoBack);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        isGoBack = getIntent().getBooleanExtra("isGoBack", false);
        super.onCreate(savedInstanceState);
        // new GetTasteInfoTask().execute("");
        setContentView(R.layout.activity_web);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        shareTools = new ShareTools(context);
//        titleView = (TextView) findViewById(R.id.title1);

        title_view = (AppTitleView) findViewById(R.id.title_view);
        title_view.setBaseActivity(this);

        initIntent(getIntent());

        Log.v(TAG, "html_url=" + html_url);


        mWebView = (WebView) findViewById(R.id.webview1);
        //自适配高度和宽度
        WebSettings settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //设置http和https资源可以混合使用，解决问题，https的地址中包含了http的视频流，导致5.0以上的手机不能播放
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //不使用缓存
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setAppCacheEnabled(false);//不缓存
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        mWebView.addJavascriptInterface(new ShareJS(), "share");
        mWebView.addJavascriptInterface(new RicePay(), "payObj");

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                // TODO Auto-generated method stub
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress < 100 && !isProgressDialogShowing() && isToShowDlg) {
                    showNetLoadingProgressDialog("加载中...");//or do it onCreate
                    if (loadingDlg != null) {
                        loadingDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                isToShowDlg = false;
                            }
                        });
                    }
                }
                if (newProgress == 100) {
                    Log.v(TAG, "newProgress == 100");
                    hideNetLoadingProgressDialog();
                }

            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
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
                if (StringUtil.isEmpty(url))
                    return true;

                if (url.trim().startsWith("tel:")) {
                    String phoneStr = url.replace("tel:", "");
                    try {
                        //直接打电话
//                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneStr)));

                        //拨号盘界面
                        String tel = StringUtil.replace(phoneStr, "-", "");
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if (url.startsWith("mqqwpa://")) {
                    //打开qq
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if (url.startsWith(OpenActivityUtil.SCHEME)
                        || url.startsWith(OpenActivityUtil.SCHEME_SUB)
                        || url.startsWith(OpenActivityUtil.SCHEME_V2)) {
                    try {
                        String action = OpenActivityUtil.getShortAction(context, url);
                        if (OpenActivityUtil.ACT_QIHUO_ACCOUNT.equals(action)) {
                            OpenActivityUtil.openQiHuoAccount(context);
                        } else {
                            Intent intent = OpenActivityUtil.getIntent(context, url);
                            if (intent == null)
                                return true;
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//跳转到main  必须要有
                            startActivity(intent);

                            //加入统计
                            String act = OpenActivityUtil.getShortAction(context, url);
                            if (OpenActivityUtil.ACT_TRADE.equals(act)) {
                                MyAppMobclickAgent.onEvent(context, "page_webView", act);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if (url.startsWith("weixin://wap/pay?")) {
                    //微信支付
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showCusToast("您还没有安装微信");
                    }

                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
                //onPageStarted will call many times progressDialog can not dismiss
                //do it onProgressChanged, byfangzhu
//				showNetLoadingProgressDialog("加载中...");
                Log.e(TAG, "onPageStarted");
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

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


    }

    /**
     * 初始化数据
     */
    public void initIntent(Intent intent) {
        html_url = intent.getStringExtra("url");
        //没有链接的时候，直接返回
        if (html_url == null) {
            finish();
        }
        if (html_url != null && !html_url.startsWith("http")) {
            //默认认为是http格式的 https忽略
            html_url = "http://" + html_url;
        }
        //添加参数 20161201
        Map<String, String> map = new LinkedHashMap<>();
        //标记是android设备
        if (html_url != null && !html_url.contains(ApiConfig.PARAM_DEVICE_TYPE + "=")) {
            map.put(ApiConfig.PARAM_DEVICE_TYPE, ApiConfig.PARAM_DEVICE_TYPE_ANDROID);
        }
        if (new UserInfoDao(context).isLogin()) {
            //传入userId，接口中没有这个参数才加上的
            if (html_url != null && !html_url.contains(UserInfo.UID + "=")) {
                map.put(UserInfo.UID, new UserInfoDao(context).queryUserInfo().getUserId());
            }
        }
        //加上全局的参数
        map = ApiConfig.getParamMap(context, map);
        html_url = NetWorkUtils.setParam4get(html_url, map);

        String title = intent.getStringExtra("title");
        if (title != null && !"".equals(title)) {
            title_view.setAppCommTitle(title);
        }
    }


    class ShareJS {
        @JavascriptInterface
        public void openShare(String title, String shareUrl) {
            if (StringUtil.isEmpty(shareUrl))
                return;
            //打开分享dlg
            ShareEntity entity = new ShareEntity();
            entity.setContent(ConvertUtil.NVL(title, ""));
            entity.setUrl(shareUrl);
            //弹出选择分享平台
            shareTools.showShareDialog(context, entity);
            //直接分享到朋友圈
//			shareTools.shareToWeiXinCircle(R.drawable.app_icon, entity, shareTools.getUmShareListener());
        }

        /**
         * 网页里的button，app响应分享
         * 目前邀请好用
         * {"content":"一个人赚钱好孤单，送你红包，快来和我一起嗨！",
         * "pic":"http://t.m.8caopan.com/static/images/invter_logo.png",
         * "title":"一个人赚钱好孤单，送你红包，快来和我一起嗨！",
         * "url":"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxad256bd251e77b0e&redirect_uri=http://t.webchat.8caopan.com/lqhb/?inviterId=8&sourceId=10&response_type=code&scope=snsapi_userinfo&state=1#wechat_redirect"}
         *
         * @param json json字段
         */
        @JavascriptInterface
        public void shareByJson(final String json) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.v(TAG, "json=" + json);
                    ShareEntity entity = new Gson().fromJson(json, ShareEntity.class);
                    if (TextUtils.isEmpty(entity.getCallback())) {
                        //弹出选择分享平台
                        shareTools.showShareDialog(context, entity);
                    } else {
                        //弹出选择分享平台
                        shareTools.showShareDialog(context, entity, true);
                    }
                }
            });
        }

    }

    class RicePay {
        @JavascriptInterface
        public void ricePay(String json) {
            Log.v(TAG, "JavascriptInterface=" + json);

            AliPayObj data = new Gson().fromJson(json, AliPayObj.class);
            AliPayUtil.doAliPay(WebActivity.this, data);
        }

    }

    public void back(View view) {
//		finish();
        doMyfinish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.v(TAG, "onNewIntent");
        super.onNewIntent(intent);
        initIntent(intent);
    }

    @Override
    public void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();

        if (getParent() != null) {
            getParent().setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try {
            if (html_url != null && !"".equals(html_url)) {
                if (mWebView.getUrl() == null
                        || !mWebView.getUrl().equalsIgnoreCase(html_url)) {
                    mWebView.loadUrl(html_url);
                } else {
//					mWebView.reload();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (shareTools != null)
            shareTools.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void doMyfinish() {
        if (mWebView != null && isGoBack) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                super.doMyfinish();
            }
            return;
        }
        super.doMyfinish();
    }

    /**
     * 详情页的头部返回，返回到上一级
     */
    @Override
    protected void appCommonGoBack() {
        if (mWebView != null && isGoBack) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                doMyfinish();
            }
        } else {
            doMyfinish();
        }
    }

    // 重写返回键，让网页页面可以“后退”
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
//				finish();
                doMyfinish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
//			mWebView.clearHistory();
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
            mWebView.removeAllViews();
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.destroy();
        }
        super.onDestroy();
    }

	/*public void doMyfinish() {
        if (OpenActivityUtil.isGoHome(this, getIntent())) {
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			if (AppSetting.getInstance(this).getIsTureNavi()) {
				intent.setClass(this, MainActivity.class);
			} else {
				intent.setClass(this, LoadingActivity.class);
			}
			startActivity(intent);
		}
		finish();
	}*/


}
