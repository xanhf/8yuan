package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.MissionTaskDao;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.missioncenter.MissionBannerData;
import com.trade.eight.entity.missioncenter.MissionData;
import com.trade.eight.entity.missioncenter.MissionTaskData;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.moudle.me.adapter.MissionCenterAdapter;
import com.trade.eight.moudle.me.adapter.MyViewPagerAdapter;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.Utils;
import com.trade.eight.view.AutoScrollViewPager;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/2/28.
 * 任务中心
 */

public class MissionCenterAct extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ListView> {


    PullToRefreshListView pullToRefreshListView = null;
    ListView listView = null;
    MissionCenterAdapter missionCenterAdapter;
    View headView;
    AutoScrollViewPager autoViewPager;
    private ArrayList<View> dotViewList = new ArrayList<View>();
    LinearLayout dotlayout;
    View currentDotView;

    boolean isShowNetDialog = true;


    public static void startAct(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MissionCenterAct.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_mission_center);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMissionData();
    }

    private void initView() {
        setAppCommonTitle(getResources().getString(R.string.mission_center));
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list_missioncenter);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setPullLoadEnabled(false);
        pullToRefreshListView.setPullRefreshEnabled(true);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setVisibility(View.VISIBLE);
        listView.setDividerHeight(0);
        headView = View.inflate(this, R.layout.missioncenter_headview, null);
        autoViewPager = (AutoScrollViewPager) headView.findViewById(R.id.autoViewPager);
        dotlayout = (LinearLayout) headView.findViewById(R.id.dotlayout);
        headView.setVisibility(View.VISIBLE);
        listView.addHeaderView(headView);
        missionCenterAdapter = new MissionCenterAdapter(this, 0, new ArrayList<MissionTaskData>());
        listView.setAdapter(missionCenterAdapter);

        BaseInterface.setPullFormartRefreshTime(pullToRefreshListView);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        getMissionData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    void initViewPager(List<MissionBannerData> pagerItemList) {
        if (pagerItemList == null || pagerItemList.size() == 0)
            return;
        List<View> mListViews = new ArrayList<>();
        for (int i = 0; i < pagerItemList.size(); i++) {
            mListViews.add(View.inflate(this, R.layout.home_top_pager_item, null));
        }
        autoViewPager.setAdapter(new MyViewPagerAdapter(MissionCenterAct.this, mListViews, pagerItemList,missionCenterAdapter.objects));

        autoViewPager.startAutoScroll();
        autoViewPager.setInterval(5000);//自动切换时间
        autoViewPager.setOnPageChangeListener(new MOnpagerChangeLister());


        if (dotlayout != null) {
            dotlayout.removeAllViews();
            dotViewList.clear();
            dotlayout.setVisibility(View.VISIBLE);

            int width = Utils.dip2px(MissionCenterAct.this, 5);
            int margin = Utils.dip2px(MissionCenterAct.this, 2);
            for (int i = 0; i < mListViews.size(); i++) {
                TextView textView = new TextView(MissionCenterAct.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
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
     * 获取任务数据
     */
    private void getMissionData() {
        HashMap<String, String> request = new HashMap<String, String>();
        final UserInfoDao userInfoDao = new UserInfoDao(this);
        request.put("userId", userInfoDao.queryUserInfo().getUserId());
        HttpClientHelper.doPostOption(MissionCenterAct.this, AndroidAPIConfig.URL_MISSIONCENTER_LIST, request, null, new NetCallback(MissionCenterAct.this) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {

            }

            @Override
            public void onResponse(String responseStr) {
                CommonResponse<MissionData> response = CommonResponse.fromJson(responseStr,MissionData.class);
                pullToRefreshListView.onPullDownRefreshComplete();
                BaseInterface.setPullFormartRefreshTime(pullToRefreshListView);
                List<MissionTaskData> taskList = response.getData().getTaskList();

                if (taskList != null && taskList.size() > 0) {
                    missionCenterAdapter.clear();
                    for (int i = 0; i < taskList.size(); i++) {
                        MissionTaskData missionTaskData = taskList.get(i);
                        // 当为答题任务时  需要跟本地的做校验
                        if (missionTaskData.getTaskType() == 1) {
                            MissionTaskDao missionTaskDao = new MissionTaskDao(MissionCenterAct.this);
                            MissionTaskData missionTaskDataLocal = missionTaskDao.queryMissionTaskData(missionTaskData.getTaskId(), userInfoDao.queryUserInfo().getUserId());
                            missionTaskData.setUserId(userInfoDao.queryUserInfo().getUserId());
                            if (missionTaskDataLocal != null) {
                                if (missionTaskDataLocal.getTaskId()==missionTaskData.getTaskId()&&missionTaskDataLocal.getLocalqueSucessNum() >= missionTaskData.getQueSucessNum()) {
                                    missionTaskData.setLocalqueSucessNum(missionTaskDataLocal.getLocalqueSucessNum());
                                    if(missionTaskData.getTaskVersion()<=missionTaskDataLocal.getTaskVersion()){
                                        missionTaskData.setQuestionData(missionTaskDataLocal.getQuestionData());
                                    }
                                    missionTaskData.setLocalId(missionTaskDataLocal.getLocalId());
                                }
                            }
//                            else {
                            missionTaskDao.addOrUpdate(missionTaskData);
//                            }
                        }
                        missionCenterAdapter.add(missionTaskData);
                    }
                    missionCenterAdapter.notifyDataSetChanged();
                    initViewPager(response.getData().getBannerList());
                }

            }
        }, isShowNetDialog);
        isShowNetDialog = false;
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
}
