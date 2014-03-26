package io.divide.shared.event;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/23/13
 * Time: 6:38 PM
 */
public abstract class AutoSubscriber implements Subscriber{

    protected AutoSubscriber(){
        EventManager.get().register(this);
    }

}
