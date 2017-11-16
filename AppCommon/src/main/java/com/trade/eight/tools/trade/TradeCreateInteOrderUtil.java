package com.trade.eight.tools.trade;

import android.app.Dialog;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.integral.AccountIntegralData;
import com.trade.eight.entity.integral.IntegralProductData;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.me.CreateIntegralOrderSuccessEvent;
import com.trade.eight.moudle.trade.activity.TradeVoucherAct;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.PreferenceSetting;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 16/12/14.
 * 积分兑换
 */

public class TradeCreateInteOrderUtil {
    private BaseActivity activity;
    private IntegralProductData integralProductData;
    private AccountIntegralData integralDataForMarket;
    Dialog dialog;

    ImageView img_inteproduct;
    TextView text_inteproduct_name;
    TextView text_account_integral;
    Button btn_createorder_minus;
    Button btn_createorder_num;
    Button btn_createorder_plus;
    TextView text_needintegral;
    LinearLayout line_rebateinfo_parent;
    LinearLayout line_rebateinfo;
    TextView text_integralrebat;
    TextView text_rebatinfo;
    Button btn_inteexchange;


    public TradeCreateInteOrderUtil(BaseActivity activity, IntegralProductData integralProductData, AccountIntegralData integralDataForMarket) {
        this.activity = activity;
        this.integralProductData = integralProductData;
        this.integralDataForMarket = integralDataForMarket;
        initDialog();
        initViews();
        displayView();
    }

    private void initDialog() {
        dialog = new Dialog(activity, R.style.dialog_trade);
        View rootView = View.inflate(activity, R.layout.dialog_createorder_integral, null);
        dialog.setContentView(rootView);
        Window w = dialog.getWindow();
        WindowManager.LayoutParams params = w.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        w.setGravity(Gravity.BOTTOM);
        w.setWindowAnimations(R.style.dialog_trade_ani);
        dialog.setCancelable(true);
    }

    private void initViews() {
        img_inteproduct = (ImageView) dialog.findViewById(R.id.img_inteproduct);
        text_inteproduct_name = (TextView) dialog.findViewById(R.id.text_inteproduct_name);
        text_account_integral = (TextView) dialog.findViewById(R.id.text_account_integral);
        btn_createorder_minus = (Button) dialog.findViewById(R.id.btn_createorder_minus);
        btn_createorder_num = (Button) dialog.findViewById(R.id.btn_createorder_num);
        btn_createorder_plus = (Button) dialog.findViewById(R.id.btn_createorder_plus);
        text_needintegral = (TextView) dialog.findViewById(R.id.text_needintegral);
        line_rebateinfo_parent =   (LinearLayout) dialog.findViewById(R.id.line_rebateinfo_parent);
        line_rebateinfo = (LinearLayout) dialog.findViewById(R.id.line_rebateinfo);
        text_integralrebat = (TextView) dialog.findViewById(R.id.text_integralrebat);
        text_integralrebat.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
        text_rebatinfo = (TextView) dialog.findViewById(R.id.text_rebatinfo);
        btn_inteexchange = (Button) dialog.findViewById(R.id.btn_inteexchange);
    }

