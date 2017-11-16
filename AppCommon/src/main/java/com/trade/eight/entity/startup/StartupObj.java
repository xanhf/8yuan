package com.trade.eight.entity.startup;

import android.content.Context;

import com.trade.eight.tools.PreferenceSetting;

import java.io.Serializable;

/**
 * Created by fangzhu on 16/10/9.
 * 开启app的配置信息 二级配置信息
 * tcp连接(首页弹窗广告，android应用推荐换量)
 */
public class StartupObj implements Serializable {
    /*长链接ip地址*/
    private String ip;
    /*长链接端口*/
    private int port;
    /*api接口加密的key，这里的key先要用MyBase64解密，然后用AesUtil解密*/
    private String k;


    //首页是否显示 窗口
    private boolean showImage;
    //首页-->大赛小窗口
    private String gameUrl;


    //我的页面-->应用推荐
    private String recommendAppUrl;

    //首页广告图
    private String image;
    //首页广告点击链接
    private String link;
    //是否显示首页广告
    private boolean show;
    //是否广告标题
    private String title;



    public String getGameUrl() {
        return gameUrl;
    }

    public void setGameUrl(String gameUrl) {
        this.gameUrl = gameUrl;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public boolean isShowImage() {
        return showImage;
    }

    public void setShowImage(boolean showImage) {
        this.showImage = showImage;
    }

    public String getRecommendAppUrl() {
        return recommendAppUrl;
    }

    public void setRecommendAppUrl(String recommendAppUrl) {
        this.recommendAppUrl = recommendAppUrl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取本地缓存的启动接口的key
     * @param context
     * @return
     */
    public static String getLocalKey (Context context) {
        return PreferenceSetting.getString(context, "start_up_key");
    }

    /**
     * 缓存启动接口的key
     * @param context
     * @param key
     */
    public static void setLocalKey (Context context, String key) {
        PreferenceSetting.setString(context, "start_up_key", key);
    }
}
