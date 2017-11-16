package com.trade.eight.net.okhttp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.easylife.ten.lib.R;
import com.google.gson.Gson;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.response.CommonResponseBase;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.moudle.trade.activity.CheckBalanceAct;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.trude.UserService;
import com.trade.eight.tools.AppDataCache;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public abstract class NetCallback implements Callback {
    String TAG = "NetCallback";

    public String resString;//接口返回的字符串

    BaseActivity baseActivity;

    public NetCallback(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
    }

    @Override
    public final void onFailure(final Call request, final IOException e) {
        if (baseActivity.isFinishing()) {
            return;
        }
        baseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                baseActivity.hideNetLoadingProgressDialog();
                onFailure("404", "网络连接失败");
            }
        });
    }

    @Override
    public final void onResponse(Call request, final Response response) throws IOException {
        if (baseActivity.isFinishing()) {
            return;
        }
        try {
            if (response.code() == 200) {
                resString = response.body().string();
                // 输出访问数据
                Log.e(TAG, resString);
                //设置cookie
                String cookie = response.header("Set-Cookie");
                if (!TextUtils.isEmpty(cookie)) {
                    HttpClientHelper.setCookie(cookie);
                }
                //TODO user对象预处理
                //如果对象未继承ResponseBase或者JSONObject，则无法统一处理1024错误
                final CommonResponseBase resObj = new Gson().fromJson(resString, CommonResponseBase.class);

                baseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 网络访问菊花消失
                        baseActivity.hideNetLoadingProgressDialog();
                        // 装载消息数据
                        if (resObj.success) {
                            onResponse(resString);
                        } else {
                            if (resObj.getErrorCode().equals(ApiConfig.ERROR_CODE_RELOGIN)) {
                                Dialog reLoginDialog = AppDataCache.getDataCache().getReLoginDialog();
                                if (reLoginDialog != null && reLoginDialog.isShowing()) {
                                    return;
                                }
                                Dialog dialog = DialogUtil.showTitleAndContentDialog(baseActivity,
                                        baseActivity.getResources().getString(R.string.singlelogin_title),
                                        baseActivity.getResources().getString(R.string.singlelogin_content),
                                        baseActivity.getResources().getString(R.string.cancel),
                                        baseActivity.getResources().getString(R.string.singlelogin_relogin),
                                        null,
                                        new Handler.Callback() {
                                            @Override
                                            public boolean handleMessage(Message message) {

                                                new UserService(baseActivity).loginOut();
                                                //直接跳转到登录页面
                                                Intent intent = new Intent(baseActivity, LoginActivity.class);
                                                baseActivity.startActivity(intent);
                                                return false;
                                            }
                                        });
                                AppDataCache.getDataCache().setReLoginDialog(dialog);
                            } else if(resObj.getErrorCode().equals(ApiConfig.ERROR_CODE_CONFIRMBALANCE)){
                                //  需要确认账单
                                CheckBalanceAct.startAct(baseActivity);
                            }else{
                                onFailure(resObj.getErrorCode(), ConvertUtil.NVL(resObj.getErrorInfo(),"网络连接失败") );
                            }
                        }
                    }
                });
            } else {
                //TODO
                baseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 网络访问菊花消失
                        baseActivity.hideNetLoadingProgressDialog();
                        onFailure(ApiConfig.CODE_NET_ERROR, "网络连接失败");//拋404错误
                    }
                });
            }
        } catch (final Exception e) {
            e.printStackTrace();
            baseActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onFailure(ApiConfig.CODE_NET_ERROR, "网络连接失败");//拋404错误
                }
            });
        }
    }

    public abstract void onFailure(String resultCode, String resultMsg);

    public abstract void onResponse(String response);
}
