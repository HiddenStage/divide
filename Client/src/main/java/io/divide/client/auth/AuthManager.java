package io.divide.client.auth;

import android.accounts.Account;
import com.google.inject.Inject;
import com.jug6ernaut.android.logging.Logger;
import io.divide.client.BackendConfig;
import io.divide.client.BackendUser;
import io.divide.client.auth.credentials.LoginCredentials;
import io.divide.client.auth.credentials.SignUpCredentials;
import io.divide.client.auth.credentials.ValidCredentials;
import io.divide.client.data.GenericResponse;
import io.divide.client.data.ServerResponse;
import io.divide.client.http.Status;
import io.divide.client.web.AbstractWebManager;
import io.divide.client.web.ConnectionListener;
import io.divide.client.web.OnRequestInterceptor;
import io.divide.shared.util.Crypto;
import io.divide.shared.util.ObjectUtils;
import io.divide.shared.web.transitory.Credentials;
import retrofit.RequestInterceptor;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.security.PublicKey;
import java.util.List;

import static io.divide.client.auth.LoginState.*;

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

    @Inject
    public AuthManager(BackendConfig config) {
        super(config);
        accountInfo = config.accountInformation;
        authUtils = AuthUtils.get(config.app, accountInfo.getAccountType());

        this.addConnectionListener(new ConnectionListener() {
            @Override
            public void onEvent(boolean connected) {
                new Thread(){
                    @Override
                    public void run(){
                        loadCachedUser();

                    }
                };
            }
        });

        this.addRequestInterceptor(new OnRequestInterceptor() {
            @Override
            public RequestInterceptor.RequestFacade onRequest(RequestInterceptor.RequestFacade requestFacade) {
                System.out.println("onRequest("+CURRENT_STATE+"): " + getUser());

                if (getUser() != null) {
                    requestFacade.addHeader("Authorization", "CUSTOM " + getUser().getAuthToken());
                } else {
                    logger.warn("No auth key: " + getUser());
                }
                return requestFacade;
            }
        });
    }

    private BackendUser loadCachedUser(){
        if(getUser() != null) return getUser();

        Account account = getStoredAccount();
        if(account != null){
            loginStoredUserASync(account);
            return getUser();
        }
        else {
            logger.warn("No Stored user.");
            return null;
        }
    }

    public AuthUtils getAuthUtils(){
        return authUtils;
    }

    public AccountInformation getAccountInfo(){
        return accountInfo;
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
            setLoginState(LOGGING_IN);
            try {
                String token = authUtils.blockingGetAuthToken(account,accountInfo.getFullAccessTokenType());
                if(token!=null){
                    logger.info("Recieved stored token: " + token);
                    logger.info("Getting user from token...");
                    getRemoteUserFromTokenAsync(token);
                } else {
                    logger.error("Login error, null token");
                }
                setLoginState(LOGGED_OUT);
            } catch (Exception e) {
                logger.error("Failed to get Token",e);
            }
        }
    }

    public Observable<ValidCredentials> getRemoteUserFromToken(String authToken){
        setLoginState(LOGGING_IN);
        return getWebService().getUserFromAuthToken(authToken);
    }

    public RecoveryResponse recoverFromOneTimeToken(String token){
        try {
            logger.debug("recoverFromOneTimeToken(" + token + ")");

            setLoginState(LOGGING_IN);
            ValidCredentials credentials = getWebService().getUserFromRecoveryToken(token).toBlockingObservable().first();

            if(credentials != null){
                Account account = authUtils.getAccount(credentials.getEmailAddress());

                authUtils.setPassword(account, credentials.getRecoveryToken());
                setLoginState(LOGGED_IN);
                return new RecoveryResponse(setUser(credentials), Status.SUCCESS_OK, "");
            } else {
                logger.error("recoverFromOneTimeToken Failure(): " + credentials);
                setLoginState(LOGGED_OUT);
                return new RecoveryResponse(null, Status.SERVER_ERROR_INTERNAL, "");
            }
        } catch (Exception e) {
            logger.error("recoverFromOneTimeToken Failure(" + token + ")", e);
            setLoginState(LOGGED_OUT);
            return new RecoveryResponse(null, Status.SERVER_ERROR_INTERNAL, e.getLocalizedMessage());
        }

    }

    private void getRemoteUserFromTokenAsync(String authToken) {
        try {
            setLoginState(LoginState.LOGGING_IN);
            getWebService().getUserFromAuthToken(authToken).subscribe(new Action1<ValidCredentials>() {
                @Override
                public void call(ValidCredentials validCredentials) {

                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    setLoginState(LOGGED_OUT);
                }
            });

        } catch (Exception e) {
            setLoginState(LOGGED_OUT);
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
//            if(config.getPushManager().isRegistered()){
//                config.getPushManager().setEnablePush(false,"");
//            }
            user = null;
        }
        setLoginState(LoginState.LOGGED_OUT);
    }

    private BackendUser setUser(ValidCredentials returned){
        logger.debug(config.id + " setUser");
        user = BackendUser.from(returned);
        logger.debug("setUser: " + getUser());

        if(returned!=null)
            storeOrUpdateAccount(returned);

        fireLoginListeners();
        setLoginState(LOGGED_IN);

        return getUser();
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
            setLoginState(LOGGING_IN);
            PublicKey key = Crypto.pubKeyFromBytes(getWebService().getPublicKey());

            loginCreds.encryptPassword(key);

            logger.debug("Login Creds: " + loginCreds);
            GenericResponse<ValidCredentials> g = new GenericResponse<ValidCredentials>(ValidCredentials.class, getWebService().userSignUp(loginCreds));
            ServerResponse<ValidCredentials> response = ServerResponse.from(g);
//            SignInResponse response = RetrofitUtils.retrofitToServerResponse(SignInResponse.class,getWebService().login(loginCreds));

            BackendUser user;
            if (response.getStatus().isSuccess()) {
                user = setUser(response.get());
            } else {
                return new SignUpResponse(null, Status.SERVER_ERROR_INTERNAL, " null user returned");
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
            setLoginState(LOGGING_IN);

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
            setLoginState(LOGGING_IN);
            if(!loginCreds.isEncrypted()){
                PublicKey key = Crypto.pubKeyFromBytes(getWebService().getPublicKey());

                loginCreds.encryptPassword(key);
            }

            logger.debug("Login Creds: " + loginCreds);
            GenericResponse<ValidCredentials> g = new GenericResponse<ValidCredentials>(ValidCredentials.class,getWebService().login(loginCreds));
            ServerResponse<ValidCredentials> response = ServerResponse.from(g);

            BackendUser user;
            if(response.getStatus().isSuccess()){
                user = setUser(response.get());
            } else {
                logger.error("Login Failure("+loginCreds.getEmailAddress()+"): " + response.getStatus().getCode() + " " + response.getError());
                setLoginState(LOGGED_OUT);
                return new SignInResponse(null, Status.SERVER_ERROR_INTERNAL,"Login failed");
            }

            return new SignInResponse(user,response.getStatus(),response.getError());
        }catch (Exception e){
            logger.error("Login Failure("+loginCreds.getEmailAddress()+")",e);
            setLoginState(LOGGED_OUT);
            return new SignInResponse(null, Status.SERVER_ERROR_INTERNAL,e.getLocalizedMessage());
        }
    }

    public Observable<BackendUser> loginASync(final LoginCredentials loginCreds){
        logger.debug("loginASync("+loginCreds+")");
        try{
            setLoginState(LOGGING_IN);

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
            setLoginState(LOGGED_OUT);
            return Observable.error(e);
//
        }
    }

    private void setLoginState(LoginState state){
        logger.debug("Changing State("+config.id+"): " + state);
        CURRENT_STATE = state;
    }

    public Observable<Void> sendUserData(BackendUser user){
        return getWebService().sendUserData(user);
    }

    public Observable<BackendUser> getUserData(){
        return getWebService().getUserData().map(new Func1<ValidCredentials, BackendUser>() {
            @Override
            public BackendUser call(ValidCredentials validCredentials) {
                return setUser(validCredentials);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
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
            loginEvent.user = getUser();
            loginEvent.state = CURRENT_STATE;
            listener.onNext(loginEvent);
        }
    }

    private LoginEvent loginEvent = new LoginEvent();
    private void fireLoginListeners(){
        loginEvent.user = getUser();
        loginEvent.state = CURRENT_STATE;
        loginEventPublisher.onNext(loginEvent);
    }



}