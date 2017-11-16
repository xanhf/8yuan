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

package com.trade.eight.mpush.handler;


import com.trade.eight.mpush.api.ClientListener;
import com.trade.eight.mpush.api.connection.SessionStorage;
import com.trade.eight.mpush.session.PersistentSession;
import com.trade.eight.mpush.client.ClientConfig;
import com.trade.eight.mpush.api.Logger;
import com.trade.eight.mpush.api.connection.Connection;
import com.trade.eight.mpush.api.connection.SessionContext;
import com.trade.eight.mpush.api.protocol.Packet;
import com.trade.eight.mpush.message.HandshakeOkMessage;
import com.trade.eight.mpush.security.AesCipher;
import com.trade.eight.mpush.security.CipherBox;
import com.trade.eight.tools.Log;

/**
 * Created by ohun on 2016/1/23.
 *
 * @author ohun@live.cn (夜色)
 */
public final class HandshakeOkHandler extends BaseMessageHandler<HandshakeOkMessage> {
    private final Logger logger = ClientConfig.I.getLogger();

    @Override
    public HandshakeOkMessage decode(Packet packet, Connection connection) {
        return new HandshakeOkMessage(packet, connection);
    }

    @Override
    public void handle(HandshakeOkMessage message) {
        logger.w(">>> handshake ok message=%s", message);

        Connection connection = message.getConnection();
        SessionContext context = connection.getSessionContext();
        byte[] serverKey = message.serverKey;
        if (serverKey.length != CipherBox.INSTANCE.getAesKeyLength()) {
            logger.w("handshake error serverKey invalid message=%s", message);
            connection.reconnect();
            return;
        }
        Log.e("HandshakeOkHandler","message.heartbeat==="+message.heartbeat);
        //设置心跳
        context.setHeartbeat(message.heartbeat);

        //更换密钥
        AesCipher cipher = (AesCipher) context.cipher;
        byte[] sessionKey = CipherBox.INSTANCE.mixKey(cipher.key, serverKey);
        context.changeCipher(new AesCipher(sessionKey, cipher.iv));

        //触发握手成功事件

        ClientListener listener = ClientConfig.I.getClientListener();
        listener.onHandshakeOk(connection.getClient(), message.heartbeat);

        //保存token
        saveToken(message, context);

    }

    private void saveToken(HandshakeOkMessage message, SessionContext context) {
        SessionStorage storage = ClientConfig.I.getSessionStorage();
        if (storage == null || message.sessionId == null) return;
        PersistentSession session = new PersistentSession();
        session.sessionId = message.sessionId;
        session.expireTime = message.expireTime;
        session.cipher = context.cipher;
        storage.saveSession(PersistentSession.encode(session));
    }
}
