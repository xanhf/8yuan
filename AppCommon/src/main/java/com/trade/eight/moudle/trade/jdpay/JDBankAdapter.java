package com.trade.eight.moudle.trade.jdpay;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.tools.MyViewHolder;

import java.util.List;

/**
 * Created by developer on 16/6/6.
 * bank list adapter
 */
public class JDBankAdapter extends ArrayAdapter<String> {
    List<String> objects;
    Context context;
    String bankName;


    public JDBankAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public void setItems(List<String> list, boolean clear) {
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
            convertView = View.inflate(context, R.layout.item_jd_bank_list, null);
        }
        TextView tv_type = MyViewHolder.get(convertView, R.id.tv_type);
        TextView tv_title = MyViewHolder.get(convertView, R.id.tv_title);
        ImageView icon = MyViewHolder.get(convertView, R.id.icon);
        ImageView iconCheck = MyViewHolder.get(convertView, R.id.iconCheck);

        String item = objects.get(position);
        tv_title.setText(item);
        if (tv_type != null)
            tv_type.setVisibility(View.GONE);

//        ImageLoader.getInstance().displayImage(item.getBankIcon(), icon, AppImageLoaderConfig.getCommonDisplayImageOptions(context, R.drawable.loading_large_icon));
//        if (!StringUtil.isEmpty(item.getBankIcon())) {
//            icon.setVisibility(View.VISIBLE);
//            ImageLoader.getInstance().displayImage(item.getBankIcon(), icon, AppImageLoaderConfig.getDisplayImageOptions(context));
//        } else {
//            icon.setVisibility(View.GONE);
//        }
        icon.setVisibility(View.GONE);
        iconCheck.setVisibility(View.GONE);
        if (bankName !=null && bankName.equals(item)) {
            iconCheck.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}

