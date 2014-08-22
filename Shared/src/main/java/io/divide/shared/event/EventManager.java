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
