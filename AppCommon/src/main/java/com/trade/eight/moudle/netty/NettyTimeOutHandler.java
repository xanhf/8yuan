
package com.trade.eight.moudle.netty;

import android.content.Context;
import android.util.Log;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 设置了读写超时 如果触发就可以在这里处理
 */
public class NettyTimeOutHandler extends ChannelDuplexHandler {
    String TAG = "TimeOutHandler";
    Context context;

    public NettyTimeOutHandler(Context context) {
        this.context = context;
    }

    /**
     * 读写超时了 就检测一次 长链接是否还在连接
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Log.e(TAG, "userEventTriggered==");
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                Log.e(TAG, "userEventTriggered== READER_IDLE");
                NettyClient.getInstance(context).reStart();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                Log.e(TAG, "userEventTriggered== WRITER_IDLE");
                NettyClient.getInstance(context).reStart();
            } else if (e.state() == IdleState.ALL_IDLE) {
                Log.e(TAG, "userEventTriggered== ALL_IDLE");
                //timeout 检测是否重连
                NettyClient.getInstance(context).reStart();

                //重新发一次codes
//                NettyUtil.TYPE_OPT_REWRITE

            }
        }
    }


}
