package com.trade.eight.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

public class Utils {
    private static SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    public static String Date2String(Date date) {
        return sdf.format(date);
    }

    public static boolean checkNetWork(Context context) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            return false;
        }
        return true;
    }

    /**
     * @param number
     * @return
     */
    public static boolean isTelecomByNumber(String number) {

        List<String> list = Arrays.asList(new String[]{"189", "180", "181",
                "153", "133"});
        if (number != null && number.length() > 4) {
            return list.contains(number.substring(0, 3));
        }
        return false;
    }

    /**
     * 动态计算设置listview高度
     *
     * @param listView
     * @函数名 setListViewHeightBasedOnChildren
     * @功能 TODO
     * @备注 <其它说明>
     */
    public static void setListViewHeightBasedOnChildren(Context context,
                                                        ListView listView, boolean isOpening) {// 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (isOpening)
            totalHeight += (listAdapter.getCount() - 1)
                    * Utils.dip2px(context, 9);
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getWindowWidth(Activity cont) {
        DisplayMetrics metrics = new DisplayMetrics();
        cont.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;// 屏幕的宽dp
    }

    public static int getWindowHeight(Activity cont) {
        DisplayMetrics metrics = new DisplayMetrics();
        cont.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;// 屏幕的宽dp
    }

    /**
     * 显示输入法
     *
     * @param cont
     */
    public static void showSoftInput(Activity cont) {
        cont.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    /**
     * 隐藏输入法
     *
     * @param cont
     */
    public static void hideSoftInput(Activity cont) {
        cont.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                        | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public static void hideSoft(Activity cont, View view) {
        InputMethodManager imm = (InputMethodManager) cont
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 强制隐藏键盘
    }

    public static void showCusAlertDialog(Context activity, String title_id,
                                          String message_id, String negativeBtn, String positiveBtn,
                                          DialogInterface.OnClickListener negativeListener,
                                          DialogInterface.OnClickListener positiveListener) {
        AlertDialog mAlertDialog = null;
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            if (positiveBtn == null) {
                mAlertDialog = new AlertDialog.Builder(activity)
                        .setTitle(title_id).setMessage(message_id)
                        .setNegativeButton(negativeBtn, negativeListener)
                        .create();
            } else if (negativeBtn == null) {
                mAlertDialog = new AlertDialog.Builder(activity)
                        .setTitle(title_id).setMessage(message_id)
                        .setPositiveButton(positiveBtn, positiveListener)
                        .create();
            } else {
                mAlertDialog = new AlertDialog.Builder(activity)
                        .setTitle(title_id).setMessage(message_id)
                        .setPositiveButton(positiveBtn, positiveListener)
                        .setNegativeButton(negativeBtn, negativeListener)
                        .create();
            }
        } else {
            if (positiveBtn == null) {
                mAlertDialog = new AlertDialog.Builder(activity)
                        .setTitle(title_id).setMessage(message_id)
                        .setNegativeButton(negativeBtn, negativeListener)
                        .create();
            } else if (negativeBtn == null) {
                mAlertDialog = new AlertDialog.Builder(activity)
                        .setTitle(title_id).setMessage(message_id)
                        .setPositiveButton(positiveBtn, positiveListener)
                        .create();
            } else {
                mAlertDialog = new AlertDialog.Builder(activity)
                        .setTitle(title_id).setMessage(message_id)
                        .setPositiveButton(positiveBtn, positiveListener)
                        .setNegativeButton(negativeBtn, negativeListener)
                        .create();
            }
        }
        if (mAlertDialog.isShowing())
            mAlertDialog.dismiss();
        else
            mAlertDialog.show();
    }

    public static AlertDialog getCusAlertDialog(Context activity, String title_id,
                                                String message_id, String negativeBtn, String positiveBtn,
                                                DialogInterface.OnClickListener negativeListener,
                                                DialogInterface.OnClickListener positiveListener) {
        AlertDialog mAlertDialog = null;
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            if (positiveBtn == null) {
                mAlertDialog = new AlertDialog.Builder(activity)
                        .setTitle(title_id).setMessage(message_id)
                        .setNegativeButton(negativeBtn, negativeListener)
                        .create();
            } else if (negativeBtn == null) {
                mAlertDialog = new AlertDialog.Builder(activity)
                        .setTitle(title_id).setMessage(message_id)
                        .setPositiveButton(positiveBtn, positiveListener)
                        .create();
            } else {
                mAlertDialog = new AlertDialog.Builder(activity)
                        .setTitle(title_id).setMessage(message_id)
                        .setPositiveButton(positiveBtn, positiveListener)
                        .setNegativeButton(negativeBtn, negativeListener)
                        .create();
            }
        } else {
            if (positiveBtn == null) {
                mAlertDialog = new AlertDialog.Builder(activity)
                        .setTitle(title_id).setMessage(message_id)
                        .setNegativeButton(negativeBtn, negativeListener)
                        .create();
            } else if (negativeBtn == null) {
                mAlertDialog = new AlertDialog.Builder(activity)
                        .setTitle(title_id).setMessage(message_id)
                        .setPositiveButton(positiveBtn, positiveListener)
                        .create();
            } else {
                mAlertDialog = new AlertDialog.Builder(activity)
                        .setTitle(title_id).setMessage(message_id)
                        .setPositiveButton(positiveBtn, positiveListener)
                        .setNegativeButton(negativeBtn, negativeListener)
                        .create();
            }
        }
        return mAlertDialog;
    }

    public static String getCurrentVersion(Context cont) {
        String versionName = null;
        PackageManager pm = cont.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = pm.getPackageInfo(cont.getPackageName(), 0);
            if (packageInfo != null)
                versionName = packageInfo.versionName;
            return versionName;
        } catch (NameNotFoundException e) {
            return null;
        }
    }


    public static String getWindowWH(Context cont) {
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) cont).getWindowManager().getDefaultDisplay()
                .getMetrics(metric);
        int width = metric.widthPixels; // 屏幕宽度（像素）
        int height = metric.heightPixels; // 屏幕高度（像素）
        return width + "*" + height;
    }

    public static String getVersion(Context cont) {
        PackageManager packageManager = cont.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(
                    cont.getPackageName(), 0);
            return info != null ? info.versionName : "";
        } catch (NameNotFoundException e) {
            return "";
        }
    }

    // 访问类型(1：WAP，2：客户端，9：富媒体客户端 10：客户端4.0)
    public static String getNetworkType(Context cont) {
        ConnectivityManager cm = (ConnectivityManager) cont
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isAvailable() ? ni.getTypeName() : "NULL";
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {

        }
        return "";
    }

    public static String getAPNType(Context cont) {
        String netType = "NONE";
        ConnectivityManager connMgr = (ConnectivityManager) cont
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null) {
            int nType = networkInfo.getType();
            if (nType == ConnectivityManager.TYPE_MOBILE) {
                if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                    netType = "CMNET";
                } else {
                    netType = "CMWAP";
                }
            } else if (nType == ConnectivityManager.TYPE_WIFI) {
                netType = "WIFI";
            }
        }
        return netType;
    }

    public static boolean isSimAvailable(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        int simState = mTelephonyManager.getSimState();
        if (simState == TelephonyManager.SIM_STATE_READY) {
            return true;
        }
        return false;
    }

    public static String getMD5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            // System.out.println("result: " + buf.toString());//32位的加密

            // System.out.println("result: " +
            // buf.toString().substring(8,24));//16位的加密
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isAppExits(Context cont, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;

        final PackageManager packageManager = cont.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        List<String> pName = new ArrayList<String>();// 用于存储所有已安装程序的包名
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    public static String toDbc(String input) {
        if (input == null)
            return "";
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    public static void sendDial(Activity context, String forward,
                                int requestCode) {
        Uri uri = Uri.parse("tel:" + forward);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivityForResult(intent, requestCode);

    }

    public static void openUrl(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static boolean isExitSdCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }


    public static PopupWindow newBasicPopupWindow(Context context) {
        final PopupWindow window = new PopupWindow(context);

        // when a touch even happens outside of the window
        // make the window go away
        window.setTouchInterceptor(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    window.dismiss();
                    return true;
                }
                return false;
            }
        });

        window.setWidth(WindowManager.LayoutParams.FILL_PARENT);
        window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        window.setTouchable(true);
        window.setFocusable(true);
        window.setOutsideTouchable(true);

        window.setBackgroundDrawable(new BitmapDrawable());

        return window;
    }

    /**
     * 把所有的double类型的小数保留两位有效数字
     */
    public static double bigDecimalDoubleNum(double targer) {
        try {
            BigDecimal big = new BigDecimal(targer);
            return big.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static int getStatusHeight(Activity context) {
        int statusBarHeight = 0;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            Field field = clazz.getField("status_bar_height");
            int id = Integer.parseInt(field.get(object).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(id);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return statusBarHeight;
    }


    public static int getScreenW(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay()
                .getMetrics(metric);
        return metric.widthPixels;
    }

    public static int getScreenH(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay()
                .getMetrics(metric);
        return metric.heightPixels;
    }

    public static int getScreenH(Context context) {
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        return metric.heightPixels;
    }

    /**
     * 获取虚拟功能键高度
     * 主要是为华为手机  虚拟键盘挡住内容
     */
    public static int getVirtualBarHeigh(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }
}
