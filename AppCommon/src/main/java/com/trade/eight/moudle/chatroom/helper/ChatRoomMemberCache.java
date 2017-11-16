package com.trade.eight.moudle.chatroom.helper;

import android.text.TextUtils;

import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.trade.eight.tools.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天室成员资料缓存
 * Created by huangjun on 2016/1/18.
 */
public class ChatRoomMemberCache {
    //固定成员有四种类型，分别是创建者,管理员,普通用户,受限用户。禁言用户和黑名单用户都属于受限用户
    /*每页限制多少条，不能超过200，超过200， 数据如果有还是会返回还是200,这里目前循环查，只有设置成200有效*/
    public static final int LIMIT = 200;
    /*注意这个不能改变，这个就是每页最多返回的数据200条*/
    public static final int PAGE_SIZE_MAX = 200;
    /*防止出错  累计最大页数，大于这个数据就不获取了，防止云信的接口出错，每次都查到200条*/
    public static final int PAGE_MAX = 20;//等于可以允许PAGE_MAX x 200条数据 //8元目前都只有四页就查完了
    /*记录页面*/
    int page = 1;

    private static final String TAG = "ChatRoomMemberCache";

    public static ChatRoomMemberCache getInstance() {
        return InstanceHolder.instance;
    }

    private Map<String, Map<String, ChatRoomMember>> cache = new HashMap<>();

    private Map<String, List<SimpleCallback<ChatRoomMember>>> frequencyLimitCache = new HashMap<>(); // 重复请求处理

    private List<RoomMemberChangedObserver> roomMemberChangedObservers = new ArrayList<>();

    public void clear() {
        cache.clear();
        frequencyLimitCache.clear();
        roomMemberChangedObservers.clear();
    }

    public void clearRoomCache(String roomId) {
        if (cache.containsKey(roomId)) {
            cache.remove(roomId);
        }
    }

    public ChatRoomMember getChatRoomMember(String roomId, String account) {
        if (cache.containsKey(roomId)) {
            return cache.get(roomId).get(account);
        }

        return null;
    }

    public void saveMyMember(ChatRoomMember chatRoomMember) {
        saveMember(chatRoomMember);
    }

