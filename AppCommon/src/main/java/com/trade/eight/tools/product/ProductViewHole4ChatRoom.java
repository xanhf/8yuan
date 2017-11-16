package com.trade.eight.tools.product;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.Optional;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.Utils;

import java.util.HashMap;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/28.
 */

public class ProductViewHole4ChatRoom {
    public class ProductView {
        public View line_pro;
        public TextView tv_title;
        public TextView tv_price;
        public TextView tv_rate;
        public Optional optional;
        public String tag;
    }

    public HashMap<String, ProductView> listProductView;
    BaseActivity context;
    String codes;
    List<Optional> optionalList;

    public ProductViewHole4ChatRoom(BaseActivity context, String codes) {
        this.context = context;
        this.codes = codes;
        listProductView = new HashMap<String, ProductView>();
        initProductViewList();
    }

    public void setOptionalList(List<Optional> optionalList) {
        this.optionalList = optionalList;
    }

    /**
     * 控件初始化
     */
    void initProductViewList() {
        String[] allProducts = codes.split(",");
        for (int i = 0; i < allProducts.length; i++) {
            ProductView productView = new ProductView();
            productView.tag = allProducts[i];
            listProductView.put(allProducts[i], productView);
        }
    }

    /**
     * 刷新界面
     *
     * @param optional
     */
    public void updateProductViewListDisplay(Optional optional) {
        if (optional == null)
            return;
        if (listProductView == null)
            return;
        String code = optional.getCode();
        ProductView productView = listProductView.get(code);
        if (productView == null)
            return;
        productView.optional = optional;
//        productView.line_pro.setVisibility(View.VISIBLE);
        if (productView.tv_title != null) {
            if (!TextUtils.isEmpty(optional.getName())) {
                productView.tv_title.setText(optional.getName());
            }
        }
        if (productView.tv_price != null) {
            productView.tv_price.setText(optional.getLastPrice());
        }
        if (productView.tv_rate != null) {
            double diff = Double.parseDouble(optional.getChange());
//            double diffPercent = optional.getRateChange();

            //格式化小数点
            String diffStr = NumberUtil.moveLast0(NumberUtil.beautifulDouble(diff, 5));

            int color = context.getResources().getColor(R.color.color_opt_lt);
            if (diff > 0) {
                color = context.getResources().getColor(R.color.color_opt_gt);
                diffStr = "+" + diffStr;
            }

            if (diff == 0)
                color = context.getResources().getColor(R.color.color_opt_eq);
            if (productView.tv_rate != null) {
                productView.tv_rate.setText(diffStr);
                productView.tv_rate.setTextColor(color);
            }
            if (productView.tv_price != null) {
                productView.tv_price.setTextColor(color);
            }
        }
    }
}
