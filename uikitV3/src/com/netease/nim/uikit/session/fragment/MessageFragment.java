package com.netease.nim.uikit.session.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.session.SessionCustomization;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nim.uikit.session.actions.ImageAction;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nim.uikit.session.module.Container;
import com.netease.nim.uikit.session.module.ModuleProxy;
import com.netease.nim.uikit.session.module.input.InputPanel;
import com.netease.nim.uikit.session.module.list.MessageListPanel;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天界面基类
 * <p/>
 * Created by huangjun on 2015/2/1.
 */
public class MessageFragment extends TFragment implements ModuleProxy {

    private View rootView;

    private SessionCustomization customization;

    protected static final String TAG = "MessageActivity";

    // 聊天对象
    protected String sessionId; // p2p对方Account或者群id

    protected SessionTypeEnum sessionType;

    // modules
    protected InputPanel inputPanel;
    protected MessageListPanel messageListPanel;
    //进入聊天界面的时候   是否从服务端获取一次历史信息；true 获取；false 获取本地的数据
    protected boolean remote = false;
    TextView btn_info;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        parseIntent();
        initListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nim_message_fragment, container, false);
        return rootView;
    }

    int sendCount = 0;

    public void initListener() {
        //按钮是act的标题栏
        btn_info = (TextView) getActivity().findViewById(R.id.btn_info);
        if (btn_info != null) {
            if (sessionId == null)
                btn_info.setVisibility(View.GONE);
            btn_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //允许发两次
                    if (sendCount >= 2) {
                        Toast.makeText(getActivity(), "请不要重复提交", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (sessionId == null)
                        return;
                    //构建文本信息
                    String text = getDeviceInfo();
                    IMMessage message = MessageBuilder.createTextMessage(sessionId, sessionType, text);
                    sendMessage(message);
                    sendCount++;
                }
            });
        }
    }

    public String NVL(Object obj, String value) {
        try {
            if (obj == null) {
                return value;
            }
            if (String.valueOf(obj).trim().equals("")) {
                return value;
            }
            if (String.valueOf(obj).trim().equalsIgnoreCase("null")) {
                return value;
            }
            return String.valueOf(obj);
        } catch (Exception e) {
            return value;
        }
    }

    public String getDeviceInfo() {
        String versionName = "";
        PackageManager pm = getActivity().getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = pm.getPackageInfo(getActivity().getPackageName(), 0);
            if (packageInfo != null)
                versionName = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder str = new StringBuilder();
        try {
            str.append("软件版本：Android " + versionName);
            str.append("\n手机型号：" + NVL(Build.BRAND, ""));//厂商meizu
            str.append(" " + NVL(Build.MODEL, ""));//型号mx5
            str.append(" " + NVL(Build.PRODUCT, ""));//m3note
            str.append("\n系统版本：" + NVL(Build.VERSION.RELEASE, ""));//5.1
//            str.append("|SDK_INT="+NVL(Build.VERSION.SDK_INT, ""));//22
            str.append("\nCPU型号：" + NVL(Build.CPU_ABI, ""));//cpu1
            str.append(" " + NVL(Build.CPU_ABI2, ""));//cpu2
//            str.append("|DISPLAY=" + ConvertUtil.NVL(Build.DISPLAY, ""));//手机显示的版本号 Flyme 5.1.10.0A

        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        Log.v(TAG, str.toString());
        return str.toString();
    }

    /**
     * ***************************** life cycle *******************************
     */

    @Override
    public void onPause() {
        super.onPause();

        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE,
                SessionTypeEnum.None);
        inputPanel.onPause();
        messageListPanel.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        messageListPanel.onResume();
        NIMClient.getService(MsgService.class).setChattingAccount(sessionId, sessionType);
        getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL); // 默认使用听筒播放
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        messageListPanel.onDestroy();
        registerObservers(false);
    }

    public boolean onBackPressed() {
        if (inputPanel.collapse(true)) {
            return true;
        }

        if (messageListPanel.onBackPressed()) {
            return true;
        }
        return false;
    }

    public void refreshMessageList() {
        messageListPanel.refreshMessageList();
    }

    private void parseIntent() {
        sessionId = getArguments().getString(Extras.EXTRA_ACCOUNT);
        sessionType = (SessionTypeEnum) getArguments().getSerializable(Extras.EXTRA_TYPE);
        IMMessage anchor = (IMMessage) getArguments().getSerializable(Extras.EXTRA_ANCHOR);

        customization = (SessionCustomization) getArguments().getSerializable(Extras.EXTRA_CUSTOMIZATION);
        Container container = new Container(getActivity(), sessionId, sessionType, this);

        if (messageListPanel == null) {
            messageListPanel = new MessageListPanel(container, rootView, anchor, false, remote);
        } else {
            messageListPanel.reload(container, anchor);
        }

        if (inputPanel == null) {
            inputPanel = new InputPanel(container, rootView, getActionList());
            inputPanel.setCustomization(customization);
        } else {
            inputPanel.reload(container, customization);
        }

        registerObservers(true);

        if (customization != null) {
            messageListPanel.setChattingBackground(customization.backgroundUri, customization.backgroundColor);
        }
    }

    /**
     * ************************* 消息收发 **********************************
     */
    // 是否允许发送消息
    protected boolean isAllowSendMessage(final IMMessage message) {
        return true;
    }

    /**
     * ****************** 观察者 **********************
     */

    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(incomingMessageObserver, register);
        service.observeMessageReceipt(messageReceiptObserver, register);
    }

    /**
     * 消息接收观察者
     */
    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            if (messages == null || messages.isEmpty()) {
                return;
            }

            messageListPanel.onIncomingMessage(messages);
            sendMsgReceipt(); // 发送已读回执
        }
    };

    private Observer<List<MessageReceipt>> messageReceiptObserver = new Observer<List<MessageReceipt>>() {
        @Override
        public void onEvent(List<MessageReceipt> messageReceipts) {
            receiveReceipt();
        }
    };


    /**
     * ********************** implements ModuleProxy *********************
     */
    @Override
    public boolean sendMessage(IMMessage message) {
        if (!isAllowSendMessage(message)) {
            return false;
        }

        // send message to server and save to db
        NIMClient.getService(MsgService.class).sendMessage(message, false);

        messageListPanel.onMsgSend(message);

        return true;
    }

    @Override
    public void onInputPanelExpand() {
        messageListPanel.jumpReload();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        inputPanel.onActivityResult(requestCode, resultCode, data);
        messageListPanel.onActivityResult(requestCode, resultCode, data);
    }

    // 操作面板集合
    protected List<BaseAction> getActionList() {
        List<BaseAction> actions = new ArrayList<>();
        actions.add(new ImageAction());
//        actions.add(new VideoAction());
//        actions.add(new LocationAction());
//
//        if (customization != null && customization.actions != null) {
//            actions.addAll(customization.actions);
//        }
        return actions;
    }

    /**
     * 发送已读回执
     */
    private void sendMsgReceipt() {
        messageListPanel.sendReceipt();
    }

    /**
     * 收到已读回执
     */
    public void receiveReceipt() {
        messageListPanel.receiveReceipt();
    }
}
