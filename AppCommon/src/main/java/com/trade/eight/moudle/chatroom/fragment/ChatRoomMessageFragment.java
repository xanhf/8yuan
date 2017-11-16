package com.trade.eight.moudle.chatroom.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.easylife.ten.lib.R;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nim.uikit.session.module.CheckMsgEnableListener;
import com.netease.nim.uikit.session.module.Container;
import com.netease.nim.uikit.session.module.ModuleProxy;
import com.netease.nim.uikit.session.module.input.InputPanel;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AppSetting;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.live.LiveRoomNew;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.moudle.chatroom.DemoCache;
import com.trade.eight.moudle.chatroom.action.ChatRoomImageAction;
import com.trade.eight.moudle.chatroom.data.ChatRoomCheckObj;
import com.trade.eight.moudle.chatroom.data.ChatRoomDataHelper;
import com.trade.eight.moudle.chatroom.helper.ChatRoomMemberCache;
import com.trade.eight.moudle.chatroom.module.ChatRoomMsgListPanel;
import com.trade.eight.moudle.home.live.LiveDatahelp;
import com.trade.eight.tools.DateUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.view.AppTitleView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 聊天室直播互动fragment
 * Created by hzxuwen on 2015/12/16.
 */
public class ChatRoomMessageFragment extends TFragment implements ModuleProxy, CheckMsgEnableListener {
    public static final String TAG = "ChatRoomMessageFragment";
    private View rootView;
    // modules
    protected ChatRoomInputPanel inputPanel;
    protected ChatRoomMsgListPanel messageListPanel;

    private String roomId;
    private String channelId;
    private int sendPicStatus;
    //是否启用语音
    public static final boolean isAudioEnable = false;

    //1条最新条消息
    View moreMsgView;

    Activity activity;
    /*
    * 统计在线人数
    * */
//    TextView tv_onlineCount;
//    View onlineCountView;
    int onlineCount = 0;
    AppTitleView titleview_title;
    AppTitleView titleview_control;

    /*最外层直播室列表对象*/
    LiveRoomNew roomNew;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            roomNew = (LiveRoomNew) getArguments().getSerializable("liveroomnew");
            if (roomNew != null) {
                roomId = roomNew.getChatRoomId();
                channelId = roomNew.getChannelId();
                sendPicStatus = roomNew.getSendPicStatus();
            }
        }
        activity = getActivity();

