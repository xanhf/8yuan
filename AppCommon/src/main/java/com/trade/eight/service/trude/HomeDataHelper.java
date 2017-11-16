package com.trade.eight.service.trude;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.dao.UserInfoDao;
import com.trade.eight.entity.RankMostNewData;
import com.trade.eight.entity.home.HomeInformation;
import com.trade.eight.entity.home.HomePagerItem;
import com.trade.eight.entity.response.CommonResponse;
import com.trade.eight.entity.response.CommonResponse4List;
import com.trade.eight.entity.trade.TradeOrder;
import com.trade.eight.entity.trude.UserInfo;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.ApiConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2015/9/6.
 */
public class HomeDataHelper {

    public static List<HomePagerItem> getHomePagerItems(String url) {
        List<HomePagerItem> homePagerItemList = null;
        try {
            String res = HttpClientHelper.getStringFromGet(null, url);
            JSONObject jsonObject = new JSONObject(res);
            if (jsonObject.has("result")) {
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                Gson gson = new Gson();
                homePagerItemList = gson.fromJson(jsonArray.toString(), new TypeToken<List<HomePagerItem>>() {
                }.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return homePagerItemList;
    }

    /**
     * 获取首页资讯
     *
     * @param context
     * @param page
     * @return
     */
    public static CommonResponse4List<HomeInformation> getHomeInformationList(Context context, int page) {
        UserInfoDao dao = new UserInfoDao(context);
//        if (!dao.isLogin()){
//            return null;
//        }
        Map<String, String> map = ApiConfig.getCommonMap(context);
        if (dao.isLogin()) {
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
        }
        map.put("page", page + "");
        map.put("pageSize", "20");
        map.put("type", "");
        String res = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_HOME_INFOEMATION_LIST, map);
        CommonResponse4List<HomeInformation> response = CommonResponse4List.fromJson(res, HomeInformation.class);
        return response;
    }

    /**
     * 首页资讯利多利空支持操作
     *
     * @param context
     * @param clickType
     * @param type
     * @param objectId
     * @return
     */
    public static CommonResponse<HomeInformation> operationInsertHomeInformation(Context context, int clickType, int type, String objectId) {
        UserInfoDao dao = new UserInfoDao(context);
//        if (!dao.isLogin()){
//            return null;
//        }
        Map<String, String> map = ApiConfig.getCommonMap(context);
        if (dao.isLogin()) {
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
        }
        map.put("objectId", objectId);
        map.put("clickType", clickType + "");
        map.put("type", type + "");
        String res = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_HOME_INFOEMATION_OPERATIONINSERT, map);
        CommonResponse<HomeInformation> response = CommonResponse.fromJson(res, HomeInformation.class);
        return response;
    }


    /**
     * 获取最新晒单用户信息
     *
     * @param context
     * @return
     */
    public static CommonResponse<RankMostNewData> getHomeRankMostNewData(Context context) {
        UserInfoDao dao = new UserInfoDao(context);
//        if (!dao.isLogin()){
//            return null;
//        }
        Map<String, String> map = ApiConfig.getCommonMap(context);
        if (dao.isLogin()) {
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
        }

        String res = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_PROFIT_MOSTNEWRANK, map);
        CommonResponse<RankMostNewData> response = CommonResponse.fromJson(res, RankMostNewData.class);
        return response;
    }

    /**
     * 获取个人当日盈利单
     * @param context
     * @return
     */
    public static CommonResponse4List<TradeOrder> getMyProfitOrderList(Context context) {
        UserInfoDao dao = new UserInfoDao(context);
//        if (!dao.isLogin()){
//            return null;
//        }
        Map<String, String> map = ApiConfig.getCommonMap(context);
        if (dao.isLogin()) {
            map.put(UserInfo.UID, dao.queryUserInfo().getUserId());
        }

        String res = HttpClientHelper.getStringFromPost(context, AndroidAPIConfig.URL_PROFIT_MYPROFITORDER, map);
        CommonResponse4List<TradeOrder> response = CommonResponse4List.fromJson(res, TradeOrder.class);
        return response;
    }
}
