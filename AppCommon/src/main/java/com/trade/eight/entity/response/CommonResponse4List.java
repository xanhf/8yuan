package com.trade.eight.entity.response;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by fangzhu on 2015/8/6.
 *
 * {"success":true,"errorCode":"","errorInfo":"","pagerManager":null,"data":}
 *
 * mark: data is jsonArray
 *
 * you can use it like this
 * CommonResponse<UserInfo> commonResponse = CommonResponse.fromJson(s, UserInfo.class);
 * 
 */
public class CommonResponse4List<T> extends CommonResponseBase {

//    public  boolean success;
//
//    public String errorCode;
//
//    public String errorInfo;
    //data is jsonArray
    private List<T> data;

    //data is jsonObject
//    private T data;

//    public boolean isSuccess() {
//        return success;
//    }
//
//    public void setSuccess(boolean success) {
//        this.success = success;
//    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

//    public String getErrorCode() {
//        return errorCode;
//    }
//
//    public void setErrorCode(String errorCode) {
//        this.errorCode = errorCode;
//    }
//
//    public String getErrorInfo() {
//        return errorInfo;
//    }
//
//    public void setErrorInfo(String errorInfo) {
//        this.errorInfo = errorInfo;
//    }

    public static CommonResponse4List fromJson(String json, Class clazz) {
        if (json == null)
            return null;
        Gson gson = new Gson();
        Type objectType = type(CommonResponse4List.class, clazz);
        return gson.fromJson(json, objectType);
    }
    public String toJson(Class<T> clazz) {
        Gson gson = new Gson();
        Type objectType = type(CommonResponse4List.class, clazz);
        return gson.toJson(this, objectType);
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
