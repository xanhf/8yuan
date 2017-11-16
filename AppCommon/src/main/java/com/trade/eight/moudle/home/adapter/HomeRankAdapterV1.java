package com.trade.eight.moudle.home.adapter;

import android.content.Context;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.RankItem;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.Utils;
import com.trade.eight.view.CircleImageView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 首页盈利排行榜
 */
public class HomeRankAdapterV1 extends ArrayAdapter<RankItem> {
    List<RankItem> objects;
    Context context;
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    public HomeRankAdapterV1(Context context, int resource, List<RankItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.home_rank_item_v1, null);
        }
        View line_homerank_item = MyViewHolder.get(convertView, R.id.line_homerank_item);
        TextView tv_icon = MyViewHolder.get(convertView, R.id.tv_icon);
        CircleImageView imgPerson = MyViewHolder.get(convertView, R.id.imgPerson);
        TextView tv_nickname = MyViewHolder.get(convertView, R.id.tv_nickname);
        TextView tv_profit = MyViewHolder.get(convertView, R.id.tv_profit);
        TextView tv_mark = MyViewHolder.get(convertView, R.id.tv_mark);
        View timeLayout = MyViewHolder.get(convertView, R.id.timeLayout);
        TextView tv_time = MyViewHolder.get(convertView, R.id.tv_time);
        TextView tv_avatartips = MyViewHolder.get(convertView, R.id.tv_avatartips);


        View rela_homerank_item = MyViewHolder.get(convertView, R.id.rela_homerank_item);
        TextView tv_icon_laterfouth = MyViewHolder.get(convertView, R.id.tv_icon_laterfouth);
        CircleImageView imgPerson_laterfouth = MyViewHolder.get(convertView, R.id.imgPerson_laterfouth);
        TextView tv_nickname_laterfouth = MyViewHolder.get(convertView, R.id.tv_nickname_laterfouth);
        TextView tv_profit_laterfouth = MyViewHolder.get(convertView, R.id.tv_profit_laterfouth);
        TextView tv_mark_laterfouth = MyViewHolder.get(convertView, R.id.tv_mark_laterfouth);
        TextView tv_avatartips_laterfouth = MyViewHolder.get(convertView, R.id.tv_avatartips_laterfouth);

        RankItem item = objects.get(position);
        UserInfoDao dao = new UserInfoDao(context);
        if (dao.isLogin()) {

            if (Utils.getMD5(dao.queryUserInfo().getUserId() + item.getOrderId()).equals(item.getUod())) {
                line_homerank_item.setBackgroundColor(context.getResources().getColor(R.color.rank_itemforself));
                rela_homerank_item.setBackgroundColor(context.getResources().getColor(R.color.rank_itemforself));
                TextPaint paint = tv_nickname.getPaint();
                paint.setFakeBoldText(true);
                TextPaint paint_1 = tv_nickname_laterfouth.getPaint();
                paint_1.setFakeBoldText(true);
            } else {
                line_homerank_item.setBackgroundColor(context.getResources().getColor(R.color.white));
                rela_homerank_item.setBackgroundColor(context.getResources().getColor(R.color.white));
                TextPaint paint = tv_nickname.getPaint();
                paint.setFakeBoldText(false);
                TextPaint paint_1 = tv_nickname_laterfouth.getPaint();
                paint_1.setFakeBoldText(false);
            }
        }
        int index = item.getIndex();
        if (index > 2) {
            line_homerank_item.setVisibility(View.GONE);
            rela_homerank_item.setVisibility(View.VISIBLE);

            ImageLoader.getInstance().displayImage(item.getAvatar(), imgPerson_laterfouth, AppImageLoaderConfig.getCommonDisplayImageOptions(context, R.drawable.liveroom_icon_person));


            tv_icon_laterfouth.setText((index + 1) + "");
            tv_icon_laterfouth.setTextColor(context.getResources().getColor(R.color.grey));
            tv_nickname_laterfouth.setText(ConvertUtil.NVL(item.getNickName(), ""));
            try {
                tv_profit_laterfouth.setText((int) (Double.parseDouble(ConvertUtil.NVL(item.getProfitRate(), "").replace("%", ""))) + "%");
            } catch (Exception e) {
                e.printStackTrace();
                tv_profit_laterfouth.setText(item.getProfitRate());
            }

            try {
                tv_time.setText(item.getCloseDate());
                timeLayout.setVisibility(View.GONE);
                if (index == 0) {
                    if (position == 0) {
                        tv_time.setText(item.getCloseDate() + " " + context.getResources().getString(R.string.home_rank_hint));
                    }
                    timeLayout.setVisibility(View.VISIBLE);
                } else {
                    timeLayout.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                timeLayout.setVisibility(View.GONE);
                e.printStackTrace();
            }

            if (tv_mark_laterfouth != null) {
                if (item.getGiveVoucher() != null) {
                    tv_mark_laterfouth.setText(context.getResources().getString(R.string.money_lable) + ConvertUtil.NVL(item.getGiveVoucher(), "--"));
                    tv_mark_laterfouth.setBackgroundResource(R.drawable.home_pic_ticket_grey);
                }
            }
        } else {
            line_homerank_item.setVisibility(View.VISIBLE);
            rela_homerank_item.setVisibility(View.GONE);

            ImageLoader.getInstance().displayImage(item.getAvatar(), imgPerson, AppImageLoaderConfig.getCommonDisplayImageOptions(context, R.drawable.liveroom_icon_person));


            if (index == 0) {
                tv_icon.setText("");
                tv_icon.setBackgroundResource(R.drawable.img_ranklist_first);
            } else if (index == 1) {
                tv_icon.setText("");
                tv_icon.setBackgroundResource(R.drawable.img_ranklist_second);
            } else if (index == 2) {
                tv_icon.setText("");
                tv_icon.setBackgroundResource(R.drawable.img_ranklist_third);
            }
            tv_icon.setTextColor(context.getResources().getColor(R.color.white));
            tv_nickname.setText(ConvertUtil.NVL(item.getNickName(), ""));
            try {
                tv_profit.setText((int) (Double.parseDouble(ConvertUtil.NVL(item.getProfitRate(), "").replace("%", ""))) + "%");
            } catch (Exception e) {
                e.printStackTrace();
                tv_profit.setText(item.getProfitRate());
            }

            try {
                tv_time.setText(item.getCloseDate());
                timeLayout.setVisibility(View.GONE);
                if (index == 0) {
                    if (position == 0) {
                        tv_time.setText(item.getCloseDate() + " " + context.getResources().getString(R.string.home_rank_hint));
                    }
                    timeLayout.setVisibility(View.VISIBLE);
                } else {
                    timeLayout.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                timeLayout.setVisibility(View.GONE);
                e.printStackTrace();
            }

            if (tv_mark != null) {
                if (item.getGiveVoucher() != null) {
                    tv_mark.setText(context.getResources().getString(R.string.money_lable) + ConvertUtil.NVL(item.getGiveVoucher(), "--"));
                    tv_mark.setBackgroundResource(R.drawable.home_pic_ticket_red);
                }
            }


        }
//        String currentCloseDate = sdf.format(new Date());
        if (item.getStatus() == 1) {
            tv_avatartips_laterfouth.setText("预计奖励");
            tv_avatartips.setText("预计奖励");
        } else {
            tv_avatartips_laterfouth.setText("奖励");
            tv_avatartips.setText("奖励");
        }
        return convertView;
    }


}
