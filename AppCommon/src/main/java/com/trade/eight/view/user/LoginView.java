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
import com.trade.eight.config.AppCollectUtil;
import com.trade.eight.config.AppSetting;
import com.trade.eight.config.AppStartUpConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.me.activity.ResetPwdIndexAct;
import com.trade.eight.moudle.push.activity.OrderAndTradeNotifyActivity;
import com.trade.eight.moudle.trade.ShareOrderEvent;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trude.UserService;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.nav.UNavConfig;

import de.greenrobot.event.EventBus;

/**
 * Created by fangzhu on 16/8/14.
 */
public class LoginView extends RelativeLayout implements View.OnClickListener {
    Context context;
    View codeView, hintView, showPwdView, findPwdView;
    EditText ed_uname, ed_code, ed_upwd;
    Button btnGetCode, btn_submit, btn_findPwd;
    ImageView img_showpwd;

    public LoginView(Context context) {
        super(context);
        init(context);
    }

    public LoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    void init(Context context) {
        this.context = context;
        View v = View.inflate(context, R.layout.frag_login, null);
        addView(v);
        initView(v);
        initTextChange ();
        setEdAction();
    }

    public void setPhone (String phone) {
        if (ed_uname != null && !StringUtil.isEmpty(phone)) {
            ed_uname.setText(phone);
        }
    }

    void initView(View view) {
        codeView = view.findViewById(R.id.codeView);
        hintView = view.findViewById(R.id.hintView);
        findPwdView = view.findViewById(R.id.findPwdView);
        showPwdView = view.findViewById(R.id.showPwdView);
        ed_uname = (EditText) view.findViewById(R.id.ed_uname);
        ed_code = (EditText) view.findViewById(R.id.ed_code);
        ed_upwd = (EditText) view.findViewById(R.id.ed_upwd);
        btnGetCode = (Button) view.findViewById(R.id.btnGetCode);
        btn_submit = (Button) view.findViewById(R.id.btn_submit);
        btn_findPwd = (Button) view.findViewById(R.id.btn_findPwd);
        img_showpwd = (ImageView) view.findViewById(R.id.img_showpwd);

        btnGetCode.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_findPwd.setOnClickListener(this);
        showPwdView.setOnClickListener(this);

        hintView.setVisibility(View.GONE);
        codeView.setVisibility(View.GONE);
        btn_submit.setEnabled(false);

        ed_upwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        String uMobile = AppSetting.getInstance(context).getUserName();
        if (!StringUtil.isEmpty(uMobile)) {
            ed_uname.setText(uMobile);
            ed_uname.setSelection(ed_uname.getText().length());
//            ed_uname.selectAll();
            //不能直接显示，有可能是换了手机操作的，所以网络请求一次
//            showInitView();
            checkMobile(uMobile);
        }
    }

