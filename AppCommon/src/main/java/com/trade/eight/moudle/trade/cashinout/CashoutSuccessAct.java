package com.trade.eight.moudle.trade.cashinout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;

/**
 * 作者：Created by ocean
 * 时间：on 2017/6/5.
 * 提现成功
 */

public class CashoutSuccessAct extends BaseActivity{

    private TextView text_tips;

    public static void startAct(Context context){
        Intent intent = new Intent(context,CashoutSuccessAct.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cash_outsucc);
        setAppCommonTitle(getResources().getString(R.string.cashout_success_title));

        initView();
    }

    private void initView(){
        text_tips = (TextView) findViewById(R.id.text_tips);
        String str = getResources().getString(R.string.cashout_success_content);
        SpannableString ss = new SpannableString(str);
        ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_opt_gt)), 16, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text_tips.setText(str);
    }
}
