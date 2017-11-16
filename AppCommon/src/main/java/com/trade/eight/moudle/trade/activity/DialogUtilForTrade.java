package com.trade.eight.moudle.trade.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.QiHuoExplainWordConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.QiHuoExplainWordData;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.Banks;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.outterapp.QiHuoExplainWordActivity;
import com.trade.eight.moudle.trade.SetViewHeightEvent;
import com.trade.eight.moudle.trade.adapter.ExplainWordAdapter;
import com.trade.eight.moudle.trade.adapter.TradeOrderDetailAdapter;
import com.trade.eight.moudle.trade.adapter.UserBankListAdapter;
import com.trade.eight.moudle.trade.cashinout.CashOutBindCardAct;
import com.trade.eight.moudle.trade.cashinout.CashOutCardManageAct;
import com.trade.eight.moudle.trade.view.ProfitLossView;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.Utils;
import com.trade.eight.tools.trade.TradeConfig;
import com.trade.eight.view.password.OnPasswordInputFinish;
import com.trade.eight.view.password.PasswordView;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 2017/5/25.
 * 交易相关的dialog
 */

public class DialogUtilForTrade {
    public static final String TAG = "DialogUtilForTrade";

    /**
     * 平仓确认弹窗
     *
     * @param context
     * @param tradeOrder
     * @param callbackNeg
     * @param callbackPost
     * @return
     */
    public static Dialog showConfirmColseOrderDlg(Activity context, TradeOrder tradeOrder, final Handler.Callback callbackNeg, final Handler.Callback callbackPost) {
        if (context == null || context.isFinishing())
            return null;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_confirm_closeorder);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        w.setGravity(Gravity.BOTTOM);

        final TextView text_makemoney = (TextView) dialog.findViewById(R.id.text_makemoney);
        text_makemoney.setTextColor(context.getResources().getColor(R.color.trade_up));
        if (Double.parseDouble(ConvertUtil.NVL(tradeOrder.getRealTimeProfitLoss(), "0")) < 0) {
            text_makemoney.setTextColor(context.getResources().getColor(R.color.trade_down));
        }
        text_makemoney.setText(ConvertUtil.NVL(tradeOrder.getRealTimeProfitLoss(), "0"));
        text_makemoney.setTag(tradeOrder);

        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        if (btn_cancle != null) {
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callbackNeg != null)
                        callbackNeg.handleMessage(new Message());
                }
            });
        }

        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);
        if (btn_commit != null) {

            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callbackPost != null) {
                        Message message = new Message();
                        callbackPost.handleMessage(message);
                    }
                }
            });
        }
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    /**
     * 提现切换银行卡
     *
     * @param context
     * @return
     */
    public static Dialog showSelectBankDlg(final BaseActivity context, final List<Banks> banksList, Banks defaultBanks, final Handler.Callback callbackPost) {
        if (context == null || context.isFinishing())
            return null;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_cashout_selectcard);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        w.setGravity(Gravity.BOTTOM);

        PullToRefreshListView mPullRefreshListView = (PullToRefreshListView) dialog.findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setPullRefreshEnabled(false);
        mPullRefreshListView.setPullLoadEnabled(false);
        ListView listView = mPullRefreshListView.getRefreshableView();
