package com.trade.eight.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;

/**
 * 这个类封装了loading动画
 */
public class AppLoadingImage extends RelativeLayout {

    /**
     * 播放的帧
     */
    private ImageView imgView;

    /**
     * 构造方法
     *
     * @param context context
     */
    public AppLoadingImage(Context context) {
        super(context);
        init(context);
    }


    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public AppLoadingImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    protected View createLoadingView(Context context) {
        View container = LayoutInflater.from(context).inflate(
                R.layout.layout_commloading, null);
        return container;
    }

    /**
     * 初始化
     *
     * @param context context
     */
    private void init(Context context) {
        addView(createLoadingView(context));
        imgView = (ImageView) findViewById(R.id.img_appload);
        AnimationDrawable animationDrawable = (AnimationDrawable) imgView.getDrawable();
        animationDrawable.start();
    }
}
