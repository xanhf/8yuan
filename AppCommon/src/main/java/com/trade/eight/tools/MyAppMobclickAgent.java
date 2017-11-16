package com.trade.eight.tools;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by fangzhu on 2015/5/29.
 */
public class MyAppMobclickAgent {


    public static void onPageStart(String s) {
        if (s != null)
            s = s.trim();
        MobclickAgent.onPageStart(s);
    }

    public static void onPageEnd(String s) {
        if (s != null)
            s = s.trim();
        MobclickAgent.onPageEnd(s);
    }


    public static void onPause(Context context) {
        MobclickAgent.onPause(context);
    }

    public static void onResume(Context context) {
        MobclickAgent.onResume(context);
    }

    public static void onResume(Context context, String s, String s1) {
        if (s != null)
            s = s.trim();
        if (s1 != null)
            s1 = s1.trim();
        MobclickAgent.onResume(context, s, s1);
    }

    public static void reportError(Context context, String s) {
        MobclickAgent.reportError(context, s);
    }

    public static void onEvent(Context context, String s) {
        if (s != null)
            s = s.trim();
        MobclickAgent.onEvent(context, s);
    }

    public static void onEvent(Context context, String s, String s1) {
        if (s != null)
            s = s.trim();
        if (s1 != null)
            s1 = s1.trim();
        MobclickAgent.onEvent(context, s, s1);
    }

    public static void onEventBegin(Context context, String s) {
        if (s != null)
            s = s.trim();
        MobclickAgent.onEventBegin(context, s);
    }

    public static void onEventEnd(Context context, String s) {
        if (s != null)
            s = s.trim();
        MobclickAgent.onEventEnd(context, s);
    }

    public static void onEventBegin(Context context, String s, String s1) {
        if (s != null)
            s = s.trim();
        if (s1 != null)
            s1 = s1.trim();
        MobclickAgent.onEventBegin(context, s, s1);
    }

    public static void onEventEnd(Context context, String s, String s1) {
        if (s != null)
            s = s.trim();
        if (s1 != null)
            s1 = s1.trim();
        MobclickAgent.onEventEnd(context, s, s1);
    }


}
