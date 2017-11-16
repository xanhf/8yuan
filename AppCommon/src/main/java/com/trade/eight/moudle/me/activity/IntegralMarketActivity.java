package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.integral.AccountIntegralData;
import com.trade.eight.entity.integral.GoodsActData;
import com.trade.eight.entity.integral.GoodsActGiftData;
import com.trade.eight.entity.integral.GoodsData;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.sharejoin.ShJoinObj;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.me.CreateIntegralOrderEvent;
import com.trade.eight.moudle.me.CreateIntegralOrderSuccessEvent;
import com.trade.eight.moudle.me.ShJoinHelp;
import com.trade.eight.moudle.me.adapter.GoodsAdapter;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.timer.CountDownTask;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.trade.TradeCreateInteOrderUtil;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshExpandListView;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 16/12/12.
 * 积分商城
 */

public class IntegralMarketActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ExpandableListView>, View.OnClickListener {
    IntegralMarketActivity context = this;
    private View headView;
    private View bottomView;
    //邀请活动
    View head_layout_inte_join, ll_join;
    ImageView img_join;
    Button btn_integral_tips;

    TextView text_account_integral;
    TextView text_account_integralrate;
    TextView text_account_integralhistory;
    ImageView img_account_lv;
    TextView text_integral_rebaterate;
    Button btn_goto_intedetail;

    PullToRefreshExpandListView pullToRefreshListView;
    ExpandableListView listView = null;
    GoodsAdapter goodsAdapter;
    CountDownTask mCountDownTask = null;
    AccountIntegralData integralDataForMarket = null;
    int[] accoutLvDrawable = {R.drawable.img_integral_l1, R.drawable.img_integral_l2, R.drawable.img_integral_l3,
            R.drawable.img_integral_l4, R.drawable.img_integral_l5, R.drawable.img_integral_l6,
            R.drawable.img_integral_l7,};

    private int pageNo = 1;

    public static void startIntegralMarketActivity(Context context, AccountIntegralData integralDataForMarket) {
        Intent intent = new Intent();
        intent.setClass(context, IntegralMarketActivity.class);
        intent.putExtra("integralDataForMarket", integralDataForMarket);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_market);
        initData();
        initView();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    void initData() {
        integralDataForMarket = (AccountIntegralData) getIntent().getSerializableExtra("integralDataForMarket");
    }

    void initView() {
        setAppCommonTitle(getResources().getString(R.string.intergral_market));

        btn_integral_tips = (Button) findViewById(R.id.btn_integral_tips);
        btn_integral_tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.start(IntegralMarketActivity.this, "积分规则", AndroidAPIConfig.URL_INTEGRAL_INTRO);
            }
        });

        pullToRefreshListView = (PullToRefreshExpandListView) findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setPullLoadEnabled(true);
        pullToRefreshListView.setPullRefreshEnabled(true);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setVisibility(View.VISIBLE);
        listView.setDividerHeight(0);

        //去掉group点击事件
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                //group不允许点击
                return true;
            }
        });
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                int type = goodsAdapter.getChildType(groupPosition,childPosition);
                if(type==goodsAdapter.CHILDTYPE_ACT){

                    GoodsActDetailActivity.startAct(IntegralMarketActivity.this,(GoodsActData)goodsAdapter.getGroup(groupPosition),(GoodsActGiftData) goodsAdapter.getChild(groupPosition,childPosition),integralDataForMarket);
                }
                return false;
            }
        });
        //去掉下划线
        listView.setGroupIndicator(null);

        headView = View.inflate(IntegralMarketActivity.this, R.layout.head_integralmarket, null);
        head_layout_inte_join = View.inflate(IntegralMarketActivity.this, R.layout.head_layout_inte_join, null);
        bottomView = View.inflate(IntegralMarketActivity.this, R.layout.layout_integral_bottom, null);
        btn_goto_intedetail = (Button) headView.findViewById(R.id.btn_goto_intedetail);
        btn_goto_intedetail.setOnClickListener(this);
        text_account_integral = (TextView) headView.findViewById(R.id.text_account_integral);
        text_account_integralrate = (TextView) headView.findViewById(R.id.text_account_integralrate);
        text_account_integralhistory = (TextView) headView.findViewById(R.id.text_account_integralhistory);
        text_account_integralhistory.setOnClickListener(this);
        img_account_lv = (ImageView) headView.findViewById(R.id.img_account_lv);
        text_integral_rebaterate = (TextView) headView.findViewById(R.id.text_integral_rebaterate);

        ll_join = head_layout_inte_join.findViewById(R.id.ll_join);
        img_join = (ImageView) head_layout_inte_join.findViewById(R.id.img_join);
        ll_join.setVisibility(View.GONE);

        listView.addHeaderView(head_layout_inte_join);
        listView.addHeaderView(headView);
        listView.addFooterView(bottomView);
        goodsAdapter = new GoodsAdapter(this, new GoodsData());
        listView.setAdapter(goodsAdapter);
        startCountDown();

        if (integralDataForMarket != null) {
            displayIntegralInfo();
        } else {// 如果外面没有获取到积分  直接请求
            checkAccountIntegral();
        }

        pullToRefreshListView.setLastUpdatedLabel(sdf.format(new Date()));

