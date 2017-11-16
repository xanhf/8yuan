package com.trade.eight.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AppSetting;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.RegData;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.entity.unifypwd.AccountCheckBindAndRegData;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.me.activity.StepNavAct;
import com.trade.eight.moudle.trade.activity.TradeLoginAct;
import com.trade.eight.moudle.trade.activity.TradeRegAct;
import com.trade.eight.moudle.trade.activity.TradeUnifyPWDLoginAct;
import com.trade.eight.moudle.trade.activity.TradeUnifyPwdFirstStepAct;
import com.trade.eight.moudle.trade.activity.TradeUnifyPwdResetExchangeAct;
import com.trade.eight.moudle.trade.activity.TradeUnifyPwdSecondAct;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
import com.trade.eight.service.trade.UnifyTradePwdHelp;
import com.trade.eight.tools.nav.UNavConfig;
import com.trade.eight.tools.trade.TradeConfig;

/**
 * Created by developer on 16/1/15.
 */
public class DialogUtil {
    public static final long TIME_SHOW_MSG = 1000L;

    public static Dialog createCustomDialog(Context context, View contentView, int style, String title, String positiveBtn, String negativeBtn, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (android.os.Build.VERSION.SDK_INT >= 11 && style != -1) {
            builder = new AlertDialog.Builder(context, style);
        }
        builder.setIcon(R.drawable.liverooms_tips_icon_pressed);
        builder.setTitle(title);
        builder.setView(contentView);
        if (positiveListener != null)
            builder.setPositiveButton(positiveBtn, positiveListener);
        if (negativeListener != null)
            builder.setNegativeButton(negativeBtn, negativeListener);
        Dialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(android.R.style.Animation_Translucent);
        return dialog;
    }

    /**
     * @param activity
     * @param source   交易所
     * @param dlgShow  dlgd的回调
     * @param callback 点击取消和确定的额外事件
     * @return
     */
    public static void showTokenDialog(final BaseActivity activity, final String source,
                                       final AuthTokenDlgShow dlgShow, final AuthTokenCallBack callback) {
        Dialog d = getTokenLoginDlg(activity, null, callback);
        dlgShow.onDlgShow(d);
        d.show();
    }

    /**
     * token 过期 重新登录
     *
     * @param activity
     * @param data     跳转url地址信息
     * @return
     */
    public static Dialog getTokenLoginDlg(final Activity activity, final RegData data,
                                          final AuthTokenCallBack callBack) {
        if (activity == null || activity.isFinishing())
            return null;
        final Dialog dialog = new Dialog(activity, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_login_token_disbale);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int width = (int) activity.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);

        //默认文字信息是token过期的，如果没有toekn记录，需要改文字
        String title = activity.getResources().getString(R.string.token_title_setpwd);
        String msg = activity.getResources().getString(R.string.token_msg_setpwd);
        String btnPostStr = activity.getResources().getString(R.string.token_btn_setpwd);

