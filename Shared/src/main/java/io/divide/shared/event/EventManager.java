/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.divide.shared.event;

import io.divide.otto.Bus;
import io.divide.otto.SubscriberHandlerFinder;
import io.divide.otto.ThreadEnforcer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventManager {

    private static Map<String,EventManager> busMap = new ConcurrentHashMap<String, EventManager>();
    private Bus eventBus = new Bus(ThreadEnforcer.ANY);

    public static EventManager get(){
        return EventManager.get("default");
    }

    public static EventManager get(String identifier){
        EventManager bus = busMap.get(identifier);
        if(bus == null){
            bus = new EventManager(identifier);
            busMap.put(identifier,bus);
        }
        return bus;
    }

    private EventManager(String identifier){
        eventBus = new Bus(ThreadEnforcer.ANY,identifier, new SubscriberHandlerFinder());
    }


    public void fire(Event event){
        eventBus.post(event);
    }

    public <E extends Event> void register(Subscriber<E> subscriber){
        eventBus.register(subscriber);
    }

    public <S extends Subscriber> void unregister(S subscriber){
        eventBus.unregister(subscriber);
    }

}