//        new GetIntegralProductTask().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        pullToRefreshListView.doPullRefreshing(true,0);
    }

    private void startCountDown() {
        mCountDownTask = CountDownTask.create();
        goodsAdapter.setCountDownTask(mCountDownTask);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
        pageNo = 1;
        new GetIntegralProductTask().execute();
        checkAccountIntegral();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
        pageNo += 1;
        new GetIntegralProductTask().execute();
    }

    private void displayIntegralInfo() {
        text_account_integral.setText(StringUtil.forNumber(integralDataForMarket.getValidPoints()));
        text_account_integralrate.setText(getString(R.string.integral_info, integralDataForMarket.getPointsRanking()));
        img_account_lv.setImageDrawable(getResources().getDrawable(accoutLvDrawable[integralDataForMarket.getLevelNum() - 1]));
        double rebaterate = NumberUtil.multiply(10, Double.parseDouble(integralDataForMarket.getRebateRate()));
        if (rebaterate == 10) {
            text_integral_rebaterate.setText(R.string.intergral_lvnorebaterate);
        } else {
            String rebaterateStr = new DecimalFormat("0.0").format(rebaterate);
            String lastRebaterateStr = rebaterateStr.substring(2, 3);
            if (Integer.parseInt(lastRebaterateStr) > 0) {

            } else {
                rebaterateStr = rebaterateStr.substring(0, 1);
            }

            String display = getString(R.string.intergral_lvrebaterate, rebaterateStr);
            SpannableString ss = new SpannableString(display);
            ss.setSpan(new RelativeSizeSpan(1.5f), display.indexOf("城") + 1, display.indexOf("折"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.integral_poin_orange)), display.indexOf("城") + 1, display.indexOf("折"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            text_integral_rebaterate.setText(ss);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.text_account_integralhistory:
                IntegralExchangeHistoryActivity.startIntegralExchangeHistoryAct(this);
                break;
            case R.id.btn_goto_intedetail:
                IntegralDetailActivity.startIntegralDetailAct(this);
                break;
        }
    }

    //邀请好友分享
    CommonResponse<ShJoinObj> shJoinRes;

    class GetIntegralProductTask extends AsyncTask<String, Void, CommonResponse<GoodsData>> {

        @Override
        protected CommonResponse<GoodsData> doInBackground(String... params) {
            UserInfoDao userInfoDao = new UserInfoDao(IntegralMarketActivity.this);
            if (!userInfoDao.isLogin()) {
                doMyfinish();
                return null;
            }

            try {
                if (shJoinRes == null) {
                    shJoinRes = ShJoinHelp.getInstance(context).getActs(context, ShJoinObj.TYPE_PAGE_INTE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            CommonResponse<GoodsData> response4List = null;
            try {
                Map<String, String> map = new HashMap<String, String>();
                map.put(UserInfo.UID, userInfoDao.queryUserInfo().getUserId());
                map.put("page", pageNo + "");
                map.put("pageSize", "20");
                map = ApiConfig.getParamMap(IntegralMarketActivity.this, map);
                String res = HttpClientHelper.getStringFromPost(IntegralMarketActivity.this, AndroidAPIConfig.URL_INTEGRAL_PRODUCTLIST, map);
                response4List = CommonResponse.fromJson(res, GoodsData.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response4List;
        }

        @Override
        protected void onPostExecute(CommonResponse<GoodsData> response) {
            super.onPostExecute(response);
            if (isFinishing()) {
                return;
            }
            //先处理好友邀请
            if (shJoinRes != null) {
                checkJoin(shJoinRes);
            }

            pullToRefreshListView.onPullDownRefreshComplete();
            pullToRefreshListView.onPullUpRefreshComplete();
            if (pageNo == 1) {
                pullToRefreshListView.setLastUpdatedLabel(sdf.format(new Date()));
            }
            if (response != null) {
                if (response.isSuccess()) {

                   GoodsData goodsData = response.getData();
                    /*if (listData == null || listData.size() == 0) {
                        if (integralProductAdapter.getCount() != 0) {
                            showCusToast(getResources().getString(R.string.data_no_more));
                            return;
                        } else {
                            return;
                        }
                    }
                    if (pageNo == 1) {
                        goodsAdapter.clear();
                    }*/
                    goodsAdapter.setGoodsData(goodsData);
                    for (int i = 0; i < goodsAdapter.getGroupCount(); i++) {
                        listView.expandGroup(i);
                    }
                } else {
                    showCusToast(response.getErrorInfo());
                }

            } else {
                showCusToast(getResources().getString(R.string.network_problem));
            }
        }
    }

    /**
     * 用户积分获取
     */
    void checkAccountIntegral() {
        if (!new UserInfoDao(context).isLogin())
            return;
        new AsyncTask<Void, Void, CommonResponse<AccountIntegralData>>() {
            @Override
            protected CommonResponse<AccountIntegralData> doInBackground(Void... params) {
                CommonResponse<AccountIntegralData> response = null;
                try {
                    UserInfoDao userInfoDao = new UserInfoDao(IntegralMarketActivity.this);
                    if (!userInfoDao.isLogin()) {
                        return null;
                    }
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(UserInfo.UID, userInfoDao.queryUserInfo().getUserId());
                    long lvVersion = PreferenceSetting.getInt(IntegralMarketActivity.this, PreferenceSetting.ACCOUNT_INTEGRAL_VERSION);
                    if (lvVersion < 0) {
                        lvVersion = 0;
                    }
                    map.put("versionNo", lvVersion + "");
                    map = ApiConfig.getParamMap(IntegralMarketActivity.this, map);
                    String res = HttpClientHelper.getStringFromPost(IntegralMarketActivity.this, AndroidAPIConfig.URL_ACCOUNT_INTEGRALINFO, map);
                    response = CommonResponse.fromJson(res, AccountIntegralData.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(CommonResponse<AccountIntegralData> accountIntegralDataCommonResponse) {
                super.onPostExecute(accountIntegralDataCommonResponse);
                if (isFinishing())
                    return;

                if (accountIntegralDataCommonResponse != null) {
                    if (accountIntegralDataCommonResponse.isSuccess()) {
                        integralDataForMarket = accountIntegralDataCommonResponse.getData();
                        long lvVersion = PreferenceSetting.getInt(IntegralMarketActivity.this, PreferenceSetting.ACCOUNT_INTEGRAL_VERSION);
                        if (lvVersion == integralDataForMarket.getVersionNo()) {
                        } else {
                            PreferenceSetting.setInt(IntegralMarketActivity.this, PreferenceSetting.ACCOUNT_INTEGRAL_VERSION, integralDataForMarket.getVersionNo());
                            String lv = new Gson().toJson(integralDataForMarket.getLevelList());
                            PreferenceSetting.setString(IntegralMarketActivity.this, PreferenceSetting.ACCOUNT_INTEGRAL_INFO, lv);
                        }
                        displayIntegralInfo();
                    }
                }
            }
        }.execute();
    }

    /**
     * 检查邀请好友积分的功能
     * 1、是否隐藏
     * 2、点击进webview
     */
    public void checkJoin(CommonResponse<ShJoinObj> response) {
        if (response == null)
            return;
        if (!response.isSuccess())
            return;
        if (ll_join == null)
            return;
        final ShJoinObj obj = response.getData();
        //如果活动关闭的 不显示
        if (obj.getStatus() == ShJoinObj.STATUS_OFF)
            return;
        ll_join.setVisibility(View.VISIBLE);
        ImageLoader.getInstance().displayImage(obj.getPic(),
                img_join,
                AppImageLoaderConfig.getCommonDisplayImageOptions(context, R.drawable.img_loading_large));

        ll_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.start(context, obj.getLinkTitle(), obj.getLink());
            }
        });
    }

    /**
     * 开始兑换
     *
     * @param event
     */
    public void onEventMainThread(CreateIntegralOrderEvent event) {
        TradeCreateInteOrderUtil tradeCreateInteOrderUtil = new TradeCreateInteOrderUtil(this, event.getIntegralProductData(), integralDataForMarket);
    }

    /**
     * 兑换成功
     *
     * @param event
     */
    public void onEventMainThread(CreateIntegralOrderSuccessEvent event) {
        integralDataForMarket = event.getAccountIntegralData();
        displayIntegralInfo();
    }

}
