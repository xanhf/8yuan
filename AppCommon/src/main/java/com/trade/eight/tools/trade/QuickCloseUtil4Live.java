package com.trade.eight.tools.trade;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.Exchange;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.trade.TradeOrderOptionEvent;
import com.trade.eight.moudle.trade.adapter.QuickCloseAdapter;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.refresh.RefreshUtil;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshExpandListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by fangzhu
 * 快速平仓 对哈贵油品种 银和油分开的 可全选效果
 */
public class QuickCloseUtil4Live {

    public static final String TAG = "QuickCloseUtil4Live";
    public static final String MOBCLICK_TAG = "page_quickClose";

    Dialog dialog;
    BaseActivity context;
    String excode;
    String code;
    View line_title_0, line_title_1, line_title_2, select_v;
    List<View> titleViewList = new ArrayList<View>();
    TextView text_title_0, text_title_1, text_title_2;
    PullToRefreshExpandListView pullToRefreshExpandListView;
    ExpandableListView listView;
    View line_content, nodataView;
    QuickCloseAdapter dateAdapter;
    Button btn_cancle, btn_submit;
    RefreshUtil refreshUtil;
    int position, listViemItemTop;

    // 持仓列表
    public HashMap<String, List<TradeOrder>> orderMap = new HashMap<String, List<TradeOrder>>();

    private static final int LOOPGETDATA = 1;//循环获取数据
    private static final int LOOPGETDATA_OVER = 2;// 数据获取结束

    public static final int CALLBACK_MSGNODATA = 1;//没有持仓数据
    public static final int CALLBACK_MSGOVER = 2;// 数据获取结束

    private String currentTradeCode = "";// 当前选中的交易所
    private String currentRefreshTradeCode = "";// 当前刷新的交易所

    Handler.Callback callback;

