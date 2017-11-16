package com.trade.eight.moudle.home.live;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.entity.live.LiveRoomNew;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;

import java.util.Map;

/**
 * Created by dufangzhu on 2017/3/24.
 * 直播的数据请求类
 */

public class LiveDatahelp {

    public static CommonResponse4List<LiveRoomNew> getRoomList(Context context) {
        try {
            Map<String, String> stringMap = ApiConfig.getCommonMap(context);
            stringMap.put(ApiConfig.PARAM_TIME, System.currentTimeMillis() + "");
            stringMap.put(ApiConfig.PARAM_AUTH, ApiConfig.getAuth(context, stringMap));
            String str = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_LIVE_LIST_V4, stringMap);
            if (str != null) {
                CommonResponse4List<LiveRoomNew> response = CommonResponse4List.fromJson(str, LiveRoomNew.class);
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询单个的房间信息
     *
     * @param context
     * @param roomId  房间ID
     * @return
     */
    public static LiveRoomNew getRoom(Context context, String roomId) {
        try {
            CommonResponse4List<LiveRoomNew> res = getRoomList(context);
            if (res != null
                    && res.getData() != null
                    && res.isSuccess()) {
                for (LiveRoomNew item : res.getData()) {
                    if (item.getChatRoomId() != null && item.getChatRoomId().equals(roomId))
                        return item;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void loadRoom(final Context context, final String roomId, final Handler.Callback callback) {
        if (roomId == null)
            return;
        new AsyncTask<Void, Void, LiveRoomNew>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected LiveRoomNew doInBackground(Void... params) {
                return getRoom(context, roomId);
            }

            @Override
            protected void onPostExecute(LiveRoomNew roomNew) {
                super.onPostExecute(roomNew);
//                context.hideNetLoadingProgressDialog();
                try {
                    if (callback != null) {
                        Message message = new Message();
                        message.obj = roomNew;
                        callback.handleMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }


}
