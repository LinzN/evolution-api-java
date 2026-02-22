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
import de.linzn.evolutionApiJava.api.exceptions.InvalidCallFormat;
import org.json.JSONObject;

public class Call {

    private final JidClient remoteClientId;
    private final CallStatus status;
    private final boolean isVideo;

    private Call(JidClient remoteClientId, CallStatus status, boolean isVideo) {
        this.remoteClientId = remoteClientId;
        this.status = status;
        this.isVideo = isVideo;
    }

    public static Call parse(JSONObject jsonObject) throws InvalidCallFormat {
        try {
            JidClient jsonRemoteClientId = EvolutionApi.getClientCache().requestOf(jsonObject.getString("from"));
            CallStatus jsonStatus = CallStatus.valueOf(jsonObject.getString("status").toUpperCase());
            boolean jsonIsVideo = jsonObject.getBoolean("isVideo");
            return new Call(jsonRemoteClientId, jsonStatus, jsonIsVideo);
        } catch (Exception e) {
            throw new InvalidCallFormat(e);
        }
    }

    public JidClient getRemoteJid() {
        return remoteClientId;
    }

    public CallStatus getStatus() {
        return status;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public enum CallStatus {
        OFFER, RINGING;
    }
}
