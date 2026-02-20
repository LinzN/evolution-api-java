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

import de.linzn.evolutionApiJava.api.Jid;
import de.linzn.evolutionApiJava.event.EventHandler;
import de.linzn.evolutionApiJava.logger.DefaultLogger;
import de.linzn.evolutionApiJava.logger.EvolutionLogger;
import de.linzn.evolutionApiJava.poolMQ.PoolManager;
import de.linzn.evolutionApiJava.webCall.instances.ConnectionStatus;
import de.linzn.evolutionApiJava.webCall.instances.FetchInstances;
import de.linzn.evolutionApiJava.webCall.instances.GetContacts;
import de.linzn.evolutionApiJava.webCall.messages.CreateStatusStorie;
import de.linzn.evolutionApiJava.webCall.messages.SendText;
import de.linzn.evolutionApiJava.webCall.messages.SendTypingPresence;
import de.linzn.evolutionApiJava.webCall.messages.SetOnlineOffline;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class EvolutionApi {
    private final WebClient webClient;
    private final PoolManager poolManager;
    private final String instanceName;
    private final EventHandler eventHandler;
    private EvolutionLogger logger;


    public EvolutionApi(String baseURL, String authKey, String instanceName, String rabbitMQHostName, String rabbitMQUsername, String rabbitMQPassword, String rabbotMQVirtualHost) {
        this.instanceName = instanceName;
        this.logger = new DefaultLogger();
        this.eventHandler = new EventHandler(this);
        this.webClient = WebClient.builder().baseUrl(baseURL).defaultHeader("apikey", authKey).build();
        this.poolManager = new PoolManager(this, rabbitMQHostName, rabbitMQUsername, rabbitMQPassword, rabbotMQVirtualHost);
    }

    public void registerLogger(EvolutionLogger evolutionLogger) {
        this.logger = evolutionLogger;
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

    public void sendTypingPresence(Jid receiverJid, int delay) {
        SendTypingPresence.builder(this.webClient, this.instanceName, receiverJid, delay);
    }

    public void SetOnlineOffline(boolean online) {
        SetOnlineOffline.builder(this.webClient, this.instanceName, online);
    }

    public void CreateStatusStorie(String content, ArrayList<Jid> contacts) {
        CreateStatusStorie.builder(this.webClient, this.instanceName, content, contacts);
    }

    public ArrayList<Jid> getContacts() {
        return GetContacts.builder(this.webClient, this.instanceName).getContacts();
    }

    public EvolutionLogger getLogger() {
        return logger;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }
}