    private boolean isDissmiss = false;//窗口是否消失  防止快速退出再次自动弹出
    List<Exchange> exchangeList;
    int loopIndex = 0;// 循环的索引

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOOPGETDATA://继续获取数据
                    getorderData(msg.obj.toString(), false, false, true);
                    break;
                case LOOPGETDATA_OVER:// 数据获取结束
                    Message message = new Message();
                    message.what = CALLBACK_MSGOVER;
                    loopIndex = 0;
                    break;
            }
        }
    };

    public QuickCloseUtil4Live(BaseActivity context) {
        this.context = context;
        exchangeList = TradeConfig.getExchangeList(context);
        initDialog(context);
        isDissmiss = false;

        getorderData(exchangeList.get(0).getExcode(), true, true, true);
    }


    public String getCurrentTradeCode() {
        return currentTradeCode;
    }

    public void setCurrentTradeCode(String currentTradeCode) {
        this.currentTradeCode = currentTradeCode;
    }

    private void initDialog(BaseActivity context) {
        if (dialog != null && dialog.isShowing())
            return;

        try {
            dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
            dialog.setContentView(R.layout.dialog_quick_close4live);
            dateAdapter = new QuickCloseAdapter(context, new ArrayList<List<TradeOrder>>());
            findViews(dialog);
            listView.setAdapter(dateAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isShowingDialog() {
        if (dialog != null)
            return dialog.isShowing();
        return false;
    }

    public void showDialog() {
        Window w = dialog.getWindow();
        w.setWindowAnimations(R.style.ani_in_bottm_out_bottom);
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = (int) context.getResources().getDimension(R.dimen.margin_448dp);
        w.setGravity(Gravity.BOTTOM);

        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (refreshUtil != null)
                    refreshUtil.stop();
                currentTradeCode = "";
                currentRefreshTradeCode = "";
                select_v = null;
                loopIndex = 0;
                isDissmiss = true;
            }
        });

        initRefresh();
    }

    void findViews(Dialog dialog) {

        line_title_0 = dialog.findViewById(R.id.line_title_0);
        line_title_1 = dialog.findViewById(R.id.line_title_1);
        line_title_2 = dialog.findViewById(R.id.line_title_2);
        text_title_0 = (TextView) dialog.findViewById(R.id.text_title_0);
        text_title_1 = (TextView) dialog.findViewById(R.id.text_title_1);
        text_title_2 = (TextView) dialog.findViewById(R.id.text_title_2);

        titleViewList.add(line_title_0);
        titleViewList.add(line_title_1);
        titleViewList.add(line_title_2);

        // 此处判断只为撤销交易所
//        if (TradeConfig.getTradeMap().containsKey(BakSourceInterface.TRUDE_SOURCE_WEIPANP_HG)) {
//            line_title_0.setVisibility(View.VISIBLE);
//        } else {
//            line_title_0.setVisibility(View.GONE);
//        }
//        if (TradeConfig.getTradeMap().containsKey(BakSourceInterface.TRUDE_SOURCE_WEIPANP_JN)) {
//            line_title_1.setVisibility(View.VISIBLE);
//        } else {
//            line_title_1.setVisibility(View.GONE);
//        }
//        if (TradeConfig.getTradeMap().containsKey(BakSourceInterface.TRUDE_SOURCE_WEIPAN)) {
//            line_title_2.setVisibility(View.VISIBLE);
//        } else {
//            line_title_2.setVisibility(View.GONE);
//        }
        int size = exchangeList.size();
        switch (size) {
            case 1:
                line_title_0.setVisibility(View.VISIBLE);
                text_title_0.setText(exchangeList.get(0).getExchangeName());
                line_title_0.setTag(exchangeList.get(0).getExcode());
                line_title_1.setVisibility(View.GONE);
                line_title_2.setVisibility(View.GONE);
                break;
            case 2:
                line_title_0.setVisibility(View.VISIBLE);
                line_title_1.setVisibility(View.VISIBLE);
                line_title_2.setVisibility(View.GONE);
                text_title_0.setText(exchangeList.get(0).getExchangeName());
                text_title_1.setText(exchangeList.get(1).getExchangeName());
                line_title_0.setTag(exchangeList.get(0).getExcode());
                line_title_1.setTag(exchangeList.get(1).getExcode());
                break;
            case 3:
                line_title_0.setVisibility(View.VISIBLE);
                line_title_1.setVisibility(View.VISIBLE);
                line_title_2.setVisibility(View.VISIBLE);
                text_title_0.setText(exchangeList.get(0).getExchangeName());
                text_title_1.setText(exchangeList.get(1).getExchangeName());
                text_title_2.setText(exchangeList.get(2).getExchangeName());
                line_title_0.setTag(exchangeList.get(0).getExcode());
                line_title_1.setTag(exchangeList.get(1).getExcode());
                line_title_2.setTag(exchangeList.get(2).getExcode());
                break;
        }
        line_title_0.setOnClickListener(clickListener);
        line_title_1.setOnClickListener(clickListener);
        line_title_2.setOnClickListener(clickListener);

        pullToRefreshExpandListView = (PullToRefreshExpandListView) dialog.findViewById(R.id.listView);
        pullToRefreshExpandListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ExpandableListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                BaseInterface.setPullFormartRefreshTime(pullToRefreshExpandListView);
                getorderData(currentTradeCode, true, false, false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {

            }
        });
        pullToRefreshExpandListView.setPullLoadEnabled(false);
        pullToRefreshExpandListView.setPullRefreshEnabled(true);
        listView = pullToRefreshExpandListView.getRefreshableView();
        listView.setVisibility(View.VISIBLE);
        listView.setDividerHeight(0);
        //去掉group点击事件
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                //group不允许点击
                return true;
            }
        });
        //去掉下划线
        listView.setGroupIndicator(null);

        line_content = dialog.findViewById(R.id.line_content);
        nodataView = dialog.findViewById(R.id.nodataView);

        btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        btn_submit = (Button) dialog.findViewById(R.id.btn_submit);
        btn_submit.setEnabled(false);
        btn_cancle.setOnClickListener(clickListener);
        btn_submit.setOnClickListener(clickListener);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    position = listView.getFirstVisiblePosition();
                    View itemView = listView.getChildAt(0);
                    listViemItemTop = (itemView == null) ? 0 : itemView.getTop();
                    Log.v(TAG, "onScrollStateChanged " + "position=" + position + "  listViemItemTop=" + listViemItemTop);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                TradeOrder item = (TradeOrder) dateAdapter.getChild(groupPosition, childPosition);
                String title = ConvertUtil.NVL(ConvertUtil.NVL(item.getProductName(), ""), "")
                        + ConvertUtil.NVL(item.getWeight(), "")
                        + ConvertUtil.NVL(item.getUnit(), "");
                MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "item_click_" + title);
                if (dateAdapter.getCheckMap().containsKey(item.getOrderId() + "")) {
                    //已经选中了 现在移除
                    dateAdapter.getCheckMap().remove(item.getOrderId() + "");

                } else {
                    //没有选中 现在选中
                    dateAdapter.getCheckMap().put(item.getOrderId() + "", item);

                    btn_submit.setEnabled(true);
                }
