package com.trade.eight.moudle.trade.jdpay;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.jdpay.JDPayObj;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.Banks;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.trade.TradeConfig;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by fangzhu on 2017/2/10.
 * 京东快捷支付 第一次绑定银行卡
 */

public class JDPayBindCardAct extends BaseActivity implements View.OnClickListener {
    JDPayBindCardAct context = this;
    public static final int CODE_REQ_CHOOSE_BANK = 10;
    public static final int CODE_RES_GET_BANK = 11;

    //充值的金额
    String money;
    View bankMore;
    TextView ed_bank;
    EditText ed_bankCard, ed_realName, ed_idcard, ed_mobileNum;

    public static void start (Context context, String money) {
        Intent intent = new Intent(context, JDPayBindCardAct.class);
        intent.putExtra("money", money);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        money = getIntent().getStringExtra("money");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_jd_bind_card);
        setAppCommonTitle("添加银行卡");
        ed_bankCard = (EditText) findViewById(R.id.ed_bankCard);
        ed_realName = (EditText) findViewById(R.id.ed_realName);
        bankMore = findViewById(R.id.bankMore);
        ed_bank = (TextView) findViewById(R.id.ed_bank);

        ed_idcard = (EditText) findViewById(R.id.ed_idcard);
        ed_mobileNum = (EditText) findViewById(R.id.ed_mobileNum);
        if (!new UserInfoDao(context).isLogin()) {
            showCusToast("请登陆后重试！");
            finish();
            return;
        }
        /**
         * 获取绑定过的银行卡
         * 哈贵只能是同一张卡充值和绑定
         */
        if (TradeConfig.isCurrentHG(context)) {
            new GetUBanksTask().execute();
        }
        try {
            String mob = new UserInfoDao(context).queryUserInfo().getMobileNum();
            ed_mobileNum.setText(mob);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bankMore.setOnClickListener(this);

        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    void submit() {
        final String bankCard = ed_bankCard.getText().toString();
        final String realName = ed_realName.getText().toString();
        final String bankName = ed_bank.getText().toString();
        final String idCard = ed_idcard.getText().toString();
        final String mobileNum = ed_mobileNum.getText().toString();
        if (StringUtil.isEmpty(ed_bankCard.getText().toString())) {
            showCusToast(getResources().getString(R.string.cash_out_input_card));
            return;
        }
        if (StringUtil.isEmpty(ed_realName.getText().toString())) {
            showCusToast(getResources().getString(R.string.cash_out_input_cardName));
            return;
        }
        if (StringUtil.isEmpty(ed_bank.getText().toString())) {
            showCusToast(getResources().getString(R.string.cash_out_input_bank));
            return;
        }
        if (StringUtil.isEmpty(ed_idcard.getText().toString())) {
            showCusToast(getResources().getString(R.string.cash_jd_idcard));
            return;
        }
        if (StringUtil.isEmpty(mobileNum)) {
            showCusToast(getResources().getString(R.string.cash_jd_mobileNum));
            return;
        }
        new AsyncTask<Void, Void, CommonResponse<JDPayObj>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showNetLoadingProgressDialog(null);
            }

            @Override
            protected CommonResponse<JDPayObj> doInBackground(Void... params) {
                try {
                    if (!new UserInfoDao(context).isLogin())
                        return null;
//                            {"success":false,"errorCode":"30011","errorInfo":"请绑定京东快捷支付！","pagerManager":null,"data":null}
                    //没有绑定过京东快捷支付
                    Map<String, String> map = new LinkedHashMap<String, String>();
                    map.put(UserInfo.UID, new UserInfoDao(context).queryUserInfo().getUserId());
                    map.put("amount", money);

                    map.put("bankCard", bankCard);
                    map.put("bankName", bankName);
                    map.put("realName", realName);
                    map.put("idCard", idCard);
                    map.put("mobileNum", mobileNum);
                    map = ApiConfig.getParamMap(context, map);
                    String api = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_JDPAY_SIGN);
                    String res = TradeHelp.post(context, api, map);
                    CommonResponse<JDPayObj> respon = CommonResponse.fromJson(res, JDPayObj.class);
                    return respon;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<JDPayObj> response) {
                super.onPostExecute(response);
                if (isFinishing())
                    return;
                hideNetLoadingProgressDialog();
                if (response != null) {
                    if (response.isSuccess()) {
                        //下一步 输入验证码
                        if (response.getData() != null
                                && response.getData().getOrderId() != null) {
                            JDPayInputSMSAct.start(context, response.getData(), money);
                            finish();
                        }
                    } else {
                        if (ApiConfig.isNeedLogin(response.getErrorCode())) {
                            //重新登录
                            showTokenDlg();
                            return;
                        }
                        DialogUtil.showMsgDialog(context, ConvertUtil.NVL(response.getErrorInfo(), "提交失败"), "确定");
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
        if (id == R.id.bankMore) {
            Intent intent = JDBanksAct.getIntent(context, ed_bank.getText().toString());
            startActivityForResult(intent, CODE_REQ_CHOOSE_BANK);

        } else if (id == R.id.btn_submit) {
            submit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_REQ_CHOOSE_BANK && resultCode == CODE_RES_GET_BANK) {
            //设置选中的银行卡
            if (data == null)
                return;
            Banks banks = (Banks) data.getSerializableExtra("bank");
            if (banks == null)
                return;
            String bankName = banks.getBankName();
            if (!StringUtil.isEmpty(bankName)) {
                ed_bank.setText(bankName);
            }
        }
    }

    /**
     * 拦截此方法点击空白隐藏键盘
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 用户持有的银行卡
     */
    class GetUBanksTask extends AsyncTask<String, Void, CommonResponse4List<Banks>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showNetLoadingProgressDialog(null);

        }

        @Override
        protected CommonResponse4List<Banks> doInBackground(String... params) {

            return TradeHelp.getBankList(context);
        }

        @Override
        protected void onPostExecute(CommonResponse4List<Banks> result) {
            // TODO Auto-generated method stub
            if (isFinishing())
                return;
            super.onPostExecute(result);
//            mPullRefreshListView.onPullDownRefreshComplete();
            hideNetLoadingProgressDialog();
            if (result != null) {
                //refresh adapter
                if (result.isSuccess()) {
                    if (result.getData() != null) {
                        //just init
                        List<Banks> banksList = result.getData();
                        if (banksList != null && banksList.size() > 0) {
                            Banks defaultBank = banksList.get(0);
                            if (defaultBank == null)
                                return;
                            ed_bankCard.setText(defaultBank.getBankCard());
                            //京东绑定的银行和提现的银行有差异，比如 中国民生银行，民生银行
//                            ed_bank.setText(defaultBank.getBankName());
                            ed_realName.setText(defaultBank.getRealName());
                            //输入发的焦点移动到身份证上
                            ed_bank.requestFocus();
                        }
                    }
                } else {
                    showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "网络连接失败！"));
                }

            } else {
                showCusToast("网络连接失败！");
            }

        }
    }

}
