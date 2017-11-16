package com.trade.eight.moudle.chatroom.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.moudle.chatroom.helper.ChatRoomMemberCache;
import com.trade.eight.tools.StringUtil;

import java.util.Map;

/**
 * 聊天室成员姓名
 * Created by hzxuwen on 2016/1/20.
 */
public class ChatRoomViewHolderHelper {
    public static final String TAG = "ChatRoomViewHolderHelper";
    //当前直播室的rooimd
    public static String currentRoomId = null;

    public static void setNameTextView(ChatRoomMessage message, TextView text, ImageView imageView, Context context) {
        if (message.getMsgType() != MsgTypeEnum.notification) {
            // 聊天室中显示姓名
            if (message.getChatRoomMessageExtension() != null) {
                String nick = message.getChatRoomMessageExtension().getSenderNick();
//                Log.v(TAG, "nick=" + nick + "  getFromAccount=" + message.getFromAccount());
                text.setText(StringUtil.delPhone(nick, "x"));
            } else {
                String uname = NimUserInfoCache.getInstance().getUserName(message.getFromAccount());
//                Log.v(TAG, "uname=" + uname + "  getFromAccount=" + message.getFromAccount());
                text.setText(StringUtil.delPhone(uname, "x"));
            }

            text.setTextColor(context.getResources().getColor(R.color.color_black_ff999999));
            text.setVisibility(View.VISIBLE);
            setNameIconView(message, imageView);
        }
    }

    /**
     * 得到直播室的昵称
     *
     * @param message
     * @return
     */
    public static String getNick(ChatRoomMessage message) {
        String nick = message.getFromNick();
        if (!StringUtil.isEmpty(nick))
            return handleNickh(nick);
//        if (message.getMsgType() != MsgTypeEnum.notification) {
        // 聊天室中显示姓名
        if (message.getChatRoomMessageExtension() != null) {
            nick = message.getChatRoomMessageExtension().getSenderNick();
//                Log.v(TAG, "nick=" + nick + "  getFromAccount=" + message.getFromAccount());
            return handleNickh(nick);
        } else {
            String uname = NimUserInfoCache.getInstance().getUserName(message.getFromAccount());
//                Log.v(TAG, "uname=" + uname + "  getFromAccount=" + message.getFromAccount());
            return handleNickh(uname);
        }
//        }
    }

