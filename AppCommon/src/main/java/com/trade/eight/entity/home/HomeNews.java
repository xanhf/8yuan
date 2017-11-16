package com.trade.eight.entity.home;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/8/16.
 */
public class HomeNews implements Serializable {
    private long id;
    private String title;
    private String digest;
    private String analystName;
    private String url;
    private String type;
    private String sourceCreateTime;
    private String replies;
    //subType v2接口使用
    private String subType;
    private String subTypeName;
    private String subTypeNameColor;

    //for detail
    private String avatarUrl;
    private String nickName;
    private String createTime;
    private String text;
//    private List<String> imageList;

    public static final int TYPE_CONTENT = 0;
    public static final int TYPE_TIME = 1;
    private int itemType;

//    //得到不同类型
//    public String getNewsType(String type) {
//        if (type == null)
//            return "策略";
//        if (type.equals("1"))
//            return "策略";
//        if (type.equals("2"))
//            return "咨讯";
//        return "策略";
//    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getAnalystName() {
        return analystName;
    }

    public void setAnalystName(String analystName) {
        this.analystName = analystName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSourceCreateTime() {
        return sourceCreateTime;
    }

    public void setSourceCreateTime(String sourceCreateTime) {
        this.sourceCreateTime = sourceCreateTime;
    }

    public String getReplies() {
        return replies;
    }

    public void setReplies(String replies) {
        this.replies = replies;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getSubTypeName() {
        return subTypeName;
    }

    public void setSubTypeName(String subTypeName) {
        this.subTypeName = subTypeName;
    }

    public String getSubTypeNameColor() {
        return subTypeNameColor;
    }

    public void setSubTypeNameColor(String subTypeNameColor) {
        this.subTypeNameColor = subTypeNameColor;
    }
}
