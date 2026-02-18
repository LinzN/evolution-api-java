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

package de.linzn.evolutionApiJava;

import de.linzn.evolutionApiJava.api.instances.ConnectionStatus;
import de.linzn.evolutionApiJava.api.instances.FetchInstances;
import de.linzn.evolutionApiJava.api.messages.SendText;
import de.linzn.evolutionApiJava.api.Jid;
import de.linzn.evolutionApiJava.poolMQ.EventType;
import de.linzn.evolutionApiJava.poolMQ.PoolManager;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class EvolutionApi {
    private final WebClient webClient;
    private final PoolManager poolManager;
    private final String instanceName;


    public EvolutionApi(String baseURL, String authKey, String instanceName, String rabbitMQHostName, String rabbitMQUsername, String rabbitMQPassword, String rabbotMQVirtualHost) {
        this.webClient = WebClient.builder().baseUrl(baseURL).defaultHeader("apikey", authKey).build();
        this.poolManager = new PoolManager(this, rabbitMQHostName, rabbitMQUsername, rabbitMQPassword, rabbotMQVirtualHost);
        this.instanceName = instanceName;
    }

    public void registerListener(EventType eventType, DataListener dataListener) {
        if (!this.poolManager.listeners.containsKey(eventType.name())) {
            ArrayList<DataListener> listener = new ArrayList<>();
            this.poolManager.listeners.put(eventType.name(), listener);
        }
        if (!this.poolManager.listeners.get(eventType.name()).contains(dataListener)) {
            this.poolManager.listeners.get(eventType.name()).add(dataListener);
        }
    }

    public void enable() throws IOException, TimeoutException {
        this.poolManager.connect();
    }

    public String getInstanceName() {
        return this.instanceName;
    }


    public String getConnectionState() {
        return ConnectionStatus.builder(this.webClient, this.instanceName).getState();
    }

    public ArrayList<FetchInstances.InstanceData> getFetchInstances() {
        return FetchInstances.builder(this.webClient).getInstances();
    }

    public void sendTextMessage(Jid receiverJid, String textMessage) {
        SendText.builder(this.webClient, this.instanceName, receiverJid, textMessage);
    }

}
