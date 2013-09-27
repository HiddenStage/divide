package com.jug6ernaut.network.shared.event;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/23/13
 * Time: 6:39 PM
 */
public abstract class Event {
    private Class<?> source;
    protected Event(Class<?> sourse){
        this.source = sourse;
    }

    public Class<?> getSource(){
        return source;
    }

    @Override
    public String toString() {
        return "Event{" +
                "source=" + source +
                '}';
    }
}
