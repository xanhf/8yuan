package com.trade.eight.mpush.message;

import com.trade.eight.mpush.api.connection.Connection;
import com.trade.eight.mpush.api.protocol.Command;
import com.trade.eight.mpush.api.protocol.Packet;
import com.trade.eight.mpush.util.ByteBuf;

import java.nio.ByteBuffer;

/**
 * 作者：Created by ocean
 * 时间：on 2017/7/10.
 */

public class QuotationMessage extends ByteBufMessage {
    public String codes;

    public QuotationMessage(Connection connection) {
        super(new Packet(Command.Quotation, genSessionId()), connection);
    }

    public QuotationMessage(Packet message, Connection connection) {
        super(message, connection);
    }

    @Override
    protected void decode(ByteBuffer body) {
        this.codes = decodeString(body);

    }

    @Override
    public void encode(ByteBuf byteBuf) {
        encodeString(byteBuf, codes);
    }

    public String getCodes() {
        return codes;
    }

    public void setCodes(String codes) {
        this.codes = codes;
    }

    @Override
    public String toString() {
        return "codes:" + codes;
    }
}