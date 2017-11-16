package com.trade.eight.moudle.home.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.easylife.ten.lib.R;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.trade.eight.app.SystemContext;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppSetting;
import com.trade.eight.config.AppStartUpConfig;
import com.trade.eight.config.MarketConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.AdsObj;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.startup.StartupConfigObj;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.mpush.coreoption.CoreOptionUtil;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.NetWorkUtils;
import com.trade.eight.nim.SessionHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.JSONObjectUtil;
import com.trade.eight.service.OptionalService;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.OpenActivityUtil;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.StringUtil;
import com.umeng.analytics.AnalyticsConfig;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 每次启动读取本地记录
 * 本地缓存图片文件，点击链接，显示文字等
 * 每次启动都检查图片的url有没有变换，如果变化更新本地缓存
 */
public class LoadingActivity extends BaseActivity {
    String TAG = "LoadingActivity";
	private static int SHOW_TIME = 2000;//开机图显示的时间
	private long startTime, endTime;
	private ImageView welcomeImg ;
    LoadingActivity context = this;
    //缓存显示文字的key
    String KEY_TEXT = "LoadingActivity_TEXT";
    //缓存点击链接的key
    String KEY_LINK = "LoadingActivity_LINK";

    /**
     * 缓存开启图的文件夹
     */
    public File cacheFileDir = null;

    /**
     * 缓存开启图的本地文件名字
     */
    public static final String FILE_NAME_LOCAL_BG = "WELOCME_LOCAL_FILE_NAME";

    /**
     * 是否开启咨询 默认开启
     */
    boolean isShowNews = true;

    /*首次安装是否使用引导页*/
    public static final boolean isNav = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//        if ((AppSetting.getInstance(this).isSmallWindowFlag()) && !SmallWindowService.isAdd && BaseInterface.SWITCH_SMALL_WINDOW_NEED) {
//            this.startService(new Intent(this, SmallWindowService.class));
//        }

        Log.v(TAG, "market=" + AppSetting.getInstance(getApplicationContext()).getAppMarket());
//        String channel = AppSetting.getInstance(getApplicationContext()).getAppMarket();
//        AnalyticsConfig.setChannel(channel);

        //不显示通知了
        if (parseP2PIntent ()) {
            //if value is true;  finish current act
            return;
        }

        //开启配置信息
        AppStartUpConfig.getInstance(context).init(new AppStartUpConfig.OnConfigLoad() {
            @Override
            public void onConfigSuccess(final StartupConfigObj obj) {
                if (obj == null)
                    return;
                new Thread() {
                    @Override
                    public void run() {
                        //获取网络图片，需要异步处理
                        if (obj.getAndroid_startup_image() != null) {
                            saveBG(obj.getAndroid_startup_image());
                        }
                    }
                }.start();

            }
        });

        //开启netty
//        NettyClient nettyClient = NettyClient.getInstance(context);
//        nettyClient.init();
        //配置交易所列表
//        ExchangeConfig.getInstance().init(context, null);

        cacheFileDir = context.getFilesDir();

        long start = System.currentTimeMillis();
        Log.v(TAG, "start="+start);

        /*有些市场的导航页必须是自己的手机logo，小米 samsung必须是体现出自己的手机壳
        * 简单的处理是直接不显示导航页面
        * */
        if (MarketConfig.isMktUseNav(context)
                && isNav) {
            //手动开关开启并且第一次没有启动导航页
            if (!AppSetting.getInstance(context).getIsTureNavi()) {
                startActivity(new Intent(context, NaviActivity.class));
                finish();
                return;
            }
        }

        //检测交易开关  特殊渠道上传市场的时候 需要隐藏 然后再开启
//        WeipanConfig.checkSwitch(this);

		setContentView(R.layout.act_loading);

        hideBottomUIMenu();

        welcomeImg = (ImageView) findViewById(R.id.welcome_img) ;
        welcomeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击启动链接
                String text = PreferenceSetting.getString(context, KEY_TEXT);
                String link = PreferenceSetting.getString(context, KEY_LINK);
                if (StringUtil.isEmpty(link))
                    return;
                mHandle.removeMessages(MSG_ENTER_MAIN);
                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra("title", text);
                intent.putExtra("url", link);
                //标记返回首页
                intent.putExtra(OpenActivityUtil.IS_GOHOME, true);
                context.startActivity(intent);

            }
        });
        Drawable d = getLocalBG(context, FILE_NAME_LOCAL_BG);
        Log.v(TAG, "getLocalBG(context, FILE_NAME_LOCAL_BG) "+(d ==null));

        if (d != null) {
            welcomeImg.setImageDrawable(d);
        }
        //每次启动检测，如果有新图片下载保存，下一次启动使用本地已经下载的图片
