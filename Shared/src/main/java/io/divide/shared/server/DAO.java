package io.divide.shared.server;

import io.divide.shared.transitory.TransientObject;
import io.divide.shared.transitory.query.Query;

import java.util.Collection;
import java.util.List;

/**
 * Created by williamwebb on 4/11/14.
 */
public interface DAO<IN extends TransientObject, OUT extends TransientObject>  {
    public <O extends OUT> List<O> query(Query query) throws DAOException; // Object, returns different types
    public <O extends OUT> Collection<O> get(String type, String... keys) throws DAOException;
    public void save(IN... objects) throws DAOException;
    public void delete(IN... objects) throws DAOException;
    public boolean exists(IN... objects);
    public int count(String objectType);

    public static class DAOException extends RuntimeException{
        int httpStatusCode = -1;

        public DAOException(int statusCode, String cause){
            super(cause, null, true ,false);
            this.httpStatusCode = statusCode;
        }

        public DAOException(int httpStatusCode, Throwable thrown){
            super(thrown);
            this.httpStatusCode = httpStatusCode;
        }

        public int getStatusCode(){
            return httpStatusCode;
        }
    }
}
