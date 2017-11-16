package com.trade.eight.moudle.trade.cashinout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.Banks;
import com.trade.eight.moudle.me.activity.CashInAndOutAct;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.trade.BankEvent;
import com.trade.eight.moudle.trade.adapter.UserBankListAdapter;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.view.AppTitleView;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 2017/5/26.
 * 银行卡卡管理
 */

public class CashOutCardManageAct extends BaseActivity implements
        PullToRefreshBase.OnRefreshListener<ListView> {

    private PullToRefreshListView mPullRefreshListView;
    private ListView listView;
    UserBankListAdapter userBankListAdapter;
    AppTitleView title_view;
    TextView text_howadd_bank;

    public static void startAct(Context context) {
        Intent intent = new Intent(context, CashOutCardManageAct.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTintResource(R.color.c_464646);
        setContentView(R.layout.act_cashout_cardmanager);
        EventBus.getDefault().register(this);
        initViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(BankEvent bankEvent) {
        doMyfinish();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPullRefreshListView.doPullRefreshing(true, 0);
    }

    void initViews() {

        title_view = (AppTitleView) findViewById(R.id.title_view);
        title_view.setBaseActivity(CashOutCardManageAct.this);
        title_view.setBackViewResource(R.drawable.img_goback_new_white);
        title_view.setAppCommTitle(R.string.lable_choose_bank);
        title_view.setTitleTextColor(R.color.white);
        title_view.setRootViewBG(R.color.c_464646);
        title_view.setSpiltLineColor(R.color.c_5c5c5d);

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setPullRefreshEnabled(true);
        mPullRefreshListView.setPullLoadEnabled(false);
        listView = mPullRefreshListView.getRefreshableView();
//        listView.setHeaderDividersEnabled(false);
//        listView.setFooterDividersEnabled(false);
        listView.setDividerHeight(0);

        userBankListAdapter = new UserBankListAdapter(CashOutCardManageAct.this, 0, new ArrayList<Banks>());
        userBankListAdapter.setShowImgChecked(false);
        listView.setAdapter(userBankListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

//                finish();
            }
        });
        mPullRefreshListView.doPullRefreshing(true, 0);


        text_howadd_bank = (TextView) findViewById(R.id.text_howadd_bank);
        text_howadd_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAppMobclickAgent.onEvent(CashOutCardManageAct.this, UmengMobClickConfig.CASH_IN_OUT, "cash_howto_addcard");
                WebActivity.start(CashOutCardManageAct.this, getString(R.string.lable_howadd_bank), AndroidAPIConfig.URL_HOWTO_ADDCARD, true);
            }
        });
    }

    private void getUserBankList() {
        HttpClientHelper.doPostOption(CashOutCardManageAct.this,
                AndroidAPIConfig.getAPI(CashOutCardManageAct.this, AndroidAPIConfig.KEY_URL_TRADE_GET_USER_BANKS),
                null,
                null,
                new NetCallback(CashOutCardManageAct.this) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
                        mPullRefreshListView.onPullUpRefreshComplete();
                        mPullRefreshListView.onPullDownRefreshComplete();
                    }

                    @Override
                    public void onResponse(String response) {
                        CommonResponse4List<Banks> commonResponse4List = CommonResponse4List.fromJson(response, Banks.class);
                        List<Banks> banksList = commonResponse4List.getData();
                        if (banksList != null && banksList.size() > 0) {
                            userBankListAdapter.setItems(banksList, true);
                        } else {
                            userBankListAdapter.clear();
                        }
                        mPullRefreshListView.onPullUpRefreshComplete();
                        mPullRefreshListView.onPullDownRefreshComplete();
                        BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
                    }
                },
                false);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        getUserBankList();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        getUserBankList();
    }
}
