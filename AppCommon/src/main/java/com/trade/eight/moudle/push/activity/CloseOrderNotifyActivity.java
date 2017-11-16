package com.trade.eight.moudle.push.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.push.entity.PushExtraObj;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 作者：Created by ocean
 * 时间：on 2017/6/3.
 */

public class CloseOrderNotifyActivity extends BaseActivity {

    View rel_close;
    PushExtraObj closeOrderNotify;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final int STOP_PROFIT_CLOSE = 4;//止盈平仓
    public static final int STOP_LOSS_CLOSE = 5;//止损平仓
    public static final int MARGIN_CALL = 6;//爆仓
    public static final int QUOTATION_CLOSE = 7;//休市平仓
    public static final int FORCE_CLOSE = 8;//强制平仓


    public static void startAct(Context context, PushExtraObj closeOrderNotify) {
        Intent intent = new Intent();
        intent.putExtra("closeOrderNotify", closeOrderNotify);
        intent.setClass(context, CloseOrderNotifyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = this.getWindow();
        win.getDecorView().setPadding(Utils.dip2px(this, 10), Utils.dip2px(this, 10), Utils.dip2px(this, 10), Utils.getVirtualBarHeigh(this));
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP;//设置对话框置顶显示
        win.setAttributes(lp);
//        setStatusBarTintResource(R.color.transparent);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.layout_closeorder_notify);
        initData();
        initView();
    }

    private void initData() {
        closeOrderNotify = (PushExtraObj) getIntent().getSerializableExtra("closeOrderNotify");
    }

    private void initView() {
        rel_close = findViewById(R.id.rel_close);
        rel_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doMyfinish();
            }
        });

        TextView text_productname = (TextView) findViewById(R.id.text_productname);
        TextView tv_typeBuy = (TextView) findViewById(R.id.tv_typeBuy);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        TextView tv_closetime = (TextView) findViewById(R.id.tv_closetime);

        TextView tv_makeMoney = (TextView) findViewById(R.id.tv_makeMoney);
        TextView tv_priceCreate = (TextView) findViewById(R.id.tv_priceCreate);
        TextView text_current_price = (TextView) findViewById(R.id.text_current_price);
        TextView tv_zhiying = (TextView) findViewById(R.id.tv_zhiying);
        TextView tv_zhisun = (TextView) findViewById(R.id.tv_zhisun);
        TextView tv_deferred = (TextView) findViewById(R.id.tv_deferred);
        TextView tv_createmoney = (TextView) findViewById(R.id.tv_createmoney);
        TextView text_createordertime = (TextView) findViewById(R.id.text_createordertime);
        TextView text_trade_order_createfee = (TextView) findViewById(R.id.text_trade_order_createfee);
        TextView text_trade_order_closetype = (TextView) findViewById(R.id.text_trade_order_closetype);

        text_productname.setText(closeOrderNotify.getProductName());

        if (closeOrderNotify.getType() == TradeOrder.BUY_DOWN) {

            tv_typeBuy.setTextColor(getResources().getColor(R.color.color_opt_lt));
            tv_title.setTextColor(getResources().getColor(R.color.color_opt_lt));
            tv_typeBuy.setText("买跌");
        } else {
            tv_typeBuy.setTextColor(getResources().getColor(R.color.color_opt_gt));
            tv_title.setTextColor(getResources().getColor(R.color.color_opt_gt));
            tv_typeBuy.setText("买涨");

        }
        tv_title.setText(closeOrderNotify.getOrderNumber() + "手");

        if (!TextUtils.isEmpty(closeOrderNotify.getCloseTime())) {

            tv_closetime.setText(DateUtil.formatDate(closeOrderNotify.getCloseTime(), "yyyy-MM-dd HH:mm", "MM-dd HH:mm"));
        }


        tv_makeMoney.setText(ConvertUtil.NVL(closeOrderNotify.getProfitLoss(), "0"));
        tv_makeMoney.setTextColor(getResources().getColor(R.color.trade_up));
        if (Double.parseDouble(ConvertUtil.NVL(closeOrderNotify.getProfitLoss(), "0")) < 0) {
            tv_makeMoney.setTextColor(getResources().getColor(R.color.trade_down));
        }
        tv_priceCreate.setText(ConvertUtil.NVL(closeOrderNotify.getCreatePrice(), ""));
        text_current_price.setText(ConvertUtil.NVL(closeOrderNotify.getClosePrice(), ""));
        tv_zhiying.setText(closeOrderNotify.getStopProfit());
        tv_zhisun.setText(closeOrderNotify.getStopLoss());
        tv_deferred.setText(ConvertUtil.NVL(closeOrderNotify.getDeferred(), "0"));
        tv_createmoney.setText(ConvertUtil.NVL(closeOrderNotify.getAmount(), "0"));
        text_trade_order_createfee.setText(ConvertUtil.NVL(closeOrderNotify.getFee(), "0"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf_1 = new SimpleDateFormat("MM-dd HH:mm");
        try {
            if (!TextUtils.isEmpty(closeOrderNotify.getCreateTime())) {
                text_createordertime.setText(sdf_1.format(sdf.parse(closeOrderNotify.getCreateTime())));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        switch (closeOrderNotify.getCloseType()) {
            case STOP_PROFIT_CLOSE:
                text_trade_order_closetype.setText("止盈平仓");
                break;
            case STOP_LOSS_CLOSE:
                text_trade_order_closetype.setText("止损平仓");
                break;
            case MARGIN_CALL:
                text_trade_order_closetype.setText("爆仓");
                break;
            case QUOTATION_CLOSE:
                text_trade_order_closetype.setText("休市平仓");
                break;
            case FORCE_CLOSE:
                text_trade_order_closetype.setText("强制平仓");
                break;
        }

    }

}
