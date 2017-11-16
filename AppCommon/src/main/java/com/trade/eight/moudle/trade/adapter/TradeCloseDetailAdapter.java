package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.trade.CheckBalanceData;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.StringUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/5.
 * 账单详情
 */

public class TradeCloseDetailAdapter extends BaseAdapter {

    Context context;
    List<TradeOrder> data;

    public TradeCloseDetailAdapter(Context context, List<TradeOrder> data) {
        this.context = context;
        this.data = data;
    }


    public void setData(List<TradeOrder> data) {
        this.data.clear();
        if (data == null) {
            return;
        }
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(context, R.layout.item_tr_closedetail, null);
        }
        TextView text_tr_buytype = MyViewHolder.get(view, R.id.text_tr_buytype);
        TextView text_tr_buynum = MyViewHolder.get(view, R.id.text_tr_buynum);
        TextView text_tr_createprice = MyViewHolder.get(view, R.id.text_tr_createprice);
        TextView text_tr_creat_time = MyViewHolder.get(view, R.id.text_tr_creat_time);
//        TextView text_trade_order_bzj = MyViewHolder.get(view, R.id.text_trade_order_bzj);
        TextView text_trade_order_createsxf = MyViewHolder.get(view, R.id.text_trade_order_createsxf);
        TextView text_trade_ordersysid = MyViewHolder.get(view, R.id.text_trade_ordersysid);
        TextView text_trade_tradeid = MyViewHolder.get(view, R.id.text_trade_tradeid);

        TradeOrder tradeOrder = data.get(position);
        if (tradeOrder.getType() == ProductObj.TYPE_BUY_UP) {
            text_tr_buytype.setText(R.string.trade_buy_up);
            text_tr_buytype.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
        } else {
            text_tr_buytype.setText(R.string.trade_buy_down);
            text_tr_buytype.setTextColor(context.getResources().getColor(R.color.c_06A969));
        }
        text_tr_buynum.setText(context.getResources().getString(R.string.lable_tr_ordernumber, tradeOrder.getPosition()));
        text_tr_createprice.setText(tradeOrder.getOpenPrice());
        text_tr_creat_time.setText(tradeOrder.getOpenTime());
//        text_trade_order_bzj.setText(tradeOrder.getUseMargin());
        text_trade_order_createsxf.setText(tradeOrder.getOpenSxf());
        text_trade_ordersysid.setText(tradeOrder.getOrderSysId());
        text_trade_tradeid.setText(tradeOrder.getTradeId());

        return view;
    }
}
