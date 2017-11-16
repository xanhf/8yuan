package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.entity.trade.Banks;
import com.trade.eight.moudle.trade.BankEvent;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.StringUtil;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by developer on 16/6/6.
 * bank list adapter
 */
public class UserBankListAdapter extends ArrayAdapter<Banks> {
    List<Banks> objects;
    BaseActivity context;
    String bankName;
    private boolean isShowUnBind = false;
    private boolean isShowImgChecked = false;
    Banks defaultBank;

    public Banks getDefaultBank() {
        return defaultBank;
    }

    public void setDefaultBank(Banks defaultBank) {
        this.defaultBank = defaultBank;
    }

    public boolean isShowUnBind() {
        return isShowUnBind;
    }

    public void setShowUnBind(boolean showUnBind) {
        isShowUnBind = showUnBind;
    }

    public boolean isShowImgChecked() {
        return isShowImgChecked;
    }

    public void setShowImgChecked(boolean showImgChecked) {
        isShowImgChecked = showImgChecked;
    }

    public UserBankListAdapter(BaseActivity context, int resource, List<Banks> objects) {
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
            convertView = View.inflate(context, R.layout.item_userbank_list, null);
        }
        LinearLayout line_bank = MyViewHolder.get(convertView, R.id.line_bank);
        ImageView img_bankicon = MyViewHolder.get(convertView, R.id.img_bankicon);
        TextView text_bankname = MyViewHolder.get(convertView, R.id.text_bankname);
        TextView text_bankcard_num = MyViewHolder.get(convertView, R.id.text_bankcard_num);

        final Banks item = objects.get(position);

        ImageLoader.getInstance().displayImage(item.getBankIcon(), img_bankicon, AppImageLoaderConfig.getDisplayImageOptions(context));
        text_bankname.setText(item.getBankName());
        text_bankcard_num.setText(StringUtil.getHintCardNo(item.getBankAccount()));

        line_bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new BankEvent(item));
            }
        });

        return convertView;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}

