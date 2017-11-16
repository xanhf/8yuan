package com.trade.eight.moudle.floatvideo;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;

import com.trade.eight.base.BaseActivity;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.StringUtil;

import java.lang.reflect.Method;


/**
 * Created by dufangzhu on 2017/4/13.
 * 小窗口使用小米 魅族手机的权限
 */

public class FloatPermission {
    public static final String TAG = "FloatPermission";
    public static final String TAG_PERMISSION = "TAG_PERMISSION";
    BaseActivity context;
    // 最多允许显示两次
    int showCount = 2;

    public FloatPermission(BaseActivity context) {
        this.context = context;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11) {
            if (isFloatWindowOpAllowed(context)) {
                //已经开启
                Log.v(TAG, "开启悬浮窗成功");
            } else {
                Log.v(TAG, "开启悬浮窗失败");
            }
        } else if (requestCode == 12) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(context)) {
                    Log.v(TAG, "权限授予失败， 无法开启悬浮窗");
                }
            }
        }

    }


    /**
     * 判断悬浮窗权限
     * 部分系统可能不准
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isFloatWindowOpAllowed(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24);  // AppOpsManager.OP_SYSTEM_ALERT_WINDOW
        } else {
            if ((context.getApplicationInfo().flags & 1 << 27) == 1 << 27) {
                return true;
            } else {
                return false;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            try {
                AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                Class<?> spClazz = Class.forName(manager.getClass().getName());
                Method method = manager.getClass().getDeclaredMethod("checkOp", int.class, int.class, String.class);
                int property = (Integer) method.invoke(manager, op,
                        Binder.getCallingUid(), context.getPackageName());
                Log.e(TAG, " property: " + property);

                if (AppOpsManager.MODE_ALLOWED == property) {
                    Log.e(TAG, " checkOp: " + true);
                    return true;
                } else {
                    Log.e(TAG, " checkOp: " + false);
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Below API 19 cannot invoke!");
        }
        return false;
    }


    /**
     * 请求用户给予悬浮窗的权限
     */
    public void requestPermission() {
//        //如果检测到已经开启 不显示权限申请弹窗
        if (isFloatWindowOpAllowed(context))
            return;

        //最多打开showCount次设置界面，不管成功与否
        Log.v(TAG, StringUtil.getDeviceInfo());
        if (PreferenceSetting.getInt(context, TAG_PERMISSION) >= showCount)
            return;
        Log.v(TAG, "isMIUI=" + isMIUI(context));
        //这里设置只有小米手机或者系统小于4.4的手机
        if (Build.VERSION.SDK_INT <= 19
                || isMIUI(context)) {
            String msg = "权限申请";
            String content = "开启桌面悬浮窗就能小窗口看直播啦\n位置：权限管理/显示悬浮窗/允许";
            String neg = "取消";
            String post = "去设置";
            DialogUtil.getAllInfoMsgDlg(context, msg, content, neg, post, new DialogUtil.AuthTokenCallBack() {
                @Override
                public void onPostClick(Object obj) {
                    openSetting();
                    int size = PreferenceSetting.getInt(context, TAG_PERMISSION);
                    if (size < 0) {
                        size = 0;
                    }
                    PreferenceSetting.setInt(context, TAG_PERMISSION, (size + 1));
                }

                @Override
                public void onNegClick() {

                }
            }).show();
        }
    }

    /**
     * 小米手机
     *
     * @param context
     * @return
     */
    public static boolean isMIUI(Context context) {
        if (ConvertUtil.NVL(Build.MANUFACTURER, "").toLowerCase().contains("xiaomi")
                || ConvertUtil.NVL(Build.BRAND, "").toLowerCase().contains("xiaomi"))
            return true;
        return false;
    }

    /**
     * meizu
     *
     * @param context
     * @return
     */
    public static boolean isMEIZU(Context context) {
        if (ConvertUtil.NVL(Build.MANUFACTURER, "").toLowerCase().contains("meizu")
                || ConvertUtil.NVL(Build.BRAND, "").toLowerCase().contains("meizu"))
            return true;
        return false;
    }

    /**
     * 打开权限设置界面
     */
    public void openSetting() {
        try {
            Intent localIntent = new Intent(
                    "miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", context.getPackageName());
            context.startActivityForResult(localIntent, 11);
            Log.v(TAG, "启动小米悬浮窗设置界面");

        } catch (Exception ex) {
            try {
                ex.printStackTrace();
                Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent1.setData(uri);
                context.startActivityForResult(intent1, 11);
                Log.v(TAG, "启动悬浮窗界面");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
