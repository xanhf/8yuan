package com.trade.eight.moudle.trade.close;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
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
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.config.QiHuoExplainWordConfig;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.response.NettyResponse;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.outterapp.QiHuoExplainWordActivity;
import com.trade.eight.moudle.product.OptionalEvent;
import com.trade.eight.moudle.trade.TradeCloseOptionEvent;
import com.trade.eight.moudle.trade.TradeOrderOptionEvent;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.moudle.trade.UpdateTradeUserInfoEvent;
import com.trade.eight.moudle.trade.activity.DialogUtilForTrade;
import com.trade.eight.moudle.trade.adapter.TradeClose4LiveAdapter;
import com.trade.eight.moudle.trade.adapter.TradeClose4ProductAdapter;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.Utils;
import com.trade.eight.tools.refresh.RefreshUtil;
import com.trade.eight.tools.trade.TradeConfig;
import com.trade.eight.tools.trade.TradeSortUtil;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;
import com.trade.eight.view.trade.TradeHeadView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 平仓------产品页
 * <p>
 * 上期所 平仓手续费分为平今  平昨  不用混合判断
 * 其他交易默认平今,所以平仓手续费
 * 买涨  （快速平仓价(申买价)-持仓均价）/最小变动价位*合约数量乘数*手数
 * 买涨  （快速平仓价(申买价)-建仓仓均价）/最小变动价位*合约数量乘数*手数
 * <p>
 * * 手数*longMarginRatioByMoney *volumeMultiple*行情价格
 * 手数*longMarginRatioByVolume
 * <p>
 * <p>
 * 如果菜单展开就刷产品   菜单合上就刷
 */

public class TradeCloseLiveDlg {
    public static final String TAG = "TradeCloseProductDlg";

    BaseActivity context;
    Dialog dialog;

    View headView = null;
    TextView text_close_title;
    TextView text_close_cancle;
    TextView text_product_isclose;
    PullToRefreshListView mPullRefreshListView;
    ListView listView;
    Button btn_create_help;
    TextView text_trade_close_order_todayprofit;
    Button btn_submit;

    TradeLoginDlg tradeLoginDlg;

    private ProductObj productObj;
    List<TradeOrder> listTradeOrderForProduct;
    TradeClose4LiveAdapter tradeClose4LiveAdapter;

    TradeHeadView tradeHeadView;

    //    boolean isExpandMenu;
    View optionView;// 被操作的view
    TradeOrder optionTradeOrder;//被操作的订单

    WindowManager.LayoutParams params;

    RefreshUtil refreshUtil;
    private boolean isExpandMenu = false;
    List<ProductObj> optionalList = null;


    private int listViewHeight = 0;

    List<TradeOrder> orderList;

    // 软键盘是否弹出  弹出就不刷新列表
    private boolean isShowSoftBord = false;

    public TradeCloseLiveDlg(BaseActivity context) {
        this.context = context;
        dialog = new Dialog(context, R.style.dialog_trade);
        View rootView = View.inflate(context, R.layout.fx_close_livedlg, null);
        dialog.setContentView(rootView);
        findViews(dialog);
        //  停用刷新 因为有长连接行情过来
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

        mPullRefreshListView = (PullToRefreshListView) dialog.findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setPullRefreshEnabled(false);
        mPullRefreshListView.setPullLoadEnabled(false);
        listView = mPullRefreshListView.getRefreshableView();
        listView.setDividerHeight(0);

        headView = View.inflate(context, R.layout.head_close_live, null);
        listView.addHeaderView(headView);

        tradeHeadView = (TradeHeadView) headView.findViewById(R.id.trade_view_liveclose);
        tradeHeadView.setBaseActivity(context);

        tradeClose4LiveAdapter = new TradeClose4LiveAdapter(context, 0, new ArrayList<TradeOrder>());
        listView.setAdapter(tradeClose4LiveAdapter);

        btn_create_help = (Button) dialog.findViewById(R.id.btn_create_help);
        text_trade_close_order_todayprofit = (TextView) dialog.findViewById(R.id.text_trade_close_order_todayprofit);
        btn_submit = (Button) dialog.findViewById(R.id.btn_submit);

        text_close_cancle.setOnClickListener(onClickListener);
        btn_create_help.setOnClickListener(onClickListener);
        btn_submit.setOnClickListener(onClickListener);
    }


