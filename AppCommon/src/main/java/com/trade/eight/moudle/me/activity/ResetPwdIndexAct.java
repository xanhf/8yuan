package com.trade.eight.moudle.me.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.service.trude.UserService;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.view.AppTitleView;

/**
 * Created by fangzhu on 16/8/1.
 * 3.1之后 重新设置密码
 */
public class ResetPwdIndexAct extends BaseActivity implements View.OnClickListener {

    AppTitleView title_view;

    EditText ed_uname, ed_code;
    Button btnGetCode, btn_submit;
    ImageView img_showpwd;
    ResetPwdIndexAct context = this;
    EditText ed_upwd;
    View showPwdView;
    boolean isForgetPWD = false;//找回密码，还是修改密码，找回密码 回到登录界面需要设置已经输入过的手机号

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_resetpwd_index);
        isForgetPWD = getIntent().getBooleanExtra("isForgetPWD", false);
        initView();
        initTextChange();

    }


    void initView() {
        title_view = (AppTitleView) findViewById(R.id.title_view);
        title_view.setBaseActivity(ResetPwdIndexAct.this);
        title_view.setAppCommTitle("重置登录密码");

        ed_uname = (EditText) findViewById(R.id.ed_uname);
        ed_upwd = (EditText) findViewById(R.id.ed_upwd);
        showPwdView = findViewById(R.id.showPwdView);
        ed_code = (EditText) findViewById(R.id.ed_code);
        btnGetCode = (Button) findViewById(R.id.btnGetCode);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        img_showpwd = (ImageView) findViewById(R.id.img_showpwd);
        showPwdView.setOnClickListener(this);

        btnGetCode.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        showPwdView.setOnClickListener(this);
        btn_submit.setEnabled(false);

        ed_uname.setText(getIntent().getStringExtra("phone"));
        ed_uname.setSelection(ed_uname.getText().length());


    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean isCodeEmpty = false;
//            if (codeView.getVisibility() == View.VISIBLE) {
//                isCodeEmpty = StringUtil.isEmpty(ed_code.getText().toString());
//            } else {
//                isCodeEmpty = false;
//            }
            if (!StringUtil.isEmpty(ed_uname.getText().toString())
                    && !isCodeEmpty
                    && !StringUtil.isEmpty(ed_upwd.getText().toString())) {
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
        ed_uname.addTextChangedListener(textWatcher);
        ed_code.addTextChangedListener(textWatcher);
        ed_upwd.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_submit) {
            submit();

        } else if (id == R.id.btnGetCode) {
            getCode();

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
        final String uName = ed_uname.getText().toString().trim();
        final String uCode = ed_code.getText().toString().trim();
        if (!StringUtil.isMobile(uName)) {
            showCusToast("请输入正确的手机号");
            return;
        }

        if (StringUtil.isEmpty(uCode)) {
            showCusToast("请输入验证码");
            return;
        }
        final String uPwd = ed_upwd.getText().toString().trim();

        if (uPwd.length() < LoginActivity.MIN_LENGTH_PWD) {
            showCusToast("密码过短！");
            return;
        }
        if (!StringUtil.isPassWord(uPwd)) {
            showCusToast("密码格式不对");
            return;
        }
//        finish();
//        Intent intent = new Intent(context, ResetPwdAct.class);
//        intent.putExtra("uName", uName);
//        intent.putExtra("uCode", uCode);
//        startActivity(intent);
        new AsyncTask<Void, Void, CommonResponse<UserInfo>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showNetLoadingProgressDialog("");
            }

            @Override
            protected CommonResponse<UserInfo> doInBackground(Void... voids) {
                try {
                    return new UserService(context).resetPWD(context, uName, uPwd, uCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<UserInfo> response) {
                super.onPostExecute(response);
                if (isFinishing())
                    return;
                hideNetLoadingProgressDialog();
                if (response != null) {
                    if (response.isSuccess()) {
                        showCusToast(ConvertUtil.NVL(response.getErrorInfo(), "设置成功"));
                        if (isForgetPWD) {
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.putExtra("phone", uName);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        }

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


    void getCode() {
        final String uName = ed_uname.getText().toString();
        if (!StringUtil.isMobile(uName)) {
            showCusToast("请输入正确的手机号");
            return;
        }

        new AsyncTask<String, Void, CommonResponse<UserInfo>>() {
            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                count = TIME_DELAY;
                if (handler.hasMessages(MSG_COUNT_TIME))
                    handler.removeMessages(MSG_COUNT_TIME);
                handler.sendEmptyMessage(MSG_COUNT_TIME);
            }

            @Override
            protected CommonResponse<UserInfo> doInBackground(String... params) {
                // TODO Auto-generated method stub
                UserService userService = new UserService(context);
                try {
                    return userService.getResetPWDSMS(context, uName);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<UserInfo> result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                if (result != null) {
                    if (result.isSuccess()) {

                    } else {
                        showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "获取验证码失败"));
                        stopTime();
                    }
                } else {
                    showCusToast("获取验证码失败");
                    stopTime();
                }
            }
        }.execute();
    }


    //倒计时的时间单位秒
    int TIME_DELAY = 60;
    int count = TIME_DELAY;
    public static final int MSG_COUNT_TIME = 10;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (isFinishing())
                return;

            switch (msg.what) {
                case MSG_COUNT_TIME:
                    --count;
                    if (count <= 0) {
                        stopTime();
                    } else {
                        btnGetCode.setEnabled(false);
                        String str = count + "s后重新发送";
                        btnGetCode.setText(str);
                        btnGetCode.setTextColor(getResources().getColor(R.color.grey));
                        handler.sendEmptyMessageDelayed(MSG_COUNT_TIME, 1000);
                    }

                    break;
                default:
                    break;
            }
        }
    };

    void stopTime() {
        btnGetCode.setEnabled(true);
        btnGetCode.setText("重新获取");
        btnGetCode.setTextColor(getResources().getColor(R.color.common_blue));
        count = TIME_DELAY;
        handler.removeMessages(MSG_COUNT_TIME);
    }
}
