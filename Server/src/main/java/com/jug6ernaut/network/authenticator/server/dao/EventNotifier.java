package com.jug6ernaut.network.authenticator.server.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/16/13
 * Time: 9:19 PM
 */
public class EventNotifier {

    private static final Map<Class<?>,List<Notifier<?>>> notifierMap = new ConcurrentHashMap<Class<?>, List<Notifier<?>>>();
//    private final static List<Notifier> notifiers = new ArrayList<Notifier>();

//    private static EventNotifier eventNotifier;
    public EventNotifier(){

    }

//    public static EventNotifier get(){
//        if(eventNotifier == null)eventNotifier = new EventNotifier();
//        return eventNotifier;
//    }

    public synchronized void addNotifier(Notifier notifier){
        List<Notifier<?>> x = notifierMap.get(notifier.getType());
        if(x==null)x=new ArrayList<Notifier<?>>(){};
        x.add(notifier);
        notifierMap.put(notifier.getType(),x);
    }

    public synchronized void removeNotifier(Notifier notifier){
        List<Notifier<?>> x = notifierMap.get(notifier.getType());
        if(x==null)return;
        else {
            x.remove(notifier);
        }
    }

    public synchronized <T> void fire(String action,T... object){
        List<Notifier<?>> x = notifierMap.get(object.getClass());
        if(x!=null){
            for(Notifier n : x){
                if(n.condition(action,object))n.onEvent(object);
            }
        }
    }

    public static abstract class Notifier<T>{
        private Class<T> type;
        protected Notifier(Class<T> type){
            this.type = type;
        }
        public abstract void onEvent(T... object);
        public abstract boolean condition(String action, T object);

        public final Class<T> getType(){
            return type;
        }
    }

}
