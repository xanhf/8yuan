package com.trade.eight.moudle.trade.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.City;
import com.trade.eight.entity.trade.Province;
import com.trade.eight.moudle.trade.adapter.CityAdapter;
import com.trade.eight.moudle.trade.adapter.ProvinceAdapter;
import com.trade.eight.moudle.trade.cashinout.CashOutBindCardAct;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;
import com.trade.eight.view.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fangzhu on 16/6/5.
 */
public class CityAct extends BaseActivity implements
        PullToRefreshBase.OnRefreshListener<ListView>, View.OnClickListener{
    public static final String TAG = "CityAct";

    CityAct context = this;
    private PullToRefreshListView mPullRefreshListView;
    private ListView listView;
    //adapter
    CityAdapter adapter = null;
    String id;
    Province p;

    EditText edit_search;
    private List<City> listDefault;
    private List<City> listSearch = new ArrayList<City>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        id = getIntent().getStringExtra("id");
        p = (Province)getIntent().getSerializableExtra("object");
        id = p.getCode();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_province);
        setAppCommonTitle("选择城市");
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

        adapter = new CityAdapter(context, 0, new ArrayList<City>());
        adapter.setCity(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                City province = (City)adapterView.getAdapter().getItem(i);
                Intent intent = new Intent();
                intent.putExtra("object", province);
                setResult(CashOutBindCardAct.CODE_RES_GET_CITY, intent);
                finish();
            }
        });

        listView.setAdapter(adapter);

        mPullRefreshListView.doPullRefreshing(true, 0);

        edit_search = (EditText) findViewById(R.id.edit_search);
        edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearchAction();
                }
                return false;
            }
        });
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                doSearchAction();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
    class GetDataTask extends AsyncTask<String, Void, CommonResponse4List<City>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected CommonResponse4List<City> doInBackground(String... params) {
            try {
                Map<String, String> map = ApiConfig.getCommonMap(context);
                map.put("provinceCode", id);
                map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, map));
                String res = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_GET_CITY, map);
                return CommonResponse4List.fromJson(res, City.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(CommonResponse4List<City> result) {
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
                        listDefault = result.getData();
                        adapter.setItems(result.getData(), false);
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

    private void doSearchAction(){
        String input = edit_search.getText().toString();
        if(TextUtils.isEmpty(input)){
            adapter.clear();
            if (listDefault != null && listDefault.size() > 0) {
                adapter.setItems(listDefault, false);
            }
        }else{
            if (listDefault == null && listDefault.size() == 0) {
                return;
            }
            listSearch.clear();
            for(City city : listDefault){
                if(city.getName().contains(input.trim())||input.trim().contains(city.getName())){
                    listSearch.add(city);
                }
            }
            if(listSearch.size()>0){
                adapter.clear();
                adapter.setItems(listSearch, false);
            }
        }
    }
}
