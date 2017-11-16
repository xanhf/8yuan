package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.trade.TradeRechargeHistory;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.MyViewHolder;

import java.util.List;

/**
 * Created by developer on 16/1/17.
 * 收支明细
 */
public class MyRechargeAdapter extends ArrayAdapter<TradeRechargeHistory> {
    List<TradeRechargeHistory> objects;
    Context context;

    public MyRechargeAdapter(Context context, int resource, List<TradeRechargeHistory> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public void setItems(List<TradeRechargeHistory> list, boolean clear) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_trade_mingxi, null);
        }
        TextView tv_labletime = MyViewHolder.get(convertView, R.id.tv_labletime);
        TextView tv_title = MyViewHolder.get(convertView, R.id.tv_title);
        TextView tv_time = MyViewHolder.get(convertView, R.id.tv_time);
        TextView tv_makeMoney = MyViewHolder.get(convertView, R.id.tv_makeMoney);
        TextView tv_market = MyViewHolder.get(convertView, R.id.tv_market);

        final TradeRechargeHistory item = objects.get(position);


        tv_labletime.setText(item.getCreateTime());
        tv_title.setText(item.getTitle());
        tv_makeMoney.setTextColor(context.getResources().getColor(R.color.trade_up));
        tv_makeMoney.setText("+"+NumberUtil.moveLast0(item.getAmount()));
        tv_time.setText(item.getCreateTime());
        tv_market.setText(item.getState());
        return convertView;
    }
}
