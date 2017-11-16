package com.netease.nim.uikit.session.viewholder;

import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.session.emoji.MoonUtil;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderText extends MsgViewHolderBase {
    public static final String TAG = "MsgViewHolderText";

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_text;
    }

    @Override
    protected void inflateContentView() {
    }

    @Override
    protected void bindContentView() {
        layoutDirection();

        TextView bodyTextView = findViewById(R.id.nim_message_item_text_body);
//        bodyTextView.setTextColor(isReceivedMessage() ? Color.BLACK : Color.WHITE);
        int textColor = isReceivedMessage() ? Color.parseColor("#24273e") : Color.WHITE;
        bodyTextView.setTextColor(textColor);
        bodyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick();
            }
        });
        MoonUtil.identifyFaceExpression(NimUIKit.getContext(), bodyTextView, getDisplayText(), ImageSpan.ALIGN_BOTTOM);
        //设置允许链接可点击
        bodyTextView.setAutoLinkMask(Linkify.PHONE_NUMBERS|Linkify.WEB_URLS);
        bodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        bodyTextView.setLinkTextColor(textColor);
        bodyTextView.setOnLongClickListener(longClickListener);
    }

    protected void layoutDirection() {
        TextView bodyTextView = findViewById(R.id.nim_message_item_text_body);
        if (isReceivedMessage()) {
            bodyTextView.setBackgroundResource(R.drawable.nim_message_item_left_selector);
//            bodyTextView.setPadding(ScreenUtil.dip2px(15), ScreenUtil.dip2px(8), ScreenUtil.dip2px(10), ScreenUtil.dip2px(8));
        } else {
            bodyTextView.setBackgroundResource(R.drawable.nim_message_item_right_selector);
//            bodyTextView.setPadding(ScreenUtil.dip2px(10), ScreenUtil.dip2px(8), ScreenUtil.dip2px(15), ScreenUtil.dip2px(8));
        }
    }


    protected String getDisplayText() {
        String str = message.getContent();
        try {
            if (str == null)
                str = "";
            if (str.endsWith("\n")) {
                //只去掉最后的换行符，中间的不能去掉
                int index = str.lastIndexOf("\n");
                str = str.substring(0, index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.v(TAG, "message.getContent()="+str);
        return str;
    }
}
