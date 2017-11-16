package com.trade.eight.moudle.me.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.Banks;
import com.trade.eight.entity.trade.CashHistoryData;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.moudle.me.adapter.CashHistoryAdapter;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/14.
 */

public class CashHistoryFrag extends BaseFragment implements PullToRefreshBase.OnRefreshListener<ListView> {
    private String TAG =CashHistoryFrag.class.getSimpleName();

    private PullToRefreshListView mPullRefreshListView;
    private ListView listView;
    private TextView tv_emptytips;

    private int page = 1;
    CashHistoryAdapter cashHistoryAdapter;

    boolean isVisible = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_cash_history, null);
        initView(rootView);
        Log.e(TAG,"onCreateView====");
        return rootView;
    }

    private void initView(View view) {
        mPullRefreshListView = (PullToRefreshListView) view
                .findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setPullRefreshEnabled(true);
        mPullRefreshListView.setPullLoadEnabled(true);
        listView = mPullRefreshListView.getRefreshableView();
        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(false);
        listView.setDividerHeight(0);
        cashHistoryAdapter = new CashHistoryAdapter(getActivity(), 0, new ArrayList<CashHistoryData>());
        listView.setAdapter(cashHistoryAdapter);

        tv_emptytips = (TextView) view.findViewById(R.id.tv_emptytips);
        if (isVisible) {
            getUserCashList();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.v(TAG, "isVisibleToUser=" + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
    }
    @Override
    public void onFragmentVisible(boolean isVisible) {
        super.onFragmentVisible(isVisible);
        Log.v(TAG, "onFragmentVisible isVisible= " + isVisible);
        this.isVisible = isVisible;
        if(mPullRefreshListView==null){
            return;
        }
        if (isVisible) {
            getUserCashList();
        } else {

        }
    }

    private void getUserCashList() {
        HashMap<String, String> req = new HashMap<>();
        req.put("page", page + "");
        req.put("pageSize", "20");

        HttpClientHelper.doPostOption((BaseActivity) getActivity(),
                AndroidAPIConfig.URL_TRADE_USER_CASHHISTORY,
                req,
                null,
                new NetCallback((BaseActivity) getActivity()) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        showCusToast(resultMsg);
                        mPullRefreshListView.onPullDownRefreshComplete();
                        mPullRefreshListView.onPullUpRefreshComplete();
                    }

                    @Override
                    public void onResponse(String response) {
                        mPullRefreshListView.onPullDownRefreshComplete();
                        mPullRefreshListView.onPullUpRefreshComplete();
                        CommonResponse4List<CashHistoryData> commonResponse4List = CommonResponse4List.fromJson(response, CashHistoryData.class);
                        List<CashHistoryData> list = commonResponse4List.getData();
                        tv_emptytips.setVisibility(View.GONE);
                        if(page==1){
                            if(list!=null&&list.size()>0){
                                cashHistoryAdapter.setItems(list,true);
                            }else{
                                tv_emptytips.setVisibility(View.VISIBLE);
                            }
                        }else{
                            cashHistoryAdapter.setItems(list,false);
                        }
                    }
                },
                true);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        page = 1;
        getUserCashList();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        page += 1;
        getUserCashList();
    }
}
