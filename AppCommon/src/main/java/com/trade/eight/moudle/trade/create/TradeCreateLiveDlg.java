package com.trade.eight.moudle.trade.create;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AppSetting;
import com.trade.eight.config.QiHuoExplainWordConfig;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.response.NettyResponse;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.me.activity.CashInAndOutAct;
import com.trade.eight.moudle.product.OptionalEvent;
import com.trade.eight.moudle.trade.TradeCreateLiveChooseProEevent;
import com.trade.eight.moudle.trade.TradeOrderOptionEvent;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.moudle.trade.activity.DialogUtilForTrade;
import com.trade.eight.moudle.trade.adapter.LiveCreateProductAdapter;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
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
import com.trade.eight.tools.refresh.RefreshUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
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
 * 通过刷新产品列表来更新行情
 */

public class TradeCreateLiveDlg {
    public static final String TAG = "TradeCreateLiveDlg";
    /*建仓的保留手数的小数点 */
    public static final int NUM_POINT = 2;

    /*购买方向*/
    int typeBuy = ProductObj.TYPE_BUY_UP;
    /*选择的产品*/
    ProductObj productObj;
    BaseActivity context;
    Dialog dialog;
    TextView text_create_cancle;

    RecyclerView recycler_livetrade;
    LinearLayoutManager linearLayoutManager;

    TextView text_create_tips;
    View upView, downView, selectUpOrDownV;
    TextView tv_byuprate;
    TextView tv_bydownrate;
    View view_buyup_empty;
    View view_buyup;
    TextView text_buyup_percent;
    View view_buydown;
    View view_buydown_empty;
    TextView text_buydown_percent;
    Button btn_deal_reduce;
    EditText ed_num;
    Button btn_deal_add;
    TextView text_create_availmoney;
    Button btn_create_help;
    TextView text_gocashin;
    TextView text_create_bzj;
    TextView text_create_sxf;
    TextView text_create_total;
    Button btn_submit;
    // 是否展示过资金不足的对话框
    boolean isShowJudgeDialog = false;
    TradeLoginDlg tradeLoginDlg;

    LiveCreateProductAdapter liveCreateProductAdapter;
    RefreshUtil refreshUtil;


    public TradeCreateLiveDlg(BaseActivity context) {
        this.context = context;

        dialog = new Dialog(context, R.style.dialog_trade);
        View rootView = View.inflate(context, R.layout.fx_create_live_dlg, null);
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

        text_create_cancle = (TextView) dialog.findViewById(R.id.text_create_cancle);
        recycler_livetrade = (RecyclerView) dialog.findViewById(R.id.recycler_livetrade);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler_livetrade.setLayoutManager(linearLayoutManager);

        liveCreateProductAdapter = new LiveCreateProductAdapter(new ArrayList<ProductObj>());
        recycler_livetrade.setAdapter(liveCreateProductAdapter);

        text_create_tips = (TextView) dialog.findViewById(R.id.text_create_tips);
        upView = dialog.findViewById(R.id.upView);
        downView = dialog.findViewById(R.id.downView);
        tv_byuprate = (TextView) dialog.findViewById(R.id.tv_byuprate);
        tv_bydownrate = (TextView) dialog.findViewById(R.id.tv_bydownrate);
        view_buyup_empty = dialog.findViewById(R.id.view_buyup_empty);
        view_buyup = dialog.findViewById(R.id.view_buyup);
        text_buyup_percent = (TextView) dialog.findViewById(R.id.text_buyup_percent);
        view_buydown = dialog.findViewById(R.id.view_buydown);
        view_buydown_empty = dialog.findViewById(R.id.view_buydown_empty);
        text_buydown_percent = (TextView) dialog.findViewById(R.id.text_buydown_percent);
        btn_deal_reduce = (Button) dialog.findViewById(R.id.btn_deal_reduce);
        ed_num = (EditText) dialog.findViewById(R.id.ed_num);
        btn_deal_add = (Button) dialog.findViewById(R.id.btn_deal_add);
        text_create_availmoney = (TextView) dialog.findViewById(R.id.text_create_availmoney);
        btn_create_help = (Button) dialog.findViewById(R.id.btn_create_help);
        text_gocashin = (TextView) dialog.findViewById(R.id.text_gocashin);
        text_create_bzj = (TextView) dialog.findViewById(R.id.text_create_bzj);
        text_create_sxf = (TextView) dialog.findViewById(R.id.text_create_sxf);
        text_create_total = (TextView) dialog.findViewById(R.id.text_create_total);
        btn_submit = (Button) dialog.findViewById(R.id.btn_submit);


        text_create_cancle.setOnClickListener(onClickListener);
        upView.setOnClickListener(onClickListener);
        downView.setOnClickListener(onClickListener);
        btn_create_help.setOnClickListener(onClickListener);
        btn_deal_reduce.setOnClickListener(onClickListener);
        btn_deal_add.setOnClickListener(onClickListener);
        btn_submit.setOnClickListener(onClickListener);
        text_gocashin.setOnClickListener(onClickListener);
    }

