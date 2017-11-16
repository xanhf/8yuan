package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AppSetting;
import com.trade.eight.moudle.home.activity.NaviActivity;
import com.trade.eight.moudle.trade.activity.TradeRegAct;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.nav.UNavConfig;

import java.util.ArrayList;

/**
 * Created by fangzhu on 16/8/18.
 * <p/>
 * 步骤引导图的效果 点击直接关闭 多页
 */
public class StepNavMuiltAct extends BaseActivity {

    public static final int TYPE_TRADE_CREATE = 100;
    public static final int TYPE_TRADE_HOLD = 101;
    int mType = 0;

    int[] createTips = {R.drawable.img_create_tips_1, R.drawable.img_create_tips_2, R.drawable.img_create_tips_3, R.drawable.img_create_tips_4};
    int[] holdTips = {R.drawable.img_hold_tips_1, R.drawable.img_hold_tips_2};
    int firstTips = 0;
    private ArrayList<View> viewlist = new ArrayList<View>();

    //    ViewPager viewpage_tips;
    ImageView img_tips;
    Button btnSkip;

    private int pos = 0;
    private int maxPos = 0;

    public static void start(Context context, int type) {
        context.startActivity(new Intent(context, StepNavMuiltAct.class).putExtra("type", type));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_step_muilt);
        hideBottomUIMenu();
        initData();
        initView();
    }

    private void initData() {
        mType = getIntent().getIntExtra("type", 0);
        if (mType == TYPE_TRADE_CREATE) {
            firstTips = createTips[0];
            maxPos = createTips.length - 1;
//            for (int i = 0; i < createTips.length; i++) {
//                View itemFirst = getLayoutInflater().inflate(R.layout.nav_item_1, null);
//                ImageView imageView = (ImageView) itemFirst.findViewById(R.id.img);
//                imageView.setImageDrawable(getResources().getDrawable(createTips[i]));
//                viewlist.add(itemFirst);
//            }
        } else if (mType == TYPE_TRADE_HOLD) {
            firstTips = holdTips[0];
            maxPos = holdTips.length - 1;

//            for (int i = 0; i < holdTips.length; i++) {
//                View itemFirst = getLayoutInflater().inflate(R.layout.nav_item_1, null);
//                ImageView imageView = (ImageView) itemFirst.findViewById(R.id.img);
//                imageView.setImageDrawable(getResources().getDrawable(holdTips[i]));
//                viewlist.add(itemFirst);
//            }
        }

    }

    private void initView() {
//        viewpage_tips = (ViewPager) findViewById(R.id.viewpage_tips);
//        viewpage_tips.setAdapter(new MyPagerAdapter());
//        viewpage_tips.setOnPageChangeListener(new mOnpagerChangeLister());

        img_tips = (ImageView) findViewById(R.id.img_tips);
        img_tips.setImageResource(firstTips);

        btnSkip = (Button) findViewById(R.id.btnSkip);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos != maxPos) {
                    pos += 1;
                    if (mType == TYPE_TRADE_CREATE) {
                        img_tips.setImageResource(createTips[pos]);
                    } else if (mType == TYPE_TRADE_HOLD) {
                        img_tips.setImageResource(holdTips[pos]);
                    }
//                    viewpage_tips.setCurrentItem(pos);
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public boolean isActivityFitsSystemWindows() {
        return false;
    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(viewlist.get(position));
        }

        @Override
        public int getCount() {
            return viewlist.size();
        }

        @Override
        public Object instantiateItem(View container, int position) {
            View view = viewlist.get(position);
            ((ViewPager) container).addView(view);
            return viewlist.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    private class mOnpagerChangeLister implements
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
            pos = arg0;
        }
    }


    @Override
    public void finish() {
        super.finish();
        if (mType == TYPE_TRADE_CREATE) {
            PreferenceSetting.setBoolean(StepNavMuiltAct.this, "trade_create", true);
        }
        if (mType == TYPE_TRADE_HOLD) {
            PreferenceSetting.setBoolean(StepNavMuiltAct.this, "trade_hold", true);
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);// 淡出淡入动画效果
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
