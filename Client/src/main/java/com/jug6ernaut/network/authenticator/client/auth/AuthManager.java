package com.jug6ernaut.network.authenticator.client.auth;

import android.accounts.Account;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.AbstractWebManager;
import com.jug6ernaut.network.authenticator.client.Backend;
import com.jug6ernaut.network.authenticator.client.BackendUser;
import com.jug6ernaut.network.shared.util.Crypto;
import com.jug6ernaut.network.shared.web.transitory.Credentials;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/4/13
 * Time: 9:06 PM
 */
public class AuthManager extends AbstractWebManager<AuthWebService> {

    public static final String ACCOUNT_TYPE = "com.jug6ernaut.tactics";
    public static final String ACCOUNT_NAME = "Tactics";
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an Tactics account";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an Tactics account";

    private static Logger logger = Logger.getLogger(AuthManager.class);

    private BackendUser user;
    private AuthUtils authUtils;

    public AuthManager(Backend backend) {
        super(backend);
        authUtils = AuthUtils.get(backend.app,ACCOUNT_TYPE);

        this.addConnectionListener(new ConnectionListner() {
            @Override
            public void onConnectionChange(boolean connected) {
                login();
            }
        });
    }

    private void login(){
        List<Account> accounts = authUtils.getAccounts();

        if(accounts.size() == 1){
            Account account = accounts.get(0);
            logger.debug("Logging in with: " + account);
            authUtils.getAuthToken(account,new com.jug6ernaut.network.authenticator.client.Callback<String>() {
                @Override
                public void callback(String s) {
                    if(s!=null){
                        logger.info("Successfully Logged in! " + s);
                        logger.info("Getting user...");
                        getUserASync(s);
                    } else {
                        logger.error("Failed to login...");
                    }
                }
            });
        } else logger.debug("Login Error: " + accounts.size());
    }

    private void getUserASync(String authToken) {
        try {
            getWebService().getUser(authToken, new Callback<Credentials>() {
                @Override
                public void success(final Credentials webUser, Response response) {
                    setUser(webUser);
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    if (retrofitError != null) logger.error("Failed to get User!", retrofitError);
                    else logger.error("Failed to get user...");
                }
            });
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public BackendUser getUser() {
        return user;
    }

    /*
     * basic stub
     */
    public void logout(){
        user = null;
    }

    private BackendUser setUser(Credentials user){
        logger.debug("setUser: " + user);
        this.user = BackendUser.from(user);

        fireLoginListeners();
        return getUser();
    }

    public PublicKey getServerKey(){
        logger.debug("getServerKey()");
        try {
            byte[] pubKey = getWebService().getPublicKey();
            logger.debug("pubKey: " + String.valueOf(pubKey));
            PublicKey key = Crypto.pubKeyFromBytes(pubKey);
            return key;
        } catch (Exception e) {
            logger.error("Failed to getServerKey()", e);
        }

        return null;
    }

    public BackendUser signUp(String email, String username, String password){
        logger.debug("signUp("+email+","+username+")");
        try {
            byte[] pubKey = getWebService().getPublicKey();
            logger.debug("pubKey: " + String.valueOf(pubKey));
            PublicKey key = Crypto.pubKeyFromBytes(pubKey);

            Credentials loginCreds = new Credentials(email,username, password);
            loginCreds.encryptPassword(key);
            logger.debug("Login Creds: " + loginCreds);

            return setUser(getWebService().userSignUp(loginCreds));
        } catch (Exception e) {
            logger.error("Failed to signUp(" + username + ")", e);
        }

        return null;
    }

    public void signUpASync(final String email,final String username,final String password){
        logger.debug("signUpASync("+email+","+username+")");
        try {
            PublicKey key = Crypto.pubKeyFromBytes(getWebService().getPublicKey());

            Credentials loginCreds = new Credentials(email,username, password);
            loginCreds.encryptPassword(key);

            getWebService().userSignUp(loginCreds, new Callback<Credentials>() {
                @Override
                public void success(Credentials credentials, Response response) {
                    setUser(credentials);
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    if (retrofitError != null) logger.error("Failed to signUp(" + username + ")", retrofitError);
                }
            });

        } catch (Exception e) {
            logger.error("Failed to signUp(" + username + ")", e);
        }

    }

    public BackendUser login(final String username,final String password){
        logger.debug("login("+username+")");
        try{
            PublicKey key = Crypto.pubKeyFromBytes(getWebService().getPublicKey());

            Credentials creds = new Credentials("",username, password);
            creds.encryptPassword(key);

            return setUser(getWebService().login(creds));
        }catch (Exception e){
            logger.error("Login Failure("+username+")",e);
            return null;
        }
    }

    public boolean loginASync(final String username,final String password){
        logger.debug("loginASync("+username+")");
        try{
            PublicKey key = Crypto.pubKeyFromBytes(getWebService().getPublicKey());

            Credentials loginCreds = new Credentials("",username, password);
            loginCreds.encryptPassword(key);

            getWebService().login(loginCreds, new Callback<Credentials>() {
                @Override
                public void success(Credentials user, Response response) {
                    setUser(user);
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    if (retrofitError != null)
                        logger.error("Failed to SignIn(" + username + ")", retrofitError);
                }
            });
            return true;
        }catch (Exception e){
            logger.error("Failed to SignIn("+username+")",e);
            return false;
        }
    }

    @Override
    protected Class getType() {
        return AuthWebService.class;
    }

    public static interface LoginListener{
        public void onLogin(BackendUser user);
    }

    private final List<LoginListener> loginListeners = new ArrayList<LoginListener>();
    public void addLoginListener(LoginListener litener){
        loginListeners.add(litener);
    }
    public boolean removeLoginListener(LoginListener listener){
        return loginListeners.remove(listener);
    }

    private void fireLoginListeners(){
        synchronized (loginListeners){
            for (LoginListener ll : loginListeners){
                ll.onLogin(getUser());
            }
        }
    }

}