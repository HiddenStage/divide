package io.divide.dao;

import io.divide.shared.web.transitory.TransientObject;
import io.divide.shared.web.transitory.query.Query;

import java.security.KeyPair;
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
    public Collection<TransientObject> get(String type, String... keys) throws DAOException;
    public void save(TransientObject... objects) throws DAOException;
    public void delete(TransientObject... objects) throws DAOException;
    public boolean exists(TransientObject... objects);
    public int count(String objectType);
    public KeyPair keys(KeyPair keys);

    public static class DAOException extends Exception{
        int httpStatusCode = -1;
        public DAOException(int httpStatusCode, Throwable thrown){
            super(thrown);
            this.httpStatusCode = httpStatusCode;
        }

        public int getStatusCode(){
            return httpStatusCode;
        }

    }
}
