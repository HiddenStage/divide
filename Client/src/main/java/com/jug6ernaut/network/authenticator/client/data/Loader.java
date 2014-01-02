package com.jug6ernaut.network.authenticator.client.data;

import com.jug6ernaut.network.authenticator.client.BackendObject;
import com.jug6ernaut.network.authenticator.client.EmptyCallback;
import com.jug6ernaut.network.authenticator.client.data.DataManager;
import com.jug6ernaut.network.shared.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by williamwebb on 11/25/13.
 */
public class Loader<B extends BackendObject> {

    private DataManager dataManager;
    private String[] keys;
    private Class<B> type;

    protected Loader(DataManager dataManager, Class<B> type, String... keys) {
        this.dataManager = dataManager;
        this.type = type;
        this.keys = keys;
    }

    public Collection<B> now() {
        return dataManager.get(type,Arrays.asList(keys));
    }

    public void async(retrofit.Callback<Collection<B>>... callbacks) {
        int length = callbacks.length;
        retrofit.Callback<Collection<B>> callback;
        if(callbacks == null || callbacks.length == 0){
            callback = new EmptyCallback<Collection<B>>();
        } else
        if(length == 1){
            callback = ObjectUtils.get1stOrNull(callbacks);
        } else {
            throw new RuntimeException("Only 1 callback allowed");
        }

        dataManager.get(type, Arrays.asList(keys), callback);
    }

}
