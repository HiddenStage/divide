package io.divide.server.utils;

import io.divide.dao.ServerDAO;
import io.divide.shared.transitory.Credentials;

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

    public static Response fromDAOExpection(ServerDAO.DAOException exception){
        return Response
                .status(exception.getStatusCode())
                .build();
    }

    public static Response ok(Credentials credentials){
        return Response
                .ok()
                .entity(credentials)
                .build();
    }

    public static Response errorResponse(Throwable error){
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }
}
