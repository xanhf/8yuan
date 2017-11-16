package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.integral.IntegralDetailData;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.me.adapter.IntegralDetailAdapter;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.view.picker.wheelPicker.picker.DatePicker;
import com.trade.eight.view.picker.wheelPicker.picker.OptionPicker;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：Created by ocean
 * 时间：on 16/12/15.
 * 积分明细
 */

public class IntegralDetailActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ListView> {
    PullToRefreshListView pullToRefreshListView;
    ListView listView = null;
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
    protected SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy年MM月");
    private String queryDate;
    private String status = "0";
    private static final String STATUS_GET = "1";// 获取积分
    private static final String STATUS_DESTORY = "2";//消耗积分

    private int pageNo = 1;
    IntegralDetailAdapter integralDetailAdapter;

    LinearLayout line_empty;

    LinearLayout line_query_date;
    LinearLayout line_query_type;

    TextView text_exhistory_timelable;
    TextView text_exhistory_typelable;

    public static void startIntegralDetailAct(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, IntegralDetailActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryDate = sdf.format(new Date());
        setContentView(R.layout.activity_integral_detail);
        initView();
    }

    void initView() {
        setAppCommonTitle(getResources().getString(R.string.integral_detailtitle));
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setPullLoadEnabled(true);
        pullToRefreshListView.setPullRefreshEnabled(true);
        pullToRefreshListView.setOnRefreshListener(this);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setVisibility(View.VISIBLE);
        listView.setSelector(getResources().getDrawable(R.drawable.bg_transpert));
        listView.setDividerHeight(0);

        integralDetailAdapter = new IntegralDetailAdapter(this, 0, new ArrayList<IntegralDetailData>());
        listView.setAdapter(integralDetailAdapter);

        listView.setDividerHeight(0);
        line_empty = (LinearLayout) findViewById(R.id.emptyView);
        TextView emptyTv = (TextView) findViewById(R.id.emptyTv);
        emptyTv.setText(R.string.integral_integral_empty);

        text_exhistory_timelable = (TextView) findViewById(R.id.text_exhistory_timelable);
        text_exhistory_timelable.setText(sdf_1.format(new Date()));

        text_exhistory_typelable  = (TextView) findViewById(R.id.text_exhistory_typelable);

        line_query_date = (LinearLayout) findViewById(R.id.line_query_date);
        line_query_type = (LinearLayout) findViewById(R.id.line_query_type);

        line_query_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        line_query_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypePicker();
            }
        });

        pullToRefreshListView.setLastUpdatedLabel(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));

        getExHistoryTask();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo = 1;
        getExHistoryTask();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        pageNo += 1;
        getExHistoryTask();
    }

    private void getExHistoryTask() {
        new AsyncTask<Void, Void, CommonResponse4List<IntegralDetailData>>() {

            @Override
            protected CommonResponse4List<IntegralDetailData> doInBackground(Void... params) {
                UserInfoDao userInfoDao = new UserInfoDao(IntegralDetailActivity.this);
                if (!userInfoDao.isLogin()) {
                    return null;
                }
                Map<String, String> map = new HashMap<String, String>();
                map.put(UserInfo.UID, userInfoDao.queryUserInfo().getUserId());
                map.put("yearMonth", queryDate);
                map.put("page", pageNo + "");
                map.put("pageSize", "20");
                map.put("status", status);
                map = ApiConfig.getParamMap(IntegralDetailActivity.this, map);
                String res = HttpClientHelper.getStringFromPost(IntegralDetailActivity.this, AndroidAPIConfig.URL_ACCOUNT_INTEGRALDETAIL, map);
                CommonResponse4List<IntegralDetailData> response = CommonResponse4List.fromJson(res, IntegralDetailData.class);
                return response;
            }

            @Override
            protected void onPostExecute(CommonResponse4List<IntegralDetailData> result) {
                super.onPostExecute(result);
                pullToRefreshListView.onPullDownRefreshComplete();
                pullToRefreshListView.onPullUpRefreshComplete();
                if (pageNo == 1) {
                    pullToRefreshListView.setLastUpdatedLabel(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
                }
                if (result != null) {
                    if (result.isSuccess()) {
                        List<IntegralDetailData> listData = result.getData();
                        if(pageNo==1){
                            integralDetailAdapter.clear();
                        }
                        if (listData == null || listData.size() == 0) {
                            if (integralDetailAdapter.getCount() != 0) {
                                showCusToast(getResources().getString(R.string.data_no_more));
                            } else {
                                line_empty.setVisibility(View.VISIBLE);
                                pullToRefreshListView.setVisibility(View.GONE);
                            }
                        } else {
                            line_empty.setVisibility(View.GONE);
                            pullToRefreshListView.setVisibility(View.VISIBLE);
                            for (IntegralDetailData integralDetailData : listData) {
                                integralDetailAdapter.add(integralDetailData);
                            }
                            integralDetailAdapter.notifyDataSetChanged();
                        }
                    } else {
                        showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "网络连接失败！"));
                    }
                } else {
                    showCusToast(getResources().getString(R.string.network_problem));
                }
            }
        }.execute();
    }
    private void showDatePicker(){
        DatePicker picker = new DatePicker(this, DatePicker.YEAR_MONTH);
        picker.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        picker.setRangeStart(2016, 12, 14);
        picker.setRangeEnd(2020, 11, 11);
//        picker.setSelectedItem(2016, 12);
        picker.setSelectedItem(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH)+1);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthPickListener() {
            @Override
            public void onDatePicked(String year, String month) {
                text_exhistory_timelable.setText(year+"年"+month+"月");
                pageNo = 1;
                queryDate = year+"-"+month;
                getExHistoryTask();
            }
        });
        picker.show();
    }

    private void showTypePicker(){
        OptionPicker picker = new OptionPicker(IntegralDetailActivity.this, new String[]{
                "全部", "只看获得积分", "只看消费积分"
        });
        picker.setOffset(1);
        picker.setSelectedIndex(0);
        picker.setTextSize(15);
        picker.setOnItemPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onItemPicked(int index, String item) {
                status = index+"";
                text_exhistory_typelable.setText(item);
                pageNo = 1;
                getExHistoryTask();
            }
        });
        picker.show();
    }
}

