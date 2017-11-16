package com.trade.eight.moudle.trade.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trade.TradeRechargeTypeAndMoneyData;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.trade.activity.CashInAct;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.trade.TradeConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：Created by changhaiyang
 * 时间：on 16/9/22.
 * 充值选择金额
 */

public class CashInChooseMoneyFrag extends BaseFragment implements View.OnClickListener {
    public static final String P_API_VERSION_NO = "versionNo";
    //2017-01-17 接口使用版本号versionNo，控制显示返回的支付列表
    /**
     * moudify at
     * 2017-03-14 新增哈贵的民生微信支付用的是原生sdk支付，IWXPAY的方式,没上线 VALUE_VERSION_NO = 5就没用
     * 2017-03-29 新增农交所的支付宝支付，扫码链接，使用webview打开 跳转支付宝，VALUE_VERSION_NO = 5
     */
    public static final int VALUE_VERSION_NO = 5;


    int selectPos = 0;
    String money = "";

    //小额 dialog item选中的位置
    int littleMoneyPos = -1;
    GridView gridView;
    MyAdapter myAdapter;
    List<String> littleMoney = new ArrayList<String>();
    List<String> bigMoney = new ArrayList<String>();
    View contentlayout, appprogressbar;
    TextView ed_number, tv_totalMoney;
    RelativeLayout viewMore;
    Button btn_submit;
    CashInAct cashInAct = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        cashInAct = (CashInAct) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_cashin_choosemoney, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        gridView = (GridView) view.findViewById(R.id.gridView);
        myAdapter = new MyAdapter(cashInAct, 0, new ArrayList<String>());
        gridView.setAdapter(myAdapter);
        contentlayout = view.findViewById(R.id.contentlayout);
        appprogressbar = view.findViewById(R.id.layoutLoding);
        ed_number = (TextView) view.findViewById(R.id.ed_number);
        tv_totalMoney = (TextView) view.findViewById(R.id.tv_totalMoney);
        viewMore = (RelativeLayout) view.findViewById(R.id.viewMore);
        viewMore.setOnClickListener(this);
        btn_submit = (Button) view.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        getData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewMore:
                if (littleMoney == null)
                    return;
                AlertDialog.Builder builder = null;
                if (android.os.Build.VERSION.SDK_INT >= 11) {
                    builder = new AlertDialog.Builder(cashInAct, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
                } else {
                    builder = new AlertDialog.Builder(cashInAct);
                }
                String str[] = new String[littleMoney.size()];
                for (int i = 0; i < littleMoney.size(); i++) {
                    str[i] = littleMoney.get(i);
                }
                builder.setSingleChoiceItems(str, littleMoneyPos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            money = littleMoney.get(which);
                            ed_number.setText(littleMoney.get(which) + "元");
                            tv_totalMoney.setText(money);
                            littleMoneyPos = which;
                            selectPos = -1;
                            myAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                AlertDialog tipDialog = builder.create();
                tipDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                tipDialog.show();
                break;
            case R.id.btn_submit:
                cashInAct.chooseMoney(money,tradeRechargeTypeAndMoneyData);
                break;
        }
    }

    TradeRechargeTypeAndMoneyData tradeRechargeTypeAndMoneyData;

    //获取充值金额 面值
    void getData() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {

                    UserInfoDao dao = new UserInfoDao(cashInAct);
                    if (!dao.isLogin())
                        return null;
                    //新接口，替换旧版本的单独的接口
                    String url = AndroidAPIConfig.URL_RECHARGE_MONEYANDTYPE;
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(TradeConfig.PARAM_EXCHANGEID, TradeConfig.getExchangeId(getActivity(), TradeConfig.getCurrentTradeCode(getActivity())) + "");
                    map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
                    //2017-01-17 接口使用版本号versionNo，控制显示返回的列表
                    map.put(P_API_VERSION_NO, VALUE_VERSION_NO + "");

                    map = ApiConfig.getParamMap(getActivity(), map);
                    String res = TradeHelp.post(cashInAct, url, map);

                    CommonResponse<TradeRechargeTypeAndMoneyData> response = CommonResponse.fromJson(res, TradeRechargeTypeAndMoneyData.class);

                    bigMoney.clear();
                    if (response == null) {
                        return FAIL;
                    } else {
                        tradeRechargeTypeAndMoneyData = response.getData();
                        if (tradeRechargeTypeAndMoneyData != null) {

                            money = response.getData().getRechargeNumber().get(0);
                            bigMoney = response.getData().getRechargeNumber();
                        }
                    }
                  /*  JSONArray big = object.getJSONArray("data");
                    if (big != null) {
                        for (int i = 0; i < big.length(); i++) {
                            bigMoney.add(big.getString(i));
                            try {
                                if (i == 0) {
                                    money = big.getString(i);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }*/
                    return SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return FAIL;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (!isAdded())
                    return;
                if (isDetached())
                    return;
                if (s != null && s.equals(SUCCESS)) {
                    appprogressbar.setVisibility(View.GONE);
                    contentlayout.setVisibility(View.VISIBLE);
                    myAdapter.clear();
                    if (bigMoney != null) {
                        for (String str : bigMoney)
                            myAdapter.add(str);
                        myAdapter.notifyDataSetChanged();
                    }
                } else {
                    appprogressbar.setVisibility(View.GONE);
                    showCusToast("数据获取失败！");
                }
                tv_totalMoney.setText(money);


            }
        }.execute();
    }

    class MyAdapter extends ArrayAdapter<String> {
        List<String> objects;

        public MyAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            this.objects = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = View.inflate(cashInAct, R.layout.item_cashin, null);
            Button btn_text = MyViewHolder.get(convertView, R.id.btn_text);
            if (btn_text != null) {
                btn_text.setText(objects.get(position) + "元");
            }

            if (position == selectPos) {
                btn_text.setSelected(true);
            } else {
                btn_text.setSelected(false);
            }
            btn_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPos = position;
                    ed_number.setText("请选择");
                    try {
                        money = objects.get(position);
                        tv_totalMoney.setText(money + "");

                        MyAppMobclickAgent.onEvent(cashInAct, "page_cashin", "" + money + "元");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }
}
