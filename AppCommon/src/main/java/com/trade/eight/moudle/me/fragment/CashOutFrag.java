package com.trade.eight.moudle.me.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.Banks;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.moudle.me.TradeLoginSuccessEvent;
import com.trade.eight.moudle.me.activity.CashInAndOutAct;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.push.activity.OrderAndTradeNotifyActivity;
import com.trade.eight.moudle.trade.BankEvent;
import com.trade.eight.moudle.trade.TradeLoginCancleEvent;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.moudle.trade.activity.DialogUtilForTrade;
import com.trade.eight.moudle.trade.cashinout.CashOutCardManageAct;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.AESUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.OpenActivityUtil;
import com.trade.eight.tools.StringUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/14.
 */

public class CashOutFrag extends BaseFragment implements View.OnClickListener {

    private String TAG = CashOutFrag.class.getSimpleName();

    View line_bank;
    ImageView img_bankicon;
    TextView text_bankname;
    TextView text_bankcard_num;
    TextView text_cashin_time;
    TextView text_cashin_amount;
    EditText edit_cashin_amount;
    TextView text_cashin_querymoney;
    EditText edit_cashin_moneypwd;
    View line_cashin_bankpwd;
    EditText edit_cashin_bankpwd;
    Button btn_submit;
    TextView text_qihuo_phone;

    List<Banks> banksList;
    Banks selectBank = null;
    boolean isVisible = false;

    CashInAndOutAct cashInAndOutAct;

