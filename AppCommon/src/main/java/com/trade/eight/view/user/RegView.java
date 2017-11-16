package com.trade.eight.view.user;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.auth.AuthUploadIdCardAct;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.me.activity.RegSuccessActivity;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.trade.ShareOrderEvent;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trude.UserService;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.nav.UNavConfig;

import de.greenrobot.event.EventBus;

/**
 * Created by fangzhu on 16/8/1.
 * 3.1之后 登录之后如果返回用户需要初始化登录密码就需要重新设置密码
 * 新注册用户不需要
 */
public class RegView extends RelativeLayout implements View.OnClickListener {
    Context context;
    View codeView, showPwdView;
    EditText ed_uname, ed_code, ed_upwd;
    Button btnGetCode, btn_submit;
    ImageView img_showpwd;

    public RegView(Context context) {
        super(context);
        init(context);
    }

    public RegView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RegView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    void init(Context context) {
        this.context = context;
        View view = View.inflate(context, R.layout.frag_reg, null);
        addView(view);
        initView(view);
        initTextChange();
        setEdAction();
    }


    void initView(View view) {
        codeView = view.findViewById(R.id.codeView);
        showPwdView = view.findViewById(R.id.showPwdView);
        ed_uname = (EditText) view.findViewById(R.id.ed_uname);
        ed_code = (EditText) view.findViewById(R.id.ed_code);
        ed_upwd = (EditText) view.findViewById(R.id.ed_upwd);
        btnGetCode = (Button) view.findViewById(R.id.btnGetCode);
        btn_submit = (Button) view.findViewById(R.id.btn_submit_reg);
        img_showpwd = (ImageView) view.findViewById(R.id.img_showpwd);

        btnGetCode.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        showPwdView.setOnClickListener(this);

        btn_submit.setEnabled(false);

        ed_upwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        View tv_rule = view.findViewById(R.id.tv_rule);
        if (tv_rule != null) {
            tv_rule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WebActivity.start(context, getResources().getString(R.string.str_fengxian_title), AndroidAPIConfig.URL_FENGXIANTISHI);
                }
            });
        }

    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean isCodeEmpty = false;
            if (codeView.getVisibility() == View.VISIBLE) {
                isCodeEmpty = StringUtil.isEmpty(ed_code.getText().toString());
            } else {
                isCodeEmpty = false;
            }
            if (!StringUtil.isEmpty(ed_uname.getText().toString())
                    && !StringUtil.isEmpty(ed_upwd.getText().toString())
                    && !isCodeEmpty) {
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
        ed_upwd.addTextChangedListener(textWatcher);
        ed_code.addTextChangedListener(textWatcher);
    }

    /**
     * 设置键盘回车键的点击事件
     */
    void setEdAction() {
        ed_upwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)//event is null,
                //  android:imeOptions="actionDone"
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (context == null)
                        return false;
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(ed_upwd.getWindowToken(), 0); //强制隐藏键盘

                    if (btn_submit.isEnabled())
                        btn_submit.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_submit_reg) {

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
        final BaseActivity baseActivity = ((BaseActivity) context);
        final String uName = ed_uname.getText().toString().trim();
        final String uPwd = ed_upwd.getText().toString().trim();
        final String uCode = ed_code.getText().toString().trim();
        if (!StringUtil.isMobile(uName)) {
            baseActivity.showCusToast("请输入正确的手机号");
            return;
        }
        if (StringUtil.isEmpty(uPwd)) {
            baseActivity.showCusToast("请输入密码");
            return;
        }
        if (uPwd.length() < LoginActivity.MIN_LENGTH_PWD) {
            baseActivity.showCusToast("密码过短！");
            return;
        }
        if (!StringUtil.isPassWord(uPwd)) {
            baseActivity.showCusToast("密码格式不对");
            return;
        }
        if (codeView != null && codeView.getVisibility() == View.VISIBLE) {
            if (StringUtil.isEmpty(uCode)) {
                baseActivity.showCusToast("请输入验证码");
                return;
            }
        }
        new AsyncTask<Void, Void, CommonResponse<UserInfo>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((BaseActivity) context).showNetLoadingProgressDialog("");
            }

            @Override
            protected CommonResponse<UserInfo> doInBackground(Void... voids) {
                try {
                    return new UserService(context).reg(context, uName, uPwd, uCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<UserInfo> response) {
                super.onPostExecute(response);
                if (baseActivity.isFinishing())
                    return;

                baseActivity.hideNetLoadingProgressDialog();
                if (response != null) {
                    if (response.isSuccess()) {
//                        ((BaseActivity) context).showCusToast(ConvertUtil.NVL(response.getErrorInfo(), "注册成功"));
//                        context.findViewById(R.id.tab1tv).performClick();
                        if (new UserInfoDao(context).isLogin()) {
                            baseActivity.doMyfinish();
                            AppSetting.getInstance(context).setUserName(uName);
                            //标记本设备已经有用户登录过
                            //这里是注册马上登录的
//                            UNavConfig.setLogined(context, true);
                            EventBus.getDefault().post(new ShareOrderEvent());// 发送刷新数据event(盈利榜)
                            baseActivity.showCusToast("注册成功");//隐藏新手引导  加弱弹窗
                            //注册成功后直接是实名认证
//                            AuthUploadIdCardAct.start(context);
                            RegSuccessActivity.startAct(context);
                        } else {
                            baseActivity.doMyfinish();
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.putExtra("phone", uName);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        }
                        //设置本设备已经注册过，首页启动的时候就不用检测 是否是老用户登录过的
                        UNavConfig.setReged(context, true);
                        UNavConfig.setShowSmallDlg(context, false);
                        //新手引导注册需要显示下单弹窗
                        UNavConfig.setShowHBRegOkDlg(context, true);

                    } else {
                        try {
                            if (ApiConfig.ERROR_CODE_NOT_INIT_PWD.equals(response.getErrorCode())) {
                                //跳转到登录页
                                LoginView loginView = (LoginView) baseActivity.findViewById(R.id.loginView);
                                if (loginView != null)
                                    loginView.hideInitView();
                                baseActivity.findViewById(R.id.tab1tv).performClick();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String error = ConvertUtil.NVL(response.getErrorInfo(), getResources().getString(R.string.network_problem));
                        ((BaseActivity) context).showCusToast(error);

                    }
                } else {
                    ((BaseActivity) context).showCusToast(getResources().getString(R.string.network_problem));
                }
            }
        }.execute();
    }


    void getCode() {
        final String uName = ed_uname.getText().toString();
        if (!StringUtil.isMobile(uName)) {
            ((BaseActivity) context).showCusToast("请输入正确的手机号");
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
                    return userService.getRegSMS(context, uName);
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
                        ((BaseActivity) context).showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "获取验证码失败"));
                        stopTime();

                        try {
                            if (ApiConfig.ERROR_CODE_NOT_INIT_PWD.equals(result.getErrorCode())) {
                                //跳转到登录页
                                LoginView loginView = (LoginView) ((BaseActivity) context).findViewById(R.id.loginView);
                                if (loginView != null)
                                    loginView.hideInitView();
                                ((BaseActivity) context).findViewById(R.id.tab1tv).performClick();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    ((BaseActivity) context).showCusToast("获取验证码失败");
                    stopTime();
                }
            }
        }.execute();
    }


    void showInitView() {
        //需要重新初始化密码
        codeView.setVisibility(View.VISIBLE);
        ed_upwd.setText("");

    }

    //倒计时的时间单位秒
    int TIME_DELAY = 60;
    int count = TIME_DELAY;
    public static final int MSG_COUNT_TIME = 10;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (((BaseActivity) context).isFinishing())
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
