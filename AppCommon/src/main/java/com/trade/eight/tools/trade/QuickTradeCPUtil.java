package com.trade.eight.tools.trade;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.response.NettyResponse;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.moudle.baksource.BakSourceService;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.netty.NettyClient;
import com.trade.eight.moudle.netty.NettyUtil;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.product.OptionalEvent;
import com.trade.eight.moudle.trade.TradeOrderOptionEvent;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.NoRepeatClickListener;
import com.trade.eight.tools.Utils;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by fangzhu 大部分代码来自TradeCreateUtil做了修改
 * 快速建仓 直播室里 cp==choose product 选择产品
 * 选中哪一个产品  更改当前交易所  所以建仓遵循之前的逻辑
 */
public class QuickTradeCPUtil {
    public static final String TAG = "QuickTradeCPUtil";

    Dialog dialog;
    BaseActivity context;
    PullToRefreshListView pullToRefreshListView = null;
    ListView listView = null;
    ProductAdapter productAdapter;
    ProductViewList productViewList;
    private HashMap<String, Double> checkChangeMap = new HashMap<String, Double>();

    String codes = "";
    String historyExcode;// 之前的默认交易所
    String currentExcode;
    TradeCreateUtil createUtil = null;
    Dialog msgDlg;


    /**
     * 建仓页面dlg
     *
     * @param c
     */
    public QuickTradeCPUtil(BaseActivity c) {
        if (dialog != null && dialog.isShowing())
            return;

        try {
            this.context = c;

            if (!new UserInfoDao(context).isLogin()) {
                context.showCusToast("请登录");
                return;
            }
            dialog = new Dialog(context, R.style.dialog_trade);
            View rootView = View.inflate(context, R.layout.dialog_qucik_trade_chooseproduct, null);
            dialog.setContentView(rootView);
            codes = TradeConfig.getCurrentCodes(context);
            findViews(dialog);
            initProData();

            historyExcode = TradeConfig.getCurrentTradeCode(context);
            currentExcode = historyExcode;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isShowingDialog() {
        if (dialog != null)
            return dialog.isShowing();
        return false;
    }

    public void dismissDlg() {
        if (dialog != null)
            dialog.dismiss();
    }

    public void showDialog() {
        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = (int) context.getResources().getDimension(R.dimen.margin_448dp);
        w.setGravity(Gravity.BOTTOM);
        w.setWindowAnimations(R.style.dialog_trade_ani);
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                TradeConfig.setCurrentTradeCode(context, historyExcode);
                try {
                    EventBus.getDefault().unregister(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

            }
        });

        try {
            if (!EventBus.getDefault().isRegistered(this))
                EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    void findViews(Dialog dialog) {
        pullToRefreshListView = (PullToRefreshListView) dialog.findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setPullLoadEnabled(false);
        pullToRefreshListView.setPullRefreshEnabled(false);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setVisibility(View.VISIBLE);
        listView.setDividerHeight(0);
        productAdapter = new ProductAdapter(context, 0, new ArrayList<List<Optional>>());
        listView.setAdapter(productAdapter);
        productViewList = new ProductViewList();
    }

    /**
     * 初始化产品
     */
    void initProData() {
        new AsyncTask<Void, Void, List<Optional>>() {
            @Override
            protected List<Optional> doInBackground(Void... params) {
                try {
                    return BakSourceService.getOptionals(context, codes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                context.showNetLoadingProgressDialog("加载中");
            }

            @Override
            protected void onPostExecute(List<Optional> list) {
                super.onPostExecute(list);
                context.hideNetLoadingProgressDialog();
                if (list != null) {
                    groupOptional(list);
                    dialog.show();
                }
                if (NettyClient.getInstance(context).isInited())
                    NettyClient.getInstance(context).write(codes);
            }
        }.execute();
    }

    /**
     * 按交易所分组,并且每行最多展示3个产品
     *
     * @param list
     */
    private void groupOptional(List<Optional> list) {

        if (checkChangeMap.size() == 0) {
            for (Optional optional : list) {
                checkChangeMap.put(optional.getType() + "|" + optional.getTreaty(), Double.parseDouble(optional.getSellone()));
            }
        }

        List<List<Optional>> groupList = new ArrayList<List<Optional>>();
        List<Optional> childList = new ArrayList<Optional>();
        HashMap<Integer, String> tempMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            childList = new ArrayList<Optional>();
            groupList.add(childList);
            tempMap.put(groupList.size(), list.get(i).getType());
            childList.add(list.get(i));
            if (i + 1 < list.size() && list.get(i).getType().equals(list.get(i + 1).getType())) {
                childList.add(list.get(i + 1));
                i++;
            }
            if (i + 1 < list.size() && list.get(i).getType().equals(list.get(i + 1).getType())) {
                childList.add(list.get(i + 1));
                i++;
            }

        }
        productAdapter.setItems(groupList);
    }

    /**
     * 建仓 成功事件
     *
     * @param event
     */
    public void onEvent(TradeOrderOptionEvent event) {
        if (event.option_type == TradeOrderOptionEvent.OPTION_CREATESUCCESS) {
            dismissDlg();
        }
    }

    /**
     * 产品listview 适配器
     */
    class ProductAdapter extends ArrayAdapter<List<Optional>> {
        List<List<Optional>> objects;

        public ProductAdapter(Context context, int resource, List<List<Optional>> objects) {
            super(context, resource, objects);
            this.objects = objects;
        }

        public void setItems(List<List<Optional>> list) {
            if (list == null || list.size() == 0)
                return;
            objects.clear();
            if (list != null) {
                objects.addAll(list);
                notifyDataSetChanged();
            }
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.dialog_quicktrade_cpitem, null);
            }
            TextView text_exchangename = MyViewHolder.get(convertView, R.id.text_exchangename);
            View rel_product_0 = MyViewHolder.get(convertView, R.id.rel_product_0);
            View rel_product_anim0 = MyViewHolder.get(convertView, R.id.rel_product_anim0);
            TextView text_productname_0 = MyViewHolder.get(convertView, R.id.text_productname_0);
            TextView text_productrate_0 = MyViewHolder.get(convertView, R.id.text_productrate_0);
            View rel_product_1 = MyViewHolder.get(convertView, R.id.rel_product_1);
            View rel_product_anim1 = MyViewHolder.get(convertView, R.id.rel_product_anim1);
            TextView text_productname_1 = MyViewHolder.get(convertView, R.id.text_productname_1);
            TextView text_productrate_1 = MyViewHolder.get(convertView, R.id.text_productrate_1);
            View rel_product_2 = MyViewHolder.get(convertView, R.id.rel_product_2);
            View rel_product_anim2 = MyViewHolder.get(convertView, R.id.rel_product_anim2);
            TextView text_productname_2 = MyViewHolder.get(convertView, R.id.text_productname_2);
            TextView text_productrate_2 = MyViewHolder.get(convertView, R.id.text_productrate_2);

            List<Optional> list = objects.get(position);
//            if (position == 0) {
//                text_exchangename.setVisibility(View.VISIBLE);
//            } else {
//                if (list.get(0).getType().equals(objects.get(position - 1).get(0).getType())) {
//                    text_exchangename.setVisibility(View.GONE);
//                }
//            }
//            text_exchangename.setText(TradeConfig.getName(context, list.get(0).getType()));
            ProductView productView = new ProductView(rel_product_0, rel_product_anim0, text_productname_0, text_productrate_0, list.get(0).getType() + "|" + list.get(0).getTreaty(), list.get(0));
            productViewList.listProductView.put(list.get(0).getType() + "|" + list.get(0).getTreaty(), productView);
            productViewList.updateProductViewListDisplay(list.get(0));

            text_productname_0.setText(list.get(0).getTitle());
            text_productrate_0.setText(list.get(0).getSellone());
            if (list.size() >= 2) {
                rel_product_1.setVisibility(View.VISIBLE);
                text_productname_1.setText(list.get(1).getTitle());
                text_productrate_1.setText(list.get(1).getSellone());
                ProductView productView_1 = new ProductView(rel_product_1, rel_product_anim1, text_productname_1, text_productrate_1, list.get(1).getType() + "|" + list.get(1).getTreaty(), list.get(1));
                productViewList.listProductView.put(list.get(1).getType() + "|" + list.get(1).getTreaty(), productView_1);
                productViewList.updateProductViewListDisplay(list.get(1));
            } else {
                rel_product_1.setVisibility(View.INVISIBLE);
                rel_product_2.setVisibility(View.INVISIBLE);
            }
            if (list.size() >= 3) {
                rel_product_2.setVisibility(View.VISIBLE);
                text_productname_2.setText(list.get(2).getTitle());
                text_productrate_2.setText(list.get(2).getSellone());
                ProductView productView_2 = new ProductView(rel_product_2, rel_product_anim2, text_productname_2, text_productrate_2, list.get(2).getType() + "|" + list.get(2).getTreaty(), list.get(2));
                productViewList.listProductView.put(list.get(2).getType() + "|" + list.get(2).getTreaty(), productView_2);
                productViewList.updateProductViewListDisplay(list.get(2));
            } else {
                rel_product_2.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
    }

    /**
     * 统一处理刷新以及涨跌幅变化
     */
    class ProductViewList {
        HashMap<String, ProductView> listProductView;

        public ProductViewList() {
            listProductView = new HashMap<String, ProductView>();
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
            String code = optional.getType() + "|" + optional.getTreaty();
            ProductView productView = listProductView.get(code);

            if (productView == null)
                return;
            if (productView.text_productrate != null) {
                productView.text_productrate.setText(optional.getSellone());
                double diff = optional.getRate();
                double diffPercent = optional.getRateChange();

                //格式化小数点

                int color = context.getResources().getColor(R.color.color_opt_lt);
                if (diff > 0) {
                    color = context.getResources().getColor(R.color.color_opt_gt);
                }

                if (diff == 0) {
                    color = context.getResources().getColor(R.color.color_opt_eq);
                }
                productView.text_productrate.setTextColor(color);
            }

            checkChange(productView.rel_product_anim, optional.getType() + "|" + optional.getTreaty(), Double.parseDouble(optional.getSellone()));

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
    }

    /**
     * 产品和view绑定的类
     */
    class ProductView {
        View rel_product;
        View rel_product_anim;
        TextView text_productname;
        TextView text_productrate;
        public String tag;
        public Optional optional;

        public ProductView(View rel_product, View rel_product_anim, TextView text_productname, TextView text_productrate, String tag, Optional optional) {
            this.rel_product = rel_product;
            this.rel_product_anim = rel_product_anim;
            this.text_productname = text_productname;
            this.text_productrate = text_productrate;
            this.tag = tag;
            this.optional = optional;

            initLisenter();
        }

        private void initLisenter() {
            rel_product.setOnClickListener(new NoRepeatClickListener() {
                @Override
                public void onNoRepeatClick(View v) {
                    if (!currentExcode.equals(optional.getType())) {
                        TradeConfig.setCurrentTradeCode(context, optional.getType());
                        currentExcode = optional.getType();
                    }
                    getProductListFromNet(optional);
                }
            });
        }
    }

    /**
     * netty连接成功后收到的广播
     *
     * @param event
     */
    public void onEvent(final OptionalEvent event) {
        //如果被隐藏了可以不执行刷新操作
        if (!dialog.isShowing())
            return;
        Log.v(TAG, "onEvent");
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event == null)
                    return;
                NettyResponse<Optional> response = event.getNettyResponse();
                if (response == null)
                    return;
                if (NettyUtil.TYPE_CONNECT.equals(response.getType())) {
                    NettyClient.getInstance(context).write(codes);

                } else if (NettyUtil.TYPE_QP.equals(response.getType())) {
                    Optional optional = response.getData();
                    if (optional == null || productViewList == null)
                        return;
                    productViewList.updateProductViewListDisplay(optional);
                }
            }
        });
    }

    boolean checkGotoWeipan() {
        if (!new UserInfoDao(context).isLogin()) {
            //直接跳转到登录页面
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            return false;
        }
        return true;
    }

    public void getProductListFromNet(final Optional optional) {
        //先检测是否需要获取数据
        if (!checkGotoWeipan())
            return;
        new AsyncTask<Void, Void, CommonResponse4List<List<TradeProduct>>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                context.showNetLoadingProgressDialog("加载中");
            }

            @Override
            protected CommonResponse4List<List<TradeProduct>> doInBackground(Void... voids) {
                return TradeHelp.getTypeProductList(context);
            }

            @Override
            protected void onPostExecute(CommonResponse4List<List<TradeProduct>> res) {
                super.onPostExecute(res);
                if (!isShowingDialog())
                    return;
                if (res == null) {
                    context.showCusToast("数据获取失败");
                    context.hideNetLoadingProgressDialog();
                    return;
                }
                if (res != null && res.isSuccess()) {
                    if (res.getData() == null) {
                        context.hideNetLoadingProgressDialog();
                        return;
                    }

                    //找到相应的品种
                    if (optional != null) {
                        String code = ConvertUtil.NVL(optional.getProductCode(), "");
                        for (int i = 0; i < res.getData().size(); i++) {
                            if (code.equals(res.getData().get(i).get(0).getContract())) {
                                List<TradeProduct> productList = res.getData().get(i);
                                if (productList != null && productList.size() > 0) {
                                    TradeProduct product = productList.get(0);
                                    startTradeCreate(product, TradeProduct.TYPE_BUY_UP, res.getData());
                                }
                                return;
                            }
                        }
                    }
                } else {
                    context.showCusToast(res.getErrorInfo());
                    context.hideNetLoadingProgressDialog();
                }
            }
        }.execute();
    }

    /**
     * @param tradeProduct
     * @param upOrdown     TradeProduct.TYPE_BUY_UP
     */
    void startTradeCreate(TradeProduct tradeProduct, int upOrdown, List<List<TradeProduct>> list) {
        if (tradeProduct == null)
            return;

        if (BaseInterface.SWITCH_DEPEND_PRODUCT_CLOSE) {
            if (TradeProduct.IS_CLOSE_YES.equals(tradeProduct.getIsClosed())) {
                context.hideNetLoadingProgressDialog();
                String title = context.getResources().getString(R.string.close_title);
                String close_title_bak = context.getResources().getString(R.string.close_title_bak);

                if (msgDlg != null && msgDlg.isShowing())
                    return;
                msgDlg = DialogUtil.getTradeCloseDlg(context, title, ConvertUtil.NVL(tradeProduct.getClosePrompt(), close_title_bak), new DialogUtil.AuthTokenCallBack() {
                    @Override
                    public void onPostClick(Object obj) {
                        String qa_trade = context.getResources().getString(R.string.qa_trade);
                        WebActivity.start(context, qa_trade, AndroidAPIConfig.URL_RULES_QA);
                    }

                    @Override
                    public void onNegClick() {

                    }
                });
                msgDlg.show();
                return;
            }
        }
        //token 过期了
        if (!TradeHelp.isTokenEnable(context)) {
            context.hideNetLoadingProgressDialog();
            DialogUtil.showTokenDialog(context, TradeConfig.getCurrentTradeCode(context),
                    new DialogUtil.AuthTokenDlgShow() {
                        @Override
                        public void onDlgShow(Dialog dlg) {

                        }
                    }, new DialogUtil.AuthTokenCallBack() {
                        @Override
                        public void onPostClick(Object obj) {

                        }

                        @Override
                        public void onNegClick() {

                        }
                    });
            return;
        }

        if (createUtil != null && createUtil.isShowingDialog())
            return;
        if (TradeConfig.isCurrentJN(context)) {
            createUtil = new TradeCreateUtil4JN(context, upOrdown, tradeProduct, list);
        } else {
            createUtil = new TradeCreateUtil4GG(context, upOrdown, tradeProduct, list);
        }
        createUtil.setGobackViewVisible(true);
        createUtil.isGotoTradeOrder(false);
        createUtil.setRefreshSelf(true);
        createUtil.showDialog(R.style.dialog_trade_ani);
    }


}

