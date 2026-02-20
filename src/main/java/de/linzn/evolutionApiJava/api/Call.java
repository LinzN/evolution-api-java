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

import de.linzn.evolutionApiJava.api.exceptions.InvalidCallFormat;
import de.linzn.evolutionApiJava.api.exceptions.InvalidTextMessageFormat;
import org.json.JSONObject;

public class Call {

    private final Jid remoteJid;
    private final String status;
    private final boolean isVideo;

    private Call(Jid remoteJid, String status, boolean isVideo) {
        this.remoteJid = remoteJid;
        this.status = status;
        this.isVideo = isVideo;
    }

    public static Call parse(JSONObject jsonObject) throws InvalidCallFormat {
        try {
            Jid jsonRemoteJid = new Jid(jsonObject.getString("from"));
            String jsonStatus = jsonObject.getString("status");
            boolean jsonIsVideo= jsonObject.getBoolean("isVideo");
            return new Call(jsonRemoteJid, jsonStatus, jsonIsVideo);
        } catch (Exception e) {
            throw new InvalidCallFormat(e);
        }
    }

    public Jid getRemoteJid() {
        return remoteJid;
    }

    public String getStatus() {
        return status;
    }

    public boolean isVideo() {
        return isVideo;
    }
}
