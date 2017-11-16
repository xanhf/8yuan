package com.trade.eight.moudle.chatroom.fragment;

import android.view.View;

import com.easylife.ten.lib.R;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nim.uikit.session.module.Container;
import com.netease.nim.uikit.session.module.input.InputPanel;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.media.record.RecordType;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.entity.live.LiveRoomNew;
import com.trade.eight.moudle.chatroom.gift.GiftPanUtil;
import com.trade.eight.tools.BaseInterface;

import java.io.File;
import java.util.List;

/**
 * Created by zhoujianghua on 2016/6/8.
 */
public class ChatRoomInputPanel extends InputPanel {

    public ChatRoomInputPanel(Container container, View view, List<BaseAction> actions, boolean isTextAudioSwitchShow) {
        super(container, view, actions, isTextAudioSwitchShow);
    }

    public ChatRoomInputPanel(Container container, View view, List<BaseAction> actions) {
        super(container, view, actions);
    }

    @Override
    protected IMMessage createTextMessage(String text) {
        return ChatRoomMessageBuilder.createChatRoomTextMessage(container.account, text);
    }

    @Override
    public void onRecordSuccess(File audioFile, long audioLength, RecordType recordType) {
        //干脆这里直接设置成直播室的就好了；可以兼容直播室的发送，也可以兼容p2p
        ChatRoomMessage audioMessage = ChatRoomMessageBuilder.createChatRoomAudioMessage(container.account, audioFile, audioLength);
        container.proxy.sendMessage(audioMessage);
    }

    /*最外层直播室列表对象*/
    LiveRoomNew roomNew;

    /**
     * extend by fangzhu
     */
    @Override
    protected void init() {
        super.init();

        //处理自己的初始化工作
        if (view != null) {
            if (container.activity == null)
                return;
            View gift_button = view.findViewById(com.netease.nim.uikit.R.id.gift_button);
            if (gift_button != null) {
                if (BaseInterface.SWITCH_NIM_CHATROOM_GIFT) {
                    gift_button.setVisibility(View.VISIBLE);
                } else {
                    gift_button.setVisibility(View.GONE);
                }
                gift_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //表情是否开启了
                        View emoticon_picker_view = container.activity.findViewById(R.id.emoticon_picker_view);
                        if (emoticon_picker_view != null) {
                            if (emoticon_picker_view.getVisibility() == View.VISIBLE) {
                                emoticon_picker_view.setVisibility(View.GONE);
//                                return;
                            }
                        }
                        View actionsLayout = container.activity.findViewById(R.id.actionsLayout);
                        if (actionsLayout != null) {
                            if (actionsLayout.getVisibility() == View.VISIBLE) {
                                actionsLayout.setVisibility(View.GONE);
//                                return;
                            }
                        }

                        //出现礼物弹窗
                        GiftPanUtil giftPanUtil = new GiftPanUtil((BaseActivity) container.activity, roomNew);
                        giftPanUtil.load((BaseActivity) container.activity);
                    }
                });
            }
        }
    }

    public LiveRoomNew getRoomNew() {
        return roomNew;
    }

    public void setRoomNew(LiveRoomNew roomNew) {
        this.roomNew = roomNew;
    }
}
