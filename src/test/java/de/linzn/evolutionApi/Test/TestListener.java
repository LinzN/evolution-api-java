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

package de.linzn.evolutionApi.Test;

import de.linzn.evolutionApiJava.DataListener;
import de.linzn.evolutionApiJava.EvolutionApi;
import de.linzn.evolutionApiJava.api.Jid;
import de.linzn.evolutionApiJava.poolMQ.EventType;
import org.json.JSONObject;

public class TestListener implements DataListener {
    private EvolutionApi evolutionApi;

    public TestListener(EvolutionApi evolutionApi){
        this.evolutionApi = evolutionApi;
    }

    @Override
    public void onReceive(EventType eventType, JSONObject data) {
        System.out.println("EventType: " + eventType.name());
        System.out.println("DATA:" + data);
        Jid remoteJid = new Jid(data.getJSONObject("key").getString("remoteJid"));
        String msg = data.getJSONObject("message").getString("conversation");
        this.evolutionApi.sendTextMessage(remoteJid, "Mirror->" + msg);
    }
}
