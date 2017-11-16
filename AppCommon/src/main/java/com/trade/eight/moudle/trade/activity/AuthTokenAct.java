package com.trade.eight.moudle.trade.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.trade.eight.base.BaseActivity;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.trade.TradeConfig;

/**
 * Created by developer on 16/1/22.
 * 交易过程中
 * 验证token 的act
 *
 */
public class AuthTokenAct extends BaseActivity{
    AuthTokenAct context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.act_auth_token);
//        Dialog dialog = DialogUtil.showTradeLogin(this);
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                finish();
//            }
//        });
        DialogUtil.showTokenDialog(this, TradeConfig.getCurrentTradeCode(context), new DialogUtil.AuthTokenDlgShow() {
            @Override
            public void onDlgShow(Dialog dlg) {
                if (dlg == null)
                    return;
                dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
            }
        }, null);

    }


}
