package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.QiHuoExplainWordConfig;
import com.trade.eight.entity.trade.TradeInfoData;
import com.trade.eight.moudle.outterapp.QiHuoExplainWordActivity;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.moudle.trade.UpdateTradeUserInfoEvent;
import com.trade.eight.moudle.trade.activity.DialogUtilForTrade;
import com.trade.eight.moudle.trade.login.TradeLoginDlg;
import com.trade.eight.service.trade.TradeHelp;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.view.AppTitleView;
import com.trade.eight.view.trade.TradeHeadView;

import java.math.BigDecimal;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/7.
 * 我的账户
 */

public class QiHuoMyAccountAct extends BaseActivity {

    AppTitleView appTitleView;
    TextView text_trade_close_profit;
    TextView text_trade_kequ_money;
    TextView text_trade_keyong_money;
    TextView text_trade_zhanyong_money;
    TextView text_trade_dongjie_money;
    TextView text_trade_shouxufei;
    TextView text_trade_dongjie_shouxufei;
    Button btn_cashin;
    Button btn_cashout;

    TextView text_moneyzy_rate;
    ImageView img_tradeinfo_dangerous;
    TextView text_money_dqqy;
    TextView text_moneyzy_kyzj;
    TextView text_moneyzy_dyyk;

    TradeInfoData infoData;

