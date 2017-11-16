package com.trade.eight.entity.qaauto.entity;

import java.io.Serializable;

/**
 * Created by fangzhu on 16/12/13.
 * 最外层模版
 "key": "de93d2d3-e377-4235-9e75-1095c0d461bb",
 "model":
 name:
 */
public class AutoIndexObj implements Serializable {
    private String key;
    private String name;
    AutoModelObj model;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public AutoModelObj getModel() {
        return model;
    }

    public void setModel(AutoModelObj model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
