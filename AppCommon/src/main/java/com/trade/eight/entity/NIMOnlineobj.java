package com.trade.eight.entity;

import java.io.Serializable;

/**
 * Created by fangzhu on 16/10/27.
 * 获取网易云信聊天的配置信息
 *
 */
public class NIMOnlineobj implements Serializable {
    /**
     * "data":{
     "customerAccid":"1477483443395015",
     "customerToken":"836218",
     "staffAccid":"18600001111"
     staffName:客服西施
     }
     */
    private String customerAccid;
    private String customerToken;
    private String staffAccid;
    private String staffName;


    public String getCustomerAccid() {
        return customerAccid;
    }

    public void setCustomerAccid(String customerAccid) {
        this.customerAccid = customerAccid;
    }

    public String getStaffAccid() {
        return staffAccid;
    }

    public void setStaffAccid(String staffAccid) {
        this.staffAccid = staffAccid;
    }

    public String getCustomerToken() {
        return customerToken;
    }

    public void setCustomerToken(String customerToken) {
        this.customerToken = customerToken;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
}
