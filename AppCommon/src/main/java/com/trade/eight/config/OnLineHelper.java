package com.trade.eight.config;

import android.app.Activity;
import android.os.AsyncTask;

import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.NIMOnlineobj;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.chatroom.DemoCache;
import com.trade.eight.moudle.chatroom.LogoutHelper;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.nim.SessionHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.AESUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.StringUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by fangzhu
 * 在线客服帮助类
 * 获取云信 账号信息 然后点击启动点对点聊天功能
 * 这里不能缓存；每次点击客服  都是去后台接口获取在线客服的
 * <p>
 * 2016-12-08
 * 云信有两个账号
 * 1、客服聊天的云信账号
 * 2、直播室的云信账号
 * 修改逻辑
 * 1、每次启动登录的话，注销掉直播室的云信账号
 * 2、登录直播室的时候，注销掉客服聊天的账号
 * 3、在直播室的时候，不能收到和客服聊天的账号的消息
 *
 * 账号密码需要解密
 */
public class OnLineHelper {
    //打开客服界面
    public static final int TYPE_LOGIN_P2P = 0;
    //自动登录的时候打传给接口的参数
    public static final int TYPE_LOGIN_AUTO = 1;

    private static OnLineHelper ourInstance = new OnLineHelper();
    public static final String TAG = "OnLineHelper";
    private NIMOnlineobj nimOnlineobj;

    private OnLineHelper() {
    }

    public static OnLineHelper getInstance() {
        return ourInstance;
    }

    AsyncTask asyncTask;
    //是否正在登录云信
//    boolean isLoginingNIM = false;
    //确保用户ID 和当前的 NIMOnlineobj是对应的
    String currentUserId = null;


