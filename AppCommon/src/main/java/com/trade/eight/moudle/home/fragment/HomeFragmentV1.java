package com.trade.eight.moudle.home.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.config.OnLineHelper;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.config.WeipanConfig;
import com.trade.eight.dao.OptionalDao;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.RankItem;
import com.trade.eight.entity.RoomData;
import com.trade.eight.entity.home.HomeCalendar;
import com.trade.eight.entity.home.HomeInformation;
import com.trade.eight.entity.home.HomePagerItem;
import com.trade.eight.entity.live.LiveRoomNew;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.response.NettyResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.baksource.BakSourceService;
import com.trade.eight.moudle.chatroom.IndexLiveItemClickEvent;
import com.trade.eight.moudle.chatroom.activity.ChatRoomActivity;
import com.trade.eight.moudle.home.HomeInformationTimeDownEvent;
import com.trade.eight.moudle.home.HomeInformationUpOrDownClickEvent;
import com.trade.eight.moudle.home.activity.HomeNewsDetailActivity;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.home.adapter.HomeInformationAdapter;
import com.trade.eight.moudle.home.adapter.HomeLiveAdapter;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.netty.NettyUtil;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.product.OptionalEvent;
import com.trade.eight.moudle.product.activity.ProductActivity;
import com.trade.eight.moudle.timer.CountDownTask;
import com.trade.eight.mpush.coreoption.CoreOptionUtil;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.NetWorkUtils;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trude.HomeDataHelper;
import com.trade.eight.tools.AESUtil;
import com.trade.eight.tools.ActivityUtil;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.OpenActivityUtil;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.Utils;
import com.trade.eight.tools.product.ProductViewHold4Home;
import com.trade.eight.tools.trade.TradeConfig;
import com.trade.eight.view.AutoScrollViewPager;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 新版首页
 */
public class HomeFragmentV1 extends BaseFragment implements PullToRefreshBase.OnRefreshListener<ListView>, View.OnClickListener {


    String TAG = "HomeFragmentV1";

    PullToRefreshListView pullToRefreshListView = null;
    ListView listView = null;
    View headerView = null;
    AutoScrollViewPager autoViewPager = null;
    private ArrayList<View> dotViewList = new ArrayList<View>();
    View currentDotView = null;

    private View rl_home_newuser_classroom;// 新手学堂
    private View rl_home_newuser_gold;//盈利榜
    AutoScrollViewPager product_viewpager;
    LinearLayout dotlayout_product;
    private ArrayList<View> dotProductViewList = new ArrayList<View>();
    View currentDotProductView = null;
    View line_homelive;
    RecyclerView recycler_homelive;
    LinearLayoutManager linearLayoutManager = null;
    View line_golive;

    private View home_tab_tradechance;//交易机会
    private TextView tv_tradechance_more;
    /*首页开户入口*/
    View btn_join;
    ImageLoader imageLoader = null;

    HomeInformationAdapter homeInformationAdapter = null;
    CountDownTask mCountDownTask = null;

    int firstVisiblePosition, listViemItemTop;

    /*初始化的时候 是否显示登录的按钮*/
    boolean isShowToLoginLayout = false;
    long TIME_NEWS_REFRESH = 15 * 1000L;

    ProductViewHold4Home productViewHold4Home;

    //oncreate 中配置codes，会根据交易所列表然后 匹配字符串
    String codes = "";//首页的热门品种  如果启动的配置没获取到，会根据本地的交易所配置顺序拼接本地codes


    private HashMap<String, Double> checkChangeMap = new HashMap<String, Double>();

    SimpleDateFormat sdf_1 = new SimpleDateFormat("MM月dd日");

