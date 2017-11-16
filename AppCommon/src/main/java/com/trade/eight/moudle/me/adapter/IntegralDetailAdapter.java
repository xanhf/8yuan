package com.trade.eight.moudle.me.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.integral.IntegralDetailData;
import com.trade.eight.tools.MyViewHolder;

import java.text.SimpleDateFormat;
import java.util.List;


/**
 * 积分详细
 */
public class IntegralDetailAdapter extends ArrayAdapter<IntegralDetailData> {
    List<IntegralDetailData> objects;
    Context context;
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    public IntegralDetailAdapter(Context context, int resource, List<IntegralDetailData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.layout_integraldetail, null);
        }
        TextView text_integraldetail_type = MyViewHolder.get(convertView, R.id.text_integraldetail_type);
        TextView text_integraldetail_inte = MyViewHolder.get(convertView, R.id.text_integraldetail_inte);
        TextView text_integraldetail_time = MyViewHolder.get(convertView, R.id.text_integraldetail_time);


        final IntegralDetailData exHistoryData = objects.get(position);
        text_integraldetail_type.setText(exHistoryData.getPointSourceName());
        if (exHistoryData.getPointsValue() > 0) {
            text_integraldetail_inte.setTextColor(context.getResources().getColor(R.color.trade_up));
            text_integraldetail_inte.setText("+" + exHistoryData.getPointsValue());
        } else {
            text_integraldetail_inte.setTextColor(context.getResources().getColor(R.color.trade_down));
            text_integraldetail_inte.setText("" + exHistoryData.getPointsValue());
        }
        text_integraldetail_time.setText(exHistoryData.getCreateTimeStr());

        return convertView;
    }

}
