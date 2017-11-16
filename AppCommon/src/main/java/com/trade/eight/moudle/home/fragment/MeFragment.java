package com.trade.eight.moudle.home.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.config.OnLineHelper;
import com.trade.eight.config.QiHuoExplainWordConfig;
import com.trade.eight.config.UmengMobClickConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.me.TradeLoginSuccessEvent;
import com.trade.eight.moudle.me.activity.AboutUsActivity;
import com.trade.eight.moudle.me.activity.CashInAndOutAct;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.me.activity.QiHuoMyAccountAct;
import com.trade.eight.moudle.me.activity.QiHuoMyAccountManagerAct;
import com.trade.eight.moudle.me.activity.UserSettingAct;
import com.trade.eight.moudle.me.activity.WeipanMsgListActivity;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.moudle.trade.UpdateTradeUserInfoEvent;
import com.trade.eight.moudle.trade.activity.DialogUtilForTrade;
import com.trade.eight.moudle.trade.activity.TradeOrderHistoryAct;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
import com.trade.eight.moudle.upgradeversion.CheckUpgradeInfoTask;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.OpenActivityUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.view.CircleImageView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;


public class MeFragment extends BaseFragment implements OnClickListener {

    String TAG = "MeFragment";
    View line_goloin;
    TextView text_my_setting;
    TextView text_username;
    CircleImageView img_useravatar;
    View line_unlogin_qihuo;
    View line_belogin_qihuo;
    View line_myaccount;
    TextView text_myaccount_qihuo;
    TextView text_myaccount_loginout;
    Button btn_my_help;
    TextView text_moneyzy_rate;
    ImageView img_tradeinfo_dangerous;
    TextView text_money_dqqy;
    TextView text_moneyzy_kyzj;
    TextView text_moneyzy_dyyk;
    Button btn_my_cashin;
    Button btn_my_cashout;
    TextView text_my_orders;
    TextView text_my_hisorders;
    TextView text_my_qihuo_am;
    TextView text_my_cashhistory;
    LinearLayout ll_my_newuser;
    LinearLayout ll_my_kefu;
    LinearLayout ll_kefu_phone;
    LinearLayout ll_my_messagecente;
    LinearLayout ll_my_aboutus;
    LinearLayout ll_checkupdate;
    TextView tv_update_status;

    TradeLoginDlg tradeLoginDlg;
    TradeInfoData tradeInfoData;

