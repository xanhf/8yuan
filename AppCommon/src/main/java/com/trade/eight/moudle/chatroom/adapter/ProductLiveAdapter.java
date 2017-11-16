package com.trade.eight.moudle.chatroom.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.Optional;
import com.trade.eight.tools.product.ProductViewHole4ChatRoom;

import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/4/10.
 * 直播室产品列表
 */

public class ProductLiveAdapter extends RecyclerView.Adapter<ProductLiveAdapter.MyViewHolder> {
    List<Optional> optionalList;
    ProductViewHole4ChatRoom productViewHole4ChatRoom;

    public ProductLiveAdapter(ProductViewHole4ChatRoom productViewHole4ChatRoom, List<Optional> optionalList) {
        this.productViewHole4ChatRoom = productViewHole4ChatRoom;
        this.optionalList = optionalList;
    }

    public void setOptionalList(List<Optional> optionalList){
        this.optionalList = optionalList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View rootView = View.inflate(viewGroup.getContext(), R.layout.item_liveproduct, null);
        return new MyViewHolder(rootView);
    }

    public void clear() {
        if (null != optionalList) {
            optionalList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        Optional optional = optionalList.get(i);
        myViewHolder.text_liveproduct_name.setText(optional.getTitle());
        myViewHolder.text_liveproduct_price.setText(optional.getSellone());

        String code = optional.getCode();
        ProductViewHole4ChatRoom.ProductView productView = productViewHole4ChatRoom.listProductView.get(code);
        if (productView == null)
            return;
        productView.tv_title = myViewHolder.text_liveproduct_name;
        productView.tv_price =  myViewHolder.text_liveproduct_price;
        productView.tv_rate = myViewHolder.text_liveproduct_rate;
        productView.optional = optional;
        productViewHole4ChatRoom.updateProductViewListDisplay(optional);
    }

    @Override
    public int getItemCount() {
        return optionalList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView text_liveproduct_name;
        TextView text_liveproduct_rate;
        TextView text_liveproduct_price;

        public MyViewHolder(View itemView) {
            super(itemView);
            text_liveproduct_name = (TextView) itemView.findViewById(R.id.text_liveproduct_name);
            text_liveproduct_rate = (TextView) itemView.findViewById(R.id.text_liveproduct_rate);
            text_liveproduct_price = (TextView) itemView.findViewById(R.id.text_liveproduct_price);
        }

    }
}
