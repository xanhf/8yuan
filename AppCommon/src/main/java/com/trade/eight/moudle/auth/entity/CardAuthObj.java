package com.trade.eight.moudle.auth.entity;

import java.io.Serializable;

/**
 * Created by dufangzhu on 2017/5/22.
 * 实名认证
 */

public class CardAuthObj implements Serializable {

    private String name;
    private String sex;
    private String idNo;
    private String expiresStart;
    private String expiresEnd;

    /*
    * 状态，1：认证失败，2：资料未提交，3：认证中，4：认证成功
    * */
    private int status;
    /*认证状态提示信息*/
    private String returnMsg;
    public static final int STATUS_FAILED = 1;
    public static final int STATUS_NOT_SUBMMIT = 2;
    public static final int STATUS_CHECKING = 3;
    public static final int STATUS_SUCCESS = 4;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getExpiresStart() {
        return expiresStart;
    }

    public void setExpiresStart(String expiresStart) {
        this.expiresStart = expiresStart;
    }

    public String getExpiresEnd() {
        return expiresEnd;
    }

    public void setExpiresEnd(String expiresEnd) {
        this.expiresEnd = expiresEnd;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }
}