        String token = AppSetting.getInstance(activity).getWPToken(activity);
        if (StringUtil.isEmpty(token)) {
            final TextView titleHit = (TextView) dialog.findViewById(R.id.titleHit);
            if (!StringUtil.isEmpty(title)) {
                titleHit.setText(title);
            }
            final TextView titleMsg = (TextView) dialog.findViewById(R.id.titleMsg);
            if (!StringUtil.isEmpty(msg)) {
                titleMsg.setText(msg);
            }
            if (!StringUtil.isEmpty(btnPostStr)) {
                btn_commit.setText(btnPostStr);
            }
        }

        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        if (btn_cancle != null) {
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callBack != null)
                        callBack.onNegClick();
                }
            });
        }
        if (btn_commit != null) {
            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
//                    activity.startActivity(new Intent(activity, TradeLoginAct.class));
                    TradeLoginDlg tradeLoginDlg = null;
                    if (tradeLoginDlg == null) {
                        tradeLoginDlg = new TradeLoginDlg((BaseActivity) activity);
                    }
                    if (!tradeLoginDlg.isShowingDialog()) {
                        tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
                    }
                    if (callBack != null) {
                        callBack.onPostClick(null);
                    }
                }
            });
        }
        dialog.setCancelable(false);
        return dialog;
    }

    /**
     * 统一交易密码 登陆提示框
     *
     * @param activity
     * @param callBack
     * @return
     */
    public static Dialog getUnifyTokenLoginDlg(final Activity activity, final AuthTokenCallBack callBack) {
        if (activity == null || activity.isFinishing())
            return null;
        final Dialog dialog = new Dialog(activity, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_login_token_disbale);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int width = (int) activity.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);

        //默认文字信息是token过期的，如果没有toekn记录，需要改文字
        String title = activity.getResources().getString(R.string.token_title_setpwd);
        String msg = activity.getResources().getString(R.string.token_msg_setpwd);
        String btnPostStr = activity.getResources().getString(R.string.token_btn_setpwd);

        String token = AppSetting.getInstance(activity).getWPToken(activity);
        if (StringUtil.isEmpty(token)) {
            final TextView titleHit = (TextView) dialog.findViewById(R.id.titleHit);
            if (!StringUtil.isEmpty(title)) {
                titleHit.setText(title);
            }
            final TextView titleMsg = (TextView) dialog.findViewById(R.id.titleMsg);
            if (!StringUtil.isEmpty(msg)) {
                titleMsg.setText(msg);
            }
            if (!StringUtil.isEmpty(btnPostStr)) {
                btn_commit.setText(btnPostStr);
            }
        }

        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        if (btn_cancle != null) {
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callBack != null)
                        callBack.onNegClick();
                }
            });
        }
        if (btn_commit != null) {
            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    activity.startActivity(new Intent(activity, TradeUnifyPWDLoginAct.class));
                    if (callBack != null) {
                        callBack.onPostClick(null);
                    }
                }
            });
        }
        dialog.setCancelable(false);
        return dialog;
    }

    /**
     * 统一交易密码 登陆提示框
     *
     * @param activity
     * @param callBack
     * @return
     */
    public static Dialog getUnifyPwdDlg4Live(final Activity activity, final AuthTokenCallBack callBack, final AccountCheckBindAndRegData accountCheckBindAndRegData) {
        if (activity == null || activity.isFinishing())
            return null;
        final Dialog dialog = new Dialog(activity, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_login_token_disbale);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int width = (int) activity.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);
        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);

        //默认文字信息是token过期的，如果没有toekn记录，需要改文字
        String title = activity.getResources().getString(R.string.unifypwd_title_oldaccount);
        String msg = activity.getResources().getString(R.string.unifypwd_msg_forquicktrade);
        String btnNegStr = activity.getResources().getString(R.string.unifypwd_posbtn_oldaccount);
        String btnPostStr = activity.getResources().getString(R.string.token_btn_init);

        final TextView titleHit = (TextView) dialog.findViewById(R.id.titleHit);
        if (!StringUtil.isEmpty(title)) {
            titleHit.setText(title);
        }
        final TextView titleMsg = (TextView) dialog.findViewById(R.id.titleMsg);
        if (!StringUtil.isEmpty(msg)) {
            titleMsg.setText(msg);
        }
        if (!StringUtil.isEmpty(btnPostStr)) {
            btn_commit.setText(btnPostStr);
        }
        if (!StringUtil.isEmpty(btnNegStr)) {
            btn_cancle.setText(btnNegStr);
        }


        if (btn_cancle != null) {
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callBack != null)
                        callBack.onNegClick();
                }
            });
        }
        if (btn_commit != null) {
            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (accountCheckBindAndRegData.isNewAccount()) {//新用户
                        TradeUnifyPwdSecondAct.startTradeUnifyPwdSecondAct(activity, true);
                    } else {// 老用户
                        Intent intent = new Intent(activity, TradeUnifyPwdFirstStepAct.class);
                        intent.putExtra("accountCheckBindAndRegData", accountCheckBindAndRegData);
                        activity.startActivity(intent);
                    }
                    if (callBack != null) {
                        callBack.onPostClick(null);
                    }
                }
            });
        }
        dialog.setCancelable(false);
        return dialog;
    }

    /**
     * 统一密码登陆  密码错误(可能存在其他平台修改密码,导致密码错误)
     *
     * @param activity
     * @param exchangeCode
     * @param callBack
     * @return
     */
    public static Dialog getResetExchangePassword(final Activity activity, final String exchangeCode, final AuthTokenCallBack callBack) {
        if (activity == null || activity.isFinishing())
            return null;
        final Dialog dialog = new Dialog(activity, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_login_token_disbale);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int width = (int) activity.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);

        //默认文字信息是token过期的，如果没有toekn记录，需要改文字
        String title = activity.getResources().getString(R.string.unifypwd_partpwd_errortitle, TradeConfig.getName(activity, exchangeCode));
        String msg = activity.getResources().getString(R.string.unifypwd_partpwd_errormsg, TradeConfig.getName(activity, exchangeCode));
        String btnPostStr = activity.getResources().getString(R.string.unifypwd_partpwd_resetbtn);

        final TextView titleHit = (TextView) dialog.findViewById(R.id.titleHit);
        if (!StringUtil.isEmpty(title)) {
            titleHit.setText(title);
        }
        final TextView titleMsg = (TextView) dialog.findViewById(R.id.titleMsg);
        if (!StringUtil.isEmpty(msg)) {
            titleMsg.setText(msg);
        }
        if (!StringUtil.isEmpty(btnPostStr)) {
            btn_commit.setText(btnPostStr);
        }

        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        if (btn_cancle != null) {
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callBack != null)
                        callBack.onNegClick();
                }
            });
        }
        if (btn_commit != null) {
            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    TradeUnifyPwdResetExchangeAct.startTradeUnifyPwdResetExchangeAct(activity, exchangeCode);
                    if (callBack != null) {
                        callBack.onPostClick(null);
                    }
                }
            });
        }
        dialog.setCancelable(false);
        return dialog;
    }

    /**
     * @param activity
     * @param source   交易所
     * @param callback
     * @return
     */
    public static Dialog getTokenInitDlg(final BaseActivity activity, final String source, final RegData data, final AuthTokenCallBack callback) {
        if (activity == null || activity.isFinishing())
            return null;
        final Dialog dialog = new Dialog(activity, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_login_token_disbale);


        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int width = (int) activity.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //code转换成name
        String sourceNmae = TradeConfig.getName(activity, source);
        //直接修改title
        String title = activity.getResources().getString(R.string.token_title_init);
        title = String.format(title, sourceNmae);
        final TextView titleHit = (TextView) dialog.findViewById(R.id.titleHit);
        if (!StringUtil.isEmpty(title)) {
            titleHit.setText(title);
        }

        //直接修改msg内容
        String msg = activity.getResources().getString(R.string.token_msg_init);
        msg = String.format(msg, sourceNmae);
        final TextView titleMsg = (TextView) dialog.findViewById(R.id.titleMsg);
        if (!StringUtil.isEmpty(msg)) {
            titleMsg.setText(msg);
        }

        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        if (btn_cancle != null) {
            btn_cancle.setText("取消");
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callback != null)
                        callback.onNegClick();
                }
            });
        }

        String token_btn_init = activity.getResources().getString(R.string.token_btn_init);
        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);
        if (btn_commit != null) {
            btn_commit.setText(token_btn_init);
            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    activity.startActivity(new Intent(activity, TradeRegAct.class));

                    if (callback != null) {
                        callback.onPostClick(null);
                    }
                }
            });
        }
        dialog.setCancelable(false);
        return dialog;
    }

    /**
     * 展示设置交易密码的对话框
     *
     * @param activity
     * @param isNewAccount//是否为新用户
     * @param callback
     * @return
     */
    public static Dialog getUnifyPWdDlg(final BaseActivity activity, final String source,
                                        final AuthTokenDlgShow dlgShow, boolean isNewAccount, final AccountCheckBindAndRegData accountCheckBindAndRegData, final AuthTokenCallBack callback) {
        if (activity == null || activity.isFinishing())
            return null;
        final Dialog dialog = new Dialog(activity, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_unifypwd);


        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int width = (int) activity.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView titleHit = (TextView) dialog.findViewById(R.id.titleHit);
        final TextView titleMsg = (TextView) dialog.findViewById(R.id.titleMsg);
        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);
        final CheckBox check_notify = (CheckBox) dialog.findViewById(R.id.check_notify);
        check_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TradeConfig.setAccountIsAllowedNotify(activity, isChecked);
