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

import de.linzn.evolutionApiJava.EvolutionApi;
import de.linzn.evolutionApiJava.api.Call;
import de.linzn.evolutionApiJava.event.EventPriority;
import de.linzn.evolutionApiJava.event.EventSettings;
import de.linzn.evolutionApiJava.event.defaultEvents.NewCallEvent;
import de.linzn.evolutionApiJava.event.defaultEvents.NewMessageEvent;

public class TestListener {
    private EvolutionApi evolutionApi;

    public TestListener(EvolutionApi evolutionApi) {
        this.evolutionApi = evolutionApi;
    }


    @EventSettings(priority = EventPriority.NORMAL)
    public void onNewMessage(NewMessageEvent event) {
        EvolutionApi.LOGGER().INFO("DATA1:" + event.textMessage().text());
    }

    @EventSettings(priority = EventPriority.NORMAL)
    public void onNewMessage(NewCallEvent event) {
        Call call = event.call();
        EvolutionApi.LOGGER().INFO("DATA2:" + call.getStatus() + ":::" + call.getRemoteJid() + "::::" + call.getStatus());
    }
}
