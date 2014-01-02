package com.jug6ernaut.network.authenticator.client.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.*;
import com.jug6ernaut.network.authenticator.client.auth.credentials.LoginCredentials;
import com.jug6ernaut.network.authenticator.client.auth.credentials.SignUpCredentials;
import com.jug6ernaut.network.authenticator.client.auth.credentials.ValidCredentials;
import com.jug6ernaut.network.authenticator.client.http.Status;
import com.jug6ernaut.network.shared.util.Crypto;
import com.jug6ernaut.network.shared.util.ObjectUtils;
import com.jug6ernaut.network.shared.web.transitory.Credentials;
import org.apache.http.HttpStatus;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.security.PublicKey;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.jug6ernaut.network.authenticator.client.auth.LoginState.LOGGED_IN;
import static com.jug6ernaut.network.authenticator.client.auth.LoginState.LOGGED_OUT;
import static com.jug6ernaut.network.authenticator.client.auth.LoginState.LOGGING_IN;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/4/13
 * Time: 9:06 PM
 */
public class AuthManager extends AbstractWebManager<AuthWebService> {

//    public static final String ACCOUNT_TYPE = "com.jug6ernaut.tactics";
//    public static final String ACCOUNT_NAME = "Tactics";
//    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
//    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an Tactics account";
//    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
//    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an Tactics account";

    private static Logger logger = Logger.getLogger(AuthManager.class);

    private BackendUser user;
    private AuthUtils authUtils;
    private AccountInformation accountInfo;
    private static LoginState CURRENT_STATE = LoginState.LOGGED_OUT;

    public AuthManager(Backend backend) {
        super(backend);
        accountInfo = backend.accountInformation;
        authUtils = AuthUtils.get(backend.app, accountInfo.getAccountType());

        this.addConnectionListener(new ConnectionListner() {
            @Override
            public void onConnectionChange(boolean connected) {
                if(connected){
                    loadCachedUser();
                }
            }
        });
    }

    @Override
    public RetrofitError onError(RetrofitError retrofitError){
        switch (retrofitError.getResponse().getStatus()) {
            case HttpStatus.SC_UNAUTHORIZED: {
                if (getUser() != null) { //TODO verify this works
                    AccountManager.get(backend.app).invalidateAuthToken(backend.accountInformation.getAccountType(),getUser().getAuthToken());
                }
            }
        }
        return retrofitError;
    }

    @Override
    public RequestInterceptor.RequestFacade onRequest(RequestInterceptor.RequestFacade requestFacade){
        if (getUser() != null) {
            requestFacade.addHeader("Authorization", "CUSTOM " + getUser().getAuthToken());
        } else {
            logger.error("no auth key: " + getUser());
        }
        return requestFacade;
    }

    private BackendUser loadCachedUser(){
        if(user != null) return user;

        Account account = getStoredAccount();
        if(account != null){
            loginStoredUserASync(account);
            return user;
        }
        else {
            logger.warn("No Stored user.");
            return null;
        }
    }

    public Account getStoredAccount(){
        List<Account> accounts = authUtils.getAccounts();
        logger.debug("getStoredAccount: " + accounts.size());
        return ObjectUtils.get1stOrNull(accounts);
    }

    /* complaints about being run on UI thread... */
    private void loginStoredUser(Account account){
        logger.debug("loginStoredUser: " + account);
        try {
            String token = authUtils.blockingGetAuthToken(account,accountInfo.getFullAccessTokenType());
            logger.info("Successfully Logged in! " + token);
            logger.info("Getting user...");
            getRemoteUserFromTokenAsync(token);
        } catch (Exception e) {
            logger.error("Login Failure: " + e);
        }
    }

//    private Boolean loggingIn = false;

    private void loginStoredUserASync(Account account){
        synchronized (CURRENT_STATE){
            if(CURRENT_STATE.equals(LOGGED_IN) || CURRENT_STATE.equals(LOGGING_IN)) return;
            CURRENT_STATE = LOGGING_IN;
            authUtils.getAuthToken(account,accountInfo.getFullAccessTokenType(),new com.jug6ernaut.network.authenticator.client.Callback<String>() {

                @Override
                public void onResult(String s) {
                    if(s!=null){
                        logger.info("Successfully Logged in! " + s);
                        logger.info("Getting user...");
                        getRemoteUserFromTokenAsync(s);
                    } else {
                        logger.error("Login error, null token");
                    }
                    CURRENT_STATE = LOGGED_OUT;
                }

                @Override
                public void onError(Exception e) {
                    logger.error("Failed to login...",e);
                    CURRENT_STATE = LOGGED_OUT;
                }
            });
        }
    }

