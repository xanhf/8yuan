package com.trade.eight.moudle.me.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.entity.integral.GoodsActData;
import com.trade.eight.entity.integral.GoodsActGiftData;
import com.trade.eight.entity.integral.GoodsData;
import com.trade.eight.entity.integral.IntegralProductData;
import com.trade.eight.moudle.home.HomeInformationTimeDownEvent;
import com.trade.eight.moudle.me.CreateIntegralOrderEvent;
import com.trade.eight.moudle.timer.CountDownTask;
import com.trade.eight.moudle.timer.CountDownTimers;
import com.trade.eight.moudle.timer.Objects;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/30.
 * 积分商城 商品适配器
 */

public class GoodsAdapter extends BaseExpandableListAdapter {

    private String TAG = GoodsAdapter.class.getSimpleName();

    Context context;
    GoodsData goodsData;
    List<List<IntegralProductData>> listVoucher = new ArrayList<List<IntegralProductData>>();

    public int GROUPTYPE_ACT = 0;
    public int GROUPTYPE_VOUCHER = 1;

    public int CHILDTYPE_ACT = 0;
    public int CHILDTYPE_VOUCHER = 1;

    private CountDownTask mCountDownTask;

    public void setCountDownTask(CountDownTask countDownTask) {
        if (!Objects.equals(mCountDownTask, countDownTask)) {
            mCountDownTask = countDownTask;
        }
    }

    private void startCountDown(final int position, final GoodsActData goodsActData, View convertView) {
        if (mCountDownTask != null) {
            mCountDownTask.until(convertView, CountDownTask.elapsedRealtime() + (goodsActData.getActEndTime() - goodsActData.getCurrTime()),
                    1000, new CountDownTimers.OnCountDownListener() {
                        @Override
                        public void onTick(View view, long millisUntilFinished) {

                            doOnTick(position, view, millisUntilFinished, 1000);
                        }

                        @Override
                        public void onFinish(View view) {
                            doOnFinish(position, view);
                        }
                    });
        }
    }

    private void doOnTick(int position, View view, long millisUntilFinished, long countDownInterval) {
        String[] date = DateUtil.formatDateDecentHMS(millisUntilFinished / 1000);

        TextView textView1 = (TextView) view.findViewById(R.id.text_goodsact_shi);
        textView1.setText(date[0]);

        TextView textView2 = (TextView) view.findViewById(R.id.text_goodsact_fen);
        textView2.setText(date[1]);
        TextView textView3 = (TextView) view.findViewById(R.id.text_goodsact_miao);
        textView3.setText(date[2]);
    }

    private void doOnFinish(int position, View view) {
        TextView textView1 = (TextView) view.findViewById(R.id.text_goodsact_shi);
        textView1.setText("00");
        TextView textView2 = (TextView) view.findViewById(R.id.text_goodsact_fen);
        textView2.setText("00");
        TextView textView3 = (TextView) view.findViewById(R.id.text_goodsact_miao);
        textView3.setText("00");
        EventBus.getDefault().post(new HomeInformationTimeDownEvent());// 发送刷新数据event
    }

    private void cancelCountDown(int position, GoodsActData goodsActData, View view) {
        if (mCountDownTask != null) {
            mCountDownTask.cancel(view);
        }
    }

    public GoodsAdapter(Context context, GoodsData goodsData) {
        this.context = context;
        this.goodsData = goodsData;
    }

    public void setGoodsData(GoodsData goodsData) {
        this.goodsData = goodsData;
        groupVoucherList(goodsData.getVoucherList());
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        int goodsActSize = 0;
        if (goodsData.getActivityList() != null) {
            goodsActSize = goodsData.getActivityList().size();
        }
        return goodsActSize + listVoucher.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int goodsActSize = 0;
        if (goodsData.getActivityList() != null) {
            goodsActSize = goodsData.getActivityList().size();
        }
        if (groupPosition >= goodsActSize) {
            return listVoucher.get(groupPosition - goodsActSize).size();
        }
        return goodsData.getActivityList().get(groupPosition).getGiftList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        int goodsActSize = 0;
        if (goodsData.getActivityList() != null) {
            goodsActSize = goodsData.getActivityList().size();
        }
        Log.e(TAG, groupPosition + "==========groupPosition======");

        if (groupPosition >= goodsActSize) {
            return listVoucher.get(groupPosition - goodsActSize).get(0);
        }
        return goodsData.getActivityList().get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        int goodsActSize = 0;
        if (goodsData.getActivityList() != null) {
            goodsActSize = goodsData.getActivityList().size();
        }
        if (groupPosition >= goodsActSize) {
            return listVoucher.get(groupPosition - goodsActSize).get(childPosition);
        }
        return goodsData.getActivityList().get(groupPosition).getGiftList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        int type = getGroupType(groupPosition);
        if (type == GROUPTYPE_ACT) {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_goodsact_group, null);
            }
            TextView text_goodsact_title = MyViewHolder.get(convertView, R.id.text_goodsact_title);
            TextView text_goodsact_desc = MyViewHolder.get(convertView, R.id.text_goodsact_desc);

