package com.trade.eight.moudle.product.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.ProFormatConfig;
import com.trade.eight.entity.product.ProductNotice;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.product.ProductNoticeEvent;
import com.trade.eight.moudle.product.ProductNoticeUtil;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.view.swpilistview.SwipeItemLayout;
import com.trade.eight.view.swpilistview.SwipeListView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 16/12/29.
 */

public class ProductNoticeAdapter extends ArrayAdapter<ProductNotice> {
    List<ProductNotice> objects;
    BaseActivity mContext;
    private boolean isShowEdit = false;
    SwipeListView swipeListView;
    ProductNoticeUtil productNoticeUtil;
    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");


    public ProductNoticeAdapter(BaseActivity context, int resource, List<ProductNotice> objects, SwipeListView swipeListView) {
        super(context, resource, objects);
        this.mContext = context;
        this.objects = objects;
        this.swipeListView = swipeListView;
    }

    public ProductNoticeAdapter(BaseActivity context, int resource, List<ProductNotice> objects, SwipeListView swipeListView, ProductNoticeUtil productNoticeUtil) {
        super(context, resource, objects);
        this.mContext = context;
        this.objects = objects;
        this.swipeListView = swipeListView;
        this.productNoticeUtil = productNoticeUtil;
    }

    public boolean isShowEdit() {
        return isShowEdit;
    }

    public void setShowEdit(boolean showEdit) {
        isShowEdit = showEdit;
        notifyDataSetChanged();
    }

    public void setObjects(List<ProductNotice> objects) {
        this.objects = objects;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HolderView holderView = null;
        if (convertView == null) {
            View view01 = LayoutInflater.from(mContext).inflate(R.layout.layout_productnotice_item, null);
            View view02 = LayoutInflater.from(mContext).inflate(R.layout.layout_productnotice_delete, null);
            convertView = new SwipeItemLayout(view01, view02, null, null);
            holderView = new HolderView();
            holderView.btn_pn_dele = (Button) view01.findViewById(R.id.btn_pn_dele);
            holderView.line_pn_content = (LinearLayout) view01.findViewById(R.id.line_pn_content);
            holderView.text_noticetype = (TextView) view01.findViewById(R.id.text_noticetype);
            holderView.text_pn_custompoint = (TextView) view01.findViewById(R.id.text_pn_custompoint);
            holderView.text_pn_customrange = (TextView) view01.findViewById(R.id.text_pn_customrange);
            holderView.text_pn_customcircle = (TextView) view01.findViewById(R.id.text_pn_customcircle);
            holderView.img_pn_edit = (LinearLayout) view01.findViewById(R.id.img_pn_edit);
            holderView.btn_delete_pn = (Button) view02.findViewById(R.id.btn_delete_pn);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }
        final ProductNotice pn = objects.get(position);
        final SwipeItemLayout optionView = (SwipeItemLayout) convertView;
        final View editView = holderView.img_pn_edit;

        if (pn.getBuyType() == TradeOrder.BUY_DOWN) {
            holderView.text_noticetype.setText("卖价");
            holderView.text_noticetype.setTextColor(mContext.getResources().getColor(R.color.trade_down));
        } else {
            holderView.text_noticetype.setText("买价");
            holderView.text_noticetype.setTextColor(mContext.getResources().getColor(R.color.trade_up));
        }
        if (isShowEdit) {
            holderView.btn_pn_dele.setVisibility(View.VISIBLE);
            holderView.img_pn_edit.setVisibility(View.VISIBLE);
        } else {
            holderView.btn_pn_dele.setVisibility(View.GONE);
            holderView.img_pn_edit.setVisibility(View.GONE);
            swipeListView.setOldSelected(-1);
        }
        holderView.text_pn_custompoint.setText(ProFormatConfig.formatByCodes(pn.getProductExcode() + "|" + pn.getProductCode(), pn.getCustomizedProfit()));
        holderView.text_pn_customrange.setText(NumberUtil.moveLast0(new BigDecimal(pn.getFloatUpProfit()).toString()));
        if (pn.getCycleType() == 1) {
//            holderView.text_pn_customcircle.setText(R.string.product_notice_day);
            holderView.text_pn_customcircle.setText(mContext.getResources().getString(R.string.product_notice_time, formatter.format(new Date(pn.getExpirationTime()))));
        } else {
            holderView.text_pn_customcircle.setText(mContext.getResources().getString(R.string.product_notice_time, formatter.format(new Date(pn.getExpirationTime()))));
//            holderView.text_pn_customcircle.setText(R.string.product_notice_week);
        }
        holderView.btn_pn_dele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swipeListView.getOldSelected() != -1) {
                    if (swipeListView.getOldSelected() >= swipeListView.getFirstVisiblePosition()
                            && swipeListView.getOldSelected() <= swipeListView.getLastVisiblePosition()) {
                        View view = swipeListView.getChildAt(swipeListView.getOldSelected() - swipeListView.getFirstVisiblePosition());
                        if (view instanceof SwipeItemLayout) {
                            ((SwipeItemLayout) view).smoothCloseMenu();
                        }
                    }
                }
                optionView.smoothOpenMenu();
                swipeListView.setOldSelected(position);
            }
        });
        holderView.btn_delete_pn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productNoticeUtil != null) {
                    productNoticeUtil.deleteProductNotice(pn);
                }
                EventBus.getDefault().post(new ProductNoticeEvent(ProductNoticeEvent.EVENTTYPE_DELETE, pn));
            }
        });
        holderView.img_pn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productNoticeUtil != null) {

                    productNoticeUtil.editProductNotice(pn);
                }
                EventBus.getDefault().post(new ProductNoticeEvent(ProductNoticeEvent.EVENTTYPE_EDIT, pn));
            }
        });
        holderView.line_pn_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (optionView.isOpen()) {
                    optionView.smoothCloseMenu();
                    return;
                }
                if (editView.getVisibility() == View.VISIBLE) {
                    if (productNoticeUtil != null) {
                        productNoticeUtil.editProductNotice(pn);
                    }
                    EventBus.getDefault().post(new ProductNoticeEvent(ProductNoticeEvent.EVENTTYPE_EDIT, pn));
                }
            }
        });
        return convertView;

    }

    class HolderView {
        Button btn_pn_dele;
        LinearLayout line_pn_content;
        TextView text_noticetype;

        TextView text_pn_custompoint;
        TextView text_pn_customrange;
        TextView text_pn_customcircle;
        LinearLayout img_pn_edit;
        Button btn_delete_pn;
    }

}
