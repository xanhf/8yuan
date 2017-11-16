package com.trade.eight.moudle.home.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.entity.AdsObj;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.NetWorkUtils;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.StringUtil;

/**
 * Created by fangzhu
 * 首页的重要消息详情页
 * 1、文字广告
 * 2、处理返回效果
 */
public class HomeNewsDetailActivity extends WebActivity {
    String TAG = "HomeNewsDetailActivity";

    HomeNewsDetailActivity context = this;
    //文字广告
    AdsObj adsObj;
//    TextView ad_tv;

    public static void start (Context context, String title, String url) {
        Intent intent = new Intent(context, HomeNewsDetailActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initViews();
        getData();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.act_home_news_detail);
    }

//    void initViews() {
//        ad_tv = (TextView) findViewById(R.id.ad_tv);
//    }

    /**
     * 详情页的头部返回，返回到上一级
     */
    @Override
    protected void appCommonGoBack() {
        if (mWebView != null) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                doMyfinish();
            }
        } else {
            doMyfinish();
        }
    }

    /**
     * 加载在详情页的广告
     */
    void getData() {
        new AsyncTask<String, Void, CommonResponse4List<AdsObj>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected CommonResponse4List<AdsObj> doInBackground(String... params) {
                //先获取广告
                try {
                    String api = AndroidAPIConfig.URL_IMPORT_NEWS_DETAIL_ADS;
                    api = NetWorkUtils.setParam4get(api, ApiConfig.getParamMap(context, null));
                    String resAds = HttpClientHelper.getStringFromGet(context, api);
                    CommonResponse4List<AdsObj> response = CommonResponse4List.fromJson(resAds, AdsObj.class);

                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;

            }

            @Override
            protected void onPostExecute(CommonResponse4List<AdsObj> response) {
                super.onPostExecute(response);
                if (response != null && response.isSuccess()) {
                    if (response.getData() != null && response.getData().size() > 0) {
                        adsObj = response.getData().get(0);
                        initAdView();
                    }
                }

            }
        }.execute("");
    }

    /**
     * 初始化广告链接
     */
    void initAdView() {
        if (adsObj != null) {
//            ad_tv.setVisibility(View.VISIBLE);
//            ad_tv.setText(ConvertUtil.NVL(adsObj.getText(), ""));
//            ad_tv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    String link = adsObj.getLink();
//                    if (StringUtil.isEmpty(link))
//                        return;
//                    WebActivity.start(context, adsObj.getText(), link);
//                }
//            });
            title_view.setRightBtnText(ConvertUtil.NVL(adsObj.getText(), ""));
            title_view.setRightBtnCallback(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    String link = adsObj.getLink();
                    if (StringUtil.isEmpty(link))
                        return false;
                    WebActivity.start(context, adsObj.getText(), link);
                    return false;
                }
            });
        }
//        else {
//            ad_tv.setVisibility(View.GONE);
//        }
    }
}
