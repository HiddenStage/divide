package com.jug6ernaut.network.authenticator.client.auth;

import android.accounts.Account;
import com.google.gson.Gson;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.*;
import com.jug6ernaut.network.authenticator.client.auth.credentials.LoginCredentials;
import com.jug6ernaut.network.authenticator.client.auth.credentials.SignUpCredentials;
import com.jug6ernaut.network.authenticator.client.auth.credentials.ValidCredentials;
import com.jug6ernaut.network.authenticator.client.http.Status;
import com.jug6ernaut.network.shared.util.Crypto;
import com.jug6ernaut.network.shared.util.ObjectUtils;
import com.jug6ernaut.network.shared.web.transitory.Credentials;
import org.apache.commons.io.IOUtils;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Func1;

import java.security.PublicKey;
import java.util.List;

import static com.jug6ernaut.network.authenticator.client.auth.LoginState.*;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/4/13
 * Time: 9:06 PM
 */
public class AuthManager extends AbstractWebManager<AuthWebService> {

    private static Logger logger = Logger.getLogger(AuthManager.class);

    private BackendUser user;
    private AuthUtils authUtils;
    private AccountInformation accountInfo;
    private LoginState CURRENT_STATE = LoginState.LOGGED_OUT;

    public AuthManager(Backend backend) {
        super(backend);
        accountInfo = backend.accountInformation;
        authUtils = AuthUtils.get(backend.app, accountInfo.getAccountType());

        this.addConnectionListener(new ConnectionListner() {
            @Override
            public void onConnectionChange(boolean connected) {
                if(connected){
                    new Thread(){
                        @Override
                        public void run() {
                            loadCachedUser();
                        }
                    }.start();
                }
            }
        });
    }

    @Override
    public int onError(int status){
        switch (status) {
            case 401: {
                logger.error("UNAUTHORIZED Recieved");
                Account account = getStoredAccount();
                if (account != null) { //TODO verify this works
                    if(getUser()!=null) authUtils.invalidateToken(accountInfo.getFullAccessTokenType(),getUser().getAuthToken());
                    recoverFromOneTimeToken(authUtils.getPassword(account));
                }
            }
        }
        return status;
    }

