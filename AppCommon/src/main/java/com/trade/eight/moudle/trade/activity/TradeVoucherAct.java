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
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.TradeVoucher;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.trade.adapter.MyVoucherAdapter;
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
 * Created by fangzhu on 16/6/5.
 */
public class TradeVoucherAct extends BaseActivity implements
        PullToRefreshBase.OnRefreshListener<ListView>, View.OnClickListener {
    public static final String TAG = "TradeVoucherAct";

    TradeVoucherAct context = this;
    private PullToRefreshListView mPullRefreshListView;
    private ListView listView;
    //代金券adapter
    MyVoucherAdapter myVoucherAdapter = null;
    View emptyView;

    int page = 1;
    int exchangeID;
    String currentCode;

    public static void startTradeVoucherAct(Context context, int exchangeID) {
        Intent intent = new Intent();
        intent.setClass(context, TradeVoucherAct.class);
        intent.putExtra("exchangeID", exchangeID);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_trade_voucher);
        setAppCommonTitle("我的代金券");
        initData();
        initViews();
    }

    void initData() {
        exchangeID = getIntent().getIntExtra("exchangeID", 0);
        currentCode = TradeConfig.getCurrentTradeCode(TradeVoucherAct.this);
        switch (exchangeID){
            case TradeConfig.exchangeId_gg:
                TradeConfig.setCurrentTradeCode(TradeVoucherAct.this,TradeConfig.code_gg);
                break;
            case TradeConfig.exchangeId_hg:
                TradeConfig.setCurrentTradeCode(TradeVoucherAct.this,TradeConfig.code_hg);
                break;
            case TradeConfig.exchangeId_jn:
                TradeConfig.setCurrentTradeCode(TradeVoucherAct.this,TradeConfig.code_jn);
                break;
            case TradeConfig.exchangeId_hn:
                TradeConfig.setCurrentTradeCode(TradeVoucherAct.this,TradeConfig.code_hn);
                break;
        }
    }


    void initViews() {
        emptyView = findViewById(R.id.emptyView);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setPullRefreshEnabled(true);
        mPullRefreshListView.setPullLoadEnabled(true);
        listView = mPullRefreshListView.getRefreshableView();
        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(false);
        listView.setDividerHeight(0);
        myVoucherAdapter = new MyVoucherAdapter(context, 0, new ArrayList<TradeVoucher>());
        listView.setAdapter(myVoucherAdapter);
        findViewById(R.id.btn_des).setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        mPullRefreshListView.doPullRefreshing(true, 0);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_des) {
            MyAppMobclickAgent.onEvent(context, "page_voucher", "btn_tickDes");
//            startActivity(new Intent(this, VoucherRuleAct.class));
            WebActivity.start(context, getString(R.string.lable_voucher_rule), AndroidAPIConfig.URL_VOUCHER_RULE);

        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        page = 1;
        new GetTradeVoucherListTask().execute();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        page++;
        new GetTradeVoucherListTask().execute();
    }

    //get 代金券 list
    class GetTradeVoucherListTask extends AsyncTask<String, Void, CommonResponse4List<TradeVoucher>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected CommonResponse4List<TradeVoucher> doInBackground(String... params) {
            return TradeHelp.getTradeVoucherList(context, page);
        }

        @Override
        protected void onPostExecute(CommonResponse4List<TradeVoucher> result) {
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
                    //test
//                    TradeVoucher voucher = new TradeVoucher();
//                    voucher.setAmount(10);
//                    voucher.setNumber(1);
//                    voucher.setLimitTime("2016-01-19");
//                    result.getData().add(voucher);
//
//                    voucher = new TradeVoucher();
//                    voucher.setAmount(100);
//                    voucher.setNumber(1);
//                    voucher.setLimitTime("2016-01-22");
//                    result.getData().add(voucher);
                    //没有代金券
                    if (page == 1)
                        myVoucherAdapter.clear();

                    if (result.getData() != null && result.getData().size() > 0) {
                        myVoucherAdapter.setItems(result.getData(), false);
                    } else if (page > 1 && (result.getData() == null || result.getData().size() == 0)) {
                        showCusToast("没有更多数据");
                    }

                    if (page == 1 && (result.getData() == null || result.getData().size() == 0)) {
                        //没有数据
                        emptyView.setVisibility(View.VISIBLE);
                    } else {

                    }

                } else {
                    //token过期 避免重复弹框
                    if (ApiConfig.isNeedLogin(result.getErrorCode())) {
                        //重新登录
                        DialogUtil.showTokenDialog(context, TradeConfig.getCurrentTradeCode(context), null, new DialogUtil.AuthTokenCallBack() {
                            @Override
                            public void onPostClick(Object obj) {

                            }

                            @Override
                            public void onNegClick() {
                                finish();
                                TradeConfig.setCurrentTradeCode(TradeVoucherAct.this,currentCode);
                            }
                        });
                        return;
                    }else{
                        if (!StringUtil.isEmpty(result.getErrorInfo())) {
                            showCusToast(result.getErrorInfo());
                        }
                    }
                }

                BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
            } else {
                showCusToast("网络连接失败！");
            }

        }
    }

    @Override
    public void doMyfinish() {
        super.doMyfinish();
        TradeConfig.setCurrentTradeCode(TradeVoucherAct.this,currentCode);
    }
}