    public ValidCredentials getRemoteUserFromToken(String authToken){
        CURRENT_STATE = LOGGING_IN;
        return getWebService().getUser(authToken);
    }

    private void getRemoteUserFromTokenAsync(String authToken) {
        try {
            CURRENT_STATE = LoginState.LOGGING_IN;
            getWebService().getUser(authToken, new Callback<ValidCredentials>() {
                @Override
                public void success(final ValidCredentials webUser, Response response) {
                    setUser(null,webUser);
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
        return loadCachedUser();
    }

    /*
     * basic stub
     */
    public void logout(){
        List<Account> accountList = authUtils.getAccounts();
        if(accountList.size() == 1){
            String userName = accountList.get(0).name;
            logger.debug("logout: " + userName);
            authUtils.removeAccount(userName);
            if(backend.getPushManager().isRegistered()){
                backend.getPushManager().setEnablePush(false,"");
            }
            user = null;
        }
        CURRENT_STATE = LoginState.LOGGED_OUT;
    }

    private BackendUser setUser(Credentials sent, ValidCredentials returned){
        logger.debug("setUser: " + returned);
        this.user = BackendUser.from(returned);
        logger.debug("user: " + user);
        CURRENT_STATE = LOGGED_IN;

        if(sent!=null)
            storeOrUpdateAccount(sent);

        fireLoginListeners();
        return user;
    }

    private void storeOrUpdateAccount(Credentials validCredentials){
        logger.debug("storeOrUpdateAccount: " + validCredentials);
        String accountName = validCredentials.getEmailAddress();
        String accountPassword = validCredentials.getPassword();
        String authToken = validCredentials.getAuthToken();

        Account account = authUtils.getAccount(accountName);

        if (account == null) {
            account = new Account(accountName, accountInfo.getAccountType());

            String authtokenType = accountInfo.getFullAccessTokenType();

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            authUtils.addAcccount(account, accountPassword, null);
//            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            authUtils.setAuthToken(account, authtokenType, authToken);
        } else {
            authUtils.setPassword(account, accountPassword);
        }
    }

    private static PublicKey serverPublicKey = null;

    public PublicKey getServerKey(){
        logger.debug("getServerKey()");
        try {
            if(serverPublicKey!=null) return serverPublicKey;

            byte[] pubKey = getWebService().getPublicKey();
            logger.debug("pubKey: " + String.valueOf(pubKey));
            serverPublicKey = Crypto.pubKeyFromBytes(pubKey);
            return serverPublicKey;
        } catch (Exception e) {
            logger.error("Failed to getServerKey()", e);
        }

        return null;
    }

    public SignUpResponse signUp(SignUpCredentials loginCreds){
        logger.debug("signUp(" + loginCreds + ")");
        try {
            CURRENT_STATE = LOGGING_IN;
            PublicKey key = Crypto.pubKeyFromBytes(getWebService().getPublicKey());

            loginCreds.encryptPassword(key);

            logger.debug("Login Creds: " + loginCreds);
            GenericResponse<ValidCredentials> g = new GenericResponse<ValidCredentials>(ValidCredentials.class, getWebService().userSignUp(loginCreds));
            ServerResponse<ValidCredentials> response = ServerResponse.from(g);
//            SignInResponse response = RetrofitUtils.retrofitToServerResponse(SignInResponse.class,getWebService().login(loginCreds));
            BackendUser user = null;
            if (response.getStatus().isSuccess()) {
                user = setUser(loginCreds, g.getBody());
            }

            return new SignUpResponse(user, response.getStatus(), response.getError());
        } catch (Exception e) {
            logger.error("SignUp Failure(" + loginCreds.getEmailAddress() + ")", e);
            return new SignUpResponse(null, Status.SERVER_ERROR_INTERNAL, e.getLocalizedMessage());
        }
    }

    public void signUpASync(final SignUpCredentials signInCreds,final com.jug6ernaut.network.authenticator.client.Callback<BackendUser> callback){
        logger.debug("signUpASync("+signInCreds+")");
        try {
            CURRENT_STATE = LOGGING_IN;
            PublicKey key = Crypto.pubKeyFromBytes(getWebService().getPublicKey());

            signInCreds.encryptPassword(key);

            logger.debug("SignIn Creds: " + signInCreds);

            getWebService().userSignUp(signInCreds, new Callback<ValidCredentials>() {
                @Override
                public void success(ValidCredentials credentials, Response response) {
                    callback.onResult(setUser(signInCreds,credentials));
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    if (retrofitError != null) logger.error("Failed to signUp(" + signInCreds.getEmailAddress() + ")", retrofitError);
                    callback.onError(retrofitError);
                }
            });

        } catch (Exception e) {
            logger.error("Failed to signUp(" + signInCreds.getEmailAddress() + ")", e);
        }

    }

    public SignInResponse login(final LoginCredentials loginCreds){
        logger.debug("login("+loginCreds+")");
        try{
            CURRENT_STATE = LOGGING_IN;
            if(!loginCreds.isEncrypted()){
                PublicKey key = Crypto.pubKeyFromBytes(getWebService().getPublicKey());

                loginCreds.encryptPassword(key);
            }

            logger.debug("Login Creds: " + loginCreds);
            GenericResponse<ValidCredentials> g = new GenericResponse<ValidCredentials>(ValidCredentials.class,getWebService().login(loginCreds));
            ServerResponse<ValidCredentials> response = ServerResponse.from(g);
//            SignInResponse response = RetrofitUtils.retrofitToServerResponse(SignInResponse.class,getWebService().login(loginCreds));
            BackendUser user = null;
            if(response.getStatus().isSuccess()){
                user = setUser(loginCreds,g.getBody());
            }

            return new SignInResponse(user,response.getStatus(),response.getError());
        }catch (Exception e){
            logger.error("Login Failure("+loginCreds.getEmailAddress()+")",e);
            CURRENT_STATE = LOGGED_OUT;
            return new SignInResponse(null, Status.SERVER_ERROR_INTERNAL,e.getLocalizedMessage());
        }
    }

    public boolean loginASync(final LoginCredentials loginCreds,final com.jug6ernaut.network.authenticator.client.Callback<BackendUser> callback){
        logger.debug("loginASync("+loginCreds+")");
        try{
            CURRENT_STATE = LOGGING_IN;
            if(!loginCreds.isEncrypted()){
                PublicKey key = Crypto.pubKeyFromBytes(getWebService().getPublicKey());

                loginCreds.encryptPassword(key);
            }

            logger.debug("Login Creds: " + loginCreds);

            getWebService().login(loginCreds, new Callback<ValidCredentials>() {
                @Override
                public void success(ValidCredentials user, Response response) {
                    callback.onResult(setUser(loginCreds,user));
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    callback.onError(retrofitError);
                    throw retrofitError;
                }
            });
            return true;
        }catch (Exception e){
            logger.error("Failed to SignIn("+loginCreds.getEmailAddress()+")",e);
            CURRENT_STATE = LOGGED_OUT;
            return false;
        }
    }

    public void sendUserData(BackendUser user){
        getWebService().sendUserData(user);
    }

    public void sendUserData(BackendUser user, Callback<String> callback){
        getWebService().sendUserData(user,callback);
    }

    public void getUserData(){
        setUser(null,getWebService().getUserData());
    }

    public void getUserData(final Callback<BackendUser> callback){
        getWebService().getUserData(new Callback<ValidCredentials>() {
            @Override
            public void success(ValidCredentials validCredentials, Response response) {
                setUser(null,validCredentials);
                callback.success(getUser(),response);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                callback.failure(retrofitError);
            }
        });
    }

    @Override
    protected Class getType() {
        return AuthWebService.class;
    }

    public static interface LoginListener{
        public void onLogin(BackendUser user, LoginState state);
    }

    private final List<LoginListener> loginListeners = Collections.synchronizedList(new CopyOnWriteArrayList<LoginListener>());
    public void addLoginListener(LoginListener listener){
        synchronized (loginListeners){
            loginListeners.add(listener);
            if(user!=null)listener.onLogin(user,CURRENT_STATE);
        }
    }
    public boolean removeLoginListener(LoginListener listener){
        synchronized (loginListeners){
            return loginListeners.remove(listener);
        }
    }

    private void fireLoginListeners(){
        synchronized (loginListeners){
            Iterator<LoginListener> i = loginListeners.iterator(); // Must be in synchronized block
            while (i.hasNext())
                i.next().onLogin(user,CURRENT_STATE);
        }
    }



}