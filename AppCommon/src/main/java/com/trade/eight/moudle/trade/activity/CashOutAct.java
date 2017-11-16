package com.trade.eight.moudle.trade.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.Banks;
import com.trade.eight.entity.trade.CashOutFee;
import com.trade.eight.entity.trade.Province;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.moudle.trade.cashinout.CashOutBindCardAct;
import com.trade.eight.moudle.trade.cashinout.CashOutCardManageAct;
import com.trade.eight.moudle.trade.cashinout.CashoutSuccessAct;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.NetWorkUtils;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.AESUtil;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.OpenActivityUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.Utils;
import com.trade.eight.tools.trade.TradeConfig;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by developer on 16/1/18.
 * 提现的 act
 */
public class CashOutAct extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "CashOutAct";

    CashOutAct context = this;
    TextView text_cardmanager;
    private View line_cashout_card;
    private ImageView img_bankicon;
    private TextView text_bankname;
    private TextView text_bankcard;
    private TextView text_addcard;
    private TextView text_cancashout;
    EditText ed_inputMoney;
    private TextView text_cashout_tips;
    private Button btn_submit;


    List<Banks> banksList;
    Banks selectedBank;
    Dialog msgDlg;
    String blance = "";
    double usdCny = 0;

    public static void startAct(Context context) {
        Intent intent = new Intent(context, CashOutAct.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        //检测登录状态
        if (!new UserInfoDao(context).isLogin()) {
            finish();
            Map<String, String> map = new HashMap<String, String>();
            Intent intent = OpenActivityUtil.initAction(context, LoginActivity.class, OpenActivityUtil.ACT_CASHOUT, map);
            if (intent != null)
                startActivity(intent);
            return;
        }
        setContentView(R.layout.act_cash_out);

        initViews();
        setAppCommonTitle(getResources().getString(R.string.trade_order_cashout));


        //先获取可提现的金额
        getUserBlanceTask();

        ed_inputMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //设置不同的字体大小
                if (StringUtil.isEmpty(ed_inputMoney.getText().toString())) {
                    ed_inputMoney.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);

                } else {
                    ed_inputMoney.setTextSize(TypedValue.COMPLEX_UNIT_SP, 34);

                    if (!checkInputMoney()) {
                        DialogUtil.showMsgDialog(CashOutAct.this, "超出你的可提现金额", "我知道了", null);
                    } else {
                        checkInputMoneyecimalPoint();
                    }
                }
                calculateCommission(ed_inputMoney.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    /**
     * 检查小数点后面位数
     *
     * @return
     */
    void checkInputMoneyecimalPoint() {
        String s = ed_inputMoney.getText().toString().trim();
        int index = s.indexOf(".");
        if (index != -1) {
            int decent = s.length() - (index + 1);
            if (decent > 2) {
                s = s.substring(0, index + 3);
                ed_inputMoney.setText(s);
                ed_inputMoney.setSelection(s.length());
            }
        }
    }

    /**
     * 检查输入的金额是否可用
     * 大于可提现金额
     *
     * @return
     */
    boolean checkInputMoney() {
        double money = 0, totalMoney = 0;
        try {
            String s = ed_inputMoney.getText().toString().trim();
            if (StringUtil.isEmpty(s))
                return false;
            s = s.replaceAll(",", "");//去掉格式化的符号
            money = Double.parseDouble(s);
            totalMoney = Double.parseDouble(blance.replaceAll(",", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (money > totalMoney)
            return false;
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v(TAG, "onNewIntent");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        //获取历史银行信息
        getUserBankList();
    }

    void initViews() {

        text_cardmanager = (TextView) findViewById(R.id.text_cardmanager);
        line_cashout_card = findViewById(R.id.line_cashout_card);
        text_addcard = (TextView) findViewById(R.id.text_addcard);

        img_bankicon = (ImageView) findViewById(R.id.img_bankicon);
        text_bankname = (TextView) findViewById(R.id.text_bankname);
        text_bankcard = (TextView) findViewById(R.id.text_bankcard);
        text_cancashout = (TextView) findViewById(R.id.text_cancashout);
        ed_inputMoney = (EditText) findViewById(R.id.ed_inputMoney);
        text_cashout_tips = (TextView) findViewById(R.id.text_cashout_tips);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        text_cardmanager.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        line_cashout_card.setOnClickListener(this);
        text_addcard.setOnClickListener(this);

    }

    /**
     * 获取用户银行卡列表
     */
    private void getUserBankList() {
        HttpClientHelper.doPostOption(CashOutAct.this,
                AndroidAPIConfig.getAPI(CashOutAct.this, AndroidAPIConfig.KEY_URL_TRADE_GET_USER_BANKS),
                null,
                null,
                new NetCallback(CashOutAct.this) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        DialogUtil.showMsgDialog(context, ConvertUtil.NVL(resultMsg, "网络连接失败！"), "确定", new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message message) {
                                doMyfinish();
                                return false;
                            }
                        });
                    }

                    @Override
                    public void onResponse(String response) {
                        CommonResponse4List<Banks> commonResponse4List = CommonResponse4List.fromJson(response, Banks.class);
                        banksList = commonResponse4List.getData();
                        if (banksList != null && banksList.size() > 0) {
                            selectedBank = banksList.get(0);
                            line_cashout_card.setVisibility(View.VISIBLE);
                            text_addcard.setVisibility(View.GONE);
                            if (!TextUtils.isEmpty(selectedBank.getIcon())) {
                                img_bankicon.setVisibility(View.VISIBLE);
                                ImageLoader.getInstance().displayImage(selectedBank.getIcon(), img_bankicon, AppImageLoaderConfig.getDisplayImageOptions(context));
                            } else {
                                img_bankicon.setVisibility(View.GONE);
                            }
                            text_bankname.setText(selectedBank.getBankName());
                            text_bankcard.setText(selectedBank.getBankNo().substring(selectedBank.getBankNo().length() - 4));
                        } else {
                            line_cashout_card.setVisibility(View.GONE);
                            text_addcard.setVisibility(View.VISIBLE);
                        }

                    }
                },
                true);
    }

    /**
     * 获取用户可提现余额
     */
    private void getUserBlanceTask() {
        HttpClientHelper.doPostOption(CashOutAct.this,
                AndroidAPIConfig.getAPI(CashOutAct.this, AndroidAPIConfig.KEY_URL_TRADE_GET_USERINFO),
                null,
                null,
                new NetCallback(CashOutAct.this) {
                    @Override
                    public void onFailure(String resultCode, String resultMsg) {
                        DialogUtil.showMsgDialog(context, ConvertUtil.NVL(resultMsg, "网络连接失败！"), "确定", new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message message) {
                                doMyfinish();
                                return false;
                            }
                        });
                    }

                    @Override
                    public void onResponse(String response) {
                        CommonResponse<UserInfo> commonResponse = CommonResponse.fromJson(response, UserInfo.class);
                        UserInfo userInfo = commonResponse.getData();
                        if (userInfo != null) {
                            blance = userInfo.getBalance();
                            usdCny = userInfo.getUsdCny();
                            text_cancashout.setText(blance);
                            text_cashout_tips.setText(getResources().getString(R.string.trade_order_cashout_tips, usdCny + "", "-"));

                        }
                    }
                },
                true);
    }

    boolean isDoing = false;

    /**
     * 提现
     *
     * @param map 提现参数
     */
    void submit(final Map<String, String> map) {
        new AsyncTask<Void, Void, CommonResponse<String>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showNetLoadingProgressDialog("请稍等");
            }

            @Override
            protected CommonResponse<String> doInBackground(Void... params) {
                try {
                    isDoing = true;
                    UserInfoDao dao = new UserInfoDao(context);
                    if (!dao.isLogin())
                        return null;
                    String url = AndroidAPIConfig.getAPI(context, AndroidAPIConfig.KEY_URL_TRADE_GET_CASH_OUT);
                    String res = TradeHelp.post(context, url, map);

                    return CommonResponse.fromJson(res, String.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(final CommonResponse<String> response) {
                super.onPostExecute(response);
                isDoing = false;
                hideNetLoadingProgressDialog();
                if (isFinishing())
                    return;
                if (response != null) {
                    if (response.isSuccess()) {
//                        showCusToast("提交成功！等待审核");
                        CashoutSuccessAct.startAct(CashOutAct.this);
                        //退出当前页面
                        finish();
                        //更新账户金额
                    } else {
                        String msg = ConvertUtil.NVL(response.getErrorInfo(), getResources().getString(R.string.cashout_faile));
                        showCusToast(msg);

                        /*String negStr = getResources().getString(R.string.close_btn_neg);
                        String postStr = getResources().getString(R.string.close_btn_post);
                        String title = getResources().getString(R.string.cashout_faile);
                        String msg = ConvertUtil.NVL(response.getErrorInfo(), "提现失败");

                        if (msgDlg != null && msgDlg.isShowing())
                            return;

                        if (!TextUtils.isEmpty(response.errorCode) && response.errorCode.equals(ApiConfig.ERROR_CODE_SHIMING)) {
                            postStr = getResources().getString(R.string.cashout_goshiming);
                            msgDlg = DialogUtil.getAllInfoMsgDlg(context, title, msg, negStr, postStr, new DialogUtil.AuthTokenCallBack() {
                                @Override
                                public void onPostClick(Object obj) {
                                    String qa_trade = context.getResources().getString(R.string.cashout_shiming);
                                    String url = AndroidAPIConfig.URL_CASHSHIMING_JN;
                                    Map<String, String> paramMap = new HashMap<String, String>();
                                    UserInfoDao dao = new UserInfoDao(CashOutAct.this);
                                    if (!dao.isLogin())
                                        return;
                                    paramMap.put("userId", dao.queryUserInfo().getUserId());
                                    paramMap.put("exchangeId", TradeConfig.getExchangeId(context, TradeConfig.getCurrentTradeCode(context)) + "");
                                    paramMap.put("token", AppSetting.getInstance(context).getWPToken(context));
                                    paramMap = ApiConfig.getParamMap(CashOutAct.this, paramMap);
                                    url = NetWorkUtils.setParam4get(url, paramMap);
                                    Log.e(TAG, url);
                                    WebActivity.start(context, qa_trade, url);
                                }

                                @Override
                                public void onNegClick() {

                                }
                            });
                        } else {
                            msgDlg = DialogUtil.getAllInfoMsgDlg(context, title, msg, negStr, postStr, new DialogUtil.AuthTokenCallBack() {
                                @Override
                                public void onPostClick(Object obj) {

                                    String qa_trade = context.getResources().getString(R.string.qa_trade);
                                    WebActivity.start(context, qa_trade, AndroidAPIConfig.URL_RULES_QA);
                                }

                                @Override
                                public void onNegClick() {

                                }
                            });
                        }
                        msgDlg.show();*/
                    }


                } else showCusToast("网络异常");
            }
        }.execute();
    }


    /**
     * 利用实时汇率计算
     *
     * @param inputMoney
     */
    void calculateCommission(String inputMoney) {

        if (!TextUtils.isEmpty(inputMoney)) {
            BigDecimal amount = new BigDecimal(inputMoney);
            String rate = NumberUtil.roundUp(NumberUtil.multiply(Double.parseDouble(inputMoney), usdCny), 2) + "";
            text_cashout_tips.setText(getResources().getString(R.string.trade_order_cashout_tips, usdCny + "", rate));
        } else {
            text_cashout_tips.setText(getResources().getString(R.string.trade_order_cashout_tips, usdCny + "", "-"));
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.text_cardmanager) {
            CashOutCardManageAct.startAct(CashOutAct.this);
        } else if (viewId == R.id.text_addcard) {
            CashOutBindCardAct.start(CashOutAct.this);
        } else if (viewId == R.id.line_cashout_card) {
            DialogUtilForTrade.showSelectBankDlg(CashOutAct.this, banksList, selectedBank, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    selectedBank = (Banks) msg.obj;
                    if (!TextUtils.isEmpty(selectedBank.getIcon())) {
                        img_bankicon.setVisibility(View.VISIBLE);
                        ImageLoader.getInstance().displayImage(selectedBank.getIcon(), img_bankicon, AppImageLoaderConfig.getDisplayImageOptions(context));
                    } else {
                        img_bankicon.setVisibility(View.GONE);
                    }
                    text_bankname.setText(selectedBank.getBankName());
                    text_bankcard.setText(selectedBank.getBankNo().substring(selectedBank.getBankNo().length() - 4));
                    return false;
                }
            });
        } else if (viewId == R.id.btn_submit) {
            double money = 0;
            try {
                String s = ed_inputMoney.getText().toString().trim();
                s = s.replaceAll(",", "");//去掉格式化的符号
                money = Double.parseDouble(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (money <= 0) {
                showCusToast("请输入提现的金额");
                return;
            }

            if (banksList == null || banksList.size() == 0) {
                showCusToast("请选择银行卡");
                return;
            }

            if(selectedBank==null){
                showCusToast("请选择银行卡");
                return;
            }

            UserInfoDao dao = new UserInfoDao(context);
            if (!dao.isLogin()) {
                showCusToast("请登录");
                return;
            }

            final Map<String, String> map = ApiConfig.getCommonMap(context);
            map.put("balance", money + "");
            map.put("bankId", selectedBank.getBankId());

            //正在提交中 就不处理
            if (!isDoing) {
                map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, map));
                submit(map);
            }
        }
    }
}
