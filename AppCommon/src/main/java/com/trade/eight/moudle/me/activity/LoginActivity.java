package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.view.UnderLineTextView;
import com.trade.eight.view.user.LoginView;
import com.trade.eight.view.user.RegView;

/**
 * 可选参数
 * tab 登录还是注册
 * phone 默认填入的手机号
 *
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
    public static final String TAG = "LoginActivity";
    LoginActivity context = this;
    //只管登录注册 的逻辑 是否关闭 上一个页面，这里设置成true
    public static final boolean DEFAUL_ISFinish = true;
    public static final String TAG_IS_FINISH = "isFinish";
    public static final String TAB_LOGIN = "login";
    public static final String TAB_REG = "reg";
    //密码最小长度
    public static final int MIN_LENGTH_PWD = 6;
    RegView regView;
    LoginView loginView;
    TextView text_chang_login_reg;
    private boolean isReg = false;


    public static void start (Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //防止是OpenActivityUtil 这些外部打开的 登录注册
        if (new UserInfoDao(context).isLogin()) {
            //如果已经登录了 直接跳转到首页
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            doMyfinish();
            return;
        }

        setContentView(R.layout.activity_login);
        initViews();
        controlKeyboardLayout(findViewById(R.id.rootView));
    }

    @Override
    public boolean isActivityFitsSystemWindows() {
        return false;
    }

//    @Override
//    public int getStatusBarTintResource() {
//        return R.drawable.img_login_status_bar;
//    }


    @Override
    protected void appCommonGoBack() {
        if (exitRegConfig()) {
            return;
        } else {
            doMyfinish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exitRegConfig()) {
                return true;
            } else {
                doMyfinish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_bottom);
//        boolean outAnim = getIntent().getBooleanExtra("outAnim", false);
//        if (outAnim)
//            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_bottom);
    }


    /**
     * @param root 最外层布局，需要调整的布局
     * 输入框，滚动root,使输入框在root可视区域的底部
     */
    private void controlKeyboardLayout(final View root) {
        if (root == null)
            return;
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (loginView == null)
                    return;
                if (regView == null)
                    return;
//
//                View focuseInputView = getWindow().getCurrentFocus();
//                if (focuseInputView == null)
//                    return;
                Log.v(TAG, "onGlobalLayout===");
                Rect rect = new Rect();
                //获取root在窗体的可视区域
                root.getWindowVisibleDisplayFrame(rect);
                //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;
                //若不可视区域高度大于100，则键盘显示
                if (rootInvisibleHeight > 100) {
                    int[] location = new int[2];
//                    int[] focuseLocation = new int[2];
//                    获取scrollToView在窗体的坐标
//                    focuseInputView.getLocationInWindow(focuseLocation);

                    View view = loginView.findViewById(R.id.findPwdView);
                    if (loginView.getVisibility() != View.VISIBLE)
                        view = regView.findViewById(R.id.ruleView);
                    if (view == null)
                        return;
                    view.getLocationInWindow(location);

//                    int minH = (int)((double)getWindowManager().getDefaultDisplay().getHeight() * (1D/2D));
//                    if (focuseLocation[1] < minH)
//                        return;
                    //计算root滚动高度，使scrollToView在可见区域
//                    int srollHeight = (location[1] + focuseInputView.getHeight()) - rect.bottom;
                    final int srollHeight = view.getHeight() + location[1];
                    root.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            root.scrollTo(0, srollHeight);
                        }
                    }, 50);

//                    root.scrollTo(0, 1483);
                } else {
                    //键盘隐藏
                    root.scrollTo(0, 0);
                }
            }
        });
    }


    boolean exitRegConfig() {
        if (isReg) {
            String msg = context.getResources().getString(R.string.reg_exit_msg);
            String negStr = context.getResources().getString(R.string.btn_reg_neg);
            String postStr = context.getResources().getString(R.string.btn_reg_post);
            DialogUtil.showConfirmDlg(context, msg, negStr, postStr, true, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    doMyfinish();
                    return false;
                }
            }, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    return false;
                }
            });
            return true;
        }
        return false;
    }

    void initViews() {

        text_chang_login_reg = (TextView) findViewById(R.id.text_chang_login_reg);
        text_chang_login_reg.setOnClickListener(this);
        loginView = (LoginView) findViewById(R.id.loginView);
        regView = (RegView) findViewById(R.id.regView);


        //根据传入的tab 选中登录还是注册
        if (TAB_REG.equals(getIntent().getStringExtra("tab"))) {
//            tab2tv.performClick();
            regView.setVisibility(View.VISIBLE);
            loginView.setVisibility(View.GONE);
            text_chang_login_reg.setText(R.string.lable_login);
            isReg = true;
        } else {
            isReg = false;
            loginView.setPhone(getIntent().getStringExtra("phone"));
            text_chang_login_reg.setText(R.string.lable_register);

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v(TAG, "onNewIntent");
//        tab1tv.performClick();
        loginView.setVisibility(View.VISIBLE);
        regView.setVisibility(View.GONE);
        loginView.setPhone(intent.getStringExtra("phone"));
        text_chang_login_reg.setText(R.string.lable_register);

        isReg = false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==R.id.text_chang_login_reg){
            if(isReg){
                loginView.setVisibility(View.VISIBLE);
                regView.setVisibility(View.GONE);
                text_chang_login_reg.setText(R.string.lable_register);
            }else{
                loginView.setVisibility(View.GONE);
                regView.setVisibility(View.VISIBLE);
                text_chang_login_reg.setText(R.string.lable_login);

            }
            isReg = !isReg;
        }
    }
}
