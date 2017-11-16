package com.trade.eight.moudle.trade.close;

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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.config.QiHuoExplainWordConfig;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.outterapp.QiHuoExplainWordActivity;
import com.trade.eight.moudle.trade.TradeOrderOptionEvent;
import com.trade.eight.moudle.trade.activity.DialogUtilForTrade;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.refresh.RefreshUtil;
import com.trade.eight.tools.trade.TradeConfig;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 平仓
 * <p>
 * 上期所 平仓手续费分为平今  平昨  不用混合判断
 * 其他交易默认平今,所以平仓手续费
 * 买涨  （快速平仓价(申买价)-持仓均价）/最小变动价位*合约数量乘数*手数
 * 买涨  （快速平仓价(申买价)-建仓仓均价）/最小变动价位*合约数量乘数*手数
 * <p>
 * * 手数*longMarginRatioByMoney *volumeMultiple*行情价格
 * 手数*longMarginRatioByVolume
 */

public class TradeCloseDlg {
    public static final String TAG = "TradeCreateDlg";
    /****
     * 订单
     ****/
    TradeOrder tradeOrder;
    BaseActivity context;
    Dialog dialog;

    TextView text_close_title;
    TextView text_close_cancle;
    TextView text_product_isclose;
    TextView text_buytype;
    TextView text_buynum;
    TextView text_trade_order_ccjj;
    TextView text_trade_close_price;
    TextView text_trade_close_num;
    Button btn_deal_reduce;
    EditText ed_num;
    Button btn_deal_add;
    TextView text_trade_close_sxf;
    TextView text_trade_close_orderprofit;
    Button btn_create_help;
    TextView text_trade_close_order_todayprofit;
    Button btn_submit;

    private boolean isSHFE = false;// 是否为上期所
    private boolean isCloseToday = false;//是否为上期所平今天的仓 当此参数为true  不用考虑isSHFE
    private int maxClosePosition = 1;
    private int minClosePosition = 1;//最小平仓手数  固定为1

    TradeLoginDlg tradeLoginDlg;
    RefreshUtil refreshUtil;


    public TradeCloseDlg(BaseActivity context, TradeOrder tradeOrder, boolean isCloseToday) {
        this.context = context;
        this.tradeOrder = tradeOrder;
        this.isCloseToday = isCloseToday;
        if (tradeOrder.getExcode().equals(TradeConfig.code_SHFE)) {
            isSHFE = true;
        } else {
            isSHFE = false;
        }
        dialog = new Dialog(context, R.style.dialog_trade);
        View rootView = View.inflate(context, R.layout.fx_close_dlg, null);
        dialog.setContentView(rootView);
        findViews(dialog);
        initRefresh();
    }

    public boolean isShowingDialog() {
        if (dialog != null)
            return dialog.isShowing();
        return false;
    }

