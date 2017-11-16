package com.trade.eight.moudle.floatvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.easylife.ten.lib.R;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoView;
import com.trade.eight.entity.live.LiveRoomNew;
import com.trade.eight.moudle.chatroom.activity.ChatRoomActivity;
import com.trade.eight.moudle.home.live.LiveDatahelp;
import com.trade.eight.moudle.player.PlayerUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.Utils;


/**
 * 视屏悬浮窗控件
 */
public class FloatVideoView extends FrameLayout implements OnTouchListener {
    public static final String TAG = "FloatVideoView";
    private final int HANDLER_TYPE_HIDE_LOGO = 100;//隐藏LOGO
    private final int HANDLER_TYPE_CANCEL_ANIM = 101;//退出动画

    private WindowManager.LayoutParams mWmParams;
    private WindowManager mWindowManager;
    private Context mContext;


    private boolean mIsRight;//logo是否在右边
    private boolean mCanHide;//是否允许隐藏
    private float mTouchStartX;
    private float mTouchStartY;
    private int mScreenWidth;
    private int mScreenHeight;
    /*首页的底部高度*/
    private int home_tab_h;
    /*小窗口的高度*/
    private int float_h;
    /*y方向的最大坐标*/
    private int maxY;
    private boolean mDraging;
    private boolean mShowLoader = true;

