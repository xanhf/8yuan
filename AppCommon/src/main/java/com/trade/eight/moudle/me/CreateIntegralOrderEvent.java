package com.trade.eight.moudle.me;

import com.trade.eight.entity.integral.IntegralProductData;

public class CreateIntegralOrderEvent {
    private IntegralProductData integralProductData;

    public CreateIntegralOrderEvent(IntegralProductData integralProductData) {
        this.integralProductData = integralProductData;
    }

    public IntegralProductData getIntegralProductData() {
        return integralProductData;
    }

    public void setIntegralProductData(IntegralProductData integralProductData) {
        this.integralProductData = integralProductData;
    }
}
