package com.trade.eight.moudle.trade.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.ShareEntity;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.trade.ShareOrderEvent;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.ShareTools;
import com.trade.eight.tools.trade.TradeConfig;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by developer on 16/1/17.
 * 平仓详情
 */
public class TradeCloseDetailAct extends BaseActivity {
    public static final String TAG = "TradeCloseDetailAct";
    ShareTools shareTools;
    TradeCloseDetailAct context = this;
    TradeOrder order;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        order = (TradeOrder) getIntent().getSerializableExtra("object");
//        if (TradeConfig.exchangeId_jn == order.getExchangeId() || TradeConfig.exchangeId_hn == order.getExchangeId()) {
//            setContentView(R.layout.act_trade_close_detail_jn);
//        } else {
            setContentView(R.layout.act_trade_close_detail);
//        }


        setAppCommonTitle("平仓详情");
        initValue(order);
        shareTools = new ShareTools(this);
        initListener();
    }

    void initValue(TradeOrder item) {
        if (item == null)
            return;
        View btn_share = findViewById(R.id.btn_share);
        View line_share = findViewById(R.id.line_share);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
//        TextView tv_time = (TextView) findViewById(R.id.tv_time);
        TextView tv_typeline = (TextView) findViewById(R.id.tv_typeline);
        TextView tv_type = (TextView) findViewById(R.id.tv_type);

        TextView tv_priceCreate = (TextView) findViewById(R.id.tv_priceCreate);
        TextView tv_timeCreate = (TextView) findViewById(R.id.tv_timeCreate);
        TextView tv_priceClose = (TextView) findViewById(R.id.tv_priceClose);
        TextView tv_timeClose = (TextView) findViewById(R.id.tv_timeClose);
        TextView tv_profit = (TextView) findViewById(R.id.tv_profit);
        TextView tv_profitLoss = (TextView) findViewById(R.id.tv_profitLoss);
        TextView tv_totalMoney = (TextView) findViewById(R.id.tv_totalMoney);
        TextView tv_fee = (TextView) findViewById(R.id.tv_fee);
        TextView tv_usequan = (TextView) findViewById(R.id.tv_usequan);
        TextView tv_closeType = (TextView) findViewById(R.id.tv_closeType);

        //农交所需要的
        TextView tv_deferredFee = (TextView) findViewById(R.id.tv_deferredFee);
        if (tv_deferredFee != null) {
            tv_deferredFee.setText(NumberUtil.moveLast0(order.getProductDeferred()));
        }
        View tv_showFee = findViewById(R.id.tv_showFee);
        if (tv_showFee != null) {
            tv_showFee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TradeDeferredAct.start(context, order.getOrderId() + "");
                }
            });
        }

        tv_title.setText(ConvertUtil.NVL(ConvertUtil.NVL(item.getProductName(), "")
                + ConvertUtil.NVL(item.getWeight(), "")
                + ConvertUtil.NVL(item.getUnit(), "")
                + item.getOrderNumber(), "") + "手");

