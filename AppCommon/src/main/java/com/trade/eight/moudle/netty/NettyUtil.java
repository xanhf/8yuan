package com.trade.eight.moudle.netty;

import com.trade.eight.entity.Optional;
import com.trade.eight.moudle.baksource.BakSourceInterface;
import com.trade.eight.tools.AESUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.MyBase64;
import com.trade.eight.tools.StringUtil;

import org.json.JSONObject;

import java.util.List;

import io.netty.channel.Channel;

/**
 * Created by fangzhu on 16/10/9.
 * 方法
 * 1、验证连接
 * 2、发送需要推送的行情
 */
public class NettyUtil {
    public static final String TAG = "NettyUtil";
    public static final String P_TYPE = "type";
    /*连接上长链接*/
    public static final String TYPE_CONNECT = "connect";
    /*长链接发送codes*/
    public static final String TYPE_RTC = "rtc";
    /*长链接接收消息*/
    public static final String TYPE_QP = "qp";
    /*tcp停止推送的字符串，发送空字符串就是停止行情推送*/
    public static final String CODES_STOP_QP = "";


    /*自定义type，长链接重新发送codes*/
    public static final String TYPE_OPT_REWRITE = "rewrite";

    /**
     * 连接成功后使用,通过验证
     *
     * @param channel
     * @param enCodeK 加密的key 先base64解密，然后aes解密
     *                <p>
     *                {"success":true,"type":"connect"}
     */
    public static void validate(Channel channel, String enCodeK) {
        try {
            if (channel == null)
                return;
            if (StringUtil.isEmpty(enCodeK))
                return;
            JSONObject json = new JSONObject();
            json.put(P_TYPE, TYPE_CONNECT);
            long l = System.currentTimeMillis();
            String k = enCodeK;
            //先使用base64解密，再用aes解密
            byte[] bs = MyBase64.decode(k);
            k = new String(bs, "utf-8");
            k = AESUtil.decrypt(k);
            String auth = StringUtil.md5(TYPE_CONNECT + "_" + l + "_" + k);
            JSONObject sjson = new JSONObject();
            sjson.put("t", l);
            sjson.put("auth", auth);
            json.put("p", sjson);
            String requestStr = json.toString() + "$_";
            Log.v(TAG, requestStr);
            channel.writeAndFlush(requestStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 停止行情推送
     * 不断开连接，仅仅不推送
     * @param channel
     */
    public static void stopWrite(Channel channel) {
        write(channel, CODES_STOP_QP);
    }
    /**
     * 发送品种的行情
     *
     * @param channel
     * @param codes   String codes="HPME|OIL,HPME|XAG1";
     *                "" 空字符 就是停止推送行情
     */
    public static void write(Channel channel, String codes) {
        //        {"p":{"codes":"HPME|OIL"},"type":"rtc"}$_
        try {
            if (channel == null)
                return;
            if (!channel.isActive()) {
                Log.v(TAG, "channel is not active");
                return;
            }

            JSONObject json = new JSONObject();
            json.put(P_TYPE, TYPE_RTC);
            JSONObject sjson = new JSONObject();
            sjson.put("codes", codes);
            json.put("p", sjson);
            String requestStr = json.toString() + "$_";
            Log.v(TAG, requestStr);
            channel.writeAndFlush(requestStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取可以推送的行情
     * 过滤掉不能长链接的source
     *
     * @param list
     * @return
     */
    public static String getCodesSpecial(List<Optional> list) {
        if (list == null)
            return null;
        StringBuffer specialStringBuffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            Optional o = list.get(i);
            String source = o.getExchangeID();
            if (StringUtil.isEmpty(source))
                continue;
            if (BakSourceInterface.specialSource.contains(source)) {
                specialStringBuffer.append(o.getExchangeID() + "|" + o.getInstrumentID() + ",");
            }
        }
        String str = specialStringBuffer.toString();
        if (str.endsWith(",")) {
            str = str.substring(0, str.lastIndexOf(","));
        }
        return str;
    }

    /**
     * @param source
     * @param code
     * @return 发送的codes
     */
    public static String getCodesSpecia (String source, String code) {
        if (BakSourceInterface.specialSource.contains(source)) {
            return source + "|" + code;
        }
        return CODES_STOP_QP;
    }



}