//        listView.setHeaderDividersEnabled(false);
//        listView.setFooterDividersEnabled(false);
        listView.setDividerHeight(0);
        View line_addbank = LayoutInflater.from(context).inflate(R.layout.layout_cardmanager_foot, null);
        listView.addFooterView(line_addbank);
        line_addbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CashOutBindCardAct.start(context);
                dialog.dismiss();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (callbackPost != null) {
                    Message message = new Message();
                    message.obj = banksList.get(position);
                    callbackPost.handleMessage(message);
                }
                dialog.dismiss();
            }
        });

        UserBankListAdapter userBankListAdapter = new UserBankListAdapter(context, 0, banksList);
        userBankListAdapter.setShowImgChecked(true);
        userBankListAdapter.setDefaultBank(defaultBanks);
        listView.setAdapter(userBankListAdapter);


        int totalHeight = 0;
        for (int i = 0; i < userBankListAdapter.getCount(); i++) {
            View listItem = userBankListAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams listParams = listView.getLayoutParams();
        int height = totalHeight + (listView.getDividerHeight() * (userBankListAdapter.getCount() - 1));
        Log.e(TAG, "height======" + height);
        height += Utils.dip2px(context, 90);
//        listParams.height = height;
//        listView.setLayoutParams(listParams);

        params.height = height;

        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }

    /**
     * 止盈止损弹窗弹窗
     *
     * @param context
     * @param tradeOrder
     * @param callbackNeg
     * @return
     */
    public static Dialog showChangeOrderDlg(final Activity context, final TradeOrder tradeOrder, final Handler.Callback callbackNeg) {
        if (context == null || context.isFinishing())
            return null;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_uppdate_orderprofit);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        w.setGravity(Gravity.BOTTOM);

        w.setWindowAnimations(R.style.dialog_trade_ani);
        TextView text_ordertitle = (TextView) dialog.findViewById(R.id.text_ordertitle);
        text_ordertitle.setText(tradeOrder.getProductName());
        final ProfitLossView profitLossView = (ProfitLossView) dialog.findViewById(R.id.profitLossView);
        profitLossView.init(tradeOrder, tradeOrder.getType(), null);

        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        if (btn_cancle != null) {
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callbackNeg != null)
                        callbackNeg.handleMessage(new Message());
                }
            });
        }
        //点击事件放在外面处理
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }


    /**
     * 持仓单详情
     *
     * @param context
     * @return
     */
    public static Dialog showQiHuoOrderDetilDlg(final BaseActivity context, TradeOrder tradeOrder) {
        if (context == null || context.isFinishing())
            return null;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_showorder_detail);


        final Window w = dialog.getWindow();
        final WindowManager.LayoutParams params = w.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        w.setGravity(Gravity.BOTTOM);

        TextView text_cancle = (TextView) dialog.findViewById(R.id.text_cancle);
        TextView text_productname = (TextView) dialog.findViewById(R.id.text_productname);
        TextView text_buytype = (TextView) dialog.findViewById(R.id.text_buytype);
        TextView text_buynum = (TextView) dialog.findViewById(R.id.text_buynum);
        TextView tetx_hold_profitloss = (TextView) dialog.findViewById(R.id.tetx_hold_profitloss);
        TextView tetx_true_profitloss = (TextView) dialog.findViewById(R.id.tetx_true_profitloss);
        Button btn_help = (Button) dialog.findViewById(R.id.btn_help);

        PullToRefreshListView mPullRefreshListView = (PullToRefreshListView) dialog.findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setPullRefreshEnabled(false);
        mPullRefreshListView.setPullLoadEnabled(false);
        final ListView listView = mPullRefreshListView.getRefreshableView();
