package com.trade.eight.moudle.trade;

import com.trade.eight.entity.trade.Province;

/**
 * Created by fangzhu on 16/11/24.
 * eventBus 传参数  选择城市
 */
public class ProvinceEvent {
    private Province province;

    public ProvinceEvent(Province province) {
        this.province = province;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }
}
