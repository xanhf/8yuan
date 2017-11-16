package com.trade.eight.moudle.trade.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.home.adapter.HomeRankProfitOrderAdapter;
import com.trade.eight.moudle.trade.adapter.TradeHistoryAdapter;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 交易历史
 * 作者：Created by ocean
 * 时间：on 2017/5/27.
 */

public class TradeHistoryAct extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ListView> {
    PullToRefreshListView pullToRefreshListView = null;
    ListView listView = null;
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    LinearLayout line_empty;
    TradeHistoryAdapter tradeHistoryAdapter;
    private int page = 1;
    private int pageSize = 10;

    public static void startAct(Context context) {
        Intent intent = new Intent(context, TradeHistoryAct.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tradehistory);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void initViews() {
        setAppCommonTitle(getResources().getString(R.string.trade_order_tradehistory));
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setPullLoadEnabled(true);
        pullToRefreshListView.setPullRefreshEnabled(true);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setVisibility(View.VISIBLE);
        listView.setDividerHeight(0);

        tradeHistoryAdapter = new TradeHistoryAdapter(this, 0, new ArrayList<TradeOrder>());
        listView.setAdapter(tradeHistoryAdapter);

        line_empty = (LinearLayout) findViewById(R.id.emptyView);
        TextView emptyTv = (TextView) findViewById(R.id.emptyTv);
        emptyTv.setText(R.string.empty_notradeorder);
        pullToRefreshListView.setLastUpdatedLabel(sdf.format(new Date()));
        pullToRefreshListView.doPullRefreshing(true, 0);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        page = 1;
        getTradeHistory();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        page += 1;
        getTradeHistory();
    }


    private void getTradeHistory() {
        HashMap<String, String> request = new HashMap<>();
        request.put("page", page + "");
        request.put("pageSize", pageSize + "");

        HttpClientHelper.doPostOption(TradeHistoryAct.this,
                AndroidAPIConfig.getAPI(TradeHistoryAct.this, AndroidAPIConfig.KEY_URL_TRADE_GET_HISTORY_LIST),
                request,
                null,
                new NetCallback(TradeHistoryAct.this) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        showCusToast(resultMsg);
                        pullToRefreshListView.onPullDownRefreshComplete();
                        pullToRefreshListView.onPullUpRefreshComplete();
                        pullToRefreshListView.setVisibility(View.GONE);
                        line_empty.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response) {
                        pullToRefreshListView.onPullDownRefreshComplete();
                        pullToRefreshListView.onPullUpRefreshComplete();
                        BaseInterface.setPullFormartRefreshTime(pullToRefreshListView);
                        pullToRefreshListView.setVisibility(View.VISIBLE);
                        line_empty.setVisibility(View.GONE);
                        CommonResponse4List<TradeOrder> commonResponse4List = CommonResponse4List.fromJson(response, TradeOrder.class);
                        List<TradeOrder> tradeOrderList = commonResponse4List.getData();
                        if (tradeOrderList != null && tradeOrderList.size() > 0) {

                            tradeHistoryAdapter.setItems(tradeOrderList, page == 1 ? true : false);

                        } else {
                            if (page == 1) {
                                pullToRefreshListView.setVisibility(View.GONE);
                                line_empty.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                },
                false);

    }

}

