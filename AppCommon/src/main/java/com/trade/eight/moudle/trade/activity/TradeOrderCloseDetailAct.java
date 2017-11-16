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
 * 平仓详情
 */

public class TradeOrderCloseDetailAct extends BaseActivity {
    TradeOrder tradeOrder;
    private PullToRefreshListView mPullRefreshListView;
    private ListView listView;

    View headView = null;
    private TextView text_product_name;
    private TextView text_buytype;
    private TextView text_buynum;
    private TextView text_tr_profitloss;
    private TextView text_close_profitloss;
    private TextView text_tr_ccjj;
    private TextView text_tr_jcjj;
    private ImageView img_expand_packup;
    View footView = null;
    private TextView text_closeprice;
    private TextView text_closetime;
    private TextView text_closetype;
    private TextView text_closesxf;
    private TextView text_ordersysid;
    private TextView text_tradeid;

    TradeCloseDetailAdapter tradeCloseDetailAdapter;
    List<TradeOrder> tradeOrders;
    boolean isExpandList = false;


    AppTitleView appTitleView;

    public static void startAct(Context context, TradeOrder tradeOrder) {
        Intent intent = new Intent(context, TradeOrderCloseDetailAct.class);
        intent.putExtra("tradeOrder", tradeOrder);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_tr_closedetail);
        initData();
        initView();
        getDetailData();
    }

    private void initData() {
        tradeOrder = (TradeOrder) getIntent().getSerializableExtra("tradeOrder");
        if (tradeOrder == null) {
            doMyfinish();
        }
    }

    private void initView() {
        appTitleView  = (AppTitleView) findViewById(R.id.line_title);
        appTitleView.setBaseActivity(TradeOrderCloseDetailAct.this);
        appTitleView.setAppCommTitle(R.string.trade_close_detail);

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setPullRefreshEnabled(false);
        mPullRefreshListView.setPullLoadEnabled(false);
        listView = mPullRefreshListView.getRefreshableView();
        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(false);
        listView.setDividerHeight(0);

        headView = View.inflate(TradeOrderCloseDetailAct.this, R.layout.head_tr_closedetail, null);
        listView.addHeaderView(headView);
        footView = View.inflate(TradeOrderCloseDetailAct.this, R.layout.foot_tr_closedetail, null);
        listView.addFooterView(footView);

        tradeCloseDetailAdapter = new TradeCloseDetailAdapter(TradeOrderCloseDetailAct.this,new ArrayList<TradeOrder>());
        listView.setAdapter(tradeCloseDetailAdapter);

        text_product_name = (TextView) headView.findViewById(R.id.text_product_name);
        text_buytype = (TextView) headView.findViewById(R.id.text_buytype);
        text_buynum = (TextView) headView.findViewById(R.id.text_buynum);
        text_tr_profitloss = (TextView) headView.findViewById(R.id.text_tr_profitloss);
        text_close_profitloss = (TextView) headView.findViewById(R.id.text_close_profitloss);
         text_tr_ccjj = (TextView) headView.findViewById(R.id.text_tr_ccjj);
        text_tr_jcjj = (TextView) headView.findViewById(R.id.text_tr_jcjj);
        img_expand_packup = (ImageView) headView.findViewById(R.id.img_expand_packup);
        text_closeprice = (TextView) footView.findViewById(R.id.text_closeprice);
        text_closetime = (TextView) footView.findViewById(R.id.text_closetime);
        text_closetype = (TextView) footView.findViewById(R.id.text_closetype);
        text_closesxf = (TextView) footView.findViewById(R.id.text_closesxf);
        text_ordersysid = (TextView) footView.findViewById(R.id.text_ordersysid);
        text_tradeid = (TextView) footView.findViewById(R.id.text_tradeid);

        img_expand_packup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(!isExpandList){
                   img_expand_packup.setImageResource(R.drawable.img_deal_btn_packup);
                   tradeCloseDetailAdapter.setData(tradeOrders);
               }else{
                   img_expand_packup.setImageResource(R.drawable.img_deal_btn_expand);
                   tradeCloseDetailAdapter.setData(new ArrayList<TradeOrder>());
               }
                isExpandList = !isExpandList;
            }
        });

        displayOrder();
    }

    void displayOrder(){
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

        text_tr_profitloss.setText(tradeOrder.getProfitLoss());
        text_tr_profitloss.setTextColor(getResources().getColor(R.color.c_06A969));
        if (Double.parseDouble(ConvertUtil.NVL(tradeOrder.getProfitLoss(), "0")) > 0) {
            text_tr_profitloss.setText("+" + tradeOrder.getProfitLoss());
            text_tr_profitloss.setTextColor(getResources().getColor(R.color.c_EA4A5E));
        }

        text_close_profitloss.setText(tradeOrder.getCloseProfitLoss());
        text_close_profitloss.setTextColor(getResources().getColor(R.color.c_06A969));
        if (Double.parseDouble(ConvertUtil.NVL(tradeOrder.getCloseProfitLoss(), "0")) > 0) {
            text_close_profitloss.setText("+" + tradeOrder.getCloseProfitLoss());
            text_close_profitloss.setTextColor(getResources().getColor(R.color.c_EA4A5E));
        }
        
        text_tr_ccjj.setText(tradeOrder.getHoldAvgPrice());
        text_tr_jcjj.setText(tradeOrder.getPrice());
        text_closeprice.setText(tradeOrder.getClosePrice());
        text_closetime.setText(tradeOrder.getTradeTime());
        if(tradeOrder.getCloseType()==TradeOrder.CLOSE_BYHAND){
            text_closetype.setText("手动平仓");
        }else{
            text_closetype.setText("强制平仓");
        }
        text_closesxf.setText(tradeOrder.getSxf());
        text_ordersysid.setText(tradeOrder.getOrderSysId());
        text_tradeid.setText(tradeOrder.getTradeId()) ;
    }

    /**
     * 获取详情数据
     */
    private void getDetailData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", tradeOrder.getId() + "");
        HttpClientHelper.doPostOption(TradeOrderCloseDetailAct.this,
                AndroidAPIConfig.URL_ORDERCLOSE_DETAIL,
                map,
                null,
                new NetCallback(TradeOrderCloseDetailAct.this) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        showCusToast(resultMsg);
                    }

                    @Override
                    public void onResponse(String response) {
                        CommonResponse4List<TradeOrder> commonResponse4List = CommonResponse4List.fromJson(response,TradeOrder.class);
                        tradeOrders = commonResponse4List.getData();
                    }
                },
                true);
    }
}
