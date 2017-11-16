package com.trade.eight.mpush.coreoption;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.trade.eight.mpush.android.MPush;
import com.trade.eight.mpush.api.Constants;
import com.trade.eight.mpush.api.http.HttpCallback;
import com.trade.eight.mpush.api.http.HttpMethod;
import com.trade.eight.mpush.api.http.HttpRequest;
import com.trade.eight.mpush.api.http.HttpResponse;
import com.trade.eight.mpush.client.ClientConfig;
import com.easylife.ten.lib.BuildConfig;
import com.trade.eight.tools.Log;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/12.
 * 此类主要用作长连接的操作
 */

public class CoreOptionUtil {
    private String TAG = CoreOptionUtil.class.getSimpleName();
    Context applicationContext;
    public static CoreOptionUtil coreOptionUtil;

    private CoreOptionUtil(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static CoreOptionUtil getCoreOptionUtil(Context context) {
        if (coreOptionUtil == null) {
            coreOptionUtil = new CoreOptionUtil(context);
        }
        return coreOptionUtil;
    }

    /**
     * mpush初始化
     *
     * @param allocServer
     * @param userId
     */
    private void initPush(String allocServer, String userId) {
        //公钥有服务端提供和私钥对应
//        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCghPCWCobG8nTD24juwSVataW7iViRxcTkey/B792VZEhuHjQvA3cAJgx2Lv8GnX8NIoShZtoCg3Cx6ecs+VEPD2fBcg2L4JK7xldGpOJ3ONEAyVsLOttXZtNXvyDZRijiErQALMTorcgi79M5uVX9/jMv2Ggb2XAeZhlLD28fHwIDAQAB";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMid20vr929xXfIKMJ0pQCxlwT1o5XmBYvApzGu308QMkLrGLg1tP2egBgZ0v8kix2Iwt793UdvYm5XrOllQkGBSZsEEe88czNaCJ+kY+V9E1Uz9l+JT3trgwrFOtfYe3bwebfCbigixlbgFkJyD7nXTYi3Byfv/zN1NWtbjkbiQIDAQAB";

        ClientConfig cc = ClientConfig.build()
                .setPublicKey(publicKey)
                .setAllotServer(allocServer)
//                .setServerHost("114.55.108.28")
//                .setServerPort(3000)
                .setDeviceId(getDeviceId())
                .setClientVersion(BuildConfig.VERSION_NAME)
//                .setLogger(new MyLog(this, (EditText) findViewById(R.id.log)))
                .setLogger(new MyLog())
                .setLogEnabled(BuildConfig.DEBUG)
                .setEnableHttpProxy(true)
                .setUserId(userId);

        MPush.I.checkInit(applicationContext).setClientConfig(cc);
    }

    private String getDeviceId() {
        TelephonyManager tm = (TelephonyManager) applicationContext.getSystemService(Activity.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            String time = Long.toString((System.currentTimeMillis() / (1000 * 60 * 60)));
            deviceId = time + time;
        }
        return deviceId;
    }

    /**
     * 开启行情长连接
     *
     * @param codes
     */
    public void startQuotationMessage(String codes) {
        Log.e(TAG, codes);

        if (MPush.I.hasStarted()) {
            MPush.I.checkInit(applicationContext).sendQuotationMessage(codes);
        }

    }

    /**
     * 停止行情长连接
     * 传空即为停止当前行情刷新
     */
    public void stopQuotationMessage() {
        if (MPush.I.hasStarted()) {

        MPush.I.checkInit(applicationContext).sendQuotationMessage("");
        }

    }

    /**
     * 绑定用户
     *
     * @param userId
     */
    public void bindUser(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            MPush.I.bindAccount(userId, "mpush:" + (int) (Math.random() * 10));
        }
    }

    /**
     * 建立连接
     */
    public void startPush(String server, String userId) {
        initPush(server, userId);
        MPush.I.checkInit(applicationContext).startPush();
    }

    /**
     * 断开连接
     */
    public void stopPush() {
        MPush.I.stopPush();
    }

    /**
     * 停止连接
     */
    public void pausePush() {
        MPush.I.pausePush();
    }

    /**
     * 刷新连接
     */
    public void resumePush() {
        MPush.I.resumePush();
    }

    /**
     * 解绑用户
     */
    public void unbindUser() {
        MPush.I.unbindAccount();
    }
}
