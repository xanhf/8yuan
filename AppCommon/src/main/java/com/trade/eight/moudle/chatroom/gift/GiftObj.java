package com.trade.eight.moudle.chatroom.gift;

import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.Serializable;

/**
 * Created by dufangzhu on 2017/4/5.
 * 直播室礼物对象
 */

public class GiftObj implements Serializable {

    /**
     * giftId : 2
     * giftName : 掌声
     * giftPic : http://t.m.8caopan.com/images/gift/0/0/2/20170328172953806.png
     * giftSmallPic : http://t.m.8caopan.com/images/gift/0/0/2/20170328173006813.png
     * poins : 8
     */

    private String giftId;
    private String giftName;
    private String giftPic;
    private String giftSmallPic;
    private int poins;


    /*
    * 直播室显示礼物的地方
    * {giftId=2, giftName=掌声, giftNum=1}
    * */
    private int giftNum;

    //自定义添加的message
    private IMMessage message;
    /*自定义的额外时间*/
    private long exTime;
    /*自定义的额外显示的数字*/
    private int exNum;


    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getGiftPic() {
        return giftPic;
    }

    public void setGiftPic(String giftPic) {
        this.giftPic = giftPic;
    }

    public String getGiftSmallPic() {
        return giftSmallPic;
    }

    public void setGiftSmallPic(String giftSmallPic) {
        this.giftSmallPic = giftSmallPic;
    }

    public int getPoins() {
        return poins;
    }

    public void setPoins(int poins) {
        this.poins = poins;
    }

    public int getGiftNum() {
        return giftNum;
    }

    public void setGiftNum(int giftNum) {
        this.giftNum = giftNum;
    }

    public IMMessage getMessage() {
        return message;
    }

    public void setMessage(IMMessage message) {
        this.message = message;
    }

    public int getExNum() {
        return exNum;
    }

    public void setExNum(int exNum) {
        this.exNum = exNum;
    }

    public long getExTime() {
        return exTime;
    }

    public void setExTime(long exTime) {
        this.exTime = exTime;
    }
}

