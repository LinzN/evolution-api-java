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

public enum RabbitMQApiType {
    APPLICATION_STARTUP,
    CALL,
    CHATS_DELETE,
    CHATS_SET,
    CHATS_UPDATE,
    CHATS_UPSERT,
    CONNECTION_UPDATE,
    CONTACTS_SET,
    CONTACTS_UPDATE,
    CONTACTS_UPSERT,
    GROUP_PARTICIPANTS_UPDATE,
    GROUP_UPDATE,
    GROUPS_UPSERT,
    LABELS_ASSOCIATION,
    LABELS_EDIT,
    LOGOUT_INSTANCE,
    MESSAGES_DELETE,
    MESSAGES_SET,
    MESSAGES_UPDATE,
    MESSAGES_UPSERT,
    PRESENCE_UPDATE,
    QRCODE_UPDATED,
    REMOVE_INSTANCE,
    SEND_MESSAGE,
    TYPEBOT_CHANGE_STATUS,
    TYPEBOT_START;

    public static RabbitMQApiType fromEventId(String eventId) {
        for (RabbitMQApiType val : values()) {
            if (val.toEventId().equalsIgnoreCase(eventId)) {
                return val;
            }
        }
        throw new IllegalArgumentException("No EventType found by this eventId: " + eventId);
    }

    public String toEventId() {
        return this.name().toLowerCase().replace('_', '.');
    }
}
