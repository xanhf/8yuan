package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.trade.City;
import com.trade.eight.entity.trade.Province;
import com.trade.eight.tools.MyViewHolder;

import java.util.List;

/**
 * Created by developer on 16/6/6.
 * 城市 和  city  的adapter
 */
public class CityAdapter extends ArrayAdapter<City> {
    List<City> objects;
    Context context;

    boolean isCity = false;

    public CityAdapter(Context context, int resource, List<City> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public void setItems(List<City> list, boolean clear) {
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
        City item = objects.get(position);

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

