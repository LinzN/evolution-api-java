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

import de.linzn.evolutionApiJava.api.ClientCache;
import de.linzn.evolutionApiJava.api.web.WebApiProvider;
import de.linzn.evolutionApiJava.event.EventHandler;
import de.linzn.evolutionApiJava.logger.DefaultLogger;
import de.linzn.evolutionApiJava.logger.EvolutionLogger;
import de.linzn.evolutionApiJava.rabbitmq.RabbitMQManager;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class EvolutionApi {
    private static EvolutionLogger logger;
    private static ClientCache clientCache;
    private final WebApiProvider webApiProvider;
    private final String instanceName;
    private final EventHandler eventHandler;
    private RabbitMQManager rabbitMQManager;


    public EvolutionApi(String baseURL, String authKey, String instanceName) {
        this.instanceName = instanceName;
        logger = new DefaultLogger();
        this.eventHandler = new EventHandler(this);
        this.webApiProvider = new WebApiProvider(this, WebClient.builder().baseUrl(baseURL).defaultHeader("apikey", authKey).build());
        if (clientCache == null) {
            clientCache = new ClientCache(this);
        }
    }

    public static ClientCache getClientCache() {
        return clientCache;
    }

    public static EvolutionLogger LOGGER() {
        return logger;
    }

    public void setRabbitMQ(String rabbitMQHostName, String rabbitMQUsername, String rabbitMQPassword, String rabbotMQVirtualHost) {
        this.rabbitMQManager = new RabbitMQManager(this, rabbitMQHostName, rabbitMQUsername, rabbitMQPassword, rabbotMQVirtualHost);
    }

    public void registerLogger(EvolutionLogger evolutionLogger) {
        logger = evolutionLogger;
    }

    public void enable() throws IOException, TimeoutException {
        if (this.rabbitMQManager != null) {
            this.rabbitMQManager.connect();
        }
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public WebApiProvider getWebApiProvider() {
        return webApiProvider;
    }
}
