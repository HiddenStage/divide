package com.jug6ernaut.network.shared.web.transitory;


import com.jug6ernaut.network.shared.util.Crypto;
import com.jug6ernaut.network.shared.util.ReflectionUtils;
import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 7/27/13
 * Time: 6:19 PM
 */

public class Credentials extends TransientObject implements Serializable {

    public static final String TYPE = Credentials.class.getSimpleName();
    private static final String PASSWORD_KEY = "pw";
    public static final String EMAIL_KEY = "email";
    public static final String AUTH_TOKEN_KEY = "auth_token";
    private static final String AUTH_TOKEN_EXPIRE_KEY = "auth_token_expire";
    public static final String USERNAME_KEY = "username";
    protected static final String VALIDATION_KEY = "validation";
    protected static final String PUSH_MESSAGING_KEY = "push_messaging_key";

//    private String emailAddress; // uuid and for recovery
//    private String password;
//    private String userKey;
//    private String authToken;
//    private long createDate;
//    private long authTokenExpireDate;
//    private String validation;

    protected Credentials(){
        super(TYPE);
    }

    private Credentials(Credentials credentials){
        super(TYPE);
        try {
//            ReflectionUtils.setObjectField(this, TransientObject.META_DATA, new HashMap(credentials.getMetaData()));
            ReflectionUtils.setObjectField(this, TransientObject.USER_DATA, new HashMap(credentials.getUserData()));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Credentials(String username, String email, String password){
        super(TYPE);
        setUsername(username);
        setEmailAddress(email);
        setPassword(password);
    }

    public String getPassword() {
        return  get(String.class,PASSWORD_KEY);
    }

    public void setPassword(String password) {
        put(PASSWORD_KEY, password);
    }

    public void decryptPassword(PrivateKey privateKey){
        byte[] decoded = Base64.decodeBase64(getPassword().getBytes());
        byte[] encrypted = Crypto.decrypt(decoded,privateKey);
        setPassword(new String(encrypted));
    }

    public void encryptPassword(PublicKey publicKey){
        byte[] encrypted  = Crypto.encrypt(getPassword().getBytes(),publicKey);
        String encoded = new String(Base64.encodeBase64(encrypted));
        setPassword(encoded);
    }

    public String getEmailAddress() {
        return  get(String.class,EMAIL_KEY);
    }

    public void setEmailAddress(String emailAddress) {
        put(EMAIL_KEY,emailAddress);
    }

    public String getAuthToken() {
        return get(String.class,AUTH_TOKEN_KEY);
    }

    public void setAuthToken(String authToken) {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        setAuthTokenExpireDate(c.getTime().getTime() + (1000*60*60*12)); //expires in one day

        put(AUTH_TOKEN_KEY,authToken);
    }

    public long getAuthTokenExpireDate() {
        if(getUserData().containsKey(AUTH_TOKEN_EXPIRE_KEY))
            return Long.valueOf( get(String.class,AUTH_TOKEN_EXPIRE_KEY));
        else
            return -1;
    }

    private void setAuthTokenExpireDate(long authTokenExpireDate) {
        put(AUTH_TOKEN_EXPIRE_KEY,String.valueOf(authTokenExpireDate));
    }

    public String getValidation() {
        return get(String.class,VALIDATION_KEY);
    }

    public void setValidation(String validation) {
        put(VALIDATION_KEY,validation);
    }

    public void setUsername(String username){
        put(USERNAME_KEY,username);
    }

    public String getUsername(){
        return get(String.class,USERNAME_KEY);
    }

    public void setPushMessagingKey(String key){
        put(PUSH_MESSAGING_KEY,key);
    }

    public String getPushMessagingKey(){
        return get(String.class,PUSH_MESSAGING_KEY);
    }

    public Credentials getSafe(){
        Credentials safeCreds = new Credentials(this);
        safeCreds.remove(PASSWORD_KEY);
        safeCreds.remove(AUTH_TOKEN_KEY);
        safeCreds.remove(AUTH_TOKEN_EXPIRE_KEY);
        safeCreds.remove(VALIDATION_KEY);
        safeCreds.remove(PUSH_MESSAGING_KEY);
        return safeCreds;
    }


//    @Override
//    public String toString() {
//        return "Credentials{" +
//                "emailAddress='" + getEmailAddress() + '\'' +
//                ", username='" + getUsername() + '\'' +
//                ", password='" + getPassword() + '\'' +
//                ", userKey='" + getUserId() + '\'' +
//                ", createDate=" + getCreateDate() +
//                ", authToken='" + getAuthToken() + '\'' +
//                ", authTokenExpireDate=" + getAuthTokenExpireDate() +
//                ", validation='" + getValidation() + '\'' +
//                '}';
//    }
}
