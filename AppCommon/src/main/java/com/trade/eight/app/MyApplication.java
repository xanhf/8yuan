package com.trade.eight.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.easylife.ten.lib.R;
import com.netease.nim.uikit.ImageLoaderKit;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.contact.ContactProvider;
import com.netease.nim.uikit.contact.core.query.PinYin;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderThumbBase;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MessageNotifierCustomization;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.trade.eight.moudle.chatroom.DemoCache;
import com.trade.eight.moudle.chatroom.util.SystemUtil;
import com.trade.eight.moudle.home.activity.LoadingActivity;
import com.trade.eight.moudle.push.PushActionUtil;
import com.trade.eight.moudle.push.entity.PushMsgObj;
import com.trade.eight.nim.SessionHelper;
import com.trade.eight.nim.preference.DemoUserPreferences;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.ShareTools;

import java.util.ArrayList;
import java.util.List;


public class MyApplication extends Application {
    public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String TAG = "MyApplication";
    private static final String SYNC_TAG = "SYNC_TAG";
    public static MyApplication application;

    public static Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
//        解决umeng统计错误，gradle中使用 multiDexEnabled true 一起使用
//        Could not find class 'com.umeng.analytics.d; 与netty使用 5.0 以上会出现
        MultiDex.install(this);

        Log.v(TAG, "onCreate");
        long start = System.currentTimeMillis();
        //微信分享 appkey appsecret
        ShareTools.initPlatfrom();

        Log.v(TAG, "start=" + start);
        initImageLoader(getApplicationContext());

        DemoCache.setContext(this);
        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        NIMClient.init(this, getLoginInfo(), getOptions());
        if (inMainProcess()) {
            // init pinyin
            PinYin.init(this);
            PinYin.validate();

            initUIKit();
        }

        if (handler == null) {
            handler = new MyHandler();
        }

        //日志记录 package/appLog
       /* if (BaseInterface.isDubug) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(this);
        }*/


        try {
            //7.0文件权限问题  编译时候使用 25
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        Log.v(TAG, "take time=" + (System.currentTimeMillis() - start));

    }

    private UserInfoProvider infoProvider = new UserInfoProvider() {
        @Override
        public UserInfo getUserInfo(String account) {
            UserInfo user = NimUserInfoCache.getInstance().getUserInfo(account);
            if (user == null) {
                NimUserInfoCache.getInstance().getUserInfoFromRemote(account, null);
            }

            return user;
        }

        @Override
        public int getDefaultIconResId() {
            return R.drawable.img_me_headdefault;
        }

        @Override
        public Bitmap getTeamIcon(String teamId) {
            /**
             * 注意：这里最好从缓存里拿，如果读取本地头像可能导致UI进程阻塞，导致通知栏提醒延时弹出。
             */
            // 从内存缓存中查找群头像
            Team team = TeamDataCache.getInstance().getTeamById(teamId);
            if (team != null) {
                Bitmap bm = ImageLoaderKit.getNotificationBitmapFromCache(team.getIcon());
                if (bm != null) {
                    return bm;
                }
            }

            // 默认图
            Drawable drawable = getResources().getDrawable(R.drawable.nim_avatar_group);
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }

            return null;
        }

        @Override
        public Bitmap getAvatarForMessageNotifier(String account) {
//            /**
//             * 注意：这里最好从缓存里拿，如果读取本地头像可能导致UI进程阻塞，导致通知栏提醒延时弹出。
//             */
//            UserInfo user = getUserInfo(account);
//            return (user != null) ? ImageLoaderKit.getNotificationBitmapFromCache(user.getAvatar()) : null;


            // 真正的通知栏图标
            Drawable drawable = getResources().getDrawable(R.drawable.app_icon);
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }

