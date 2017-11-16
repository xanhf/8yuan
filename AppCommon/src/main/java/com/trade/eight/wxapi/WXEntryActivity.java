package com.trade.eight.wxapi;


import com.tencent.mm.sdk.modelbase.BaseResp;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

/**
 * Created by ntop on 15/9/4.
 */
public class WXEntryActivity extends WXCallbackActivity {

    @Override
    public void onResp(BaseResp resp) {
        try {
            if (resp == null)
                return;
            super.onResp(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
