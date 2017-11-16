package com.trade.eight.moudle.chatroom.viewholder;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.session.emoji.MoonUtil;
import com.netease.nim.uikit.session.emoji.MyImageSpan;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderText;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.trade.eight.moudle.chatroom.gift.GiftPanUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.StringUtil;

/**
 * Created by fangzhu  完全自定义不使用demo的布局
 * <p>
 * 重写
 * getResId
 * inflate
 * refresh
 */
public class ChatRoomViewHolderText extends MsgViewHolderText {
    public static final String TAG = "ChatRoomViewHolderText";


    HeadImageView head_avater;

    @Override
    protected int getResId() {
        Log.v(TAG, "getResId");
        return R.layout.fz_chat_item_text;
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
//        View.inflate(view.getContext(), getContentResId(), contentContainer);
    }

    @Override
    protected void refresh(Object item) {
        Log.v(TAG, "init msg");
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

//        TextView tv_nickname = findViewById(R.id.tv_nickname);
//        ImageView ic_vip = (ImageView) findViewById(R.id.ic_vip);
//        tv_nickname.setTextColor(isReceivedMessage() ? Color.parseColor("#3a69e3") : Color.parseColor("#848999"));

//        tv_nickname.setText(nick + ": ");
//        tv_nickname.setVisibility(View.GONE);

        //设置加v标示
//        ChatRoomViewHolderHelper.setNameIconView((ChatRoomMessage) message, ic_vip);

//        tv_content.setTextColor(isReceivedMessage() ? Color.BLACK : Color.WHITE);
//        MoonUtil.identifyFaceExpression(NimUIKit.getContext(), tv_content, getDisplayText(), MyImageSpan.ALIGN_CENTER);
//        tv_content.setMovementMethod(LinkMovementMethod.getInstance());
//        tv_content.setOnLongClickListener(longClickListener);
//        tv_content.setTextColor(isReceivedMessage() ? Color.parseColor("#24273e") : Color.parseColor("#4877e6"));


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

        String text = nickStr + getDisplayText();
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
//            contentColor = Color.parseColor("#24273e");
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
    }


    @Override
    protected boolean isShowBubble() {
        return false;
    }

    @Override
    protected boolean isShowHeadImage() {
        return false;
    }

    @Override
    public void setNameTextView() {

    }

    @Override
    protected boolean onItemLongClick() {
        return false;
    }

    @Override
    protected void onItemClick() {

    }
}
