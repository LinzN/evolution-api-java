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
import de.linzn.evolutionApiJava.api.exceptions.InvalidJidClient;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class ClientCache {
    private final EvolutionApi evolutionApi;

    private final ArrayList<JidClient> clients;

    public ClientCache(EvolutionApi evolutionApi) {
        this.evolutionApi = evolutionApi;
        this.clients = new ArrayList<>();
    }

    public JidClient requestOf(String number) throws InvalidJidClient {
        number = number.replaceAll(":(.*?)@", "@");
        boolean isLid;
        boolean valid;
        if (number.endsWith("@s.whatsapp.net")) {
            isLid = false;
            valid = true;
        } else if (number.endsWith("@lid")) {
            isLid = true;
            valid = true;
        } else if (number.endsWith("@")) {
            isLid = false;
            valid = false;
        } else {
            number = number + "@s.whatsapp.net";
            isLid = false;
            valid = true;
        }

        if (valid) {
            JidClient client = this.searchClient(number);
            if (client != null) {
                EvolutionApi.LOGGER().DEBUG("JidClient found in cache: " + number);
                return client;
            }
            if (isLid) {
                EvolutionApi.LOGGER().DEBUG("JidClient NOT found in cache. Try to get lid number online: " + number);
                JidClient jidClient = new JidClient();
                jidClient.lid = number;
                ArrayList<String> queryNumbers = new ArrayList<>();
                queryNumbers.add(number);
                Map<String, JSONObject> data = this.evolutionApi.getWebApiProvider().getJidNumbers(queryNumbers);
                if (data.containsKey(number)) {
                    JSONObject jsonObject = data.get(number);
                    if (jsonObject.getBoolean("exists")) {
                        String jid = jsonObject.getString("jid");
                        if (jid.endsWith("@lid")) {
                            EvolutionApi.LOGGER().WARNING("Lid was returned from query. Jid not found: " + number);
                        } else {
                            JidClient searchedJid = this.searchClient(jid);
                            if (searchedJid != null) {
                                EvolutionApi.LOGGER().DEBUG("Queried jid found in cache. Merging!");
                                searchedJid.lid = number;
                                return searchedJid;
                            }
                            jidClient.jid = jid;
                            EvolutionApi.LOGGER().DEBUG("Valid jid returned from query: " + number + " -> " + jid);
                        }
                        this.clients.add(jidClient);
                        return jidClient;
                    }
                }

            } else {
                EvolutionApi.LOGGER().DEBUG("JidClient NOT found in cache. Add to cache without query: " + number);
                JidClient jidClient = new JidClient();
                jidClient.jid = number;
                this.clients.add(jidClient);
                return jidClient;
            }
        } else {
            EvolutionApi.LOGGER().DEBUG("Invalid number: " + number);
            throw new InvalidJidClient(number);
        }
        return null;
    }

    private JidClient searchClient(String number) {
        return this.clients.stream()
                .filter(c -> (c.jid != null && c.jid.equals(number)) ||
                        (c.lid != null && c.lid.equals(number)))
                .findFirst()
                .orElse(null);
    }
}
