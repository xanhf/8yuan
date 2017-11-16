package com.trade.eight.moudle.trade.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.base.BaseFragment;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.RegData;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.jdpay.JDPayObj;
import com.trade.eight.entity.jdpay.RechargeType;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trade.TradeRechargeTypeAndMoneyData;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.trade.activity.CashInAct;
import com.trade.eight.moudle.trade.activity.RedirectH5Act;
import com.trade.eight.moudle.trade.activity.WeiXinRechargeAct;
import com.trade.eight.moudle.trade.adapter.RechargeTypeAdapter;
import com.trade.eight.moudle.trade.alipay.AliPayUtil;
import com.trade.eight.moudle.trade.jdpay.JDPayBindCardAct;
import com.trade.eight.moudle.trade.jdpay.JDPayInputSMSAct;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.JSONObjectUtil;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.pay.WXPayUtil;
import com.trade.eight.tools.trade.TradeConfig;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：Created by changhaiyang
 * 时间：on 16/9/22.
 * 充值选择充值平台
 */

public class CashInChoosePlatformFrag extends BaseFragment {
    String TAG = CashInChoosePlatformFrag.class.getSimpleName();
    CashInAct cashInAct = null;
    TextView tv_cashin_amount;
    //使用listview填充
    ListView listView;
    Button btn_submit;
    String money = "";
    //只有一个size的时候，是否自动提交
    public static final boolean AUTO_COMMIT_ONE_SIZE = true;
    RechargeTypeAdapter adapter;
    //ActivityForResult 微信支付
    private static int CODE_REQUEST = 100;
    //  支付类型数据
    TradeRechargeTypeAndMoneyData tradeRechargeTypeAndMoneyData;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        cashInAct = (CashInAct) getActivity();
        Bundle bundle = getArguments();
        money = bundle.getString("money");
        tradeRechargeTypeAndMoneyData = (TradeRechargeTypeAndMoneyData) bundle.getSerializable("rechargeType");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_cashin_chooseplatform, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tv_cashin_amount = (TextView) view.findViewById(R.id.tv_cashin_amount);
        listView = (ListView) view.findViewById(R.id.listView);
        btn_submit = (Button) view.findViewById(R.id.btn_submit);
        initListener();
        tv_cashin_amount.setText(money + "元");

        List<RechargeType> rechargeTypeList = tradeRechargeTypeAndMoneyData.getRechargeType();
        if (rechargeTypeList != null) {
            //test 微信app pay
//            RechargeType r = new RechargeType();
//            r.setPayType(RechargeType.TYPE_WX_DIAN_XIN_APP);
//            r.setPayName("微信支付");
//            rechargeTypeList.add(r);

            adapter = new RechargeTypeAdapter(getActivity(), 0, rechargeTypeList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        Log.v(TAG, "position="+position);
                        adapter.setSelectPos(position);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            //只有一个数据的时候是否自动提交
            if (rechargeTypeList.size() == 1) {
                RechargeType rechargeType = rechargeTypeList.get(0);
                //自动提交
                if (AUTO_COMMIT_ONE_SIZE)
                    handleCommit(rechargeType);
            }
        }
    }


