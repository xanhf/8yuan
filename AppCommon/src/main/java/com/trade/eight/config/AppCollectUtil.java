package com.trade.eight.config;

import android.content.Context;
import android.os.AsyncTask;

import com.igexin.sdk.PushManager;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.TempObject;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.StringUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by fangzhu on 16/9/21.
 * 登录状态下，启动app收集一次，收集成功就不再请求
 * 启动之后 需要收集的配置信息
 */
public class AppCollectUtil {
    /**
     sourceId
     udid
     idfa
     deviceToken
     */
    //特别说明一下，注册的时候是这个字段收集imei
    public static final String P_idfa = "idfa";
    //启动就手机imei
    public static final String P_imei = "imei";
    public static final String P_userId = "userId";
    public static final String P_deviceToken = "deviceToken";
    public static final String TAG = "AppStartConfig";

//    version cid deviceInformation
    public static final String GETUI_P_VERSION = "version";
    public static final String GETUI_P_CID = "cid";
    public static final String GETUI_P_DEVICE = "deviceInformation";

    /**
     * 启动收集手机的imei，收集一次成功就不再收集
     * 登录了才收集
     * @param context
     */
    public static void collectIMEI (final Context context) {
        if (context == null)
            return;
        if (!new UserInfoDao(context).isLogin())
            return;
        final String userId = new UserInfoDao(context).queryUserInfo().getUserId();
        final String imei = StringUtil.getIMEI(context);
        if (StringUtil.isEmpty(imei))
            return;
        Log.v(TAG, "imei="+imei);
        //当前设备是否已经收集过了
        boolean isColllected = PreferenceSetting.getBoolean(context, TAG);
        if (isColllected)
            return;
        new AsyncTask<Void, Void, CommonResponse<TempObject>>() {
            @Override
            protected CommonResponse<TempObject> doInBackground(Void... voids) {
                try {
                    Map<String, String> map = new LinkedHashMap<String, String>();
                    map.put(P_userId, userId);
                    map.put(P_imei, imei);
                    map = ApiConfig.getParamMap(context, map);
                    String res = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_COLLECT_IMEI, map);
                    return CommonResponse.fromJson(res, TempObject.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<TempObject> response) {
                super.onPostExecute(response);
                if (response != null) {
                    if (response.isSuccess()) {
                        PreferenceSetting.setBoolean(context, TAG, true);
                    }
                }

            }
        }.execute();
    }

    /**
     * 必须登录才能使用
     * 1、cid收集个推推送需要的信息
     * 2、deviceInformation 收集手机信息 用来跟踪用户错误日志
     * @param context
     */
    public static void collectPushInfo (final Context context) {
        if (context == null)
            return;
        //个推cid
        final String clientid = PushManager.getInstance().getClientid(context);
        if (!StringUtil.isEmpty(clientid)) {
            Log.v(TAG, "clientid=" + clientid);
        } else {
            Log.v(TAG, "clientid=null");
            //为空就不收集
            return;
        }

        //必须登录
        if (!new UserInfoDao(context).isLogin())
            return;
        final String userId = new UserInfoDao(context).queryUserInfo().getUserId();

        new AsyncTask<Void, Void, CommonResponse<TempObject>>() {
            @Override
            protected CommonResponse<TempObject> doInBackground(Void... voids) {
                try {
                    Map<String, String> map = new LinkedHashMap<String, String>();
                    map.put(UserInfo.UID, userId);
                    map.put(GETUI_P_CID, clientid);
                    map.put(GETUI_P_DEVICE, StringUtil.getDeviceInfo());
                    map.put(GETUI_P_VERSION, AppSetting.getInstance(context).getAppVersion());

                    map = ApiConfig.getParamMap(context, map);
                    String res = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_COLLECT_GETUI_INFO, map);
                    return CommonResponse.fromJson(res, TempObject.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<TempObject> response) {
                super.onPostExecute(response);
                if (response != null) {
                    if (response.isSuccess()) {

                    }
                }

            }
        }.execute();
    }



}
