package com.trade.eight.moudle.home.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.app.SystemContext;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.mpush.coreoption.CoreOptionUtil;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.JSONObjectUtil;
import com.trade.eight.service.OptionalService;
import com.trade.eight.tools.ActivityUtil;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NaviActivity extends BaseActivity {
    public static final String TAG = "NaviActivity";
    private int currentPageScrollStatus;
    private Button btn_jump;
    private ViewPager mViewPager;
    private ArrayList<View> viewlist;
    private int pos = 0;
    private int maxPos = 0;
    NaviActivity context = this;

    boolean isShowNews = true;
    int[] navIds = {R.drawable.nav_img_01, R.drawable.nav_img_02, R.drawable.nav_img_03};

    @SuppressLint("InflateParams")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.NoTitleBarFullscreen);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.show_navi);
        mViewPager = (ViewPager) findViewById(R.id.viewpager1);
        viewlist = new ArrayList<View>();


        //2.0的导航页有登录注册按钮  必须先初始化DB
        new OptionalService(context).initDb();
        SystemContext.getInstance(context);


        for (int i = 0; i < navIds.length; i++) {
            View itemFirst = getLayoutInflater().inflate(R.layout.nav_item_1, null);
            ImageView imageView = (ImageView) itemFirst.findViewById(R.id.img);
            imageView.setImageDrawable(getResources().getDrawable(navIds[i]));
            viewlist.add(itemFirst);
        }
        btn_jump = (Button) findViewById(R.id.btn_jump);
        btn_jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goHome();
            }
        });
        mViewPager.setAdapter(new MyPagerAdapter());
        mViewPager.setOnPageChangeListener(new mOnpagerChangeLister());
        maxPos = viewlist.size() - 1;
        // 进入即建立长连接
        UserInfoDao userInfoDao = new UserInfoDao(NaviActivity.this);
//        CoreOptionUtil.getCoreOptionUtil(getApplicationContext()).startPush("192.168.100.233:3000", userInfoDao.isLogin() ? userInfoDao.queryUserInfo().getUserId() : "0");
//        CoreOptionUtil.getCoreOptionUtil(getApplicationContext()).startPush("114.55.108.28:3000", userInfoDao.isLogin() ? userInfoDao.queryUserInfo().getUserId() : "0");
        CoreOptionUtil.getCoreOptionUtil(getApplicationContext()).startPush(AndroidAPIConfig.HOST_QUOTATION, userInfoDao.isLogin() ? userInfoDao.queryUserInfo().getUserId() : "0");

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
            currentPageScrollStatus = arg0;

        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            if (pos == maxPos) {
				AppSetting.getInstance(context).setTureNavi(true);//到最后一页 认为已经使用完导航页
                // 已经在最后一页还想往右划
                if (positionOffsetPixels == 0 && currentPageScrollStatus == 1) {
                    goHome();
                }
            }

        }

        @Override
        public void onPageSelected(int arg0) {
            pos = arg0;
            if(pos==maxPos){
                mHandle.sendEmptyMessageDelayed(MSG_ENTER_MAIN, 2000L);
            }
        }

    }

    void goHome() {
        mHandle.removeMessages(MSG_ENTER_MAIN);
        AppSetting.getInstance(this).setTureNavi(true);
        startActivity(new Intent().setClass(NaviActivity.this, MainActivity.class).putExtra("isShowNews", isShowNews));
        NaviActivity.this.finish();
    }

    public static final int MSG_ENTER_MAIN = 0;
    Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_ENTER_MAIN) {
                goHome();
            }

        }
    };
}
