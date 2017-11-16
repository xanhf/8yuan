package com.trade.eight.moudle.chatroom.viewholder;

import com.easylife.ten.lib.R;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderText;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Created by fangzhu  完全自定义不使用demo的布局
 * <p>
 * 重写
 * getResId
 * inflate
 * refresh
 */
public class ChatRoomViewHolderUnKnow extends MsgViewHolderText {


    @Override
    protected int getResId() {
        return R.layout.fz_chat_item_unkow;
    }

    @Override
    protected void inflate() {
        if (message == null)
            return;

    }

    @Override
    protected void refresh(Object item) {
        message = (IMMessage) item;
        bindContentView();
    }

    @Override
    protected void bindContentView() {
        if (message == null)
            return;

    }


}
