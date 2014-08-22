/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.divide.otto;

import io.divide.shared.event.Event;
import io.divide.shared.event.Subscriber;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SubscriberHandlerFinder implements HandlerFinder {

    private static final Map<Class<?>, Map<Class<?>, Method>> PRODUCERS_CACHE =
            new HashMap<Class<?>, Map<Class<?>, Method>>();

    /** Cache event bus subscriber methods for each class. */
    private static final Map<Class<?>, Map<Class<?>, Set<Method>>> SUBSCRIBERS_CACHE =
            new HashMap<Class<?>, Map<Class<?>, Set<Method>>>();

    @Override
    public Map<Class<?>, EventProducer> findAllProducers(Object listener) {
        final Class<?> listenerClass = listener.getClass();
        Map<Class<?>, EventProducer> handlersInMethod = new HashMap<Class<?>, EventProducer>();

        if (!PRODUCERS_CACHE.containsKey(listenerClass)) {
            loadProducers(listenerClass);
        }
        Map<Class<?>, Method> methods = PRODUCERS_CACHE.get(listenerClass);
        if (!methods.isEmpty()) {
            for (Map.Entry<Class<?>, Method> e : methods.entrySet()) {
                EventProducer producer = new EventProducer(listener, e.getValue());
                handlersInMethod.put(e.getKey(), producer);
            }
        }

        return handlersInMethod;
    }

    @Override
    public Map<Class<?>, Set<EventHandler>> findAllSubscribers(Object listener) {
        Class<?> listenerClass = listener.getClass();
        Map<Class<?>, Set<EventHandler>> handlersInMethod = new HashMap<Class<?>, Set<EventHandler>>();

        if (!SUBSCRIBERS_CACHE.containsKey(listenerClass)) {
            loadSubscribers(listenerClass);
        }
        Map<Class<?>, Set<Method>> methods = SUBSCRIBERS_CACHE.get(listenerClass);
        if (!methods.isEmpty()) {
            for (Map.Entry<Class<?>, Set<Method>> e : methods.entrySet()) {
                Set<EventHandler> handlers = new HashSet<EventHandler>();
                for (Method m : e.getValue()) {
                    handlers.add(new EventHandler(listener, m));
                }
                handlersInMethod.put(e.getKey(), handlers);
            }
        }

        return handlersInMethod;
    }

    private static boolean isSubscriberMethod(Method m){
        if(!m.getName().equals("onEvent"))return false;
        Class param = m.getParameterTypes()[0];
        if(!Event.class.isAssignableFrom(param) || param.equals(Event.class))return false;
        return true;
    }

    private static boolean isSubscriberClass(Class clazz){
        if(clazz.equals(Subscriber.class) || Subscriber.class.isAssignableFrom(clazz))return true;
        return false;
    }

    private static void loadSubscribers(Class<?> listenerClass) {
        Map<Class<?>, Set<Method>> subscriberMethods = new HashMap<Class<?>, Set<Method>>();

        if(!isSubscriberClass(listenerClass))return; // no need checking this for each method...

        for (Method method : listenerClass.getDeclaredMethods()) {
            if (isSubscriberMethod(method)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new IllegalArgumentException("Method " + method + " has @Subscribe annotation but requires "
                            + parameterTypes.length + " arguments.  Methods must require a single argument.");
                }

                Class<?> eventType = parameterTypes[0];
                if (eventType.isInterface()) {
                    throw new IllegalArgumentException("Method " + method + " has @Subscribe annotation on " + eventType
                            + " which is an interface.  Subscription must be on a concrete class type.");
                }

                if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
                    throw new IllegalArgumentException("Method " + method + " has @Subscribe annotation on " + eventType
                            + " but is not 'public'.");
                }

                System.out.println("EventType: " + eventType);
                Set<Method> methods = subscriberMethods.get(eventType);
                if (methods == null) {
                    methods = new HashSet<Method>();
                    subscriberMethods.put(eventType, methods);
                }
                methods.add(method);
            }
        }

        SUBSCRIBERS_CACHE.put(listenerClass, subscriberMethods);
    }

    private static void loadProducers(Class<?> listenerClass) {
        Map<Class<?>, Method> producerMethods = new HashMap<Class<?>, Method>();

        for (Method method : listenerClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Produce.class)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 0) {
                    throw new IllegalArgumentException("Method " + method + "has @Produce annotation but requires "
                            + parameterTypes.length + " arguments.  Methods must require zero arguments.");
                }
                if (method.getReturnType() == Void.class) {
                    throw new IllegalArgumentException("Method " + method
                            + " has a return type of void.  Must declare a non-void type.");
                }

                Class<?> eventType = method.getReturnType();
                if (eventType.isInterface()) {
                    throw new IllegalArgumentException("Method " + method + " has @Produce annotation on " + eventType
                            + " which is an interface.  Producers must return a concrete class type.");
                }
                if (eventType.equals(Void.TYPE)) {
                    throw new IllegalArgumentException("Method " + method + " has @Produce annotation but has no return type.");
                }

                if ((method.getModifiers() & Modifier.PUBLIC) == 0) {
                    throw new IllegalArgumentException("Method " + method + " has @Produce annotation on " + eventType
                            + " but is not 'public'.");
                }

                if (producerMethods.containsKey(eventType)) {
                    throw new IllegalArgumentException("Producer for type " + eventType + " has already been registered.");
                }
                producerMethods.put(eventType, method);
            }
        }

        PRODUCERS_CACHE.put(listenerClass, producerMethods);
    }
}
