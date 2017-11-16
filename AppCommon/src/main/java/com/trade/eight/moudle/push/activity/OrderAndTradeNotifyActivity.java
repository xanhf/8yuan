package com.trade.eight.moudle.push.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.utils.L;
import com.trade.eight.app.MyApplication;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.me.activity.CashInAndOutAct;
import com.trade.eight.moudle.push.entity.PushExtraObj;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
import com.trade.eight.mpush.entity.OrderNotifyData;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.Utils;

/**
 * 作者：Created by ocean
 * 时间：on 2017/6/3.
 * 订单反馈以及交易风险提示
 */

public class OrderAndTradeNotifyActivity extends BaseActivity {

    View view_statusbar;
    ImageView img_notify;
    TextView text_notify_title;
    TextView text_notify_content;
    View view_bottombar;

    public static final int ORDERNOTIFY = 1;//订单反馈
    public static final int TRADENOTIFY = 2;//交易用户风险提示
    public static final int LOGINNOTIFY = 3;//登陆
    public static final int CASHINNOTIFY = 4;//入金事件
    public static final int CASHOUTNOTIFY = 5;//提现事件

    int type = -1;
    // 订单反馈提醒
    OrderNotifyData orderNotifyData;
    // 资金危险提示
    int safeOrDangerous = 1;
    long showTime = 3000L;
    //登录成功或者失败
    int successOrFail = 1;
    String content = "";

    //    Android:windowIsTranslucent透明 导致activity退出动画无效
    protected int activityCloseEnterAnimation;
    protected int activityCloseExitAnimation;

