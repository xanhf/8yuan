package com.trade.eight.moudle.trade.cashinout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.trade.TradeCashOut;
import com.trade.eight.entity.trade.TradeRechargeHistory;
import com.trade.eight.tools.StringUtil;


/**
 * 出入金详情页
 */

public class CashDetailAct extends BaseActivity {
    CashDetailAct context = this;
    TextView ed_liushui;
    View line_bank;
    View line_divider_bank;
    TextView ed_bank;
    View line_card;
    View line_divider_card;
    TextView ed_card;
    TextView ed_balance_lable;
    TextView ed_balance;
    TextView ed_time;
    TextView ed_state;


    public static void start(Context context, String title, TradeRechargeHistory tradeRechargeHistory) {
        Intent intent = new Intent(context, CashDetailAct.class);
        intent.putExtra("title", title);
        intent.putExtra("tradeRechargeHistory", tradeRechargeHistory);
        context.startActivity(intent);
    }

    public static void start(Context context, String title, TradeCashOut tradeCashOut) {
        Intent intent = new Intent(context, CashDetailAct.class);
        intent.putExtra("title", title);
        intent.putExtra("tradeCashOut", tradeCashOut);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cash_detail);
        setAppCommonTitle(getResources().getString(R.string.trade_order_bindcard_title));

        ed_liushui = (TextView) findViewById(R.id.ed_liushui);
        line_bank = findViewById(R.id.line_bank);
        ed_bank = (TextView) findViewById(R.id.ed_bank);
        line_divider_bank = findViewById(R.id.line_divider_bank);
        line_card = findViewById(R.id.line_card);
        ed_card = (TextView) findViewById(R.id.ed_card);
        line_divider_card = findViewById(R.id.line_divider_card);

        ed_balance_lable = (TextView) findViewById(R.id.ed_balance_lable);
        ed_balance = (TextView) findViewById(R.id.ed_balance);
        ed_time = (TextView) findViewById(R.id.ed_time);
        ed_state = (TextView) findViewById(R.id.ed_state);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        setAppCommonTitle(intent.getStringExtra("title"));
        TradeRechargeHistory tradeRechargeHistory = (TradeRechargeHistory) intent.getSerializableExtra("tradeRechargeHistory");
        if (tradeRechargeHistory != null) {
            line_bank.setVisibility(View.GONE);
            line_card.setVisibility(View.GONE);
            line_divider_bank.setVisibility(View.GONE);
            line_divider_card.setVisibility(View.GONE);

            ed_liushui.setText(tradeRechargeHistory.getId()+"");
            ed_balance_lable.setText("金额");
            ed_balance.setText(tradeRechargeHistory.getAmount());
            ed_time.setText(tradeRechargeHistory.getCreateTime());
            ed_state.setText(tradeRechargeHistory.getState());
        } else {
            TradeCashOut tradeCashOut = (TradeCashOut) intent.getSerializableExtra("tradeCashOut");
            if (tradeCashOut != null) {
                ed_liushui.setText(tradeCashOut.getLogNo());
                ed_bank.setText(tradeCashOut.getBankName());
                ed_card.setText(StringUtil.getHintCardNo(tradeCashOut.getBankNo()));
                ed_balance.setText(tradeCashOut.getBalance());
                ed_time.setText(tradeCashOut.getLastHandleTime());
                StringBuilder sb = new StringBuilder(tradeCashOut.getState());
                if(!TextUtils.isEmpty(tradeCashOut.getMark())){
                    sb.append("(");
                    sb.append(tradeCashOut.getMark());
                    sb.append(")");
                }
                ed_state.setText(sb.toString());
            }
        }
    }

}