//        tv_onlineCount = (TextView) activity.findViewById(R.id.tv_onlineCount);
//        onlineCountView = activity.findViewById(R.id.onlineCountView);
        titleview_title = (AppTitleView) activity.findViewById(R.id.titleview_title);
        titleview_control = (AppTitleView) activity.findViewById(R.id.titleview_control);

        rootView = inflater.inflate(R.layout.chat_room_message_fragment, container, false);

        findViews();
        getKeyWordList();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (inputPanel != null) {
            inputPanel.onPause();
        }
        if (messageListPanel != null) {
            messageListPanel.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (messageListPanel != null) {
            messageListPanel.onResume();
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


    }

    public boolean onBackPressed() {
        if (inputPanel != null && inputPanel.collapse(true)) {
            return true;
        }

        if (messageListPanel != null && messageListPanel.onBackPressed()) {
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerObservers(false);

        if (messageListPanel != null) {
            messageListPanel.onDestroy();
        }
    }

    public void onLeave() {
        if (inputPanel != null) {
            inputPanel.collapse(false);
        }
    }

    public void init(Bundle bundle) {
        registerObservers(true);
//        findViews();
        //获取固定成员 区分管理员
        ChatRoomMemberCache.getInstance().fetchRoomMembers(roomId, MemberQueryType.NORMAL, 0, ChatRoomMemberCache.LIMIT, null);

        //设置初始化的在线人数
        ChatRoomInfo roomInfo = (ChatRoomInfo) bundle.getSerializable("obj");
        if (roomInfo == null)
            return;
        //真实的人数
//        onlineCount = roomInfo.getOnlineUserCount();
//        onlineCount *= scale;

        onlineCount = bundle.getInt("count");
        int channelStatus = bundle.getInt("channelStatus");
        boolean isFloat = bundle.getBoolean("isFloat");
        //isFloat true是小窗口过来的，重新获取人数
        if (isFloat) {
            //重新获取在线人数,判断是否需要显示
            updateRoomInfo(roomInfo.getRoomId());
        } else {
            if (channelStatus == LiveRoomNew.STATUS_OFF) {
//                if (onlineCountView != null) {
//                    onlineCountView.setVisibility(View.GONE);
//                }
            } else {
//                if (onlineCountView != null) {
//                    onlineCountView.setVisibility(View.VISIBLE);
//                }
            }
            setOnlineCountText(onlineCount);
        }
    }

    /**
     * 更新room的信息
     *
     * @param roomId
     */
    void updateRoomInfo(String roomId) {
        LiveDatahelp.loadRoom(getContext(), roomId, new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                try {
                    if (msg != null) {
                        LiveRoomNew roomNew = (LiveRoomNew) msg.obj;
                        //在直播状态
                        if (roomNew != null && roomNew.getChannelStatus() == LiveRoomNew.STATUS_ON) {
//                            if (onlineCountView != null) {
//                                onlineCountView.setVisibility(View.VISIBLE);
//                            }
                            onlineCount = Integer.parseInt(roomNew.getOnlineNumber());
                            setOnlineCountText(onlineCount);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    /**
     * 设置在线人数
     */
    public void setOnlineCountText(int count) {
        if (titleview_title != null) {
            if (count < 0)
                count = 0;
            String text = "" + count + "人";
            if (count > 10000) {
                //大于10000显示10000+
                text = "" + count + "+";
            }
            if (roomNew != null) {
                if (titleview_title != null) {
                    titleview_title.setAppCommTitle(getActivity().getResources().getString(R.string.lable_online_num, roomNew.getChannelName(), text));
                }

                if (titleview_control != null) {
                    titleview_control.setAppCommTitle(getActivity().getResources().getString(R.string.lable_online_num, roomNew.getChannelName(), text));
                }
            }
        }
    }

    /**
     * 显示底部最新多少条最新消息的布局
     */
    public void initMoreMsgView() {
        moreMsgView = rootView.findViewById(R.id.moreMsgView);
        if (moreMsgView != null) {
            //累计最新消息
            moreMsgView.setTag(0);
            moreMsgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    messageListPanel.scrollToBottomNow();
                    moreMsgView.setVisibility(View.GONE);
                }
            });
        }
    }

    private void findViews() {
        Container container = new Container(activity, roomId, SessionTypeEnum.ChatRoom, this);
        if (messageListPanel == null) {
            messageListPanel = new ChatRoomMsgListPanel(container, rootView);
        }
        initMoreMsgView();


        if (inputPanel == null) {
            inputPanel = new ChatRoomInputPanel(container, rootView, getActionList(), isAudioEnable);
            inputPanel.setRoomNew(roomNew);
            inputPanel.setCheckMsgEnableListener(this);
            //设置输入法出现，表情view出现的事件
            inputPanel.setOnChatLayoutChange(new InputPanel.OnChatLayoutChange() {
                @Override
                public void onChatLayoutChange(boolean isPickViewShow) {
                    View quickView = activity.findViewById(R.id.quickView);
                    if (isPickViewShow) {
                        if (quickView != null) {
                            quickView.setVisibility(View.GONE);
                        }
                    } else {
                        if (quickView != null) {
                            quickView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        } else {
            inputPanel.reload(container, null);
        }
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
    }

    Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
        @Override
        public void onEvent(List<ChatRoomMessage> messages) {
            if (messages == null || messages.isEmpty()) {
                return;
            }
            //过滤掉通知 add by fangzhu  两个地方注意这里和messageListPanel.handleNewMessage(messages, moreMsgView);
            List<ChatRoomMessage> filterMsgList = new ArrayList<>();
            for (ChatRoomMessage item : messages) {
                //显示的消息类型
                if (item.getMsgType() == MsgTypeEnum.text
                        || item.getMsgType() == MsgTypeEnum.image
                        || item.getMsgType() == MsgTypeEnum.custom) {
                    filterMsgList.add(item);
                }
                //监听人数变化是通知消息类型
                if (item.getMsgType() == MsgTypeEnum.notification) {
                    //只能通过这里的消息来统计在线人数的变化
                    if (item.getAttachment() != null) {
                        ChatRoomNotificationAttachment attachment = (ChatRoomNotificationAttachment) item.getAttachment();
                        switch (attachment.getType()) {
                            case ChatRoomMemberIn:
                                onlineCount++;
                                setOnlineCountText(onlineCount);
                                break;
                            case ChatRoomMemberExit:
                                onlineCount--;
                                setOnlineCountText(onlineCount);
                                break;
                            case ChatRoomMemberKicked:
                                //被踢出直播间
                                onlineCount--;
                                setOnlineCountText(onlineCount);
                                break;
                        }
                    }

                }
            }
            //重新设置list
            messages = filterMsgList;

            //过滤以后可能size为0
            if (messages == null || messages.isEmpty()) {
                return;
            }

            //显示最新消息
            //过滤掉通知类型的消息
            messageListPanel.handleNewMessage(messages, moreMsgView);

            //这里包含了进出直播室的通知，所以会特别多
            Log.v(TAG, "incomingChatRoomMsg ");
            messageListPanel.onIncomingMessage(messages);
        }
    };

    /************************** Module proxy ***************************/
    /**
     * ChatRoomMessage extends IMMessage
     * 所以直接发送 ChatRoomMessage 当成p2p的message 也是可以的
     *
     * @param msg
     * @return
     */
    @Override
    public boolean sendMessage(IMMessage msg) {
        //如果需要加文件发送，  参考ImageAction.onPicked
        ChatRoomMessage message = (ChatRoomMessage) msg;

        Map<String, Object> ext = new HashMap<>();
        ChatRoomMember chatRoomMember = ChatRoomMemberCache.getInstance().getChatRoomMember(roomId, DemoCache.getAccount());
        if (chatRoomMember != null && chatRoomMember.getMemberType() != null) {
            ext.put("type", chatRoomMember.getMemberType().getValue());
            message.setRemoteExtension(ext);
        }

        NIMClient.getService(ChatRoomService.class).sendMessage(message, false)
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        Log.v(TAG, "onSuccess");

                    }

                    @Override
                    public void onFailed(int code) {
                        Log.v(TAG, "onFailed");
                        if (code == ResponseCode.RES_CHATROOM_MUTED) {
                            Toast.makeText(getActivity(), "用户被禁言", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "消息发送失败：code:" + code, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                        Log.v(TAG, "onException");
                        Toast.makeText(getActivity(), "消息发送失败！", Toast.LENGTH_SHORT).show();
                    }
                });
        messageListPanel.onMsgSend(msg);
        return true;
    }

    @Override
    public void onInputPanelExpand() {
        messageListPanel.scrollToBottom();
    }

    @Override
    public void shouldCollapseInputPanel() {
        inputPanel.collapse(false);
    }

    @Override
    public boolean isLongClickEnabled() {
        return !inputPanel.isRecording();
    }

    /**
     * 发送图片的逻辑等
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult");
        inputPanel.onActivityResult(requestCode, resultCode, data);
//        messageListPanel.onActivityResult(requestCode, resultCode, data);

    }

    // 操作面板集合
    protected List<BaseAction> getActionList() {
        List<BaseAction> actions = new ArrayList<>();
        actions.add(new ChatRoomImageAction((BaseActivity) activity, roomId, channelId, sendPicStatus));
        return actions;
    }

    //敏感词集合
    List<ChatRoomCheckObj> keyWrodList;

    /**
     * 获取敏感词信息
     */
    void getKeyWordList() {
        new AsyncTask<Void, Void, List<ChatRoomCheckObj>>() {
            @Override
            protected List<ChatRoomCheckObj> doInBackground(Void... voids) {
                String userId = "";
                UserInfoDao dao = new UserInfoDao(getActivity());
                if (dao.isLogin()) {
                    userId = dao.queryUserInfo().getUserId();
                }
                List<ChatRoomCheckObj> list = ChatRoomDataHelper.getKeyWordList(getActivity(), userId);
                if (list != null) {
                    keyWrodList = list;
                }
                return list;
            }

        }.execute();
    }

    /**
     * 禁言
     *
     * @param mark 备注
     */
    void disableUser(final String mark) {
        new AsyncTask<Void, Void, CommonResponse<Object>>() {
            @Override
            protected CommonResponse<Object> doInBackground(Void... voids) {
                String userId = "";
                UserInfoDao dao = new UserInfoDao(getActivity());
                if (dao.isLogin()) {
                    userId = dao.queryUserInfo().getUserId();
                }
                return ChatRoomDataHelper.disableUser(getActivity(), userId, mark);
            }

        }.execute();
    }

    /**
     * 敏感词过滤
     *
     * @param msgContent
     * @return
     */
    @Override
    public boolean isTextEnabel(String msgContent) {
        BaseActivity act = (BaseActivity) getActivity();
        if (msgContent == null) {
            Toast.makeText(act, "请输入文字", Toast.LENGTH_LONG).show();
            return false;
        }
        Pattern p = Pattern.compile("<[^>]+>");//html正则表达式
        Matcher m = p.matcher(msgContent.trim());
        if (m.find()) {
            act.showCusToast("非法的文字格式");
            return false;
        }
        if (keyWrodList == null) {
            //没有获取到敏感词
            act.showCusToast("发送失败请重试");
            getKeyWordList();
            return false;
        }

        if (keyWrodList != null) {
            for (ChatRoomCheckObj obj : keyWrodList) {
                String keyWord = obj.getText();
                //如果为空字符，过滤掉
                if (StringUtil.isEmpty(keyWord))
                    continue;

                //匹配字符串的时候，把发送的内容转换成小写
                String sendMsgLower = msgContent.toLowerCase();
                //去标点
                sendMsgLower = com.trade.eight.tools.StringUtil.moveDot(sendMsgLower);
                //敏感词页转成小写匹配
                String keyStrLower = keyWord.toLowerCase();
                //匹配是否包含敏感词
                if (sendMsgLower.contains(keyStrLower)) {
                    //包含敏感词
                    //1、不允许发送  2、直接禁言当前用户
                    String str = act.getResources().getString(R.string.chatroom_user_disable_text);
                    act.showCusToast(str);
                    //写入格式
                    String mark = getActivity().getResources().getString(R.string.chatroom_text_key_word);
                    mark = mark.replace("{keyword}", keyWord);
                    mark = mark.replace("{time}", DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
                    mark = mark.replace("{version}", AppSetting.getInstance(getActivity()).getAppVersion());
                    mark = mark.replace("{sendMsg}", msgContent);

                    //直接禁言
                    disableUser(mark);
                    act.finish();
                    return false;
                }
            }
        }

        return true;
    }
}
