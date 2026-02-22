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

package de.linzn.evolutionApiJava.rabbitmq;

import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import de.linzn.evolutionApiJava.EvolutionApi;
import de.linzn.evolutionApiJava.api.Call;
import de.linzn.evolutionApiJava.api.TextMessage;
import de.linzn.evolutionApiJava.event.defaultEvents.NewCallEvent;
import de.linzn.evolutionApiJava.event.defaultEvents.NewMessageEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class RabbitMQListener implements DeliverCallback {
    private final RabbitMQManager rabbitMQManager;

    public RabbitMQListener(RabbitMQManager rabbitMQManager) {
        this.rabbitMQManager = rabbitMQManager;
    }

    @Override
    public void handle(String s, Delivery delivery) throws IOException {
        try {
            JSONObject input = new JSONObject(new String(delivery.getBody(), "UTF-8"));
            RabbitMQApiType rabbitMQApiType = RabbitMQApiType.fromEventId(input.getString("event"));
            JSONObject data = new JSONObject();
            if (input.get("data") instanceof JSONObject) {
                data = input.getJSONObject("data");
            } else if (input.get("data") instanceof JSONArray) {
                data.put("entries", input.getJSONArray("data"));
            }

            switch (rabbitMQApiType) {
                case APPLICATION_STARTUP -> {
                    //TODO handel APPLICATION_STARTUP
                }
                case CALL -> {
                    this.rabbitMQManager.evolutionApi.getEventHandler().fireEvent(new NewCallEvent(Call.parse(data)));
                }
                case CHATS_DELETE -> {
                    //TODO handel CHATS_DELETE
                }
                case CHATS_SET -> {
                    //TODO handel CHATS_SET
                }
                case CHATS_UPDATE -> {
                    //TODO handel CHATS_UPDATE
                }
                case CHATS_UPSERT -> {
                    //TODO handel CHATS_UPSERT
                }
                case CONNECTION_UPDATE -> {
                    //TODO handel CONNECTION_UPDATE
                }
                case CONTACTS_SET -> {
                    //TODO handel CONTACTS_SET
                }
                case CONTACTS_UPDATE -> {
                    //TODO handel CONTACTS_UPDATE
                }
                case CONTACTS_UPSERT -> {
                    //TODO handel CONTACTS_UPSERT
                }
                case GROUP_PARTICIPANTS_UPDATE -> {
                    //TODO handel GROUP_PARTICIPANTS_UPDATE
                }
                case GROUP_UPDATE -> {
                    //TODO handel GROUP_UPDATE
                }
                case GROUPS_UPSERT -> {
                    //TODO handel GROUPS_UPSERT
                }
                case LABELS_ASSOCIATION -> {
                    //TODO handel LABELS_ASSOCIATION
                }
                case LABELS_EDIT -> {
                    //TODO handel LABELS_EDIT
                }
                case LOGOUT_INSTANCE -> {
                    //TODO handel LOGOUT_INSTANCE
                }
                case MESSAGES_DELETE -> {
                    //TODO handel MESSAGES_DELETE
                }
                case MESSAGES_SET -> {
                    //TODO handel MESSAGES_SET
                }
                case MESSAGES_UPDATE -> {
                    //TODO handel MESSAGES_UPDATE
                }
                case MESSAGES_UPSERT ->
                        this.rabbitMQManager.evolutionApi.getEventHandler().fireEvent(new NewMessageEvent(TextMessage.parse(data)));
                case PRESENCE_UPDATE -> {
                    //TODO handel PRESENCE_UPDATE
                }
                case QRCODE_UPDATED -> {
                    //TODO handel QRCODE_UPDATED
                }
                case REMOVE_INSTANCE -> {
                    //TODO handel REMOVE_INSTANCE
                }
                case SEND_MESSAGE -> {
                    //TODO handel SEND_MESSAGE
                }
                case TYPEBOT_CHANGE_STATUS -> {
                    //TODO handel TYPEBOT_CHANGE_STATUS
                }
                case TYPEBOT_START -> {
                    //TODO handel TYPEBOT_START
                }
            }

        } catch (Exception e) {
            EvolutionApi.LOGGER().ERROR(e);
        }
    }
}
