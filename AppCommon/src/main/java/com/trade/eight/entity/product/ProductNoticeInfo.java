package com.trade.eight.entity.product;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 2017/2/13.
 * 行情提醒产品基本信息
 */

public class ProductNoticeInfo implements Serializable {
    private String excode;//	交易所编码
    private String code;//	产品编码
    private String name;//	产品名称
    private String exname;//	交易所名称
    private int limitSize;//	限制条数
    private int status;//	状态（1.关闭，0开启）
    private String pointListStr;//	浮动点位，字符串逗号分隔（5,10,15,20）

    public String getExcode() {
        return excode;
    }

    public void setExcode(String excode) {
        this.excode = excode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExname() {
        return exname;
    }

    public void setExname(String exname) {
        this.exname = exname;
    }

    public int getLimitSize() {
        return limitSize;
    }

    public void setLimitSize(int limitSize) {
        this.limitSize = limitSize;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPointListStr() {
        return pointListStr;
    }

    public void setPointListStr(String pointListStr) {
        this.pointListStr = pointListStr;
    }
}
