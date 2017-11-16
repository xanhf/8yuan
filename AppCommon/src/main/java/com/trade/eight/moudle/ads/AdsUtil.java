package com.trade.eight.moudle.ads;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.trade.eight.entity.startup.StartupObj;
import com.trade.eight.moudle.netty.NettyClient;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.nav.UNavConfig;

/**
 * Created by fangzhu on 16/11/18.
 * 广告的工具类
 */
public class AdsUtil {
    public static final String TAG = "AdsUtil";
    public static final String CASH_TIME_TAG = "LASTE_SHOW_TIME";
    //本地显示时间
    public static final long CHECK_TIME = 1 * 24 * 60 * 60 * 1000;

    public static void checkAds(final Activity context) {
        final StartupObj obj = NettyClient.getInstance(context).getStartupObj();
        if (obj == null)
            return;
        //接口开关是关闭的
        if (!obj.isShow())
            return;
        Log.v(TAG, "isShowHBDlg=" + UNavConfig.isShowHBDlg(context));
        //和大红包冲突的时候 不显示
        if (UNavConfig.isShowHBDlg(context)) {
            Log.v(TAG, "isShowHBDlg");
            return;
        }

        //图片地址没有
        if (StringUtil.isEmpty(obj.getImage()))
            return;
        long lastTime = PreferenceSetting.getLong(context, CASH_TIME_TAG);
        if (System.currentTimeMillis() - lastTime >= CHECK_TIME) {
            final String imgurl = obj.getImage().trim();
            //显示广告图
            final long l = System.currentTimeMillis();
            //先下载图片
            ImageLoader.getInstance().loadImage(imgurl, new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    if (loadedImage == null)
                        return;
                    Log.v(TAG, "viewW taketime="+(System.currentTimeMillis()-l)+"");
                    Log.v(TAG, "loadedImage "+loadedImage.getWidth()+" "+loadedImage.getHeight());
                    FullAdsAct.start(context, imgurl, obj.getTitle(), obj.getLink());
                    context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        }
    }

    /**
     * 是否加载ads
     * @param context
     * @return
     */
    public static final boolean isAdsEnable (Context context) {
        long lastTime = PreferenceSetting.getLong(context, CASH_TIME_TAG);
        if (System.currentTimeMillis() - lastTime >= CHECK_TIME) {
            return true;
        }
        return false;
    }
}
