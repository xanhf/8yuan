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
import com.trade.eight.mpush.api.Logger;
import com.trade.eight.mpush.api.connection.Connection;
import com.trade.eight.mpush.api.protocol.Packet;
import com.trade.eight.mpush.client.ClientConfig;
import com.trade.eight.mpush.entity.OrderNotifyData;
import com.trade.eight.mpush.message.PushOrderNotifyMessage;
import com.trade.eight.mpush.message.ResponseQuotationMessage;

/**
 * 订单推送响应
 */
public final class OrderNotifyHandler extends BaseMessageHandler<PushOrderNotifyMessage> {
    private final Logger logger = ClientConfig.I.getLogger();

    @Override
    public PushOrderNotifyMessage decode(Packet packet, Connection connection) {
        return new PushOrderNotifyMessage(packet, connection);
    }

    @Override
    public void handle(PushOrderNotifyMessage message) {

        ClientListener listener = ClientConfig.I.getClientListener();
        listener.onOrderNotify(OrderNotifyData.messageToData(message));
        logger.w(">>> receive ok message=%s", message);
    }
}
