package com.trade.eight.moudle.floatvideo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.trade.eight.entity.live.LiveRoomNew;
import com.trade.eight.moudle.floatvideo.view.FloatVideoView;

/**
 * 视频悬浮窗口的Service
 */
public class FloatVideoService extends Service {

    private FloatVideoView mFloatView;

    @Override
    public IBinder onBind(Intent intent) {
        return new FloatViewServiceBinder();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mFloatView = new FloatVideoView(this);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        destroyFloat();
        mFloatView = new FloatVideoView(this);
    }

    /**
     * 释放video资源
     */
    public void releaseVideo() {
        if (mFloatView != null) {
            mFloatView.releaseVideo();
        }
    }

    public void showFloat() {
        if (mFloatView != null) {
            mFloatView.show();
        }
    }

    public void setData(LiveRoomNew data) {
        if (mFloatView != null) {
            mFloatView.setData(data);
        }
    }
    public void setVideoPath(String path) {
        if (mFloatView != null) {
            mFloatView.setVideoPath(path);
        }
    }

    public void hideFloat() {
        if (mFloatView != null) {
            mFloatView.hide();
        }
    }

    public void destroyFloat() {
        if (mFloatView != null) {
            mFloatView.destroy();
        }
        mFloatView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyFloat();
    }

    public class FloatViewServiceBinder extends Binder {
        public FloatVideoService getService() {
            return FloatVideoService.this;
        }
    }
}
