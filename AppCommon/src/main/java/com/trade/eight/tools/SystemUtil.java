package com.trade.eight.tools;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.Locale;

/**
 * 作者：Created by ocean
 * 时间：on 16/11/14.
 * 获取系统信息
 */

public class SystemUtil {
    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return  语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号(如5.1)
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号(如22)
     */
    public static String getSystemVersionOfSDK() {
        return Build.VERSION.SDK;
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号(如mx5)
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return  手机厂商(如meizu)
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return  手机IMEI
     */
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getDeviceId();
        }
        return null;
    }
}
