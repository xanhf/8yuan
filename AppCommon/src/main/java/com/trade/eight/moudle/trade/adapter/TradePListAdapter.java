package com.trade.eight.moudle.trade.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.ProFormatConfig;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.entity.trade.TradeOrderAndUserInfoData;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.product.activity.ProductActivity;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.moudle.trade.create.TradeCreateDlg;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
import com.trade.eight.mpush.coreoption.CoreOptionUtil;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.NoRepeatClickListener;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.Utils;
import com.trade.eight.tools.product.ProductViewHole4TradeContent;
import com.trade.eight.tools.trade.TradeConfig;

import java.util.List;

/**
 * Created by fangzhu
 * 交易大厅 产品列表adapter
 */
public class TradePListAdapter extends ArrayAdapter<ProductObj> {
    List<ProductObj> objects;
    BaseActivity context;
    Dialog msgDlg;
    TradeCreateDlg createDlg;
    /*保留头部第一个空白距离*/
    private boolean keepTopTemp = true;

    TradeLoginDlg tradeLoginDlg;
    ProductViewHole4TradeContent productViewHole4TradeContent;


    public TradePListAdapter(BaseActivity context, int resource, List<ProductObj> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public void setProductViewHole4TradeContent(ProductViewHole4TradeContent productViewHole4TradeContent) {
        this.productViewHole4TradeContent = productViewHole4TradeContent;
    }

    public ProductViewHole4TradeContent getProductViewHole4TradeContent() {
        return productViewHole4TradeContent;
    }

    public List<ProductObj> getItems() {
        return objects;
    }

    public int getProductIndex(String codes) {
        for (ProductObj productObj : objects) {
            if (codes.equals(productObj.getExcode() + "|" + productObj.getInstrumentId())) {
                return objects.indexOf(productObj);
            }
        }
        return 0;
    }

    public void setItems(List<ProductObj> list) {
        try {
            if (list == null || list.size() == 0)
                return;

        } catch (Exception e) {
            e.printStackTrace();
        }
        objects.clear();

        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                objects.add(list.get(i));
            }

            notifyDataSetChanged();
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_p_list, null);
        }

        View real_tradeinfo = MyViewHolder.get(convertView, R.id.real_tradeinfo);
        TextView text_productname = MyViewHolder.get(convertView, R.id.text_productname);
        TextView text_product_isclose = MyViewHolder.get(convertView, R.id.text_product_isclose);
        TextView text_product_cjl = MyViewHolder.get(convertView, R.id.text_product_cjl);
        TextView text_product_time = MyViewHolder.get(convertView, R.id.text_product_time);
        RelativeLayout buyUpView = MyViewHolder.get(convertView, R.id.buyupView);
        TextView tv_buyup_rate = MyViewHolder.get(convertView, R.id.tv_buyup_rate);
        RelativeLayout buyDownView = MyViewHolder.get(convertView, R.id.buyDownView);
        TextView tv_buydown_rate = MyViewHolder.get(convertView, R.id.tv_buydown_rate);
        View view_buyup_empty = MyViewHolder.get(convertView, R.id.view_buyup_empty);
        View view_buyup = MyViewHolder.get(convertView, R.id.view_buyup);
        TextView text_buyup_percent = MyViewHolder.get(convertView, R.id.text_buyup_percent);
        View view_buydown = MyViewHolder.get(convertView, R.id.view_buydown);
        View view_buydown_empty = MyViewHolder.get(convertView, R.id.view_buydown_empty);
        TextView text_buydown_percent = MyViewHolder.get(convertView, R.id.text_buydown_percent);

        final ProductObj item = objects.get(position);

        if (productViewHole4TradeContent != null) {
            ProductViewHole4TradeContent.ProductView productView = productViewHole4TradeContent.listProductView.get(item.getExcode() + "|" + item.getInstrumentId());
            if (productView != null) {
                productView.tv_buyup_rate = tv_buyup_rate;
                productView.tv_buydown_rate = tv_buydown_rate;
                productView.tag = item.getExcode() + "|" + item.getInstrumentId();
            }
        }

        text_productname.setText(item.getInstrumentName());
        if (ProductObj.IS_CLOSE_YES.equals(item.getIsClosed())) {
            text_product_isclose.setVisibility(View.VISIBLE);
        } else {
            text_product_isclose.setVisibility(View.GONE);
        }
        text_product_cjl.setText(item.getTradeVolume());
        text_product_time.setText(item.getExchangeTimePrompt());

        tv_buyup_rate.setText(item.getAskPrice1());
        tv_buydown_rate.setText(item.getBidPrice1());
        text_buyup_percent.setText(item.getLongRate() + "%");
        text_buydown_percent.setText(item.getShortRate() + "%");
        int up = Integer.parseInt(item.getLongRate());
        int down = 100 - up;
        LinearLayout.LayoutParams view_buyup_empty_lp = new LinearLayout.LayoutParams(0, Utils.dip2px(context, 3), down);
        view_buyup_empty.setLayoutParams(view_buyup_empty_lp);
        LinearLayout.LayoutParams view_buyup_lp = new LinearLayout.LayoutParams(0, Utils.dip2px(context, 3), up);
        view_buyup.setLayoutParams(view_buyup_lp);
        LinearLayout.LayoutParams view_buydown_empty_lp = new LinearLayout.LayoutParams(0, Utils.dip2px(context, 3), up);
        view_buydown_empty.setLayoutParams(view_buydown_empty_lp);
        LinearLayout.LayoutParams view_buydown_lp = new LinearLayout.LayoutParams(0, Utils.dip2px(context, 3), down);
        view_buydown.setLayoutParams(view_buydown_lp);

        real_tradeinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (StringUtil.isEmpty(item.getInstrumentId()))
                    return;
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.PAGE_INDEX_TRADE_GODETAIL, item.getName());
                Intent intent = new Intent(context, ProductActivity.class);
                intent.putExtra("code", item.getInstrumentId());
                intent.putExtra("excode", item.getExcode());
                context.startActivity(intent);
            }
        });

        if (buyUpView != null) {
//            if (ProductObj.IS_CLOSE_YES.equals(item.getIsClosed())) {
//                buyUpView.setEnabled(false);
//                buyUpView.setSelected(true);
//            } else {
//                buyUpView.setEnabled(true);
//                buyUpView.setSelected(false);
//            }
            buyUpView.setOnClickListener(new NoRepeatClickListener() {
                @Override
                public void onNoRepeatClick(View v) {

                    if (BaseInterface.SWITCH_DEPEND_PRODUCT_CLOSE) {
                        if (ProductObj.IS_CLOSE_YES.equals(item.getIsClosed())) {
                            String title = context.getResources().getString(R.string.close_title);
                            String close_title_bak = context.getResources().getString(R.string.close_title_bak);
                            if (msgDlg != null && msgDlg.isShowing())
                                return;
                            msgDlg = DialogUtil.getTradeCloseDlg(context, title, ConvertUtil.NVL(item.getClosePrompt(), close_title_bak), new DialogUtil.AuthTokenCallBack() {
                                @Override
                                public void onPostClick(Object obj) {
                                }

                                @Override
                                public void onNegClick() {

                                }
                            });
                            msgDlg.show();
                            return;
                        }
                    }
                    //没有登录的话 必须先登录
                    UserInfoDao dao = new UserInfoDao(context);
                    if (!dao.isLogin()) {
                        context.startActivity(new Intent(context, LoginActivity.class));
//                        DialogUtil.showConfirmDlg(context, null, null, null, true, new Handler.Callback() {
//                            @Override
//                            public boolean handleMessage(Message message) {
//
//                                return false;
//                            }
//                        }, new Handler.Callback() {
//                            @Override
//                            public boolean handleMessage(Message message) {
//                                context.startActivity(new Intent(context, LoginActivity.class));
//                                return false;
//                            }
//                        });
                        return;
                    }
                    //token 过期了
                    if (!TradeHelp.isTokenEnable((BaseActivity) context)) {
//                        DialogUtil.showTokenDialog(context, TradeConfig.getCurrentTradeCode(context),
//                                new DialogUtil.AuthTokenDlgShow() {
//                                    @Override
//                                    public void onDlgShow(Dialog dlg) {
//
//                                    }
//                                }, new DialogUtil.AuthTokenCallBack() {
//                                    @Override
//                                    public void onPostClick(Object obj) {
//
//                                    }
//
//                                    @Override
//                                    public void onNegClick() {
//
//                                    }
//                                });
                        if (tradeLoginDlg == null) {
                            tradeLoginDlg = new TradeLoginDlg(context);
                        }
                        if (!tradeLoginDlg.isShowingDialog()) {
                            tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                        }
                        return;
                    }
                    MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.PAGE_INDEX_TRADE_BUYUP, item.getName());
                    createDlg = new TradeCreateDlg(context, item, ProductObj.TYPE_BUY_UP);
                    createDlg.showDialog(R.style.dialog_trade_ani);
                    createDlg.setCreateCallback(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {

                            TradeInfoData userInfoData = TradeUserInfoData4Situation.getInstance(context, null).getTradeInfoData();
                            if (userInfoData == null) {
                                TradeUserInfoData4Situation.getInstance(context, null).loadTradeOrderAndUserInfoData(null, false);
                            }

                            return false;
                        }
                    });

                }
            });
        }

        if (buyDownView != null) {
//            if (ProductObj.IS_CLOSE_YES.equals(item.getIsClosed())) {
//                buyDownView.setEnabled(false);
//                buyDownView.setSelected(true);
//            } else {
//                buyDownView.setEnabled(true);
//                buyDownView.setSelected(false);
//            }
            buyDownView.setOnClickListener(new NoRepeatClickListener() {
                @Override
                public void onNoRepeatClick(View v) {
                    if (BaseInterface.SWITCH_DEPEND_PRODUCT_CLOSE) {
                        if (ProductObj.IS_CLOSE_YES.equals(item.getIsClosed())) {
                            String title = context.getResources().getString(R.string.close_title);
                            String close_title_bak = context.getResources().getString(R.string.close_title_bak);
                            if (msgDlg != null && msgDlg.isShowing())
                                return;
                            msgDlg = DialogUtil.getTradeCloseDlg(context, title, ConvertUtil.NVL(item.getClosePrompt(), close_title_bak), new DialogUtil.AuthTokenCallBack() {
                                @Override
                                public void onPostClick(Object obj) {
                                }

                                @Override
                                public void onNegClick() {

                                }
                            });
                            msgDlg.show();
                            return;
                        }
                    }

                    //没有登录的话 必须先登录
                    UserInfoDao dao = new UserInfoDao(context);
                    if (!dao.isLogin()) {
                        DialogUtil.showConfirmDlg(context, null, null, null, true, new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message message) {

                                return false;
                            }
                        }, new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message message) {
                                context.startActivity(new Intent(context, LoginActivity.class));
                                return false;
                            }
                        });
                        return;
                    }
                    //token 过期了
                    if (!TradeHelp.isTokenEnable((BaseActivity) context)) {
//                        DialogUtil.showTokenDialog(context, TradeConfig.getCurrentTradeCode(context),
//                                new DialogUtil.AuthTokenDlgShow() {
//                                    @Override
//                                    public void onDlgShow(Dialog dlg) {
//
//                                    }
//                                }, new DialogUtil.AuthTokenCallBack() {
//                                    @Override
//                                    public void onPostClick(Object obj) {
//
//                                    }
//
//                                    @Override
//                                    public void onNegClick() {
//
//                                    }
//                                });
                        if (tradeLoginDlg == null) {
                            tradeLoginDlg = new TradeLoginDlg(context);
                        }
                        if (!tradeLoginDlg.isShowingDialog()) {
                            tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                        }
                        return;
                    }
                    MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.PAGE_INDEX_TRADE_BUYDOWN, item.getName());

                    createDlg = new TradeCreateDlg(context, item, ProductObj.TYPE_BUY_DOWN);
                    createDlg.showDialog(R.style.dialog_trade_ani);
                    createDlg.setCreateCallback(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            TradeInfoData userInfoData = TradeUserInfoData4Situation.getInstance(context, null).getTradeInfoData();
                            if (userInfoData == null) {
                                TradeUserInfoData4Situation.getInstance(context, null).loadTradeOrderAndUserInfoData(null, false);
                            }
                            return false;
                        }
                    });
                }
            });
        }
        return convertView;
    }

    public void updateView(int itemIndex) {
//        //得到第一个可显示控件的位置，
//        int visiblePosition = mListView.getFirstVisiblePosition();
//        //只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
//        if (itemIndex - visiblePosition >= 0) {
//            //得到要更新的item的view
//            View view = mListView.getChildAt(itemIndex - visiblePosition);
//            //从view中取得holder
//            ViewHolder holder = (ViewHolder) view.getTag();
//
//            HashMap<String, Object> item = data.get(itemIndex);
//            //获取到具体的控件，
//            holder.name = (TextView) view.findViewById(R.id.name);
//            holder.process = (ProcessBar) view.findViewById(R.id.process);
//            .......
//            //对控件进行操作
//            holder.process.setMax(item.get("max"));
//            holder.process.setProgress(item.get("progress"));
//            ......
//
//        }
    }

    public boolean isKeepTopTemp() {
        return keepTopTemp;
    }

    public void setKeepTopTemp(boolean keepTopTemp) {
        this.keepTopTemp = keepTopTemp;
    }
}