    /**
     * 从服务器获取聊天室成员资料（去重处理）（异步）
     */
    public void fetchMember(final String roomId, final String account, final SimpleCallback<ChatRoomMember> callback) {
        if (TextUtils.isEmpty(roomId) || TextUtils.isEmpty(account)) {
            callback.onResult(false, null);
            return;
        }

        // 频率控制
        if (frequencyLimitCache.containsKey(account)) {
            if (callback != null) {
                frequencyLimitCache.get(account).add(callback);
            }
            return; // 已经在请求中，不要重复请求
        } else {
            List<SimpleCallback<ChatRoomMember>> cbs = new ArrayList<>();
            if (callback != null) {
                cbs.add(callback);
            }
            frequencyLimitCache.put(account, cbs);
        }

        // fetch
        List<String> accounts = new ArrayList<>(1);
        accounts.add(account);
        NIMClient.getService(ChatRoomService.class).fetchRoomMembersByIds(roomId, accounts).setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>() {
            @Override
            public void onResult(int code, List<ChatRoomMember> members, Throwable exception) {
                ChatRoomMember member = null;
                boolean hasCallback = !frequencyLimitCache.get(account).isEmpty();
                boolean success = code == ResponseCode.RES_SUCCESS && members != null && !members.isEmpty();

                // cache
                if (success) {
                    saveMembers(members);
                    member = members.get(0);
                } else {
                    LogUtil.e(TAG, "fetch chat room member failed, code=" + code);
                }

                // callback
                if (hasCallback) {
                    List<SimpleCallback<ChatRoomMember>> cbs = frequencyLimitCache.get(account);
                    for (SimpleCallback<ChatRoomMember> cb : cbs) {
                        cb.onResult(success, member);
                    }
                }

                frequencyLimitCache.remove(account);
            }
        });
    }

    /**
     * 注意云信的逻辑
     * 1、查MemberQueryType.NORMAL 指的是查三类，（不包含正常使用的用户）
     * 房间创建者，房间管理员，房间被设置成limited的(拉黑，禁言的用户) 不管在线情况都会查出来。
     * 2、传入的参数limit  必须<=200,大于200返回的还是200,所以大于200无效。（不能参考demo，demo就是错误的）
     * 3、time第一次传0，后面传上一页最后一个的updatetime
     * 4、从获取到的每页数据中筛选出管理员 缓存在本地
     *
     * @param roomId
     * @param memberQueryType
     * @param time
     * @param limit
     * @param callback
     */
    public void fetchRoomMembers(final String roomId, final MemberQueryType memberQueryType, final long time, final int limit,
                                 final SimpleCallback<List<ChatRoomMember>> callback) {
        page = 1;//重置
        query(roomId, memberQueryType, time, limit, callback, PAGE_MAX);
    }

    public void query (final String roomId, final MemberQueryType memberQueryType,
                       final long time, final int limit,
                       final SimpleCallback<List<ChatRoomMember>> callback, final int maxPage) {
        if (TextUtils.isEmpty(roomId)) {
            callback.onResult(false, null);
            return;
        }

        NIMClient.getService(ChatRoomService.class).fetchRoomMembers(roomId, memberQueryType, time, limit).setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>() {
            @Override
            public void onResult(int code, List<ChatRoomMember> result, Throwable exception) {
                boolean success = code == ResponseCode.RES_SUCCESS;

                if (success) {
                    if (result != null) {
                        Log.v(TAG, "fetchRoomMembers=" + result.size());
                        saveMembers(result);
                        if (result.size() > 0 && result.size() == PAGE_SIZE_MAX) {
                            //证明还没有拉完数据，循环继续
                            long updateTime = result.get(result.size() - 1).getUpdateTime();
                            page++;
                            //如果page > PAGE_MAX数据还没查完。 就认为接口出错了，不循环了，防止死循环。
                            if (page <= PAGE_MAX)
                                query(roomId, memberQueryType, updateTime, limit, callback, maxPage);
                        }
                    }
                } else {
                    LogUtil.e(TAG, "fetch members by page failed, code:" + code);
                }

                if (callback != null) {
                    callback.onResult(success, result);
                }
            }
        });
    }
    //只保留管理员的
    boolean keep_Admin = true;
    private void saveMember(ChatRoomMember member) {
        if (member == null)
            return;
        if (keep_Admin) {
            if (member.getMemberType() != MemberType.CREATOR
                    && member.getMemberType() != MemberType.ADMIN)
                //不是管理员或者主播  就不用 cash
                return;
        }
        Log.v(TAG, "getNick()=" + member.getNick() + " getMemberType=" + member.getMemberType() + " getAccount=" + member.getAccount());
        if (member != null && !TextUtils.isEmpty(member.getRoomId()) && !TextUtils.isEmpty(member.getAccount())) {
            Map<String, ChatRoomMember> members = cache.get(member.getRoomId());

            if (members == null) {
                members = new HashMap<>();
                cache.put(member.getRoomId(), members);
            }

            members.put(member.getAccount(), member);
        }
    }

    private void saveMembers(List<ChatRoomMember> members) {
        if (members == null || members.isEmpty()) {
            return;
        }

        for (ChatRoomMember m : members) {
            saveMember(m);
        }
    }

    /**
     * ************************************ 单例 ***************************************
     */
    static class InstanceHolder {
        final static ChatRoomMemberCache instance = new ChatRoomMemberCache();
    }

    /**
     * ********************************** 监听 ********************************
     */

    public void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
    }

    private Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
        @Override
        public void onEvent(List<ChatRoomMessage> messages) {
            if (messages == null || messages.isEmpty()) {
                return;
            }

            for (IMMessage msg : messages) {
                if (msg == null) {
                    LogUtil.e(TAG, "receive chat room message null");
                    continue;
                }

                if (msg.getMsgType() == MsgTypeEnum.notification) {
                    handleNotification(msg);
                }
            }
        }
    };

    private void handleNotification(IMMessage message) {
        if (message.getAttachment() == null) {
            return;
        }

        String roomId = message.getSessionId();
        ChatRoomNotificationAttachment attachment = (ChatRoomNotificationAttachment) message.getAttachment();
        List<String> targets = attachment.getTargets();
        if (targets != null) {
            for (String target : targets) {
                ChatRoomMember member = getChatRoomMember(roomId, target);
                handleMemberChanged(attachment.getType(), member);
            }
        }
    }

    private void handleMemberChanged(NotificationType type, ChatRoomMember member) {
        if (member == null) {
            return;
        }

        switch (type) {
            case ChatRoomMemberIn:
                for (RoomMemberChangedObserver o : roomMemberChangedObservers) {
                    o.onRoomMemberIn(member);
                }
                break;
            case ChatRoomMemberExit:
                for (RoomMemberChangedObserver o : roomMemberChangedObservers) {
                    o.onRoomMemberExit(member);
                }
                break;
            case ChatRoomManagerAdd:
                member.setMemberType(MemberType.ADMIN);
                break;
            case ChatRoomManagerRemove:
                member.setMemberType(MemberType.NORMAL);
                break;
            case ChatRoomMemberBlackAdd:
                member.setInBlackList(true);
                break;
            case ChatRoomMemberBlackRemove:
                member.setInBlackList(false);
                break;
            case ChatRoomMemberMuteAdd:
                member.setMuted(true);
                break;
            case ChatRoomMemberMuteRemove:
                member.setMuted(false);
                member.setMemberType(MemberType.GUEST);
                break;
            case ChatRoomCommonAdd:
                member.setMemberType(MemberType.NORMAL);
                break;
            case ChatRoomCommonRemove:
                member.setMemberType(MemberType.GUEST);
                break;
            default:
                break;
        }

        saveMember(member);
    }

    /**
     * ************************** 在线用户变化通知 ****************************
     */

    public interface RoomMemberChangedObserver {
        void onRoomMemberIn(ChatRoomMember member);

        void onRoomMemberExit(ChatRoomMember member);
    }

    public void registerRoomMemberChangedObserver(RoomMemberChangedObserver o, boolean register) {
        if (o == null) {
            return;
        }

        if (register) {
            if (!roomMemberChangedObservers.contains(o)) {
                roomMemberChangedObservers.add(o);
            }
        } else {
            roomMemberChangedObservers.remove(o);
        }
    }
}