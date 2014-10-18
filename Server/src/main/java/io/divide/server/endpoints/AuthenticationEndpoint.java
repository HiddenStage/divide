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

package io.divide.server.endpoints;

import io.divide.dao.ServerDAO;
import io.divide.server.auth.SecManager;
import io.divide.server.auth.UserContext;
import io.divide.server.dao.DAOManager;
import io.divide.server.dao.Session;
import io.divide.shared.server.AuthServerLogic;
import io.divide.shared.transitory.Credentials;
import io.divide.shared.transitory.TransientObject;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import static io.divide.server.utils.ResponseUtils.*;
import static javax.ws.rs.core.Response.Status;

@Path("/auth")
public final class AuthenticationEndpoint{
    Logger logger = Logger.getLogger(AuthenticationEndpoint.class.getName());

    @Context DAOManager dao;
    @Context SecManager keyManager;

    private static Calendar c = Calendar.getInstance(TimeZone.getDefault());
    AuthServerLogic<TransientObject> authServerLogic;

    @PostConstruct
    public void init(){
        authServerLogic = new AuthServerLogic<TransientObject>(dao,keyManager);
    }

    /*
     * Saves user credentials
     */

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userSignUp(@Context ContainerRequestContext context, Credentials credentials) {
        try{
            Credentials toSave = authServerLogic.userSignUp(credentials);

            context.setSecurityContext(new UserContext(context.getUriInfo(),toSave));

            logger.info("SignUp Successful. Returning: " + toSave);
            return ok(toSave);
        } catch (ServerDAO.DAOException e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
            return fromDAOExpection(e);
        } catch (Exception e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
            return Response.serverError().build();
        }
    }

    /**
     * Checks username/password against that stored in DB, if same return
     * token, if token expired create new.
     * @param credentials
     * @return authentication token
     */

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userSignIn(@Context ContainerRequestContext context, Credentials credentials) {
        try{
            Credentials dbCreds = authServerLogic.userSignIn(credentials);

            context.setSecurityContext(new UserContext(context.getUriInfo(),dbCreds));

            logger.info("Login Successful. Returning: " + dbCreds);
            return ok(dbCreds);
        }catch (ServerDAO.DAOException e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
            return fromDAOExpection(e);
        } catch (Exception e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/key")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPublicKey()  {
        try{
        return Response
                .ok()
                .entity(authServerLogic.getPublicKey())
                .build();
        } catch (Exception e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
            return Response.serverError().build();
        }
    }

    /**
     * Validate a user account
     * @param token
     */

    @GET
    @Path("/validate/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateAccount(@PathParam("token") String token) {
        try{
            if (authServerLogic.validateAccount(token)) {
                return Response.ok().build();
            } else {
                return Response.status(Status.NOT_FOUND).build();
            }
        }catch (ServerDAO.DAOException e) {
            return fromDAOExpection(e);
        }
    }

    @GET
    @Path("/from/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserFromToken(@Context ContainerRequestContext context, @PathParam("token") String token) {
        try{
            logger.warning("getUserFromToken");
            Credentials user = authServerLogic.getUserFromAuthToken(token);
            context.setSecurityContext(new UserContext(context.getUriInfo(),user));
            return Response.ok(user).build();
        }catch (ServerDAO.DAOException e) {
            e.printStackTrace();
            logger.severe(ExceptionUtils.getStackTrace(e));
            return fromDAOExpection(e);
        }
    }

    @GET
    @Path("/recover/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response recoverFromOneTimeToken(@Context ContainerRequestContext context, @PathParam("token") String token) {
        try{
            Credentials user = authServerLogic.getUserFromRecoveryToken(token);
            context.setSecurityContext(new UserContext(context.getUriInfo(),user));
            return Response.ok(user).build();
        }catch (ServerDAO.DAOException e) {
            e.printStackTrace();
            logger.severe(ExceptionUtils.getStackTrace(e));
            return fromDAOExpection(e);
        }
    }

//    @POST
//    @Path("/reset")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response resetAccount(EncryptedEntity encrypted) {
//        try{
//            String email = encrypted.getPlainText(getKeys().getPrivate());
//            Query q = new QueryBuilder().select(null).from(Credentials.class).where("emailAddress", OPERAND.EQ, email).limit(1).build();
//
//            TransientObject to = (TransientObject) ObjectUtils.get1stOrNull(dao.query(q));
//            if (to != null) {
//                ServerCredentials creds = new ServerCredentials(to);
//                creds.setValidation(getNewAuthToken());
//                dao.save(creds);
//
//                EmailMessage emailMessage = new EmailMessage(
//                        "someEmail",
//                        email,
//                        "Tactics Password Reset",
//                        "some link " + creds.getAuthToken());
//
//                sendEmail(emailMessage);
//
//                return Response
//                        .ok()
//                        .build();
//            } else {
//                return Response
//                        .status(Status.NOT_FOUND)
//                        .build();
//            }
//        } catch (NoSuchAlgorithmException e) {
//            return Response.serverError().build();
//        } catch (DAO.DAOException e) {
//            return fromDAOExpection(e);
//        } catch (MessagingException e) {
//            return errorResponse(e);
//        } catch (UnsupportedEncodingException e) {
//            return errorResponse(e);
//        }
//    }

    @POST
    @Path("/user/data/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response recieveUserData(@Context Session session, @PathParam("userId") String userId, Map<String,?> data) {
        try{
            authServerLogic.recieveUserData(userId,data);
            return Response.ok().build();
        } catch (ServerDAO.DAOException e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
            return fromDAOExpection(e);
        }
    }

    @PUT
    @Path("/user/data/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendUserData(@Context Session session, @PathParam("userId") String userId) {
        try{
            return Response.ok(authServerLogic.sendUserData(userId)).build(); // ok(getUserById(dao,userId).getUserData());
        }catch (Exception e) {
            return errorResponse(e);
        }
    }

//    public void sendEmail(EmailMessage emailMessage) throws MessagingException, UnsupportedEncodingException {
//
//        Properties props = new Properties();
//        Session session = Session.getDefaultInstance(props, null);
//
//        Message msg = new MimeMessage(session);
//        msg.setFrom(new InternetAddress(emailMessage.getFrom(), ""));
//        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(emailMessage.getTo(), ""));
//        msg.setSubject(emailMessage.getSubject());
//        msg.setText(emailMessage.getBody());
//        Transport.send(msg);
//
//    }
//
//    public static class EmailMessage {
//        private String from;
//        private String to;
//        private String subject;
//        private String body;
//
//        public EmailMessage(String from, String to, String subject, String body) throws MessagingException {
//            setFrom(from);
//            setTo(to);
//            setSubject(subject);
//            setBody(body);
//        }
//
//        public String getFrom() {
//            return from;
//        }
//
//        public void setFrom(String from) throws MessagingException {
//            if (!validEmail(from)) throw new MessagingException("Invalid email address!");
//            this.from = from;
//        }
//
//        public String getTo() {
//            return to;
//        }
//
//        public void setTo(String to) throws MessagingException {
//            if (!validEmail(to)) throw new MessagingException("Invalid email address!");
//            this.to = to;
//        }
//
//        public String getSubject() {
//            return subject;
//        }
//
//        public void setSubject(String subject) {
//            this.subject = subject;
//        }
//
//        public String getBody() {
//            return body;
//        }
//
//        public void setBody(String body) {
//            this.body = body;
//        }
//
//        private boolean validEmail(String email) {
//            // editing to make requirements listed
//            // return email.matches("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}");
//            return email.matches("[A-Z0-9._%+-][A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{3}");
//        }
//    }

}