//                PreferenceSetting.setBoolean(activity, PreferenceSetting.NOT_ALLOW_UNIFYPWDDLG, isChecked);
            }
        });
        if (isNewAccount) {//新用户
            if (btn_cancle != null) {
                btn_cancle.setText("取消");
                btn_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (callback != null)
                            callback.onNegClick();
                    }
                });
            }

            if (btn_commit != null) {
                btn_commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        TradeUnifyPwdSecondAct.startTradeUnifyPwdSecondAct(activity, true);
                        if (callback != null) {
                            callback.onPostClick(null);
                        }
                    }
                });
            }
        } else {// 老用户
            titleHit.setText(R.string.unifypwd_title_oldaccount);
            titleMsg.setText(R.string.unifypwd_msg_oldaccount);
            btn_cancle.setText(R.string.unifypwd_negbtn_oldaccount);
            check_notify.setVisibility(View.VISIBLE);

            if (btn_cancle != null) {
                btn_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (check_notify.isChecked()) {// 选中不在提示  出现我知道了
                            check_notify.setVisibility(View.GONE);
                            titleHit.setText(R.string.unifypwd_title_oldaccount_1);
//                            titleMsg.setText(R.string.unifypwd_msgtips_oldaccount);
                            titleMsg.setText(Html.fromHtml(activity.getResources().getString(R.string.unifypwd_msgtips_oldaccount)));
                            btn_cancle.setVisibility(View.GONE);
                            btn_commit.setText(R.string.unifypwd_posbtn_oldaccount);
                        } else {// 没有选中不在提示 直接关闭
                            dialog.dismiss();
                            if (callback != null) {
                                callback.onNegClick();
                            }
                        }
                    }
                });
            }
            if (btn_commit != null) {
                btn_commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (btn_commit.getText().equals(activity.getResources().getString(R.string.unifypwd_posbtn_oldaccount))) {
                            if (callback != null) {
                                callback.onNegClick();
//                                userNotUnifyPwdImmediately(activity, source, accountCheckBindAndRegData, dlgShow, callback);
                            }
                        } else {
                            Intent intent = new Intent(activity, TradeUnifyPwdFirstStepAct.class);
                            intent.putExtra("accountCheckBindAndRegData", accountCheckBindAndRegData);
                            activity.startActivity(intent);
                            if (callback != null) {
                                callback.onPostClick(null);
                            }
                        }
                    }
                });
            }
        }
        dialog.setCancelable(false);
        return dialog;
    }

    /**
     * 统一密码  用户暂不设置  继续原来的逻辑
     *
     * @param activity
     * @param source
     * @param dlgShow
     * @param callback
     */
    public static void userNotUnifyPwdImmediately(final BaseActivity activity, final String source, AccountCheckBindAndRegData accountCheckBindAndRegData,
                                                  final AuthTokenDlgShow dlgShow, final AuthTokenCallBack callback) {
        //当前手机号本地已经存有 初始化过交易所的密码了
        boolean isLocalCash = TradeConfig.isInitPwdLocal(activity, source);
        if (isLocalCash) {
            Dialog d = getTokenLoginDlg(activity, null, callback);
            d.show();
            if (dlgShow != null)
                dlgShow.onDlgShow(d);
            activity.hideNetLoadingProgressDialog();
            return;
        }

        activity.hideNetLoadingProgressDialog();

        // 由前面统一检测接口  优化原来接口  旨在减少网络访问
        AccountCheckBindAndRegData.ExchangeRegInfo exchangeRegInfo = accountCheckBindAndRegData.getExchangeRegInfoBySource(source);
        if (exchangeRegInfo == null) {
            activity.showCusToast(activity.getResources().getString(R.string.network_problem));
            if (callback != null)
                callback.onNegClick();
            return;
        }
        RegData data = new RegData();
        data.setReg(exchangeRegInfo.isReg());

        //优先处理农交所
        if (TradeConfig.getCurrentTradeCode(activity).equals(TradeConfig.code_jn)) {
            if (!data.isReg()) {
                //跳转注册
                //第一次初始化密码
                //如果是点击了下单按钮出现的 弄个蒙版, 选择的是农交所 需要弹窗了并且是交易大厅第一页，就认为是点击了下单按钮
                try {
                    if (!PreferenceSetting.getBoolean(activity, "trade_jn_plist")) {
                        if (activity instanceof MainActivity) {
                            MainActivity mainActivity = (MainActivity) activity;
                            ViewPager tradeViewPager = (ViewPager) mainActivity.findViewById(R.id.tradeViewPager);
                            if (tradeViewPager.getCurrentItem() == 0) {
                                StepNavAct.start(activity, StepNavAct.TYPE_JN_TRADE_LIST);
                                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Dialog d = getTokenInitDlg(activity, source, data, callback);
                if (d == null)
                    return;
                d.show();
                if (dlgShow != null)
                    dlgShow.onDlgShow(d);
            } else {
                //跳转登录
                Dialog d = getTokenLoginDlg(activity, data, callback);
                d.show();
                if (dlgShow != null)
                    dlgShow.onDlgShow(d);
            }
            return;
        }

        //广贵和哈贵
        if (!data.isReg()) {
            //第一次初始化密码
            Dialog d = getTokenInitDlg(activity, source, data, callback);
            if (d == null)
                return;
            d.show();
            if (dlgShow != null)
                dlgShow.onDlgShow(d);
        } else {
            //登录
            Dialog d = getTokenLoginDlg(activity, data, callback);
            d.show();
            if (dlgShow != null)
                dlgShow.onDlgShow(d);
        }
    }

    /**
     * 对话框 单条信息  单按钮 不带回调
     *
     * @param context
     * @param msg
     * @param btnMsg
     */
    public static void showMsgDialog(Activity context, String msg, String btnMsg) {
        showMsgDialog(context, msg, btnMsg, null);
    }

    /**
     * 对话框 单条信息  单按钮 带回调
     *
     * @param context
     * @param msg
     * @param btnMsg
     * @param callback
     */
    public static void showMsgDialog(Activity context, String msg, String btnMsg, final Handler.Callback callback) {
        if (context == null || context.isFinishing())
            return;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.app_dialog_show_msg);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int width = (int) context.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView tv_msg = (TextView) dialog.findViewById(R.id.tv_msg);
        if (tv_msg != null
                && !StringUtil.isEmpty(msg))
            tv_msg.setText(msg);
        final Button btnPos = (Button) dialog.findViewById(R.id.btnPos);
        if (btnPos != null) {
            if (!StringUtil.isEmpty(btnMsg))
                btnPos.setText(btnMsg);
            btnPos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callback != null) {
                        Message message = new Message();
                        callback.handleMessage(message);
                    }
                }
            });
        }
        dialog.setCancelable(false);
        dialog.show();

    }

    /**
     * 对话框 标题 内容  单按钮 带回调
     *
     * @param context
     * @param title
     * @param content
     * @param btnMsg
     * @param callback
     */
    public static void showTitleAndContentDialog(Activity context, String title, String content, String btnMsg, final Handler.Callback callback) {
        if (context == null || context.isFinishing())
            return;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.app_dialog_show_titlecontent);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int width = (int) context.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        if (tv_title != null)
            tv_title.setText(title);
        final TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        if (tv_content != null)
            tv_content.setText(content);
        final Button btnPos = (Button) dialog.findViewById(R.id.btnPos);
        if (btnPos != null) {
            if (!StringUtil.isEmpty(btnMsg))
                btnPos.setText(btnMsg);
            btnPos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callback != null) {
                        Message message = new Message();
                        callback.handleMessage(message);
                    }
                }
            });
        }
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 对话框 标题 内容  双按钮 双按钮文字 都带回调
     *
     * @param context
     * @param title
     * @param content
     * @param btnNavMsg
     * @param btnPosMsg
     * @param navCallback
     * @param posCallback
     */
    public static Dialog showTitleAndContentDialog(Activity context, String title, String content, String btnNavMsg, String btnPosMsg, final Handler.Callback navCallback, final Handler.Callback posCallback) {
        if (context == null || context.isFinishing())
            return null;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.app_dialog_show_titlecontent_2btn);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int width = (int) context.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        if (tv_title != null)
            tv_title.setText(title);
        final TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        if (tv_content != null) {
            if (TextUtils.isEmpty(content)) {
                tv_content.setVisibility(View.GONE);
            } else {
                tv_content.setVisibility(View.VISIBLE);
                tv_content.setText(content);
            }
        }
        final Button btnPos = (Button) dialog.findViewById(R.id.btnPos);
        if (btnPos != null) {
            if (!StringUtil.isEmpty(btnPosMsg))
                btnPos.setText(btnPosMsg);
            btnPos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (posCallback != null) {
                        Message message = new Message();
                        posCallback.handleMessage(message);
                    }
                }
            });
        }
        final Button btnNav = (Button) dialog.findViewById(R.id.btnNav);
        if (btnNav != null) {
            if (!StringUtil.isEmpty(btnNavMsg)) {
                btnNav.setText(btnNavMsg);
            }
            btnNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (navCallback != null) {
                        Message message = new Message();
                        navCallback.handleMessage(message);
                    }
                }
            });
        }
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    /**
     * 对话框 标题 内容  双按钮 双按钮文字 都带回调
     *
     * @param context
     * @param title
     * @param content
     * @param btnNavMsg
     * @param btnPosMsg
     * @param navCallback
     * @param posCallback
     */
    public static Dialog showTitleAndContentDialog(Activity context, String title, String content, String btnNavMsg, String btnPosMsg, final Handler.Callback navCallback, final Handler.Callback posCallback, boolean isCancleable) {
        if (context == null || context.isFinishing())
            return null;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.app_dialog_show_titlecontent_2btn);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int width = (int) context.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        if (tv_title != null)
            tv_title.setText(title);
        final TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        if (tv_content != null)
            tv_content.setText(content);
        final Button btnPos = (Button) dialog.findViewById(R.id.btnPos);
        if (btnPos != null) {
            if (!StringUtil.isEmpty(btnPosMsg))
                btnPos.setText(btnPosMsg);
            btnPos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (posCallback != null) {
                        Message message = new Message();
                        posCallback.handleMessage(message);
                    }
                }
            });
        }
        final Button btnNav = (Button) dialog.findViewById(R.id.btnNav);
        if (btnNav != null) {
            if (!StringUtil.isEmpty(btnNavMsg)) {
                btnNav.setText(btnNavMsg);
            }
            btnNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (navCallback != null) {
                        Message message = new Message();
                        navCallback.handleMessage(message);
                    }
                }
            });
        }
        dialog.setCancelable(isCancleable);
        dialog.show();
        return dialog;
    }

    /**
     *
     *
     * @param context
     * @param title
     * @param content
     * @param btnNavMsg
     * @param btnPosMsg
     * @param navCallback
     * @param posCallback
     */

    /**
     * 对话框 标题 内容  双按钮 双按钮文字 都带回调
     * 标题带icon
     *
     * @param context
     * @param title
     * @param content
     * @param btnNavMsg
     * @param btnPosMsg
     * @param showIcon
     * @param navCallback
     * @param posCallback
     * @return
     */
    public static Dialog showTitleAndContentDialog(Activity context, String title, String content, String btnNavMsg, String btnPosMsg, boolean showIcon, final Handler.Callback navCallback, final Handler.Callback posCallback) {
        if (context == null || context.isFinishing())
            return null;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.app_dialog_show_titlecontent_2btn);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int width = (int) context.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        if (tv_title != null)
            tv_title.setText(title);
        ImageView img_titleicon = (ImageView) dialog.findViewById(R.id.img_titleicon);
        if (showIcon) {
            img_titleicon.setVisibility(View.VISIBLE);
        } else {
            img_titleicon.setVisibility(View.GONE);
        }
        final TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        if (tv_content != null)
            tv_content.setText(content);
        final Button btnPos = (Button) dialog.findViewById(R.id.btnPos);
        if (btnPos != null) {
            if (!StringUtil.isEmpty(btnPosMsg))
                btnPos.setText(btnPosMsg);
            btnPos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (posCallback != null) {
                        Message message = new Message();
                        posCallback.handleMessage(message);
                    }
                }
            });
        }
        final Button btnNav = (Button) dialog.findViewById(R.id.btnNav);
        if (btnNav != null) {
            if (!StringUtil.isEmpty(btnNavMsg)) {
                btnNav.setText(btnNavMsg);
            }
            btnNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (navCallback != null) {
                        Message message = new Message();
                        navCallback.handleMessage(message);
                    }
                }
            });
        }
        dialog.setCancelable(true);
        dialog.show();
        return dialog;
    }


    /**
     * 操作成功的提示  icon ， 文字
     * 下单成功
     *
     * @param context
     * @param msg     null 使用默认
     * @param icon    0 使用默认
     */
    public static void showSuccessDialog(Activity context, String msg, int icon, final Handler.Callback callback) {
        if (context == null || context.isFinishing())
            return;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_icon_success);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int width = (int) context.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView titleHit = (TextView) dialog.findViewById(R.id.titleHit);
        if (titleHit != null && !StringUtil.isEmpty(msg))
            titleHit.setText(msg);
        final ImageView iconImg = (ImageView) dialog.findViewById(R.id.icon);
        try {
            if (iconImg != null && icon != 0)
                iconImg.setImageResource(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                dialog.dismiss();
                if (callback != null)
                    callback.handleMessage(new Message());
            }
        };
        //延时关闭
        handler.sendEmptyMessageDelayed(0, TIME_SHOW_MSG);
        dialog.setCancelable(false);
        dialog.show();

    }

    /**
     * 操作成功的提示  icon ， 文字
     * 下单成功 小提示框
     *
     * @param context
     * @param msg     null 使用默认
     * @param icon    0 使用默认
     */
    public static void showSuccessSmallDialog(Activity context, String msg, int icon, final Handler.Callback callback) {
        if (context == null || context.isFinishing())
            return;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_icon_success_small);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int width = (int) context.getResources().getDimension(R.dimen.margin_160dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = (int) context.getResources().getDimension(R.dimen.margin_60dp);

        final TextView titleHit = (TextView) dialog.findViewById(R.id.titleHit);
        if (titleHit != null && !StringUtil.isEmpty(msg))
            titleHit.setText(msg);
        final ImageView iconImg = (ImageView) dialog.findViewById(R.id.icon);
        try {
            if (iconImg != null && icon != 0)
                iconImg.setImageResource(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                dialog.dismiss();
                if (callback != null)
                    callback.handleMessage(new Message());
            }
        };
        //延时关闭
        handler.sendEmptyMessageDelayed(0, TIME_SHOW_MSG);
        dialog.setCancelable(false);
        dialog.show();

    }

    /**
     * 建仓成功
     *
     * @param context
     * @param msg
     * @param callback
     */
    public static void showTradeSuccessDlg(Activity context, String msg, final Handler.Callback callback) {
        if (context == null || context.isFinishing())
            return;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_trade_success);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int width = (int) context.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView titleHit = (TextView) dialog.findViewById(R.id.titleHit);
        if (titleHit != null && !StringUtil.isEmpty(msg))
            titleHit.setText(msg);
