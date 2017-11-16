package com.trade.eight.moudle.trade.activity;

import android.content.Context;
import android.content.Intent;
import android.media.DrmInitData;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.OnLineHelper;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trade.CheckBalanceData;
import com.trade.eight.moudle.trade.adapter.CheckBalanceCloseAdapter;
import com.trade.eight.moudle.trade.adapter.CheckBalanceCreateAdapter;
import com.trade.eight.moudle.trade.adapter.CheckBalanceHoldAdapter;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.Utils;
import com.trade.eight.view.UnderLineTextView;
import com.trade.eight.view.pulltorefresh.PullToRefreshExpandListView;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/4.
 * 结算账单
 */

public class CheckBalanceAct extends BaseActivity implements View.OnClickListener {


    Button btn_balancecheck;
    private PullToRefreshListView mPullRefreshListView;
    private ListView listView;


    TextView text_kefu;

    View headView;
    TextView text_qihuo_companey;
    TextView text_qihuo_username;
    TextView text_qihuo_account;
    TextView text_qihuo_currency;
    TextView text_qihuo_tradedate;
    TextView text_qihuo_querydate;
    TextView text_qihuo_srjc;
    TextView text_qihuo_drjc;
    TextView text_qihuo_dqqy;
    TextView text_qihuo_drcrj;
    TextView text_qihuo_kyzj;
    TextView text_qihuo_drpcyk;
    TextView text_qihuo_ztzj;
    TextView text_qihuo_drsxf;
    TextView text_qihuo_ccyk;
    TextView text_qihuo_yzjzj;
    TextView text_qihuo_zybzj;
    TextView text_qihuo_zjzyl;
    private RelativeLayout lable01, lable02, lable03;
    UnderLineTextView tv_1, tv_2, tv_3, selectView;
    private ImageView cursor;// 动画图片
    private View empty_view;
    private TextView text_checkbalance_empty;

    CheckBalanceData checkBalanceData;

    CheckBalanceCreateAdapter checkBalanceCreateAdapter = null;
    CheckBalanceCloseAdapter checkBalanceCloseAdapter = null;
    CheckBalanceHoldAdapter checkBalanceHoldAdapter = null;

    int position, listViemItemTop;


    public static void startAct(Context context) {
        Intent intent = new Intent(context, CheckBalanceAct.class);
        context.startActivity(intent);
    }

    public static void startAct(Context context, CheckBalanceData checkBalanceData) {
        Intent intent = new Intent(context, CheckBalanceAct.class);
        intent.putExtra("checkBalanceData", checkBalanceData);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_checkbalance);

        checkBalanceCreateAdapter = new CheckBalanceCreateAdapter(CheckBalanceAct.this, new ArrayList<CheckBalanceData.TransactionRecord>());
        checkBalanceCloseAdapter = new CheckBalanceCloseAdapter(CheckBalanceAct.this, new ArrayList<CheckBalanceData.PositionClose>());
        checkBalanceHoldAdapter = new CheckBalanceHoldAdapter(CheckBalanceAct.this, new ArrayList<CheckBalanceData.PositionsDetail>());
        initView();

