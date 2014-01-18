package com.jug6ernaut.network.authenticator.server.endpoints;

import com.google.common.base.Stopwatch;
import com.jug6ernaut.network.authenticator.server.auth.KeyManager;
import com.jug6ernaut.network.authenticator.server.auth.UserContext;
import com.jug6ernaut.network.authenticator.server.dao.DAO;
import com.jug6ernaut.network.authenticator.server.dao.DAOManager;
import com.jug6ernaut.network.authenticator.server.dao.ServerCredentials;
import com.jug6ernaut.network.authenticator.server.dao.Session;
import com.jug6ernaut.network.shared.util.AuthTokenUtils;
import com.jug6ernaut.network.shared.util.ObjectUtils;
import com.jug6ernaut.network.shared.web.transitory.Credentials;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import com.jug6ernaut.network.shared.web.transitory.query.OPERAND;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import com.jug6ernaut.network.shared.web.transitory.query.QueryBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mindrot.jbcrypt.BCrypt;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Logger;

import static com.jug6ernaut.network.authenticator.server.utils.ResponseUtils.*;
import static javax.ws.rs.core.Response.Status;

@Path("/auth")
public final class AuthenticationEndpoint{
    Logger logger = Logger.getLogger(AuthenticationEndpoint.class.getName());

    @Context
    DAOManager dao;

    @Context
    KeyManager keyManager;

    public static final String key = "somekey";

    private static Calendar c = Calendar.getInstance(TimeZone.getDefault());

    /*
     * Saves user credentials
     */

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userSignUp(@Context ContainerRequestContext context, Credentials credentials) {
        try{
            if (getUserByEmail(dao,credentials.getEmailAddress())!=null){
                return Response
                        .status(Status.CONFLICT)
                        .build();
            }
            ServerCredentials toSave = new ServerCredentials(credentials);

            String en = toSave.getPassword();
            toSave.decryptPassword(keyManager.getPrivateKey()); //decrypt the password
            String de = toSave.getPassword();
            String ha = BCrypt.hashpw(de, BCrypt.gensalt(10));
            logger.info("PW\n" +
                        "Encrypted: " + en + "\n" +
                        "Decrypted: " + de + "\n" +
                        "Hashed:    " + ha);
            toSave.setPassword(ha); //hash the password for storage
            toSave.setAuthToken(AuthTokenUtils.getToken(key, toSave));
            toSave.setRecoveryToken(AuthTokenUtils.getToken(key, toSave));
            toSave.setOwnerId(dao.count(Credentials.class.getName()) + 1);

//            session.setup(new UserContext(null,toSave));
            context.setSecurityContext(new UserContext(context.getUriInfo(),toSave));
            dao.save(toSave);

            toSave.setPassword("");

            logger.info("SignUp Successful. Returning: " + toSave);
            return ok(toSave);
        } catch (DAO.DAOException e) {
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
            Credentials dbCreds = getUserByEmail(dao,credentials.getEmailAddress());
            if (dbCreds == null){
                logger.warning("User not found(" + credentials.getEmailAddress() + ")");
                return notAuthReponse("User not found("+credentials.getEmailAddress()+")");
            }
            else {
                //check if we are resetting the password
                if(dbCreds.getValidation()!=null && dbCreds.getValidation().equals(credentials.getValidation())){
                    logger.info("Validated, setting new password");
                    credentials.decryptPassword(keyManager.getPrivateKey()); //decrypt the password
                    dbCreds.setPassword(BCrypt.hashpw(credentials.getPassword(), BCrypt.gensalt(10))); //set the new password
                }
                //else check password
                else {
                    String en = credentials.getPassword();
                    credentials.decryptPassword(keyManager.getPrivateKey()); //decrypt the password
                    String de = credentials.getPassword();
                    String ha = BCrypt.hashpw(de, BCrypt.gensalt(10));
                    logger.info("Comparing passwords.\n" +
                            "Encrypted: " + en + "\n" +
                            "Decrypted: " + de + "\n" +
                            "Hashed:    " + ha + "\n" +
                            "Stored:    " + dbCreds.getPassword());

                    if (!BCrypt.checkpw(de, dbCreds.getPassword())){
                        logger.warning("Password missmatch");
                        return notAuthReponse("Password missmatch");
                    }
                }

                context.setSecurityContext(new UserContext(context.getUriInfo(),dbCreds));
//                session.
//                // check if token is expired, if so return/set new
                AuthTokenUtils.AuthToken token = new AuthTokenUtils.AuthToken(key,dbCreds.getAuthToken());
                if (c.getTime().getTime() > token.expirationDate) {
                    logger.info("Updating ExpireDate");
                    dbCreds.setAuthToken(AuthTokenUtils.getToken(key, dbCreds));
                    dao.save(dbCreds);
                }

                logger.info("Login Successful. Returning: " + dbCreds);
                return ok(dbCreds);
            }
        }catch (DAO.DAOException e) {
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
        PublicKey publicKey = keyManager.getPublicKey();

        return Response
                .ok()
                .entity(publicKey.getEncoded())
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
            Query q = new QueryBuilder().select(null).from(Credentials.class).where("validation", OPERAND.EQ, token).build();

            TransientObject to = ObjectUtils.get1stOrNull(dao.query(q));
            if (to != null) {
                ServerCredentials creds = new ServerCredentials(to);
                creds.setValidation("1");
                dao.save(creds);
                return ok(creds);
            } else {
                return Response
                        .status(Status.NOT_FOUND)
                        .build();
            }

        }catch (DAO.DAOException e) {
            return fromDAOExpection(e);
        }
    }