//        final ImageView iconImg = (ImageView) dialog.findViewById(R.id.icon);
//        try {
//            if (iconImg != null && icon != 0)
//                iconImg.setImageResource(icon);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        if (btn_cancle != null) {
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });
        }

        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);
        if (btn_commit != null) {
            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callback != null) {
                        callback.handleMessage(new Message());
                    }
                }
            });
        }

        dialog.setCancelable(false);
        dialog.show();

    }

    /**
     * 积分兑换代金券
     *
     * @param context
     * @param msg
     * @param callback
     */
    public static void showExchangeIntegralSuccessDlg(Activity context, String msg, final Handler.Callback callback) {
        if (context == null || context.isFinishing())
            return;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_trade_success);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int width = (int) context.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView titleHit = (TextView) dialog.findViewById(R.id.titleHit);
        if (titleHit != null && !StringUtil.isEmpty(msg))
            titleHit.setText(msg);
//        final ImageView iconImg = (ImageView) dialog.findViewById(R.id.icon);
//        try {
//            if (iconImg != null && icon != 0)
//                iconImg.setImageResource(icon);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);

        btn_cancle.setText("查看代金券");
        btn_commit.setText("继续兑换");
        if (btn_commit != null) {
            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });
        }
        if (btn_cancle != null) {
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callback != null) {
                        callback.handleMessage(new Message());
                    }
                }
            });
        }

        dialog.setCancelable(false);
        dialog.show();

    }

    /**
     * 下单确认
     *
     * @param context
     * @param upOrdown 买涨买跌
     * @param totalMsg 总花费
     * @param unitMsg  单位
     * @param callback 确定按钮的监听事件
     */
    public static void tradeCreateConfigDlg(Activity context, int upOrdown, String nameMsg,
                                            String totalMsg, String unitMsg, final Handler.Callback callback) {
        if (context == null || context.isFinishing())
            return;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_trade_create_confirm);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int width = (int) context.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView tv_type = (TextView) dialog.findViewById(R.id.tv_type);
        final TextView tv_total = (TextView) dialog.findViewById(R.id.tv_total);
        final TextView tv_unit = (TextView) dialog.findViewById(R.id.tv_unit);
        tv_type.setText(nameMsg);
        tv_total.setText(totalMsg);
        tv_unit.setText(unitMsg);
        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        if (btn_cancle != null) {
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });
        }

        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);
        if (btn_commit != null) {
            if (upOrdown == TradeProduct.TYPE_BUY_DOWN)
                btn_commit.setBackgroundResource(R.drawable.index_weipan_btn_sell_bg);
            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callback != null) {
                        Message message = new Message();
                        callback.handleMessage(message);
                    }
                }
            });
        }
        dialog.setCancelable(false);
        dialog.show();

    }

    /**
     * @param context
     * @param msg
     * @param btnNegEnable 是否显示 取消按钮
     * @param callbackPost
     */
    public static Dialog getConfirmDlg(Activity context, String msg, boolean btnNegEnable, final Handler.Callback callbackNeg, final Handler.Callback callbackPost) {
        if (context == null || context.isFinishing())
            return null;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_confirm);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int width = (int) context.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView titleHit = (TextView) dialog.findViewById(R.id.titleHit);
        titleHit.setText(msg);
        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        if (btn_cancle != null) {
            if (!btnNegEnable)
                btn_cancle.setVisibility(View.GONE);
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callbackNeg != null)
                        callbackNeg.handleMessage(new Message());
                }
            });
        }

        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);
        if (btn_commit != null) {
            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callbackPost != null) {
                        Message message = new Message();
                        callbackPost.handleMessage(message);
                    }
                }
            });
        }
        dialog.setCancelable(false);
        return dialog;

    }

    /**
     * 确认下单  确认平仓 dialog样式
     *
     * @param context
     * @param msg
     * @param btnNegEnable
     * @param callback
     */
    public static void showTradeConfirmDlg(Activity context, String msg, boolean btnNegEnable, final Handler.Callback callback) {
        Dialog d = getConfirmDlg(context, msg, btnNegEnable, null, callback);
        if (d != null)
            d.show();
    }

    /**
     * 确认登录 dialog
     * showConfirmDlg
     *
     * @param context
     * @param msg
     * @param btnNegEnable
     * @param callback
     */
    public static void showLoginConfirmDlg(Activity context, String msg, boolean btnNegEnable, final Handler.Callback callback) {
        showConfirmDlg(context, msg, null, "登录", btnNegEnable, null, callback);
    }

    /**
     * 默认确定样式
     *
     * @param context
     * @param msg
     * @param callback
     */
    public static void showConfirmDlg(Activity context, String msg, final Handler.Callback callback) {
        showConfirmDlg(context, msg, null, null, true, null, callback);
    }

    /**
     * 确认登录 dialog
     * with btn for callbackNeg
     *
     * @param context
     * @param msg
     * @param btnNegEnable
     * @param callbackNeg
     * @param callbackPost
     */
    public static Dialog showConfirmDlg(Activity context, String msg, String negMsg, String postMsg, boolean btnNegEnable, final Handler.Callback callbackNeg, final Handler.Callback callbackPost) {
        if (context == null || context.isFinishing())
            return null;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_login_confirm);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int width = (int) context.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView titleHit = (TextView) dialog.findViewById(R.id.titleHit);
        if (!StringUtil.isEmpty(msg))
            titleHit.setText(msg);
        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        if (btn_cancle != null) {
            if (!StringUtil.isEmpty(negMsg))
                btn_cancle.setText(negMsg);
            if (!btnNegEnable)
                btn_cancle.setVisibility(View.GONE);
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callbackNeg != null)
                        callbackNeg.handleMessage(new Message());
                }
            });
        }

        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);
        if (btn_commit != null) {
            if (!StringUtil.isEmpty(postMsg))
                btn_commit.setText(postMsg);
            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callbackPost != null) {
                        Message message = new Message();
                        callbackPost.handleMessage(message);
                    }
                }
            });
        }
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    /**
     * 自定义toast
     *
     * @param context
     * @param msg
     */
    public static void toast(Context context, String msg) {
        if (context == null)
            return;
//        Toast toast = Toast.makeText(context,
//                msg, Toast.LENGTH_LONG);
        Toast toast = new Toast(context);
        View view = View.inflate(context, R.layout.app_toast, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        toast.setView(view);
        tv_title.setText(msg);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 全局的加载 dialog
     *
     * @param context
     * @return
     */
    public static Dialog getLoadingDlg(Context context, String msg) {
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.app_loading_layout);
        ImageView img_appload = (ImageView) dialog.findViewById(R.id.img_appload);
        AnimationDrawable animationDrawable = (AnimationDrawable) img_appload.getDrawable();
        animationDrawable.start();

//        TextView apptextview = (TextView) dialog.findViewById(R.id.apptextview);
//        if (apptextview != null) {
//            apptextview.setText(ConvertUtil.NVL(msg, context.getResources().getString(R.string.str_msg_longing)));
//        }
        dialog.setCancelable(true);
        return dialog;
    }

    /**
     * 显示红包dlg
     *
     * @param context
     * @return
     */
    public static Dialog getHBDlg(final Activity context) {
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        try {
            dialog.setContentView(R.layout.dialog_new_tips);

            Window w = dialog.getWindow();
            WindowManager.LayoutParams params = w.getAttributes();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            final View closeView = dialog.findViewById(R.id.closeView);
            final View btn_commit = dialog.findViewById(R.id.btn_commit);

            if (closeView != null) {
                closeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
            if (btn_commit != null) {
                btn_commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.putExtra("tab", LoginActivity.TAB_REG);
                        context.startActivity(intent);

                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    /**
     * 注册完成之后 领取红包弹窗
     *
     * @param context
     */
    public static Dialog getHBDlgRegOK(final Activity context) {
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        try {
            dialog.setContentView(R.layout.dialog_u_reg_ok);

            Window w = dialog.getWindow();
            WindowManager.LayoutParams params = w.getAttributes();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            final View btn_cancle = dialog.findViewById(R.id.btn_cancle);
            final View btn_commit = dialog.findViewById(R.id.btn_commit);

            if (btn_cancle != null) {
                btn_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });
            }
            if (btn_commit != null) {
                btn_commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        UNavConfig.setShowStep01(context, true);

                        if (!new UserInfoDao(context).isLogin()) {
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                            return;
                        }
                        //到下单页面
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.WEIPAN);
                        intent.putExtra(BaseInterface.TAB_PARAME_INDEX, 0);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    /**
     * 交易密码(提现输入)
     *
     * @param context
     * @param callback
     */
    public static void showInputTradePasswordDlg(final Activity context, final Handler.Callback callback) {
        if (context == null || context.isFinishing())
            return;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_input_tradepassword);
        dialog.setCancelable(true);
        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int width = (int) context.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText edInputTradePassword = (EditText) dialog.findViewById(R.id.ed_input_tradepassword);


        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_confirm);
        if (btn_commit != null) {
            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pwd = edInputTradePassword.getText().toString().trim();
                    if (TextUtils.isEmpty(pwd)) {
                        ((BaseActivity) context).showCusToast("请输入交易密码");
                        return;
                    }
                    dialog.dismiss();
                    if (callback != null) {
                        Message message = new Message();
                        message.obj = pwd;
                        callback.handleMessage(message);
                    }
                }
            });
        }

//        dialog.setCancelable(false);
        dialog.show();

    }

    public static void showMsgForTwoLinesDialog(Activity context, String msg, String msg_1, String btnMsg, final Handler.Callback callback) {
        if (context == null || context.isFinishing())
            return;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.app_dialog_show_twolinesmsg);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int width = (int) context.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView tv_msg = (TextView) dialog.findViewById(R.id.tv_msg);
        if (tv_msg != null)
            tv_msg.setText(msg);
        final TextView tv_msg_1 = (TextView) dialog.findViewById(R.id.tv_msg_secondline);
        if (tv_msg_1 != null)
            tv_msg_1.setText(msg_1);
        final Button btnPos = (Button) dialog.findViewById(R.id.btnPos);
        if (btnPos != null) {
            if (!StringUtil.isEmpty(btnMsg))
                btnPos.setText(btnMsg);
            btnPos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callback != null) {
                        Message message = new Message();
                        callback.handleMessage(message);
                    }
                }
            });
        }
        dialog.setCancelable(false);
        dialog.show();

    }

    /**
     * 验证token的dialog
     */
    public interface AuthTokenCallBack {
        //确定按钮
        void onPostClick(Object obj);

        //取消按钮
        void onNegClick();
    }


    /**
     * dlg 显示的回调
     */
    public interface AuthTokenDlgShow {
        void onDlgShow(Dialog dlg);
    }

    /**
     * 包含title msg 确定 取消按钮的dlg
     *
     * @param activity
     * @param title
     * @param msg
     * @param negPostStr
     * @param btnPostStr
     * @param callBack
     * @return
     */
    public static Dialog getAllInfoMsgDlg(final Activity activity, String title,
                                          String msg, String negPostStr, String btnPostStr,
                                          final AuthTokenCallBack callBack) {
        if (activity == null || activity.isFinishing())
            return null;
        final Dialog dialog = new Dialog(activity, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_msg_config);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int width = (int) activity.getResources().getDimension(R.dimen.margin_270dp);
        if (width >= screenWidth * 0.9)
            params.width = (int) (screenWidth * 0.75);
        else
            params.width = width;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);


        final TextView titleHit = (TextView) dialog.findViewById(R.id.titleHit);
        if (!StringUtil.isEmpty(title)) {
            titleHit.setText(title);
        }
        final TextView titleMsg = (TextView) dialog.findViewById(R.id.titleMsg);
        if (!StringUtil.isEmpty(msg)) {
            titleMsg.setText(msg);
        }
        if (!StringUtil.isEmpty(btnPostStr)) {
            btn_commit.setText(btnPostStr);
        }
        if (!StringUtil.isEmpty(negPostStr)) {
            btn_cancle.setText(negPostStr);
        }


        if (btn_cancle != null) {
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callBack != null)
                        callBack.onNegClick();
                }
            });
        }
        if (btn_commit != null) {
            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callBack != null) {
                        callBack.onPostClick(null);
                    }
                }
            });
        }
        dialog.setCancelable(false);
        return dialog;
    }

    /**
     * 休市提醒的dlg
     *
     * @param activity
     * @param title
     * @param msg
     * @param callBack
     * @return
     */
    public static Dialog getTradeCloseDlg(final Activity activity, String title, String msg,
                                          final AuthTokenCallBack callBack) {
        String negStr = activity.getResources().getString(R.string.close_btn_neg);
        String postStr = activity.getResources().getString(R.string.close_btn_post);
        return getAllInfoMsgDlg(activity, title, msg, negStr, postStr, callBack);
    }

    /**
     * 首页积分弹窗
     *
     * @param context
     * @param callback
     */
    public static void showHomeIntegralDialog(Activity context, final Handler.Callback callback) {
        if (context == null || context.isFinishing())
            return;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_homeintegral);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        params.width = (int) context.getResources().getDimension(R.dimen.margin_266dp);
        params.height = (int) context.getResources().getDimension(R.dimen.margin_337dp);

        final Button btn_dialog_close = (Button) dialog.findViewById(R.id.btn_dialog_close);
        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);

        if (btn_dialog_close != null) {
            btn_dialog_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });
        }

        if (btn_cancle != null) {
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });
        }
        if (btn_commit != null) {
            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callback != null) {
                        callback.handleMessage(new Message());
                    }
                }
            });
        }
        dialog.setCancelable(false);

        UNavConfig.setShowIntegralDlg(context, true);

        dialog.show();
    }

    /**
     * "我的"积分等级升级弹窗
     *
     * @param context
     * @param level
     * @param callback
     */
    public static void showHomeUpLvDialog(Activity context, int level, final Handler.Callback callback) {
        if (context == null || context.isFinishing())
            return;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_home_uplv);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        params.width = (int) context.getResources().getDimension(R.dimen.margin_266dp);
        params.height = (int) context.getResources().getDimension(R.dimen.margin_339dp);

        ImageView img_uplv = (ImageView) dialog.findViewById(R.id.img_uplv);
        switch (level) {
            case 2:
                img_uplv.setImageResource(R.drawable.img_uplv_2);
                break;
            case 3:
                img_uplv.setImageResource(R.drawable.img_uplv_3);
                break;
            case 4:
                img_uplv.setImageResource(R.drawable.img_uplv_4);
                break;
            case 5:
                img_uplv.setImageResource(R.drawable.img_uplv_5);
                break;
            case 6:
                img_uplv.setImageResource(R.drawable.img_uplv_6);
                break;
            case 7:
                img_uplv.setImageResource(R.drawable.img_uplv_7);
                break;
        }

        final Button btn_dialog_close = (Button) dialog.findViewById(R.id.btn_dialog_close);
        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);

        if (btn_dialog_close != null) {
            btn_dialog_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (btn_cancle != null) {
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        if (btn_commit != null) {
            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callback != null) {
                        callback.handleMessage(new Message());
                    }
                }
            });
        }
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 答题完成弹框
     *
     * @param context
     * @param navCallback
     * @param posCallback
     */
    public static void showAQCompleteDlg(Activity context, final Handler.Callback navCallback, final Handler.Callback posCallback) {
        if (context == null || context.isFinishing())
            return;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_aq_complete);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        params.width = (int) context.getResources().getDimension(R.dimen.margin_266dp);
        params.height = (int) context.getResources().getDimension(R.dimen.margin_288dp);

        String displayTitle = context.getResources().getString(R.string.aq_complete_title);
        SpannableString ss = new SpannableString(displayTitle);
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.mission_center_orange)), 5, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_title.setText(ss);
        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);


        if (btn_cancle != null) {
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (navCallback != null) {
                        navCallback.handleMessage(new Message());
                    }
                }
            });
        }

        if (btn_commit != null) {
            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (posCallback != null) {
                        posCallback.handleMessage(new Message());
                    }
                }
            });
        }
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 首页展示任务中心
     *
     * @param context
     * @param callback
     */
    public static void showHomeMissionCenterDialog(Activity context, final Handler.Callback callback) {
        if (context == null || context.isFinishing())
            return;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        dialog.setContentView(R.layout.dialog_home_missioncenter);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        params.width = (int) context.getResources().getDimension(R.dimen.margin_266dp);
        params.height = (int) context.getResources().getDimension(R.dimen.margin_308dp);

        final Button btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        final Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);


        if (btn_cancle != null) {
            btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();

                }
            });
        }
        if (btn_commit != null) {
            btn_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (callback != null) {
                        callback.handleMessage(new Message());
                    }
                }
            });
        }
        dialog.setCancelable(false);

        UNavConfig.setShowMissionCenterDlg(context, true);

        dialog.show();
    }
}
