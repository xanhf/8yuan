package com.trade.eight.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.easylife.ten.lib.R;
import com.trade.eight.moudle.me.activity.LoginActivity;

import java.util.List;
import java.util.Map;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by fangzhu on 2015/5/20.
 */
public class ActivityUtil extends OpenActivityUtil {
    private static final String YY_PACKAGE_NAME = "com.duowan.mobile";
    private static final String YY_CHANNEL_ACTIVITY_NAME = "com.yy.mobile.ui.channel.ChannelActivity";

    public static final String TAG = "ActivityUtil";

    public static boolean isInstallYY(Context context) {
        String packageName = YY_PACKAGE_NAME;
        // 获得包信息
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // 没安装YY
            return false;
        }
        if (packageInfo == null) {

            return false;
        }

        return true;
    }

    /**
     * 跳转到YY 直播页面
     *
     * @param sid
     * @param ssid
     * @throws PackageManager.NameNotFoundException
     */
    public static void startYYLive(Context context, long sid, long ssid) {
        String packageName = YY_PACKAGE_NAME;
        // 获得包信息
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // 没安装YY
            Toast.makeText(context.getApplicationContext(), "请安装YY语音后使用！", Toast.LENGTH_SHORT).show();
        }
        if (packageInfo == null) {
            Toast.makeText(context.getApplicationContext(), "请安装YY语音后使用！", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent();
            ComponentName cn = new ComponentName(packageInfo.packageName, YY_CHANNEL_ACTIVITY_NAME);
            intent.setComponent(cn);
            intent.putExtra("channel_sid", sid);
            intent.putExtra("channel_ssid", ssid);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context.getApplicationContext(), "请升级到最新版！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static Dialog createDialog(Context context, String title, String message, String positiveBtn, DialogInterface.OnClickListener positiveListener) {

        return createDialog(context, title, message, positiveBtn, null, positiveListener, null);
    }

    public static Dialog createDialog(Context context, String title, String message, String positiveBtn, String negativeBtn, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        Dialog dialog = null;
        AlertDialog.Builder dialogBuilder = null;
        if (Build.VERSION.SDK_INT >= 11) {
            dialogBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        } else {
            dialogBuilder = new AlertDialog.Builder(context);
        }
        if (negativeBtn == null) {
            dialog = dialogBuilder.setTitle(title).setIcon(R.drawable.liverooms_tips_icon_pressed)
                    .setMessage(message).setPositiveButton(positiveBtn, positiveListener).create();
        } else {
            dialog = dialogBuilder.setTitle(title).setIcon(R.drawable.liverooms_tips_icon_pressed)
                    .setMessage(message).setPositiveButton(positiveBtn, positiveListener).setNegativeButton(negativeBtn, negativeListener).create();
        }
        Window w = dialog.getWindow();
        w.setType(WindowManager.LayoutParams.TYPE_PHONE);
        WindowManager.LayoutParams attrs = w.getAttributes();
        w.setBackgroundDrawableResource(android.R.color.transparent);
        attrs.gravity = Gravity.CENTER;
        attrs.dimAmount = 0.5f;
        attrs.windowAnimations = android.R.style.Animation_InputMethod;
        w.setAttributes(attrs);

//		w.setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        w.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        w.setWindowAnimations(android.R.style.Animation_Translucent);
        return dialog;
    }

    /*
        callBackAct 跳转activity 根据OpenActivityUtil中定义action决定
        stringMap 跳转activity所需参数
     */
    public static Dialog createLoginDialog(final Activity context, String message, final int requestCode, final String callBackAct, final Map<String, String> stringMap, DialogInterface.OnClickListener negativeListener) {
        return createDialog(context, "提示信息", message, "登录", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(callBackAct)) {
                    Intent intent = OpenActivityUtil.initAction(context, LoginActivity.class, callBackAct, stringMap);
                    context.startActivity(intent);
                } else if (requestCode > 0) {
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.putExtra("startActivityForResult", requestCode);
                    context.startActivityForResult(intent, requestCode);
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }
            }
        }, negativeListener);
    }

    public static Dialog createLoginDialog(final Activity context, String message, final int requestCode) {
        return createLoginDialog(context, message, requestCode, null, null, null);
    }

    public static Dialog createLoginDialog(final Activity context, String message, final int requestCode, DialogInterface.OnClickListener negativeListener) {
        return createLoginDialog(context, message, requestCode, null, null, negativeListener);
    }

    public static Dialog createLoginDialog(final Activity context, String message) {
        return createLoginDialog(context, message, -1, null, null, null);
    }

    public static Dialog createLoginDialog(final Activity context, String message, String callBackAct, final Map<String, String> stringMap) {
        return createLoginDialog(context, message, -1, callBackAct, stringMap, null);
    }

    public static boolean isPkgInstalled(Context context, String packageName) {

        if (packageName == null || "".equals(packageName))
            return false;
        android.content.pm.ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void runAppByPackageName(Context context, String packageName) {
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.setPackage(pi.packageName);
            PackageManager pManager = context.getPackageManager();
            List<ResolveInfo> apps = pManager.queryIntentActivities(
                    resolveIntent, 0);
            ResolveInfo ri = apps.iterator().next();
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;
                Intent intent = new Intent(Intent.ACTION_MAIN);
                ComponentName cn = new ComponentName(packageName, className);
                intent.setComponent(cn);
                context.startActivity(intent);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 判断act是否运行在最顶层
     *
     * @param context
     * @return
     */
    public static boolean isActRunInTop(Context context, String className) {
        boolean isTop = false;
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            Log.v(TAG, "isActRunInTop = " + cn.getClassName());
            if (cn.getClassName() != null
                    && cn.getClassName().contains(className)) {
                isTop = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v(TAG, "isTop = " + isTop);
        return isTop;
    }


}
