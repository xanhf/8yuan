package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.entity.home.HomePagerItem;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.OpenActivityUtil;
import com.trade.eight.view.UnderLineTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/23.
 */

public class RegSuccessActivity extends BaseActivity implements View.OnClickListener {

    UnderLineTextView tv_1, tv_2, selectView;
    private View line_newuser, line_golive, line_openaccount, line_golive_1;
    TextView text_seeee, text_seeee_1;

    ViewPager regsuccessPager;
    List<View> listViews = new ArrayList<>();

    public static void startAct(Context context) {
        Intent intent = new Intent(context, RegSuccessActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_regsuccess);

        initView();
    }

    private void initView() {
        tv_1 = (UnderLineTextView) findViewById(R.id.tv_1);
        tv_2 = (UnderLineTextView) findViewById(R.id.tv_2);
        tv_1.setOnClickListener(this);
        tv_2.setOnClickListener(this);
        selectView = tv_1;
        selectView.setSelected(true);

        regsuccessPager = (ViewPager) findViewById(R.id.regsuccessPager);

//        line_newuser = findViewById(R.id.line_newuser);
//        line_openaccount = findViewById(R.id.line_openaccount);
//        line_golive = findViewById(R.id.line_golive);
//        text_seeee = (TextView) findViewById(R.id.text_seeee);
//
//        line_newuser.setOnClickListener(this);
//        line_openaccount.setOnClickListener(this);
//        line_golive.setOnClickListener(this);
//        text_seeee.setOnClickListener(this);

        addViewPagerViews();
    }

    private void addViewPagerViews() {
        listViews.add(View.inflate(RegSuccessActivity.this, R.layout.layout_regsuccess_new, null));
        listViews.add(View.inflate(RegSuccessActivity.this, R.layout.layout_regsuccess_old, null));
        regsuccessPager.setAdapter(new MyViewPagerAdapter(listViews));
        regsuccessPager.setOnPageChangeListener(new MOnpagerChangeLister());
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.text_seeee:
                doMyfinish();
                break;
            case R.id.line_golive:
                startActivity(OpenActivityUtil.getIntent(RegSuccessActivity.this, OpenActivityUtil.SCHEME+OpenActivityUtil.ACT_LIVE_LIST));
                doMyfinish();
                break;
            case R.id.text_seeee_1:
                doMyfinish();
                break;
            case R.id.line_golive_1:
                startActivity(OpenActivityUtil.getIntent(RegSuccessActivity.this, OpenActivityUtil.SCHEME+OpenActivityUtil.ACT_LIVE_LIST));
                doMyfinish();
                break;
            case R.id.line_openaccount:
                OpenActivityUtil.openQiHuoAccountWelcome(RegSuccessActivity.this);
                doMyfinish();
                break;
            case R.id.line_newuser:
                String string = "新手学堂";
                WebActivity.start(RegSuccessActivity.this, string, AndroidAPIConfig.URL_HOME_XSXT, true);

                doMyfinish();
                break;
            case R.id.tv_1:
                selectView.setSelected(false);
                selectView.setIsLineEnable(false);
                selectView = tv_1;
                selectView.setSelected(true);
//                line_newuser.setVisibility(View.VISIBLE);
//                line_openaccount.setVisibility(View.GONE);
//                line_golive.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_2:
                selectView.setSelected(false);
                selectView.setIsLineEnable(false);
                selectView = tv_2;
                selectView.setSelected(true);

//                line_newuser.setVisibility(View.GONE);
//                line_openaccount.setVisibility(View.VISIBLE);
//                line_golive.setVisibility(View.VISIBLE);
                break;
        }
    }


    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;


        public MyViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));//删除页卡
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {    //这个方法用来实例化页卡

            container.addView(mListViews.get(position), 0);//添加页卡
            View itemView = mListViews.get(position);
            if (position == 0) {
                line_newuser = itemView.findViewById(R.id.line_newuser);
                line_golive = itemView.findViewById(R.id.line_golive);
                text_seeee = (TextView) itemView.findViewById(R.id.text_seeee);

                line_newuser.setOnClickListener(RegSuccessActivity.this);
                line_golive.setOnClickListener(RegSuccessActivity.this);
                text_seeee.setOnClickListener(RegSuccessActivity.this);

            } else {
                line_openaccount = itemView.findViewById(R.id.line_openaccount);
                line_golive_1 = itemView.findViewById(R.id.line_golive_1);
                text_seeee_1 = (TextView) itemView.findViewById(R.id.text_seeee_1);

                line_openaccount.setOnClickListener(RegSuccessActivity.this);
                line_golive_1.setOnClickListener(RegSuccessActivity.this);
                text_seeee_1.setOnClickListener(RegSuccessActivity.this);
            }


            return mListViews.get(position);
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
            if (arg0 == 0) {
                tv_1.performClick();
            } else {
                tv_2.performClick();
            }
        }
    }
}