    /**
     * 1、处理直播室昵称显示的长度
     * 2、处理昵称是手机号码
     *
     * @param nick
     * @return
     */
    public static String handleNickh(String nick) {
        try {
            //最大显示的昵称字符串
            int MAX_LENGTH_nikName = 8;
            if (nick == null)
                return nick;
            if (nick.length() > MAX_LENGTH_nikName) {
                nick = nick.substring(0, MAX_LENGTH_nikName);
            }
            //如果昵称是手机号或者包含手机号
            nick = StringUtil.delPhone(nick, "x");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nick;
    }

    /**
     * 直播室礼物显示 截断昵称长度
     *
     * @param nick
     * @return
     */
    public static String subNick4GiftLive(String nick) {
        if (StringUtil.isEmpty(nick))
            return "";
        if (nick.length() > 4) {
            return nick.substring(0, 4) + "...";
        }
        return nick;
    }


    public static void setNameIconView(ChatRoomMessage message, ImageView nameIconView) {
        final String KEY = "type";
        Map<String, Object> ext = message.getRemoteExtension();

        if (ext == null || !ext.containsKey(KEY)) {
            nameIconView.setVisibility(View.GONE);
//            Log.v(TAG, "message.getRemoteExtension() == null");
//            String nick = "";
//            if (message.getChatRoomMessageExtension() != null) {
//                nick = message.getChatRoomMessageExtension().getSenderNick();
//            }
//            Log.v(TAG, "nick=" + nick + "  getFromAccount=" + message.getFromAccount());
            if (currentRoomId == null || currentRoomId.trim().length() == 0)
                return;
            String account = message.getFromAccount();
            if (account == null)
                return;
            ChatRoomMember member = ChatRoomMemberCache.getInstance().getChatRoomMember(currentRoomId, account);
            if (member == null)
                return;
            //当前直播室的固定成员列表  和account对比；是否是管理员加v
            MemberType type = member.getMemberType();
            if (type == MemberType.ADMIN) {
                nameIconView.setImageResource(R.drawable.chat_room_ic_v);
                nameIconView.setVisibility(View.VISIBLE);
            } else if (type == MemberType.CREATOR) {
                nameIconView.setImageResource(R.drawable.chat_room_ic_v);
                nameIconView.setVisibility(View.VISIBLE);
            }
            return;
        }

        String nick = null;
        if (message.getChatRoomMessageExtension() != null) {
            nick = message.getChatRoomMessageExtension().getSenderNick();
        }

        //sdk  这个type很多情况下获取不到，必须按照上面那样处理；
        MemberType type = MemberType.typeOfValue((Integer) ext.get(KEY));
//        Log.v(TAG, "type=" +type + "" + ConvertUtil.NVL(nick, ""));
        if (type == MemberType.ADMIN) {
//            nameIconView.setImageResource(R.drawable.admin_icon);
            nameIconView.setImageResource(R.drawable.chat_room_ic_v);
            nameIconView.setVisibility(View.VISIBLE);
        } else if (type == MemberType.CREATOR) {
//            nameIconView.setImageResource(R.drawable.master_icon);
            nameIconView.setImageResource(R.drawable.chat_room_ic_v);
            nameIconView.setVisibility(View.VISIBLE);
        } else {
            nameIconView.setVisibility(View.GONE);
        }
    }

    /**
     * 判断是管理员  创建者 还是普通游客
     *
     * @param message
     * @return
     */
    public static MemberType getMemberType(ChatRoomMessage message) {
        MemberType memberType = MemberType.GUEST;
        final String KEY = "type";
        Map<String, Object> ext = message.getRemoteExtension();
        /*这里是我自己处理得到的类型*/
        if (ext == null || !ext.containsKey(KEY)) {
//            Log.v(TAG, "message.getRemoteExtension() == null");
//            String nick = "";
//            if (message.getChatRoomMessageExtension() != null) {
//                nick = message.getChatRoomMessageExtension().getSenderNick();
//            }
//            Log.v(TAG, "nick=" + nick + "  getFromAccount=" + message.getFromAccount());
            if (currentRoomId == null || currentRoomId.trim().length() == 0)
                return memberType;
            String account = message.getFromAccount();
            if (account == null)
                return memberType;
            ChatRoomMember member = ChatRoomMemberCache.getInstance().getChatRoomMember(currentRoomId, account);
            if (member == null)
                return memberType;
            //当前直播室的固定成员列表  和account对比；是否是管理员加v
            return member.getMemberType();
        }

        //sdk  这个type很多情况下获取不到，必须按照上面那样处理；
        return MemberType.typeOfValue((Integer) ext.get(KEY));
    }

    /**
     * 直播室获取等级
     *
     * @param message
     * @return
     */
    public static int getLevel(Context context, ChatRoomMessage message) {
        try {
            if (message == null)
                return 0;
            //如果是自己的，等级不会显示在扩展字段中

            if (!isReceivedMessage(message)) {
                //自己发的 直接取本地登录账号的等级
                if (new UserInfoDao(context).isLogin()) {
                    return new UserInfoDao(context).queryUserInfo().getLevelNum();
                }
            }
            if (message.getChatRoomMessageExtension() != null) {
                Map<String, Object> map = message.getChatRoomMessageExtension().getSenderExtension();
                if (map != null && map.containsKey(UserInfo.ULEVELNUM)) {
                    int level = (int) map.get(UserInfo.ULEVELNUM);
                    return level;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 判断消息方向，是否是接收到的消息
    protected static boolean isReceivedMessage(IMMessage message) {
        return message.getDirect() == MsgDirectionEnum.In;
    }

}
