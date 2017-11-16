package com.trade.eight.moudle.trade.activity;

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
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.TradeCashOut;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.entity.trade.TradeRechargeHistory;
import com.trade.eight.moudle.home.fragment.TradeMoneyJNFrag;
import com.trade.eight.moudle.trade.adapter.MyCashOutAdapter;
import com.trade.eight.moudle.trade.adapter.MyRechargeAdapter;
import com.trade.eight.moudle.trade.adapter.TradeHistoryAdapter;
import com.trade.eight.moudle.trade.cashinout.CashDetailAct;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.view.UnderLineTextView;
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

public class CashHistoryAct extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ListView>, View.OnClickListener {
    PullToRefreshListView pullToRefreshListView = null;
    ListView listView = null;
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    LinearLayout line_empty;
    TextView emptyTv;
    private int page = 1;
    private int pageSize = 20;

    UnderLineTextView tv_1, tv_2, selectView;

    //提现记录
    MyCashOutAdapter myCashOutAdapter = null;
    //充值记录
    MyRechargeAdapter myRechargeAdapter = null;


    public static void startAct(Context context) {
        Intent intent = new Intent(context, CashHistoryAct.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashhistory);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void initViews() {
        setAppCommonTitle(getResources().getString(R.string.trade_order_cashhistory));

        tv_1 = (UnderLineTextView) findViewById(R.id.tv_cashin);
        tv_2 = (UnderLineTextView) findViewById(R.id.tv_cashout);
        tv_1.setOnClickListener(this);
        tv_2.setOnClickListener(this);
        selectView = tv_1;
        selectView.setSelected(true);

        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setPullLoadEnabled(true);
        pullToRefreshListView.setPullRefreshEnabled(true);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setVisibility(View.VISIBLE);
        listView.setDividerHeight(0);

        myRechargeAdapter = new MyRechargeAdapter(CashHistoryAct.this, 0, new ArrayList<TradeRechargeHistory>());
        myCashOutAdapter = new MyCashOutAdapter(CashHistoryAct.this, 0, new ArrayList<TradeCashOut>());

        listView.setAdapter(myRechargeAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectView == tv_1) {
                    CashDetailAct.start(CashHistoryAct.this,
                            getResources().getString(R.string.cashin_history),
                            myRechargeAdapter.getItem(position));
                } else if (selectView == tv_2) {
                    CashDetailAct.start(CashHistoryAct.this,
                            getResources().getString(R.string.cashout_history),
                            myCashOutAdapter.getItem(position));
                }
            }
        });

        line_empty = (LinearLayout) findViewById(R.id.emptyView);
        emptyTv = (TextView) findViewById(R.id.emptyTv);
        pullToRefreshListView.setLastUpdatedLabel(sdf.format(new Date()));
        pullToRefreshListView.doPullRefreshing(true, 0);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        page = 1;
        doPullDown();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        page += 1;
        doPullDown();
    }

    void doPullDown() {
        if (tv_1.isSelected()) {
            new GetRechargeListTask().execute();
        } else if (tv_2.isSelected()) {
            new GetCashOutListTask().execute();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_cashin) {
            selectView.setSelected(false);
            selectView.setIsLineEnable(false);
            selectView = tv_1;
            selectView.setSelected(true);
            selectView.setIsLineEnable(true);
            listView.setAdapter(myRechargeAdapter);
            page = 1;
            new GetRechargeListTask().execute();

        } else if (id == R.id.tv_cashout) {
            selectView.setSelected(false);
            selectView.setIsLineEnable(false);
            selectView = tv_2;
            selectView.setSelected(true);
            selectView.setIsLineEnable(true);
            listView.setAdapter(myCashOutAdapter);
            page = 1;
            new GetCashOutListTask().execute();

        }
    }

    //get detail list 提现记录
    class GetCashOutListTask extends AsyncTask<String, Void, CommonResponse4List<TradeCashOut>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected CommonResponse4List<TradeCashOut> doInBackground(String... params) {
            return TradeHelp.getCashOutList(CashHistoryAct.this, page);
        }

        @Override
        protected void onPostExecute(CommonResponse4List<TradeCashOut> result) {
            // TODO Auto-generated method stub

            super.onPostExecute(result);
            pullToRefreshListView.onPullDownRefreshComplete();
            pullToRefreshListView.onPullUpRefreshComplete();

            if (result != null) {
                //refresh adapter
                if (result.isSuccess()) {
                    if (page == 1)
                        myCashOutAdapter.clear();
                    if (result.getData() != null && result.getData().size() > 0) {
                        line_empty.setVisibility(View.GONE);
                        myCashOutAdapter.setItems(result.getData(), false);
                    } else {
                        if (page == 1) {
                            line_empty.setVisibility(View.VISIBLE);
                            emptyTv.setText(getResources().getString(R.string.out_historyempty));
                        }
                    }
                } else {
                    if (!ApiConfig.ERROR_CODE_RELOGIN.equals(result.getErrorCode())) {
                        showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "网络连接失败！"));
                    }
                }

                BaseInterface.setPullFormartRefreshTime(pullToRefreshListView);
            } else {
                showCusToast("网络连接失败！");
            }

        }
    }

    //get detail list 充值记录
    class GetRechargeListTask extends AsyncTask<String, Void, CommonResponse4List<TradeRechargeHistory>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected CommonResponse4List<TradeRechargeHistory> doInBackground(String... params) {
            return TradeHelp.getRechargeList(CashHistoryAct.this, page);
        }

        @Override
        protected void onPostExecute(CommonResponse4List<TradeRechargeHistory> result) {
            // TODO Auto-generated method stub

            super.onPostExecute(result);
            pullToRefreshListView.onPullDownRefreshComplete();
            pullToRefreshListView.onPullUpRefreshComplete();

            if (result != null) {
                //refresh adapter
                if (result.isSuccess()) {
                    if (page == 1)
                        myRechargeAdapter.clear();
                    if (result.getData() != null && result.getData().size() > 0) {
                        line_empty.setVisibility(View.GONE);
                        myRechargeAdapter.setItems(result.getData(), false);
                    } else {
                        if (page == 1) {
                            line_empty.setVisibility(View.VISIBLE);
                            emptyTv.setText(getResources().getString(R.string.in_historyempty));
                        }
                    }
                } else {
                    //token过期 避免重复弹框
                    if (ApiConfig.isNeedLogin(result.getErrorCode())) {
                        //重新登录
//                        showTokenDialog();

                        return;
                    }
                    if (!ApiConfig.ERROR_CODE_RELOGIN.equals(result.getErrorCode())) {
                        showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "网络连接失败！"));
                    }
                }

                BaseInterface.setPullFormartRefreshTime(pullToRefreshListView);
            } else {
                showCusToast("网络连接失败！");
            }

        }
    }
}

