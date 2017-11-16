package com.trade.eight.moudle.chatroom.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easylife.ten.lib.R;
import com.netease.nim.uikit.common.util.sys.ReflectionUtil;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomKickOutEvent;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomStatusChangeData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.config.WeipanConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.RoomData;
import com.trade.eight.entity.live.LiveRoomNew;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.NettyResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.entity.unifypwd.AccountCheckBindAndRegData;
import com.trade.eight.moudle.baksource.BakSourceService;
import com.trade.eight.moudle.chatroom.ChatRoomNoticeEvent;
import com.trade.eight.moudle.chatroom.DemoCache;
import com.trade.eight.moudle.chatroom.LogoutHelper;
import com.trade.eight.moudle.chatroom.OrderNotifyEvent;
import com.trade.eight.moudle.chatroom.adapter.CustomViewPagerAdapter;
import com.trade.eight.moudle.chatroom.adapter.ProductLiveAdapter;
import com.trade.eight.moudle.chatroom.fragment.CallListFragment;
import com.trade.eight.moudle.chatroom.fragment.ChatRoomMessageFragment;
import com.trade.eight.moudle.chatroom.fragment.FXSListFragment;
import com.trade.eight.moudle.chatroom.helper.ChatRoomMemberCache;
import com.trade.eight.moudle.chatroom.viewholder.ChatRoomViewHolderHelper;
import com.trade.eight.moudle.floatvideo.event.EventFloat;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.netty.NettyClient;
import com.trade.eight.moudle.netty.NettyUtil;
import com.trade.eight.moudle.player.PlayerUtil;
import com.trade.eight.moudle.product.OptionalEvent;
import com.trade.eight.moudle.push.activity.OrderAndTradeNotifyActivity;
import com.trade.eight.moudle.push.entity.PushExtraObj;
import com.trade.eight.moudle.trade.close.TradeCloseLiveDlg;
import com.trade.eight.moudle.trade.create.LivePListDlg;
import com.trade.eight.moudle.trade.create.TradeCreateLiveDlg;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
import com.trade.eight.mpush.coreoption.CoreOptionUtil;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.NoRepeatClickListener;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.product.ProductViewHole4ChatRoom;
import com.trade.eight.tools.refresh.RefreshUtil;
import com.trade.eight.tools.trade.ExchangeConfig;
import com.trade.eight.tools.trade.QuickCloseUtil;
import com.trade.eight.tools.trade.QuickCloseUtil4Live;
import com.trade.eight.tools.trade.QuickTradeCPUtil;
import com.trade.eight.tools.trade.TradeConfig;
import com.trade.eight.view.AppTitleView;
import com.trade.eight.view.RoundProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;


/**
 * 聊天室
 * <p/>
 * 1、视屏播放
 * 2、获取文字喊单
 * 3、登录云信
 * 4、登录聊天室
 * txmp:腾讯的播放器
 */
public class ChatRoomActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    //循环检查直播室是否
    public static final long TIME_CHECK_CHAT_ENABLE = 60000L;
    private final static String EXTRA_ROOM_ID = "ROOM_ID";//房间id
    private final static String EXTRA_CHANNEL_ID = "CHANNEL_ID";//直播室编号
    private final static String EXTRA_SENDPICSTATUS = "SENDPICSTATUS";//是否允许发图片
    /*标记这次离开直播室的时间，用来对比到下一次进入直播室的时间*/
    public static final String ENTER_TAG = "enterTime";
    /*两次进入直播的时间差*/
    public static final long reEnterTime = 3000;
    /*释放资源延时进入直播室*/
    public static final long realseTime = 2000;

    private static final String TAG = ChatRoomActivity.class.getSimpleName();
    ChatRoomActivity context = this;
    QuickTradeCPUtil quickTradeCPUtil;
    QuickCloseUtil4Live quickCloseUtil4Live = null;
    //调试所用 是否刷新行情
    boolean SWITCH_REFRESH = true;
    /**
     * 聊天室基本信息
     */
    private String roomId;
    private String channelId;
    private int sendPicStatus;
    private ChatRoomInfo roomInfo;
    /**
     * 直播页面
     */
    private ChatRoomMessageFragment messageFragment;
    private AbortableFuture<EnterChatRoomResultData> enterRequest;

    private List<View> tabLines = new ArrayList<>();
    private List<View> tabTextView = new ArrayList<>();
    ViewPager viewPager;
    String title;

    //视频文件文件路径
    private String mVideoPath;
    long TIME_DELAY = 5000L;
    AppTitleView titleview_title;
    AppTitleView titleview_control;


    //右边控制拦 头部控制拦
