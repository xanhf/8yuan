package com.trade.eight.entity.trade;

import java.io.Serializable;

/**
 * Created by fangzhu on 16/6/6.
 */
public class City implements Serializable {
    private String code	;//	城市编码
    private String name	;//	城市名称
    private String provinceCode	;//	省编码

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

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
}
