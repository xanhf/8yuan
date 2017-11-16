package com.trade.eight.moudle.home.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.entity.Exchange;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.trade.TradeOrderOptionEvent;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.moudle.trade.UpdateTradeUserInfoEvent;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.Utils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 首页交易大厅
 * viewpager 装载 fragment
 */
public class HomeTradeFrag extends BaseFragment implements OnClickListener {

    public static final String TAG = "HomeTradeFrag";
//    ObservableScrollView scrollView;
    ViewPager viewPager;
    private ImageView cursor;// 动画图片

    private MFragPageAdapter mMyFragPageAdapter;

    private List<BaseFragment> fragments = new ArrayList<BaseFragment>();

    private RelativeLayout lable01, lable02, lable03;
    private ImageView img_traderule;
    TextView selectedView = null, tab1tv = null, tab2tv = null, tab3tv = null;
    String PAGE_TAG = "page_trade";
    View rootView;
//    TextView text_moneyzy_rate, text_money_dqqy, text_moneyzy_kyzj, text_moneyzy_dyyk;
    View rel_tradeviewpager;

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
        rootView = inflater.inflate(R.layout.fragment_home_trade, null);

        //上面是否显示新手的引导页，显示如果新手的引导页没有出来，就显示这个
        final View homeHelpLayout = getActivity().findViewById(R.id.homeHelpLayout);
        if (homeHelpLayout.getVisibility() != View.VISIBLE) {
        }

        initFragments();
        initView(rootView);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        return rootView;
    }

    /**
     * mark
     * 删掉这个方法之后 出现错误
     * has no public methods called onEvent
     * <p>
     * 重新获取了交易所列表后
     *
     * @param exchange
     */
    public void onEventMainThread(Exchange exchange) {
        Log.v(TAG, "onEventMainThread");
        if (exchange == null)
            return;
    }

    /**
     * 建仓成功  或者平仓成功
     *
     * @param tradeOrderOptionEvent
     */
    public void onEventMainThread(TradeOrderOptionEvent tradeOrderOptionEvent) {
        if (viewPager != null) {
            viewPager.setCurrentItem(1);
        }
    }


    /**
     * 设置容器的大小
     * <p>
     * <p>
     */
