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

package de.linzn.evolutionApiJava.poolMQ;

import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import de.linzn.evolutionApiJava.DataListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class PoolListener implements DeliverCallback {
    private final PoolManager poolManager;

    public PoolListener(PoolManager poolManager) {
        this.poolManager = poolManager;
    }

    @Override
    public void handle(String s, Delivery delivery) throws IOException {
        try {
            JSONObject input = new JSONObject(new String(delivery.getBody(), "UTF-8"));
            EventType eventType = EventType.fromEventId(input.getString("event"));
            JSONObject data = new JSONObject();
            if (input.get("data") instanceof JSONObject) {
                data = input.getJSONObject("data");
            } else if (input.get("data") instanceof JSONArray) {
                data.put("entries", input.getJSONArray("data"));
            }

            if (this.poolManager.listeners.containsKey(eventType.name())) {
                for (DataListener dataListener : this.poolManager.listeners.get(eventType.name())) {
                    try {
                        dataListener.onReceive(eventType, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
