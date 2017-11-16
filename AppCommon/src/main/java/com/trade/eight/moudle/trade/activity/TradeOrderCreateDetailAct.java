package com.trade.eight.moudle.trade.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.trade.adapter.TradeCloseDetailAdapter;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.view.AppTitleView;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/6.
 * 建仓详情
 */

public class TradeOrderCreateDetailAct extends BaseActivity {
    TradeOrder tradeOrder;

    AppTitleView appTitleView;
    TextView text_product_name;
    TextView text_buytype;
    TextView text_buynum;
    TextView text_trade_order_createprice;
    TextView text_create_time;
    TextView text_createsxf;
    TextView text_ordersysid;
    TextView text_tradeid;

    public static void startAct(Context context, TradeOrder tradeOrder) {
        Intent intent = new Intent(context, TradeOrderCreateDetailAct.class);
        intent.putExtra("tradeOrder", tradeOrder);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_tr_createdetail);
        initData();
        initView();
    }

    private void initData() {
        tradeOrder = (TradeOrder) getIntent().getSerializableExtra("tradeOrder");
        if (tradeOrder == null) {
            doMyfinish();
        }
    }

    private void initView() {
        appTitleView = (AppTitleView) findViewById(R.id.line_title);
        appTitleView.setBaseActivity(TradeOrderCreateDetailAct.this);
        appTitleView.setAppCommTitle(R.string.trade_create_detail);

        text_product_name = (TextView) findViewById(R.id.text_product_name);
        text_buytype = (TextView) findViewById(R.id.text_buytype);
        text_buynum = (TextView) findViewById(R.id.text_buynum);
        text_trade_order_createprice = (TextView) findViewById(R.id.text_trade_order_createprice);
        text_create_time = (TextView) findViewById(R.id.text_create_time);
        text_createsxf = (TextView) findViewById(R.id.text_createsxf);
        text_ordersysid = (TextView) findViewById(R.id.text_ordersysid);
        text_tradeid = (TextView) findViewById(R.id.text_tradeid);

        displayOrder();
    }

    void displayOrder() {
        text_product_name.setText(tradeOrder.getInstrumentName());
        if (tradeOrder.getType() == ProductObj.TYPE_BUY_UP) {
            text_buytype.setText(R.string.trade_buy_up);
            text_buytype.setTextColor(getResources().getColor(R.color.c_EA4A5E));
            text_buynum.setTextColor(getResources().getColor(R.color.c_EA4A5E));
        } else {
            text_buytype.setText(R.string.trade_buy_down);
            text_buytype.setTextColor(getResources().getColor(R.color.c_06A969));
            text_buynum.setTextColor(getResources().getColor(R.color.c_06A969));

        }
        text_buynum.setText(getResources().getString(R.string.lable_tr_ordernumber, tradeOrder.getPosition()));
        text_trade_order_createprice.setText(tradeOrder.getPrice());
        text_create_time.setText(tradeOrder.getTradeTime());
        text_createsxf.setText(tradeOrder.getSxf());
        text_ordersysid.setText(tradeOrder.getOrderSysId());
        text_tradeid.setText(tradeOrder.getTradeId());
    }

}
