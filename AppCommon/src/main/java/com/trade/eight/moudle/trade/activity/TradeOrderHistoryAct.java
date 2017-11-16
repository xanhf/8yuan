package com.trade.eight.moudle.trade.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.kchart.fragment.KLineFragment;
import com.trade.eight.moudle.me.fragment.CashHistoryFrag;
import com.trade.eight.moudle.me.fragment.CashInFrag;
import com.trade.eight.moudle.me.fragment.CashOutFrag;
import com.trade.eight.moudle.trade.TradeHistorySelectTimeEvent;
import com.trade.eight.moudle.trade.adapter.TradeHistoryCloseAdapter;
import com.trade.eight.moudle.trade.adapter.TradeHistoryCreateAdapter;
import com.trade.eight.moudle.trade.fragment.TradeHistoryCloseFrag;
import com.trade.eight.moudle.trade.fragment.TradeHistoryCreateFrag;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.trade.TradeSortUtil;
import com.trade.eight.view.AppTitleView;
import com.trade.eight.view.UnderLineTextView;
import com.trade.eight.view.picker.wheelPicker.picker.DatePicker;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/7.
 * 历史订单
 */

public class TradeOrderHistoryAct extends BaseActivity implements View.OnClickListener {

    private String TAG = TradeOrderHistoryAct.class.getSimpleName();

    AppTitleView appTitleView;
    View line_starttime;
    TextView text_starttime;
    ImageView img_starttime;
    View line_endtime;
    TextView text_endtime;
    ImageView img_endtime;
    TextView text_true_profitloss;
    TextView text_createnum;
    TextView text_closenum;


    TextView text_lable_2;
    TextView text_lable_3;
    TextView text_lable_4;

    ViewPager viewPager;
    UnderLineTextView tv_1, tv_2, selectView;
    private MFragPageAdapter mMyFragPageAdapter;

    private List<BaseFragment> fragments = new ArrayList<BaseFragment>();

    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    int selectIndex = 0;

    TradeLoginDlg tradeLoginDlg;

    public static void startAct(Context context) {
        Intent intent = new Intent(context, TradeOrderHistoryAct.class);
        context.startActivity(intent);
    }

