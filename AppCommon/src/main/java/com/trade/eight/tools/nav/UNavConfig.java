package com.trade.eight.tools.nav;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppStartUpConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.HomeActData;
import com.trade.eight.entity.RegData;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.me.activity.IntegralMarketActivity;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.me.activity.MissionCenterAct;
import com.trade.eight.moudle.netty.NettyClient;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.trade.activity.TradeLoginAct;
import com.trade.eight.moudle.trade.activity.TradeRegAct;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.NetWorkUtils;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.trade.TradeConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fangzhu on 16/8/18.
 */
public class UNavConfig {

    private static String SHARE_PRE = "user_nav";

    /**
     * 1、新用户弹出 新人红包，已经登录的用户没有这个逻辑
     * 新用户判断：当前设备还没有用户登录过
     * 标示是否是新手来判断受否为新手
     * 2、点击 注册领券，去注册逻辑
     * 3、大红包消失后，小红包显示；点击小红包，小红包消失，大红包显示
     *
     * @param activity
     */
    public static void initDlgView(final Activity activity, final String tag) {
        final View smallView = activity.findViewById(R.id.smallView);
        if (smallView == null)
            return;
        smallView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smallView.setVisibility(View.GONE);
                showDlgUIndex(activity);
            }
        });

        //已经登录了
        if (isLogined(activity)) {
            smallView.setVisibility(View.GONE);
            if (hBDlg != null && hBDlg.isShowing())
                hBDlg.dismiss();
            if (hBRegDlg != null && hBRegDlg.isShowing()) {
                hBRegDlg.dismiss();
            }
            return;
        }
        if (isShowSmallDlg(activity)) {
            //需要显示小红包
            smallView.setVisibility(View.VISIBLE);
            if (hBDlg != null && hBDlg.isShowing())
                hBDlg.dismiss();
            if (hBRegDlg != null && hBRegDlg.isShowing()) {
                hBRegDlg.dismiss();
            }
            return;
        } else {
            smallView.setVisibility(View.GONE);
            if (!new UserInfoDao(activity).isLogin()) {
                //显示新手红包
                if (UNavConfig.isShowHBDlg(activity) && isNewUser(activity)) {
                    //没有登录并且是新手并且需要显示新手红包
                    showDlgUIndex(activity);
                }
            } else {
                if (isLogined(activity)) {
                    //已经手动登录过了
                    return;
                }

                if (isShowHBRegOkDlg(activity)) {
                    //注册成功之后的弹窗  去除新手引导
//                    showDlgHBRegOk(activity);
                }
            }
        }

    }

    /**
     * 首页积分弹窗
     *
     * @param activity
     * @param tag
     */
    public static void initHomeIntegralDlg(final Activity activity, String tag) {

        if (!MainActivity.HOME.equals(tag)) {// 必须在首页
            return;
        }
        if (hBDlg != null && hBDlg.isShowing()) {
            return;
        }
        if (hBRegDlg != null && hBRegDlg.isShowing()) {
            return;
        }
        if (isShowIntegralDlg(activity)) {
            return;
        }
        DialogUtil.showHomeIntegralDialog(activity, new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                IntegralMarketActivity.startIntegralMarketActivity(activity, null);
                return false;
            }
        });
    }

    /**
     * 首页任务中心弹窗
     *
     * @param activity
     * @param tag
     */
    public static void initHomeMissionCenterDlg(final Activity activity, String tag) {

        if (!MainActivity.HOME.equals(tag)) {// 必须在首页
            return;
        }
        if (!new UserInfoDao(activity).isLogin()) {
            return;
        }
        if (isShowMissionCenterDlg(activity)) {
            return;
        }
        DialogUtil.showHomeMissionCenterDialog(activity, new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                MissionCenterAct.startAct(activity);
                return false;
            }
        });
    }


    /**
     * 首页比赛按鈕控制
     * 已经登录了  并且当前是在首页  就显示左下角比赛按钮
     *
     * @param activity
     */
    public static void initBtnHomeCompetity(final Activity activity, String tag) {
        final View btnHomeCompetity = activity.findViewById(R.id.btn_homecompetity);
        if (btnHomeCompetity == null)
            return;
        btnHomeCompetity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NettyClient.getInstance(activity).getStartupObj() == null) {
                    return;
                }

                String url = NettyClient.getInstance(activity).getStartupObj().getGameUrl();
                if (StringUtil.isEmpty(url))
                    return;

                Map<String, String> paramMap = new HashMap<String, String>();
                UserInfoDao dao = new UserInfoDao(activity);
                if (!dao.isLogin())
                    return;
                paramMap.put("userId", dao.queryUserInfo().getUserId());
                paramMap = ApiConfig.getParamMap(activity, paramMap);
                url = NetWorkUtils.setParam4get(url, paramMap);
                MyAppMobclickAgent.onEvent(activity, "page_home", "homecompety");
                WebActivity.start(activity, activity.getResources().getString(R.string.lable_compety), url);
            }
        });

        if (NettyClient.getInstance(activity).getStartupObj() == null) {
            btnHomeCompetity.setVisibility(View.GONE);
            return;
        }
        UserInfoDao dao = new UserInfoDao(activity);
        //已经登录了
        if (dao.isLogin() && !StringUtil.isEmpty(NettyClient.getInstance(activity).getStartupObj().getGameUrl())) {
            if (MainActivity.HOME.equals(tag)) {

                btnHomeCompetity.setVisibility(View.VISIBLE);
            } else {
                btnHomeCompetity.setVisibility(View.GONE);

            }
            return;
        } else {
            btnHomeCompetity.setVisibility(View.GONE);
        }


    }

    /**
     * 新手的引导  只对哈贵广贵有用
     * 注意内存溢出
     */
    public static void initStepNav(final Activity activity) {
        if (activity == null)
            return;
        if (TradeConfig.isCurrentJN(activity))
            return;
        //如果不是新手
        if (!new UserInfoDao(activity).isLogin()
                && !isReged(activity))
            return;
        if (isLogined(activity))
            return;
        //如果引导注册dlg在就不显示新手蒙板
        if (isShowHBRegOkDlg(activity))
            return;
        final View homeHelpLayout = activity.findViewById(R.id.homeHelpLayout);
        final ImageView homeHelpView = (ImageView) activity.findViewById(R.id.homeHelpView);
        final View btnSkip = activity.findViewById(R.id.btnSkip);

        if (homeHelpLayout == null)
            return;

        //已经开启了跳过操作
        if (UNavConfig.isSkipStep(activity)) {
            if (homeHelpLayout.getVisibility() == View.VISIBLE)
                homeHelpLayout.setVisibility(View.GONE);
            return;
        }
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeHelpLayout.setVisibility(View.GONE);
                UNavConfig.setSkipStep(activity, true);
            }
        });

        //显示第一步去设置哈贵密码
        if (UNavConfig.isShowStep01(activity)) {
            try {
                //跳过按钮不显示
                btnSkip.setVisibility(View.GONE);
                homeHelpView.setImageResource(R.drawable.unav_step01);
                homeHelpLayout.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                homeHelpLayout.setVisibility(View.GONE);
                UNavConfig.setShowStep01(activity, false);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                homeHelpLayout.setVisibility(View.GONE);
                UNavConfig.setShowStep01(activity, false);
                System.gc();//通知gc回收
            }
            homeHelpView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //设置不显示第一步
                    UNavConfig.setShowStep01(activity, false);
                    homeHelpLayout.setVisibility(View.GONE);

                    //设置哈贵交易密码
                    if (new UserInfoDao(activity).isLogin()) {
                        //已经登录 直接去token 检测页面
                        check((BaseActivity) activity);
                    } else {
                        //还没有登录
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivity(intent);
                    }
                }
            });

        } else if (UNavConfig.isShowStep02(activity)) {
            //第二步
            if (UNavConfig.isShowStep02(activity)) {
                homeHelpLayout.setVisibility(View.VISIBLE);
                try {
                    btnSkip.setVisibility(View.VISIBLE);
                    homeHelpView.setImageResource(R.drawable.unav_step02);
                    homeHelpLayout.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    UNavConfig.setShowStep02(activity, false);
                    homeHelpLayout.setVisibility(View.GONE);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    UNavConfig.setShowStep02(activity, false);
                    homeHelpLayout.setVisibility(View.GONE);
                    System.gc();//通知gc回收
                }

                homeHelpView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UNavConfig.setShowStep02(activity, false);
                        UNavConfig.setShowStep03(activity, true);
                        //第三步放在下单dialog出现的时候，TradeCreateUtil
                        homeHelpLayout.setVisibility(View.GONE);
                    }
                });
            } else {
                homeHelpLayout.setVisibility(View.GONE);
            }
        }
    }


    static Dialog hBDlg = null;

    /**
     * 首页新手红包
     *
     * @param activity
     */
    static void showDlgUIndex(final Activity activity) {
        if (activity == null || activity.isFinishing())
            return;
        if (hBDlg != null && hBDlg.isShowing()) {
            return;
        }
        final View smallView = activity.findViewById(R.id.smallView);
        hBDlg = DialogUtil.getHBDlg(activity);
        if (hBDlg != null)
            hBDlg.show();
        hBDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                smallView.setVisibility(View.VISIBLE);
                setShowSmallDlg(activity, true);
            }
        });
    }


    static Dialog hBRegDlg = null;

    /**
     * 下单红包
     *
     * @param activity
     */
    static void showDlgHBRegOk(final Activity activity) {
        if (activity == null || activity.isFinishing())
            return;
        if (hBRegDlg != null && hBRegDlg.isShowing()) {
            return;
        }
        final View smallView = activity.findViewById(R.id.smallView);

        hBRegDlg = DialogUtil.getHBDlgRegOK(activity);
        if (hBRegDlg != null) {
            hBRegDlg.show();
            hBRegDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    //小红包不用显示了
                    setShowSmallDlg(activity, false);
                    smallView.setVisibility(View.GONE);

                    //不显示注册回来的下单
                    UNavConfig.setShowHBRegOkDlg(activity, false);

                }
            });
        }

    }

    /**
     * 新手第一次红包dlg
     *
     * @param context
     * @return
     */
    public static boolean isShowHBDlg(Context context) {
        return context.getSharedPreferences(SHARE_PRE, 0).getBoolean("isShowHBDlg", true);
    }

    public static void setShowHBDlg(Context context, boolean isShow) {
        if (context == null)
            return;
        context.getSharedPreferences(SHARE_PRE, 0).edit().putBoolean("isShowHBDlg", isShow).commit();
    }

    /**
     * 是否显示小红包
     *
     * @param context
     * @return
     */
    public static boolean isShowSmallDlg(Context context) {
        return context.getSharedPreferences(SHARE_PRE, 0).getBoolean("isShowSmallDlg", false);
    }

    public static void setShowSmallDlg(Context context, boolean isShow) {
        if (context == null)
            return;
        context.getSharedPreferences(SHARE_PRE, 0).edit().putBoolean("isShowSmallDlg", isShow).commit();
    }


    /**
     * 新手引导注册回来之后显示 下单弹窗
     *
     * @param context
     * @return
     */
    public static boolean isShowHBRegOkDlg(Context context) {
        return context.getSharedPreferences(SHARE_PRE, 0).getBoolean("isShowHBRegOkDlg", false);
    }

    public static void setShowHBRegOkDlg(Context context, boolean isShow) {
        if (context == null)
            return;
        context.getSharedPreferences(SHARE_PRE, 0).edit().putBoolean("isShowHBRegOkDlg", isShow).commit();
    }


    /**
     * 判断是否为新手
     * 当前设备没有登录，并且也没有登录过得记录
     *
     * @param context
     * @return
     */
    public static boolean isNewUser(Context context) {
        if (!new UserInfoDao(context).isLogin()
                && !isLogined(context)
                && !isReged(context))
            return true;
        return false;
    }


    /**
     * 记录当前设备是否登录过
     *
     * @param context
     * @return
     */
    public static boolean isLogined(Context context) {
        return context.getSharedPreferences(SHARE_PRE, 0).getBoolean("isLogined", false);
    }

    public static void setLogined(Context context, boolean isShow) {
        if (context == null)
            return;
        context.getSharedPreferences(SHARE_PRE, 0).edit().putBoolean("isLogined", isShow).commit();
    }

    public static boolean isReged(Context context) {
        return context.getSharedPreferences(SHARE_PRE, 0).getBoolean("isReged", false);
    }

    public static void setReged(Context context, boolean isShow) {
        if (context == null)
            return;
        context.getSharedPreferences(SHARE_PRE, 0).edit().putBoolean("isReged", isShow).commit();
    }


    /**
     * 跳过新手引导的步骤
     *
     * @param context
     * @return
     */
    public static boolean isSkipStep(Context context) {
        return context.getSharedPreferences(SHARE_PRE, 0).getBoolean("isSkipStep", false);
    }

    public static void setSkipStep(Context context, boolean isShow) {
        if (context == null)
            return;
        context.getSharedPreferences(SHARE_PRE, 0).edit().putBoolean("isSkipStep", isShow).commit();
    }

    /**
     * 新手引导第一步
     *
     * @param context
     * @return
     */
    public static boolean isShowStep01(Context context) {
        return context.getSharedPreferences(SHARE_PRE, 0).getBoolean("isShowStep01", true);
    }

    public static void setShowStep01(Context context, boolean isShow) {
        if (context == null)
            return;
        context.getSharedPreferences(SHARE_PRE, 0).edit().putBoolean("isShowStep01", isShow).commit();
    }

    public static boolean isShowStep02(Context context) {
        return context.getSharedPreferences(SHARE_PRE, 0).getBoolean("isShowStep02", false);
    }

    public static void setShowStep02(Context context, boolean isShow) {
        if (context == null)
            return;
        context.getSharedPreferences(SHARE_PRE, 0).edit().putBoolean("isShowStep02", isShow).commit();
    }

    public static boolean isShowStep03(Context context) {
        return context.getSharedPreferences(SHARE_PRE, 0).getBoolean("isShowStep03", false);
    }

    public static void setShowStep03(Context context, boolean isShow) {
        if (context == null)
            return;
        context.getSharedPreferences(SHARE_PRE, 0).edit().putBoolean("isShowStep03", isShow).commit();
    }

    public static boolean isShowStep04(Context context) {
        return context.getSharedPreferences(SHARE_PRE, 0).getBoolean("isShowStep04", false);
    }

    public static void setShowStep04(Context context, boolean isShow) {
        if (context == null)
            return;
        context.getSharedPreferences(SHARE_PRE, 0).edit().putBoolean("isShowStep04", isShow).commit();
    }

    /**
     * 显示哈贵所设置交易密码
     *
     * @param context
     * @return
     */
    public static boolean isShowHGTrade(Context context) {
        return context.getSharedPreferences(SHARE_PRE, 0).getBoolean("isShowHGTrade", true);
    }

    public static void setShowHGTrade(Context context, boolean isShow) {
        if (context == null)
            return;
        context.getSharedPreferences(SHARE_PRE, 0).edit().putBoolean("isShowHGTrade", isShow).commit();
    }

    /**
     * 用户引导的check接口
     *
     * @param activity
     */
    public static void check(final BaseActivity activity) {
        if (!new UserInfoDao(activity).isLogin())
            return;
        new AsyncTask<String, Void, CommonResponse<RegData>>() {
            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                activity.showNetLoadingProgressDialog(null);
            }

            @Override
            protected CommonResponse<RegData> doInBackground(String... params) {
                // TODO Auto-generated method stub
                try {
                    Map<String, String> paraMap = ApiConfig.getCommonMap(activity);
                    paraMap.put(UserInfo.UID, new UserInfoDao(activity).queryUserInfo().getUserId());
                    paraMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(activity, paraMap));
                    String url = AndroidAPIConfig.getAPI(activity, AndroidAPIConfig.KEY_URL_TRADE_USER_CHECK);
                    String res = HttpClientHelper.getStringFromPost(activity, url, paraMap);
                    if (res != null)
                        return CommonResponse.fromJson(res, RegData.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<RegData> result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                activity.hideNetLoadingProgressDialog();
                try {
                    if (result != null) {
                        if (result.isSuccess()) {
                            if (result.getData() == null)
                                return;
                            RegData regData = result.getData();
                            if (regData == null)
                                return;
                            if (regData.getType() == RegData.TYPE_REG) {
                                activity.startActivity(new Intent(activity, TradeRegAct.class));
                            } else {
                                activity.startActivity(new Intent(activity, TradeLoginAct.class));
                            }
                        } else {
                            activity.showCusToast(ConvertUtil.NVL(result.getErrorInfo(), "网络异常，请重试！"));
                        }

                    } else {
                        activity.showCusToast("网络异常，请重试！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    /**
     * 用户第一次进app展示积分商城
     *
     * @param context
     * @return
     */
    public static boolean isShowIntegralDlg(Context context) {
        return context.getSharedPreferences(SHARE_PRE, 0).getBoolean("isShowIntegralDlg", false);
    }

    public static void setShowIntegralDlg(Context context, boolean isShow) {
        if (context == null)
            return;
        context.getSharedPreferences(SHARE_PRE, 0).edit().putBoolean("isShowIntegralDlg", isShow).commit();
    }

    /**
     * 设置是否展示任务中心
     *
     * @param context
     * @return
     */
    public static boolean isShowMissionCenterDlg(Context context) {
        return context.getSharedPreferences(SHARE_PRE, 0).getBoolean("isShowMissionCenterDlg", false);
    }

    public static void setShowMissionCenterDlg(Context context, boolean isShow) {
        if (context == null)
            return;
        context.getSharedPreferences(SHARE_PRE, 0).edit().putBoolean("isShowMissionCenterDlg", isShow).commit();
    }

    /**
     * 首页活动入口
     * 已经登录了  并且当前是在首页  就显示左下角比赛按钮
     *
     * @param activity
     */
    public static void initBtnHomeAct(final Activity activity, String tag) {
        final ImageView btnHomeCompetity = (ImageView) activity.findViewById(R.id.btn_homecompetity);
        if (btnHomeCompetity == null)
            return;
        btnHomeCompetity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppStartUpConfig.getInstance(activity).getStartupConfigObj() == null) {
                    return;
                }
                HomeActData homeActData = AppStartUpConfig.getInstance(activity).getStartupConfigObj().getIndexActivity();
                if(homeActData==null){
                    return;
                }
                String url = homeActData.getUrl();
                if (StringUtil.isEmpty(url))
                    return;
                Map<String, String> paramMap = new HashMap<String, String>();
                UserInfoDao dao = new UserInfoDao(activity);
                if (!dao.isLogin())
                    return;
                paramMap.put("userId", dao.queryUserInfo().getUserId());
                paramMap = ApiConfig.getParamMap(activity, paramMap);
                url = NetWorkUtils.setParam4get(url, paramMap);
                WebActivity.start(activity, homeActData.getBntName(), url);
            }
        });

        if (AppStartUpConfig.getInstance(activity).getStartupConfigObj() == null) {
            btnHomeCompetity.setVisibility(View.GONE);
            return;
        }
        UserInfoDao dao = new UserInfoDao(activity);
        HomeActData homeActData = AppStartUpConfig.getInstance(activity).getStartupConfigObj().getIndexActivity();
        //已经登录了
        if (dao.isLogin() && homeActData != null) {
            if (MainActivity.HOME.equals(tag)) {// 只在首页显示
                if(homeActData.isShow()){// 当前状态是显示
                    if (MainActivity.HOME.equals(tag)) {
                        ImageLoader.getInstance().displayImage(homeActData.getImgUrl(), btnHomeCompetity);
                        btnHomeCompetity.setVisibility(View.VISIBLE);
                    }
                }else{
                    btnHomeCompetity.setVisibility(View.GONE);
                }
            } else {
                btnHomeCompetity.setVisibility(View.GONE);

            }
        } else {
            btnHomeCompetity.setVisibility(View.GONE);
        }
    }
}
