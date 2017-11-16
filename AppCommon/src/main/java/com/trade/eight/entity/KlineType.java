package com.trade.eight.entity;

import java.io.Serializable;

/**
 * Created by fangzhu on 16/5/28.
 */
public class KlineType implements Serializable {
    private String name;
    private String type;

    public KlineType() {
    }

    public KlineType(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
