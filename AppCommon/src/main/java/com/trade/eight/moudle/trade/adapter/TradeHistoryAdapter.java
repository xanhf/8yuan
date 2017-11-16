package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.config.ProFormatConfig;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.trade.TradeConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/5/27.
 * 交易记录
 */

public class TradeHistoryAdapter extends ArrayAdapter<TradeOrder> {
    List<TradeOrder> objects;
    Context context;

    public TradeHistoryAdapter(Context context, int resource, List<TradeOrder> objects) {
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
        TextView text_productname = MyViewHolder.get(convertView, R.id.text_productname);
        TextView tv_typeBuy = MyViewHolder.get(convertView, R.id.tv_typeBuy);
        TextView tv_title = MyViewHolder.get(convertView, R.id.tv_title);
        TextView tv_tips = MyViewHolder.get(convertView, R.id.tv_tips);
        TextView tv_closetime = MyViewHolder.get(convertView, R.id.tv_closetime);

        TextView tv_makeMoney = MyViewHolder.get(convertView, R.id.tv_makeMoney);
        TextView tv_priceCreate = MyViewHolder.get(convertView, R.id.tv_priceCreate);
        TextView text_current_price = MyViewHolder.get(convertView, R.id.text_current_price);
        TextView tv_zhiying = MyViewHolder.get(convertView, R.id.tv_zhiying);
        TextView tv_zhisun = MyViewHolder.get(convertView, R.id.tv_zhisun);
        TextView tv_deferred = MyViewHolder.get(convertView, R.id.tv_deferred);
        TextView tv_createmoney = MyViewHolder.get(convertView, R.id.tv_createmoney);
        TextView text_createordertime = MyViewHolder.get(convertView, R.id.text_createordertime);
        TextView text_trade_order_createfee = MyViewHolder.get(convertView, R.id.text_trade_order_createfee);
        TextView text_createordertype = MyViewHolder.get(convertView, R.id.text_createordertype);


        final TradeOrder item = objects.get(position);

        text_productname.setText(item.getNameByCode(context));

        if (item.getType() == TradeOrder.BUY_DOWN) {

            tv_typeBuy.setTextColor(context.getResources().getColor(R.color.color_opt_lt));
            tv_title.setTextColor(context.getResources().getColor(R.color.color_opt_lt));
            tv_typeBuy.setText("买跌");
        } else {
            tv_typeBuy.setTextColor(context.getResources().getColor(R.color.color_opt_gt));
            tv_title.setTextColor(context.getResources().getColor(R.color.color_opt_gt));
            tv_typeBuy.setText("买涨");

        }
        tv_title.setText(item.getOrderNumber() + "手");
        if (!TextUtils.isEmpty(item.getCloseTime())) {
            tv_closetime.setText(DateUtil.formatDate(item.getCloseTime(), "yyyy-MM-dd HH:mm", "MM-dd HH:mm"));
        }

        tv_makeMoney.setText(ConvertUtil.NVL(item.getProfitLoss(), "0"));
        tv_makeMoney.setTextColor(context.getResources().getColor(R.color.trade_up));
        if (Double.parseDouble(ConvertUtil.NVL(item.getProfitLoss(), "0")) < 0) {
            tv_makeMoney.setTextColor(context.getResources().getColor(R.color.trade_down));
        }
//        tv_priceCreate.setText(ConvertUtil.NVL(item.getCreatePrice(), ""));
//        text_current_price.setText(ConvertUtil.NVL(item.getClosePrice(), ""));

        tv_priceCreate.setText(ProFormatConfig.formatByCodes((TextUtils.isEmpty(item.getExcode())? TradeConfig.DEFAULT_EXCHANGE: item.getExcode())+ "|" + item.getCode(), ConvertUtil.NVL(item.getCreatePrice(), "")));
        text_current_price.setText(ProFormatConfig.formatByCodes((TextUtils.isEmpty(item.getExcode())? TradeConfig.DEFAULT_EXCHANGE: item.getExcode()) + "|" + item.getCode(), ConvertUtil.NVL(item.getClosePrice(), "")));

        tv_zhiying.setText(item.getStopProfit());
        tv_zhisun.setText(item.getStopLoss());
        if(Double.parseDouble(item.getDeferred())>0){
            tv_deferred.setText("+"+ConvertUtil.NVL(item.getDeferred(), "0"));
        }else{
            tv_deferred.setText(ConvertUtil.NVL(item.getDeferred(), "0"));
        }
        tv_createmoney.setText(ConvertUtil.NVL(item.getAmount(), "0"));
        text_trade_order_createfee.setText(ConvertUtil.NVL(item.getFee(), "0"));
        text_createordertype.setText(ConvertUtil.NVL(item.getCloseType(), ""));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf_1 = new SimpleDateFormat("MM-dd HH:mm");
        try {
            if (!TextUtils.isEmpty(item.getCreateTime())) {

                text_createordertime.setText(sdf_1.format(sdf.parse(item.getCreateTime())));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