//        listView.setHeaderDividersEnabled(false);
//        listView.setFooterDividersEnabled(false);
        listView.setDividerHeight(0);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                int totalHeight = 0;
                ListAdapter adapter = listView.getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    View listItem = adapter.getView(i, null, listView);
                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
                }
                ViewGroup.LayoutParams listParams = listView.getLayoutParams();
                int height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
                Log.e(TAG, "height======" + height);
                height += Utils.dip2px(context, 142);
                if (height > (Utils.getScreenH(context) - Utils.getStatusHeight(context))) {
                    height = (int) (Utils.getScreenH(context) * 0.8);
                }
                params.height = height;//
                w.setAttributes(params);
                listParams.height = height - Utils.dip2px(context, 142);
                listView.setLayoutParams(listParams);
            }
        });

        text_productname.setText(tradeOrder.getInstrumentName());
        if (tradeOrder.getType() == ProductObj.TYPE_BUY_UP) {
            text_buytype.setText(R.string.trade_buy_up);
            text_buytype.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
            text_buynum.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
        } else {
            text_buytype.setText(R.string.trade_buy_down);
            text_buytype.setTextColor(context.getResources().getColor(R.color.c_06A969));
            text_buynum.setTextColor(context.getResources().getColor(R.color.c_06A969));
        }
        text_buynum.setText(context.getResources().getString(R.string.lable_tr_ordernumber, tradeOrder.getPosition()));

        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QiHuoExplainWordActivity.startAct(context,
                        new String[]{QiHuoExplainWordConfig.CCYK,
                                QiHuoExplainWordConfig.SJYK,
                                QiHuoExplainWordConfig.JCSXF,
                                QiHuoExplainWordConfig.DRBZJ});
            }
        });
        text_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tetx_hold_profitloss.setTextColor(context.getResources().getColor(R.color.c_06A969));
        tetx_hold_profitloss.setText(tradeOrder.getTodayProfit());
        if (Double.parseDouble(tradeOrder.getTodayProfit()) > 0) {
            tetx_hold_profitloss.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
            tetx_hold_profitloss.setText("+" + tradeOrder.getTodayProfit());
        }

        tetx_true_profitloss.setTextColor(context.getResources().getColor(R.color.c_06A969));
        tetx_true_profitloss.setText(tradeOrder.getTotalProfit());
        if (Double.parseDouble(tradeOrder.getTotalProfit()) > 0) {
            tetx_true_profitloss.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
            tetx_true_profitloss.setText("+" + tradeOrder.getTotalProfit());
        }

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("instrumentId", tradeOrder.getInstrumentId());
        hashMap.put("type", tradeOrder.getType() + "");
        hashMap.put("todayPosition", tradeOrder.getTodayPosition() + "");

        HttpClientHelper.doPostOption(context,
                AndroidAPIConfig.URL_TRADE_ORDER_DETAIL,
                hashMap,
                null,
                new NetCallback(context) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        context.showCusToast(resultMsg);

                    }

                    @Override
                    public void onResponse(String response) {
                        CommonResponse4List<TradeOrder> commonResponse4List = CommonResponse4List.fromJson(response, TradeOrder.class);
                        List<TradeOrder> list = commonResponse4List.getData();
                        if (list != null && list.size() > 0) {
                            TradeOrderDetailAdapter tradeOrderDetailAdapter = new TradeOrderDetailAdapter(context, 0, list);
                            listView.setAdapter(tradeOrderDetailAdapter);

                            dialog.show();
                        }
                    }
                }, true);
        dialog.setCancelable(true);
        return dialog;
    }


    /**
     * 输入数字密码
     *
     * @param context
     * @param selectedBank
     * @param posCall
     * @return
     */
    public static Dialog showInputPWDDlg(final BaseActivity context, Banks selectedBank, final Handler.Callback posCall) {
        if (context == null || context.isFinishing())
            return null;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dlg_input_bankput);

        Window w = dialog.getWindow();
        final WindowManager.LayoutParams params = w.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        w.setGravity(Gravity.BOTTOM);

        final PasswordView passwordView = (PasswordView) dialog.findViewById(R.id.password_view);
        passwordView.setOnFinishInput(new OnPasswordInputFinish() {
            @Override
            public void inputFinish() {
                dialog.dismiss();
                if (posCall != null) {
                    Message message = new Message();
                    message.obj = passwordView.getStrPassword();
                    posCall.handleMessage(message);
                }
            }

            @Override
            public void onCancle() {
                dialog.dismiss();
            }
        });

        passwordView.setSelectedBank(selectedBank);
        passwordView.setPosCall(posCall);
        dialog.show();
        dialog.setCancelable(true);
        return dialog;
    }


    /**
     * 输入资金密码(查询银行卡余额)
     * @param context
     * @param selectedBank
     * @param posCall
     * @return
     */
    public static Dialog showInputMoneyPwdDialog(final BaseActivity context, Banks selectedBank, final Handler.Callback posCall) {
        if (context == null || context.isFinishing())
            return null;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dlg_input_moneypwd);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int width = (int) context.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;


        final TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        if (tv_content != null){
            tv_content.setText(context.getResources().getString(R.string.lable_cashin_bankmoney_tips,
                    ConvertUtil.NVL(selectedBank.getBankName(), ""),
                    StringUtil.getHintCardNo(selectedBank.getBankAccount())));
        }
        final EditText edit_moneypwd = (EditText) dialog.findViewById(R.id.edit_moneypwd);
        final Button btnPos = (Button) dialog.findViewById(R.id.btnPos);
        if (btnPos != null) {

            btnPos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String password = edit_moneypwd.getText().toString();
                    if (TextUtils.isEmpty(password)) {
                        context.showCusToast("请输入资金密码");
                        return;
                    }

                    dialog.dismiss();
                    if (posCall != null) {
                        Message message = new Message();
                        message.obj = password;
                        posCall.handleMessage(message);
                    }
                }
            });
        }
        final Button btnNav = (Button) dialog.findViewById(R.id.btnNav);
        if (btnNav != null) {

            btnNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });
        }
        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }


    /**
     * 名词解释
     *
     * @param context
     * @return
     */
    public static Dialog showQiHuoExplainDlg(final BaseActivity context, String[] keys) {
        if (context == null || context.isFinishing())
            return null;
        final Dialog dialog = new Dialog(context, R.style.dialog_forqihuoexplain);
        dialog.setContentView(R.layout.act_qihuo_explainword);

        final Window w = dialog.getWindow();
        final WindowManager.LayoutParams params = w.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        w.setWindowAnimations(R.style.dialog_trade_ani);
        w.setGravity(Gravity.BOTTOM);

        TextView text_qhlogin_cancle;
        ListView recycle_explain;

        text_qhlogin_cancle = (TextView) dialog.findViewById(R.id.text_qhlogin_cancle);
        text_qhlogin_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        recycle_explain = (ListView) dialog.findViewById(R.id.recycle_explain);

        View footView = View.inflate(context, R.layout.view_explain_foot, null);
        recycle_explain.addFooterView(footView);

        List<QiHuoExplainWordData> list = QiHuoExplainWordConfig.getDisplayList(keys);
        ExplainWordAdapter explainWordAdapter = new ExplainWordAdapter(context, 0, list);
        recycle_explain.setAdapter(explainWordAdapter);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
//                int totalHeight = 0;
//                ListAdapter adapter = listView.getAdapter();
//                for (int i = 0; i < adapter.getCount(); i++) {
//                    View listItem = adapter.getView(i, null, listView);
//                    listItem.measure(0, 0);
//                    totalHeight += listItem.getMeasuredHeight();
//                }
//                ViewGroup.LayoutParams listParams = listView.getLayoutParams();
//                int height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
//                Log.e(TAG, "height======" + height);
//                height += Utils.dip2px(context, 142);
//                if (height > (Utils.getScreenH(context) - Utils.getStatusHeight(context))) {
//                    height = (int) (Utils.getScreenH(context) * 0.8);
//                }
//                params.height = height;//
//                w.setAttributes(params);
//                listParams.height = height - Utils.dip2px(context, 142);
//                listView.setLayoutParams(listParams);
            }
        });
        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }


}
