package com.trade.eight.moudle.baksource;

import com.trade.eight.config.AndroidAPIConfig;
import com.trade.eight.entity.KlineCycle;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fangzhu on 2015/4/17.
 */
public class BakSourceInterface {
    //http必须需要的参数  {time}&token={token}&key={key}
    public static final String PARAM_TIMNE = "time";
    public static final String PARAM_TOKEN = "token";
    public static final String PARAM_KEY = "key";
    public static final String PARAM_TOKEN_VALUE = "310242a4068f1cad5096aec31a774f6c";
    public static final String PARAM_CUSTOM = "custom";

    /*k线图参数对应
            日线=day
    周线=week
    5分钟=min5
    15分钟=min15
    30分钟=min30
    60分钟=min60
    2小时=hr2
    4小时=hr4*/
    public static final String PARAM_KLINE_TIME = "min1";//分时
    public static final String PARAM_KLINE_5M = "min5";
    public static final String PARAM_KLINE_15M = "min15";
    public static final String PARAM_KLINE_30M = "min30";
    public static final String PARAM_KLINE_60M = "min60";
    public static final String PARAM_KLINE_2H = "hr2";
    public static final String PARAM_KLINE_4H = "hr4";
    public static final String PARAM_KLINE_WEEK = "week";
    public static final String PARAM_KLINE_1D = "day";
    public static final String PARAM_KLINE_MONTH = "month";


    /*

    type=1  1分钟图
    =2  5
            =3  15
            =4  30
            =5  1小时
    =9  4小时
    =6   一天
    =7  周线
    =8   月线*/
    public static final String PARAM_KLINE_TIME_WEIPAN = "1";//分时
    public static final String PARAM_KLINE_1M_WEIPAN = "10";//一分钟
    public static final String PARAM_KLINE_5M_WEIPAN = "2";
    public static final String PARAM_KLINE_15M_WEIPAN = "3";
    public static final String PARAM_KLINE_30M_WEIPAN = "4";
    public static final String PARAM_KLINE_60M_WEIPAN = "5";
    //    public static final String PARAM_KLINE_2H_WEIPAN = "";
    public static final String PARAM_KLINE_4H_WEIPAN = "9";
    public static final String PARAM_KLINE_WEEK_WEIPAN = "7";
    public static final String PARAM_KLINE_1D_WEIPAN = "6";
//    public static final String PARAM_KLINE_MONTH_WEIPAN = "8";

    public static List<KlineCycle> klineCycleList = new ArrayList<KlineCycle>();

    static {
        klineCycleList.add(new KlineCycle("分时", PARAM_KLINE_TIME));
        klineCycleList.add(new KlineCycle("5分钟", PARAM_KLINE_5M));
        klineCycleList.add(new KlineCycle("15分钟", PARAM_KLINE_15M));
        klineCycleList.add(new KlineCycle("30分钟", PARAM_KLINE_30M));
        klineCycleList.add(new KlineCycle("1小时", PARAM_KLINE_60M));
        klineCycleList.add(new KlineCycle("2小时", PARAM_KLINE_2H));
        klineCycleList.add(new KlineCycle("4小时", PARAM_KLINE_4H));
        klineCycleList.add(new KlineCycle("日线", PARAM_KLINE_1D));
        klineCycleList.add(new KlineCycle("周线", PARAM_KLINE_WEEK));
//        klineCycleList.add(new KlineCycle("月线", PARAM_KLINE_MONTH));
    }

    /*的周期*/
    public static List<KlineCycle> specialklineCycleList = new ArrayList<KlineCycle>();

