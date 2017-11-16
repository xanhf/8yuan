package com.trade.eight.config;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by Administrator on 2015/6/23.
 */
public class AppImageLoaderConfig {

    /**
     * @param context
     * @param drawableId loading drawable res id
     * @return
     */
    public static DisplayImageOptions getCommonDisplayImageOptions(Context context, int drawableId) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(drawableId)
                .showImageForEmptyUri(drawableId)
                .showImageOnFail(drawableId)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ALPHA_8)
//             bitmapConfig(Bitmap.Config.RGB_565)

                .build();
        return options;
    }

    /**
     * move res showImageOnLoading
     * showImageForEmptyUri
     * showImageOnFail
     *
     * @param context
     * @return
     */
    public static DisplayImageOptions getDisplayImageOptions(Context context) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ALPHA_8)
                .build();
        return options;
    }

    /**
     * 圆角
     *
     * @param context
     * @param drawableId loading drawable res id
     * @param round      Utils.dip2px(context, 5)
     * @return
     */
    public static DisplayImageOptions getRoundDisplayImageOptions(Context context, int drawableId, int round) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(drawableId)
                .showImageForEmptyUri(drawableId)
                .showImageOnFail(drawableId)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(round))
                .build();
        return options;
    }
}
