package com.jug6ernaut.network.authenticator.server.endpoints;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.jug6ernaut.network.authenticator.server.dao.DAO;
import com.jug6ernaut.network.authenticator.server.dao.Session;
import com.jug6ernaut.network.shared.Constants;
import com.jug6ernaut.network.shared.util.Crypto;
import com.jug6ernaut.network.shared.web.transitory.Credentials;
import com.jug6ernaut.network.shared.web.transitory.EncryptedEntity;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Logger;

import static com.jug6ernaut.network.authenticator.server.utils.ResponseUtils.fromDAOExpection;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/10/13
 * Time: 7:04 PM
 */
@Path("/push")
public class PushEndpoint {

    Logger logger = Logger.getLogger(PushEndpoint.class.getName());

    @Context
    DAO dao;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(@Context Session session,EncryptedEntity entity){
        try{
            Credentials credentials = session.getUser();
            String token = entity.getPlainText(Crypto.get().getPrivateKey());
            credentials.setPushMessagingKey(token);
            dao.save(credentials);
        } catch (DAO.DAOException e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
            return fromDAOExpection(e);
        } catch (NoSuchAlgorithmException e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @DELETE
    public Response unregister(@Context Session session){
        try{
            Credentials credentials = session.getUser();
            credentials.setPushMessagingKey("");
            dao.save(credentials);
        } catch (DAO.DAOException e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
            return fromDAOExpection(e);
        }
        return Response.ok().build();
    }

    @GET
    @Path("/test/{email}/{data}")
    @Produces(MediaType.TEXT_HTML)
    public Response pushToDevice(@PathParam("email")String userId, @PathParam("data")String data){

        try {
            String result = sendMessageToDevice(userId,data);
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    private String sendMessageToDevice(String email, String input) throws DAO.DAOException, IOException {

        Credentials user = getUserByEmail(dao,email);

        Sender sender = new Sender(Constants.PUSH_KEY);
        Message message = new Message.Builder().addData("body", input).build();

        MulticastResult result = sender.send(message, Arrays.asList(user.getPushMessagingKey()), 5);

        System.out.println("Result = " + result);
        return result.toString();
    }

    private Credentials getUserByEmail(DAO dao, String email) throws DAO.DAOException {
        return AuthenticationEndpoint.getUserByEmail(dao,email);
    }

    private Credentials getUserById(DAO dao, String userId) throws DAO.DAOException {
        return AuthenticationEndpoint.getUserById(dao,userId);
    }

}
