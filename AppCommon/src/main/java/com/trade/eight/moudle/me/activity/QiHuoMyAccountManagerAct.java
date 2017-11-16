package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.tools.OpenActivityUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.view.AppTitleView;
import com.trade.eight.view.trade.TradeHeadView;

import java.math.BigDecimal;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/7.
 * 我的账户管理
 */

public class QiHuoMyAccountManagerAct extends BaseActivity {

    AppTitleView appTitleView;
    TradeHeadView trade_view;
    TextView text_qihuo_companey;
    TextView text_qihuo_account;
    Button btn_updatepwd;
    Button btn_updatepwd_money;
    TextView text_qihuo_phonetips;
    TextView text_qihuo_phone;

    TradeInfoData infoData;


    public static void statartAct(Context context, TradeInfoData infoData) {
        Intent intent = new Intent(context, QiHuoMyAccountManagerAct.class);
        if (infoData != null) {
            intent.putExtra("infoData", infoData);
        }
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_qihuo_myaccount_manager);
        initView();
        initData();
    }

    public void initView() {
        appTitleView = (AppTitleView) findViewById(R.id.title);
        appTitleView.setBaseActivity(QiHuoMyAccountManagerAct.this);
        appTitleView.setAppCommTitle(R.string.lable_my_qihuo_am);

        text_qihuo_companey = (TextView) findViewById(R.id.text_qihuo_companey);
        text_qihuo_account = (TextView) findViewById(R.id.text_qihuo_account);

        btn_updatepwd = (Button) findViewById(R.id.btn_updatepwd);
        btn_updatepwd_money = (Button) findViewById(R.id.btn_updatepwd_money);
        text_qihuo_phonetips = (TextView) findViewById(R.id.text_qihuo_phonetips);
        text_qihuo_phone = (TextView) findViewById(R.id.text_qihuo_phone);


        btn_updatepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QiHuoUpDatePwdAct.startAct(QiHuoMyAccountManagerAct.this, QiHuoUpDatePwdAct.TRADE_PWD);
            }
        });

        btn_updatepwd_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QiHuoUpDatePwdAct.startAct(QiHuoMyAccountManagerAct.this, QiHuoUpDatePwdAct.MONEY_PWD);
            }
        });

        text_qihuo_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenActivityUtil.callPhone(QiHuoMyAccountManagerAct.this,getResources().getString(R.string.lable_kefu_partcompany));
            }
        });

    }

    private void initData() {
        infoData = (TradeInfoData) getIntent().getSerializableExtra("infoData");
        if (infoData != null) {
            displayView();
        } else {

            TradeUserInfoData4Situation.getInstance(QiHuoMyAccountManagerAct.this, null).loadTradeOrderAndUserInfoData(new TradeUserInfoData4Situation.GetDataCallback() {
                @Override
                public void onFailure(String resultCode, String resultMsg) {
                    showCusToast(resultMsg);
                }

                @Override
                public void onResponse(TradeInfoData tradeInfoData) {
                    displayView();
                }
            }, false);

        }

    }

    private void displayView() {
        text_qihuo_companey.setText(infoData.getExchangeName());
        text_qihuo_account.setText(infoData.getAccountId());
//        text_qihuo_phone.setText(infoData.getCustomerServicephone());
    }
}