        initData();
    }

    private void initData() {
        checkBalanceData = (CheckBalanceData) getIntent().getSerializableExtra("checkBalanceData");
        if (checkBalanceData == null) {
            getData();
        } else {
            displayBalanceInfo();
        }
    }


    private void initView() {

        text_kefu = (TextView) findViewById(R.id.text_kefu);
        text_kefu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnLineHelper.getInstance().startP2p(CheckBalanceAct.this);
            }
        });

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setPullRefreshEnabled(false);
        mPullRefreshListView.setPullLoadEnabled(false);
        listView = mPullRefreshListView.getRefreshableView();
        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(false);
        listView.setDividerHeight(0);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

                // 不滚动时保存当前滚动到的位置
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    position = listView.getFirstVisiblePosition();
                    View itemView = listView.getChildAt(0);
                    listViemItemTop = (itemView == null) ? 0 : itemView.getTop();
                }


            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });


        headView = View.inflate(this, R.layout.head_checkbalance, null);
        listView.addHeaderView(headView);

        listView.setAdapter(checkBalanceCreateAdapter);

        text_qihuo_companey = (TextView) headView.findViewById(R.id.text_qihuo_companey);
        text_qihuo_username = (TextView) headView.findViewById(R.id.text_qihuo_username);
        text_qihuo_account = (TextView) headView.findViewById(R.id.text_qihuo_account);
        text_qihuo_currency = (TextView) headView.findViewById(R.id.text_qihuo_currency);
        text_qihuo_tradedate = (TextView) headView.findViewById(R.id.text_qihuo_tradedate);
        text_qihuo_querydate = (TextView) headView.findViewById(R.id.text_qihuo_querydate);
        text_qihuo_srjc = (TextView) headView.findViewById(R.id.text_qihuo_srjc);
        text_qihuo_drjc = (TextView) headView.findViewById(R.id.text_qihuo_drjc);
        text_qihuo_dqqy = (TextView) headView.findViewById(R.id.text_qihuo_dqqy);
        text_qihuo_drcrj = (TextView) headView.findViewById(R.id.text_qihuo_drcrj);
        text_qihuo_kyzj = (TextView) headView.findViewById(R.id.text_qihuo_kyzj);
        text_qihuo_drpcyk = (TextView) headView.findViewById(R.id.text_qihuo_drpcyk);
        text_qihuo_ztzj = (TextView) headView.findViewById(R.id.text_qihuo_ztzj);
        text_qihuo_drsxf = (TextView) headView.findViewById(R.id.text_qihuo_drsxf);
        text_qihuo_ccyk = (TextView) headView.findViewById(R.id.text_qihuo_ccyk);
        text_qihuo_yzjzj = (TextView) headView.findViewById(R.id.text_qihuo_yzjzj);
        text_qihuo_zybzj = (TextView) headView.findViewById(R.id.text_qihuo_zybzj);
        text_qihuo_zjzyl = (TextView) headView.findViewById(R.id.text_qihuo_zjzyl);
        lable01 = (RelativeLayout) headView.findViewById(R.id.lable01);
        lable02 = (RelativeLayout) headView.findViewById(R.id.lable02);
        lable03 = (RelativeLayout) headView.findViewById(R.id.lable03);
        tv_1 = (UnderLineTextView) headView.findViewById(R.id.tab1tv);
        tv_2 = (UnderLineTextView) headView.findViewById(R.id.tab2tv);
        tv_3 = (UnderLineTextView) headView.findViewById(R.id.tab3tv);
        cursor = (ImageView) headView.findViewById(R.id.cursor);// 动画图片

        empty_view = headView.findViewById(R.id.empty_view);
        text_checkbalance_empty = (TextView) headView.findViewById(R.id.text_checkbalance_empty);

        if (tv_1 != null) {
            selectView = tv_1;
            selectView.setSelected(true);
        }

        lable01.setOnClickListener(this);
        lable02.setOnClickListener(this);
        lable03.setOnClickListener(this);


        btn_balancecheck = (Button) findViewById(R.id.btn_balancecheck);
        btn_balancecheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCheckBalanceOpt();
            }
        });
    }

    void displayBalanceInfo() {
        text_qihuo_companey.setText(R.string.lable_qihuo_companey);
        text_qihuo_username.setText(checkBalanceData.getClientName());
        text_qihuo_account.setText(checkBalanceData.getClientId());
        text_qihuo_currency.setText(checkBalanceData.getCurrency());
        text_qihuo_querydate.setText(checkBalanceData.getDate());
        text_qihuo_tradedate.setText(checkBalanceData.getCreationDate());

        text_qihuo_srjc.setText(checkBalanceData.getBalanceBF());
        text_qihuo_drjc.setText(checkBalanceData.getBalanceCF());
        text_qihuo_dqqy.setText(checkBalanceData.getClientEquity());
        text_qihuo_drcrj.setText(checkBalanceData.getDepositWithdrawal());
        text_qihuo_kyzj.setText(checkBalanceData.getFundAvail());
        text_qihuo_drpcyk.setText(checkBalanceData.getRealizedPL());
        text_qihuo_ztzj.setText("0.0");
        text_qihuo_drsxf.setText(checkBalanceData.getCommission());
        text_qihuo_ccyk.setText(checkBalanceData.getMtmPL());
        text_qihuo_yzjzj.setText(checkBalanceData.getMarginCall());
        text_qihuo_zybzj.setText(checkBalanceData.getMarginOccupied());
//        // 资金占用率=占用保证金/当前权益
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        try {
            String result = "0";
            if(new BigDecimal(ConvertUtil.NVL(checkBalanceData.getClientEquity(),"0")).doubleValue()!=0){
                result = numberFormat.format((1-new BigDecimal(checkBalanceData.getFundAvail()).doubleValue() / new BigDecimal(checkBalanceData.getClientEquity()).doubleValue()) * 100);
            }
            text_qihuo_zjzyl.setText(result + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }

        selectView = tv_1;
        selectView.setSelected(true);
        selectView.setIsLineEnable(false);
        if (checkBalanceData.getTransactionList() == null || checkBalanceData.getTransactionList().size() == 0) {
            empty_view.setVisibility(View.VISIBLE);
            text_checkbalance_empty.setText(R.string.checkbalance_empty_1);
        } else {
            empty_view.setVisibility(View.GONE);
            checkBalanceCreateAdapter.setData(checkBalanceData.getTransactionList());
            listView.setAdapter(checkBalanceCreateAdapter);
            listView.setSelection(0);
        }
    }

    private int currentIndex = 0;

    private void doAnimation(int position, int index) {
        int one = Utils.dip2px(CheckBalanceAct.this, 62);
        Animation animation = new TranslateAnimation(one * currentIndex,
                one * position, 0, 0);
        currentIndex = index;
        animation.setFillAfter(true);
        animation.setDuration(300);
        cursor.startAnimation(animation);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.lable01) {
            selectView.setSelected(false);
            selectView.setIsLineEnable(false);
            selectView = tv_1;
            selectView.setSelected(true);
            selectView.setIsLineEnable(false);
            if (checkBalanceData.getTransactionList() == null || checkBalanceData.getTransactionList().size() == 0) {
                empty_view.setVisibility(View.VISIBLE);
                text_checkbalance_empty.setText(R.string.checkbalance_empty_1);
                checkBalanceCreateAdapter.setData(checkBalanceData.getTransactionList());
                listView.setAdapter(checkBalanceCreateAdapter);
//                listView.setSelection(listView.getBottom());

                listView.setSelectionFromTop(position, listViemItemTop);

            } else {
                empty_view.setVisibility(View.GONE);
                checkBalanceCreateAdapter.setData(checkBalanceData.getTransactionList());
                listView.setAdapter(checkBalanceCreateAdapter);
                listView.setSelection(1);
            }
            doAnimation(0, 0);
        } else if (id == R.id.lable02) {
            selectView.setSelected(false);
            selectView.setIsLineEnable(false);
            selectView = tv_2;
            selectView.setSelected(true);
            selectView.setIsLineEnable(false);
            if (checkBalanceData.getCloseList() == null || checkBalanceData.getCloseList().size() == 0) {
                empty_view.setVisibility(View.VISIBLE);
                text_checkbalance_empty.setText(R.string.checkbalance_empty_2);
                checkBalanceCloseAdapter.setData(checkBalanceData.getCloseList());
                listView.setAdapter(checkBalanceCloseAdapter);
//                listView.setSelection(listView.getBottom());
                listView.setSelectionFromTop(position, listViemItemTop);

            } else {
                empty_view.setVisibility(View.GONE);
                checkBalanceCloseAdapter.setData(checkBalanceData.getCloseList());
                listView.setAdapter(checkBalanceCloseAdapter);
                listView.setSelection(1);
            }
            doAnimation(1, 1);
        } else if (id == R.id.lable03) {
            selectView.setSelected(false);
            selectView.setIsLineEnable(false);
            selectView = tv_3;
            selectView.setSelected(true);
            selectView.setIsLineEnable(false);
            if (checkBalanceData.getPositionsDetailList() == null || checkBalanceData.getPositionsDetailList().size() == 0) {
                empty_view.setVisibility(View.VISIBLE);
                text_checkbalance_empty.setText(R.string.checkbalance_empty_3);
                checkBalanceHoldAdapter.setData(checkBalanceData.getPositionsDetailList());
                listView.setAdapter(checkBalanceHoldAdapter);
//                listView.setSelection(listView.getBottom());
                listView.setSelectionFromTop(position, listViemItemTop);
            } else {
                empty_view.setVisibility(View.GONE);
                checkBalanceHoldAdapter.setData(checkBalanceData.getPositionsDetailList());
                listView.setAdapter(checkBalanceHoldAdapter);
                listView.setSelection(1);
            }
            doAnimation(2, 2);
        }
    }

    /*
     * 获取数据
        */
    private void getData() {

        HttpClientHelper.doPostOption(CheckBalanceAct.this,
                AndroidAPIConfig.URL_CHECKBALANCE_DETAIL,
                null,
                null,
                new NetCallback(CheckBalanceAct.this) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        showCusToast(resultMsg);
                        doMyfinish();
                    }

                    @Override
                    public void onResponse(String response) {
                        CommonResponse<CheckBalanceData> commonResponse = CommonResponse.fromJson(response, CheckBalanceData.class);
                        checkBalanceData = commonResponse.getData();
                        if (checkBalanceData == null) {
                            showCusToast("无可确认账单");
                            doMyfinish();
                        } else {
                            displayBalanceInfo();
                        }
                    }
                },
                true);
    }


    /*
     * 确认账单
*/
    private void doCheckBalanceOpt() {
        HashMap<String, String> map = new HashMap<String, String>();
        HttpClientHelper.doPostOption(CheckBalanceAct.this,
                AndroidAPIConfig.URL_CHECKBALANCE_OPTION,
                map,
                null,
                new NetCallback(CheckBalanceAct.this) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        showCusToast(resultMsg);
                    }

                    @Override
                    public void onResponse(String response) {
                        doMyfinish();
                        showCusToast("账单已确认");
                    }
                },
                true);
    }

}
