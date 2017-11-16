package com.trade.eight.moudle.home.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.config.QiHuoExplainWordConfig;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.Banks;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.me.activity.StepNavMuiltAct;
import com.trade.eight.moudle.outterapp.QiHuoExplainWordActivity;
import com.trade.eight.moudle.product.activity.ProductActivity;
import com.trade.eight.moudle.trade.TradeLoginCancleEvent;
import com.trade.eight.moudle.trade.TradeOrderOptionEvent;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.moudle.trade.UpdateTradeUserInfoEvent;
import com.trade.eight.moudle.trade.activity.DialogUtilForTrade;
import com.trade.eight.moudle.trade.close.TradeCloseDlg;
import com.trade.eight.moudle.trade.hold.TradeHoldDetailDlg;
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
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.Utils;
import com.trade.eight.tools.refresh.RefreshUtil;
import com.trade.eight.tools.trade.TradeConfig;
import com.trade.eight.tools.trade.TradeSortUtil;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshExpandListView;
import com.trade.eight.view.trade.TradeHeadView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by Administrator
 * 交易持仓页面
 */
public class TradeOrderHoldFrag extends BaseFragment implements PullToRefreshBase.OnRefreshListener<ExpandableListView> {
    String TAG = "TradeOrderHoldFrag";

    MyDateAdapter dateAdapter = null;
    PullToRefreshExpandListView mPullRefreshListView;
    private ExpandableListView listView;
    View nodataView;

    //拖动的时候后获取高度
    View rootView;
    Dialog confirmColseOrderDlg;

    private int currentEXGroupPosition = -1;
    private int currentEXChildPosition = -1;
    private View currentEXView = null;

    TradeCloseDlg tradeCloseDlg;
    TradeHoldDetailDlg tradeHoldDetailDlg;

    View headView = null;
    View optional_title;
    View optional_title_pop;
    ImageView img_btnhelp_pop;
    ImageView img_btnhelp;
    TradeHeadView tradeHeadView;
    RefreshUtil refreshUtil;

    TradeLoginDlg tradeLoginDlg;
    int position, listViemItemTop;


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
        rootView = inflater.inflate(R.layout.frag_tradeorder_hold, null);
        dateAdapter = new MyDateAdapter(new ArrayList<List<TradeOrder>>());
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initRefresh();
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
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

                // 不滚动时保存当前滚动到的位置
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                        scrolledX = listView.getScrollX();
//                        scrolledY = getScrollY();

                    position = listView.getFirstVisiblePosition();
                    View itemView = listView.getChildAt(0);
                    listViemItemTop = (itemView == null) ? 0 : itemView.getTop();
                    Log.v(TAG, "onScrollStateChanged " + "position=" + position + "  listViemItemTop=" + listViemItemTop);
                }


            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                //吸顶
                if (optional_title != null) {
                    int[] position = new int[2];
                    optional_title.getLocationOnScreen(position);
                    if (position[1] < Utils.getStatusHeight(getActivity()) + Utils.dip2px(getActivity(), 44)) {
                        optional_title_pop.setVisibility(View.VISIBLE);
                    } else {
                        optional_title_pop.setVisibility(View.GONE);
                    }
                }

                position = listView.getFirstVisiblePosition();
                View itemView = listView.getChildAt(0);
                listViemItemTop = (itemView == null) ? 0 : itemView.getTop();
            }
        });

        tradeHeadView = new TradeHeadView(getActivity());
        tradeHeadView.setBaseActivity((BaseActivity) getActivity());

        listView.addHeaderView(tradeHeadView);
        headView = View.inflate(getActivity(), R.layout.view_trade_head_1, null);
        img_btnhelp = (ImageView) headView.findViewById(R.id.img_btnhelp);
        optional_title = headView.findViewById(R.id.optional_title);
        optional_title_pop = view.findViewById(R.id.optional_title_pop);
        img_btnhelp_pop = (ImageView) view.findViewById(R.id.img_btnhelp_pop);

        img_btnhelp_pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                QiHuoExplainWordActivity.startAct(getActivity(),
                DialogUtilForTrade.showQiHuoExplainDlg((BaseActivity) getActivity(),
                        new String[]{QiHuoExplainWordConfig.PROTOCL,
                                QiHuoExplainWordConfig.CCJJ,
                                QiHuoExplainWordConfig.LASTPRICE,
                                QiHuoExplainWordConfig.CCYK});
            }
        });

        img_btnhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                QiHuoExplainWordActivity.startAct(getActivity(),
                DialogUtilForTrade.showQiHuoExplainDlg((BaseActivity) getActivity(),
                        new String[]{QiHuoExplainWordConfig.PROTOCL,
                                QiHuoExplainWordConfig.CCJJ,
                                QiHuoExplainWordConfig.LASTPRICE,
                                QiHuoExplainWordConfig.CCYK});
            }
        });
        nodataView = headView.findViewById(R.id.empty_view);
        listView.addHeaderView(headView);