    /**
     * 订单反馈
     *
     * @param context
     * @param orderNotifyData
     */
    public static void startOrderNotifyAct(Context context, OrderNotifyData orderNotifyData) {
        if (MyApplication.getInstance().isApplicationBroughtToBackground(context)) {
            //app在后台运行
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, OrderAndTradeNotifyActivity.class);
        intent.putExtra("ordernotify", orderNotifyData);
        intent.putExtra("type", ORDERNOTIFY);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 交易风险提示
     *
     * @param context
     */
    public static void startTradeNotifyAct(Context context, int safeOrDangerous) {
        if (MyApplication.getInstance().isApplicationBroughtToBackground(context)) {
            //app在后台运行
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, OrderAndTradeNotifyActivity.class);
        intent.putExtra("type", TRADENOTIFY);
        intent.putExtra("safeOrDangerous", safeOrDangerous);
        context.startActivity(intent);
    }

    /**
     * 登录成功或者失败
     *
     * @param context
     */
    public static void startLoginNotifyAct(Context context, int successOrFail, String content) {
        if (MyApplication.getInstance().isApplicationBroughtToBackground(context)) {
            //app在后台运行
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, OrderAndTradeNotifyActivity.class);
        intent.putExtra("type", LOGINNOTIFY);
        intent.putExtra("successOrFail", successOrFail);
        intent.putExtra("content", content);
        intent.putExtra("showTime", 3000l);
        context.startActivity(intent);
    }

    /**
     * 充值事件(成功或者失败)
     *
     * @param context
     */
    public static void starCashInNotifyAct(Context context, int successOrFail, String content) {
        if (MyApplication.getInstance().isApplicationBroughtToBackground(context)) {
            //app在后台运行
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, OrderAndTradeNotifyActivity.class);
        intent.putExtra("type", CASHINNOTIFY);
        intent.putExtra("successOrFail", successOrFail);
        intent.putExtra("content", content);
        intent.putExtra("showTime", 3000l);
        context.startActivity(intent);
    }

    /**
     * 提现事件(成功或者失败)
     *
     * @param context
     */
    public static void starCashOutNotifyAct(Context context, int successOrFail, String content) {
        if (MyApplication.getInstance().isApplicationBroughtToBackground(context)) {
            //app在后台运行
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, OrderAndTradeNotifyActivity.class);
        intent.putExtra("type", CASHOUTNOTIFY);
        intent.putExtra("successOrFail", successOrFail);
        intent.putExtra("content", content);
        intent.putExtra("showTime", 3000l);
        context.startActivity(intent);
    }

//    @Override
//    public boolean isActivityFitsSystemWindows() {
//        return false;
//    }

    private void applyCompat() {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = Utils.dip2px(OrderAndTradeNotifyActivity.this, 48) + Utils.getStatusHeight(OrderAndTradeNotifyActivity.this) * 2;

        lp.gravity = Gravity.TOP;//设置对话框置顶显示
        win.setAttributes(lp);
        // 让底部activity 可以操作  http://blog.csdn.net/androidstarjack/article/details/54016594
        win.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
//        win.setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN,WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setFinishOnTouchOutside(false);

//        applyCompat();
        if (Build.VERSION.SDK_INT == 19) {
            setStatusBarTintResource(R.color.white);
        }
        setContentView(R.layout.act_orderandtrade_notify);
        applyCompat();

//    Android:windowIsTranslucent透明 导致activity退出动画无效 添加code
        TypedArray activityStyle = getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowAnimationStyle});
        int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);
        activityStyle.recycle();
        activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId, new int[]{android.R.attr.activityCloseEnterAnimation, android.R.attr.activityCloseExitAnimation});
        activityCloseEnterAnimation = activityStyle.getResourceId(0, 0);
        activityCloseExitAnimation = activityStyle.getResourceId(1, 0);
        activityStyle.recycle();

        initData();
        initView();

        mHandle.sendEmptyMessageDelayed(MSG_HIDDEN, showTime);
    }

    private void initData() {
        Intent intent = getIntent();
        type = intent.getIntExtra("type", -1);
        orderNotifyData = (OrderNotifyData) intent.getSerializableExtra("ordernotify");
        safeOrDangerous = intent.getIntExtra("safeOrDangerous", 1);
        showTime = intent.getLongExtra("showTime", 3000L);
        successOrFail = intent.getIntExtra("successOrFail", 1);
        content = intent.getStringExtra("content");
    }

    private void initView() {
        view_statusbar = findViewById(R.id.view_statusbar);
        img_notify = (ImageView) findViewById(R.id.img_notify);
        text_notify_title = (TextView) findViewById(R.id.text_notify_title);
        text_notify_content = (TextView) findViewById(R.id.text_notify_content);
        view_bottombar = findViewById(R.id.view_bottombar);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view_statusbar.getLayoutParams();
        lp.height = Utils.getStatusHeight(OrderAndTradeNotifyActivity.this);
        view_statusbar.setLayoutParams(lp);

        LinearLayout.LayoutParams lp_1 = (LinearLayout.LayoutParams) view_bottombar.getLayoutParams();
        lp_1.height = Utils.getStatusHeight(OrderAndTradeNotifyActivity.this);
        view_bottombar.setLayoutParams(lp_1);
        displayView();
    }

    private void displayView() {
        if (type == ORDERNOTIFY) {
            String title = "";
            String buyType = "";

            switch (orderNotifyData.getType()) {
                case ProductObj.TYPE_BUY_UP:
                    buyType = getResources().getString(R.string.trade_buy_up);
                    break;
                case ProductObj.TYPE_BUY_DOWN:
                    buyType = getResources().getString(R.string.trade_buy_down);
                    break;
            }
            switch (orderNotifyData.getOrderType()) {
                case 0:
                    title = getResources().getString(R.string.lable_create);
                    break;
                case 1:
                    title = getResources().getString(R.string.trade_close);
                    break;

            }
            switch (orderNotifyData.getStatus()) {
                case 1:
                    img_notify.setImageResource(R.drawable.img_ordernotify_success);
                    text_notify_title.setText(getResources().getString(R.string.ordernotify_1, title));
                    text_notify_content.setText(getResources().getString(R.string.ordernotify_6, orderNotifyData.getInstrumentName(), buyType, orderNotifyData.getAllCount()));
                    break;
                case 2:
                    img_notify.setImageResource(R.drawable.img_tradenotify_3);
                    text_notify_title.setText(getResources().getString(R.string.ordernotify_3, title));
                    if (orderNotifyData.getOrderType() == 0) {
                        text_notify_content.setText(getResources().getString(R.string.ordernotify_4, orderNotifyData.getInstrumentName(), buyType, orderNotifyData.getSuccessCount(), orderNotifyData.getFailCount()));
                    } else {
                        text_notify_content.setText(getResources().getString(R.string.ordernotify_5, orderNotifyData.getInstrumentName(), buyType, orderNotifyData.getSuccessCount(), orderNotifyData.getFailCount()));
                    }
                    break;
                case 3:
                    img_notify.setImageResource(R.drawable.img_ordernotify_fail);
                    text_notify_title.setText(getResources().getString(R.string.ordernotify_2, title));
                    text_notify_content.setText(getResources().getString(R.string.ordernotify_6, orderNotifyData.getInstrumentName(), buyType, orderNotifyData.getAllCount()));
                    break;
            }
        } else if (type == TRADENOTIFY) {
            switch (safeOrDangerous) {
                case TradeInfoData.TRADEINFO_DANGEROUS:
                    img_notify.setImageResource(R.drawable.img_tradenotify_2);
                    text_notify_title.setText(R.string.tradeinfonotify_1);
                    text_notify_content.setText(R.string.tradeinfonotify_2);
                    PreferenceSetting.setLong(OrderAndTradeNotifyActivity.this, TradeInfoData.KEY_DANGEROUS, System.currentTimeMillis());
                    break;
                case TradeInfoData.TRADEINFO_DANGEROUS_MOST_1:
                    img_notify.setImageResource(R.drawable.img_tradenotify_1);
                    text_notify_title.setText(R.string.tradeinfonotify_3);
                    text_notify_content.setText(R.string.tradeinfonotify_2);
                    PreferenceSetting.setLong(OrderAndTradeNotifyActivity.this, TradeInfoData.KEY_DANGEROUS_MOST_1, System.currentTimeMillis());

                    break;
                case TradeInfoData.TRADEINFO_DANGEROUS_MOST_2:
                    img_notify.setImageResource(R.drawable.img_tradenotify_1);
                    text_notify_title.setText(R.string.tradeinfonotify_3);
                    text_notify_content.setText(R.string.tradeinfonotify_2);
                    PreferenceSetting.setLong(OrderAndTradeNotifyActivity.this, TradeInfoData.KEY_DANGEROUS_MOST_2, System.currentTimeMillis());

                    break;
                case TradeInfoData.TRADEINFO_DANGEROUS_MOST_3:
                    img_notify.setImageResource(R.drawable.img_tradenotify_1);
                    text_notify_title.setText(R.string.tradeinfonotify_3);
                    text_notify_content.setText(R.string.tradeinfonotify_2);
                    PreferenceSetting.setLong(OrderAndTradeNotifyActivity.this, TradeInfoData.KEY_DANGEROUS_MOST_3, System.currentTimeMillis());

                    break;
            }
        } else if (type == LOGINNOTIFY) {

            switch (successOrFail) {
                case TradeLoginDlg.SUCCESS:
                    img_notify.setImageResource(R.drawable.img_ordernotify_success);
                    text_notify_title.setText("登录成功");
                    text_notify_content.setVisibility(View.GONE);
                    break;
                case TradeLoginDlg.FAIL:
                    img_notify.setImageResource(R.drawable.img_ordernotify_fail);
                    text_notify_title.setText("登录失败");
                    text_notify_content.setText(content);
                    break;
            }
        } else if (type == CASHINNOTIFY) {
            switch (successOrFail) {
                case CashInAndOutAct.CASHIN_SUCCESS:
                    img_notify.setImageResource(R.drawable.img_ordernotify_success);
                    text_notify_title.setText("充值成功");
                    text_notify_content.setVisibility(View.GONE);
                    break;
                case CashInAndOutAct.CASHIN_FAIL:
                    img_notify.setImageResource(R.drawable.img_ordernotify_fail);
                    text_notify_title.setText("充值失败");
                    text_notify_content.setText(content);
                    break;
            }
        } else if (type == CASHOUTNOTIFY) {
            switch (successOrFail) {
                case CashInAndOutAct.CASHOUT_SUCCESS:
                    img_notify.setImageResource(R.drawable.img_ordernotify_success);
                    text_notify_title.setText("提现成功");
                    text_notify_content.setVisibility(View.GONE);
                    break;
                case CashInAndOutAct.CASHOUT_FAIL:
                    img_notify.setImageResource(R.drawable.img_ordernotify_fail);
                    text_notify_title.setText("提现失败");
                    text_notify_content.setText(content);
                    break;
            }
        }
    }

    public static final int MSG_HIDDEN = 0;
    Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_HIDDEN) {
                doMyfinish();
                //    Android:windowIsTranslucent透明 导致activity退出动画无效 添加code
            }
        }
    };

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(activityCloseEnterAnimation, activityCloseExitAnimation);
    }
}
