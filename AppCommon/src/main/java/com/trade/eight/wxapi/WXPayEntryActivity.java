package com.trade.eight.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.trade.eight.moudle.trade.CashInEvent;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.trade.TradeConfig;

import de.greenrobot.event.EventBus;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String appId = BaseInterface.WX_APP_KEY;
        if (TradeConfig.isCurrentJN(this)) {
            appId = BaseInterface.WX_APP_ID_PAY_JN;
        }
        if (TradeConfig.isCurrentHG(this)) {
            appId = BaseInterface.WX_APP_ID_PAY_HG;
        }
        api = WXAPIFactory.createWXAPI(this, appId);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
//        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

        try {
            if (resp == null)
                return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            // resp.errCode == -1 原因：支付错误,可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
            if (resp.errCode == 0) // 支付成功
            {
                Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
                finish();
                EventBus.getDefault().post(new CashInEvent(true));
            } else {
                Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}