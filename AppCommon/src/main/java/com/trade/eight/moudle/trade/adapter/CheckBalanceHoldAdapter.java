package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.trade.CheckBalanceData;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.StringUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/5.
 */

public class CheckBalanceHoldAdapter extends BaseAdapter {


    Context context;
    List<CheckBalanceData.PositionsDetail> data;

    public CheckBalanceHoldAdapter(Context context, List<CheckBalanceData.PositionsDetail> data) {
        this.context = context;
        this.data = data;
    }

    public void setData(List<CheckBalanceData.PositionsDetail> data) {

        this.data.clear();
        if (data == null) {
            return;
        }
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(context, R.layout.item_checkbalance_hold, null);
        }
        TextView text_cb_name = MyViewHolder.get(view, R.id.text_cb_name);
        TextView text_tr_buytype = MyViewHolder.get(view, R.id.text_tr_buytype);
        TextView text_tr_buynum = MyViewHolder.get(view, R.id.text_tr_buynum);
        TextView text_tr_jsj = MyViewHolder.get(view, R.id.text_tr_jsj);
        TextView text_tr_dryk = MyViewHolder.get(view, R.id.text_tr_dryk);
        TextView text_tr_sjyk = MyViewHolder.get(view, R.id.text_tr_sjyk);

        CheckBalanceData.PositionsDetail positionsDetail = data.get(position);
        text_cb_name.setText(positionsDetail.getProduct());
        text_tr_buytype.setText(positionsDetail.getBs());
        text_tr_buynum.setText(positionsDetail.getPositon() + "手");
        text_tr_jsj.setText(positionsDetail.getSettlementPrice());
        text_tr_dryk.setText(positionsDetail.getMtmPl());
        text_tr_sjyk.setText(StringUtil.forNumber(new BigDecimal(positionsDetail.getAccumPL()).doubleValue()));
        if (positionsDetail.getBs().contains("涨")) {
            text_tr_buytype.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
        } else {
            text_tr_buytype.setTextColor(context.getResources().getColor(R.color.c_06A969));
        }
        return view;
    }
}