//        getBGFromNET(AppSetting.APPSOURCE_TRADE);

        Log.v(TAG, "111take time="+(System.currentTimeMillis() - start));

		startTime = System.currentTimeMillis();


        /**
         * 如果需要获取开关  优先获取
         */
//        getSwitich ();


        //2.0的导航页有登录注册按钮  必须先初始化DB
        new Thread(){
            @Override
            public void run() {
                new OptionalService(context).initDb() ;
                SystemContext.getInstance(context);
            }
        }.start();
//        new OptionalService(context).initDb() ;
//        SystemContext.getInstance(context);

        Log.v(TAG, "222take time=" + (System.currentTimeMillis() - start));

        mHandle.sendEmptyMessageDelayed(MSG_ENTER_MAIN, SHOW_TIME);

        // 进入即建立长连接
        UserInfoDao userInfoDao = new UserInfoDao(LoadingActivity.this);
//        CoreOptionUtil.getCoreOptionUtil(getApplicationContext()).startPush("192.168.100.233:3000", userInfoDao.isLogin() ? userInfoDao.queryUserInfo().getUserId() : "0");
//        CoreOptionUtil.getCoreOptionUtil(getApplicationContext()).startPush("106.14.183.203:30000", userInfoDao.isLogin() ? userInfoDao.queryUserInfo().getUserId() : "0");
        CoreOptionUtil.getCoreOptionUtil(getApplicationContext()).startPush(AndroidAPIConfig.HOST_QUOTATION, userInfoDao.isLogin() ? userInfoDao.queryUserInfo().getUserId() : "0");

        Log.v(TAG, "take time="+(System.currentTimeMillis() - start));

	}

    @Override
    public boolean isActivityFitsSystemWindows() {
        return false;
    }

    public static final int MSG_ENTER_MAIN = 0;
    Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_ENTER_MAIN) {
                AppSetting.getInstance(context).setTureNavi(true);//no nav page, we set it true,if use nav page,just move this code.

                startActivity(new Intent(getApplicationContext(),
                        MainActivity.class).putExtra("isShowNews", isShowNews));
                context.finish();

                /*if (AppSetting.getInstance(context)
                        .getIsTureNavi()) {
                    context.finish();
                    startActivity(new Intent(getApplicationContext(),
                            MainActivity.class).putExtra("isShowNews", isShowNews));ng
                } else {
                    //放在NaviActivity中使用
//                    AppSetting.getInstance(context)
//                            .setTureNavi(true);
                    context.finish();
                    startActivity(new Intent(getApplicationContext(),
                            NaviActivity.class).putExtra("isShowNews", isShowNews));

                }*/
            }

        }
    };

    /**
     * 检查接口 图片是否更新
     * @param appsource
     */
    void getBGFromNET(int appsource) {
        final long i = System.currentTimeMillis();
        //开启过导航页了，就获取广告图片
        String url = AndroidAPIConfig.URL_ADS_APP;
        Map<String, String> map = ApiConfig.getCommonMap(context);
        map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, map));
        url = NetWorkUtils.setParam4get(url, map);

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    //获取接口信息
                    String res = HttpClientHelper.getStringFromGet(context, params[0]);
                    if (res == null)
                        return null;

                    CommonResponse<AdsObj> response = CommonResponse.fromJson(res, AdsObj.class);
                    if (response == null || !response.isSuccess())
                        return "";
                    AdsObj adsObj = response.getData();
                    if (adsObj == null)
                        return "";
                    saveBG(adsObj);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";

            }

            @Override
            protected void onPostExecute(String string) {
                super.onPostExecute(string);

            }
        }.execute(url);

    }

    /**
     * 缓存获取到的启动图信息
     * @param adsObj
     */
    public void saveBG (AdsObj adsObj) {
        try {
            String currentImgCacheKey = null, imgUrl = null;
            imgUrl = adsObj.getImageUrl();
            currentImgCacheKey = adsObj.getImageUrl();
            //缓存其他信息
            PreferenceSetting.setString(context, KEY_TEXT, ConvertUtil.NVL(adsObj.getText(), ""));
            PreferenceSetting.setString(context, KEY_LINK, ConvertUtil.NVL(adsObj.getLink(), ""));
            Log.v(TAG, "currentImgCacheKey=" + currentImgCacheKey + "  imgUrl=" + imgUrl);

            //是否需要更新图片到本地
            boolean isUpdate = true;
            //是否清除旧图片 若本地缓存有图片并且和接口是同一张 就使用缓存图片
            String imgCacheKey = context.getSharedPreferences("cache_img", 0).getString("welcom_img_cachekey", null);
            if (imgCacheKey != null && currentImgCacheKey != null && imgCacheKey.equals(currentImgCacheKey)) {
                isUpdate = false;
            }
            Log.v(TAG, "getLocalBG(context, FILE_NAME_LOCAL_BG) "+(getLocalBG(context, FILE_NAME_LOCAL_BG) ==null));
            Log.v(TAG, "isUpdate="+isUpdate);
            if (isUpdate || getLocalBG(context, FILE_NAME_LOCAL_BG) == null) {
                Log.v(TAG, "isUpdate ");
                clearImageCache(context, imgUrl);
                //获取图片字节流并用缓存
                boolean b = setLocalBG(context, imgUrl, FILE_NAME_LOCAL_BG);
                Log.v(TAG, "is saved " + b);
                if (b)
                    context.getSharedPreferences("cache_img", 0).edit().putString("welcom_img_cachekey", currentImgCacheKey).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

    public boolean clearImageCache (Context context, String fileName) {
        try {
            if (fileName == null || fileName.trim().length() == 0)
                return true;

                fileName = StringUtil.md5(fileName);
            File file = new File(cacheFileDir, fileName);
            if (file.isFile())
                return file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将网络图片缓存到本地
     * @param context
     * @param imageUrl 网络图片地址
     * @param localFileName 本地缓存的文件名
     */
    public boolean setLocalBG(Context context, String imageUrl, String localFileName) {
        try {
               if (imageUrl == null || imageUrl.trim().length() == 0)
                   return false;
                if (localFileName !=  null && localFileName.length() != 0) {
                    localFileName = StringUtil.md5(localFileName);
                }
                //欢迎界面 覆盖式缓存在本地
                File file = new File(cacheFileDir, localFileName);
                if (!file.isDirectory()) {
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        InputStream is = HttpClientHelper.getStreamFromGet(context, imageUrl);
                        if (is == null)
                            return false;
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        int len = 1024;
                        byte[] buff = new byte[len];
                        int data;
                        while ((data = is.read(buff,0,len)) != -1) {
                            bos.write(buff,0,data);
                        }
                        bos.close();
                        is.close();
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        file.delete();
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
        return false;
    }

    public Drawable getLocalBG (Context context, String fileName){
        Drawable drawable = null;
        try {
            if (fileName == null || fileName.trim().length() == 0)
                return null;

            fileName = StringUtil.md5(fileName);
            File file = new File(cacheFileDir, fileName);
            if (file.exists() && !file.isDirectory()) {
                try {
                    drawable = Drawable.createFromPath(file.toString());
                } catch (OutOfMemoryError error) {
                    error.printStackTrace();
                    System.gc();
                    Log.v(TAG, "getLocalBG OutOfMemoryError we try it again");
                    drawable = Drawable.createFromPath(file.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return drawable;
    }

    /**
     * 1 百度渠道才需要去获取
        2 5.20号以前才需要每次获取
     条件&&

     如果开启时间内没有获取到 开关默认开启

     */
    void getSwitich (){
        new Thread(){
            @Override
            public void run() {
                try {
                    List<String> list = new ArrayList<String>();
                    list.add(context.getResources().getString(R.string.market_baidu));
                    list.add(context.getResources().getString(R.string.market_taobao));
                    list.add(context.getResources().getString(R.string.market_huawei));

                    String market = AppSetting.getInstance(context).getAppMarket();
                    long currentTime = System.currentTimeMillis();
                    long lastOpenTime = DateUtil.parseTime4Long("2015-07-20 00:00:00");
                    if (list.contains(market) && lastOpenTime - currentTime > 0) {
                        Map<String, String> stringMap = new LinkedHashMap<String, String>();
                        stringMap.put(ApiConfig.PARAM_TIME, System.currentTimeMillis() + "");
                        stringMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, stringMap));
                        String res = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_SWITCH_SHOWNEWS, stringMap);
                        if (res != null) {
                            JSONObject jsonObject = new JSONObject(res);
                            isShowNews = JSONObjectUtil.getBoolean(jsonObject, "success", true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    /**
     * 云信聊天是 通知过来的时候直接转换的
     * @return
     */
    boolean parseP2PIntent () {
        try {
            Intent intent = getIntent();
            if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                //点对点是list
                ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
                //else 别的不是list
//                IMMessage message = (IMMessage) getIntent().getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
                if (messages == null)
                    return false;
                if (messages.size() == 0)
                    return false;
                IMMessage message = messages.get(0);

                switch (message.getSessionType()) {
                    case P2P:
                        /**
                         * 这里必须登录了才有用
                         * 是否别的客服发过来的消息 也显示
                         * 可在这里加判断
                         */
                        Log.v(TAG, ""+ NIMClient.getStatus());
                        SessionHelper.startP2PSession(this, message.getSessionId(), null);
                        break;
                    case Team:
                        //SessionHelper.startTeamSession(this, message.getSessionId());
                        break;
                    default:
                        break;
                }
                finish();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}
