package com.trade.eight.moudle.product.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.config.ProFormatConfig;
import com.trade.eight.config.QiHuoExplainWordConfig;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.config.WeipanConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.KlineCycle;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.integral.AccountIntegralData;
import com.trade.eight.entity.product.ProductNotice;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.response.NettyResponse;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.kchart.fragment.KLineFragment;
import com.trade.eight.kchart.fragment.KMinuteFragment;
import com.trade.eight.moudle.baksource.BakSourceInterface;
import com.trade.eight.moudle.baksource.BakSourceService;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.me.activity.StepNavAct;
import com.trade.eight.moudle.netty.NettyClient;
import com.trade.eight.moudle.netty.NettyUtil;
import com.trade.eight.moudle.outterapp.QiHuoExplainWordActivity;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.product.OptionalEvent;
import com.trade.eight.moudle.product.ProductNoticeEvent;
import com.trade.eight.moudle.product.ProductNoticeUtil;
import com.trade.eight.moudle.trade.TradeOrderOptionEvent;
import com.trade.eight.moudle.trade.activity.DialogUtilForTrade;
import com.trade.eight.moudle.trade.close.TradeCloseProductDlg;
import com.trade.eight.moudle.trade.create.TradeCreateDlg;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
import com.trade.eight.mpush.coreoption.CoreOptionUtil;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.Utils;
import com.trade.eight.tools.refresh.RefreshUtil;
import com.trade.eight.tools.trade.QuickCloseUtil;
import com.trade.eight.tools.trade.TradeConfig;
import com.trade.eight.view.AppTitleView;
import com.trade.eight.view.rise.RiseNumberTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 产品的详情页面
 * <p>
 * android:hardwareAccelerated="true"
 * 硬件加速问题  导致多层次的view重新绘制
 * activity中设置hardwareAccelerated true
 */
public class ProductActivity extends BaseActivity implements OnClickListener {

    public static String TAG = "ProductActivity";

    ProductActivity context = this;
    AppTitleView titleView;
    RecyclerView recyclerView, landRecycleViewCycle;
    Optional optional = null;
    TextView tv_rate, tv_time, tv_buy, tv_deal_vol, tv_sell,
            tv_hol_num, tv_upmost, tv_open, tv_high,
            tv_lowmost, tv_avgprice, tv_low,
            tv_zuoshou, tv_jiesuan, tv_closed, tv_title,

    tv_title_land, tv_time_land, tv_rate_land,
            tv_buy_land, tv_deal_vol_land, tv_sell_land,
            tv_hol_num_land, tv_upmost_land, tv_open_land, tv_high_land,
            tv_lowmost_land, tv_avgprice_land, tv_low_land,
            tv_jiesuan_land, tv_closed_land, tv_zuoshou_land;
    Button btn_help;
    View infoView_detail, infoView_detail_land;
    ImageView img_expand_info, img_expand_info_land;
    View fragment_container = null;
    View goLiveView = null, refreshView_land = null;
    RiseNumberTextView tv_latest, tv_latest_land;
    View kline_fullscreenView;
    View line_tradeclose;
    TextView tv_tradeclose;
    View line_buyUp;
    View line_buydown;
    TextView text_buyprice, text_sellprice;
    View tradeLayout, goWeipanListView;
    public static final String CODE_DK_SHOCK = "shock";
    public static final String CODE_DK_FUTURE = "future";

    public static final int RESULT_DK_VIEW = 100;
    RefreshUtil refreshUtil;
    String excode = null;
    String code = null;
    int tradeFlag = Optional.TRADEFLAG_YES;
    QuickCloseUtil quickCloseUtil;
    UserInfoDao userInfoDao;

    View line_trade_bcjg;
    TextView text_trade_bcjg;

    TradeLoginDlg tradeLoginDlg;// 登录弹窗
    TradeCreateDlg createUtil = null;//建仓弹窗
    TradeCloseProductDlg tradeCloseProductDlg = null;// 平仓弹窗

    /**
     * 启动界面
     */
    public static void start(Context c, String excode, String code, int tradeFlag) {
        Intent intent = new Intent(c, ProductActivity.class);
        intent.putExtra("excode", excode);
        intent.putExtra("code", code);
        intent.putExtra("tradeFlag", tradeFlag);
        c.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        excode = getIntent().getStringExtra("excode");
        code = getIntent().getStringExtra("code");
        tradeFlag = getIntent().getIntExtra("tradeFlag", Optional.TRADEFLAG_YES);
        super.onCreate(savedInstanceState);
        setStatusBarTintResource(R.color.c_262626);
        setContentView(R.layout.activity_product);
        userInfoDao = new UserInfoDao(this);
        initView();


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //当前为横屏
            handeVisibleView(true);
        } else {
            //切换到竖屏
            handeVisibleView(false);
        }

        initData();
        getCurrentProduct();

//        replaceFragment(getCurrentMinuteFragment(), false);
        initRefresh();

        //引导页
//        if (!PreferenceSetting.getBoolean(context, "nav_productnotice")) {
//            StepNavAct.start(context, StepNavAct.TYPE_PRODUCTNOTICE);
//            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        }
    }

    /**
     * 传入的excode code转成optional
     *
     * @return
     */
    Optional getInitOptional() {
        String excode = getIntent().getStringExtra("excode");
        String code = getIntent().getStringExtra("code");
        Optional optional = new Optional();
        optional.setType(excode);
        optional.setExchangeID(excode);
        optional.setInstrumentID(code);
        optional.setCustomCode(code);
        optional.setProductCode(code);
        return optional;
    }

    void initRefresh() {
        refreshUtil = new RefreshUtil(context);
        //设置刷新时间
        refreshUtil.setRefreshTime(AppSetting.getInstance(context).getRefreshTime());
        refreshUtil.setOnRefreshListener(new RefreshUtil.OnRefreshListener() {
            @Override
            public Object doInBackground() {
                return getData();
            }

            @Override
            public void onUpdate(Object result) {
                if (result == null)
                    return;
                setTextValue((Optional) result);
            }
        });
    }

    void initData() {
        new AsyncTask<Void, Void, Optional>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                showNetLoadingProgressDialog(null);
            }

            @Override
            protected Optional doInBackground(Void... voids) {
                return getData();
            }

            @Override
            protected void onPostExecute(Optional optional) {
                super.onPostExecute(optional);
                if (isFinishing())
                    return;
                hideNetLoadingProgressDialog();
                if (optional == null)
                    return;

                handler.sendEmptyMessageDelayed(GETORDER_CODE, 1500);

//                getProductNoticeList();
                setTextValue(optional);
//                NettyClient.getInstance(context).write(optional);
                CoreOptionUtil.getCoreOptionUtil(getApplicationContext()).startQuotationMessage(optional.getCode());

                if (selectPos == 0)
                    replaceFragment(getCurrentMinuteFragment());
                else {
                    MyAdapter adapter = (MyAdapter) recyclerView.getAdapter();
                    String cycle = adapter.getItem(selectPos).getCode();
                    replaceFragment(getKlineFragment(cycle));
                }
                if (refreshView_land != null)
                    refreshView_land.clearAnimation();
            }
        }.execute();
    }


    /**
     * 周六凌晨4点-周一早上8点这段时间休市。
     */
    void checkStatus() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.v(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            handeVisibleView(true);
        } else {
            handeVisibleView(false);
        }

    }

    @Override
    public void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();

        checkStatus();
        getTradeOrderList(false);
