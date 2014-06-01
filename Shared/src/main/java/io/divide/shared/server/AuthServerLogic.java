package io.divide.shared.server;

import io.divide.shared.util.AuthTokenUtils;
import io.divide.shared.util.ObjectUtils;
import io.divide.shared.util.ReflectionUtils;
import io.divide.shared.transitory.Credentials;
import io.divide.shared.transitory.TransientObject;
import io.divide.shared.transitory.query.OPERAND;
import io.divide.shared.transitory.query.Query;
import io.divide.shared.transitory.query.QueryBuilder;
import org.apache.http.HttpStatus;
import org.mindrot.jbcrypt.BCrypt;

import javax.security.sasl.AuthenticationException;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import static io.divide.shared.server.DAO.DAOException;

/**
 * Created by williamwebb on 4/11/14.
 */
public class AuthServerLogic<DAOOut extends TransientObject> extends ServerLogic<DAOOut> {

    private static Calendar c = Calendar.getInstance(TimeZone.getDefault());

    private KeyManager keyManager;

    public AuthServerLogic(DAO<TransientObject,DAOOut> dao, KeyManager keyManager) {
        super(dao);
        this.keyManager = keyManager;
    }

    /*
     * Saves user credentials
     */

    public Credentials userSignUp(Credentials credentials) throws DAOException{
            if (getUserByEmail(dao,credentials.getEmailAddress())!=null){
                throw new DAOException(HttpStatus.SC_CONFLICT,"User Already Exists");
            }
            ServerCredentials toSave = new ServerCredentials(credentials);

            toSave.decryptPassword(keyManager.getPrivateKey()); //decrypt the password
            String de = toSave.getPassword();
            String ha = BCrypt.hashpw(de, BCrypt.gensalt(10));

            toSave.setPassword(ha); //hash the password for storage
            toSave.setAuthToken(AuthTokenUtils.getNewToken(keyManager.getSymmetricKey(), toSave));
            toSave.setRecoveryToken(AuthTokenUtils.getNewToken(keyManager.getSymmetricKey(), toSave));
            toSave.setOwnerId(dao.count(Credentials.class.getName()) + 1);

            dao.save(toSave);

            return null;
    }

    /**
     * Checks username/password against that stored in DB, if same return
     * token, if token expired create new.
     * @param credentials
     * @return authentication token
     */

    public Credentials userSignIn(Credentials credentials) throws DAOException {
            Credentials dbCreds = getUserByEmail(dao,credentials.getEmailAddress());
            if (dbCreds == null){
                throw new DAOException(HttpStatus.SC_UNAUTHORIZED,"User Doesnt exist");
            }
            else {
                //check if we are resetting the password
                if(dbCreds.getValidation()!=null && dbCreds.getValidation().equals(credentials.getValidation())){
                    credentials.decryptPassword(keyManager.getPrivateKey()); //decrypt the password
                    dbCreds.setPassword(BCrypt.hashpw(credentials.getPassword(), BCrypt.gensalt(10))); //set the new password
                }
                //else check password
                else {
                    String en = credentials.getPassword();
                    credentials.decryptPassword(keyManager.getPrivateKey()); //decrypt the password
                    String de = credentials.getPassword();
                    String ha = BCrypt.hashpw(de, BCrypt.gensalt(10));
                    System.out.println("Comparing passwords.\n" +
                            "Encrypted: " + en + "\n" +
                            "Decrypted: " + de + "\n" +
                            "Hashed:    " + ha + "\n" +
                            "Stored:    " + dbCreds.getPassword());
                    if (!BCrypt.checkpw(de, dbCreds.getPassword())){
                        throw new DAOException(HttpStatus.SC_UNAUTHORIZED,"User Already Exists");
                    }
                }

//              check if token is expired, if so return/set new
                AuthTokenUtils.AuthToken token;
                try {
                    token = new AuthTokenUtils.AuthToken(keyManager.getSymmetricKey(),dbCreds.getAuthToken());
                } catch (AuthenticationException e) {
                    throw new DAOException(HttpStatus.SC_INTERNAL_SERVER_ERROR,"internal error");
                }
                if (c.getTime().getTime() > token.expirationDate) {
                    dbCreds.setAuthToken(AuthTokenUtils.getNewToken(keyManager.getSymmetricKey(), dbCreds));
                    dao.save(dbCreds);
                }

                return dbCreds;
            }
    }

    public byte[] getPublicKey()  {
        PublicKey publicKey = keyManager.getPublicKey();
        return publicKey.getEncoded();
    }

    /**
     * Validate a user account
     * @param token
     */

    public boolean validateAccount(String token) throws DAOException {
            Query q = new QueryBuilder().select().from(Credentials.class).where("validation", OPERAND.EQ, token).build();

            TransientObject to = ObjectUtils.get1stOrNull(dao.query(q));
            if (to != null) {
                ServerCredentials creds = new ServerCredentials(to);
                creds.setValidation("1");
                dao.save(creds);
                return true;
            } else {
                return false;
            }

    }

