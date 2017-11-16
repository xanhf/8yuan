package com.trade.eight.moudle.chatroom.action;

import android.os.AsyncTask;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.session.actions.PickImageAction;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.live.LiveRoomNew;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.moudle.chatroom.data.ChatRoomDataHelper;

import java.io.File;

/**
 * Created by fangzhu
 */
public class ChatRoomImageAction extends PickImageAction {
    private String roomId;
    private String channelId;
    private int sendPicStatus;
    BaseActivity activity;

    public static final int ALLOWSENDPIC = LiveRoomNew.sendPicStatus_ENABLE;//聊天室允许发图片
    public static final int NORALLOWSENDPIC = LiveRoomNew.sendPicStatus_DIS_ENABLE;//聊天室禁止发图片


    public ChatRoomImageAction(BaseActivity activity, String roomId, String channelId, int sendPicStatus) {
        super(R.drawable.nim_message_plus_photo_selector, R.string.input_panel_photo, true);
        this.channelId = channelId;
        this.roomId = roomId;
        this.sendPicStatus = sendPicStatus;
        this.activity = activity;

    }

    @Override
    protected void onPicked(File file) {
        //干脆这里直接设置成直播室的就好了；可以兼容直播室的发送，也可以兼容p2p
        ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomImageMessage(getAccount(), file, file.getName());
        sendMessage(message);
    }

    @Override
    public void onClick() {
        //test send imgage
//        ChatRoomImageAction.super.onClick();

        if (sendPicStatus == NORALLOWSENDPIC) {//进直播室时不允许发图片  直接提示
            activity.showCusToast("聊天室暂时禁止发送图片");
            return;
        } else {
            chenckSendImg();
        }
    }

    void chenckSendImg() {
        // 进直播室时允许发图片  请求然后判断
        new AsyncTask<Void, Void, CommonResponse<Integer>>() {
            @Override
            protected CommonResponse<Integer> doInBackground(Void... voids) {
                CommonResponse<Integer> response = ChatRoomDataHelper.sendPicStatus(activity, roomId, channelId);
                return response;
            }

            @Override
            protected void onPostExecute(CommonResponse<Integer> objectCommonResponse) {
                super.onPostExecute(objectCommonResponse);
                if (objectCommonResponse != null) {
                    if (objectCommonResponse.isSuccess()) {
                        if (objectCommonResponse.getData() == ALLOWSENDPIC) {//允许发图
                            ChatRoomImageAction.super.onClick();
                        } else {//不允许发图
                            activity.showCusToast("聊天室暂时禁止发送图片");
                        }
                    } else {
                        activity.showCusToast("聊天室暂时禁止发送图片");
                    }
                } else {//请求失败
                    activity.showCusToast("聊天室暂时禁止发送图片");
                }
            }
        }.execute();
    }
}

