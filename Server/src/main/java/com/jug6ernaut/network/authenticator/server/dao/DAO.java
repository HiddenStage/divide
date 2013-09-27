package com.jug6ernaut.network.authenticator.server.dao;

import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import com.jug6ernaut.network.shared.web.transitory.query.Query;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/5/13
 * Time: 4:39 PM
 */
public interface DAO {
    public List<TransientObject> query(Query query) throws DAOException; // Object, returns different types
    public Collection<TransientObject> get(TransientObject... keys) throws DAOException;
    public void save(TransientObject... objects) throws DAOException;
    public void delete(TransientObject... objects) throws DAOException;
    public boolean exists(TransientObject... objects);
    public int count(String objectType);

    public static class DAOException extends Exception{
        int httpStatusCode = -1;
        Object entity;
        public DAOException(int httpStatusCode, Object entity){
            this.httpStatusCode = httpStatusCode;
            this.entity = entity;
        }

        public int getStatusCode(){
            return httpStatusCode;
        }

        public Object getEntity(){
            return entity;
        }
    }
}