    /**
     * 检测手机号是否已经注册
     * 用来显示初始化密码的view
     */
    void checkMobile (final String mobile) {
        //本地记录的已经初始化过密码的手机号和当前需要检测的手机号是同一个 就不需要再检测
        String localTag = PreferenceSetting.getSharedPreferences(context, "initNumPre", "initNum");
        if (!StringUtil.isEmpty(localTag) && localTag.equals(mobile))
            return;

        new AsyncTask<Void, Void, CommonResponse<TempObject>>() {
            @Override
            protected CommonResponse<TempObject> doInBackground(Void... voids) {
                try {
                    return new UserService(context).checkInitPWD(context, mobile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<TempObject> response) {
                super.onPostExecute(response);
                if (response != null) {
                    if (response.isSuccess()) {
                        //已经初始化过密码
                        //清除本地手机号记录
//                        AppSetting.getInstance(context).setUserName("");

                        hideInitView();
                        PreferenceSetting.setSharedPreferences(context, "initNumPre", "initNum", mobile);
                    }
                    if (!response.isSuccess()){
                        if (ApiConfig.ERROR_CODE_NOT_INIT_PWD.equals(response.getErrorCode())) {
                            //没有初始化过密码
                            showInitView();
                        }
                        if (!StringUtil.isEmpty(response.getErrorInfo()))
                            ((BaseActivity)context).showCusToast(response.getErrorInfo());
                    }

                }
            }
        }.execute();
    }
    

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            checkBtnEnable ();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    void initTextChange () {
        ed_uname.addTextChangedListener(textWatcher);
        ed_upwd.addTextChangedListener(textWatcher);
        ed_code.addTextChangedListener(textWatcher);
        //手机号框 失去焦点就检查是否已经初始化密码
        ed_uname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    return;
                String text = ed_uname.getText().toString().trim();
                if (StringUtil.isMobile(text)) {
                    checkMobile(text);
                }
            }
        });
    }

    /**
     * 设置键盘回车键的点击事件
     */
    void setEdAction() {
        ed_upwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)//event is null,
                //  android:imeOptions="actionDone"
                if (actionId == EditorInfo.IME_ACTION_DONE){
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


    void checkBtnEnable () {
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
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_submit) {
            if (codeView.getVisibility() == View.VISIBLE) {
                submit4Init();
            } else {
                submit();
            }

        }
        else if (id == R.id.btn_findPwd) {
            //找回密码
            context.startActivity(new Intent(context, ResetPwdIndexAct.class).putExtra("isForgetPWD", true));

        }
        else if (id == R.id.btnGetCode) {
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

    void submit () {
        final BaseActivity baseActivity = ((BaseActivity)context);
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
        //登录的时候  密码格式不能判断，有可能是网页注册过来的
//        if (!StringUtil.isPassWord(uPwd)) {
//            baseActivity.showCusToast("密码格式不对");
//            return;
//        }
//        if (codeView != null && codeView.getVisibility() == View.VISIBLE) {
//            if (StringUtil.isEmpty(uCode)) {
//                baseActivity.showCusToast("请输入验证码");
//                return;
//            }
//        }
        new AsyncTask<Void, Void, CommonResponse<UserInfo>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((BaseActivity)context).showNetLoadingProgressDialog("");
            }

            @Override
            protected CommonResponse<UserInfo> doInBackground(Void... voids) {
                try {
                    return new UserService(context).login(context, uName, uPwd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<UserInfo> response) {
                super.onPostExecute(response);
                if (((BaseActivity) context).isFinishing())
                    return;
                baseActivity.hideNetLoadingProgressDialog();
                if (response != null) {
                    if (response.isSuccess()) {
                        AppSetting.getInstance(context).setUserName(uName);
                        ((BaseActivity)context).showCusToast(ConvertUtil.NVL(response.getErrorInfo(), "登录成功"));
                        ((BaseActivity)context).doMyfinish();

                        //标记本设备已经有用户登录过
                        UNavConfig.setLogined(context, true);

                        EventBus.getDefault().post(new ShareOrderEvent());// 发送刷新数据event(盈利榜)

                        //收集个推id
                        AppCollectUtil.collectPushInfo(context);
                    } else {
                        //密码错误之后 清空密码
                        ed_upwd.setText("");
                        String error = ConvertUtil.NVL(response.getErrorInfo(), getResources().getString(R.string.network_problem));
                        ((BaseActivity)context).showCusToast(error);
                        if (ApiConfig.ERROR_CODE_NOT_INIT_PWD.equals(response.getErrorCode())) {
                            showInitView ();
                        }

                    }
                } else {
                    ((BaseActivity)context).showCusToast(getResources().getString(R.string.network_problem));
                }
            }
        }.execute();
    }

    void submit4Init () {
        final BaseActivity baseActivity = ((BaseActivity)context);
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
            ((BaseActivity)context).showCusToast("密码过短！");
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
                ((BaseActivity)context).showNetLoadingProgressDialog("");
            }

            @Override
            protected CommonResponse<UserInfo> doInBackground(Void... voids) {
                try {
                    return new UserService(context).initPWD(context, uName, uPwd, uCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<UserInfo> response) {
                super.onPostExecute(response);
                if (((BaseActivity) context).isFinishing())
                    return;

                baseActivity.hideNetLoadingProgressDialog();
                if (response != null) {
                    if (response.isSuccess()) {
                        ((BaseActivity)context).showCusToast(ConvertUtil.NVL(response.getErrorInfo(), "初始化成功"));
                        //本地手机号记录
                        AppSetting.getInstance(context).setUserName(uName);
                        PreferenceSetting.setSharedPreferences(context, "initNumPre", "initNum", uName);
                        //返回了登录信息
                        if (new UserInfoDao(context).isLogin()) {
                            ((BaseActivity) context).doMyfinish();
                            //标记本设备已经登录过
                            UNavConfig.setLogined(context, true);

                        } else {
                            //没有返回登录信息，需要重新登录
                            hideInitView();
                        }

                        //开启配置信息
                        AppStartUpConfig.getInstance(context).init();
                    } else {
                        String error = ConvertUtil.NVL(response.getErrorInfo(), getResources().getString(R.string.network_problem));
                        ((BaseActivity)context).showCusToast(error);

                    }
                } else {
                    ((BaseActivity)context).showCusToast(getResources().getString(R.string.network_problem));
                }
            }
        }.execute();
    }
    //是否获取过验证码
    boolean isinitedCode = false;
    void getCode () {
        final String uName = ed_uname.getText().toString().trim();
        if (!StringUtil.isMobile(uName)) {
            ((BaseActivity)context).showCusToast("请输入正确的手机号");
            return;
        }

        new AsyncTask<String, Void, CommonResponse<UserInfo>> () {
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
                    return userService.initPWDSMS(context, uName);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<UserInfo> result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                if(result != null) {
                    if(result.isSuccess()) {
                        isinitedCode = true;
                    }else{
                        ((BaseActivity)context).showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "获取验证码失败"));
                        stopTime ();
                    }
                } else {
                    ((BaseActivity)context).showCusToast("获取验证码失败");
                    stopTime ();
                }
            }
        }.execute();
    }


    /**
     * 显示初始化密码的相关view
     */
    public void showInitView () {
        //需要重新初始化密码
        codeView.setVisibility(View.VISIBLE);
        hintView.setVisibility(View.VISIBLE);
        findPwdView.setVisibility(View.GONE);
        stopTime();
        ed_upwd.setText("");

    }
    /**
     * 隐藏初始化密码的相关view
     */
    public void hideInitView () {
        //需要重新初始化密码
        codeView.setVisibility(View.GONE);
        hintView.setVisibility(View.GONE);
        findPwdView.setVisibility(View.VISIBLE);
        ed_upwd.setText("");
        stopTime();

    }

    //倒计时的时间单位秒
    int TIME_DELAY = 60;
    int count = TIME_DELAY;
    public static final int MSG_COUNT_TIME = 10;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (context == null)
                return;
            if (((BaseActivity)context).isFinishing())
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
        if (isinitedCode)
            btnGetCode.setText("重新获取");
        else
            btnGetCode.setText("点此获取");
        btnGetCode.setTextColor(getResources().getColor(R.color.common_blue));
        count = TIME_DELAY;
        handler.removeMessages(MSG_COUNT_TIME);
    }

}
