package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.trade.TradeRedPacketDetail;
import com.trade.eight.tools.MyViewHolder;

import java.util.List;

/**
 * Created by developer on 16/1/17.
 * 交易记录adapter
 */
public class MyRedPacketAdapter extends ArrayAdapter<TradeRedPacketDetail> {
    List<TradeRedPacketDetail> objects;
    Context context;

    public MyRedPacketAdapter(Context context, int resource, List<TradeRedPacketDetail> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public void setItems(List<TradeRedPacketDetail> list, boolean clear) {
//        if (list == null || list.size() == 0)
//            return;
        if (clear)
            objects.clear();
        if (list != null) {
            objects.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return objects.size() == 0 ? 1 : objects.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (objects.size() == 0) {
            convertView = View.inflate(context, R.layout.item_redpacket_emptyview, null);
            return convertView;
        }

        if (convertView == null||!(convertView.getTag() instanceof MyViewHolder )) {
            convertView = View.inflate(context, R.layout.item_redpacket_detail, null);
        }
        TextView tv_title = MyViewHolder.get(convertView, R.id.tv_title);
        TextView tv_time = MyViewHolder.get(convertView, R.id.tv_time);
        TextView tv_makeMoney = MyViewHolder.get(convertView, R.id.tv_makeMoney);

        final TradeRedPacketDetail item = objects.get(position);

        tv_title.setText(item.getTitle());
        tv_time.setText(item.getCreateDate());

        if (item.getInOut() == 1) {
            tv_makeMoney.setTextColor(context.getResources().getColor(R.color.trade_up));
            tv_makeMoney.setText("+" + item.getAmount() + "元");

        } else {
            tv_makeMoney.setTextColor(context.getResources().getColor(R.color.trade_down));
            tv_makeMoney.setText("-" + item.getAmount() + "元");
        }

        return convertView;
    }
}

