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

package de.linzn.evolutionApiJava.event;


import de.linzn.evolutionApiJava.EvolutionApi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EventHandler {
    private final EvolutionApi evolutionApi;
    private final Map<Object, Map<Method, Class<EvolutionEvent>>> activeListener;


    public EventHandler(EvolutionApi evolutionApi) {
        this.evolutionApi = evolutionApi;
        this.activeListener = new HashMap<>();
    }

    /**
     * Gets methods and event class in a listener
     *
     * @param listener Listener to check if a method has an annotation
     * @return Map with event class and methods for this listener
     */
    private Map<Method, Class<EvolutionEvent>> findHandlers(Object listener) {
        Map<Method, Class<EvolutionEvent>> methods = new HashMap<>();

        for (Method m : listener.getClass().getDeclaredMethods()) {
            EventSettings annotation = m.getAnnotation(EventSettings.class);
            if (annotation != null) {
                Class<?>[] params = m.getParameterTypes();
                if (params.length != 1) {
                    EvolutionApi.LOGGER().ERROR("Method " + m + " in class " + listener.getClass() + " annotated with " + annotation + " does not have single argument");
                    continue;
                }
                Class<EvolutionEvent> iEvent = (Class<EvolutionEvent>) params[0];
                methods.put(m, iEvent);
            }
        }
        return methods;
    }


    /**
     * Call a listener method
     *
     * @param event         EvolutionEvent to call
     * @param method        Method in listener
     * @param classInstance classInstance object which contains the method to call
     */
    private void callMethod(EvolutionEvent event, Method method, Object classInstance) {
        try {
            method.invoke(classInstance, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            EvolutionApi.LOGGER().ERROR(e);
        }
    }


    /**
     * Register a new Event listener classInstance
     *
     * @param classInstance Event listener classInstance to register
     */
    public void register(Object classInstance) {
        Map<Method, Class<EvolutionEvent>> handler = findHandlers(classInstance);
        this.activeListener.put(classInstance, handler);
    }


    /**
     * Unregister an event listener classInstance
     *
     * @param classInstance Event listener classInstance to unregister
     */
    public void unregister(Object classInstance) {
        activeListener.remove(classInstance);
    }

    /**
     * Call all listener with the IEvent
     *
     * @param event EvolutionEvent to call in classInstance
     */
    public void fireEvent(final EvolutionEvent event) {
        EvolutionApi.LOGGER().DEBUG("Fire event " + event.getName());
        if (!event.isCanceled()) {
            fireEventPriority(event, EventPriority.HIGH);
        }
        if (!event.isCanceled()) {
            fireEventPriority(event, EventPriority.NORMAL);
        }
        if (!event.isCanceled()) {
            fireEventPriority(event, EventPriority.LOW);
        }
        fireEventPriority(event, EventPriority.CANCELED);
    }

    private void fireEventPriority(EvolutionEvent event, EventPriority eventPriority) {
        for (Object classInstance : this.activeListener.keySet()) {
            Map<Method, Class<EvolutionEvent>> handler = this.activeListener.get(classInstance);
            for (Method method : handler.keySet()) {
                Class<EvolutionEvent> stemEventClass = handler.get(method);
                if (stemEventClass.equals(event.getClass())) {
                    EventSettings annotation = method.getAnnotation(EventSettings.class);
                    EventPriority priority = annotation.priority();
                    if (priority == eventPriority) {
                        try {
                            callMethod(event, method, classInstance);
                        } catch (Exception e) {
                            EvolutionApi.LOGGER().ERROR(e);
                        }
                    }
                }
            }
        }
    }
}
