package com.trade.eight.entity.messagecenter;

import java.io.Serializable;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/10.
 * 消息中心实体
 */

public class MessageData implements Serializable {
    private long messageId;//	主消息ID
    private String messageDetailId;//	消息ID
    private int messageType;//	消息类型（1:系统提醒,2行情提醒，3爆仓提醒，4代金券到账，5代金券到期，6代金券即将到期7止盈止损，8交易风险）
    private String messageTitle;//	消息标题
    private String messageContent;//	消息内容
    private int status;//	消息状态（1:未读，2:已读,3失败）
    private long createTime;//	创建时间
    private String excode;//	交易所编号
    private String orderId;//	订单号
    private String url;//	跳转地址
    private TargetObject targetObject;//	目标对象

    //消息状态
    public static final int MSG_READ = 1;//1:未读
    public static final int MSG_UNREAD = 2;//2:已读
    public static final int MSG_READFAILED = 3;//3失败


    //对应消息类型
    public static final int TYPE_XITONG = 1;//1:系统提醒
    public static final int TYPE_HANGQING = 2;//2行情提醒
    public static final int TYPE_BAOCANG = 3;//3爆仓提醒
    public static final int TYPE_QUANDAOZHANG = 4;//4代金券到账
    public static final int TYPE_QUANDAOQI = 5;//5代金券到期
    public static final int TYPE_QUANJJDAOQI = 6;//6代金券即将到期
    public static final int TYPE_ZYZS = 7;//7止盈止损
    public static final int TYPE_JYFX = 8;//交易风险


    public class TargetObject {
        private int count;//	//手数 targetObject属性
        private String productName;//	产品名称 targetObject属性
        private String profit;//	盈亏 targetObject属性
        private String bl;//	比例 targetObject属性
        private long createTime;//	创建时间 targetObject属性
        private String createPoint;//	建仓价 targetObject属性
        private String closePoint;//	平仓价格 targetObject属性
        private long closeTime;//	平仓时间 targetObject属性
        private String fee;//	手续费 targetObject属性
        private int closeType;//	平仓类型 targetObject属性
        private int buyType;//	购买类型 targetObject属性
        private String closeTypeName;//	平仓类型名称 targetObject属性
        private String buyTypeName;//	购买类型名称 targetObject属性
        private String weight;//	重量 targetObject属性
        private String unit;//	单位 targetObject属性
        private int voucher;//	是否用券 targetObject属性

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProfit() {
            return profit;
        }

        public void setProfit(String profit) {
            this.profit = profit;
        }

        public String getBl() {
            return bl;
        }

        public void setBl(String bl) {
            this.bl = bl;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getCreatePoint() {
            return createPoint;
        }

        public void setCreatePoint(String createPoint) {
            this.createPoint = createPoint;
        }

        public String getClosePoint() {
            return closePoint;
        }

        public void setClosePoint(String closePoint) {
            this.closePoint = closePoint;
        }

        public long getCloseTime() {
            return closeTime;
        }

        public void setCloseTime(long closeTime) {
            this.closeTime = closeTime;
        }

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public int getCloseType() {
            return closeType;
        }

        public void setCloseType(int closeType) {
            this.closeType = closeType;
        }

        public int getBuyType() {
            return buyType;
        }

        public void setBuyType(int buyType) {
            this.buyType = buyType;
        }

        public String getCloseTypeName() {
            return closeTypeName;
        }

        public void setCloseTypeName(String closeTypeName) {
            this.closeTypeName = closeTypeName;
        }

        public String getBuyTypeName() {
            return buyTypeName;
        }

        public void setBuyTypeName(String buyTypeName) {
            this.buyTypeName = buyTypeName;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public int getVoucher() {
            return voucher;
        }

        public void setVoucher(int voucher) {
            this.voucher = voucher;
        }
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getMessageDetailId() {
        return messageDetailId;
    }

    public void setMessageDetailId(String messageDetailId) {
        this.messageDetailId = messageDetailId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getExcode() {
        return excode;
    }

    public void setExcode(String excode) {
        this.excode = excode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public TargetObject getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(TargetObject targetObject) {
        this.targetObject = targetObject;
    }
}