            GoodsActData goodsActData = (GoodsActData) getGroup(groupPosition);
            text_goodsact_title.setText(goodsActData.getActTitle());
            text_goodsact_desc.setText(goodsActData.getActDesc());

            if (goodsActData.getActEndTime() - goodsActData.getCurrTime() > 0) {
                startCountDown(groupPosition, goodsActData, convertView);
            } else {
                cancelCountDown(groupPosition, goodsActData, convertView);
            }
        } else if (type == GROUPTYPE_VOUCHER) {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_goodsvoucher_group, null);
            }
            TextView text_integral_exchangename = MyViewHolder.get(convertView, R.id.text_integral_exchangename);
            IntegralProductData integralProductData = (IntegralProductData) getGroup(groupPosition);
            switch (integralProductData.getExcode()) {
                case 1:
                    text_integral_exchangename.setText("广贵所");
                    break;
                case 2:
                    text_integral_exchangename.setText("哈贵所");
                    break;
                case 3:
                    text_integral_exchangename.setText("农交所");
                    break;
                case 4:
                    text_integral_exchangename.setText("华凝所");
                    break;
            }
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        int type = getGroupType(groupPosition);
        if (type == GROUPTYPE_ACT) {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_goodsact_child, null);
            }
            ImageView img_goodsact_pic = MyViewHolder.get(convertView, R.id.img_goodsact_pic);
            TextView text_giftname = MyViewHolder.get(convertView, R.id.text_giftname);
            TextView text_giftleft = MyViewHolder.get(convertView, R.id.text_giftleft);
            TextView text_giftexcount = MyViewHolder.get(convertView, R.id.text_giftexcount);
            TextView btn_exnow = MyViewHolder.get(convertView, R.id.btn_exnow);
            TextView text_giftjifen = MyViewHolder.get(convertView, R.id.text_giftjifen);

            GoodsActGiftData goodsActGiftData = (GoodsActGiftData) getChild(groupPosition, childPosition);
            ImageLoader.getInstance().displayImage(goodsActGiftData.getGiftPic(), img_goodsact_pic, AppImageLoaderConfig.getCommonDisplayImageOptions(context, R.drawable.img_te_default));
            text_giftname.setText(goodsActGiftData.getGiftName());
            text_giftjifen.setText(context.getResources().getString(R.string.intergral_point, StringUtil.forNumber(goodsActGiftData.getPoins())));
            text_giftexcount.setText(context.getResources().getString(R.string.intergral_takenum, StringUtil.forNumber(goodsActGiftData.getTakeNum())));
            text_giftleft.setText(context.getResources().getString(R.string.intergral_takeleft, StringUtil.forNumber(goodsActGiftData.getGiftLimitNum())));

            if (goodsActGiftData.getBuyStatus() == GoodsActGiftData.BUYSTATUS_NO) {
                btn_exnow.setText("马上抢");
                btn_exnow.setTextColor(context.getResources().getColor(R.color.sub_blue));
                if (!TextUtils.isEmpty(goodsActGiftData.getBtnTextColor())) {
                    btn_exnow.setTextColor(Color.parseColor(goodsActGiftData.getBtnTextColor()));
                }
                btn_exnow.setBackgroundResource(R.drawable.white_round);
                GoodsActData goodsActData = (GoodsActData) getGroup(groupPosition);
                if (goodsActData.getStatus() == GoodsActData.CHANGESTATUS_YES) {
                    btn_exnow.setTextColor(context.getResources().getColor(R.color.white_30));
                    btn_exnow.setBackgroundResource(R.drawable.white_round_30);
                    if (!TextUtils.isEmpty(goodsActGiftData.getSubTextColorOpacity())) {
                        GradientDrawable mBitmapDrawable = (GradientDrawable) context.getResources().getDrawable(R.drawable.white_round_30);
                        mBitmapDrawable.setStroke(Utils.dip2px(context,0.5f),Color.parseColor(goodsActGiftData.getSubTextColorOpacity()));
                        int sdk = android.os.Build.VERSION.SDK_INT;
                        if (sdk >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            btn_exnow.setBackground(mBitmapDrawable);
                        } else {
                            btn_exnow.setBackgroundDrawable(mBitmapDrawable);
                        }
                        btn_exnow.setTextColor(Color.parseColor(goodsActGiftData.getSubTextColorOpacity()));
                    }
                }
            } else if (goodsActGiftData.getBuyStatus() == GoodsActGiftData.BUYSTATUS_YES) {
                btn_exnow.setText("已抢购");
                btn_exnow.setTextColor(context.getResources().getColor(R.color.white));
                btn_exnow.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            } else if (goodsActGiftData.getBuyStatus() == GoodsActGiftData.BUYSTATUS_EMPTY) {
                btn_exnow.setTextColor(context.getResources().getColor(R.color.white));
                btn_exnow.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                btn_exnow.setText("已抢光");
            }
        } else if (type == GROUPTYPE_VOUCHER) {
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_goodsvoucher_child, null);
            }
            ImageView img_integralpic = MyViewHolder.get(convertView, R.id.img_integralpic);
            TextView text_integral_productname = MyViewHolder.get(convertView, R.id.text_integral_productname);
            TextView text_integral_need = MyViewHolder.get(convertView, R.id.text_integral_need);
            TextView text_integral_takenum = MyViewHolder.get(convertView, R.id.text_integral_takenum);
            Button btn_integralexchange = MyViewHolder.get(convertView, R.id.btn_integralexchange);

            final IntegralProductData integralProductData = (IntegralProductData) getChild(groupPosition, childPosition);
            ImageLoader.getInstance().displayImage(integralProductData.getGiftPic(), img_integralpic, AppImageLoaderConfig.getCommonDisplayImageOptions(context, R.drawable.img_integralmarket_default));
            text_integral_productname.setText(integralProductData.getGiftName());
            text_integral_need.setText(context.getResources().getString(R.string.intergral_point, StringUtil.forNumber(integralProductData.getPoins())));
            text_integral_takenum.setText(context.getResources().getString(R.string.intergral_takenum, StringUtil.forNumber(integralProductData.getTakeNum())));

            btn_integralexchange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new CreateIntegralOrderEvent(integralProductData));
                }
            });
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getGroupType(int groupPosition) {
        int goodsActSize = 0;
        if (goodsData.getActivityList() != null) {
            goodsActSize = goodsData.getActivityList().size();
        }
        if (groupPosition >= goodsActSize) {
            return GROUPTYPE_VOUCHER;
        }
        return GROUPTYPE_ACT;
    }

    @Override
    public int getGroupTypeCount() {
        return 2;
    }

    @Override
    public int getChildTypeCount() {
        return 2;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        int goodsActSize = 0;
        if (goodsData.getActivityList() != null) {
            goodsActSize = goodsData.getActivityList().size();
        }
        if (groupPosition >= goodsActSize) {
            return CHILDTYPE_VOUCHER;
        }
        return CHILDTYPE_ACT;
    }

    /**
     * 将代金券分组
     *
     * @param voucherList
     */
    public void groupVoucherList(List<IntegralProductData> voucherList) {
        //最外层的list
        List<List<IntegralProductData>> mainList = new ArrayList<>();
        //当前类别 所有的品种
        List<IntegralProductData> detailList = new ArrayList<>();
        //重新整理成list
        if (voucherList != null && voucherList.size() > 0) {
            Map<String, String> filterIds = new HashMap<>();
            for (int i = 0; i < voucherList.size(); i++) {
                IntegralProductData integralProductData = voucherList.get(i);
                int contract = integralProductData.getExcode();
                if (StringUtil.isEmpty(String.valueOf(contract)))
                    continue;
                if (filterIds.containsKey(String.valueOf(contract))) {
                    continue;
                }
                detailList = new ArrayList<>();
                mainList.add(detailList);
                filterIds.put(String.valueOf(contract), String.valueOf(contract));
                for (int j = 0; j < voucherList.size(); j++) {
                    if (String.valueOf(contract).equals(String.valueOf(voucherList.get(j).getExcode()))) {
                        detailList.add(voucherList.get(j));
                    }
                }
            }
        }
        Log.e(TAG, mainList.size() + "================");
        this.listVoucher = mainList;
    }
}
