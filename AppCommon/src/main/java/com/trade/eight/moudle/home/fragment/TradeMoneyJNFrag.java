package com.trade.eight.moudle.home.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.TradeCashOut;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.entity.trade.TradeRechargeHistory;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.trade.activity.CashInAct;
import com.trade.eight.moudle.trade.activity.CashOutAct;
import com.trade.eight.moudle.trade.activity.TradeCloseDetailAct;
import com.trade.eight.moudle.trade.activity.TradeVoucherAct;
import com.trade.eight.moudle.trade.adapter.MyCashOutAdapter;
import com.trade.eight.moudle.trade.adapter.MyHistoryDataAdapter;
import com.trade.eight.moudle.trade.adapter.MyRechargeAdapter;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.trade.ExchangeConfig;
import com.trade.eight.tools.trade.TradeConfig;
import com.trade.eight.view.UnderLineTextView;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by Administrator
 * <p/>
 * 交易 金额 农交所独有
 */
public class TradeMoneyJNFrag extends BaseFragment implements
        PullToRefreshBase.OnRefreshListener<ListView>, View.OnClickListener {
    String TAG = "TradeMoneyJNFrag";

    //交易记录
    MyHistoryDataAdapter myHistoryDataAdapter = null;
    //提现记录
    MyCashOutAdapter myCashOutAdapter = null;
    //充值记录
    MyRechargeAdapter myRechargeAdapter = null;

    private PullToRefreshListView mPullRefreshListView;
    private ListView listView;
    private TextView tv_emptytips;
    //    View headView;
    TextView tv_totalMoney;
    TextView tv_voucercategory;
    UnderLineTextView tv_1, tv_2, tv_3, selectView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.v(TAG, "onSaveInstanceState != null");
            if (savedInstanceState.getBoolean("isHidden")) {
                Log.v(TAG, "onSaveInstanceState != null && isHidden == true");
                getChildFragmentManager().beginTransaction().hide(this).commit();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "onSaveInstanceState");
        outState.putBoolean("isHidden", isHidden());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.frag_trade_money_jn, null);
        myHistoryDataAdapter = new MyHistoryDataAdapter(getActivity(), 0, new ArrayList<TradeOrder>());
        myCashOutAdapter = new MyCashOutAdapter(getActivity(), 0, new ArrayList<TradeCashOut>());
        myRechargeAdapter = new MyRechargeAdapter(getActivity(), 0, new ArrayList<TradeRechargeHistory>());
        initView(view);
        return view;
    }

    void initView(View view) {
        mPullRefreshListView = (PullToRefreshListView) view
                .findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setPullRefreshEnabled(true);
        mPullRefreshListView.setPullLoadEnabled(true);
        listView = mPullRefreshListView.getRefreshableView();
        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(false);
        listView.setDividerHeight(0);

        tv_emptytips = (TextView) view.findViewById(R.id.tv_emptytips);

        view.findViewById(R.id.btn_tixian).setOnClickListener(this);
        view.findViewById(R.id.btn_chongzhi).setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Adapter adapter = parent.getAdapter();
                    if (adapter == null)
                        return;
                    if (position < listView.getHeaderViewsCount())
                        return;

                    if (tv_1.isSelected()) {
                        isRefreshOnVisible = false;
                        //交易记录
                        Intent intent = new Intent(getActivity(), TradeCloseDetailAct.class);
                        intent.putExtra("object", (TradeOrder) adapter.getItem(position));
                        startActivity(intent);

                    } else if (tv_2.isSelected()) {
                        //收支明细

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        tv_totalMoney = (TextView) headView.findViewById(R.id.tv_totalMoney);
        tv_totalMoney = (TextView) view.findViewById(R.id.tv_totalMoney);
        tv_voucercategory = (TextView) view.findViewById(R.id.tv_voucercategory);


        tv_1 = (UnderLineTextView) view.findViewById(R.id.tv_1);
        tv_2 = (UnderLineTextView) view.findViewById(R.id.tv_2);
        tv_3 = (UnderLineTextView) view.findViewById(R.id.tv_3);
        tv_1.setOnClickListener(this);
        tv_2.setOnClickListener(this);
        tv_3.setOnClickListener(this);
        selectView = tv_1;
        selectView.setSelected(true);

        // 农交所收支分开展示
//        if (TradeConfig.isCurrentJN(getActivity())) {
//            tv_2.setText(R.string.out_history);
//        } else {
//            tv_2.setText(R.string.inandout_history);
//            tv_3.setVisibility(View.GONE);
//        }

        listView.setAdapter(myHistoryDataAdapter);

        tv_totalMoney.setText(blance);

        BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);


        View tickView = view.findViewById(R.id.tickView);
        tickView.setOnClickListener(this);
        if (TradeConfig.isCurrentHN(getActivity())) {
            if (!BaseInterface.isShowHNVoucher) {// 华凝所暂时先隐藏代金券入口
                tickView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.v(TAG, "isVisibleToUser=" + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
    }

    Dialog tokenDlg;

    /**
     * dialog的处理
     */
    void showTokenDialog() {
        if (!isVisible)
            return;

        if (tokenDlg != null) {
            if (tokenDlg.isShowing())
                return;
        }
        if (!isAdded())
            return;
        if (isDetached())
            return;
        DialogUtil.showTokenDialog((BaseActivity) getActivity(), TradeConfig.getCurrentTradeCode(getActivity()), new DialogUtil.AuthTokenDlgShow() {
            @Override
            public void onDlgShow(Dialog dlg) {
                tokenDlg = dlg;
            }
        }, new DialogUtil.AuthTokenCallBack() {
            @Override
            public void onPostClick(Object obj) {

            }

            @Override
            public void onNegClick() {
                //跳到交易大厅去
                ViewPager tradeViewPager = (ViewPager) getActivity().findViewById(R.id.tradeViewPager);
                if (tradeViewPager != null) {
                    tradeViewPager.setCurrentItem(0);
                }
            }
        });

    }

    //onFragmentVisible 的时候是否需要刷新 网络list，点击了 list item之后不需要刷新
    boolean isRefreshOnVisible = true;
    String currentUid = null;
    boolean isVisible = false;

    @Override
    public void onFragmentVisible(boolean isVisible) {
        super.onFragmentVisible(isVisible);
        Log.v(TAG, "onFragmentVisible isVisible= " + isVisible);
        this.isVisible = isVisible;
        if (mPullRefreshListView == null)
            return;
        if (isVisible) {
            UserInfoDao dao = new UserInfoDao(getActivity());
            if (!dao.isLogin()) {
                DialogUtil.showConfirmDlg(getActivity(), null, null, null, true, new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        //跳到交易大厅去
                        ViewPager tradeViewPager = (ViewPager) getActivity().findViewById(R.id.tradeViewPager);
                        if (tradeViewPager != null) {
                            tradeViewPager.setCurrentItem(0);
                        }
                        return false;
                    }
                }, new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        return false;
                    }
                });
                return;
            }
            //红点隐藏
            View tvMoneyDot = getActivity().findViewById(R.id.tvMoneyDot);
            if (tvMoneyDot != null) {
                tvMoneyDot.setVisibility(View.INVISIBLE);
            }


            String uid = dao.queryUserInfo().getUserId();
            if (currentUid != null && !currentUid.equals(uid)) {
                //已经切换了用户,应该要 先清除数据
                tv_totalMoney.setText("--");
                myCashOutAdapter.clear();
                myHistoryDataAdapter.clear();
                myRechargeAdapter.clear();
            }

            //本地先验证token 过期了
            if (!TradeHelp.isTokenEnable((BaseActivity) getActivity())) {
                showTokenDialog();

                return;
            }
            if (isRefreshOnVisible) {
                doPullDown();
//                mPullRefreshListView.doPullRefreshing(false, 0);
            }
            isRefreshOnVisible = true;
        } else {

        }
    }

    int page = 1;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_chongzhi) {
            MyAppMobclickAgent.onEvent(getActivity(), "page_trade_money", "cashin");
            if (new UserInfoDao(getActivity()).isLogin()) {
                startActivity(new Intent(getActivity(), CashInAct.class));
            } else {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        } else if (id == R.id.btn_tixian) {
            MyAppMobclickAgent.onEvent(getActivity(), "page_trade_money", "cashout");
            if (new UserInfoDao(getActivity()).isLogin()) {
                startActivity(new Intent(getActivity(), CashOutAct.class));
            } else {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }

        } else if (id == R.id.tv_1) {
            selectView.setSelected(false);
            selectView.setIsLineEnable(false);
            selectView = tv_1;
            selectView.setSelected(true);
            selectView.setIsLineEnable(true);
            listView.setAdapter(myHistoryDataAdapter);
            page = 1;
            new GetTradeHistoryTask().execute();

        } else if (id == R.id.tv_2) {
            selectView.setSelected(false);
            selectView.setIsLineEnable(false);
            selectView = tv_2;
            selectView.setSelected(true);
            selectView.setIsLineEnable(true);
            listView.setAdapter(myCashOutAdapter);
            page = 1;
            new GetCashOutListTask().execute();

        } else if (id == R.id.tv_3) {
            selectView.setSelected(false);
            selectView.setIsLineEnable(false);
            selectView = tv_3;
            selectView.setSelected(true);
            selectView.setIsLineEnable(true);
            listView.setAdapter(myRechargeAdapter);
            page = 1;
            new GetRechargeListTask().execute();
        } else if (id == R.id.tickView) {
            MyAppMobclickAgent.onEvent(getActivity(), "page_trade_money", "tickList");
//            startActivity(new Intent(getActivity(), TradeRedPacketAct.class));、
            //改用代金券2017-01-17
            startActivity(new Intent(getActivity(), TradeVoucherAct.class));
        }
    }

    String blance = "";

    //get total money
    class GetBlanceTask extends AsyncTask<String, Void, CommonResponse<UserInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected CommonResponse<UserInfo> doInBackground(String... params) {
            return TradeHelp.getUserBalance((BaseActivity)getActivity());
        }

        @Override
        protected void onPostExecute(CommonResponse<UserInfo> result) {
            // TODO Auto-generated method stub
            if (!isAdded())
                return;
            super.onPostExecute(result);
//            mPullRefreshListView.onPullDownRefreshComplete();

            if (result != null) {
                //refresh adapter
                if (result.isSuccess()) {
                    if (result.getData() != null) {
                        blance = result.getData().getBalance();
                    }
                    tv_totalMoney.setText(blance);

                    BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
                } else {
                    //token过期 避免重复弹框
                    if (ApiConfig.isNeedLogin(result.getErrorCode())) {
//                        if (!StringUtil.isEmpty(result.getErrorInfo())) {
//                            ((BaseActivity) getActivity()).showCusToast(result.getErrorInfo());
//                        }
                        //重新登录
                        showTokenDialog();

                        return;
                    }
                    showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "网络连接失败！"));
                }

            } else {
                showCusToast("网络连接失败！");
            }

        }
    }

    //getdata from net
    class GetTradeHistoryTask extends AsyncTask<String, Void, CommonResponse4List<TradeOrder>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected CommonResponse4List<TradeOrder> doInBackground(String... params) {
            return TradeHelp.getTradeHistoryList((BaseActivity)getActivity(), page);
        }

        @Override
        protected void onPostExecute(CommonResponse4List<TradeOrder> result) {
            // TODO Auto-generated method stub
            if (!isAdded())
                return;
            super.onPostExecute(result);
            mPullRefreshListView.onPullDownRefreshComplete();
            mPullRefreshListView.onPullUpRefreshComplete();
            if (result != null) {
                //refresh adapter
                if (result.isSuccess()) {
                    if (page == 1)
                        myHistoryDataAdapter.clear();
                    if (result.getData() != null && result.getData().size() > 0) {
                        tv_emptytips.setVisibility(View.GONE);
                        myHistoryDataAdapter.setItems(result.getData(), false);
                    } else {
                        if (page == 1) {
                            tv_emptytips.setVisibility(View.VISIBLE);
                            tv_emptytips.setText("暂无交易记录");
                        }
                    }
                } else {
                    //token过期 避免重复弹框
                    if (ApiConfig.isNeedLogin(result.getErrorCode())) {
//                        if (!StringUtil.isEmpty(result.getErrorInfo())) {
//                            ((BaseActivity) getActivity()).showCusToast(result.getErrorInfo());
//                        }
                        //重新登录
                        showTokenDialog();

                        return;
                    }
                    showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "网络连接失败！"));
                }

                BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
            } else {
                showCusToast("网络连接失败！");
            }

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
            return TradeHelp.getCashOutList((BaseActivity)getActivity(), page);
        }

        @Override
        protected void onPostExecute(CommonResponse4List<TradeCashOut> result) {
            // TODO Auto-generated method stub
            if (!isAdded())
                return;
            super.onPostExecute(result);
            mPullRefreshListView.onPullDownRefreshComplete();
            mPullRefreshListView.onPullUpRefreshComplete();

            if (result != null) {
                //refresh adapter
                if (result.isSuccess()) {
                    if (page == 1)
                        myCashOutAdapter.clear();
                    if (result.getData() != null && result.getData().size() > 0) {
                        tv_emptytips.setVisibility(View.GONE);
                        myCashOutAdapter.setItems(result.getData(), false);
                    } else {
                        if (page == 1) {
                            tv_emptytips.setVisibility(View.VISIBLE);
                            tv_emptytips.setText("暂无提现记录");
                        }
                    }
                } else {
                    //token过期 避免重复弹框
                    if (ApiConfig.isNeedLogin(result.getErrorCode())) {
//                        if (!StringUtil.isEmpty(result.getErrorInfo())) {
//                            ((BaseActivity) getActivity()).showCusToast(result.getErrorInfo());
//                        }
                        //重新登录
                        showTokenDialog();

                        return;
                    }
                    showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "网络连接失败！"));
                }

                BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
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
            return TradeHelp.getRechargeList((BaseActivity)getActivity(), page);
        }

        @Override
        protected void onPostExecute(CommonResponse4List<TradeRechargeHistory> result) {
            // TODO Auto-generated method stub
            if (!isAdded())
                return;
            super.onPostExecute(result);
            mPullRefreshListView.onPullDownRefreshComplete();
            mPullRefreshListView.onPullUpRefreshComplete();

            if (result != null) {
                //refresh adapter
                if (result.isSuccess()) {
                    if (page == 1)
                        myRechargeAdapter.clear();
                    if (result.getData() != null && result.getData().size() > 0) {
                        tv_emptytips.setVisibility(View.GONE);
                        myRechargeAdapter.setItems(result.getData(), false);
                    } else {
                        if (page == 1) {
                            tv_emptytips.setVisibility(View.VISIBLE);
                            tv_emptytips.setText("暂无充值记录");
                        }
                    }
                } else {
                    //token过期 避免重复弹框
                    if (ApiConfig.isNeedLogin(result.getErrorCode())) {
//                        if (!StringUtil.isEmpty(result.getErrorInfo())) {
//                            ((BaseActivity) getActivity()).showCusToast(result.getErrorInfo());
//                        }
                        //重新登录
                        showTokenDialog();

                        return;
                    }
                    showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "网络连接失败！"));
                }

                BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
            } else {
                showCusToast("网络连接失败！");
            }

        }
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        ExchangeConfig.getInstance().check4TabTrade(getActivity());
        doPullDown();
    }

    void doPullDown() {
        //记录当前用户id
        UserInfoDao dao = new UserInfoDao(getActivity());
        if (!dao.isLogin())
            return;
        currentUid = dao.queryUserInfo().getUserId();

        page = 1;
        new GetBlanceTask().execute();

        if (tv_1.isSelected()) {
            new GetTradeHistoryTask().execute();
        } else if (tv_2.isSelected()) {
            new GetCashOutListTask().execute();
        } else if (tv_3.isSelected()) {
            new GetRechargeListTask().execute();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        page++;
        if (tv_1.isSelected()) {
            new GetTradeHistoryTask().execute();
        } else if (tv_2.isSelected()) {
            new GetCashOutListTask().execute();
        } else if (tv_3.isSelected()) {
            new GetRechargeListTask().execute();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
