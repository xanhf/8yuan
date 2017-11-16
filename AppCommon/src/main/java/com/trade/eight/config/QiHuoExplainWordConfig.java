package com.trade.eight.config;

import com.trade.eight.entity.QiHuoExplainWordData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/24.
 * 期货名字解释
 */

public class QiHuoExplainWordConfig {


    static String fxlTitle = "风险率：";
    static String fxlContent = "风险率是一种风险控制指标，风险率越低，您的账户就越安全\n计算公式为：风险率=占用保证金÷当前权益×100% (占用保证金是您建仓或持仓时所花费的保证金)";

    static String fxlsmTitle = "风险率说明：";
    static String fxlsmContent = "风险率＜30%，安全\n" +
            "30%-50%，相对安全\n" +
            "50%-70%，危险\n" +
            "70%-100%，极度危险\n" +
            "当风险率处于危险状态时，您需及时补充资金避免被强行平仓";

    static String dqqyTitle = "当前权益：";
    static String dqqyContent = "当前权益是指可用资金及仓单总价值\n" +
            "计算公式为：当前权益=可用资金+占用保证金+持仓盈亏";


    static String kyzjTitle = "可用资金：";
    static String kyzjContent = "可用资金是指可用于新开仓使用的资金\n" +
            "计算公式为：可用资金=当前权益－占用保证金";


    static String ccykTitle = "持仓盈亏：";
    static String ccykContent = "持仓盈亏是指您所持仓单的当日盈亏，它是按照持仓均价和现在平仓时对应的最新价计算的盈亏\n" +
            "计算公式为：持仓盈亏=(最新价-持仓均价)×买涨手数 或 =(持仓均价-最新价)×买跌手数\n";

    static String zccykTitle = "总持仓盈亏：";
    static String zccykContent = "总持仓盈亏是指您所持仓单的当日总盈亏之和";


    static String sjykTitle = "实际盈亏(参考)：";
    static String sjykContent = "实际盈亏是指从建仓以后至当前的平仓盈亏之和\n" +
            "计算公式为：实际盈亏=平历史仓盈亏+平当日仓盈亏";


    static String ccjjTitle = "持仓均价：";
    static String ccjjContent = "持仓均价是上一交易日的结算价，即上一交易日所有交易的加权平均价\n" +
            "若当日建仓，当日交易日结算前：持仓均价=建仓均价\n" +
            "第二日开盘后：持仓均价=上一日结算价";


    static String lastPriceTitle = "最新价：";
    static String lastPriceContent = "最新价是指某交易日某一期货合约交易期间的即时成交价格";


    static String pcykTitle = "平仓盈亏：";
    static String pcykContent = "平仓盈亏是指在了结交易时做出平仓动作成交时候的盈亏\n" +
            "计算公式为：\n" +
            "①当日平仓：平仓盈亏= (快速平仓价-建仓价) ×平仓数量×合约乘量\n" +
            "②隔日平仓：平仓盈亏= (快速平仓价-持仓均价) ×平仓数量×合约乘量\n";


    static String closePriceTitle = "平仓价：";
    static String closePriceContent = "平仓价是指在了结交易（卖出原来买入的合约或买入原来卖出的合约）时的成交价格";


    static String buyPriceTitle = "买价：";
    static String buyPriceContent = "买价为当前买方委托买入但未成交的即时最高申报价格";


    static String sellPriceTitle = "卖价：";
    static String sellPriceContent = "卖价为当前卖方委托卖出但未成交的即时最低申报价格";


    static String cjlTitle = "成交量：";
    static String cjlContent = "成交量是指某一期货合约成交的双边累计数量，单位为“手”";


    static String cclTitle = "持仓量：";
    static String cclContent = "持仓量也称空盘量或未平仓合约量，是指期货交易者所持有的未平仓合约的双边累计数量";


