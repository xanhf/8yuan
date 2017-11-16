package com.trade.eight.moudle.home.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.entity.home.HomeInformation;
import com.trade.eight.kchart.fragment.KMinuteFragment;
import com.trade.eight.moudle.home.HomeInformationTimeDownEvent;
import com.trade.eight.moudle.home.HomeInformationUpOrDownClickEvent;
import com.trade.eight.moudle.timer.CountDownTask;
import com.trade.eight.moudle.timer.CountDownTimers;
import com.trade.eight.moudle.timer.Objects;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.view.RoundedBackgroundSpan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * 作者：Created by ocean
 * 时间：on 16/10/17.
 * 首页资讯适配器
 */
//public class HomeInformationAdapter extends ArrayAdapter<HomeInformation> {
public class HomeInformationAdapter extends BaseAdapter {

    private Context context;
    List<HomeInformation> list;

    protected SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
    String beforeTimeLabl = null;


    private CountDownTask mCountDownTask;

    public void setCountDownTask(CountDownTask countDownTask) {
        if (!Objects.equals(mCountDownTask, countDownTask)) {
            mCountDownTask = countDownTask;
        }
    }

    private void startCountDown(final int position, final HomeInformation homeInformation, View convertView) {
        if (mCountDownTask != null) {
            mCountDownTask.until(convertView, CountDownTask.elapsedRealtime() + (homeInformation.getReportTime() - System.currentTimeMillis()),
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

        TextView textView1 = (TextView) view.findViewById(R.id.tv_homeinformation_ri);
        textView1.setText(date[0]);

        TextView textView2 = (TextView) view.findViewById(R.id.tv_homeinformation_shi);
        textView2.setText(date[1]);
        TextView textView3 = (TextView) view.findViewById(R.id.tv_homeinformation_fen);
        textView3.setText(date[2]);
    }

    private void doOnFinish(int position, View view) {
        TextView textView1 = (TextView) view.findViewById(R.id.tv_homeinformation_ri);
        textView1.setText("00");
        TextView textView2 = (TextView) view.findViewById(R.id.tv_homeinformation_shi);
        textView2.setText("00");
        TextView textView3 = (TextView) view.findViewById(R.id.tv_homeinformation_fen);
        textView3.setText("00");
        EventBus.getDefault().post(new HomeInformationTimeDownEvent());// 发送刷新数据event
    }

    private void cancelCountDown(int position, HomeInformation homeInformation, View view) {
        if (mCountDownTask != null) {
            mCountDownTask.cancel(view);
        }
    }

    public HomeInformationAdapter(Context context, int resource, List<HomeInformation> objects) {

        this.context = context;
        this.list = objects;
    }

    public void setDataList(List<HomeInformation> objects) {
        this.list.clear();
        this.list.addAll(objects);
        notifyDataSetChanged();
        beforeTimeLabl = sdf.format(new Date(objects.get(0).getCreateTime()));
    }

    public void addDataList(List<HomeInformation> objects) {
        this.list.addAll(objects);
        notifyDataSetChanged();
    }

    public List<HomeInformation> getList() {
        return list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        int type = getItemViewType(position);
//
//        // 公告、早间布局
//        if (type == 0) {
//            ViewHolderType_1 viewHolderType_1 = null;
//            if (convertView == null) {
//                //初始化
//                convertView = View.inflate(context, R.layout.home_information_item_type_1, null);
//                viewHolderType_1 = new ViewHolderType_1();
//                viewHolderType_1.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
//                viewHolderType_1.view_devider = convertView.findViewById(R.id.view_devider);
//                viewHolderType_1.tv_homeinformation_title = (TextView) convertView.findViewById(R.id.tv_homeinformation_title);
//                viewHolderType_1.tv_homeinformation_display57 = (TextView) convertView.findViewById(R.id.tv_homeinformation_display57);
//                viewHolderType_1.tv_homeinformation_display33 = (TextView) convertView.findViewById(R.id.tv_homeinformation_display33);
//                viewHolderType_1.tv_homeinformation_time = (TextView) convertView.findViewById(R.id.tv_homeinformation_time);
//                viewHolderType_1.tv_homeinformation_teacher = (TextView) convertView.findViewById(R.id.tv_homeinformation_teacher);
//                convertView.setTag(viewHolderType_1);
//            } else {
//                viewHolderType_1 = (ViewHolderType_1) convertView.getTag();
//            }
//
//            HomeInformation homeInformation = list.get(position);
//            controlTopTimeLable(position, viewHolderType_1.tv_time, viewHolderType_1.view_devider);
//            if (homeInformation.getInformactionType() == 1) {//公告
//                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolderType_1.tv_homeinformation_display33.getLayoutParams();
//
//                String displayTitle = context.getResources().getString(R.string.home_information_type1, homeInformation.getInformactionContent());
//                SpannableString ss = new SpannableString(displayTitle);
//
//                float textSize = 12 * context.getResources().getDisplayMetrics().scaledDensity; // sp to px
//                ss.setSpan(new RoundedBackgroundSpan(context, layoutParams.width, layoutParams.height, textSize), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                ss.setSpan(new RelativeSizeSpan(1.25f), 2, displayTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                ss.setSpan(new StyleSpan(Typeface.BOLD), 2, displayTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                viewHolderType_1.tv_homeinformation_title.setText(ss);
//
//                viewHolderType_1.tv_homeinformation_time.setText(DateUtil.parseMillisSecondToHHmm(homeInformation.getCreateTime()));
//                viewHolderType_1.tv_homeinformation_teacher.setText(homeInformation.getAuthorName());
//            } else if (homeInformation.getInformactionType() == 2) {//早间布局
//                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolderType_1.tv_homeinformation_display57.getLayoutParams();
//                String displayTitle = context.getResources().getString(R.string.home_information_type2, homeInformation.getInformactionContent());
//                SpannableString ss = new SpannableString(displayTitle);
//                float textSize = 12 * context.getResources().getDisplayMetrics().scaledDensity; // sp to px
//                ss.setSpan(new RoundedBackgroundSpan(context, layoutParams.width, layoutParams.height, textSize), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                ss.setSpan(new RelativeSizeSpan(1.25f), 4, displayTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                ss.setSpan(new StyleSpan(Typeface.BOLD), 4, displayTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                viewHolderType_1.tv_homeinformation_title.setText(ss);
//                viewHolderType_1.tv_homeinformation_time.setText(DateUtil.parseMillisSecondToHHmm(homeInformation.getCreateTime()));
//                viewHolderType_1.tv_homeinformation_teacher.setText(homeInformation.getAuthorName());
//            } else if (homeInformation.getInformactionType() == 5) {//早间资讯
//                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolderType_1.tv_homeinformation_display57.getLayoutParams();
//                String displayTitle = context.getResources().getString(R.string.home_information_type5, homeInformation.getInformactionContent());
//                SpannableString ss = new SpannableString(displayTitle);
//                float textSize = 12 * context.getResources().getDisplayMetrics().scaledDensity; // sp to px
//                ss.setSpan(new RoundedBackgroundSpan(context, layoutParams.width, layoutParams.height, textSize), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                ss.setSpan(new RelativeSizeSpan(1.25f), 4, displayTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                ss.setSpan(new StyleSpan(Typeface.BOLD), 4, displayTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                viewHolderType_1.tv_homeinformation_title.setText(ss);
//                viewHolderType_1.tv_homeinformation_time.setText(DateUtil.parseMillisSecondToHHmm(homeInformation.getCreateTime()));
//                viewHolderType_1.tv_homeinformation_teacher.setText(homeInformation.getAuthorName());
//            }
//        }
//// 行情预演
//        if (type == 1) {
//            ViewHolderType_3 viewHolderType_3 = null;
//            if (convertView == null) {
//                //初始化
//                convertView = View.inflate(context, R.layout.home_information_item_type_3, null);
//                viewHolderType_3 = new ViewHolderType_3();
//                viewHolderType_3.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
//                viewHolderType_3.view_devider = convertView.findViewById(R.id.view_devider);
//                viewHolderType_3.tv_homeinformation_abstract = (TextView) convertView.findViewById(R.id.tv_homeinformation_abstract);
//                viewHolderType_3.tv_homeinformation_ri = (TextView) convertView.findViewById(R.id.tv_homeinformation_ri);
//                viewHolderType_3.tv_homeinformation_shi = (TextView) convertView.findViewById(R.id.tv_homeinformation_shi);
//                viewHolderType_3.tv_homeinformation_fen = (TextView) convertView.findViewById(R.id.tv_homeinformation_fen);
//                viewHolderType_3.tv_homeinformation_content = (TextView) convertView.findViewById(R.id.tv_homeinformation_content);
//                viewHolderType_3.btn_homeinformation_red = (Button) convertView.findViewById(R.id.btn_homeinformation_red);
//                viewHolderType_3.tv_homeinformation_redhold_percent = (TextView) convertView.findViewById(R.id.tv_homeinformation_redhold_percent);
//                viewHolderType_3.btn_homeinformation_green = (Button) convertView.findViewById(R.id.btn_homeinformation_green);
//                viewHolderType_3.tv_homeinformation_greenhold_percent = (TextView) convertView.findViewById(R.id.tv_homeinformation_greenhold_percent);
//                viewHolderType_3.text_homeinformation_red = (TextView) convertView.findViewById(R.id.text_homeinformation_red);
//                viewHolderType_3.text_homeinformation_green = (TextView) convertView.findViewById(R.id.text_homeinformation_green);
//                convertView.setTag(viewHolderType_3);
//            } else {
//                viewHolderType_3 = (ViewHolderType_3) convertView.getTag();
//            }
//            // 界面赋值
//            final HomeInformation homeInformation = list.get(position);
//            controlTopTimeLable(position, viewHolderType_3.tv_time, viewHolderType_3.view_devider);
//
//            viewHolderType_3.tv_homeinformation_abstract.setText(homeInformation.getInformactionAbstract());
//            viewHolderType_3.tv_homeinformation_content.setText(homeInformation.getInformactionContent());
//
//            int tradeUpPercent = ConvertUtil.calculateTwoIntPercent(homeInformation.getMore(), homeInformation.getLess());
//            viewHolderType_3.tv_homeinformation_redhold_percent.setText(getHoldPercent(R.string.home_information_redhold, tradeUpPercent));
//            viewHolderType_3.tv_homeinformation_greenhold_percent.setText(getHoldPercent(R.string.home_information_greenhold, 100 - tradeUpPercent));
//            viewHolderType_3.tv_homeinformation_ri.setTag(homeInformation);
//            final View tagView = convertView;
////            HomeInformationTimer homeInformationTimer = new HomeInformationTimer(homeInformation,tagView);
////            homeInformationTimer.setDisplayTextview(new TextView[]{viewHolderType_3.tv_homeinformation_ri, viewHolderType_3.tv_homeinformation_shi, viewHolderType_3.tv_homeinformation_fen}, homeInformation.getReportTime());
//            if (homeInformation.getReportTime() - System.currentTimeMillis() > 0) {
//                startCountDown(position, homeInformation, convertView);
//            } else {
//                cancelCountDown(position, homeInformation, convertView);
//            }
//
//            if(homeInformation.getClickType()==0){
//                viewHolderType_3.btn_homeinformation_red.setBackgroundResource(R.drawable.btn_homeinformation_red);
//                viewHolderType_3.btn_homeinformation_red.setSelected(false);
//                viewHolderType_3.btn_homeinformation_green.setBackgroundResource(R.drawable.btn_homeinformation_green);
//                viewHolderType_3.btn_homeinformation_green.setSelected(false);
//            }else if(homeInformation.getClickType()==1){// 点过后 对面变灰色
//                viewHolderType_3.btn_homeinformation_red.setBackgroundResource(R.drawable.btn_homeinformation_red);
//                viewHolderType_3.btn_homeinformation_red.setSelected(true);
//                viewHolderType_3.btn_homeinformation_green.setBackgroundResource(R.drawable.img_homeinformation_grey);
//                viewHolderType_3.btn_homeinformation_green.setSelected(false);
//            }else if(homeInformation.getClickType()==2){// 点过后 对面变灰色
//                viewHolderType_3.btn_homeinformation_red.setBackgroundResource(R.drawable.img_homeinformation_grey);
//                viewHolderType_3.btn_homeinformation_red.setSelected(false);
//                viewHolderType_3.btn_homeinformation_green.setBackgroundResource(R.drawable.btn_homeinformation_green);
//                viewHolderType_3.btn_homeinformation_green.setSelected(true);
//            }
//
//            viewHolderType_3.btn_homeinformation_red.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//
//                    EventBus.getDefault().post(new HomeInformationUpOrDownClickEvent(tagView, homeInformation, HomeInformationUpOrDownClickEvent.MORE, position));// 发送刷新数据event
//                }
//            });
//            viewHolderType_3.btn_homeinformation_green.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    EventBus.getDefault().post(new HomeInformationUpOrDownClickEvent(tagView, homeInformation, HomeInformationUpOrDownClickEvent.LESS, position));// 发送刷新数据event
//                }
//            });
//        }
//        // 交易机会
//        if (type == 2) {
//            ViewHolderType_4 viewHolderType_4 = null;
//            if (convertView == null) {
//                //初始化
//                convertView = View.inflate(context, R.layout.home_information_item_type_4, null);
//                viewHolderType_4 = new ViewHolderType_4();
//                viewHolderType_4.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
//                viewHolderType_4.view_devider = convertView.findViewById(R.id.view_devider);
//                viewHolderType_4.tv_homeinformation_product = (TextView) convertView.findViewById(R.id.tv_homeinformation_product);
//                viewHolderType_4.tv_homeinformation_abstract = (TextView) convertView.findViewById(R.id.tv_homeinformation_abstract);
//                viewHolderType_4.tv_homeinformation_time = (TextView) convertView.findViewById(R.id.tv_homeinformation_time);
//                viewHolderType_4.tv_homeinformation_display57 = (TextView) convertView.findViewById(R.id.tv_homeinformation_display57);
//                viewHolderType_4.tv_homeinformation_content = (TextView) convertView.findViewById(R.id.tv_homeinformation_content);
//                viewHolderType_4.tv_homeinformation_authorPropose = (TextView) convertView.findViewById(R.id.tv_homeinformation_authorPropose);
//                viewHolderType_4.tv_homeinformation_teacher = (TextView) convertView.findViewById(R.id.tv_homeinformation_teacher);
//                viewHolderType_4.tv_homeinformation_tradeup = (TextView) convertView.findViewById(R.id.tv_homeinformation_tradeup);
//                viewHolderType_4.view_homeinformation_tradeup = convertView.findViewById(R.id.view_homeinformation_tradeup);
//                viewHolderType_4.view_homeinformation_tradedown = convertView.findViewById(R.id.view_homeinformation_tradedown);
//                viewHolderType_4.tv_homeinformation_tradedown = (TextView) convertView.findViewById(R.id.tv_homeinformation_tradedown);
//                convertView.setTag(viewHolderType_4);
//            } else {
//                viewHolderType_4 = (ViewHolderType_4) convertView.getTag();
//            }
//            // 界面赋值
//            HomeInformation homeInformation = list.get(position);
//            controlTopTimeLable(position, viewHolderType_4.tv_time, viewHolderType_4.view_devider);
//
//            viewHolderType_4.tv_homeinformation_product.setText(homeInformation.getInformactionProduct());
//            if (homeInformation.getInformactionAbstract().contains("多") || homeInformation.getInformactionAbstract().contains("涨")) {
//                viewHolderType_4.tv_homeinformation_abstract.setTextColor(context.getResources().getColor(R.color.trade_up));
//            } else {
//                viewHolderType_4.tv_homeinformation_abstract.setTextColor(context.getResources().getColor(R.color.trade_down));
//
//            }
//            viewHolderType_4.tv_homeinformation_abstract.setText(homeInformation.getInformactionAbstract());
//            viewHolderType_4.tv_homeinformation_time.setText(DateUtil.parseMillisSecondToHHmm(homeInformation.getCreateTime()));
//            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolderType_4.tv_homeinformation_display57.getLayoutParams();
//            String displayTitle = context.getResources().getString(R.string.home_information_type4_1, homeInformation.getInformactionContent());
//            SpannableString ss = new SpannableString(displayTitle);
//            float textSize = 12 * context.getResources().getDisplayMetrics().scaledDensity; // sp to px
//            ss.setSpan(new RoundedBackgroundSpan(context, layoutParams.width, layoutParams.height, textSize), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            ss.setSpan(new RelativeSizeSpan(1.25f), 4, displayTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            ss.setSpan(new StyleSpan(Typeface.BOLD), 4, displayTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            viewHolderType_4.tv_homeinformation_content.setText(ss);
////            viewHolderType_4.tv_homeinformation_content.setText(homeInformation.getInformactionContent());
//            if (TextUtils.isEmpty(homeInformation.getAuthorPropose())) {
//                viewHolderType_4.tv_homeinformation_authorPropose.setVisibility(View.GONE);
//                viewHolderType_4.tv_homeinformation_authorPropose.setText("");
//            } else {
//                viewHolderType_4.tv_homeinformation_authorPropose.setVisibility(View.VISIBLE);
//                viewHolderType_4.tv_homeinformation_authorPropose.setText("建议: " + homeInformation.getAuthorPropose());
//            }
//            viewHolderType_4.tv_homeinformation_teacher.setText(homeInformation.getAuthorName());
//            int tradeUpPercent = ConvertUtil.calculateTwoIntPercent(homeInformation.getMore(), homeInformation.getLess());
//            viewHolderType_4.tv_homeinformation_tradeup.setText(context.getResources().getString(R.string.home_information_tradeup, tradeUpPercent, "%"));
//            LinearLayout.LayoutParams view_homeinformation_tradeup_lp_old = (LinearLayout.LayoutParams) viewHolderType_4.view_homeinformation_tradeup.getLayoutParams();
//            LinearLayout.LayoutParams view_homeinformation_tradeup_lp_new = new LinearLayout.LayoutParams(0, view_homeinformation_tradeup_lp_old.height, tradeUpPercent);
//            viewHolderType_4.view_homeinformation_tradeup.setLayoutParams(view_homeinformation_tradeup_lp_new);
//
//            LinearLayout.LayoutParams view_homeinformation_tradedown_lp_old = (LinearLayout.LayoutParams) viewHolderType_4.view_homeinformation_tradedown.getLayoutParams();
//            LinearLayout.LayoutParams view_homeinformation_tradedown_lp_new = new LinearLayout.LayoutParams(0, view_homeinformation_tradedown_lp_old.height, 100 - tradeUpPercent);
//            viewHolderType_4.view_homeinformation_tradedown.setLayoutParams(view_homeinformation_tradedown_lp_new);
//            viewHolderType_4.tv_homeinformation_tradedown.setText(context.getResources().getString(R.string.home_information_tradedown, (100 - tradeUpPercent), "%"));
//
//        }


        if (convertView == null) {
            convertView = View.inflate(context, R.layout.home_information_item, null);
        }
        TextView text_information_type = MyViewHolder.get(convertView, R.id.text_information_type);
        TextView text_information_title = MyViewHolder.get(convertView, R.id.text_information_title);
        TextView text_information_content = MyViewHolder.get(convertView, R.id.text_information_content);
        ImageView img_information = MyViewHolder.get(convertView, R.id.img_information);
        TextView tv_homeinformation_teacher = MyViewHolder.get(convertView, R.id.tv_homeinformation_teacher);
        TextView tv_homeinformation_time = MyViewHolder.get(convertView, R.id.tv_homeinformation_time);
        HomeInformation homeInformation = list.get(position);

        int type = homeInformation.getInformactionType();
        if (type == HomeInformation.GONGGAO) {
            text_information_type.setText(R.string.lable_gonggao);
            if (!TextUtils.isEmpty(homeInformation.getInformactionContent())) {
                text_information_content.setVisibility(View.VISIBLE);
                text_information_content.setText(homeInformation.getInformactionContent());
            } else {
                text_information_content.setVisibility(View.GONE);
            }
        } else {
            switch (type) {
                case HomeInformation.ZAOJIANBUJU_QIHUO:
                    text_information_type.setText(R.string.lable_morning);
                    break;
                case HomeInformation.ZHENGWUJIEPAN:
                    text_information_type.setText(R.string.lable_noon);
                    break;
                case HomeInformation.WANJIANCELUE:
                    text_information_type.setText(R.string.lable_night);
                    break;
            }
            text_information_title.setText(context.getResources().getString(R.string.lable_prefix, homeInformation.getInformactionAbstract()));
        }

        if (!TextUtils.isEmpty(homeInformation.getInformationImg())) {
            img_information.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(homeInformation.getInformationImg(),
                    img_information, AppImageLoaderConfig.getCommonDisplayImageOptions(context, BaseInterface.getLoadingDrawable(context, true)));
        } else {
            img_information.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(homeInformation.getInformactionContent())) {
            text_information_content.setVisibility(View.VISIBLE);
            text_information_content.setText(homeInformation.getInformactionContent());
        } else {
            text_information_content.setVisibility(View.GONE);
        }

        tv_homeinformation_teacher.setText(homeInformation.getAuthorName());
        tv_homeinformation_time.setText(DateUtil.getHomeInformationTime(homeInformation.getCreateTime()));
        return convertView;
    }


    /**
     * 控制顶部时间lable devider的显示
     *
     * @param position      // 当前索引
     * @param tvTimeLable// 顶部时间lable
     * @param viewDevider// 顶部分割线
     */
    private void controlTopTimeLable(int position,
                                     TextView tvTimeLable,
                                     View viewDevider) {
        HomeInformation homeInformationCurrent = list.get(position);
        if (position == 0) {
            // 置顶逻辑start  把置顶归到当天
            if (homeInformationCurrent.getTop() > 0) {
                // 只替换年月日  防止刷新导致时间一直变
                homeInformationCurrent.setCreateTime(DateUtil.changeDateYMD(homeInformationCurrent.getCreateTime()));
            }
            // 置顶逻辑end  把置顶归到当天
            String currentTimeLabl = sdf.format(new Date(homeInformationCurrent.getCreateTime()));
            if (beforeTimeLabl.equals(currentTimeLabl)) {
                tvTimeLable.setVisibility(View.GONE);
                viewDevider.setVisibility(View.GONE);
            } else {
                viewDevider.setVisibility(View.GONE);
                tvTimeLable.setVisibility(View.VISIBLE);
                tvTimeLable.setText(currentTimeLabl);
            }
            return;
        }
        HomeInformation homeInformationBefore = list.get(position - 1);
        // 置顶逻辑start  把置顶归到当天
        if (homeInformationBefore.getTop() > 0) {
            // 只替换年月日  防止刷新导致时间一直变
            homeInformationCurrent.setCreateTime(DateUtil.changeDateYMD(homeInformationCurrent.getCreateTime()));
        }
        // 置顶逻辑end  把置顶归到当天
        String currentTime = sdf.format(new Date(homeInformationCurrent.getCreateTime()));
        String beforeTime = sdf.format(new Date(homeInformationBefore.getCreateTime()));
        if (currentTime.equals(beforeTime)) {
            tvTimeLable.setVisibility(View.GONE);
            viewDevider.setVisibility(View.VISIBLE);
        } else {
            viewDevider.setVisibility(View.GONE);
            tvTimeLable.setVisibility(View.VISIBLE);
            tvTimeLable.setText(currentTime);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public HomeInformation getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub

        HomeInformation homeInformation = getItem(position);

        if (homeInformation.getInformactionType() == 0) {
            return 0;
        }

        return 1;
    }

    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 3;
    }

    /**
     * 公告 早间布局
     */
    private class ViewHolderType_1 {
        TextView tv_time;
        View view_devider;
        TextView tv_homeinformation_title;
        TextView tv_homeinformation_display57;
        TextView tv_homeinformation_display33;
        TextView tv_homeinformation_time;
        TextView tv_homeinformation_teacher;

    }

    /**
     * 行情预演
     */
    public class ViewHolderType_3 {
        TextView tv_time;
        View view_devider;
        TextView tv_homeinformation_abstract;
        public TextView tv_homeinformation_ri;
        public TextView tv_homeinformation_shi;
        public TextView tv_homeinformation_fen;
        TextView tv_homeinformation_content;
        public Button btn_homeinformation_red;
        public TextView tv_homeinformation_redhold_percent;
        public Button btn_homeinformation_green;
        public TextView tv_homeinformation_greenhold_percent;
        public TextView text_homeinformation_red;
        public TextView text_homeinformation_green;
    }

    /**
     * 交易机会
     */
    private class ViewHolderType_4 {
        TextView tv_time;
        View view_devider;
        TextView tv_homeinformation_product;
        TextView tv_homeinformation_abstract;
        TextView tv_homeinformation_time;
        TextView tv_homeinformation_display57;
        TextView tv_homeinformation_content;
        TextView tv_homeinformation_authorPropose;
        TextView tv_homeinformation_teacher;
        TextView tv_homeinformation_tradeup;
        View view_homeinformation_tradeup;
        View view_homeinformation_tradedown;
        TextView tv_homeinformation_tradedown;
    }

    public SpannableString getHoldPercent(int id, int percent) {
        String tradeUpPercentString = context.getResources().getString(id, percent, "%");
        SpannableString spannableString = new SpannableString(tradeUpPercentString);
        spannableString.setSpan(new RelativeSizeSpan(0.75f), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
