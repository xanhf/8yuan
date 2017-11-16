package com.trade.eight.moudle.home.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.RankItem;
import com.trade.eight.entity.RankMostNewData;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.moudle.home.adapter.HomeRankAdapterV1;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.trade.ShareOrderEvent;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trude.HomeDataHelper;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.Utils;
import com.trade.eight.view.CircleImageView;
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
 * 作者：Created by ocean
 * 时间：on 16/10/21.
 * 盈利榜
 * <p>
 * 交易日  1 没有晒单超过3个 头部展示虚位以待
 * 非交易日 1 头部展示休市  2  底部消失
 */

public class HomeRankActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ListView>, View.OnClickListener {
    PullToRefreshListView pullToRefreshListView = null;
    ListView listView = null;
    HomeRankAdapterV1 homeRankAdapter = null;
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    protected SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd");
    int rankPage = 1;

    Button btn_homerank_tips;

    View headViewWithRank;
    RelativeLayout rl_rankhead;
    RelativeLayout rl_rankhead_empty;

    TextView tv_homerank_time;
    RelativeLayout line_homerank_first;
    ImageView img_homerank_first_head;
    TextView tv_homerank_first_name;
    TextView tv_homerank_first_rate;
    TextView tv_homerank_first_voucher;
    RelativeLayout line_homerank_second;
    ImageView img_homerank_second_head;
    TextView tv_homerank_second_name;
    TextView tv_homerank_second_rate;
    TextView tv_homerank_second_voucher;
    RelativeLayout line_homerank_third;
    ImageView img_homerank_third_head;
    TextView tv_homerank_third_name;
    TextView tv_homerank_third_rate;
    TextView tv_homerank_third_voucher;
    TextView tv_rankhead_empty;

    RelativeLayout rela_homerank_bottomshare;
    LinearLayout line_homerank_notin;// 没人晒单 底部条
    CircleImageView img_homerank_notin;
    LinearLayout line_homerank_alreadyin;// 有人晒单 底部条
    RelativeLayout rela_homerank_mostnewone;
    CircleImageView img_homerank_mostnewone;
    RelativeLayout rela_homerank_mostnewtwo;
    CircleImageView img_homerank_mostnewtwo_animation;
    RelativeLayout rela_homerank_mostnewtwo_animation;
    CircleImageView img_homerank_mostnewtwo;
    View view_whirecirlce;
    View view_whirecirlce_two;
    RelativeLayout rela_homerank_mostnewthree;
    CircleImageView img_homerank_mostnewthree;
    RelativeLayout rela_homerank_mostnewthree_animation;
    CircleImageView img_homerank_mostnewthree_animation;
    Button btn_homerank_shareorder;
    CircleImageView img_homerank_myself;
    TextView tv_homerank_myrank;
    TextView tv_homerank_sharecount;
    LinearLayout line_homerank_notlogin;//尚未登录底部条
    Button btn_homerank_gologin;

    RankMostNewData rankMostNewDataHistory = null;
    int firstVisiblePosition, listViemItemTop;

    RelativeLayout backLayout;

