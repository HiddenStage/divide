/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.divide.shared.server;

import io.divide.shared.transitory.TransientObject;
import io.divide.shared.transitory.query.Query;

import java.util.Collection;
import java.util.List;

public interface DAO<IN extends TransientObject, OUT extends TransientObject>  {
    public <O extends OUT> List<O> query(Query query) throws DAOException; // Object, returns different types
    public <O extends OUT> Collection<O> get(String type, String... keys) throws DAOException;
    public void save(IN... objects) throws DAOException;
    public void delete(IN... objects) throws DAOException;
    public boolean exists(IN... objects);
    public int count(String objectType);

    public static class DAOException extends RuntimeException{
        int httpStatusCode = -1;

        public DAOException(Throwable throwable){
            this(500,throwable);
        }

        public DAOException(int statusCode, String cause){
            super(cause);
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
