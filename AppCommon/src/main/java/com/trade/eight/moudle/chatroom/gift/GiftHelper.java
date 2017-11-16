package com.trade.eight.moudle.chatroom.gift;

import android.content.Context;

import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.live.LiveRoomNew;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dufangzhu on 2017/4/5.
 * 直播室礼物的帮助类
 */

public class GiftHelper {

    /**
     * 获取直播室礼物列表 包含可用积分
     *
     * @param context
     * @return
     */
    public static synchronized CommonResponse<GiftSuperObj> getIntegral(Context context) {
        try {
            UserInfoDao userInfoDao = new UserInfoDao(context);
            if (!userInfoDao.isLogin()) {
                return null;
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put(UserInfo.UID, userInfoDao.queryUserInfo().getUserId());

            map = ApiConfig.getParamMap(context, map);
            String res = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_LIVE_GIFT_LIST, map);
            CommonResponse<GiftSuperObj> response = CommonResponse.fromJson(res, GiftSuperObj.class);

            //缓存本地数据
            if (response != null
                    && response.isSuccess()) {
                if (response.getData() != null
                        && response.getData().getGiftList() != null) {
                    GiftPanUtil.giftCashMap.clear();
                    for (GiftObj giftObj : response.getData().getGiftList()) {
                        GiftPanUtil.giftCashMap.put(giftObj.getGiftId(), giftObj);
                    }
                }
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送礼物
     *
     * @param context
     * @param giftObj 礼物对象
     * @param roomNew 直播室对象
     * @return
     */
    public static synchronized CommonResponse<GiftSuperObj> send(Context context, GiftObj giftObj, LiveRoomNew roomNew) {
        try {
            UserInfoDao userInfoDao = new UserInfoDao(context);
            if (!userInfoDao.isLogin()) {
                return null;
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put(UserInfo.UID, userInfoDao.queryUserInfo().getUserId());
            map.put("giftId", giftObj.getGiftId());
            map.put("authorId", roomNew.getAuthorId() + "");
            map.put("channelId", roomNew.getChannelId());
            map.put("roomId", roomNew.getChatRoomId());

            map = ApiConfig.getParamMap(context, map);
            String res = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_LIVE_GIFT_SEND, map);
            CommonResponse<GiftSuperObj> response = CommonResponse.fromJson(res, GiftSuperObj.class);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

