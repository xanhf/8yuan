package com.trade.eight.moudle.me.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.integral.AccountIntegralData;
import com.trade.eight.entity.integral.GoodsActData;
import com.trade.eight.entity.integral.GoodsActGiftData;
import com.trade.eight.entity.integral.GoodsActGiftDetailData;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.me.CreateIntegralOrderSuccessEvent;
import com.trade.eight.moudle.timer.CountDownTask;
import com.trade.eight.moudle.timer.CountDownTimers;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.net.okhttp.NetCallback;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.BitMapUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.PreferenceSetting;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.Utils;
import com.trade.eight.view.TextViewBorder;

import java.text.DecimalFormat;
import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/30.
 * 特权卡详情页
 */

public class GoodsActDetailActivity extends BaseActivity {
    GoodsActData goodsActData;
    GoodsActGiftData goodsActGiftData;
    AccountIntegralData integralDataForMarket;
    GoodsActGiftDetailData goodsActGiftDetailData;

    View rootview;
    ImageView img_goodsact_pic;
    TextView text_gift_limitnum;
    TextView text_giftname;
    TextView text_giftpoins;
    TextView text_giftpoins_debeat;
    TextViewBorder text_rebatinfo;
    TextView text_goodsact_shi;
    TextView text_goodsact_fen;
    TextView text_goodsact_miao;
    TextView text_takenum;
    TextView text_gift_limitnum_1;
    TextView text_giftname_1;
    TextView text_giftdesc;
    TextView text_giftauthdesc;
    TextView text_giftrule;
    TextView text_giftmask;
    TextView text_giftpoins_debeat_1;
    TextView text_userdebeat_info;
    TextView text_user_integral;
    TextView text_buynow;

    View line_optionview;// 底部的view

    private CountDownTask mCountDownTask;
    // 进度条颜色值
    int[] accoutLvColor = {Color.parseColor("#FFB601"), Color.parseColor("#FF9F01"), Color.parseColor("#FF7901"),
            Color.parseColor("#FF5101"), Color.parseColor("#FF3A01"), Color.parseColor("#FF0000"),
            Color.parseColor("#FF006A")};

