package com.trade.eight.moudle.trade.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.OrderDeferred;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.trade.adapter.MyDeferredAdapter;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.trade.TradeConfig;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by fangzhu
 * 持仓过夜费列表
 */
public class TradeDeferredAct extends BaseActivity implements
        PullToRefreshBase.OnRefreshListener<ListView>, View.OnClickListener{
    public static final String TAG = "TradeVoucherAct";
    public static final String PAGE_TAG = "page_voucher";
    TradeDeferredAct context = this;
    private PullToRefreshListView mPullRefreshListView;
    private ListView listView;
    MyDeferredAdapter myVoucherAdapter = null;
    View emptyView;
    String orderId;

    public static void start (Context c, String orderId) {
        Intent intent = new Intent(c, TradeDeferredAct.class);
        intent.putExtra("orderId", orderId);
        c.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //没有登录状态
        if (!new UserInfoDao(context).isLogin()) {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        orderId = getIntent().getStringExtra("orderId");
        setContentView(R.layout.act_trade_deferred);
        setAppCommonTitle("持仓过夜手续费");
        initViews();
    }


    void initViews () {
        emptyView = findViewById(R.id.emptyView);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setPullRefreshEnabled(true);
        mPullRefreshListView.setPullLoadEnabled(true);
        listView = mPullRefreshListView.getRefreshableView();
        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(false);
        listView.setDividerHeight(0);

        myVoucherAdapter = new MyDeferredAdapter(context, 0, new ArrayList<OrderDeferred>());
        listView.setAdapter(myVoucherAdapter);
        BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
        mPullRefreshListView.doPullRefreshing(true, 0);

        findViewById(R.id.desView).setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.desView) {
            MyAppMobclickAgent.onEvent(context, PAGE_TAG, "btn_tickDes");
            String string = context.getResources().getString(R.string.str_lable_jn_rule,TradeConfig.isCurrentJN(context) ? TradeConfig.str_trade_jn : TradeConfig.str_trade_hn);
            WebActivity.start(context, string, AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_RULE));

        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        new GetTradeVoucherListTask().execute();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        new GetTradeVoucherListTask().execute();
    }

    //get 持仓过夜费 list
    class GetTradeVoucherListTask extends AsyncTask<String, Void, CommonResponse4List<OrderDeferred>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myVoucherAdapter.clear();
        }

        @Override
        protected CommonResponse4List<OrderDeferred> doInBackground(String... params) {
            String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_DEFERRED);;
            return TradeHelp.getDeferredList(context, url, orderId);
        }

        @Override
        protected void onPostExecute(CommonResponse4List<OrderDeferred> result) {
            // TODO Auto-generated method stub
            if (isFinishing())
                return;
            super.onPostExecute(result);
            mPullRefreshListView.onPullDownRefreshComplete();
            mPullRefreshListView.onPullUpRefreshComplete();

            emptyView.setVisibility(View.GONE);
            if (result != null) {
                //refresh adapter
                if (result.isSuccess()) {
                    if (result.getData() != null && result.getData().size() > 0) {
                        myVoucherAdapter.setItems(result.getData(), true);
                    }
                    if ((result.getData() == null || result.getData().size() == 0)) {
                        //没有数据
                        emptyView.setVisibility(View.VISIBLE);
                    } else  {

                    }

                } else {
                    //token过期 避免重复弹框
                    if (ApiConfig.isNeedLogin(result.getErrorCode())) {
                        if (!StringUtil.isEmpty(result.getErrorInfo())) {
                            showCusToast(result.getErrorInfo());
                        }
                        //重新登录
                        DialogUtil.showTokenDialog(context, TradeConfig.getCurrentTradeCode(context), null, new DialogUtil.AuthTokenCallBack() {
                            @Override
                            public void onPostClick(Object obj) {

                            }

                            @Override
                            public void onNegClick() {
                                finish();
                            }
                        });
                        return;
                    }
//                    showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "网络连接失败！"));
                }

                BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
            } else {
                showCusToast("网络连接失败！");
            }

        }
    }
}
