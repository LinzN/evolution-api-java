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

package de.linzn.evolutionApiJava.event.defaultEvents;

import de.linzn.evolutionApiJava.event.EvolutionEvent;
import org.json.JSONObject;

public class NewMessageEvent implements EvolutionEvent {
    private final JSONObject message;

    public NewMessageEvent(JSONObject message){
        this.message = message;
    }

    public JSONObject getMessage() {
        return message;
    }
}
