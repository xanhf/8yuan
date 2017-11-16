package com.trade.eight.moudle.trade.activity;

import android.os.Bundle;

import com.easylife.ten.lib.R;
import com.trade.eight.base.BaseActivity;

/**
 * Created by fangzhu on 16/6/24.
 */
public class VoucherRuleAct extends BaseActivity {

    private int from = FROMVOUCHER;
    public static final int FROMVOUCHER = 0;//  来着优惠券
    public static final int FROMREDPACKET = 1;// 来自我的红包

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        from = getIntent().getIntExtra("from", FROMVOUCHER);

        if (from == FROMVOUCHER) {
            setContentView(R.layout.act_voucher_rule);
            setAppCommonTitle("使用规则");
        } else if (from == FROMREDPACKET) {

            setContentView(R.layout.act_redpacket_rule);
            setAppCommonTitle("红包说明");
        }
    }


}
