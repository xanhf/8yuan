package com.trade.eight.moudle.trade.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AppSetting;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.unifypwd.AccountCheckExchangePasswordData;
import com.trade.eight.service.trade.UnifyTradePwdHelp;
import com.trade.eight.tools.AESUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.Utils;
import com.trade.eight.tools.nav.UNavConfig;
import com.trade.eight.tools.trade.TradeConfig;

import java.util.List;

/**
 * 统一密码交易所登录
 */
public class TradeUnifyPWDLoginAct extends BaseActivity implements View.OnClickListener {
    Button btn_submit;
    ImageView img_showpwd;
    TradeUnifyPWDLoginAct context = this;
    EditText ed_upwd;
    View showPwdView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_trade_login);
        initView();
        initTextChange();
    }

    void initView() {
        setAppCommonTitle(getResources().getString(R.string.unifypwd_logintitle));
        ed_upwd = (EditText) findViewById(R.id.ed_upwd);
        // 聚焦并弹出软键盘
        ed_upwd.setFocusable(true);
        ed_upwd.setFocusableInTouchMode(true);
        ed_upwd.requestFocus();
        Utils.showSoftInput(TradeUnifyPWDLoginAct.this);
        showPwdView = findViewById(R.id.showPwdView);
        img_showpwd = (ImageView) findViewById(R.id.img_showpwd);
        View btn_findPwd = findViewById(R.id.btn_findPwd);

        btn_submit = (Button) findViewById(R.id.btn_submit);

        showPwdView.setOnClickListener(this);

        btn_submit.setOnClickListener(this);
        btn_submit.setEnabled(false);

        btn_findPwd.setOnClickListener(this);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (!StringUtil.isEmpty(ed_upwd.getText().toString())) {
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
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_submit) {
            submit();
        } else if (id == R.id.btn_findPwd) {
            startActivity(new Intent(context, TradeUnifyPwdResetAct.class));
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
        }
    }

    void submit() {
        final String uPwd = ed_upwd.getText().toString().trim();

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
                    return UnifyTradePwdHelp.loginByUnifyPWD(context, AESUtil.encrypt(uPwd));
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
                    if (response.isSuccess()) {// 有一个登陆成功  就算成功(success=true)

                        List<AccountCheckExchangePasswordData> accountCheckExchangePasswordDataList = response.getData();
                        boolean loginSuccess = true;// 默认登陆成功
                        // 检测验证结果
                        for (AccountCheckExchangePasswordData accountCheckExchangePasswordData : accountCheckExchangePasswordDataList) {
                            if (TextUtils.isEmpty(accountCheckExchangePasswordData.getErrorInfo())) {
                                // 更新验证通过交易所的token
                                saveToken(accountCheckExchangePasswordData.getToken(),accountCheckExchangePasswordData.getExcode());
                            } else {
                                // 密码错误,提示充值密码
                                if("20002".equals(accountCheckExchangePasswordData.getErrorCode())){
                                    loginSuccess = false;
                                    Dialog dlg = DialogUtil.getResetExchangePassword(TradeUnifyPWDLoginAct.this, accountCheckExchangePasswordData.getExcode(), new DialogUtil.AuthTokenCallBack() {
                                        @Override
                                        public void onPostClick(Object obj) {
                                        }

                                        @Override
                                        public void onNegClick() {
                                            doMyfinish();
                                        }
                                    });
                                    dlg.show();
                                }else{
                                    showCusToast(accountCheckExchangePasswordData.getErrorInfo());
                                }
                            }
                        }
                        // 验证通过跳转
//                        if (loginSuccess) {
                            UNavConfig.setShowSmallDlg(context, false);
//                        }
                        if(loginSuccess){
                            doMyfinish();
                            TradeConfig.setAccountIsUnifyPWD(context,new UserInfoDao(context).queryUserInfo().getUserId());
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
        //如果传了token
        if (token != null) {
            //本地设置token
            AppSetting.getInstance(context).setWPToken(context, tradecode, token);
            //本地设置token获取时间
            AppSetting.getInstance(context).setRefreshTimeWPToken(context, tradecode, System.currentTimeMillis());
        }
        //本地记录已经初始化过密码 登录成功表示肯定已经是设置过交易所的密码了
        TradeConfig.setInitPwdLocal(context, tradecode, true);
    }
}
