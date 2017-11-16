package com.trade.eight.tools;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;

import com.easylife.ten.lib.R;
import com.trade.eight.config.AppSetting;
import com.trade.eight.view.pulltorefresh.PullToRefreshBase;

import java.util.Date;


/**
 * Created by fangzhu
 */
public class BaseInterface {
    /*是否开启debug模式*/
    public static boolean isDubug = false;



    //用true。依据产品是否停盘 弹出提示框，false： 不弹出提提示
    public static final boolean SWITCH_DEPEND_PRODUCT_CLOSE = false;
    public static final boolean SWITCH_SMALL_WINDOW_NEED = true;//是否使用小窗口功能
    public static final boolean SWITCH_KLINE_REFRESH = true;//K线是否开启刷新机制
    public static final String WX_APP_KEY = "";
    public static final String WX_APP_SECRET = "";
    //bugly的日志手机appid
    public static final String APPID_BUGLY = "d4df4cc1f6";
    //农交所的微信支付的appid
    public static final String WX_APP_ID_PAY_JN = "";
    //哈贵的微信支付appid
    public static final String WX_APP_ID_PAY_HG = "";


    //云信的客服聊天信息 是否显示通知
    public static final boolean SWITCH_NIM_MSG_NOTF = true;
    /*直播室是否显示 发送礼物btn*/
    public static final boolean SWITCH_NIM_CHATROOM_GIFT = false;
    //bugly的日志开关
    public static final boolean SWITCH_BUGLY = true;
    //首页需要切换的 tab 标示
    public static final String TAB_MAIN_PARAME = "tabTag";
    //viewpager 的第几页
    public static final String TAB_PARAME_INDEX = "pageTag";
    public static final String SCHEMA_QQ_START = "mqqwpa://im/chat?chat_type=wpa&uin={qq}";
    public static final String FORMART_REFRESH_TIME = "yyyy-MM-dd HH:mm";

    public static boolean isBakSource = true;//备用数据源

    public static boolean isShowHNVoucher = false;//是否展示华凝所代金券入口

    /**
     * 大图小图模式
     *
     * @param isLarge
     * @return
     */
    public static int getLoadingDrawable(Context context, boolean isLarge) {
        int drawableId = 0;
        try {
            if (isLarge)
                drawableId = R.drawable.img_loading_large;
            else
                drawableId = R.drawable.img_loading_large;

        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return drawableId;
    }

    /**
     * 大图小图模式
     *
     * @param isLarge
     * @return
     */
    public static Drawable getLoadingImg(Context context, boolean isLarge) {
        try {
            return context.getResources().getDrawable(getLoadingDrawable(context, isLarge));
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 是否开启百度网盟的统计
     * 仅仅支持百度网盟渠道开启
     *
     * @return
     */
    public static boolean isBaiduMon(Context context) {
        try {
            if (AppSetting.getInstance(context).getAppMarket().equalsIgnoreCase(context.getString(R.string.market_baidumon))) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void callQQ(Activity activity, String qq) {
        try {
            if (qq == null || qq.trim().length() == 0)
                return;
            String uriStr = BaseInterface.SCHEMA_QQ_START.replace("{qq}", qq.trim());
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriStr)));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(activity, "请安装QQ！", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "当前版本暂不支持！", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 统一下拉刷新的时间格式
     *
     * @param pull_refresh_list
     */
    public static void setPullFormartRefreshTime(PullToRefreshBase pull_refresh_list) {
        long currentTime = System.currentTimeMillis();
        pull_refresh_list.setLastUpdatedLabel(DateUtil.formatDate(new Date(currentTime), FORMART_REFRESH_TIME));

    }
}
