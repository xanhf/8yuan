package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.messagecenter.MessageData;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.moudle.me.adapter.MessageCenterAdapter;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.trade.activity.TradeVoucherAct;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.tools.ActivityUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.trade.TradeConfig;
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
 * Created by Administrator on 2015/9/9.
 */
public class WeipanMsgListActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ListView> {
    String TAG = "WeipanMsgListActivity";
    WeipanMsgListActivity context = this;
    PullToRefreshListView pullToRefreshListView = null;
    ListView listView = null;
    MessageCenterAdapter messageCenterAdapter;
    int page = 1;
    private String yearMonth = "";
    private String messageTypes = "";

    protected SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy年MM月");

    LinearLayout line_empty;

    LinearLayout line_query_date;
    LinearLayout line_query_type;

    TextView text_exhistory_timelable;
    TextView text_exhistory_typelable;

    public static void startAct(Context context){
        Intent intent = new Intent(context,WeipanMsgListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_weipa_msglist);
        if (!new UserInfoDao(WeipanMsgListActivity.this).isLogin()) {
            DialogUtil.showLoginConfirmDlg(context, "消息列表需要登录后才能使用", true, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    //跳转到登录页面
                    Map<String, String> map = new HashMap<String, String>();
                    startActivity(ActivityUtil.initAction(context, LoginActivity.class, ActivityUtil.ACT_MSGLIST, map));

                    return false;
                }
            });
            finish();
            return;
        }
        initViews();
        pullToRefreshListView.setLastUpdatedLabel(sdf.format(new Date()));
        pullToRefreshListView.doPullRefreshing(true, 10);
    }

    @Override
    public void doMyfinish() {
        readAllMsg();
        super.doMyfinish();
    }

    void initViews() {
        setAppCommonTitle(getResources().getString(R.string.mc_title));
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setPullLoadEnabled(true);
        pullToRefreshListView.setPullRefreshEnabled(true);
        listView = pullToRefreshListView.getRefreshableView();
        listView.setVisibility(View.VISIBLE);
        listView.setDividerHeight(0);

        messageCenterAdapter = new MessageCenterAdapter(context, 0, new ArrayList<MessageData>());
        listView.setAdapter(messageCenterAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessageData messageData = messageCenterAdapter.getItem(position);
                readMsg(messageData);
                SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
                viewHolder.get(R.id.text_mc_new).setVisibility(View.GONE);
                if (messageData.getMessageType() == 1 || messageData.getMessageType() == 8) {
                    //系统消息
                    if (!TextUtils.isEmpty(messageData.getUrl())) {
                        WebActivity.start(WeipanMsgListActivity.this, messageData.getMessageTitle(), messageData.getUrl());
                    }
                } else if (messageData.getMessageType() == 4 || messageData.getMessageType() == 5 || messageData.getMessageType() == 6) {
                    //券
                    int exchangId = 0;
                    if (TradeConfig.code_gg.equals(messageData.getExcode())) {
                        exchangId = 1;
                    } else if (TradeConfig.code_hg.equals(messageData.getExcode())) {
                        exchangId = 2;
                    } else if (TradeConfig.code_jn.equals(messageData.getExcode())) {
                        exchangId = 3;
                    }
                    if (exchangId == 0) {
                        return;
                    }
                    TradeVoucherAct.startTradeVoucherAct(WeipanMsgListActivity.this, exchangId);
                } else if (messageData.getMessageType() == 3 || messageData.getMessageType() == 7) {
                    //持仓

                }
            }
        });

        line_empty = (LinearLayout) findViewById(R.id.emptyView);
        TextView emptyTv = (TextView) findViewById(R.id.emptyTv);
        emptyTv.setText(R.string.mc_empty);

        text_exhistory_timelable = (TextView) findViewById(R.id.text_exhistory_timelable);
        text_exhistory_timelable.setText(sdf_1.format(new Date()));

        text_exhistory_typelable = (TextView) findViewById(R.id.text_exhistory_typelable);

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
    }

    /**
     * 获取消息列表数据
     *
     * @param yearMonth    年月查询
     * @param messageTypes 消息类型查询
     */
    void initMsgListTask(String yearMonth, String messageTypes) {
        HashMap<String, String> request = new HashMap<String, String>();
        request.put("userId", new UserInfoDao(WeipanMsgListActivity.this).queryUserInfo().getUserId());
        request.put("yearMonth", yearMonth);
        request.put("messageTypes", messageTypes);
        request.put("page", page + "");
        request.put("pageSize", "20");
        HttpClientHelper.doPostOption(WeipanMsgListActivity.this, AndroidAPIConfig.URL_MESSAGECENTER_LIST, request, null, new NetCallback(WeipanMsgListActivity.this) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                showCusToast(resultMsg);
            }

            @Override
            public void onResponse(String response) {
                pullToRefreshListView.onPullDownRefreshComplete();
                CommonResponse4List<MessageData> commonResponse4List = CommonResponse4List.fromJson(response, MessageData.class);
                List<MessageData> messageDataList = commonResponse4List.getData();
                if (messageDataList != null && messageDataList.size() > 0) {
                    line_empty.setVisibility(View.GONE);
                    pullToRefreshListView.setVisibility(View.VISIBLE);
                    messageCenterAdapter.setItems(messageDataList);
                } else {
                    line_empty.setVisibility(View.VISIBLE);
                    pullToRefreshListView.setVisibility(View.GONE);
                }
            }
        }, true);
    }

    /**
     * 读取单条消息
     *
     * @param messageData
     */
    void readMsg(MessageData messageData) {
        HashMap<String, String> request = new HashMap<String, String>();
        request.put("userId", new UserInfoDao(WeipanMsgListActivity.this).queryUserInfo().getUserId());
        request.put("messageDetailId", messageData.getMessageDetailId());
        HttpClientHelper.doPostOption(WeipanMsgListActivity.this, AndroidAPIConfig.URL_MESSAGECENTER_READ, request, null, new NetCallback(WeipanMsgListActivity.this) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {

            }

            @Override
            public void onResponse(String response) {

            }
        }, false);
    }

    /**
     * 读取所有消息
     */
    void readAllMsg() {
        HashMap<String, String> request = new HashMap<String, String>();
        request.put("userId", new UserInfoDao(WeipanMsgListActivity.this).queryUserInfo().getUserId());
        HttpClientHelper.doPostOption(WeipanMsgListActivity.this, AndroidAPIConfig.URL_MESSAGECENTER_READALL, request, null, new NetCallback(WeipanMsgListActivity.this) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {

            }

            @Override
            public void onResponse(String response) {

            }
        }, false);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        page = 1;
        initMsgListTask(yearMonth, messageTypes);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        ++page;
        initMsgListTask(yearMonth, messageTypes);

    }

    private void showDatePicker() {
        DatePicker picker = new DatePicker(this, DatePicker.YEAR_MONTH);
        picker.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        picker.setRangeStart(2017, 02, 14);
        picker.setRangeEnd(2020, 11, 11);
//        picker.setSelectedItem(2016, 12);
        picker.setSelectedItem(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthPickListener() {
            @Override
            public void onDatePicked(String year, String month) {
                text_exhistory_timelable.setText(year + "年" + month + "月");
                page = 1;
                yearMonth = year + "-" + month;
                initMsgListTask(yearMonth, messageTypes);
            }
        });
        picker.show();
    }

    private void showTypePicker() {
        OptionPicker picker = new OptionPicker(WeipanMsgListActivity.this, new String[]{
                "全部", "系统消息", "代金券", "平仓提醒",
        });
        picker.setOffset(1);
        picker.setSelectedIndex(0);
        picker.setTextSize(15);
        picker.setOnItemPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onItemPicked(int index, String item) {

                switch (index) {
                    case 0:
                        messageTypes = "";
                        break;
                    case 1:
                        messageTypes = MessageData.TYPE_XITONG + "," + MessageData.TYPE_JYFX;
                        break;
                    case 2:
                        messageTypes = MessageData.TYPE_QUANDAOQI + "," + MessageData.TYPE_QUANDAOZHANG + "," + MessageData.TYPE_QUANJJDAOQI;
                        break;
                    case 3:
                        messageTypes = MessageData.TYPE_BAOCANG + "," + MessageData.TYPE_ZYZS;
                        break;
                }
                text_exhistory_typelable.setText(item);
                page = 1;
                initMsgListTask(yearMonth, messageTypes);
            }
        });
        picker.show();
    }

}