    final Handler mTimerHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_TYPE_HIDE_LOGO) {
                // 比如隐藏悬浮框
                if (mCanHide) {
                    mCanHide = false;
                    if (mIsRight) {

                    } else {

                    }
                    mWmParams.alpha = 0.7f;
                    mWindowManager.updateViewLayout(FloatVideoView.this, mWmParams);


                }
            } else if (msg.what == HANDLER_TYPE_CANCEL_ANIM) {

                mShowLoader = false;
            }
            super.handleMessage(msg);
        }
    };

    public FloatVideoView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context mContext) {
        this.mContext = mContext;

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        this.mWmParams = new WindowManager.LayoutParams();
        // 设置window type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                mWmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            } else {
                mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
            //*****注意 注意 使用 TYPE_SYSTEM_ALERT；必须手动开启设置界面的悬浮窗权限；否则是看不见
//            mWmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        } else {
            mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mWmParams.format = PixelFormat.RGBA_8888;
        // 设置图片格式，效果为背景透明
//        mWmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        mWmParams.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        // 调整悬浮窗显示的停靠位置
        mWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();

        //首页的底部高度
        home_tab_h = (int) getResources().getDimension(R.dimen.home_tab);
        float_h = (int) getResources().getDimension(R.dimen.float_h);

        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        mWmParams.x = mScreenWidth;
        maxY = mScreenHeight;
        /*初始化的位置*/
        int initY = mScreenHeight - home_tab_h - float_h - Utils.dip2px(mContext, 20);
        mWmParams.y = initY;
        // 设置悬浮窗口长宽数据
        mWmParams.width = LayoutParams.WRAP_CONTENT;
        mWmParams.height = LayoutParams.WRAP_CONTENT;
        addView(createView(mContext));
        mWindowManager.addView(this, mWmParams);

        setOnTouchListener(this);

        //默认状态
        hide();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        int oldX = mWmParams.x;
        int oldY = mWmParams.y;
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE://横屏
                if (mIsRight) {
                    mWmParams.x = mScreenWidth;
                    mWmParams.y = oldY;
                } else {
                    mWmParams.x = oldX;
                    mWmParams.y = oldY;
                }
                break;
            case Configuration.ORIENTATION_PORTRAIT://竖屏
                if (mIsRight) {
                    mWmParams.x = mScreenWidth;
                    mWmParams.y = oldY;
                } else {
                    mWmParams.x = oldX;
                    mWmParams.y = oldY;
                }
                break;
        }
        mWindowManager.updateViewLayout(this, mWmParams);
    }


    /**
     * 创建Float view
     *
     * @param context
     * @return
     */
    private View createView(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // 从布局文件获取浮动窗口视图
        View rootFloatView = inflater.inflate(R.layout.float_video, null);
        View closeView = rootFloatView.findViewById(R.id.closeView);
        mVideoView = (PLVideoView) rootFloatView.findViewById(R.id.videoView);
//        View mCoverView = rootFloatView.findViewById(R.id.CoverView);
//        mVideoView.setCoverView(mCoverView);


        mLoadingView = rootFloatView.findViewById(R.id.loadingView);
        emptyView = rootFloatView.findViewById(R.id.emptyView);
        mVideoView.setBufferingIndicator(mLoadingView);
        mLoadingView.setVisibility(View.VISIBLE);

        // 1 -> hw codec enable, 0 -> disable [recommended]
        PlayerUtil.setOptions(mVideoView, PlayerUtil.MY_DEFAULT_MEDIA_CODEC);

        mVideoView.setDisplayAspectRatio(mDisplayAspectRatio);
        // Set some listeners
        mVideoView.setOnInfoListener(mOnInfoListener);
        mVideoView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        mVideoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnSeekCompleteListener(mOnSeekCompleteListener);
        mVideoView.setOnErrorListener(mOnErrorListener);

        mVideoView.setVideoPath(videoPath);

        // You can also use a custom `MediaController` widget
//        mMediaController = new MediaController(this, false, mIsLiveStreaming == 1);
//        mVideoView.setMediaController(mMediaController);

        View apptextview = rootFloatView.findViewById(R.id.apptextview);
        if (apptextview != null) {
            apptextview.setVisibility(View.GONE);
        }
        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseVideo();
                hide();
            }
        });
        rootFloatView.measure(MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED), MeasureSpec
                .makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        mVideoView.setKeepScreenOn(true);

        return rootFloatView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                mWmParams.alpha = 1f;
                mWindowManager.updateViewLayout(this, mWmParams);
                mDraging = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();
                // 如果移动量大于3才移动
                if (Math.abs(mTouchStartX - mMoveStartX) > 3
                        && Math.abs(mTouchStartY - mMoveStartY) > 3) {
                    mDraging = true;
                    // 更新浮动窗口位置参数
                    mWmParams.x = (int) (x - mTouchStartX);
                    mWmParams.y = (int) (y - mTouchStartY);
                    if (mWmParams.y > maxY) {
                        mWmParams.y = maxY;
                    }
                    mWindowManager.updateViewLayout(this, mWmParams);
                    return false;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                if (Math.abs(event.getX() - mTouchStartX) < 5 && Math.abs(event.getY() - mTouchStartY) < 5) {
                    //认为是点击事件
//                    Toast.makeText(context, "click！", Toast.LENGTH_SHORT).show();

                    if (data != null) {
                        doEvent();
                    }
                    return false;
                }

                if (mWmParams.x >= mScreenWidth / 2) {
                    mWmParams.x = mScreenWidth;
                    mIsRight = true;
                } else if (mWmParams.x < mScreenWidth / 2) {
                    mIsRight = false;
                    mWmParams.x = 0;
                }
                if (mWmParams.y > maxY) {
                    mWmParams.y = maxY;
                }
                mWindowManager.updateViewLayout(this, mWmParams);
                // 初始化
                mTouchStartX = mTouchStartY = 0;
                break;
        }
        return false;
    }

    /*播放出现过错误*/
    boolean hasError = false;

    void enterRoom() {
        Intent intent = ChatRoomActivity.getMyIntent(mContext, data);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //入口是小窗口
        intent.putExtra("isFloat", true);
        mContext.startActivity(intent);
    }

    /**
     * float悬浮窗的点击事件
     */
    public void doEvent() {
        if (data == null)
            return;
        //状态一直是播放的
//        data.getChannelStatus() == LiveRoomNew.STATUS_ON
        if (mVideoView.isPlaying() || !hasError) {
            enterRoom();
        } else {
            //如果是直播室关闭状态，重新获取数据，设置在线人数
            final Dialog dialog = DialogUtil.getLoadingDlg(mContext, null);
//            <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
            //dialog 需要依附于act才能显示，service中设置为系统级别可显示；但是必须要获取到权限
//            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
            } else {
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
            }

            dialog.show();
            LiveDatahelp.loadRoom(mContext, data.getChatRoomId(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    try {
                        if (dialog != null)
                            dialog.dismiss();
                        if (msg != null) {
                            LiveRoomNew roomNew = (LiveRoomNew) msg.obj;
                            if (roomNew != null) {
                                data = roomNew;
                                enterRoom();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });

        }
    }

    private void removeFloatView() {
        try {
            mWindowManager.removeView(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 隐藏悬浮窗
     */
    public void hide() {
        try {
            setVisibility(View.GONE);
            Message message = mTimerHandler.obtainMessage();
            message.what = HANDLER_TYPE_HIDE_LOGO;
            mTimerHandler.sendMessage(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示悬浮窗
     */
    public void show() {
        try {
            //重置状态码
            hasError = false;
            emptyView.setVisibility(GONE);
            if (getVisibility() != View.VISIBLE) {
                setVisibility(View.VISIBLE);
                if (mShowLoader) {
                    mWmParams.alpha = 1f;
                    mWindowManager.updateViewLayout(this, mWmParams);

                    mShowLoader = false;
                }
            }

            // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
            mWmParams.x = mScreenWidth;
            maxY = mScreenHeight;
            /*初始化的位置*/
            int initY = mScreenHeight - home_tab_h - float_h - Utils.dip2px(mContext, 20);
            mWmParams.y = initY;
            mWindowManager.updateViewLayout(this, mWmParams);

            if (!StringUtil.isEmpty(videoPath)) {
                startVideo(videoPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否Float view
     */
    public void destroy() {
        hide();
        removeFloatView();
        releaseVideo();
        try {
            mTimerHandler.removeMessages(1);
        } catch (Exception e) {
        }
    }

    String videoPath;
    /*直播室的信息*/
    LiveRoomNew data;

    public LiveRoomNew getData() {
        return data;
    }

    public void setData(LiveRoomNew data) {
        this.data = data;
        if (data != null) {
            videoPath = data.getRtmpDownstreamAddress();
        } else {
            videoPath = null;
        }
    }

    /**
     * 释放video资源
     */
    public void releaseVideo() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    /**
     * 直播暂停 就是停止 然后重新播放
     */
    public void pauseVideo() {
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    public void startVideo(final String mVideoPath) {
        if (mVideoView != null) {
            Log.v(TAG, "startVideo");
            mVideoView.setVideoPath(mVideoPath);

//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    //这里是延时开启
//                    mVideoView.start();
//                }
//            }, 1000);

            mVideoView.start();

            mLoadingView.setVisibility(View.VISIBLE);
        }

    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    private static final int MESSAGE_ID_RECONNECTING = 0x01;

    //    private MediaController mMediaController;
    private PLVideoView mVideoView;
    private int mDisplayAspectRatio = PLVideoView.ASPECT_RATIO_PAVED_PARENT;
    private boolean mIsActivityPaused = true;
    private View mLoadingView, emptyView;
    //    private View mCoverView = null;
    private int mIsLiveStreaming = 1;
    /*允许播放的最大循环次数*/
    public static int PLAYER_ERROR_COUNT_MAX = 3;
    int playErrorCount = 0;

    public void onClickSwitchScreen(View v) {
        mDisplayAspectRatio = (mDisplayAspectRatio + 1) % 5;
        mVideoView.setDisplayAspectRatio(mDisplayAspectRatio);
        switch (mVideoView.getDisplayAspectRatio()) {
            case PLVideoView.ASPECT_RATIO_ORIGIN:
                showToastTips("Origin mode");
                break;
            case PLVideoView.ASPECT_RATIO_FIT_PARENT:
                showToastTips("Fit parent !");
                break;
            case PLVideoView.ASPECT_RATIO_PAVED_PARENT:
                showToastTips("Paved parent !");
                break;
            case PLVideoView.ASPECT_RATIO_16_9:
                showToastTips("16 : 9 !");
                break;
            case PLVideoView.ASPECT_RATIO_4_3:
                showToastTips("4 : 3 !");
                break;
            default:
                break;
        }
    }

    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
            Log.d(TAG, "onInfo: " + what + ", " + extra);
            return false;
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer plMediaPlayer, int errorCode) {
            hasError = true;
            boolean isNeedReconnect = false;
            Log.e(TAG, "Error happened, errorCode = " + errorCode);
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                    showToastTips("Invalid URL !");
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    showToastTips("404 resource not found !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    showToastTips("Connection refused !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    showToastTips("Connection timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                    showToastTips("Empty playlist !");
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    showToastTips("Stream disconnected !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    showToastTips("Network IO Error !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    showToastTips("Unauthorized Error !");
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    showToastTips("Prepare timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    showToastTips("Read frame timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_HW_DECODE_FAILURE:
                    PlayerUtil.setOptions(mVideoView, AVOptions.MEDIA_CODEC_SW_DECODE);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                default:
                    showToastTips("unknown error !");
                    break;
            }
            // Todo pls handle the error status here, reconnect or call finish()
//            if (isNeedReconnect) {
//                sendReconnectMessage();
//            } else {
//
//            }
            if (data.getChannelStatus() == LiveRoomNew.STATUS_ON) {
                //直播过程中出错

            } else {
                if (isNeedReconnect) {
                    playErrorCount++;
                    if (playErrorCount < PLAYER_ERROR_COUNT_MAX) {
                        sendReconnectMessage();
                        return true;
                    }
                }
            }
            emptyView.setVisibility(VISIBLE);
            mLoadingView.setVisibility(GONE);
            // Return true means the error has been handled
            // If return false, then `onCompletion` will be called
            return true;
        }
    };

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            Log.d(TAG, "Play Completed !");
            showToastTips("Play Completed !");
        }
    };

    private PLMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new PLMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int precent) {
//            Log.d(TAG, "onBufferingUpdate: " + precent);
        }
    };

    private PLMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new PLMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(PLMediaPlayer plMediaPlayer) {
            Log.d(TAG, "onSeekComplete !");
        }
    };

    private PLMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int width, int height, int videoSar, int videoDen) {
            Log.d(TAG, "onVideoSizeChanged: width = " + width + ", height = " + height + ", sar = " + videoSar + ", den = " + videoDen);
        }
    };

    private void showToastTips(final String tips) {
        if (mIsActivityPaused) {
            return;
        }

//        Toast.makeText(mContext, tips, Toast.LENGTH_SHORT).show();
    }

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != MESSAGE_ID_RECONNECTING) {
                return;
            }
//            if (mIsActivityPaused || !Utils.isLiveStreamingAvailable()) {
//                finish();
//                return;
//            }
//            if (!Utils.isNetworkAvailable(PLVideoViewActivity.this)) {
//                sendReconnectMessage();
//                return;
//            }
            mVideoView.setVideoPath(videoPath);
            mVideoView.start();
        }
    };

    private void sendReconnectMessage() {
        showToastTips("正在重连...");
        mLoadingView.setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_ID_RECONNECTING), 100);
    }
}
