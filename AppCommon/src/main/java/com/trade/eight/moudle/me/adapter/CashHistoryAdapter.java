package com.trade.eight.moudle.me.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.trade.CashHistoryData;
import com.trade.eight.entity.trade.TradeRechargeHistory;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.MyViewHolder;

import java.util.List;

/**
 * Created by developer on 16/1/17.
 * 收支明细
 */
public class CashHistoryAdapter extends ArrayAdapter<CashHistoryData> {
    List<CashHistoryData> objects;
    Context context;

    public CashHistoryAdapter(Context context, int resource, List<CashHistoryData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public void setItems(List<CashHistoryData> list, boolean clear) {
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
            convertView = View.inflate(context, R.layout.item_cash_history, null);
        }
        TextView tv_cashtype = MyViewHolder.get(convertView, R.id.tv_cashtype);
        TextView tv_makeMoney = MyViewHolder.get(convertView, R.id.tv_makeMoney);
        TextView tv_time = MyViewHolder.get(convertView, R.id.tv_time);
        TextView tv_status = MyViewHolder.get(convertView, R.id.tv_status);
        TextView tv_bankname = MyViewHolder.get(convertView, R.id.tv_bankname);
        TextView tv_bankcard_num = MyViewHolder.get(convertView, R.id.tv_bankcard_num);
        final CashHistoryData item = objects.get(position);

        tv_cashtype.setText(item.getType());
        tv_makeMoney.setText(item.getAmount());
        tv_time.setText(item.getDate());
        tv_status.setText(item.getStatus());
        tv_bankname.setText(item.getBankName());
        tv_bankcard_num.setText(context.getResources().getString(R.string.lable_cardnum_last, TextUtils.isEmpty(item.getBankAccount()) ? "****" : item.getBankAccount().substring(item.getBankAccount().length() - 4, item.getBankAccount().length())));
        return convertView;
    }
}