    public Credentials getUserFromAuthToken(String token) throws DAOException {

        AuthTokenUtils.AuthToken authToken = null;
        try {
            authToken = new AuthTokenUtils.AuthToken(keyManager.getSymmetricKey(),token);
        } catch (AuthenticationException e) {
            throw new DAOException(HttpStatus.SC_INTERNAL_SERVER_ERROR,"internal error");
        }
        if(authToken.isExpired()) throw new DAOException(HttpStatus.SC_UNAUTHORIZED,"Expired");

            Query q = new QueryBuilder().select().from(Credentials.class).where(Credentials.AUTH_TOKEN_KEY,OPERAND.EQ,token).build();

            TransientObject to = ObjectUtils.get1stOrNull(dao.query(q));
            if(to!=null){
                ServerCredentials sc = new ServerCredentials(to);
                return sc;
            } else {
                throw new DAOException(HttpStatus.SC_BAD_REQUEST,"invalid auth token");
            }
    }

    public Credentials getUserFromRecoveryToken(String token) throws DAOException {
        Query q = new QueryBuilder().select().from(Credentials.class).where(Credentials.RECOVERY_TOKEN_KEY,OPERAND.EQ,token).build();

        TransientObject to = ObjectUtils.get1stOrNull(dao.query(q));
        if(to!=null){
            ServerCredentials sc = new ServerCredentials(to);
            sc.setAuthToken(AuthTokenUtils.getNewToken(keyManager.getSymmetricKey(), sc));
            sc.setRecoveryToken(AuthTokenUtils.getNewToken(keyManager.getSymmetricKey(), sc));
            dao.save(sc);
            return sc;
        } else {
            throw new DAOException(HttpStatus.SC_BAD_REQUEST,"invalid recovery token");
        }
    }

////    @POST
////    @Path("/reset")
////    @Consumes(MediaType.APPLICATION_JSON)
////    @Produces(MediaType.APPLICATION_JSON)
////    public Response resetAccount(EncryptedEntity encrypted) {
////        try{
////            String email = encrypted.getPlainText(getKeys().getPrivate());
////            Query q = new QueryBuilder().select(null).from(Credentials.class).where("emailAddress", OPERAND.EQ, email).limit(1).build();
////
////            TransientObject to = (TransientObject) ObjectUtils.get1stOrNull(dao.query(q));
////            if (to != null) {
////                ServerCredentials creds = new ServerCredentials(to);
////                creds.setValidation(getNewAuthToken());
////                dao.save(creds);
////
////                EmailMessage emailMessage = new EmailMessage(
////                        "someEmail",
////                        email,
////                        "Tactics Password Reset",
////                        "some link " + creds.getAuthToken());
////
////                sendEmail(emailMessage);
////
////                return Response
////                        .ok()
////                        .build();
////            } else {
////                return Response
////                        .status(Status.NOT_FOUND)
////                        .build();
////            }
////        } catch (NoSuchAlgorithmException e) {
////            return Response.serverError().build();
////        } catch (DAO.DAOException e) {
////            return fromDAOExpection(e);
////        } catch (MessagingException e) {
////            return errorResponse(e);
////        } catch (UnsupportedEncodingException e) {
////            return errorResponse(e);
////        }
////    }

    public void recieveUserData(Credentials user, Map data) throws DAOException {
        user.removeAll();
        user.putAll(data);
        dao.save(user);
    }

//    public Response sendUserData(@Context Session session) {
//        try{
//            return ok(session.getUser());
//        }catch (Exception e) {
//            return errorResponse(e);
//        }
//    }
//
//    private static Response errorResponse(Throwable error){
//        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build();
//    }

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

    public Credentials getUserByEmail(DAO<TransientObject,DAOOut> serverDao, String email) throws DAOException {
        Query query = new QueryBuilder()
                .select()
                .from(Credentials.class)
                .where(Credentials.EMAIL_KEY,OPERAND.EQ,email)
                .build();

        TransientObject to = ObjectUtils.get1stOrNull(serverDao.query(query));
        if(to==null){
            return null;
        } else {
            return new ServerCredentials(to);
        }
    }

    public Credentials getUserById(DAO<TransientObject,DAOOut> serverDao, String userId) throws DAOException {
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

    private static class ServerCredentials extends Credentials {

        public ServerCredentials(TransientObject serverObject){
            try {
                Map meta = (Map) ReflectionUtils.getObjectField(serverObject, TransientObject.META_DATA);
                Map user = (Map) ReflectionUtils.getObjectField(serverObject,TransientObject.USER_DATA);

                ReflectionUtils.setObjectField(this, TransientObject.META_DATA, meta);
                ReflectionUtils.setObjectField(this, TransientObject.USER_DATA, user);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void setOwnerId(Integer id){
            super.setOwnerId(id);
        }

    }
}
