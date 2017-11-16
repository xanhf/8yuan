package com.trade.eight.moudle.trade.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.google.gson.Gson;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.unifypwd.AccountCheckBindAndRegData;
import com.trade.eight.entity.unifypwd.AccountCheckExchangePasswordData;
import com.trade.eight.entity.unifypwd.ExchangeData;
import com.trade.eight.moudle.baksource.BakSourceInterface;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.service.trade.UnifyTradePwdHelp;
import com.trade.eight.tools.AESUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.trade.TradeConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 统一交易密码第一步(老用户才会进来)
 */
public class TradeUnifyPwdFirstStepAct extends BaseActivity implements View.OnClickListener {

    TradeUnifyPwdFirstStepAct context = this;

    LinearLayout line_unifypwd_gg;
    RelativeLayout rel_unifypwd_gg;
    EditText ed_unifypwd_ggpwd;
    ImageView img_showpwd;
    LinearLayout line_unifypwd_hg;
    RelativeLayout rel_unifypwd_hg;
    EditText ed_unifypwd_hgpwd;
    ImageView img_showpwd_hg;
    LinearLayout line_unifypwd_jn;
    RelativeLayout rel_unifypwd_jn;
    EditText ed_unifypwd_jnpwd;
    ImageView img_showpwd_jn;
    CheckBox check_agreeprotocl;
    TextView tv_unifypwd_protocl;
    Button btn_submit;

    TextView tv_verifyerror_gg;
    TextView tv_verifyerror_hg;
    TextView tv_verifyerror_jn;

    TextView tv_unifypwd_ggforgetpwd;
    TextView tv_unifypwd_hgforgetpwd;
    TextView tv_unifypwd_jnforgetpwd;

