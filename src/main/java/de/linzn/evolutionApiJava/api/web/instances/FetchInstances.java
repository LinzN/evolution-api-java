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


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;


public class FetchInstances {
    private final WebClient webClient;
    private ArrayList<InstanceData> instances;

    private FetchInstances(WebClient webClient) {
        this.webClient = webClient;
    }

    public static FetchInstances builder(WebClient webClient) {
        return new FetchInstances(webClient).call();
    }

    private FetchInstances call() {

        Mono<String> response = this.webClient.get()
                .uri("/instance/fetchInstances")
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Server Error")))
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Client Error")))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3))
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)));

        this.fill(new JSONArray(Objects.requireNonNull(response.block())));
        return this;
    }

    private void fill(JSONArray jsonArray) {
        this.instances = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            InstanceData instanceData = new InstanceData(jsonObject);
            this.instances.add(instanceData);
        }
    }

    public ArrayList<InstanceData> getInstances() {
        return instances;
    }

    public static class InstanceData {
        public String id;
        public String name;
        public String connectionStatus;
        public String ownerJid;
        public String profileName;
        public String integration;

        public InstanceData(JSONObject jsonObject) {
            id = jsonObject.getString("id");
            name = jsonObject.getString("name");
            connectionStatus = jsonObject.getString("connectionStatus");
            ownerJid = jsonObject.getString("ownerJid");
            profileName = jsonObject.getString("profileName");
            integration = jsonObject.getString("integration");
        }
    }
}