//    View controlLayout, controlTitleView;
    //控制拦的整个view  不隐藏
    View controlRootLayout;
    //没有视频的时候的view
    View emptyView;
    //加载view
    View mLoadingView;
    boolean isHasPlayed = false;//是否已经第一次播放过
    int status = LiveRoomNew.STATUS_OFF;

    RefreshUtil checkChatEnable;
    LinearLayout line_tabs;
    View quickView;
    TextView text_livetrade;
    View line_liveclose;
    TextView text_gomarket; //直播室去行情
    RecyclerView list_product;
    ProductLiveAdapter productLiveAdapter;

    View rel_chatroom_notice;
    RoundProgressBar pro_time;
    TextView text_notice_content;

    String codes = "";
    //    ProductViewList productViewList;
    ProductViewHole4ChatRoom productViewHole4ChatRoom;

    private static final int MESSAGE_ID_RECONNECTING = 0x01;
    //    private MediaController mMediaController;
    private PLVideoView mVideoView;
    /*
    FIT_PARENT全屏的时候上线铺满 左右两边有留出距离没拉伸
    PAVED_PARENT全屏的时候铺满全屏，但是上下被截断一部分视频画面
    */
    private int mDisplayAspectRatio = PLVideoView.ASPECT_RATIO_PAVED_PARENT;
    private boolean mIsActivityPaused = true;
    /*允许播放的最大循环次数*/
    public static int PLAYER_ERROR_COUNT_MAX = 3;
    int playErrorCount = 0;
    QuickCloseUtil quickCloseUtil;

    TradeLoginDlg tradeLoginDlg;
    TradeCreateLiveDlg tradeCreateLiveDlg;
    TradeCloseLiveDlg tradeCloseLiveDlg;


    public static void start(Context context, int status, String roomId, String title, String mVideoPath, String channelId, int sendPicStatus) {
        Intent intent = new Intent();
        intent.setClass(context, ChatRoomActivity.class);
        intent.putExtra(EXTRA_ROOM_ID, roomId);
        intent.putExtra("mVideoPath", mVideoPath);
        intent.putExtra("title", title);
        intent.putExtra("status", status);
        intent.putExtra(EXTRA_CHANNEL_ID, channelId);
        intent.putExtra(EXTRA_SENDPICSTATUS, sendPicStatus);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void start(Context context, LiveRoomNew roomNew) {
        Intent intent = getMyIntent(context, roomNew);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static Intent getMyIntent(Context context, LiveRoomNew roomNew) {
        Intent intent = new Intent();
        intent.setClass(context, ChatRoomActivity.class);
        intent.putExtra("obj", roomNew);
        return intent;
    }

    //初始化的交易所，快速建仓平仓都是哈贵所的，如果在首页交易大厅做了处理，这里可以不管
    String sourceCode;
    LiveRoomNew roomNew;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStatusBarTintResource(R.color.black);
        setContentView(R.layout.act_chat_room);
        sourceCode = TradeConfig.getCurrentTradeCode(context);


        roomNew = (LiveRoomNew) getIntent().getSerializableExtra("obj");
        if (roomNew == null) {
            showCusToast("房间信息获取失败！");
            finish();
            return;
        }
        roomId = roomNew.getChatRoomId();
        channelId = roomNew.getChannelId();
        sendPicStatus = roomNew.getSendPicStatus();
        mVideoPath = roomNew.getRtmpDownstreamAddress();
        title = roomNew.getChannelName();
        status = roomNew.getChannelStatus();


        initViews();
        // 注册监听
        registerObservers(true);

        //1、先登录云信， 2、云信登录成功再登录聊天室
        if (System.currentTimeMillis() - PreferenceSetting.getLong(context, ENTER_TAG) < reEnterTime) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.v(TAG, "postDelayed login");
                    login();
                }
            }, realseTime);
        } else {
            Log.v(TAG, "oncreate login");
            login();
        }

        initVideo();

//        initNavView();

//        initData(false);
        initCheckRefresh();

        checkTradeConfig();

        initProductViewPager();

        try {
            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 每天只显示一次
        String cacheStr = PreferenceSetting.getCacheString(this, new UserInfoDao(this).queryUserInfo().getUserId() + roomId, "");
        if (!cacheStr.equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
            showNoticePop(null, 10);
            PreferenceSetting.setCacheString(this, new UserInfoDao(this).queryUserInfo().getUserId() + roomId, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }

        /*退出隐藏视频*/
        EventFloat.postHideEvent();


    }

    /**
     * 蒙板导航
     */
    void initNavView() {
        final View navView = findViewById(R.id.navView);
        if (navView == null)
            return;
        if (StringUtil.isEmpty(PreferenceSetting.getSharedPreferences(context, TAG, "nav_v2"))) {
            navView.setVisibility(View.VISIBLE);
            //处理和蒙板重叠情况
            if (quickView != null)
                quickView.setVisibility(View.GONE);
            //大图需要显示的时候才显示 减少内存
            try {
                navView.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_nav_video));
                navView.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_nav_video));
            } catch (Exception e) {
                e.printStackTrace();
                if (quickView != null)
                    quickView.setVisibility(View.VISIBLE);
                navView.setVisibility(View.GONE);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                if (quickView != null)
                    quickView.setVisibility(View.VISIBLE);
                navView.setVisibility(View.GONE);
            }
            navView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (quickView != null)
                        quickView.setVisibility(View.VISIBLE);
                    PreferenceSetting.setSharedPreferences(context, TAG, "nav_v2", "inited");
                    navView.setVisibility(View.GONE);
                }
            });
        } else {
            navView.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean isActivityFitsSystemWindows() {
        return false;
    }

    /**
     * 设置当前的交易所名字
     */
    void setExchangeText() {
//        final TextView btn_exchange = (TextView) findViewById(R.id.btn_exchange);
//        if (btn_exchange != null) {
//            btn_exchange.setText(TradeConfig.getCurrentTradeName(context).replace("所", ""));
//        }
    }

    void initViews() {

        titleview_title = (AppTitleView) findViewById(R.id.titleview_title);
        titleview_title.setBaseActivity(ChatRoomActivity.this);
        titleview_title.setAppCommTitle(title);
        titleview_title.setBackViewResource(R.drawable.img_goback_new_grey);
        titleview_title.setSpiltLineDisplay(false);
        titleview_title.setRightImgBtn_1(true, R.drawable.live_btn_openlive);// 按钮icon
        titleview_title.setRightImgBtn(true, R.drawable.img_productdetail_fullscreen);// 按钮icon
        titleview_title.setTitleTextColor(R.color.color_999999);// title字的颜色
        titleview_title.setRootViewBG(R.color.white);// 背景条颜色
        if (titleview_title != null) {
            titleview_title.setVisibility(View.GONE);
        }
        titleview_title.setRightImgCallback(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                    changeScreen(false);
                    MyAppMobclickAgent.onEvent(ChatRoomActivity.this, UmengMobClickConfig.PAGE_LIVE_CLOSELAND);
                } else {
                    //切换成横屏
                    MyAppMobclickAgent.onEvent(ChatRoomActivity.this, UmengMobClickConfig.PAGE_LIVE_OPENLAND);
                    changeScreen(true);
                }
                return false;
            }
        });
        titleview_title.setRightImgCallback_1(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                MyAppMobclickAgent.onEvent(ChatRoomActivity.this, UmengMobClickConfig.PAGE_LIVE_OPENVIDEO);
                //重新播放
                View videLayout = findViewById(R.id.videLayout);
                videLayout.setVisibility(View.VISIBLE);
                if (titleview_title != null) {
                    titleview_title.setVisibility(View.GONE);
                }
                mLoadingView.setVisibility(View.VISIBLE);
                startVideo();
//                setStatusBarTintResource(R.color.black);
                list_product.setBackgroundColor(getResources().getColor(R.color.black));
                return false;
            }
        });
