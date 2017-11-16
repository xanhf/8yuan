package com.netease.nim.uikit.session.module;

/**
 * Created by fangzhu on 16/12/28.
 *
 * 检测直播室的msg 是否可用
 */
public interface CheckMsgEnableListener {
    //文字是否可用
    boolean isTextEnabel(String msgContent);
    //是否允许发图
//    boolean isImageEnabel();
    //是否允许发言
//    boolean isSendEnabel(String msgContent);

}