    @Override
    public RequestInterceptor.RequestFacade onRequest(RequestInterceptor.RequestFacade requestFacade){
        if (getUser() != null) {
            requestFacade.addHeader("Authorization", "CUSTOM " + getUser().getAuthToken());
        } else {
            logger.warn("no auth key: " + getUser());
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

    private void loginStoredUserASync(Account account){
        synchronized (CURRENT_STATE){
            if(CURRENT_STATE.equals(LOGGED_IN) || CURRENT_STATE.equals(LOGGING_IN)) return;
            logger.debug("loginStoredUserASync: " + account);
            CURRENT_STATE = LOGGING_IN;
            try {
                String token = authUtils.blockingGetAuthToken(account,accountInfo.getFullAccessTokenType());
                if(token!=null){
                    logger.info("Recieved stored token: " + token);
                    logger.info("Getting user from token...");
                    getRemoteUserFromTokenAsync(token);
                } else {
                    logger.error("Login error, null token");
                }
                CURRENT_STATE = LOGGED_OUT;
            } catch (Exception e) {
                logger.error("Failed to get Token",e);
            }
        }
    }

    public ValidCredentials getRemoteUserFromToken(String authToken){
        CURRENT_STATE = LOGGING_IN;
        return getWebService().getUser(authToken);
    }

    public RecoveryResponse recoverFromOneTimeToken(String token){
        try {
            logger.debug("recoverFromOneTimeToken(" + token + ")");

            CURRENT_STATE = LOGGING_IN;
            Response response = getWebService().recoverFromOneTimeToken(token);

            String body = IOUtils.toString(response.getBody().in());

            ValidCredentials credentials = new Gson().fromJson(body,ValidCredentials.class);
//            String newRecoveryToken = response.getHeaders().get(0).getValue();

            Account account = authUtils.getAccount(credentials.getEmailAddress());

            authUtils.setPassword(account, credentials.getRecoveryToken());
            return new RecoveryResponse(setUser(credentials), Status.SUCCESS_OK, "");
        } catch (Exception e) {
            logger.error("recoverFromOneTimeToken Failure(" + token + ")", e);
            return new RecoveryResponse(null, Status.SERVER_ERROR_INTERNAL, e.getLocalizedMessage());
        }

    }

    private void getRemoteUserFromTokenAsync(String authToken) {
        try {
            CURRENT_STATE = LoginState.LOGGING_IN;
            getWebService().getUser(authToken, new Callback<ValidCredentials>() {
                @Override
                public void success(final ValidCredentials webUser, Response response) {
                    setUser(webUser);
                }

                @Override
                public void failure(RetrofitError retrofitError) {
//                    ignore here, this is handled in AbstractWebManager
//                    if (retrofitError != null){
//                        logger.error("Failed to get User!", retrofitError);
//                    }
//                    else logger.error("Failed to get user...");
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

    private BackendUser setUser(ValidCredentials returned){
        logger.debug("setUser: " + returned);
        this.user = BackendUser.from(returned);
        logger.debug("user: " + user);
        CURRENT_STATE = LOGGED_IN;

        if(returned!=null)
            storeOrUpdateAccount(returned);

        fireLoginListeners();
        return user;
    }

    private void storeOrUpdateAccount(Credentials validCredentials){
        logger.debug("storeOrUpdateAccount: " + validCredentials);

        String accountName = validCredentials.getEmailAddress();
        String accountRecoveryToken = validCredentials.getRecoveryToken();
        String authToken = validCredentials.getAuthToken();
        String authTokenType = accountInfo.getFullAccessTokenType();

        logger.debug("name: " + accountName);
        logger.debug("recoveryToken: " + accountRecoveryToken);
        logger.debug("authToken: " + authToken);
        logger.debug("authTokenType: " + authTokenType);

        Account account = authUtils.getAccount(accountName);

        if (account == null) {
            account = new Account(accountName, accountInfo.getAccountType());

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            authUtils.addAcccount(account, accountRecoveryToken, null);
//            mAccountManager.addAccountExplicitly(account, accountPassword, null);
        }

        if(accountRecoveryToken!=null && accountRecoveryToken.length()>0)
            authUtils.setPassword(account, accountRecoveryToken);

        authUtils.setAuthToken(account, authTokenType, authToken);

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
                user = setUser(g.getBody());
            }

            return new SignUpResponse(user, response.getStatus(), response.getError());
        } catch (Exception e) {
            logger.error("SignUp Failure(" + loginCreds.getEmailAddress() + ")", e);
            return new SignUpResponse(null, Status.SERVER_ERROR_INTERNAL, e.getLocalizedMessage());
        }
    }

    public Observable<BackendUser> signUpASync(final SignUpCredentials signInCreds){
        logger.debug("signUpASync("+signInCreds+")");
        try {
            CURRENT_STATE = LOGGING_IN;

            return getWebService().getPublicKeyA().flatMap(new Func1<byte[], Observable<SignUpCredentials>>() {
                @Override
                public Observable<SignUpCredentials> call(byte[] bytes) {
                    try {
                        PublicKey key = Crypto.pubKeyFromBytes(bytes);
                        signInCreds.encryptPassword(key);

                        return Observable.from(signInCreds);
                    }catch (Exception e) {
                        return Observable.error(e);
                    }
                }
            }).flatMap(new Func1<SignUpCredentials, Observable<ValidCredentials>>() {
                @Override
                public Observable<ValidCredentials> call(SignUpCredentials o) {
                    return getWebService().userSignUpA(signInCreds);
                }
            }).map(new Func1<ValidCredentials, BackendUser>() {
                @Override
                public BackendUser call(ValidCredentials validCredentials) {
                    return setUser(validCredentials);
                }
            });
        } catch (Exception e) {
            logger.error("Failed to signUp(" + signInCreds.getEmailAddress() + ")", e);
            return Observable.error(e);
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
                user = setUser(g.getBody());
            }

            return new SignInResponse(user,response.getStatus(),response.getError());
        }catch (Exception e){
            logger.error("Login Failure("+loginCreds.getEmailAddress()+")",e);
            CURRENT_STATE = LOGGED_OUT;
            return new SignInResponse(null, Status.SERVER_ERROR_INTERNAL,e.getLocalizedMessage());
        }
    }

    public Observable<BackendUser> loginASync(final LoginCredentials loginCreds){
        logger.debug("loginASync("+loginCreds+")");
        try{
            CURRENT_STATE = LOGGING_IN;

            return getWebService().getPublicKeyA().flatMap(new Func1<byte[], Observable<LoginCredentials>>() {
                @Override
                public Observable<LoginCredentials> call(byte[] bytes) {
                    try {
                        if (!loginCreds.isEncrypted()) {
                            PublicKey key = Crypto.pubKeyFromBytes(bytes);
                            loginCreds.encryptPassword(key);
                        }
                        return Observable.from(loginCreds);
                    } catch (Exception e) {
                        return Observable.error(e);
                    }
                }
            }).flatMap(new Func1<LoginCredentials, Observable<ValidCredentials>>() {
                @Override
                public Observable<ValidCredentials> call(LoginCredentials credentials) {
                    return getWebService().loginA(credentials);
                }
            }).map(new Func1<ValidCredentials, BackendUser>() {
                @Override
                public BackendUser call(ValidCredentials validCredentials) {
                    return setUser(validCredentials);
                }
            });

        }catch (Exception e){
            logger.error("Failed to SignIn("+loginCreds.getEmailAddress()+")",e);
            CURRENT_STATE = LOGGED_OUT;
            return Observable.error(e);
//
        }
    }

    public void sendUserData(BackendUser user){
        getWebService().sendUserData(user);
    }

    public void sendUserData(BackendUser user, Callback<String> callback){
        getWebService().sendUserData(user,callback);
    }

    public Observable<BackendUser> getUserData(){
        return Observable.create(new Observable.OnSubscribeFunc<BackendUser>() {
            @Override
            public Subscription onSubscribe(Observer<? super BackendUser> observer) {
                try {
                    setUser(getWebService().getUserData());
                    observer.onNext(getUser());
                    observer.onCompleted();
                } catch (Exception e) {
                    observer.onError(e);
                }

                return Subscriptions.empty();
            }
        }).subscribeOn(Schedulers.threadPoolForIO());
    }

    @Override
    protected Class getType() {
        return AuthWebService.class;
    }

    PublishSubject<LoginEvent> loginEventPublisher = PublishSubject.create();

    public void addLoginListener(LoginListener listener){
        Subscription subscription = loginEventPublisher
                .subscribeOn(Schedulers.currentThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listener);
        listener.setSubscription(subscription);
        if(getUser()!=null){
            loginEvent.user = user;
            loginEvent.state = CURRENT_STATE;
            listener.onNext(loginEvent);
        }
    }

    private LoginEvent loginEvent = new LoginEvent();
    private void fireLoginListeners(){
        loginEvent.user = user;
        loginEvent.state = CURRENT_STATE;
        loginEventPublisher.onNext(loginEvent);
    }



}