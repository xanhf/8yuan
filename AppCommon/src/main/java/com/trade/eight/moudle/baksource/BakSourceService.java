package com.trade.eight.moudle.baksource;

import android.content.Context;

import com.trade.eight.app.ServiceException;
import com.trade.eight.config.AppSetting;
import com.trade.eight.dao.OptionalDao;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.trade.TradeProduct;
import com.trade.eight.net.HttpClientHelper;
import com.trade.eight.service.JSONObjectUtil;
import com.trade.eight.service.NumberUtil;
import com.trade.eight.tools.ConvertUtil;
import com.trade.eight.tools.StringUtil;
import com.trade.eight.tools.trade.TradeConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fangzhu on 2015/4/17.
 */
public class BakSourceService {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final int INIT_SHOW_SIZE = 4;//默认最多显示15条

    /**
     * 根据交易所的type 获取当前交易所的品种
     * <p/>
     * 添加品种页面需要调用
     */
    public static List<Optional> getOptionalsByType(Context context, String realType, String localSource)
            throws ServiceException {
        try {
            if (realType == null)
                return null;

            String url = null;
            if (BakSourceInterface.specialSource.contains(realType)) {
                url = BakSourceInterface.URL_SOURCE_ALLPRODUCT_WEIPAN.replace("{excode}", realType);
            } else {
                url = BakSourceInterface.setCommonParam4get(BakSourceInterface.URL_SOURCE_ALLPRODUCT, realType);
                url = url.replace("{source}", realType);
            }


            String res = HttpClientHelper.getStringFromGet(context, url);
            List<Optional> optionals = null;
            if (BakSourceInterface.specialSource.contains(realType)) {
                optionals = parseOptionals4Weipan(res);
                List<Optional> list = new ArrayList<>();

                //修改排序把油放在第一位
                if (optionals != null) {
                    for (Optional o: optionals) {
                        //行情过滤掉吉银 油 2017-02-13
                        //吉银
                        if (TradeConfig.code_jn.equals(realType)
                            && TradeProduct.CODE_JN_AG.equals(o.getProductCode())) {
                            continue;
                        }
                        //吉油
                        if (TradeConfig.code_jn.equals(realType)
                                && TradeProduct.CODE_JN_CO.equals(o.getProductCode())) {
                            continue;
                        }

                        if (TradeProduct.CODE_OIL.equals(o.getProductCode())) {
                            list.add(0, o);
                        } else
                            list.add(o);
                    }
                    optionals = list;
                }
            } else {
                optionals = parseOptionals(res, localSource);
            }
            if (optionals != null) {
                AppSetting.getInstance(context).setInitOptionalType(localSource, true);
                OptionalDao dao = new OptionalDao(context);
                dao.updateOptionalList(optionals);
            }
            return optionals;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Optional> firstInit4Ten(Context context) {
        try {
            List<Optional> optionals = getOptionalsByType(context, BakSourceInterface.TRUDE_SOURCE_INIT_APP, BakSourceInterface.TRUDE_SOURCE_INIT_APP);
            OptionalDao dao = new OptionalDao(context);

            if (optionals == null)
                optionals = new ArrayList<Optional>();

            List<Optional> localOptionals = new ArrayList<Optional>();
            String code = "CONC";
            Optional optional = dao.queryOptionalsBySymbol(code);
            if (optional == null) {
                optional = new Optional();
                optional.setTitle("原油指数");
                optional.setTreaty(code);
                optional.setProductCode(code);
                optional.setCustomCode(code);
                optional.setOptional(true);
                optional.setType(BakSourceInterface.TRUDE_SOURCE_NYMEX);
                optional.setAdd_time(sdf.format(new Date()));

                dao.addOrUpdateOptional(optional);
            }
            localOptionals.add(optional);

            code = "USD";
            optional = dao.queryOptionalsBySymbol(code);
            if (optional == null) {
                optional = new Optional();
                optional.setTitle("美元指数");
                optional.setTreaty(code);
                optional.setProductCode(code);
                optional.setCustomCode(code);
                optional.setOptional(true);
                optional.setType(BakSourceInterface.TRUDE_SOURCE_WH);
                optional.setAdd_time(sdf.format(new Date()));

                dao.addOrUpdateOptional(optional);
            }
            localOptionals.add(optional);

            List<Optional> list = getOptionals(context, localOptionals);
            if (list != null)
                optionals.addAll(list);
            for (int i = 0; i < optionals.size(); i++) {
                Optional op = optionals.get(i);
                op.setOptional(true);
                op.setAdd_time(sdf.format(new Date()));
                op.setDrag(optionals.size() - i);
                dao.updateFavLocal(op);

            }
            optionals = dao.queryAllMyOptionals();

            return optionals;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



//    /**
//     * 根据当前id查询  ?code=WGJS|XAP,WH|USDHKD
//     *
//     * @param context
//     * @param ids
//     * @param localSource 本地数据库的type
//     * @return
//     */
//    public static List<Optional> getOptionalsByIds(Context context, String ids, String localSource) {
//        new ArrayList<Optional>();
//        try {
//            if (ids == null)
//                return null;
//            if (localSource == null || localSource.trim().length() == 0)
//                localSource = BakSourceInterface.PARAM_CUSTOM;
//            String url = null;
//            if (BakSourceInterface.specialSource.contains(localSource)) {
//                url = BakSourceInterface.URL_GET_PRODUCTS_WEIPAN;
//                url = url.replace("{codes}", ids);
//            } else {
//                url = BakSourceInterface.setCommonParam4get(BakSourceInterface.URL_GET_PRODUCTS, localSource);
//                url = url.replace("{code}", ids);
//            }
//
//
//            String res = HttpClientHelper.getStringFromGet(context, url);
//            List<Optional> optionals = null;
//            if (BakSourceInterface.specialSource.contains(localSource)) {
//                optionals = parseOptionals4Weipan(res);
//            } else {
//                optionals = parseOptionals(res, localSource);
//            }
//
//            if (optionals != null) {
//                OptionalDao dao = new OptionalDao(context);
//                dao.updateOptionalList(optionals);
//            }
//            return optionals;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * 刷新行情
     *
     * @param context
     * @param list
     * @return
     */
    public static List<Optional> getOptionals(Context context, List<Optional> list) {
        try {
            if (list == null)
                return null;
            StringBuffer stringBuffer = new StringBuffer();

            StringBuffer specialStringBuffer = new StringBuffer();//非fx678接口的刷新


            //code=WGJS|XAP,WH|USDHKD
            for (int i = 0; i < list.size(); i++) {
                Optional o = list.get(i);
                String source = o.getType();
                if (source != null && BakSourceInterface.specialSource.contains(source)) {
                    if (i != list.size() - 1)
                        specialStringBuffer.append(o.getType() + "|" + o.getProductCode() + ",");
                    else
                        specialStringBuffer.append(o.getType() + "|" + o.getProductCode());
                } else {
                    if (i != list.size() - 1)
                        stringBuffer.append(o.getType() + "|" + o.getProductCode() + ",");
                    else
                        stringBuffer.append(o.getType() + "|" + o.getProductCode());
                }

            }
            String ids = stringBuffer.toString();
            if (ids.endsWith(",")) {
                ids = ids.substring(0, ids.length() - 1);
            }
            List<Optional> optionals = new ArrayList<>();
            if (ids != null && ids.trim().length() > 0) {
                String url = BakSourceInterface.setCommonParam4get(BakSourceInterface.URL_GET_PRODUCTS, BakSourceInterface.PARAM_CUSTOM);
                url = url.replace("{code}", ids);

                String res = HttpClientHelper.getStringFromGet(context, url);
                optionals = parseOptionals(res, "");
                if (optionals != null) {
                    OptionalDao dao = new OptionalDao(context);
                    dao.updateOptionalList(optionals);
                }
            }


            //刷新特殊交易所下的数据
            String specialIds = specialStringBuffer.toString();
            if (specialIds != null && specialIds.trim().length() > 0) {
                if (specialIds.endsWith(",")) {
                    specialIds = specialIds.substring(0, specialIds.length() - 1);
                }

                String specialUrl = BakSourceInterface.URL_GET_PRODUCTS_WEIPAN;
                specialUrl = specialUrl.replace("{codes}", specialIds);
                String specialRes = HttpClientHelper.getStringFromGet(context, specialUrl);
                List<Optional> specialOptionals = parseOptionals4Weipan(specialRes);
                if (specialOptionals != null) {
                    OptionalDao dao = new OptionalDao(context);
                    dao.updateOptionalList(specialOptionals);
                }
                optionals.addAll(specialOptionals);//每次都是在本地再去取得数据，可以不用管顺序
            }
            return optionals;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过codes刷新行情
     * @param context
     * @param list
     * @return
     */
    public static List<Optional> getOptionals(Context context, String list) {
        try {

            List<Optional> optionals = new ArrayList<>();
            //刷新特殊交易所下的数据
            String specialIds = list;
            if (specialIds != null && specialIds.trim().length() > 0) {
                if (specialIds.endsWith(",")) {
                    specialIds = specialIds.substring(0, specialIds.length() - 1);
                }

                String specialUrl = BakSourceInterface.URL_GET_PRODUCTS_WEIPAN;
                specialUrl = specialUrl.replace("{codes}", specialIds);
                String specialRes = HttpClientHelper.getStringFromGet(context, specialUrl);
                List<Optional> specialOptionals = parseOptionals4Weipan(specialRes);
                if (specialOptionals != null) {
                    OptionalDao dao = new OptionalDao(context);
                    dao.updateOptionalList(specialOptionals);
                }
                optionals.addAll(specialOptionals);//每次都是在本地再去取得数据，可以不用管顺序
            }
            return optionals;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 单个刷新
     *
     * @param context
     * @param optional
     * @return
     */
    public static Optional getOptionals(Context context, Optional optional) {
        if (BakSourceInterface.specialSource.contains(optional.getType())) {
            try {
                String specialUrl = BakSourceInterface.URL_GET_PRODUCTS_WEIPAN;
                specialUrl = specialUrl.replace("{codes}", optional.getType() + "|"+optional.getProductCode());
                String specialRes = HttpClientHelper.getStringFromGet(context, specialUrl);
                if (specialRes == null)
                    return null;
                JSONObject resJson = new JSONObject(specialRes);
                if (resJson.has("data")) {
                    JSONObject data = resJson.getJSONObject("data");
                    if (data.has("list")) {
                        //只有一个
                        JSONArray list = data.getJSONArray("list");
                        return parseWeipan(list.getJSONObject(0));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            List<Optional> list = new ArrayList<Optional>();
            list.add(optional);
            list = getOptionals(context, list);
            if (list != null && list.size() > 0) {
                if (list.size() == 1) {
                    Optional op = list.get(0);
                    OptionalDao dao = new OptionalDao(context);
                    dao.addOrUpdateOptional(op);
                    return op;
                }
            }
        }
        return null;
    }

    private static List<Optional> parseOptionals(String res, String source) {
        try {
            if (res == null)
                return null;
            List<Optional> optionals = new ArrayList<Optional>();
            JSONArray jsonArray = new JSONArray(res);
            if (jsonArray != null && jsonArray.length() > 0) {
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Optional optional = new Optional();
                    JSONObject object = jsonArray.getJSONObject(i);

                    if (object.has("Name")) {
                        String name = object.getString("Name");
                        if (name != null)
                            name = name.replace("[T]", "");
                        if (name != null && source != null && source.equalsIgnoreCase(BakSourceInterface.TRUDE_SOURCE_TJPME)) {
                            Pattern pattern = Pattern.compile("\\d");
                            Matcher matcher = pattern.matcher(name);
                            if (matcher.find())
                                continue;
                        }
                        optional.setTitle(name);
                    }
                    try {
                        String s = sf.format(new Date(JSONObjectUtil.getLong(object, "QuoteTime", 0) * 1000));
                        optional.setTime(s);
                        optional.setAdd_time(s);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (object.has("Code"))
                        optional.setProductCode(object.getString("Code"));
                    if (object.has("Code"))
                        optional.setCustomCode(object.getString("Code"));

                    if (object.has("Open")) {
                        optional.setOpening(NumberUtil.moveLast0(NumberUtil.beautifulDouble(object.getDouble("Open"),5)));
                    }
                    if (object.has("High")) {
                        optional.setHighest(NumberUtil.moveLast0(NumberUtil.beautifulDouble(object.getDouble("High"),5)));
                    }
                    if (object.has("Low")) {
                        optional.setLowest(NumberUtil.moveLast0(NumberUtil.beautifulDouble(object.getDouble("Low"),5)));
                    }
                    double currentPrice = 0;
                    if (object.has("Last")) {
                        currentPrice = object.getDouble("Last");
                        optional.setNewest(currentPrice + "");
                    }
                    if (object.has("LastClose")) {
                        optional.setClosed(NumberUtil.moveLast0(NumberUtil.beautifulDouble(object.getDouble("LastClose"),5)));
                    }
                    //代码里头是把sellone 当成是最新价格newest的
                    if (object.has("Last")) {
                        optional.setSellone(NumberUtil.moveLast0(NumberUtil.beautifulDouble(object.getDouble("Last"),5)));
                    } else {
                        optional.setSellone("0");
                    }

                    if (object.has("Buy")) {
                        optional.setBuyone(NumberUtil.beautifulDouble(object.getDouble("Buy")));
                    } else {
                        optional.setBuyone("0");
                    }


                    optional.setTreaty(optional.getCustomCode());//if need

                    //just while isbaksource is true for currentMinView---start
                    if (object.has("End"))
                        optional.setBaksourceEnd(object.getString("End"));
                    if (object.has("Start"))
                        optional.setBaksourceStart(object.getString("Start"));
                    if (object.has("Middle"))
                        optional.setBaksourceMiddle(object.getString("Middle"));
                    //just while isbaksource is true for currentMinView---end


                    //处理停盘 sellone buyone 为0的时候 使用最新数据
                    try {
                        if (Double.parseDouble(optional.getSellone()) <= 0
                                && Double.parseDouble(optional.getBuyone()) <= 0
                                && optional.getNewest() != null) {
                            optional.setBuyone(optional.getNewest());
                            optional.setSellone(optional.getNewest());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!StringUtil.isEmpty(source))
                        optional.setType(source);//
                    optional.setInitData(true);//标记当前goods已经初始化了

                    //处理特殊需要修改的品种  美原油连续--->原油指数
                    if ("CONC".equals(optional.getProductCode())) {
                        optional.setTitle("原油指数");
                    }

                    optionals.add(optional);
                }
            }
            return optionals;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 这里解析交易所的数据和详情的数据使用同一个
     * 注意 详情页返回的数据有多余的
     *
     * @param res
     * @return
     */
    private static List<Optional> parseOptionals4Weipan(String res) {
        try {
            if (res == null)
                return null;
            List<Optional> optionals = new ArrayList<Optional>();
            JSONObject jsonObject = new JSONObject(res);
            if (!jsonObject.has("data")) {
                return optionals;
            }
            JSONObject data = jsonObject.getJSONObject("data");
            if (data == null)
                return optionals;
            if (!data.has("list") && !data.has("candle"))
                return optionals;
            JSONArray jsonArray = null;
            if (data.has("list")) {
                jsonArray = data.getJSONArray("list");
            } else if (data.has("candle")) {
                jsonArray = data.getJSONArray("candle");
            }

            if (jsonArray != null && jsonArray.length() > 0) {
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject object = jsonArray.getJSONObject(i);
                    Optional optional = parseWeipan(object);
                    if (optional == null)
                        continue;
                    optionals.add(optional);
                }
            }
            return optionals;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Optional parseWeipan(JSONObject object) {
        try {
            Optional optional = new Optional();

            if (object.has("name")) {
                optional.setTitle(JSONObjectUtil.getString(object, "name", ""));
            }
            if (object.has("excode")) {
                optional.setType(JSONObjectUtil.getString(object, "excode", ""));
            }

            try {
                String s = JSONObjectUtil.getString(object, "time", "");
                optional.setTime(s);
                optional.setAdd_time(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (object.has("open")) {
                optional.setOpening(JSONObjectUtil.getString(object, "open", "0"));
            }
            if (object.has("top")) {
                optional.setHighest(JSONObjectUtil.getString(object, "top", "0"));
            }
            if (object.has("low")) {
                optional.setLowest(JSONObjectUtil.getString(object, "low", "0"));
            }

            //Last 没有
//                    double currentPrice = 0;
//                    if (object.has("Last")) {
//                        currentPrice = object.getDouble("Last");
//                        optional.setNewest(currentPrice + "");
//                    }
//                    //LastClose 昨收没有拿今开是每天强行平仓的。
            if (object.has("last_close"))
                optional.setClosed(JSONObjectUtil.getString(object, "last_close", "0"));
//            if(!StringUtils.isEmpty(optional.getOpening())){
//                optional.setClosed(optional.getOpening());
//            }
            if (object.has("sell")) {
                optional.setSellone(JSONObjectUtil.getString(object, "sell", "0"));
            } else {
                optional.setSellone("0");
            }
            if (object.has("buy")) {
                optional.setBuyone(JSONObjectUtil.getString(object, "buy", "0"));
            } else {
                optional.setBuyone("0");
            }
//                                    if (object.has("id"))
//                                        optional.setGoodsid(object.getInt("id"));
            //code may be null,if code is null use name
            String code = ConvertUtil.NVL(JSONObjectUtil.getString(object, "code", ""), optional.getTitle());
            optional.setProductCode(code);
            optional.setCustomCode(code);

//            if (object.has("id"))
//                optional.setGoodsid(object.getInt("id"));

            optional.setTreaty(optional.getCustomCode());//if need

            //  没有 just while isbaksource is true for currentMinView---start
            if (object.has("End"))
                optional.setBaksourceEnd(object.getString("End"));
            if (object.has("Start"))
                optional.setBaksourceStart(object.getString("Start"));
            if (object.has("Middle"))
                optional.setBaksourceMiddle(object.getString("Middle"));
            //just while isbaksource is true for currentMinView---end

            if (object.has("instrumentID"))
                optional.setInstrumentID(object.getString("instrumentID"));
            if (object.has("exchangeID"))
                optional.setExchangeID(object.getString("exchangeID"));
            if (object.has("productId"))
                optional.setProductId(object.getString("productId"));
            if (object.has("lastPrice"))
                optional.setLastPrice(object.getString("lastPrice"));
            if (object.has("change"))
                optional.setChange(object.getString("change"));
            if (object.has("chg"))
                optional.setChg(object.getString("chg"));
            if (object.has("name"))
                optional.setName(object.getString("name"));
            if (object.has("chg"))
                optional.setChg(object.getString("chg"));
            if (object.has("askPrice1"))
                optional.setAskPrice1(object.getString("askPrice1"));
            if (object.has("bidPrice1"))
                optional.setBidPrice1(object.getString("bidPrice1"));
            if (object.has("volume"))
                optional.setVolume(object.getString("volume"));
            if (object.has("openInterest"))
                optional.setOpenInterest(object.getString("openInterest"));
            if (object.has("upperLimitPrice"))
                optional.setUpperLimitPrice(object.getString("upperLimitPrice"));
            if (object.has("openPrice"))
                optional.setOpenPrice(object.getString("openPrice"));
            if (object.has("highestPrice"))
                optional.setHighestPrice(object.getString("highestPrice"));
            if (object.has("lowerLimitPrice"))
                optional.setLowerLimitPrice(object.getString("lowerLimitPrice"));
            if (object.has("lowestPrice"))
                optional.setLowestPrice(object.getString("lowestPrice"));
            if (object.has("settlementPrice"))
                optional.setSettlementPrice(object.getString("settlementPrice"));
            if (object.has("preClosePrice"))
                optional.setPreClosePrice(object.getString("preClosePrice"));
            if (object.has("quotationDateTime"))
                optional.setQuotationDateTime(object.getString("quotationDateTime"));
            if (object.has("preSettlementPrice"))
                optional.setPreSettlementPrice(object.getString("preSettlementPrice"));
            if (object.has("averagePrice"))
                optional.setAveragePrice(object.getString("averagePrice"));

            optional.setInitData(true);//标记当前goods已经初始化了
            return optional;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