//        getProductNoticeList();
        //http 刷新
        if (!BakSourceInterface.specialSource.contains(excode)) {
            if (refreshUtil != null)
                refreshUtil.start();
        } else {
            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this);
        }

    }

    @Override
    public void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();
        if (refreshUtil != null)
            refreshUtil.stop();

    }

    /**
     * 是否为横屏状态
     *
     * @param isLand
     */
    protected void handeVisibleView(boolean isLand) {
        View layoutTop = findViewById(R.id.layoutTop);
        View layoutTop_land = findViewById(R.id.layoutTop_land);
        View layoutBottom = findViewById(R.id.layoutBottom);
        View layoutBottom_land = findViewById(R.id.layoutBottom_land);
        if (isLand) {
            //横屏状态
            if (layoutTop != null)
                layoutTop.setVisibility(View.GONE);
            if (layoutTop_land != null)
                layoutTop_land.setVisibility(View.VISIBLE);
            if (layoutBottom != null)
                layoutBottom.setVisibility(View.GONE);
//            if (layoutBottom_land != null && selectPos != 0)//默认分时第一位
            if (layoutBottom_land != null)//默认分时第一位
                layoutBottom_land.setVisibility(View.VISIBLE);


            if (landRecycleViewCycle != null)
                landRecycleViewCycle.getAdapter().notifyDataSetChanged();

        } else {
            //竖屏状态
            if (layoutTop != null)
                layoutTop.setVisibility(View.VISIBLE);
            if (layoutTop_land != null)
                layoutTop_land.setVisibility(View.GONE);
            if (layoutBottom != null)
                layoutBottom.setVisibility(View.VISIBLE);
            if (layoutBottom_land != null)
                layoutBottom_land.setVisibility(View.GONE);

            //上市场屏蔽交易
            if (layoutBottom != null) {
                if (WeipanConfig.isShowWeipan(context)) {
                    layoutBottom.setVisibility(View.VISIBLE);
                } else {
                    layoutBottom.setVisibility(View.GONE);
                }
            }

            if (recyclerView != null)
                recyclerView.getAdapter().notifyDataSetChanged();

        }
    }

    public void initView() {
        List<KlineCycle> klineCycles = new ArrayList<>();

        if (BakSourceInterface.specialSource.contains(excode)) {
            //
            klineCycles.addAll(BakSourceInterface.specialklineCycleList);
            //如果不需要天玑线去掉下面代码
//            klineCycles.add(1, new KlineCycle(getResources().getString(R.string.str_dk_shock), CODE_DK_SHOCK));
//            klineCycles.add(2, new KlineCycle(getResources().getString(R.string.str_dk_future), CODE_DK_FUTURE));
        } else {
            klineCycles.addAll(BakSourceInterface.klineCycleList);
//            klineCycles.add(1, new KlineCycle(getResources().getString(R.string.str_dk_shock), CODE_DK_SHOCK));
//            klineCycles.add(2, new KlineCycle(getResources().getString(R.string.str_dk_future), CODE_DK_FUTURE));
        }

        titleView = (AppTitleView) findViewById(R.id.titleView);
        titleView.setBaseActivity(ProductActivity.this);
        titleView.setBackViewResource(R.drawable.img_goback_new_grey);
//        titleView.setAppCommTitle(R.string.lable_my_qihuo_am);
        titleView.setTitleTextColor(R.color.color_999999);
        titleView.setRootViewBG(R.color.c_262626);
        titleView.setSpiltLineDisplay(false);
        titleView.setRightImgBtn(true, R.drawable.img_productdetail_fullscreen);
        titleView.setRightImgCallback(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (Configuration.ORIENTATION_LANDSCAPE == getResources()
                        .getConfiguration().orientation) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    MyAppMobclickAgent.onEvent(ProductActivity.this, UmengMobClickConfig.PAGE_PRODUCT_PORSCREEN);

                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    MyAppMobclickAgent.onEvent(ProductActivity.this, UmengMobClickConfig.PAGE_PRODUCT_LANDSCREEN);
                }
                return false;
            }
        });

        btn_help = (Button) findViewById(R.id.btn_help);
        btn_help.setOnClickListener(this);

        tradeLayout = findViewById(R.id.tradeLayout);
        goWeipanListView = findViewById(R.id.goWeipanListView);
        goWeipanListView.setOnClickListener(this);
        line_buyUp = findViewById(R.id.line_buyUp);
        line_buydown = findViewById(R.id.line_buydown);
        line_tradeclose = findViewById(R.id.line_tradeclose);
        tv_tradeclose = (TextView) findViewById(R.id.tv_tradeclose);
        line_buyUp.setOnClickListener(this);
        line_buydown.setOnClickListener(this);
        line_tradeclose.setOnClickListener(this);

