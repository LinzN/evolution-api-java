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
import org.json.JSONObject;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Base64;
import java.util.Objects;

/**
 * Sends an image, video, document or audio file ({@code POST /message/sendMedia/{instance}}).
 */
public class SendMedia {
    private final WebClient webClient;
    private final String instanceName;
    private final JidClient receiverClientId;
    private final String mediaType;
    private final String mimeType;
    private final String fileName;
    private final String caption;
    private final byte[] data;

    private SendMedia(WebClient webClient, String instanceName, JidClient receiverClientId, String mediaType,
                      String mimeType, String fileName, String caption, byte[] data) {
        this.webClient = webClient;
        this.instanceName = instanceName;
        this.receiverClientId = receiverClientId;
        this.mediaType = mediaType;
        this.mimeType = mimeType;
        this.fileName = fileName;
        this.caption = caption;
        this.data = data;
    }

    /**
     * @param mediaType image, video, document or audio
     */
    public static SendMedia builder(WebClient webClient, String instanceName, JidClient receiverClientId, String mediaType,
                                    String mimeType, String fileName, String caption, byte[] data) {
        return new SendMedia(webClient, instanceName, receiverClientId, mediaType, mimeType, fileName, caption, data).call();
    }

    private SendMedia call() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number", this.receiverClientId);
        jsonObject.put("mediatype", this.mediaType);
        if (this.mimeType != null) {
            jsonObject.put("mimetype", this.mimeType);
        }
        if (this.fileName != null) {
            jsonObject.put("fileName", this.fileName);
        }
        if (this.caption != null && !this.caption.isBlank()) {
            jsonObject.put("caption", this.caption);
        }
        jsonObject.put("media", Base64.getEncoder().encodeToString(this.data));

        Mono<String> response = this.webClient.post()
                .uri("/message/sendMedia/" + instanceName)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonObject.toString())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Server Error")))
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Client Error")))
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(60))
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(3)));

        Objects.requireNonNull(response.block());
        return this;
    }
}