    boolean isVisible = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.v(TAG, "onSaveInstanceState != null");
            if (savedInstanceState.getBoolean("isHidden")) {
                Log.v(TAG, "onSaveInstanceState != null && isHidden == true");
                getFragmentManager().beginTransaction().hide(this).commit();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "onSaveInstanceState");
        outState.putBoolean("isHidden", isHidden());
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools, null);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initView(view);
        initListener();
        checkUser();
        return view;
    }

    public void initView(View view) {
        Log.e(TAG, "initView=========");
        line_goloin = view.findViewById(R.id.line_goloin);
        text_my_setting = (TextView) view.findViewById(R.id.text_my_setting);
        text_username = (TextView) view.findViewById(R.id.text_username);
        img_useravatar = (CircleImageView) view.findViewById(R.id.img_useravatar);
        line_unlogin_qihuo = (LinearLayout) view.findViewById(R.id.line_unlogin_qihuo);
        line_belogin_qihuo = view.findViewById(R.id.line_belogin_qihuo);
        line_myaccount = view.findViewById(R.id.line_myaccount);
        text_myaccount_qihuo = (TextView) view.findViewById(R.id.text_myaccount_qihuo);
        text_myaccount_loginout = (TextView) view.findViewById(R.id.text_myaccount_loginout);
        btn_my_help = (Button) view.findViewById(R.id.btn_my_help);
        text_moneyzy_rate = (TextView) view.findViewById(R.id.text_moneyzy_rate);
        img_tradeinfo_dangerous = (ImageView) view.findViewById(R.id.img_tradeinfo_dangerous);
        text_money_dqqy = (TextView) view.findViewById(R.id.text_money_dqqy);
        text_moneyzy_kyzj = (TextView) view.findViewById(R.id.text_moneyzy_kyzj);
        text_moneyzy_dyyk = (TextView) view.findViewById(R.id.text_moneyzy_dyyk);
        btn_my_cashin = (Button) view.findViewById(R.id.btn_my_cashin);
        btn_my_cashout = (Button) view.findViewById(R.id.btn_my_cashout);
        text_my_orders = (TextView) view.findViewById(R.id.text_my_orders);
        text_my_hisorders = (TextView) view.findViewById(R.id.text_my_hisorders);
        text_my_qihuo_am = (TextView) view.findViewById(R.id.text_my_qihuo_am);
        text_my_cashhistory = (TextView) view.findViewById(R.id.text_my_cashhistory);
        ll_my_newuser = (LinearLayout) view.findViewById(R.id.ll_my_newuser);
        ll_my_kefu = (LinearLayout) view.findViewById(R.id.ll_my_kefu);
        ll_kefu_phone = (LinearLayout) view.findViewById(R.id.ll_kefu_phone);
        ll_my_messagecente = (LinearLayout) view.findViewById(R.id.ll_my_messagecente);
        ll_my_aboutus = (LinearLayout) view.findViewById(R.id.ll_my_aboutus);
        ll_checkupdate = (LinearLayout) view.findViewById(R.id.ll_checkupdate);
        tv_update_status = (TextView) view.findViewById(R.id.tv_update_status);

        tv_update_status.setText("V" + AppSetting.getInstance(getActivity()).getAppVersionName());

        text_my_setting.setOnClickListener(this);
        line_goloin.setOnClickListener(this);
        line_unlogin_qihuo.setOnClickListener(this);
        line_myaccount.setOnClickListener(this);
        text_myaccount_loginout.setOnClickListener(this);
        img_tradeinfo_dangerous.setOnClickListener(this);
        btn_my_cashin.setOnClickListener(this);
        btn_my_cashout.setOnClickListener(this);
        text_my_orders.setOnClickListener(this);
        text_my_hisorders.setOnClickListener(this);
        text_my_qihuo_am.setOnClickListener(this);
        text_my_cashhistory.setOnClickListener(this);
        ll_my_newuser.setOnClickListener(this);
        ll_my_kefu.setOnClickListener(this);
        ll_kefu_phone.setOnClickListener(this);
        ll_my_messagecente.setOnClickListener(this);
        ll_my_aboutus.setOnClickListener(this);
        ll_checkupdate.setOnClickListener(this);
        btn_my_help.setOnClickListener(this);

    }

    /**
     * 登陆成功
     *
     * @param tradeLoginSuccessEvent
     */
    public void onEventMainThread(TradeLoginSuccessEvent tradeLoginSuccessEvent) {
        line_unlogin_qihuo.setVisibility(View.GONE);
        line_belogin_qihuo.setVisibility(View.VISIBLE);

        Log.e(TAG, "TradeLoginSuccessEvent");

        TradeInfoData tradeInfoData = TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), this).getTradeInfoData();
        if (tradeInfoData != null) {
            displayView(tradeInfoData);

        } else {
            TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), this).loadTradeOrderAndUserInfoData(new TradeUserInfoData4Situation.GetDataCallback() {
                @Override
                public void onFailure(String resultCode, String resultMsg) {
                    //token过期 避免重复弹框
//                    if (ApiConfig.isNeedLogin(resultCode)) {
//                        //重新登录
////                        showTokenDialog();
//                        if (tradeLoginDlg == null) {
//                            tradeLoginDlg = new TradeLoginDlg((BaseActivity) getActivity());
//                        }
//                        if (!tradeLoginDlg.isShowingDialog()) {
//                            tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
//                        }
//                        return;
//                    }
                    showCusToast(resultMsg);
                }

                @Override
                public void onResponse(TradeInfoData tradeInfoData) {
                    displayView(tradeInfoData);
                }
            }, false);
        }
    }

    /**
     * 数据刷新成功
     *
     * @param updateTradeUserInfoEvent
     */
    public void onEventMainThread(UpdateTradeUserInfoEvent updateTradeUserInfoEvent) {
        TradeInfoData tradeInfoData = updateTradeUserInfoEvent.tradeInfoData;
        displayView(tradeInfoData);
    }

    public void displayView(TradeInfoData tradeInfoData) {
        img_tradeinfo_dangerous.setVisibility(View.GONE);
        if (tradeInfoData == null) {
            text_moneyzy_rate.setText("--");
            text_money_dqqy.setText("--");
            text_moneyzy_kyzj.setText("--");
            text_moneyzy_dyyk.setText("--");
            return;
        }
        this.tradeInfoData = tradeInfoData;
        text_moneyzy_rate.setText(tradeInfoData.getCapitalProportion());
        text_money_dqqy.setText(StringUtil.forNumber(new BigDecimal(tradeInfoData.getRightsInterests()).doubleValue()));
        text_moneyzy_kyzj.setText(StringUtil.forNumber(new BigDecimal(tradeInfoData.getAvailable()).doubleValue()));
        if (new BigDecimal(ConvertUtil.NVL(tradeInfoData.getPositionProfit(), "0")).doubleValue() > 0) {
            text_moneyzy_dyyk.setTextColor(getActivity().getResources().getColor(R.color.c_EA4A5E));
            text_moneyzy_dyyk.setText("+" + StringUtil.forNumber(new BigDecimal(ConvertUtil.NVL(tradeInfoData.getPositionProfit(), "0")).doubleValue()));
        } else if (new BigDecimal(ConvertUtil.NVL(tradeInfoData.getPositionProfit(), "0")).doubleValue() < 0) {
            text_moneyzy_dyyk.setTextColor(getActivity().getResources().getColor(R.color.c_06A969));
            text_moneyzy_dyyk.setText(StringUtil.forNumber(new BigDecimal(ConvertUtil.NVL(tradeInfoData.getPositionProfit(), "0")).doubleValue()));
        } else {
            text_moneyzy_dyyk.setTextColor(getActivity().getResources().getColor(R.color.c_464646));
            text_moneyzy_dyyk.setText(StringUtil.forNumber(new BigDecimal(ConvertUtil.NVL(tradeInfoData.getPositionProfit(), "0")).doubleValue()));
        }

        if (tradeInfoData.getTradeInfoSafeOrDangerous() != 0) {
            img_tradeinfo_dangerous.setVisibility(View.VISIBLE);
            switch (tradeInfoData.getTradeInfoSafeOrDangerous()) {
                case TradeInfoData.TRADEINFO_DANGEROUS:
                    img_tradeinfo_dangerous.setClickable(false);
                    img_tradeinfo_dangerous.setImageResource(R.drawable.img_tradenotify_2);
                    break;
                case TradeInfoData.TRADEINFO_DANGEROUS_MOST_1:
                    img_tradeinfo_dangerous.setClickable(true);
                    img_tradeinfo_dangerous.setImageResource(R.drawable.img_tradenotify_1);
                    break;
                case TradeInfoData.TRADEINFO_DANGEROUS_MOST_2:
                    img_tradeinfo_dangerous.setClickable(true);
                    img_tradeinfo_dangerous.setImageResource(R.drawable.img_tradenotify_1);
                    break;
                case TradeInfoData.TRADEINFO_DANGEROUS_MOST_3:
                    img_tradeinfo_dangerous.setClickable(true);
                    img_tradeinfo_dangerous.setImageResource(R.drawable.img_tradenotify_1);
                    break;
            }
        }
    }

    public void initListener() {

    }


    @Override
    public void onFragmentVisible(boolean isVisible) {
        super.onFragmentVisible(isVisible);
        this.isVisible = isVisible;
        if (text_username == null) {
            return;
        }
        if (isVisible) {
            checkUser();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void checkUser() {

        UserInfoDao userInfoDao = new UserInfoDao(getActivity());
        if (userInfoDao.isLogin()) {
            UserInfo userInfo = userInfoDao.queryUserInfo();
            String name = ConvertUtil.NVL(userInfo.getNickName(), ConvertUtil.NVL(userInfo.getUserName(), ConvertUtil.NVL(userInfo.getMobileNum(), "")));
            text_username.setText(name);
            //头像
            ImageLoader.getInstance().displayImage(userInfo.getAvatar(), img_useravatar, AppImageLoaderConfig.getCommonDisplayImageOptions(getActivity(), R.drawable.img_me_headdefault));
            if (TextUtils.isEmpty(AppSetting.getInstance(getActivity()).getWPToken(getActivity()))) {
                line_unlogin_qihuo.setVisibility(View.VISIBLE);
                line_belogin_qihuo.setVisibility(View.GONE);
            } else {
                line_unlogin_qihuo.setVisibility(View.GONE);
                line_belogin_qihuo.setVisibility(View.VISIBLE);
                line_unlogin_qihuo.setVisibility(View.GONE);
                line_belogin_qihuo.setVisibility(View.VISIBLE);
                //本地先验证token 过期了
                if (!TradeHelp.isTokenEnable((BaseActivity) getActivity())) {
                    showTradeLoginDlg();
                    return;
                }

                TradeInfoData tradeInfoData = TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), this).getTradeInfoData();
                if (tradeInfoData != null) {
                    displayView(tradeInfoData);
                } else {
                    TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), this).loadTradeOrderAndUserInfoData(new TradeUserInfoData4Situation.GetDataCallback() {
                        @Override
                        public void onFailure(String resultCode, String resultMsg) {
                            if (ApiConfig.isNeedLogin(resultCode)) {
                                line_unlogin_qihuo.setVisibility(View.VISIBLE);
                                line_belogin_qihuo.setVisibility(View.GONE);
                                return;
                            }
                            showCusToast(resultMsg);
                            displayView(null);
                        }

                        @Override
                        public void onResponse(TradeInfoData tradeInfoData) {
                            displayView(tradeInfoData);
                        }
                    }, false);
                }
            }
        } else {
            text_username.setText(R.string.lable_my_regandlogin);
            img_useravatar.setImageResource(R.drawable.img_me_headdefault);
            line_unlogin_qihuo.setVisibility(View.VISIBLE);
            line_belogin_qihuo.setVisibility(View.GONE);
        }
    }


    CheckUpgradeInfoTask upgradeInfoTask;

    void upgradeApp() {
        if (upgradeInfoTask != null && upgradeInfoTask.getStatus() == AsyncTask.Status.RUNNING)
            return;
        if (upgradeInfoTask != null && upgradeInfoTask.isDlgShowing())
            return;
        upgradeInfoTask = new CheckUpgradeInfoTask((BaseActivity) getActivity(), CheckUpgradeInfoTask.FROM_ME);
        upgradeInfoTask.execute();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ll_checkupdate) {
            //检测更新
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "检查更新");
            upgradeApp();
        } else if (i == R.id.text_my_setting || i == R.id.line_goloin) {// 设置和未登录
            if (!new UserInfoDao(getActivity()).isLogin()) {
                MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "登陆指盈");
                Map<String, String> map = new HashMap<String, String>();
                map.put(BaseInterface.TAB_MAIN_PARAME, MainActivity.ME);
                Intent intent = OpenActivityUtil.initAction(getActivity(), LoginActivity.class,
                        OpenActivityUtil.ACT_MAIN, map);
                if (intent != null)
                    startActivity(intent);
            } else {
                MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "账户管理");
                Intent intent = new Intent(getActivity(), UserSettingAct.class);
                getActivity().startActivity(intent);
            }
        } else if (i == R.id.line_unlogin_qihuo) {//期货未登录
            if (tradeLoginDlg == null) {
                tradeLoginDlg = new TradeLoginDlg((BaseActivity) getActivity());
            }
            if (!tradeLoginDlg.isShowingDialog()) {
                tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
            }
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "登录期货账户");
        } else if (i == R.id.line_myaccount) {// 我的账户
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "我的期货账户");
            //本地先验证token 过期了
            if (!TradeHelp.isTokenEnable((BaseActivity) getActivity())) {
                showTradeLoginDlg();
                return;
            }
            QiHuoMyAccountAct.statartAct(getActivity(), tradeInfoData);

        } else if (i == R.id.btn_my_cashin) {
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "充值");
            //本地先验证token 过期了
            if (!TradeHelp.isTokenEnable((BaseActivity) getActivity())) {
                showTradeLoginDlg();
                return;
            }
            CashInAndOutAct.startAct(getActivity());
        } else if (i == R.id.btn_my_cashout) {
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "提现");

            //本地先验证token 过期了
            if (!TradeHelp.isTokenEnable((BaseActivity) getActivity())) {
                showTradeLoginDlg();
                return;
            }
            CashInAndOutAct.startAct(getActivity(), 1);
        } else if (i == R.id.text_my_orders) {
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "我的持仓");

            //本地先验证token 过期了
            if (!TradeHelp.isTokenEnable((BaseActivity) getActivity())) {
                showTradeLoginDlg();
                return;
            }
            getActivity().findViewById(R.id.tab_weipan).performClick();
            //跳到交易大厅去
            ViewPager tradeViewPager = (ViewPager) getActivity().findViewById(R.id.tradeViewPager);
            if (tradeViewPager != null) {
                tradeViewPager.setCurrentItem(1);
            }
        } else if (i == R.id.text_my_hisorders) {
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "交易记录");

            //本地先验证token 过期了
            if (!TradeHelp.isTokenEnable((BaseActivity) getActivity())) {
                showTradeLoginDlg();
                return;
            }
            TradeOrderHistoryAct.startAct(getActivity());
        } else if (i == R.id.text_my_qihuo_am) {
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "期货账户管理");

            //本地先验证token 过期了
            if (!TradeHelp.isTokenEnable((BaseActivity) getActivity())) {
                showTradeLoginDlg();
                return;
            }
            QiHuoMyAccountManagerAct.statartAct(getActivity(), tradeInfoData);
        } else if (i == R.id.text_my_cashhistory) {
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "出入金明细");

            //本地先验证token 过期了
            if (!TradeHelp.isTokenEnable((BaseActivity) getActivity())) {
                showTradeLoginDlg();
                return;
            }
            CashInAndOutAct.startAct(getActivity(), 2);

        } else if (i == R.id.ll_my_newuser) {// 新手入门
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "新手入门");

            String string = "新手入门";
            WebActivity.start(getActivity(), string, AndroidAPIConfig.URL_HOME_XSXT, true);

        } else if (i == R.id.ll_my_kefu) {//客服
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "在线客服");
            OnLineHelper.getInstance().startP2p((BaseActivity) getActivity());
        } else if (i == R.id.ll_kefu_phone) {
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "客服电话");
            OpenActivityUtil.callPhone(getActivity(), getActivity().getResources().getString(R.string.lable_kefu_phonenum));

        } else if (i == R.id.ll_my_messagecente) {//通知中心
            WeipanMsgListActivity.startAct(getActivity());
        } else if (i == R.id.ll_my_aboutus) {//关于我们
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "关于我们");
            AboutUsActivity.startIndexAct(getActivity());
        } else if (i == R.id.ll_checkupdate) {//版本检测
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "检查更新");
            upgradeApp();
        } else if (i == R.id.text_myaccount_loginout) {
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "安全退出");
            loginOutQiHuo();
        } else if (i == R.id.img_tradeinfo_dangerous) {
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "资金危险");

            DialogUtil.showTitleAndContentDialog(getActivity(),
                    getActivity().getResources().getString(R.string.tradeinfonotify_4),
                    getActivity().getResources().getString(R.string.tradeinfonotify_5, this.tradeInfoData.getCapitalProportion()),
                    getActivity().getResources().getString(R.string.unifypwd_posbtn_oldaccount),
                    getActivity().getResources().getString(R.string.tradeinfonotify_chargenow),
                    true,
                    new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message message) {
                            return false;
                        }
                    },
                    new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message message) {
                            CashInAndOutAct.startAct(getActivity());

                            return false;
                        }
                    });
        } else if (i == R.id.btn_my_help) {
            MyAppMobclickAgent.onEvent(getActivity(), UmengMobClickConfig.PAGE_ME, "资金面板气泡");

//            QiHuoExplainWordActivity.startAct(getActivity(),
            DialogUtilForTrade.showQiHuoExplainDlg((BaseActivity) getActivity(),

                    new String[]{QiHuoExplainWordConfig.FXL,
                            QiHuoExplainWordConfig.DQQY,
                            QiHuoExplainWordConfig.KYZJ,
                            QiHuoExplainWordConfig.ZCCYK});
        }
    }

    /**
     * 展示登录对话框
     */
    private void showTradeLoginDlg() {
        if (!isVisible)
            return;
        if (!isAdded())
            return;
        if (isDetached())
            return;
        if (tradeLoginDlg == null) {
            tradeLoginDlg = new TradeLoginDlg((BaseActivity) getActivity());
        }
        if (!tradeLoginDlg.isShowingDialog()) {
            tradeLoginDlg.showDialog(R.style.dialog_trade_ani);
        }
    }

    /**
     * 退出期货登录
     */
    private void loginOutQiHuo() {

        HttpClientHelper.doPostOption((BaseActivity) getActivity(),
                AndroidAPIConfig.URL_TRADE_USER_LOGIN_OUT,
                null,
                null,
                new NetCallback((BaseActivity) getActivity()) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {

                    }

                    @Override
                    public void onResponse(String response) {

                    }
                },
                false);
        AppSetting appSetting = AppSetting.getInstance(getActivity());
        //清理的token
        appSetting.clearWPToken();
        // 未登录的view展示
        line_unlogin_qihuo.setVisibility(View.VISIBLE);
        // 已登录的view隐藏
        line_belogin_qihuo.setVisibility(View.GONE);

        TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), MeFragment.this).setTradeInfoData(null);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
//        TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), this).stopRefresh();
    }

    @Override
    public void onPause() {
        super.onPause();
//        TradeUserInfoData4Situation.getInstance((BaseActivity) getActivity(), this).stopRefresh();
    }
}