    /**
     * 1、首先获取云信登录信息
     * 请求参数:userId  uid  sourceId  market
     * 2、用云信登录信息 登录云信聊天系统
     * 3、登录成功后打开聊天界面
     *
     * @param context
     */
    public void startP2p(final BaseActivity context) {
        //重复点击
        if (asyncTask != null && asyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            return;
        }
        //重复点击
//        if (isLoginingNIM)
//            return;

        context.showNetLoadingProgressDialog(null);

        //缓存数据
//        //已经获取过接口信息了  确定是不是之前的用户获取的
//        if (new UserInfoDao(context).isLogin()) {
//            String userId = new UserInfoDao(context).queryUserInfo().getUserId();
//            if (currentUserId != null && !userId.equals(currentUserId)) {
//                nimOnlineobj = null;
//            }
//        }
//        if (nimOnlineobj != null) {
//            loginNIM(context, nimOnlineobj);
//            return;
//        }
        //第一次之后 重新获取
        asyncTask = new AsyncTask<Void, Void, CommonResponse<NIMOnlineobj>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected CommonResponse<NIMOnlineobj> doInBackground(Void... params) {
                return getCfg(context, TYPE_LOGIN_P2P);
            }

            @Override
            protected void onPostExecute(CommonResponse<NIMOnlineobj> response) {
                super.onPostExecute(response);
                if (response != null && response.isSuccess()) {
                    nimOnlineobj = response.getData();
                    loginNIM(context, response.getData(), false);
                } else {
                    String msg = "网络异常";
                    if (response != null)
                        msg = ConvertUtil.NVL(response.getErrorInfo(), "网络异常");
                    context.showCusToast(msg);
                    context.hideNetLoadingProgressDialog();
                }

            }
        }.execute();
    }

    /**
     * 异步去登录云信
     *
     * @param context
     */
    public void loginNIMAsync(final BaseActivity context) {
        new AsyncTask<Void, Void, CommonResponse<NIMOnlineobj>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected CommonResponse<NIMOnlineobj> doInBackground(Void... params) {
                return getCfg(context, TYPE_LOGIN_AUTO);
            }

            @Override
            protected void onPostExecute(CommonResponse<NIMOnlineobj> response) {
                super.onPostExecute(response);
                if (response != null && response.isSuccess()) {
                    nimOnlineobj = response.getData();
                    loginNIM(context, nimOnlineobj, true);
                }

            }
        }.execute();
    }

    /**
     * 回去云信账号
     *
     * @param context
     * @param loginType 区分自动登录
     * @return
     */
    public CommonResponse<NIMOnlineobj> getCfg(final Activity context, int loginType) {
        try {
            Map<String, String> map = new LinkedHashMap<String, String>();
            map.put("uid", StringUtil.getDeviceId(context));
            if (new UserInfoDao(context).isLogin()) {
                currentUserId = new UserInfoDao(context).queryUserInfo().getUserId();
                map.put(UserInfo.UID, currentUserId);
            }
            //区分是自动登录还是点击了客服登录
            map.put("loginType", loginType + "");
            map = ApiConfig.getParamMap(context, map);

            String res = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_GET_ONLINE_HELP, map);//sellectAll
            CommonResponse<NIMOnlineobj> response = CommonResponse.fromJson(res, NIMOnlineobj.class);
            if (response != null && response.isSuccess()) {
                nimOnlineobj = response.getData();
                //让云信退出
                if (NIMClient.getStatus() == StatusCode.LOGINED
                        && nimOnlineobj != null
                        && !isSameAccount(context, nimOnlineobj.getCustomerAccid())) {
                    //已经时候登录状态并且 不是同一个账号的话，执行退出操作
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            logoutCurrentAccount(context);
                        }
                    });
                    try {
                        //让云信退出
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param activity
     * @param obj      登录用户信息
     * @param isAsy    只登录  不回调
     */
    void loginNIM(final BaseActivity activity, final NIMOnlineobj obj, final boolean isAsy) {
        if (obj == null)
            return;
        String accId = null, token = null;
        try {
            accId = AESUtil.decrypt(obj.getCustomerAccid());
            token = AESUtil.decrypt(obj.getCustomerToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        LoginInfo loginInfo = new LoginInfo(accId, token);
        //需要回调的时候才需要记录标示
//        if (!isAsy)
//            isLoginingNIM = true;
        AbortableFuture<LoginInfo> loginRequest = NIMClient.getService(AuthService.class).login(loginInfo);

        loginRequest.setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                Log.v(TAG, "loginRequest onSuccess success");
                //不需要回调处理
                if (isAsy)
                    return;
//                isLoginingNIM = false;
                activity.hideNetLoadingProgressDialog();
                //打开聊天界面
                String toChatId = obj.getStaffAccid();
//                toChatId = "18600006666";//test
                //设置当前登录的账号
                try {
                    DemoCache.setAccount(AESUtil.decrypt(obj.getCustomerAccid()));
                    toChatId = AESUtil.decrypt(obj.getStaffAccid());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SessionHelper.startP2PSession(activity, toChatId, obj.getStaffName());

                //同步头像信息,客服的信息也设置用户的头像
//                if (new UserInfoDao(activity).isLogin()) {
//                    //使用登录的头像
//                    String avatar = new UserInfoDao(activity).queryUserInfo().getAvatar();
//                    avatar = UserInfo.getLargeAvatar(avatar);
//                    String hasUpdateAvatar = PreferenceSetting.getString(activity, "nim_update_userInfo_avater");
//                    if (!StringUtil.isEmpty(avatar) && !avatar.equals(hasUpdateAvatar)) {
//                        update(activity, UserInfoFieldEnum.AVATAR, avatar, null);
//                    }
//                }
            }

            @Override
            public void onFailed(int code) {
                Log.v(TAG, "loginRequest onFailed" + code);
                //不需要回调处理
                if (isAsy)
                    return;
//                isLoginingNIM = false;
                activity.hideNetLoadingProgressDialog();
                activity.showCusToast("获取客服信息失败 code=" + code);
            }

            @Override
            public void onException(Throwable exception) {
                Log.v(TAG, "loginRequest onException");
                //不需要回调处理
                if (isAsy)
                    return;
//                isLoginingNIM = false;
                activity.hideNetLoadingProgressDialog();
                activity.showCusToast("获取客服信息失败");
                exception.printStackTrace();

            }
        });
    }

    /**
     * 退出时候需要清除
     */
    public void clear() {
        nimOnlineobj = null;
    }

    /**
     * 更新用户资料
     */
    public static void update(final BaseActivity activity, final UserInfoFieldEnum field, final Object value, RequestCallbackWrapper<Void> callback) {
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
        fields.put(field, value);
        update(activity, fields, callback);
    }

    /**
     * 更新用户资料
     */
    private static void update(final BaseActivity activity, final Map<UserInfoFieldEnum, Object> fields, final RequestCallbackWrapper<Void> callback) {
        NIMClient.getService(UserService.class).updateUserInfo(fields).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void result, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS) {
                    if (fields.containsKey(UserInfoFieldEnum.AVATAR)) {
                        String avater = fields.get(UserInfoFieldEnum.AVATAR).toString();
                        PreferenceSetting.setString(activity, "nim_update_userInfo_avater", avater);
                    }
                    Log.v(TAG, "update userInfo success, update fields count=" + fields.size());
                } else {
                    if (exception != null) {
                        Log.v(TAG, "update userInfo failed, exception=" + exception.getMessage());
                    }
                }
                if (callback != null) {
                    callback.onResult(code, result, exception);
                }
            }
        });
    }


    /**
     * 登录的云信账号是不是同一个
     *
     * @param context
     * @return
     */
    public static boolean isSameAccount(Activity context, String uid) {
        try {
            if (NIMClient.getStatus() == StatusCode.LOGINED) {
                //已经登录了云信了
                if (DemoCache.getAccount() != null
                        && DemoCache.getAccount().equals(uid)) {
                    //认为是直播室的云信id登录了
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void logoutCurrentAccount(Activity context) {
        Log.v(TAG, "logoutCurrentAccount");
//        NIMClient.getService(AuthService.class).logout();
        LogoutHelper.logout();
    }
}