    static String jgrqTitle = "交割日期：";
    static String jgrqContent = "交割日期是指合约到期时，按照期货交易所的规则和程序，交易双方通过该合约所载标的物所有权的转移，或者按照规定结算价格进行现金差价结算，了结到期未平仓合约的过程\n" +
            "交割日期前的最后平仓时间为：交割所在月份的上个月最后一天15:00前。\n" +
            "例如：合约“螺纹钢1707 买跌10手”；交割日期为5-15，请于4-30 15:00前平仓";


    static String jcsxfTitle = "建仓手续费：";
    static String jcsxfContent = "计算公式为：建仓手续费=建仓价×手数×合约单位×开仓手续费比例";


    static String pcsxfTitle = "平仓手续费：";
    static String pcsxfContent = "计算公式为：平仓手续费=平仓价×手数×合约单位×平仓手续费比例";


    static String sxfTitle = "手续费：";
    static String sxfContent = "计算公式为：手续费=建仓手续费+平仓手续费";


    static String dzsxfTitle = "冻结手续费：";
    static String dzsxfContent = "冻结手续费是指建仓未成功时，被冻结的建仓手续费，冻结资金不可用，若建仓失败，冻结手续费将返回至您的可用资金里，若建仓成功，冻结手续费会将为建仓手续费\n";


    static String bzjTitle = "保证金：";
    static String bzjContent = "保证金=占用保证金\n" +
            "计算公式为：建仓价×手数×合约单位×保证金比例\n" +
            "例如：螺纹钢 买涨 2手，建仓价是3820，合约单位是10吨/手，保证金比例是5%。\n" +
            "即：保证金=3820×2×10×5%\n";


    static String kqbzjTitle = "可取保证金：";
    static String kqbzjContent = "可取保证金是指可以通过银期转账从期货交易账户转至银行账户的保证金";


    static String dzbzjTitle = "冻结保证金：";
    static String dzbzjContent = "冻结保证金是指建仓未成功时，被冻结的保证金，冻结资金不可用，若建仓失败，冻结保证金将返回至您的可用资金里，若建仓成功，冻结保证金将转为占用保证金";

    static String drbzjTitle = "当日保证金：";
    static String drbzjContent = "当日保证金的计算公式：持仓均价×手数×合约单位×保证金比例";


    static String gfqhTitle = "国富期货：";
    static String gfqhContent = "国富期货有限公司是商品期货经纪、金融期货经纪，资产管理，期货投资咨询平台\n" +
            "账户：为您的开户短信中的8位数字账户\n" +
            "交易密码：开户过程中设置的6位数数字证书密码或您修改后的6-16位交易密码\n" +
            "如果忘记密码请联系国富期货大连分公司 0411-88853019 \n";


    static String protoclTitle = "合约：";
    static String protoclContent = "合约名称由期货品种名称+合约到期月份组成\n" +
            "例如：“螺纹钢1707”是指“螺纹钢+2017年07月”";

    static String kspcjTitle = "快速平仓价：";
    static String kspcjContent = "快速平仓价是指在了结交易（卖出原来买入的合约或买入原来卖出的合约）时，按照市场当前走势快速平仓的价格";


    public static final String FXL = "fxl";
    public static final String FXLSM = "fxlsm";
    public static final String DQQY = "dqqy";
    public static final String KYZJ = "kyzj";
    public static final String CCYK = "ccyk";
    public static final String ZCCYK = "ZCCYK";
    public static final String SJYK = "sjyk";
    public static final String CCJJ = "ccjj";
    public static final String LASTPRICE = "lastPrice";
    public static final String PCYK = "pcyk";
    public static final String CLOSEPRICE = "closePrice";
    public static final String BUYPRICE = "buyPrice";
    public static final String SELLPRICE = "sellPrice";
    public static final String CJL = "cjl";
    public static final String CCL = "ccl";
    public static final String JGRQ = "jgrq";
    public static final String JCSXF = "jcsxf";
    public static final String PCSXF = "pcsxf";
    public static final String SXF = "sxf";
    public static final String DZSXF = "dzsxf";
    public static final String BZJ = "bzj";
    public static final String KQBZJ = "kqbzj";
    public static final String DZBZJ = "dzbzj";
    public static final String DRBZJ = "drbzj";
    public static final String GFQH = "gfqh";
    public static final String PROTOCL = "protocl";
    public static final String KSPCJ = "kspcj";

