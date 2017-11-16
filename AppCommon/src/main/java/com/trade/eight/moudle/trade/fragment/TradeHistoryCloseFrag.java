package com.trade.eight.moudle.trade.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.trade.TradeHistorySelectTimeEvent;
import com.trade.eight.moudle.trade.adapter.TradeHistoryCloseAdapter;
import com.trade.eight.moudle.trade.adapter.TradeHistoryCreateAdapter;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.Log;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/28.
 * 交易记录已平仓tab
 */

public class TradeHistoryCloseFrag extends BaseFragment implements PullToRefreshBase.OnRefreshListener<ListView> {

    private String TAG = TradeHistoryCloseFrag.class.getSimpleName();
    private PullToRefreshListView mPullRefreshListView;
    private ListView listView;
    private TextView tv_emptytips;

    private int page = 1;
    TradeHistoryCloseAdapter tradeHistoryCloseAdapter;

    boolean isVisible = false;

    private String startTime = "";
    private String endTime = "";

    TradeLoginDlg tradeLoginDlg;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_tradehistory_create, null);
        startTime = getArguments().getString("startTime");
        endTime = getArguments().getString("endTime");
        initView(view);
        EventBus.getDefault().register(this);
        return view;
    }

    public void onEventMainThread(TradeHistorySelectTimeEvent tradeHistorySelectTimeEvent) {
//        startTime = getArguments().getString("startTime");
//        endTime = getArguments().getString("endTime");
        startTime = tradeHistorySelectTimeEvent.startTime;
        endTime = tradeHistorySelectTimeEvent.endTime;
        if (isVisible) {
            page = 1;
            getOrderHistoryClose();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        tradeHistoryCloseAdapter = new TradeHistoryCloseAdapter(getActivity(), 0, new ArrayList<TradeOrder>());
        listView.setAdapter(tradeHistoryCloseAdapter);

        tv_emptytips = (TextView) view.findViewById(R.id.tv_emptytips);
        if (isVisible) {
            getOrderHistoryClose();
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
        if (mPullRefreshListView == null) {
            return;
        }
        if (isVisible) {
            page = 1;
            getOrderHistoryClose();
        } else {

        }
    }

    /**
     * 获取历史平仓
     */
    private void getOrderHistoryClose() {
        HashMap<String, String> map = new HashMap<>();
        map.put("dayStart", startTime);
        map.put("dayEnd", endTime);
        map.put("page", page + "");
        map.put("pageSize", "20");
        HttpClientHelper.doPostOption((BaseActivity) getActivity(),
                AndroidAPIConfig.URL_ORDERHISTORY_CLOSELIST,
                map,
                null,
                new NetCallback((BaseActivity) getActivity()) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
                        mPullRefreshListView.onPullUpRefreshComplete();
                        mPullRefreshListView.onPullDownRefreshComplete();
                        if (page == 1) {
                            tv_emptytips.setVisibility(View.VISIBLE);
                            tv_emptytips.setText("暂无交易记录");
                        }

                        if (ApiConfig.isNeedLogin(resultCode)) {

                            if (tradeLoginDlg == null) {
                                tradeLoginDlg = new TradeLoginDlg((BaseActivity) getActivity());
                            }
                            if (!tradeLoginDlg.isShowingDialog()) {
                                tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                            }
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
                        mPullRefreshListView.onPullUpRefreshComplete();
                        mPullRefreshListView.onPullDownRefreshComplete();
                        CommonResponse4List<TradeOrder> commonResponse4List = CommonResponse4List.fromJson(response, TradeOrder.class);
                        List<TradeOrder> orderList = commonResponse4List.getData();

                        if (orderList != null && orderList.size() > 0) {
                            tradeHistoryCloseAdapter.setItems(orderList, page == 1 ? true : false);

                            tv_emptytips.setVisibility(View.GONE);
                        } else {
                            if (page == 1) {
                                tv_emptytips.setVisibility(View.VISIBLE);
                                tv_emptytips.setText("暂无交易记录");
                            }
                        }
                    }
                }
                , page == 1);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        page = 1;
        getOrderHistoryClose();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        page += 1;
        getOrderHistoryClose();
    }
}
