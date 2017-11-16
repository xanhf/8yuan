package com.trade.eight.nim.extension;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;
import com.trade.eight.tools.Log;

/**
 * Created by zhoujianghua on 2015/4/9.
 */
public class CustomAttachParser implements MsgAttachmentParser {

    public static final String TAG = "CustomAttachParser";

    public static final String KEY_TYPE = "type";
    public static final String KEY_DATA = "data";
    //客服自动回复的解析内容字段
    //{"modelList":[{"hrefUrl":"","message":"充值问题","modelList":[{"hrefUrl":"http://www.baidu.com","message":"2.吉弄充值问题","parentNode":"eebba64d-6bdb-4e06-82c4-a86607d55e51","type":1,"uuid":"a93cbc52-97b3-4c15-adc4-80465fe219b5"}],"parentNode":"","type":0,"uuid":"eebba64d-6bdb-4e06-82c4-a86607d55e51"},{"hrefUrl":"","message":"","parentNode":"","uuid":""},{"hrefUrl":"","message":"","parentNode":"","uuid":""}],"msg":"您好，很高兴为您服务！以下是吉弄充值问题，如有其它问题请继续咨询！"}
    public static final String KEY_MSG = "msg";

    @Override
    public MsgAttachment parse(String json) {
        CustomAttachment attachment = null;
        try {
            JSONObject object = JSON.parseObject(json);
            int type = MemberType.UNKNOWN.getValue();
            if (object.containsKey(KEY_TYPE))
                type = object.getInteger(KEY_TYPE);
            JSONObject data = null;
            if (type == CustomAttachmentType.AUTO_REPLY) {
                data = JSON.parseObject(object.getString(KEY_MSG));
            } else {
                data = object.getJSONObject(KEY_DATA);
            }
            Log.e(TAG, "type=" + type);
            Log.e(TAG, "data=" + data);
            switch (type) {
                case CustomAttachmentType.Guess:
                    attachment = new GuessAttachment();
                    break;
                case CustomAttachmentType.SnapChat:
                    return new SnapChatAttachment(data);
                case CustomAttachmentType.Sticker:
                    attachment = new StickerAttachment();
                    break;
//                case CustomAttachmentType.RTS:
//                    attachment = new RTSAttachment();
//                    break;
                case CustomAttachmentType.AUTO_REPLY:
                    attachment = new AutoReplyAttachment(data.toString());
                    break;
                default:
                    attachment = new DefaultCustomAttachment();
                    break;
            }

            if (attachment != null) {
                attachment.fromJson(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attachment;
    }

    public static String packData(int type, JSONObject data) {
        JSONObject object = new JSONObject();
        object.put(KEY_TYPE, type);
        if (data != null) {
            object.put(KEY_DATA, data);
        }

        return object.toJSONString();
    }
}
