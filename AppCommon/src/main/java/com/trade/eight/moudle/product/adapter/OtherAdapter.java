
package com.trade.eight.moudle.product.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.Optional;

import java.util.List;

public class OtherAdapter extends BaseAdapter {
    private final Context context;
    public List<Optional> channelList;
    private TextView item_text;

    boolean isVisible = true;
    public int remove_position = -1;

    public OtherAdapter(Context context, List<Optional> channelList) {
        this.context = context;
        this.channelList = channelList;
    }

    @Override
    public int getCount() {
        return channelList == null ? 0 : channelList.size();
    }

    @Override
    public Optional getItem(int position) {
        if (channelList != null && channelList.size() != 0) {
            return channelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_manager_item, null);
        item_text = (TextView) view.findViewById(R.id.text_item);
        Optional channel = getItem(position);
        item_text.setText(channel.getName());
        if (!isVisible && (position == -1 + channelList.size())) {
            item_text.setText("");
        }
        if (remove_position == position) {
            item_text.setText("");
        }
        return view;
    }

    public List<Optional> getChannnelLst() {
        return channelList;
    }

    public void addItem(Optional channel) {
        channelList.add(channel);
        notifyDataSetChanged();
    }

    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
        // notifyDataSetChanged();
    }

    public void remove() {
        if (remove_position < channelList.size())
            channelList.remove(remove_position);
        remove_position = -1;
        notifyDataSetChanged();
    }

    public void setListDate(List<Optional> list) {
        channelList = list;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
