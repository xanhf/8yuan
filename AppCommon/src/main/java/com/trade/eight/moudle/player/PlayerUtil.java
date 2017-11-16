package com.trade.eight.moudle.player;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.widget.PLVideoView;

/**
 * Created by dufangzhu on 2017/3/22.
 * 播放器的工具类
 */

public class PlayerUtil {
    /*判断是否为直播流*/
    public static int MY_DEFAULT_ISLIVE_STREAMING = 1;
    /*推荐软解解码  兼容低版本*/
    public static int MY_DEFAULT_MEDIA_CODEC = AVOptions.MEDIA_CODEC_SW_DECODE;

    /**
     * 设置pldroid player的AVOptions
     *
     * @param mVideoView
     * @param codecType
     */
    public static void setOptions(PLVideoView mVideoView, int codecType) {
        AVOptions options = new AVOptions();

        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);
        // Some optimization with buffering mechanism when be set to 1
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, MY_DEFAULT_ISLIVE_STREAMING);
        if (MY_DEFAULT_ISLIVE_STREAMING == 1) {
            options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
        }

        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, codecType);

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        mVideoView.setAVOptions(options);

    }
}
