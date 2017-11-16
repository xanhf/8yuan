package com.trade.eight.moudle.product;

import com.trade.eight.entity.product.ProductNotice;

/**
 * 作者：Created by ocean
 * 时间：on 2017/2/8.
 */

public class ProductNoticeEvent {
    public int eventType;
    public ProductNotice productNotice;

    public static final int EVENTTYPE_ADD = 1;// 添加提醒
    public static final int EVENTTYPE_EDIT= 2;// 编辑提醒
    public static final int EVENTTYPE_SAVE = 3;// 存储提醒
    public static final int EVENTTYPE_DELETE = 4;// 删除提醒
    public static final int EVENTTYPE_EDIT_CANCLE= 5;// 编辑操作取消
    public static final int EVENTTYPE_EDIT_DELETE= 6;// 编辑操作 删除
    public ProductNoticeEvent(int eventType) {
        this.eventType = eventType;
    }

    public ProductNoticeEvent(int eventType, ProductNotice productNotice) {
        this.eventType = eventType;
        this.productNotice = productNotice;
    }
}
