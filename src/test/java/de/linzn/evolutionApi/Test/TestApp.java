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
import de.linzn.evolutionApiJava.api.Jid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class TestApp {

    static void main() {

        EvolutionApi evolutionApi = new EvolutionApi("http://10.50.0.22:8080", "xxxx", "MirraAPI", "10.50.0.22", "user", "xxx", null);
        try {
            evolutionApi.enable();
            evolutionApi.getEventHandler().register(new TestListener(evolutionApi));
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        evolutionApi.SetOnlineOffline(true);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //
        evolutionApi.SetOnlineOffline(false);
        ArrayList<Jid> jids = evolutionApi.getContacts();
        for (Jid jid : jids) {
            System.out.println(jid);
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