    public static void startHomeRankActivity(Context context) {
        Intent intent = new Intent(context, HomeRankActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homerank);
        initViews();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(ShareOrderEvent event) {
        onPullDown(true);
    }

    @Override
    public void onResume() {
        super.onResume();
//        pullToRefreshListView.setLastUpdatedLabel(sdf.format(new Date()));
//        onPullDown(false);

        // 最新晒单数据
//        new GetRankMostNewData().execute();
    }

    void initViews() {
        setAppCommonTitle(ConvertUtil.NVL(getIntent().getStringExtra("title"), "盈利榜"));
        backLayout = (RelativeLayout) findViewById(R.id.backLayout);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setPullLoadEnabled(true);
        pullToRefreshListView.setPullRefreshEnabled(true);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setVisibility(View.VISIBLE);
        listView.setDividerHeight(0);

        btn_homerank_tips = (Button) findViewById(R.id.btn_homerank_tips);
        btn_homerank_tips.setOnClickListener(HomeRankActivity.this);


        headViewWithRank = View.inflate(this, R.layout.layout_homerank_head, null);
        rl_rankhead = (RelativeLayout) headViewWithRank.findViewById(R.id.rl_rankhead);
        rl_rankhead_empty = (RelativeLayout) headViewWithRank.findViewById(R.id.rl_rankhead_empty);

        tv_homerank_time = (TextView) headViewWithRank.findViewById(R.id.tv_homerank_time);
        tv_homerank_time.setText(sdf.format(new Date()));
        line_homerank_first = (RelativeLayout) headViewWithRank.findViewById(R.id.line_homerank_first);
        img_homerank_first_head = (ImageView) headViewWithRank.findViewById(R.id.img_homerank_first_head);
        tv_homerank_first_name = (TextView) headViewWithRank.findViewById(R.id.tv_homerank_first_name);
        tv_homerank_first_rate = (TextView) headViewWithRank.findViewById(R.id.tv_homerank_first_rate);
        tv_homerank_first_voucher = (TextView) headViewWithRank.findViewById(R.id.tv_homerank_first_voucher);
        line_homerank_second = (RelativeLayout) headViewWithRank.findViewById(R.id.line_homerank_second);
        img_homerank_second_head = (ImageView) headViewWithRank.findViewById(R.id.img_homerank_second_head);
        tv_homerank_second_name = (TextView) headViewWithRank.findViewById(R.id.tv_homerank_second_name);
        tv_homerank_second_rate = (TextView) headViewWithRank.findViewById(R.id.tv_homerank_second_rate);
        tv_homerank_second_voucher = (TextView) headViewWithRank.findViewById(R.id.tv_homerank_second_voucher);
        line_homerank_third = (RelativeLayout) headViewWithRank.findViewById(R.id.line_homerank_third);
        img_homerank_third_head = (ImageView) headViewWithRank.findViewById(R.id.img_homerank_third_head);
        tv_homerank_third_name = (TextView) headViewWithRank.findViewById(R.id.tv_homerank_third_name);
        tv_homerank_third_rate = (TextView) headViewWithRank.findViewById(R.id.tv_homerank_third_rate);
        tv_homerank_third_voucher = (TextView) headViewWithRank.findViewById(R.id.tv_homerank_third_voucher);
        tv_rankhead_empty = (TextView) headViewWithRank.findViewById(R.id.tv_rankhead_empty);
        line_homerank_first.setOnClickListener(this);
        line_homerank_second.setOnClickListener(this);
        line_homerank_third.setOnClickListener(this);

        // 带123名的头部
        listView.addHeaderView(headViewWithRank);
        homeRankAdapter = new HomeRankAdapterV1(this, 0, new ArrayList<RankItem>());
        listView.setAdapter(homeRankAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                RankItem item = homeRankAdapter.getItem(position - 1);
                startHomeRankDetailAct(item);
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


        rela_homerank_bottomshare = (RelativeLayout) findViewById(R.id.rela_homerank_bottomshare);
        line_homerank_notin = (LinearLayout) findViewById(R.id.line_homerank_notin);
        img_homerank_notin = (CircleImageView) findViewById(R.id.img_homerank_notin);
        line_homerank_alreadyin = (LinearLayout) findViewById(R.id.line_homerank_alreadyin);
        // 底部信息
        img_homerank_mostnewone = (CircleImageView) findViewById(R.id.img_homerank_mostnewone);
        img_homerank_mostnewtwo_animation = (CircleImageView) findViewById(R.id.img_homerank_mostnewtwo_animation);
        img_homerank_mostnewtwo = (CircleImageView) findViewById(R.id.img_homerank_mostnewtwo);
        img_homerank_mostnewthree = (CircleImageView) findViewById(R.id.img_homerank_mostnewthree);
        img_homerank_mostnewthree_animation = (CircleImageView) findViewById(R.id.img_homerank_mostnewthree_animation);
        btn_homerank_shareorder = (Button) findViewById(R.id.btn_homerank_shareorder);
        btn_homerank_shareorder.setOnClickListener(this);

        rela_homerank_mostnewone = (RelativeLayout) findViewById(R.id.rela_homerank_mostnewone);
        rela_homerank_mostnewtwo = (RelativeLayout) findViewById(R.id.rela_homerank_mostnewtwo);
        rela_homerank_mostnewtwo_animation = (RelativeLayout) findViewById(R.id.rela_homerank_mostnewtwo_animation);
        rela_homerank_mostnewthree_animation = (RelativeLayout) findViewById(R.id.rela_homerank_mostnewthree_animation);
        rela_homerank_mostnewthree = (RelativeLayout) findViewById(R.id.rela_homerank_mostnewthree);

        img_homerank_myself = (CircleImageView) findViewById(R.id.img_homerank_myself);
        tv_homerank_myrank = (TextView) findViewById(R.id.tv_homerank_myrank);
        tv_homerank_sharecount = (TextView) findViewById(R.id.tv_homerank_sharecount);
        line_homerank_notlogin = (LinearLayout) findViewById(R.id.line_homerank_notlogin);
        btn_homerank_gologin = (Button) findViewById(R.id.btn_homerank_gologin);
        btn_homerank_gologin.setOnClickListener(this);

        pullToRefreshListView.setLastUpdatedLabel(sdf.format(new Date()));
        onPullDown(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.line_homerank_first:
                RankItem rankItemFirst = (RankItem) v.getTag();
                startHomeRankDetailAct(rankItemFirst);
                break;
            case R.id.line_homerank_second:
                RankItem rankItemSecond = (RankItem) v.getTag();
                startHomeRankDetailAct(rankItemSecond);
                break;
            case R.id.line_homerank_third:
                RankItem rankItemThird = (RankItem) v.getTag();
                startHomeRankDetailAct(rankItemThird);
                break;
            case R.id.btn_homerank_tips:
                showPopWindown();
                break;
            case R.id.btn_homerank_shareorder:
                HomeRankProfitOrderActivity.startHomeRankProfitOrderActivity(this);
                break;
            case R.id.btn_homerank_gologin:
                startActivity(new Intent(HomeRankActivity.this, LoginActivity.class));
                break;
        }
    }

    void onPullDown(boolean isCheckData) {
        rankPage = 1;
        new GetRankTask(isCheckData).execute();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        onPullDown(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        rankPage += 1;
        new GetRankTask().execute();
    }

    private List<RankItem> rankTopHeadList = new ArrayList<RankItem>();


    /**
     * 获取排行榜
     * 没有分页
     * 排行榜 不能根据第一个ID
     * 第一个id 是第一名 很可能不变
     */
    class GetRankTask extends AsyncTask<String, Void, CommonResponse4List<RankItem>> {

        boolean isCheckData = false;

        public GetRankTask(boolean isCheckData) {
            this.isCheckData = isCheckData;
        }

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
                int page = rankPage;
                if (isCheckData) {
                    page = 1;
                }
                map.put("page", page + "");
                map = ApiConfig.getParamMap(HomeRankActivity.this, map);
                //不加参数
                String res = HttpClientHelper.getStringFromPost(HomeRankActivity.this, AndroidAPIConfig.URL_PROFIT_RANK_V1, map);
                CommonResponse4List<RankItem> commonResponse = CommonResponse4List.fromJson(res, RankItem.class);
                if (commonResponse != null && commonResponse.isSuccess()) {
                    Log.e("GetRankTask", "commonResponse==" + commonResponse.getData().size());

                } else {
                    Log.e("GetRankTask", "commonResponse==null");
                }
                return commonResponse;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(CommonResponse4List<RankItem> response4List) {
            super.onPostExecute(response4List);
            if (isFinishing())
                return;
            tv_homerank_time.setText(sdf.format(new Date()));
            pullToRefreshListView.onPullDownRefreshComplete();
            pullToRefreshListView.onPullUpRefreshComplete();

            if (response4List != null) {
                if (isCheckData) {
                    homeRankAdapter.clear();
                    rankPage = 1;
                }


                if (response4List.getData() == null || response4List.getData().size() == 0) {
                    if (rankPage == 1) {
                        listView.setAdapter(homeRankAdapter);
                        showCusToast(getResources().getString(R.string.data_empty));
                    }
                    return;
                }

                if (rankPage == 1) {
                    if (homeRankAdapter.getCount() > 0) {

                        RankItem firstItem = homeRankAdapter.getItem(0);
                        if (rankTopHeadList.size() == 3) {
                            firstItem = rankTopHeadList.get(0);
                        }
                        if (firstItem != null && firstItem.getOrderId() == response4List.getData().get(0).getOrderId()) {
                            // 最新晒单数据
                            new GetRankMostNewData().execute();
                            return;
                        } else {
                            homeRankAdapter.clear();
                        }
                    }
                }
                // 给拿到数据排名
                for (int i = 0; i < response4List.getData().size(); i++) {
                    RankItem item = response4List.getData().get(i);
                    if (i == 0) {
                        //第一页
                        if (rankPage == 1) {
                            item.setIndex(i);
                        } else {
                            //第二页以后
                            if (homeRankAdapter.getCount() > 0) {
                                RankItem rankItemBefore = (RankItem) homeRankAdapter.getItem(homeRankAdapter.getCount() - 1);
                                if (item.getCloseDate().equals(rankItemBefore.getCloseDate())) {
                                    item.setIndex(rankItemBefore.getIndex() + 1);
                                } else {
                                    item.setIndex(0);
                                }
                            }
                        }

                    } else if (i > 0) {
                        RankItem rankItemBefore = response4List.getData().get(i - 1);
                        if (item.getCloseDate().equals(rankItemBefore.getCloseDate())) {
                            item.setIndex(rankItemBefore.getIndex() + 1);
                        } else {
                            item.setIndex(0);
                        }
                    }

                    homeRankAdapter.add(item);
                }
                // 当第一页的时候 需要筛选头部
                if (rankPage == 1) {
                    rankTopHeadList.clear();
                    String currentCloseDate = sdf_1.format(new Date());
                    for (int i = 0; i < 3; i++) {// 抽取前3名
                        RankItem rankItem = homeRankAdapter.getItem(i);
                        // 如果前三名在当天,就加入进来
                        if (currentCloseDate.equals(rankItem.getCloseDate())) {
                            rankTopHeadList.add(rankItem);
                        }
                    }
                    if (rankTopHeadList.size() == 3) {
                        // 有123名adapter就删除前123位
                        for (RankItem rankItem : rankTopHeadList) {
                            homeRankAdapter.remove(rankItem);
                        }
                    }
                    controlShowHeadViewWithRank();
                }
                // 最新晒单数据
                new GetRankMostNewData().execute();
                homeRankAdapter.notifyDataSetChanged();
                listView.setSelectionFromTop(firstVisiblePosition, listViemItemTop);

            } else {
                showCusToast(getResources().getString(R.string.network_problem));
            }
        }
    }


    class GetRankMostNewData extends AsyncTask<String, Void, CommonResponse<RankMostNewData>> {

        @Override
        protected CommonResponse<RankMostNewData> doInBackground(String... params) {
            CommonResponse<RankMostNewData> commonResponse = HomeDataHelper.getHomeRankMostNewData(HomeRankActivity.this);

            return commonResponse;
        }

        @Override
        protected void onPostExecute(CommonResponse<RankMostNewData> rankMostNewDataCommonResponse) {
            super.onPostExecute(rankMostNewDataCommonResponse);
            UserInfoDao dao = new UserInfoDao(HomeRankActivity.this);
            if (!dao.isLogin()) {
                // 设置未登录标识
                controlShowBottomBar(null, 0);
                return;
            }
            if (rankMostNewDataCommonResponse == null) {//返回为null  底部隐藏?

            } else {
                RankMostNewData rankMostNewData = rankMostNewDataCommonResponse.getData();
                if (rankMostNewData != null) {
                    if (rankMostNewData.getIsClose() == 2) {//休市
                        controlShowBottomBar(rankMostNewData, 1);
                        return;
                    }
                    controlShowBottomBar(rankMostNewData, 2);
                }
            }
        }
    }

    /**
     * 头部展示控制
     */
    private void controlShowHeadViewWithRank() {
        if (rankTopHeadList.size() == 3) {
            rl_rankhead.setVisibility(View.VISIBLE);
            rl_rankhead_empty.setVisibility(View.GONE);
            displayHomeRankHead();

        } else {
            rl_rankhead.setVisibility(View.GONE);
            rl_rankhead_empty.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 底部展示控制
     * 0 未登录 1 休市 2 正常显示
     *
     * @param bottomType
     */
    private void controlShowBottomBar(RankMostNewData rankMostNewData, int bottomType) {
        if (bottomType == 0) {// 未登录
            rela_homerank_bottomshare.setVisibility(View.VISIBLE);
            line_homerank_notlogin.setVisibility(View.VISIBLE);
            line_homerank_notin.setVisibility(View.GONE);
            line_homerank_alreadyin.setVisibility(View.GONE);
            btn_homerank_shareorder.setVisibility(View.GONE);
        } else if (bottomType == 1) {// 周六or周日
            rela_homerank_bottomshare.setVisibility(View.GONE);
            tv_rankhead_empty.setText("今日休市........");
        } else if (bottomType == 2) {//正常交易日
            rela_homerank_bottomshare.setVisibility(View.VISIBLE);
            line_homerank_notlogin.setVisibility(View.GONE);
            btn_homerank_shareorder.setVisibility(View.VISIBLE);
            if (rankMostNewData.getShowOrderNum() == 0 || rankMostNewData.getMyRanking() == 0) {// 无人晒单

                line_homerank_notin.setVisibility(View.VISIBLE);
                line_homerank_alreadyin.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(rankMostNewData.getMyAvatar(), img_homerank_notin, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));

            } else {// 有人晒单
                line_homerank_notin.setVisibility(View.GONE);
                line_homerank_alreadyin.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(rankMostNewData.getMyAvatar(), img_homerank_myself, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));
                tv_homerank_myrank.setText(getResources().getString(R.string.homerank_myranknum, rankMostNewData.getMyRanking()));
                tv_homerank_sharecount.setText(getResources().getString(R.string.homerank_rank_totalnum, rankMostNewData.getShowOrderNum()));
                if (rankMostNewData.getAvatars().size() == 1) {
//                    img_homerank_mostnewone.setVisibility(View.GONE);
//                    img_homerank_mostnewtwo.setVisibility(View.GONE);
//                    img_homerank_mostnewtwo_animation.setVisibility(View.GONE);
                    rela_homerank_mostnewone.setVisibility(View.GONE);
                    rela_homerank_mostnewtwo.setVisibility(View.GONE);
                    rela_homerank_mostnewtwo_animation.setVisibility(View.GONE);
                    ImageLoader.getInstance().displayImage(rankMostNewData.getAvatars().get(0), img_homerank_mostnewthree_animation, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));
                    ImageLoader.getInstance().displayImage(rankMostNewData.getAvatars().get(0), img_homerank_mostnewthree, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));
                } else if (rankMostNewData.getAvatars().size() == 2) {
//                    img_homerank_mostnewone.setVisibility(View.GONE);
//                    img_homerank_mostnewtwo.setVisibility(View.VISIBLE);
//                    img_homerank_mostnewtwo_animation.setVisibility(View.GONE);
                    rela_homerank_mostnewone.setVisibility(View.GONE);
                    rela_homerank_mostnewtwo.setVisibility(View.VISIBLE);
                    rela_homerank_mostnewtwo_animation.setVisibility(View.GONE);
                    ImageLoader.getInstance().displayImage(rankMostNewData.getAvatars().get(1), img_homerank_mostnewtwo, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));
                    ImageLoader.getInstance().displayImage(rankMostNewData.getAvatars().get(0), img_homerank_mostnewthree_animation, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));
                    ImageLoader.getInstance().displayImage(rankMostNewData.getAvatars().get(0), img_homerank_mostnewthree, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));
                } else if (rankMostNewData.getAvatars().size() == 3) {

//                    img_homerank_mostnewone.clearAnimation();
//                    img_homerank_mostnewtwo.clearAnimation();
//                    img_homerank_mostnewthree_animation.clearAnimation();
//                    img_homerank_mostnewthree.clearAnimation();
                    rela_homerank_mostnewone.clearAnimation();
                    rela_homerank_mostnewtwo.clearAnimation();
                    rela_homerank_mostnewthree_animation.clearAnimation();
                    rela_homerank_mostnewthree.clearAnimation();

                    if (rankMostNewDataHistory == null) {
                        // 展示动画
                        displayAnimation(rankMostNewData);
                    } else {
//                        img_homerank_mostnewone.setVisibility(View.VISIBLE);
                        rela_homerank_mostnewone.setVisibility(View.VISIBLE);
                        ImageLoader.getInstance().displayImage(rankMostNewData.getAvatars().get(2), img_homerank_mostnewone, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));
//                        img_homerank_mostnewtwo.setVisibility(View.VISIBLE);
                        rela_homerank_mostnewtwo.setVisibility(View.VISIBLE);
                        ImageLoader.getInstance().displayImage(rankMostNewData.getAvatars().get(1), img_homerank_mostnewtwo, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));
                        ImageLoader.getInstance().displayImage(rankMostNewData.getAvatars().get(0), img_homerank_mostnewthree_animation, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));
                        ImageLoader.getInstance().displayImage(rankMostNewData.getAvatars().get(0), img_homerank_mostnewthree, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));
                        // 当第一条数据不一样  就展示动画
                        if (!rankMostNewDataHistory.getAvatars().get(0).equals(rankMostNewData.getAvatars().get(0))) {
//                            animationOperate();
                            displayAnimation(rankMostNewData);
                        }
                    }
                    rankMostNewDataHistory = rankMostNewData;
                }
            }
        }
    }

    /**
     * 跳转到详情页
     *
     * @param item
     */
    private void startHomeRankDetailAct(RankItem item) {

        Intent intent = new Intent();
        intent.setClass(HomeRankActivity.this, HomeRankDetailV1Act.class);
        intent.putExtra("id", item.getOrderId() + "");
        intent.putExtra("exchangeId", item.getExchangeId() + "");
        intent.putExtra("rate", item.getProfitRate());
        intent.putExtra("mark", item.getGiveVoucher());
        intent.putExtra("nickName", ConvertUtil.NVL(item.getNickName(), ""));
        intent.putExtra("index", item.getIndex());
        intent.putExtra("closeTime", item.getCloseDate());
        startActivity(intent);
    }


    void displayHomeRankHead() {
        RankItem rankItemFirst = rankTopHeadList.get(0);
        line_homerank_first.setTag(rankItemFirst);
        ImageLoader.getInstance().displayImage(rankItemFirst.getAvatar(), img_homerank_first_head, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));
        tv_homerank_first_name.setText(rankItemFirst.getNickName());
        try {
            tv_homerank_first_rate.setText((int) (Double.parseDouble(ConvertUtil.NVL(rankItemFirst.getProfitRate(), "").replace("%", ""))) + "%");
        } catch (Exception e) {
            e.printStackTrace();
            tv_homerank_first_rate.setText(rankItemFirst.getProfitRate());
        }
        tv_homerank_first_voucher.setText(getResources().getString(R.string.money_lable) + ConvertUtil.NVL(rankItemFirst.getGiveVoucher(), "--"));

        RankItem rankItemSecond = rankTopHeadList.get(1);
        line_homerank_second.setTag(rankItemSecond);
        ImageLoader.getInstance().displayImage(rankItemSecond.getAvatar(), img_homerank_second_head, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));
        tv_homerank_second_name.setText(rankItemSecond.getNickName());
        try {
            tv_homerank_second_rate.setText((int) (Double.parseDouble(ConvertUtil.NVL(rankItemSecond.getProfitRate(), "").replace("%", ""))) + "%");
        } catch (Exception e) {
            e.printStackTrace();
            tv_homerank_second_rate.setText(rankItemSecond.getProfitRate());
        }
        tv_homerank_second_voucher.setText(getResources().getString(R.string.money_lable) + ConvertUtil.NVL(rankItemSecond.getGiveVoucher(), "--"));

        RankItem rankItemThird = rankTopHeadList.get(2);
        line_homerank_third.setTag(rankItemThird);
        ImageLoader.getInstance().displayImage(rankItemThird.getAvatar(), img_homerank_third_head, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));
        tv_homerank_third_name.setText(rankItemThird.getNickName());
        try {
            tv_homerank_third_rate.setText((int) (Double.parseDouble(ConvertUtil.NVL(rankItemThird.getProfitRate(), "").replace("%", ""))) + "%");
        } catch (Exception e) {
            e.printStackTrace();
            tv_homerank_third_rate.setText(rankItemThird.getProfitRate());
        }
        tv_homerank_third_voucher.setText(getResources().getString(R.string.money_lable) + ConvertUtil.NVL(rankItemThird.getGiveVoucher(), "--"));
    }

    void showPopWindown() {
        View view = View.inflate(HomeRankActivity.this, R.layout.layout_homerank_tips, null);

        int w = (int) getResources().getDimension(R.dimen.margin_289dp);
        int h = (int) getResources().getDimension(R.dimen.margin_190dp);
//        int h = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(view, w, h);

        popupWindow.setFocusable(true);//影响listView的item点击
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int y = Utils.dip2px(HomeRankActivity.this, 10);
        popupWindow.showAsDropDown(btn_homerank_tips, -w + (int) getResources().getDimension(R.dimen.margin_33dp), 10);
    }

    float threeAnmationRadius = 0;
    float twoAnmationRadius = 0;

    private void displayAnimation(RankMostNewData rankMostNewData) {
//        img_homerank_mostnewone.setVisibility(View.VISIBLE);
        rela_homerank_mostnewone.setVisibility(View.VISIBLE);
        ImageLoader.getInstance().displayImage(rankMostNewData.getAvatars().get(2), img_homerank_mostnewone, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));
