package com.jug6ernaut.network.shared.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jug6ernaut.network.shared.util.ListenerManager.Listener;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/16/13
 * Time: 9:19 PM
 */
public class ListenerManager<L extends Listener,Type> {

//    private static final Map<Class<?>,List<Listener<?>>> listenerMap = new ConcurrentHashMap<Class<?>, List<Listener<?>>>();
    private static final List<Listener<?>> listenerMap = new ArrayList<Listener<?>>();
    ExecutorService executor = Executors.newFixedThreadPool(4);

    public ListenerManager(){

    }

    public synchronized void addListener(L listener){
        listenerMap.add(listener);
    }

    public synchronized void removeListener(L listener){
        listenerMap.remove(listener);
    }

    public synchronized void fire(final Type... object){
        executor.submit(new Runnable() {
            @Override
            public void run() {
                for(Listener n : listenerMap){
                    if(n.condition(object))n.onEvent(object);
                }
            }
        });
//        new Thread(){
//            @Override
//            public void run(){
//
//            }
//        }.start();
    }

    public static abstract class Listener<Type>{
        private Listener listener;
        protected Listener(){listener = this;}
        public abstract void onEvent(Type... object);
        protected abstract boolean condition(Type... object);

        public Class<?> getType() {
            Field notifierField = null;
            try {
                notifierField = this.getClass().getDeclaredField("listener");
                ParameterizedType stringListType = (ParameterizedType) notifierField.getGenericType();
                Class<?> theType = (Class<?>) stringListType.getActualTypeArguments()[0];
                return theType;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return Object.class;
            }
        }
    }

}
