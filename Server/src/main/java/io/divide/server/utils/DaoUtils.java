package io.divide.server.utils;

import io.divide.dao.ServerDAO;
import io.divide.server.dao.ServerCredentials;
import io.divide.shared.util.ObjectUtils;
import io.divide.shared.transitory.Credentials;
import io.divide.shared.transitory.TransientObject;
import io.divide.shared.transitory.query.OPERAND;
import io.divide.shared.transitory.query.Query;
import io.divide.shared.transitory.query.QueryBuilder;

/**
 * Created by williamwebb on 5/31/14.
 */
public class DaoUtils {

    /**
     * Convience method to do a Credentials query against an email address
     * @param serverDao dao object to query against.
     * @param email email address used in query.
     * @return Credentials object found or null.
     * @throws ServerDAO.DAOException
     */
    public static Credentials getUserByEmail(ServerDAO serverDao, String email) throws ServerDAO.DAOException {
        Query query = new QueryBuilder()
                .select()
                .from(Credentials.class)
                .where(Credentials.EMAIL_KEY, OPERAND.EQ,email)
                .build();

        TransientObject to = ObjectUtils.get1stOrNull(serverDao.query(query));
        if(to==null){
            return null;
        } else {
            return new ServerCredentials(to);
        }
    }

    /**
     * Convience method to do a Credentials query against an user id.
     * @param serverDao dao object to query against.
     * @param userId user id used in query.
     * @return Credentials object found or null.
     * @throws ServerDAO.DAOException
     */
    public static Credentials getUserById(ServerDAO serverDao, String userId) throws ServerDAO.DAOException {
        Query query = new QueryBuilder()
                .select()
                .from(Credentials.class)
                .where(Credentials.OWNER_ID_KEY,OPERAND.EQ,userId)
                .build();

        TransientObject to = ObjectUtils.get1stOrNull(serverDao.query(query));
        if(to==null){
            return null;
        } else {
            return new ServerCredentials(to);
        }
    }
}
