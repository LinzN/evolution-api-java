/*
 * Copyright (c) 2026 MirraNET, Niklas Linz. All rights reserved.
 *
 * This file is part of the MirraNET project and is licensed under the
 * GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You may use, distribute and modify this code under the terms
 * of the LGPLv3 license. You should have received a copy of the
 * license along with this file. If not, see <https://www.gnu.org/licenses/lgpl-3.0.html>
 * or contact: niklas.linz@mirranet.de
 */

package de.linzn.evolutionApiJava.poolMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import de.linzn.evolutionApiJava.EvolutionApi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class PoolManager {
    final EvolutionApi evolutionApi;
    private final ConnectionFactory factory;
    private final DeliverCallback deliverCallback;

    public PoolManager(EvolutionApi evolutionApi, String hostname, String username, String password, String virtualHost) {
        this.evolutionApi = evolutionApi;
        this.factory = new ConnectionFactory();
        this.factory.setHost(hostname);
        this.factory.setUsername(username);
        this.factory.setPassword(password);
        if (virtualHost != null) {
            this.factory.setVirtualHost(virtualHost);
        }
        this.deliverCallback = new PoolListener(this);
    }

    public void connect() throws IOException, TimeoutException {
        Connection connection = this.factory.newConnection();
        Channel channel = connection.createChannel();
        Map<String, Object> args = new HashMap<>();
        args.put("x-queue-type", "quorum");

        for (PoolApiType poolApiType : PoolApiType.values()) {
            channel.queueDeclare(this.evolutionApi.getInstanceName() + "." + poolApiType.toEventId(), true, false, false, args);
            channel.basicConsume(this.evolutionApi.getInstanceName() + "." + poolApiType.toEventId(), true, deliverCallback, consumerTag -> {
            });
        }
    }
}
