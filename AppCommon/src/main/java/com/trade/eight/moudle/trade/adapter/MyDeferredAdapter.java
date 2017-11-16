package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.OrderDeferred;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.MyViewHolder;

import java.util.List;

/**
 * Created by developer
 * 过夜费adapter
 */
public class MyDeferredAdapter extends ArrayAdapter<OrderDeferred> {
    List<OrderDeferred> objects;
    Context context;

    public MyDeferredAdapter(Context context, int resource, List<OrderDeferred> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public void setItems(List<OrderDeferred> list, boolean clear) {
        if (list == null || list.size() == 0)
            return;
        if (clear)
            objects.clear();
        objects.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_trade_deferred, null);
        }
        TextView tv_amount = MyViewHolder.get(convertView, R.id.tv_amount);
        TextView tv_time = MyViewHolder.get(convertView, R.id.tv_time);
        final OrderDeferred item = objects.get(position);
        tv_amount.setText(ConvertUtil.NVL(item.getAmount() , "") + "元");
        tv_time.setText(item.getDate());

        return convertView;
    }
}
