package com.trade.eight.moudle.trade.create;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AppSetting;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.moudle.home.fragment.TradeContentListFrag;
import com.trade.eight.moudle.home.trade.ProductObj;
import com.trade.eight.moudle.trade.adapter.TradePListAdapter;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.Utils;
import com.trade.eight.tools.refresh.RefreshUtil;
import com.trade.eight.tools.trade.ExchangeConfig;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dufangzhu on 2017/6/3.
 * <p>
 * 直播室建仓产品列表
 */

public class LivePListDlg implements PullToRefreshBase.OnRefreshListener<ListView> {

    BaseActivity context;
    RefreshUtil refreshUtil;
    TradePListAdapter dateAdapter = null;
    private PullToRefreshListView mPullRefreshListView;
    private ListView listView;
    List<ProductObj> optionalList = null;

    Dialog dialog;

    public LivePListDlg(BaseActivity context) {
        this.context = context;

        dialog = new Dialog(context, R.style.dialog_trade);
        View rootView = View.inflate(context, R.layout.p_list_live, null);
        dialog.setContentView(rootView);
        findViews(rootView);
        initRefresh();
    }

    public void findViews(View view) {
        mPullRefreshListView = (PullToRefreshListView) view
                .findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setPullRefreshEnabled(true);
        mPullRefreshListView.setPullLoadEnabled(false);
        listView = mPullRefreshListView.getRefreshableView();
        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(false);
        listView.setDividerHeight(0);

        dateAdapter = new TradePListAdapter(context, 0, new ArrayList<ProductObj>());
        dateAdapter.setKeepTopTemp(false);
        listView.setAdapter(dateAdapter);

        BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
//        mPullRefreshListView.doPullRefreshing(true, 0);
    }


    public boolean isShowingDialog() {
        if (dialog != null)
            return dialog.isShowing();
        return false;
    }

    public void showDialog(int aniStyle) {
        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int screenWidth = displayMetrics.widthPixels;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        int h = (int) context.getResources().getDimension(R.dimen.margin_448dp);
        int h2 = (int)(Utils.getScreenH(context) * 0.7);
        if (h > (int)(Utils.getScreenH(context) * 0.9)) {
            //认为是超过了屏幕
            h = h2;
        }
        params.height = h;
                w.setGravity(Gravity.BOTTOM);
        w.setWindowAnimations(aniStyle);
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                if (refreshUtil != null)
//                    refreshUtil.stop();
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
//                if (UNavConfig.isShowStep03(context)) {
//                    StepNavAct.start(context, StepNavAct.TYPE_STP_03);
//                    context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);// 淡出淡入动画效果
//                }
            }
        });
//        dialog.show();
//        initRefresh();
        mPullRefreshListView.doPullRefreshing(true, 0);

    }

    void initRefresh() {
        refreshUtil = new RefreshUtil(context);
        refreshUtil.setRefreshTime(AppSetting.getInstance(context).getRefreshTimeWPList());
        refreshUtil.setOnRefreshListener(new RefreshUtil.OnRefreshListener() {
            @Override
            public Object doInBackground() {
                try {
                    if (context.isFinishing())
                        return null;
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
                if (context.isFinishing())
                    return;
                doMyPostExecute();
            }
        });
    }


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

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

        new GetDataPriceTask(true).execute();

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    //封装一下  仅仅作为刷新使用
    class GetDataPriceTask extends AsyncTask<String, Void, CommonResponse4List<ProductObj>> {
        private boolean isPullRefresh;

        public GetDataPriceTask(boolean isPullRefresh) {
            this.isPullRefresh = isPullRefresh;
        }

        @Override
        protected CommonResponse4List<ProductObj> doInBackground(String... params) {
            if (context.isFinishing())
                return null;

            //get list data
            CommonResponse4List<ProductObj> response4List = getData();
//            if (optionalList != null && optionalList.size() > 0) {
//                // 停盘标示
//                closeStr = optionalList.get(0).getIsClosed();
//            }
            return response4List;
        }

        @Override
        protected void onPostExecute(CommonResponse4List<ProductObj> result) {
            // TODO Auto-generated method stub
            if (context.isFinishing())
                return;
            super.onPostExecute(result);
            context.hideNetLoadingProgressDialog();

            BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
            mPullRefreshListView.onPullUpRefreshComplete();
            mPullRefreshListView.onPullDownRefreshComplete();

            if (result != null
                    && result.isSuccess()) {
                doMyPostExecute();
//                setListViewHeightBasedOnChildren(listView);
                //没有停盘
//                if (!TradeProduct.IS_CLOSE_YES.equals(closeStr)) {
//                    //开始刷新
//                    if (refreshUtil != null)
//                        refreshUtil.start();
//                }
                //不用判断是否停盘再刷新，外汇不是以一个品种来标记的
                if (refreshUtil != null) {
                    //先停止之前的刷新
                    refreshUtil.stop();
                    refreshUtil.start();
                }
                if (dialog != null && !dialog.isShowing())
                    dialog.show();

            } else {
                String msg = "网络连接失败！";
                if (result != null) {
                    msg = ConvertUtil.NVL(result.getErrorInfo(), msg);
                }
                context.showCusToast(msg);
            }

        }
    }

    void doMyPostExecute() {
        if (context.isFinishing())
            return;

        dateAdapter.setItems(optionalList);
    }
}
