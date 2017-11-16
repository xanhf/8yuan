package com.trade.eight.nim.extension;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trade.eight.entity.qaauto.entity.AutoModelObj;

/**
 * Created by fangzhu
 * 客服聊天自动回复的消息类型
 */
public class AutoReplyAttachment extends CustomAttachment {
    private String content;//json str
    //接收到的都是model
    private AutoModelObj modelObj;

    public AutoReplyAttachment() {
        super(CustomAttachmentType.AUTO_REPLY);
    }

    public AutoReplyAttachment(String content) {
        this();
        this.content = content;
        this.modelObj = JSON.parseObject(content, AutoModelObj.class);
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(CustomAttachParser.KEY_MSG, content);
        return data;
    }

    @Override
    protected void parseData(JSONObject data) {
//        content = data.toString();
    }

    public AutoModelObj getModelObj() {
        return modelObj;
    }

    public void setModelObj(AutoModelObj modelObj) {
        this.modelObj = modelObj;
    }
}
