package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.MyViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fangzhu on 16/8/29.
 *
 * 快速平仓的adapter
 *
 */
public class QuickCloseAdapter extends BaseExpandableListAdapter {
    Context context;
    /**
     * 数据集合
     * 分组 外层list就是group
     */
    List<List<TradeOrder>> data;
    //全选逻辑判断
    Map<String, TradeOrder> checkMap = new HashMap<>();
    public Map<String, TradeOrder> getCheckMap() {
        return checkMap;
    }
    public QuickCloseAdapter(Context context, List<List<TradeOrder>> data) {
        this.context = context;
        this.data = data;
    }

    public List<List<TradeOrder>> getData() {
        return data;
    }

    public void setData(List<List<TradeOrder>> data) {
        this.data = data;
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return data.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return data.get(i);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(context, R.layout.item_quick_close_group, null);
        }
        View tempView = MyViewHolder.get(view, R.id.tempView);
        TextView tv_name = MyViewHolder.get(view, R.id.tv_name);
//        TextView tv_price = MyViewHolder.get(view, R.id.tv_price);
//        TextView tv_Rate = MyViewHolder.get(view, R.id.tv_Rate);
//        TextView tv_RateChange = MyViewHolder.get(view, R.id.tv_RateChange);
        final TextView tv_checkAll = (MyViewHolder.get(view, R.id.tv_checkAll));

        if (groupPosition == 0) {
            tempView.setVisibility(View.GONE);
        } else {
            tempView.setVisibility(View.VISIBLE);
        }
        //每个分组的第一个数据
        TradeOrder orderFirst = (TradeOrder) getChild(groupPosition, 0);
//        double rate = 0;
//        try {
//            rate = Double.parseDouble(ConvertUtil.NVL(orderFirst.getMargin(), "0"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        tv_name.setText(TradeProduct.getNameByCode(context, orderFirst.getProductName(), orderFirst.getCode()));
//        tv_price.setText(orderFirst.getRealTimePrice());
//        tv_Rate.setText(NumberUtil.moveLast0(rate) + "");
//        String rateChange = orderFirst.getMp();
//        tv_RateChange.setText(rateChange);
//        int color = context.getResources().getColor(R.color.color_opt_gt);
//        if (rate > 0) {
//            tv_Rate.setText("+" + NumberUtil.moveLast0(rate) + "");
//            tv_RateChange.setText("+" + rateChange);
//            color = context.getResources().getColor(R.color.color_opt_gt);
//        } else if (rate == 0) {
//            color = context.getResources().getColor(R.color.color_opt_eq);
//        } else {
//            color = context.getResources().getColor(R.color.color_opt_lt);
//        }
//        tv_price.setTextColor(color);
//        tv_Rate.setTextColor(color);
//        tv_RateChange.setTextColor(color);
        if(judgeIsCheckedAll(groupPosition)){
            tv_checkAll.setText("取消全选");
        }else{
            tv_checkAll.setText("全选");
        }

        tv_checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isCheckAll = judgeIsCheckedAll(groupPosition);

                for (int i = 0; i < getChildrenCount(groupPosition); i++) {
                    TradeOrder order = (TradeOrder)getChild(groupPosition, i);
                    if (!isCheckAll) {
                        //点击全选中
                        getCheckMap().put(order.getOrderId() + "", order);
                        tv_checkAll.setText("取消全选");
                    } else {
                        //点击全去掉
                        if (getCheckMap().containsKey(order.getOrderId() + ""))
                            getCheckMap().remove(order.getOrderId() + "");
                        tv_checkAll.setText("全选");
                    }
                }
                //更新adapter  显示选中的icon
                notifyDataSetChanged();
                //额外处理
                if (myExCheckAllClick != null) {
                    myExCheckAllClick.handleMessage(new Message());
                }
            }
        });
        return view;
    }

    public boolean judgeIsCheckedAll(int groupPosition){
        //先筛选出这个组中有几个选中了
        int size = 0;
        for (int i = 0; i < getChildrenCount(groupPosition); i ++) {
            //检测map中是否有这个组的id，
            TradeOrder order = (TradeOrder)getChild(groupPosition, i);
            if (checkMap.containsKey(order.getOrderId() + "")) {
                size++;
            }
        }
        //获取已经的状态 全选还是全部反选
        boolean isCheckAll = false;
//                //如果size刚好和组里的childCount一样  就是手动全选了
        if (size == getChildrenCount(groupPosition))
            isCheckAll = true;
        else
            isCheckAll = false;
        return isCheckAll;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_quick_close, null);
        }
        TextView tv_typeBuy = MyViewHolder.get(convertView, R.id.tv_typeBuy);
        TextView tv_buyCount = MyViewHolder.get(convertView, R.id.tv_buyCount);
        TextView tv_priceCreate = MyViewHolder.get(convertView, R.id.tv_priceCreate);
        TextView tv_makeMoney = MyViewHolder.get(convertView, R.id.tv_makeMoney);
        TextView tv_moneyLable = MyViewHolder.get(convertView, R.id.tv_moneyLable);
        TextView tv_createtime = MyViewHolder.get(convertView, R.id.tv_createtime);
        final View imgCheck = MyViewHolder.get(convertView, R.id.imgCheck);
        final TradeOrder item = (TradeOrder)getChild(groupPosition, childPosition);


        if (checkMap.containsKey(item.getOrderId() +"")){
            imgCheck.setSelected(true);
        } else {
            imgCheck.setSelected(false);
        }

        tv_buyCount.setText(item.getOrderNumber() + "手");
        if (item.getType() == TradeOrder.BUY_DOWN) {
            tv_typeBuy.setTextColor(context.getResources().getColor(R.color.trade_down));
            tv_buyCount.setTextColor(context.getResources().getColor(R.color.trade_down));
            tv_typeBuy.setText(R.string.trade_buy_down);
        }else{
            tv_typeBuy.setTextColor(context.getResources().getColor(R.color.trade_up));
            tv_buyCount.setTextColor(context.getResources().getColor(R.color.trade_up));
            tv_typeBuy.setText(R.string.trade_buy_up);
        }

        tv_priceCreate.setText(ConvertUtil.NVL(item.getCreatePrice(), ""));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf_1 = new SimpleDateFormat("MM-dd HH:mm");
        try {
            tv_createtime.setText(sdf_1.format(sdf.parse(item.getCreateTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tv_makeMoney.setTextColor(context.getResources().getColor(R.color.trade_up));
        tv_moneyLable.setTextColor(context.getResources().getColor(R.color.trade_up));
        if (Double.parseDouble(ConvertUtil.NVL(item.getRealTimeProfitLoss(), "0")) < 0) {
            tv_makeMoney.setText(ConvertUtil.NVL(item.getRealTimeProfitLoss(), "0"));
            tv_makeMoney.setTextColor(context.getResources().getColor(R.color.trade_down));
            tv_moneyLable.setTextColor(context.getResources().getColor(R.color.trade_down));
        } else {
            tv_makeMoney.setText("+" + ConvertUtil.NVL(item.getRealTimeProfitLoss(), "0"));
        }
        return convertView;
    }

    /**
     * 允许child可以点击
     * @param i
     * @param i1
     * @return
     */
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public void clear () {
        data.clear();
    }

    /**
     * 全选额外点击处理
     */
    Handler.Callback myExCheckAllClick;

    public Handler.Callback getMyExCheckAllClick() {
        return myExCheckAllClick;
    }

    public void setMyExCheckAllClick(Handler.Callback myExCheckAllClick) {
        this.myExCheckAllClick = myExCheckAllClick;
    }
}
