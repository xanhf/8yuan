package com.trade.eight.moudle.trade.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AppSetting;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.sharejoin.JoinVouchObj;
import com.trade.eight.entity.unifypwd.AccountCheckExchangePasswordData;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.service.trade.UnifyTradePwdHelp;
import com.trade.eight.tools.AESUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.nav.UNavConfig;
import com.trade.eight.tools.trade.TradeConfig;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 交易所统一交易密码第二步
 */
public class TradeUnifyPwdSecondAct extends BaseActivity implements View.OnClickListener {
    Button btn_submit;
    ImageView img_showpwd, img_showpwd02;
    TradeUnifyPwdSecondAct context = this;
    EditText ed_upwd, ed_upwd02;
    View showPwdView, showPwdView02;
    TextView tv_unifypwd_secondtips;

    boolean isNewAccount = false;
    String exjson = "";

    public static void startTradeUnifyPwdSecondAct(Context context, boolean isNewAccount, String exjson) {
        Intent intent = new Intent();
        intent.putExtra("isNewAccount", isNewAccount);
        intent.putExtra("exjson", exjson);
        intent.setClass(context, TradeUnifyPwdSecondAct.class);
        context.startActivity(intent);
    }

    public static void startTradeUnifyPwdSecondAct(Context context, boolean isNewAccount) {
        Intent intent = new Intent();
        intent.putExtra("isNewAccount", isNewAccount);
        intent.setClass(context, TradeUnifyPwdSecondAct.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_trade_unifypwd_ss);
        initData();
        initView();
        initTextChange();
    }

    private void initData() {
        isNewAccount = getIntent().getBooleanExtra("isNewAccount", false);
        if (!isNewAccount) {// 老用户才需要接收此字段
            exjson = getIntent().getStringExtra("exjson");
        }
    }

    void initView() {
        if (!isNewAccount) {
            // 老用户才需要接收此字段
            setAppCommonTitle(getResources().getString(R.string.unifypwd_melable));
        } else {
            setAppCommonTitle(getResources().getString(R.string.unifypwd_title));
        }
        tv_unifypwd_secondtips = (TextView) findViewById(R.id.tv_unifypwd_secondtips);
        if (isNewAccount) {// 新用户进来不需要顶部条提示
            tv_unifypwd_secondtips.setVisibility(View.GONE);
        } else {// 老用户需要提示
            tv_unifypwd_secondtips.setVisibility(View.VISIBLE);
        }

        ed_upwd = (EditText) findViewById(R.id.ed_upwd);
        showPwdView = findViewById(R.id.showPwdView);
        img_showpwd = (ImageView) findViewById(R.id.img_showpwd);

        ed_upwd02 = (EditText) findViewById(R.id.ed_upwd02);
        showPwdView02 = findViewById(R.id.showPwdView02);
        img_showpwd02 = (ImageView) findViewById(R.id.img_showpwd02);

        btn_submit = (Button) findViewById(R.id.btn_submit);

        showPwdView.setOnClickListener(this);
        showPwdView02.setOnClickListener(this);

        btn_submit.setOnClickListener(this);
        btn_submit.setEnabled(false);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (!StringUtil.isEmpty(ed_upwd.getText().toString())
                    && !StringUtil.isEmpty(ed_upwd02.getText().toString())) {
                btn_submit.setEnabled(true);
            } else {
                btn_submit.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    void initTextChange() {
        ed_upwd.addTextChangedListener(textWatcher);
        ed_upwd02.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_submit) {
            submit();
        } else if (id == R.id.showPwdView) {
            if (ed_upwd.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                //当前是密码模式，设置成可见模式
                ed_upwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ed_upwd.setSelection(ed_upwd.getText().length());
                img_showpwd.setImageResource(R.drawable.icon_show_pwd);
            } else {
                //隐藏密码
                ed_upwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ed_upwd.setSelection(ed_upwd.getText().length());
                img_showpwd.setImageResource(R.drawable.icon_hide_pwd);
            }

        } else if (id == R.id.showPwdView02) {
            if (ed_upwd02.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                //当前是密码模式，设置成可见模式
                ed_upwd02.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ed_upwd02.setSelection(ed_upwd02.getText().length());
                img_showpwd02.setImageResource(R.drawable.icon_show_pwd);
            } else {
                //隐藏密码
                ed_upwd02.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ed_upwd02.setSelection(ed_upwd02.getText().length());
                img_showpwd02.setImageResource(R.drawable.icon_hide_pwd);
            }
        }
    }

    void submit() {
        final String uPwd = ed_upwd.getText().toString().trim();
        final String uPwd02 = ed_upwd02.getText().toString().trim();
        if (uPwd.length() < LoginActivity.MIN_LENGTH_PWD) {
            showCusToast("密码过短！");
            return;
        }
        if (!StringUtil.isPassWord(uPwd)) {
            showCusToast("密码格式不对");
            return;
        }
        if (!uPwd.equals(uPwd02)) {
            showCusToast("两次密码不一致");
            return;
        }
        if (!new UserInfoDao(context).isLogin()) {
            showCusToast("请登录后再操作");
            return;
        }
        new AsyncTask<Void, Void, CommonResponse4List<AccountCheckExchangePasswordData>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showNetLoadingProgressDialog("");
            }

            @Override
            protected CommonResponse4List<AccountCheckExchangePasswordData> doInBackground(Void... voids) {
                try {
                    return UnifyTradePwdHelp.setUnifyPWD(context, exjson, AESUtil.encrypt(uPwd));
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
                        //检测好友邀请
                        EventBus.getDefault().post(new JoinVouchObj());

                        List<AccountCheckExchangePasswordData> accountCheckExchangePasswordDataList = response.getData();
                        boolean loginSuccess = true;// 默认登陆成功
                        // 检测验证结果
                        for (AccountCheckExchangePasswordData accountCheckExchangePasswordData : accountCheckExchangePasswordDataList) {
                            if (accountCheckExchangePasswordData.isSuccess()) {
                                // 更新验证通过交易所的token
                                saveToken(accountCheckExchangePasswordData.getToken(), accountCheckExchangePasswordData.getExcode());
                            } else {
                                loginSuccess = false;//默认登陆失败
                                showCusToast(accountCheckExchangePasswordData.getErrorInfo());
                            }
                        }
                        // 验证通过跳转
                        if (loginSuccess) {
                            showCusToast(ConvertUtil.NVL(response.getErrorInfo(), "设置成功"));
                        }
                         /*红包引导的逻辑＝＝＝＝＝＝start*/
                        UNavConfig.setShowStep01(context, false);
                        UNavConfig.setShowStep02(context, true);
                        //设置完交易密码之后就不显示小红包
                        UNavConfig.setShowSmallDlg(context, false);
                        TradeConfig.setAccountIsUnifyPWD(context,new UserInfoDao(context).queryUserInfo().getUserId());
                        doMyfinish();
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
        //本地记录已经初始化过密码 登录成功表示肯定已经是设置过交易所的密码了
        TradeConfig.setInitPwdLocal(context, tradecode, true);
        //如果传了token
        if (token != null) {
            //本地设置token
            AppSetting.getInstance(context).setWPToken(context, tradecode, token);
            //本地设置token获取时间
            AppSetting.getInstance(context).setRefreshTimeWPToken(context, tradecode, System.currentTimeMillis());
        }
    }
}
