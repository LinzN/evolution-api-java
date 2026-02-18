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

package de.linzn.evolutionApiJava.api.instances;


import de.linzn.evolutionApiJava.api.Jid;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;


public class GetContacts {
    private final WebClient webClient;
    private final String instanceName;
    private ArrayList<Jid> contacts;


    private GetContacts(WebClient webClient, String instanceName) {
        this.webClient = webClient;
        this.instanceName = instanceName;
    }

    public static GetContacts builder(WebClient webClient, String instanceName) {
        return new GetContacts(webClient, instanceName).call();
    }

    private GetContacts call() {
        Mono<String> response = this.webClient.post()
                .uri("/chat/findContacts/" + instanceName)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Server Error")))
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Client Error")))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3))
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)));

        JSONArray queryData = new JSONArray(Objects.requireNonNull(response.block()));

        this.contacts = new ArrayList<>();
        for (int i = 0; i < queryData.length(); i++) {
            JSONObject obj = queryData.getJSONObject(i);
            String jidString = obj.getString("remoteJid");
            if (!jidString.contains("@lid") && jidString.contains("@s.whatsapp.net") && !jidString.contains(":")) {
                Jid jid = new Jid(jidString);
                contacts.add(jid);
            }

        }
        return this;
    }


    public ArrayList<Jid> getContacts() {
        return this.contacts;
    }

    public String getInstanceName() {
        return instanceName;
    }
}