package com.trade.eight.moudle.home.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.easylife.ten.lib.R;
import com.igexin.sdk.PushManager;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;
import com.nostra13.universalimageloader.utils.L;
import com.tencent.bugly.crashreport.CrashReport;
import com.trade.eight.app.SystemContext;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AppCollectUtil;
import com.trade.eight.config.OnLineHelper;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.config.WeipanConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.live.LiveRoomNew;
import com.trade.eight.entity.sharejoin.JoinVouchObj;
import com.trade.eight.moudle.ads.AdsUtil;
import com.trade.eight.moudle.floatvideo.FloatPermission;
import com.trade.eight.moudle.floatvideo.event.EventFloat;
import com.trade.eight.moudle.floatvideo.service.FloatVideoService;
import com.trade.eight.moudle.home.adapter.FragmentTabAdapter;
import com.trade.eight.moudle.home.fragment.HomeFragmentV1;
import com.trade.eight.moudle.home.fragment.HomeTradeFrag;
import com.trade.eight.moudle.home.fragment.IndexLiveFragment;
import com.trade.eight.moudle.home.fragment.MeFragment;
import com.trade.eight.moudle.home.fragment.OptionalFragment;
import com.trade.eight.moudle.listener.HomeWatcher;
import com.trade.eight.moudle.me.ShJoinHelp;
import com.trade.eight.moudle.netty.NettyClient;
import com.trade.eight.moudle.push.AppPushCoreService;
import com.trade.eight.moudle.push.AppPushIntentService;
import com.trade.eight.moudle.push.activity.CashOutNoticeActivity;
import com.trade.eight.moudle.push.activity.CloseOrderNotifyActivity;
import com.trade.eight.moudle.push.activity.OrderAndTradeNotifyActivity;
import com.trade.eight.moudle.push.activity.ProductNoticeNotifyActivity;
import com.trade.eight.moudle.push.activity.TradeFXNoticeActivity;
import com.trade.eight.moudle.push.entity.PushExtraObj;
import com.trade.eight.moudle.push.entity.PushMsgObj;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.moudle.upgradeversion.CheckUpgradeInfoTask;
import com.trade.eight.mpush.coreoption.CoreOptionUtil;
import com.trade.eight.mpush.entity.OrderNotifyData;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.Utils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class MainActivity extends BaseActivity implements HomeWatcher.OnHomePressedListener {

    public static final String HOME = "HOME";
    public static final String MARKET = "MARKET";
    public static final String WEIPAN = "WEIPAN";
    public static final String NEWS = "NEWS";
    public static final String LIVING = "LIVING";//do not modify  tabTag is used in webView js
    public static final String ME = "ME";

    private String currentTag = HOME;

    String TAG = "MainActivity";
    View layouthelp = null;

    String SHAREDPRE_INDEX_HELP = "shared_help_V2";//20150422
    //home键的监听
    private HomeWatcher mHomeWatcher;

    MainActivity context = this;

    public List<Fragment> fragmentList = new ArrayList<Fragment>();
    private FrameLayout frag_container;
    private RadioGroup home_radiogroup;
    private RadioButton tab_home;
    private RadioButton tab_quotations;
    private RadioButton tab_weipan;
    private RadioButton tab_live;
    private RadioButton tab_me;

    public FragmentTabAdapter tabAdapter;
    //单 位是dip
    int dipTabIconW = 28;
    int dipTabIconH = 28;
    int dipTabIconWTrade = 50;
    int dipTabIconHTrade = 44;

//    int dipTabIconW = 28;
//    int dipTabIconH = 26;
//    int dipTabIconWTrade = 44;
//    int dipTabIconHTrade = 40;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
//        setStatusBarTintResource(R.color.white);

        setContentView(R.layout.activity_main_v1);
        //第一次main tab的导航
        initPush();
        //imageLader 的debug
        L.writeLogs(false);
        L.writeDebugLogs(false);

        //现场安全InstanceState维护
        if (savedInstanceState != null) {
            currentTag = savedInstanceState.getString("currentTag");
            Log.v(TAG, "onCreate==savedInstanceState==" + currentTag);
        } else {
            String tag = getIntent().getStringExtra(BaseInterface.TAB_MAIN_PARAME);
            if (tag != null) {
                Log.v(TAG, "tag=" + tag);
                currentTag = tag;
            }
        }
        initViews();

        EventBus.getDefault().register(this);

        // 用户账户信息刷新
        TradeUserInfoData4Situation.getInstance(MainActivity.this, null).loadTradeOrderAndUserInfoData(null, false);

        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(this);
        // 注册广播
        mHomeWatcher.startWatch();

        //检查是否显示广告
        AdsUtil.checkAds(context);

        //添加一个空的handler,处理异步任务
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                //自动更新检测
                new CheckUpgradeInfoTask(context, CheckUpgradeInfoTask.FROM_MAIN).execute();
                //手机imei
//                AppCollectUtil.collectIMEI(context);
                //个推cid
                String clientid = PushManager.getInstance().getClientid(context);
                if (!StringUtil.isEmpty(clientid)) {
                    AppCollectUtil.collectPushInfo(context);
                    Log.v(TAG, "clientid=" + clientid);
                } else {
                    Log.v(TAG, "clientid=null");
                }

                Log.v(TAG, "NIMClient.getStatus()=" + NIMClient.getStatus());
                //这样才认为是同一个云信用户
                //用户登录了才能接收云信的信息推送,每次启动的时候检测一下 是否需要登录云信
                if (new UserInfoDao(context).isLogin()
                        && NIMClient.getStatus() != StatusCode.LOGINED) {
                    //异步去登录云信
                    OnLineHelper.getInstance().loginNIMAsync(context);
                }

                if (BaseInterface.SWITCH_BUGLY) {
                    //bugly日志手机
                    CrashReport.initCrashReport(getApplicationContext(), BaseInterface.APPID_BUGLY, false);
                    //必须登录
                    if (new UserInfoDao(context).isLogin()) {
                        String userId = new UserInfoDao(context).queryUserInfo().getUserId();
                        CrashReport.setUserId(userId);
                    }
                }

//                if (new UserInfoDao(context).isLogin()) {
//                    int state = new UserInfoDao(context).queryUserInfo().getAuthStatus();
//                    if (state != CardAuthObj.STATUS_SUCCESS) {
//                        //启动的时候， 已经登录并且没认证通过的用户检查一次状态
//                        AuthDataHelp.checkStatus(context, null);
//                    }
//                }
                return false;
            }
        });

        PushExtraObj productNoticeNotify = (PushExtraObj) getIntent().getSerializableExtra("productNoticeNotify");
        if (productNoticeNotify != null) {
            ProductNoticeNotifyActivity.startProductNoticeActivity(context, productNoticeNotify);
        }


        /*启动视频小窗口service*/
        startFloat();
        /*小米手机需申请小窗口权限*/
        FloatPermission floatPermission = new FloatPermission(context);
        floatPermission.requestPermission();


    }