    public static void startAct(Context context, int index) {
        Intent intent = new Intent(context, TradeOrderHistoryAct.class);
        intent.putExtra("selectIndex", index);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_tradeorder_history);
        initData();
        initFragments();
        initView();
    }

    public void initFragments() {

        TradeHistoryCreateFrag tradeHistoryCreateFrag = new TradeHistoryCreateFrag();
        TradeHistoryCloseFrag tradeHistoryCloseFrag = new TradeHistoryCloseFrag();

        Bundle bundle = new Bundle();
        bundle.putString("startTime", getDefaultStartTime());
        bundle.putString("endTime", "");
        tradeHistoryCreateFrag.setArguments(bundle);
        tradeHistoryCloseFrag.setArguments(bundle);

        fragments.add(tradeHistoryCreateFrag);
        fragments.add(tradeHistoryCloseFrag);

        //初始化可以见
        tradeHistoryCreateFrag.onFragmentVisible(true);
    }

    private void initData() {
        selectIndex = getIntent().getIntExtra("selectIndex", 0);
    }

    /**
     * 获取默认时间  7天前
     *
     * @return
     */
    private String getDefaultStartTime() {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - 7);
        return dft.format(date.getTime());
    }

    private void initView() {
        appTitleView = (AppTitleView) findViewById(R.id.title);
        appTitleView.setBaseActivity(TradeOrderHistoryAct.this);
        appTitleView.setAppCommTitle(R.string.trade_history);
        appTitleView.setSpiltLineDisplay(false);

        line_starttime = findViewById(R.id.line_starttime);
        text_starttime = (TextView) findViewById(R.id.text_starttime);
        img_starttime = (ImageView) findViewById(R.id.img_starttime);
        line_endtime = findViewById(R.id.line_endtime);
        text_endtime = (TextView) findViewById(R.id.text_endtime);
        img_endtime = (ImageView) findViewById(R.id.img_endtime);
        text_true_profitloss = (TextView) findViewById(R.id.text_true_profitloss);
        text_createnum = (TextView) findViewById(R.id.text_createnum);
        text_closenum = (TextView) findViewById(R.id.text_closenum);
        tv_1 = (UnderLineTextView) findViewById(R.id.tv_1);
        tv_2 = (UnderLineTextView) findViewById(R.id.tv_2);
        tv_1.setOnClickListener(this);
        tv_2.setOnClickListener(this);
        selectView = tv_1;
        selectView.setSelected(true);
        selectView.setIsLineEnable(true);

        viewPager = (ViewPager) findViewById(R.id.tradeViewPager);
        viewPager.setOffscreenPageLimit(2);
        mMyFragPageAdapter = new MFragPageAdapter(getSupportFragmentManager(), TAG);
        viewPager.setAdapter(mMyFragPageAdapter);
        viewPager.setOnPageChangeListener(pageChangerListener);

        text_lable_2 = (TextView) findViewById(R.id.text_lable_2);
        text_lable_3 = (TextView) findViewById(R.id.text_lable_3);
        text_lable_4 = (TextView) findViewById(R.id.text_lable_4);

        text_lable_2.setVisibility(View.INVISIBLE);
        text_lable_3.setVisibility(View.INVISIBLE);
        text_lable_4.setText(R.string.trade_order_createprice);

        text_starttime.setText(getDefaultStartTime());
        text_endtime.setText("今天");

        line_starttime.setOnClickListener(this);
        line_endtime.setOnClickListener(this);

        getOrderHistoryTotal();

        if (selectIndex != 0) {
            tv_2.performClick();
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.line_starttime:
                showDatePicker(true);
                img_starttime.setImageResource(R.drawable.img_deal_btn_packup);
                break;
            case R.id.line_endtime:
                showDatePicker(false);
                img_endtime.setImageResource(R.drawable.img_deal_btn_packup);
                break;
            case R.id.tv_1:
                viewPager.setCurrentItem(0);

//                listView.setAdapter(tradeHistoryCreateAdapter);
//                getOrderHistoryCreate();

                text_lable_2.setVisibility(View.INVISIBLE);
                text_lable_3.setVisibility(View.INVISIBLE);
                text_lable_4.setText(R.string.trade_order_createprice);
                break;
            case R.id.tv_2:
//                selectView.setSelected(false);
//                selectView.setIsLineEnable(false);
//                selectView = tv_2;
//                selectView.setSelected(true);
                viewPager.setCurrentItem(1);

//                listView.setAdapter(tradeHistoryCloseAdapter);
//                getOrderHistoryClose();
                text_lable_2.setVisibility(View.VISIBLE);
                text_lable_3.setVisibility(View.VISIBLE);
                text_lable_2.setText(R.string.trade_order_createprice);
                text_lable_3.setText(R.string.trade_order_closeprice);
                text_lable_4.setText(R.string.lable_true_profitloss);
                break;
        }
    }

    /**
     * 开始时间
     *
     * @return
     */
    private String getStartTime() {
        String startTime = text_starttime.getText().toString();
        if (startTime.equals("今天")) {
            return "";
        }
        return startTime;
    }

    /**
     * 结束时间
     */
    private String getEndTime() {

        String endTime = text_endtime.getText().toString();
        if (endTime.equals("今天")) {
            return "";
        }
        return endTime;
    }

    /**
     * 展示时间段统计信息
     *
     * @param tradeInfoData
     */
    private void displayTotalInfo(TradeInfoData tradeInfoData) {
        if (tradeInfoData == null) {
            text_true_profitloss.setText("--");
            text_createnum.setText("--");
            text_closenum.setText("--");
            return;
        }
        text_true_profitloss.setTextColor(getResources().getColor(R.color.c_06A969));

        text_true_profitloss.setText(tradeInfoData.getProfitLoss());
        if (Double.parseDouble(tradeInfoData.getProfitLoss()) > 0) {
            text_true_profitloss.setTextColor(getResources().getColor(R.color.c_EA4A5E));
            text_true_profitloss.setText("+" + tradeInfoData.getProfitLoss());
        }
        text_createnum.setText(getResources().getString(R.string.lable_tr_ordernumber, tradeInfoData.getOpen()));
        text_closenum.setText(getResources().getString(R.string.lable_tr_ordernumber, tradeInfoData.getClose()));
    }

    /**
     * 获取历史盈利汇总
     */
    private void getOrderHistoryTotal() {
        HashMap<String, String> map = new HashMap<>();
        map.put("dayStart", getStartTime());
        map.put("dayEnd", getEndTime());
        HttpClientHelper.doPostOption(TradeOrderHistoryAct.this,
                AndroidAPIConfig.URL_ORDERHISTORY_TOTAL,
                map,
                null,
                new NetCallback(TradeOrderHistoryAct.this) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        if (ApiConfig.isNeedLogin(resultCode)) {

                            if (tradeLoginDlg == null) {
                                tradeLoginDlg = new TradeLoginDlg(TradeOrderHistoryAct.this);
                            }
                            if (!tradeLoginDlg.isShowingDialog()) {
                                tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                            }
                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        CommonResponse<TradeInfoData> commonResponse = CommonResponse.fromJson(response, TradeInfoData.class);
                        TradeInfoData tradeInfoData = commonResponse.getData();
                        displayTotalInfo(tradeInfoData);
                    }
                }
                , false);
    }

    /**
     * 设置fragment的可见状态
     *
     * @param index
     */
    void visibleFrags(int index) {
        if (fragments == null || fragments.size() == 0)
            return;

        //滚动到原点
//        scrollView.scrollTo(0, 0);
//        scrollView.smoothScrollTo(0, 0);
        for (int i = 0; i < fragments.size(); i++) {
            BaseFragment fragment = fragments.get(i);
            if (fragment == null)
                continue;
            //只是存在内存 isadd 为false
            if (!fragment.isAdded()) {
                getSupportFragmentManager().beginTransaction().attach(fragment).commit();
            }
            if (index == i)
                fragment.onFragmentVisible(true);
            else
                fragment.onFragmentVisible(false);
        }
    }

    private ViewPager.OnPageChangeListener pageChangerListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            // TODO Auto-generated method stub
            if (selectView != null) {
                selectView.setSelected(false);
                selectView.setIsLineEnable(false);
            }
            if (position == 0) {
                selectView = tv_1;
                if (selectView != null)
                    selectView.setSelected(true);

                visibleFrags(0);

            } else if (position == 1) {
                selectView = tv_2;
                if (selectView != null)
                    selectView.setSelected(true);

                visibleFrags(1);

            }
            if (selectView != null) {
                selectView.setIsLineEnable(true);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // TODO Auto-generated method stub
        }
    };

    class MFragPageAdapter extends FragmentPagerAdapter {

        public MFragPageAdapter(FragmentManager fm, String uniqueFlag) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        public void setItem(List<BaseFragment> list) {
            fragments.clear();
            if (list != null)
                fragments.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragments.get(arg0);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return fragments.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            return super.instantiateItem(container, position);
        }
    }


    /**
     * 执行查询操作
     */
    private void doQuery() {
        if (selectView == null) {
            return;
        }
        getOrderHistoryTotal();
        EventBus.getDefault().post(new TradeHistorySelectTimeEvent(getStartTime(), getEndTime()));
    }

    /**
     * 时间选择器确认
     *
     * @param isStart
     * @param year
     * @param month
     * @param day
     */
    private void datePickerSubmit(boolean isStart, String year, String month, String day) {
        String date = year + "-" + month + "-" + day;
        if (isStart) {
            img_starttime.setImageResource(R.drawable.img_deal_btn_expand);
            text_starttime.setText(date);
        } else {
            img_endtime.setImageResource(R.drawable.img_deal_btn_expand);
            if (date.equals(sdf.format(new Date()))) {
                text_endtime.setText("今天");
            } else {
                text_endtime.setText(date);
            }
        }
        doQuery();
    }

    /**
     * 展示时间选择器
     *
     * @param isStart
     */
    private void showDatePicker(final boolean isStart) {
        final DatePicker picker = new DatePicker(this, DatePicker.YEAR_MONTH_DAY);
        picker.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        picker.setRangeStart(2017, 07, 01);
        picker.setRangeEnd(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        picker.setSelectedItem(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                datePickerSubmit(isStart, year, month, day);
            }
        });
        picker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (isStart) {
                    img_starttime.setImageResource(R.drawable.img_deal_btn_expand);
                } else {
                    img_endtime.setImageResource(R.drawable.img_deal_btn_expand);
                }
            }
        });
        picker.show();
//        picker.show(0, Utils.dip2px(TradeOrderHistoryAct.this, 88));
    }
}