    // 期货名词解释
    public static HashMap<String, QiHuoExplainWordData> explainWordMap = new HashMap<String, QiHuoExplainWordData>();

    static {
        explainWordMap.put(FXL, new QiHuoExplainWordData(fxlTitle, fxlContent));
        explainWordMap.put(FXLSM, new QiHuoExplainWordData(fxlsmTitle, fxlsmContent));
        explainWordMap.put(DQQY, new QiHuoExplainWordData(dqqyTitle, dqqyContent));
        explainWordMap.put(KYZJ, new QiHuoExplainWordData(kyzjTitle, kyzjContent));
        explainWordMap.put(CCYK, new QiHuoExplainWordData(ccykTitle, ccykContent));
        explainWordMap.put(ZCCYK, new QiHuoExplainWordData(zccykTitle, zccykContent));
        explainWordMap.put(SJYK, new QiHuoExplainWordData(sjykTitle, sjykContent));
        explainWordMap.put(CCJJ, new QiHuoExplainWordData(ccjjTitle, ccjjContent));
        explainWordMap.put(LASTPRICE, new QiHuoExplainWordData(lastPriceTitle, lastPriceContent));
        explainWordMap.put(PCYK, new QiHuoExplainWordData(pcykTitle, pcykContent));
        explainWordMap.put(CLOSEPRICE, new QiHuoExplainWordData(closePriceTitle, closePriceContent));
        explainWordMap.put(BUYPRICE, new QiHuoExplainWordData(buyPriceTitle, buyPriceContent));
        explainWordMap.put(SELLPRICE, new QiHuoExplainWordData(sellPriceTitle, sellPriceContent));
        explainWordMap.put(CJL, new QiHuoExplainWordData(cjlTitle, cjlContent));
        explainWordMap.put(CCL, new QiHuoExplainWordData(cclTitle, cclContent));
        explainWordMap.put(JGRQ, new QiHuoExplainWordData(jgrqTitle, jgrqContent));
        explainWordMap.put(JCSXF, new QiHuoExplainWordData(jcsxfTitle, jcsxfContent));
        explainWordMap.put(PCSXF, new QiHuoExplainWordData(pcsxfTitle, pcsxfContent));
        explainWordMap.put(SXF, new QiHuoExplainWordData(sxfTitle, sxfContent));
        explainWordMap.put(DZSXF, new QiHuoExplainWordData(dzsxfTitle, dzsxfContent));
        explainWordMap.put(BZJ, new QiHuoExplainWordData(bzjTitle, bzjContent));
        explainWordMap.put(KQBZJ, new QiHuoExplainWordData(kqbzjTitle, kqbzjContent));
        explainWordMap.put(DZBZJ, new QiHuoExplainWordData(dzbzjTitle, dzbzjContent));
        explainWordMap.put(DRBZJ, new QiHuoExplainWordData(drbzjTitle, drbzjContent));
        explainWordMap.put(GFQH, new QiHuoExplainWordData(gfqhTitle, gfqhContent));
        explainWordMap.put(PROTOCL, new QiHuoExplainWordData(protoclTitle, protoclContent));
        explainWordMap.put(KSPCJ, new QiHuoExplainWordData(kspcjTitle, kspcjContent));
    }


    /**
     * 根据传进来的key  获取返回的list
     *
     * @param args
     * @return
     */
    public static List<QiHuoExplainWordData> getDisplayList(String... args) {
        List<QiHuoExplainWordData> displayList = new ArrayList<QiHuoExplainWordData>();
        for (String key : args) {
            if (explainWordMap.containsKey(key)) {
                displayList.add(explainWordMap.get(key));
            }
        }
        return displayList;
    }


}