//        if (TradeConfig.getCurrentCodes(ProductActivity.this).contains(excode + "|" + code)) {
//            tradeLayout.setVisibility(View.VISIBLE);
//            goWeipanListView.setVisibility(View.GONE);
//        } else {
//            tradeLayout.setVisibility(View.GONE);
//            goWeipanListView.setVisibility(View.VISIBLE);
//        }


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);//linearLayoutManager is already attached recyclerView不能共用linearLayoutManager
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        if (recyclerView != null) {
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(new MyAdapter(klineCycles, MyAdapter.ITEM_NORMAL));
            recyclerView.setItemViewCacheSize(klineCycles.size());
        }

        landRecycleViewCycle = (RecyclerView) findViewById(R.id.landRecycleViewCycle);
        if (landRecycleViewCycle != null) {
            LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(this);
            linearLayoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
            landRecycleViewCycle.setLayoutManager(linearLayoutManager3);
            landRecycleViewCycle.setAdapter(new MyAdapter(klineCycles, MyAdapter.ITEM_LAND));
            landRecycleViewCycle.setItemViewCacheSize(klineCycles.size());
        }

        kline_fullscreenView = findViewById(R.id.kline_fullscreenView);
        if (kline_fullscreenView != null)
            kline_fullscreenView.setOnClickListener(this);
        View iconback = findViewById(R.id.iconback);
        if (iconback != null)
            iconback.setOnClickListener(this);


        goLiveView = findViewById(R.id.goLiveView);
        if (goLiveView != null)
            goLiveView.setOnClickListener(this);
        refreshView_land = findViewById(R.id.refreshView_land);
        if (refreshView_land != null)
            refreshView_land.setOnClickListener(this);

        //common view
        tv_latest = (RiseNumberTextView) findViewById(R.id.tv_latest);
        tv_rate = (TextView) findViewById(R.id.tv_rate);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_open = (TextView) findViewById(R.id.tv_open);
        tv_closed = (TextView) findViewById(R.id.tv_closed);
        tv_high = (TextView) findViewById(R.id.tv_high);
        tv_low = (TextView) findViewById(R.id.tv_low);
        tv_buy = (TextView) findViewById(R.id.tv_buy);
        tv_sell = (TextView) findViewById(R.id.tv_sell);
        tv_deal_vol = (TextView) findViewById(R.id.tv_deal_vol);
        tv_hol_num = (TextView) findViewById(R.id.tv_hol_num);
        tv_upmost = (TextView) findViewById(R.id.tv_upmost);
        tv_lowmost = (TextView) findViewById(R.id.tv_lowmost);
        tv_avgprice = (TextView) findViewById(R.id.tv_avgprice);
        tv_zuoshou = (TextView) findViewById(R.id.tv_zuoshou);
        tv_jiesuan = (TextView) findViewById(R.id.tv_jiesuan);
        text_buyprice = (TextView) findViewById(R.id.text_buyprice);
        text_sellprice = (TextView) findViewById(R.id.text_sellprice);

        infoView_detail = findViewById(R.id.infoView_detail);
        img_expand_info = (ImageView) findViewById(R.id.img_expand_info);
        img_expand_info.setOnClickListener(this);
        infoView_detail_land = findViewById(R.id.infoView_detail_land);
        img_expand_info_land = (ImageView) findViewById(R.id.img_expand_info_land);
        img_expand_info_land.setOnClickListener(this);

        fragment_container = findViewById(R.id.fragment_container);


        tv_latest_land = (RiseNumberTextView) findViewById(R.id.tv_latest_land);
        tv_rate_land = (TextView) findViewById(R.id.tv_rate_land);
        tv_title_land = (TextView) findViewById(R.id.tv_title_land);
        tv_time_land = (TextView) findViewById(R.id.tv_time_land);
        tv_buy_land = (TextView) findViewById(R.id.tv_buy_land);
        tv_deal_vol_land = (TextView) findViewById(R.id.tv_deal_vol_land);
        tv_sell_land = (TextView) findViewById(R.id.tv_sell_land);
        tv_hol_num_land = (TextView) findViewById(R.id.tv_hol_num_land);
        tv_upmost_land = (TextView) findViewById(R.id.tv_upmost_land);
        tv_open_land = (TextView) findViewById(R.id.tv_open_land);
        tv_high_land = (TextView) findViewById(R.id.tv_high_land);
        tv_lowmost_land = (TextView) findViewById(R.id.tv_lowmost_land);
        tv_avgprice = (TextView) findViewById(R.id.tv_avgprice);
        tv_high_land = (TextView) findViewById(R.id.tv_high_land);
        tv_jiesuan_land = (TextView) findViewById(R.id.tv_jiesuan_land);
        tv_closed_land = (TextView) findViewById(R.id.tv_closed_land);
        tv_avgprice_land = (TextView) findViewById(R.id.tv_avgprice_land);
        tv_low_land = (TextView) findViewById(R.id.tv_low_land);
        tv_zuoshou_land = (TextView) findViewById(R.id.tv_zuoshou_land);

        line_trade_bcjg = findViewById(R.id.line_trade_bcjg);
        text_trade_bcjg = (TextView) findViewById(R.id.text_trade_bcjg);

        lineAnimate();
    }

    void lineAnimate() {
        // 隐藏改刷新条
        //使用Runnalbe()来加载,默认xml中不会播放
//        final ImageView plineView = (ImageView) findViewById(R.id.plineView);
//        plineView.post(new Runnable() {
//            @Override
//            public void run() {
//                Animation animation = AnimationUtils.loadAnimation(context, R.anim.p_head_line);
//                plineView.startAnimation(animation);
//            }
//        });
    }


    public void setTextValue(Optional optional) {
        if (optional == null)
            return;


        if (!TextUtils.isEmpty(optional.getName())) {

            titleView.setAppCommTitle(optional.getName());
        }

        if (tv_title != null)
            tv_title.setText(ConvertUtil.NVL(optional.getTitle(), ""));
//        if (tv_time != null)
//            tv_time.setText(ConvertUtil.NVL(optional.getQuotationDateTime(), ""));
        if (tv_buy != null)
            tv_buy.setText(optional.getBidPrice1());
        if (text_buyprice != null)
            text_buyprice.setText(optional.getAskPrice1());
        if (tv_deal_vol != null)
            tv_deal_vol.setText(optional.getVolume());
        if (tv_sell != null)
            tv_sell.setText(optional.getAskPrice1());
        if (text_sellprice != null)
            text_sellprice.setText(optional.getBidPrice1());
        if (tv_hol_num != null)
            tv_hol_num.setText(optional.getOpenInterest());
        if (tv_upmost != null)
            tv_upmost.setText(optional.getUpperLimitPrice());
        if (tv_open != null)
            tv_open.setText(optional.getOpenPrice());
        if (tv_high != null)
            tv_high.setText(optional.getHighestPrice());
        if (tv_lowmost != null)
            tv_lowmost.setText(optional.getLowerLimitPrice());
        if (tv_avgprice != null)
            tv_avgprice.setText(optional.getAveragePrice());
        if (tv_low != null)
            tv_low.setText(optional.getLowestPrice());
        if (tv_zuoshou != null)
            tv_zuoshou.setText(optional.getPreClosePrice());
        if (tv_jiesuan != null) {
            if (TextUtils.isEmpty(optional.getSettlementPrice())) {
                tv_jiesuan.setText("--");
            } else {
                if (Double.parseDouble(optional.getSettlementPrice()) == 0) {
                    tv_jiesuan.setText("--");
                } else {
                    tv_jiesuan.setText(optional.getSettlementPrice());
                }
            }
        }
        if (tv_closed != null) {
            if (TextUtils.isEmpty(optional.getPreSettlementPrice())) {
                tv_closed.setText("--");
            } else {
                if (Double.parseDouble(optional.getPreSettlementPrice()) == 0) {
                    tv_closed.setText("--");
                } else {
                    tv_closed.setText(optional.getPreSettlementPrice());
                }
            }
        }

        if (tv_title_land != null) {
            if (!TextUtils.isEmpty(optional.getName())) {
                tv_title_land.setText(ConvertUtil.NVL(optional.getName(), ""));
            }
        }
//        if (tv_time_land != null)
//            tv_time_land.setText(ConvertUtil.NVL(optional.getQuotationDateTime(), ""));
        if (tv_buy_land != null)
            tv_buy_land.setText(optional.getBidPrice1());

        if (tv_deal_vol_land != null)
            tv_deal_vol_land.setText(optional.getVolume());
        if (tv_sell_land != null)
            tv_sell_land.setText(optional.getAskPrice1());

        if (tv_hol_num_land != null)
            tv_hol_num_land.setText(optional.getOpenInterest());
        if (tv_upmost_land != null)
            tv_upmost_land.setText(optional.getUpperLimitPrice());
        if (tv_open_land != null)
            tv_open_land.setText(optional.getOpenPrice());
        if (tv_high_land != null)
            tv_high_land.setText(optional.getHighestPrice());
        if (tv_lowmost_land != null)
            tv_lowmost_land.setText(optional.getLowerLimitPrice());
        if (tv_avgprice_land != null)
            tv_avgprice_land.setText(optional.getAveragePrice());
        if (tv_low_land != null)
            tv_low_land.setText(optional.getLowestPrice());

        if (tv_jiesuan_land != null) {
            if (TextUtils.isEmpty(optional.getSettlementPrice())) {
                tv_jiesuan_land.setText("--");
            } else {
                if (Double.parseDouble(optional.getSettlementPrice()) == 0) {
                    tv_jiesuan_land.setText("--");
                } else {
                    tv_jiesuan_land.setText(optional.getSettlementPrice());
                }
            }
        }

        if (tv_closed_land != null)
            tv_closed_land.setText(ConvertUtil.NVL(optional.getPreSettlementPrice(), "--"));
        if (tv_zuoshou_land != null)
            tv_zuoshou_land.setText(ConvertUtil.NVL(optional.getPreClosePrice(), "--"));

        String prefix = "";
        int mcolor = getResources().getColor(R.color.color_opt_lt);

        double diff = Double.parseDouble(optional.getChange());
        if (diff > 0) {
            mcolor = getResources().getColor(R.color.color_opt_gt);
            prefix = "+";
        }
        if (diff == 0) {
            mcolor = getResources().getColor(R.color.color_opt_eq);
            prefix = "";
        }
        tv_open.setTextColor(mcolor);
        tv_high.setTextColor(mcolor);
        tv_avgprice.setTextColor(mcolor);
        tv_low.setTextColor(mcolor);
        tv_open_land.setTextColor(mcolor);
        tv_high_land.setTextColor(mcolor);
        tv_avgprice_land.setTextColor(mcolor);
        tv_low_land.setTextColor(mcolor);

        //格式化小数点
        String change = optional.getChange();
        String changeRate = optional.getChg();

        if (tv_latest != null) {
            tv_latest.setOptional(optional);
            tv_latest.setTextByAnimation(ConvertUtil.NVL(optional.getLastPrice(), ""));

            tv_latest.setTextByAnimation(ProFormatConfig.formatByCodes(optional.getCode(), ConvertUtil.NVL(optional.getLastPrice(), ConvertUtil.NVL(optional.getLastPrice(), ""))));
            tv_latest.setTextColor(mcolor);
        }
        if (tv_rate != null) {
            tv_rate.setText(prefix + NumberUtil.moveLast0(change) + "    " + prefix + changeRate);
            tv_rate.setTextColor(mcolor);
        }

        if (tv_latest_land != null) {
            tv_latest_land.setOptional(optional);
            tv_latest_land.setTextByAnimation(ConvertUtil.NVL(optional.getLastPrice(), ""));
            tv_latest_land.setTextByAnimation(ProFormatConfig.formatByCodes(optional.getCode(), ConvertUtil.NVL(optional.getLastPrice(), ConvertUtil.NVL(optional.getLastPrice(), ""))));
            tv_latest_land.setTextColor(mcolor);
        }
        if (tv_rate_land != null) {
            tv_rate_land.setText(prefix + NumberUtil.moveLast0(change) + "    " + prefix + changeRate);
            tv_rate_land.setTextColor(mcolor);
        }
    }


    protected Fragment getCurrentMinuteFragment() {
        if (optional == null) {
            initData();
            return null;
        }

        KMinuteFragment fragment = new KMinuteFragment();
        Bundle bundle = new Bundle();
        bundle.putString("treaty", optional.getInstrumentID());
        bundle.putString("type", optional.getExchangeID());

        String closed = optional.getPreClosePrice();
        bundle.putString("closed", closed);
        bundle.putString("code", optional.getInstrumentID());
        bundle.putInt("goodsid", optional.getGoodsid());
        bundle.putString("source", optional.getExchangeID());

        //when baksource is true, currentMin view need start and end time
        bundle.putString("startTimeStr", optional.getBaksourceStart());
        bundle.putString("stopTimeStr", optional.getBaksourceEnd());
        bundle.putString("middleTimeStr", optional.getBaksourceMiddle());
        fragment.setArguments(bundle);
        return fragment;

    }

    protected KLineFragment getKlineFragment(String cycleCode) {
        if (optional == null) {
            initData();
            return null;
        }
        Bundle bundle = new Bundle();

        bundle.putString("treaty", optional.getInstrumentID());
        bundle.putString("type", optional.getExchangeID());
        bundle.putString("interval", cycleCode);

        String closed = optional.getPreClosePrice();
        bundle.putString("closed", closed);
        bundle.putString("code", optional.getInstrumentID());
        bundle.putInt("goodsid", optional.getGoodsid());
        bundle.putString("source", optional.getExchangeID());
        KLineFragment fragment = KLineFragment.newInstance(bundle);
        return fragment;

    }

    protected void replaceFragment(Fragment fragment) {
        if (isFinishing())
            return;
        if (optional == null) {
            initData();
            return;
        }

        if (fragment_container == null)
            return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
//        if (addToBackStack) {
//            transaction.addToBackStack(null);
//        }
        transaction.commitAllowingStateLoss();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                notifyToDrawOrderHelpLine();
//                notifyToDrawPNHelpLine();
//            }
//        }, 500);

    }

    void goLive() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.LIVING);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.kline_fullscreenView) {
            MyAppMobclickAgent.onEvent(this, "v3_page_product_detail", "btn_horizontal_click");
            showPopWindown();
        } else if (id == R.id.goLiveView) {
            //直播入口go live
            goLive();
            MyAppMobclickAgent.onEvent(this, "v3_page_product_detail", "btn_goLive");

        } else if (id == R.id.refreshView_land) {
            MyAppMobclickAgent.onEvent(this, UmengMobClickConfig.PAGE_PRODUCT_LANDREFRESH);

            Animation refeshAnim = AnimationUtils.loadAnimation(this, R.anim.refesh_image);
            LinearInterpolator lin = new LinearInterpolator();
            refeshAnim.setInterpolator(lin);
            if (refreshView_land != null) {
                refreshView_land.startAnimation(refeshAnim);
                initData();
            }
        } else if (id == R.id.iconback) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (id == R.id.line_buyUp) {
            MyAppMobclickAgent.onEvent(this, UmengMobClickConfig.PAGE_PRODUCT_BUYUP);
            getProductListFromNet(TradeProduct.TYPE_BUY_UP);
        } else if (id == R.id.line_buydown) {
            MyAppMobclickAgent.onEvent(this, UmengMobClickConfig.PAGE_PRODUCT_BUYDOWN);
            getProductListFromNet(TradeProduct.TYPE_BUY_DOWN);
        } else if (id == R.id.line_tradeclose) {// 平仓
            MyAppMobclickAgent.onEvent(this, UmengMobClickConfig.PAGE_PRODUCT_HOLD);
            if (optional == null) {
                return;
            }
            if (ConvertUtil.NVL(optional.getInstrumentID(), "").equals(ConvertUtil.NVL(optional.getProductId(), ""))) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.WEIPAN);
                intent.putExtra(BaseInterface.TAB_PARAME_INDEX, 1);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return;
            }
            if (listTradeOrderForProduct == null || listTradeOrderForProduct.size() == 0) {
                getTradeOrderList(true);
            } else {
                quickClose();
            }

        } else if (id == R.id.line_productnotice) {//提醒
            MyAppMobclickAgent.onEvent(this, "v3_page_product_detail", "行情提醒");

            if (popWinForMoreOption != null && popWinForMoreOption.isShowing()) {
                popWinForMoreOption.dismiss();
            }
            if (!judgeIsLogin(true)) {
                return;
            }

            ProductNoticeUtil productNoticeUtil = new ProductNoticeUtil(context, excode, code);
            productNoticeUtil.showDlg();

        } else if (id == R.id.line_product_fullscr) {//全屏
            if (Configuration.ORIENTATION_LANDSCAPE == getResources()
                    .getConfiguration().orientation) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            if (popWinForMoreOption != null && popWinForMoreOption.isShowing()) {
                popWinForMoreOption.dismiss();
            }
        } else if (id == R.id.line_product_line) {//辅助线
            MyAppMobclickAgent.onEvent(this, "v3_page_product_detail", "辅助线开启/关闭");

            if (popWinForMoreOption != null && popWinForMoreOption.isShowing()) {
                popWinForMoreOption.dismiss();
            }
            if (!judgeIsLogin(true)) {
                return;
            }
            if (TradeConfig.getAccountIsOpenHelpLine(ProductActivity.this, userInfoDao.queryUserInfo().getUserId())) {
                img_product_oc.setImageResource(R.drawable.img_product_helpline_open);
                text_product_oc.setText(R.string.product_openline);
                Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (mFragment instanceof KMinuteFragment) {
                    //绘制持仓辅助线
                    KMinuteFragment minFragment = (KMinuteFragment) mFragment;
                    minFragment.setHelpLineForTradeOrder(new ArrayList<TradeOrder>());
                    minFragment.setHelpLineForProductNotice(new ArrayList<ProductNotice>());
                }
                TradeConfig.setAccountIsOpenHelpLine(ProductActivity.this, userInfoDao.queryUserInfo().getUserId(), false);
            } else {
                img_product_oc.setImageResource(R.drawable.img_product_helpline);
                text_product_oc.setText(R.string.product_closeline);
                Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (mFragment instanceof KMinuteFragment) {
                    //绘制持仓辅助线
                    KMinuteFragment minFragment = (KMinuteFragment) mFragment;
                    minFragment.setHelpLineForTradeOrder(listTradeOrder);
                    minFragment.setHelpLineForProductNotice(listProductNotice);
                }
                TradeConfig.setAccountIsOpenHelpLine(ProductActivity.this, userInfoDao.queryUserInfo().getUserId(), true);
            }

        } else if (id == R.id.line_productqa) {//帮助说明
            MyAppMobclickAgent.onEvent(this, "v3_page_product_detail", "功能说明");
            if (popWinForMoreOption != null && popWinForMoreOption.isShowing()) {
                popWinForMoreOption.dismiss();
            }
            WebActivity.start(this, "功能说明", AndroidAPIConfig.URL_PRODUCRTNOTICE_INTRO, true);
        } else if (id == R.id.goWeipanListView) {
            MyAppMobclickAgent.onEvent(this, "v3_page_product_detail", "goto_trade_index");
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.WEIPAN);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.img_expand_info) {
            MyAppMobclickAgent.onEvent(this, UmengMobClickConfig.PAGE_PRODUCT_POREXPAND);

            if (infoView_detail.getVisibility() == View.VISIBLE) {
                infoView_detail.setVisibility(View.GONE);
                img_expand_info.setImageResource(R.drawable.img_productdetail_info_expand);
            } else {
                infoView_detail.setVisibility(View.VISIBLE);
                img_expand_info.setImageResource(R.drawable.img_productdetail_info_close);

            }
        } else if (id == R.id.img_expand_info_land) {
            MyAppMobclickAgent.onEvent(this, UmengMobClickConfig.PAGE_PRODUCT_LANDEXPAND);

            if (infoView_detail_land.getVisibility() == View.VISIBLE) {
                infoView_detail_land.setVisibility(View.GONE);
                img_expand_info_land.setImageResource(R.drawable.img_productdetail_info_expand);
            } else {
                infoView_detail_land.setVisibility(View.VISIBLE);
                img_expand_info_land.setImageResource(R.drawable.img_productdetail_info_close);
            }
        } else if (id == R.id.btn_help) {
            MyAppMobclickAgent.onEvent(this, UmengMobClickConfig.PAGE_PRODUCT_EXPLAIN);
//            QiHuoExplainWordActivity.startAct(context,
                    DialogUtilForTrade.showQiHuoExplainDlg(context,
                    new String[]{QiHuoExplainWordConfig.BUYPRICE,
                            QiHuoExplainWordConfig.SELLPRICE,
                            QiHuoExplainWordConfig.CJL,
                            QiHuoExplainWordConfig.CCL,
                            QiHuoExplainWordConfig.JGRQ});
        }
    }


    /**
     * 刷新行情去数据 需要按照交易所 和code去取
     */
    protected Optional getData() {
        try {
            if (isFinishing())
                return null;
            Optional currentOptional = BakSourceService.getOptionals(context, getInitOptional());
            optional = currentOptional;
            return currentOptional;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    int selectPos = 0;
    View selectTextView = null, currentDKView = null;

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        List<KlineCycle> goodsList;
        int type;
        public static final int ITEM_NORMAL = 0;//正常状态下 竖屏
        public static final int ITEM_LAND = 1;//横屏状态下

        public MyAdapter(List<KlineCycle> goodsList, int type) {
            this.goodsList = goodsList;
            this.type = type;
        }

        public KlineCycle getItem(int position) {
            if (goodsList != null && position < goodsList.size())
                return goodsList.get(position);
            return null;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View rootView = null;
            if (type == ITEM_LAND) {
                rootView = View.inflate(viewGroup.getContext(), R.layout.product_land_cycle_item, null);
            } else {
                rootView = View.inflate(viewGroup.getContext(), R.layout.product_klinecycle_item, null);
            }

            return new MyViewHolder(rootView);
        }

        public void clear() {
            if (null != goodsList) {
                goodsList.clear();
                notifyDataSetChanged();
            }

        }

        @Override
        public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
            if (myViewHolder.textView == null)
                return;
            if (i == selectPos) {
                selectTextView = myViewHolder.textView;
                selectTextView.setSelected(true);
            } else {
                myViewHolder.textView.setSelected(false);
            }
            final int position = i;
            myViewHolder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String code = goodsList.get(position).getCode();
                    String name = goodsList.get(position).getName();
                    if (type == ITEM_LAND) {
                        MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.PAGE_PRODUCT_LANDTYPE, name);
                    } else {
                        MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.PAGE_PRODUCT_PORTYPE, name);
                    }


                    if (code.equalsIgnoreCase(BakSourceInterface.PARAM_KLINE_TIME) || code.equalsIgnoreCase(BakSourceInterface.PARAM_KLINE_TIME_WEIPAN)) {
                        //分时
                        replaceFragment(getCurrentMinuteFragment());
                    } else if (CODE_DK_FUTURE.equals(code) || CODE_DK_SHOCK.equals(code)) {
//                        //八卦线

                    } else {
                        //周期K线
                        replaceFragment(getKlineFragment(code));
                    }

                    if (selectTextView != null)
                        selectTextView.setSelected(false);
                    selectTextView = v;
                    selectPos = position;
                    if (selectTextView != null)
                        selectTextView.setSelected(true);
                    if (recyclerView != null)
                        recyclerView.scrollToPosition(position);
                    if (landRecycleViewCycle != null)
                        landRecycleViewCycle.scrollToPosition(position);
                }
            });

            myViewHolder.textView.setText(goodsList.get(i).getName());
        }

        @Override
        public int getItemCount() {
            return goodsList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textView;

            public MyViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.item_name);
            }
        }
    }

    public boolean isLandScape() {
        return Configuration.ORIENTATION_LANDSCAPE == getResources()
                .getConfiguration().orientation;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (isLandScape()) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    if (refreshUtil != null)
                        refreshUtil.stop();
//                    NettyClient.getInstance(context).stopWrite();
                    CoreOptionUtil.getCoreOptionUtil(getApplicationContext()).stopQuotationMessage();

                    finish();
                }
                return true;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * netty连接成功后收到的广播
     *
     * @param event
     */
    public void onEvent(final OptionalEvent event) {
        if (optional == null)
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event == null)
                    return;
                NettyResponse<Optional> response = event.getNettyResponse();
                if (response == null)
                    return;
                if (NettyUtil.TYPE_CONNECT.equals(response.getType())) {
                    CoreOptionUtil.getCoreOptionUtil(getApplicationContext()).startQuotationMessage(optional.getCode());
                } else if (NettyUtil.TYPE_QP.equals(response.getType())) {
                    Optional o = response.getData();
                    if (o == null)
                        return;
                    //推送过来的数据是当前的品种
                    if (optional.getExchangeID() != null
                            && optional.getInstrumentID() != null
                            && optional.getExchangeID().equals(o.getExchangeID())
                            && optional.getInstrumentID().equals(o.getInstrumentID())) {
                        setTextValue(o);

                        if (BaseInterface.SWITCH_KLINE_REFRESH) {
                            Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                            if (mFragment instanceof KLineFragment) {
                                KLineFragment kFragment = (KLineFragment) mFragment;
                                kFragment.setLastKData(o);
                            } else if (mFragment instanceof KMinuteFragment) {
                                //刷新分时图
                                KMinuteFragment minFragment = (KMinuteFragment) mFragment;
                                minFragment.setLastKData(o);
                            }
                        }
                        if (tradeCloseProductDlg != null && tradeCloseProductDlg.isShowingDialog()) {
                            tradeCloseProductDlg.updatePrice(o);
                        }

                    }
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
        if (refreshUtil != null)
            refreshUtil.stop();
        NettyClient.getInstance(context).stopWrite();

        if (BakSourceInterface.specialSource.contains(excode)) {
            try {
                EventBus.getDefault().unregister(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_DK_VIEW && resultCode == RESULT_OK) {
                if (currentDKView != null)
                    currentDKView.performClick();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCurrentProduct() {

        if (excode == null)
            return;
        if (code == null)
            return;
        new AsyncTask<Void, Void, CommonResponse<ProductObj>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected CommonResponse<ProductObj> doInBackground(Void... voids) {
                return TradeHelp.getFxProduct(context, excode, code);
            }

            @Override
            protected void onPostExecute(CommonResponse<ProductObj> res) {
                super.onPostExecute(res);
                if (isFinishing())
                    return;
                if (res == null) {
                    return;
                }
                if (res != null && res.isSuccess()) {
                    if (res.getData() == null) {
                        return;
                    }
                    ProductObj productObj = res.getData();
                    String startDelivDate = ConvertUtil.NVL(productObj.getStartDelivDate(), "");
                    if (tv_time != null)
                        tv_time.setText(startDelivDate);
                    if (tv_time_land != null)
                        tv_time_land.setText(startDelivDate);

                    if (ProductObj.IS_CLOSE_YES.equals(productObj.getIsClosed())) {
                        titleView.setProCloseViewVisiable(true);
                    } else {
                        titleView.setProCloseViewVisiable(false);
                    }
                }
            }
        }.execute();
    }

    public void getProductListFromNet(final int upOrdown) {
        //先检测是否需要获取数据
        if (!checkGotoWeipan())
            return;
        if (excode == null)
            return;
        if (code == null)
            return;
        new AsyncTask<Void, Void, CommonResponse<ProductObj>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showNetLoadingProgressDialog("加载中");
            }

            @Override
            protected CommonResponse<ProductObj> doInBackground(Void... voids) {
                // 此处测试  excode SHFE code  ag1712
                return TradeHelp.getFxProduct(context, excode, code);
//                return TradeHelp.getFxProduct(context, "SHFE", "ag1712");

            }

            @Override
            protected void onPostExecute(CommonResponse<ProductObj> res) {
                super.onPostExecute(res);
                if (isFinishing())
                    return;
//                hideNetLoadingProgressDialog();///放在dlg中去关闭
                if (res == null) {
                    showCusToast("数据获取失败");
                    hideNetLoadingProgressDialog();
                    return;
                }
                if (res != null && res.isSuccess()) {
                    if (res.getData() == null) {
                        hideNetLoadingProgressDialog();
                        return;
                    }

                    //找到相应的品种
                    if (optional != null) {
                        startTradeCreate(res.getData(), upOrdown);
                    }
                } else {
                    hideNetLoadingProgressDialog();
                }
            }
        }.execute();
    }


    boolean checkGotoWeipan() {
        if (!new UserInfoDao(context).isLogin()) {
            //直接跳转到登录页面
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            return false;
        }
        return true;
    }


    /**
     * @param tradeProduct
     * @param upOrdown     ProductObj.TYPE_BUY_UP
     */
    void startTradeCreate(ProductObj tradeProduct, int upOrdown) {
        if (tradeProduct == null)
            return;

//        if (ProductObj.IS_CLOSE_YES.equals(tradeProduct.getIsClosed())) {
//            hideNetLoadingProgressDialog();
//            String title = getResources().getString(R.string.close_title);
//            String msg = getResources().getString(R.string.close_title_bak);
//            msg = ConvertUtil.NVL(tradeProduct.getClosePrompt(), msg);
//            DialogUtil.showMsgDialog(context, msg, getString(R.string.btn_ok));
//            return;
//        }
        //token 过期了
        if (!TradeHelp.isTokenEnable(context)) {
            hideNetLoadingProgressDialog();
            if (tradeLoginDlg == null) {
                tradeLoginDlg = new TradeLoginDlg(context);
            }
            if (!tradeLoginDlg.isShowingDialog()) {
                tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
            }

            /*DialogUtil.showTokenDialog(context, TradeConfig.getCurrentTradeCode(context),
                    new DialogUtil.AuthTokenDlgShow() {
                        @Override
                        public void onDlgShow(Dialog dlg) {

                        }
                    }, new DialogUtil.AuthTokenCallBack() {
                        @Override
                        public void onPostClick(Object obj) {

                        }

                        @Override
                        public void onNegClick() {

                        }
                    });*/
            return;
        }

        if (createUtil != null && createUtil.isShowingDialog())
            return;
        createUtil = new TradeCreateDlg(context, tradeProduct, upOrdown);
        createUtil.showDialog(R.style.dialog_trade_ani);

    }


    PopupWindow popWinForMoreOption;
    private LinearLayout line_productnotice;
    private TextView text_product_noticenum;
    private LinearLayout line_product_fullscr;
    private LinearLayout line_product_line;
    private ImageView img_product_oc;
    private TextView text_product_oc;
    private LinearLayout line_productqa;
    private List<TradeOrder> listTradeOrder;//持仓单
    private List<TradeOrder> listTradeOrderForProduct = new ArrayList<TradeOrder>();//当前品种持仓单,绘制辅助线所用
    private List<ProductNotice> listProductNotice = new ArrayList<ProductNotice>();//当前品种行情提醒,绘制辅助线所用

    /**
     * 产品详情页更多弹窗
     */
    private void showPopWindown() {
        View view = View.inflate(ProductActivity.this, R.layout.layout_productmore, null);

        line_productnotice = (LinearLayout) view.findViewById(R.id.line_productnotice);
        text_product_noticenum = (TextView) view.findViewById(R.id.text_product_noticenum);
        line_product_fullscr = (LinearLayout) view.findViewById(R.id.line_product_fullscr);
        line_product_line = (LinearLayout) view.findViewById(R.id.line_product_line);
        img_product_oc = (ImageView) view.findViewById(R.id.img_product_oc);
        text_product_oc = (TextView) view.findViewById(R.id.text_product_oc);
        line_productqa = (LinearLayout) view.findViewById(R.id.line_productqa);

        text_product_noticenum.setText("(" + listProductNotice.size() + ")");

        line_productnotice.setOnClickListener(this);
        line_product_fullscr.setOnClickListener(this);
        line_product_line.setOnClickListener(this);
        line_productqa.setOnClickListener(this);

        if (userInfoDao.isLogin()) {
            if (TradeConfig.getAccountIsOpenHelpLine(ProductActivity.this, userInfoDao.queryUserInfo().getUserId())) {
                img_product_oc.setImageResource(R.drawable.img_product_helpline);
                text_product_oc.setText(R.string.product_closeline);
            } else {
                img_product_oc.setImageResource(R.drawable.img_product_helpline_open);
                text_product_oc.setText(R.string.product_openline);
            }
        } else {
            img_product_oc.setImageResource(R.drawable.img_product_helpline);
            text_product_oc.setText(R.string.product_closeline);
        }

        int w = (int) getResources().getDimension(R.dimen.margin_143dp);
        int h = (int) getResources().getDimension(R.dimen.margin_175dp);
        popWinForMoreOption = new PopupWindow(view, w, h);
        popWinForMoreOption.setFocusable(true);//影响listView的item点击
        popWinForMoreOption.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popWinForMoreOption.setBackgroundDrawable(new BitmapDrawable());
        int y = Utils.dip2px(ProductActivity.this, 10);
        popWinForMoreOption.showAsDropDown(kline_fullscreenView, -w + (int) getResources().getDimension(R.dimen.margin_40dp), 0);
    }

    private static final int GETORDER_CODE = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GETORDER_CODE:
                    getTradeOrderList(false);
                    break;
            }
        }
    };

    /**
     * 获取该品种所在交易所持仓单、以及该品种持仓单
     */
    private void getTradeOrderList(final boolean showTokenDlg) {
        new AsyncTask<Void, Void, CommonResponse4List<TradeOrder>>() {
            @Override
            protected CommonResponse4List<TradeOrder> doInBackground(Void... params) {
                if (isFinishing())
                    return null;
                return TradeHelp.getTradeOrderList(ProductActivity.this);
            }


            @Override
            protected void onPostExecute(CommonResponse4List<TradeOrder> response4List) {
                super.onPostExecute(response4List);
                if (isFinishing())
                    return;
                if (response4List != null) {
                    if (response4List.isSuccess()) {

                        if (response4List.getData() != null) {
                            listTradeOrder = response4List.getData();
                            listTradeOrderForProduct.clear();
                            if (listTradeOrder != null && listTradeOrder.size() != 0) {
                                for (TradeOrder tradeOrder : listTradeOrder) {
                                    if (tradeOrder.getExcode().equals(excode) && tradeOrder.getInstrumentId().equals(code)) {
                                        listTradeOrderForProduct.add(tradeOrder);
                                    }
                                }

                                if (listTradeOrderForProduct != null && listTradeOrderForProduct.size() > 0) {// 品种有持仓单 平仓按钮高亮  画辅助线
//                                    tv_tradeclose.setBackgroundResource(R.drawable.index_weipan_btn_close_bg);
//                                    tv_tradeclose.setTextColor(getResources().getColor(R.color.white));
//                                    tv_tradeclose.setText(R.string.trade_close);
                                    tv_tradeclose.setText(getResources().getString(R.string.trade_orderhold_num, listTradeOrderForProduct.size()));


                                } else {
//                                    tv_tradeclose.setBackgroundResource(R.drawable.btn_bg_go_weipan);
//                                    tv_tradeclose.setTextColor(getResources().getColor(R.color.grey));
//                                    tv_tradeclose.setText(R.string.trade_closeempty);
                                    tv_tradeclose.setText(getResources().getString(R.string.trade_orderhold_num, 0));

                                }
                            } else {
//                                tv_tradeclose.setBackgroundResource(R.drawable.btn_bg_go_weipan);
//                                tv_tradeclose.setTextColor(getResources().getColor(R.color.grey));
//                                tv_tradeclose.setText(R.string.trade_closeempty);

                                tv_tradeclose.setText(getResources().getString(R.string.trade_orderhold_num, 0));
                            }
//                            notifyToDrawOrderHelpLine();
                        } else {
                            if (ApiConfig.isNeedLogin(response4List.getErrorCode()) && showTokenDlg) {
                                AppSetting appSetting = AppSetting.getInstance(context);
                                //清理的token,让本地需要登录；不能直接clearWPToken，会把三个交易所的都清理掉
                                appSetting.setRefreshTimeWPToken(context, 0);

                                if (tradeLoginDlg == null) {
                                    tradeLoginDlg = new TradeLoginDlg(context);
                                }
                                if (!tradeLoginDlg.isShowingDialog()) {
                                    tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                                }
                            }
//                            tv_tradeclose.setBackgroundResource(R.drawable.btn_bg_go_weipan);
//                            tv_tradeclose.setTextColor(getResources().getColor(R.color.grey));
//                            tv_tradeclose.setText(R.string.trade_queryorder);
                            tv_tradeclose.setText(getResources().getString(R.string.trade_orderhold_num, 0));
                        }
                    } else {
                        if (ApiConfig.isNeedLogin(response4List.getErrorCode()) && showTokenDlg) {
                            AppSetting appSetting = AppSetting.getInstance(context);
                            //清理的token,让本地需要登录；不能直接clearWPToken，会把三个交易所的都清理掉
                            appSetting.setRefreshTimeWPToken(context, 0);

                            if (tradeLoginDlg == null) {
                                tradeLoginDlg = new TradeLoginDlg(context);
                            }
                            if (!tradeLoginDlg.isShowingDialog()) {
                                tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                            }
                        }
//                        tv_tradeclose.setBackgroundResource(R.drawable.btn_bg_go_weipan);
//                        tv_tradeclose.setTextColor(getResources().getColor(R.color.grey));
                        tv_tradeclose.setText(getResources().getString(R.string.trade_orderhold_num, 0));
                    }
                }
            }
        }.execute();
    }

    /**
     * 获取行情提醒列表
     */
    private void getProductNoticeList() {
        HashMap<String, String> request = new HashMap<String, String>();
        UserInfoDao userInfoDao = new UserInfoDao(ProductActivity.this);
        if (!userInfoDao.isLogin()) {
            return;
        }
        request.put("userId", userInfoDao.queryUserInfo().getUserId());
        request.put("excode", excode);
        request.put("code", code);

        HttpClientHelper.doPostOption(ProductActivity.this, AndroidAPIConfig.URL_PRODUCRTNOTICE_LIST, request, null, new NetCallback(this) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                showCusToast(resultMsg);
            }

            @Override
            public void onResponse(String str) {
                CommonResponse4List<ProductNotice> response = CommonResponse4List.fromJson(str, ProductNotice.class);
                List<ProductNotice> productNoticeList = response.getData();
                if (productNoticeList != null && productNoticeList.size() > 0) {
                    listProductNotice.clear();
                    listProductNotice.addAll(productNoticeList);
                } else {
                    listProductNotice.clear();
                }
                notifyToDrawPNHelpLine();
            }
        }, false);
    }

    /**
     * 建仓 平仓事件响应
     *
     * @param event
     */
    public void onEvent(TradeOrderOptionEvent event) {
        getTradeOrderList(false);
    }

    public void onEventMainThread(ProductNoticeEvent obj) {
        switch (obj.eventType) {

            case ProductNoticeEvent.EVENTTYPE_SAVE://添加提醒成功
                getProductNoticeList();
                break;
            case ProductNoticeEvent.EVENTTYPE_EDIT_DELETE://编辑操作  删除提醒
                getProductNoticeList();
                break;
        }
    }

    /**
     * 快速平仓
     */
    private void quickClose() {
        if (tradeCloseProductDlg == null || !tradeCloseProductDlg.isShowingDialog()) {
            ProductObj productObj = new ProductObj();
            productObj.setInstrumentName(optional.getName());
            productObj.setExcode(optional.getExchangeID());
            productObj.setInstrumentId(optional.getInstrumentID());
            productObj.setBidPrice1(optional.getBidPrice1());
            productObj.setAskPrice1(optional.getAskPrice1());
            tradeCloseProductDlg = new TradeCloseProductDlg(ProductActivity.this, productObj, listTradeOrderForProduct);
            tradeCloseProductDlg.showDialog(R.style.dialog_trade_ani);
        }
    }

    /**
     * 绘制订单行情辅助线
     */
    private void notifyToDrawOrderHelpLine() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (mFragment instanceof KLineFragment) {
            KLineFragment kFragment = (KLineFragment) mFragment;
        } else if (mFragment instanceof KMinuteFragment) {
            //绘制持仓辅助线
            KMinuteFragment minFragment = (KMinuteFragment) mFragment;
            if (userInfoDao.isLogin()) {
                if (TradeConfig.getAccountIsOpenHelpLine(ProductActivity.this, userInfoDao.queryUserInfo().getUserId())) {
                    minFragment.setHelpLineForTradeOrder(listTradeOrderForProduct);
                }
            }
        }
    }

    /**
     * 绘制行情提醒辅助线
     */
    private void notifyToDrawPNHelpLine() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (mFragment instanceof KLineFragment) {
            KLineFragment kFragment = (KLineFragment) mFragment;
        } else if (mFragment instanceof KMinuteFragment) {
            //绘制持仓辅助线
            KMinuteFragment minFragment = (KMinuteFragment) mFragment;
            if (userInfoDao.isLogin()) {
                if (TradeConfig.getAccountIsOpenHelpLine(ProductActivity.this, userInfoDao.queryUserInfo().getUserId())) {
                    minFragment.setHelpLineForProductNotice(listProductNotice);
                }
            }
        }
    }

    /**
     * 判断是否登陆以及是否弹出登陆窗口
     *
     * @param isShowDialog
     * @return
     */
    private boolean judgeIsLogin(boolean isShowDialog) {
        //没有登录的话 必须先登录
        UserInfoDao dao = new UserInfoDao(context);
        if (!dao.isLogin()) {
            if (isShowDialog) {
                DialogUtil.showConfirmDlg(context, null, null, null, true, new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {

                        return false;
                    }
                }, new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        context.startActivity(new Intent(context, LoginActivity.class));
                        return false;
                    }
                });
            }
            return false;
        }
        return true;
    }

    /**
     * 用户积分等级获取
     */
    private void checkAccountIntegral() {
        new AsyncTask<Void, Void, CommonResponse<AccountIntegralData>>() {
            @Override
            protected CommonResponse<AccountIntegralData> doInBackground(Void... params) {

                UserInfoDao userInfoDao = new UserInfoDao(context);
                UserInfo userInfo = userInfoDao.queryUserInfo();
                CommonResponse<AccountIntegralData> response = new CommonResponse<AccountIntegralData>();
                if (userInfo.getLevelNum() < 2) {

                    Map<String, String> map = new HashMap<String, String>();
                    map.put(UserInfo.UID, userInfoDao.queryUserInfo().getUserId());
                    long lvVersion = PreferenceSetting.getInt(context, PreferenceSetting.ACCOUNT_INTEGRAL_VERSION);
                    if (lvVersion < 0) {
                        lvVersion = 0;
                    }
                    map.put("versionNo", lvVersion + "");
                    map = ApiConfig.getParamMap(context, map);
                    String res = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_ACCOUNT_INTEGRALINFO, map);
                    response = CommonResponse.fromJson(res, AccountIntegralData.class);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ProductNoticeUtil productNoticeUtil = new ProductNoticeUtil(context, excode, code);
                            productNoticeUtil.showDlg();
                        }
                    });
