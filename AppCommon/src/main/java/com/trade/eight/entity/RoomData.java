package com.trade.eight.entity;

import java.io.Serializable;

/**
 * Created by fangzhu on 16/3/10.
 * chatroom
 *
 * {
 "accId": "fxbtg_205543",
 "name": "吞吞吐吐他",
 "roomId": 2176,
 "token": "defaultToken",
 "userId": 205543
 }

 */
public class RoomData implements Serializable {
    //直接使用 String
    private String userId;
    private String roomId;
    private String token;
    /*云信账号*/
    private String accId;
    /*昵称*/
    private String name;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
