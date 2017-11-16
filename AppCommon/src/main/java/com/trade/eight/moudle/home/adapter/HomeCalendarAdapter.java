package com.trade.eight.moudle.home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.home.HomeCalendar;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.StringUtil;

import java.util.List;

/**
 * Created by Administrator on 2015/8/16.
 */
public class HomeCalendarAdapter extends ArrayAdapter<HomeCalendar> {
    List<HomeCalendar> objects;
    Context context;
    public HomeCalendarAdapter(Context context, int resource, List<HomeCalendar> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.home_calendar_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.news_time = (TextView) convertView.findViewById(R.id.news_time);
            viewHolder.news_country = (TextView) convertView.findViewById(R.id.news_country);
            viewHolder.news_title = (TextView) convertView.findViewById(R.id.news_title);
            viewHolder.news_value_for = (TextView) convertView.findViewById(R.id.news_value_for);
            viewHolder.news_value_expected = (TextView) convertView.findViewById(R.id.news_value_expected);
            viewHolder.news_value_now = (TextView) convertView.findViewById(R.id.news_value_now);
            viewHolder.valueView = convertView.findViewById(R.id.valueView);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        HomeCalendar forecastData = objects.get(position);
        setViewHolder(viewHolder, forecastData, position);
        return convertView;
    }

    void setViewHolder (ViewHolder viewHolder, HomeCalendar forecastData, int position) {
        if (viewHolder.news_time != null) {
            viewHolder.news_time.setText(DateUtil.formatDate(forecastData.getZtTime(), "HH:mm:ss", "HH:mm"));
        }
        if (viewHolder.news_country != null) {
            viewHolder.news_country.setVisibility(View.VISIBLE);
            if (StringUtil.isEmpty(forecastData.getCountry()))
                viewHolder.news_country.setVisibility(View.GONE);
            viewHolder.news_country.setText(forecastData.getCountry());
        }
        if (viewHolder.news_title != null) {
            viewHolder.news_title.setText(forecastData.getContent());
        }
        if (viewHolder.news_value_for != null) {
            viewHolder.news_value_for.setText(forecastData.getOldvalue());
        }
        if (viewHolder.news_value_expected != null) {
            viewHolder.news_value_expected.setText(forecastData.getPredict());
        }
        if (viewHolder.news_value_now != null) {
            viewHolder.news_value_now.setText(forecastData.getPublished());
        }

        viewHolder.valueView.setVisibility(View.VISIBLE);
        if (isNULL(forecastData)) {
            viewHolder.valueView.setVisibility(View.GONE);
        }
    }

    public boolean isTextNULL (String string) {
        if (StringUtil.isEmpty(string))
            return true;
        if("--".equals(string))
            return true;
        return false;
    }

    public boolean isNULL(HomeCalendar data) {
        if (isTextNULL(data.getContent())
                && isTextNULL(data.getOldvalue())
                && isTextNULL(data.getPredict())
                && isTextNULL(data.getPublished())) {
            return true;
        }
        return false;
    }

    class ViewHolder {
        TextView news_time, news_country, news_title,
                news_value_for, news_value_expected, news_value_now;
        View valueView;
    }
}