    boolean isVisible = false;

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
        EventBus.getDefault().register(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "onSaveInstanceState");
        outState.putBoolean("isHidden", isHidden());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_home_v1, null);
        imageLoader = ImageLoader.getInstance();
        Log.v(TAG, "onCreateView");
        codes = TradeConfig.getCurrentCodes((BaseActivity) getActivity());
        Log.v(TAG, "codes=" + codes);

        initTempAdapter();
        initViews(view);
        initProductViewPager();

        pullToRefreshListView.setLastUpdatedLabel(sdf.format(new Date()));
        return view;
    }

    @Override
    public void onFragmentVisible(boolean isVisible) {
        super.onFragmentVisible(isVisible);

        this.isVisible = isVisible;
        if (pullToRefreshListView == null)
            return;
        Log.v(TAG, "onFragmentVisible=" + isVisible);
        if (isVisible) {
            autoViewPager.startAutoScroll();
            //页面显示了 刷新一次数据
            initProData();
            checkData();
            if (currentDotView != null)
                currentDotView.setSelected(true);

            //检测邀请好友注册送积分功能
//            ShJoinHelp.checkFunJoin(getActivity());
        } else {
            autoViewPager.stopAutoScroll();
            // 长连接断开
            CoreOptionUtil.getCoreOptionUtil(getActivity().getApplicationContext()).stopQuotationMessage();
            Log.v(TAG, "onFragmentVisible=行情断开");
//            NettyClient.getInstance(getActivity()).stopWrite();
        }
    }

    @Override
    public void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
    }


    void initTempAdapter() {
        List<HomeCalendar> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HomeCalendar homeCalendar = new HomeCalendar();
            list.add(homeCalendar);
        }
    }

    void gotoWeipan() {
        if (!new UserInfoDao(getActivity()).isLogin()) {

            DialogUtil.showLoginConfirmDlg(getActivity(), "需要登录后才能使用", true, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    //跳转到登录页面
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(BaseInterface.TAB_MAIN_PARAME, MainActivity.WEIPAN);
                    startActivity(ActivityUtil.initAction(getActivity(), LoginActivity.class, ActivityUtil.ACT_MAIN, map));
                    return false;
                }
            });
        } else {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity.tabAdapter != null) {
                mainActivity.tabAdapter.setCurrentTabByTag(MainActivity.WEIPAN);
            }
        }
    }



    void initViews(View view) {
        //设置首页顶部显示文字
//        TextView tv_title = (TextView)view.findViewById(R.id.tv_title);
//        if (tv_title != null) {
//            tv_title.setText(AppSetting.getInstance(getActivity()).getAppMarket());
//        }
        btn_join = view.findViewById(R.id.btn_join);

        View weipanBtn = view.findViewById(R.id.weipanView);
        if (weipanBtn != null) {
            if (WeipanConfig.isShowWeipan(getActivity())) {
                weipanBtn.setVisibility(View.VISIBLE);
            } else {
                weipanBtn.setVisibility(View.GONE);
            }
            weipanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoWeipan();
                }
            });
        }
        view.findViewById(R.id.page_choose_view).setVisibility(View.GONE);
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setPullLoadEnabled(true);
        pullToRefreshListView.setPullRefreshEnabled(true);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setVisibility(View.VISIBLE);

        headerView = View.inflate(getActivity(), R.layout.home_header_view_v1, null);
        autoViewPager = (AutoScrollViewPager) headerView.findViewById(R.id.autoViewPager);
        rl_home_newuser_classroom = headerView.findViewById(R.id.rl_home_newuser_classroom);
        rl_home_newuser_classroom.setOnClickListener(this);

        rl_home_newuser_gold = headerView.findViewById(R.id.rl_home_newuser_gold);
        rl_home_newuser_gold.setOnClickListener(this);

        product_viewpager = (AutoScrollViewPager) headerView.findViewById(R.id.product_viewpager);
        dotlayout_product = (LinearLayout) headerView.findViewById(R.id.dotlayout_product);

        line_homelive = headerView.findViewById(R.id.line_homelive);
        recycler_homelive = (RecyclerView) headerView.findViewById(R.id.recycler_homelive);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler_homelive.setLayoutManager(linearLayoutManager);
        line_golive = headerView.findViewById(R.id.line_golive);
        line_golive.setOnClickListener(this);

        home_tab_tradechance = headerView.findViewById(R.id.home_tab_tradechance);
        home_tab_tradechance.setOnClickListener(this);

        tv_tradechance_more = (TextView) headerView.findViewById(R.id.tv_tradechance_more);
//        tv_tradechance_more.setText(DateUtil.getCurrentMMdd());
        listView.addHeaderView(headerView);

        listView.setDividerHeight(0);
        homeInformationAdapter = new HomeInformationAdapter(getActivity(), 0, new ArrayList<HomeInformation>());
        listView.setAdapter(homeInformationAdapter);
        startCountDown();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HomeInformation homeInformation = homeInformationAdapter.getItem(position - 1);
                String url = homeInformation.getArticleUrl();
                if (!TextUtils.isEmpty(url)) {
                    Map<String, String> params = new HashMap<String, String>();
                    UserInfoDao dao = new UserInfoDao(getActivity());
                    if (dao.isLogin()) {
                        params.put("userId", dao.queryUserInfo().getUserId());
                    }
                    params.put("informationId", homeInformation.getInformactionId());
                    params.put("articleId", homeInformation.getArticleId());
                    params.put(ApiConfig.PARAM_SOURCEID, AppSetting.APPSOURCE_TRADE + "");
                    String title = homeInformation.getInformationTypeName(homeInformation.getInformactionType());
                    //详情页的处理
                    HomeNewsDetailActivity.start(getActivity(), title, NetWorkUtils.setParam4get(url, params));
                    //点击一次算一次
                    String clickId = ConvertUtil.NVL(homeInformation.getArticleId(), "") + "_"
                            + title
                            + ConvertUtil.NVL(homeInformation.getInformactionProduct(), "") + "_"
                            + ConvertUtil.NVL(homeInformation.getInformactionAbstract(), "") + "_";
                    MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_HOME_ARTIC, clickId);
                }
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    firstVisiblePosition = listView.getFirstVisiblePosition();
                    View itemView = listView.getChildAt(0);
                    listViemItemTop = (itemView == null) ? 0 : itemView.getTop();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        View btn_help = view.findViewById(R.id.btn_help);
        if (btn_help != null)
            btn_help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_HOME, "客服");
                    OnLineHelper.getInstance().startP2p((BaseActivity) getActivity());
                }
            });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rl_home_newuser_classroom:// 新手学堂

                MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_HOME, "新手学堂");
                String string = "新手学堂";
                WebActivity.start(getActivity(), string, AndroidAPIConfig.URL_HOME_XSXT, true);
                break;
            case R.id.rl_home_newuser_gold:// 盈利(期货开户)
                MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_HOME, "期货开户");
