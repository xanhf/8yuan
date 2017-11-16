package com.trade.eight.moudle.trade.login;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.config.QiHuoExplainWordConfig;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trade.CheckBalanceData;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.me.TradeLoginSuccessEvent;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.outterapp.QiHuoExplainWordActivity;
import com.trade.eight.moudle.push.activity.OrderAndTradeNotifyActivity;
import com.trade.eight.moudle.trade.TradeLoginCancleEvent;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.moudle.trade.activity.CheckBalanceAct;
import com.trade.eight.moudle.trade.activity.DialogUtilForTrade;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.AESUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.OpenActivityUtil;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * 平仓
 * <p>
 * 上期所 平仓手续费分为平今  平昨  不用混合判断
 * 其他交易默认平昨,所以平仓手续费
 * <p>
 * * 手数*longMarginRatioByMoney *volumeMultiple*行情价格
 * 手数*longMarginRatioByVolume
 */

public class TradeLoginDlg {
    public static final String TAG = "TradeCreateDlg";
    public static final String MOBCLICK_TAG = "dlg_create";

    BaseActivity context;
    Dialog dialog;

    TextView text_qhlogin_cancle;
    EditText edit_qhlogin_zh;
    EditText edit_qhlogin_pwd;
    Button btn_qhlogin_submit;
    View line_qhlogin_openim;

    Button btn_qihuologin_help;

    public static final int SUCCESS = 1;
    public static final int FAIL = 2;


    public TradeLoginDlg(BaseActivity context) {
        this.context = context;
        dialog = new Dialog(context, R.style.dialog_trade);
        View rootView = View.inflate(context, R.layout.dialog_qihuo_login, null);
        dialog.setContentView(rootView);
        findViews(dialog);
    }

    public boolean isShowingDialog() {
        if (dialog != null)
            return dialog.isShowing();
        return false;
    }

    public void findViews(Dialog dialog) {

        text_qhlogin_cancle = (TextView) dialog.findViewById(R.id.text_qhlogin_cancle);
        edit_qhlogin_zh = (EditText) dialog.findViewById(R.id.edit_qhlogin_zh);
        edit_qhlogin_pwd = (EditText) dialog.findViewById(R.id.edit_qhlogin_pwd);
        btn_qhlogin_submit = (Button) dialog.findViewById(R.id.btn_qhlogin_submit);
        line_qhlogin_openim = dialog.findViewById(R.id.line_qhlogin_openim);
        btn_qihuologin_help = (Button) dialog.findViewById(R.id.btn_qihuologin_help);


        //        if (!TextUtils.isEmpty(AppSetting.getInstance(context).getWPToken(context))) {
        // 默认补上期货账号
        UserInfoDao userInfoDao = new UserInfoDao(context);
        if (userInfoDao.isLogin()) {
            String account = userInfoDao.queryUserInfo().getCurrentQHAccount();
            if (!TextUtils.isEmpty(account)) {
                edit_qhlogin_zh.setText(account);
                edit_qhlogin_zh.clearFocus();
                edit_qhlogin_zh.setSelected(false);
                edit_qhlogin_pwd.setSelected(true);
                edit_qhlogin_pwd.setFocusable(true);
                edit_qhlogin_pwd.setFocusableInTouchMode(true);
                edit_qhlogin_pwd.requestFocus();
            }
        }
//        }

        text_qhlogin_cancle.setOnClickListener(onClickListener);
        btn_qhlogin_submit.setOnClickListener(onClickListener);
        line_qhlogin_openim.setOnClickListener(onClickListener);
        btn_qihuologin_help.setOnClickListener(onClickListener);
    }


    public void showDialog(int aniStyle) {
        if (!new UserInfoDao(context).isLogin()) {
            DialogUtil.showTitleAndContentDialog(context,

                    context.getResources().getString(R.string.please_login_reg),
                    null,
                    context.getResources().getString(R.string.btn_cancle),
                    context.getResources().getString(R.string.lable_my_regandlogin),
                    null,
                    new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message message) {
                            context.startActivity(new Intent(context, LoginActivity.class));
                            return false;
                        }
                    });
            return;
        }
        if (context == null || context.isFinishing())
            return;

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        w.setGravity(Gravity.BOTTOM);
        w.setWindowAnimations(aniStyle);
        dialog.setCancelable(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
            }
        });

