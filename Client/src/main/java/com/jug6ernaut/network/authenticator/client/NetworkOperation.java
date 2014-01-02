package com.jug6ernaut.network.authenticator.client;

import com.jug6ernaut.network.shared.web.transitory.TransientObject;

import java.util.Collection;

/**
 * Created by williamwebb on 11/25/13.
 */
public interface NetworkOperation<T extends TransientObject> {
    public Collection<T> now();
    public void async(retrofit.Callback<Collection<T>>... callbacks);
}
