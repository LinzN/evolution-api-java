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

import de.linzn.evolutionApiJava.api.MediaAttachment;
import de.linzn.evolutionApiJava.api.TextMessage;
import org.json.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Objects;

/**
 * Downloads the media of a message as base64 ({@code POST /chat/getBase64FromMediaMessage/{instance}}).
 */
public class GetMediaBase64 {
    private final WebClient webClient;
    private final String instanceName;
    private final TextMessage message;
    private MediaAttachment media;

    private GetMediaBase64(WebClient webClient, String instanceName, TextMessage message) {
        this.webClient = webClient;
        this.instanceName = instanceName;
        this.message = message;
    }

    public static GetMediaBase64 builder(WebClient webClient, String instanceName, TextMessage message) {
        return new GetMediaBase64(webClient, instanceName, message).call();
    }

    private GetMediaBase64 call() {
        if (!this.message.hasMedia()) {
            throw new IllegalArgumentException("The message has no media");
        }
        if (this.message.media().hasData()) {
            this.media = this.message.media();
            return this;
        }
        JSONObject source = this.message.raw() != null
                ? new JSONObject().put("key", this.message.raw().getJSONObject("key")).put("message", this.message.raw().getJSONObject("message"))
                : new JSONObject().put("key", new JSONObject().put("id", this.message.messageId()));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", source);
        jsonObject.put("convertToMp4", false);

        Mono<String> response = this.webClient.post()
                .uri("/chat/getBase64FromMediaMessage/" + instanceName)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonObject.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Server Error")))
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Client Error")))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(60))
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(5)));

        this.fill(new JSONObject(Objects.requireNonNull(response.block())));
        return this;
    }

    private void fill(JSONObject jsonObject) {
        String base64 = jsonObject.optString("base64", null);
        if (base64 == null || base64.isEmpty()) {
            throw new IllegalStateException("Evolution API returned no media data");
        }
        this.media = this.message.media().withBase64(base64, jsonObject.optString("mimetype", null), jsonObject.optString("fileName", null));
    }

    public MediaAttachment getMedia() {
        return media;
    }
}