    public static void statartAct(Context context, TradeInfoData infoData) {
        Intent intent = new Intent(context, QiHuoMyAccountAct.class);
        if (infoData != null) {
            intent.putExtra("infoData", infoData);
        }
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_qihuo_myaccount);
        initView();
        initData();
    }

    public void initView() {
        appTitleView = (AppTitleView) findViewById(R.id.title);
        appTitleView.setBaseActivity(QiHuoMyAccountAct.this);
        appTitleView.setAppCommTitle(R.string.lable_myaccount);
        appTitleView.setRightImgBtn(true,R.drawable.img_hometrade_help);
        appTitleView.setRightImgCallback(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
//                QiHuoExplainWordActivity.startAct(QiHuoMyAccountAct.this,
                DialogUtilForTrade.showQiHuoExplainDlg(QiHuoMyAccountAct.this,
                        new String[]{QiHuoExplainWordConfig.FXL,
                                QiHuoExplainWordConfig.DQQY,
                                QiHuoExplainWordConfig.KYZJ,
                                QiHuoExplainWordConfig.ZCCYK,
                                QiHuoExplainWordConfig.PCYK,
                                QiHuoExplainWordConfig.KQBZJ,
                                QiHuoExplainWordConfig.BZJ,
                                QiHuoExplainWordConfig.DZBZJ,
                                QiHuoExplainWordConfig.SXF,
                                QiHuoExplainWordConfig.DZSXF});
                return false;
            }
        });

        text_moneyzy_rate = (TextView) findViewById(R.id.text_moneyzy_rate);
        img_tradeinfo_dangerous = (ImageView) findViewById(R.id.img_tradeinfo_dangerous);
        text_money_dqqy = (TextView)findViewById(R.id.text_money_dqqy);
        text_moneyzy_kyzj = (TextView) findViewById(R.id.text_moneyzy_kyzj);
        text_moneyzy_dyyk = (TextView) findViewById(R.id.text_moneyzy_dyyk);

        text_trade_close_profit = (TextView) findViewById(R.id.text_trade_close_profit);
        text_trade_kequ_money = (TextView) findViewById(R.id.text_trade_kequ_money);
        text_trade_keyong_money = (TextView) findViewById(R.id.text_trade_keyong_money);
        text_trade_zhanyong_money = (TextView) findViewById(R.id.text_trade_zhanyong_money);
        text_trade_dongjie_money = (TextView) findViewById(R.id.text_trade_dongjie_money);
        text_trade_shouxufei = (TextView) findViewById(R.id.text_trade_shouxufei);
        text_trade_dongjie_shouxufei = (TextView) findViewById(R.id.text_trade_dongjie_shouxufei);

        btn_cashin = (Button) findViewById(R.id.btn_cashin);
        btn_cashout = (Button) findViewById(R.id.btn_cashout);

        img_tradeinfo_dangerous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogUtil.showTitleAndContentDialog(QiHuoMyAccountAct.this,
                        getResources().getString(R.string.tradeinfonotify_4),
                        getResources().getString(R.string.tradeinfonotify_5, infoData.getCapitalProportion()),
                        getResources().getString(R.string.unifypwd_posbtn_oldaccount),
                        getResources().getString(R.string.tradeinfonotify_chargenow),
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
                                CashInAndOutAct.startAct(QiHuoMyAccountAct.this);
                                return false;
                            }
                        });
            }
        });

        btn_cashin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CashInAndOutAct.startAct(QiHuoMyAccountAct.this);
            }
        });

        btn_cashout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CashInAndOutAct.startAct(QiHuoMyAccountAct.this,1);
            }
        });

    }

    private void initData() {
        infoData = (TradeInfoData) getIntent().getSerializableExtra("infoData");
        if (infoData != null) {
            displayView(infoData);
            text_trade_close_profit.setText(StringUtil.forNumber(new BigDecimal(infoData.getCloseProfit()).doubleValue()));
            text_trade_kequ_money.setText(StringUtil.forNumber(new BigDecimal(infoData.getWithdrawQuota()).doubleValue()));
            text_trade_keyong_money.setText(StringUtil.forNumber(new BigDecimal(infoData.getAvailable()).doubleValue()));
            text_trade_zhanyong_money.setText(StringUtil.forNumber(new BigDecimal(infoData.getCurrMargin()).doubleValue()));
            text_trade_dongjie_money.setText(StringUtil.forNumber(new BigDecimal(infoData.getFrozenCash()).doubleValue()));
            text_trade_shouxufei.setText(StringUtil.forNumber(new BigDecimal(infoData.getCommission()).doubleValue()));
            text_trade_dongjie_shouxufei.setText(StringUtil.forNumber(new BigDecimal(infoData.getFrozenCommission()).doubleValue()));
        }else{
            TradeUserInfoData4Situation.getInstance(QiHuoMyAccountAct.this, null).loadTradeOrderAndUserInfoData(new TradeUserInfoData4Situation.GetDataCallback() {
                @Override
                public void onFailure(String resultCode, String resultMsg) {
//                showCusToast(resultMsg);
                }

                @Override
                public void onResponse(TradeInfoData tradeInfoData) {
                    displayView(tradeInfoData);
                    text_trade_close_profit.setText(StringUtil.forNumber(new BigDecimal(infoData.getCloseProfit()).doubleValue()));
                    text_trade_kequ_money.setText(StringUtil.forNumber(new BigDecimal(infoData.getWithdrawQuota()).doubleValue()));
                    text_trade_keyong_money.setText(StringUtil.forNumber(new BigDecimal(infoData.getAvailable()).doubleValue()));
                    text_trade_zhanyong_money.setText(StringUtil.forNumber(new BigDecimal(infoData.getCurrMargin()).doubleValue()));
                    text_trade_dongjie_money.setText(StringUtil.forNumber(new BigDecimal(infoData.getFrozenCash()).doubleValue()));
                    text_trade_shouxufei.setText(StringUtil.forNumber(new BigDecimal(infoData.getCommission()).doubleValue()));
                    text_trade_dongjie_shouxufei.setText(StringUtil.forNumber(new BigDecimal(infoData.getFrozenCommission()).doubleValue()));
                }
            }, false);
        }
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
        this.infoData = tradeInfoData;
        text_moneyzy_rate.setText(tradeInfoData.getCapitalProportion());
        text_money_dqqy.setText(StringUtil.forNumber(new BigDecimal(tradeInfoData.getRightsInterests()).doubleValue()));
        text_moneyzy_kyzj.setText(StringUtil.forNumber(new BigDecimal(tradeInfoData.getAvailable()).doubleValue()));
        if (new BigDecimal(ConvertUtil.NVL(tradeInfoData.getPositionProfit(), "0")).doubleValue() > 0) {
            text_moneyzy_dyyk.setTextColor(getResources().getColor(R.color.c_EA4A5E));
            text_moneyzy_dyyk.setText("+" + StringUtil.forNumber(new BigDecimal(ConvertUtil.NVL(tradeInfoData.getPositionProfit(), "0")).doubleValue()));
        } else if (new BigDecimal(ConvertUtil.NVL(tradeInfoData.getPositionProfit(), "0")).doubleValue() < 0) {
            text_moneyzy_dyyk.setTextColor(getResources().getColor(R.color.c_06A969));
            text_moneyzy_dyyk.setText(StringUtil.forNumber(new BigDecimal(ConvertUtil.NVL(tradeInfoData.getPositionProfit(), "0")).doubleValue()));
        } else {
            text_moneyzy_dyyk.setTextColor(getResources().getColor(R.color.c_464646));
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

    /**
     * 数据刷新成功
     *
     * @param updateTradeUserInfoEvent
     */
    public void onEventMainThread(UpdateTradeUserInfoEvent updateTradeUserInfoEvent) {
        TradeInfoData tradeInfoData = updateTradeUserInfoEvent.tradeInfoData;
        displayView(tradeInfoData);
    }
}