    @GET
    @Path("/from/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserFromToken(@Context ContainerRequestContext context, @PathParam("token") String token) {
        try{
            Query q = new QueryBuilder().select().from(Credentials.class).where(Credentials.AUTH_TOKEN_KEY,OPERAND.EQ,token).build();

            TransientObject to = ObjectUtils.get1stOrNull(dao.query(q));
            if(to!=null){
                ServerCredentials sc = new ServerCredentials(to);
                logger.info("Successfully gotUserFromToken: " + sc);
                context.setSecurityContext(new UserContext(context.getUriInfo(),sc));
                return ok(sc);
            } else {
                return Response.status(Status.NOT_FOUND).entity("whoopsie...").build();
            }
        }catch (DAO.DAOException e) {
            return fromDAOExpection(e);
        }
    }

    @GET
    @Path("/recover/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response recoverFromOneTimeToken(@Context ContainerRequestContext context, @PathParam("token") String token) {
        try{
            Query q = new QueryBuilder().select().from(Credentials.class).where(Credentials.RECOVERY_TOKEN_KEY,OPERAND.EQ,token).build();

            TransientObject to = ObjectUtils.get1stOrNull(dao.query(q));
            if(to!=null){
                ServerCredentials sc = new ServerCredentials(to);
                logger.info("Successfully gotUserFromToken: " + sc);
                context.setSecurityContext(new UserContext(context.getUriInfo(),sc));
                sc.setRecoveryToken(AuthTokenUtils.getToken(key, sc));
                Response r = Response.ok().header("1tk", sc.getRecoveryToken()).entity(sc).build();
                return r;
            } else {
                return Response.status(Status.NOT_FOUND).entity("whoopsie...").build();
            }
        }catch (DAO.DAOException e) {
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
    @Path("/user/data")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response recieveUserData(@Context Session session, Credentials credentials) {
        try{
            Credentials user = session.getUser();
            user.removeAll();
            user.putAll(credentials.getUserData());
            dao.save(user);
            return Response.ok().build();
        } catch (DAO.DAOException e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
            return fromDAOExpection(e);
        }
    }

    @GET
    @Path("/user/data")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendUserData(@Context Session session) {
        try{
            return ok(session.getUser());
        }catch (Exception e) {
            return errorResponse(e);
        }
    }

    private static Response errorResponse(Throwable error){
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build();
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

    public static Credentials getUserByEmail(DAO dao, String email) throws DAO.DAOException {
        Query query = new QueryBuilder()
                .select()
                .from(Credentials.class)
                .where(Credentials.EMAIL_KEY,OPERAND.EQ,email)
                .build();

        TransientObject to = ObjectUtils.get1stOrNull(dao.query(query));
        if(to==null){
            return null;
        } else {
            return new ServerCredentials(to);
        }
    }

    public static Credentials getUserById(DAO dao, String userId) throws DAO.DAOException {
        Query query = new QueryBuilder()
                .select()
                .from(Credentials.class)
                .where(Credentials.OWNER_ID_KEY,OPERAND.EQ,userId)
                .build();

        TransientObject to = ObjectUtils.get1stOrNull(dao.query(query));
        if(to==null){
            return null;
        } else {
            return new ServerCredentials(to);
        }
    }

    private String doHash(String plainText, int interations){
        Stopwatch s = new Stopwatch();String salt = BCrypt.gensalt(20);
        for(int x=0;x<interations;x++){
            plainText = BCrypt.hashpw(plainText,salt);
        }
        return plainText;
    }

}