    static {
        specialklineCycleList.add(new KlineCycle("分时", PARAM_KLINE_TIME_WEIPAN));
        specialklineCycleList.add(new KlineCycle("1分钟", PARAM_KLINE_1M_WEIPAN));
        specialklineCycleList.add(new KlineCycle("5分钟", PARAM_KLINE_5M_WEIPAN));
        specialklineCycleList.add(new KlineCycle("15分钟", PARAM_KLINE_15M_WEIPAN));
        specialklineCycleList.add(new KlineCycle("30分钟", PARAM_KLINE_30M_WEIPAN));
        specialklineCycleList.add(new KlineCycle("1小时", PARAM_KLINE_60M_WEIPAN));
//        specialklineCycleList.add(new KlineCycle("2小时", PARAM_KLINE_2H_WEIPAN));
        specialklineCycleList.add(new KlineCycle("4小时", PARAM_KLINE_4H_WEIPAN));
        specialklineCycleList.add(new KlineCycle("日线", PARAM_KLINE_1D_WEIPAN));
        specialklineCycleList.add(new KlineCycle("周线", PARAM_KLINE_WEEK_WEIPAN));
//        klineCycleList.add(new KlineCycle("月线", PARAM_KLINE_MONTH)_WEIPAN);
    }
//    public static final String PARAM_EXCODE = "excode";

    /*对应周期的时间间隔*/
    public static Map<String, Long> cycleTMap = new HashMap<>();

    static {
        cycleTMap.put(PARAM_KLINE_TIME_WEIPAN, 1 * 60 * 1000L);
        cycleTMap.put(PARAM_KLINE_1M_WEIPAN, 1 * 60 * 1000L);
        cycleTMap.put(PARAM_KLINE_5M_WEIPAN, 5 * 60 * 1000L);
        cycleTMap.put(PARAM_KLINE_15M_WEIPAN, 15 * 60 * 1000L);
        cycleTMap.put(PARAM_KLINE_30M_WEIPAN, 30 * 60 * 1000L);
        cycleTMap.put(PARAM_KLINE_60M_WEIPAN, 60 * 60 * 1000L);
        cycleTMap.put(PARAM_KLINE_4H_WEIPAN, 4 * 60 * 60 * 1000L);
        cycleTMap.put(PARAM_KLINE_WEEK_WEIPAN, 7 * 24 * 60 * 1000L);
        cycleTMap.put(PARAM_KLINE_1D_WEIPAN, 24 * 60 * 60 * 1000L);
    }

    //交易所下所有品种的最新数据
    //http://htmmarket.fx678.com/list.php?excode=CCDPT&time=1429076077&token=310242a4068f1cad5096aec31a774f6c&key=ed284ef243ee3c6c02f85875842cf21f
    public static String URL_SOURCE_ALLPRODUCT = "http://htmmarket.fx678.com/list.php?excode={source}&time={time}&token={token}&key={key}";

    //品种的数据查询
    //http://htmmarket.fx678.com/custom.php?excode=custom&code=WGJS|XAP,WH|USDHKD,WH|EURJPY,WH|USDCAD,NYMEX|CONC&time=1429155426&token=310242a4068f1cad5096aec31a774f6c&key=4955f666347a6521ff0274dfc9bacc32
    public static String URL_GET_PRODUCTS = "http://htmmarket.fx678.com/custom.php?excode=custom&code={code}&time={time}&token={token}&key={key}";


    //k线图
    //http://htmmarket.fx678.com/kline.php?excode=SZPEC&code=QHO10S&type=min60&time=1429079043&token=310242a4068f1cad5096aec31a774f6c&key=f76a5fd395fae769151101d74fca7202
    public static String URL_GET_KLINE = "http://htmmarket.fx678.com/kline.php?excode={source}&code={code}&type={type}&time={time}&token={token}&key={key}";

    //K线分时线
//    http://htmmarket.fx678.com/time.php?excode=WGJS&code=XAU&time=1429273503&token=310242a4068f1cad5096aec31a774f6c&key=4984f18ca3e42ea1fdea4b53ff309299
    public static String URL_GET_KLINE_TIME = "http://htmmarket.fx678.com/time.php?excode={source}&code={code}&type={type}&time={time}&token={token}&key={key}";

