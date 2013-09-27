package com.jug6ernaut.network.shared.event;

import com.jug6ernaut.otto.Bus;
import com.jug6ernaut.otto.SubscriberHandlerFinder;
import com.jug6ernaut.otto.ThreadEnforcer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/16/13
 * Time: 9:19 PM
 */
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
