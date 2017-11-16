package com.trade.eight.moudle.chatroom.module;

import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.easylife.ten.lib.R;
import com.google.gson.Gson;
import com.netease.nim.uikit.UserPreferences;
import com.netease.nim.uikit.common.adapter.TAdapterDelegate;
import com.netease.nim.uikit.common.adapter.TViewHolder;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.listview.AutoRefreshListView;
import com.netease.nim.uikit.common.ui.listview.ListViewUtil;
import com.netease.nim.uikit.common.ui.listview.MessageListView;
import com.netease.nim.uikit.session.audio.MessageAudioControl;
import com.netease.nim.uikit.session.module.Container;
import com.netease.nim.uikit.session.module.list.MsgAdapter;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.avchat.model.AVChatAttachment;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.AttachmentProgress;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.trade.eight.moudle.chatroom.gift.GiftObj;
import com.trade.eight.moudle.chatroom.gift.GiftPanUtil;
import com.trade.eight.moudle.chatroom.gift.view.GiftLayout;
import com.trade.eight.moudle.chatroom.viewholder.ChatRoomMsgViewHolderFactory;
import com.trade.eight.tools.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 聊天室消息收发模块
 * Created by huangjun on 2016/1/27.
 */
public class ChatRoomMsgListPanel implements TAdapterDelegate {
    public static final String TAG = "ChatRoomMsgListPanel";
    private static final int MESSAGE_CAPACITY = 500;

    // container
    private Container container;
    private View rootView;
    private Handler uiHandler;

    // message list view
    private MessageListView messageListView;
    private LinkedList<IMMessage> items;
    private MsgAdapter adapter;

    public ChatRoomMsgListPanel(Container container, View rootView) {
        this.container = container;
        this.rootView = rootView;

        init();
    }

    public void onResume() {
        setEarPhoneMode(UserPreferences.isEarPhoneModeEnable());
    }

    public void onPause() {
        MessageAudioControl.getInstance(container.activity).stopAudio();
    }

    public void onDestroy() {
        registerObservers(false);
    }

    public boolean onBackPressed() {
        uiHandler.removeCallbacks(null);
        MessageAudioControl.getInstance(container.activity).stopAudio(); // 界面返回，停止语音播放
        return false;
    }

    private void init() {
        initListView();
        this.uiHandler = new Handler();
        registerObservers(true);
    }

    //是否是listview的最后一个可见
    boolean isLasteItemVisible = false;

