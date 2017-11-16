package com.trade.eight.moudle.me.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.messagecenter.MessageData;
import com.trade.eight.tools.MyViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/10.
 */

public class MessageCenterAdapter extends ArrayAdapter<MessageData> {
    List<MessageData> objects;
    Context context;
    SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public MessageCenterAdapter(Context context, int resource, List<MessageData> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
    }

    public void setItems(List<MessageData> list) {
        if (list == null || list.size() == 0)
            return;
        objects.clear();
        if (list != null) {
            objects.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_messagecenter, null);
        }
        TextView text_mc_type = MyViewHolder.get(convertView, R.id.text_mc_type);
        TextView text_mc_title = MyViewHolder.get(convertView, R.id.text_mc_title);
        View text_mc_new = MyViewHolder.get(convertView, R.id.text_mc_new);
        TextView text_mc_time = MyViewHolder.get(convertView, R.id.text_mc_time);
        TextView text_mc_abstract = MyViewHolder.get(convertView, R.id.text_mc_abstract);


        MessageData messageData = objects.get(position);
        text_mc_title.setText(messageData.getMessageTitle());
        text_mc_abstract.setText(messageData.getMessageContent());
        text_mc_time.setText(simpleFormatter.format(new Date(messageData.getCreateTime())));
        if (messageData.getMessageType() == MessageData.TYPE_XITONG || messageData.getMessageType() == MessageData.TYPE_JYFX) {
            //系统消息
            text_mc_type.setBackgroundResource(R.drawable.bg_btn_colorf54a40);
            text_mc_type.setText(context.getResources().getString(R.string.mc_guan));
        } else if (messageData.getMessageType() == MessageData.TYPE_QUANDAOQI || messageData.getMessageType() == MessageData.TYPE_QUANDAOZHANG || messageData.getMessageType() == MessageData.TYPE_QUANJJDAOQI) {
            //券
            text_mc_type.setBackgroundResource(R.drawable.bg_btn_colorfea484);
            text_mc_type.setText(context.getResources().getString(R.string.mc_quan));
        } else if (messageData.getMessageType() == MessageData.TYPE_BAOCANG || messageData.getMessageType() == MessageData.TYPE_ZYZS) {
            //持仓
            text_mc_type.setBackgroundResource(R.drawable.bg_btn_color3a69e3);
            text_mc_type.setText(context.getResources().getString(R.string.mc_cang));

        }
        if (messageData.getStatus() == MessageData.MSG_UNREAD) {
            text_mc_new.setVisibility(View.GONE);
        } else {
            text_mc_new.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
