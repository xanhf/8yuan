package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.trade.TradeDetail;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.MyViewHolder;

import java.util.List;

/**
 * Created by developer on 16/1/17.
 * 收支明细
 */
public class MyDetailDataAdapter extends ArrayAdapter<TradeDetail> {
    List<TradeDetail> objects;
    Context context;

    public MyDetailDataAdapter(Context context, int resource, List<TradeDetail> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public void setItems(List<TradeDetail> list, boolean clear) {
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

        final TradeDetail item = objects.get(position);

//        if (position == 0) {
//            tv_labletime.setVisibility(View.VISIBLE);
//        } else {
//            //当前时间和前一个时间对比 年月日 相同 只显示一个
//            String strTimePre = DateUtil.formatDate(objects.get(position - 1).getCreateTime(), "yyyy-MM-dd HH:mm", "yyyy年MM月");
//            String strTime = DateUtil.formatDate(objects.get(position).getCreateTime(), "yyyy-MM-dd HH:mm", "yyyy年MM月");
//            if (strTimePre != null && !strTimePre.equals(strTime)) {
//                tv_labletime.setVisibility(View.VISIBLE);
//            } else {
//                tv_labletime.setVisibility(View.GONE);
//            }
//        }
        tv_labletime.setText(DateUtil.formatDate(item.getCreateTime(), "yyyy-MM-dd HH:mm", "yyyy年MM月"));
        //类型
//        tv_title.setText(ConvertUtil.NVL(item.getTypeName(), ""));
        //备注

        String textStr = ConvertUtil.NVL(item.getTypeName(), "");
//        if (!StringUtil.isEmpty(item.getRemark())) {
//            textStr = textStr + "(" + item.getRemark() + ")";
//        }
        tv_title.setText(textStr);
        tv_market.setText(item.getRemark());
//        tv_time.setText(item.getCreateTime());
        tv_time.setText(DateUtil.formatDate(item.getCreateTime(), "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm"));

        tv_makeMoney.setTextColor(context.getResources().getColor(R.color.trade_down));
        try {
            if (Double.parseDouble(ConvertUtil.NVL(item.getAmount(), "0").replace(",", "")) > 0) {
                tv_makeMoney.setTextColor(context.getResources().getColor(R.color.trade_up));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_makeMoney.setText(NumberUtil.moveLast0(item.getAmount()) + "元");
        return convertView;
    }
}
