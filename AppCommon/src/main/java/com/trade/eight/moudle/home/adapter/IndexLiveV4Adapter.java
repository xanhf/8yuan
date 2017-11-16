package com.trade.eight.moudle.home.adapter;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.live.LiveRoomNew;
import com.trade.eight.moudle.chatroom.IndexLiveItemClickEvent;
import com.trade.eight.moudle.home.fragment.IndexLiveFragment;
import com.trade.eight.moudle.me.activity.LoginActivity;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.DialogUtil;
import com.trade.eight.tools.MyAppMobclickAgent;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.view.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 作者：Created by ocean
 * 时间：on 16/12/29.
 */

public class IndexLiveV4Adapter extends BaseExpandableListAdapter {
    BaseActivity mContext;

    private List<String> groupList = new ArrayList<>();

    private HashMap<String, List<LiveRoomNew>> childMap = new HashMap<>();

    public IndexLiveV4Adapter(BaseActivity context) {
        this.mContext = context;
    }

//    public void clear(){
//        groupList.clear();
//        childMap.clear();
//    }

    /**
     * 设置组
     *
     * @param groupList
     */
    public void setGroupList(List<String> groupList) {
        this.groupList = new ArrayList<>();
        this.groupList = groupList;
    }

    /**
     * 设置直播室子类
     *
     * @param childMap
     */
    public void setChildMap(HashMap<String, List<LiveRoomNew>> childMap) {
        this.childMap = new HashMap<>();
        this.childMap = childMap;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return childMap.get(groupList.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return groupList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return childMap.get(groupList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(mContext, R.layout.item_indexlive_group, null);
        }
        TextView text_indexlive_group = MyViewHolder.get(view, R.id.text_indexlive_group);
        text_indexlive_group.setText(groupList.get(groupPosition));
        return view;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        LiveRoomNew liveRoomNew = (LiveRoomNew) getChild(groupPosition, childPosition);
        if (liveRoomNew.getChannelStatus() != LiveRoomNew.CT_STATUS_PALY) {
            return 0;
        }
        return LiveRoomNew.CT_STATUS_PALY;
    }

    @Override
    public int getChildTypeCount() {
        return 2;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
        int type = getChildType(groupPosition, childPosition);
        if (type == LiveRoomNew.CT_STATUS_PALY) {
            final LiveRoomNew liveRoomNew = (LiveRoomNew) getChild(groupPosition, childPosition);
            if (view == null) {
                view = View.inflate(mContext, R.layout.item_indexlive_inlive, null);
            }
            View rel_teacher = MyViewHolder.get(view, R.id.rel_teacher);
            CircleImageView img_fxs_avatar = MyViewHolder.get(view, R.id.img_fxs_avatar);
            TextView text_fxs_name = MyViewHolder.get(view, R.id.text_fxs_name);
            TextView text_fxs_title = MyViewHolder.get(view, R.id.text_fxs_title);
            TextView text_fxs_introduction = MyViewHolder.get(view, R.id.text_fxs_introduction);
            TextView text_online_num = MyViewHolder.get(view, R.id.text_online_num);
            View view_spiltline = MyViewHolder.get(view, R.id.view_spiltline);

            if (liveRoomNew.getSegmentModel() != null) {
                ImageLoader.getInstance().displayImage(liveRoomNew.getImage(), img_fxs_avatar, AppImageLoaderConfig.getCommonDisplayImageOptions(mContext, R.drawable.img_avater_loading_noround));
                text_fxs_title.setText(ConvertUtil.NVL(liveRoomNew.getSegmentModel().getProfessionalTitle(), ""));
                text_fxs_name.setText(ConvertUtil.NVL(liveRoomNew.getSegmentModel().getAuthorName(), ""));
                text_fxs_introduction.setText(ConvertUtil.NVL(liveRoomNew.getSegmentModel().getIntroduction(), ""));
                if (text_online_num != null) {
                    text_online_num.setText(mContext.getResources().getString(R.string.lable_live_onlinenum, ConvertUtil.NVL(liveRoomNew.getOnlineNumberSimpleName(), "")));
                }
            }

            if (childPosition == getChildrenCount(groupPosition) - 1) {
                view_spiltline.setVisibility(View.GONE);
            } else {
                view_spiltline.setVisibility(View.VISIBLE);
            }
            rel_teacher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new IndexLiveItemClickEvent(liveRoomNew));

                }
            });
        } else {
            final LiveRoomNew liveRoomNew = (LiveRoomNew) getChild(groupPosition, childPosition);
            if (view == null) {
                view = View.inflate(mContext, R.layout.item_indexlive_nolive, null);
            }
            View rel_teacher = MyViewHolder.get(view, R.id.rel_teacher);
            CircleImageView img_fxs_avatar = MyViewHolder.get(view, R.id.img_fxs_avatar);
            TextView text_fxs_name = MyViewHolder.get(view, R.id.text_fxs_name);
            TextView text_fxs_title = MyViewHolder.get(view, R.id.text_fxs_title);
            TextView text_fxs_introduction = MyViewHolder.get(view, R.id.text_fxs_introduction);
            TextView text_live_starttime = MyViewHolder.get(view, R.id.text_live_starttime);
            View view_spiltline = MyViewHolder.get(view, R.id.view_spiltline);

            if (liveRoomNew.getSegmentModel() != null) {
                ImageLoader.getInstance().displayImage(liveRoomNew.getImage(), img_fxs_avatar, AppImageLoaderConfig.getCommonDisplayImageOptions(mContext, R.drawable.img_avater_loading_noround));
                text_fxs_title.setText(ConvertUtil.NVL(liveRoomNew.getSegmentModel().getProfessionalTitle(), ""));
                text_fxs_name.setText(ConvertUtil.NVL(liveRoomNew.getSegmentModel().getAuthorName(), ""));
                text_fxs_introduction.setText(ConvertUtil.NVL(liveRoomNew.getSegmentModel().getIntroduction(), ""));
                if (text_live_starttime != null) {
                    text_live_starttime.setText(mContext.getResources().getString(R.string.lable_live_starttime, ConvertUtil.NVL(liveRoomNew.getSegmentModel().getStartTime(), "")));
                }
            }

            if (childPosition == getChildrenCount(groupPosition) - 1) {
                view_spiltline.setVisibility(View.GONE);
            } else {
                view_spiltline.setVisibility(View.VISIBLE);
            }

            rel_teacher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new IndexLiveItemClickEvent(liveRoomNew));

                }
            });
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }



   /* @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_index_livenew_v4, null);
        }
        View rel_channel_1 = MyViewHolder.get(convertView, R.id.rel_channel_1);
        CircleImageView img_authoravater_live_1 = MyViewHolder.get(convertView, R.id.img_authoravater_live_1);
        View line_channel_islive_1 = MyViewHolder.get(convertView, R.id.line_channel_islive_1);
        Button btn_islive_tag_1 = MyViewHolder.get(convertView, R.id.btn_islive_tag_1);
        TextView text_channel_nolive_1 = MyViewHolder.get(convertView, R.id.text_channel_nolive_1);
        TextView text_channel_name_1 = MyViewHolder.get(convertView, R.id.text_channel_name_1);
        TextView text_teacher_labe_1 = MyViewHolder.get(convertView, R.id.text_teacher_labe_1);
        TextView text_channel_label_1 = MyViewHolder.get(convertView, R.id.text_channel_label_1);
        TextView text_channel_watchnum_1 = MyViewHolder.get(convertView, R.id.text_channel_watchnum_1);

        View rel_channel_2 = MyViewHolder.get(convertView, R.id.rel_channel_2);
        CircleImageView img_authoravater_live_2 = MyViewHolder.get(convertView, R.id.img_authoravater_live_2);
        View line_channel_islive_2 = MyViewHolder.get(convertView, R.id.line_channel_islive_2);
        Button btn_islive_tag_2 = MyViewHolder.get(convertView, R.id.btn_islive_tag_2);
        TextView text_channel_nolive_2 = MyViewHolder.get(convertView, R.id.text_channel_nolive_2);
        TextView text_channel_name_2 = MyViewHolder.get(convertView, R.id.text_channel_name_2);
        TextView text_teacher_labe_2 = MyViewHolder.get(convertView, R.id.text_teacher_labe_2);
        TextView text_channel_label_2 = MyViewHolder.get(convertView, R.id.text_channel_label_2);
        TextView text_channel_watchnum_2 = MyViewHolder.get(convertView, R.id.text_channel_watchnum_2);


        final LiveRoomNew liveRoom = objects.get(position * 2);
        ImageLoader.getInstance().displayImage(liveRoom.getImage(), img_authoravater_live_1, AppImageLoaderConfig.getCommonDisplayImageOptions(mContext, R.drawable.img_loading_large));
        if (liveRoom.getChannelStatus() != LiveRoomNew.CT_STATUS_PALY) {//休息中
            line_channel_islive_1.setVisibility(View.GONE);
            text_channel_nolive_1.setVisibility(View.VISIBLE);
            if (liveRoom.getSegmentModel() != null) {
                text_channel_nolive_1.setText(mContext.getResources().getString(R.string.ct_starttime, ConvertUtil.NVL(liveRoom.getSegmentModel().getStartTime(), "")));
            } else {
                text_channel_nolive_1.setText(mContext.getResources().getString(R.string.ct_starttime, ""));
            }
        } else {//正在直播
            line_channel_islive_1.setVisibility(View.VISIBLE);
            text_channel_nolive_1.setVisibility(View.GONE);
            AnimationDrawable animationDrawable = (AnimationDrawable) btn_islive_tag_1.getBackground();
            animationDrawable.start();
        }
        //check by fangzhu 对象不要直接用，检查空指针。。。。。。。。
        text_channel_name_1.setText("");
        text_teacher_labe_1.setText("");
        if (liveRoom.getSegmentModel() != null) {
            text_channel_name_1.setText(liveRoom.getSegmentModel().getAuthorName());
            text_teacher_labe_1.setText(liveRoom.getSegmentModel().getProfessionalTitle());
        }

        if (!TextUtils.isEmpty(liveRoom.getLabel())) {
            text_channel_label_1.setVisibility(View.VISIBLE);
            String[] labs = liveRoom.getLabel().split(",");
            text_channel_label_1.setText(labs[0]);
        } else {
            text_channel_label_1.setVisibility(View.GONE);
        }
        text_channel_watchnum_1.setText(liveRoom.getOnlineNumberSimpleName());
        rel_channel_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new IndexLiveItemClickEvent(liveRoom));
            }
        });


        if (position * 2 + 1 < objects.size()) {
            final LiveRoomNew liveRoom_1 = objects.get(position * 2 + 1);
            rel_channel_2.setVisibility(View.VISIBLE);
            rel_channel_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new IndexLiveItemClickEvent(liveRoom_1));
                }
            });
            ImageLoader.getInstance().displayImage(liveRoom_1.getImage(), img_authoravater_live_2, AppImageLoaderConfig.getCommonDisplayImageOptions(mContext, R.drawable.img_loading_large));
            if (liveRoom_1.getChannelStatus() != LiveRoomNew.CT_STATUS_PALY) {//休息中
                line_channel_islive_2.setVisibility(View.GONE);
                text_channel_nolive_2.setVisibility(View.VISIBLE);
                text_channel_nolive_2.setText(mContext.getResources().getString(R.string.ct_starttime, liveRoom_1.getSegmentModel().getStartTime()));
            } else {//正在直播
                line_channel_islive_2.setVisibility(View.VISIBLE);
                text_channel_nolive_2.setVisibility(View.GONE);
                AnimationDrawable animationDrawable = (AnimationDrawable) btn_islive_tag_2.getBackground();
                animationDrawable.start();
            }
            text_channel_name_2.setText("");
            text_teacher_labe_2.setText("");
            if (liveRoom_1.getSegmentModel() != null) {
                text_channel_name_2.setText(liveRoom_1.getSegmentModel().getAuthorName());
                text_teacher_labe_2.setText(liveRoom_1.getSegmentModel().getProfessionalTitle());
            }
            if (!TextUtils.isEmpty(liveRoom_1.getLabel())) {
                text_channel_label_2.setVisibility(View.VISIBLE);
                String[] labs = liveRoom_1.getLabel().split(",");
                text_channel_label_2.setText(labs[0]);
            } else {
                text_channel_label_2.setVisibility(View.GONE);
            }
            text_channel_watchnum_2.setText(liveRoom_1.getOnlineNumberSimpleName());
        } else {
            rel_channel_2.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }*/

}
