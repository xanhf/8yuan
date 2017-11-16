package com.trade.eight.moudle.chatroom.viewholder;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.session.emoji.MoonUtil;
import com.netease.nim.uikit.session.emoji.MyImageSpan;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderPicture;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.trade.eight.moudle.chatroom.gift.GiftPanUtil;
import com.trade.eight.moudle.outterapp.ImageViewAttachActivity;

/**
 * Created by fangzhu on 16/3/20.
 */
public class ChatRoomMsgViewHolderPicture extends MsgViewHolderPicture {

    HeadImageView head_avater;

    @Override
    protected int getResId() {
        return R.layout.fz_chat_item_pic;
    }

    @Override
    protected void inflate() {
//        if (message == null)
//            return;

        head_avater = findViewById(R.id.head_avater);
        if (isShowAvater) {
            head_avater.setVisibility(View.VISIBLE);
        } else {
            head_avater.setVisibility(View.GONE);
        }

    }

    @Override
    protected void refresh(Object item) {
        message = (IMMessage) item;
        if (head_avater != null) {
            head_avater.setMessage((ChatRoomMessage) message);
            head_avater.loadBuddyAvatar(message.getFromAccount());
        }
        bindContentView();
    }

    @Override
    protected void bindContentView() {
        if (message == null)
            return;
        ImageView img_level = (ImageView) findViewById(R.id.img_level);
        TextView tv_content = findViewById(R.id.tv_content);
        String nick = ChatRoomViewHolderHelper.getNick((ChatRoomMessage) message);
        MemberType memberType = ChatRoomViewHolderHelper.getMemberType((ChatRoomMessage) message);
        if (head_avater != null) {

            head_avater.setMessage((ChatRoomMessage) message);
            head_avater.loadBuddyAvatar(message.getFromAccount());
        }

        int level = ChatRoomViewHolderHelper.getLevel(context, ((ChatRoomMessage) message));
        if (GiftPanUtil.liveLevelMap.containsKey(level)) {
            img_level.setImageResource(GiftPanUtil.liveLevelMap.get(level));
            // 期货没有积分  先隐藏积分等级 update 08-09 by 海洋
            img_level.setVisibility(View.GONE);
        } else {
            img_level.setVisibility(View.GONE);
        }


        String nickStr = nick + ":";
        String iconReg = "{v}";//替换管理员的图片占位
        boolean isAdmin = false;
        if (memberType == MemberType.ADMIN
                || memberType == MemberType.CREATOR) {
            isAdmin = true;
            nickStr = iconReg + nick + ":";
            //管理员去掉等级的显示
            img_level.setVisibility(View.GONE);
        }

        String text = nickStr + "";
        SpannableString spannableString = MoonUtil.replaceEmoticons(NimUIKit.getContext(), text, MoonUtil.DEF_SCALE, MyImageSpan.ALIGN_CENTER);

        int nickColor = 0, contentColor = 0;
        nickColor = Color.parseColor("#b3999999");
        contentColor = Color.parseColor("#464646");
//        if (isReceivedMessage()) {
//            //服务端端
//            nickColor = Color.parseColor("#848999");
//            contentColor = Color.parseColor("#24273e");
//        } else {
//            //自己发的
//            nickColor = Color.parseColor("#4877e6");
//            contentColor = Color.parseColor("#4877e6");
//        }
//        if (isAdmin) {
//            //管理员的字体颜色
//            nickColor = Color.parseColor("#4877e6");
//            contentColor = Color.parseColor("#4877e6");
//        }
        try {
            //设置nick的颜色
            spannableString.setSpan(new ForegroundColorSpan(nickColor), 0, nickStr.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(contentColor), nickStr.length(), text.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);

            if (isAdmin) {
                int start = 0;
                int end = start + iconReg.length();
                spannableString = MoonUtil.repAdmIcon(context, spannableString, start, end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_content.setText(spannableString);

        inflateContentView();
        super.bindContentView();
        if (thumbnail != null) {
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPic();
                }
            });
        }


    }

//    /**
//     * 重写一下 显示昵称的方法
//     */
//    @Override
//    public void setNameTextView() {
//        nameContainer.setPadding(ScreenUtil.dip2px(6), 0, 0, ScreenUtil.dip2px(5));
//        ChatRoomViewHolderHelper.setNameTextView((ChatRoomMessage) message, nameTextView, nameIconView, context);
//    }
//
//    /**
//     * 重写一下 图片的点击事件
//     */
//    @Override
//    protected void onItemClick() {
//        try {
//            FileAttachment fileAttachment = (FileAttachment)message.getAttachment();
//            ImageViewAttachActivity.start(context, fileAttachment.getUrl(), false);
//        } catch (Exception e) {e.printStackTrace();
//
//        }
//    }

    public void showPic() {
        try {
            FileAttachment fileAttachment = (FileAttachment) message.getAttachment();
            ImageViewAttachActivity.start(context, fileAttachment.getUrl(), false);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


}
