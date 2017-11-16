package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.entity.trade.Banks;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.StringUtil;

import java.util.List;

/**
 * Created by developer on 16/6/6.
 * bank list adapter
 */
public class BankListAdapter extends ArrayAdapter<Banks> {
    List<Banks> objects;
    Context context;
    String bankName;


    public BankListAdapter(Context context, int resource, List<Banks> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public void setItems(List<Banks> list, boolean clear) {
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
            convertView = View.inflate(context, R.layout.item_bank_list, null);
        }
        TextView tv_type = MyViewHolder.get(convertView, R.id.tv_type);
        TextView tv_title = MyViewHolder.get(convertView, R.id.tv_title);
        ImageView icon = MyViewHolder.get(convertView, R.id.icon);
        ImageView iconCheck = MyViewHolder.get(convertView, R.id.iconCheck);

        Banks item = objects.get(position);
        tv_title.setText(item.getName());
        tv_type.setVisibility(View.GONE);
        if (position == 0) {
            tv_type.setText("热门银行");
            tv_type.setVisibility(View.VISIBLE);
        } else {
            if (item.getTop() != objects.get(position - 1).getTop()) {
                tv_type.setText("其他银行");
                tv_type.setVisibility(View.VISIBLE);
            }
        }
        tv_type.setVisibility(View.GONE);
//        ImageLoader.getInstance().displayImage(item.getBankIcon(), icon, AppImageLoaderConfig.getCommonDisplayImageOptions(context, R.drawable.loading_large_icon));
        if (!StringUtil.isEmpty(item.getIcon())) {
            icon.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(item.getIcon(), icon, AppImageLoaderConfig.getDisplayImageOptions(context));
        } else {
            icon.setVisibility(View.GONE);
        }
        iconCheck.setVisibility(View.GONE);
        if (bankName !=null && bankName.equals(item.getBankName())) {
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