//    @Override
//    public int getStatusBarTintResource() {
//        return R.color.sys_nav_bar_color;
//    }

    private void initViews() {
        frag_container = (FrameLayout) findViewById(R.id.frag_container);
        home_radiogroup = (RadioGroup) findViewById(R.id.home_radiogroup);

        tab_home = (RadioButton) findViewById(R.id.tab_home);
        changeBounds(tab_home, dipTabIconW, dipTabIconH);
        tab_quotations = (RadioButton) findViewById(R.id.tab_quotations);
        changeBounds(tab_quotations, dipTabIconW, dipTabIconH);
        tab_weipan = (RadioButton) findViewById(R.id.tab_weipan);
        changeBounds(tab_weipan, dipTabIconWTrade, dipTabIconHTrade);
        changeBounds(tab_weipan, dipTabIconW, dipTabIconH);

        tab_live = (RadioButton) findViewById(R.id.tab_live);
        changeBounds(tab_live, dipTabIconW, dipTabIconH);
        tab_me = (RadioButton) findViewById(R.id.tab_me);
        changeBounds(tab_me, dipTabIconW, dipTabIconH);

        fragmentList.add(new HomeFragmentV1());
        fragmentList.add(new OptionalFragment());
        fragmentList.add(new HomeTradeFrag());
        fragmentList.add(new IndexLiveFragment());
        fragmentList.add(new MeFragment());

        //处理后台开关，屏蔽微盘tab,后台配置的
        if (!WeipanConfig.isShowWeipan(context)) {
            tab_weipan.setVisibility(View.GONE);
        }


        Log.v(TAG, "onCreate==initViews==" + currentTag);

        ((RadioButton) home_radiogroup.getChildAt(FragmentTabAdapter.tabTag.get(currentTag))).setChecked(true);


        tabAdapter = new FragmentTabAdapter(this, fragmentList, R.id.frag_container, home_radiogroup, FragmentTabAdapter.tabTag.get(currentTag));
        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                switch (index) {
                    case 0:
                        tabAdapter.setCurrentTag(MainActivity.HOME);
                        MyAppMobclickAgent.onEvent(MainActivity.this, UmengMobClickConfig.PAGE_MAIN_TAB, "HOME");
                        break;
                    case 1:
                        tabAdapter.setCurrentTag(MainActivity.MARKET);
                        MyAppMobclickAgent.onEvent(MainActivity.this, UmengMobClickConfig.PAGE_MAIN_TAB, "MARKET");
                        break;
                    case 2:
                        tabAdapter.setCurrentTag(MainActivity.WEIPAN);
                        MyAppMobclickAgent.onEvent(MainActivity.this, UmengMobClickConfig.PAGE_MAIN_TAB, "WEIPAN");
                        break;
                    case 3:
                        tabAdapter.setCurrentTag(MainActivity.LIVING);
                        MyAppMobclickAgent.onEvent(MainActivity.this, UmengMobClickConfig.PAGE_MAIN_TAB, "LIVING");
                        break;
                    case 4:
                        tabAdapter.setCurrentTag(MainActivity.ME);
                        MyAppMobclickAgent.onEvent(MainActivity.this, UmengMobClickConfig.PAGE_MAIN_TAB, "ME");
                        break;
                }
//                UNavConfig.initBtnHomeAct(context, tabAdapter.getCurrentTag());
            }
        });
        tabAdapter.setCurrentTag(currentTag);
    }

    private void changeBounds(RadioButton radioButton, int width, int height) {
        Drawable[] drawables = radioButton.getCompoundDrawables();
        drawables[1].setBounds(0, 0, Utils.dip2px(this, width), Utils.dip2px(this, height));
        radioButton.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    //初始化推送消息
    void initPush() {
        String clientid = PushManager.getInstance().getClientid(context);
        if (clientid != null) {
            Log.v(TAG, "clientid=" + clientid);
        } else {
            Log.v(TAG, "clientid=null");
        }

        //个推的推送
        PushManager.getInstance().initialize(this.getApplicationContext(), AppPushCoreService.class);
        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
        // IntentService, 必须在 AndroidManifest 中声明)
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), AppPushIntentService.class);
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        Log.v(TAG, "onNewIntent");
        if (intent != null) {
            final String tag = intent.getStringExtra(BaseInterface.TAB_MAIN_PARAME);
            if (tag != null) {
                currentTag = tag;
                tabAdapter.setCurrentTabByTag(currentTag);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (WEIPAN.equals(tag)) {
                            //滑动到 交易的第几页
                            int index = intent.getIntExtra(BaseInterface.TAB_PARAME_INDEX, 0);
                            ViewPager viewPager = (ViewPager) findViewById(R.id.tradeViewPager);
                            if (viewPager != null)
                                viewPager.setCurrentItem(index);

                        }
                    }
                }, 300);
            }

            // 行情提醒的推送
            PushExtraObj productNoticeNotify = (PushExtraObj) intent.getSerializableExtra("productNoticeNotify");
            if (productNoticeNotify != null) {
                ProductNoticeNotifyActivity.startProductNoticeActivity(context, productNoticeNotify);
            }

            // 爆仓的推送
            PushExtraObj closeOrderNotify = (PushExtraObj) intent.getSerializableExtra("closeOrderNotify");
            if (closeOrderNotify != null) {
                CloseOrderNotifyActivity.startAct(context, closeOrderNotify);
            }

            // 爆仓警告的推送
            PushMsgObj bcjgPushObj = (PushMsgObj) intent.getSerializableExtra("bcjg");
            if (bcjgPushObj != null) {
                TradeFXNoticeActivity.startAct(context, bcjgPushObj);
            }

            // 出金提醒
            PushMsgObj cashoutPushObj = (PushMsgObj) intent.getSerializableExtra("cashout");
            if (cashoutPushObj != null) {
                CashOutNoticeActivity.startCashOutNoticeActivity(context, cashoutPushObj);
            }

            // 订单反馈
            OrderNotifyData orderNotifyData = (OrderNotifyData) intent.getSerializableExtra("orderNotify");
            if (orderNotifyData != null) {
                OrderAndTradeNotifyActivity.startOrderNotifyAct(context, orderNotifyData);
            }
        }
    }

    @Override
    public void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();

        try {
            BaseFragment baseFragment = (BaseFragment) tabAdapter.getCurrentFragment();
            if (baseFragment != null)
                baseFragment.onFragmentVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        Log.v(TAG, "onResume");
        super.onResume();


        try {
            BaseFragment baseFragment = (BaseFragment) tabAdapter.getCurrentFragment();
            if (baseFragment != null)
                baseFragment.onFragmentVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onHomePressed() {
        // TODO
        //清除token
//        AppSetting.getInstance(this).setRefreshTimeWPToken(0L);
//        Toast.makeText(this, "短按Home键,实现自己的逻辑", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onHomeLongPressed() {
        // TODO
//        Toast.makeText(this, "长按Home键,实现自己的逻辑", Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v(TAG, "onSaveInstanceState");
        //注释掉不让系统自己回收fragment
//        super.onSaveInstanceState(outState);

        outState.putInt("position", tabAdapter.getCurrentTab());
        String mTag = tabAdapter.getCurrentTag();
        outState.putString("currentTag", mTag);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();


        if (mHomeWatcher != null)
            mHomeWatcher.stopWatch();
        EventBus.getDefault().unregister(this);

//        CoreOptionUtil.getCoreOptionUtil(getApplicationContext()).stopPush();

        PreferenceSetting.setBoolean(MainActivity.this, PreferenceSetting.ISSHOW_UNLOGIN_VIEW, false);


        destroyFloat();

    }

    private ImageView noticeFlag;

    public ImageView getNoticeFlag() {
        return noticeFlag;
    }


    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 1000) {
                showCusToast("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                NettyClient.getInstance(context).close();
                SystemContext.getInstance(context).close();
                finishAll();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                        System.exit(0);
                    }
                }).start();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 交易所注册完成之后，检测好友红包
     *
     * @param obj
     */
    public void onEventMainThread(JoinVouchObj obj) {
        ShJoinHelp.checkJoinReg(context);
    }

    /**
     * 显示悬浮窗
     * 隐藏悬浮窗
     *
     * @param obj
     */
    public void onEventMainThread(EventFloat obj) {
        Log.v(TAG, "onEventMainThread EventFloat");
        if (obj == null)
            return;
        //隐藏 event
        if (obj.isHideFloat()) {
            mFloatViewService.hideFloat();
            mFloatViewService.releaseVideo();
            return;
        }

        //显示event
        LiveRoomNew roomNew = obj.getData();
        if (StringUtil.isEmpty(roomNew.getRtmpDownstreamAddress())) {
            //没有数据的时候
            mFloatViewService.setData(null);
            mFloatViewService.hideFloat();
            mFloatViewService.releaseVideo();
            return;
        }
        mFloatViewService.setData(roomNew);
        mFloatViewService.showFloat();

    }

    FloatVideoService mFloatViewService;

    /**
     * 开启float
     */
    public void startFloat() {
        try {
            Intent intent = new Intent(this, FloatVideoService.class);
            startService(intent);
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放关联的数据
     */
    public void destroyFloat() {
        try {
            stopService(new Intent(this, FloatVideoService.class));
            unbindService(mServiceConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接到Service
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.v(TAG, "onServiceConnected");
            mFloatViewService = ((FloatVideoService.FloatViewServiceBinder) iBinder).getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mFloatViewService = null;
        }
    };


}