    private void displayView() {
        if (typeBuy == ProductObj.TYPE_BUY_UP) {
            selectUpOrDownV = upView;
        } else {
            selectUpOrDownV = downView;
        }
        selectUpOrDownV.setSelected(true);
        tv_byuprate.setText(productObj.getAskPrice1());
        tv_bydownrate.setText(productObj.getBidPrice1());

        text_buyup_percent.setText(productObj.getLongRate() + "%");
        text_buydown_percent.setText(productObj.getShortRate() + "%");
        int up = Integer.parseInt(productObj.getLongRate());
        int down = 100 - up;
        LinearLayout.LayoutParams view_buyup_empty_lp = new LinearLayout.LayoutParams(0, Utils.dip2px(context, 3), down);
        view_buyup_empty.setLayoutParams(view_buyup_empty_lp);
        LinearLayout.LayoutParams view_buyup_lp = new LinearLayout.LayoutParams(0, Utils.dip2px(context, 3), up);
        view_buyup.setLayoutParams(view_buyup_lp);
        LinearLayout.LayoutParams view_buydown_empty_lp = new LinearLayout.LayoutParams(0, Utils.dip2px(context, 3), up);
        view_buydown_empty.setLayoutParams(view_buydown_empty_lp);
        LinearLayout.LayoutParams view_buydown_lp = new LinearLayout.LayoutParams(0, Utils.dip2px(context, 3), down);
        view_buydown.setLayoutParams(view_buydown_lp);

        // 设置控件的背景
        setViewBG(typeBuy == ProductObj.TYPE_BUY_UP);
        if (ProductObj.IS_CLOSE_YES.equals(productObj.getIsClosed())) {
            btn_submit.setText(R.string.trade_qihuo_closetrade);
            btn_submit.setClickable(false);
            btn_submit.setBackgroundColor(context.getResources().getColor(R.color.c_d1d1d1));
        }else{
            btn_submit.setText(R.string.dlg_create);
            btn_submit.setClickable(true);

        }

        ed_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.v(TAG, "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.v(TAG, "onTextChanged");
                setTotalMoney();
                ykTips();
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.v(TAG, "afterTextChanged");
            }
        });

        ed_num.setText(NumberUtil.moveLast0(productObj.getMinSl()));
        //当前是最小手
        ed_num.setSelection(ed_num.getText().toString().length());
        ed_num.clearFocus();
        ed_num.setSelected(false);
        ed_num.setCursorVisible(false);
        ykTips();
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
                EventBus.getDefault().unregister(this);
                if (refreshUtil != null)
                    refreshUtil.stop();
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
            }
        });
        new GetDataPriceTask().execute();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
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

    List<ProductObj> optionalList = null;

    /**
     * 获取数据
     */
    CommonResponse4List<ProductObj> getData() {
        try {
            CommonResponse4List<ProductObj> response4List = TradeHelp.getFxPList(context);
            if (response4List != null && response4List.isSuccess()) {
                optionalList = response4List.getData();
            }
            return response4List;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    void initRefresh() {
        refreshUtil = new RefreshUtil(context);
        refreshUtil.setRefreshTime(AppSetting.getInstance(context).getRefreshTimeWPList());
        refreshUtil.setOnRefreshListener(new RefreshUtil.OnRefreshListener() {
            @Override
            public Object doInBackground() {
                try {
                    CommonResponse4List<ProductObj> response4List = TradeHelp.getFxPList(context);
                    if (response4List != null && response4List.isSuccess()) {
                        optionalList = response4List.getData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return optionalList;
            }

            @Override
            public void onUpdate(Object result) {
                doMyPostExecute();
            }
        });
    }


    //封装一下  仅仅作为刷新使用
    class GetDataPriceTask extends AsyncTask<String, Void, CommonResponse4List<ProductObj>> {

        @Override
        protected CommonResponse4List<ProductObj> doInBackground(String... params) {
            //get list data
            CommonResponse4List<ProductObj> response4List = getData();
            return response4List;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            context.showNetLoadingProgressDialog(null);
        }

        @Override
        protected void onPostExecute(CommonResponse4List<ProductObj> result) {
            // TODO Auto-generated method stub

            super.onPostExecute(result);
            context.hideNetLoadingProgressDialog();
            if (result != null && result.isSuccess()) {
                productObj = optionalList.get(0);
                doMyPostExecute();
                displayView();
                getUserInfoData();
            } else {
                if (result == null) {
                    context.showCusToast("网络连接失败");
                } else {
                    context.showCusToast(result.getErrorInfo());
                }
            }

        }
    }

    void doMyPostExecute() {
        if (context == null)
            return;
        if (context.isFinishing())
            return;
        // 更新页面展示
        for (ProductObj pro : optionalList) {
            //推送过来的数据是当前的品种
            if (productObj.getExcode() != null
                    && productObj.getInstrumentId() != null
                    && productObj.getExcode().equals(pro.getExcode())
                    && productObj.getInstrumentId().equals(pro.getInstrumentId())) {

                productObj.setBidPrice1(pro.getBidPrice1());
                productObj.setAskPrice1(pro.getAskPrice1());
                tv_byuprate.setText(productObj.getAskPrice1());
                tv_bydownrate.setText(productObj.getBidPrice1());
                setTotalMoney();
            }
        }
        recycler_livetrade.setItemViewCacheSize(optionalList.size());
        liveCreateProductAdapter.setOptionalList(optionalList);
    }

    /**
     * 选择产品
     *
     * @param event
     */
    public void onEventMainThread(final TradeCreateLiveChooseProEevent event) {
        productObj = event.productObj;
        displayView();

        moveToPosition(event.position);
    }

    private void moveToPosition(int n) {
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = linearLayoutManager.findLastVisibleItemPosition();

        //然后区分情况
        if (n <= firstItem ){
            //当要置顶的项在当前显示的第一个项的前面时
            recycler_livetrade.scrollToPosition(n);
        }else if ( n <= lastItem ){
            //当要置顶的项已经在屏幕上显示时
//            int top = recycler_livetrade.getChildAt(n - firstItem).gnetTop();
//            recycler_livetrade.scrollBy(0, top);

//            recycler_livetrade.scrollToPosition(n);

            linearLayoutManager.scrollToPositionWithOffset(n, 0);
            linearLayoutManager.setStackFromEnd(true);
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
            tv_byuprate.setText(productObj.getAskPrice1());
            tv_bydownrate.setText(productObj.getBidPrice1());
            setTotalMoney();
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_submit) {
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.T_CREATE_SUBMIT);
                confirmBeforeSubmit();
            } else if (id == R.id.text_create_cancle) {
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.T_CREATE_CANCLE);
                dialog.dismiss();
            } else if (id == R.id.upView) {
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.T_CREATE_BUYUP);

                if (v.equals(selectUpOrDownV))
                    return;
                selectUpOrDownV.setSelected(false);
                selectUpOrDownV = v;
                selectUpOrDownV.setSelected(true);
                typeBuy = ProductObj.TYPE_BUY_UP;
                setViewBG(true);
                setTotalMoney();
                ykTips();

            } else if (id == R.id.downView) {
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.T_CREATE_BUYDOWN);

                if (v.equals(selectUpOrDownV))
                    return;
                selectUpOrDownV.setSelected(false);
                selectUpOrDownV = v;
                selectUpOrDownV.setSelected(true);

                typeBuy = ProductObj.TYPE_BUY_DOWN;
                setViewBG(false);
                setTotalMoney();
                ykTips();

            } else if (id == R.id.btn_deal_reduce) {
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.T_CREATE_REDUCE);

                lessAddNum(false);
            } else if (id == R.id.btn_deal_add) {
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.T_CREATE_PLUS);

                lessAddNum(true);
            } else if (id == R.id.btn_create_help) {
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.T_CREATE_EXPLAIN);

