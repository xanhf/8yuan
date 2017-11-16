package com.trade.eight.nim.viewholder;

import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.session.emoji.MoonUtil;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.trade.eight.entity.qaauto.entity.AutoInnerObj;
import com.trade.eight.moudle.outterapp.WebActivity;
import com.trade.eight.nim.extension.AutoReplyAttachment;

import java.util.List;

/**
 * Created by fangzhu
 * 客服自动回复的展示信息
 */
public class MsgViewHolderAutoReply extends MsgViewHolderBase {
    private static final String TAG = "MsgViewHolderAutoReply";

    @Override
    protected int getContentResId() {
        return R.layout.item_auto_reply;
    }

    @Override
    protected void inflateContentView() {
    }

    @Override
    protected void bindContentView() {
        AutoReplyAttachment attachment = (AutoReplyAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }
        if (attachment.getModelObj() == null)
            return;
        String lable = attachment.getModelObj().getMsg();
        if (lable == null) {
            lable = "";
//            如果没有客服发的消息title
//            return;
        }

        layoutDirection();

        TextView bodyTextView = findViewById(R.id.tv_lable);
        TextView tv_line = findViewById(R.id.tv_line);
        if (!isReceivedMessage()) {
            bodyTextView.setTextColor(context.getResources().getColor(R.color.auto_lable_custom));
            tv_line.setTextColor(context.getResources().getColor(R.color.auto_lable_custom));
        } else {
            bodyTextView.setTextColor(context.getResources().getColor(R.color.auto_lable_service));
            tv_line.setTextColor(context.getResources().getColor(R.color.auto_lable_service));
        }
        bodyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick();
            }
        });
        MoonUtil.identifyFaceExpression(NimUIKit.getContext(), bodyTextView, lable, ImageSpan.ALIGN_BOTTOM);
        bodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        bodyTextView.setOnLongClickListener(longClickListener);

        LinearLayout ll_content = findViewById(R.id.ll_content);
        ll_content.setVisibility(View.GONE);
        tv_line.setVisibility(View.GONE);
        if (attachment.getModelObj() != null) {
            ll_content.removeAllViews();
            List<AutoInnerObj> list = attachment.getModelObj().getModelList();
            if (list == null || list.size() == 0) {

                return;
            }
            //隐藏线条
            tv_line.setVisibility(View.VISIBLE);
            ll_content.setVisibility(View.VISIBLE);
            for (int i = 0; i < list.size(); i++) {
                final AutoInnerObj autoInnerObj = list.get(i);
                //第一层qa
                View itemView = View.inflate(context, R.layout.item_auto_qa_list, null);
                TextView tv_qa = (TextView)itemView.findViewById(R.id.tv_qa);
                //设置颜色
                if (!isReceivedMessage()) {
                    tv_qa.setTextColor(context.getResources().getColor(R.color.auto_index_qa_custom));
                } else {
                    tv_qa.setTextColor(context.getResources().getColor(R.color.auto_index_qa_service));
                }
                tv_qa.setText(autoInnerObj.getMessage());
                tv_qa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(autoInnerObj.getModelList() == null
                            || autoInnerObj.getModelList().size() == 0) {
                            //没有子类了直接是一个链接
                            if (autoInnerObj.getHrefUrl() != null
                                    && autoInnerObj.getHrefUrl().length() > 0
                                    && autoInnerObj.getHrefUrl().startsWith("http")) {
                                WebActivity.start(context, autoInnerObj.getMessage(), autoInnerObj.getHrefUrl());
                            }
                        } else {
                            //设置flag 关闭展开子类
                            autoInnerObj.setShow(!autoInnerObj.isShow());
                            adapter.notifyDataSetChanged();
                        }

                    }
                });
                LinearLayout ll_inner = (LinearLayout)itemView.findViewById(R.id.ll_inner);
                ll_inner.removeAllViews();
                if(!autoInnerObj.isShow())
                    ll_inner.setVisibility(View.GONE);
                else
                    ll_inner.setVisibility(View.VISIBLE);

                if (autoInnerObj.getModelList() != null && autoInnerObj.getModelList().size() > 0) {
                    for (final AutoInnerObj innerObj : autoInnerObj.getModelList()) {
                        View innerItemView = View.inflate(context, R.layout.item_auto_qa_inner, null);
                        TextView tv_inner = (TextView)innerItemView.findViewById(R.id.tv_inner);
                        tv_inner.setText(innerObj.getMessage());
                        //设置颜色
                        if (!isReceivedMessage()) {
                            tv_inner.setTextColor(context.getResources().getColor(R.color.auto_inner_msg_custom));
                        } else {
                            tv_inner.setTextColor(context.getResources().getColor(R.color.auto_inner_msg_service));
                        }
                        tv_inner.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (innerObj.getHrefUrl() != null
                                        && innerObj.getHrefUrl().length() > 0
                                        && innerObj.getHrefUrl().startsWith("http")) {
                                    WebActivity.start(context, innerObj.getMessage(), innerObj.getHrefUrl());
                                }
                            }
                        });
                        ll_inner.addView(innerItemView);
                    }
                }
                //添加第一层
                ll_content.addView(itemView);
            }
        }
    }

    private void layoutDirection() {
//        TextView bodyTextView = findViewById(R.id.tv_lable);
//        if (isReceivedMessage()) {
//            bodyTextView.setBackgroundResource(com.netease.nim.uikit.R.drawable.nim_message_item_left_selector);
//            bodyTextView.setPadding(ScreenUtil.dip2px(15), ScreenUtil.dip2px(8), ScreenUtil.dip2px(10), ScreenUtil.dip2px(8));
//        } else {
//            bodyTextView.setBackgroundResource(com.netease.nim.uikit.R.drawable.nim_message_item_right_selector);
//            bodyTextView.setPadding(ScreenUtil.dip2px(10), ScreenUtil.dip2px(8), ScreenUtil.dip2px(15), ScreenUtil.dip2px(8));
//        }
    }

}
