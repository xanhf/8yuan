package com.trade.eight.moudle.netty;


import android.content.Context;

import com.trade.eight.dao.OptionalDao;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.response.NettyResponse;
import com.trade.eight.moudle.baksource.BakSourceService;
import com.trade.eight.moudle.product.OptionalEvent;
import com.trade.eight.service.JSONObjectUtil;
import com.trade.eight.tools.Log;
import com.trade.eight.tools.NetworkUtil;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * 1、第一次连接之后验证是否合法
 * 2、接收行情推送
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {
    String TAG = "NettyClientHandler";
    Context context;

    public NettyClientHandler(Context context) {
        this.context = context;
    }

    /**
     * 和服务端断开
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Log.e(TAG, "channelInactive");
        //这个会重复检测
//        //检测是否重连
//        if (NetworkUtil.checkNetwork(context)) {
//            NettyClient.getInstance(context).reStart();
//        }
    }

    /**
     * 接收推送的数据
     *
     * @param channelHandlerContext
     * @param map                   1、{"success":true,"type":"connect"}
     *                              2、{"success":true,"type":"rtc"}
     *                              3、{"errorCode":"00003","errorInfo":"服务器验证失败！","success":false,"type":"connect"}
     *                              4、{"data":{"buy":"2546.8","code":"OIL","excode":"HPME","id":"3",
     *                              "isClosed":"1","last_close":"2540.6","low":"2519.6","margin":"6.2",
     *                              "mp":"0.24%","name":"哈贵油","open":"2521.3","sell":"2546.8",
     *                              "time":"2016-10-10 16:16:30","top":"2552.7","updatetime":"1476087390000"},"success":true,"type":"qp"}
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String map) throws Exception {
        Log.e(TAG, "channelRead0 push str=" + map);
        if (map == null)
            return;
        NettyResponse<Optional> response = parseRes(map);
        if (response == null)
            return;
        if (!response.isSuccess()) {
//            00003
            return;
        }


        if (NettyUtil.TYPE_CONNECT.equals(response.getType())) {
            //验证通过
            EventBus.getDefault().post(new OptionalEvent(response));

        } else if (NettyUtil.TYPE_RTC.equals(response.getType())) {
            //发送行情codes成功
        } else if (NettyUtil.TYPE_QP.equals(response.getType())) {
            //收到行情
            EventBus.getDefault().post(new OptionalEvent(response));

        }


    }


    /**
     * 建立连接成功
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.e(TAG, "channelActive");
        //验证连接
        NettyClient.getInstance(context).validateConnect();
    }

    /**
     * 网络中断会触发
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Log.e(TAG, "exceptionCaught");
//        cause.printStackTrace();
        //如果是有网络的情况下 停止了长链接，检测重连
        if (NetworkUtil.checkNetwork(context)) {
            NettyClient.getInstance(context).reStart();
        }
    }


    /**
     * 这里每次都是存本地再取出
     *
     * @param str
     * @return
     */
    NettyResponse<Optional> parseRes(String str) {
        try {
            NettyResponse<Optional> response = new NettyResponse<Optional>();
            JSONObject jsonObject = new JSONObject(str);
            response.setSuccess(JSONObjectUtil.getBoolean(jsonObject, "success", false));
            response.setType(JSONObjectUtil.getString(jsonObject, "type", ""));
            if (jsonObject.has("data")) {
                JSONObject data = jsonObject.getJSONObject("data");
                Optional optional = BakSourceService.parseWeipan(data);
                response.setData(optional);

                OptionalDao dao = new OptionalDao(context);
                dao.addOrUpdateOptional(optional);
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