//                QiHuoExplainWordActivity.startAct(context,
                        DialogUtilForTrade.showQiHuoExplainDlg(context,

                                new String[]{QiHuoExplainWordConfig.KYZJ,
                                QiHuoExplainWordConfig.BZJ,
                                QiHuoExplainWordConfig.JCSXF});
            } else if (id == R.id.text_gocashin) {
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.T_CREATE_GOCHARGE);
                CashInAndOutAct.startAct(context);
            }

        }
    };

    /**
     * 改变背景色
     *
     * @param isUp
     */
    void setViewBG(boolean isUp) {
        if (isUp) {
            view_buyup.setBackgroundResource(R.drawable.bg_view_buyup);
            text_buyup_percent.setTextColor(context.getResources().getColor(R.color.c_EA4A5E));
            view_buydown.setBackgroundResource(R.drawable.bg_view_buy_noselect);
            text_buydown_percent.setTextColor(context.getResources().getColor(R.color.c_999999_70));
            if (!ProductObj.IS_CLOSE_YES.equals(productObj.getIsClosed())) {
                btn_submit.setBackgroundResource(R.drawable.qucik_trade_submit);
                btn_submit.setClickable(true);
                btn_submit.setText(R.string.dlg_create);
            }
        } else {
            view_buydown.setBackgroundResource(R.drawable.bg_view_buydown);
            text_buydown_percent.setTextColor(context.getResources().getColor(R.color.c_06A969));
            view_buyup.setBackgroundResource(R.drawable.bg_view_buy_noselect);
            text_buyup_percent.setTextColor(context.getResources().getColor(R.color.c_999999_70));
            if (!ProductObj.IS_CLOSE_YES.equals(productObj.getIsClosed())) {
                btn_submit.setBackgroundResource(R.drawable.qucik_trade_submit_green);
                btn_submit.setClickable(true);
                btn_submit.setText(R.string.dlg_create);
            }
        }
    }

    /**
     * 提交前二次确认
     */
    void confirmBeforeSubmit() {
        if (productObj == null)
            return;
        String orderNumber = ed_num.getText().toString();
        if (StringUtil.isEmpty(orderNumber)) {
            context.showCusToast(context.getResources().getString(R.string.lable_createorder_tips, orderNumber));
            return;
        }
        if (Integer.parseInt(orderNumber) < productObj.getMinSl()) {
            context.showCusToast(context.getResources().getString(R.string.lable_createorder_tips, orderNumber));
            return;
        }
        String buyType = "";
        if (typeBuy == ProductObj.TYPE_BUY_UP) {
            buyType = context.getResources().getString(R.string.trade_buy_up);
        } else {
            buyType = context.getResources().getString(R.string.trade_buy_down);
        }
        DialogUtil.showTitleAndContentDialog(context,
                "确认建仓",
                context.getResources().getString(R.string.lable_createorder_contenet, productObj.getInstrumentName(), buyType, orderNumber, text_create_total.getText().toString()),
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
     * 建仓提交
     */
    void submit() {

        if (productObj == null)
            return;
        String orderNumber = ed_num.getText().toString();
        if (StringUtil.isEmpty(orderNumber)) {
            context.showCusToast(context.getResources().getString(R.string.lable_createorder_tips, orderNumber));
            return;
        }
        if (Integer.parseInt(orderNumber) < productObj.getMinSl()) {
            context.showCusToast(context.getResources().getString(R.string.lable_createorder_tips, orderNumber));
            return;
        }

        context.showNetLoadingProgressDialog(null);
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put(ProductObj.PARAM_instrumentId, productObj.getInstrumentId());
        if (typeBuy == ProductObj.TYPE_BUY_UP) {
            map.put(ProductObj.PARAM_PRICE, productObj.getAskPrice1() + "");
        } else {
            map.put(ProductObj.PARAM_PRICE, productObj.getBidPrice1() + "");
        }
        map.put(ProductObj.PARAM_ORDER_NB, orderNumber);//手数
        map.put(ProductObj.PARAM_TYPE, typeBuy + "");

        TradeHelp.fxCreate(context, map, new NetCallback(context) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                context.hideNetLoadingProgressDialog();
                //token过期 避免重复弹框
                if (ApiConfig.isNeedLogin(resultCode)) {
                    //重新登录
//                    showTokenDialog();
                    if (tradeLoginDlg == null) {
                        tradeLoginDlg = new TradeLoginDlg(context);
                    }
                    if (!tradeLoginDlg.isShowingDialog()) {
                        tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                    }
                    return;
                }
//                if ("余额不足".contains(resultMsg)) {
//                    DialogUtil.showConfirmDlg(context,
//                            ConvertUtil.NVL(resultMsg, context.getString(R.string.trade_create_fail)),
//                            null,
//                            "入金",
//                            true,
//                            null,
//                            new Handler.Callback() {
//                                @Override
//                                public boolean handleMessage(Message message) {
//                                    FXBTGCashInH5Act.startCashin(context);
//                                    return false;
//                                }
//                            });
//                    return;
//                }


//                context.showCusToast(ConvertUtil.NVL(resultMsg, context.getString(R.string.trade_create_fail)));
            }

            @Override
            public void onResponse(String response) {
                CommonResponse<TempObject> commonResponse = CommonResponse.fromJson(response, TempObject.class);
                context.hideNetLoadingProgressDialog();
                if (response != null
                        && commonResponse.isSuccess()) {
//                    context.showCusToast(context.getString(R.string.trade_create_success));
                    dialog.dismiss();

                    if (createCallback != null) {
                        //建仓成功需要处理的逻辑
                        createCallback.handleMessage(new Message());
                    }
                    //辅助线
                    createOrderSuccess();
                } else {
                    //token过期 避免重复弹框
                    if (ApiConfig.isNeedLogin(commonResponse.getErrorCode())) {
                        //重新登录
//                        showTokenDialog();
                        if (tradeLoginDlg == null) {
                            tradeLoginDlg = new TradeLoginDlg(context);
                        }
                        if (!tradeLoginDlg.isShowingDialog()) {
                            tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                        }
                        return;
                    }
//                    context.showCusToast(ConvertUtil.NVL(commonResponse.getErrorInfo(), context.getString(R.string.trade_create_fail)));
                }

            }
        });
    }


    TradeInfoData userInfoData;

    private void getUserInfoData() {

        TradeInfoData tradeInfoData = TradeUserInfoData4Situation.getInstance(context, null).getTradeInfoData();
        if (tradeInfoData != null) {
            if (tradeInfoData != null) {
                userInfoData = tradeInfoData;
            }
            //开始刷新
            if (refreshUtil != null)
                refreshUtil.start();
            //成功了才显示
            if (!dialog.isShowing())
                dialog.show();
            setTotalMoney();
        } else {
            TradeUserInfoData4Situation.getInstance(context, null).loadTradeOrderAndUserInfoData(new TradeUserInfoData4Situation.GetDataCallback() {
                @Override
                public void onFailure(String resultCode, String resultMsg) {
//                showCusToast(resultMsg);
                    if (ApiConfig.isNeedLogin(resultCode)) {
                        //重新登录
//                        showTokenDialog();
                        if (tradeLoginDlg == null) {
                            tradeLoginDlg = new TradeLoginDlg(context);
                        }
                        if (!tradeLoginDlg.isShowingDialog()) {
                            tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                        }
                        return;
                    }
                    context.showCusToast(ConvertUtil.NVL(resultMsg, context.getString(R.string.network_problem)));
                }

                @Override
                public void onResponse(TradeInfoData tradeInfoData) {
                    if (tradeInfoData != null) {
                        userInfoData = tradeInfoData;
                    }

                    //成功了才显示
                    if (!dialog.isShowing())
                        dialog.show();
                    //开始刷新
                    if (refreshUtil != null)
                        refreshUtil.start();
                    setTotalMoney();
                }
            }, false);
        }
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
            if (shouShu >= productObj.getMaxSl()) {
                shouShu = productObj.getMaxSl();
            }
        } else {
            shouShu -= 1;
            if (shouShu <= productObj.getMinSl()) {
                shouShu = productObj.getMinSl();
            }
        }
        ed_num.setText(String.valueOf(shouShu));
        ed_num.setSelection(ed_num.getText().toString().length());
        ed_num.clearFocus();
        ed_num.setSelected(false);
        ed_num.setCursorVisible(false);
        ykTips();
        setTotalMoney();
    }

    /**
     * 显示需要的预付款
     */
    void setTotalMoney() {
        try {
            if (productObj == null)
                return;
            if (userInfoData == null)
                return;
            if (StringUtil.isEmpty(ed_num.getText().toString()))
                return;

            text_create_availmoney.setText(context.getResources().getString(R.string.display_money, userInfoData.getAvailable()));

            int shouShu = Integer.parseInt(ed_num.getText().toString());
            if (shouShu <= 0) {
                shouShu = productObj.getMinSl();
            }
            // 先计算一手保证金的金额
            double oneSlBZj = 0;
            if (typeBuy == ProductObj.TYPE_BUY_DOWN) {//买跌
                if (TextUtils.isEmpty(productObj.getLongMarginRatioByMoney()) || Double.parseDouble(productObj.getLongMarginRatioByMoney()) == 0) {
                    oneSlBZj = Double.parseDouble(productObj.getLongMarginRatioByVolume());
                } else {
                    oneSlBZj = NumberUtil.multiply(new BigDecimal(productObj.getLongMarginRatioByMoney()).doubleValue(), new BigDecimal(productObj.getBidPrice1()).doubleValue()) * productObj.getVolumeMultiple();
                }
            } else {//买涨
                if (TextUtils.isEmpty(productObj.getShortMarginRatioByMoney()) || Double.parseDouble(productObj.getShortMarginRatioByMoney()) == 0) {
                    oneSlBZj = Double.parseDouble(productObj.getShortMarginRatioByVolume());
                } else {
                    oneSlBZj = NumberUtil.multiply(new BigDecimal(productObj.getShortMarginRatioByMoney()).doubleValue(), new BigDecimal(productObj.getAskPrice1()).doubleValue()) * productObj.getVolumeMultiple();
                }
            }
            String totalBZJ = NumberUtil.moveLast0(NumberUtil.multiply(oneSlBZj, new BigDecimal(shouShu).doubleValue()));

            text_create_bzj.setText(context.getResources().getString(R.string.display_money, StringUtil.forNumber(new BigDecimal(totalBZJ).doubleValue())));

            // 先计算一手手续费的金额
            double oneSlSXF = 0;
            if (TextUtils.isEmpty(productObj.getOpenRatioByMoney()) || Double.parseDouble(productObj.getOpenRatioByMoney()) == 0) {
                oneSlSXF = Double.parseDouble(productObj.getOpenRatioByVolume());
            } else {
                if (typeBuy == ProductObj.TYPE_BUY_DOWN) {//买跌
                    oneSlSXF = NumberUtil.multiply(new BigDecimal(productObj.getOpenRatioByMoney()).doubleValue(), new BigDecimal(productObj.getBidPrice1()).doubleValue()) * productObj.getVolumeMultiple();
                } else {
                    oneSlSXF = NumberUtil.multiply(new BigDecimal(productObj.getOpenRatioByMoney()).doubleValue(), new BigDecimal(productObj.getAskPrice1()).doubleValue()) * productObj.getVolumeMultiple();
                }
            }
            String totalSXF = NumberUtil.moveLast0(NumberUtil.multiply(oneSlSXF, new BigDecimal(shouShu).doubleValue()));
            text_create_sxf.setText(context.getResources().getString(R.string.display_money, StringUtil.forNumber(new BigDecimal(totalSXF).doubleValue())));

            String totalMoney = NumberUtil.moveLast0(NumberUtil.multiply((oneSlSXF + oneSlBZj), new BigDecimal(shouShu).doubleValue()));
            text_create_total.setText(StringUtil.forNumber(new BigDecimal(totalMoney).doubleValue()));

            if (!ProductObj.IS_CLOSE_YES.equals(productObj.getIsClosed())) {
                if (Double.parseDouble(totalMoney) > Double.parseDouble(userInfoData.getAvailable())) {
                    btn_submit.setText(R.string.trade_qihuo_nomoney);
                    btn_submit.setClickable(false);
                    btn_submit.setBackgroundColor(context.getResources().getColor(R.color.c_d1d1d1));
                } else {
                    setViewBG(typeBuy == ProductObj.TYPE_BUY_UP);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 计算每波动一个点的盈利
     */
    public void ykTips() {
        String input = ed_num.getText().toString();
        if (TextUtils.isEmpty(input)) {
            return;
        }
        int shouShu = Integer.parseInt(input.trim());
        if (shouShu <= 0) {
            shouShu = productObj.getMinSl();
        }
        text_create_tips.setText(context.getResources().getString(R.string.create_order_tips,
                productObj.getPriceTick(),
                NumberUtil.moveLast0(NumberUtil.multiply(Double.parseDouble(productObj.getPriceTick()), Double.parseDouble(productObj.getVolumeMultiple() * shouShu + "")))));

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
        EventBus.getDefault().post(new TradeOrderOptionEvent(TradeOrderOptionEvent.OPTION_CREATESUCCESS));
    }
}