    public static String TRUDE_SOURCE_WEIPAN = "weiPan";//广贵 weiPan
    public static String TRUDE_SOURCE_WEIPANP_HG = "HPME";//哈贵
    public static String TRUDE_SOURCE_WEIPANP_JN = "JCCE";//农交所
    public static String TRUDE_SOURCE_WEIPANP_HN = "HNCE";//华凝所
    public static String TRUDE_SOURCE_WEIPANP_FXBTG = "FXBTG";
    public static String TRUDE_SOURCE_QIHUO_SHFE = "SHFE";//上海期货交易所
    public static String TRUDE_SOURCE_QIHUO_DCE = "DCE";//大连商品交易所
    public static String TRUDE_SOURCE_QIHUO_CZCE = "CZCE";//郑州
    public static String TRUDE_SOURCE_QIHUO_CFFE = "CFFE";//中金所


    //非http://htmmarket.fx678.com 域名的就当作是特殊交易所数据处理  这里就当成是的
    public static List<String> specialSource = new ArrayList<String>();

    static {
        specialSource.add(TRUDE_SOURCE_WEIPAN);
        specialSource.add(TRUDE_SOURCE_WEIPANP_HG);
        specialSource.add(TRUDE_SOURCE_WEIPANP_JN);
        specialSource.add(TRUDE_SOURCE_WEIPANP_HN);
        specialSource.add(TRUDE_SOURCE_WEIPANP_FXBTG);
        specialSource.add(TRUDE_SOURCE_QIHUO_SHFE);
        specialSource.add(TRUDE_SOURCE_QIHUO_DCE);
        specialSource.add(TRUDE_SOURCE_QIHUO_CZCE);
        specialSource.add(TRUDE_SOURCE_QIHUO_CFFE);

    }

    //接口
    //交易所下所有品种的最新数据  excode
    public static String URL_SOURCE_ALLPRODUCT_WEIPAN = AndroidAPIConfig.HOST_WEIPAN_MARKET + "/wp/quotation/v2/realTime/excode/list?excode={excode}";//&time={time}&token={token}&key={key}
    //品种的数据查询
//    public static String URL_GET_PRODUCTS_WEIPAN = AndroidAPIConfig.HOST_WEIPAN_MARKET + "/wp/quotation/v2/realTime?codes={codes}";
    public static String URL_GET_PRODUCTS_WEIPAN = AndroidAPIConfig.HOST_WEIPAN_MARKET + "/quotation/realTime?codes={codes}";

    //k线图
    public static String URL_GET_KLINE_WEIPAN = AndroidAPIConfig.HOST_WEIPAN_MARKET + "/quotation/kChart?excode={excode}&code={code}&type={type}";//type=1
    //K线分时线
    public static String URL_GET_KLINE_TIME_WEIPAN = AndroidAPIConfig.HOST_WEIPAN_MARKET + "/quotation/tickChart?excode={excode}&code={code}";


    /* * 品类
             国际黄金=WGJS	外汇=WH	全球股指=GJZS	LME金属=LME	布伦特原油=IPE	美国原油=NYMEX	    上海黄金=SGE	上海期货=SHFE	津贵所=TJPME	广贵所=PMEC	深石油=SZPEC

     */
    //    外汇=WH  津贵所=TJPME	深石油=SZPEC

    public static String TRUDE_SOURCE_INIT_APP = TRUDE_SOURCE_WEIPANP_HG;

