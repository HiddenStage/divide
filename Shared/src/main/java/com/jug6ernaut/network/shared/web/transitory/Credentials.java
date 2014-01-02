package com.jug6ernaut.network.shared.web.transitory;


import com.jug6ernaut.network.shared.util.Crypto;
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

//    private static final String PASSWORD_KEY = "pw";
//    public static final String EMAIL_KEY = "email";
//    public static final String AUTH_TOKEN_KEY = "auth_token";
//    private static final String AUTH_TOKEN_EXPIRE_KEY = "auth_token_expire";
//    public static final String USERNAME_KEY = "username";
//    protected static final String VALIDATION_KEY = "validation";
//    protected static final String PUSH_MESSAGING_KEY = "push_messaging_key";

    private static final MetaKey PASSWORD_KEY = new MetaKey("pw");
    public static final MetaKey EMAIL_KEY = new MetaKey("email");
    public static final MetaKey AUTH_TOKEN_KEY = new MetaKey("auth_token");
    private static final MetaKey AUTH_TOKEN_EXPIRE_KEY = new MetaKey("auth_token_expire");
    public static final MetaKey USERNAME_KEY = new MetaKey("username");
    protected static final MetaKey VALIDATION_KEY = new MetaKey("validation");
    protected static final MetaKey PUSH_MESSAGING_KEY = new MetaKey("push_messaging_key");
    public static final MetaKey USER_GROUP_KEY = new MetaKey("user_group_key");


    protected Credentials(){
        super(Credentials.class);
    }

    private Credentials(Credentials credentials){
        super(Credentials.class);
        this.meta_data = new HashMap<String, String>(credentials.meta_data);
        this.user_data = new HashMap<String, Object>(credentials.user_data);
    }

    public Credentials(String username, String email, String password){
        super(Credentials.class);
        setUsername(username);
        setEmailAddress(email);
        setPassword(password);
    }

    protected boolean isSystemUser(){
        return false;
    }

    public String getUserGroup(){
        return meta_get(String.class,USER_GROUP_KEY);
    }

    public void setUserGroup(String group){
        meta_put(USER_GROUP_KEY,group);
    }

//    @Override
//    protected String getLoggedInUser(){
//        return getOwnerId();
//    }

    public String getPassword() {
        return meta_get(String.class, PASSWORD_KEY);
    }

    public void setPassword(String password) {
        meta_put(PASSWORD_KEY, password);
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
        return meta_get(String.class, EMAIL_KEY);
    }

    public void setEmailAddress(String emailAddress) {
        meta_put(EMAIL_KEY, emailAddress);
    }

    public String getAuthToken() {
        return meta_get(String.class, AUTH_TOKEN_KEY);
    }

    public void setAuthToken(String authToken) {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        setAuthTokenExpireDate(c.getTime().getTime() + (1000*60*60*12)); //expires in one day

        meta_put(AUTH_TOKEN_KEY, authToken);
    }

    public long getAuthTokenExpireDate() {
        if(getUserData().containsKey(AUTH_TOKEN_EXPIRE_KEY))
            return Long.valueOf( meta_get(String.class, AUTH_TOKEN_EXPIRE_KEY));
        else
            return -1;
    }

    private void setAuthTokenExpireDate(long authTokenExpireDate) {
        meta_put(AUTH_TOKEN_EXPIRE_KEY, String.valueOf(authTokenExpireDate));
    }

    public String getValidation() {
        return meta_get(String.class, VALIDATION_KEY);
    }

    public void setValidation(String validation) {
        meta_put(VALIDATION_KEY, validation);
    }

    public void setUsername(String username){
        meta_put(USERNAME_KEY, username);
    }

    public String getUsername(){
        return meta_get(String.class, USERNAME_KEY);
    }

    public void setPushMessagingKey(String key){
        meta_put(PUSH_MESSAGING_KEY, key);
    }

    public String getPushMessagingKey(){
        return meta_get(String.class, PUSH_MESSAGING_KEY);
    }

    public Credentials getSafe(){
        Credentials safeCreds = new Credentials(this);
        safeCreds.meta_remove(PASSWORD_KEY);
        safeCreds.meta_remove(AUTH_TOKEN_KEY);
        safeCreds.meta_remove(AUTH_TOKEN_EXPIRE_KEY);
        safeCreds.meta_remove(VALIDATION_KEY);
        safeCreds.meta_remove(PUSH_MESSAGING_KEY);
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
