package com.trade.eight.moudle.push;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.igexin.sdk.GTServiceManager;
import com.trade.eight.tools.Log;

/**
 * 核心服务, 继承 android.app.Service, 必须实现以下几个接口, 并在 AndroidManifest 声明该服务并配置成
 * android:process=":pushservice", 具体参考demo
 * PushManager.getInstance().initialize(this.getApplicationContext(), userPushService), 其中
 * userPushService 为 用户自定义服务 即 DemoPushService.
 */
public class AppPushCoreService extends Service {

    public static final String TAG = AppPushCoreService.class.getName();

    @Override
    public void onCreate() {
        // 该行日志在 release 版本去掉
        Log.v(TAG, TAG + " call -> onCreate -------");

        super.onCreate();
        GTServiceManager.getInstance().onCreate(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 该行日志在 release 版本去掉
        Log.v(TAG, TAG + " call -> onStartCommand -------");

        super.onStartCommand(intent, flags, startId);
        return GTServiceManager.getInstance().onStartCommand(this, intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 该行日志在 release 版本去掉
        Log.v(TAG, "onBind -------");
        return GTServiceManager.getInstance().onBind(intent);
    }

    @Override
    public void onDestroy() {
        // 该行日志在 release 版本去掉
        Log.v(TAG, "onDestroy -------");

        super.onDestroy();
        GTServiceManager.getInstance().onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        GTServiceManager.getInstance().onLowMemory();
    }
}
