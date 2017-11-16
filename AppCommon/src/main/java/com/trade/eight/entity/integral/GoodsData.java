package com.trade.eight.entity.integral;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/30.
 * 积分商城商品
 */

public class GoodsData implements Serializable{
    //1=代金券，2=直播室礼物，3=特权卡 4实物
    public static final int  GIFTTYPE_QUAN = 1;
    public static final int  GIFTTYPE_LIVE = 2;
    public static final int  GIFTTYPE_TE = 3;
    public static final int  GIFTTYPE_SHIWU = 4;
    private List<GoodsActData> activityList;// 活动list(特权卡)
    private List<IntegralProductData> voucherList;// 代金券

    public List<GoodsActData> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<GoodsActData> activityList) {
        this.activityList = activityList;
    }

    public List<IntegralProductData> getVoucherList() {
        return voucherList;
    }

    public void setVoucherList(List<IntegralProductData> voucherList) {
        this.voucherList = voucherList;
    }
}
