package com.trade.eight.moudle.me;

import com.trade.eight.entity.integral.AccountIntegralData;

/**
 * 积分商城兑换事件
 */
public class CreateIntegralOrderSuccessEvent {
    private AccountIntegralData accountIntegralData;

    public CreateIntegralOrderSuccessEvent(AccountIntegralData accountIntegralData) {
        this.accountIntegralData = accountIntegralData;
    }

    public AccountIntegralData getAccountIntegralData() {
        return accountIntegralData;
    }

    public void setAccountIntegralData(AccountIntegralData accountIntegralData) {
        this.accountIntegralData = accountIntegralData;
    }
}