    private void initListView() {
        items = new LinkedList<>();
        adapter = new MsgAdapter(container.activity, items, this);
        adapter.setEventListener(new MsgItemEventListener());

        messageListView = (MessageListView) rootView.findViewById(com.netease.nim.uikit.R.id.messageListView);
        messageListView.requestDisallowInterceptTouchEvent(true);

        messageListView.addOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount + firstVisibleItem == totalItemCount) {
                    isLasteItemVisible = true;
                    resetNewMessage(rootView.findViewById(R.id.moreMsgView));
                } else {
                    isLasteItemVisible = false;
                }
            }
        });


        messageListView.setMode(AutoRefreshListView.Mode.START);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            messageListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        // adapter
        messageListView.setAdapter(adapter);

        messageListView.setListViewEventListener(new MessageListView.OnListViewEventListener() {
            @Override
            public void onListViewStartScroll() {
                container.proxy.shouldCollapseInputPanel();
            }
        });
        messageListView.setOnRefreshListener(new MessageLoader());
    }

    // 刷新消息列表
    public void refreshMessageList() {
        container.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void scrollToBottom() {
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ListViewUtil.scrollToBottom(messageListView);
            }
        }, 200);
    }

    /**
     * add by fangzhu
     * 不延时 滑动到底部
     */
    public void scrollToBottomNow() {
        if (adapter != null)
            messageListView.setSelection(adapter.getCount() - 1);
    }

    public void onIncomingMessage(List<ChatRoomMessage> messages) {
        boolean needScrollToBottom = ListViewUtil.isLastMessageVisible(messageListView);
        Log.v(TAG, "needScrollToBottom=" + needScrollToBottom);
        Log.v(TAG, "isLasteItemVisible=" + isLasteItemVisible);
        if (messages == null)
            return;
        if (messages.size() == 0)
            return;
        boolean needRefresh = false;
        List<IMMessage> addedListItems = new ArrayList<>(messages.size());
        for (IMMessage message : messages) {
            // 保证显示到界面上的消息，来自同一个聊天室
            if (isMyMessage(message)) {
                saveMessage(message, false);
                addedListItems.add(message);
                needRefresh = true;
            }

            //处理直播室礼物的显示
            try {
                if (message.getMsgType() == MsgTypeEnum.custom) {
                    Map<String, Object> map = message.getRemoteExtension();
                    //礼物的显示
                    if (map.containsKey(GiftPanUtil.KEY_TYPE)
                            && (int) map.get(GiftPanUtil.KEY_TYPE) == GiftPanUtil.CUSTOM_TYPE_GIFT) {
                        if (map.containsKey(GiftPanUtil.KEY_DATA)) {


                            final GiftLayout giftView01 = (GiftLayout) rootView.findViewById(R.id.giftView01);
                            final GiftLayout giftView02 = (GiftLayout) rootView.findViewById(R.id.giftView02);
                            String data = map.get(GiftPanUtil.KEY_DATA).toString();
                            GiftObj giftObj = new Gson().fromJson(data, GiftObj.class);
                            if (giftObj != null) {
                                //自定义关系
                                giftObj.setMessage(message);
                                giftObj.setExNum(giftObj.getGiftNum());
                                GiftPanUtil.showGift(giftObj, giftView01, giftView02);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (needRefresh) {
            adapter.notifyDataSetChanged();
        }


        // incoming messages tip
        IMMessage lastMsg = messages.get(messages.size() - 1);

        //同一个房间，在最底部
        if (isMyMessage(lastMsg) && needScrollToBottom) {
            ListViewUtil.scrollToBottom(messageListView);
        }
    }

    /**
     * 隐藏最新消息view
     *
     * @param moreMsgView
     */
    public void resetNewMessage(View moreMsgView) {
        if (moreMsgView == null)
            return;
        moreMsgView.setTag(0);
        moreMsgView.setVisibility(View.GONE);
    }

    /**
     * 出理显示的新消息
     *
     * @param messages
     */
    public void handleNewMessage(List<ChatRoomMessage> messages, View moreMsgView) {
        if (moreMsgView == null)
            return;
        boolean isLastMessageVisible = ListViewUtil.isLastMessageVisible(messageListView);
        Log.v(TAG, "isLastMessageVisible=" + isLastMessageVisible);
        if (isLastMessageVisible) {
            moreMsgView.setTag(0);
            moreMsgView.setVisibility(View.GONE);
            return;
        }
        if (messages == null)
            return;
//        List<IMMessage> addedListItems = new ArrayList<>(messages.size());
        int count = 0;
        try {
            count = (int) moreMsgView.getTag();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (IMMessage message : messages) {
            // 保证显示到界面上的消息，来自同一个聊天室
            if (isMyMessage(message)) {
                //只统计显示的类型
                if (message.getMsgType() == MsgTypeEnum.text
                        || message.getMsgType() == MsgTypeEnum.image) {
                    count++;
                }
            }
        }
        if (count == 0) {
            moreMsgView.setVisibility(View.GONE);
            return;
        }
        moreMsgView.setTag(count);
        TextView tv_moreMsg = (TextView) moreMsgView.findViewById(R.id.tv_moreMsg);
        if (tv_moreMsg != null) {
            String regx = container.activity.getResources().getString(R.string.chatroom_more_msg);
            tv_moreMsg.setText(String.format(regx, count + ""));
        }
        moreMsgView.setVisibility(View.VISIBLE);
    }

    // 发送消息后，更新本地消息列表
    public void onMsgSend(IMMessage message) {
        // add to listView and refresh
        saveMessage(message, false);
        List<IMMessage> addedListItems = new ArrayList<>(1);
        addedListItems.add(message);

        adapter.notifyDataSetChanged();
        ListViewUtil.scrollToBottom(messageListView);
    }

    private void saveMessage(final List<IMMessage> messageList, boolean addFirst) {
        if (messageList == null || messageList.isEmpty()) {
            return;
        }

        for (IMMessage msg : messageList) {
            saveMessage(msg, addFirst);
        }
    }

    public void saveMessage(final IMMessage message, boolean addFirst) {
        if (message == null) {
            return;
        }

        if (items.size() >= MESSAGE_CAPACITY) {
//            items.poll();
            firstVisibleMsg = items.poll();
        }

        if (addFirst) {
            items.add(0, message);
        } else {
            items.add(message);
        }
    }

    /**
     * *************** implements TAdapterDelegate ***************
     */
    @Override
    public int getViewTypeCount() {
        return ChatRoomMsgViewHolderFactory.getViewTypeCount();
    }

    @Override
    public Class<? extends TViewHolder> viewHolderAtPosition(int position) {
        return ChatRoomMsgViewHolderFactory.getViewHolderByType(items.get(position));
    }

    @Override
    public boolean enabled(int position) {
        return false;
    }


    //listview 最顶上的msg  有可能是通知类型 不可见
    IMMessage firstVisibleMsg = null;

    /*
    * 开关 是否过滤掉通知类型的消息
    * true表示过滤掉消息通知
    * 注意：
    * 这里使用true，回过滤数据；造成和判断listview是否为最后一条有冲突
    * 需要重写逻辑 AutoRefreshListView 的refreshableStart
    * */
    public static final boolean filterNotfMsg = true;
    /*开关允许看历史信息*/
    public static final boolean showHisMsg = false;


    /**
     * *************** MessageLoader ***************
     */
    private class MessageLoader implements AutoRefreshListView.OnRefreshListener {

        private static final int LOAD_MESSAGE_COUNT = 10;

        private IMMessage anchor;

        private boolean firstLoad = true;

        public MessageLoader() {
            anchor = null;
            //初始化的时候
            loadFromLocal();
//            if (showHisMsg) {
//                loadFromLocal();
//            }
        }

        private RequestCallback<List<ChatRoomMessage>> callback = new RequestCallbackWrapper<List<ChatRoomMessage>>() {
            @Override
            public void onResult(int code, List<ChatRoomMessage> messages, Throwable exception) {
                Log.v(TAG, "RequestCallback onResult");

                if (messages == null || messages.isEmpty()) {
                    return;
                }
                /*没有过滤之前的数据size*/
                int realSize = messages.size();

                if (filterNotfMsg) {
                    //过滤掉通知 add by fangzhu
                    List<ChatRoomMessage> showMsgList = new ArrayList<>();
                    for (int i = 0; i < messages.size(); i++) {
                        ChatRoomMessage item = messages.get(i);
//                        if (i == 0)//不能使用第一个应该使用最后一个
                        firstVisibleMsg = item;

                        if (item.getMsgType() != MsgTypeEnum.notification) {
                            showMsgList.add(item);
                        }

                    }
                    //重新设置list
                    messages = showMsgList;
                    //过滤以后可能size为0
                    if (messages == null || messages.isEmpty()) {
                        return;
                    }
                }


                if (messages != null) {
                    onMessageLoaded(messages, realSize);
                } else {
                    messageListView.onRefreshComplete(LOAD_MESSAGE_COUNT, LOAD_MESSAGE_COUNT, false);
                }
            }
        };

        private void loadFromLocal() {
            Log.v(TAG, "loadFromLocal");
            messageListView.onRefreshStart(AutoRefreshListView.Mode.START);
            NIMClient.getService(ChatRoomService.class).pullMessageHistory(container.account, anchor().getTime(), LOAD_MESSAGE_COUNT)
                    .setCallback(callback);
        }

        private IMMessage anchor() {
            if (items.size() == 0) {
                return (anchor == null ? ChatRoomMessageBuilder.createEmptyChatRoomMessage(container.account, 0) : anchor);
            } else {
                //add by fangzhu
                if (firstVisibleMsg != null)
                    return firstVisibleMsg;

                return items.get(0);
            }
        }

        /**
         * 历史消息加载处理
         *
         * @param realSize 没有过滤的数据size
         */
        private void onMessageLoaded(List<ChatRoomMessage> messages, int realSize) {
//            int count = messages.size();
            //check by fangzhu
            int count = realSize;


            if (items.size() > 0) {
                // 在第一次加载的过程中又收到了新消息，做一下去重
                for (IMMessage message : messages) {
                    for (IMMessage item : items) {
                        if (item.isTheSame(message)) {
                            items.remove(item);
                            break;
                        }
                    }
                }
            }

            List<IMMessage> result = new ArrayList<>();
            for (IMMessage message : messages) {
                result.add(message);
            }
            saveMessage(result, true);


            // 如果是第一次加载，updateShowTimeItem返回的就是lastShowTimeItem
            if (firstLoad) {
                ListViewUtil.scrollToBottom(messageListView);
            }

            refreshMessageList();
            messageListView.onRefreshComplete(count, LOAD_MESSAGE_COUNT, true);

            firstLoad = false;
        }

        /**
         * *************** OnRefreshListener ***************
         */
        @Override
        public void onRefreshFromStart() {
            if (showHisMsg) {
                loadFromLocal();
            } else {
                messageListView.onRefreshComplete();
            }
        }

        @Override
        public void onRefreshFromEnd() {
        }
    }


    /**
     * ************************* 观察者 ********************************
     */

    private void registerObservers(boolean register) {
        ChatRoomServiceObserver service = NIMClient.getService(ChatRoomServiceObserver.class);
        service.observeMsgStatus(messageStatusObserver, register);
        service.observeAttachmentProgress(attachmentProgressObserver, register);
    }

    /**
     * 消息状态变化观察者
     */
    Observer<ChatRoomMessage> messageStatusObserver = new Observer<ChatRoomMessage>() {
        @Override
        public void onEvent(ChatRoomMessage message) {
            if (isMyMessage(message)) {
                onMessageStatusChange(message);
            }
        }
    };

    /**
     * 消息附件上传/下载进度观察者
     */
    Observer<AttachmentProgress> attachmentProgressObserver = new Observer<AttachmentProgress>() {
        @Override
        public void onEvent(AttachmentProgress progress) {
            onAttachmentProgressChange(progress);
        }
    };

    private void onMessageStatusChange(IMMessage message) {
        int index = getItemIndex(message.getUuid());
        if (index >= 0 && index < items.size()) {
            IMMessage item = items.get(index);
            item.setStatus(message.getStatus());
            item.setAttachStatus(message.getAttachStatus());
            if (item.getAttachment() instanceof AVChatAttachment
                    || item.getAttachment() instanceof AudioAttachment) {
                item.setAttachment(message.getAttachment());
            }
            refreshViewHolderByIndex(index);
        }
    }

    private void onAttachmentProgressChange(AttachmentProgress progress) {
        int index = getItemIndex(progress.getUuid());
        if (index >= 0 && index < items.size()) {
            IMMessage item = items.get(index);
            float value = (float) progress.getTransferred() / (float) progress.getTotal();
            adapter.putProgress(item, value);
            refreshViewHolderByIndex(index);
        }
    }

    public boolean isMyMessage(IMMessage message) {
        return message.getSessionType() == container.sessionType
                && message.getSessionId() != null
                && message.getSessionId().equals(container.account);
    }

    /**
     * 刷新单条消息
     *
     * @param index
     */
    private void refreshViewHolderByIndex(final int index) {
        container.activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (index < 0) {
                    return;
                }

                Object tag = ListViewUtil.getViewHolderByIndex(messageListView, index);
                if (tag instanceof MsgViewHolderBase) {
                    MsgViewHolderBase viewHolder = (MsgViewHolderBase) tag;
                    viewHolder.refreshCurrentItem();
                }
            }
        });
    }

    private int getItemIndex(String uuid) {
        for (int i = 0; i < items.size(); i++) {
            IMMessage message = items.get(i);
            if (TextUtils.equals(message.getUuid(), uuid)) {
                return i;
            }
        }

        return -1;
    }

    private class MsgItemEventListener implements MsgAdapter.ViewHolderEventListener {

        @Override
        public void onFailedBtnClick(IMMessage message) {
            if (message.getDirect() == MsgDirectionEnum.Out) {
                // 发出的消息，如果是发送失败，直接重发，否则有可能是漫游到的多媒体消息，但文件下载
                if (message.getStatus() == MsgStatusEnum.fail) {
                    resendMessage(message); // 重发
                } else {
                    if (message.getAttachment() instanceof FileAttachment) {
                        FileAttachment attachment = (FileAttachment) message.getAttachment();
                        if (TextUtils.isEmpty(attachment.getPath())
                                && TextUtils.isEmpty(attachment.getThumbPath())) {
                            showReDownloadConfirmDlg(message);
                        }
                    } else {
                        resendMessage(message);
                    }
                }
            } else {
                showReDownloadConfirmDlg(message);
            }
        }

        @Override
        public boolean onViewHolderLongClick(View clickView, View viewHolderView, IMMessage item) {
            return true;
        }

        // 重新下载(对话框提示)
        private void showReDownloadConfirmDlg(final IMMessage message) {
            EasyAlertDialogHelper.OnDialogActionListener listener = new EasyAlertDialogHelper.OnDialogActionListener() {

                @Override
                public void doCancelAction() {
                }

                @Override
                public void doOkAction() {
                    // 正常情况收到消息后附件会自动下载。如果下载失败，可调用该接口重新下载
                    if (message.getAttachment() != null && message.getAttachment() instanceof FileAttachment)
                        NIMClient.getService(ChatRoomService.class).downloadAttachment((ChatRoomMessage) message, true);
                }
            };

            final EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(container.activity, null,
                    container.activity.getString(com.netease.nim.uikit.R.string.repeat_download_message), true, listener);
            dialog.show();
        }

        // 重发消息到服务器
        private void resendMessage(IMMessage message) {
            // 重置状态为unsent
            int index = getItemIndex(message.getUuid());
            if (index >= 0 && index < items.size()) {
                IMMessage item = items.get(index);
                item.setStatus(MsgStatusEnum.sending);
                refreshViewHolderByIndex(index);
            }

            NIMClient.getService(ChatRoomService.class).sendMessage((ChatRoomMessage) message, true);
        }

    }

    private void setEarPhoneMode(boolean earPhoneMode) {
        UserPreferences.setEarPhoneModeEnable(earPhoneMode);
        MessageAudioControl.getInstance(container.activity).setEarPhoneModeEnable(earPhoneMode);
    }

    public MsgAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(MsgAdapter adapter) {
        this.adapter = adapter;
    }
}
