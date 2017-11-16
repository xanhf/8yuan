package com.trade.eight.moudle.chatroom.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.moudle.chatroom.gift.GiftObj;
import com.trade.eight.moudle.chatroom.gift.GiftPanUtil;
import com.trade.eight.tools.BaseInterface;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dufangzhu on 2017/4/5.
 */

public class GiftPagerAdapter extends PagerAdapter {
    private List<View> mListViews;
    /*目前是一页放三个*/
    List<List<GiftObj>> pagerItemList;
    Context context;

    public GiftPagerAdapter(Context context, List<View> mListViews, List<List<GiftObj>> pagerItemList) {
        this.context = context;
        this.mListViews = mListViews;
        this.pagerItemList = pagerItemList;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        position = position % pagerItemList.size();
        container.removeView(mListViews.get(position));//删除页卡
    }

    /*选中的itemview*/
    View selectedItemV = null;
    /*选中的礼物*/
    GiftObj selectObj;

    @Override
    public Object instantiateItem(ViewGroup container, int position) {    //这个方法用来实例化页卡
        final int mPos = position % pagerItemList.size();
        container.addView(mListViews.get(mPos), 0);//添加页卡

        View containerView = mListViews.get(mPos);
        View item01 = containerView.findViewById(R.id.item01);
        View item02 = containerView.findViewById(R.id.item02);
        View item03 = containerView.findViewById(R.id.item03);

        ArrayList<View> viewArrayList = new ArrayList<>();
        viewArrayList.add(item01);
        viewArrayList.add(item02);
        viewArrayList.add(item03);

        for (int i = 0; i < viewArrayList.size(); i++) {
            final View itemView = viewArrayList.get(i);
            if (i > pagerItemList.get(mPos).size() - 1) {
                itemView.setVisibility(View.GONE);
                return containerView;
            } else {
                itemView.setVisibility(View.VISIBLE);
            }
            final GiftObj obj = pagerItemList.get(mPos).get(i);
            ImageView item_img = (ImageView) itemView.findViewById(R.id.item_img);

            //优先获取本地图片，本地图片如果没有
            if (GiftPanUtil.giftRes.containsKey(obj.getGiftId())) {
                item_img.setImageResource(GiftPanUtil.giftRes.get(obj.getGiftId()));
            } else {
                //网络加载
                if (GiftPanUtil.giftCashMap.containsKey(obj.getGiftId())) {
                    GiftObj cashobj = GiftPanUtil.giftCashMap.get(obj.getGiftId());
                    ImageLoader.getInstance().displayImage(cashobj.getGiftPic(),
                            item_img, AppImageLoaderConfig.getCommonDisplayImageOptions(context, BaseInterface.getLoadingDrawable(context, false)));

                }
            }

//            ImageLoader.getInstance().displayImage(obj.getGiftPic(),
//                    item_img, AppImageLoaderConfig.getCommonDisplayImageOptions(context, BaseInterface.getLoadingDrawable(context, false)));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClick(obj, itemView);
                }
            });
            if (i == 0) {
                itemClick(obj, itemView);
            }
            TextView tv_point = (TextView) itemView.findViewById(R.id.tv_point);
            tv_point.setText(obj.getPoins() + "");
        }
        return containerView;
    }

    void itemClick(GiftObj obj, View itemView) {
        try {
            selectObj = obj;
            //更新选中效果
            if (selectedItemV != null) {
                //已经选中再次点击
                if (itemView.equals(selectedItemV)) {
                    //直接发送
                    if (callback != null) {
                        callback.handleMessage(new Message());
                    }
                    return;
                }

                selectedItemV.setSelected(false);
                View checkView = selectedItemV.findViewById(R.id.checkView);
                if (checkView != null)
                    checkView.setVisibility(View.GONE);
            }

            //更改状态
            selectedItemV = itemView;
            selectedItemV.setSelected(true);
            View checkView = selectedItemV.findViewById(R.id.checkView);
            if (checkView != null)
                checkView.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return mListViews.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    public GiftObj getSelectObj() {
        return selectObj;
    }

    public void setSelectObj(GiftObj selectObj) {
        this.selectObj = selectObj;
    }

    Handler.Callback callback;

    public Handler.Callback getCallback() {
        return callback;
    }

    public void setCallback(Handler.Callback callback) {
        this.callback = callback;
    }
}
