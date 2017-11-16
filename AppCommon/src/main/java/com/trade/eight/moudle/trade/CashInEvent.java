package com.trade.eight.moudle.trade;

/**
 * 作者：Created by changhaiyang
 * 时间：on 16/9/23.
 * 充值回调事件
 */

public class CashInEvent {
    private boolean isSuccess;// 是否成功
    private String errorMessage;

    public CashInEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public CashInEvent(boolean isSuccess, String errorMessage) {
        this.isSuccess = isSuccess;
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
