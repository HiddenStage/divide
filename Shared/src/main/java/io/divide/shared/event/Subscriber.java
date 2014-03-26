package io.divide.shared.event;

import io.divide.otto.Subscribe;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/23/13
 * Time: 6:38 PM
 */
public interface Subscriber<E extends Event>{

    @Subscribe
    public void onEvent(final E event);

}
