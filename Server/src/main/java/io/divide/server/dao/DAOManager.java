package io.divide.server.dao;

import io.divide.dao.ServerDAO;
import io.divide.shared.event.Event;
import io.divide.shared.event.EventManager;
import io.divide.shared.transitory.TransientObject;
import io.divide.shared.transitory.query.Query;
import io.divide.shared.util.ObjectUtils;

import java.security.KeyPair;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/22/13
 * Time: 11:25 AM
 */
public final class DAOManager implements ServerDAO {

    public static final String ACTION_QUERY = "action_query";
    public static final String ACTION_GET = "action_get";
    public static final String ACTION_SAVE = "action_save";
    public static final String ACTION_DELETE = "action_delete";
    public static final String ACTION_EXISTS = "action_exists";

    private EventManager eventManager = EventManager.get();
    private ServerDAO serverDao;

    public DAOManager(ServerDAO serverDao){
        this.serverDao = serverDao;
    }

//    @Override
//    public List<TransientObject> query(Query query) throws DAOException {
//        List<TransientObject> results = serverDao.query(query);
//        eventManager.fire(new QUERY_EVENT(results));
//        return results;
//    }

    @Override
    public <O extends TransientObject> Collection<O> get(String objectType, String... keys) throws DAOException {
        Collection<O> results = serverDao.get(objectType,keys);
        eventManager.fire(new GET_EVENT<O>(results));
        return results;
    }

    @Override
    public <O extends TransientObject> List<O> query(Query query) throws DAOException {
        List<O> results = serverDao.query(query);
        eventManager.fire(new QUERY_EVENT<O>(results));
        return results;
    }

    @Override
    public void save(TransientObject... objects) throws DAOException {
        serverDao.save(objects);
        eventManager.fire(new SAVE_EVENT(objects));
    }

    @Override
    public void delete(TransientObject... objects) throws DAOException {
        serverDao.delete(objects);
        eventManager.fire(new DELETE_EVENT(objects));
    }

    @Override
    public boolean exists(TransientObject... objects) {
        boolean exists = serverDao.exists(objects);
        eventManager.fire(new EXISTS_EVENT(objects));
        return exists;
    }

    @Override
    public int count(String objectType) {
        return serverDao.count(objectType);
    }

    @Override
    public KeyPair keys(KeyPair keys) {
        return serverDao.keys(keys);
    }

    public static final class QUERY_EVENT<T extends TransientObject> extends Event {
        Collection<T> transientObjects;
        protected QUERY_EVENT(Collection<T> transientObjects) {
            super(DAOManager.class);
            this.transientObjects = transientObjects;
        }
    }

    public static final class GET_EVENT<T extends TransientObject> extends Event {
        Collection<T> transientObjects;
        protected GET_EVENT(Collection<T> transientObjects) {
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
