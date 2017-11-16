package com.trade.eight.moudle.trade.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.entity.jdpay.RechargeType;
import com.trade.eight.tools.StringUtil;

import java.util.List;

/**
 * Created by fangzhu on 2017/2/10.
 * 充值方式列表
 **/

public class RechargeTypeAdapter extends ArrayAdapter<RechargeType> {
    List<RechargeType> objects;
    Context context;
    /*银联 微信 支付宝 京东快捷支付*/

    //选中的item
    int selectPos = 0;

    public RechargeTypeAdapter(Context context, int resource, List<RechargeType> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;

        //获取到默认的支付方式
        for (int i = 0; i < objects.size(); i++) {
            RechargeType item = objects.get(i);
            if (item.getIsDefault() == RechargeType.IS_DEFAULT_YES) {
                selectPos = i;
                break;
            }
        }

    }

    /**
     * mark 如果多种布局，使用getViewTypeCount，
     * 然后使用notifyDataSetChanged();会出错
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            RechargeType item = objects.get(position);
            int itemType = item.getPayType();
            //item_recharge_content添加不同的item布局，方便自定义各自的item
            LinearLayout rechargeContent = (LinearLayout)View.inflate(context, R.layout.item_recharge_content, null);
            convertView = rechargeContent;
            View itemView = null;
            if (itemType == RechargeType.TYPE_YINLIAN
                    || itemType == RechargeType.TYPE_YINLIAN_BEST_PAY) {
                //银联
                itemView = View.inflate(context, R.layout.item_recharge_yinlian, null);
            } else if (itemType == RechargeType.TYPE_WX_SCHEME
                    || itemType == RechargeType.TYPE_WX_DIAN_XIN_WAP
                    || itemType == RechargeType.TYPE_WX_DIAN_XIN_APP
                    || itemType == RechargeType.TYPE_WX_IWXAPI) {
                //微信
                itemView = View.inflate(context, R.layout.item_recharge_wx, null);
            } else if (itemType == RechargeType.TYPE_ZHIFUBAO
                    || itemType == RechargeType.TYPE_ZHIFUBAO_SCAN) {
                //支付宝
                itemView = View.inflate(context, R.layout.item_recharge_zfb, null);
            } else if (itemType == RechargeType.TYPE_JD_KJZF) {
                //京东快捷支付
                itemView = View.inflate(context, R.layout.item_recharge_jd, null);
            }
            rechargeContent.addView(itemView);

            View checkView = convertView.findViewById(R.id.checkView);
            TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            TextView tv_subtitle = (TextView) convertView.findViewById(R.id.tv_subtitle);
            if (tv_title != null) {
                if (!StringUtil.isEmpty(item.getPayName()))
                    tv_title.setText(item.getPayName());
            }
            if (tv_subtitle != null) {
                if (!StringUtil.isEmpty(item.getPaySubTitle()))
                    tv_subtitle.setText(item.getPaySubTitle());
            }
            if (selectPos == position) {
                if (checkView != null)
                    checkView.setBackgroundResource(R.drawable.cashin_selected);
            } else {
                if (checkView != null)
                    checkView.setBackgroundResource(R.drawable.cashin_normal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    /**
     * @return 当前选中的item
     */
    public RechargeType getSelectItem() {
        if (selectPos < objects.size())
            return objects.get(selectPos);
        return null;
    }

    public int getSelectPos() {
        return selectPos;
    }

    public void setSelectPos(int selectPos) {
        this.selectPos = selectPos;
    }
}
