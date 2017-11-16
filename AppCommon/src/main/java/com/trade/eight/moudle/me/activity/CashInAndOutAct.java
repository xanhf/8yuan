package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
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
import com.trade.eight.moudle.me.fragment.CashHistoryFrag;
import com.trade.eight.moudle.me.fragment.CashInFrag;
import com.trade.eight.moudle.me.fragment.CashOutFrag;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.Utils;
import com.trade.eight.view.AppTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/14.
 */

public class CashInAndOutAct extends BaseActivity implements View.OnClickListener {

    public static final String TAG = CashInAndOutAct.class.getSimpleName();

    public static final int CASHIN_SUCCESS = 1;// 充值成功
    public static final int CASHIN_FAIL = 2;// 充值失败
    public static final int CASHOUT_SUCCESS = 3;// 提现成功
    public static final int CASHOUT_FAIL = 4;// 提现失败

    AppTitleView title_view;
    ViewPager viewPager;
    private ImageView cursor;// 动画图片
    private RelativeLayout lable01, lable02, lable03;

    TextView selectedView = null, tab1tv = null, tab2tv = null, tab3tv = null;
    private MFragPageAdapter mMyFragPageAdapter;

    private List<BaseFragment> fragments = new ArrayList<BaseFragment>();

    public static void startAct(Context context, int selectIndex) {
        Intent intent = new Intent(context, CashInAndOutAct.class);
        intent.putExtra("selectIndex", selectIndex);
        context.startActivity(intent);
    }

    public static void startAct(Context context) {
        Intent intent = new Intent(context, CashInAndOutAct.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cashin_out);
        initFragments();
        initView();
        initData();
    }

    private void initData() {
        int selectIndex = getIntent().getIntExtra("selectIndex", -1);
        if (selectIndex != -1) {
            switch (selectIndex) {
                case 0:
                    if (lable01 != null) {
                        lable01.performClick();
                    }
                    break;
                case 1:
                    if (lable02 != null) {
                        lable02.performClick();
                    }
                    break;
                case 2:
                    if (lable03 != null) {
                        lable03.performClick();
                    }
                    break;
            }

        }
    }

    public void initFragments() {
        CashInFrag cashInFrag = new CashInFrag();
        CashOutFrag cashOutFrag = new CashOutFrag();
        CashHistoryFrag cashHistoryFrag = new CashHistoryFrag();

        fragments.add(cashInFrag);
        fragments.add(cashOutFrag);
        fragments.add(cashHistoryFrag);

        //初始化可以见
        cashInFrag.onFragmentVisible(true);
    }


    public void initView() {
        title_view = (AppTitleView) findViewById(R.id.title_view);
        title_view.setBaseActivity(CashInAndOutAct.this);
        title_view.setAppCommTitle(R.string.lable_cashout_in);
        title_view.setSpiltLineDisplay(false);
        title_view.setRightBtnText("出入金规则");
        title_view.setRightBtnCallback(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                MyAppMobclickAgent.onEvent(CashInAndOutAct.this, UmengMobClickConfig.CASH_IN_OUT,"cash_rule");
                WebActivity.start(CashInAndOutAct.this, "出入金规则", AndroidAPIConfig.URL_CASHINOUT_RULE);
                return false;
            }
        });

        viewPager = (ViewPager) findViewById(R.id.tradeViewPager);
        viewPager.setOffscreenPageLimit(3);
        cursor = (ImageView) findViewById(R.id.cursor);

        mMyFragPageAdapter = new MFragPageAdapter(getSupportFragmentManager(), TAG);
        viewPager.setAdapter(mMyFragPageAdapter);
        viewPager.setOnPageChangeListener(pageChangerListener);
        initAnimView();

        lable01 = (RelativeLayout) findViewById(R.id.lable01);
        lable02 = (RelativeLayout) findViewById(R.id.lable02);
        lable03 = (RelativeLayout) findViewById(R.id.lable03);

        tab1tv = (TextView) findViewById(R.id.tab1tv);
        if (tab1tv != null) {
            selectedView = tab1tv;
            selectedView.setSelected(true);
        }
        tab2tv = (TextView) findViewById(R.id.tab2tv);
        tab3tv = (TextView) findViewById(R.id.tab3tv);

        lable01.setOnClickListener(this);
        lable02.setOnClickListener(this);
        lable03.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int i = v.getId();
        if (i == R.id.lable01) {
            viewPager.setCurrentItem(0);
            MyAppMobclickAgent.onEvent(CashInAndOutAct.this, UmengMobClickConfig.CASH_IN_OUT,"cash_in");

        } else if (i == R.id.lable02) {
            viewPager.setCurrentItem(1);
            MyAppMobclickAgent.onEvent(CashInAndOutAct.this, UmengMobClickConfig.CASH_IN_OUT,"cash_out");

        } else if (i == R.id.lable03) {
            viewPager.setCurrentItem(2);
            MyAppMobclickAgent.onEvent(CashInAndOutAct.this, UmengMobClickConfig.CASH_IN_OUT,"cash_history");

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
                getSupportFragmentManager().beginTransaction().attach(fragment).commit();
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
            int one = Utils.dip2px(CashInAndOutAct.this, 62);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cursor.getLayoutParams();
            if (position == 2) {
                layoutParams.width = Utils.dip2px(CashInAndOutAct.this, 75);
            } else {
                layoutParams.width = Utils.dip2px(CashInAndOutAct.this, 30);

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