    public void showDialog(int aniStyle) {
        if (!new UserInfoDao(context).isLogin()) {
            context.showCusToast(context.getString(R.string.login_please));
            return;
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        Window w = dialog.getWindow();
        params = w.getAttributes();
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
                listViewHeight = 0;
                EventBus.getDefault().unregister(this);
                if (refreshUtil != null)
                    refreshUtil.stop();
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                if (listViewHeight == 0) {
                    listViewHeight = getListviewHeight();
                }

                ViewGroup.LayoutParams listParams = listView.getLayoutParams();
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
                int totalHeight = listViewHeight + Utils.dip2px(context, 320);
                if (totalHeight > (Utils.getScreenH(context) - Utils.getStatusHeight(context))) {
                    totalHeight = (int) (Utils.getScreenH(context) * 0.9);
                }
                p.height = totalHeight;//
                dialogWindow.setAttributes(p);
                listParams.height = listViewHeight;
                listParams.height = totalHeight - Utils.dip2px(context, 115);
                listView.setLayoutParams(listParams);


            }
        });

        getOrderList();
    }

    void initRefresh() {
        refreshUtil = new RefreshUtil(context);
        refreshUtil.setRefreshTime(AppSetting.getInstance(context).getRefreshTimeWPHoldOrder());
        refreshUtil.setOnRefreshListener(new RefreshUtil.OnRefreshListener() {
            @Override
            public Object doInBackground() {
                try {
                    CommonResponse4List<TradeOrder> response4List = TradeHelp.getTradeOrderList(context);
                    if (response4List != null && response4List.isSuccess()) {
                        orderList = response4List.getData();
                        return orderList;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onUpdate(Object result) {
                if (orderList != null && orderList.size() > 0) {
                    if (!isShowSoftBord) {
                        tradeClose4LiveAdapter.setData(orderList);
                    }
                }
            }
        });
    }

    /**
     * 获取一次产品列表
     */
    class GetDataPriceTask extends AsyncTask<String, Void, CommonResponse4List<ProductObj>> {

        @Override
        protected CommonResponse4List<ProductObj> doInBackground(String... params) {
            //get list data
            CommonResponse4List<ProductObj> response4List = TradeHelp.getFxPList(context);
            return response4List;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(CommonResponse4List<ProductObj> result) {
            // TODO Auto-generated method stub

            super.onPostExecute(result);
            context.hideNetLoadingProgressDialog();
            if (result != null && result.isSuccess()) {
                optionalList = result.getData();
            } else {

            }

        }
    }

    /**
     * netty连接成功后收到的广播
     *
     * @param event
     */
    public void onEventMainThread(final OptionalEvent event) {
        if (!isShowingDialog()) {
            return;
        }
        if (productObj == null)
            return;
        if (event == null)
            return;

        NettyResponse<Optional> response = event.getNettyResponse();
        if (response == null)
            return;
        Optional o = response.getData();
        if (o == null)
            return;
        //推送过来的数据是当前的品种
        if (productObj.getExcode() != null
                && productObj.getInstrumentId() != null
                && productObj.getExcode().equals(o.getExchangeID())
                && productObj.getInstrumentId().equals(o.getInstrumentID())) {

            productObj.setBidPrice1(o.getBidPrice1());
            productObj.setAskPrice1(o.getAskPrice1());

            setTotalMoney();
            setProfitAndLoss();
            judgeIsClose();
        }
    }

    private void getOrderList() {

        String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_ORDER_HOLD_LIST);
        HttpClientHelper.doPostOption(context,
                url,
                null,
                null,
                new NetCallback(context) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {

                        if (ApiConfig.isNeedLogin(resultCode)) {
                            //重新登录
//                            showTokenDialog();
                            if (context == null)
                                return;
                            if (context.isFinishing())
                                return;

                            if (tradeLoginDlg == null) {
                                tradeLoginDlg = new TradeLoginDlg(context);
                            }
                            if (!tradeLoginDlg.isShowingDialog()) {
                                tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                            }
                            return;
                        }
                        resultMsg = ConvertUtil.NVL(resultMsg, context.getResources().getString(R.string.network_problem));
                        context.showCusToast(resultMsg);

                    }

                    @Override
                    public void onResponse(String response) {
                        BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
                        mPullRefreshListView.onPullUpRefreshComplete();
                        mPullRefreshListView.onPullDownRefreshComplete();
                        CommonResponse4List<TradeOrder> commonResponse4List = CommonResponse4List.fromJson(response, TradeOrder.class);
                        orderList = commonResponse4List.getData();

                        if (orderList != null && orderList.size() > 0) {
                            tradeClose4LiveAdapter.setData(orderList);
                            getTradeInfoData();
                            // 初始化产品列表
                            new GetDataPriceTask().execute();

                            if (refreshUtil != null) {
                                refreshUtil.start();
                            }
                        } else {
                            context.showCusToast("暂无持仓");
                        }
                    }
                }, true);
    }


    private void getTradeInfoData() {

        TradeInfoData tradeInfoData = TradeUserInfoData4Situation.getInstance(context, null).getTradeInfoData();
        if (tradeInfoData != null) {
            //成功了才显示
            if (!dialog.isShowing())
                dialog.show();
            tradeHeadView.displayView(tradeInfoData);
        } else {
            TradeUserInfoData4Situation.getInstance(context, null).loadTradeOrderAndUserInfoData(new TradeUserInfoData4Situation.GetDataCallback() {
                @Override
                public void onFailure(String resultCode, String resultMsg) {
                    context.showCusToast(resultMsg);
                }

                @Override
                public void onResponse(TradeInfoData tradeInfoData) {
                    //成功了才显示
                    if (!dialog.isShowing())
                        dialog.show();
                    tradeHeadView.displayView(tradeInfoData);
                }
            }, false);
        }

    }

    TextView text_trade_close_price;
    TextView text_trade_close_num;
    Button btn_deal_reduce;
    EditText ed_num;
    Button btn_deal_add;
    TextView text_trade_close_sxf;
    TextView text_trade_close_orderprofit;
    private int maxClosePosition = 1;
    private int minClosePosition = 1;//最小平仓手数  固定为1
    private boolean isSHFE = false;// 是否为上期所
    private boolean isCloseToday = false;//是否为上期所平今天的仓 当此参数为true  不用考虑isSHFE


    /**
     * 头部刷新
     *
     * @param updateTradeUserInfoEvent
     */
    public void onEventMainThread(UpdateTradeUserInfoEvent updateTradeUserInfoEvent) {
        TradeInfoData tradeInfoData = updateTradeUserInfoEvent.tradeInfoData;
//        if (tradeHeadView != null) {
        tradeHeadView.displayView(tradeInfoData);
//        }
    }

    /**
     * 平仓面板适配器内的操作
     *
     * @param tradeCloseOptionEvent
     */
    public void onEventMainThread(TradeCloseOptionEvent tradeCloseOptionEvent) {
        Log.e(TAG, "onEventMainThread======");
        int optionTye = tradeCloseOptionEvent.option_type;
        if (optionTye == TradeCloseOptionEvent.OPTION_EXPANDMENU) {
            isExpandMenu = tradeCloseOptionEvent.isExpandMenu;
            if (isExpandMenu) {
                optionView = tradeCloseOptionEvent.optionView;
                optionTradeOrder = tradeCloseOptionEvent.optionTradeOrder;
                getProductObj();

                if (listViewHeight == 0) {
                    listViewHeight = getListviewHeight();
                }
                int listViewActHeight = listViewHeight + Utils.dip2px(context, 208);

                ViewGroup.LayoutParams listParams = listView.getLayoutParams();
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
                int totalHeight = listViewActHeight + Utils.dip2px(context, 320);
                if (totalHeight > (Utils.getScreenH(context) - Utils.getStatusHeight(context))) {
                    totalHeight = (int) (Utils.getScreenH(context) * 0.9);
                }
                p.height = totalHeight;//
                dialogWindow.setAttributes(p);
                listParams.height = listViewActHeight;
                listParams.height = totalHeight - Utils.dip2px(context, 115);
                listView.setLayoutParams(listParams);

                initView(optionView);
                setTotalMoney();
                setProfitAndLoss();
                judgeIsClose();
//
            } else {
                optionView = null;
                optionTradeOrder = null;
                text_trade_close_order_todayprofit.setText("--");

                if (listViewHeight == 0) {
                    listViewHeight = getListviewHeight();
                }
                ViewGroup.LayoutParams listParams = listView.getLayoutParams();
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
                int totalHeight = listViewHeight + Utils.dip2px(context, 320);
                if (totalHeight > (Utils.getScreenH(context) - Utils.getStatusHeight(context))) {
                    totalHeight = (int) (Utils.getScreenH(context) * 0.9);
                }
                p.height = totalHeight;//
                dialogWindow.setAttributes(p);
                listParams.height = totalHeight - Utils.dip2px(context, 115);
                listView.setLayoutParams(listParams);
            }
        } else {

        }

    }

    /**
     * 获取listview 高度
     *
     * @return
     */
    private int getListviewHeight() {
        int totalHeight = 0;
        for (int i = 0; i < tradeClose4LiveAdapter.getCount(); i++) {
            View listItem = tradeClose4LiveAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        int height = totalHeight + (listView.getDividerHeight() * (tradeClose4LiveAdapter.getCount() - 1));
        Log.e(TAG, "height======" + height);
        return height;
    }

    private void initView(View view) {
        if (view == null) {
            return;
        }
        if (optionTradeOrder.getExcode().equals(TradeConfig.code_SHFE)) {
            isSHFE = true;
        } else {
            isSHFE = false;
        }

        if (isSHFE) {
            if (optionTradeOrder.getTodayPosition() > 0) {
                isCloseToday = true;
            } else {
                isCloseToday = false;
            }
        } else {
            isCloseToday = false;
        }
        text_trade_close_price = (TextView) view.findViewById(R.id.text_trade_close_price);
        text_trade_close_num = (TextView) view.findViewById(R.id.text_trade_close_num);
        btn_deal_reduce = (Button) view.findViewById(R.id.btn_deal_reduce);
        ed_num = (EditText) view.findViewById(R.id.ed_num);
        btn_deal_add = (Button) view.findViewById(R.id.btn_deal_add);
        text_trade_close_sxf = (TextView) view.findViewById(R.id.text_trade_close_sxf);
        text_trade_close_orderprofit = (TextView) view.findViewById(R.id.text_trade_close_orderprofit);


        btn_deal_reduce.setOnClickListener(onClickListener);
        btn_deal_add.setOnClickListener(onClickListener);
        btn_submit.setOnClickListener(onClickListener);

        if (isSHFE) {//当前是上期所
            if (isCloseToday) {// 平今 最大平仓数为 todayPosition
                maxClosePosition = optionTradeOrder.getPosition();
            } else {// 平昨 最大平仓数为 ydPosition
                maxClosePosition = optionTradeOrder.getYdPosition();
            }
        } else {//其他交易所 最大平仓数为 position
            maxClosePosition = optionTradeOrder.getPosition();
        }
        text_trade_close_num.setText(context.getResources().getString(R.string.trade_close_num, maxClosePosition));
//        text_trade_close_orderprofit.setText(StringUtil.forNumber(new BigDecimal(optionTradeOrder.getTotalProfit()).doubleValue()));
//        if (new BigDecimal(ConvertUtil.NVL(optionTradeOrder.getTotalProfit(), "0")).doubleValue() > 0) {
//            text_trade_close_orderprofit.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
//        } else {
//            text_trade_close_orderprofit.setTextColor(context.getResources().getColor(R.color.c_06A969));
//        }
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
//        ed_num.setFocusable(true);
//        ed_num.setSelected(true);
//        ed_num.setCursorVisible(true);
        ed_num.clearFocus();
        ed_num.setSelected(false);
        ed_num.setCursorVisible(false);

        controlCursor(dialog.getWindow());

        setTotalMoney();
        setProfitAndLoss();
    }

    private void controlCursor(Window w) {
        if (ed_num == null) {
            return;
        }
        final int[] point = new int[2];
        ed_num.getLocationOnScreen(point);
        ed_num.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
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
                        isShowSoftBord = false;
                    } else if (point[1] > pointNew[1]) {
                        ed_num.setSelection(ed_num.getText().toString().length());
                        ed_num.setSelected(true);
                        ed_num.setCursorVisible(true);
                        isShowSoftBord = true;
                    }
                    point[0] = pointNew[0];
                    point[1] = pointNew[1];

                }
            }
        });
    }


    public void updatePrice(Optional optional) {
        productObj.setBidPrice1(optional.getBidPrice1());
        productObj.setAskPrice1(optional.getAskPrice1());
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

                        new String[]{QiHuoExplainWordConfig.KSPCJ,
                                QiHuoExplainWordConfig.PCSXF,
                                QiHuoExplainWordConfig.SJYK,
                                QiHuoExplainWordConfig.PCYK,
                                QiHuoExplainWordConfig.CCJJ,
                                QiHuoExplainWordConfig.CCYK});
            }

        }
    };

    /**
     * 提交前二次确认
     */
    void confirmBeforeSubmit() {
        if (optionTradeOrder == null)
            return;
        if (productObj == null)
            return;
        if (optionView == null) {
            return;
        }
        String orderNumber = ed_num.getText().toString();
        if (StringUtil.isEmpty(orderNumber)) {
            context.showCusToast(context.getResources().getString(R.string.lable_closeorder_tips));
            return;
        }
        String buyType = "";
        if (optionTradeOrder.getType() == ProductObj.TYPE_BUY_UP) {
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
        if (optionTradeOrder.getType() == TradeOrder.BUY_UP) {
            map.put(ProductObj.PARAM_PRICE, productObj.getBidPrice1());
        } else {
            map.put(ProductObj.PARAM_PRICE, productObj.getAskPrice1());
        }
        if (isCloseToday) {// 平今才加入此参数
            map.put(ProductObj.PARAM_TODAYPOSITION, optionTradeOrder.getTodayPosition() + "");
        }
        map.put(ProductObj.PARAM_TYPE, optionTradeOrder.getType() + "");//
        map.put(ProductObj.PARAM_ORDER_NB, orderNumber + "");//
        map.put(ProductObj.PARAM_HOLDAVGPRICE, optionTradeOrder.getHoldAvgPrice());//


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
//                        createCallback.handleMessage(new Message());
                        dialog.dismiss();
                        createOrderSuccess();

                    }
                }, true);
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

    private void judgeIsClose() {
        if (productObj == null) {
            return;
        }
        if (optionView != null) {
            if (optionTradeOrder.getType() == TradeOrder.BUY_UP) {
                text_trade_close_price.setText(productObj.getBidPrice1());
            } else {
                text_trade_close_price.setText(productObj.getAskPrice1());
            }
        }
        if (ProductObj.IS_CLOSE_YES.equals(productObj.getIsClosed())) {
            text_product_isclose.setVisibility(View.GONE);
            btn_submit.setText(R.string.trade_qihuo_closetrade);
            btn_submit.setClickable(false);
            btn_submit.setBackgroundColor(context.getResources().getColor(R.color.c_d1d1d1));
            if (optionView != null) {
                text_trade_close_price.setText("--");
            }
        } else {
            text_product_isclose.setVisibility(View.GONE);
            setViewBG();
        }
    }

    /**
     * 改变背景色
     */
    void setViewBG() {

        btn_submit.setText(R.string.trade_close_speed);
        if (optionTradeOrder.getType() == ProductObj.TYPE_BUY_UP) {
            if (!ProductObj.IS_CLOSE_YES.equals(productObj.getIsClosed())) {
                btn_submit.setBackgroundResource(R.drawable.qucik_trade_submit);
            }

        } else {
            if (!ProductObj.IS_CLOSE_YES.equals(productObj.getIsClosed())) {
                btn_submit.setBackgroundResource(R.drawable.qucik_trade_submit_green);
            }
        }
    }

    /**
     * 显示需要的手续费
     */
    void setTotalMoney() {
        try {
            if (optionTradeOrder == null)
                return;
            if (productObj == null)
                return;
            if (optionView == null) {
                return;
            }
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
                if (optionTradeOrder.getType() == ProductObj.TYPE_BUY_DOWN) {//买跌
                    oneCloseToday = NumberUtil.multiply(new BigDecimal(productObj.getCloseTodayRatioByMoney()).doubleValue(), new BigDecimal(productObj.getBidPrice1()).doubleValue()) * productObj.getVolumeMultiple();
                } else {//买涨
                    oneCloseToday = NumberUtil.multiply(new BigDecimal(productObj.getCloseTodayRatioByMoney()).doubleValue(), new BigDecimal(productObj.getAskPrice1()).doubleValue()) * productObj.getVolumeMultiple();
                }
            }

            double oneCloseYD = 0;// 平今一手的手续费
            if (TextUtils.isEmpty(productObj.getCloseTodayRatioByMoney()) || Double.parseDouble(productObj.getCloseTodayRatioByMoney()) == 0) {
                // 根据手数算
                oneCloseYD = Double.parseDouble(productObj.getCloseRatioByVolume());
            } else {
                if (optionTradeOrder.getType() == ProductObj.TYPE_BUY_DOWN) {//买跌
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
                if (shouShu <= optionTradeOrder.getTodayPosition()) {
                    totalSXF = NumberUtil.moveLast0(NumberUtil.multiply(oneCloseToday, new BigDecimal(shouShu).doubleValue()));
                } else {
                    double todaySXF = NumberUtil.multiply(oneCloseToday, new BigDecimal(optionTradeOrder.getTodayPosition()).doubleValue());
                    double ydSXF = NumberUtil.multiply(oneCloseYD, new BigDecimal(shouShu - optionTradeOrder.getTodayPosition()).doubleValue());
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
            if (optionTradeOrder == null)
                return;
            if (productObj == null)
                return;
            if (optionView == null) {
                return;
            }
            if (StringUtil.isEmpty(ed_num.getText().toString()))
                return;
            int shouShu = Integer.parseInt(ed_num.getText().toString());
            if (shouShu <= 0) {
                shouShu = minClosePosition;
            }
            double oneProfitAndLossToday = 0;// 一手的平仓盈亏
            double oneProfitAndLossTotal = 0;// 一手的订单实际盈亏

            if (optionTradeOrder.getType() == ProductObj.TYPE_BUY_DOWN) {//买跌

                oneProfitAndLossToday = NumberUtil.multiply(NumberUtil.divide(NumberUtil.subtract(new BigDecimal(optionTradeOrder.getHoldAvgPrice()).doubleValue(), new BigDecimal(productObj.getAskPrice1()).doubleValue()),
                        new BigDecimal(productObj.getPriceTick()).doubleValue()),
                        NumberUtil.multiply(new BigDecimal(productObj.getVolumeMultiple()).doubleValue(), new BigDecimal(productObj.getPriceTick()).doubleValue()));


                oneProfitAndLossTotal = NumberUtil.multiply(NumberUtil.divide(NumberUtil.subtract(new BigDecimal(optionTradeOrder.getOpenAvgPrice()).doubleValue(), new BigDecimal(productObj.getAskPrice1()).doubleValue()),
                        new BigDecimal(productObj.getPriceTick()).doubleValue()),
                        NumberUtil.multiply(new BigDecimal(productObj.getVolumeMultiple()).doubleValue(), new BigDecimal(productObj.getPriceTick()).doubleValue()));

            } else {//买涨

                oneProfitAndLossToday = NumberUtil.multiply(NumberUtil.divide(NumberUtil.subtract(new BigDecimal(productObj.getBidPrice1()).doubleValue(), new BigDecimal(optionTradeOrder.getHoldAvgPrice()).doubleValue()),
                        new BigDecimal(productObj.getPriceTick()).doubleValue()),
                        NumberUtil.multiply(new BigDecimal(productObj.getVolumeMultiple()).doubleValue(), new BigDecimal(productObj.getPriceTick()).doubleValue()));

                oneProfitAndLossTotal = NumberUtil.multiply(NumberUtil.divide(NumberUtil.subtract(new BigDecimal(productObj.getBidPrice1()).doubleValue(), new BigDecimal(optionTradeOrder.getOpenAvgPrice()).doubleValue()),
                        new BigDecimal(productObj.getPriceTick()).doubleValue()),
                        NumberUtil.multiply(new BigDecimal(productObj.getVolumeMultiple()).doubleValue(), new BigDecimal(productObj.getPriceTick()).doubleValue()));

            }

            String todayProfit = NumberUtil.moveLast0(NumberUtil.multiply(oneProfitAndLossToday, new BigDecimal(shouShu).doubleValue()));
            String totalProfit = NumberUtil.moveLast0(NumberUtil.multiply(oneProfitAndLossTotal, new BigDecimal(shouShu).doubleValue()));
            if (new BigDecimal(ConvertUtil.NVL(totalProfit, "0")).doubleValue() > 0) {
                text_trade_close_orderprofit.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
                text_trade_close_orderprofit.setText("+" + StringUtil.forNumber(new BigDecimal(totalProfit).doubleValue()));

            } else {
                text_trade_close_orderprofit.setTextColor(context.getResources().getColor(R.color.c_06A969));
                text_trade_close_orderprofit.setText(StringUtil.forNumber(new BigDecimal(totalProfit).doubleValue()));

            }
            if (new BigDecimal(ConvertUtil.NVL(todayProfit, "0")).doubleValue() > 0) {
                text_trade_close_order_todayprofit.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
                text_trade_close_order_todayprofit.setText("+" + StringUtil.forNumber(new BigDecimal(todayProfit).doubleValue()));
            } else {
                text_trade_close_orderprofit.setTextColor(context.getResources().getColor(R.color.c_06A969));
                text_trade_close_orderprofit.setText(StringUtil.forNumber(new BigDecimal(totalProfit).doubleValue()));

            }


            if (ProductObj.IS_CLOSE_YES.equals(productObj.getIsClosed())) {
                text_trade_close_order_todayprofit.setText("--");
            } else {
                if (new BigDecimal(ConvertUtil.NVL(todayProfit, "0")).doubleValue() > 0) {
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

    /**
     * 获取选中的产品信息
     */
    private void getProductObj() {

        if (optionalList == null) {
            return;
        }
        if (optionTradeOrder == null) {
            return;
        }
        for (ProductObj pro : optionalList) {
            if (pro.getExcode().equals(optionTradeOrder.getExcode()) && pro.getInstrumentId().equals(optionTradeOrder.getInstrumentId())) {
                productObj = pro;
                break;
            }
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