//                    ProductNoticeUtil productNoticeUtil = new ProductNoticeUtil(context,excode, code);
//                    productNoticeUtil.showDlg();
//                    ProductNoticeActivity.startProductNoticeActivity(context, excode, code);
                }

                return response;
            }

            @Override
            protected void onPostExecute(CommonResponse<AccountIntegralData> accountIntegralDataCommonResponse) {
                super.onPostExecute(accountIntegralDataCommonResponse);
                if (isFinishing())
                    return;
                if (accountIntegralDataCommonResponse != null) {
                    if (accountIntegralDataCommonResponse.isSuccess()) {
                        AccountIntegralData accountIntegralData = accountIntegralDataCommonResponse.getData();
                        UserInfoDao userInfoDao = new UserInfoDao(context);
                        UserInfo userInfo = userInfoDao.queryUserInfo();
                        userInfo.setLevelNum(accountIntegralData.getLevelNum());
                        userInfoDao.addOrUpdate(userInfo);
                        if (accountIntegralData.getLevelNum() < 2) {
                            DialogUtil.showTitleAndContentDialog(ProductActivity.this, getResources().getString(R.string.product_notice_permissiontitle), getResources().getString(R.string.product_notice_permissioncontent), null, null);
                        } else {
//                            ProductNoticeActivity.startProductNoticeActivity(context, excode, code);
                            ProductNoticeUtil productNoticeUtil = new ProductNoticeUtil(context, excode, code);
                            productNoticeUtil.showDlg();
                        }
                    }
                }
            }
        }.execute();
    }
}