//        setAppCommonTitle(title);
//        TextView tv_hintTitle = (TextView) findViewById(R.id.tv_hintTitle);
//        if (tv_hintTitle != null)
//            tv_hintTitle.setText(title);
//        View backLayout = findViewById(R.id.titleBarView);
//        if (backLayout != null)
//            backLayout.setVisibility(View.GONE);

        ArrayList<Fragment> pages = new ArrayList<Fragment>();
        CallListFragment callListFragment = new CallListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("roomId", roomId);
        callListFragment.setArguments(bundle);


        messageFragment = new ChatRoomMessageFragment();
        Bundle bundle02 = new Bundle();
        bundle02.putSerializable("liveroomnew", roomNew);
        messageFragment.setArguments(bundle02);

        FXSListFragment fxsListFragment = new FXSListFragment();
        Bundle bundle03 = new Bundle();
        bundle03.putSerializable("liveroomnew", roomNew);
        fxsListFragment.setArguments(bundle03);

        pages.add(messageFragment);
        pages.add(callListFragment);
        pages.add(fxsListFragment);

        quickView = findViewById(R.id.quickView);
        emptyView = findViewById(R.id.emptyView);

        if (quickView != null) {
            if (WeipanConfig.isShowWeipan(context)) {
                quickView.setVisibility(View.VISIBLE);
            } else {
                quickView.setVisibility(View.GONE);
            }
        }

        rel_chatroom_notice = findViewById(R.id.rel_chatroom_notice);
        rel_chatroom_notice.setVisibility(View.GONE);
        pro_time = (RoundProgressBar) findViewById(R.id.pro_time);
        text_notice_content = (TextView) findViewById(R.id.text_notice_content);

        // Initialize the ViewPager and set an adapter
        viewPager = (ViewPager) findViewById(R.id.chat_room_viewpager);
        CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(getSupportFragmentManager(), pages);

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.setOnPageChangeListener(this);


        //init tabs
        line_tabs = (LinearLayout) findViewById(R.id.line_tabs);

        View tab01 = findViewById(R.id.tab01);
        View tab02 = findViewById(R.id.tab02);
        View tab03 = findViewById(R.id.tab03);

        View line1 = findViewById(R.id.line1);
        View line2 = findViewById(R.id.line2);
        View line3 = findViewById(R.id.line3);

        tabLines.add(line1);
        tabLines.add(line2);
        tabLines.add(line3);

        View tv_video = findViewById(R.id.tv_video);
        View tv_advice = findViewById(R.id.tv_advice);
        View tv_fxs = findViewById(R.id.tv_fxs);
        tabTextView.add(tv_video);
        tabTextView.add(tv_advice);
        tabTextView.add(tv_fxs);

        setSelectLine(0);

        tab01.setOnClickListener(this);
        tab02.setOnClickListener(this);
        tab03.setOnClickListener(this);

        list_product = (RecyclerView) findViewById(R.id.list_product);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        list_product.setLayoutManager(linearLayoutManager);


        text_livetrade = (TextView) findViewById(R.id.text_livetrade);
        text_livetrade.setOnClickListener(new NoRepeatClickListener() {
            @Override
            public void onNoRepeatClick(View v) {

//                OrderAndTradeNotifyActivity.startLoginNotifyAct(context, 1, "");
                MyAppMobclickAgent.onEvent(ChatRoomActivity.this, UmengMobClickConfig.PAGE_LIVE_CREATE);

                //本地先验证token 过期了
                if (!TradeHelp.isTokenEnable(ChatRoomActivity.this)) {
                    //dialog 没有显示`
//                showDialog();
                    if (tradeLoginDlg == null) {
                        tradeLoginDlg = new TradeLoginDlg(ChatRoomActivity.this);
                    }
                    if (!tradeLoginDlg.isShowingDialog()) {
                        tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                    }
                    return;
                }

                if (tradeCreateLiveDlg != null && tradeCreateLiveDlg.isShowingDialog()) {
                    return;
                }
                if (tradeCreateLiveDlg == null) {
                    tradeCreateLiveDlg = new TradeCreateLiveDlg(ChatRoomActivity.this);
                }
                if (!tradeCreateLiveDlg.isShowingDialog()) {
                    tradeCreateLiveDlg.showDialog(R.style.dialog_trade_ani);
                }
            }
        });
        line_liveclose = findViewById(R.id.line_liveclose);
        line_liveclose.setOnClickListener(new NoRepeatClickListener() {
            @Override
            public void onNoRepeatClick(View v) {
                MyAppMobclickAgent.onEvent(ChatRoomActivity.this, UmengMobClickConfig.PAGE_LIVE_CLOSE);

                //本地先验证token 过期了
                if (!TradeHelp.isTokenEnable(ChatRoomActivity.this)) {
                    //dialog 没有显示`
//                showDialog();
                    if (tradeLoginDlg == null) {
                        tradeLoginDlg = new TradeLoginDlg(ChatRoomActivity.this);
                    }
                    if (!tradeLoginDlg.isShowingDialog()) {
                        tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                    }
                    return;
                }
                if (tradeCloseLiveDlg != null && tradeCloseLiveDlg.isShowingDialog()) {
                    return;
                }
                if (tradeCloseLiveDlg == null) {
                    tradeCloseLiveDlg = new TradeCloseLiveDlg(ChatRoomActivity.this);
                }
                if (!tradeCloseLiveDlg.isShowingDialog()) {
                    tradeCloseLiveDlg.showDialog(R.style.dialog_trade_ani);
                }
            }
        });
        text_gomarket = (TextView) findViewById(R.id.text_gomarket);
        text_gomarket.setOnClickListener(new View.OnClickListener() {// 此处需要  直播室小窗口模式
            @Override
            public void onClick(View v) {

//                OrderAndTradeNotifyActivity.startLoginNotifyAct(ChatRoomActivity.this,1,"测试");

                MyAppMobclickAgent.onEvent(ChatRoomActivity.this, UmengMobClickConfig.PAGE_LIVE_OPTION);
                Intent intent = new Intent();
                intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.MARKET);
                intent.setClass(ChatRoomActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }


    /**
     * 检查交易所列表
     */
    public void checkTradeConfig() {
        //检查交易所列表
        if (ExchangeConfig.getInstance().isInited(context)) {
            ExchangeConfig.getInstance().init(context, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    setExchangeText();
                    return false;
                }
            });
        }

    }

    Handler handlerIcon = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            controlLayout.clearAnimation();
