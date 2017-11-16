package com.trade.eight.moudle.home.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AppSetting;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.response.NettyResponse;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.me.activity.StepNavAct;
import com.trade.eight.moudle.me.activity.StepNavMuiltAct;
import com.trade.eight.moudle.netty.NettyClient;
import com.trade.eight.moudle.netty.NettyUtil;
import com.trade.eight.moudle.product.OptionalEvent;
import com.trade.eight.moudle.trade.SetViewHeightEvent;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.moudle.trade.UpdateTradeUserInfoEvent;
import com.trade.eight.moudle.trade.adapter.TradePListAdapter;
import com.trade.eight.mpush.coreoption.CoreOptionUtil;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.product.ProductViewHole4TradeContent;
import com.trade.eight.tools.refresh.RefreshUtil;
import com.trade.eight.tools.trade.TradeConfig;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;
import com.trade.eight.view.trade.TradeHeadView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/11/1.
 * 交易大厅下方  买卖 tab
 * 买卖list
 */
public class TradeContentListFrag extends BaseFragment implements
        PullToRefreshBase.OnRefreshListener<ListView> {
    String TAG = "TradeContentListFrag";
    //取到第一条数据是否停盘  外汇停盘不一样  所以不能使用
//    String closeStr = null;
    View view;
    RefreshUtil refreshUtil;

    TradePListAdapter dateAdapter = null;
    private PullToRefreshListView mPullRefreshListView;
    private ListView listView;
    View headView = null;
    TradeHeadView tradeHeadView;

    String productCodes = "";

    ProductViewHole4TradeContent productViewHole4TradeContent;

    View line_unlogin;
    ImageView img_close_unlogin;
    Button btn_unlogin;

    private String getProductsCodes() {
        if (!TextUtils.isEmpty(productCodes)) {
            return productCodes;
        }
//        List<ProductObj> objects = dateAdapter.getItems();
        if (optionalList == null) {
            return productCodes;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (ProductObj productObj : optionalList) {
            stringBuffer.append(productObj.getExcode());
            stringBuffer.append("|");
            stringBuffer.append(productObj.getInstrumentId());
            stringBuffer.append(",");
        }
        String temp = stringBuffer.toString();
        if (!TextUtils.isEmpty(temp)) {
            productCodes = temp.substring(0, temp.length() - 1);
        }
        return productCodes;
    }

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
        EventBus.getDefault().register(this);
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
        view = inflater.inflate(R.layout.frag_tradecontent_list, null);
        initRefresh();
        initView(view);
        return view;
    }


    void initView(View view) {

        line_unlogin = view.findViewById(R.id.line_unlogin);
        img_close_unlogin = (ImageView) view.findViewById(R.id.img_close_unlogin);
        btn_unlogin = (Button) view.findViewById(R.id.btn_unlogin);
        initUnLoginViewClick();

        mPullRefreshListView = (PullToRefreshListView) view
                .findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setPullRefreshEnabled(true);
        mPullRefreshListView.setPullLoadEnabled(false);
        listView = mPullRefreshListView.getRefreshableView();
        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(false);
        listView.setDividerHeight(0);
        dateAdapter = new TradePListAdapter((BaseActivity) getActivity(), 0, new ArrayList<ProductObj>());

//        headView = View.inflate(getActivity(), R.layout.view_trade_head_2, null);
        tradeHeadView = new TradeHeadView(getActivity());
        tradeHeadView.setBaseActivity((BaseActivity) getActivity());
        listView.addHeaderView(tradeHeadView);

        listView.setAdapter(dateAdapter);

        if (isVisible) {
            BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
            mPullRefreshListView.doPullRefreshing(true, 0);
            getTradeInfoData();
            controlUnLoginView();
        }
    }

    /**
     * 初始化点击时间
     */
    void initUnLoginViewClick() {
        img_close_unlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                line_unlogin.setVisibility(View.GONE);
                MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_INDEX_TRADE_LOGIN_CLOSE);
                PreferenceSetting.setBoolean(getActivity(), PreferenceSetting.ISSHOW_UNLOGIN_VIEW, true);
            }
        });
        btn_unlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_INDEX_TRADE_LOGIN_ZY);
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
    }

    /**
     * 控制view的显示
     */
    void controlUnLoginView() {
        UserInfoDao dao = new UserInfoDao(getActivity());
        if (!dao.isLogin()) {
            if (!PreferenceSetting.getBoolean(getActivity(), PreferenceSetting.ISSHOW_UNLOGIN_VIEW)) {
                if (line_unlogin != null) {
                    line_unlogin.setVisibility(View.VISIBLE);
                }
            } else {
                line_unlogin.setVisibility(View.GONE);
            }
        } else {
            if (line_unlogin != null) {
                line_unlogin.setVisibility(View.GONE);
            }
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


    boolean isVisible = false;

    @Override
    public void onFragmentVisible(boolean isVisible) {
        super.onFragmentVisible(isVisible);
        Log.v(TAG, "onFragmentVisible isVisible= " + isVisible);
        this.isVisible = isVisible;
        if (mPullRefreshListView == null)
            return;
        Log.v(TAG, "isVisible=" + isVisible);
        if (isVisible) {

            controlUnLoginView();
            if (optionalList != null) {
                //没有停盘
                if (refreshUtil != null)
                    refreshUtil.start();
                CoreOptionUtil.getCoreOptionUtil(getActivity().getApplicationContext()).startQuotationMessage(getProductsCodes());


                //开启长连接获取最新行情

//                if (NettyClient.getInstance(getActivity()).isInited())
//                    NettyClient.getInstance(getActivity()).write(TradeConfig.getCurrentCodes((BaseActivity) getActivity()));
//                setListViewHeightBasedOnChildren(listView);
            } else {
                new GetDataPriceTask(true).execute();
            }

            getTradeInfoData();


        } else {
            // 停止刷新
            if (refreshUtil != null)
                refreshUtil.stop();
            //断开长连接
//            NettyClient.getInstance(getActivity()).stopWrite();
            CoreOptionUtil.getCoreOptionUtil(getActivity().getApplicationContext()).stopQuotationMessage();

            if (listView.getCount() > 0) {
                listView.smoothScrollToPosition(0);
            }
        }
    }

    List<ProductObj> optionalList = null;

    void initRefresh() {
        refreshUtil = new RefreshUtil(getActivity());
        refreshUtil.setRefreshTime(AppSetting.getInstance(getActivity()).getRefreshTimeWPList());
        refreshUtil.setOnRefreshListener(new RefreshUtil.OnRefreshListener() {
            @Override
            public Object doInBackground() {
                try {
                    if (isDetached())
                        return null;
                    if (!isAdded())
                        return null;
                    CommonResponse4List<ProductObj> response4List = TradeHelp.getFxPList((BaseActivity) getActivity());
                    if (response4List != null && response4List.isSuccess()) {
                        optionalList = response4List.getData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return optionalList;
            }

            @Override
            public void onUpdate(Object result) {
                if (isDetached())
                    return;
                if (!isAdded())
                    return;
                doMyPostExecute();
            }
        });
    }


    /**
     * 获取数据
     */
    CommonResponse4List<ProductObj> getData() {
        try {
            CommonResponse4List<ProductObj> response4List = TradeHelp.getFxPList((BaseActivity) getActivity());
            if (response4List != null && response4List.isSuccess()) {
                optionalList = response4List.getData();
            }
            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        new GetDataPriceTask(true).execute();
        getTradeInfoData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }


    //封装一下  仅仅作为刷新使用
    class GetDataPriceTask extends AsyncTask<String, Void, CommonResponse4List<ProductObj>> {
        private boolean isPullRefresh;

        public GetDataPriceTask(boolean isPullRefresh) {
            this.isPullRefresh = isPullRefresh;
        }

        @Override
        protected CommonResponse4List<ProductObj> doInBackground(String... params) {
            if (!isAdded())
                return null;
            if (isDetached())
                return null;

            //get list data
            CommonResponse4List<ProductObj> response4List = getData();
            return response4List;
        }

        @Override
        protected void onPostExecute(CommonResponse4List<ProductObj> result) {
            // TODO Auto-generated method stub
            if (!isAdded())
                return;
            super.onPostExecute(result);

            BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
            mPullRefreshListView.onPullUpRefreshComplete();
            mPullRefreshListView.onPullDownRefreshComplete();

            if (result != null
                    && result.isSuccess()) {
                doMyPostExecute();
//                setListViewHeightBasedOnChildren(listView);
                //没有停盘
//                if (isVisible && !TradeProduct.IS_CLOSE_YES.equals(closeStr)) {
//                    //开始刷新
//                    if (refreshUtil != null)
//                        refreshUtil.start();
//                }
                //开始刷新
                if (refreshUtil != null)
                    refreshUtil.start();
                //开启长连接获取最新行情
//                if (NettyClient.getInstance(getActivity()).isInited())
//                    NettyClient.getInstance(getActivity()).write(TradeConfig.getCurrentCodes((BaseActivity) getActivity()));

                CoreOptionUtil.getCoreOptionUtil(getActivity().getApplicationContext()).startQuotationMessage(getProductsCodes());

                //引导页
                if (!PreferenceSetting.getBoolean(getActivity(), "trade_create")) {
                    StepNavMuiltAct.start(getActivity(), StepNavMuiltAct.TYPE_TRADE_CREATE);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }

            } else {
                String msg = getActivity().getResources().getString(R.string.network_problem);
                if (result != null) {
                    msg = ConvertUtil.NVL(result.getErrorInfo(), msg);
                }
                showCusToast(msg);
            }

        }
    }

    void doMyPostExecute() {
        if (isDetached())
            return;
        if (!isAdded())
            return;
        if (dateAdapter.getProductViewHole4TradeContent() == null) {
            productViewHole4TradeContent = new ProductViewHole4TradeContent((BaseActivity) getActivity(), getProductsCodes());
            dateAdapter.setProductViewHole4TradeContent(productViewHole4TradeContent);
        }
        dateAdapter.setItems(optionalList);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (refreshUtil != null)
                refreshUtil.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventBus.getDefault().unregister(this);
    }

    /**
     * netty连接成功后收到的广播
     *
     * @param event
     */
    public void onEvent(final OptionalEvent event) {
        //如果被隐藏了可以不执行刷新操作
        if (isHidden())
            return;
        Log.v(TAG, "onEvent");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event == null)
                    return;
                NettyResponse<Optional> response = event.getNettyResponse();
                if (response == null)
                    return;
                if (NettyUtil.TYPE_CONNECT.equals(response.getType())) {
                    CoreOptionUtil.getCoreOptionUtil(getActivity().getApplicationContext()).startQuotationMessage(getProductsCodes());
                } else if (NettyUtil.TYPE_QP.equals(response.getType())) {
                    Optional optional = response.getData();
                    if (optional == null)
                        return;
                    //还没有初始化
                    if (optionalList == null)
                        return;

                    // 局部刷新
                    Log.e(TAG, "listView.getHeaderViewsCount()====" + listView.getHeaderViewsCount());
                    int visiblePosition = listView.getFirstVisiblePosition();
                    Log.e(TAG, "visiblePosition====" + visiblePosition);
//                Log.e(TAG,optional.getName()+"=====");
                    int visibleLastPosition = listView.getLastVisiblePosition();

                    //只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
                    int itemIndex = dateAdapter.getProductIndex(optional.getCode()) + listView.getHeaderViewsCount();
                    if (itemIndex - visiblePosition >= 0 && itemIndex < visibleLastPosition + listView.getHeaderViewsCount()) {
                        //得到要更新的item的view
                        View view = listView.getChildAt(itemIndex - visiblePosition);
//                    //调用adapter更新界面
//                    dateAdapter.updateView(view, itemIndex);
                        TextView tv_buyup_rate = (TextView) view.findViewById(R.id.tv_buyup_rate);
                        TextView tv_buydown_rate = (TextView) view.findViewById(R.id.tv_buydown_rate);
                        tv_buyup_rate.setText(optional.getAskPrice1());
                        tv_buydown_rate.setText(optional.getBidPrice1());
                    }
                }
            }
        });

    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        Log.e(TAG, "height======" + height);
        EventBus.getDefault().post(new SetViewHeightEvent(height));
        params.height = height;
        listView.setLayoutParams(params);
    }

}
