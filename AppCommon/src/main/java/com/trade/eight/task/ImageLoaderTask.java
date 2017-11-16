package com.trade.eight.task;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.moudle.listener.FileLoadListener;
import com.trade.eight.tools.BitMapUtil;
import com.trade.eight.tools.FileOperator;

import java.io.File;

public class ImageLoaderTask {

    TackCallBack tackCallBack = null;
    private Context context;

    public ImageLoaderTask(Context context) {
        this.context = context;
    }

    public TackCallBack getTackCallBack() {
        return tackCallBack;
    }

    public void setTackCallBack(TackCallBack tackCallBack) {
        this.tackCallBack = tackCallBack;
    }

    public void showoImage(final ImageView imgIcon, final String url, final int newWidth, final int newHeight) {
        File file = FileOperator.getImageByUrl(context, url);
        if (file.exists()) {
            // 将图片显示到ImageView中
            BitMapUtil.showBitmap(imgIcon, file.getAbsolutePath(), newWidth, newHeight);
            if (tackCallBack != null)
                tackCallBack.onCallBack();
        } else {
            new DownLoadFileTask(context).downloadImage(url,
                    new FileLoadListener() {
                        @Override
                        public void onLoadingStarted() {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onLoadingFailed() {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onLoadingComplete() {
                            // TODO Auto-generated method stub
                            File file = FileOperator.getImageByUrl(context, url);
                            if (file.exists()) {
                                BitMapUtil.showBitmap(imgIcon, file.getAbsolutePath(), newWidth, newHeight);
                            }
                            if (tackCallBack != null)
                                tackCallBack.onCallBack();
                        }

                        @Override
                        public void onLoadingCancelled() {
                            // TODO Auto-generated method stub
                        }
                    });
        }
    }

    public void showoImage(final ImageView imgIcon, final String url, final int newWidth, final int newHeight, final ImageLoader imageLoader, final DisplayImageOptions options) {

        if (url == null)
            return;

        File file = FileOperator.getImageByUrl(context, url);
        if (file.exists()) {
            // 将图片显示到ImageView中
//			BitMapUtil.showBitmap(imgIcon, file.getAbsolutePath(), newWidth, newHeight);
            String uri = "file://" + file.getAbsolutePath();
            imageLoader.displayImage(uri, imgIcon, options);

        } else {
            new DownLoadFileTask(context).downloadImage(url,
                    new FileLoadListener() {
                        @Override
                        public void onLoadingStarted() {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onLoadingFailed() {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onLoadingComplete() {
                            // TODO Auto-generated method stub
                            File file = FileOperator.getImageByUrl(context, url);
                            if (file.exists()) {
//						BitMapUtil.showBitmap(imgIcon, file.getAbsolutePath(), newWidth, newHeight);
                                String uri = "file://" + file.getAbsolutePath();
                                imageLoader.displayImage(uri, imgIcon, options);
                            }
                        }

                        @Override
                        public void onLoadingCancelled() {
                            // TODO Auto-generated method stub
                        }
                    });
        }
    }


    public interface TackCallBack {
        void onCallBack();
    }


}
