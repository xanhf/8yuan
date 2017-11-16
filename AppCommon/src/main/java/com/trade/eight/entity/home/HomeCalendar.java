package com.trade.eight.entity.home;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/8/16.
 */
public class HomeCalendar implements Serializable {
    private long id;
    private String country;
    private String ztTime;
    private String ztDate;
    private String content;
    private String published;
    private String predict;
    private String oldvalue;


    //for detail
    private String ecOrg;
    private String ecEffect;
    private String ecCountry;
    private String ecName;
    private String ecTitle;
    private String ecImportance;
    private String ecReason;
    private String ecMethod;
    private String ecMeaning;
    private String ecNextTime;
    private String ecFrequency;
    private String comment;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZtTime() {
        return ztTime;
    }

    public void setZtTime(String ztTime) {
        this.ztTime = ztTime;
    }

    public String getZtDate() {
        return ztDate;
    }

    public void setZtDate(String ztDate) {
        this.ztDate = ztDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getPredict() {
        return predict;
    }

    public void setPredict(String predict) {
        this.predict = predict;
    }

    public String getOldvalue() {
        return oldvalue;
    }

    public void setOldvalue(String oldvalue) {
        this.oldvalue = oldvalue;
    }

    public String getEcOrg() {
        return ecOrg;
    }

    public void setEcOrg(String ecOrg) {
        this.ecOrg = ecOrg;
    }

    public String getEcEffect() {
        return ecEffect;
    }

    public void setEcEffect(String ecEffect) {
        this.ecEffect = ecEffect;
    }

    public String getEcCountry() {
        return ecCountry;
    }

    public void setEcCountry(String ecCountry) {
        this.ecCountry = ecCountry;
    }

    public String getEcName() {
        return ecName;
    }

    public void setEcName(String ecName) {
        this.ecName = ecName;
    }

    public String getEcTitle() {
        return ecTitle;
    }

    public void setEcTitle(String ecTitle) {
        this.ecTitle = ecTitle;
    }

    public String getEcImportance() {
        return ecImportance;
    }

    public void setEcImportance(String ecImportance) {
        this.ecImportance = ecImportance;
    }

    public String getEcReason() {
        return ecReason;
    }

    public void setEcReason(String ecReason) {
        this.ecReason = ecReason;
    }

    public String getEcMethod() {
        return ecMethod;
    }

    public void setEcMethod(String ecMethod) {
        this.ecMethod = ecMethod;
    }

    public String getEcMeaning() {
        return ecMeaning;
    }

    public void setEcMeaning(String ecMeaning) {
        this.ecMeaning = ecMeaning;
    }

    public String getEcNextTime() {
        return ecNextTime;
    }

    public void setEcNextTime(String ecNextTime) {
        this.ecNextTime = ecNextTime;
    }

    public String getEcFrequency() {
        return ecFrequency;
    }

    public void setEcFrequency(String ecFrequency) {
        this.ecFrequency = ecFrequency;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
