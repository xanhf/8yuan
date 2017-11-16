package com.trade.eight.moudle.me;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.sharejoin.JoinVouchObj;
import com.trade.eight.entity.sharejoin.ShJoinObj;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.trade.activity.TradeVoucherAct;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.trade.TradeConfig;

import java.util.LinkedHashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by fangzhu on 2017/1/20.
 * 邀请好友分享拿券
 */

public class ShJoinHelp {

    private static ShJoinHelp ourInstance;

    public static ShJoinHelp getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new ShJoinHelp(context);
        return ourInstance;
    }

    private ShJoinHelp(Context context) {
    }

    /**
     * 获取活动入口的信息
     *
     * @param context
     * @param type
     */
    public synchronized CommonResponse<ShJoinObj> getActs(Context context, int type) {
        if (!new UserInfoDao(context).isLogin())
            return null;

        Map<String, String> map = new LinkedHashMap<>();
        map.put("type", type + "");
        map.put(UserInfo.UID, new UserInfoDao(context).queryUserInfo().getUserId());
        map = ApiConfig.getParamMap(context, map);
        String res = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_SHARE_JOIN_GETACTS, map);
        return CommonResponse.fromJson(res, ShJoinObj.class);
    }

    /**
     * 获取已经领取的代金券dlg
     *
     * @param context
     * @return
     */
    public synchronized CommonResponse<JoinVouchObj> getVoche(Context context) {
        if (!new UserInfoDao(context).isLogin())
            return null;

        Map<String, String> map = new LinkedHashMap<>();
        map.put(UserInfo.UID, new UserInfoDao(context).queryUserInfo().getUserId());
        map = ApiConfig.getParamMap(context, map);
        String res = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_SHARE_JOIN_GET_VOCHE, map);
        return CommonResponse.fromJson(res, JoinVouchObj.class);
    }


    /**
     * 显示新功能邀请好友
     * 1、检测首页的好友活动，有活动才显示，如果活动不显示，就不出现
     * 2、右下方的钱袋子，点击关闭后出现，只在首页
     *
     * @param context
     * @return
     */
    public static Dialog getJoinFunDlg(final Activity context, final ShJoinObj obj) {
        if (obj == null)
            return null;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        try {
            dialog.setContentView(R.layout.join_fun_dlg);

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
                        //显示小钱袋子
                        View btn_join = context.findViewById(R.id.btn_join);
                        if (btn_join != null) {
                            btn_join.setVisibility(View.VISIBLE);
                            btn_join.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EventBus.getDefault().post(new ShJoinObj());
                                    //打开活动
                                    WebActivity.start(context, obj.getLinkTitle(), obj.getLink());
                                }
                            });
                        }
                    }
                });
            }
            if (btn_commit != null) {
                btn_commit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        //打开活动
                        WebActivity.start(context, obj.getLinkTitle(), obj.getLink());
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    /**
     * 检测活动是否开启
     * 1、必须登录
     * 2、首页活动必须开启
     * 3、没有显示过dlg
     */
    public static void checkFunJoin(final Activity context) {
        if (!new UserInfoDao(context).isLogin())
            return;
        new AsyncTask<Void, Void, CommonResponse<ShJoinObj>>() {
            @Override
            protected CommonResponse<ShJoinObj> doInBackground(Void... params) {
                try {
                    return ShJoinHelp.getInstance(context).getActs(context, ShJoinObj.TYPE_PAGE_HOME_BANNER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<ShJoinObj> response) {
                super.onPostExecute(response);
                if (context.isFinishing()) {
                    return;
                }
                if (response == null)
                    return;
                if (!response.isSuccess())
                    return;
                if (response.getData() == null)
                    return;
                final ShJoinObj obj = response.getData();
                //活动已经关闭
                if (obj.getStatus() == ShJoinObj.STATUS_OFF)
                    return;
                if (!isShowedFunDlg(context)){
                    //没有显示过大dlg，就显示大dlg提示
                    Dialog d = getJoinFunDlg(context, response.getData());
                    if (d != null) {
                        d.show();
                        setShowedFunDlg(context, true);
                    }
                } else {
                    //显示小钱袋子
                    View btn_join = context.findViewById(R.id.btn_join);
                    if (btn_join != null) {
                        btn_join.setVisibility(View.VISIBLE);
                        btn_join.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EventBus.getDefault().post(new ShJoinObj());
                                //打开活动
                                WebActivity.start(context, obj.getLinkTitle(), obj.getLink());
                            }
                        });
                    }
                }


            }
        }.execute();
    }

    /**
     * 被邀请成功显示红包数
     *
     * @param context
     * @param obj
     * @return
     */
    public static Dialog getJoinRegOkDlg(final Activity context, final JoinVouchObj obj) {
        if (obj == null)
            return null;
        final Dialog dialog = new Dialog(context, R.style.dialog_Translucent_NoTitle);
        try {
            dialog.setContentView(R.layout.join_reg_get_vouch_dlg);

            Window w = dialog.getWindow();
            WindowManager.LayoutParams params = w.getAttributes();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            final View closeView = dialog.findViewById(R.id.closeView);
            final TextView tv_getvoucher = (TextView) dialog.findViewById(R.id.tv_getvoucher);
            final TextView tv_reg = (TextView) dialog.findViewById(R.id.tv_reg);
            final TextView tv_money = (TextView) dialog.findViewById(R.id.tv_money);
            final View btn_voucher = dialog.findViewById(R.id.btn_voucher);
            final View btn_trade = dialog.findViewById(R.id.btn_trade);

            if (closeView != null) {
                closeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
            if (tv_money != null) {
                tv_money.setText((obj.getInviteVoucherNum() + obj.getRegVoucherNum()) * 8 + "元红包");
            }
            if (tv_getvoucher != null)
                tv_getvoucher.setTag(obj.getInviteVoucherNum());
            if (tv_getvoucher != null)
                tv_getvoucher.setText(obj.getInviteVoucherNum() + "张");
            if (tv_reg != null)
                tv_reg.setText(obj.getRegVoucherNum() + "张");

            if (btn_voucher != null) {
                btn_voucher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        try {
                            //查看代金券
                            TradeConfig.setCurrentTradeCode(context, TradeConfig.getExchangeList(context).get(0).getExcode());
                            context.startActivity(new Intent(context, TradeVoucherAct.class));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
            if (btn_trade != null) {
                btn_trade.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        //去交易
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra(BaseInterface.TAB_MAIN_PARAME, MainActivity.WEIPAN);
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
     * 检查邀请好友红包
     *
     * @param context
     */
    public static void checkJoinReg(final Activity context) {
        if (!new UserInfoDao(context).isLogin())
            return;
        new AsyncTask<Void, Void, CommonResponse<JoinVouchObj>>() {
            @Override
            protected CommonResponse<JoinVouchObj> doInBackground(Void... params) {
                try {
                    return ShJoinHelp.getInstance(context).getVoche(context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<JoinVouchObj> response) {
                super.onPostExecute(response);
                if (context.isFinishing()) {
                    return;
                }
                if (response == null)
                    return;
                if (!response.isSuccess())
                    return;
                if (response.getData() == null)
                    return;
                final JoinVouchObj obj = response.getData();
                //红包都是0，就不显示
                if (obj.getInviteVoucherNum() == 0
                        && obj.getRegVoucherNum() == 0)
                    return;
                Dialog d = getJoinRegOkDlg(context, obj);
                if (d != null) {
                    d.show();
                }


            }
        }.execute();
    }


    public static final String SHARE_PRE = "share_join";

    /**
     * 是否显示过新功能的dlg
     *
     * @param context
     * @return
     */
    public static boolean isShowedFunDlg(Context context) {
        return context.getSharedPreferences(SHARE_PRE, 0).getBoolean("isShowedFunDlg", false);
    }

    public static void setShowedFunDlg(Context context, boolean isShow) {
        if (context == null)
            return;
        context.getSharedPreferences(SHARE_PRE, 0).edit().putBoolean("isShowedFunDlg", isShow).commit();
    }

}