    TradeLoginDlg tradeLoginDlg;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_cashin, null);
        initView(rootView);
        EventBus.getDefault().register(this);
        cashInAndOutAct = (CashInAndOutAct) getActivity();
        return rootView;
    }

    private void initView(View view) {
        line_bank = view.findViewById(R.id.line_bank);
        img_bankicon = (ImageView) view.findViewById(R.id.img_bankicon);
        text_bankname = (TextView) view.findViewById(R.id.text_bankname);
        text_bankcard_num = (TextView) view.findViewById(R.id.text_bankcard_num);
        text_cashin_time = (TextView) view.findViewById(R.id.text_cashin_time);
        text_cashin_amount = (TextView) view.findViewById(R.id.text_cashin_amount);
        edit_cashin_amount = (EditText) view.findViewById(R.id.edit_cashin_amount);
        text_cashin_querymoney = (TextView) view.findViewById(R.id.text_cashin_querymoney);
        edit_cashin_moneypwd = (EditText) view.findViewById(R.id.edit_cashin_moneypwd);
        line_cashin_bankpwd = view.findViewById(R.id.line_cashin_bankpwd);
        edit_cashin_bankpwd = (EditText) view.findViewById(R.id.edit_cashin_bankpwd);
        btn_submit = (Button) view.findViewById(R.id.btn_submit);
        text_qihuo_phone = (TextView) view.findViewById(R.id.text_qihuo_phone);

        btn_submit.setText(R.string.lable_cashout_confirm);
        text_cashin_time.setText(R.string.lable_cashout_time);
        text_cashin_amount.setText(R.string.lable_cashout_amount);
        edit_cashin_amount.setHint(R.string.lable_cashout_amount_hint);

        text_qihuo_phone.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        line_bank.setOnClickListener(this);

        if (isVisible) {
            getUserBankList();
            text_cashin_querymoney.setTextColor(getActivity().getResources().getColor(R.color.c_999999));
            TradeInfoData tradeInfoData = TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), CashOutFrag.this).getTradeInfoData();
            if (tradeInfoData != null) {
                text_cashin_querymoney.setText(getActivity().getResources().getString(R.string.lable_cashout_money, StringUtil.forNumber(new BigDecimal(tradeInfoData.getWithdrawQuota()).doubleValue())));
            } else {
                TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), this).loadTradeOrderAndUserInfoData(new TradeUserInfoData4Situation.GetDataCallback() {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        showCusToast(resultMsg);
                    }

                    @Override
                    public void onResponse(TradeInfoData tradeInfoData) {
                        text_cashin_querymoney.setText(getActivity().getResources().getString(R.string.lable_cashout_money, StringUtil.forNumber(new BigDecimal(tradeInfoData.getWithdrawQuota()).doubleValue())));
                    }
                }, false);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.v(TAG, "isVisibleToUser=" + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onFragmentVisible(boolean isVisible) {
        super.onFragmentVisible(isVisible);
        Log.v(TAG, "onFragmentVisible isVisible= " + isVisible);
        this.isVisible = isVisible;
        if (line_bank == null) {
            return;
        }
        if (isVisible) {
            getUserBankList();
            text_cashin_querymoney.setTextColor(getActivity().getResources().getColor(R.color.c_999999));
            TradeInfoData tradeInfoData = TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), CashOutFrag.this).getTradeInfoData();
            if (tradeInfoData != null) {
                text_cashin_querymoney.setText(getActivity().getResources().getString(R.string.lable_cashout_money, StringUtil.forNumber(new BigDecimal(tradeInfoData.getWithdrawQuota()).doubleValue())));
            } else {
                TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), this).loadTradeOrderAndUserInfoData(new TradeUserInfoData4Situation.GetDataCallback() {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        showCusToast(resultMsg);
                    }

                    @Override
                    public void onResponse(TradeInfoData tradeInfoData) {
                        text_cashin_querymoney.setText(getActivity().getResources().getString(R.string.lable_cashout_money, StringUtil.forNumber(new BigDecimal(tradeInfoData.getWithdrawQuota()).doubleValue())));
                    }
                }, false);
            }
        } else {

        }
    }

    private void displayBank(Banks banks) {

        selectBank = banks;
        if (selectBank.getRechargeFlag() == 1) {
            line_cashin_bankpwd.setVisibility(View.VISIBLE);
        } else {
            line_cashin_bankpwd.setVisibility(View.GONE);
        }
        ImageLoader.getInstance().displayImage(selectBank.getBankIcon(), img_bankicon, AppImageLoaderConfig.getDisplayImageOptions(getActivity()));
        text_bankname.setText(selectBank.getBankName());
        text_bankcard_num.setText(StringUtil.getHintCardNo(selectBank.getBankAccount()));

    }

    /**
     * 获取银行卡列表
     */
    private void getUserBankList() {
        HttpClientHelper.doPostOption((BaseActivity) getActivity(),
                AndroidAPIConfig.getAPI(getActivity(), AndroidAPIConfig.KEY_URL_TRADE_GET_USER_BANKS),
                null,
                null,
                new NetCallback((BaseActivity) getActivity()) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {

                        if (resultCode.equals(ApiConfig.ERROR_CODE_NOYINQI)) {
                            showNoYinQi();
                            return;
                        }
                        if (ApiConfig.isNeedLogin(resultCode)) {
                            if (!isVisible)
                                return;
                            if (!isAdded())
                                return;
                            if (isDetached())
                                return;
                            if (tradeLoginDlg == null) {
                                tradeLoginDlg = new TradeLoginDlg((BaseActivity) getActivity());
                            }
                            if (!tradeLoginDlg.isShowingDialog()) {
                                tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                            }
                            return;
                        }
                        showCusToast(resultMsg);

                    }

                    @Override
                    public void onResponse(String response) {
                        CommonResponse4List<Banks> commonResponse4List = CommonResponse4List.fromJson(response, Banks.class);
                        banksList = commonResponse4List.getData();
                        if (banksList != null && banksList.size() > 0) {
                            line_bank.setVisibility(View.VISIBLE);
                            displayBank(banksList.get(0));
                        } else {
                            showNoYinQi();
                        }
                    }
                },
                true);
    }

    /**
     * 展示没有银期对话框
     */
    private void showNoYinQi() {
        line_bank.setVisibility(View.GONE);
        DialogUtil.showTitleAndContentDialog(getActivity(),

                getActivity().getResources().getString(R.string.lable_no_yiqi),
                null,
                getActivity().getResources().getString(R.string.lable_cashin_cancle),
                getActivity().getResources().getString(R.string.lable_no_yiqi_enter),
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        cashInAndOutAct.doMyfinish();
                        return false;
                    }
                },
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        WebActivity.start(getActivity(), "如何开通银期", AndroidAPIConfig.URL_HOWTO_ADDCARD);
                        cashInAndOutAct.doMyfinish();
                        return false;
                    }
                });
    }

    /**
     * 用户提现
     */
    private void submit() {
        String money = edit_cashin_amount.getText().toString();
        if (TextUtils.isEmpty(money)) {
            showCusToast("请输入提现金额");
            return;
        }
        String password = edit_cashin_moneypwd.getText().toString();
        if (TextUtils.isEmpty(password)) {
            showCusToast("请输入资金密码");
            return;
        }
        String bankPwd = "";
        if(selectBank==null){
            showCusToast("请选择银行卡");
            return;
        }
        if (selectBank.getRechargeFlag() == 1) {
            bankPwd = edit_cashin_bankpwd.getText().toString();
            if (TextUtils.isEmpty(bankPwd)) {
                showCusToast("请输入银行卡密码");
                return;
            }
        }
        HashMap<String, String> hashMap = new HashMap<>();

        try {
            hashMap.put("brokerBranchId", selectBank.getBrokerBranchId());
            hashMap.put("password", AESUtil.encrypt(password));//资金密码
            hashMap.put("bankId", selectBank.getBankId());
            hashMap.put("bankBranchId", selectBank.getBankBranchId());
            hashMap.put("bankAccount", selectBank.getBankAccount());
            hashMap.put("bankPassword", AESUtil.encrypt(bankPwd));//银行卡密码
            hashMap.put("amount", money);//银行卡密码
        } catch (Exception e) {
            e.printStackTrace();
        }


        HttpClientHelper.doPostOption((BaseActivity) getActivity(),
                AndroidAPIConfig.URL_TRADE_USER_CASHOUT,
                hashMap,
                null,
                new NetCallback((BaseActivity) getActivity()) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        OrderAndTradeNotifyActivity.starCashOutNotifyAct(getActivity(),CashInAndOutAct.CASHOUT_FAIL, ConvertUtil.NVL(resultMsg,"网络连接失败"));
                    }

                    @Override
                    public void onResponse(String response) {
//                        showCusToast("充值成功");
                        OrderAndTradeNotifyActivity.starCashOutNotifyAct(getActivity(),CashInAndOutAct.CASHOUT_SUCCESS,"");
                        // 数据清0
                        edit_cashin_amount.setText("");
                        edit_cashin_moneypwd.setText("");
                        edit_cashin_bankpwd.setText("");

                        TradeInfoData tradeInfoData = TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), CashOutFrag.this).getTradeInfoData();
                        if (tradeInfoData != null) {
                            text_cashin_querymoney.setText(getActivity().getResources().getString(R.string.lable_cashout_money, StringUtil.forNumber(new BigDecimal(tradeInfoData.getWithdrawQuota()).doubleValue())));
                        } else {
                            TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), CashOutFrag.this).loadTradeOrderAndUserInfoData(new TradeUserInfoData4Situation.GetDataCallback() {
                                @Override
                                public void onFailure(String resultCode, String resultMsg) {
                                    showCusToast(resultMsg);
                                }

                                @Override
                                public void onResponse(TradeInfoData tradeInfoData) {
                                    text_cashin_querymoney.setText(getActivity().getResources().getString(R.string.lable_cashout_money, StringUtil.forNumber(new BigDecimal(tradeInfoData.getWithdrawQuota()).doubleValue())));
                                }
                            }, false);
                        }
                    }
                },
                true);
    }


    @Override
    public void onClick(View view) {
        int viewID = view.getId();
        if (viewID == R.id.btn_submit) {
            submit();
        } else if (viewID == R.id.text_qihuo_phone) {
            OpenActivityUtil.callPhone(getActivity(), getActivity().getResources().getString(R.string.lable_kefu_phonenum));

        } else if (viewID == R.id.line_bank) {
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.CASH_IN_OUT,"cashout_choosebank");
            CashOutCardManageAct.startAct(getActivity());
        }
    }

    public void onEventMainThread(BankEvent bankEvent) {
        selectBank = bankEvent.getBanks();
        displayBank(selectBank);
    }

    /**
     * 登录取消
     *
     * @param tradeLoginCancleEvent
     */
    public void onEventMainThread(TradeLoginCancleEvent tradeLoginCancleEvent) {
        cashInAndOutAct.doMyfinish();
    }

    /**
     * 登陆成功
     *
     * @param tradeLoginSuccessEvent
     */
    public void onEventMainThread(TradeLoginSuccessEvent tradeLoginSuccessEvent) {
        getUserBankList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