    private void displayView() {
        ImageLoader.getInstance().displayImage(integralProductData.getGiftSmallPic(), img_inteproduct, AppImageLoaderConfig.getDisplayImageOptions(activity));
        text_inteproduct_name.setText(integralProductData.getGiftName());
        text_account_integral.setText(integralDataForMarket.getValidPoints() + "");

        double rebaterate = NumberUtil.multiply(10, Double.parseDouble(integralDataForMarket.getRebateRate()));
        if (rebaterate == 10) {//无优惠不展示
            line_rebateinfo_parent.setVisibility(View.GONE);
            line_rebateinfo.setVisibility(View.GONE);
        } else {
            line_rebateinfo_parent.setVisibility(View.VISIBLE);
            line_rebateinfo.setVisibility(View.VISIBLE);
            text_integralrebat.setText(integralProductData.getPoins() + "");
            String rebaterateStr = new DecimalFormat("0.0").format(rebaterate);
            String lastRebaterateStr = rebaterateStr.substring(2,3);
            if(Integer.parseInt(lastRebaterateStr)>0){

            }else{
                rebaterateStr = rebaterateStr.substring(0,1);
            }
            text_rebatinfo.setText(activity.getResources().getString(R.string.intergral_rebattips, integralDataForMarket.getLevelName(), rebaterateStr));
        }
        text_needintegral.setText(NumberUtil.roundUp(NumberUtil.multiply(integralProductData.getPoins(), Double.parseDouble(integralDataForMarket.getRebateRate())), 0));

        btn_createorder_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusOrPlus(true);
            }
        });
        btn_createorder_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusOrPlus(false);
            }
        });

        btn_inteexchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = btn_createorder_num.getText().toString();
                int num = Integer.parseInt(text);
                submit(num);
            }
        });
        dialog.show();
    }

    private void minusOrPlus(boolean minusOrPlus) {
        String text = btn_createorder_num.getText().toString();
        int num = Integer.parseInt(text);
        if (minusOrPlus) {
            if (num == 1) {
                activity.showCusToast("亲,已经是最少张数了");
                return;
            }
            num -= 1;
            btn_createorder_num.setText(num + "");
            int needIntegral = integralProductData.getPoins() * num;
            text_needintegral.setText(NumberUtil.roundUp(NumberUtil.multiply(needIntegral, Double.parseDouble(integralDataForMarket.getRebateRate())), 0));
            text_integralrebat.setText(needIntegral + "");
        } else {
            int needIntegral = integralProductData.getPoins() * (num + 1);
            int integralrebat = Integer.parseInt(NumberUtil.roundUp(NumberUtil.multiply(needIntegral, Double.parseDouble(integralDataForMarket.getRebateRate())), 0));
            if (integralrebat > integralDataForMarket.getValidPoints()) {
                activity.showCusToast("亲,已达到您的兑换上限了");
                return;
            }
            num += 1;
            btn_createorder_num.setText(num + "");
            text_needintegral.setText(integralrebat+"");
            text_integralrebat.setText(needIntegral + "");
        }
    }

    private void submit(final int num) {
        new AsyncTask<Void, Void, CommonResponse<AccountIntegralData>>() {
            @Override
            protected CommonResponse<AccountIntegralData> doInBackground(Void... params) {
                try {
                    UserInfoDao userInfoDao = new UserInfoDao(activity);
                    if (!userInfoDao.isLogin()) {
                        return null;
                    }
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(UserInfo.UID, userInfoDao.queryUserInfo().getUserId());
                    long lvVersion = PreferenceSetting.getInt(activity, PreferenceSetting.ACCOUNT_INTEGRAL_VERSION);
                    if (lvVersion < 0) {
                        lvVersion = 0;
                    }
                    map.put("versionNo", lvVersion + "");
                    map.put("giftId", integralProductData.getGiftId() + "");
                    map.put("giftNum", num + "");
                    map = ApiConfig.getParamMap(activity, map);
                    String res = HttpClientHelper.getStringFromPost(activity, AndroidAPIConfig.URL_ACCOUNT_INTEGRAL_EXCHANGE, map);
                    CommonResponse<AccountIntegralData> response = CommonResponse.fromJson(res, AccountIntegralData.class);
                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                activity.showNetLoadingProgressDialog(activity.getResources().getString(R.string.str_trade_deal));
            }

            @Override
            protected void onPostExecute(CommonResponse<AccountIntegralData> accountIntegralDataCommonResponse) {
                super.onPostExecute(accountIntegralDataCommonResponse);
                if (activity.isFinishing()) {
                    return;
                }

                activity.hideNetLoadingProgressDialog();
                if (accountIntegralDataCommonResponse != null) {

                    if (accountIntegralDataCommonResponse.isSuccess()) {
                        integralDataForMarket = accountIntegralDataCommonResponse.getData();
                        long lvVersion = PreferenceSetting.getInt(activity, PreferenceSetting.ACCOUNT_INTEGRAL_VERSION);
                        if (lvVersion == integralDataForMarket.getVersionNo()) {
                        } else {
                            PreferenceSetting.setInt(activity, PreferenceSetting.ACCOUNT_INTEGRAL_VERSION, integralDataForMarket.getVersionNo());
                            String lv = new Gson().toJson(integralDataForMarket.getLevelList());
                            PreferenceSetting.setString(activity, PreferenceSetting.ACCOUNT_INTEGRAL_INFO, lv);
                        }
                        String msg = activity.getResources().getString(R.string.integral_create_success);
                        //成功的逻辑处理
                        if (dialog != null)
                            dialog.dismiss();
                        EventBus.getDefault().post(new CreateIntegralOrderSuccessEvent(integralDataForMarket));
                        DialogUtil.showExchangeIntegralSuccessDlg(activity, msg, new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message message) {
                                TradeVoucherAct.startTradeVoucherAct(activity,integralProductData.getExcode());
                                return false;
                            }
                        });

                    } else {
                        //成功的逻辑处理
                        if (dialog != null)
                            dialog.dismiss();
                        activity.showCusToast(accountIntegralDataCommonResponse.getErrorInfo());
                    }
                } else {
                    activity.showCusToast(activity.getResources().getString(R.string.network_problem));

                }
            }
        }.execute();
    }
}
