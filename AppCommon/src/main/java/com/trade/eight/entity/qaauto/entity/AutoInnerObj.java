package com.trade.eight.entity.qaauto.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fangzhu on 16/12/13.
 * 自动回复消息体
 {
 "hrefUrl": "http://www.baidu.com",
 "message": "1.哈贵充值问题",
 "modelList": null,
 "node": null,
 "parentNode": "eebba64d-6bdb-4e06-82c4-a86607d55e51",
 "type": 1,
 "uuid": "78e79a1a-f3e2-42d0-b6b1-6cccf20dc5b6"
 } */
public class AutoInnerObj implements Serializable {
    /**
     * hrefUrl : http://www.baidu.com
     * message : 1.哈贵充值问题
     * node :
     * parentNode : eebba64d-6bdb-4e06-82c4-a86607d55e51
     * type : 1
     * uuid : 78e79a1a-f3e2-42d0-b6b1-6cccf20dc5b6
     */
    private String hrefUrl;
    private String message;
    private String node;
    List<AutoInnerObj> modelList;
    private String parentNode;
    private int type;
    private String uuid;

    //本地标记 item是否显示
    private boolean isShow;

    public String getHrefUrl() {
        return hrefUrl;
    }

    public void setHrefUrl(String hrefUrl) {
        this.hrefUrl = hrefUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getParentNode() {
        return parentNode;
    }

    public void setParentNode(String parentNode) {
        this.parentNode = parentNode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<AutoInnerObj> getModelList() {
        return modelList;
    }

    public void setModelList(List<AutoInnerObj> modelList) {
        this.modelList = modelList;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