//        img_homerank_mostnewtwo.setVisibility(View.VISIBLE);
        rela_homerank_mostnewtwo.setVisibility(View.VISIBLE);
        ImageLoader.getInstance().displayImage(rankMostNewData.getAvatars().get(2), img_homerank_mostnewtwo, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));
        ImageLoader.getInstance().displayImage(rankMostNewData.getAvatars().get(1), img_homerank_mostnewthree_animation, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));
        ImageLoader.getInstance().displayImage(rankMostNewData.getAvatars().get(0), img_homerank_mostnewthree, AppImageLoaderConfig.getCommonDisplayImageOptions(this, R.drawable.liveroom_icon_person));

        animationOperate();
    }

    private void animationOperate() {

//        img_homerank_mostnewone.clearAnimation();
//        img_homerank_mostnewtwo.clearAnimation();
//        img_homerank_mostnewthree_animation.clearAnimation();
//        img_homerank_mostnewthree.clearAnimation();

        rela_homerank_mostnewone.clearAnimation();
        rela_homerank_mostnewtwo.clearAnimation();
        rela_homerank_mostnewthree_animation.clearAnimation();
        rela_homerank_mostnewthree.clearAnimation();
        // 最右边头像的动画 移动到中间头像位置  透明度变化  缩小
        Animation threeAnmation_1 = new AlphaAnimation(1f, 0.6f);
        threeAnmation_1.setDuration(2000);
        int[] start_location_1 = new int[2];
        int[] end_location_1 = new int[2];
        rela_homerank_mostnewthree_animation.getLocationInWindow(start_location_1);
        rela_homerank_mostnewtwo.getLocationInWindow(end_location_1);
        Animation threeAnmation_2 = new TranslateAnimation(
                0,// 动画开始的点离当前View X坐标上的差值
//                end_location_1[0]-start_location_1[0],// 动画结束的点离当前View X坐标上的差值
                -Utils.dip2px(this, 15),
                0,//动画开始的点离当前View Y坐标上的差值
                0);//动画开始的点离当前View Y坐标上的差值
        threeAnmation_2.setDuration(2000);
        Animation threeAnmation_3 = new ScaleAnimation(
                1.0f,
                32f / 36f,
                1.0f,
                32f / 36f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        threeAnmation_3.setDuration(2000);
        AnimationSet animationSet_1 = new AnimationSet(true);
        animationSet_1.setFillAfter(true);
        animationSet_1.addAnimation(threeAnmation_1);
        animationSet_1.addAnimation(threeAnmation_2);
        animationSet_1.addAnimation(threeAnmation_3);

        // 中间头像的动画 移动到中间头像位置  透明度变化  缩小
        Animation threeAnmation_2_1 = new AlphaAnimation(1f, 0.5f);
        threeAnmation_2_1.setDuration(2000);
        int[] start_location_2_1 = new int[2];
        int[] end_location_2_1 = new int[2];
        rela_homerank_mostnewtwo.getLocationInWindow(start_location_2_1);
        rela_homerank_mostnewone.getLocationInWindow(end_location_2_1);
        Animation threeAnmation_2_2 = new TranslateAnimation(
                0,// 动画开始的点离当前View X坐标上的差值
//                end_location_2_1[0]-start_location_2_1[0],// 动画结束的点离当前View X坐标上的差值
                -Utils.dip2px(this, 14),
                0,//动画开始的点离当前View Y坐标上的差值
                0);//动画开始的点离当前View Y坐标上的差值
        threeAnmation_2_2.setDuration(2000);
        Animation threeAnmation_2_3 = new ScaleAnimation(
                1.0f,
                28f / 32f,
                1.0f,
                28f / 32f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        threeAnmation_2_3.setDuration(2000);
        AnimationSet animationSet_2_1 = new AnimationSet(true);
        animationSet_2_1.setFillAfter(true);
        animationSet_2_1.addAnimation(threeAnmation_2_1);
        animationSet_2_1.addAnimation(threeAnmation_2_2);
        animationSet_2_1.addAnimation(threeAnmation_2_3);

        // 左边头像的动画 移动到中间头像位置  透明度变化  缩小
        Animation threeAnmation_3_1 = new AlphaAnimation(1f, 0.0f);
        threeAnmation_3_1.setDuration(2000);
        Animation threeAnmation_3_2 = new TranslateAnimation(
                0,// 动画开始的点离当前View X坐标上的差值
                -20,// 动画结束的点离当前View X坐标上的差值
                0,//动画开始的点离当前View Y坐标上的差值
                0);//动画开始的点离当前View Y坐标上的差值
        threeAnmation_3_2.setDuration(2000);
        AnimationSet animationSet_3_1 = new AnimationSet(true);
        animationSet_3_1.setFillAfter(true);
        animationSet_3_1.addAnimation(threeAnmation_3_1);
        animationSet_3_1.addAnimation(threeAnmation_3_2);

        //
        Animation threeAnmation_4_1 = new AlphaAnimation(0.0f, 1f);
        threeAnmation_4_1.setDuration(2000);

        rela_homerank_mostnewone.startAnimation(animationSet_3_1);
        rela_homerank_mostnewtwo.startAnimation(animationSet_2_1);
        rela_homerank_mostnewthree_animation.startAnimation(animationSet_1);
        rela_homerank_mostnewthree.startAnimation(threeAnmation_4_1);
    }
}
