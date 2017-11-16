package com.trade.eight.tools.product;

import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.ProFormatConfig;
import com.trade.eight.entity.Optional;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.Utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/24.
 */

public class ProductViewHold4Home {
    public class ProductView {
        public RelativeLayout rl_product = null;
        public TextView tv_product;
        public TextView tv_pro_open;
        public TextView tv_pro_rate;
        public TextView tv_pro_ratechange;
        public String tag;
        public Optional optional;
    }

    List<View> mListView;
    public HashMap<String, ProductView> listProductView;
    BaseActivity context;
    private HashMap<String, Double> checkChangeMap = new HashMap<String, Double>();


    public HashMap<String, Double> getCheckChangeMap() {
        return checkChangeMap;
    }

    public void setCheckChangeMap(HashMap<String, Double> checkChangeMap) {
        this.checkChangeMap = checkChangeMap;
    }

    public ProductViewHold4Home(List<View> mListView, BaseActivity context, String codes) {
        this.mListView = mListView;
        this.context = context;
        listProductView = new HashMap<String, ProductView>();

        initProductViewList(codes);
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
        if (productView.tv_product != null) {
            if (!TextUtils.isEmpty(optional.getName())) {
                productView.tv_product.setText(optional.getName());
            }
        }
        if (productView.tv_pro_open != null)
            productView.tv_pro_open.setText(optional.getLastPrice());
        if (productView.tv_pro_rate != null) {

            double diff = Double.parseDouble(optional.getChange());
            //格式化小数点
            String diffStr = optional.getChange();
            String diffPercentStr = optional.getChg();

            int color = context.getResources().getColor(R.color.color_opt_lt);
            if (diff > 0) {
                color = context.getResources().getColor(R.color.color_opt_gt);
                diffStr = "+" + diffStr;
                diffPercentStr = "+" + diffPercentStr;
            }

            if (diff == 0)
                color = context.getResources().getColor(R.color.color_opt_eq);
            if (productView.tv_pro_rate != null) {
                productView.tv_pro_rate.setText(diffStr);
                productView.tv_pro_rate.setTextColor(color);
            }
            if (productView.tv_pro_open != null)
                productView.tv_pro_open.setTextColor(color);
            if (productView.tv_pro_ratechange != null) {
                productView.tv_pro_ratechange.setText(diffPercentStr);
                productView.tv_pro_ratechange.setTextColor(color);
            }
        }

        checkChange(productView.rl_product, optional.getCode(), Double.parseDouble(optional.getLastPrice()));

    }

    /**
     * 展示刷新
     *
     * @param v
     * @param getTreaty
     * @param Sellone
     */
    private void checkChange(View v, String getTreaty, Double Sellone) {
        Iterator<?> iter = checkChangeMap.entrySet().iterator();
        while (iter.hasNext()) {
            @SuppressWarnings("rawtypes")
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();

            if (key.equals(getTreaty)) {

                if (val == null) {

                } else {
                    if (Sellone > (Double) val) {
                        v.setBackgroundResource(R.drawable.home_animat_red);
                        try {
                            AnimationDrawable cusRedAnimationDrawable = (AnimationDrawable) v
                                    .getBackground();
                            cusRedAnimationDrawable.stop();
                            cusRedAnimationDrawable.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        checkChangeMap.put(getTreaty, Sellone);

                    } else if (Sellone < (Double) val) {
                        v.setBackgroundResource(R.drawable.home_animat_green);
                        try {
                            AnimationDrawable cusGreenAnimationDrawable = (AnimationDrawable) v
                                    .getBackground();
                            cusGreenAnimationDrawable.stop();
                            cusGreenAnimationDrawable.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        checkChangeMap.put(getTreaty, Sellone);
                    }
                }
                break;
            }
        }
    }

    /**
     * 控件初始化
     */
    void initProductViewList(String codes) {
        String[] allProducts = codes.split(",");
        for (int i = 0; i < mListView.size(); i++) {
            View view = mListView.get(i);
            for (int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                if (j == 0) {
                    ProductView productView_1 = new ProductView();
                    productView_1.rl_product = (RelativeLayout) view.findViewById(R.id.rl_product_1);
                    productView_1.tv_product = (TextView) view.findViewById(R.id.tv_product_1);
                    productView_1.tv_pro_open = (TextView) view.findViewById(R.id.tv_pro_open_1);
                    productView_1.tv_pro_rate = (TextView) view.findViewById(R.id.tv_pro_rate_1);
                    productView_1.tv_pro_ratechange = (TextView) view.findViewById(R.id.tv_pro_ratechange_1);

                    if (index < allProducts.length) {
//                            productView_1.tv_product.setText(producrName.get(allProducts[index]));
                        listProductView.put(allProducts[index], productView_1);
                    } else {
                        productView_1.rl_product.setVisibility(View.INVISIBLE);
                    }
                } else if (j == 1) {
                    ProductView productView_2 = new ProductView();
                    productView_2.rl_product = (RelativeLayout) view.findViewById(R.id.rl_product_2);
                    productView_2.tv_product = (TextView) view.findViewById(R.id.tv_product_2);
                    productView_2.tv_pro_open = (TextView) view.findViewById(R.id.tv_pro_open_2);
                    productView_2.tv_pro_rate = (TextView) view.findViewById(R.id.tv_pro_rate_2);
                    productView_2.tv_pro_ratechange = (TextView) view.findViewById(R.id.tv_pro_ratechange_2);

                    if (index < allProducts.length) {
//                            productView_2.tv_product.setText(producrName.get(allProducts[index]));
                        listProductView.put(allProducts[index], productView_2);
                    } else {
                        productView_2.rl_product.setVisibility(View.INVISIBLE);
                    }
                } else if (j == 2) {
                    ProductView productView_3 = new ProductView();
                    productView_3.rl_product = (RelativeLayout) view.findViewById(R.id.rl_product_3);
                    productView_3.tv_product = (TextView) view.findViewById(R.id.tv_product_3);
                    productView_3.tv_pro_open = (TextView) view.findViewById(R.id.tv_pro_open_3);
                    productView_3.tv_pro_rate = (TextView) view.findViewById(R.id.tv_pro_rate_3);
                    productView_3.tv_pro_ratechange = (TextView) view.findViewById(R.id.tv_pro_ratechange_3);

                    if (index < allProducts.length) {
//                            productView_3.tv_product.setText(producrName.get(allProducts[index]));
                        listProductView.put(allProducts[index], productView_3);
                    } else {
                        productView_3.rl_product.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }
}