//    public void onEventMainThread(SetViewHeightEvent setViewHeightEvent) {
//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rel_tradeviewpager.getLayoutParams();
//        int height = setViewHeightEvent.viewpageHeight;
//        if (height == 0) {
//            height = Utils.getScreenH(getActivity()) - (Utils.getStatusHeight(getActivity()) + Utils.dip2px(getActivity(), 232));
//        }
//        layoutParams.height = height;
//        rel_tradeviewpager.setLayoutParams(layoutParams);
//        //滚动到原点
//        scrollView.scrollTo(0, 0);
//        scrollView.smoothScrollTo(0, 0);
//    }

    /**
     * 头部刷新
     *
     * @param updateTradeUserInfoEvent
     */
    public void onEventMainThread(UpdateTradeUserInfoEvent updateTradeUserInfoEvent) {
        TradeInfoData tradeInfoData = updateTradeUserInfoEvent.tradeInfoData;
//        updateTitle(tradeInfoData);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
//        TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), this).stopRefresh();
    }

    @Override
    public void onPause() {
        super.onPause();
//        TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), this).stopRefresh();
    }

    public void initFragments() {
        TradeContentListFrag contentListFrag = new TradeContentListFrag();
        TradeOrderHoldFrag holdFrag = new TradeOrderHoldFrag();
        BaseFragment moneyFrag = new TradeOrderCloseFrag();
//        if (TradeConfig.isCurrentJN(getActivity()) || TradeConfig.isCurrentHN(getActivity())) {
//            moneyFrag = new TradeMoneyJNFrag();
//        } else {
//            moneyFrag = new TradeMoneyFrag();
//        }

        fragments.add(contentListFrag);//买卖
        fragments.add(holdFrag);//持仓
        fragments.add(moneyFrag);//资金

        //初始化可以见
        contentListFrag.onFragmentVisible(true);

    }


    @Override
    public void onFragmentVisible(boolean isVisible) {
        super.onFragmentVisible(isVisible);
        Log.v(TAG, "onFragmentVisible " + isVisible);
        if (viewPager == null)
            return;
        if (fragments == null
                || fragments.size() == 0)
            return;

        try {
            if (isVisible) {
                //新手引导的
                visibleFrags(viewPager.getCurrentItem());
            } else {
                visibleFrags(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void initView(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.tradeViewPager);
        viewPager.setOffscreenPageLimit(3);
        cursor = (ImageView) view.findViewById(R.id.cursor);

        mMyFragPageAdapter = new MFragPageAdapter(getChildFragmentManager(), TAG);
        viewPager.setAdapter(mMyFragPageAdapter);
        viewPager.setOnPageChangeListener(pageChangerListener);
        initAnimView();

        lable01 = (RelativeLayout) view.findViewById(R.id.lable01);
        lable02 = (RelativeLayout) view.findViewById(R.id.lable02);
        lable03 = (RelativeLayout) view.findViewById(R.id.lable03);

        img_traderule = (ImageView) view.findViewById(R.id.img_traderule);

        tab1tv = (TextView) view.findViewById(R.id.tab1tv);
        if (tab1tv != null) {
            selectedView = tab1tv;
            selectedView.setSelected(true);
        }
        tab2tv = (TextView) view.findViewById(R.id.tab2tv);
        tab3tv = (TextView) view.findViewById(R.id.tab3tv);

        lable01.setOnClickListener(this);
        lable02.setOnClickListener(this);
        lable03.setOnClickListener(this);
        img_traderule.setOnClickListener(this);
        rel_tradeviewpager = view.findViewById(R.id.rel_tradeviewpager);

    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int i = v.getId();
        if (i == R.id.desView) {
            MyAppMobclickAgent.onEvent(getActivity(), PAGE_TAG, "btn_tickDes");
            Intent intentll_pro = new Intent(getActivity(), WebActivity.class);
            intentll_pro.putExtra("isWeipan", true);
            intentll_pro.putExtra("url", AndroidAPIConfig.URL_WEIPAN_DES);
            intentll_pro.putExtra("title", getResources().getString(R.string.me_know_eight));
            startActivity(intentll_pro);
        } else if (i == R.id.lable01) {
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_INDEX_TRADE, "建仓");
            viewPager.setCurrentItem(0);

        } else if (i == R.id.lable02) {
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_INDEX_TRADE, "持仓");
            viewPager.setCurrentItem(1);

        } else if (i == R.id.lable03) {
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_INDEX_TRADE, "已平仓");
            viewPager.setCurrentItem(2);

        } else if(i==R.id.img_traderule){

            String string = "交易规则";
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_INDEX_TRADE, string);
            WebActivity.start(getActivity(), string, AndroidAPIConfig.URL_TRADE_RULE, true);
        }
    }


    // 初始化动画
    private void initAnimView() {
        Matrix matrix = new Matrix();
        matrix.postTranslate(0, 0);
        cursor.setImageMatrix(matrix);// 设置动画初始位置
    }

    /**
     * 设置fragment的可见状态
     *
     * @param index
     */
    void visibleFrags(int index) {
        if (fragments == null || fragments.size() == 0)
            return;

        //滚动到原点
//        scrollView.scrollTo(0, 0);
//        scrollView.smoothScrollTo(0, 0);
        for (int i = 0; i < fragments.size(); i++) {
            BaseFragment fragment = fragments.get(i);
            if (fragment == null)
                continue;
            //只是存在内存 isadd 为false
            if (!fragment.isAdded()) {
                getChildFragmentManager().beginTransaction().attach(fragment).commit();
            }
            if (index == i)
                fragment.onFragmentVisible(true);
            else
                fragment.onFragmentVisible(false);
        }
    }

    private int currentIndex = 0;

    private ViewPager.OnPageChangeListener pageChangerListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            // TODO Auto-generated method stub
//            int one = Utils.getWindowWidth(getActivity()) / fragments.size();
            int one = Utils.dip2px(getActivity(), 62);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cursor.getLayoutParams();
            if(position==2){
                layoutParams.width = Utils.dip2px(getActivity(),45);
            }else{
                layoutParams.width = Utils.dip2px(getActivity(),30);

            }
            cursor.setLayoutParams(layoutParams);

            Animation animation = new TranslateAnimation(one * currentIndex,
                    one * position, 0, 0);
            currentIndex = position;
            animation.setFillAfter(true);
            animation.setDuration(300);
            cursor.startAnimation(animation);

            if (selectedView != null) {
                selectedView.setSelected(false);
            }
            if (position == 0) {
                selectedView = tab1tv;
                if (selectedView != null)
                    selectedView.setSelected(true);

                visibleFrags(0);

            } else if (position == 1) {
                selectedView = tab2tv;
                if (selectedView != null)
                    selectedView.setSelected(true);

                visibleFrags(1);

            } else if (position == 2) {
                selectedView = tab3tv;
                if (selectedView != null)
                    selectedView.setSelected(true);

                visibleFrags(2);

            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // TODO Auto-generated method stub
        }
    };

    class MFragPageAdapter extends FragmentPagerAdapter {

        public MFragPageAdapter(FragmentManager fm, String uniqueFlag) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        public void setItem(List<BaseFragment> list) {
            fragments.clear();
            if (list != null)
                fragments.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragments.get(arg0);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return fragments.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            return super.instantiateItem(container, position);
        }
    }

}
