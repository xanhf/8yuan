package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.MyViewHolder;

import java.util.List;

/**
 * Created by developer on 16/1/17.
 * 交易记录adapter
 */
public class MyHistoryDataAdapter extends ArrayAdapter<TradeOrder> {
    List<TradeOrder> objects;
    Context context;

    public MyHistoryDataAdapter(Context context, int resource, List<TradeOrder> objects) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_trade_history, null);
        }
//        TextView tv_typeBuy = MyViewHolder.get(convertView, R.id.tv_typeBuy);
        TextView tv_labletime = MyViewHolder.get(convertView, R.id.tv_labletime);
        TextView tv_title = MyViewHolder.get(convertView, R.id.tv_title);
        TextView tv_time = MyViewHolder.get(convertView, R.id.tv_time);
        TextView tv_makeMoney = MyViewHolder.get(convertView, R.id.tv_makeMoney);

        final TradeOrder item = objects.get(position);

        //时间的处理 这里用平仓时间
//        if (position == 0) {
//            tv_labletime.setVisibility(View.VISIBLE);
//        } else {
//            //当前时间和前一个时间对比 年月日 相同 只显示一个
//            String strTimePre = DateUtil.formatDate(objects.get(position - 1).getCloseTime(), "yyyy-MM-dd HH:mm", "yyyy年MM月");
//            String strTime = DateUtil.formatDate(objects.get(position).getCloseTime(), "yyyy-MM-dd HH:mm", "yyyy年MM月");
//            if (strTimePre != null && !strTimePre.equals(strTime)) {
//                tv_labletime.setVisibility(View.VISIBLE);
//            } else {
//                tv_labletime.setVisibility(View.GONE);
//            }
//        }
//        tv_labletime.setText(DateUtil.formatDate(item.getCloseTime(), "yyyy-MM-dd HH:mm", "yyyy年MM月"));

//        tv_title.setText(ConvertUtil.NVL(item.getProductName(), "") +
//                " " + ConvertUtil.NVL(item.getType(), "") +
//                "" + ConvertUtil.NVL(item.getOrderNumber(), "") + "手"
//                + " (" + ConvertUtil.NVL(item.getAmount(), "") + "元)");

        tv_title.setText(ConvertUtil.NVL(item.getProductName(), "") +
                " (" + ConvertUtil.NVL(item.getAmount(), "") + "元)");
//        tv_time.setText(ConvertUtil.NVL(item.getCloseTime(), ""));
        tv_time.setText(DateUtil.formatDate(item.getCloseTime(), "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm"));


        // 盈利 亏损
        double getMoney = 0;
        try {
            getMoney = Double.parseDouble(item.getProfitLoss().replace(",", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_makeMoney.setText("+" + NumberUtil.moveLast0(getMoney) + "元");
        tv_makeMoney.setTextColor(context.getResources().getColor(R.color.trade_up));
        if (getMoney < 0) {
            tv_makeMoney.setText(NumberUtil.moveLast0(getMoney) + ""+ "元");
            tv_makeMoney.setTextColor(context.getResources().getColor(R.color.trade_down));
        }

        return convertView;
    }
}

