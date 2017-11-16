package com.trade.eight.moudle.product;

import com.trade.eight.entity.Optional;
import com.trade.eight.entity.response.NettyResponse;

/**
 * Created by fangzhu on 16/10/10.
 * 推送行情
 */
public class OptionalEvent {
    private NettyResponse<Optional> nettyResponse;
    private Optional optional;

    public OptionalEvent(NettyResponse<Optional> nettyResponse) {
        this.nettyResponse = nettyResponse;
    }

    public OptionalEvent(Optional optional) {
        this.optional = optional;
    }

    public Optional getOptional() {
        return optional;
    }

    public void setOptional(Optional optional) {
        this.optional = optional;
    }

    public NettyResponse<Optional> getNettyResponse() {
        return nettyResponse;
    }

    public void setNettyResponse(NettyResponse<Optional> nettyResponse) {
        this.nettyResponse = nettyResponse;
    }
}
