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
import android.widget.Toast;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AppSetting;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.entity.trade.TradeOrderAndUserInfoData;
import com.trade.eight.moudle.trade.TradeOrderOptionEvent;
import com.trade.eight.moudle.trade.adapter.QuickCloseAdapter;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.refresh.RefreshUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by fangzhu
 * 快速平仓 对哈贵油品种 银和油分开的 可全选效果
 */
public class QuickCloseUtil {
    public static final String TAG = "QuickCloseUtil";
    public static final String MOBCLICK_TAG = "page_quickClose";

    Dialog dialog;
    BaseActivity context;
    String excode;
    String code;

    ExpandableListView listView;
    QuickCloseAdapter dateAdapter;
    Button btn_cancle, btn_submit;
    RefreshUtil refreshUtil;
    int position, listViemItemTop;

    public QuickCloseUtil(BaseActivity context) {
        this.context = context;
        initDialog(context);
    }

    /**
     * 主要用于产品详情页 平仓筛选
     *
     * @param context
     * @param excode
     * @param code
     */
    public QuickCloseUtil(BaseActivity context, String excode, String code) {
        this.context = context;
        this.excode = excode;
        this.code = code;
        initDialog(context);
    }

    private void initDialog(BaseActivity context) {
        if (dialog != null && dialog.isShowing())
            return;

        try {

            dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
            dialog.setContentView(R.layout.dialog_quick_close);

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
//        int screenWidth = displayMetrics.widthPixels;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        w.setGravity(Gravity.BOTTOM);

        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (refreshUtil != null)
                    refreshUtil.stop();
            }
        });
        new GetDataTask(true).execute();

        initRefresh();
    }

    void findViews(Dialog dialog) {
        listView = (ExpandableListView) dialog.findViewById(R.id.listView);
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
//                    dateAdapter.getCheckMap().remove(dateAdapter.getCheckMap().get(item.getOrderId() + ""));
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

            }
        }
    };


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
//                        && upDateList.size() > 0
                    setListViewData(upDateList);
//                        nodataView.setVisibility(View.GONE);
                } else {
//                        nodataView.setVisibility(View.VISIBLE);
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
            final CommonResponse4List<TradeOrder> response4List = TradeHelp.getTradeOrderList(context);
            if (response4List != null && response4List.isSuccess()) {
                upDateList = response4List.getData();
            } else {
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
                if (dialog != null)
                    dialog.dismiss();
                context.showNetLoadingProgressDialog(null);
            }

            @Override
            protected CommonResponse<TempObject> doInBackground(Void... params) {
                CommonResponse<TempObject> response = null;
                try {
                    for (String id : checkMap.keySet()) {
                        response = TradeHelp.closeOrder(context, id);
                        if (!response.isSuccess()) {
                            errorCount++;
                            errorInfo = response.getErrorInfo();

                            //只要有一笔平仓出错，就不继续平仓；直接返回错误
                            return response;
                        } else {

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

                        } else
                            context.getResources().getString(R.string.trade_close_fail);
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
        //重复显示
//        if (dialog != null && dialog.isShowing()) {
//            return;
//        }
        if (tokenDlg != null) {
            tokenDlg.dismiss();
//            return;
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

    Handler.Callback callback;

    public Handler.Callback getCallback() {
        return callback;
    }

    public void setCallback(Handler.Callback callback) {
        this.callback = callback;
    }

    class GetDataTask extends AsyncTask<String, Void, CommonResponse4List<TradeOrder>> {
        private boolean isPullRefresh;

        public GetDataTask(boolean isPullRefresh) {
            this.isPullRefresh = isPullRefresh;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            context.showNetLoadingProgressDialog(null);
        }

        @Override
        protected CommonResponse4List<TradeOrder> doInBackground(String... params) {
            return TradeHelp.getTradeOrderList(context);
        }

        @Override
        protected void onPostExecute(CommonResponse4List<TradeOrder> result) {
            // TODO Auto-generated method stub
            if (context.isFinishing())
                return;
            super.onPostExecute(result);
            context.hideNetLoadingProgressDialog();

            if (result != null) {
                if (result.isSuccess()) {
                    if (result.getData() != null && result.getData().size() > 0) {

                        if (dialog != null && !dialog.isShowing())
                            dialog.show();
                        setListViewData(result.getData());

//                        //开启刷新操作
                        if (refreshUtil != null)
                            refreshUtil.start();
                    } else {
                        dateAdapter.clear();
                        DialogUtil.showConfirmDlg(context, "您当前暂无持仓", "取消", "去建仓", true, new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message message) {
                                return false;
                            }
                        }, new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message message) {
                                if (callback != null)
                                    callback.handleMessage(message);

                                return false;
                            }
                        });
                        //显示没数据View
                    }
                } else {
                    //访问返回了错误代码  直接清除数据好了
                    dateAdapter.clear();

                    //token过期 避免重复弹框
                    if (ApiConfig.isNeedLogin(result.getErrorCode())) {
                        //重新登录
                        showTokenDialog();
                        return;
                    }
                    if (!StringUtil.isEmpty(result.getErrorInfo())) {
                        Toast.makeText(context, result.getErrorInfo(), Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(context, context.getResources().getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
            }

        }
    }


}
