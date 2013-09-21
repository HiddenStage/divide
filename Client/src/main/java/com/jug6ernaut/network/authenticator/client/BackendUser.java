package com.jug6ernaut.network.authenticator.client;

import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.auth.AuthManager;
import com.jug6ernaut.network.shared.util.ReflectionUtils;
import com.jug6ernaut.network.shared.web.transitory.Credentials;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/24/13
 * Time: 9:26 PM
 */
public final class BackendUser extends Credentials{

    private static Logger logger = Logger.getLogger(BackendUser.class);
    private static AuthManager authManager;

    private BackendUser(){
        if(authManager==null) authManager = Backend.get().getAuthManager();
    }

    public static BackendUser from(Credentials credentials){
        BackendUser beu = new BackendUser();
        try {
//            ReflectionUtils.setObjectField(beu, TransientObject.META_DATA,credentials.getMetaData());
            ReflectionUtils.setObjectField(beu, TransientObject.USER_DATA,credentials.getUserData());
            Method setObjectType = ReflectionUtils.getObjectMethod(beu, "setObjectType", String.class);
            setObjectType.invoke(beu,"BackendUser");
        } catch (Exception e) {
            logger.error("Failed to init BackendUser from Credentials",e);
            return null;
        }
        return beu;
    }

    public static BackendUser create(String email, String username, String password){
        BackendUser beu = new BackendUser();
        beu.setEmailAddress(email);
        beu.setUsername(username);
        beu.setPassword(password);
        return beu;
    }

    static BackendUser logIn(String username, String password){
        return Backend.get().getAuthManager().login(username,password);
    }
    static void	logInInBackground(String username, String password, Callback<BackendUser> callback){

    }
    public void	logOut(){

    }

    public void	requestPasswordReset(String email){

    }
    public void	requestPasswordResetInBackground(String email, Callback<Boolean> callback){

    }

    public void signUp(Callback<Boolean> results){

    }
    void signUpInBackground(Callback<BackendUser> callback){

    }

    @Override
    public String toString() {
        return "BackendUser{" +
                "emailAddress='" + getEmailAddress() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", userKey='" + getUserId() + '\'' +
                ", authToken='" + getAuthToken() + '\'' +
                ", createDate=" + getCreateDate() +
                ", authTokenExpireDate=" + getAuthTokenExpireDate() +
                ", validation='" + getValidation() + '\'' +
                '}';
    }

}
