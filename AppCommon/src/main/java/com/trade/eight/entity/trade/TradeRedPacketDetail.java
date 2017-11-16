package com.trade.eight.entity.trade;

import java.io.Serializable;

/**
 * 作者：Created by changhaiyang
 * 时间：on 16/9/26.
 * 红包明细
 */

public class TradeRedPacketDetail implements Serializable {
    private String amount;//金额
    private String createDate;//流水时间
    private int inOut;//流水类型，1：收入，2：支出
    private String title;//流水标题

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getInOut() {
        return inOut;
    }

    public void setInOut(int inOut) {
        this.inOut = inOut;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
