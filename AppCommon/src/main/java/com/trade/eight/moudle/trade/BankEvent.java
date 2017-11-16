package com.trade.eight.moudle.trade;

import com.trade.eight.entity.trade.Banks;

/**
 * Created by fangzhu on 16/11/24.
 * eventBus 传参数  选择银行
 */
public class BankEvent {
    private Banks banks;

    public BankEvent(Banks banks) {
        this.banks = banks;
    }

    public Banks getBanks() {
        return banks;
    }

    public void setBanks(Banks banks) {
        this.banks = banks;
    }


}
