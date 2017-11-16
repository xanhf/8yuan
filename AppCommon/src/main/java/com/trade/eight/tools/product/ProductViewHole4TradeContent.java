package com.trade.eight.tools.product;

import android.widget.TextView;

import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.Optional;

import java.util.HashMap;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/28.
 * 交易大厅局部刷新所用
 */

public class ProductViewHole4TradeContent {
    public class ProductView {
        public TextView tv_buyup_rate;
        public TextView tv_buydown_rate;
        public String tag;
    }

    public HashMap<String, ProductView> listProductView;
    BaseActivity context;
    String codes;
    List<Optional> optionalList;

    public ProductViewHole4TradeContent(BaseActivity context, String codes) {
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
        if (productView.tv_buyup_rate != null) {
            productView.tv_buyup_rate.setText(optional.getAskPrice1());
        }
        if (productView.tv_buydown_rate != null) {
            productView.tv_buydown_rate.setText(optional.getBidPrice1());
        }

    }
}