    public static String TRUDE_SOURCE_WH = "WH";//外汇
    public static String TRUDE_SOURCE_SZPEC = "SZPEC";//深石油
    public static String TRUDE_SOURCE_TJPME = "TJPME";//津贵所
    public static String TRUDE_SOURCE_IPE = "IPE";//布伦特原油
    public static String TRUDE_SOURCE_WGJS = "WGJS";//国际黄金
    public static String TRUDE_SOURCE_NYMEX = "NYMEX";//美国原油


//    public static List<Goods> goodsList = new ArrayList<Goods>();
//
//    static {
//        goodsList.add(new Goods(TRUDE_SOURCE_WEIPANP_HG, "哈贵所"));
//        goodsList.add(new Goods(TRUDE_SOURCE_WEIPANP_JN, "农交所"));
//        goodsList.add(new Goods(TRUDE_SOURCE_WEIPAN, "广贵所"));
//        goodsList.add(new Goods("WGJS", "国际黄金"));
//        goodsList.add(new Goods("WH", "外汇"));
//        goodsList.add(new Goods("GJZS", "全球股指"));
//        goodsList.add(new Goods("LME", "LME金属"));
////        goodsList.add(new Goods("IPE", "布伦特原油"));
//        goodsList.add(new Goods("NYMEX", "美国原油"));
////        goodsList.add(new Goods("SGE", "上海黄金"));
//        goodsList.add(new Goods("SHFE", "上海期货"));
//        goodsList.add(new Goods("TJPME", "津贵所"));
//        goodsList.add(new Goods("SZPEC", "深石油"));
//        goodsList.add(new Goods("PMEC", "广贵所"));
//    }


    //链接加密算法
    public static void main(String[] args) {
        long unixTime = 1429672922000L;
        String excode = "SHFE";
        System.out.println(hashParams(excode + getUnixTimeParam(unixTimeExceptMilisecond(unixTime)) + PARAM_TOKEN_VALUE + "htm_key_market_2099"));
    }


    public static String getKey(String code) {
        try {
            return hashParams(code + getUnixTimeParam(unixTimeExceptMilisecond(System.currentTimeMillis())) + PARAM_TOKEN_VALUE + "htm_key_market_2099");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PARAM_TOKEN_VALUE;//Exception value
    }

    /**
     * unix 时间 去除毫秒
     *
     * @param unixTime
     * @return
     */
    public static long unixTimeExceptMilisecond(long unixTime) {
        return unixTime / 1000L;
    }

    public static String getUnixTimeParam(long time) {
        String timeStr = String.valueOf(time);
        int length = timeStr.length();
        if (length < 6) {
            return null;
        }
        return timeStr.substring(length - 6, length);
    }

    /**
     * 计算key
     *
     * @param paramString
     * @return
     */
    public static String hashParams(String paramString) {
        StringBuilder localStringBuilder;
        try {
            byte[] arrayOfByte = MessageDigest.getInstance("MD5").digest(paramString.getBytes("UTF-8"));
            localStringBuilder = new StringBuilder(2 * arrayOfByte.length);
            int i = arrayOfByte.length;
            for (int j = 0; j < i; j++) {
                int k = arrayOfByte[j];
                if ((k & 0xFF) < 16)
                    localStringBuilder.append("0");
                localStringBuilder.append(Integer.toHexString(k & 0xFF));
            }
        } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
            throw new RuntimeException("Huh, MD5 should be supported?", localNoSuchAlgorithmException);
        } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", localUnsupportedEncodingException);
        }
        return localStringBuilder.toString();
    }

    /**
     * {time}&token={token}&key={key}
     *
     * @param url
     * @return
     */
    public static String setCommonParam4get(String url, String code) {
        url = url.replace("{time}", System.currentTimeMillis() / 1000 + "");
        url = url.replace("{token}", PARAM_TOKEN_VALUE);
        url = url.replace("{key}", getKey(code));
        return url;
    }

    public static String setParam4get(String api, Map<String, String> paraMap) {
        StringBuffer sb = new StringBuffer(api);
        int i = 0;
        if (paraMap != null && !paraMap.isEmpty()) {
            for (Map.Entry<String, String> entry : paraMap.entrySet()) {
                String key = entry.getKey().toString();
                String value = "";
                try {
                    value = entry.getValue().toString();
                } catch (Exception e) {
                    // TODO: handle exception
                }
                if (api.contains("?")) {
                    sb.append("&");
                } else {
                    if (i == 0)
                        sb.append("?");
                    else
                        sb.append("&");
                }

                sb.append(key + "=" + value);
                i++;
            }
        }
        return sb.toString();
    }
}
