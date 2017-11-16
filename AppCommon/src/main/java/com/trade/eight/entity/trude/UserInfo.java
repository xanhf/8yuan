package com.trade.eight.entity.trude;

import android.content.Context;

import com.j256.ormlite.field.DatabaseField;
import com.trade.eight.dao.UserInfoDao;

public class UserInfo {
    //can not modify static value
    public static final String UID = "userId";//本地数据库的 colnmuName
    public static final String UNICKNAME = "nickName";
    public static final String UNAME = "userName";
    public static final String UMOBLE = "mobile";
    public static final String UPASSWORD = "password";
    public static final String UMOBLE_NUMBER = "mobileNum";
    public static final String UCODE = "code";
    public static final String ULEVELNUM = "levelNum";


    @DatabaseField(generatedId = true)
    private int localId;//本地主键 delete删除时候需要
    @DatabaseField
    private String userId;//用户id
    @DatabaseField
    private String mobileNum;
    @DatabaseField
    private String nickName;
    @DatabaseField
    private int authStatus;//认证状态
    @DatabaseField
    private String avatar;
    @DatabaseField
    private String token;//交易token


    @DatabaseField
    private String userName;
    @DatabaseField
    private String password;

    @DatabaseField
    private String email;
    @DatabaseField
    private String mobileEncode;//加密后的手机号
    @DatabaseField
    private String accId;//云信 账号
    @DatabaseField
    private String token_IM;//云信 token
    @DatabaseField
    private int levelNum;// 积分等级

    @DatabaseField
    private String balance;//金额
    private double usdCny;//美元汇率
    @DatabaseField
    private String currentQHAccount;//期货账户

    /**
     * {
     "balance": 0,
     "coupon": 0,
     "couponValidDate": null
     }
     */
    //农交所资金信息
    private String coupon;
    private String couponValidDate;

    public UserInfo() {
    }

    public String getCurrentQHAccount() {
        return currentQHAccount;
    }

    public void setCurrentQHAccount(String currentQHAccount) {
        this.currentQHAccount = currentQHAccount;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getMobileEncode() {
        return mobileEncode;
    }

    public void setMobileEncode(String mobileEncode) {
        this.mobileEncode = mobileEncode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public String getAvatar() {
        return avatar;
    }

    /**
     * 获取用户大头像
     * 上传头像的时候, 后台会存一份大图何小图
     * @param smallpic 原来的小图片地址
     * @return
     */
    public static String getLargeAvatar(String smallpic) {
        if (smallpic == null)
            return null;
        //从小图替换到大图
        smallpic = smallpic.replace("avatar/3/", "avatar/1/");
        return smallpic;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getToken_IM() {
        return token_IM;
    }

    public void setToken_IM(String token_IM) {
        this.token_IM = token_IM;
    }

    public String getCouponValidDate() {
        return couponValidDate;
    }

    public void setCouponValidDate(String couponValidDate) {
        this.couponValidDate = couponValidDate;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public int getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(int authStatus) {
        this.authStatus = authStatus;
    }

    public double getUsdCny() {
        return usdCny;
    }

    public void setUsdCny(double usdCny) {
        this.usdCny = usdCny;
    }

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }
}