    AccountCheckBindAndRegData accountCheckBindAndRegData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_trade_unifypwd_fs);
        initData();
        initView();
    }

    private void initView() {
        setAppCommonTitle(getResources().getString(R.string.unifypwd_melable));
        rel_unifypwd_gg = (RelativeLayout) findViewById(R.id.rel_unifypwd_gg);
        line_unifypwd_gg = (LinearLayout) findViewById(R.id.line_unifypwd_gg);
        ed_unifypwd_ggpwd = (EditText) findViewById(R.id.ed_unifypwd_ggpwd);
        img_showpwd = (ImageView) findViewById(R.id.img_showpwd);
        rel_unifypwd_hg = (RelativeLayout) findViewById(R.id.rel_unifypwd_hg);
        line_unifypwd_hg = (LinearLayout) findViewById(R.id.line_unifypwd_hg);
        ed_unifypwd_hgpwd = (EditText) findViewById(R.id.ed_unifypwd_hgpwd);
        img_showpwd_hg = (ImageView) findViewById(R.id.img_showpwd_hg);
        rel_unifypwd_jn = (RelativeLayout) findViewById(R.id.rel_unifypwd_jn);
        line_unifypwd_jn = (LinearLayout) findViewById(R.id.line_unifypwd_jn);
        ed_unifypwd_jnpwd = (EditText) findViewById(R.id.ed_unifypwd_jnpwd);
        img_showpwd_jn = (ImageView) findViewById(R.id.img_showpwd_jn);
        check_agreeprotocl = (CheckBox) findViewById(R.id.check_agreeprotocl);
        tv_unifypwd_protocl = (TextView) findViewById(R.id.tv_unifypwd_protocl);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        img_showpwd.setOnClickListener(this);
        img_showpwd_hg.setOnClickListener(this);
        img_showpwd_jn.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        tv_unifypwd_protocl.setOnClickListener(this);

        tv_verifyerror_gg = (TextView) findViewById(R.id.tv_verifyerror_gg);
        tv_verifyerror_hg = (TextView) findViewById(R.id.tv_verifyerror_hg);
        tv_verifyerror_jn = (TextView) findViewById(R.id.tv_verifyerror_jn);

        tv_unifypwd_ggforgetpwd = (TextView) findViewById(R.id.tv_unifypwd_ggforgetpwd);
        tv_unifypwd_hgforgetpwd = (TextView) findViewById(R.id.tv_unifypwd_hgforgetpwd);
        tv_unifypwd_jnforgetpwd = (TextView) findViewById(R.id.tv_unifypwd_jnforgetpwd);

        tv_unifypwd_ggforgetpwd.setOnClickListener(this);
        tv_unifypwd_hgforgetpwd.setOnClickListener(this);
        tv_unifypwd_jnforgetpwd.setOnClickListener(this);

        for (AccountCheckBindAndRegData.ExchangeRegInfo exchangeRegInfo : accountCheckBindAndRegData.getExchanges()) {
            if (exchangeRegInfo.getExcode().equals(BakSourceInterface.TRUDE_SOURCE_WEIPAN)) {
                if (exchangeRegInfo.isReg()) {
                    line_unifypwd_gg.setVisibility(View.VISIBLE);
                } else {
                    line_unifypwd_gg.setVisibility(View.GONE);
                }
            }
            if (exchangeRegInfo.getExcode().equals(BakSourceInterface.TRUDE_SOURCE_WEIPANP_HG)) {
                if (exchangeRegInfo.isReg()) {
                    line_unifypwd_hg.setVisibility(View.VISIBLE);
                } else {
                    line_unifypwd_hg.setVisibility(View.GONE);
                }
            }
            if (exchangeRegInfo.getExcode().equals(BakSourceInterface.TRUDE_SOURCE_WEIPANP_JN)) {
                if (exchangeRegInfo.isReg()) {
                    line_unifypwd_jn.setVisibility(View.VISIBLE);
                } else {
                    line_unifypwd_jn.setVisibility(View.GONE);
                }
            }
        }
    }

    private void initData() {
        accountCheckBindAndRegData = (AccountCheckBindAndRegData) getIntent().getSerializableExtra("accountCheckBindAndRegData");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_submit) {
            submit();
        } else if (id == R.id.img_showpwd) {
            if (ed_unifypwd_ggpwd.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                //当前是密码模式，设置成可见模式
                ed_unifypwd_ggpwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ed_unifypwd_ggpwd.setSelection(ed_unifypwd_ggpwd.getText().length());
                img_showpwd.setImageResource(R.drawable.icon_show_pwd);
            } else {
                //隐藏密码
                ed_unifypwd_ggpwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ed_unifypwd_ggpwd.setSelection(ed_unifypwd_ggpwd.getText().length());
                img_showpwd.setImageResource(R.drawable.icon_hide_pwd);
            }

        } else if (id == R.id.img_showpwd_hg) {
            if (ed_unifypwd_hgpwd.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                //当前是密码模式，设置成可见模式
                ed_unifypwd_hgpwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ed_unifypwd_hgpwd.setSelection(ed_unifypwd_hgpwd.getText().length());
                img_showpwd_hg.setImageResource(R.drawable.icon_show_pwd);
            } else {
                //隐藏密码
                ed_unifypwd_hgpwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ed_unifypwd_hgpwd.setSelection(ed_unifypwd_hgpwd.getText().length());
                img_showpwd_hg.setImageResource(R.drawable.icon_hide_pwd);
            }

        } else if (id == R.id.img_showpwd_jn) {
            if (ed_unifypwd_jnpwd.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                //当前是密码模式，设置成可见模式
                ed_unifypwd_jnpwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ed_unifypwd_jnpwd.setSelection(ed_unifypwd_jnpwd.getText().length());
                img_showpwd_jn.setImageResource(R.drawable.icon_show_pwd);
            } else {
                //隐藏密码
                ed_unifypwd_jnpwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ed_unifypwd_jnpwd.setSelection(ed_unifypwd_jnpwd.getText().length());
                img_showpwd_jn.setImageResource(R.drawable.icon_hide_pwd);
            }
        } else if (id == R.id.tv_unifypwd_ggforgetpwd) {
            TradeUnifyPwdResetExchangeAct.startTradeUnifyPwdResetExchangeAct(TradeUnifyPwdFirstStepAct.this, TradeConfig.code_gg);
        } else if (id == R.id.tv_unifypwd_hgforgetpwd) {
            TradeUnifyPwdResetExchangeAct.startTradeUnifyPwdResetExchangeAct(TradeUnifyPwdFirstStepAct.this, TradeConfig.code_hg);
        } else if (id == R.id.tv_unifypwd_jnforgetpwd) {
            TradeUnifyPwdResetExchangeAct.startTradeUnifyPwdResetExchangeAct(TradeUnifyPwdFirstStepAct.this, TradeConfig.code_jn);
        } else if (id == R.id.tv_unifypwd_protocl) {
            WebActivity.start(TradeUnifyPwdFirstStepAct.this, "统一密码条款", AndroidAPIConfig.URL_ACCOUNT_UNIFYPWD_PROTOCOL);
        }
    }

    void submit() {

        final String gg_pwd = ed_unifypwd_ggpwd.getText().toString().trim();
        final String hg_pwd = ed_unifypwd_hgpwd.getText().toString().trim();
        final String jn_pwd = ed_unifypwd_jnpwd.getText().toString().trim();
        if (TextUtils.isEmpty(gg_pwd) && TextUtils.isEmpty(hg_pwd) && TextUtils.isEmpty(jn_pwd)) {
            showCusToast("请输入交易所密码");
            return;
        }
        if (!check_agreeprotocl.isChecked()) {
            showCusToast("您还没有同意本功能各项条款");
            return;
        }
        // 检测密码之前 错误提现隐藏
        tv_verifyerror_gg.setVisibility(View.GONE);
        tv_verifyerror_hg.setVisibility(View.GONE);
        tv_verifyerror_jn.setVisibility(View.GONE);
        rel_unifypwd_gg.setBackgroundResource(R.drawable.input_login_stork_round);
        rel_unifypwd_hg.setBackgroundResource(R.drawable.input_login_stork_round);
        rel_unifypwd_jn.setBackgroundResource(R.drawable.input_login_stork_round);


        new AsyncTask<Void, Void, CommonResponse4List<AccountCheckExchangePasswordData>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showNetLoadingProgressDialog("");
            }

            @Override
            protected CommonResponse4List<AccountCheckExchangePasswordData> doInBackground(Void... voids) {
                try {
                    return UnifyTradePwdHelp.checkAccountExchangedPWDBatch(TradeUnifyPwdFirstStepAct.this, getExJson(new String[]{gg_pwd, hg_pwd, jn_pwd}));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse4List<AccountCheckExchangePasswordData> response) {
                super.onPostExecute(response);
                if (isFinishing())
                    return;
                hideNetLoadingProgressDialog();
                if (response != null) {
                    if (response.isSuccess()) {

                        List<AccountCheckExchangePasswordData> accountCheckExchangePasswordDataList = response.getData();
                        boolean verifyPass = true;// 默认验证通过
                        // 检测验证结果
                        for (AccountCheckExchangePasswordData accountCheckExchangePasswordData : accountCheckExchangePasswordDataList) {
                            if (TextUtils.isEmpty(accountCheckExchangePasswordData.getErrorInfo())) {
                                // 更新验证通过交易所的token
                                saveToken(accountCheckExchangePasswordData.getToken(), accountCheckExchangePasswordData.getExcode());
                            } else {
                                verifyPass = false;//验证未通过, 显示错误提示信息
                                if (accountCheckExchangePasswordData.getExcode().equals(TradeConfig.code_gg)) {
                                    rel_unifypwd_gg.setBackgroundResource(R.drawable.input_login_stork_round_error);
                                    tv_verifyerror_gg.setVisibility(View.VISIBLE);
//                                    tv_verifyerror_gg.setText(accountCheckExchangePasswordData.getErrorInfo());
                                } else if (accountCheckExchangePasswordData.getExcode().equals(TradeConfig.code_hg)) {
                                    tv_verifyerror_hg.setVisibility(View.VISIBLE);
                                    rel_unifypwd_hg.setBackgroundResource(R.drawable.input_login_stork_round_error);
//                                    tv_verifyerror_hg.setText(accountCheckExchangePasswordData.getErrorInfo());
                                } else if (accountCheckExchangePasswordData.getExcode().equals(TradeConfig.code_jn)) {
                                    tv_verifyerror_jn.setVisibility(View.VISIBLE);
                                    rel_unifypwd_jn.setBackgroundResource(R.drawable.input_login_stork_round_error);
//                                    tv_verifyerror_jn.setText(accountCheckExchangePasswordData.getErrorInfo());
                                }
                            }
                        }
                        // 验证通过跳转
                        if (verifyPass) {
                            TradeUnifyPwdSecondAct.startTradeUnifyPwdSecondAct(TradeUnifyPwdFirstStepAct.this, false, getExJson(new String[]{gg_pwd, hg_pwd, jn_pwd}));
                            doMyfinish();
                        }
                    } else {
                        String error = ConvertUtil.NVL(response.getErrorInfo(), getResources().getString(R.string.network_problem));
                        showCusToast(error);
                    }
                } else {
                    showCusToast(getResources().getString(R.string.network_problem));
                }
            }
        }.execute();
    }

    /**
     * 验证交易密码之后保存token
     *
     * @param token
     * @param tradecode
     */
    private void saveToken(String token, String tradecode) {
        //本地记录已经初始化过密码
        TradeConfig.setInitPwdLocal(context, tradecode, true);
        //如果传了token
        if (token != null) {
            //本地设置token
            AppSetting.getInstance(context).setWPToken(context, tradecode, token);
            //本地设置token获取时间
            AppSetting.getInstance(context).setRefreshTimeWPToken(context, tradecode, System.currentTimeMillis());
        }
    }

    /**
     * 获取交易所密码json串
     *
     * @param args
     * @return
     */
    private String getExJson(String[] args) {
        List<ExchangeData> exchangeDataList = new ArrayList<ExchangeData>();
        try {

            // 广贵所密码
            if (line_unifypwd_gg.getVisibility() == View.VISIBLE) {
                ExchangeData exchangeData = new ExchangeData();
                exchangeData.setExchangeId(1);
                if (!TextUtils.isEmpty(args[0])) {
                    exchangeData.setPassword(AESUtil.encrypt(args[0]));
                }
                exchangeDataList.add(exchangeData);
            }

            // 哈贵所密码
            if (line_unifypwd_hg.getVisibility() == View.VISIBLE) {
                ExchangeData exchangeData = new ExchangeData();
                exchangeData.setExchangeId(2);
                if (!TextUtils.isEmpty(args[1])) {
                    exchangeData.setPassword(AESUtil.encrypt(args[1]));
                }
                exchangeDataList.add(exchangeData);
            }
            // 农交所密码
            if (line_unifypwd_jn.getVisibility() == View.VISIBLE) {
                ExchangeData exchangeData = new ExchangeData();
                exchangeData.setExchangeId(3);
                if (!TextUtils.isEmpty(args[2])) {
                    exchangeData.setPassword(AESUtil.encrypt(args[2]));
                }
                exchangeDataList.add(exchangeData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Gson().toJson(exchangeDataList);
    }

    /*class ExchangeData {
        private int exchangeId;
        private String password;

        public int getExchangeId() {
            return exchangeId;
        }

        public void setExchangeId(int exchangeId) {
            this.exchangeId = exchangeId;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }*/
}
