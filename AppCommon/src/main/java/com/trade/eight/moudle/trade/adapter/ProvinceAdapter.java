package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.trade.Province;
import com.trade.eight.tools.MyViewHolder;

import java.util.List;

/**
 * Created by developer on 16/6/6.
 * 城市 和  city  的adapter
 */
public class ProvinceAdapter extends ArrayAdapter<Province> {
    List<Province> objects;
    Context context;

    boolean isCity = false;

    public ProvinceAdapter(Context context, int resource, List<Province> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public void setItems(List<Province> list, boolean clear) {
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
            convertView = View.inflate(context, R.layout.item_provnice, null);
        }
        TextView tv_title = MyViewHolder.get(convertView, R.id.tv_title);
        Province item = objects.get(position);

        tv_title.setText(item.getName());
        return convertView;
    }

    public boolean isCity() {
        return isCity;
    }

    public void setCity(boolean city) {
        isCity = city;
    }
}

