package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.tools.AESUtil;
import com.trade.eight.view.AppTitleView;

import java.util.HashMap;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/15.
 * 期货修改密码  包括  资金密码  交易密码
 */

public class QiHuoUpDatePwdAct extends BaseActivity {

    public static final int TRADE_PWD = 1;
    public static final int MONEY_PWD = 2;

    private int pwd_type;

    AppTitleView appTitleView;
    EditText edit_old_pwd;
    EditText edit_new_pwd;
    EditText edit_new_pwd_1;
    Button btn_submit;


    public static void startAct(Context context, int type) {
        Intent intent = new Intent(context, QiHuoUpDatePwdAct.class);
        intent.putExtra("pwd_type", type);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_qihuo_updatepwd);
        initData();
        initView();
    }

    private void initData() {
        pwd_type = getIntent().getIntExtra("pwd_type", TRADE_PWD);
    }

    private void initView() {
        appTitleView = (AppTitleView) findViewById(R.id.title);
        appTitleView.setBaseActivity(QiHuoUpDatePwdAct.this);
        if (pwd_type == TRADE_PWD) {
            appTitleView.setAppCommTitle(R.string.lable_update_tradepwd);
        } else if (pwd_type == MONEY_PWD) {
            appTitleView.setAppCommTitle(R.string.lable_update_moneypwd);
        }
        edit_old_pwd = (EditText) findViewById(R.id.edit_old_pwd);
        edit_new_pwd = (EditText) findViewById(R.id.edit_new_pwd);
        edit_new_pwd_1 = (EditText) findViewById(R.id.edit_new_pwd_1);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pwd_type == TRADE_PWD) {
                    submit();
                } else if (pwd_type == MONEY_PWD) {
                    submitMoneyPWD();
                }
            }
        });
    }

    private boolean judgeInput() {
        String oldPWD = edit_old_pwd.getText().toString();
        if (TextUtils.isEmpty(oldPWD)) {
            showCusToast("请输入原密码");
            return false;
        }
        String password = edit_new_pwd.getText().toString();
        if (TextUtils.isEmpty(password)) {
            showCusToast("请输入新密码");
            return false;
        }
        String password_1 = edit_new_pwd_1.getText().toString();
        if (TextUtils.isEmpty(password_1)) {
            showCusToast("请确认新密码");
            return false;
        }
        if(!password.equals(password_1)){
            showCusToast("两次输入密码不一致");
            return false;
        }
        return true;
    }

    /**
     * 用户修改交易密码
     */
    private void submit() {
        if(!judgeInput()){
            return;
        }
        HashMap<String, String> hashMap = new HashMap<>();
        try {
            hashMap.put("oldPassword", AESUtil.encrypt(edit_old_pwd.getText().toString()));//资金密码
            hashMap.put("newPassword", AESUtil.encrypt(edit_new_pwd.getText().toString()));//资金密码
        } catch (Exception e) {
            e.printStackTrace();
        }


        HttpClientHelper.doPostOption(QiHuoUpDatePwdAct.this,
                AndroidAPIConfig.URL_TRADE_USER_UPDATEPWD,
                hashMap,
                null,
                new NetCallback(QiHuoUpDatePwdAct.this) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        showCusToast(resultMsg);
                    }

                    @Override
                    public void onResponse(String response) {
                        showCusToast("交易密码修改成功");
                    }
                },
                true);
    }

    /**
     * 用户修改交易密码
     */
    private void submitMoneyPWD() {
        if(!judgeInput()){
            return;
        }
        HashMap<String, String> hashMap = new HashMap<>();
        try {
            hashMap.put("oldPassword", AESUtil.encrypt(edit_old_pwd.getText().toString()));//资金密码
            hashMap.put("newPassword", AESUtil.encrypt(edit_new_pwd.getText().toString()));//资金密码
        } catch (Exception e) {
            e.printStackTrace();
        }


        HttpClientHelper.doPostOption(QiHuoUpDatePwdAct.this,
                AndroidAPIConfig.URL_TRADE_USER_UPDATEPWD_MONEY,
                hashMap,
                null,
                new NetCallback(QiHuoUpDatePwdAct.this) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        showCusToast(resultMsg);
                    }

                    @Override
                    public void onResponse(String response) {
                        showCusToast("资金密码修改成功");
                    }
                },
                true);
    }
}