//        tv_time.setText(item.getCreateTime());

        tv_type.setText(item.getType());
        if (ConvertUtil.NVL(item.getType(), "").contains("跌")) {
            tv_typeline.setBackgroundColor(context.getResources().getColor(R.color.color_opt_lt));
            tv_type.setTextColor(context.getResources().getColor(R.color.color_opt_lt));
        }
        if (tv_priceCreate != null)
            tv_priceCreate.setText(item.getCreatePrice());
        if (tv_timeCreate != null) {
//            tv_timeCreate.setText(item.getCreateTime());
            tv_timeCreate.setText(DateUtil.formatDate(item.getCreateTime(), "yyyy-MM-dd HH:mm", "MM-dd HH:mm"));
        }

        if (tv_priceClose != null)
            tv_priceClose.setText(item.getClosePrice());

        if (tv_timeClose != null) {
//            tv_timeClose.setText(item.getCloseTime());
            tv_timeClose.setText(DateUtil.formatDate(item.getCloseTime(), "yyyy-MM-dd HH:mm", "MM-dd HH:mm"));
        }
        if (tv_profit != null)
            tv_profit.setText(item.getStopProfit());
        if (tv_profitLoss != null)
            tv_profitLoss.setText(item.getStopLoss());
        if (tv_totalMoney != null) {
            tv_totalMoney.setText(NumberUtil.moveLast0(item.getProfitLoss()));
            try {
                double d = Double.parseDouble(ConvertUtil.NVL(item.getProfitLoss(), "0").replace(",", ""));
                tv_totalMoney.setTextColor(getResources().getColor(R.color.trade_up));
                if (d < 0) {
                    tv_totalMoney.setTextColor(getResources().getColor(R.color.trade_down));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (tv_fee != null) {
            tv_fee.setText(NumberUtil.moveLast0(item.getFee()));

            //test 特殊包
//            if (BaseInterface.isDubug) {
//                if (BaseInterface.test.containsKey(item.getUnitPrice())) {
//                    double d = BaseInterface.test.get(item.getUnitPrice());
//                    tv_fee.setText("" + NumberUtil.multiply(d, Integer.parseInt(ConvertUtil.NVL(item.getOrderNumber(), "0"))));
//                }
//            }
        }
        try {
            //是否使用代金券 直接使用手续费为0 判断
            if (tv_usequan != null) {
                if (Double.parseDouble(ConvertUtil.NVL(item.getFee(), "0").replace(",", "")) != 0) {
                    tv_usequan.setText("账户余额");
                    //显示晒单按钮的逻辑，使用现金下的单并且盈利了
                    if (Double.parseDouble(ConvertUtil.NVL(item.getProfitLoss(), "0")) > 0)
                        line_share.setVisibility(View.VISIBLE);
                } else {
                    tv_usequan.setText("代金券");
                    line_share.setVisibility(View.GONE);
                    //                btn_share.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tv_closeType != null)
            tv_closeType.setText(item.getCloseType());


    }

    void initListener() {
        View line_share = findViewById(R.id.line_share);
        if (line_share != null) {
            line_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
//                        showShareDialog(context);
                        doShare(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    Dialog shareDlg = null;

    public void showShareDialog(Activity context) {
        if (shareDlg != null && shareDlg.isShowing())
            return;
        shareDlg = shareTools.getShareDialog(context);
        final View shareFriendView = shareDlg.findViewById(R.id.shareFriendView);
        if (shareFriendView != null) {
            shareFriendView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareDlg.dismiss();
                    try {
                        doShare(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        final View shareCircleView = shareDlg.findViewById(R.id.shareCircleView);
        if (shareCircleView != null) {
            shareCircleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareDlg.dismiss();
                    try {
                        doShare(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        shareDlg.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        shareTools.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 获取分享地址
     */
    public void doShare(final boolean isFriend) throws Exception {
        new AsyncTask<Void, Void, CommonResponse<ShareEntity>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showNetLoadingProgressDialog("请稍等");
            }

            @Override
            protected CommonResponse<ShareEntity> doInBackground(Void... params) {
                try {
                    Map<String, String> paraMap = ApiConfig.getCommonMap(context);
                    paraMap.put(UserInfo.UID, new UserInfoDao(context).queryUserInfo().getUserId());
                    paraMap.put(TradeProduct.PARAM_ORDER_ID, order.getOrderId() + "");
                    paraMap.put(TradeProduct.PARAM_EXCHANGE_ID, order.getExchangeId() + "");

                    paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

                    String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_SHARE_ORDER, paraMap);
                    Log.v(TAG, "result=" + result);
                    if (result == null)
                        return null;
                    CommonResponse response = CommonResponse.fromJson(result, ShareEntity.class);
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<ShareEntity> response) {
                super.onPostExecute(response);
                //share 自己会有 一个dialog 的显示
                hideNetLoadingProgressDialog();
                if (response != null) {
                    if (response.isSuccess()) {
                        if (isFriend) {
                            //微信好友
                            shareTools.shareToWeiXinFriend(R.drawable.app_icon, response.getData(), umShareListener);

                        } else {
                            //朋友圈
                            MyAppMobclickAgent.onEvent(context, "page_closeDetail_step1_share_success", new UserInfoDao(context).queryUserInfo().getUserId());

                            //分享，并设置成功后的回调
                            shareTools.shareToWeiXinCircle(R.drawable.app_icon, response.getData(), umShareListener);
                        }
                    } else {
//                        showCusToast(ConvertUtil.NVL(response.getErrorInfo(), "分享失败"));
                        DialogUtil.showMsgDialog(context, ConvertUtil.NVL(response.getErrorInfo(), "分享失败"), "确定");

                    }
                } else {
                    showCusToast("网络异常");
                }
            }
        }.execute();
    }

    /**
     * 分享的回调
     */
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            Toast.makeText(context, " 分享成功", Toast.LENGTH_SHORT).show();
            try {
                doShareCallBack();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(context, " 分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(context, " 分享已取消", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 分享成功之后回调接口
     */
    public void doShareCallBack() throws Exception {
        new AsyncTask<Void, Void, CommonResponse<String>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected CommonResponse<String> doInBackground(Void... params) {
                try {
                    Map<String, String> paraMap = ApiConfig.getCommonMap(context);
                    paraMap.put(UserInfo.UID, new UserInfoDao(context).queryUserInfo().getUserId());
                    paraMap.put(TradeProduct.PARAM_ORDER_ID, order.getOrderId() + "");
                    paraMap.put(TradeProduct.PARAM_EXCHANGE_ID, order.getExchangeId() + "");

                    paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, paraMap));

                    String result = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_SHARE_ORDER_SUCCESS, paraMap);
                    Log.v(TAG, "result=" + result);
                    if (result == null)
                        return null;
                    CommonResponse response = CommonResponse.fromJson(result, String.class);
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<String> response) {
                super.onPostExecute(response);
                if (response != null) {
                    if (response.isSuccess()) {
                        //成功统计  这里用上uid
                        try {
                            MyAppMobclickAgent.onEvent(context, "page_closeDetail_step2_getQuan_success", new UserInfoDao(context).queryUserInfo().getUserId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //成功 可以弹出提示
//                        showCusToast(ConvertUtil.NVL(response.getErrorInfo(), "已处理成功"));
//                        Toast.makeText(context, ConvertUtil.NVL(response.getErrorInfo(), "已处理成功"), Toast.LENGTH_LONG).show();
//                        DialogUtil.showMsgDialog(context, ConvertUtil.NVL(response.getErrorInfo(), "分享成功"), "确定");
                        DialogUtil.showSuccessDialog(context, "晒单成功", 0, null);
                        EventBus.getDefault().post(new ShareOrderEvent());// 发送刷新数据event
                    } else {
//                        showCusToast(ConvertUtil.NVL(response.getErrorInfo(), "信息获取失败"));
                        DialogUtil.showMsgDialog(context, ConvertUtil.NVL(response.getErrorInfo(), "信息获取失败"), "确定");

                    }
                } else {
                    showCusToast("网络异常");
                }
            }
        }.execute();
    }
}