//            controlLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.video_control_out));
//            controlLayout.setVisibility(View.GONE);

            titleview_control.clearAnimation();
            titleview_control.startAnimation(AnimationUtils.loadAnimation(context, R.anim.out_to_top));
            titleview_control.setVisibility(View.GONE);

        }
    };


    void initVideo() {
        mVideoView = (PLVideoView) findViewById(R.id.videoView);


        controlRootLayout = findViewById(R.id.controlRootLayout);
//        controlLayout = findViewById(R.id.controlLayout);
//        controlTitleView = findViewById(R.id.controlTitleView);
        titleview_control = (AppTitleView) findViewById(R.id.titleview_control);
        titleview_control.setSpiltLineDisplay(false);
        titleview_control.setBaseActivity(ChatRoomActivity.this);
        titleview_control.setBackViewResource(R.drawable.img_goback_new_grey);
        titleview_control.setAppCommTitle(title);
        titleview_control.setRightImgBtn_1(true, R.drawable.live_btn_closelive);// 按钮icon
        titleview_control.setRightImgBtn(true, R.drawable.img_productdetail_fullscreen);// 按钮icon
        titleview_control.setTitleTextColor(R.color.color_999999);// title字的颜色
        titleview_control.setRootViewBG(R.color.black_80);// 背景条颜色
        if (titleview_control != null) {
            titleview_control.setVisibility(View.GONE);
        }
        titleview_control.setRightImgCallback(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                    MyAppMobclickAgent.onEvent(ChatRoomActivity.this, UmengMobClickConfig.PAGE_LIVE_CLOSELAND);
                    changeScreen(false);
                } else {
                    MyAppMobclickAgent.onEvent(ChatRoomActivity.this, UmengMobClickConfig.PAGE_LIVE_OPENLAND);
                    //切换成横屏
                    changeScreen(true);
                }
                return false;
            }
        });
        titleview_control.setRightImgCallback_1(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                MyAppMobclickAgent.onEvent(ChatRoomActivity.this, UmengMobClickConfig.PAGE_LIVE_CLOSEVIDEO);

                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                    //当前为横屏 直接切换成竖屏
                    changeScreen(false);
                    return false;
                }
                long time = System.currentTimeMillis();
                Log.v(TAG, "time=" + time);
                //视频资源释放  放在线程中去  防止ANR
                releaseVideo();
                //5 s
                Log.v(TAG, "take time=" + (System.currentTimeMillis() - time) + " sec=" + ((System.currentTimeMillis() - time) / 1000));

                View videLayout = findViewById(R.id.videLayout);
                videLayout.setVisibility(View.GONE);
                if (titleview_title != null) {
                    titleview_title.setVisibility(View.VISIBLE);
                }
//                setStatusBarTintResource(R.color.white);
                list_product.setBackgroundColor(getResources().getColor(R.color.c_f6f6f6));
                return false;
            }
        });


        mLoadingView = findViewById(R.id.buffering_prompt);

        mVideoView.setBufferingIndicator(mLoadingView);
        // 1 -> hw codec enable, 0 -> disable [recommended]
        PlayerUtil.setOptions(mVideoView, PlayerUtil.MY_DEFAULT_MEDIA_CODEC);

        mVideoView.setDisplayAspectRatio(mDisplayAspectRatio);
        // Set some listeners
        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mVideoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnSeekCompleteListener(mOnSeekCompleteListener);
        mVideoView.setOnErrorListener(mOnErrorListener);

        mVideoView.setVideoPath(mVideoPath);
        startVideo();
//        mLivePlayer.setPlayListener(new ITXLivePlayListener() {
//            @Override
//            public void onPlayEvent(int playStatus, Bundle bundle) {
//                android.util.Log.v(TAG, "status=" + status);
//                if (playStatus == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
//                    mLoadingView.setVisibility(View.GONE);
//                    isHasPlayed = true;
//                }
////                else if (playStatus == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
////                    //效果会慢点
////                    //PLAY_ERR_NET_DISCONNECT	-2301	网络断连,且经多次重连抢救无效,可以放弃治疗,更多重试请自行重启播放
////                    if (status == LiveRoom.STATUS_OFF) {
////                        emptyView.setVisibility(View.VISIBLE);
////                    } else {
////                        DialogUtil.showMsgDialog(context, "该视频暂时不能播放", "确定");
////                        emptyView.setVisibility(View.GONE);
////                    }
////                    buffering_prompt.setVisibility(View.GONE);
////                }
//                else if (!isHasPlayed && playStatus == TXLiveConstants.PLAY_WARNING_RECONNECT) {
//                    //从列表传入的状态是关闭的
//                    if (status == LiveRoomNew.STATUS_OFF) {
//                        emptyView.setVisibility(View.VISIBLE);
//                    } else {
//                        DialogUtil.showMsgDialog(context, "该视频暂时不能播放", "确定");
//                        emptyView.setVisibility(View.GONE);
//                    }
//                    pauseVideo();
//                    mLoadingView.setVisibility(View.GONE);
//                } else if (playStatus == TXLiveConstants.PLAY_EVT_PLAY_END) {
//                    emptyView.setVisibility(View.VISIBLE);
//                    mLoadingView.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onNetStatus(Bundle bundle) {
//
//            }
//        });

        //播放时间保持常亮
        mVideoView.setKeepScreenOn(true);

        //init visible
