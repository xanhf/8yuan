package com.trade.eight.moudle.auth.data;

import android.content.Context;

import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.auth.entity.CardAuthObj;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.NetWorkUtils;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.UploadFileUtil;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by dufangzhu on 2017/5/22.
 * 实名认证的数据请求
 */

public class AuthDataHelp {
    public static final String TAG = "AuthDataHelp";

    /**
     * 获取验证状态
     *
     * @param netCallback
     */
    public static void checkStatus(final BaseActivity context, final NetCallback netCallback) {
        HttpClientHelper.doGetOption(context, AndroidAPIConfig.URL_AUTH_STATUS, null, new NetCallback(context) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                if (netCallback != null) {
                    netCallback.onFailure(resultCode, resultMsg);
                }
            }

            @Override
            public void onResponse(String str) {
                if (netCallback != null) {
                    netCallback.onResponse(str);
                }
                CommonResponse<CardAuthObj> response = CommonResponse.fromJson(str,CardAuthObj.class);
                if (response != null
                        && response.isSuccess()
                        && response.getData() != null) {
                    if (new UserInfoDao(context).isLogin()) {
                        //更新本地数据库的数据
                        UserInfoDao dao = new UserInfoDao(context);
                        UserInfo u = dao.queryUserInfo();
                        u.setAuthStatus(response.getData().getStatus());
                        dao.addOrUpdate(u);
                    }
                }
            }
        }, false);
    }

    public static CardAuthObj getCheckStatus(BaseActivity baseActivity) {
        try {
            if (baseActivity == null)
                return null;
            UserInfoDao dao = new UserInfoDao(baseActivity);
            if (!dao.isLogin())
                return null;
            String response = HttpClientHelper.getStringFromGet(baseActivity, AndroidAPIConfig.URL_AUTH_STATUS, ApiConfig.getParamMap(baseActivity, null));
            CommonResponse<CardAuthObj> commonResponse = CommonResponse.fromJson(response, CardAuthObj.class);
            CardAuthObj cardAuthObj = commonResponse.getData();
            if (cardAuthObj != null) {
                UserInfo u = dao.queryUserInfo();
                u.setAuthStatus(cardAuthObj.getStatus());
                dao.addOrUpdate(u);
            }
            return cardAuthObj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * mark 顺序必须是 t token userId auth
     *
     * @param context
     * @param file
     * @param requestURL
     * @param callback
     */
    public static void upload(final Context context, final File file,
                              final String requestURL,
                              final UploadFileUtil.LoadCallBack callback) {
        //上传文件接口参数，后台验证固定的参数，参数不能增加
        Map<String, String> map = new LinkedHashMap<>();
        map.put(ApiConfig.PARAM_TIME, System.currentTimeMillis() + "");
        if (new UserInfoDao(context).isLogin()) {
            UserInfo userInfo = new UserInfoDao(context).queryUserInfo();
            map.put(ApiConfig.PARAM_TRADE_TOKEN, userInfo.getToken());
            map.put(UserInfo.UID, userInfo.getUserId());
        }
        map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, map));

        Log.v(TAG, "body=" + NetWorkUtils.getRequestData(map));
        UploadFileUtil uploadFileUtil = new UploadFileUtil();
        uploadFileUtil.uploadFile(context, file, requestURL, map, true, callback);
    }

    /**
     * 提交审核
     *
     * @param context
     * @param map             name idNo sex expiresStart expiresEnd
     * @param callback
     */
    public static void submit(BaseActivity context, Map<String, String> map, NetCallback callback) {
        HttpClientHelper.doGetOption(context, AndroidAPIConfig.URL_AUTH_SUBMIT, map, callback,false);
    }
}
