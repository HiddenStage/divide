/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
