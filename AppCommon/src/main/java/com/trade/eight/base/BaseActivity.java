package com.trade.eight.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.trade.eight.app.MyApplication;
import com.trade.eight.app.SystemBarTintManager;
import com.trade.eight.config.AppSetting;
import com.trade.eight.moudle.home.activity.LoadingActivity;
import com.trade.eight.moudle.home.activity.MainActivity;
import com.trade.eight.moudle.trade.TradeUserInfoData4Situation;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.OpenActivityUtil;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

/**
 * 输入法遮住输入框的问题 是fitsSystemWindows造成的
 * android:fitsSystemWindows="false"
 * 使用了SystemBarTintManager 造成的影响
 * 去掉就ok了
 * 然后 EditText 设置了 android:background=""属性也会遮住输入框
 */

@SuppressLint("NewApi")
public abstract class BaseActivity extends FragmentActivity {

    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";
    public static final String FAIL = "FAIL";
    public static LinkedList<Activity> sAllActivitys = new LinkedList<Activity>();
    @SuppressLint("SimpleDateFormat")
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    //系统导航栏的颜色
    int statusBarRes = R.color.black;
    protected Dialog loadingDlg = null;

    public static boolean isActive = true; //全局变量


    public static void finishAll() {
        for (Activity activity : sAllActivitys) {
            activity.finish();
        }
        sAllActivitys.clear();
    }

    /**
     * 统一加上 loading 样式
     */
//	public void setMyBaseContentView(View view) {
//		super.setContentView(R.layout.app_base);
//		RelativeLayout baseContent = (RelativeLayout) findViewById(R.id.baseContent);
//		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//				RelativeLayout.LayoutParams.MATCH_PARENT);
//		baseContent.addView(view, p);
//	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        sAllActivitys.add(this);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (Build.VERSION.SDK_INT >= 21) {
            updateStatusBarColor(getResources().getColor(getStatusBarTintResource()));
        } else {
            configSystemBar();
        }
        backListener();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        if (Build.VERSION.SDK_INT >= 21) {
            updateStatusBarColor(getResources().getColor(getStatusBarTintResource()));
        } else {
            configSystemBar();
        }
        backListener();
    }

    /**
     * 显示LoadingView,隐藏Content
     */
    public void showLoadingView() {
        View loadingView = findViewById(R.id.layoutLoding);
        if (loadingView != null)
            loadingView.setVisibility(View.VISIBLE);

        View baseContent = findViewById(R.id.appBaseContentView);
        if (baseContent != null)
            baseContent.setVisibility(View.GONE);

    }

    /**
     * 隐藏LoadingView，显示Content
     */
    public void hideLoadingView() {
        View loadingView = findViewById(R.id.layoutLoding);
        if (loadingView != null)
            loadingView.setVisibility(View.GONE);

        View baseContent = findViewById(R.id.appBaseContentView);
        if (baseContent != null)
            baseContent.setVisibility(View.VISIBLE);
    }

    /**
     * 设置title背景颜色
     * setContentView 之后调用
     *
     * @param color
     */
    public void setCommonTitleBgColor(int color) {
        View backLayout = findViewById(R.id.backLayout);
        if (backLayout != null) {
            backLayout.setBackgroundColor(color);
        }
    }

    /**
     * config current activity
     * fitsSystemWindows to  Override return false will not fitsSystemWindows
     * must used with xml contView
     *
     * @return
     */
    public boolean isActivityFitsSystemWindows() {
        return true;
    }

    public void configSystemBar() {
        try {
            if (!isActivityFitsSystemWindows())
                return;

            //fitsSystemWindows是否修改状态栏的总开关   使用style 达不到效果，只能在xml布局中使用fitsSystemWindows属性
            boolean flag = getResources().getBoolean(R.bool.fitsSystemWindows);
            if (!flag)
                return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setTranslucentStatus(true);
            }

            statusBarTintResource();


        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    /**
     * 状态栏颜色 或者图片
     *
     * @return
     */
    public int getStatusBarTintResource() {
        return statusBarRes;
    }

    public void setStatusBarTintResource(int res) {
        statusBarRes = res;
    }

    /**
     * Override this method to setStatusBarTintResource
     */
    public void statusBarTintResource() {
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(getStatusBarTintResource());
    }

    /**
     * for systemBar
     *
     * @param on
     */
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void backListener() {
        View gobackView = findViewById(R.id.gobackView);
        if (gobackView != null) {
            gobackView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appCommonGoBack();
                }
            });
        }
    }

    protected void appCommonGoBack() {
        doMyfinish();
    }

    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        if (MyApplication.getInstance().isApplicationBroughtToBackground(this)) {//app在后台运行)
            //app 进入后台
            isActive = false;//记录当前已经进入后台
            Log.i("ACTIVITY", "程序进入后台");
            TradeUserInfoData4Situation.getInstance(this, null).stopRefresh();
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        MyAppMobclickAgent.onPause(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        MyAppMobclickAgent.onResume(this);
        if (!isActive) {
            //app 从后台唤醒，进入前台
            isActive = true;
            TradeUserInfoData4Situation.getInstance(this, null).startRefresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 生成dialog
     *
     * @param loadingMsg
     * @return
     */
    public Dialog getmProgressDialog(String loadingMsg) {
        return DialogUtil.getLoadingDlg(this, loadingMsg);
    }

    public void showNetLoadingProgressDialog(String loadingMsg) {
        try {
            if (isFinishing())
                return;
            if (isProgressDialogShowing())
                return;
            loadingDlg = getmProgressDialog(loadingMsg);
            if (!isFinishing() && !loadingDlg.isShowing()) {
                loadingDlg.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isProgressDialogShowing() {
        return loadingDlg != null && loadingDlg.isShowing();
    }

    public void hideNetLoadingProgressDialog() {
        if (!isFinishing() && loadingDlg != null && loadingDlg.isShowing()) {
            loadingDlg.dismiss();
        }
    }

    public void intentTo(Context packageContext, Class<?> cls) {
        Intent i = new Intent();
        i.setClass(packageContext, cls);
        startActivity(i);
    }

    @SuppressLint("ShowToast")
    public void showCusToast(final String msg) {
//		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        BaseActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtil.toast(BaseActivity.this, msg);
            }
        });
    }

    public void showCusToast(String msg, boolean isLong) {
//		if (isLong)
//			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
//		else
//			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        DialogUtil.toast(this, msg);
    }

    /**
     * set common title text
     *
     * @param title
     */
    public void setAppCommonTitle(String title) {
        try {
            TextView title_tv = (TextView) findViewById(R.id.title_tv);
            if (title_tv != null)
                title_tv.setText(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doMyfinish() {
        if (OpenActivityUtil.isGoHome(this, getIntent())) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (AppSetting.getInstance(this).getIsTureNavi()) {
                intent.setClass(this, MainActivity.class);
            } else {
                intent.setClass(this, LoadingActivity.class);
            }
            startActivity(intent);
        }
        //finish 之前确保dialog 都是关闭的；解决问题has leaked window
        hideNetLoadingProgressDialog();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            doMyfinish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 更新StatusBar颜色
     *
     * @param color
     */
    public void updateStatusBarColor(int color) {
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.setStatusBarColor(color);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);

    }

//	private boolean isTopActivity(Activity activity) {
//		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//		return cn.getClassName().contains(activity.getClass().getName());
//	}


    /**
     * 重写getResources  保证改变系统字体大小不引起适配问题
     *
     * @return
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

}
