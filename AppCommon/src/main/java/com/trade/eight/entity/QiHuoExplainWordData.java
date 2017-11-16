package com.trade.eight.entity;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/24.
 * 期货名词解释
 */

public class QiHuoExplainWordData implements Serializable {
    String title;
    String content;

    public QiHuoExplainWordData(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
