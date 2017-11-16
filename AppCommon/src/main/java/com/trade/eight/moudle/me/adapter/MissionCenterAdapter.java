package com.trade.eight.moudle.me.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.entity.ShareEntity;
import com.trade.eight.entity.missioncenter.MissionTaskData;
import com.trade.eight.moudle.me.activity.AnswerQuestionAct;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.tools.MyViewHolder;
import com.trade.eight.tools.OpenActivityUtil;
import com.trade.eight.tools.ShareTools;

import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/3/1.
 * 任务中心适配器
 */

public class MissionCenterAdapter extends ArrayAdapter<MissionTaskData> {
    BaseActivity context;
    public List<MissionTaskData> objects;


    public MissionCenterAdapter(BaseActivity context, int resource, List<MissionTaskData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public void setObjects(List<MissionTaskData> objects) {
        this.objects = objects;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_missioncenter, null);
        }
        ImageView img_mission_icon = MyViewHolder.get(convertView, R.id.img_mission_icon);
        TextView text_tasktitle = MyViewHolder.get(convertView, R.id.text_tasktitle);
        TextView text_taskabstract = MyViewHolder.get(convertView, R.id.text_taskabstract);
        LinearLayout line_answerprogress = MyViewHolder.get(convertView, R.id.line_answerprogress);
        ProgressBar pr_answer = MyViewHolder.get(convertView, R.id.pr_answer);
        TextView text_answerprogress = MyViewHolder.get(convertView, R.id.text_answerprogress);
        Button btn_taskoption = MyViewHolder.get(convertView, R.id.btn_taskoption);

        final MissionTaskData missionTaskData = objects.get(position);

        ImageLoader.getInstance().displayImage(missionTaskData.getTaskIcon(), img_mission_icon, AppImageLoaderConfig.getDisplayImageOptions(context));
        text_tasktitle.setText(missionTaskData.getTaskTitle());
        text_taskabstract.setText(missionTaskData.getTaskDesc());
        if (missionTaskData.getTaskType() == 1) {
            line_answerprogress.setVisibility(View.VISIBLE);
            pr_answer.setMax(missionTaskData.getQueTotalNum());

            pr_answer.setProgress(missionTaskData.getQueSucessNum());
            text_answerprogress.setText(missionTaskData.getQueSucessNum() + "/" + missionTaskData.getQueTotalNum());
            if (missionTaskData.getLocalqueSucessNum() >= missionTaskData.getQueSucessNum() && missionTaskData.getQueSucessNum() != missionTaskData.getQueTotalNum()) {
                pr_answer.setProgress(missionTaskData.getLocalqueSucessNum());
                text_answerprogress.setText(missionTaskData.getLocalqueSucessNum() + "/" + missionTaskData.getQueTotalNum());
            }
            if (missionTaskData.getQueSucessNum() == 0) {

                btn_taskoption.setBackgroundResource(R.drawable.bg_btn_colorff7901);
                btn_taskoption.setText(context.getResources().getString(R.string.mc_undo));
                btn_taskoption.setTextColor(context.getResources().getColor(R.color.white));

            } else if (missionTaskData.getQueSucessNum() == missionTaskData.getQueTotalNum()) {

                btn_taskoption.setBackgroundResource(R.drawable.bg_btn_grey_solidgrey);
                btn_taskoption.setText(context.getResources().getString(R.string.mc_did));
                btn_taskoption.setTextColor(context.getResources().getColor(R.color.white));

            } else {

                btn_taskoption.setBackgroundResource(R.drawable.bg_btn_colorff7901_solidtranspert);
                btn_taskoption.setText(context.getResources().getString(R.string.mc_doing));
                btn_taskoption.setTextColor(context.getResources().getColor(R.color.mission_center_orange));
            }

        } else {
            line_answerprogress.setVisibility(View.GONE);
        }

        switch (missionTaskData.getUserTaskStatus()) {
            case 1://完成
                btn_taskoption.setBackgroundResource(R.drawable.bg_btn_grey_solidgrey);
                btn_taskoption.setText(context.getResources().getString(R.string.mc_did));
                btn_taskoption.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case 2://未开始
                btn_taskoption.setBackgroundResource(R.drawable.bg_btn_colorff7901);
                btn_taskoption.setText(context.getResources().getString(R.string.mc_undo));
                btn_taskoption.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case 3:// 进行中
                btn_taskoption.setBackgroundResource(R.drawable.bg_btn_colorff7901_solidtranspert);
                btn_taskoption.setText(context.getResources().getString(R.string.mc_doing));
                btn_taskoption.setTextColor(context.getResources().getColor(R.color.mission_center_orange));
                break;
        }

        btn_taskoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (missionTaskData.getTaskType()) {//1=答题，2=分享，3=协议跳转，4=H5跳转
                    case 1:
                        AnswerQuestionAct.startAct(context, missionTaskData);
                        break;
                    case 2:
                        ShareTools shareTools = new ShareTools(context);
                        ShareEntity entity = new Gson().fromJson(missionTaskData.getTaskLink(), ShareEntity.class);
                        //弹出选择分享平台
                        shareTools.showShareDialog(context, entity,true);
                        break;
                    case 3:
                        context.startActivity(OpenActivityUtil.getIntent(context, missionTaskData.getTaskLink()));
                        break;
                    case 4:
                        Intent intent = new Intent();
                        intent.setClass(context, WebActivity.class);
                        intent.putExtra("url", missionTaskData.getTaskLink());
                        intent.putExtra("title", missionTaskData.getTaskLinkTitle());
                        context.startActivity(intent);
                        break;
                }
            }
        });

        return convertView;
    }
}
