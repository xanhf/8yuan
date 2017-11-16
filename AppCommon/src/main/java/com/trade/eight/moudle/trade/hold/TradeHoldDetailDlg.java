package com.trade.eight.moudle.trade.hold;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.QiHuoExplainWordConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.me.activity.CashInAndOutAct;
import com.trade.eight.moudle.outterapp.QiHuoExplainWordActivity;
import com.trade.eight.moudle.product.OptionalEvent;
import com.trade.eight.moudle.trade.TradeOrderOptionEvent;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.moudle.trade.activity.DialogUtilForTrade;
import com.trade.eight.moudle.trade.adapter.TradeOrderDetailAdapter;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.Utils;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by dufangzhu on 2017/5/25.
 * fxbtg的建仓
 * <p>
 * 手数*longMarginRatioByMoney *volumeMultiple*行情价格
 * 手数*longMarginRatioByVolume
 * 行情根据长连接变化
 */

public class TradeHoldDetailDlg {
    public static final String TAG = "TradeCreateDlg";

    BaseActivity context;
    Dialog dialog;

    TradeOrder tradeOrder;

    TextView text_cancle;
    TextView text_productname;
    TextView text_buytype;
    TextView text_buynum;
    TextView tetx_hold_profitloss;
    TextView tetx_true_profitloss;
    Button btn_help;

    PullToRefreshListView mPullRefreshListView;
    ListView listView;


    public TradeHoldDetailDlg(BaseActivity context, TradeOrder tradeOrder) {
        this.context = context;
        this.tradeOrder = tradeOrder;

        dialog = new Dialog(context, R.style.dialog_trade);
        View rootView = View.inflate(context, R.layout.dialog_showorder_detail, null);
        dialog.setContentView(rootView);
        findViews(dialog);
    }

    public boolean isShowingDialog() {
        if (dialog != null)
            return dialog.isShowing();
        return false;
    }

    public void findViews(final Dialog dialog) {


        text_cancle = (TextView) dialog.findViewById(R.id.text_cancle);
        text_productname = (TextView) dialog.findViewById(R.id.text_productname);
        text_buytype = (TextView) dialog.findViewById(R.id.text_buytype);
        text_buynum = (TextView) dialog.findViewById(R.id.text_buynum);
        tetx_hold_profitloss = (TextView) dialog.findViewById(R.id.tetx_hold_profitloss);
        tetx_true_profitloss = (TextView) dialog.findViewById(R.id.tetx_true_profitloss);
        btn_help = (Button) dialog.findViewById(R.id.btn_help);

        mPullRefreshListView = (PullToRefreshListView) dialog.findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setPullRefreshEnabled(false);
        mPullRefreshListView.setPullLoadEnabled(false);
        listView = mPullRefreshListView.getRefreshableView();
        listView.setDividerHeight(0);

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
//                QiHuoExplainWordActivity.startAct(context,
                        DialogUtilForTrade.showQiHuoExplainDlg(context,

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

//        tetx_hold_profitloss.setTextColor(context.getResources().getColor(R.color.c_06A969));
//        tetx_hold_profitloss.setText(tradeOrder.getTodayProfit());
//        if (Double.parseDouble(tradeOrder.getTodayProfit()) > 0) {
//            tetx_hold_profitloss.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
//            tetx_hold_profitloss.setText("+" + tradeOrder.getTodayProfit());
//        }
//
//        tetx_true_profitloss.setTextColor(context.getResources().getColor(R.color.c_06A969));
//        tetx_true_profitloss.setText(tradeOrder.getTotalProfit());
//        if (Double.parseDouble(tradeOrder.getTotalProfit()) > 0) {
//            tetx_true_profitloss.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
//            tetx_true_profitloss.setText("+" + tradeOrder.getTotalProfit());
//        }

        setTradeOrder(tradeOrder);
    }

    public void setTradeOrder(TradeOrder tradeOrder) {

        this.tradeOrder = tradeOrder;
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
    }

    public void showDialog(int aniStyle) {
        if (!new UserInfoDao(context).isLogin()) {
            context.showCusToast(context.getString(R.string.login_please));
            return;
        }
        final Window w = dialog.getWindow();
        final WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        w.setGravity(Gravity.BOTTOM);
        w.setWindowAnimations(aniStyle);
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });


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

        getTradeDetailList();
    }

    private void getTradeDetailList() {
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
    }

}
