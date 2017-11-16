package com.trade.eight.moudle.outterapp;

import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.entity.AdsObj;
import com.trade.eight.entity.home.HomeCalendar;
import com.trade.eight.entity.home.HomeNews;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.home.adapter.HomeCalendarAdapter;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.NetWorkUtils;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.view.CircleImageView;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;


/**
 * Created by fangzhu
 * 3.4.6以前 首页的重要消息详情页
 */
public class StrategyDetailActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ListView> {
    String TAG = "StrategyDetailActivity";

    PullToRefreshListView pullToRefreshListView = null;
    ListView listView = null;
    View headerView = null, loadingView = null;
    StrategyDetailActivity context = this;
    //文字广告
    AdsObj adsObj;
    TextView ad_tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_strate_detail);
        initViews();
        getData();

    }

    void initViews() {
        setAppCommonTitle(ConvertUtil.NVL(getIntent().getStringExtra("title"), "详情"));
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setPullLoadEnabled(false);
        pullToRefreshListView.setPullRefreshEnabled(false);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setVisibility(View.VISIBLE);
        listView.setDividerHeight(0);


        loadingView = findViewById(R.id.layoutLoding);
        headerView = View.inflate(context, R.layout.home_news_detail_header, null);
        ad_tv = (TextView) headerView.findViewById(R.id.ad_tv);

        headerView.setVisibility(View.GONE);
        listView.addHeaderView(headerView);
        listView.setAdapter(new HomeCalendarAdapter(context, 0, new ArrayList<HomeCalendar>()));//listview use an adapter just to show headerVeiw

        findViewById(R.id.btn_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAppMobclickAgent.onEvent(context, "page_home_news_detail", "openVideo");
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.LIVING);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    void initHeaderView(HomeNews homeNews) {
        if (homeNews == null)
            return;
        CircleImageView user_icon = (CircleImageView) headerView.findViewById(R.id.user_icon);
        TextView title_tv = (TextView) headerView.findViewById(R.id.title_tv);
        TextView time_tv = (TextView) headerView.findViewById(R.id.time_tv);
        WebView webView = (WebView) headerView.findViewById(R.id.webView);
        if (user_icon != null) {
            ImageLoader.getInstance().displayImage(homeNews.getAvatarUrl(), user_icon, AppImageLoaderConfig.getCommonDisplayImageOptions(context, R.drawable.liveroom_icon_person));
        }
        if (title_tv != null) {
            String str = ConvertUtil.NVL(homeNews.getNickName(), "");
            try {
                int s = str.lastIndexOf("（");
                if (s <= 0)
                    s = str.lastIndexOf("(");
                if (s > 0) {
                    str = str.substring(0, s);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            title_tv.setText(Html.fromHtml(str));
//            title_tv.setText(Html.fromHtml(ConvertUtil.NVL(homeNews.getNickName(), "")));
        }
        if (time_tv != null) {
            time_tv.setText(ConvertUtil.NVL(homeNews.getCreateTime(), ""));
//            time_tv.setText(Html.fromHtml(ConvertUtil.NVL(homeNews.getCreateTime(), "")));
        }
        if (webView != null) {
            String text = ConvertUtil.NVL(homeNews.getText(), "");
            webView.loadDataWithBaseURL(null, text, "text/html", "utf-8",
                    null);
//            content_tv.setText(Html.fromHtml(ConvertUtil.NVL(homeNews.getText(), "")));
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    void getData() {
        new AsyncTask<String, Void, CommonResponse<HomeNews>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (loadingView != null)
                    loadingView.setVisibility(View.VISIBLE);
            }

            @Override
            protected CommonResponse<HomeNews> doInBackground(String... params) {
                //先获取广告
                try {
                    String api = AndroidAPIConfig.URL_IMPORT_NEWS_DETAIL_ADS;
                    api = NetWorkUtils.setParam4get(api, ApiConfig.getParamMap(context, null));
                    String resAds = HttpClientHelper.getStringFromGet(context, api);
                    CommonResponse4List<AdsObj> response = CommonResponse4List.fromJson(resAds, AdsObj.class);
                    if (response != null && response.isSuccess())
                        adsObj = response.getData().get(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //获取详情信息
                try {
                    String id = getIntent().getStringExtra("id");
                    String url = getIntent().getStringExtra("url");
                    if (url == null || url.trim().length() == 0)
                        url = AndroidAPIConfig.URL_IMPORT_NEWS_DETAIL.replace("{id}", id);
                    String res = HttpClientHelper.getStringFromGet(context, url);
                    CommonResponse<HomeNews> commonResponse = CommonResponse.fromJson(res, HomeNews.class);
                    if (commonResponse != null)
                        return commonResponse;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<HomeNews> homeNewsCommonResponse) {
                super.onPostExecute(homeNewsCommonResponse);
                if (loadingView != null)
                    loadingView.setVisibility(View.GONE);
                initAdView();
                if (homeNewsCommonResponse != null && homeNewsCommonResponse.isSuccess()) {
                    if (homeNewsCommonResponse.getData() != null) {
                        headerView.setVisibility(View.VISIBLE);
                        initHeaderView(homeNewsCommonResponse.getData());
                    } else
                        showCusToast(getResources().getString(R.string.data_empty));
                }
            }
        }.execute("");
    }

    void initAdView() {
        if (adsObj != null) {
            ad_tv.setVisibility(View.VISIBLE);
            ad_tv.setText(ConvertUtil.NVL(adsObj.getText(), ""));
            ad_tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
            ad_tv.getPaint().setAntiAlias(true);//抗锯齿
            ad_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String link = adsObj.getLink();
                    if (StringUtil.isEmpty(link))
                        return;
                    WebActivity.start(context, adsObj.getText(), link);
                }
            });
        } else {
            ad_tv.setVisibility(View.GONE);
        }
    }
}
