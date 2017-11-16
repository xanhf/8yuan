package com.trade.eight.config;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.trade.eight.entity.startup.MarketVersionObj;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.JSONObjectUtil;
import com.trade.eight.tools.ConvertUtil;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/10/9.
 * 新配置
 * 每次都是网络获取
 * 得到版本号 和 需要 特殊渠道list
 * 1、相同版本 才处理
 * 2、是特殊渠道才处理
 * 1、特殊渠道的包
 * 3、是用isShowWeipan判断app是否开启入口
 * 退出app 设置是用isShowWeipan 为false
 */
public class WeipanConfig {
    //默认值
    public static final boolean ENABLE_DEFAULT = true;
    //是否检查 本地开关 如果本地开关关闭 ，就不去网络获取开关
    public static final boolean ISCHECK = true;

    /**
     * {
     * enable: true,
     * version: "1.0.1"
     * androidMarket:"main,other"
     * }
     * 只需要 version  和本地一样 就关闭，不需要判断enable
     *
     * @param context
     * @return true 显示
     */
    public static boolean getSwitch(Context context) {
        try {
            //test for debug
//            Thread.sleep(3000);


            String res = HttpClientHelper.getStringFromGet(context, AndroidAPIConfig.URL_WEIPAN_SWITCH);
            if (res != null) {
                JSONObject object = new JSONObject(res);
                String versionName = JSONObjectUtil.getString(object, "version", "").trim();
                //版本相同才处理，否则返回默认开关
                if (versionName.equals(getVersionName(context))) {
                    //解析市场
                    String androidMarket = JSONObjectUtil.getString(object, "androidMarket", "").trim();
                    if (androidMarket != null) {
                        if (androidMarket.contains(",")) {
                            String string[] = androidMarket.split(",");
                            for (String s : string) {
                                //相同版本相同渠道  关闭
                                if (s != null && s.trim().equals(AppSetting.getInstance(context).getAppMarket()))
                                    return false;
                            }
                        } else {
                            if (androidMarket != null && androidMarket.trim().equals(AppSetting.getInstance(context).getAppMarket()))
                                return false;
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ENABLE_DEFAULT;
    }

    /**
     * 是特殊渠道 才取开关
     * 在开启app的地方获取
     *
     * @param activity
     */
//    public static void checkSwitch(final Activity activity) {
//
//        //如果本地不需要检查
//        if (!ISCHECK)
//            return;
//        new AsyncTask<Void, Void, Boolean>() {
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//
//            @Override
//            protected Boolean doInBackground(Void... params) {
//                return getSwitch(activity);
//            }
//
//            @Override
//            protected void onPostExecute(Boolean aBoolean) {
//                super.onPostExecute(aBoolean);
//                if (aBoolean == null)
//                    return;
//                if (activity.isFinishing())
//                    return;
//                isShowWeipan = aBoolean.booleanValue();
//            }
//        }.execute();
//    }

    /**
     * just get versionName
     * 不加小版本号 方便统一关闭
     *
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            if (packageInfo != null)
                versionName = packageInfo.versionName;
            return versionName;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 微盘的开关
     * @param context
     * @return
     */
    public static boolean isShowWeipan(Context context) {
        if (!ISCHECK)
            return ENABLE_DEFAULT;
        if (AppStartUpConfig.getInstance(context).getStartupConfigObj() == null)
            return ENABLE_DEFAULT;
        if (AppStartUpConfig.getInstance(context).getStartupConfigObj().getAndriod_version() == null)
            return ENABLE_DEFAULT;
        MarketVersionObj obj = AppStartUpConfig.getInstance(context).getStartupConfigObj().getAndriod_version();

        String versionName = ConvertUtil.NVL(obj.getVersion(), "").trim();
        if (versionName == null)
            return false;
        //市场
        String androidMarket = ConvertUtil.NVL(obj.getAndroidMarket(), "").trim();
        //版本相同才处理，否则返回默认开关
        if (versionName.equals(getVersionName(context))) {
            if (androidMarket != null) {
                if (androidMarket.contains(",")) {
                    String string[] = androidMarket.split(",");
                    for (String s : string) {
                        //相同版本相同渠道  关闭
                        if (s != null && s.trim().equals(AppSetting.getInstance(context).getAppMarket()))
                            return false;
                    }
                } else {
                    if (androidMarket != null && androidMarket.trim().equals(AppSetting.getInstance(context).getAppMarket()))
                        return false;
                }
            }
        }
        return ENABLE_DEFAULT;
    }
}
