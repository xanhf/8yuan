package com.trade.eight.moudle.auth.entity;

import java.io.Serializable;

/**
 * Created by dufangzhu on 2017/5/22.
 * 身份证信息
 */

public class IdCardObj implements Serializable{
    /*签发机关*/
    private String authority;
    /*有效期开始*/
    private String expirationStart;
    /*有效期结束*/
    private String expirationEnd;


    /*姓名*/
    private String name;
    private String idNo;
    private String birth;
    private String sex;
    private String address;
    /*民族*/
    private String nation;

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getExpirationStart() {
        return expirationStart;
    }

    public void setExpirationStart(String expirationStart) {
        this.expirationStart = expirationStart;
    }

    public String getExpirationEnd() {
        return expirationEnd;
    }

    public void setExpirationEnd(String expirationEnd) {
        this.expirationEnd = expirationEnd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }
}