//        if (!TextUtils.isEmpty(AppSetting.getInstance(context).getWPToken(context))) {
        // 默认补上期货账号
        UserInfoDao userInfoDao = new UserInfoDao(context);
        if (userInfoDao.isLogin()) {
            String account = userInfoDao.queryUserInfo().getCurrentQHAccount();
            if (!TextUtils.isEmpty(account)) {
                edit_qhlogin_zh.setText(account);
                edit_qhlogin_zh.clearFocus();
                edit_qhlogin_zh.setSelected(false);
                edit_qhlogin_pwd.setSelected(true);
                edit_qhlogin_pwd.setFocusable(true);
                edit_qhlogin_pwd.setFocusableInTouchMode(true);
                edit_qhlogin_pwd.requestFocus();
            }
        }
//        }
        dialog.show();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.btn_qhlogin_submit) {
                doQiHuoLogin();
            } else if (id == R.id.text_qhlogin_cancle) {
                dialog.dismiss();
                EventBus.getDefault().post(new TradeLoginCancleEvent());
            } else if (id == R.id.line_qhlogin_openim) {
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.QIHUO_LOGIN_DLG, "期货开户");
                OpenActivityUtil.openQiHuoAccountWelcome(context);
            } else if (id == R.id.btn_qihuologin_help) {
                MyAppMobclickAgent.onEvent(context, UmengMobClickConfig.QIHUO_LOGIN_DLG, "小灯泡");
                DialogUtilForTrade.showQiHuoExplainDlg(context,
                        new String[]{QiHuoExplainWordConfig.GFQH});
            }

        }
    };


    /**
     * 期货账户登陆
     */
    private void doQiHuoLogin() {

        final String qhlogin_zh = edit_qhlogin_zh.getText().toString();
        if (TextUtils.isEmpty(qhlogin_zh)) {
            context.showCusToast("请输入期货账户");
            return;
        }
        String qhlogin_pwd = edit_qhlogin_pwd.getText().toString();
        if (TextUtils.isEmpty(qhlogin_pwd)) {
            context.showCusToast("请输入期货账户密码");
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("account", qhlogin_zh);
        try {
            map.put("password", AESUtil.encrypt(qhlogin_pwd));
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpClientHelper.doPostOption(context,
                AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_USER_LOGIN),
                map,
                null,
                new NetCallback(context) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
//                        context.showCusToast(resultMsg);
                        // 非登录时间 隐藏登录框
                        if (ApiConfig.ERROR_CODE_NOTLOGINTIME.equals(resultCode)) {
                            dialog.dismiss();
                        }
                        OrderAndTradeNotifyActivity.startLoginNotifyAct(context, FAIL, ConvertUtil.NVL(resultMsg, "网络连接失败"));
                    }

                    @Override
                    public void onResponse(String response) {

//                        context.showCusToast("登录成功");
                        OrderAndTradeNotifyActivity.startLoginNotifyAct(context, SUCCESS, "");


                        CommonResponse<UserInfo> commonResponse = CommonResponse.fromJson(response, UserInfo.class);
                        UserInfo userInfoRes = commonResponse.getData();

                        if (userInfoRes != null) {
                            //更新本地的token
                            AppSetting.getInstance(context).updateToken(userInfoRes.getToken());
                        }
                        // 缓存当前登陆的账户
                        UserInfoDao dao = new UserInfoDao(context);
                        UserInfo userInfo = dao.queryUserInfo();
                        userInfo.setCurrentQHAccount(qhlogin_zh);
                        dao.addOrUpdate(userInfo);

                        EventBus.getDefault().post(new TradeLoginSuccessEvent());

                        // 开始刷新
//                        TradeUserInfoData4Situation.getInstance(context, null).loadTradeOrderAndUserInfoData(null, false);
                        TradeUserInfoData4Situation.getInstance(context, null).startRefresh();
                        //进入期货账单确认
//                        CheckBalanceAct.startAct(context);
                        getData();
                    }
                },
                true);
        edit_qhlogin_pwd.setText("");
    }

    /**
     * 获取确认订单数据
     */
    private void getData() {
        HttpClientHelper.doPostOption(context,
                AndroidAPIConfig.URL_CHECKBALANCE_DETAIL,
                null,
                null,
                new NetCallback(context) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response) {
                        CommonResponse<CheckBalanceData> commonResponse = CommonResponse.fromJson(response, CheckBalanceData.class);
                        CheckBalanceData checkBalanceData = commonResponse.getData();
                        if (checkBalanceData != null) {
                            CheckBalanceAct.startAct(context, checkBalanceData);
                        }
                        dialog.dismiss();
                    }
                },
                true);
    }

}
