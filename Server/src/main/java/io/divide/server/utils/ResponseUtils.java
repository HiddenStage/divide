package io.divide.server.utils;

import io.divide.dao.DAO;
import io.divide.shared.web.transitory.Credentials;

import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/4/13
 * Time: 6:30 PM
 */
public class ResponseUtils {

    public static Response notAuthReponse(String string){
        return Response
                .status(Response.Status.UNAUTHORIZED)
                .entity(string + "\r\n")
                .build();
    }

    public static Response fromDAOExpection(DAO.DAOException exception){
        return Response
                .status(exception.getStatusCode())
                .build();
    }

    public static Response ok(Credentials credentials){
        return Response
                .ok()
//                .header("CUSTOM",credentials.getAuthToken())
//                .cookie(new NewCookie(ContainerRequest.AUTHORIZATION,"CUSTOM " + credentials.getAuthToken()))
                .entity(credentials)
                .build();
    }
}
