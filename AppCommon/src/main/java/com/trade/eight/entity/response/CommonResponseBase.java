package com.trade.eight.entity.response;

import android.os.Parcel;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 17/1/10.
 */

public class CommonResponseBase implements Serializable {
    public  boolean success;

    public String errorCode;

    public String errorInfo;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }


    public CommonResponseBase() {
    }

    protected CommonResponseBase(Parcel in){
        this.success = in.readByte() ==1 ?true:false;
        this.errorCode = in.readString();
        this.errorInfo = in.readString();
    }
}