//        controlLayout.setVisibility(View.VISIBLE);
        titleview_control.setVisibility(View.VISIBLE);
        handlerIcon.sendEmptyMessageDelayed(0, TIME_DELAY);

        controlRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleview_control.getVisibility() != View.VISIBLE) {
                    //马上显示control
                    handlerIcon.removeMessages(0);
//                    controlLayout.clearAnimation();
//                    controlLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.video_control_in));
//                    controlLayout.setVisibility(View.VISIBLE);

                    titleview_control.clearAnimation();
                    titleview_control.startAnimation(AnimationUtils.loadAnimation(context, R.anim.in_from_top));
                    titleview_control.setVisibility(View.VISIBLE);

                    handlerIcon.sendEmptyMessageDelayed(0, TIME_DELAY);
                } else {
                    //隐藏
//                    controlLayout.clearAnimation();
//                    controlLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.video_control_out));
//                    controlLayout.setVisibility(View.GONE);

                    titleview_control.clearAnimation();
                    titleview_control.startAnimation(AnimationUtils.loadAnimation(context, R.anim.out_to_top));
                    titleview_control.setVisibility(View.GONE);
                }
            }
        });

    }

    public void controllClick(View view) {
        /*if (view.getId() == R.id.btn_fullscreen) {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                changeScreen(false);
                findViewById(R.id.tv_fullscreen).setBackgroundResource(R.drawable.icon_video_to_full);
            } else {
                //切换成横屏
                changeScreen(true);
                findViewById(R.id.tv_fullscreen).setBackgroundResource(R.drawable.icon_video_to_small);
            }

        } else if (view.getId() == R.id.closeVideoView) {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                //当前为横屏 直接切换成竖屏
                changeScreen(false);
                return;
            }
            long time = System.currentTimeMillis();
            Log.v(TAG, "time=" + time);
//            mVideoView.pause();
//            mVideoView.setMute(false);
//            mVideoView.release_resource();

            //视频资源释放  放在线程中去  防止ANR
            releaseVideo();
            //5 s
            Log.v(TAG, "take time=" + (System.currentTimeMillis() - time) + " sec=" + ((System.currentTimeMillis() - time) / 1000));


            View videLayout = findViewById(R.id.videLayout);
            videLayout.setVisibility(View.GONE);
//            View backLayout = findViewById(R.id.titleBarView);
//            if (backLayout != null)
//                backLayout.setVisibility(View.VISIBLE);

            if(titleview_title!=null){
                titleview_title.setVisibility(View.VISIBLE);
            }
        } else if (view.getId() == R.id.openVideoView) {
            //重新播放
            View videLayout = findViewById(R.id.videLayout);
            videLayout.setVisibility(View.VISIBLE);
//            View backLayout = findViewById(R.id.titleBarView);
//            if (backLayout != null)
//                backLayout.setVisibility(View.GONE);
            if(titleview_title!=null){
                titleview_title.setVisibility(View.VISIBLE);
            }

            mLoadingView.setVisibility(View.VISIBLE);
            startVideo();

        } else if (view.getId() == R.id.backViewRel) {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                //当前为横屏 直接切换成竖屏
                changeScreen(false);
                return;
            }
            finish();
            logOutChatRoom();

//            DialogUtil.showConfirmDlg(context, "确定退出直播室？", new Handler.Callback() {
//                @Override
//                public boolean handleMessage(Message message) {
//                    finish();
//                    logOutChatRoom();
//                    return false;
//                }
//            });

        }*/
    }

    /**
     * 释放video资源
     */
    void releaseVideo() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    /**
     * 直播暂停 就是停止 然后重新播放
     */
    void pauseVideo() {
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    void startVideo() {
        mVideoView.setVideoPath(mVideoPath);
        mVideoView.start();
        emptyView.setVisibility(View.GONE);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mLivePlayer.startPlay(mVideoPath, TXVideoConfig.getPlayType(mVideoPath));
//            }
//        }, 1000);
        if (!mVideoView.isPlaying())
            mLoadingView.setVisibility(View.VISIBLE);
    }


    /**
     * 视频横竖屏切换
     *
     * @param toScreen
     */
    void changeScreen(boolean toScreen) {
        if (toScreen) {
            //切换到横屏
            findViewById(R.id.contentView).setVisibility(View.GONE);
//            findViewById(R.id.closeVideoView).setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//            mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_FIT_PARENT);//FIT_PARENT全屏的时候上线铺满 左右两边有留出距离没拉伸
//            mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);//PAVED_PARENT全屏的时候铺满全屏，但是上下被截断一部分视频画面

        } else {
            //切换到竖屏
            findViewById(R.id.contentView).setVisibility(View.VISIBLE);
//            findViewById(R.id.closeVideoView).setVisibility(View.VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        }

    }

    @Override
    public void startActivity(Intent intent) {
        Log.v(TAG, "startActivity");
        isStartAct = true;
        super.startActivity(intent);

    }

    /**
     * 1、处理查看图片的时候允许播放视频
     * 2、按home键，电源键的时候停止播放
     * 掉用了startActivity 就允许后台播放
     */
    boolean isStartAct = false;

    @Override
    public void onPause() {
        Log.d(TAG, " onPause"+isStartAct);
        super.onPause();

        //允许播放
        if (!isStartAct) {
            pauseVideo();
        }

    }

    @Override
    public void onStart() {
        Log.d(TAG, " onStart");
        super.onStart();

    }

    @Override
    public void onResume() {
        Log.d(TAG, " onResume"+isStartAct);
        checkChatEnable.start();
        super.onResume();
        //是否需要重新播放，必须是播放控件存在的时候才开始播放
        if (!isStartAct) {
            View videLayout = findViewById(R.id.videLayout);
            if (videLayout.getVisibility() == View.VISIBLE)
                startVideo();
        }
        //还原初始化状态
        isStartAct = false;

    }

    /**
     * 订单异步反馈
     *
     * @param orderNotifyEvent
     */
    public void onEventMainThread(OrderNotifyEvent orderNotifyEvent) {
        // 主要防止重新加载
        isStartAct = true;
        Log.d(TAG, " onEventMainThread=== isStartAct = true");

    }

    @Override
    public void onStop() {
        Log.d(TAG, " onStop");
        super.onStop();
        if (!isStartAct)
            releaseVideo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        checkChatEnable.stop();
        releaseVideo();
        registerObservers(false);
        ChatRoomMemberCache.getInstance().clear();

        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            LogoutHelper.logout();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int pageIndex) {
        setSelectLine(pageIndex);


    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    /**
     * 设置选中的线tab
     *
     * @param pageIndex
     */
    void setSelectLine(int pageIndex) {
        for (int i = 0; i < tabLines.size(); i++) {
            if (i == pageIndex) {
                tabLines.get(i).setVisibility(View.VISIBLE);
                if (i < tabTextView.size()) {
                    tabTextView.get(i).setSelected(true);
                }

            } else {
                tabLines.get(i).setVisibility(View.GONE);
                if (i < tabTextView.size()) {
                    tabTextView.get(i).setSelected(false);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tab01) {

            MyAppMobclickAgent.onEvent(this, UmengMobClickConfig.PAGE_LIVE_CHAT);
            viewPager.setCurrentItem(0);
        } else if (id == R.id.tab02) {

            MyAppMobclickAgent.onEvent(this, UmengMobClickConfig.PAGE_LIVE_CALLLIST);
            viewPager.setCurrentItem(1);
        } else if (id == R.id.tab03) {

            MyAppMobclickAgent.onEvent(this, UmengMobClickConfig.PAGE_LIVE_FXS);
            viewPager.setCurrentItem(2);
        }
    }


    @Override
    public void onBackPressed() {
//        if (messageFragment == null || !messageFragment.onBackPressed()) {
//            super.onBackPressed();
//        }
//        logOutChatRoom();
        invokeFragmentManagerNoteStateNotSaved();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void invokeFragmentManagerNoteStateNotSaved() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ReflectionUtil.invokeMethod(getFragmentManager(), "noteStateNotSaved", null);
        }
    }


    public void doMyfinish() {
        //表情是否开启了
        View emoticon_picker_view = findViewById(R.id.emoticon_picker_view);
        if (emoticon_picker_view != null) {
            if (emoticon_picker_view.getVisibility() == View.VISIBLE) {
                emoticon_picker_view.setVisibility(View.GONE);
                return;
            }
        }
        View actionsLayout = findViewById(R.id.actionsLayout);
        if (actionsLayout != null) {
            if (actionsLayout.getVisibility() == View.VISIBLE) {
                actionsLayout.setVisibility(View.GONE);
                return;
            }
        }

        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
            //当前为横屏 直接切换成竖屏
            changeScreen(false);
            return;
        }

        finish();
        logOutChatRoom();

//        DialogUtil.showConfirmDlg(context, "确定退出直播室？", new Handler.Callback() {
//            @Override
//            public boolean handleMessage(Message message) {
//                finish();
//                logOutChatRoom();
//                return false;
//            }
//        });
    }


    @Override
    public void finish() {
        super.finish();
        //防止时间过短，还没有释放完资源，造成进入云信登录失败，出现错误码
        PreferenceSetting.setLong(context, ENTER_TAG, System.currentTimeMillis());
        //还原sourceCode
//        TradeConfig.setCurrentTradeCode(context, sourceCode);
        if (mVideoView != null) {
            //关闭act的时候，通知首页的act显示悬浮窗口
            if (mVideoView.isPlaying()) {
                EventBus.getDefault().post(new EventFloat(roomNew));
            }
        }

    }

    private AbortableFuture<LoginInfo> loginRequest;

    /**
     * 登录云信系统
     */
    void login() {
        if (!new UserInfoDao(context).isLogin())
            return;
        final UserInfo userInfo = new UserInfoDao(context).queryUserInfo();
        if (userInfo == null)
            return;
//        if (dialog == null)
//            dialog = DialogUtil.getLoadingDlg(context);
//        if (!dialog.isShowing())
//            dialog.show();
//        dialog.setCancelable(true);

        loginRequest = NIMClient.getService(AuthService.class)
                .login(new LoginInfo(userInfo.getAccId(), userInfo.getToken_IM()));
        loginRequest.setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                Log.v(TAG, "loginRequest onSuccess success");
//                hideDialog();
                DemoCache.setAccount(userInfo.getAccId());
                enterRoom();

                //test  获取在线人数
//                getMembers(data.getRoomId(), MemberQueryType.ONLINE_NORMAL, 0, 0);
            }

            @Override
            public void onFailed(int code) {
                Log.v(TAG, "loginRequest onFailed" + code);
                if (dialog != null)
                    dialog.dismiss();
//                http://dev.netease.im/docs/product/%E9%80%9A%E7%94%A8/%E7%8A%B6%E6%80%81%E7%A0%81
                if (code == 302 || code == 404) {
                    showCusToast(getResources().getString(R.string.login_failed));
                } else if (code == 408 || code == 415) {
                    showCusToast("网络连接失败，请重试！");
                } else if (code == 422) {
                    showCusToast("您的账号已被禁用，请联系客服！");
                } else {
                    showCusToast("聊天室登录失败，错误码：" + code);
                }
            }

            @Override
            public void onException(Throwable exception) {
                Log.v(TAG, "loginRequest onException");
                exception.printStackTrace();
                if (dialog != null)
                    dialog.dismiss();
            }
        });
    }

    Dialog dialog;

    private void enterRoom() {
//        if (dialog == null)
//            dialog = DialogUtil.getLoadingDlg(context);
//        if (!dialog.isShowing())
//            dialog.show();
//        dialog.setCancelable(true);

        EnterChatRoomData data = new EnterChatRoomData(roomId);
        if (new UserInfoDao(context).isLogin()) {
            //设置扩展字段 每次收到信息都会有这个字段
            UserInfo userInfo = new UserInfoDao(context).queryUserInfo();
            if (data.getExtension() == null) {
                data.setExtension(new HashMap<String, Object>());
            }
            data.getExtension().put(UserInfo.ULEVELNUM, userInfo.getLevelNum());
        }
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>() {
            @Override
            public void onSuccess(EnterChatRoomResultData result) {
                Log.v(TAG, "enterRoom onSuccess");
                if (result == null)
                    return;
                onLoginDone();
                roomInfo = result.getRoomInfo();
                if (roomInfo == null)
                    return;
                ChatRoomMember member = result.getMember();
                member.setRoomId(roomInfo.getRoomId());
                ChatRoomMemberCache.getInstance().saveMyMember(member);
                ChatRoomViewHolderHelper.currentRoomId = roomId;
//                initChatRoomFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("obj", roomInfo);
                try {
                    bundle.putInt("count", Integer.parseInt(roomNew.getOnlineNumber()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bundle.putInt("channelStatus", roomNew.getChannelStatus());
                //标记是从小窗口过来的
                bundle.putBoolean("isFloat", getIntent().getBooleanExtra("isFloat", false));
                initMessageFragment(bundle);
            }

            @Override
            public void onFailed(int code) {
                Log.v(TAG, "enterRoom enterRequest onFailed code=" + code);
                onLoginDone();
                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
                    showCusToast("你已被拉入黑名单，不能再进入");
//                    Toast.makeText(ChatRoomActivity.this, "你已被拉入黑名单，不能再进入", Toast.LENGTH_SHORT).show();
                } else {
                    if (code == ResponseCode.RES_ENONEXIST) {
                        //聊天室不存在，客户端提示关闭信息
                        showCusToast("聊天室已关闭");
                    } else
                        showCusToast("进入聊天室错误, code=" + code);
//                    Toast.makeText(ChatRoomActivity.this, "enter chat room failed, code=" + code, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(ChatRoomActivity.this, "进入直播室错误, code=" + code, Toast.LENGTH_SHORT).show();
                }
                //可能视频能放 不能用文字直播
//                finish();
            }

            @Override
            public void onException(Throwable exception) {
                onLoginDone();
                showCusToast("enterRoom exception, e=" + exception.getMessage());
//                Toast.makeText(ChatRoomActivity.this, "enter chat room exception, e=" + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                finish();
            }
        });
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeOnlineStatus(onlineStatus, register);
        NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver, register);
    }

    private void logOutChatRoom() {
//        NettyClient.getInstance(context).stopWrite();
        // 长连接断开
        CoreOptionUtil.getCoreOptionUtil(getApplicationContext()).stopQuotationMessage();

        NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
        clearChatRoom();
//        mVideoView.pause();
//        mVideoView.release_resource();
        releaseVideo();
    }

    public void clearChatRoom() {
        ChatRoomMemberCache.getInstance().clearRoomCache(roomId);
//        finish();
    }

    Observer<ChatRoomStatusChangeData> onlineStatus = new Observer<ChatRoomStatusChangeData>() {
        @Override
        public void onEvent(ChatRoomStatusChangeData chatRoomStatusChangeData) {
            Log.v(TAG, "chatRoomStatusChangeData.status=" + chatRoomStatusChangeData.status);
            if (chatRoomStatusChangeData.status == StatusCode.CONNECTING) {
//                DialogMaker.updateLoadingMessage("连接中...");
//                if (dialog == null)
//                    dialog = DialogUtil.getLoadingDlg(context);
//                if (!dialog.isShowing())
//                    dialog.show();

            } else if (chatRoomStatusChangeData.status == StatusCode.UNLOGIN) {
//                Toast.makeText(ChatRoomActivity.this, R.string.nim_status_unlogin, Toast.LENGTH_SHORT).show();
//                showCusToast(getResources().getString(R.string.nim_status_unlogin));
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINING) {
//                DialogMaker.updateLoadingMessage("登录中...");
//                if (dialog == null)
//                    dialog = DialogUtil.getLoadingDlg(context);
//                if (!dialog.isShowing())
//                    dialog.show();
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINED) {
//                if (fragment != null) {
//                    fragment.updateOnlineStatus(true);
//                }
            } else if (chatRoomStatusChangeData.status.wontAutoLogin()) {

            } else if (chatRoomStatusChangeData.status == StatusCode.NET_BROKEN) {
//                if (fragment != null) {
//                    fragment.updateOnlineStatus(false);
//                }
//                Toast.makeText(ChatRoomActivity.this, R.string.net_broken, Toast.LENGTH_SHORT).show();
                showCusToast(getString(R.string.net_broken));
            }
            Log.v(TAG, "Chat Room Online Status:" + chatRoomStatusChangeData.status.name());
        }
    };

    Observer<ChatRoomKickOutEvent> kickOutObserver = new Observer<ChatRoomKickOutEvent>() {
        @Override
        public void onEvent(ChatRoomKickOutEvent chatRoomKickOutEvent) {
//            showCusToast("被踢出聊天室，原因:" + chatRoomKickOutEvent.getReason());
            showCusToast("你已被踢出聊天室!");
//            Toast.makeText(ChatRoomActivity.this, "被踢出聊天室，原因:" + chatRoomKickOutEvent.getReason(), Toast.LENGTH_SHORT).show();
            clearChatRoom();
            finish();
        }
    };

    private void initMessageFragment(Bundle bundle) {
        if (messageFragment != null) {
            messageFragment.init(bundle);
        }
    }

    private void onLoginDone() {
        enterRequest = null;
//        DialogMaker.dismissProgressDialog();
        if (dialog != null)
            dialog.dismiss();
    }

    public ChatRoomInfo getRoomInfo() {
        return roomInfo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult");
        messageFragment.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 检测当前用户是否可以发言
     */
    void initCheckRefresh() {
        checkChatEnable = new RefreshUtil(context);
        checkChatEnable.setRefreshTime(TIME_CHECK_CHAT_ENABLE);
        checkChatEnable.setOnRefreshListener(new RefreshUtil.OnRefreshListener() {
            @Override
            public Object doInBackground() {
                try {
                    if (!new UserInfoDao(context).isLogin()) {
                        return null;
                    }
                    Map<String, String> stringMap = ApiConfig.getCommonMap(context);
                    stringMap.put(UserInfo.UID, new UserInfoDao(context).queryUserInfo().getUserId());
                    stringMap.put(ApiConfig.PARAM_TIME, System.currentTimeMillis() + "");
                    stringMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, stringMap));
                    //这里一定不要使用get请求，get请求有时候出现服务端验证失败，可能是空格转义的问题
                    String str = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_CHATROOM_ROOMID, stringMap);
                    if (str != null) {
                        return CommonResponse.fromJson(str, RoomData.class);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onUpdate(Object result) {
                //检测到接口false，用户不能发言
                CommonResponse<RoomData> dataCommonResponse = (CommonResponse<RoomData>) result;
                if (dataCommonResponse != null && !dataCommonResponse.isSuccess()) {
                    showCusToast(ConvertUtil.NVL(dataCommonResponse.getErrorInfo(), "您已经被禁言！！！"));
                    finish();
                    logOutChatRoom();
                }
            }
        });
    }

    /**
     * netty连接成功后收到的广播
     *
     * @param event
     */
    public void onEvent(final OptionalEvent event) {
        Log.v(TAG, "onEvent");
        if(isFinishing()){
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event == null)
                    return;


                NettyResponse<Optional> response = event.getNettyResponse();
                if (response == null)
                    return;
                if (NettyUtil.TYPE_CONNECT.equals(response.getType())) {
                    CoreOptionUtil.getCoreOptionUtil(getApplicationContext()).startQuotationMessage(codes);
                } else if (NettyUtil.TYPE_QP.equals(response.getType())) {
                    Optional optional = response.getData();
                    if (optional == null)
                        return;
                    if (productViewHole4ChatRoom == null)
                        return;
                    productViewHole4ChatRoom.updateProductViewListDisplay(optional);
                }
            }
        });

    }

    List<Optional> optionalList;


    /**
     * 产品viewpager
     */
    void initProductViewPager() {
        codes = TradeConfig.getCurrentCodes(context);
        productViewHole4ChatRoom = new ProductViewHole4ChatRoom(ChatRoomActivity.this, codes);
        productLiveAdapter = new ProductLiveAdapter(productViewHole4ChatRoom, new ArrayList<Optional>());
        list_product.setAdapter(productLiveAdapter);

        String[] allProducts = codes.split(",");
        int size = 20;
        if (allProducts != null)
            size = allProducts.length;
        list_product.setItemViewCacheSize(size);
        initProData();
    }

    /**
     * 初始化产品
     */
    void initProData() {
        new AsyncTask<Void, Void, List<Optional>>() {
            @Override
            protected List<Optional> doInBackground(Void... params) {
                try {
                    return BakSourceService.getOptionals(context, codes);
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
                if (list != null) {
                    optionalList = list;
                    productViewHole4ChatRoom.setOptionalList(optionalList);
                    productLiveAdapter.setOptionalList(optionalList);
                }
//                if (NettyClient.getInstance(context).isInited())
//                    NettyClient.getInstance(context).write(codes);
                CoreOptionUtil.getCoreOptionUtil(getApplicationContext()).startQuotationMessage(codes);
            }
        }.execute();
    }

    /**
     * 聊天室公告弹框 start
     ***********************/
    int defaultTime = 10;// 默认禁止发广告公告为10s

    private void showNoticePop(String content, int time) {
        if (rel_chatroom_notice != null) {
            rel_chatroom_notice.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(content)) {
            text_notice_content.setText(content);
        }
        rel_chatroom_notice.setVisibility(View.VISIBLE);
        if (time == 0) {
            time = defaultTime;
        }
        pro_time.setMax(100);
        // 进度为100  max为100  10秒倒计时  动画10*10ms执行一次
        pro_time.startProgressAnim(time * 10, 100, RoundProgressBar.TYPE_COUNT_NORMAL, new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                rel_chatroom_notice.setVisibility(View.GONE);
                return false;
            }
        });
    }

    public void onEventMainThread(ChatRoomNoticeEvent chatRoomNoticeEvent) {
        PushExtraObj pushExtraObj = chatRoomNoticeEvent.pushExtraObj;
        if (roomId.equals(pushExtraObj.getRoomId())) {// 同一个聊天室才弹窗
            showNoticePop(pushExtraObj.getContent(), pushExtraObj.getTimes());
        }
    }

    /**
     * 聊天室公告弹框 end
     ***********************/


    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
            Log.d(TAG, "onInfo: " + what + ", " + extra);
            return false;
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer plMediaPlayer, int errorCode) {
            boolean isNeedReconnect = false;
            Log.e(TAG, "Error happened, errorCode = " + errorCode);
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                    showToastTips("Invalid URL !");
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    showToastTips("404 resource not found !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    showToastTips("Connection refused !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    showToastTips("Connection timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                    showToastTips("Empty playlist !");
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    showToastTips("Stream disconnected !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    showToastTips("Network IO Error !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    showToastTips("Unauthorized Error !");
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    showToastTips("Prepare timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    showToastTips("Read frame timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_HW_DECODE_FAILURE:
                    PlayerUtil.setOptions(mVideoView, AVOptions.MEDIA_CODEC_SW_DECODE);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                default:
                    showToastTips("unknown error !");
                    break;
            }
            // Todo pls handle the error status here, reconnect or call finish()
            /*发现重连很容易出现奔溃*/
//            if (isNeedReconnect) {
//                sendReconnectMessage();
//            }
//            else {
            //从列表传入的状态是关闭的
            if (status == LiveRoomNew.STATUS_OFF) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
//                    DialogUtil.showMsgDialog(context, "该视频暂时不能播放", "确定");
//                    emptyView.setVisibility(View.VISIBLE);
                if (isNeedReconnect) {
                    playErrorCount++;
                    if (playErrorCount < PLAYER_ERROR_COUNT_MAX) {
                        sendReconnectMessage();
                        return true;
                    }
                }
            }
            pauseVideo();
            mLoadingView.setVisibility(View.GONE);

//            }
            // Return true means the error has been handled
            // If return false, then `onCompletion` will be called
            return true;
        }
    };

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            Log.d(TAG, "Play Completed !");
            showToastTips("Play Completed !");

            emptyView.setVisibility(View.VISIBLE);
            mLoadingView.setVisibility(View.GONE);

        }
    };

    private PLMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new PLMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int precent) {
//            Log.d(TAG, "onBufferingUpdate: " + precent);
        }
    };

    private PLMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new PLMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(PLMediaPlayer plMediaPlayer) {
            Log.d(TAG, "onSeekComplete !");
        }
    };

    private PLMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int width, int height, int videoSar, int videoDen) {
            Log.d(TAG, "onVideoSizeChanged: width = " + width + ", height = " + height + ", sar = " + videoSar + ", den = " + videoDen);
        }
    };

    Toast mToast;

    private void showToastTips(final String tips) {
//        if (mIsActivityPaused) {
//            return;
//        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (mToast != null) {
//                    mToast.cancel();
//                }
//                mToast = Toast.makeText(context, tips, Toast.LENGTH_SHORT);
//                mToast.show();
//            }
//        });
    }

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != MESSAGE_ID_RECONNECTING) {
                return;
            }
//            if (mIsActivityPaused || !Utils.isLiveStreamingAvailable()) {
//                finish();
//                return;
//            }
//            if (!NetworkUtil.checkNetwork(context)) {
//                sendReconnectMessage();
//                return;
//            }
            mVideoView.setVideoPath(mVideoPath);
            mVideoView.start();
        }
    };

    private void sendReconnectMessage() {
//        showToastTips("正在重连...");
        mLoadingView.setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_ID_RECONNECTING), 1000);
    }
}
