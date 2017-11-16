package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.entity.integral.ExHistoryData;
import com.trade.eight.entity.integral.GoodsActGiftDetailData;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.BitMapUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.Utils;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/30.
 * 特权卡详情页--兑换历史
 */

public class GoodsActDetailHistoryActivity extends BaseActivity {
    GoodsActGiftDetailData goodsActGiftDetailData;

    View rootview;
    ImageView img_goodsact_pic;
    TextView text_giftname;
    TextView text_giftpoins;
    TextView text_giftpoins_debeat;
    TextView text_rebatinfo;

    TextView text_extime;
    TextView text_giftdesc;
    TextView text_giftauthdesc;
    TextView text_giftrule;
    TextView text_giftmask;

    ExHistoryData exHistoryData;

    public static void startAct(Context context, ExHistoryData exHistoryData) {
        Intent intent = new Intent();
        intent.setClass(context, GoodsActDetailHistoryActivity.class);
        intent.putExtra("exHistoryData", exHistoryData);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_goodsactdetail_history);
        initView();
        initData();
    }

    private void initData() {
        exHistoryData = (ExHistoryData) getIntent().getSerializableExtra("exHistoryData");

        getDetailData();
    }

    private void initView() {
        rootview = findViewById(R.id.rootview);
        img_goodsact_pic = (ImageView) findViewById(R.id.img_goodsact_pic);
        text_giftname = (TextView) findViewById(R.id.text_giftname);
        text_extime = (TextView) findViewById(R.id.text_extime);
        text_giftpoins = (TextView) findViewById(R.id.text_giftpoins);
        text_giftpoins_debeat = (TextView) findViewById(R.id.text_giftpoins_debeat);
        text_rebatinfo = (TextView) findViewById(R.id.text_rebatinfo);
        text_giftdesc = (TextView) findViewById(R.id.text_giftdesc);
        text_giftauthdesc = (TextView) findViewById(R.id.text_giftauthdesc);
        text_giftrule = (TextView) findViewById(R.id.text_giftrule);
        text_giftmask = (TextView) findViewById(R.id.text_giftmask);
    }


    private void displayViews() {

        setAppCommonTitle(goodsActGiftDetailData.getGiftName());

        ImageLoader.getInstance().displayImage(goodsActGiftDetailData.getGiftBigPic(),
                img_goodsact_pic,
                AppImageLoaderConfig.getCommonDisplayImageOptions(GoodsActDetailHistoryActivity.this, R.drawable.img_te_default),
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        if(bitmap!=null){// 图片等比例缩放
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) img_goodsact_pic.getLayoutParams();
                            int width = Utils.getScreenW(GoodsActDetailHistoryActivity.this)-Utils.dip2px(GoodsActDetailHistoryActivity.this,20f);
                            int height = (int)(width/(float)bitmap.getWidth()*bitmap.getHeight());
                            layoutParams.width = width;
                            layoutParams.height = height;
                            img_goodsact_pic.setLayoutParams(layoutParams);
                            img_goodsact_pic.setImageBitmap(BitMapUtil.scaleBitmap(bitmap,width,height));
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                }
        );
        text_giftname.setText(goodsActGiftDetailData.getGiftName());

        double rebaterate = NumberUtil.multiply(10, Double.parseDouble(goodsActGiftDetailData.getRebateRate()));
        if (rebaterate == 10) {
            text_rebatinfo.setText("V1会员无优惠");

            String str = getResources().getString(R.string.intergral_point, goodsActGiftDetailData.getGiftPoins() + "");
            SpannableString spannableString = new SpannableString(str);
            spannableString.setSpan(new RelativeSizeSpan(0.5f), str.indexOf("积"), str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_common_content)), str.indexOf("积"), str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text_giftpoins_debeat.setText(spannableString);

        } else {
            String rebaterateStr = new DecimalFormat("0.0").format(rebaterate);
            String lastRebaterateStr = rebaterateStr.substring(2, 3);
            if (Integer.parseInt(lastRebaterateStr) > 0) {

            } else {
                rebaterateStr = rebaterateStr.substring(0, 1);
            }
            text_rebatinfo.setText(getResources().getString(R.string.intergral_rebattips, goodsActGiftDetailData.getLevelName(), rebaterateStr));
            String str = getResources().getString(R.string.intergral_needpoint, NumberUtil.roundUp(NumberUtil.multiply(goodsActGiftDetailData.getGiftPoins(), Double.parseDouble(goodsActGiftDetailData.getRebateRate())), 0), goodsActGiftDetailData.getGiftPoins() + "");
            SpannableString spannableString = new SpannableString(str);
            spannableString.setSpan(new RelativeSizeSpan(0.5f), str.indexOf("积"), str.indexOf("积") + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_common_content)), str.indexOf("积"), str.indexOf("积") + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(0.5f), str.indexOf("分") + 1, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.grey)), str.indexOf("分") + 1, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StrikethroughSpan(), str.indexOf("分") + 1, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text_giftpoins_debeat.setText(spannableString);
            text_giftpoins_debeat.setText(spannableString);
        }
        text_giftpoins.setText(getResources().getString(R.string.intergral_point, NumberUtil.roundUp(NumberUtil.multiply(goodsActGiftDetailData.getGiftPoins(), Double.parseDouble(goodsActGiftDetailData.getRebateRate())), 0)));
        text_giftdesc.setText(getBoldSpan(ConvertUtil.NVL(goodsActGiftDetailData.getGiftDesc(),"")));
        text_giftauthdesc.setText(getBoldSpan(ConvertUtil.NVL(goodsActGiftDetailData.getGiftAuthDesc(),"")));
        text_giftrule.setText(getBoldSpan(ConvertUtil.NVL(goodsActGiftDetailData.getGiftRuleDesc(),"")));
        text_giftmask.setText(goodsActGiftDetailData.getGiftRemark());
        text_extime.setText(exHistoryData.getCreateTimeStr());
    }

    private void getDetailData() {
        HashMap<String, String> request = new HashMap<String, String>();
        request.put("historyRecId", exHistoryData.getHistoryRecId() + "");
        HttpClientHelper.doPostOption(GoodsActDetailHistoryActivity.this, AndroidAPIConfig.URL_ACTGIFT_DETAIL_HISTORY, request, null, new NetCallback(GoodsActDetailHistoryActivity.this) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                showCusToast(resultMsg);
                GoodsActDetailHistoryActivity.this.finish();
            }

            @Override
            public void onResponse(String response) {
                CommonResponse<GoodsActGiftDetailData> commonResponse = CommonResponse.fromJson(response, GoodsActGiftDetailData.class);
                goodsActGiftDetailData = commonResponse.getData();
                if (goodsActGiftDetailData != null) {
                    displayViews();
                }

            }
        }, true);
    }
    /**
     * 首行加粗
     * @param text
     * @return
     */
    private SpannableString getBoldSpan(String text){
        String giftDesc = text.replace("r&n", "\n");
        SpannableString ss = new SpannableString(giftDesc);
        ss.setSpan(new StyleSpan(Typeface.BOLD),0,giftDesc.indexOf("\n"), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return ss;
    }
}
