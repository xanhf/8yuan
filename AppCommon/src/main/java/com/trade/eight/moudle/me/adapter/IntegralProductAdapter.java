package com.trade.eight.moudle.me.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.entity.integral.IntegralProductData;
import com.trade.eight.moudle.me.CreateIntegralOrderEvent;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.StringUtil;

import java.text.SimpleDateFormat;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 *积分商城适配器
 */
public class IntegralProductAdapter extends ArrayAdapter<IntegralProductData> {
    List<IntegralProductData> objects;
    Context context;
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    public IntegralProductAdapter(Context context, int resource, List<IntegralProductData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.layout_integralmarket, null);
        }
        TextView text_integral_exchangename  = MyViewHolder.get(convertView, R.id.text_integral_exchangename);
        ImageView img_integralpic = MyViewHolder.get(convertView, R.id.img_integralpic);
        TextView text_integral_productname  = MyViewHolder.get(convertView, R.id.text_integral_productname);
        TextView text_integral_need  = MyViewHolder.get(convertView, R.id.text_integral_need);
        TextView text_integral_takenum  = MyViewHolder.get(convertView, R.id.text_integral_takenum);
        Button btn_integralexchange = MyViewHolder.get(convertView, R.id.btn_integralexchange);
        View view_top = MyViewHolder.get(convertView, R.id.view_top);
        View view_bottom = MyViewHolder.get(convertView, R.id.view_bottom);
        final IntegralProductData integralProductData = objects.get(position);
        controlTitle(position,text_integral_exchangename,integralProductData.getExcode(),view_top,view_bottom);
        ImageLoader.getInstance().displayImage(integralProductData.getGiftPic(), img_integralpic, AppImageLoaderConfig.getCommonDisplayImageOptions(context,R.drawable.img_integralmarket_default));
        text_integral_productname.setText(integralProductData.getGiftName());
//        text_integral_need.setText(context.getResources().getString(R.string.intergral_point,integralProductData.getPoins()));
        text_integral_need.setText(context.getResources().getString(R.string.intergral_point, StringUtil.forNumber(integralProductData.getPoins())));

        text_integral_takenum.setText(context.getResources().getString(R.string.intergral_takenum,integralProductData.getTakeNum()));

        btn_integralexchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new CreateIntegralOrderEvent(integralProductData));
            }
        });
        return convertView;
    }

    private void controlTitle(int position,TextView text_integral_exchangename,int excode, View view_top,View view_bottom){
        if(position==0){
            text_integral_exchangename.setVisibility(View.VISIBLE);
            view_top.setVisibility(View.VISIBLE);
            view_bottom.setVisibility(View.VISIBLE);
            switch (excode){
                case 1:
                    text_integral_exchangename.setText("广贵所");
                    break;
                case 2:
                    text_integral_exchangename.setText("哈贵所");
                    break;
                case 3:
                    text_integral_exchangename.setText("农交所");
                    break;
                case 4:
                    text_integral_exchangename.setText("华凝所");
                    break;
            }
            return;
        }
        IntegralProductData integralProductData = getItem(position-1);
        if(integralProductData.getExcode()==excode){
            text_integral_exchangename.setVisibility(View.GONE);
            view_top.setVisibility(View.GONE);
            view_bottom.setVisibility(View.VISIBLE);
        }else{
            text_integral_exchangename.setVisibility(View.VISIBLE);
            view_top.setVisibility(View.VISIBLE);
            view_bottom.setVisibility(View.VISIBLE);
            switch (excode) {
                case 1:
                    text_integral_exchangename.setText("广贵所");
                    break;
                case 2:
                    text_integral_exchangename.setText("哈贵所");
                    break;
                case 3:
                    text_integral_exchangename.setText("农交所");
                    break;
                case 4:
                    text_integral_exchangename.setText("华凝所");
                    break;
            }
        }
    }

}
