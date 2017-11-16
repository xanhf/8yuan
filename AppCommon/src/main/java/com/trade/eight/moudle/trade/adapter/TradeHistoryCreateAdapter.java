package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.trade.activity.TradeOrderCreateDetailAct;
import com.trade.eight.tools.MyViewHolder;

import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/10.
 */

public class TradeHistoryCreateAdapter extends ArrayAdapter<TradeOrder> {
    List<TradeOrder> objects;
    Context context;

    public TradeHistoryCreateAdapter(Context context, int resource, List<TradeOrder> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public void setItems(List<TradeOrder> list, boolean clear) {
        if (list == null || list.size() == 0)
            return;

        if (clear)
            objects.clear();
        if (list != null) {
            objects.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(context, R.layout.item_tradeorder_history, null);
        }
        View line_tr_item = MyViewHolder.get(view, R.id.line_tr_item);
        TextView text_product_name = MyViewHolder.get(view, R.id.text_product_name);
        TextView text_tr_buytype = MyViewHolder.get(view, R.id.text_tr_buytype);
        TextView text_tr_buynum = MyViewHolder.get(view, R.id.text_tr_buynum);
        final ImageView img_tr_today = MyViewHolder.get(view, R.id.img_tr_today);
        TextView text_tr_junjia = MyViewHolder.get(view, R.id.text_tr_junjia);
        TextView text_tr_newprice = MyViewHolder.get(view, R.id.text_tr_newprice);
        TextView text_tr_profitloss = MyViewHolder.get(view, R.id.text_tr_profitloss);

        final TradeOrder tradeOrder = objects.get(position);
        text_product_name.setText(tradeOrder.getInstrumentName());
        if (tradeOrder.getType() == ProductObj.TYPE_BUY_UP) {
            text_tr_buytype.setText(R.string.trade_buy_up);
            text_tr_buytype.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
            text_tr_buynum.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
            text_tr_profitloss.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
        } else {
            text_tr_buytype.setText(R.string.trade_buy_down);
            text_tr_buytype.setTextColor(context.getResources().getColor(R.color.c_06A969));
            text_tr_buynum.setTextColor(context.getResources().getColor(R.color.c_06A969));
            text_tr_profitloss.setTextColor(context.getResources().getColor(R.color.c_06A969));

        }
        text_tr_buynum.setText(context.getResources().getString(R.string.lable_tr_ordernumber, tradeOrder.getPosition()));

        img_tr_today.setVisibility(View.GONE);

        text_tr_profitloss.setText(tradeOrder.getPrice());

        line_tr_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TradeOrderCreateDetailAct.startAct(context,tradeOrder);
            }
        });

        return view;
    }

}
