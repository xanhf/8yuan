package com.trade.eight.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/9/9.
 */
public class WeipanMsg implements Serializable {

    private long mnId;
    private String message;
    private long createTime;

    public long getMnId() {
        return mnId;
    }

    public void setMnId(long mnId) {
        this.mnId = mnId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
