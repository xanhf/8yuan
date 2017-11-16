package com.trade.eight.tools.pay;

import android.content.Context;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.trade.eight.tools.StringUtil;

import org.json.JSONObject;

/**
 * Created by dufangzhu
 * 封装使用微信支付
 * WXPayEntryActivity 中监听支付结果
 */

public class WXPayUtil {

//    String jsonStr = "{\"sign\":\"7A1618E6A0E29FAA246CC76E7BBF258E\",\"timestamp\":\"1487829493\",\"noncestr\":\"228bb4ed138345f9a1e20e49557d0ca1\",\"partnerid\":\"21144452\",\"prepayid\":\"wx20170223135813ac00c55e6e0967994769\",\"package\":\"Sign=WXPay\",\"appid\":\"wx5f74d9afd2c19b86\"}";

//    {"sign":"3736EF94D3670CE0BDA68FE659082361",
//            "timestamp":"1489504677",
//            "noncestr":"d21b3d53c08447359df624179b109e37",
//            "partnerid":"1900008901",
//            "prepayid":"wx20170314232042248f14f3a40284636109",
//            "package":"Sign=WXPay",
//            "appid":"wx7105d0f953cb3fb1"}}

    /**
     * 支付
     *
     * @param context
     * @param api     IWXAPI
     * @param jsonStr 包含订单id等信息
     */
    public static void pay(Context context, IWXAPI api, String jsonStr) {
        try {
            if (StringUtil.isEmpty(jsonStr))
                return;
            JSONObject json = new JSONObject(jsonStr);
            PayReq req = new PayReq();
            req.appId = json.getString("appid");
            req.partnerId = json.getString("partnerid");
            req.prepayId = json.getString("prepayid");
            req.nonceStr = json.getString("noncestr");
            req.timeStamp = json.getString("timestamp");
            req.packageValue = json.getString("package");
            req.sign = json.getString("sign");
            req.extData = "app data"; // optional
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            api.sendReq(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
