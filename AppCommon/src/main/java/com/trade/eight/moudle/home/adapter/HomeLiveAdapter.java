package com.trade.eight.moudle.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.entity.live.LiveRoomNew;
import com.trade.eight.moudle.chatroom.IndexLiveItemClickEvent;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.view.CircleImageView;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/22.
 */

public class HomeLiveAdapter extends RecyclerView.Adapter<HomeLiveAdapter.MyViewHolder> {
    List<LiveRoomNew> goodsList;
    Context context;
    public HomeLiveAdapter(Context context,List<LiveRoomNew> goodsList) {
        this.goodsList = goodsList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View rootView = View.inflate(viewGroup.getContext(), R.layout.item_home_liveroom, null);
        return new MyViewHolder(rootView);
    }


    public void clear() {
        if (null != goodsList) {
            goodsList.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {

        final LiveRoomNew liveRoomNew = goodsList.get(i);
        if (liveRoomNew.getChannelStatus() != LiveRoomNew.CT_STATUS_PALY) {//休息中
            myViewHolder.rel_homelive_bg.setBackgroundResource(R.drawable.bg_btn_c1c4cc);
            myViewHolder.text_homelive_name.setTextColor(context.getResources().getColor(R.color.c_999999_70));
            myViewHolder.text_homelive_onlinenum.setTextColor(context.getResources().getColor(R.color.c_999999_70));

            if (liveRoomNew.getSegmentModel() != null) {
                myViewHolder.text_homelive_onlinenum.setText(context.getResources().getString(R.string.lable_live_starttime,ConvertUtil.NVL(liveRoomNew.getSegmentModel().getStartTime(), "")));
            }
        } else {
            myViewHolder.rel_homelive_bg.setBackgroundResource(R.drawable.bg_btn_bb8b7d);
            myViewHolder.text_homelive_name.setTextColor(context.getResources().getColor(R.color.c_464646));
            myViewHolder.text_homelive_onlinenum.setTextColor(context.getResources().getColor(R.color.c_464646));

            if (liveRoomNew.getSegmentModel() != null) {
                myViewHolder.text_homelive_onlinenum.setText(context.getResources().getString(R.string.lable_live_onlinenum,ConvertUtil.NVL(liveRoomNew.getOnlineNumberSimpleName(), "")));
            }
        }
        if (liveRoomNew.getSegmentModel() != null) {
            ImageLoader.getInstance().displayImage(liveRoomNew.getSegmentModel().getAuthorAvatar(), myViewHolder.img_homelive_avater, AppImageLoaderConfig.getCommonDisplayImageOptions(context, R.drawable.img_avater_loading));
            myViewHolder.text_homelive_name.setText(ConvertUtil.NVL(liveRoomNew.getSegmentModel().getAuthorName(), ""));
        }

        myViewHolder.line_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new IndexLiveItemClickEvent(liveRoomNew));
            }
        });
    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        View line_root;
        View rel_homelive_bg;
        CircleImageView img_homelive_avater;
        TextView text_homelive_name;
        TextView text_homelive_onlinenum;


        public MyViewHolder(View itemView) {
            super(itemView);
            line_root= itemView.findViewById(R.id.line_root);
            rel_homelive_bg = itemView.findViewById(R.id.rel_homelive_bg);
            img_homelive_avater = (CircleImageView) itemView.findViewById(R.id.img_homelive_avater);
            text_homelive_name = (TextView) itemView.findViewById(R.id.text_homelive_name);
            text_homelive_onlinenum = (TextView) itemView.findViewById(R.id.text_homelive_onlinenum);

        }

    }
}