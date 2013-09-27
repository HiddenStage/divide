package com.jug6ernaut.network.authenticator.server.dao;

import com.jug6ernaut.network.shared.event.Event;
import com.jug6ernaut.network.shared.event.EventManager;
import com.jug6ernaut.network.shared.util.ObjectUtils;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import com.jug6ernaut.network.shared.web.transitory.query.Query;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/22/13
 * Time: 11:25 AM
 */
public final class DAOManager implements DAO {

    public static final String ACTION_QUERY = "action_query";
    public static final String ACTION_GET = "action_get";
    public static final String ACTION_SAVE = "action_save";
    public static final String ACTION_DELETE = "action_delete";
    public static final String ACTION_EXISTS = "action_exists";

    private EventManager eventManager = EventManager.get();
    private DAO dao;

    public DAOManager(DAO dao){
        this.dao = dao;
    }

    @Override
    public List<TransientObject> query(Query query) throws DAOException {
        List<TransientObject> results = dao.query(query);
        eventManager.fire(new QUERY_EVENT(results));
        return results;
    }

    @Override
    public Collection<TransientObject> get(TransientObject... keys) throws DAOException {
        Collection<TransientObject> results = dao.get(keys);
        eventManager.fire(new GET_EVENT(results));
        return results;
    }

    @Override
    public void save(TransientObject... objects) throws DAOException {
        dao.save(objects);
        eventManager.fire(new SAVE_EVENT(objects));
    }

    @Override
    public void delete(TransientObject... objects) throws DAOException {
        dao.delete(objects);
        eventManager.fire(new DELETE_EVENT(objects));
    }

    @Override
    public boolean exists(TransientObject... objects) {
        boolean exists = dao.exists(objects);
        eventManager.fire(new EXISTS_EVENT(objects));
        return exists;
    }

    @Override
    public int count(String objectType) {
        return dao.count(objectType);
    }

    public static final class QUERY_EVENT extends Event {
        Collection<TransientObject> transientObjects;
        protected QUERY_EVENT(Collection<TransientObject> transientObjects) {
            super(DAOManager.class);
            this.transientObjects = transientObjects;
        }
    }

    public static final class GET_EVENT extends Event {
        Collection<TransientObject> transientObjects;
        protected GET_EVENT(Collection<TransientObject> transientObjects) {
            super(DAOManager.class);
            this.transientObjects = transientObjects;
        }
    }

    public static final class SAVE_EVENT extends Event {
        Collection<TransientObject> transientObjects;
        protected SAVE_EVENT(TransientObject... object) {
            super(DAOManager.class);
            this.transientObjects = ObjectUtils.v2c(object);
        }
    }

    public static final class DELETE_EVENT extends Event {
        Collection<TransientObject> transientObjects;
        protected DELETE_EVENT(TransientObject... object) {
            super(DAOManager.class);
            this.transientObjects = ObjectUtils.v2c(object);
        }
    }

    public static final class EXISTS_EVENT extends Event {
        Collection<TransientObject> transientObjects;
        protected EXISTS_EVENT(TransientObject... object) {
            super(DAOManager.class);
            this.transientObjects = ObjectUtils.v2c(object);
        }
    }



}
