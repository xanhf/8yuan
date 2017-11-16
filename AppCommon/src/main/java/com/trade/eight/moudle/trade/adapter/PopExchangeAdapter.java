package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.Exchange;
import com.trade.eight.tools.MyViewHolder;

import java.util.List;

/**
 * 建仓页面下拉更多产品
 * dialog的adapter
 */
public class PopExchangeAdapter extends ArrayAdapter<Exchange> {
    List<Exchange> objects;
    Context context;


    public PopExchangeAdapter(Context context, int resource, List<Exchange> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_pop_trade, null);
        }
        TextView tv_name = MyViewHolder.get(convertView, R.id.btnContent);
        final Exchange item = objects.get(position);
        tv_name.setText(item.getExchangeName());

        return convertView;
    }
}
