package com.trade.eight.moudle.trade.adapter;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.entity.trade.Banks;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.StringUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by developer on 16/6/6.
 * bank list adapter
 */
public class TradeOrderDetailAdapter extends ArrayAdapter<TradeOrder> {
    List<TradeOrder> objects;
    BaseActivity context;

    public TradeOrderDetailAdapter(BaseActivity context, int resource, List<TradeOrder> objects) {
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
            convertView = View.inflate(context, R.layout.item_tradeorder_detail, null);
        }
        TextView text_tr_buytype = MyViewHolder.get(convertView, R.id.text_tr_buytype);
        TextView text_tr_buynum = MyViewHolder.get(convertView, R.id.text_tr_buynum);
        TextView text_tr_createtime = MyViewHolder.get(convertView, R.id.text_tr_createtime);
        TextView text_tr_createprice = MyViewHolder.get(convertView, R.id.text_tr_createprice);
        TextView text_tr_bzj = MyViewHolder.get(convertView, R.id.text_tr_bzj);
        TextView text_tr_createsxf = MyViewHolder.get(convertView, R.id.text_tr_createsxf);

        TradeOrder tradeOrder = objects.get(position);
        if (tradeOrder.getType() == ProductObj.TYPE_BUY_UP) {
            text_tr_buytype.setText(R.string.trade_buy_up);
            text_tr_buytype.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
        } else {
            text_tr_buytype.setText(R.string.trade_buy_down);
            text_tr_buytype.setTextColor(context.getResources().getColor(R.color.c_06A969));
        }
        text_tr_buynum.setText(context.getResources().getString(R.string.lable_tr_ordernumber, tradeOrder.getCount()));
        text_tr_createtime.setText(new SimpleDateFormat("MM-dd").format(DateUtil.getShortDate(tradeOrder.getOpenDate())));
        text_tr_createprice.setText(StringUtil.forNumber(new BigDecimal(tradeOrder.getCreatePrice()).doubleValue()));
        text_tr_bzj.setText(StringUtil.forNumber(new BigDecimal(tradeOrder.getUseMargin()).doubleValue()));
        text_tr_createsxf.setText(tradeOrder.getSxf());
        return convertView;
    }

}

