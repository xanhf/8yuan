package com.trade.eight.moudle.ads;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.Utils;

/**
 * Created by fangzhu on 16/11/18.
 * 全屏显示广告， 根据图片大小适配
 */
public class FullAdsAct extends BaseActivity {
    public static final String TAG = "FullAdsAct";
    ImageView imgAds, imgClose;
    FullAdsAct context = this;
    String imgurl, link, title;

    public static void start(Context context, String imgurl, String title, String link) {
        Intent intent = new Intent(context, FullAdsAct.class);
        intent.putExtra("url", imgurl);
        intent.putExtra("title", title);
        intent.putExtra("link", link);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        title = getIntent().getStringExtra("title");
        imgurl = getIntent().getStringExtra("url");
        link = getIntent().getStringExtra("link");

        setContentView(R.layout.act_full_ads);
        imgAds = (ImageView) findViewById(R.id.imgAds);
        imgClose = (ImageView) findViewById(R.id.imgClose);

        final long l = System.currentTimeMillis();
        ImageLoader.getInstance().loadImage(imgurl, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                try {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    if (loadedImage == null)
                        return;

                    int viewW = loadedImage.getWidth(), viewH = loadedImage.getHeight();
                    int screenW = Utils.getScreenW(context);
                    int screenH = Utils.getScreenH(context);
                    //720:screenW = x:viewW
                    //宽度超过屏幕
                    if (viewW > screenW) {
                        viewW = screenW;
                    }

                    //loadedImage.getWidth():loadedImage.getHeight()=viewW:viewH
                    int myImageh = viewH * viewW / viewH;
                    if (viewH > screenH) {
                        //高度超过屏幕
                        if (myImageh < screenH)
                            viewH = myImageh;
                        else
                            viewH = screenW;
                    }
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(viewW, viewH);
                    imgAds.setLayoutParams(params);

                    imgAds.setImageBitmap(loadedImage);
                    PreferenceSetting.setLong(context, AdsUtil.CASH_TIME_TAG, System.currentTimeMillis());
                    Log.v(TAG, "viewW taketime="+(System.currentTimeMillis()-l)+"");
                    Log.v(TAG, "loadedImage "+loadedImage.getWidth()+" "+loadedImage.getHeight());
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                finish();
            }
        });


        imgAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                WebActivity.start(context, title, link);

            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);// 淡出淡入动画效果
    }
}