            return null;
        }

        @Override
        public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum sessionType) {
            String nick = null;
            if (sessionType == SessionTypeEnum.P2P) {
                nick = NimUserInfoCache.getInstance().getAlias(account);
            } else if (sessionType == SessionTypeEnum.Team) {
                nick = TeamDataCache.getInstance().getTeamNick(sessionId, account);
                if (TextUtils.isEmpty(nick)) {
                    nick = NimUserInfoCache.getInstance().getAlias(account);
                }
            }
            // 返回null，交给sdk处理。如果对方有设置nick，sdk会显示nick
            if (TextUtils.isEmpty(nick)) {
                return null;
            }

            return nick;
        }
    };
    private ContactProvider contactProvider = new ContactProvider() {
        @Override
        public List<UserInfoProvider.UserInfo> getUserInfoOfMyFriends() {
            List<NimUserInfo> nimUsers = NimUserInfoCache.getInstance().getAllUsersOfMyFriend();
            List<UserInfoProvider.UserInfo> users = new ArrayList<>(nimUsers.size());
            if (!nimUsers.isEmpty()) {
                users.addAll(nimUsers);
            }

            return users;
        }

        @Override
        public int getMyFriendsCount() {
            return FriendDataCache.getInstance().getMyFriendCounts();
        }

        @Override
        public String getUserDisplayName(String account) {
            return NimUserInfoCache.getInstance().getUserDisplayName(account);
        }
    };
    private MessageNotifierCustomization messageNotifierCustomization = new MessageNotifierCustomization() {
        @Override
        public String makeNotifyContent(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }

        @Override
        public String makeTicker(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }
    };

    public static MyApplication getInstance() {
        synchronized (SYNC_TAG) {
            if (null == application) {
                application = new MyApplication();
            }
            return application;
        }
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * 如果返回值为 null，则全部使用默认参数。
     * 设置sdk配置信息
     *
     * @return
     */
    private SDKOptions getOptions() {

        if (!BaseInterface.SWITCH_NIM_MSG_NOTF)
            return null;

        //下面是配置通知的代码，如果需要通知的话
        SDKOptions options = new SDKOptions();
        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。
        StatusBarNotificationConfig config = DemoUserPreferences.getStatusConfig();
        if (config == null) {
            config = new StatusBarNotificationConfig();
        }
        // 点击通知需要跳转到的界面
        config.notificationEntrance = LoadingActivity.class;
        config.notificationSmallIconId = R.drawable.app_icon;

        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://" + getApplicationContext().getPackageName() + "/raw/msg";

        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;

        options.statusBarNotificationConfig = config;
        DemoCache.setNotificationConfig(config);
        DemoUserPreferences.setStatusConfig(config);

        // 配置保存图片，文件，log等数据的目录
        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";
        options.sdkStorageRootPath = sdkPath;

        // 配置数据库加密秘钥
        options.databaseEncryptKey = "NETEASE";

        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小，
        options.thumbnailSize = MsgViewHolderThumbBase.getImageMaxEdge();

        // 用户信息提供者
        options.userInfoProvider = infoProvider;

        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
        options.messageNotifierCustomization = messageNotifierCustomization;

        return options;
    }

    private LoginInfo getLoginInfo() {

        return null;
    }

    private void initUIKit() {
        // 初始化，需要传入用户信息提供者
        NimUIKit.init(this, infoProvider, contactProvider);

        // 设置地理位置提供者。如果需要发送地理位置消息，该参数必须提供。如果不需要，可以忽略。
//		NimUIKit.setLocationProvider(new NimDemoLocationProvider());

        // 会话窗口的定制初始化。
        SessionHelper.init();

        // 通讯录列表定制初始化
//		ContactHelper.init();
    }

    public boolean inMainProcess() {
        String packageName = getPackageName();
        String processName = SystemUtil.getProcessName(this);
        return packageName.equals(processName);
    }


    public static void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            PushMsgObj pushMsgObj = (PushMsgObj) msg.obj;
            PushActionUtil.open(getApplicationContext(), pushMsgObj);
        }
    }

    /**
     *   * 需要权限:android.permission.GET_TASKS
     *   *
     *   * @param context
     *   * @return
     *   
     */
    public boolean isApplicationBroughtToBackground(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断应用是否已经启动
     * @param context 一个context
     * @param packageName 要判断应用的包名
     * @return boolean
     */
    public static boolean isAppAlive(Context context, String packageName){
        ActivityManager activityManager =
                (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos
                = activityManager.getRunningAppProcesses();
        for(int i = 0; i < processInfos.size(); i++){
            if(processInfos.get(i).processName.equals(packageName)){
                return true;
            }
        }
        return false;
    }
}
