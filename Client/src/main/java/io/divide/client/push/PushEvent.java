package io.divide.client.push;

import io.divide.shared.event.Event;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/23/13
 * Time: 6:34 PM
 */
public class PushEvent extends Event {
    private String message;
    protected PushEvent(String message) {
        super(PushManager.class);
        this.message = message;
    }
}