    public void findViews(Dialog dialog) {

        text_close_title = (TextView) dialog.findViewById(R.id.text_close_title);
        text_close_cancle = (TextView) dialog.findViewById(R.id.text_close_cancle);
        text_product_isclose = (TextView) dialog.findViewById(R.id.text_product_isclose);
        text_buytype = (TextView) dialog.findViewById(R.id.text_buytype);
        text_buynum = (TextView) dialog.findViewById(R.id.text_buynum);
        text_trade_order_ccjj = (TextView) dialog.findViewById(R.id.text_trade_order_ccjj);
        text_trade_close_price = (TextView) dialog.findViewById(R.id.text_trade_close_price);
        text_trade_close_num = (TextView) dialog.findViewById(R.id.text_trade_close_num);
        btn_deal_reduce = (Button) dialog.findViewById(R.id.btn_deal_reduce);
        ed_num = (EditText) dialog.findViewById(R.id.ed_num);
        btn_deal_add = (Button) dialog.findViewById(R.id.btn_deal_add);
        text_trade_close_sxf = (TextView) dialog.findViewById(R.id.text_trade_close_sxf);
        text_trade_close_orderprofit = (TextView) dialog.findViewById(R.id.text_trade_close_orderprofit);
        btn_create_help = (Button) dialog.findViewById(R.id.btn_create_help);
        text_trade_close_order_todayprofit = (TextView) dialog.findViewById(R.id.text_trade_close_order_todayprofit);
        btn_submit = (Button) dialog.findViewById(R.id.btn_submit);

        text_close_title.setText(context.getResources().getString(R.string.lable_close_title, tradeOrder.getInstrumentName()));
        if (tradeOrder.getType() == ProductObj.TYPE_BUY_UP) {
            text_buytype.setText(R.string.trade_buy_up);
            text_buytype.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
            text_buynum.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
            btn_submit.setBackgroundResource(R.color.c_EA4A5E);
        } else {
            text_buytype.setText(R.string.trade_buy_down);
            text_buytype.setTextColor(context.getResources().getColor(R.color.c_06A969));
            text_buynum.setTextColor(context.getResources().getColor(R.color.c_06A969));
            btn_submit.setBackgroundResource(R.color.c_06A969);
        }

        text_buynum.setText(context.getResources().getString(R.string.lable_tr_ordernumber, tradeOrder.getPosition()));
        text_trade_order_ccjj.setText(tradeOrder.getHoldAvgPrice());
        if (isSHFE) {//当前是上期所
            if (isCloseToday) {// 平今 最大平仓数为 todayPosition
                maxClosePosition = tradeOrder.getTodayPosition();
            } else {// 平昨 最大平仓数为 ydPosition
                maxClosePosition = tradeOrder.getPosition();
            }
        } else {//其他交易所 最大平仓数为 position
            maxClosePosition = tradeOrder.getPosition();
        }
        text_trade_close_num.setText(context.getResources().getString(R.string.trade_close_num, maxClosePosition));


        text_close_cancle.setOnClickListener(onClickListener);
        btn_create_help.setOnClickListener(onClickListener);
        btn_deal_reduce.setOnClickListener(onClickListener);
        btn_deal_add.setOnClickListener(onClickListener);
        btn_submit.setOnClickListener(onClickListener);

        ed_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.v(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.v(TAG, "onTextChanged");
                setTotalMoney();
                setProfitAndLoss();
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.v(TAG, "afterTextChanged");
            }
        });

        ed_num.setText(maxClosePosition + "");
        //当前是最小手
        ed_num.setSelection(ed_num.getText().toString().length());
        ed_num.clearFocus();
        ed_num.setSelected(false);
        ed_num.setCursorVisible(false);
    }


    public void showDialog(int aniStyle) {
        if (!new UserInfoDao(context).isLogin()) {
            context.showCusToast(context.getString(R.string.login_please));
            return;
        }

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        w.setGravity(Gravity.BOTTOM);
        w.setWindowAnimations(aniStyle);
        dialog.setCancelable(true);

        controlCursor(w);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (refreshUtil != null)
                    refreshUtil.stop();
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
            }
        });
        getProductObj();
    }

    private void controlCursor(Window w){
        final int[] point = new int[2];
        ed_num.getLocationOnScreen(point);
        ed_num.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    ed_num.setSelected(true);
                    ed_num.setCursorVisible(true);
                }
            }
        });
        w.getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,
                                       int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                if (oldBottom != 0 && bottom != 0) {
                    int[] pointNew = new int[2];
                    ed_num.getLocationOnScreen(pointNew);
                    if (point[1] < pointNew[1]) {
                        ed_num.setCursorVisible(false);
                    }else if(point[1] > pointNew[1]){
                        ed_num.setSelection(ed_num.getText().toString().length());
                        ed_num.setSelected(true);
                        ed_num.setCursorVisible(true);
                    }
                    point[0] = pointNew[0];
                    point[1] = pointNew[1];

                }
            }
        });
    }

    void initRefresh() {
        refreshUtil = new RefreshUtil(context);
        refreshUtil.setRefreshTime(AppSetting.getInstance(context).getRefreshTimeWPCloseOrder());
        refreshUtil.setOnRefreshListener(new RefreshUtil.OnRefreshListener() {
            @Override
            public Object doInBackground() {
                try {
                    if (!isShowingDialog())
                        return null;

                    HashMap<String, String> map = new HashMap<>();
                    map.put("instrumentId", tradeOrder.getInstrumentId());
                    String response = HttpClientHelper.getStringFromPost(context,
                            AndroidAPIConfig.URL_TRADE_PRODUCT_DETAIL,
                            ApiConfig.getParamMap(context, map),
                            null);
                    CommonResponse<ProductObj> commonResponse = CommonResponse.fromJson(response, ProductObj.class);
                    productObj = commonResponse.getData();


                } catch (Exception e) {
                    e.printStackTrace();
                }

                return productObj;
            }

            @Override
            public void onUpdate(Object result) {
                if (!isShowingDialog())
                    return;
                updatePrice();
            }
        });
    }

    public void updatePrice() {
        setTotalMoney();
        setProfitAndLoss();
        judgeIsClose();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_submit) {
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.T_CLOSE_SUBMIT);
                confirmBeforeSubmit();
            } else if (id == R.id.text_close_cancle) {
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.T_CLOSE_CANCLE);
                dialog.dismiss();
            } else if (id == R.id.btn_deal_reduce) {
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.T_CLOSE_REDUCE);

                lessAddNum(false);
            } else if (id == R.id.btn_deal_add) {
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.T_CLOSE_PLUS);
                lessAddNum(true);
            } else if (id == R.id.btn_create_help) {
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.T_CLOSE_EXPLAIN);

