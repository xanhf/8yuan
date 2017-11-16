package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.trade.TradeCloseOptionEvent;
import com.trade.eight.tools.MyViewHolder;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/17.
 * 产品详情页的适配器
 */

public class TradeClose4LiveAdapter extends ArrayAdapter<TradeOrder>{
    List<TradeOrder> objects;
    Context context;
    private boolean isExpandMenu = false;
    private View optionView = null;
    private ImageView optionImageView = null;
    private TradeOrder optionTradeOrder = null;
    private int selectPositon = -1;

    public TradeClose4LiveAdapter(Context context, int resource, List<TradeOrder> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public void setData(List<TradeOrder> list){
        this.objects.clear();
        if(list!=null){
            this.objects.addAll(list);
        }
        notifyDataSetChanged();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_tradeclose_live, null);
        }
        View  line_tradeclose_info  = MyViewHolder.get(convertView,R.id.line_tradeclose_info);
        final ImageView imgCheck = MyViewHolder.get(convertView,R.id.imgCheck);
        TextView text_instrumentname = MyViewHolder.get(convertView,R.id.text_instrumentname);
        TextView text_buytype_count = MyViewHolder.get(convertView,R.id.text_buytype_count);
        TextView text_trade_order_ccjj = MyViewHolder.get(convertView,R.id.text_trade_order_ccjj);
        TextView text_trade_order_ccyk = MyViewHolder.get(convertView,R.id.text_trade_order_ccyk);
        final View  line_tradeclose_menu  = MyViewHolder.get(convertView,R.id.line_tradeclose_menu);
//        TextView text_trade_close_price = MyViewHolder.get(convertView,R.id.text_trade_close_price);
//        TextView text_trade_close_num = MyViewHolder.get(convertView,R.id.text_trade_close_num);
//        Button btn_deal_reduce = MyViewHolder.get(convertView,R.id.btn_deal_reduce);
//        EditText ed_num = MyViewHolder.get(convertView,R.id.ed_num);
//        Button btn_deal_add = MyViewHolder.get(convertView,R.id.btn_deal_add);
//        TextView text_trade_close_sxf = MyViewHolder.get(convertView,R.id.text_trade_close_sxf);
//        TextView text_trade_close_orderprofit = MyViewHolder.get(convertView,R.id.text_trade_close_orderprofit);

        final TradeOrder tradeOrder = objects.get(position);
        text_instrumentname.setText(tradeOrder.getInstrumentName());
        if (tradeOrder.getType() == ProductObj.TYPE_BUY_UP) {
            text_buytype_count.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
            text_buytype_count.setText(context.getResources().getString(R.string.lable_tr_type_ordernumber,
                    context.getResources().getString(R.string.trade_buy_up),
                    tradeOrder.getPosition()));

        } else {
            text_buytype_count.setTextColor(context.getResources().getColor(R.color.c_06A969));
            text_buytype_count.setText(context.getResources().getString(R.string.lable_tr_type_ordernumber,
                    context.getResources().getString(R.string.trade_buy_down),
                    tradeOrder.getPosition()));
        }

        text_trade_order_ccjj.setText(tradeOrder.getHoldAvgPrice());
        text_trade_order_ccyk.setText(tradeOrder.getTodayProfit());
        text_trade_order_ccyk.setTextColor(context.getResources().getColor(R.color.c_06A969));
        if (Double.parseDouble(tradeOrder.getTodayProfit()) > 0) {
            text_trade_order_ccyk.setText("+" + tradeOrder.getTodayProfit());
            text_trade_order_ccyk.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
        }

        if(selectPositon==position){
            imgCheck.setSelected(true);
            line_tradeclose_menu.setVisibility(View.VISIBLE);
        }else{
            line_tradeclose_menu.setVisibility(View.GONE);
            imgCheck.setSelected(false);
        }

        line_tradeclose_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isExpandMenu){
                    isExpandMenu = true;
                    optionView = line_tradeclose_menu;
                    optionTradeOrder = tradeOrder;
                    optionImageView = imgCheck;
                    line_tradeclose_menu.setVisibility(View.VISIBLE);// 显示操作菜单
                    imgCheck.setSelected(true);
                    selectPositon = position;
                }else{
                    if(selectPositon == position){
                        isExpandMenu = false;
                        optionView = null ;
                        optionTradeOrder = null;
                        line_tradeclose_menu.setVisibility(View.GONE);// 隐藏操作菜单
                        imgCheck.setSelected(false);
                        selectPositon = -1;
                    }else{
                        if(optionView!=null){
                            optionView.setVisibility(View.GONE);// 隐藏操作菜单
                            optionImageView.setSelected(false);
//                            optionView.findViewById(R.id.imgCheck).setSelected(false);
                        }
                        isExpandMenu = true;
                        optionView = line_tradeclose_menu;
                        optionTradeOrder = tradeOrder;
                        line_tradeclose_menu.setVisibility(View.VISIBLE);// 显示操作菜单
                        optionImageView = imgCheck;
                        imgCheck.setSelected(true);
                        selectPositon = position;
                    }
                }
                EventBus.getDefault().post(new TradeCloseOptionEvent(TradeCloseOptionEvent.OPTION_EXPANDMENU,isExpandMenu,optionView,optionTradeOrder));
            }
        });

        return convertView;
    }
}
