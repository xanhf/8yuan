package com.trade.eight.moudle.trade.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.Optional;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.trade.TradeCreateLiveChooseProEevent;
import com.trade.eight.tools.product.ProductViewHole4ChatRoom;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 2017/4/10.
 * 直播室产品列表
 */

public class LiveCreateProductAdapter extends RecyclerView.Adapter<LiveCreateProductAdapter.MyViewHolder> {
    List<ProductObj> optionalList;
    private View selectView = null;
    private int selectPosition = 0;

    public LiveCreateProductAdapter(List<ProductObj> optionalList) {
        this.optionalList = optionalList;
    }

    public void setOptionalList(List<ProductObj> optionalList) {
        this.optionalList = optionalList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View rootView = View.inflate(viewGroup.getContext(), R.layout.item_liveproduct_trade, null);
        return new MyViewHolder(rootView);
    }

    public void clear() {
        if (null != optionalList) {
            optionalList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        final ProductObj optional = optionalList.get(position);
        String instrumentName = optional.getInstrumentName();
        String[] nameArray = instrumentName.split("(\\d+)");

        String instrumentId_name = nameArray[0];
        String instrumentId_num = instrumentName.substring(instrumentId_name.length(),instrumentName.length());

        myViewHolder.text_instrumentId_name.setText(instrumentId_name);
        myViewHolder.text_instrumentId_num.setText(instrumentId_num);
        if (ProductObj.IS_CLOSE_YES.equals(optional.getIsClosed())) {
            myViewHolder.text_product_isclose.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.text_product_isclose.setVisibility(View.GONE);
        }
        myViewHolder.line_livetrade_product.setSelected(false);
        if(position==selectPosition){
            zoomBigText(myViewHolder);
            selectPosition = position;
            selectView = myViewHolder.line_livetrade_product;
            selectView.setSelected(true);
            myViewHolder.line_livetrade_product.setSelected(true);
        }else{
            zoomSmallText(myViewHolder);
            myViewHolder.line_livetrade_product.setSelected(false);
        }

        myViewHolder.line_livetrade_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectPosition==position){
                    return;
                }
                selectPosition = -1;
                selectView.setSelected(false);

                selectPosition = position;
                selectView = myViewHolder.line_livetrade_product;
                selectView.setSelected(true);
                zoomBigText(myViewHolder);
                EventBus.getDefault().post(new TradeCreateLiveChooseProEevent(optional,selectPosition));
            }
        });

    }

    private void zoomBigText(MyViewHolder myViewHolder){
        TextPaint tp = myViewHolder.text_instrumentId_name.getPaint();
        tp.setFakeBoldText(true);
        myViewHolder.text_instrumentId_name.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);

        TextPaint tp_1 = myViewHolder.text_instrumentId_num.getPaint();
        tp_1.setFakeBoldText(true);
        myViewHolder.text_instrumentId_num.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
    }

    private void zoomSmallText(MyViewHolder myViewHolder){
        TextPaint tp = myViewHolder.text_instrumentId_name.getPaint();
        tp.setFakeBoldText(false);
        myViewHolder.text_instrumentId_name.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);

        TextPaint tp_1 = myViewHolder.text_instrumentId_num.getPaint();
        tp_1.setFakeBoldText(true);
        myViewHolder.text_instrumentId_num.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
    }

    @Override
    public int getItemCount() {
        return optionalList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        View line_livetrade_product;
        TextView text_instrumentId_name;
        TextView text_instrumentId_num;
        TextView text_product_isclose;

        public MyViewHolder(View itemView) {
            super(itemView);
            line_livetrade_product = itemView.findViewById(R.id.line_livetrade_product);
            text_instrumentId_name = (TextView) itemView.findViewById(R.id.text_instrumentId_name);
            text_instrumentId_num = (TextView) itemView.findViewById(R.id.text_instrumentId_num);
            text_product_isclose = (TextView) itemView.findViewById(R.id.text_product_isclose);
        }

    }
}
