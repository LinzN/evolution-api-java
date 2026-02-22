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

package de.linzn.evolutionApiJava.api.web.messages;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GetJidNumbers {
    private final WebClient webClient;
    private final String instanceName;
    private final ArrayList<String> numbers;
    private Map<String, JSONObject> jids;

    private GetJidNumbers(WebClient webClient, String instanceName, ArrayList<String> numbers) {
        this.webClient = webClient;
        this.instanceName = instanceName;
        this.numbers = numbers;
    }

    public static GetJidNumbers builder(WebClient webClient, String instanceName, ArrayList<String> numbers) {
        return new GetJidNumbers(webClient, instanceName, numbers).call();
    }

    private GetJidNumbers call() {
        JSONArray jsonArray = new JSONArray();
        for (String number : numbers) {
            jsonArray.put(number);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("numbers", jsonArray);

        Mono<String> response = this.webClient.post()
                .uri("/chat/whatsappNumbers/" + instanceName)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonObject.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Server Error")))
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Client Error")))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3))
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)));

        this.jids = new HashMap<>();
        this.fill(new JSONArray(Objects.requireNonNull(response.block())));
        return this;
    }

    private void fill(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            this.jids.put(obj.getString("number"), obj);
        }
    }

    public Map<String, JSONObject> getJids() {
        return jids;
    }
}
