package com.trade.eight.moudle.me.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.entity.integral.ExHistoryData;
import com.trade.eight.entity.integral.GoodsData;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.StringUtil;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 兑换历史
 */
public class IntegralExHistoryAdapter extends ArrayAdapter<ExHistoryData> {
    public List<ExHistoryData> objects;
    Context context;
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    public IntegralExHistoryAdapter(Context context, int resource, List<ExHistoryData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        if (objects.get(position).getGiftType() == GoodsData.GIFTTYPE_TE) {
            return GoodsData.GIFTTYPE_TE;
        }else if(objects.get(position).getGiftType() == GoodsData.GIFTTYPE_SHIWU){
            return GoodsData.GIFTTYPE_SHIWU;
        }
        return GoodsData.GIFTTYPE_QUAN;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == GoodsData.GIFTTYPE_QUAN) {

            if (convertView == null) {
                convertView = View.inflate(context, R.layout.layout_integralexhistory, null);
            }
            ImageView img_integralpic = MyViewHolder.get(convertView, R.id.img_integralpic);
            TextView text_integral_productname = MyViewHolder.get(convertView, R.id.text_integral_productname);
            TextView text_integral_need = MyViewHolder.get(convertView, R.id.text_integral_need);
            TextView text_integral_exstate = MyViewHolder.get(convertView, R.id.text_integral_exstate);
            TextView text_integral_extime = MyViewHolder.get(convertView, R.id.text_integral_extime);


            final ExHistoryData exHistoryData = objects.get(position);
            ImageLoader.getInstance().displayImage(exHistoryData.getGiftPic(), img_integralpic, AppImageLoaderConfig.getDisplayImageOptions(context));
            text_integral_productname.setText(exHistoryData.getFullName());
            text_integral_need.setText(context.getResources().getString(R.string.integral_exintegral_needed, StringUtil.forNumber(Math.abs(exHistoryData.getTotalPoins()))));
            text_integral_extime.setText(exHistoryData.getCreateTimeStr());
        }else if (viewType == GoodsData.GIFTTYPE_SHIWU) {

            if (convertView == null) {
                convertView = View.inflate(context, R.layout.layout_integralexhistory_shiwu, null);
            }
            ImageView img_integralpic = MyViewHolder.get(convertView, R.id.img_integralpic);
            TextView text_integral_productname = MyViewHolder.get(convertView, R.id.text_integral_productname);
            TextView text_integral_productnum = MyViewHolder.get(convertView, R.id.text_integral_productnum);
            TextView text_integral_need = MyViewHolder.get(convertView, R.id.text_integral_need);
            TextView text_integral_exstate = MyViewHolder.get(convertView, R.id.text_integral_exstate);
            TextView text_integral_extime = MyViewHolder.get(convertView, R.id.text_integral_extime);


            final ExHistoryData exHistoryData = objects.get(position);
            ImageLoader.getInstance().displayImage(exHistoryData.getGiftPic(), img_integralpic, AppImageLoaderConfig.getDisplayImageOptions(context));
            text_integral_productname.setText(exHistoryData.getFullName());
            text_integral_need.setText(context.getResources().getString(R.string.integral_exintegral_needed, StringUtil.forNumber(Math.abs(exHistoryData.getTotalPoins()))));
            text_integral_extime.setText(exHistoryData.getCreateTimeStr());
            text_integral_productnum.setText(exHistoryData.getSpecDesc());
        }else if (viewType == GoodsData.GIFTTYPE_TE) {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.layout_integralexhistory_te, null);
            }
            ImageView img_integralpic = MyViewHolder.get(convertView, R.id.img_integralpic);
            TextView text_giftname = MyViewHolder.get(convertView, R.id.text_giftname);
            TextView text_giftpoins = MyViewHolder.get(convertView, R.id.text_giftpoins);
            TextView text_gifttaketime = MyViewHolder.get(convertView, R.id.text_gifttaketime);

            final ExHistoryData exHistoryData = objects.get(position);
            ImageLoader.getInstance().displayImage(exHistoryData.getGiftPic(), img_integralpic, AppImageLoaderConfig.getDisplayImageOptions(context));
            text_giftname.setText(exHistoryData.getGiftName());
            text_giftpoins.setText(context.getResources().getString(R.string.integral_exintegral_needed, StringUtil.forNumber(Math.abs(exHistoryData.getTotalPoins()))));
            text_gifttaketime.setText(exHistoryData.getCreateTimeStr());
        }

        return convertView;
    }

}