    private void initListener() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter != null)
                    handleCommit(adapter.getSelectItem());
            }
        });
    }

    /**
     * 根据选中的支付类型 提交
     * @param type 选中的支付类型
     */
    public void handleCommit (RechargeType type) {
        if (type == null)
            return;
        int itemType = type.getPayType();
        if (itemType == RechargeType.TYPE_YINLIAN
                || itemType == RechargeType.TYPE_YINLIAN_BEST_PAY) {
            //银联
            submit();
        } else if (itemType == RechargeType.TYPE_WX_SCHEME
                || itemType == RechargeType.TYPE_WX_DIAN_XIN_WAP
                || itemType == RechargeType.TYPE_WX_DIAN_XIN_APP) {
            //微信
            submit4WX();
        } else if (itemType == RechargeType.TYPE_ZHIFUBAO) {
            //支付宝
            submit4ZFB();
        } else if (itemType == RechargeType.TYPE_JD_KJZF) {
            //京东快捷支付
            submit4JD();
        } else if (itemType == RechargeType.TYPE_WX_IWXAPI) {
            //京东快捷支付
            submit4IWXAPI();
        } else if (itemType == RechargeType.TYPE_ZHIFUBAO_SCAN) {
            //支付宝扫码支付 webview跳转
            AliPayUtil.alipayScan(cashInAct, AndroidAPIConfig.URL_TRADE_ZHIFUBAO_SCAN_JN, money, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what == 1) {
                        showTokenDlg();
                    }
                    return false;
                }
            });
        }
    }

    Dialog tokenDlg = null;

    boolean checkTokenDlg() {
        //网页充值的时候判断是否token可用
        if (!TradeHelp.isTokenEnable(cashInAct)) {
            if (tokenDlg != null && tokenDlg.isShowing())
                return false;
            showTokenDlg();
            return false;
        }
        return true;
    }

    void showTokenDlg() {
        //网页充值的时候判断是否token可用
        if (tokenDlg != null && tokenDlg.isShowing())
            return;
        DialogUtil.showTokenDialog(cashInAct, TradeConfig.getCurrentTradeCode(cashInAct), new DialogUtil.AuthTokenDlgShow() {
            @Override
            public void onDlgShow(Dialog dlg) {
                tokenDlg = dlg;
            }
        }, new DialogUtil.AuthTokenCallBack() {
            @Override
            public void onPostClick(Object obj) {

            }

            @Override
            public void onNegClick() {

            }
        });
    }

    /**
     * 提交 获取h5
     * <p/>
     * 与注册、登录服务一样，使用webview 加载，url，
     * 监测到webview加载url与redirectUrl一样，
     * 就直接跳转个人页面。
     */

    void submit() {
        if (!checkTokenDlg())
            return;
        new AsyncTask<Void, Void, CommonResponse<RegData>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                cashInAct.showNetLoadingProgressDialog(null);
            }

            @Override
            protected CommonResponse<RegData> doInBackground(Void... params) {
                try {
                    UserInfoDao dao = new UserInfoDao(cashInAct);
                    if (!dao.isLogin())
                        return null;
                    String url = AndroidAPIConfig.getAPI(cashInAct, AndroidAPIConfig.KEY_URL_TRADE_CASH_IN);
                    Map<String, String> map = ApiConfig.getCommonMap(cashInAct);

                    map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
                    map.put("amount", money + "");
                    map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(cashInAct, map));

                    String res = TradeHelp.post(cashInAct, url, map);

                    return CommonResponse.fromJson(res, RegData.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<RegData> response) {
                super.onPostExecute(response);
                if (!isAdded())
                    return;
                if (isDetached())
                    return;
                cashInAct.hideNetLoadingProgressDialog();
                if (response != null) {
                    if (response.isSuccess() && response.getData() != null) {
                        RedirectH5Act.startCashin(cashInAct, response.getData());
                    } else {
                        if (ApiConfig.isNeedLogin(response.getErrorCode())) {
                            //重新登录
                            showTokenDlg();
                            return;
                        }
                        DialogUtil.showMsgDialog(cashInAct, ConvertUtil.NVL(response.getErrorInfo(), "提交失败"), "确定");
                    }

                } else showCusToast("网络异常");
            }
        }.execute();
    }

    boolean isError = false;
    RegData data = null;

    /**
     * 微信支付
     */
    void submit4WX() {
        new AsyncTask<Void, Void, CommonResponse<RegData>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                cashInAct.showNetLoadingProgressDialog(null);
            }

            @Override
            protected CommonResponse<RegData> doInBackground(Void... params) {
                try {
                    UserInfoDao dao = new UserInfoDao(cashInAct);
                    if (!dao.isLogin())
                        return null;
                    String url = AndroidAPIConfig.getAPI(cashInAct, AndroidAPIConfig.KEY_URL_TRADE_CASH_IN_WX);
                    Map<String, String> map = ApiConfig.getCommonMap(cashInAct);

                    map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
                    map.put("amount", money + "");
                    map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(cashInAct, map));

                    String res = TradeHelp.post(cashInAct, url, map);

                    return CommonResponse.fromJson(res, RegData.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<RegData> response) {
                super.onPostExecute(response);
                if (!isAdded())
                    return;
                if (isDetached())
                    return;
                cashInAct.hideNetLoadingProgressDialog();
                if (response != null) {
                    data = response.getData();
                    if (response.isSuccess() && data != null) {

                        if (TradeConfig.isCurrentHG(cashInAct)) {
                            switch (data.getType()) {
                                case 1:
                                    //微信支付  现在支付
                                    try {
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(data.getScheme()));
                                        //Result回来作检测支付是否成功
                                        startActivityForResult(intent, CODE_REQUEST);
                                    } catch (Exception e) {
                                        isError = true;
                                        e.printStackTrace();
                                        DialogUtil.showMsgDialog(cashInAct, getResources().getString(R.string.str_need_wx_pay), "确定");
                                    }
                                    break;
                                case 2:
                                    // 点心支付
                                    data.setCashInType(RegData.CASHIN_WEIXIN_HG);
                                    WeiXinRechargeAct.startCashin(cashInAct, data);
                                    break;
                            }

                        } else if (TradeConfig.isCurrentJN(cashInAct)) {
                            if (data.getType() == 4) {// wap支付
                                String payUrl = data.getPayUrl();
                                if (TextUtils.isEmpty(payUrl)) {
                                    data.setUrl(data.getPayPage());
                                    // 标识是农交所微信充值
                                    data.setCashInType(RegData.CASHIN_WEIXIN_JN);
                                    WeiXinRechargeAct.startCashin(cashInAct, data);
                                } else {
                                    //微信支付
                                    try {
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(payUrl));
                                        //Result回来作检测支付是否成功
                                        startActivityForResult(intent, CODE_REQUEST);
                                    } catch (Exception e) {
                                        isError = true;
                                        e.printStackTrace();
                                        DialogUtil.showMsgDialog(cashInAct, getResources().getString(R.string.str_need_wx_pay), "确定");
                                    }
                                }
                            } else if (data.getType() == 5) {// app支付
                                wxPayByAppJN(data);
                            }
                        }
                    } else {
                        if (ApiConfig.isNeedLogin(response.getErrorCode())) {
                            //重新登录
                            showTokenDlg();
                            return;
                        }
                        DialogUtil.showMsgDialog(cashInAct, ConvertUtil.NVL(response.getErrorInfo(), "提交失败"), "确定");
                    }
                } else {
                    showCusToast("网络异常");
                }
            }
        }.execute();
    }

    /**
     * 微信的原生sdk支付
     * IWXAPI 方式
     */
    void submit4IWXAPI() {
        new AsyncTask<Void, Void, CommonResponse<String>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                cashInAct.showNetLoadingProgressDialog(null);
            }

            @Override
            protected CommonResponse<String> doInBackground(Void... params) {
                try {
                    UserInfoDao dao = new UserInfoDao(cashInAct);
                    if (!dao.isLogin())
                        return null;
                    String url = AndroidAPIConfig.getAPI(cashInAct, AndroidAPIConfig.KEY_URL_IWXPAY_WX_PAY);
                    Map<String, String> map = ApiConfig.getCommonMap(cashInAct);

                    map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
                    map.put("amount", money + "");
                    map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(cashInAct, map));

                    String res = TradeHelp.post(cashInAct, url, map);

                    //自己解析
                    JSONObject jsonObject = new JSONObject(res);
                    CommonResponse commonResponse = new CommonResponse();
                    commonResponse.setData(JSONObjectUtil.getString(jsonObject, "data", ""));
                    commonResponse.setSuccess(JSONObjectUtil.getBoolean(jsonObject, "success", false));
                    commonResponse.setErrorInfo(JSONObjectUtil.getString(jsonObject, "errorInfo", ""));
                    commonResponse.setErrorCode(JSONObjectUtil.getString(jsonObject, "errorCode", ""));

                    return commonResponse;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<String> response) {
                super.onPostExecute(response);
                try {
                    if (!isAdded())
                        return;
                    if (isDetached())
                        return;
                    cashInAct.hideNetLoadingProgressDialog();
                    if (response != null) {
                        if (response.isSuccess()) {
                            String json = response.getData();
                            String appid = BaseInterface.WX_APP_ID_PAY_HG;
                            //注意这里 如果后台传入的appid 和app配置的不一样，就是用后台传入的appid
                            JSONObject jsonObject = new JSONObject(json);
                            //后台传入的appid
                            String appidService = JSONObjectUtil.getString(jsonObject, "appid", "");
                            if (!StringUtil.isEmpty(appidService)) {
                                appid = appidService;
                            }
                            //发起支付
                            WXPayUtil.pay(cashInAct, WXAPIFactory.createWXAPI(cashInAct, appid), json);
                        } else {
                            if (ApiConfig.isNeedLogin(response.getErrorCode())) {
                                //重新登录
                                showTokenDlg();
                                return;
                            }
                            DialogUtil.showMsgDialog(cashInAct, ConvertUtil.NVL(response.getErrorInfo(), "提交失败"), "确定");
                        }
                    } else {
                        showCusToast("网络异常");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    /**
     * 哈贵的支付宝支付
     */
    void submit4ZFB() {
        new AsyncTask<Void, Void, CommonResponse<RegData>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                cashInAct.showNetLoadingProgressDialog(null);
            }

            @Override
            protected CommonResponse<RegData> doInBackground(Void... params) {
                try {
                    UserInfoDao dao = new UserInfoDao(cashInAct);
                    if (!dao.isLogin())
                        return null;
                    String url = AndroidAPIConfig.getAPI(cashInAct, AndroidAPIConfig.KEY_URL_USER_CASHIN_ALIPAY);
                    Map<String, String> map = ApiConfig.getCommonMap(cashInAct);

                    map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
                    map.put("amount", money + "");
                    map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(cashInAct, map));

                    String res = TradeHelp.post(cashInAct, url, map);

                    return CommonResponse.fromJson(res, RegData.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<RegData> response) {
                super.onPostExecute(response);
                if (!isAdded())
                    return;
                if (isDetached())
                    return;
                cashInAct.hideNetLoadingProgressDialog();
                if (response != null) {
                    data = response.getData();
                    if (response.isSuccess() && data != null) {

                        chargeMoneyByZFBApp(data);
                    } else {
                        if (ApiConfig.isNeedLogin(response.getErrorCode())) {
                            //重新登录
                            showTokenDlg();
                            return;
                        }
                        DialogUtil.showMsgDialog(cashInAct, ConvertUtil.NVL(response.getErrorInfo(), "提交失败"), "确定");
                    }
                } else {
                    showCusToast("网络异常");
                }
            }
        }.execute();
    }

    /**
     * 京东快捷支付
     */
    void submit4JD() {
        new AsyncTask<Void, Void, CommonResponse<JDPayObj>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                cashInAct.showNetLoadingProgressDialog(null);
            }

            @Override
            protected CommonResponse<JDPayObj> doInBackground(Void... params) {
                try {
                    if (!new UserInfoDao(getActivity()).isLogin())
                        return null;
                    Map<String, String> map = new LinkedHashMap<String, String>();
                    map.put(UserInfo.UID, new UserInfoDao(getActivity()).queryUserInfo().getUserId());
                    map.put("amount", money);
                    map = ApiConfig.getParamMap(getActivity(), map);
                    String api = AndroidAPIConfig.getAPI(getActivity(), AndroidAPIConfig.KEY_URL_TRADE_JDPAY_CHECK_BIND);
                    String res = TradeHelp.post((BaseActivity)getActivity(), api, map);
                    CommonResponse <JDPayObj> respon = CommonResponse.fromJson(res, JDPayObj.class);
                    return respon;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<JDPayObj> response) {
                super.onPostExecute(response);
                if (!isAdded())
                    return;
                if (isDetached())
                    return;
                cashInAct.hideNetLoadingProgressDialog();
                if (response != null) {
                    if (response.isSuccess()) {
                        //下一步 输入验证码
                        if (response.getData() != null
                                && response.getData().getOrderId() != null) {
                            JDPayInputSMSAct.start(getActivity(), response.getData(), money);
                        }
                    } else {
                        if (ApiConfig.isNeedLogin(response.getErrorCode())) {
                            //重新登录
                            showTokenDlg();
                            return;
                        }
                        //没有绑定过京东快捷支付,下一步绑定京东快捷支付信息
                        if (ApiConfig.ERROR_CODE_BIND_JD_IDCARD.equals(response.getErrorCode())) {
                            //请绑定京东快捷支付 30011
                            JDPayBindCardAct.start(getActivity(), money);
                            return;
                        }
                        DialogUtil.showMsgDialog(cashInAct, ConvertUtil.NVL(response.getErrorInfo(), "提交失败"), "确定");
                    }
                } else {
                    showCusToast("网络异常");
                }
            }
        }.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "requestCode=" + requestCode + " resultCode=" + resultCode);
        //requestCode=100 resultCode=0
        //微信支付回来的 resultCode 都是0，微信没有作Result处理，
        // 做法在这里使用 接口自己检测，如果成功，返回到资金页面去
        if (requestCode == CODE_REQUEST && !isError) {
            checkWXPAY();
        }
        if (data != null) {
            String respCode = data.getExtras().getString("resultCode");
            if (!TextUtils.isEmpty(respCode) && respCode.equalsIgnoreCase("success")) {
                cashInAct.doMyfinish();//关闭当前页面
                //标示支付成功
                showCusToast("充值成功");
            } else { //其他状态NOPAY状态：取消支付，未支付等状态
                showCusToast("充值失败或已取消充值");
            }
        }
    }

    /* *
      * 检测支付结果
     */
    void checkWXPAY() {
        new AsyncTask<Void, Void, CommonResponse<TempObject>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                cashInAct.showNetLoadingProgressDialog(null);
            }

            @Override
            protected CommonResponse<TempObject> doInBackground(Void... params) {
                try {
                    UserInfoDao dao = new UserInfoDao(cashInAct);
                    if (!dao.isLogin())
                        return null;
                    String url = AndroidAPIConfig.getAPI(cashInAct, AndroidAPIConfig.KEY_URL_TRADE_CASH_IN_WXCHECK);
                    Map<String, String> map = ApiConfig.getCommonMap(cashInAct);
                    map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
                    if (TradeConfig.isCurrentHG(cashInAct)) {
                        map.put("id", data.getId());
                    } else if (TradeConfig.isCurrentJN(cashInAct)) {
                        map.put("orderId", data.getOrderId());
                    }
                    map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(cashInAct, map));

                    String res = TradeHelp.post(cashInAct, url, map);

                    return CommonResponse.fromJson(res, RegData.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<TempObject> response) {
                super.onPostExecute(response);
                if (!isAdded())
                    return;
                if (isDetached())
                    return;
                data = null;
                cashInAct.hideNetLoadingProgressDialog();
                if (response != null) {
                    if (response.isSuccess()) {
                        cashInAct.doMyfinish();//关闭当前页面
                        showCusToast("充值成功");
                    } else
                        DialogUtil.showMsgDialog(cashInAct, ConvertUtil.NVL(response.getErrorInfo(), "提交失败"), "确定");

                } else showCusToast("网络异常");
            }
        }.execute();
    }

    /**
     * 农交所的微信app支付
     *
     * @param regData
     */
    private void wxPayByAppJN(RegData regData) {
        try {
            if (regData == null)
                return;
            if (StringUtil.isEmpty(regData.getAppId()))
                return;
            RequestMsg msg = new RequestMsg();
            msg.setTokenId(regData.getTokenId());//token_id为服务端预下单返回
            msg.setTradeType(MainApplication.WX_APP_TYPE);
            msg.setAppId(regData.getAppId());//appid为商户自己在微信开放平台的应用appid
            PayPlugin.unifiedAppPay(cashInAct, msg);
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, "wxPayByAppJN exception ");
        }
    }

    /**
     * 支付宝支付
     *
     * @param regData
     */
    private void chargeMoneyByZFBApp(RegData regData) {
//        RequestMsg msg = new RequestMsg();
//        msg.setTokenId(regData.getTokenId());//token_id为服务端预下单返回
//        msg.setTradeType(MainApplication.PAY_NEW_ZFB_WAP);
//        PayPlugin.unifiedH5Pay(cashInAct, msg);
    }


}
