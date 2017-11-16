package com.trade.eight.entity.response;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Administrator on 2015/5/5.
 *
 * 阿里云的api返回格式
 */
public class AliApiResult implements Serializable{

    private boolean success;
    private String errorCode;
    private String errorInfo;
    private JSONObject data;



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

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static AliApiResult fromJson(String json, Class clazz) {
        Gson gson = new Gson();
        Type objectType = type(AliApiResult.class, clazz);
        return gson.fromJson(json, objectType);
    }

    static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }
}
