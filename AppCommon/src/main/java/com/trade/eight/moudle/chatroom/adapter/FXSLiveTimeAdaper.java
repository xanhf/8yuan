package com.trade.eight.moudle.chatroom.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.live.LiveTeacherTimeData;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.view.MyEffectView;

import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/31.
 */

public class FXSLiveTimeAdaper extends ArrayAdapter<LiveTeacherTimeData> {
    Context context;
    List<LiveTeacherTimeData> objects;
    public FXSLiveTimeAdaper(Context context, int resource, List<LiveTeacherTimeData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_fxs_livetime, null);
        }
        View view_topline = MyViewHolder.get(convertView, R.id.view_topline);
        TextView icon = MyViewHolder.get(convertView, R.id.icon);
        View effect_bottom = MyViewHolder.get(convertView, R.id.effect_bottom);
        TextView text_time_name = MyViewHolder.get(convertView, R.id.text_time_name);
        TextView text_fxslable = MyViewHolder.get(convertView, R.id.text_fxslable);
        TextView tv_content = MyViewHolder.get(convertView, R.id.tv_content);

        LiveTeacherTimeData liveTeacherTimeData = objects.get(position);
        if (position == 0 ) {
            view_topline.setVisibility(View.INVISIBLE);
        } else {
            view_topline.setVisibility(View.VISIBLE);
        }
        switch (liveTeacherTimeData.getStatus()){
            case LiveTeacherTimeData.STATUS_NOLIVE:
                icon.setBackgroundColor(context.getResources().getColor(R.color.c_464646));
                text_time_name.setTextColor(context.getResources().getColor(R.color.c_464646));
                tv_content.setTextColor(context.getResources().getColor(R.color.c_464646));
                view_topline.setBackgroundColor(context.getResources().getColor(R.color.c_464646));
                effect_bottom.setBackgroundColor(context.getResources().getColor(R.color.c_464646));
                break;
            case LiveTeacherTimeData.STATUS_INLIVE:
                icon.setBackgroundColor(context.getResources().getColor(R.color.c_464646));
                text_time_name.setTextColor(context.getResources().getColor(R.color.c_464646));
                tv_content.setTextColor(context.getResources().getColor(R.color.c_464646));
                view_topline.setBackgroundColor(context.getResources().getColor(R.color.c_464646));
                effect_bottom.setBackgroundColor(context.getResources().getColor(R.color.c_464646));
                break;
            case LiveTeacherTimeData.STATUS_COMPLETELIVE:
                icon.setBackgroundColor(context.getResources().getColor(R.color.c_999999));
                text_time_name.setTextColor(context.getResources().getColor(R.color.c_999999));
                tv_content.setTextColor(context.getResources().getColor(R.color.c_999999));
                view_topline.setBackgroundColor(context.getResources().getColor(R.color.c_999999));
                effect_bottom.setBackgroundColor(context.getResources().getColor(R.color.c_999999));
                break;
        }
        text_time_name.setText(liveTeacherTimeData.getLiveDescription());
        tv_content.setText(liveTeacherTimeData.getLiveTime());
        return convertView;
    }
}
