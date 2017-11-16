package com.trade.eight.moudle.trade.jdpay;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.jdpay.JDPayObj;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.trade.CashInEvent;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.trade.TradeConfig;

import java.util.LinkedHashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;


/**
 * Created by fangzhu on 2017/2/10.
 * 京东快捷支付 输入短信验证码，提交充值
 */

public class JDPayInputSMSAct extends BaseActivity implements View.OnClickListener {
    private int TIME_MAX_SECOND = 60;
    private int count = TIME_MAX_SECOND;

    JDPayInputSMSAct context = this;
    //充值的金额
    JDPayObj jdPayObj;
    //充值的金额
    String money;
    TextView tv_cardinfo, tv_money, tv_phone, tv_codetime;
    EditText ed_sms;

    public static void start (Context context, JDPayObj obj, String money) {
        Intent intent = new Intent(context, JDPayInputSMSAct.class);
        intent.putExtra("obj", obj);
        intent.putExtra("money", money);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        money = getIntent().getStringExtra("money");
        jdPayObj = (JDPayObj)getIntent().getSerializableExtra("obj");
        if (jdPayObj == null) {
            finish();
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_jd_input_sms);
        setAppCommonTitle("输入短信验证码");
        tv_cardinfo = (TextView) findViewById(R.id.tv_cardinfo);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_codetime = (TextView) findViewById(R.id.tv_codetime);

        ed_sms = (EditText) findViewById(R.id.ed_sms);

        UserInfoDao dao = new UserInfoDao(context);
        if (dao.isLogin()) {
            String phone = dao.queryUserInfo().getMobileNum();
            tv_phone.setText(StringUtil.getHintPhone(phone));
        }
        tv_money.setText(money);
        try {
            tv_cardinfo.setText(jdPayObj.getBankName() +
                    "(" + jdPayObj.getBankCard().substring(jdPayObj.getBankCard().length() - 4,
                    jdPayObj.getBankCard().length()) + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }

        time();

        findViewById(R.id.commitView).setOnClickListener(this);
    }

    private boolean isRun = true;
    public void time() {
        count = TIME_MAX_SECOND;
        isRun = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRun) {
                    count--;
                    handler.sendEmptyMessage(10);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
    }
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10:
                    if (count <= 0) {
                        stopTimer();
                    } else {
                        tv_codetime.setEnabled(false);
                        tv_codetime.setText("" + count + "s后重新发送");
                    }

                    break;
                default:
                    break;
            }
        }
    };
    public void stopTimer() {
        isRun = false;
        tv_codetime.setEnabled(true);
        tv_codetime.setText("短信已发送");
    }

    void submit() {
        final String smsString = ed_sms.getText().toString();

        if (StringUtil.isEmpty(smsString)) {
            showCusToast(getResources().getString(R.string.cash_out_input_checkCode));
            return;
        }
        new AsyncTask<Void, Void, CommonResponse<Object>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showNetLoadingProgressDialog(null);
            }

            @Override
            protected CommonResponse<Object> doInBackground(Void... params) {
                if (!new UserInfoDao(context).isLogin())
                    return null;
                //{"success":false,"errorCode":"30011","errorInfo":"请绑定京东快捷支付！","pagerManager":null,"data":null}
                try {
                    Map<String, String> map = new LinkedHashMap<String, String>();
                    map.put(UserInfo.UID, new UserInfoDao(context).queryUserInfo().getUserId());
                    map.put("smsCode", smsString);
                    map.put("orderId", jdPayObj.getOrderId());
                    map = ApiConfig.getParamMap(context, map);
                    String api = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_JDPAY_PAY);
                    String res = TradeHelp.post(context, api, map);
                    CommonResponse<Object> respon = CommonResponse.fromJson(res, Object.class);
                    return respon;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<Object> response) {
                super.onPostExecute(response);
                if (isFinishing())
                    return;
                hideNetLoadingProgressDialog();
                if (response != null) {
                    if (response.isSuccess()) {
                        showCusToast("支付成功");
                        //关闭act
                        finish();
                        CashInEvent event = new CashInEvent(true);
                        EventBus.getDefault().post(event);

                    } else {
                        if (ApiConfig.isNeedLogin(response.getErrorCode())) {
                            //重新登录
                            showTokenDlg();
                            return;
                        }
                        DialogUtil.showMsgDialog(context, ConvertUtil.NVL(response.getErrorInfo(), "支付失败"), "确定");
                    }
                } else {
                    showCusToast("网络异常");
                }
            }
        }.execute();
    }

    Dialog tokenDlg = null;

    void showTokenDlg() {
        //网页充值的时候判断是否token可用
        if (tokenDlg != null && tokenDlg.isShowing())
            return;
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.commitView) {
            submit();
        }
    }
}