//                //更新adapter
                dateAdapter.notifyDataSetChanged();

                if (getAllCheckMap().size() > 0) {
                    btn_submit.setEnabled(true);
                } else {
                    btn_submit.setEnabled(false);
                }
                return false;
            }
        });
        dateAdapter.setMyExCheckAllClick(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (getAllCheckMap().size() > 0) {
                    btn_submit.setEnabled(true);
                } else {
                    btn_submit.setEnabled(false);
                }
                return false;
            }
        });

    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.btn_cancle) {
                MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "btn_cancle_click");
                if (dialog != null)
                    dialog.dismiss();

            } else if (id == R.id.btn_submit) {
                MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "btn_Close_click");
                //循环平仓
                closeHold();
            } else if (id == R.id.line_title_0) {
                titleViewClick(R.id.line_title_0, view);

            } else if (id == R.id.line_title_1) {
                titleViewClick(R.id.line_title_1, view);

            } else if (id == R.id.line_title_2) {
                titleViewClick(R.id.line_title_2, view);
            }
        }
    };

    /**
     * 顶部view切换
     *
     * @param id
     * @param view
     */
    private void titleViewClick(int id, View view) {
        switch (id) {
            case R.id.line_title_0:
//                currentTradeCode = BakSourceInterface.TRUDE_SOURCE_WEIPANP_HG;
                currentTradeCode = exchangeList.get(0).getExcode();
                break;
            case R.id.line_title_1:
//                currentTradeCode = BakSourceInterface.TRUDE_SOURCE_WEIPANP_JN;
                currentTradeCode = exchangeList.get(1).getExcode();
                break;
            case R.id.line_title_2:
                currentTradeCode = exchangeList.get(2).getExcode();
//                currentTradeCode = BakSourceInterface.TRUDE_SOURCE_WEIPAN;
                break;
        }
        refreshUtil.stop();
        dateAdapter.clear();
        dateAdapter.notifyDataSetChanged();

        select_v.clearFocus();
        select_v.setSelected(false);
        select_v = view;
        select_v.setSelected(true);

        getAllCheckMap().clear();


        if (orderMap.get(currentTradeCode) != null && orderMap.get(currentTradeCode).size() > 0) {
            setListViewData(orderMap.get(currentTradeCode));
            refreshUtil.start();
        } else {
            pullToRefreshExpandListView.doPullRefreshing(true, 0);
        }
    }


    void initRefresh() {
        refreshUtil = new RefreshUtil(context);
        refreshUtil.setRefreshTime(AppSetting.getInstance(context).getRefreshTimeWPList());
        refreshUtil.setOnRefreshListener(new RefreshUtil.OnRefreshListener() {
            @Override
            public Object doInBackground() {
                getData();
                return upDateList;
            }

            @Override
            public void onUpdate(Object result) {
                if (upDateList != null) {
                    if (currentRefreshTradeCode.equals(currentTradeCode)) {
                        setListViewData(upDateList);
                    } else {
                        refreshUtil.stop();
                    }
                } else {
                    line_content.setVisibility(View.GONE);
                    nodataView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    List<TradeOrder> upDateList = null;

    /**
     * 获取数据
     */
    void getData() {
        try {
            currentRefreshTradeCode = currentTradeCode;
            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin())
                return;
            Map<String, String> map = new HashMap<>();
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());

            String token = AppSetting.getInstance(context).getWPToken(context, currentTradeCode);
            if (!StringUtil.isEmpty(token))
                map.put(ApiConfig.PARAM_TRADE_TOKEN, token);
            map.put(TradeConfig.PARAM_EXCHANGEID, TradeConfig.getTradeMap().get(currentTradeCode).getExchangeId() + "");

            map = ApiConfig.getParamMap(context, map);

            Map<String, String> header = new HashMap<String, String>();
            header.put(ApiConfig.PARAM_TRADE_REQUEST_HEADER,
                    ApiConfig.PARAM_TRADE_TOKEN + "=" + token);

            String response = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_ORDER_HOLD_LIST, currentTradeCode), map, header);

            final CommonResponse4List<TradeOrder> response4List = CommonResponse4List.fromJson(response, TradeOrder.class);
            if (upDateList != null) {
                upDateList.clear();
            }
            if (response4List != null && response4List.isSuccess()) {
                upDateList = response4List.getData();
            } else {
                upDateList = null;
                //刷新过程中token过期
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //token过期 避免重复弹框
                        if (response4List != null && ApiConfig.isNeedLogin(response4List.getErrorCode())) {
                            //重新登录
                            showTokenDialog();

                            return;
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 得到所有选中的品种
     *
     * @return
     */
    Map<String, TradeOrder> getAllCheckMap() {
        return dateAdapter.getCheckMap();
    }

    /**
     * 平仓
     */
    void closeHold() {
        final Map<String, TradeOrder> checkMap = getAllCheckMap();
        if (checkMap.size() == 0) {
            context.showCusToast("请至少选中一个品种");
            return;
        }

        new AsyncTask<Void, Void, CommonResponse<TempObject>>() {

            int errorCount = 0;
            String errorInfo = "";

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //直接关闭dialog
//                if (dialog != null)
//                    dialog.dismiss();

                context.showNetLoadingProgressDialog(null);
            }

            @Override
            protected CommonResponse<TempObject> doInBackground(Void... params) {
                CommonResponse<TempObject> response = null;
                try {
                    for (String id : checkMap.keySet()) {
//                        response = TradeHelp.closeOrder(context, id);


                        UserInfoDao dao = new UserInfoDao(context);
                        if (!dao.isLogin())
                            return response;
                        Map<String, String> map = new HashMap<>();
                        map.put(UserInfo.UID, dao.queryUserInfo().getUserId());

                        String token = AppSetting.getInstance(context).getWPToken(context, currentTradeCode);
                        if (!StringUtil.isEmpty(token))
                            map.put(ApiConfig.PARAM_TRADE_TOKEN, token);
                        map.put(TradeConfig.PARAM_EXCHANGEID, TradeConfig.getTradeMap().get(currentTradeCode).getExchangeId() + "");
                        map.put("orderId", id);

                        map = ApiConfig.getParamMap(context, map);

                        Map<String, String> header = new HashMap<String, String>();
                        header.put(ApiConfig.PARAM_TRADE_REQUEST_HEADER,
                                ApiConfig.PARAM_TRADE_TOKEN + "=" + token);

                        String responseStr = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_ORDER_CLOSE, currentTradeCode), map, header);
                        response = CommonResponse.fromJson(responseStr, TempObject.class);

                        if (!response.isSuccess()) {
                            errorCount++;
                            errorInfo = response.getErrorInfo();

                            //只要有一笔平仓出错，就不继续平仓；直接返回错误
                            return response;
                        } else {
                            List<TradeOrder> orderList = orderMap.get(currentTradeCode);
                            int removeIndex = -1;
                            for (int i = 0; i < orderList.size(); i++) {
                                if (orderList.get(i).getOrderId() == checkMap.get(id).getOrderId()) {
                                    removeIndex = i;
                                }
                            }
                            orderList.remove(removeIndex);
                            orderMap.put(currentTradeCode, orderList);
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setListViewData(orderMap.get(currentTradeCode));
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(CommonResponse<TempObject> response) {
                super.onPostExecute(response);
                if (context.isFinishing())
                    return;
                context.hideNetLoadingProgressDialog();
                getAllCheckMap().clear();
                if (response != null) {
                    if (response.isSuccess()) {
                        MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "closeSuccess");

                        String msg = context.getResources().getString(R.string.trade_close_success);
                        DialogUtil.showSuccessSmallDialog(context, msg, 0, null);
                        //成功的逻辑处理  刷新列表
                        //标记资金变化了 显示红点
                        AppSetting.getInstance(context).setMoneyFlag(true);
                        // 平仓成功
                        EventBus.getDefault().post(new TradeOrderOptionEvent(TradeOrderOptionEvent.OPTION_CLOSESUCCESS));

                    } else {
                        MyAppMobclickAgent.onEvent(context, MOBCLICK_TAG, "closeError_code_" + ConvertUtil.NVL(response.getErrorCode(), ""));
                        //token过期 避免重复弹框
                        if (ApiConfig.isNeedLogin(response.getErrorCode())) {
                            if (!StringUtil.isEmpty(response.getErrorInfo())) {
                                context.showCusToast(response.getErrorInfo());
                            }
                            //重新登录
                            showTokenDialog();

                            return;
                        }
                        //错误提示信息
                        if (!StringUtil.isEmpty(response.getErrorInfo())) {
                            context.showCusToast(response.getErrorInfo());
                        } else {
                            context.showCusToast(context.getResources().getString(R.string.trade_close_fail));
                        }
                    }
                } else {
                    context.getResources().getString(R.string.trade_close_fail);
                }
            }
        }.execute();
    }

    Dialog tokenDlg = null;

    /**
     * dialog的处理
     */
    void showTokenDialog() {
        if (dialog == null)
            return;
        if (tokenDlg != null) {
            tokenDlg.dismiss();
        }
        if (context.isFinishing())
            return;
        //token 的dialog 显示后 停止刷新
        if (refreshUtil != null)
            refreshUtil.stop();
        DialogUtil.showTokenDialog(context, TradeConfig.getCurrentTradeCode(context), new DialogUtil.AuthTokenDlgShow() {
            @Override
            public void onDlgShow(Dialog dlg) {
                tokenDlg = dlg;
            }
        }, new DialogUtil.AuthTokenCallBack() {
            @Override
            public void onPostClick(Object obj) {
            }

            @Override
            public void onNegClick() {
            }
        });
    }

    /**
     * 设置listview的数据
     * 重新封装成group的方式
     *
     * @param list
     */
    void setListViewData(List<TradeOrder> list) {
        if (list == null)
            return;
        if (list.size() > 0) {
            line_content.setVisibility(View.VISIBLE);
            nodataView.setVisibility(View.GONE);
        } else {
            line_content.setVisibility(View.GONE);
            nodataView.setVisibility(View.VISIBLE);
            return;
        }
        List<List<TradeOrder>> mainList = null;
        if (TextUtils.isEmpty(excode) && TextUtils.isEmpty(code)) {
            //排序 油 银 咖啡
            mainList = TradeSortUtil.sortOrderInGroup(list);
        } else {
            mainList = new ArrayList<>();
            List<TradeOrder> listTradeOrderForProduct = new ArrayList<TradeOrder>();//当前品种持仓单
            for (TradeOrder tradeOrder : list) {
                if (tradeOrder.getExcode().equals(excode) && tradeOrder.getCode().equals(code)) {
                    listTradeOrderForProduct.add(tradeOrder);
                }
            }
            mainList.add(listTradeOrderForProduct);
        }

        dateAdapter.setData(mainList);
        listView.setAdapter(dateAdapter);
        for (int i = 0; i < dateAdapter.getGroupCount(); i++) {
            listView.expandGroup(i);
        }
        listView.setSelectionFromTop(position, listViemItemTop);
    }


    public Handler.Callback getCallback() {
        return callback;
    }

    public void setCallback(Handler.Callback callback) {
        this.callback = callback;
    }

    /**
     * 获取持仓单
     *
     * @param tradeCode       交易所code
     * @param showTokenDialog 是否展示token过期token
     * @param showLoding      是否展示菊花
     * @param isLoop          是否嵌套循环获取数据
     */
    public void getorderData(final String tradeCode, final boolean showTokenDialog, final boolean showLoding, final boolean isLoop) {

        UserInfoDao dao = new UserInfoDao(context);
        if (!dao.isLogin())
            return;
        HashMap<String, String> map = new HashMap<>();
        map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
        String token = AppSetting.getInstance(context).getWPToken(context, tradeCode);
        if (!StringUtil.isEmpty(token))
            map.put(ApiConfig.PARAM_TRADE_TOKEN, token);
        map.put(TradeConfig.PARAM_EXCHANGEID, TradeConfig.getTradeMap().get(tradeCode).getExchangeId() + "");

        Map<String, String> header = new HashMap<String, String>();
        header.put(ApiConfig.PARAM_TRADE_REQUEST_HEADER,
                ApiConfig.PARAM_TRADE_TOKEN + "=" + token);

        HttpClientHelper.doPostOption(context,
                AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_ORDER_HOLD_LIST, tradeCode),
                map,
                header,
                new NetCallback(context) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        context.hideNetLoadingProgressDialog();
                        pullToRefreshExpandListView.onPullUpRefreshComplete();
                        pullToRefreshExpandListView.onPullDownRefreshComplete();
                        if (ApiConfig.isNeedLogin(resultCode) && showTokenDialog) {
                            //重新登录
                            showTokenDialog();
                        }
//                        if (showTokenDialog) {
//                            context.showCusToast(resultMsg);
//                        }
                        if (currentTradeCode.equals(tradeCode)) {
                            line_content.setVisibility(View.GONE);
                            nodataView.setVisibility(View.VISIBLE);
                        }
                        sendMessage(tradeCode, isLoop);
                    }

                    @Override
                    public void onResponse(String response) {
                        context.hideNetLoadingProgressDialog();
                        pullToRefreshExpandListView.onPullUpRefreshComplete();
                        pullToRefreshExpandListView.onPullDownRefreshComplete();
                        CommonResponse4List<TradeOrder> response4List = CommonResponse4List.fromJson(response, TradeOrder.class);
                        if (select_v == null || TextUtils.isEmpty(currentTradeCode)) {
//                            if (tradeCode == BakSourceInterface.TRUDE_SOURCE_WEIPANP_HG) {
//                                select_v = line_title_0;
//                                currentTradeCode = BakSourceInterface.TRUDE_SOURCE_WEIPANP_HG;
//                            } else if (tradeCode == BakSourceInterface.TRUDE_SOURCE_WEIPANP_JN) {
//                                select_v = line_title_1;
//                                currentTradeCode = BakSourceInterface.TRUDE_SOURCE_WEIPANP_JN;
//
//                            } else {
//                                select_v = line_title_2;
//                                currentTradeCode = BakSourceInterface.TRUDE_SOURCE_WEIPAN;
//                            }
                            for (int i = 0; i < exchangeList.size(); i++) {
                                Exchange exchange = exchangeList.get(i);
                                if (tradeCode.equals(exchange.getExcode())) {
                                    select_v = titleViewList.get(i);
                                    currentTradeCode = exchange.getExcode();
                                }
                            }
                            select_v.setSelected(true);
                            if (!isDissmiss) {
                                dialog.show();
                            }
                        }
                        orderMap.put(tradeCode, response4List.getData());
                        if (response4List.getData() != null && response4List.getData().size() > 0) {

                            if (select_v != null) {
                                if (currentTradeCode.equals(tradeCode)) {
                                    dateAdapter.clear();
                                    setListViewData(orderMap.get(tradeCode));
                                    line_content.setVisibility(View.VISIBLE);
                                    nodataView.setVisibility(View.GONE);
                                    if (refreshUtil != null)
                                        refreshUtil.start();
                                }
                            }
                        } else {
                            if (currentTradeCode.equals(tradeCode)) {
                                line_content.setVisibility(View.GONE);
                                nodataView.setVisibility(View.VISIBLE);
                            }
                        }
                        sendMessage(tradeCode, isLoop);
                    }
                }, showLoding);
    }

    /**
     * 发送hanlder数据
     *
     * @param tradeCode
     * @param isLoop
     */
    private void sendMessage(String tradeCode, boolean isLoop) {
        if (isLoop) {
            Message message = new Message();
            message.what = LOOPGETDATA;
            loopIndex += 1;
//            if (TradeConfig.getTradeMap().containsKey(BakSourceInterface.TRUDE_SOURCE_WEIPAN)) {
//                if (tradeCode.equals(BakSourceInterface.TRUDE_SOURCE_WEIPAN)) {// 当为广贵所时,结束循环(有广贵)
//
//                }
//            } else {
//                if (tradeCode.equals(BakSourceInterface.TRUDE_SOURCE_WEIPANP_JN)) {// 当为农交所所时,结束循环(无广贵)
//                    message.what = LOOPGETDATA_OVER;
//                }
//            }
//            if (tradeCode.equals(BakSourceInterface.TRUDE_SOURCE_WEIPANP_HG)) {
//                message.obj = BakSourceInterface.TRUDE_SOURCE_WEIPANP_JN;
//            } else if (tradeCode.equals(BakSourceInterface.TRUDE_SOURCE_WEIPANP_JN)) {
//                message.obj = BakSourceInterface.TRUDE_SOURCE_WEIPAN;

            if (loopIndex < exchangeList.size()) {
                message.obj = exchangeList.get(loopIndex).getExcode();
            } else {
                message.what = LOOPGETDATA_OVER;
            }
            handler.sendMessage(message);
        }
    }
}
