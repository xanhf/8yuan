package com.trade.eight.entity.home;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：Created by ocean
 * 时间：on 16/10/17.
 * 首页资讯
 */

public class HomeInformation implements Parcelable{

    // 资讯的类型
    public static final int GONGGAO =1;//公告
    public static final int ZAOJIANBUJU =2;//早间布局
    public static final int HANGQINGYUYAN =3;//行情预演
    public static final int JIAOYIJIHUI =4;//交易机会
    public static final int ZAOJIANBUJU_QIHUO =5;//早间布局
    public static final int ZHENGWUJIEPAN =6;//正午解盘
    public static final int WANJIANCELUE =7;//晚间策略

    private String articleId;//文章编号
    private String articleUrl;//文章地址
    private String authorHeadPortrait;//专家头像
    private int authorId;//专家id
    private String authorName;//专家名字
    private String authorPropose;//专家建议
    private int clickType;
    private long createTime;//创建时间

    private String informactionId;//资讯编号
    private String informactionProduct;//产品名称（油，银，咖啡，美元）
//    //类型（1公告，2早间布局，3行情预演，4交易机会，不填写表示所有 5 早间资讯
//    MORNING(5,"早间布局","33C8E1"),
//    NOON(6,"正午解盘","F5A245"),
//    NIGHT(7,"晚间策略","33C8E1");// ）
    private int informactionType;
    private String informactionAbstract;//摘要
    private String informactionContent;//内容
    private String informationImg;// 资讯图片
    private int less;//买跌
    private int more;//买涨
    private long reportTime;//倒计时时间
    private int sourceId;
    private int top;


    public String getInformationImg() {
        return informationImg;
    }

    public void setInformationImg(String informationImg) {
        this.informationImg = informationImg;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getAuthorHeadPortrait() {
        return authorHeadPortrait;
    }

    public void setAuthorHeadPortrait(String authorHeadPortrait) {
        this.authorHeadPortrait = authorHeadPortrait;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorPropose() {
        return authorPropose.trim();
    }

    public void setAuthorPropose(String authorPropose) {
        this.authorPropose = authorPropose;
    }

    public int getClickType() {
        return clickType;
    }

    public void setClickType(int clickType) {
        this.clickType = clickType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getInformactionAbstract() {
        return informactionAbstract.trim();
    }

    public void setInformactionAbstract(String informactionAbstract) {
        this.informactionAbstract = informactionAbstract;
    }

    public String getInformactionContent() {
        return informactionContent.trim();
    }

    public void setInformactionContent(String informactionContent) {
        this.informactionContent = informactionContent;
    }

    public String getInformactionId() {
        return informactionId;
    }

    public void setInformactionId(String informactionId) {
        this.informactionId = informactionId;
    }

    public String getInformactionProduct() {
        return informactionProduct;
    }

    public void setInformactionProduct(String informactionProduct) {
        this.informactionProduct = informactionProduct;
    }

    public int getInformactionType() {
        return informactionType;
    }

    public void setInformactionType(int informactionType) {
        this.informactionType = informactionType;
    }

    public int getLess() {
        return less;
    }

    public void setLess(int less) {
        this.less = less;
    }

    public int getMore() {
        return more;
    }

    public void setMore(int more) {
        this.more = more;
    }

    public long getReportTime() {
        return reportTime;
    }

    public void setReportTime(long reportTime) {
        this.reportTime = reportTime;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.articleId);
        dest.writeString(this.articleUrl);
        dest.writeString(this.authorHeadPortrait);
        dest.writeString(this.authorPropose);
        dest.writeString(this.informactionAbstract);
        dest.writeString(this.informactionContent);
        dest.writeString(this.informactionId);
        dest.writeString(this.informactionProduct);

        dest.writeInt(this.authorId);
        dest.writeInt(this.clickType);
        dest.writeInt(this.informactionType);
        dest.writeInt(this.less);
        dest.writeInt(this.more);
        dest.writeInt(this.sourceId);
        dest.writeInt(this.top);

        dest.writeLong(this.createTime);
        dest.writeLong(this.reportTime);
    }

    /**
     * 获取类型的title
     * @param type
     * @return
     */
    public String getInformationTypeName(int type){
        String title = "";
        switch (type) {
            case GONGGAO:
                title = "公告";
                break;
            case ZAOJIANBUJU:
                title = "早间布局";
                break;
            case HANGQINGYUYAN:
                title = "行情预演";
                break;
            case JIAOYIJIHUI:
                title = "交易机会";
                break;
            case ZAOJIANBUJU_QIHUO:
                title = "早间布局";
                break;
            case ZHENGWUJIEPAN:
                title = "正午解盘";
                break;
            case WANJIANCELUE:
                title = "晚间策略";
                break;
        }
        return title;
    }
}
