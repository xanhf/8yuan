package com.trade.eight.moudle.me.adapter;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.easylife.ten.lib.R;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.entity.ShareEntity;
import com.trade.eight.entity.missioncenter.MissionBannerData;
import com.trade.eight.entity.missioncenter.MissionTaskData;
import com.trade.eight.moudle.me.activity.AnswerQuestionAct;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.tools.BaseInterface;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.OpenActivityUtil;
import com.trade.eight.tools.ShareTools;

import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/3.
 */

public class MyViewPagerAdapter extends PagerAdapter {
    private List<View> mListViews;
    List<MissionBannerData> pagerItemList;
    List<MissionTaskData> missionTaskDataList;
    BaseActivity context;

    public MyViewPagerAdapter(BaseActivity context,List<View> mListViews, List<MissionBannerData> pagerItemList,List<MissionTaskData> objects) {
        this.mListViews = mListViews;
        this.pagerItemList = pagerItemList;
        this.context = context;
        this.missionTaskDataList = objects;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        position = position % pagerItemList.size();
        container.removeView(mListViews.get(position));//删除页卡
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {    //这个方法用来实例化页卡

        final int mPos = position % pagerItemList.size();
        container.addView(mListViews.get(mPos), 0);//添加页卡

        View itemView = mListViews.get(mPos);
        ImageView item_img = (ImageView) itemView.findViewById(R.id.item_img);
        ImageLoader.getInstance().displayImage(pagerItemList.get(mPos).getPic(),
                item_img, AppImageLoaderConfig.getCommonDisplayImageOptions(context, BaseInterface.getLoadingDrawable(context, true)));
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MyAppMobclickAgent.onEvent(context, "page_missioncenter", "banner_" + mPos + ConvertUtil.NVL(pagerItemList.get(mPos).getLinkTitle(), ""));
//                    currentDotView.setSelected(true);//第一次click 会失去selected效果，这里强制设置 true
//                    String url = pagerItemList.get(mPos).getLink();
//                    if (url != null && url.startsWith(OpenActivityUtil.SCHEME_SUB)) {
//                        context.startActivity(OpenActivityUtil.getIntent(context, url));
//                    } else {
//                        Intent intent = new Intent();
//                        intent.setClass(context, WebActivity.class);
//                        intent.putExtra("url", url);
//                        intent.putExtra("title", pagerItemList.get(mPos).getLinkTitle());
//                        context.startActivity(intent);
//                    }

                    switch (pagerItemList.get(mPos).getLinkType()) {//1=答题，2=分享，3=协议跳转，4=H5跳转
                        case 1:
                            for(MissionTaskData missionTaskData : missionTaskDataList){
                                if(missionTaskData.getTaskId()== Long.parseLong(pagerItemList.get(mPos).getLink())){
                                    AnswerQuestionAct.startAct(context, missionTaskData);
                                }
                            }
                            break;
                        case 2:
                            ShareTools shareTools = new ShareTools(context);
                            ShareEntity entity = new Gson().fromJson(pagerItemList.get(mPos).getLink(), ShareEntity.class);
                            //弹出选择分享平台
                            shareTools.showShareDialog(context, entity,true);
                            break;
                        case 3:
                            context.startActivity(OpenActivityUtil.getIntent(context, pagerItemList.get(mPos).getLink()));
                            break;
                        case 4:
                            Intent intent = new Intent();
                            intent.setClass(context, WebActivity.class);
                            intent.putExtra("url", pagerItemList.get(mPos).getLink());
                            intent.putExtra("title", pagerItemList.get(mPos).getLinkTitle());
                            context.startActivity(intent);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        return mListViews.get(mPos);
    }

    @Override
    public int getCount() {
        return mListViews.size();//返回页卡的数量
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;//官方提示这样写
    }
}
