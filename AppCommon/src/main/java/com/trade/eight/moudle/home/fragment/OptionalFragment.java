package com.trade.eight.moudle.home.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trade.eight.app.ServiceException;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.dao.OptionalDao;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.Exchange;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.response.NettyResponse;
import com.trade.eight.moudle.baksource.BakSourceInterface;
import com.trade.eight.moudle.baksource.BakSourceService;
import com.trade.eight.moudle.home.adapter.OptionalAdapter;
import com.trade.eight.moudle.home.adapter.OptionalAdapter.DiffOrPercentChangeListener;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.netty.NettyClient;
import com.trade.eight.moudle.netty.NettyUtil;
import com.trade.eight.moudle.optional.OptHelper;
import com.trade.eight.moudle.product.OptionalEvent;
import com.trade.eight.moudle.product.activity.ProductActivity;
import com.trade.eight.moudle.product.activity.ProductManagerActivity;
import com.trade.eight.mpush.coreoption.CoreOptionUtil;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.OptionalService;
import com.trade.eight.tools.ActivityUtil;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.refresh.RefreshUtil;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 自选行情
 */
public class OptionalFragment extends BaseFragment implements
        OnRefreshListener<ListView> {

    String TAG = "OptionalFragment";

    private PullToRefreshListView mPullRefreshListView;

    private ListView listView;

    private OptionalAdapter mOptionalAdapter;

    private List<Optional> options = new ArrayList<>();

    private TextView zdf;


    private View layoutLoding;

    /**
     * 封装type交易所
     */
    RecyclerView recyclerView = null;
    /**
     * recyclerView 必须设置 LinearLayoutManager
     */
    LinearLayoutManager linearLayoutManager = null;

    int myFav = 0;//我的自选标示

    int selectSource = myFav;

    List<Exchange> exchangeList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.v(TAG, "onSaveInstanceState != null");
            if (savedInstanceState.getBoolean("isHidden")) {
                Log.v(TAG, "onSaveInstanceState != null && isHidden == true");
                getFragmentManager().beginTransaction().hide(this).commit();
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
        View view = inflater.inflate(R.layout.fragment_optional, null);
        initView(view);
        return view;
    }

    boolean isVisible = false;

    @Override
    public void onFragmentVisible(boolean isVisible) {
        super.onFragmentVisible(isVisible);
        this.isVisible = isVisible;
        if (mPullRefreshListView == null)
            return;

        if (isVisible) {
            getAllProducts();
        } else {
            CoreOptionUtil.getCoreOptionUtil(getActivity().getApplicationContext()).stopQuotationMessage();
//            NettyClient.getInstance(getActivity()).stopWrite();
        }
    }

    @Override
    public void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onDetach() {
        // TODO Auto-generated method stub
        super.onDetach();
    }

    View footVeiw = null;

    public void initView(View view) {

        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        if (tv_title != null) {
            tv_title.setText(getResources().getString(R.string.tab_main_market));
        }

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(linearLayoutManager);

        getActivity().getLayoutInflater();
        footVeiw = LayoutInflater.from(getActivity()).inflate(
                R.layout.option_add, null);
        if (footVeiw != null)
            footVeiw.setVisibility(View.INVISIBLE);
        layoutLoding = view.findViewById(R.id.layoutLoding);
        layoutLoding.setVisibility(View.GONE);//这里默认需要隐藏，第一次是默认加载的是，本地自选
        mPullRefreshListView = (PullToRefreshListView) view
                .findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mOptionalAdapter = new OptionalAdapter(getActivity(), options);
        mOptionalAdapter.listener = changeListener;
        listView = mPullRefreshListView.getRefreshableView();
        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(false);
        listView.setDividerHeight(0);
        if (footVeiw != null)
            listView.addFooterView(footVeiw);
        listView.setAdapter(mOptionalAdapter);

        BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);

        mPullRefreshListView.doPullRefreshing(true, 0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == mOptionalAdapter.getCount()) {
                    if (!new UserInfoDao(getActivity()).isLogin()) {
                        DialogUtil.showLoginConfirmDlg(getActivity(), "添加自选需要登录后才能使用", true, new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message message) {
                                Intent intent = ActivityUtil.initAction(getActivity(), LoginActivity.class, ActivityUtil.ACT_OPT_MANAGER, new Hashtable<String, String>());
                                if (intent != null)
                                    startActivity(intent);
                                return false;
                            }
                        });
                        return;
                    }
                    Intent intent = new Intent(getActivity(), ProductManagerActivity.class);
                    startActivity(intent);

                    MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_INDEX_OPTION, "btn_addproduct_click");
                } else {
                    //mark: please copy same codes for top three items in OnClickListener

                    Optional optional = mOptionalAdapter.getItem(arg2);
                    if (optional != null) {
                        MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_INDEX_OPTION, ConvertUtil.NVL(optional.getTitle(), "") + "_" + ConvertUtil.NVL(optional.getCustomCode(), ""));
//                        if (!optional.isInitData()) {
//                            handleNotINitData(optional);
//                            return;
//                        }
                        Intent intent = new Intent(getActivity(), ProductActivity.class);
                        intent.putExtra("excode", optional.getExchangeID());
                        intent.putExtra("code", optional.getInstrumentID());
                        intent.putExtra("tradeFlag", optional.getTradeFlag());
                        startActivity(intent);

                    }
                }
            }
        });

        zdf = (TextView) view.findViewById(R.id.zhang_die_fu);
    }

    private DiffOrPercentChangeListener changeListener = new DiffOrPercentChangeListener() {

        @Override
        public void change(int count) {
            // TODO Auto-generated method stub
            switch (count % 2) {
                case 0:
                    zdf.setText("涨跌幅");
                    break;
                case 1:
                    zdf.setText("涨跌值");
                    break;
                default:
                    break;
            }
        }
    };


    public void initData(List<Optional> options) {
        if (myFav == selectSource) {
            options = new OptionalService(getActivity()).queryAllMyOptional();
        }
        if (options != null && options.size() > 0) {
            if (myFav == selectSource) {
                if (footVeiw != null && footVeiw.getVisibility() != View.VISIBLE)
                    footVeiw.setVisibility(View.VISIBLE);
            }
            mOptionalAdapter.setItems(options);
        } else {
            mOptionalAdapter.setItems(null);
        }
    }

    /**
     * 每次都调用这个检测是否启动刷新
     */
    void checkRefresh() {
        //先检测是否需要发送tcp
        if (StringUtil.isEmpty(getSpecialCodes())) {
            //没有tcp刷新的品种
//            NettyClient.getInstance(getActivity()).stopWrite();
            CoreOptionUtil.getCoreOptionUtil(getActivity().getApplicationContext()).stopQuotationMessage();

        } else {
            //发送tcp品种
            CoreOptionUtil.getCoreOptionUtil(getActivity().getApplicationContext()).startQuotationMessage(getSpecialCodes());

//            NettyClient.getInstance(getActivity()).write(getSpecialCodes());
        }
    }

    private void getAllProducts() {

        final String url = AndroidAPIConfig.URL_TAB_GETALL_PRODUCT;
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                return HttpClientHelper.getStringFromPost(getActivity(), url, null);
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                try {
                    mPullRefreshListView.onPullDownRefreshComplete();
                    if (!TextUtils.isEmpty(response)) {
                        JSONObject resJson = new JSONObject(response);
                        if (resJson.has("data")) {
                            JSONObject data = resJson.getJSONObject("data");
                            if (data.has("list")) {
                                JSONArray list = data.getJSONArray("list");
                                Type type = new TypeToken<ArrayList<Exchange>>() {
                                }.getType();
                                exchangeList = (List<Exchange>) new Gson().fromJson(list.toString(), type);
                                if (exchangeList != null && exchangeList.size() > 0) {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    // 添加自选
                                    Exchange goods = new Exchange(0, "自选", "自选");
                                    List<Optional> options = new OptionalService(getActivity()).queryAllMyOptional();
                                    goods.setOptionalsList(options);
                                    exchangeList.add(0, goods);
                                    // 添加主连
                                    Exchange mainContinue = new Exchange(1, "主力合约", "主力合约");
                                    List<Optional> mainContinueOpt = new ArrayList<Optional>();
                                    for (int i = 1; i < exchangeList.size(); i++) {
                                        mainContinueOpt.addAll(exchangeList.get(i).getOptionalsList());
                                    }
                                    mainContinue.setOptionalsList(mainContinueOpt);
                                    exchangeList.add(1, mainContinue);

                                    MyAdapter myAdapter = new MyAdapter(exchangeList);
                                    recyclerView.setAdapter(myAdapter);
                                    recyclerView.setItemViewCacheSize(myAdapter.getItemCount());

                                    loadDataByTags(selectSource);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        getAllProducts();

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }


    void handleNotINitData(Optional optional) {
        showCusToast("当前品种暂无数据！");
    }

    /**
     * 点击头部tab 切换行情
     *
     * @param index
     */
    public void loadDataByTags(int index) {

        footVeiw.setVisibility(View.GONE);
        mOptionalAdapter.clear();
        selectSource = index;
        options = exchangeList.get(index).getOptionalsList();
        layoutLoding.setVisibility(View.VISIBLE);
        if (index == myFav) {
            footVeiw.setVisibility(View.VISIBLE);
            options = new OptionalService(getActivity()).queryAllMyOptional();
            mOptionalAdapter.setItems(options);
            layoutLoding.setVisibility(View.GONE);
            checkRefresh();
        } else {
            if (index == 1) {
                List<Optional> mainContinueOpt = new ArrayList<Optional>();
                for (int i = 1; i < exchangeList.size(); i++) {
                    mainContinueOpt.addAll(exchangeList.get(i).getOptionalsList());
                }
                options = mainContinueOpt;
            }
            layoutLoding.setVisibility(View.GONE);

            if (options != null) {
//                        initData(options);
                mOptionalAdapter.setItems(options);
            }
            checkRefresh();

        }
    }

    View selectView;

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        List<Exchange> goodsList;

        public MyAdapter(List<Exchange> goodsList) {
            this.goodsList = goodsList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View rootView = View.inflate(viewGroup.getContext(), R.layout.product_goods_item, null);
            return new MyViewHolder(rootView);
        }

        public void clear() {
            if (null != goodsList) {
                goodsList.clear();
                notifyDataSetChanged();
            }
        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {
            if (myViewHolder.textView == null)
                return;
            if (selectSource == i) {
                myViewHolder.textView.setSelected(true);
                selectView = myViewHolder.textView;
            } else {
                myViewHolder.textView.setSelected(false);
            }

            final int position = i;
            myViewHolder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectSource == position)
                        return;
                    if (selectView != null) {
                        selectView.setSelected(false);
                    }
                    selectView = v;
                    v.setSelected(true);
                    recyclerView.scrollToPosition(position);
                    mOptionalAdapter.clearGetMap();
                    loadDataByTags(position);

                    MyAppMobclickAgent.onEvent(getActivity(), "page_market_tab_source", ConvertUtil.NVL(goodsList.get(position).getExchangeName(), ""));

                }
            });

            myViewHolder.textView.setText(goodsList.get(i).getExchangeName());
        }

        @Override
        public int getItemCount() {
            return goodsList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textView;

            public MyViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.item_name);
            }

        }
    }

    /**
     * 当前选择的tab 可以长链接的codes
     *
     * @return
     */
    String getSpecialCodes() {
        if (mOptionalAdapter.getItems() != null) {
            return NettyUtil.getCodesSpecial(mOptionalAdapter.getItems());
        }
        return null;
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
                    CoreOptionUtil.getCoreOptionUtil(getActivity().getApplicationContext()).startQuotationMessage(getSpecialCodes());
                } else if (NettyUtil.TYPE_QP.equals(response.getType())) {
                    Optional optional = response.getData();
                    if (optional == null)
                        return;
                    //还没有初始化
                    if (options == null)
                        return;


                    /*List<Optional> newList = new ArrayList<Optional>();
                    for (int i = 0; i < options.size(); i++) {
                        if (options.get(i).getExchangeID().equals(optional.getExchangeID())
                                && options.get(i).getInstrumentID().equals(optional.getInstrumentID())) {
                            optional.setName(options.get(i).getName());
                            newList.add(optional);
                        } else {
                            newList.add(options.get(i));
                        }
                    }
                    options = newList;
                    mOptionalAdapter.setItems(options);*/
                    // 局部刷新
                    Log.e(TAG, "listView.getHeaderViewsCount()====" + listView.getHeaderViewsCount());
                    int visiblePosition = listView.getFirstVisiblePosition();
                    Log.e(TAG, "visiblePosition====" + visiblePosition);
//                Log.e(TAG,optional.getName()+"=====");
                    int visibleLastPosition = listView.getLastVisiblePosition();

                    //只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
                    int itemIndex = mOptionalAdapter.getOptionIndex(optional.getCode()) + listView.getHeaderViewsCount();
                    if (itemIndex - visiblePosition >= 0 && itemIndex < visibleLastPosition + listView.getHeaderViewsCount()) {
                        //得到要更新的item的view
                        View view = listView.getChildAt(itemIndex - visiblePosition);
//                    //调用adapter更新界面
//                    dateAdapter.updateView(view, itemIndex);

                        TextView sellingRate = (TextView) view.findViewById(R.id.selling_rate);
                        TextView changeRate = (TextView) view.findViewById(R.id.change_rate);
                        TextView change_chengjiaoliang = (TextView) view.findViewById(R.id.change_chengjiaoliang);
                        mOptionalAdapter.updateItem(sellingRate,changeRate,change_chengjiaoliang,optional);
                    }
                }
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        try {
            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