//                HomeRankActivity.startHomeRankActivity(getActivity())
                OpenActivityUtil.openQiHuoAccountWelcome((BaseActivity) getActivity());
                break;
            case R.id.home_tab_tradechance://交易机会
                break;

            case R.id.line_golive:
                getActivity().startActivity(OpenActivityUtil.getIntent(getActivity(), OpenActivityUtil.SCHEME + OpenActivityUtil.ACT_LIVE_LIST));
                MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_HOME, "直播按钮");
                break;
        }
    }

    List<HomePagerItem> homePagerItemList = null;

    void initViewPager(List<HomePagerItem> pagerItemList) {
        if (pagerItemList == null || pagerItemList.size() == 0)
            return;
        List<View> mListViews = new ArrayList<>();
        for (int i = 0; i < pagerItemList.size(); i++) {
            mListViews.add(View.inflate(getActivity(), R.layout.home_top_pager_item, null));
        }
        autoViewPager.setAdapter(new MyViewPagerAdapter(mListViews, pagerItemList));

        autoViewPager.startAutoScroll();
        autoViewPager.setInterval(5000);//自动切换时间
        autoViewPager.setOnPageChangeListener(new MOnpagerChangeLister());

        LinearLayout dotlayout = (LinearLayout) headerView.findViewById(R.id.dotlayout);
        if (dotlayout != null) {
            dotlayout.removeAllViews();
            dotViewList.clear();
            dotlayout.setVisibility(View.VISIBLE);

            int width = Utils.dip2px(getActivity(), 12);
            int margin = Utils.dip2px(getActivity(), 2);
            int height = Utils.dip2px(getActivity(), 1);
            for (int i = 0; i < mListViews.size(); i++) {
                TextView textView = new TextView(getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                params.setMargins(margin, 0, margin, 0);
                textView.setLayoutParams(params);
                textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_top_pager_dot_bg));
                dotlayout.addView(textView);
                final int index = i;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        autoViewPager.setCurrentItem(index);
                    }
                });
                dotViewList.add(textView);
            }
        }
        currentDotView = dotViewList.get(0);
        currentDotView.setSelected(true);
        autoViewPager.setCurrentItem(0);
    }

    /**
     * 首页产品viewpager
     */
    void initProductViewPager() {

        List<View> mListViews = new ArrayList<>();
        String[] allProducts = codes.split(",");
        int count = allProducts.length / 3;
        int decentCount = allProducts.length % 3;
        if (decentCount != 0) {
            count += 1;
        }
        for (int i = 0; i < count; i++) {
            mListViews.add(View.inflate(getActivity(), R.layout.layout_homepropage_3, null));
        }

        product_viewpager.setAdapter(new MyProductViewPagerAdapter(mListViews));
        product_viewpager.setOnPageChangeListener(new ProductOnpagerChangeLister());
        if (dotlayout_product != null) {
            dotlayout_product.removeAllViews();
            dotProductViewList.clear();
            dotlayout_product.setVisibility(View.VISIBLE);

            int width = Utils.dip2px(getActivity(), 12);
            int margin = Utils.dip2px(getActivity(), 2);
            int height = Utils.dip2px(getActivity(), 1);
            for (int i = 0; i < mListViews.size(); i++) {
                TextView textView = new TextView(getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
                params.setMargins(margin, 0, margin, 0);
                textView.setLayoutParams(params);
                textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.home_top_pager_productdot_bg));
                dotlayout_product.addView(textView);
                final int index = i;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        product_viewpager.setCurrentItem(index);
                    }
                });
                dotProductViewList.add(textView);
            }
        }
        currentDotProductView = dotProductViewList.get(0);
        currentDotProductView.setSelected(true);
        product_viewpager.setCurrentItem(0);

        productViewHold4Home = new ProductViewHold4Home(mListViews, (BaseActivity) getActivity(), codes);
    }


    /**
     * 获取排行榜
     * 没有分页
     * 排行榜 不能根据第一个ID
     * 第一个id 是第一名 很可能不变
     */
    void checkData() {
        ispullToRefresh = false;
//        pageNo = 1;
        new GetInformationTask(true).execute();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//        MyAppMobclickAgent.onEvent(getActivity(), PAGE_EVENT, "onPullDownToRefresh");
        ispullToRefresh = true;
        pageNo = 1;
        new GetInformationTask().execute();
        initProData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo++;
        new GetInformationTask().execute();
    }

    /**
     * 首页滚动banner
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;
        List<HomePagerItem> pagerItemList;


        public MyViewPagerAdapter(List<View> mListViews, List<HomePagerItem> pagerItemList) {
            this.mListViews = mListViews;
            this.pagerItemList = pagerItemList;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            position = position % pagerItemList.size();
            container.removeView(mListViews.get(position));//删除页卡
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {    //这个方法用来实例化页卡

            final int mPos = position % pagerItemList.size();
            container.addView(mListViews.get(mPos), 0);//添加页卡

            View itemView = mListViews.get(mPos);
            ImageView item_img = (ImageView) itemView.findViewById(R.id.item_img);
            imageLoader.displayImage(pagerItemList.get(mPos).getImage_url(),
                    item_img, AppImageLoaderConfig.getCommonDisplayImageOptions(getActivity(), R.drawable.img_homebanner_default));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_HOME_BANNER, "首页banner_" + mPos + ConvertUtil.NVL(pagerItemList.get(mPos).getTitle(), ""));
                        currentDotView.setSelected(true);//第一次click 会失去selected效果，这里强制设置 true
                        String url = pagerItemList.get(mPos).getUrl();
                        if (url != null && url.startsWith(OpenActivityUtil.SCHEME_SUB)) {
                            startActivity(OpenActivityUtil.getIntent(getActivity(), url));
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), WebActivity.class);
                            intent.putExtra("url", url);
                            intent.putExtra("title", pagerItemList.get(mPos).getTitle());
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            return mListViews.get(mPos);
        }

        @Override
        public int getCount() {
            return mListViews.size();//返回页卡的数量
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;//官方提示这样写
        }
    }

    /**
     * 首页产品viewpager
     */
    public class MyProductViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyProductViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));//删除页卡
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {    //这个方法用来实例化页卡
            final int mPos = position % mListViews.size();
            container.addView(mListViews.get(mPos), 0);//添加页卡
            return mListViews.get(mPos);
        }

        @Override
        public int getCount() {
            return mListViews.size();//返回页卡的数量
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;//官方提示这样写
        }
    }

    /**
     * 首页banner监听
     */
    private class MOnpagerChangeLister implements
            ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int arg0) {
            if (dotViewList == null || dotViewList.size() == 0)
                return;
            arg0 = arg0 % dotViewList.size();

            if (arg0 < dotViewList.size()) {
                if (currentDotView != null)
                    currentDotView.setSelected(false);
                currentDotView = dotViewList.get(arg0);
                currentDotView.setSelected(true);
            }
        }
    }

    /**
     * 首页产品监听
     */
    private class ProductOnpagerChangeLister implements
            ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int arg0) {
            if (dotProductViewList == null || dotProductViewList.size() == 0)
                return;

            arg0 = arg0 % dotProductViewList.size();

            if (arg0 < dotProductViewList.size()) {
                if (currentDotProductView != null)
                    currentDotProductView.setSelected(false);
                currentDotProductView = dotProductViewList.get(arg0);
                currentDotProductView.setSelected(true);
            }
        }
    }

    //首页资讯
    List<HomeInformation> homeInformationList = null;
    int pageNo = 1;

    boolean ispullToRefresh = false;

    /**
     * 第一次加载数据
     */
    class GetInformationTask extends AsyncTask<String, Void, String> {
        boolean isCheckData = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public GetInformationTask() {

        }

        public GetInformationTask(boolean isCheckData) {
            this.isCheckData = isCheckData;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                int page = pageNo;
                if (isCheckData) {
                    page = 1;
                }
                CommonResponse4List<HomeInformation> commonResponse = HomeDataHelper.getHomeInformationList(getActivity(), page);
                if (commonResponse == null || !commonResponse.isSuccess()) {
                    return ERROR;
                }
                homeInformationList = commonResponse.getData();
                if (homeInformationList == null || homeInformationList.size() == 0) {
                    if (pageNo != 1) {
                        pageNo -= 1;
                    }
                    return "NODATA";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ERROR;
            }
            return SUCCESS;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if (!isAdded())
                return;
            if (isDetached())
                return;

            super.onPostExecute(result);

            if (pageNo == 1) {
                //just do refresh
                initViewPager();//不写在同一个线程里， 网络请求太多了会慢，数据获取完之后再请求头部广告
                initHomeLiveList();
//                new GetRankTask().execute();// 刷新盈利榜数据

            }
            HomeInformation firstNews = null;
            if (homeInformationAdapter.getCount() > 0) {
                firstNews = homeInformationAdapter.getItem(0);
            }
            pullToRefreshListView.setLastUpdatedLabel(sdf.format(new Date()));
            pullToRefreshListView.onPullDownRefreshComplete();
            pullToRefreshListView.onPullUpRefreshComplete();

            if (SUCCESS.equalsIgnoreCase(result)) {
//                listView.setAdapter(homeInformationAdapter);
                if (homeInformationList == null) {
                    ispullToRefresh = false;
                    return;
                }
                boolean hasNewData = false;
                if (isCheckData) {
                    if (firstNews != null && homeInformationList.size() > 0) {
                        if (TextUtils.isEmpty(homeInformationList.get(0).getInformactionId())) {
                            return;
                        }
                        if (homeInformationList.get(0).getInformactionId().equals(firstNews.getInformactionId())) {
                            hasNewData = false;
                        } else {
                            hasNewData = true;
                        }
                    }
                }
                if (hasNewData || pageNo == 1) {
                    cancleCountDown();
                    startCountDown();
                    if (homeInformationList.get(0).getTop() > 0) {
                        HomeInformation homeInformation = homeInformationList.get(0);
                        homeInformation.setCreateTime(DateUtil.changeDateYMD(homeInformation.getCreateTime()));
                        homeInformationList.set(0, homeInformation);
                        tv_tradechance_more.setText(sdf_1.format(new Date(homeInformation.getCreateTime())));
                    } else {
                        tv_tradechance_more.setText(sdf_1.format(new Date(homeInformationList.get(0).getCreateTime())));
                    }
                    homeInformationAdapter.setDataList(homeInformationList);
                } else {
                    if (isCheckData) {
                        return;
                    }
                    cancleCountDown();
                    startCountDown();
                    homeInformationAdapter.addDataList(homeInformationList);
                }

                //如果有新数据 滑动到最顶上
                if (ispullToRefresh || hasNewData) {
                    listView.setSelection(0);
                    listView.requestFocus();
                    firstVisiblePosition = listView.getFirstVisiblePosition();
                    View itemView = listView.getChildAt(0);
                    listViemItemTop = (itemView == null) ? 0 : itemView.getTop();
                } else {
//                    firstVisiblePosition = listView.getFirstVisiblePosition();
//                    View itemView = listView.getChildAt(0);
//                    listViemItemTop = (itemView == null) ? 0 : itemView.getTop();
                    listView.setSelectionFromTop(firstVisiblePosition, listViemItemTop);

                }
            } else {
                //如果是检测数据更新的刷新就不用 toast
                if (isCheckData)
                    return;
                if ("NODATA".equals(result)) {
                    if (homeInformationAdapter.getCount() != 0) {
                        showCusToast(getActivity().getResources().getString(R.string.data_no_more));
                    } else {
                        showCusToast(getActivity().getResources().getString(R.string.data_empty));
                    }
                } else
                    showCusToast(getActivity().getResources().getString(R.string.network_problem));
            }
            ispullToRefresh = false;
        }
    }

    void initViewPager() {
        HashMap<String, String> request = new HashMap<>();
        request.put("callType", HomePagerItem.REQUEST_HOME);
        String url = AndroidAPIConfig.URL_ADS_4HOMEANDLIVE;
        HttpClientHelper.doPostOption((BaseActivity) getActivity(), url, request, null, new NetCallback((BaseActivity) getActivity()) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {

            }

            @Override
            public void onResponse(String response) {
                CommonResponse4List<HomePagerItem> commonResponse4List = CommonResponse4List.fromJson(response, HomePagerItem.class);
                homePagerItemList = commonResponse4List.getData();
                if (homePagerItemList != null) {
                    initViewPager(homePagerItemList);
                }

                View topPagerLayout = headerView.findViewById(R.id.topPagerLayout);
                if (topPagerLayout != null && autoViewPager.getAdapter() != null)
                    topPagerLayout.setVisibility(View.VISIBLE);
            }
        }, false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    View contentView;// 当前操作的item
    HomeInformation homeInformation;// 当前操作的data
    int clickType;// 支持利多or利空
    int index;// 操作的索引

    /**
     * 用户支持利多利空操作
     *
     * @param event
     */
    public void onEventMainThread(HomeInformationUpOrDownClickEvent event) {

        contentView = event.contentView;
        homeInformation = event.homeInformation;
        clickType = event.clickType;
        index = event.idnex;
        UserInfoDao dao = new UserInfoDao(getActivity());

        if (!dao.isLogin()) {
            showCusToast("请您先登录再执行此操作");
            return;
        }
        if (homeInformation.getClickType() != 0) {
            showCusToast("不能重复操作哦");
            return;
        }
        new HomeInformationUpOrDownClickTask().execute();
    }

    class HomeInformationUpOrDownClickTask extends AsyncTask<String, Void, CommonResponse<HomeInformation>> {

        @Override
        protected CommonResponse<HomeInformation> doInBackground(String... params) {
            return HomeDataHelper.operationInsertHomeInformation(getActivity(), clickType, 1, homeInformation.getInformactionId());
        }

        @Override
        protected void onPostExecute(CommonResponse<HomeInformation> homeInformationCommonResponse) {
            super.onPostExecute(homeInformationCommonResponse);
            if (homeInformationCommonResponse != null) {

                if (homeInformationCommonResponse.isSuccess()) {
//                    showCusToast(getActivity().getResources().getString(R.string.operation_success));

                    homeInformation.setMore(homeInformationCommonResponse.getData().getMore());
                    homeInformation.setLess(homeInformationCommonResponse.getData().getLess());
                    homeInformation.setClickType(clickType);// 防止重复点击
                    // 局部刷新
                    final HomeInformationAdapter.ViewHolderType_3 viewHolderType_3 = (HomeInformationAdapter.ViewHolderType_3) contentView.getTag();
                    int tradeUpPercent = ConvertUtil.calculateTwoIntPercent(homeInformation.getMore(), homeInformation.getLess());
                    viewHolderType_3.tv_homeinformation_redhold_percent.setText(homeInformationAdapter.getHoldPercent(R.string.home_information_redhold, tradeUpPercent));
                    viewHolderType_3.tv_homeinformation_greenhold_percent.setText(homeInformationAdapter.getHoldPercent(R.string.home_information_greenhold, 100 - tradeUpPercent));

                    Animation animation = new TranslateAnimation(
                            0,// 动画开始的点离当前View X坐标上的差值
                            0,// 动画结束的点离当前View X坐标上的差值
                            0,//动画开始的点离当前View Y坐标上的差值
                            -50);//动画开始的点离当前View Y坐标上的差值
                    animation.setDuration(1000);
                    animation.setFillAfter(true);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            viewHolderType_3.text_homeinformation_red.clearAnimation();
                            viewHolderType_3.text_homeinformation_red.setVisibility(View.INVISIBLE);
                            viewHolderType_3.text_homeinformation_green.clearAnimation();
                            viewHolderType_3.text_homeinformation_green.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    if (homeInformation.getClickType() == 1) {// 点过后 对面变灰色
                        viewHolderType_3.btn_homeinformation_red.setBackgroundResource(R.drawable.btn_homeinformation_red);
                        viewHolderType_3.btn_homeinformation_red.setSelected(true);
                        viewHolderType_3.btn_homeinformation_green.setBackgroundResource(R.drawable.img_homeinformation_grey);
                        viewHolderType_3.btn_homeinformation_green.setSelected(false);
                        viewHolderType_3.text_homeinformation_red.setVisibility(View.VISIBLE);
                        viewHolderType_3.text_homeinformation_red.startAnimation(animation);
                    } else if (homeInformation.getClickType() == 2) {// 点过后 对面变灰色
                        viewHolderType_3.btn_homeinformation_red.setBackgroundResource(R.drawable.img_homeinformation_grey);
                        viewHolderType_3.btn_homeinformation_red.setSelected(false);
                        viewHolderType_3.btn_homeinformation_green.setBackgroundResource(R.drawable.btn_homeinformation_green);
                        viewHolderType_3.btn_homeinformation_green.setSelected(true);
                        viewHolderType_3.text_homeinformation_green.setVisibility(View.VISIBLE);
                        viewHolderType_3.text_homeinformation_green.startAnimation(animation);
                    }
                    homeInformationAdapter.getList().set(index, homeInformation);//改变数据 防止重复点击
                } else {
                    String errerInfo = ConvertUtil.NVL(homeInformationCommonResponse.getErrorInfo(), getActivity().getResources().getString(R.string.network_problem));
                    showCusToast(errerInfo);
                }
            } else {
                showCusToast(getActivity().getResources().getString(R.string.network_problem));
            }
        }
    }

    private void startCountDown() {
        mCountDownTask = CountDownTask.create();
        homeInformationAdapter.setCountDownTask(mCountDownTask);
    }

    private void cancleCountDown() {
        homeInformationAdapter.setCountDownTask(null);
        mCountDownTask.cancel();
    }


    class GetRankTask extends AsyncTask<String, Void, CommonResponse4List<RankItem>> {

        public GetRankTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected CommonResponse4List<RankItem> doInBackground(String... params) {

            try {
                Map<String, String> map = new HashMap<>();
                int page = 1;

                map.put("page", page + "");
                map = ApiConfig.getParamMap(getActivity(), map);
                //不加参数
                String res = HttpClientHelper.getStringFromPost(getActivity(), AndroidAPIConfig.URL_PROFIT_RANK, map);
                CommonResponse4List<RankItem> commonResponse = CommonResponse4List.fromJson(res, RankItem.class);
                return commonResponse;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(CommonResponse4List<RankItem> response4List) {
            super.onPostExecute(response4List);
            if (isDetached())
                return;
            pullToRefreshListView.onPullDownRefreshComplete();
            pullToRefreshListView.onPullUpRefreshComplete();

            if (response4List != null) {


                if (response4List.getData() == null || response4List.getData().size() == 0) {

                } else {
                    RankItem rankItem = response4List.getData().get(0);
                }

            } else {
                showCusToast(getResources().getString(R.string.network_problem));
            }

        }
    }

    /**
     * 行情预演倒计时结束  列表刷新
     *
     * @param event
     */
    public void onEventMainThread(HomeInformationTimeDownEvent event) {
        checkData();
    }
    /*华丽分割线 首页产品刷新********************************/

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
                    CoreOptionUtil.getCoreOptionUtil(getActivity().getApplicationContext()).startQuotationMessage(codes);
                } else if (NettyUtil.TYPE_QP.equals(response.getType())) {
                    Optional optional = response.getData();
                    if (optional == null || productViewHold4Home == null)
                        return;
                    productViewHold4Home.updateProductViewListDisplay(optional);
                }
            }
        });
    }

    /**
     * 初始化热门产品
     */
    void initProData() {
        new AsyncTask<Void, Void, List<Optional>>() {
            @Override
            protected List<Optional> doInBackground(Void... params) {
                try {
                    return BakSourceService.getOptionals(getActivity(), codes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(List<Optional> list) {
                super.onPostExecute(list);
                if (!isAdded())
                    return;
                if (isDetached())
                    return;
                if (list != null) {
                    initProLayout(list);
                }
                //
//                if (NettyClient.getInstance(getActivity()).isInited())
//                    NettyClient.getInstance(getActivity()).write(codes);
                CoreOptionUtil.getCoreOptionUtil(getActivity().getApplicationContext()).startQuotationMessage(codes);
            }
        }.execute();
    }

    /**
     * 初始化界面
     *
     * @param list
     */
    void initProLayout(List<Optional> list) {
        if (checkChangeMap.size() == 0) {
            for (Optional optional : list) {
                checkChangeMap.put(optional.getCode(), Double.parseDouble(optional.getLastPrice()));
                if (!PreferenceSetting.getBoolean(getActivity(), PreferenceSetting.ISINITZIXUAN)) {
                    // 将热门产品加进自选
                    optional.setOptional(true);
                    new OptionalDao(getActivity()).updateCount(optional);
                }
            }
            PreferenceSetting.setBoolean(getActivity(), PreferenceSetting.ISINITZIXUAN, true);
        }
        productViewHold4Home.setCheckChangeMap(checkChangeMap);
        for (final Optional optional : list) {
            productViewHold4Home.updateProductViewListDisplay(optional);
            // 添加点击事件
            productViewHold4Home.listProductView
                    .get(optional.getCode())
                    .rl_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_HOME, "热门产品_" + optional.getTitle());

                    Intent intent = new Intent(getActivity(), ProductActivity.class);
                    intent.putExtra("excode", optional.getExchangeID());
                    intent.putExtra("code", optional.getInstrumentID());
                    startActivity(intent);
                }
            });
        }
    }
    /******************华丽分割线 首页产品刷新**************/


    /*********************************/

    void initHomeLiveList() {
        HttpClientHelper.doPostOption((BaseActivity) getActivity(),
                AndroidAPIConfig.URL_LIVE_LIST_V4,
                null,
                null,
                new NetCallback((BaseActivity) getActivity()) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        line_homelive.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response) {
                        CommonResponse4List<LiveRoomNew> liveRoomNewCommonResponse4List = CommonResponse4List.fromJson(response, LiveRoomNew.class);
                        List<LiveRoomNew> liveRoomNews = liveRoomNewCommonResponse4List.getData();
                        if (liveRoomNews != null && liveRoomNews.size() > 0) {
                            line_homelive.setVisibility(View.VISIBLE);
                            List<LiveRoomNew> list = new ArrayList<LiveRoomNew>();
                            for (int i = 0; i < liveRoomNews.size(); i++) {
                                LiveRoomNew liveRoomNew = liveRoomNews.get(i);
                                // 只展示没有隐藏 视频直播室 以及免费直播室
                                if (liveRoomNew.getHidden() == LiveRoomNew.ISHIDDEN_NO && liveRoomNew.getIsPay() == LiveRoomNew.ISPAY_NO && liveRoomNew.getChannelType() == LiveRoomNew.CHANNELTYPE_VIDEO) {
                                    list.add(liveRoomNew);
                                }
                            }
                            HomeLiveAdapter homeLiveAdapter = new HomeLiveAdapter(getActivity(), list);
                            recycler_homelive.setAdapter(homeLiveAdapter);
                            recycler_homelive.setItemViewCacheSize(homeLiveAdapter.getItemCount());
                        } else {
                            line_homelive.setVisibility(View.GONE);
                        }
                    }
                },
                false);
    }

    LiveRoomNew item;

    /**
     * 选择要进入的房间
     *
     * @param event
     */
    public void onEventMainThread(IndexLiveItemClickEvent event) {
        if (!isVisible) {
            return;
        }
        //必须登录
        if (!new UserInfoDao(getActivity()).isLogin()) {
            DialogUtil.showLoginConfirmDlg(getActivity(), "直播需要登录后才能观看", true, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return false;
                }
            });
            return;
        }
        //首先获取数据接口 检查是否为第一次 进直播，需要输入昵称
        item = event.liveRoomNew;
        if (item.getSegmentModel() != null) {
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_HOME_TEACHER, item.getSegmentModel().getAuthorName());
        }

        new GetDataTask(true).execute();
    }

    Dialog dialog = null;

    void hideDialog() {
        if (dialog != null)
            dialog.dismiss();
    }


    /**
     * get room data
     * <p>
     * 如果房间信息返回 需要输入 昵称的错误 就需要注册一下云信
     */
    class GetDataTask extends AsyncTask<String, Void, CommonResponse<RoomData>> {
        boolean isCallBack;

        public GetDataTask(boolean isCallBack) {
            this.isCallBack = isCallBack;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = DialogUtil.getLoadingDlg(getActivity(), null);
            dialog.show();
            try {
                //必须先判断是否登录了云信，才能执行注销操作，不然云信会出错
                if (NIMClient.getStatus() == StatusCode.LOGINED) {
                    //已经登录了云信了
                    if (new UserInfoDao(getActivity()).isLogin()) {
                        String uid = new UserInfoDao(getActivity()).queryUserInfo().getUserId();
                        if (!OnLineHelper.isSameAccount(getActivity(), uid)) {
                            //认为是和客服聊天的云信id登录
                            OnLineHelper.logoutCurrentAccount(getActivity());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected CommonResponse<RoomData> doInBackground(String... params) {
            try {
                if (!new UserInfoDao(getActivity()).isLogin()) {
                    return null;
                }
                Map<String, String> stringMap = ApiConfig.getCommonMap(getActivity());
                stringMap.put(UserInfo.UID, new UserInfoDao(getActivity()).queryUserInfo().getUserId());
                stringMap.put(ApiConfig.PARAM_TIME, System.currentTimeMillis() + "");
                stringMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(getActivity(), stringMap));
                //这里一定不要使用get请求，get请求有时候出现服务端验证失败，可能是空格转义的问题
                String str = HttpClientHelper.getStringFromPost(getActivity(), AndroidAPIConfig.URL_CHATROOM_ROOMID, stringMap);
                if (str != null) {
                    return CommonResponse.fromJson(str, RoomData.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CommonResponse<RoomData> res) {
            super.onPostExecute(res);
            if (isDetached())
                return;
            if (!isAdded())
                return;
            hideDialog();
            if (res != null) {
                //已经注册过
                if (res.isSuccess()) {
                    RoomData data = res.getData();
                    if (data == null)
                        return;
                    //保存token
                    UserInfoDao dao = new UserInfoDao(getActivity());
                    UserInfo userInfo = dao.queryUserInfo();
                    if (userInfo != null) {
                        try {
                            userInfo.setAccId(AESUtil.decrypt(data.getAccId()));
                            userInfo.setToken_IM(AESUtil.decrypt(data.getToken()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dao.addOrUpdate(userInfo);
                    }
//                    if (isCallBack)
//                        login(data);
                    enterRoom();
                } else if (ApiConfig.ERROR_CODE_NOT_REG.equals(res.getErrorCode())) {
                    //没有注册过，注册云信 ，同时返回房间信息和token
                    //客户端弹出输入昵称dialog 输入昵称之后再 进入房间
                    showDialogInput(getActivity(), false);


                } else {
                    showCusToast(ConvertUtil.NVL(res.getErrorInfo(), "信息获取失败"));
                }

            } else {
                showCusToast("信息获取失败");

            }

        }
    }

    /**
     * 输入昵称
     *
     * @param activity
     * @param isCallBack 是否跳转
     * @return
     */
    public Dialog showDialogInput(final Activity activity, final boolean isCallBack) {
        final Dialog dialog = new Dialog(activity, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.app_input_dialog);
        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        params.width = (int) (screenWidth * 0.8);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText editText = (EditText) dialog.findViewById(R.id.editText);
        final Button btnPos = (Button) dialog.findViewById(R.id.btnPos);
        btnPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nikName = editText.getText().toString();
                if (!StringUtil.isNickName(getActivity(), nikName)) {
                    return;
                }

                dialog.dismiss();
                new RegTask().execute(nikName);
            }
        });
        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }

    class RegTask extends AsyncTask<String, Void, CommonResponse<RoomData>> {
        //        boolean isCallBack;
//
//        public RegTask(boolean tag) {
//            isCallBack = tag;
//        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = DialogUtil.getLoadingDlg(getActivity(), null);
            dialog.show();
        }

        String userNameNew;

        @Override
        protected CommonResponse<RoomData> doInBackground(String... params) {
            try {
                if (!new UserInfoDao(getActivity()).isLogin()) {
                    return null;
                }
                UserInfo userInfo = new UserInfoDao(getActivity()).queryUserInfo();
                Map<String, String> stringMap = ApiConfig.getCommonMap(getActivity());
                stringMap.put(UserInfo.UID, userInfo.getUserId());
//                stringMap.put("name", ConvertUtil.NVL(userInfo.getNickName(), ConvertUtil.NVL(userInfo.getUserName(), userInfo.getMobileNum())));
                userNameNew = params[0];
                stringMap.put("name", userNameNew);

                stringMap.put(ApiConfig.PARAM_TIME, System.currentTimeMillis() + "");
                stringMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(getActivity(), stringMap));
                String str = HttpClientHelper.getStringFromPost(getActivity(), AndroidAPIConfig.URL_CHATROOM_UPDATE_NAME, stringMap);
                if (str != null) {
                    return CommonResponse.fromJson(str, RoomData.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CommonResponse<RoomData> res) {
            super.onPostExecute(res);
            if (isDetached())
                return;
            if (!isAdded())
                return;
            hideDialog();
            if (res != null) {
                //成功
                if (res.isSuccess()) {
                    RoomData data = res.getData();
                    if (data == null)
                        return;
                    //保存token
                    UserInfoDao dao = new UserInfoDao(getActivity());
                    UserInfo userInfo = dao.queryUserInfo();
                    if (userInfo != null) {
                        try {
                            userInfo.setAccId(AESUtil.decrypt(data.getAccId()));
                            userInfo.setToken_IM(AESUtil.decrypt(data.getToken()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //注册成功需要更新本地用户信息
                        userInfo.setUserName(userNameNew);
                        dao.addOrUpdate(userInfo);
                    }
                    enterRoom();

                } else {
                    showCusToast(ConvertUtil.NVL(res.getErrorInfo(), "信息获取失败"));
                }

            } else {
                showCusToast("信息获取失败");
            }
        }
    }

    /**
     * 进入直播室去
     */
    void enterRoom() {

        ChatRoomActivity.start(getActivity(), item);

    }
}
