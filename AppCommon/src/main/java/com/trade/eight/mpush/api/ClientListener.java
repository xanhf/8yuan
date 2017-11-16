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

package com.trade.eight.mpush.api;


import com.trade.eight.entity.Optional;
import com.trade.eight.mpush.entity.OrderNotifyData;

/**
 * Created by ohun on 2016/1/23.
 *
 * @author ohun@live.cn (夜色)
 */
public interface ClientListener {

    void onConnected(Client client);

    void onDisConnected(Client client);

    void onHandshakeOk(Client client, int heartbeat);

    void onReceivePush(Client client, byte[] content, int messageId);

    void onKickUser(String deviceId, String userId);

    void onBind(boolean success, String userId);

    void onUnbind(boolean success, String userId);

    // 订单反馈
    void onOrderNotify(OrderNotifyData orderNotifyData);

    // 接收到行情消息
    void onReceiveQuotation(Optional optional);
}
