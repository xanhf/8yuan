package com.trade.eight.moudle.netty;

import android.content.Context;

import com.trade.eight.config.AppStartUpConfig;
import com.trade.eight.entity.Optional;
import com.trade.eight.entity.startup.StartupConfigObj;
import com.trade.eight.entity.startup.StartupObj;
import com.trade.eight.tools.Log;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 启动netty
 */
public class NettyClient {
    String TAG = "NettyClient";
    private static NettyClient ourInstance;
    Context context;
    /**
     * 配置NIO线程组
     * 注意这里每次重新连接的时候
     * 必须是单实例的，重连的时候，不能每次都重复new
     * 当前NettyClient是singleInstance的，所以eventLoopGroup也是singInstance
     */
    EventLoopGroup eventLoopGroup = new NioEventLoopGroup();


    public static NettyClient getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new NettyClient(context);
        return ourInstance;
    }

    private NettyClient(Context context) {
        this.context = context;
    }

    /*ip端口的配置信息*/
    private StartupObj startupObj;
    /*是否已经获取到配置信息*/
    private boolean isInited = false;
    /*SocketChannel对象*/
    private SocketChannel socketChannel;


    /**
     * 关闭连接
     */
    public void close() {
        try {
            Log.v(TAG, "close");
            if (socketChannel != null
                    && socketChannel.isActive()) {
                socketChannel.close();
            }
            startupObj = null;
            isInited = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    /**
     * 第一次建立连接
     */
    public void validateConnect() {
        if (socketChannel == null)
            return;
        NettyUtil.validate(socketChannel, startupObj.getK());
    }

    /**
     * 停止行情推送
     * 不断开连接，仅仅不推送
     */
    public void stopWrite() {
        if (socketChannel == null)
            return;
        NettyUtil.stopWrite(socketChannel);
    }

    /**
     * 发送需要推送的行情
     *
     * @param codes String codes="HPME|OIL,HPME|XAG1"; 多个,分隔
     */
    public void write(String codes) {
        if (socketChannel == null || !socketChannel.isActive()) {
            reStart();
            return;
        }

        NettyUtil.write(socketChannel, codes);
    }

    /**
     * 发送需要推送的行情
     *
     * @param o
     */
    public void write(Optional o) {
        List<Optional> list = new ArrayList<>();
        list.add(o);
        write(NettyUtil.getCodesSpecial(list));
    }

    /**
     * 发送需要推送的行情
     *
     * @param list
     */
    public void write(List<Optional> list) {
        if (list == null)
            return;
        write(NettyUtil.getCodesSpecial(list));
    }

    /**
     * 初始化配置信息
     */
    public void init() {
        StartupConfigObj obj = AppStartUpConfig.getInstance(context).getStartupConfigObj();
        if (obj == null)
            return;
        if (obj.getConfig() == null)
            return;
        startupObj = obj.getConfig();
        isInited = true;

        start();
    }

    /**
     * 重新连接
     */
    public void reStart() {
        if (!NettyClient.getInstance(context).isInited()) {
//            NettyClient.getInstance(context).init();
            //还没有获取到接口数据
            AppStartUpConfig.getInstance(context).init();
        } else {
            //长连接已经断开，重新连接
            if (socketChannel == null || !socketChannel.isActive()) {
                NettyClient.getInstance(context).start();
            }
        }
    }

    /**
     * 开启连接
     */
    public void start() {
        if (startupObj == null) {
            return;
        }
        /*连接是耗时的操作 需要thread启动*/
        new Thread() {
            @Override
            public void run() {
                connect ();
            }
        }.start();
    }

    /**
     * 连接tcp
     */
    private synchronized void connect () {
        try {
            if (socketChannel != null && socketChannel.isActive()) {
                return;
            }
            Log.v(TAG, "netty connect-----");
            Bootstrap bootstrap = new Bootstrap();
            //设置NioSocketChannel
            bootstrap.channel(NioSocketChannel.class);
            //设置心跳机制
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            //设置eventLoopGroup
            bootstrap.group(eventLoopGroup);
            //设置连接ip  端口
            bootstrap.remoteAddress(startupObj.getIp(), startupObj.getPort());
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(2048, Unpooled.copiedBuffer("$_".getBytes())));
                    socketChannel.pipeline().addLast(new StringEncoder(Charset.forName("utf-8")));
                    socketChannel.pipeline().addLast(new StringDecoder(Charset.forName("utf-8")));
                    socketChannel.pipeline().addLast("idleStateHandler", new IdleStateHandler(0, 0, 60));
                    socketChannel.pipeline().addLast("timeOutHandler", new NettyTimeOutHandler(context));
                    socketChannel.pipeline().addLast(new NettyClientHandler(context));

                }
            });
            //连接服务端
            ChannelFuture future = bootstrap.connect().sync();
            if (future.isSuccess()) {
                socketChannel = (SocketChannel) future.channel();
                Log.e(TAG, "连接成功－－－－");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "连接Exception－－－－");
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            Log.e(TAG, "OutOfMemoryError－－－－");
        }
    }

    /**
     * 这里的重新连接没有用
     * 不用这个
     */
//    ChannelFutureListener channelFutureListener = new ChannelFutureListener() {
//        public void operationComplete(ChannelFuture f) throws Exception {
//            if (f.isSuccess()) {
//                Log.e(TAG, "重新连接服务器成功");
//
//            } else {
//                Log.e(TAG, "重新连接服务器失败");
//                //  3秒后重新连接
//                f.channel().eventLoop().schedule(new Runnable() {
//                    @Override
//                    public void run() {
//                        start();
//                    }
//                }, 3, TimeUnit.SECONDS);
//            }
//        }
//    };

    public boolean isInited() {
        return isInited;
    }

    public void setInited(boolean inited) {
        isInited = inited;
    }

    public StartupObj getStartupObj() {
        return startupObj;
    }

    public void setStartupObj(StartupObj startupObj) {
        this.startupObj = startupObj;
    }

}
