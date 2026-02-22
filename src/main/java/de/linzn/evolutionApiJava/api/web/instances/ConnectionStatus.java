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

package de.linzn.evolutionApiJava.api.web.instances;


import org.json.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Objects;


public class ConnectionStatus {
    private final WebClient webClient;
    private final String instanceName;
    private String state;


    private ConnectionStatus(WebClient webClient, String instanceName) {
        this.webClient = webClient;
        this.instanceName = instanceName;
    }

    public static ConnectionStatus builder(WebClient webClient, String instanceName) {
        return new ConnectionStatus(webClient, instanceName).call();
    }

    private ConnectionStatus call() {
        Mono<String> response = this.webClient.get()
                .uri("/instance/connectionState/" + instanceName)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Server Error")))
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Client Error")))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3))
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)));

        JSONObject queryData = new JSONObject(Objects.requireNonNull(response.block()));
        this.state = queryData.getJSONObject("instance").getString("state");
        return this;
    }

    public String getState() {
        return state;
    }

    public String getInstanceName() {
        return instanceName;
    }
}