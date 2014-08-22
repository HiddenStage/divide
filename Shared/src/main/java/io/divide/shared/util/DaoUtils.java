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

package io.divide.shared.util;

import io.divide.shared.server.DAO;
import io.divide.shared.transitory.Credentials;
import io.divide.shared.transitory.TransientObject;
import io.divide.shared.transitory.query.OPERAND;
import io.divide.shared.transitory.query.Query;
import io.divide.shared.transitory.query.QueryBuilder;

import java.lang.reflect.Constructor;
import java.util.Map;

public class DaoUtils {

    /**
     * Convience method to do a Credentials query against an email address
     * @param serverDao dao object to query against.
     * @param email email address used in query.
     * @return Credentials object found or null.
     * @throws io.divide.shared.server.DAO.DAOException
     */
    public static Credentials getUserByEmail(DAO serverDao, String email) throws DAO.DAOException {
        Query query = new QueryBuilder()
                .select()
                .from(Credentials.class)
                .where(Credentials.EMAIL_KEY, OPERAND.EQ,email)
                .build();

        TransientObject to = (TransientObject) ObjectUtils.get1stOrNull(serverDao.query(query));
        if(to==null){
            return null;
        } else {
            return to(Credentials.class, to);
        }
    }

    /**
     * Convience method to do a Credentials query against an user id.
     * @param serverDao dao object to query against.
     * @param userId user id used in query.
     * @return Credentials object found or null.
     * @throws io.divide.shared.server.DAO.DAOException
     */
    public static Credentials getUserById(DAO serverDao, String userId) throws DAO.DAOException {
        Query query = new QueryBuilder()
                .select()
                .from(Credentials.class)
                .where(Credentials.OWNER_ID_KEY,OPERAND.EQ,userId)
                .build();

        TransientObject to = (TransientObject) ObjectUtils.get1stOrNull(serverDao.query(query));
        if(to==null){
            return null;
        } else {
            return to(Credentials.class,to);
        }
    }

    public static <T extends TransientObject> T to(Class<T> type, TransientObject from){
        try {
            Constructor<T> constructor;
            constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);

            T t = constructor.newInstance();
            Map meta = (Map) ReflectionUtils.getObjectField(from, TransientObject.META_DATA);
            Map user = (Map) ReflectionUtils.getObjectField(from,TransientObject.USER_DATA);

            ReflectionUtils.setObjectField(t, TransientObject.META_DATA, meta);
            ReflectionUtils.setObjectField(t, TransientObject.USER_DATA, user);

            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
