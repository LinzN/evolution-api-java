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

package de.linzn.evolutionApiJava.api;

import de.linzn.evolutionApiJava.EvolutionApi;
import de.linzn.evolutionApiJava.api.exceptions.InvalidTextMessageFormat;
import org.json.JSONObject;

/**
 * An incoming WhatsApp message.
 *
 * @param remoteClientId chat the message belongs to
 * @param text           text of the message or the caption of a media message; empty if there is none
 * @param pushName       display name of the sender
 * @param fromMe         sent by this instance
 * @param messageId      WhatsApp message id
 * @param media          attached media, null for text messages
 * @param raw            the message as received from Evolution API; needed to download media
 */
public record TextMessage(JidClient remoteClientId, String text, String pushName, boolean fromMe,
                          String messageId, MediaAttachment media, JSONObject raw) {

    /* message types that only wrap another message */
    private static final String[] WRAPPERS = {"ephemeralMessage", "viewOnceMessage", "viewOnceMessageV2", "viewOnceMessageV2Extension", "documentWithCaptionMessage"};

    public TextMessage(JidClient remoteClientId, String text, String pushName, boolean fromMe) {
        this(remoteClientId, text, pushName, fromMe, null, null, null);
    }

    public TextMessage(JidClient remoteClientId, String text, String pushName) {
        this(remoteClientId, text, pushName, false);
    }

    /**
     * @return the chat id as sent by WhatsApp, e.g. {@code 4912345@s.whatsapp.net}, {@code ...@g.us} or {@code status@broadcast}
     */
    public String remoteJid() {
        return this.raw != null ? this.raw.getJSONObject("key").optString("remoteJid", null) : String.valueOf(this.remoteClientId);
    }

    /**
     * @return true for status updates and broadcast lists
     */
    public boolean isBroadcast() {
        String jid = this.remoteJid();
        return jid != null && jid.endsWith("@broadcast");
    }

    /**
     * @return true for group chats
     */
    public boolean isGroup() {
        String jid = this.remoteJid();
        return jid != null && jid.endsWith("@g.us");
    }

    public boolean hasMedia() {
        return this.media != null;
    }

    public static TextMessage parse(JSONObject jsonObject) throws InvalidTextMessageFormat {
        try {
            JSONObject key = jsonObject.getJSONObject("key");
            JidClient jsonRemoteClientId = EvolutionApi.getClientCache().requestOf(key.getString("remoteJid"));
            String jsonPushName = "Unknown";
            if (!jsonObject.isNull("pushName")) {
                jsonPushName = jsonObject.getString("pushName");
            }
            boolean jsonFromMe = key.getBoolean("fromMe");
            String messageId = key.optString("id", null);

            JSONObject message = unwrap(jsonObject.getJSONObject("message"));
            String text = "";
            MediaAttachment media = null;
            if (message.has("conversation")) {
                text = message.optString("conversation", "");
            } else if (message.has("extendedTextMessage")) {
                text = message.getJSONObject("extendedTextMessage").optString("text", "");
            } else if (message.has("imageMessage")) {
                JSONObject image = message.getJSONObject("imageMessage");
                text = image.optString("caption", "");
                media = media("image", image, false, jsonObject);
            } else if (message.has("documentMessage")) {
                JSONObject document = message.getJSONObject("documentMessage");
                text = document.optString("caption", "");
                media = media("document", document, false, jsonObject);
            } else if (message.has("audioMessage")) {
                JSONObject audio = message.getJSONObject("audioMessage");
                media = media("audio", audio, audio.optBoolean("ptt", false), jsonObject);
            } else if (message.has("videoMessage")) {
                JSONObject video = message.getJSONObject("videoMessage");
                text = video.optString("caption", "");
                media = media("video", video, false, jsonObject);
            } else if (message.has("stickerMessage")) {
                media = media("sticker", message.getJSONObject("stickerMessage"), false, jsonObject);
            } else {
                throw new IllegalArgumentException("Unsupported message type: " + message.keySet());
            }
            return new TextMessage(jsonRemoteClientId, text, jsonPushName, jsonFromMe, messageId, media, jsonObject);
        } catch (Exception e) {
            throw new InvalidTextMessageFormat(e);
        }
    }

    private static JSONObject unwrap(JSONObject message) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String wrapper : WRAPPERS) {
                JSONObject inner = message.optJSONObject(wrapper);
                if (inner != null && inner.optJSONObject("message") != null) {
                    message = inner.getJSONObject("message");
                    changed = true;
                }
            }
        }
        return message;
    }

    private static MediaAttachment media(String type, JSONObject media, boolean voice, JSONObject root) {
        /* with "webhook base64" enabled, Evolution API puts the content into message.base64 */
        String base64 = root.getJSONObject("message").optString("base64", null);
        long size = 0;
        Object length = media.opt("fileLength");
        if (length instanceof Number number) {
            size = number.longValue();
        } else if (length instanceof JSONObject object) {
            size = object.optLong("low", 0);
        } else if (length != null) {
            try {
                size = Long.parseLong(length.toString());
            } catch (NumberFormatException ignored) {
            }
        }
        return new MediaAttachment(type, media.optString("mimetype", null), media.optString("fileName", null), size, voice, base64);
    }
}