//        footView = View.inflate(getActivity(), R.layout.foot_orderhold, null);
//        text_orderhold_tips = (TextView) footView.findViewById(R.id.text_orderhold_tips);
//        listView.addFooterView(footView);

        listView.setAdapter(dateAdapter);

        BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
        //初始化的时候HomeTradeFrag会刷新 这里不用刷新
//        mPullRefreshListView.doPullRefreshing(true, 0);
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


    /**
     * 建仓成功  或者平仓成功
     *
     * @param tradeOrderOptionEvent
     */
    public void onEventMainThread(TradeOrderOptionEvent tradeOrderOptionEvent) {
        // 展开默认收起
        if (currentEXView != null) {
            currentEXView.setVisibility(View.GONE);
            currentEXGroupPosition = -1;
            currentEXChildPosition = -1;
        }
        // 平仓成功让其自动刷
        if (tradeOrderOptionEvent.option_type == TradeOrderOptionEvent.OPTION_CLOSESUCCESS) {
            return;
        }

//        if (isVisible) {
//            doPullDown(true);
//        }
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
                //dialog 没有显示`
//                showDialog();
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
            if (refreshUtil != null)
                refreshUtil.stop();
            // 展开默认收起
            if (currentEXView != null) {
                currentEXView.setVisibility(View.GONE);
                currentEXGroupPosition = -1;
                currentEXChildPosition = -1;
            }
            if (listView.getCount() > 0) {
                listView.smoothScrollToPosition(0);
            }

            tradeLoginDlg = null;
        }
    }

    List<List<TradeOrder>> mainList;

    void initRefresh() {
        refreshUtil = new RefreshUtil(getActivity());
        refreshUtil.setRefreshTime(AppSetting.getInstance(getActivity()).getRefreshTimeWPHoldOrder());
        refreshUtil.setOnRefreshListener(new RefreshUtil.OnRefreshListener() {
            @Override
            public Object doInBackground() {
                try {
                    if (isDetached())
                        return null;
                    if (!isAdded())
                        return null;
                    CommonResponse4List<TradeOrder> response4List = TradeHelp.getTradeOrderList((BaseActivity) getActivity());
                    if (response4List != null && response4List.isSuccess()) {
                        List<TradeOrder> tradeOrders = response4List.getData();
                        // 按时间倒序排
                        Collections.sort(tradeOrders, new Comparator<TradeOrder>() {
                            @Override
                            public int compare(TradeOrder tradeOrder, TradeOrder tradeOrder_1) {
                                //降序
                                return (int) (Long.parseLong(tradeOrder_1.getUpdateTime()) - Long.parseLong(tradeOrder.getUpdateTime()));
                            }
                        });
                        mainList = TradeSortUtil.sortOrderInGroup(tradeOrders);
                    } else {
                        if (ApiConfig.isNeedLogin(response4List.getErrorCode())) {
                            //重新登录
//                            showTokenDialog();
                            if (!isVisible) {
                                return null;
                            }
                            if (!isAdded())
                                return null;
                            if (isDetached())
                                return null;
                            if (tradeLoginDlg == null) {
                                tradeLoginDlg = new TradeLoginDlg((BaseActivity) getActivity());
                            }
                            if (!tradeLoginDlg.isShowingDialog()) {
                                tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                            }
                            return null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return mainList;
            }

            @Override
            public void onUpdate(Object result) {
                if (isDetached())
                    return;
                if (!isAdded())
                    return;
                setDataList(mainList);
            }
        });
    }

    private void setDataList(List<List<TradeOrder>> mainList) {
        if (mainList == null) {
            return;
        }
        if (mainList.size() > 0) {
            nodataView.setVisibility(View.GONE);
//            text_orderhold_tips.setVisibility(View.VISIBLE);
        }
        dateAdapter.setData(mainList);
        for (int i = 0; i < dateAdapter.getGroupCount(); i++) {
            listView.expandGroup(i);
        }
//        setListViewHeightBasedOnChildren(listView);
    }

    private void getOrderList(boolean doRefesh) {

        String url = AndroidAPIConfig.getAPI((BaseActivity) getActivity(), AndroidAPIConfig.KEY_URL_TRADE_ORDER_HOLD_LIST);
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
//                        text_orderhold_tips.setVisibility(View.GONE);
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
                            // 按时间倒序排
                            Collections.sort(orderList, new Comparator<TradeOrder>() {
                                @Override
                                public int compare(TradeOrder tradeOrder, TradeOrder tradeOrder_1) {
                                    //降序
                                    return (int) (Long.parseLong(tradeOrder_1.getUpdateTime()) - Long.parseLong(tradeOrder.getUpdateTime()));
                                }
                            });
                            inited = true;
                            nodataView.setVisibility(View.GONE);
                            List<List<TradeOrder>> mainList = TradeSortUtil.sortOrderInGroup(orderList);
                            setDataList(mainList);
//                            text_orderhold_tips.setVisibility(View.VISIBLE);

                            if (refreshUtil != null)
                                refreshUtil.startDelay(AppSetting.getInstance(getActivity()).getRefreshTimeWPHoldOrder());

                            //引导页
                            if (!PreferenceSetting.getBoolean(getActivity(), "trade_hold") && isVisible) {
                                StepNavMuiltAct.start(getActivity(), StepNavMuiltAct.TYPE_TRADE_HOLD);
                                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        } else {
                            dateAdapter.clear();
                            nodataView.setVisibility(View.VISIBLE);
//                            text_orderhold_tips.setVisibility(View.GONE);
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

        getOrderList(doRefesh);
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

                    MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_HOLD_GODETAIL, tradeOrder.getInstrumentName());

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
            final View real_tr_detail = MyViewHolder.get(view, R.id.real_tr_detail);
            TextView text_tr_bzj = MyViewHolder.get(view, R.id.text_tr_bzj);
            TextView text_tr_jgrq = MyViewHolder.get(view, R.id.text_tr_jgrq);
            TextView text_tr_sxf = MyViewHolder.get(view, R.id.text_tr_sxf);
            TextView text_tr_opendetail = MyViewHolder.get(view, R.id.text_tr_opendetail);
            TextView text_tr_close = MyViewHolder.get(view, R.id.text_tr_close);
            ImageView img_btnhelp = MyViewHolder.get(view, R.id.img_btnhelp);

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
            if (tradeOrder.getExcode().equals(TradeConfig.code_SHFE)) {
                if (tradeOrder.getTodayPosition() > 0) {
                    img_tr_today.setVisibility(View.VISIBLE);
                } else {
                    img_tr_today.setVisibility(View.GONE);
                }
            } else {
                img_tr_today.setVisibility(View.GONE);
            }
            text_tr_junjia.setText(tradeOrder.getHoldAvgPrice());
            text_tr_newprice.setText(tradeOrder.getSettlementPrice());
            text_tr_profitloss.setText(tradeOrder.getTodayProfit());
            text_tr_profitloss.setTextColor(getActivity().getResources().getColor(R.color.c_06A969));
            if (Double.parseDouble(tradeOrder.getTodayProfit()) > 0) {
                text_tr_profitloss.setText("+" + tradeOrder.getTodayProfit());
                text_tr_profitloss.setTextColor(getActivity().getResources().getColor(R.color.c_EA4A5E));
            }
            text_tr_bzj.setText(StringUtil.forNumber(new BigDecimal(tradeOrder.getUseMargin()).doubleValue()));
            text_tr_jgrq.setText(getActivity().getResources().getString(R.string.lable_tr_jgrq, tradeOrder.getEndDelivDate(), tradeOrder.getDelivDateStr()));
            text_tr_sxf.setText(tradeOrder.getSxf());
            line_tr_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_HOLD_TRADEORDER, tradeOrder.getInstrumentName());

                    if (currentEXGroupPosition == groupPosition && currentEXChildPosition == childPosition) {
                        if (currentEXView != null) {
                            currentEXView.setVisibility(View.GONE);
                            currentEXGroupPosition = -1;
                            currentEXChildPosition = -1;
                            return;
                        }
                    }
                    if (currentEXView != null) {
                        currentEXView.setVisibility(View.GONE);
                        currentEXGroupPosition = -1;
                        currentEXChildPosition = -1;
                    }
                    currentEXView = real_tr_detail;
                    currentEXView.setVisibility(View.VISIBLE);
                    currentEXGroupPosition = groupPosition;
                    currentEXChildPosition = childPosition;

                    doScrollForShowMenu(groupPosition, childPosition);

//                    listView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            listView.setSelectedChild(groupPosition,childPosition,true);
//                        }
//                    });
                }
            });

            text_tr_opendetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_HOLD_TRADEORDER_DETAIL);
                    tradeHoldDetailDlg = new TradeHoldDetailDlg((BaseActivity) getActivity(), tradeOrder);
                    tradeHoldDetailDlg.showDialog(R.style.dialog_trade_ani);
