package com.trade.eight.moudle.trade.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.Banks;
import com.trade.eight.moudle.trade.adapter.BankListAdapter;
import com.trade.eight.moudle.trade.cashinout.CashOutBindCardAct;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by fangzhu on 16/6/5.
 */
public class BankListAct extends BaseActivity implements
        PullToRefreshBase.OnRefreshListener<ListView>, View.OnClickListener{
    public static final String TAG = "BankListAct";

    BankListAct context = this;
    private PullToRefreshListView mPullRefreshListView;
    private ListView listView;
    //adapter
    BankListAdapter adapter = null;

    String api, bankName;

    /**
     * 指定一个接口地址获取银行列表
     * @param context
     * @param api
     */
    public static void startByApi (Context context, String api) {
        Intent intent = new Intent(context, BankListAct.class);
        intent.putExtra("api", api);
        context.startActivity(intent);
    }
    public static void start (Context context, String selectBank) {
        Intent intent = new Intent(context, BankListAct.class);
        intent.putExtra("bankName", selectBank);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        api = getIntent().getStringExtra("api");
        if (StringUtil.isEmpty(api)) {
            api = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_BANK_LIST);
        }
        bankName = getIntent().getStringExtra("bankName");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_bank_list);
        setAppCommonTitle("选择银行");
        initViews();
    }


    void initViews () {
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setPullRefreshEnabled(true);
        mPullRefreshListView.setPullLoadEnabled(false);
        listView = mPullRefreshListView.getRefreshableView();
        listView.setHeaderDividersEnabled(false);
        listView.setFooterDividersEnabled(false);
//		listView.setDivider(getResources().getDrawable(R.drawable.app_common_list_divider));
//		listView.setDividerHeight((int) getResources().getDimension(R.dimen.liveroom_divider_h));
        listView.setDividerHeight(0);

        adapter = new BankListAdapter(context, 0, new ArrayList<Banks>());
        adapter.setBankName(bankName);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Banks item = (Banks)adapterView.getAdapter().getItem(i);
                Intent intent = new Intent();
                intent.putExtra("bank", item);
                setResult(CashOutBindCardAct.CODE_RES_GET_BANK, intent);
                finish();
            }
        });
        mPullRefreshListView.doPullRefreshing(true, 0);

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        new GetDataTask().execute();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        new GetDataTask().execute();
    }

    //get
    class GetDataTask extends AsyncTask<String, Void, CommonResponse4List<Banks>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected CommonResponse4List<Banks> doInBackground(String... params) {
            try {
                Map<String, String> map = ApiConfig.getCommonMap(context);
                map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, map));
                String res = HttpClientHelper.getStringFromPost(context, api, map);
                return CommonResponse4List.fromJson(res, Banks.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CommonResponse4List<Banks> result) {
            // TODO Auto-generated method stub
            if (isFinishing())
                return;
            super.onPostExecute(result);
            mPullRefreshListView.onPullDownRefreshComplete();
            mPullRefreshListView.onPullUpRefreshComplete();

            if (result != null) {
                //refresh adapter
                if (result.isSuccess()) {
                    adapter.clear();
                    if (result.getData() != null && result.getData().size() > 0) {
                        List<Banks> list = result.getData();
                        Collections.sort(list, new Comparator<Banks>() {
                            @Override
                            public int compare(Banks banks, Banks t1) {
                                //降序
                                return t1.getTop() - banks.getTop();
                            }
                        });
                        adapter.setItems(list, true);
                    }
                } else {
                    showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "网络连接失败！"));
                }

                BaseInterface.setPullFormartRefreshTime(mPullRefreshListView);
            } else {
                showCusToast("网络连接失败！");
            }

        }
    }
}
