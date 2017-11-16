package com.trade.eight.moudle.home.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.home.adapter.HomeRankProfitOrderAdapter;
import com.trade.eight.moudle.trade.activity.TradeCloseDetailAct;
import com.trade.eight.service.trude.HomeDataHelper;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 作者：Created by ocean
 * 时间：on 16/10/21.
 * 盈利单列表
 */

public class HomeRankProfitOrderActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ListView> {
    PullToRefreshListView pullToRefreshListView = null;
    ListView listView = null;
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    protected SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd");
    int rankPage = 1;

    LinearLayout line_empty;
    HomeRankProfitOrderAdapter homeRankProfitOrderAdapter;


    public static void startHomeRankProfitOrderActivity(Context context) {
        Intent intent = new Intent(context, HomeRankProfitOrderActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homerank_profitorder);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    void initViews() {
        setAppCommonTitle(ConvertUtil.NVL(getIntent().getStringExtra("title"), "今日盈利单"));
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setPullLoadEnabled(false);
        pullToRefreshListView.setPullRefreshEnabled(true);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setVisibility(View.VISIBLE);
        listView.setDividerHeight(0);

        homeRankProfitOrderAdapter = new HomeRankProfitOrderAdapter(this, 0, new ArrayList<TradeOrder>());
        listView.setAdapter(homeRankProfitOrderAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //交易记录
                Intent intent = new Intent(HomeRankProfitOrderActivity.this, TradeCloseDetailAct.class);
                intent.putExtra("object", (TradeOrder) homeRankProfitOrderAdapter.getItem(position));
                startActivity(intent);
            }
        });

        line_empty = (LinearLayout) findViewById(R.id.emptyView);
        TextView emptyTv = (TextView) findViewById(R.id.emptyTv);
        emptyTv.setText(R.string.empty_noprofitorder);
        pullToRefreshListView.setLastUpdatedLabel(sdf.format(new Date()));
        pullToRefreshListView.doPullRefreshing(true, 0);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        new GetRankTask().execute();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    class GetRankTask extends AsyncTask<String, Void, CommonResponse4List<TradeOrder>> {


        public GetRankTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected CommonResponse4List<TradeOrder> doInBackground(String... params) {


            return HomeDataHelper.getMyProfitOrderList(HomeRankProfitOrderActivity.this);
        }

        @Override
        protected void onPostExecute(CommonResponse4List<TradeOrder> response4List) {
            super.onPostExecute(response4List);
            if (isDestroyed())
                return;
            pullToRefreshListView.onPullDownRefreshComplete();
            pullToRefreshListView.onPullUpRefreshComplete();
            if (response4List.getData() == null || response4List.getData().size() == 0) {
                pullToRefreshListView.setVisibility(View.GONE);
                line_empty.setVisibility(View.VISIBLE);

            } else {
                pullToRefreshListView.setVisibility(View.VISIBLE);
                line_empty.setVisibility(View.GONE);
                homeRankProfitOrderAdapter.clear();
                for (TradeOrder tradeOrder : response4List.getData()) {
                    homeRankProfitOrderAdapter.add(tradeOrder);
                }
                homeRankProfitOrderAdapter.notifyDataSetChanged();
            }
        }
    }


}
