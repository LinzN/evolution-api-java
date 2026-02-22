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

import de.linzn.evolutionApiJava.api.JidClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;

public class CreateStatusStorie {
    private final WebClient webClient;
    private final String instanceName;
    private final String content;
    private final ArrayList<JidClient> contacts;

    private CreateStatusStorie(WebClient webClient, String instanceName, String content, ArrayList<JidClient> contacts) {
        this.webClient = webClient;
        this.instanceName = instanceName;
        this.content = content;
        this.contacts = contacts;
    }

    public static CreateStatusStorie builder(WebClient webClient, String instanceName, String content, ArrayList<JidClient> contacts) {
        return new CreateStatusStorie(webClient, instanceName, content, contacts).call();
    }

    private CreateStatusStorie call() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "text");
        jsonObject.put("content", content);
        jsonObject.put("caption", content);
        jsonObject.put("backgroundColor", "#008000");
        jsonObject.put("font", 2);
        jsonObject.put("allContacts", false);
        JSONArray list = new JSONArray();
        for (JidClient clientId : contacts) {
            list.put(clientId.toString());
        }
        jsonObject.put("statusJidList", list);


        Mono<String> response = this.webClient.post()
                .uri("/message/sendStatus/" + instanceName)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonObject.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Server Error")))
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Client Error")))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3))
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)));

        this.fill(new JSONObject(Objects.requireNonNull(response.block())));
        return this;
    }

    private void fill(JSONObject jsonObject) {

    }
}