//                    DialogUtilForTrade.showQiHuoOrderDetilDlg((BaseActivity) getActivity(), tradeOrder);
                }
            });

            if (currentEXChildPosition == childPosition && currentEXGroupPosition == groupPosition) {
                if (tradeHoldDetailDlg != null && tradeHoldDetailDlg.isShowingDialog()) {
                    tradeHoldDetailDlg.setTradeOrder(tradeOrder);
                }
            }

            text_tr_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_HOLD_TRADEORDER_CLOSE);
                    boolean isCloseToday = img_tr_today.getVisibility() == View.VISIBLE;
                    tradeCloseDlg = new TradeCloseDlg((BaseActivity) getActivity(), tradeOrder, isCloseToday);
                    tradeCloseDlg.showDialog(R.style.dialog_trade_ani);
                    tradeCloseDlg.setCreateCallback(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message message) {
//                            doPullDown(true);
                            return false;
                        }
                    });
                }
            });

            img_btnhelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    QiHuoExplainWordActivity.startAct(getActivity(),
                    DialogUtilForTrade.showQiHuoExplainDlg((BaseActivity) getActivity(),
                            new String[]{QiHuoExplainWordConfig.DRBZJ,
                                    QiHuoExplainWordConfig.JCSXF,
                                    QiHuoExplainWordConfig.JGRQ});
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


    /**
     * 当出来的菜单  在导航栏的下方  就进行移动操作
     *
     * @param groupPosition
     * @param childPosition
     */
    private void doScrollForShowMenu(int groupPosition, int childPosition) {

        int[] location = new int[2];
        currentEXView.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int offest = Utils.dip2px(getActivity(), 137);// 菜单栏的高度
        // 屏幕的高度-底部tab的高度= 最底部的y坐标
        int bottomTabY = Utils.getScreenH(getActivity()) - Utils.dip2px(getActivity(), 63);
        // 菜单在最高点之上
        if (y + offest > bottomTabY) {
//            listView.scrollTo(0,scrolledY+offest);
            listView.setSelectionFromTop(position, listViemItemTop - offest);
//            listView.setSelectedChild(groupPosition, childPosition, true);

        }

    }

//    public int getScrollY() {
//        View c = listView.getChildAt(0);
//        if(c == null) {
//            return 0;
//        }
//        int firstVisiblePosition = listView.getFirstVisiblePosition();
//        int top = c.getTop();
//        return -top + firstVisiblePosition * c.getHeight() ;
//    }

//    public void setListViewHeightBasedOnChildren(ListView listView) {
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null) {
//            return;
//        }
//        int totalHeight = 0;
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            View listItem = listAdapter.getView(i, null,
//listView);
//            listItem.measure(0, 0);
//            totalHeight += listItem.getMeasuredHeight();
//        }
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        int height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        Log.e(TAG, "height======" + height);
//        height += Utils.dip2px(getActivity(), 44 + 137);
//        EventBus.getDefault().post(new SetViewHeightEvent(height));
//        params.height = height;
//        listView.setLayoutParams(params);
//    }
}
