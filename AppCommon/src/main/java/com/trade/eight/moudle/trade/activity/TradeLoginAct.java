package com.trade.eight.moudle.trade.activity;

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

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.me.activity.ResetPwdIndexAct;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.trude.UserService;
import com.trade.eight.tools.AESUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.nav.UNavConfig;
import com.trade.eight.tools.trade.TradeConfig;

import java.util.HashMap;

/**
 * 交易所登录
 */
public class TradeLoginAct extends BaseActivity implements View.OnClickListener {
    Button  btn_submit;
    ImageView img_showpwd;
    TradeLoginAct context = this;
    EditText ed_upwd;
    View showPwdView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!new UserInfoDao(context).isLogin()) {
            LoginActivity.start(context);
            finish();
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_trade_login);
        initView();
        initTextChange();

    }


    void initView() {
        String reg = getResources().getString(R.string.input_trade_pwd);
        setAppCommonTitle(String.format(reg));
        ed_upwd = (EditText) findViewById(R.id.ed_upwd);
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
//            startActivity(new Intent(context, TradeRestPwdAct.class));
            if (!new UserInfoDao(context).isLogin())
                return;
            Intent intent = new Intent(context, ResetPwdIndexAct.class);
            intent.putExtra("phone", new UserInfoDao(context).queryUserInfo().getMobileNum());
            startActivity(intent);

        } else if (id == R.id.showPwdView) {
//            if (StringUtil.isEmpty(ed_upwd.getText().toString()))
//                return;
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
        HashMap<String, String> map = new HashMap<>();
        map.put("account", new UserInfoDao(context).queryUserInfo().getCurrentQHAccount());
        try {
            map.put("password", AESUtil.encrypt(uPwd));
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpClientHelper.doPostOption(TradeLoginAct.this,
                AndroidAPIConfig.getAPI(TradeLoginAct.this, AndroidAPIConfig.KEY_URL_TRADE_USER_LOGIN),
                map,
                null,
                new NetCallback((BaseActivity) TradeLoginAct.this) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        String error = ConvertUtil.NVL(resultMsg, getResources().getString(R.string.network_problem));
                        showCusToast(error);
                    }

                    @Override
                    public void onResponse(String response) {
                        CommonResponse<UserInfo> commonResponse = CommonResponse.fromJson(response, UserInfo.class);
                        UserInfo userInfoRes = commonResponse.getData();

                        if (userInfoRes != null) {
                            //本地设置token
                            AppSetting.getInstance(context).setWPToken(context, userInfoRes.getToken());
                        }

                        //本地设置token获取时间
                        AppSetting.getInstance(context).setRefreshTimeWPToken(context, System.currentTimeMillis());
//                        showCusToast(ConvertUtil.NVL(response.getErrorInfo(), "登录成功"));
                        //本地记录已经初始化过密码 登录成功表示肯定已经是设置过交易所的密码了
                        TradeConfig.setInitPwdLocal(context, TradeConfig.getCurrentTradeCode(context), true);
                        /*红包引导的逻辑＝＝＝＝＝＝start*/
                        UNavConfig.setShowSmallDlg(context, false);
                        /*红包引导的逻辑＝＝＝＝＝＝end*/
                        doMyfinish();

                    }
                },
                true);
    }

}
