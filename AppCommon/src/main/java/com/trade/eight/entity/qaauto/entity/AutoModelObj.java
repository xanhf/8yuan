package com.trade.eight.entity.qaauto.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trade.eight.nim.extension.CustomAttachParser;
import com.trade.eight.nim.extension.CustomAttachmentType;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fangzhu on 16/12/13.
 * 自动回复消息体 model
 "msg":
 modelList:
 */
public class AutoModelObj implements Serializable {
    private String msg;
    List<AutoInnerObj> modelList;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<AutoInnerObj> getModelList() {
        return modelList;
    }

    public void setModelList(List<AutoInnerObj> modelList) {
        this.modelList = modelList;
    }

    /**
     * 发送对象
     * @param obj
     * @return
     */
    public static String toJsonStr (AutoModelObj obj) {
        JSONObject object = new JSONObject();
        object.put(CustomAttachParser.KEY_TYPE, CustomAttachmentType.AUTO_REPLY);
        if (obj != null) {
            object.put(CustomAttachParser.KEY_DATA, JSON.toJSONString(obj));
        }

        return object.toJSONString();
    }
}
