package com.trade.eight.moudle.chatroom;

import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.trade.eight.config.AppStartUpConfig;
import com.trade.eight.moudle.chatroom.helper.ChatRoomMemberCache;

import static com.igexin.sdk.GTServiceManager.context;

/**
 * 注销帮助类
 * Created by huangjun on 2015/10/8.
 */
public class LogoutHelper {
    public static void logout() {
        // 清理缓存&注销监听&清除状态
        NimUIKit.clearCache();
//        ChatRoomHelper.logout();
        NIMClient.getService(AuthService.class).logout();
        ChatRoomMemberCache.getInstance().registerObservers(false);
        ChatRoomMemberCache.getInstance().clear();
        DemoCache.clear();
        LoginSyncDataStatusObserver.getInstance().reset();

        //开启配置信息
        AppStartUpConfig.getInstance(context).init();
    }
}