    public static void startAct(Context context, GoodsActData goodsActData, GoodsActGiftData goodsActGiftData, AccountIntegralData integralDataForMarket) {
        Intent intent = new Intent();
        intent.setClass(context, GoodsActDetailActivity.class);
        intent.putExtra("goodsActData", goodsActData);
        intent.putExtra("goodsActGiftData", goodsActGiftData);
        intent.putExtra("integralDataForMarket", integralDataForMarket);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_goodsactdetail);
        initView();
        initData();
    }

    private void initData() {
        goodsActData = (GoodsActData) getIntent().getSerializableExtra("goodsActData");
        goodsActGiftData = (GoodsActGiftData) getIntent().getSerializableExtra("goodsActGiftData");
        integralDataForMarket = (AccountIntegralData) getIntent().getSerializableExtra("integralDataForMarket");
        setAppCommonTitle(goodsActGiftData.getGiftName());
        getDetailData();
    }

    private void initView() {
        rootview = findViewById(R.id.rootview);
        img_goodsact_pic = (ImageView) findViewById(R.id.img_goodsact_pic);
        text_gift_limitnum = (TextView) findViewById(R.id.text_gift_limitnum);
        text_giftname = (TextView) findViewById(R.id.text_giftname);
        text_giftpoins = (TextView) findViewById(R.id.text_giftpoins);
        text_giftpoins_debeat = (TextView) findViewById(R.id.text_giftpoins_debeat);
        text_rebatinfo = (TextViewBorder) findViewById(R.id.text_rebatinfo);
        text_goodsact_shi = (TextView) findViewById(R.id.text_goodsact_shi);
        text_goodsact_fen = (TextView) findViewById(R.id.text_goodsact_fen);
        text_goodsact_miao = (TextView) findViewById(R.id.text_goodsact_miao);
        text_takenum = (TextView) findViewById(R.id.text_takenum);
        text_gift_limitnum_1 = (TextView) findViewById(R.id.text_gift_limitnum_1);
        text_giftname_1 = (TextView) findViewById(R.id.text_giftname_1);
        text_giftdesc = (TextView) findViewById(R.id.text_giftdesc);
        text_giftauthdesc = (TextView) findViewById(R.id.text_giftauthdesc);
        text_giftrule = (TextView) findViewById(R.id.text_giftrule);
        text_giftmask = (TextView) findViewById(R.id.text_giftmask);
        text_giftpoins_debeat_1 = (TextView) findViewById(R.id.text_giftpoins_debeat_1);
        text_userdebeat_info = (TextView) findViewById(R.id.text_userdebeat_info);
        text_user_integral = (TextView) findViewById(R.id.text_user_integral);
        text_buynow = (TextView) findViewById(R.id.text_buynow);
        line_optionview = findViewById(R.id.line_optionview);

        text_buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showTitleAndContentDialog(GoodsActDetailActivity.this,
                        getResources().getString(R.string.integral_exchangedlgtitle, goodsActGiftData.getGiftName(), NumberUtil.roundUp(NumberUtil.multiply(goodsActGiftData.getPoins(), Double.parseDouble(integralDataForMarket.getRebateRate())), 0)),
                        getResources().getString(R.string.integral_exchangedlgcontent),
                        getResources().getString(R.string.close_btn_neg),
                        getResources().getString(R.string.integral_exchangedlg_posbtn),
                        null,
                        new Handler.Callback() {

                            @Override
                            public boolean handleMessage(Message msg) {
                                doExchange();
                                return false;
                            }
                        });
            }
        });

        mCountDownTask = CountDownTask.create();
    }


    private void displayViews() {
        ImageLoader.getInstance().displayImage(goodsActGiftDetailData.getGiftBigPic(),
                img_goodsact_pic,
                AppImageLoaderConfig.getCommonDisplayImageOptions(GoodsActDetailActivity.this, R.drawable.img_te_default),
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
                            int width = Utils.getScreenW(GoodsActDetailActivity.this)-Utils.dip2px(GoodsActDetailActivity.this,20f);
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
        text_gift_limitnum.setText(getResources().getString(R.string.intergral_takeleft, StringUtil.forNumber(goodsActGiftData.getGiftLimitNum())));
        text_giftname.setText(goodsActGiftData.getGiftName());

        text_rebatinfo.setTextColor(accoutLvColor[integralDataForMarket.getLevelNum() - 1]);
        text_rebatinfo.setBorderColor(accoutLvColor[integralDataForMarket.getLevelNum() - 1]);

        double rebaterate = NumberUtil.multiply(10, Double.parseDouble(integralDataForMarket.getRebateRate()));
        if (rebaterate == 10) {
            text_userdebeat_info.setText("V1会员无优惠");
            text_rebatinfo.setText("V1会员无优惠");

            String str = getResources().getString(R.string.intergral_point, goodsActGiftData.getPoins() + "");
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
            text_userdebeat_info.setText(getResources().getString(R.string.integral_rebatnum, rebaterateStr));
            text_rebatinfo.setText(getResources().getString(R.string.intergral_rebattips, integralDataForMarket.getLevelName(), rebaterateStr));
            String str = getResources().getString(R.string.intergral_needpoint, NumberUtil.roundUp(NumberUtil.multiply(goodsActGiftData.getPoins(), Double.parseDouble(integralDataForMarket.getRebateRate())), 0), goodsActGiftData.getPoins() + "");
            SpannableString spannableString = new SpannableString(str);
            spannableString.setSpan(new RelativeSizeSpan(0.5f), str.indexOf("积"), str.indexOf("积") + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_common_content)), str.indexOf("积"), str.indexOf("积") + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(0.5f), str.indexOf("分") + 1, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.grey)), str.indexOf("分") + 1, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StrikethroughSpan(), str.indexOf("分") + 1, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            text_giftpoins_debeat.setText(spannableString);
            text_giftpoins_debeat.setText(spannableString);
        }
        text_giftpoins.setText(getResources().getString(R.string.intergral_point, NumberUtil.roundUp(NumberUtil.multiply(goodsActGiftData.getPoins(), Double.parseDouble(integralDataForMarket.getRebateRate())), 0)));
        text_giftpoins_debeat_1.setText(NumberUtil.roundUp(NumberUtil.multiply(goodsActGiftData.getPoins(), Double.parseDouble(integralDataForMarket.getRebateRate())), 0));
        if (goodsActData.getActEndTime() - goodsActData.getCurrTime() > 0) {
            startCountDown(goodsActData, rootview);
        } else {
            cancelCountDown(goodsActData, rootview);
        }
        text_takenum.setText(getResources().getString(R.string.intergral_takenum, StringUtil.forNumber(goodsActGiftData.getTakeNum())));
        text_gift_limitnum_1.setText(getResources().getString(R.string.intergral_takeleft, StringUtil.forNumber(goodsActGiftData.getGiftLimitNum())));
        text_giftname_1.setText(goodsActGiftData.getGiftName());
        text_giftdesc.setText(getBoldSpan(ConvertUtil.NVL(goodsActGiftDetailData.getGiftDesc(),"")));
        text_giftauthdesc.setText(getBoldSpan(ConvertUtil.NVL(goodsActGiftDetailData.getGiftAuthDesc(),"")));
        text_giftrule.setText(getBoldSpan(ConvertUtil.NVL(goodsActGiftDetailData.getGiftRuleDesc(),"")));
        text_giftmask.setText(goodsActGiftDetailData.getGiftRemark());
        text_user_integral.setText(getResources().getString(R.string.integral_left, integralDataForMarket.getValidPoints() + ""));

        if (goodsActGiftData.getBuyStatus() == GoodsActGiftData.BUYSTATUS_NO) {
            text_buynow.setText("马上抢");
            text_buynow.setBackgroundColor(getResources().getColor(R.color.color_opt_gt));
        } else if (goodsActGiftData.getBuyStatus() == GoodsActGiftData.BUYSTATUS_YES) {
            text_buynow.setText("已抢购");
            text_buynow.setBackgroundColor(getResources().getColor(R.color.grey_30));
            text_buynow.setClickable(false);
        } else if (goodsActGiftData.getBuyStatus() == GoodsActGiftData.BUYSTATUS_EMPTY) {
            text_buynow.setBackgroundColor(getResources().getColor(R.color.grey_30));
            text_buynow.setText("已抢光");
            text_buynow.setClickable(false);
        }
        if(goodsActData.getStatus()==GoodsActData.CHANGESTATUS_YES){
            text_buynow.setBackgroundColor(getResources().getColor(R.color.grey_30));
            text_buynow.setClickable(false);
        }
    }

    private void getDetailData() {
        HashMap<String, String> request = new HashMap<String, String>();
        final UserInfoDao userInfoDao = new UserInfoDao(this);
        request.put("userId", userInfoDao.queryUserInfo().getUserId());
        request.put("giftId", goodsActGiftData.getGiftId() + "");
        HttpClientHelper.doPostOption(GoodsActDetailActivity.this, AndroidAPIConfig.URL_ACTGIFT_DETAIL, request, null, new NetCallback(GoodsActDetailActivity.this) {
            @Override
            public void onFailure(String resultCode, String resultMsg) {
                showCusToast(resultMsg);
                GoodsActDetailActivity.this.finish();
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

    private void doExchange() {
        UserInfoDao userInfoDao = new UserInfoDao(GoodsActDetailActivity.this);
        if (!userInfoDao.isLogin()) {
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(UserInfo.UID, userInfoDao.queryUserInfo().getUserId());
        long lvVersion = PreferenceSetting.getInt(GoodsActDetailActivity.this, PreferenceSetting.ACCOUNT_INTEGRAL_VERSION);
        if (lvVersion < 0) {
            lvVersion = 0;
        }
        map.put("versionNo", lvVersion + "");
        map.put("giftId", goodsActGiftData.getGiftId() + "");
        map.put("giftNum", "1");
        HttpClientHelper.doPostOption(GoodsActDetailActivity.this, AndroidAPIConfig.URL_ACCOUNT_INTEGRAL_EXCHANGE, map, null, new NetCallback(GoodsActDetailActivity.this) {

            @Override
            public void onFailure(String resultCode, String resultMsg) {
                showCusToast(resultMsg);
            }

            @Override
            public void onResponse(String response) {
                CommonResponse<AccountIntegralData> accountIntegralData = CommonResponse.fromJson(response, AccountIntegralData.class);
                integralDataForMarket = accountIntegralData.getData();
                if (integralDataForMarket != null) {
                    long lvVersion = PreferenceSetting.getInt(GoodsActDetailActivity.this, PreferenceSetting.ACCOUNT_INTEGRAL_VERSION);
                    if (lvVersion == integralDataForMarket.getVersionNo()) {
                    } else {
                        PreferenceSetting.setInt(GoodsActDetailActivity.this, PreferenceSetting.ACCOUNT_INTEGRAL_VERSION, integralDataForMarket.getVersionNo());
                        String lv = new Gson().toJson(integralDataForMarket.getLevelList());
                        PreferenceSetting.setString(GoodsActDetailActivity.this, PreferenceSetting.ACCOUNT_INTEGRAL_INFO, lv);
                    }
                    EventBus.getDefault().post(new CreateIntegralOrderSuccessEvent(integralDataForMarket));
                }
                String msg = getResources().getString(R.string.integral_create_success);
                DialogUtil.showSuccessSmallDialog(GoodsActDetailActivity.this, msg, 0, null);
            }
        }, true);
    }

    private void startCountDown(final GoodsActData goodsActData, View convertView) {
        if (mCountDownTask != null) {
            mCountDownTask.until(convertView, CountDownTask.elapsedRealtime() + (goodsActData.getActEndTime() - goodsActData.getCurrTime()),
                    1000, new CountDownTimers.OnCountDownListener() {
                        @Override
                        public void onTick(View view, long millisUntilFinished) {

                            doOnTick(view, millisUntilFinished, 1000);
                        }

                        @Override
                        public void onFinish(View view) {
                            doOnFinish(view);
                        }
                    });
        }
    }

    private void doOnTick(View view, long millisUntilFinished, long countDownInterval) {
        String[] date = DateUtil.formatDateDecentHMS(millisUntilFinished / 1000);

        TextView textView1 = (TextView) view.findViewById(R.id.text_goodsact_shi);
        textView1.setText(date[0]);

        TextView textView2 = (TextView) view.findViewById(R.id.text_goodsact_fen);
        textView2.setText(date[1]);
        TextView textView3 = (TextView) view.findViewById(R.id.text_goodsact_miao);
        textView3.setText(date[2]);
    }

    private void doOnFinish(View view) {
        TextView textView1 = (TextView) view.findViewById(R.id.text_goodsact_shi);
        textView1.setText("00");
        TextView textView2 = (TextView) view.findViewById(R.id.text_goodsact_fen);
        textView2.setText("00");
        TextView textView3 = (TextView) view.findViewById(R.id.text_goodsact_miao);
        textView3.setText("00");
    }

    private void cancelCountDown(GoodsActData goodsActData, View view) {
        if (mCountDownTask != null) {
            mCountDownTask.cancel(view);
        }
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
