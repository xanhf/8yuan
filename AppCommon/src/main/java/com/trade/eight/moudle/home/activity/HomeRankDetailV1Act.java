package com.trade.eight.moudle.home.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.entity.RankOrder;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Created by developer
 * 榜单详情
 */
public class HomeRankDetailV1Act extends BaseActivity {
    HomeRankDetailV1Act context = this;
    String orderId;
    String exchangeId;
    String rate;
    String mark;
    String nickName;
    int index;
    String closeTime;

    protected SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderId = getIntent().getStringExtra("id");
        exchangeId = getIntent().getStringExtra("exchangeId");
        rate = getIntent().getStringExtra("rate");
        mark = getIntent().getStringExtra("mark");
        index = getIntent().getIntExtra("index", 1);
        nickName = getIntent().getStringExtra("nickName");
        closeTime = getIntent().getStringExtra("closeTime");
        setContentView(R.layout.act_rank_detail_v1);

        setAppCommonTitle("盈利详情");
        getData();

    }

    void getData() {
        new AsyncTask<Void, Void, CommonResponse<RankOrder>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingView();
            }

            @Override
            protected CommonResponse<RankOrder> doInBackground(Void... params) {
                try {
                    String api = AndroidAPIConfig.URL_PROFIT_RANK_DETAIL;
                    Map<String, String> map = ApiConfig.getCommonMap(context);
                    map.put(TradeProduct.PARAM_ORDER_ID, orderId);
                    map.put(TradeProduct.PARAM_EXCHANGE_ID, exchangeId);
                    map.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, map));

                    String res = HttpClientHelper.getStringFromPost(context, api, map);
                    return CommonResponse.fromJson(res, RankOrder.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(CommonResponse<RankOrder> rankOrderCommonResponse) {
                super.onPostExecute(rankOrderCommonResponse);
                hideLoadingView();
                if (rankOrderCommonResponse == null)
                    return;
                if (!rankOrderCommonResponse.isSuccess())
                    return;
                if (rankOrderCommonResponse.getData() == null)
                    return;
                initValue(rankOrderCommonResponse.getData());
            }
        }.execute();
    }

    void initValue(RankOrder item) {
        if (item == null)
            return;

        TextView tv_rankdetail_time = (TextView) findViewById(R.id.tv_rankdetail_time);
        ImageView imgPerson = (ImageView) findViewById(R.id.img_rankdetail_head);
        ImageView img_rankdetail_headdefault = (ImageView) findViewById(R.id.img_rankdetail_headdefault);
        TextView tv_rankdetail_index = (TextView) findViewById(R.id.tv_rankdetail_index);
        TextView tv_rankdetail_nickname = (TextView) findViewById(R.id.tv_rankdetail_nickname);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        TextView tv_count = (TextView) findViewById(R.id.tv_count);
        TextView tv_priceCreate = (TextView) findViewById(R.id.tv_priceCreate);
        TextView tv_timeCreate = (TextView) findViewById(R.id.tv_timeCreate);
        TextView tv_priceClose = (TextView) findViewById(R.id.tv_priceClose);
        TextView tv_timeClose = (TextView) findViewById(R.id.tv_timeClose);
        TextView tv_profit = (TextView) findViewById(R.id.tv_profit);
        TextView tv_rateProfit = (TextView) findViewById(R.id.tv_rateProfit);
        TextView tv_fee = (TextView) findViewById(R.id.tv_fee);
        TextView tv_closeType = (TextView) findViewById(R.id.tv_closeType);
        TextView tv_mark = (TextView) findViewById(R.id.tv_mark);
        View view_buyType = findViewById(R.id.view_buyType);
        TextView tv_buyType = (TextView) findViewById(R.id.tv_buyType);
        try {
            SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd");
            tv_rankdetail_time.setText(sdf.format(sdf_1.parse(closeTime))+"排名");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        switch (index) {
            case 0:
                img_rankdetail_headdefault.setBackgroundResource(R.drawable.img_ranhhead_first);
                break;
            case 1:
                img_rankdetail_headdefault.setBackgroundResource(R.drawable.img_ranhhead_second);

                break;
            case 2:
                img_rankdetail_headdefault.setBackgroundResource(R.drawable.img_ranhhead_third);

                break;
            default:
                img_rankdetail_headdefault.setBackgroundResource(R.drawable.img_ranhhead_fouth);
                tv_rankdetail_index.setText((index + 1) + "");
        }
        if (imgPerson != null) {
            if (!StringUtil.isEmpty(item.getAvatar()))
                ImageLoader.getInstance().displayImage(item.getAvatar(), imgPerson, AppImageLoaderConfig.getCommonDisplayImageOptions(context, R.drawable.liveroom_icon_person));
        }
        tv_rankdetail_nickname.setText(nickName);
        String str = ConvertUtil.NVL(item.getProductName(), "");
        if (!StringUtil.isEmpty(ConvertUtil.NVL(item.getWeight(), ""))) {
            str = str + " " + ConvertUtil.NVL(item.getWeight(), "");
            str = str + "" + ConvertUtil.NVL(item.getUnit(), "");
            String p = ConvertUtil.NVL(item.getProductPrice(), "");
            if (!StringUtil.isEmpty(p))
                str = str + " (" + p + "元/手)";
        }
//        str = str + " (" + ConvertUtil.NVL(item.getPrice(), "--") + "元/手)";


        tv_title.setText(str);
        String type = ConvertUtil.NVL(item.getType(), "");
        if (type.contains("跌")) {

            tv_buyType.setTextColor(getResources().getColor(R.color.color_opt_lt));
            view_buyType.setBackgroundColor(getResources().getColor(R.color.color_opt_lt));
        }
        tv_buyType.setText(type);

        if (tv_count != null) {
            tv_count.setText(item.getOrderNumber());
        }
        if (tv_fee != null) {
            tv_fee.setText(item.getFee());
        }
        String sourceF = "yyyy-MM-dd HH:mm:ss";
        String f = "MM-dd HH:mm:ss";

        if (tv_priceCreate != null)
            tv_priceCreate.setText(item.getCreatePrice());
        if (tv_timeCreate != null)
            tv_timeCreate.setText(DateUtil.formatDate(item.getCreateTime(), sourceF, f));
        if (tv_priceClose != null)
            tv_priceClose.setText(item.getClosePrice());
        if (tv_timeClose != null)
            tv_timeClose.setText(DateUtil.formatDate(item.getCloseTime(), sourceF, f));
        if (tv_closeType != null)
            tv_closeType.setText(item.getCloseType());
        if (tv_profit != null) {
            String ss = item.getProfitLoss() + " 元";
            SpannableString spannableStringProfit = new SpannableString(ss);
            spannableStringProfit.setSpan(new RelativeSizeSpan(0.6f), ss.length() - 1, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_profit.setText(spannableStringProfit);
        }

        if (tv_rateProfit != null) {
            SpannableString spannableStringProfitRate = new SpannableString(rate);
            spannableStringProfitRate.setSpan(new RelativeSizeSpan(0.6f), rate.length() - 1, rate.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_rateProfit.setText(spannableStringProfitRate);
//        tv_rateProfit.setText(ConvertUtil.NVL(rate, "").replace("%", ""));
        }
        if (tv_mark != null) {
            if (mark != null) {
                tv_mark.setText(getResources().getString(R.string.money_lable) + ConvertUtil.NVL(mark, "--"));
            }
        }


    }


}
