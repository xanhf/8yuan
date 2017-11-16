package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.integral.ExHistoryData;
import com.trade.eight.entity.integral.GoodsData;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.me.adapter.IntegralExHistoryAdapter;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.view.picker.wheelPicker.picker.DatePicker;
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
 * 积分兑换历史
 */

public class IntegralExchangeHistoryActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ListView> {
    LinearLayout line_query;
    TextView text_exhistory_timelable;
    ImageView img_exhistory_timelable;

    PullToRefreshListView pullToRefreshListView;
    ListView listView = null;
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
    protected SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy年MM月");
    private String queryDate;
    private int pageNo = 1;

    LinearLayout line_empty;

    IntegralExHistoryAdapter integralExHistoryAdapter;

    public static void startIntegralExchangeHistoryAct(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, IntegralExchangeHistoryActivity.class);
        context.startActivity(intent);
    }

    public String getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(String queryDate) {
        this.queryDate = queryDate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryDate = sdf.format(new Date());
        setContentView(R.layout.activity_integral_exhistory);
        initView();
    }

    void initView() {
        setAppCommonTitle(getResources().getString(R.string.integral_exhistory));
        line_query = (LinearLayout) findViewById(R.id.line_query);
        line_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* */

                showDatePicker();
            }
        });
        text_exhistory_timelable = (TextView) findViewById(R.id.text_exhistory_timelable);
        text_exhistory_timelable.setText(sdf_1.format(new Date()));
        img_exhistory_timelable = (ImageView) findViewById(R.id.img_exhistory_timelable);

        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setPullLoadEnabled(true);
        pullToRefreshListView.setPullRefreshEnabled(true);
        pullToRefreshListView.setOnRefreshListener(this);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setVisibility(View.VISIBLE);
        listView.setDividerHeight(0);
        listView.setSelector(getResources().getDrawable(R.drawable.bg_transpert));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<ExHistoryData> objects = integralExHistoryAdapter.objects;
                if (objects != null) {
                    if(objects.get(position).getGiftType()== GoodsData.GIFTTYPE_TE){
                        GoodsActDetailHistoryActivity.startAct(IntegralExchangeHistoryActivity.this,objects.get(position));
                    }
                }
            }
        });
        line_empty = (LinearLayout) findViewById(R.id.emptyView);
        TextView emptyTv = (TextView) findViewById(R.id.emptyTv);
        emptyTv.setText(R.string.integral_exhistory_empty);
        integralExHistoryAdapter = new IntegralExHistoryAdapter(this, 0, new ArrayList<ExHistoryData>());
        listView.setAdapter(integralExHistoryAdapter);

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
        new AsyncTask<Void, Void, CommonResponse4List<ExHistoryData>>() {

            @Override
            protected CommonResponse4List<ExHistoryData> doInBackground(Void... params) {
                UserInfoDao userInfoDao = new UserInfoDao(IntegralExchangeHistoryActivity.this);
                if (!userInfoDao.isLogin()) {
                    return null;
                }
                Map<String, String> map = new HashMap<String, String>();
                map.put(UserInfo.UID, userInfoDao.queryUserInfo().getUserId());
                map.put("yearMonth", queryDate);
                map.put("page", pageNo + "");
                map.put("pageSize", "20");
                map = ApiConfig.getParamMap(IntegralExchangeHistoryActivity.this, map);
                String res = HttpClientHelper.getStringFromPost(IntegralExchangeHistoryActivity.this, AndroidAPIConfig.URL_ACCOUNT_INTEGRAL_EXCHANGEHISTORY, map);
                CommonResponse4List<ExHistoryData> response = CommonResponse4List.fromJson(res, ExHistoryData.class);
                return response;
            }

            @Override
            protected void onPostExecute(CommonResponse4List<ExHistoryData> result) {
                super.onPostExecute(result);
                pullToRefreshListView.onPullDownRefreshComplete();
                pullToRefreshListView.onPullUpRefreshComplete();
                if (pageNo == 1) {
                    pullToRefreshListView.setLastUpdatedLabel(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
                }

                if (result != null) {
                    if (result.isSuccess()) {
                        List<ExHistoryData> listData = result.getData();
                        if (pageNo == 1) {
                            integralExHistoryAdapter.clear();
                        }
                        if (listData == null || listData.size() == 0) {
                            if (integralExHistoryAdapter.getCount() != 0) {
                                showCusToast(getResources().getString(R.string.data_no_more));
                            } else {
                                line_empty.setVisibility(View.VISIBLE);
                                pullToRefreshListView.setVisibility(View.GONE);
                            }
                        } else {
                            line_empty.setVisibility(View.GONE);
                            pullToRefreshListView.setVisibility(View.VISIBLE);
                            for (ExHistoryData exHistoryData : listData) {
                                integralExHistoryAdapter.add(exHistoryData);
                            }
                            integralExHistoryAdapter.notifyDataSetChanged();
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

    private void datePickerSubmit(String year, String month) {
        text_exhistory_timelable.setText(year + "年" + month + "月");
        pageNo = 1;
        queryDate = year + "-" + month;
        getExHistoryTask();
    }

    private void showDatePicker() {
        DatePicker picker = new DatePicker(this, DatePicker.YEAR_MONTH);
        picker.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        picker.setRangeStart(2016, 12, 14);
        picker.setRangeEnd(2020, 11, 11);
        picker.setSelectedItem(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthPickListener() {
            @Override
            public void onDatePicked(String year, String month) {
                datePickerSubmit(year, month);
            }
        });
        picker.show();
    }
}

