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

import de.linzn.evolutionApiJava.api.exceptions.InvalidTextMessageFormat;
import org.json.JSONObject;

public record TextMessage(Jid remoteJid, String text, String pushName, boolean fromMe) {

    public TextMessage(Jid remoteJid, String text, String pushName) {
        this(remoteJid, text, pushName, false);
    }

    public static TextMessage parse(JSONObject jsonObject) throws InvalidTextMessageFormat {
        try {

            Jid jsonRemoteJid = new Jid(jsonObject.getJSONObject("key").getString("remoteJid"));
            String jsonPushName = "Unknown";
            if (!jsonObject.isNull("pushName")) {
                jsonPushName = jsonObject.getString("pushName");
            }
            String jsonTextMessage = jsonObject.getJSONObject("message").getString("conversation");
            boolean jsonFromMe = jsonObject.getJSONObject("key").getBoolean("fromMe");
            return new TextMessage(jsonRemoteJid, jsonTextMessage, jsonPushName, jsonFromMe);
        } catch (Exception e) {
            throw new InvalidTextMessageFormat(e);
        }
    }
}
