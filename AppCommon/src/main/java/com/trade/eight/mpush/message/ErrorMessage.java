/*
 * (C) Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     ohun@live.cn (夜色)
 */

package com.trade.eight.mpush.message;


import com.trade.eight.mpush.api.connection.Connection;
import com.trade.eight.mpush.api.protocol.Command;
import com.trade.eight.mpush.api.protocol.Packet;
import com.trade.eight.mpush.util.ByteBuf;

import java.nio.ByteBuffer;

/**
 * Created by ohun on 2015/12/28.
 *
 * @author ohun@live.cn (夜色)
 */
public final class ErrorMessage extends ByteBufMessage {
    public int cmd;
    public byte code;
    public String reason;
    public String data;

    public ErrorMessage(int cmd, Packet message, Connection connection) {
        super(message, connection);
        this.cmd = cmd;
    }

    public ErrorMessage(Packet message, Connection connection) {
        super(message, connection);
    }

    @Override
    public void decode(ByteBuffer body) {
        cmd = decodeInt(body);
        code = decodeByte(body);
        reason = decodeString(body);
        data = decodeString(body);
    }

    @Override
    public void encode(ByteBuf body) {
        encodeInt(body, cmd);
        encodeByte(body, code);
        encodeString(body, reason);
        encodeString(body, data);
    }

    public static ErrorMessage from(BaseMessage src) {
        return new ErrorMessage(src.packet.cmd, new Packet(Command.ERROR
                , src.packet.sessionId), src.connection);
    }

    public ErrorMessage setReason(String reason) {
        this.reason = reason;
        return this;
    }

    @Override
    public void send() {
        super.sendRaw();
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "cmd=" + cmd +
                ", code=" + code +
                ", reason='" + reason + '\'' +
                '}';
    }
}
