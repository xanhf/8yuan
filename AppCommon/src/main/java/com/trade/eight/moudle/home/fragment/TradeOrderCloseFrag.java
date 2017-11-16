package com.trade.eight.moudle.home.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.QiHuoExplainWordConfig;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.outterapp.QiHuoExplainWordActivity;
import com.trade.eight.moudle.product.activity.ProductActivity;
import com.trade.eight.moudle.trade.TradeLoginCancleEvent;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.moudle.trade.UpdateTradeUserInfoEvent;
import com.trade.eight.moudle.trade.activity.DialogUtilForTrade;
import com.trade.eight.moudle.trade.activity.TradeOrderCloseDetailAct;
import com.trade.eight.moudle.trade.activity.TradeOrderHistoryAct;
import com.trade.eight.moudle.trade.close.TradeCloseDlg;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.trade.TradeConfig;
import com.trade.eight.tools.trade.TradeSortUtil;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshExpandListView;
import com.trade.eight.view.trade.TradeHeadView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator
 * 已平仓仓页面
 */
public class TradeOrderCloseFrag extends BaseFragment implements PullToRefreshBase.OnRefreshListener<ExpandableListView> {
    String TAG = "TradeOrderHoldFrag";

    MyDateAdapter dateAdapter = null;
    PullToRefreshExpandListView mPullRefreshListView;
    private ExpandableListView listView;

    //拖动的时候后获取高度
    View rootView;

    private int currentEXGroupPosition = -1;
    private int currentEXChildPosition = -1;
    private View currentEXView = null;

    TradeCloseDlg tradeCloseDlg;

    View headView = null;
    View nodataView;
    ImageView img_btnhelp;
    TextView text_emptytips;
    TextView text_lable_3;
    TextView text_lable_4;

    TradeHeadView tradeHeadView;
    View footView;

    TradeLoginDlg tradeLoginDlg;

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
        rootView = inflater.inflate(R.layout.frag_tradeorder_close, null);
        dateAdapter = new MyDateAdapter(new ArrayList<List<TradeOrder>>());
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initView(rootView);
        return rootView;
    }


    void initView(View view) {
        mPullRefreshListView = (PullToRefreshExpandListView) view
                .findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setPullRefreshEnabled(true);
        mPullRefreshListView.setPullLoadEnabled(false);
        listView = mPullRefreshListView.getRefreshableView();
        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(false);
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
            public boolean onChildClick(ExpandableListView expandableListView, View view, final int groupPosition, final int childPosition, long l) {
                return true;
            }
        });
        //去掉下划线
        listView.setGroupIndicator(null);

        tradeHeadView = new TradeHeadView(getActivity());
        tradeHeadView.setBaseActivity((BaseActivity) getActivity());

        listView.addHeaderView(tradeHeadView);
        headView = View.inflate(getActivity(), R.layout.view_trade_head_1, null);
        img_btnhelp = (ImageView) headView.findViewById(R.id.img_btnhelp);
        img_btnhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                QiHuoExplainWordActivity.startAct(getActivity(),
                MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_CLOSE_EXPLAIN);

                DialogUtilForTrade.showQiHuoExplainDlg((BaseActivity) getActivity(),

                                new String[]{QiHuoExplainWordConfig.PROTOCL,
                                QiHuoExplainWordConfig.CCJJ,
                                QiHuoExplainWordConfig.CLOSEPRICE,
                                QiHuoExplainWordConfig.PCYK});
            }
        });
        listView.addHeaderView(headView);
