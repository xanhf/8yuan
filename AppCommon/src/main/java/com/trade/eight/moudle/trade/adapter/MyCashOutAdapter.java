package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.trade.TradeCashOut;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.MyViewHolder;

import java.util.List;

/**
 * Created by developer on 16/1/17.
 * 收支明细
 */
public class MyCashOutAdapter extends ArrayAdapter<TradeCashOut> {
    List<TradeCashOut> objects;
    Context context;

    public MyCashOutAdapter(Context context, int resource, List<TradeCashOut> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public void setItems(List<TradeCashOut> list, boolean clear) {
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
        TextView tv_title = MyViewHolder.get(convertView, R.id.tv_title);
        TextView tv_time = MyViewHolder.get(convertView, R.id.tv_time);
        TextView tv_makeMoney = MyViewHolder.get(convertView, R.id.tv_makeMoney);
        TextView tv_market = MyViewHolder.get(convertView, R.id.tv_market);

        final TradeCashOut item = objects.get(position);

        tv_title.setText(item.getTitle());
        tv_makeMoney.setTextColor(context.getResources().getColor(R.color.color_opt_lt));
        tv_makeMoney.setText("-"+item.getBalance());
        tv_time.setText(item.getLastHandleTime());
        tv_market.setText(ConvertUtil.NVL(item.getState(), ""));
        return convertView;
    }
}
