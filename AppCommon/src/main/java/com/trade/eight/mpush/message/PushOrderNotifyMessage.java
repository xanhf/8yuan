package com.trade.eight.mpush.message;

import com.trade.eight.mpush.api.connection.Connection;
import com.trade.eight.mpush.api.protocol.Command;
import com.trade.eight.mpush.api.protocol.Packet;
import com.trade.eight.mpush.util.ByteBuf;

import java.nio.ByteBuffer;

/**
 * Created by yu on 2017/7/11.
 */
public class PushOrderNotifyMessage extends ByteBufMessage {

    /**
     * 买跌，买涨
     */
    private int type;
    /**
     * 建仓平仓
     */
    private int orderType;
    /**
     * 合约代码
     */
    private String instrumentId;
    /**
     * 合约名称
     */
    private String instrumentName;
    /**
     * 1、全部成功，2、部分成功，3：全部失败
     */
    private int status;
    /**
     * 全部数量
     */
    private int allCount;
    /**
     * 成功数量
     */
    private int successCount;
    /**
     * 失败数量
     */
    private int failCount;

    private String text;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PushOrderNotifyMessage(Packet packet, Connection connection) {
        super(packet, connection);
    }

    public PushOrderNotifyMessage(Connection connection) {
        super(new Packet(Command.OrderNotify), connection);
    }


    @Override
    public void decode(ByteBuffer byteBuf) {
        this.type = decodeInt(byteBuf);
        this.orderType = decodeInt(byteBuf);
        this.instrumentId = decodeString(byteBuf);
        this.instrumentName = decodeString(byteBuf);
        this.status = decodeInt(byteBuf);
        this.allCount = decodeInt(byteBuf);
        this.successCount = decodeInt(byteBuf);
        this.failCount = decodeInt(byteBuf);
        this.text = decodeString(byteBuf);

    }

    @Override
    public void encode(ByteBuf byteBuf) {
        encodeInt(byteBuf, type);
        encodeInt(byteBuf, orderType);
        encodeString(byteBuf, instrumentId);
        encodeString(byteBuf, instrumentName);
        encodeInt(byteBuf, status);
        encodeInt(byteBuf, allCount);
        encodeInt(byteBuf, successCount);
        encodeInt(byteBuf, failCount);
        encodeString(byteBuf, text);

    }

    @Override
    public String toString() {
        return
                "{type:" + type +
                        "{orderType:" + orderType +
                        "{instrumentId:" + type +
                        "{instrumentName:" + instrumentName +
                        "{status:" + status +
                        "{allCount:" + allCount +
                        "{successCount:" + successCount +
                        "{failCount:" + failCount +
                        "{text:" + text +
                        "}";
    }
}
