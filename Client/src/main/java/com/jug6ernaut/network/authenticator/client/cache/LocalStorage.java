package com.jug6ernaut.network.authenticator.client.cache;

import com.jug6ernaut.network.authenticator.client.BackendObject;
import com.jug6ernaut.network.shared.web.transitory.query.Query;

import java.util.Collection;
import java.util.List;

/**
 * Created by williamwebb on 11/2/13.
 */
public interface LocalStorage {

    public <B extends BackendObject> List<B> query(Class<B> type, Query<B> query);
    public <B extends BackendObject> List<B> getAllByType(Class<B> type);
    public <B extends BackendObject> void save(Collection<B> objects);
    public <B extends BackendObject> boolean exists(Class<B> type,String objectKey);
    public <B extends BackendObject> long count(Class<B> type);
}
