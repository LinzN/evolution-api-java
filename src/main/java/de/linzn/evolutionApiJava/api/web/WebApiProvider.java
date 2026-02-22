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

package de.linzn.evolutionApiJava.api.web;

import de.linzn.evolutionApiJava.EvolutionApi;
import de.linzn.evolutionApiJava.api.JidClient;
import de.linzn.evolutionApiJava.api.web.instances.ConnectionStatus;
import de.linzn.evolutionApiJava.api.web.instances.FetchInstances;
import de.linzn.evolutionApiJava.api.web.instances.GetContacts;
import de.linzn.evolutionApiJava.api.web.messages.*;
import org.json.JSONObject;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Map;

public class WebApiProvider {

    private final EvolutionApi evolutionApi;
    private final WebClient webClient;

    public WebApiProvider(EvolutionApi evolutionApi, WebClient webClient) {
        this.evolutionApi = evolutionApi;
        this.webClient = webClient;
    }

    public String getConnectionState() {
        return ConnectionStatus.builder(this.webClient, this.evolutionApi.getInstanceName()).getState();
    }

    public ArrayList<FetchInstances.InstanceData> getFetchInstances() {
        return FetchInstances.builder(this.webClient).getInstances();
    }

    public void sendTextMessage(JidClient receiverClientId, String textMessage) {
        SendText.builder(this.webClient, this.evolutionApi.getInstanceName(), receiverClientId, textMessage);
    }

    public void sendTypingPresence(JidClient receiverClientId, int delay) {
        SendTypingPresence.builder(this.webClient, this.evolutionApi.getInstanceName(), receiverClientId, delay);
    }

    public void SetOnlineOffline(boolean online) {
        SetOnlineOffline.builder(this.webClient, this.evolutionApi.getInstanceName(), online);
    }

    public void CreateStatusStorie(String content, ArrayList<JidClient> contacts) {
        CreateStatusStorie.builder(this.webClient, this.evolutionApi.getInstanceName(), content, contacts);
    }

    public ArrayList<JidClient> getContacts() {
        return GetContacts.builder(this.webClient, this.evolutionApi.getInstanceName()).getContacts();
    }

    public Map<String, JSONObject> getJidNumbers(ArrayList<String> numbers) {
        return GetJidNumbers.builder(this.webClient, this.evolutionApi.getInstanceName(), numbers).getJids();
    }
}
