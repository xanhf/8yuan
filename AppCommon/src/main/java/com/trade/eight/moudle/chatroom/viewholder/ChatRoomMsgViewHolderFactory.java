package com.trade.eight.moudle.chatroom.viewholder;

import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderFactory;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.HashMap;

/**
 * 聊天室消息项展示ViewHolder工厂类。
 */
public class ChatRoomMsgViewHolderFactory {

    private static HashMap<Class<? extends MsgAttachment>, Class<? extends MsgViewHolderBase>> viewHolders = new HashMap<>();

    static {
        // built in
        register(ChatRoomNotificationAttachment.class, ChatRoomMsgViewHolderNotification.class);
//        register(GuessAttachment.class, ChatRoomMsgViewHolderGuess.class);
//        register(ImageAttachment.class, MsgViewHolderPicture.class);
        //使用自己的图片Picture 处理类
        register(ImageAttachment.class, ChatRoomMsgViewHolderPicture.class);
        //语音  使用自己的
        register(AudioAttachment.class, ChatRoomMsgViewHolderAudio.class);
    }

    public static void register(Class<? extends MsgAttachment> attach, Class<? extends MsgViewHolderBase> viewHolder) {
        viewHolders.put(attach, viewHolder);
    }

    public static Class<? extends MsgViewHolderBase> getViewHolderByType(IMMessage message) {
        try {
            if (message.getMsgType() == MsgTypeEnum.text) {
                return ChatRoomViewHolderText.class;
            } else {
                Class<? extends MsgViewHolderBase> viewHolder = null;
                if (message.getAttachment() != null) {
                    Class<? extends MsgAttachment> clazz = message.getAttachment().getClass();
                    while (viewHolder == null && clazz != null) {
                        viewHolder = viewHolders.get(clazz);
                        if (viewHolder == null) {
                            clazz = MsgViewHolderFactory.getSuperClass(clazz);
                        }
                    }
                }
                //直播室隐藏未知消息类型
//                return viewHolder == null ? MsgViewHolderUnknown.class : viewHolder;
                return viewHolder == null ? ChatRoomViewHolderUnKnow.class : viewHolder;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //直播室隐藏未知消息类型
//        return MsgViewHolderUnknown.class;
        return ChatRoomViewHolderUnKnow.class;
    }

    public static int getViewTypeCount() {
        // plus text and unknown
        return viewHolders.size() + 2;
    }
}