//                QiHuoExplainWordActivity.startAct(context,
                        DialogUtilForTrade.showQiHuoExplainDlg(context,

                                new String[]{
                                QiHuoExplainWordConfig.CCJJ,
                                QiHuoExplainWordConfig.KSPCJ,
                                QiHuoExplainWordConfig.PCSXF,
                                QiHuoExplainWordConfig.SJYK,
                                QiHuoExplainWordConfig.PCYK});
            }

        }
    };

    /**
     * 提交前二次确认
     */
    void confirmBeforeSubmit() {
        if (productObj == null)
            return;
        String orderNumber = ed_num.getText().toString();
        if (StringUtil.isEmpty(orderNumber)) {
            context.showCusToast(context.getResources().getString(R.string.lable_closeorder_tips));
            return;
        }
        String buyType = "";
        if (tradeOrder.getType() == ProductObj.TYPE_BUY_UP) {
            buyType = context.getResources().getString(R.string.trade_buy_up);
        } else {
            buyType = context.getResources().getString(R.string.trade_buy_down);
        }
        DialogUtil.showTitleAndContentDialog(context,
                "确认平仓",
                context.getResources().getString(R.string.lable_closeorder_contenet, productObj.getInstrumentName(), buyType, orderNumber, text_trade_close_order_todayprofit.getText().toString()),
                context.getResources().getString(R.string.btn_cancle),
                context.getResources().getString(R.string.btn_ok),
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        dialog.dismiss();
                        return false;
                    }
                },
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        submit();
                        return false;
                    }
                });
    }


    /**
     * 平仓提交
     */
    void submit() {

        String orderNumber = ed_num.getText().toString();
//        context.showNetLoadingProgressDialog(null);
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(ProductObj.PARAM_instrumentId, productObj.getInstrumentId());
        if (tradeOrder.getType() == TradeOrder.BUY_UP) {
            map.put(ProductObj.PARAM_PRICE, productObj.getBidPrice1());
        } else {
            map.put(ProductObj.PARAM_PRICE, productObj.getAskPrice1());
        }
        if (isCloseToday) {// 平今才加入此参数
            map.put(ProductObj.PARAM_TODAYPOSITION, tradeOrder.getTodayPosition() + "");
        }
        map.put(ProductObj.PARAM_TYPE, tradeOrder.getType() + "");//
        map.put(ProductObj.PARAM_ORDER_NB, orderNumber + "");//
        map.put(ProductObj.PARAM_HOLDAVGPRICE, tradeOrder.getHoldAvgPrice());//


        HttpClientHelper.doPostOption(context,
                AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_ORDER_CLOSE),
                map,
                null,
                new NetCallback(context) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        //token过期 避免重复弹框
                        if (ApiConfig.isNeedLogin(resultCode)) {
                            //重新登录
//                            showTokenDialog();
                            if (tradeLoginDlg == null) {
                                tradeLoginDlg = new TradeLoginDlg(context);
                            }
                            if (!tradeLoginDlg.isShowingDialog()) {
                                tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                            }
                            return;
                        }
//                        context.showCusToast(ConvertUtil.NVL(resultMsg, context.getString(R.string.trade_create_fail)));
                    }

                    @Override
                    public void onResponse(String response) {
//                        String msg = context.getResources().getString(R.string.trade_close_success);
//                        context.showCusToast(msg);

                        if (createCallback != null) {
                            createCallback.handleMessage(new Message());
                        }
                        dialog.dismiss();
                        createOrderSuccess();

                    }
                }, true);
    }


    ProductObj productObj;

    private void judgeIsClose() {
        if (productObj == null) {
            return;
        }
        if (tradeOrder.getType() == TradeOrder.BUY_UP) {
            text_trade_close_price.setText(productObj.getBidPrice1());
        } else {
            text_trade_close_price.setText(productObj.getAskPrice1());
        }
        if (ProductObj.IS_CLOSE_YES.equals(productObj.getIsClosed())) {
            text_product_isclose.setVisibility(View.VISIBLE);
            btn_submit.setText(R.string.trade_qihuo_closetrade);
            btn_submit.setClickable(false);
            btn_submit.setBackgroundColor(context.getResources().getColor(R.color.c_d1d1d1));
            text_trade_close_price.setText("--");
        } else {
            text_product_isclose.setVisibility(View.GONE);
        }
    }

    /**
     * 获取产品信息
     */
    private void getProductObj() {
        HashMap<String, String> map = new HashMap<>();
        map.put("instrumentId", tradeOrder.getInstrumentId());
        HttpClientHelper.doPostOption(context,
                AndroidAPIConfig.URL_TRADE_PRODUCT_DETAIL,
                map,
                null,
                new NetCallback(context) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        context.showCusToast(ConvertUtil.NVL(resultMsg, context.getString(R.string.network_problem)));
                    }

                    @Override
                    public void onResponse(String response) {
                        CommonResponse<ProductObj> commonResponse = CommonResponse.fromJson(response, ProductObj.class);
                        productObj = commonResponse.getData();
                        if (productObj != null) {
                            //成功了才显示
                            if (!dialog.isShowing())
                                dialog.show();
                            setTotalMoney();
                            setProfitAndLoss();
                            judgeIsClose();
                            if (refreshUtil != null)
                                refreshUtil.start();
                        }

                    }
                },
                true);
    }


    /**
     * 加减号的点击
     *
     * @param isAdd true表示加号
     */
    void lessAddNum(boolean isAdd) {
        String input = ed_num.getText().toString();
        int shouShu = 0;
        if (TextUtils.isEmpty(input)) {

        } else {
            shouShu = Integer.parseInt(input.trim());
        }
        if (isAdd) {
            shouShu += 1;
            if (shouShu >= maxClosePosition) {
                shouShu = maxClosePosition;
            }
        } else {
            shouShu -= 1;
            if (shouShu <= minClosePosition) {
                shouShu = minClosePosition;
            }
        }
        ed_num.setText(String.valueOf(shouShu));
        ed_num.setSelection(ed_num.getText().toString().length());
        ed_num.clearFocus();
        ed_num.setSelected(false);
        ed_num.setCursorVisible(false);
        setTotalMoney();
        setProfitAndLoss();
    }

    /**
     * 显示需要的手续费
     */
    void setTotalMoney() {
        try {
            if (tradeOrder == null)
                return;
            if (productObj == null)
                return;
            if (StringUtil.isEmpty(ed_num.getText().toString()))
                return;
            int shouShu = Integer.parseInt(ed_num.getText().toString());
            if (shouShu <= 0) {
                shouShu = minClosePosition;
            }

            double oneCloseToday = 0;// 平今一手的手续费
            if (TextUtils.isEmpty(productObj.getCloseTodayRatioByMoney()) || Double.parseDouble(productObj.getCloseTodayRatioByMoney()) == 0) {
                // 根据手数算
                oneCloseToday = Double.parseDouble(productObj.getCloseTodayRatioByVolume());
            } else {
                if (tradeOrder.getType() == ProductObj.TYPE_BUY_DOWN) {//买跌
                    oneCloseToday = NumberUtil.multiply(new BigDecimal(productObj.getCloseTodayRatioByMoney()).doubleValue(), new BigDecimal(productObj.getBidPrice1()).doubleValue()) * productObj.getVolumeMultiple();
                } else {//买涨
                    oneCloseToday = NumberUtil.multiply(new BigDecimal(productObj.getCloseTodayRatioByMoney()).doubleValue(), new BigDecimal(productObj.getAskPrice1()).doubleValue()) * productObj.getVolumeMultiple();
                }
            }

            double oneCloseYD = 0;// 平昨一手的手续费
            if (TextUtils.isEmpty(productObj.getCloseTodayRatioByMoney()) || Double.parseDouble(productObj.getCloseTodayRatioByMoney()) == 0) {
                // 根据手数算
                oneCloseYD = Double.parseDouble(productObj.getCloseRatioByVolume());
            } else {
                if (tradeOrder.getType() == ProductObj.TYPE_BUY_DOWN) {//买跌
                    oneCloseYD = NumberUtil.multiply(new BigDecimal(productObj.getCloseRatioByMoney()).doubleValue(), new BigDecimal(productObj.getBidPrice1()).doubleValue()) * productObj.getVolumeMultiple();
                } else {//买涨
                    oneCloseYD = NumberUtil.multiply(new BigDecimal(productObj.getCloseRatioByMoney()).doubleValue(), new BigDecimal(productObj.getAskPrice1()).doubleValue()) * productObj.getVolumeMultiple();
                }
            }

            String totalSXF = "0";
            if (isSHFE) {//当前是上期所
                if (isCloseToday) {// 平今 最大平仓数为 todayPosition
                    totalSXF = NumberUtil.moveLast0(NumberUtil.multiply(oneCloseToday, new BigDecimal(shouShu).doubleValue()));
                } else {// 平昨 最大平仓数为 ydPosition
                    totalSXF = NumberUtil.moveLast0(NumberUtil.multiply(oneCloseYD, new BigDecimal(shouShu).doubleValue()));
                }
            } else {//其他交易所 最大平仓数为 position
                if (shouShu <= tradeOrder.getTodayPosition()) {
                    totalSXF = NumberUtil.moveLast0(NumberUtil.multiply(oneCloseToday, new BigDecimal(shouShu).doubleValue()));
                } else {
                    double todaySXF = NumberUtil.multiply(oneCloseToday, new BigDecimal(tradeOrder.getTodayPosition()).doubleValue());
                    double ydSXF = NumberUtil.multiply(oneCloseYD, new BigDecimal(shouShu - tradeOrder.getTodayPosition()).doubleValue());
                    totalSXF = NumberUtil.moveLast0(todaySXF + ydSXF);
                }
            }
            text_trade_close_sxf.setText(context.getResources().getString(R.string.display_money, StringUtil.forNumber(new BigDecimal(totalSXF).doubleValue())));
        } catch (Exception e) {
            e.printStackTrace();
            text_trade_close_sxf.setText("--");
        }
    }

    /**
     * 计算盈亏
     * 买涨  （快速平仓价(申买价)-持仓均价）/最小变动价位*合约数量乘数*手数
     * 买涨  （快速平仓价(申买价)-建仓仓均价）/最小变动价位*合约数量乘数*手数
     */
    void setProfitAndLoss() {
        try {
            if (tradeOrder == null)
                return;
            if (productObj == null)
                return;
            if (StringUtil.isEmpty(ed_num.getText().toString()))
                return;
            int shouShu = Integer.parseInt(ed_num.getText().toString());
            if (shouShu <= 0) {
                shouShu = minClosePosition;
            }
            double oneProfitAndLossToday = 0;// 一手的平仓盈亏
            double oneProfitAndLossTotal = 0;// 一手的订单实际盈亏

            if (tradeOrder.getType() == ProductObj.TYPE_BUY_DOWN) {//买跌

                oneProfitAndLossToday = NumberUtil.multiply(NumberUtil.divide(NumberUtil.subtract(new BigDecimal(tradeOrder.getHoldAvgPrice()).doubleValue(), new BigDecimal(productObj.getAskPrice1()).doubleValue()),
                        new BigDecimal(productObj.getPriceTick()).doubleValue()),
                        NumberUtil.multiply(new BigDecimal(productObj.getVolumeMultiple()).doubleValue(), new BigDecimal(productObj.getPriceTick()).doubleValue()));


                oneProfitAndLossTotal = NumberUtil.multiply(NumberUtil.divide(NumberUtil.subtract(new BigDecimal(tradeOrder.getOpenAvgPrice()).doubleValue(), new BigDecimal(productObj.getAskPrice1()).doubleValue()),
                        new BigDecimal(productObj.getPriceTick()).doubleValue()),
                        NumberUtil.multiply(new BigDecimal(productObj.getVolumeMultiple()).doubleValue(), new BigDecimal(productObj.getPriceTick()).doubleValue()));

            } else {//买涨

                oneProfitAndLossToday = NumberUtil.multiply(NumberUtil.divide(NumberUtil.subtract(new BigDecimal(productObj.getBidPrice1()).doubleValue(), new BigDecimal(tradeOrder.getHoldAvgPrice()).doubleValue()),
                        new BigDecimal(productObj.getPriceTick()).doubleValue()),
                        NumberUtil.multiply(new BigDecimal(productObj.getVolumeMultiple()).doubleValue(), new BigDecimal(productObj.getPriceTick()).doubleValue()));

                oneProfitAndLossTotal = NumberUtil.multiply(NumberUtil.divide(NumberUtil.subtract(new BigDecimal(productObj.getBidPrice1()).doubleValue(), new BigDecimal(tradeOrder.getOpenAvgPrice()).doubleValue()),
                        new BigDecimal(productObj.getPriceTick()).doubleValue()),
                        NumberUtil.multiply(new BigDecimal(productObj.getVolumeMultiple()).doubleValue(), new BigDecimal(productObj.getPriceTick()).doubleValue()));

            }

            String todayProfit = NumberUtil.moveLast0(NumberUtil.multiply(oneProfitAndLossToday, new BigDecimal(shouShu).doubleValue()));
            String totalProfit = NumberUtil.moveLast0(NumberUtil.multiply(oneProfitAndLossTotal, new BigDecimal(shouShu).doubleValue()));
            if(new BigDecimal(totalProfit).doubleValue()>0){
                text_trade_close_orderprofit.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
                text_trade_close_orderprofit.setText("+"+StringUtil.forNumber(new BigDecimal(totalProfit).doubleValue()));

            }else{
                text_trade_close_orderprofit.setTextColor(context.getResources().getColor(R.color.c_06A969));
                text_trade_close_orderprofit.setText(StringUtil.forNumber(new BigDecimal(totalProfit).doubleValue()));
            }


            if (ProductObj.IS_CLOSE_YES.equals(productObj.getIsClosed())) {
                text_trade_close_order_todayprofit.setText("--");
            }else{
                if (new BigDecimal(todayProfit).doubleValue() > 0) {
                    text_trade_close_order_todayprofit.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
                    text_trade_close_order_todayprofit.setText("+" + StringUtil.forNumber(new BigDecimal(todayProfit).doubleValue()));
                } else {
                    text_trade_close_order_todayprofit.setTextColor(context.getResources().getColor(R.color.c_06A969));
                    text_trade_close_order_todayprofit.setText(StringUtil.forNumber(new BigDecimal(todayProfit).doubleValue()));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            text_trade_close_orderprofit.setText("--");
            text_trade_close_order_todayprofit.setText("--");
        }
    }


    /*建仓成功的callback*/
    Handler.Callback createCallback;

    public Handler.Callback getCreateCallback() {
        return createCallback;
    }

    public void setCreateCallback(Handler.Callback createCallback) {
        this.createCallback = createCallback;
    }

    /**
     * 建仓成功
     * 1  平仓按钮的状态显示
     * 2  绘制持仓辅助线
     */
    public void createOrderSuccess() {
        EventBus.getDefault().post(new TradeOrderOptionEvent(TradeOrderOptionEvent.OPTION_CLOSESUCCESS));
    }
}