//
//        footView = View.inflate(getActivity(), R.layout.foot_closehold, null);
//        listView.addFooterView(footView);
        footView = view.findViewById(R.id.footView);
        footView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_CLOSE_VIEWWALL);
                TradeOrderHistoryAct.startAct(getActivity(), 1);
            }
        });

        listView.setAdapter(dateAdapter);

        BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
        nodataView = headView.findViewById(R.id.empty_view);
        text_emptytips = (TextView) headView.findViewById(R.id.text_emptytips);
        text_emptytips.setText("当前交易日暂无平仓");
        text_lable_3 = (TextView) headView.findViewById(R.id.text_lable_3);
        text_lable_4 = (TextView) headView.findViewById(R.id.text_lable_4);

        text_lable_3.setText("平仓价");
        text_lable_4.setText(R.string.trade_close_order_todayprofit);
        if (isVisible) {
            getTradeInfoData();
        }
    }

    private void getTradeInfoData() {

        TradeInfoData tradeInfoData = TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), this).getTradeInfoData();
        if (tradeInfoData != null) {
            tradeHeadView.displayView(tradeInfoData);
        } else {
            TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), this).loadTradeOrderAndUserInfoData(new TradeUserInfoData4Situation.GetDataCallback() {
                @Override
                public void onFailure(String resultCode, String resultMsg) {
//                showCusToast(resultMsg);
                    tradeHeadView.displayView(null);
                }

                @Override
                public void onResponse(TradeInfoData tradeInfoData) {
                    tradeHeadView.displayView(tradeInfoData);
                }
            }, false);
        }
    }

    /**
     * 头部刷新
     *
     * @param updateTradeUserInfoEvent
     */
    public void onEventMainThread(UpdateTradeUserInfoEvent updateTradeUserInfoEvent) {
        TradeInfoData tradeInfoData = updateTradeUserInfoEvent.tradeInfoData;
//        if (tradeHeadView != null) {
        tradeHeadView.displayView(tradeInfoData);
//        }
    }

    /**
     * 登录取消
     *
     * @param tradeLoginCancleEvent
     */
    public void onEventMainThread(TradeLoginCancleEvent tradeLoginCancleEvent) {
        //跳到交易大厅去
        ViewPager tradeViewPager = (ViewPager) getActivity().findViewById(R.id.tradeViewPager);
        if (tradeViewPager != null) {
            tradeViewPager.setCurrentItem(0);
        }
    }


    Dialog tokenDlg = null;

    /**
     * dialog的处理
     */
    void showDialog() {
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
                if (tokenDlg != null) {
                    if (tokenDlg.isShowing())
                        return;
                }
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.v(TAG, "isVisibleToUser=" + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
    }

    String currentUid = null;
    boolean isVisible = false;
    //是否已经初始化过数据了
    boolean inited = false;

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
                if (dateAdapter != null)
                    dateAdapter.clear();
                //没有登录会在HomeTradeFrag 中弹出登录框 这删掉
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                //跳到交易大厅去
                ViewPager tradeViewPager = (ViewPager) getActivity().findViewById(R.id.tradeViewPager);
                if (tradeViewPager != null) {
                    tradeViewPager.setCurrentItem(0);
                }
                return;
            }

            String uid = dao.queryUserInfo().getUserId();
            if (currentUid != null && !currentUid.equals(uid)) {
                //已经切换了用户,应该要 先清除数据
                dateAdapter.clear();
            }

            //本地先验证token 过期了
            if (!TradeHelp.isTokenEnable((BaseActivity) getActivity())) {
                //dialog 没有显示
//                showDialog();

                //重新登录
//                            showTokenDialog();
                if (!isVisible)
                    return;
                if (!isAdded())
                    return;
                if (isDetached())
                    return;
                if (tradeLoginDlg == null) {
                    tradeLoginDlg = new TradeLoginDlg((BaseActivity) getActivity());
                }
                if (!tradeLoginDlg.isShowingDialog()) {
                    tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                }
                return;
            }

            //开始刷新数据  因为不是在viewpager得第一页 getdata 会调用一次 这里会一次
            if (inited) {
                //获取过数据不需要下拉效果
                doPullDown(true);
            } else {
                //第一次
                mPullRefreshListView.doPullRefreshing(false, 0);
            }
            getTradeInfoData();
        } else {
            if (listView.getCount() > 0) {
                listView.smoothScrollToPosition(0);
            }

            tradeLoginDlg = null;
        }
    }

    private void setDataList(List<List<TradeOrder>> mainList) {
        dateAdapter.setData(mainList);
        for (int i = 0; i < dateAdapter.getGroupCount(); i++) {
            listView.expandGroup(i);
        }
    }

    private void getCloseOrderList(boolean doRefesh) {

        String url = AndroidAPIConfig.URL_ORDERCLOSE_LIST;
        HttpClientHelper.doPostOption((BaseActivity) getActivity(),
                url,
                null,
                null,
                new NetCallback((BaseActivity) getActivity()) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {

                        BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
                        mPullRefreshListView.onPullUpRefreshComplete();
                        mPullRefreshListView.onPullDownRefreshComplete();
                        dateAdapter.clear();
                        nodataView.setVisibility(View.VISIBLE);
                        if (ApiConfig.isNeedLogin(resultCode)) {
                            //重新登录
//                            showTokenDialog();
                            if (!isVisible)
                                return;
                            if (!isAdded())
                                return;
                            if (isDetached())
                                return;
                            if (tradeLoginDlg == null) {
                                tradeLoginDlg = new TradeLoginDlg((BaseActivity) getActivity());
                            }
                            if (!tradeLoginDlg.isShowingDialog()) {
                                tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                            }
                            return;
                        }
                        resultMsg = ConvertUtil.NVL(resultMsg, getActivity().getResources().getString(R.string.network_problem));
                        showCusToast(resultMsg);
                    }

                    @Override
                    public void onResponse(String response) {
                        BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
                        mPullRefreshListView.onPullUpRefreshComplete();
                        mPullRefreshListView.onPullDownRefreshComplete();
                        CommonResponse4List<TradeOrder> commonResponse4List = CommonResponse4List.fromJson(response, TradeOrder.class);
                        List<TradeOrder> orderList = commonResponse4List.getData();


                        if (orderList != null && orderList.size() > 0) {
                            inited = true;
                            nodataView.setVisibility(View.GONE);
                            List<List<TradeOrder>> mainList = TradeSortUtil.sortOrderInGroup(orderList);
                            setDataList(mainList);
                        } else {
                            dateAdapter.clear();
                            nodataView.setVisibility(View.VISIBLE);
                        }
                    }
                }, false);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
        doPullDown(true);
    }

    void doPullDown(boolean doRefesh) {
        //记录当前用户id
        UserInfoDao dao = new UserInfoDao(getActivity());
        if (!dao.isLogin())
            return;
        currentUid = dao.queryUserInfo().getUserId();

        getCloseOrderList(doRefesh);
        getTradeInfoData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {

    }

    class MyDateAdapter extends BaseExpandableListAdapter {
        /**
         * 数据集合
         * 分组 外层list就是group
         */
        List<List<TradeOrder>> data;

        public MyDateAdapter(List<List<TradeOrder>> data) {
            this.data = data;
        }

        public List<List<TradeOrder>> getData() {
            return data;
        }

        public void setData(List<List<TradeOrder>> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        public void clear() {
            data.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            return data.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return data.get(i).size();
        }

        @Override
        public Object getGroup(int i) {
            return data.get(i).get(0);
        }

        @Override
        public Object getChild(int i, int i1) {
            return data.get(i).get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = View.inflate(getActivity(), R.layout.item_tradeorder_group, null);
            }
            View line_root = MyViewHolder.get(view, R.id.line_root);
            TextView text_tr_productname = MyViewHolder.get(view, R.id.text_tr_productname);
            final TradeOrder tradeOrder = data.get(groupPosition).get(0);
            text_tr_productname.setText(tradeOrder.getInstrumentName());
            line_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    ((BaseActivity) getActivity()).showCusToast("line_root");
                    MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_CLOSE_GODETAIL,tradeOrder.getInstrumentName());
                    if (StringUtil.isEmpty(tradeOrder.getInstrumentId()))
                        return;

                    Intent intent = new Intent(getActivity(), ProductActivity.class);
                    intent.putExtra("code", tradeOrder.getInstrumentId());
                    intent.putExtra("excode", tradeOrder.getExcode());
                    getActivity().startActivity(intent);
                }
            });
            return view;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = View.inflate(getActivity(), R.layout.item_tradeorder_child, null);
            }
            View line_tr_item = MyViewHolder.get(view, R.id.line_tr_item);
            TextView text_tr_buytype = MyViewHolder.get(view, R.id.text_tr_buytype);
            TextView text_tr_buynum = MyViewHolder.get(view, R.id.text_tr_buynum);
            final ImageView img_tr_today = MyViewHolder.get(view, R.id.img_tr_today);
            TextView text_tr_junjia = MyViewHolder.get(view, R.id.text_tr_junjia);
            TextView text_tr_newprice = MyViewHolder.get(view, R.id.text_tr_newprice);
            TextView text_tr_profitloss = MyViewHolder.get(view, R.id.text_tr_profitloss);


            final TradeOrder tradeOrder = data.get(groupPosition).get(childPosition);
            if (tradeOrder.getType() == ProductObj.TYPE_BUY_UP) {
                text_tr_buytype.setText(R.string.trade_buy_up);
                text_tr_buytype.setTextColor(getActivity().getResources().getColor(R.color.c_EA4A5E));
                text_tr_buynum.setTextColor(getActivity().getResources().getColor(R.color.c_EA4A5E));
            } else {
                text_tr_buytype.setText(R.string.trade_buy_down);
                text_tr_buytype.setTextColor(getActivity().getResources().getColor(R.color.c_06A969));
                text_tr_buynum.setTextColor(getActivity().getResources().getColor(R.color.c_06A969));

            }
            text_tr_buynum.setText(getActivity().getResources().getString(R.string.lable_tr_ordernumber, tradeOrder.getPosition()));

            img_tr_today.setVisibility(View.GONE);
            text_tr_junjia.setText(tradeOrder.getHoldAvgPrice());
            text_tr_newprice.setText(tradeOrder.getClosePrice());
            text_tr_profitloss.setText(tradeOrder.getCloseProfitLoss());
            text_tr_profitloss.setTextColor(getActivity().getResources().getColor(R.color.c_06A969));
            if (Double.parseDouble(ConvertUtil.NVL(tradeOrder.getCloseProfitLoss(), "0")) > 0) {
                text_tr_profitloss.setTextColor(getActivity().getResources().getColor(R.color.c_EA4A5E));
                text_tr_profitloss.setText("+" + tradeOrder.getCloseProfitLoss());
            }

            line_tr_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_CLOSE_CLOSEDETAIL,tradeOrder.getInstrumentName());
                    TradeOrderCloseDetailAct.startAct(getActivity(), tradeOrder);
                }
            });
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
