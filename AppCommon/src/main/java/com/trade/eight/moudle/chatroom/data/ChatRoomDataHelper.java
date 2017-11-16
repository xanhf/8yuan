package com.trade.eight.moudle.chatroom.data;

import android.content.Context;

import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;
import com.trade.eight.service.JSONObjectUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fangzhu on 16/12/28.
 * 直播室扩展功能数据请求帮助类
 */
public class ChatRoomDataHelper {
    /**
     * 获取敏感词
     *
     * @param context
     * @param userId  用户id
     * @return
     */
    public static List<ChatRoomCheckObj> getKeyWordList(Context context, String userId) {
        List<ChatRoomCheckObj> list = new ArrayList<>();
        try {
            Map<String, String> map = new LinkedHashMap<>();
            map.put(UserInfo.UID, userId);
            map = ApiConfig.getParamMap(context, map);
            String url = AndroidAPIConfig.URL_LIVE_KEYWORD;
            String res = HttpClientHelper.getStringFromPost(context, url, map);
            Log.v("getKeyWordList", "res="+res);
            JSONObject jsonObject = new JSONObject(res);
            if (JSONObjectUtil.getBoolean(jsonObject, "success", false)) {
                JSONArray array = jsonObject.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    ChatRoomCheckObj obj = new ChatRoomCheckObj();
                    String text = array.getString(i);
                    if (StringUtil.isEmpty(text))
                        continue;
                    obj.setText(text);
                    list.add(obj);
                }
            }
//            CommonResponse4List<ChatRoomCheckObj> response4List = CommonResponse4List.fromJson(res, ChatRoomCheckObj.class);
//            if (response4List != null && response4List.isSuccess()) {
//                return response4List.getData();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 禁言
     *
     * @param context
     * @param userId
     * @param remark  原因
     * @return
     */
    public static CommonResponse<Object> disableUser(Context context, String userId, String remark) {
        try {
            Map<String, String> map = new LinkedHashMap<>();
            map.put(UserInfo.UID, userId);
            map.put("remark", remark);

            map = ApiConfig.getParamMap(context, map);
            String url = AndroidAPIConfig.URL_LIVE_DISABLE_USER;
            String res = HttpClientHelper.getStringFromPost(context, url, map);
            return CommonResponse.fromJson(res, Object.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 直播室是否允许发图
     * @param context
     * @param roomId
     * @param channelId
     * @return
     */
    public static  CommonResponse<Integer> sendPicStatus(Context context, String roomId, String channelId){
        try {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("chatRoomId", roomId);
            map.put("channelId", channelId);
            map = ApiConfig.getParamMap(context, map);
            String url = AndroidAPIConfig.URL_LIVE_SENDPIC;
            String res = HttpClientHelper.getStringFromPost(context, url, map);
            return CommonResponse.fromJson(res, Integer.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
