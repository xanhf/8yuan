package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.trade.TradeVoucher;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.MyViewHolder;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by developer on 16/1/17.
 * 代金券
 */
public class MyVoucherAdapter extends ArrayAdapter<TradeVoucher> {
    List<TradeVoucher> objects;
    Context context;

    public MyVoucherAdapter(Context context, int resource, List<TradeVoucher> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public void setItems(List<TradeVoucher> list, boolean clear) {
        if (list == null || list.size() == 0)
            return;

        if (clear)
            objects.clear();
        if (list != null) {
            //过滤掉已经使用了的 代金券
            for (int i = 0; i < list.size(); i ++) {
                TradeVoucher item = list.get(i);
                if (item.getType() != TradeVoucher.TYPE_USED) {
                    objects.add(item);
                }
            }

//            objects.addAll(list);
            notifyDataSetChanged();
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_trade_voucher, null);
        }
        View leftView = MyViewHolder.get(convertView, R.id.leftView);
        View rightView = MyViewHolder.get(convertView, R.id.rightView);
        TextView tv_title = MyViewHolder.get(convertView, R.id.tv_title);
        TextView tv_moneyIcon = MyViewHolder.get(convertView, R.id.tv_moneyIcon);
        TextView tv_lable = MyViewHolder.get(convertView, R.id.tv_lable);
        TextView tv_time = MyViewHolder.get(convertView, R.id.tv_time);
        final TradeVoucher item = objects.get(position);
//        String amonut = ConvertUtil.NVL(item.getAmount(), "0").replace("元券", "");
        String am = ConvertUtil.NVL(item.getAmount(), "0");
        Pattern p = Pattern.compile("[^0-9]");
        Matcher matcher = p.matcher(am);
        String amonut = matcher.replaceAll("");
        //设置背景色
        if (amonut.equals("8")) {
            leftView.setBackgroundResource(R.drawable.item_voucher_left_8);
            rightView.setBackgroundResource(R.drawable.item_voucher_right_8);
            tv_moneyIcon.setTextColor(context.getResources().getColor(R.color.voucher_8));
            tv_title.setTextColor(context.getResources().getColor(R.color.voucher_8));
        } else if (amonut.equals("80")) {
            leftView.setBackgroundResource(R.drawable.item_voucher_left_80);
            rightView.setBackgroundResource(R.drawable.item_voucher_right_80);
            tv_moneyIcon.setTextColor(context.getResources().getColor(R.color.voucher_80));
            tv_title.setTextColor(context.getResources().getColor(R.color.voucher_80));

        } else if (amonut.equals("200")) {
            leftView.setBackgroundResource(R.drawable.item_voucher_left_200);
            rightView.setBackgroundResource(R.drawable.item_voucher_right_200);
            tv_moneyIcon.setTextColor(context.getResources().getColor(R.color.voucher_200));
            tv_title.setTextColor(context.getResources().getColor(R.color.voucher_200));
        } else {
            leftView.setBackgroundResource(R.drawable.item_voucher_left_8);
            rightView.setBackgroundResource(R.drawable.item_voucher_right_8);
            tv_moneyIcon.setTextColor(context.getResources().getColor(R.color.voucher_8));
            tv_title.setTextColor(context.getResources().getColor(R.color.voucher_8));
        }

        tv_title.setText(amonut);

//        tv_time.setText("有效期："+ DateUtil.formatDate(item.getLimitTime(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"));
        tv_time.setText("有效期："+ ConvertUtil.NVL(item.getLimitTime(), ""));
        String str = context.getString(R.string.voucher_tips);
        tv_lable.setText(String.format(str, ConvertUtil.NVL(amonut, "0")));
        //已经使用过了
        if (item.getType() == TradeVoucher.TYPE_USED) {

        } else {

        }
        return convertView;
    }
}
