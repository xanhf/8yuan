package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.trade.CheckBalanceData;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.tools.MyViewHolder;

import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/5.
 */

public class CheckBalanceCreateAdapter extends BaseAdapter {


    Context context;
    List<CheckBalanceData.TransactionRecord> data;

    public CheckBalanceCreateAdapter(Context context, List<CheckBalanceData.TransactionRecord> data) {
        this.context = context;
        this.data = data;
    }

    public void setData(List<CheckBalanceData.TransactionRecord> data){
        this.data.clear();
        if(data==null){
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
            view = View.inflate(context, R.layout.item_checkbalance_create, null);
        }
        TextView text_cb_name = MyViewHolder.get(view, R.id.text_cb_name);
        TextView text_tr_buytype = MyViewHolder.get(view, R.id.text_tr_buytype);
        TextView text_tr_buynum = MyViewHolder.get(view, R.id.text_tr_buynum);
        TextView text_tr_createprice = MyViewHolder.get(view, R.id.text_tr_createprice);
        TextView text_tr_createsxf = MyViewHolder.get(view, R.id.text_tr_createsxf);

        CheckBalanceData.TransactionRecord transactionRecord = data.get(position);
        text_cb_name.setText(transactionRecord.getProduct());
        text_tr_buytype.setText(transactionRecord.getBs());
        text_tr_buynum.setText(transactionRecord.getLots()+"手");
        text_tr_createprice.setText(transactionRecord.getPrice());
        text_tr_createsxf.setText(transactionRecord.getFee());
        if (transactionRecord.getBs().contains("涨")) {
            text_tr_buytype.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
        } else {
            text_tr_buytype.setTextColor(context.getResources().getColor(R.color.c_06A969));
        }
        return view;
    }
}
