package io.divide.client.auth;

import com.google.inject.Inject;
import io.divide.client.BackendUser;
import io.divide.client.Config;
import io.divide.client.auth.credentials.LocalCredentials;
import io.divide.client.auth.credentials.LoginCredentials;
import io.divide.client.auth.credentials.SignUpCredentials;
import io.divide.client.auth.credentials.ValidCredentials;
import io.divide.client.data.ServerResponse;
import io.divide.client.http.Status;
import io.divide.client.web.AbstractWebManager;
import io.divide.shared.logging.Logger;
import io.divide.shared.util.Crypto;
import io.divide.shared.util.ObjectUtils;
import io.divide.shared.transitory.Credentials;
import org.jetbrains.annotations.NotNull;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.security.PublicKey;
import java.util.List;
import java.util.Map;

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
    private AccountStorage accountStorage;
    private LoginState CURRENT_STATE = LoginState.LOGGED_OUT;
    private PublishSubject<BackendUser> loginEventPublisher = PublishSubject.create();

    @Inject
    public AuthManager(Config config, AccountStorage accountStorage) {
        super(config);
        this.accountStorage = accountStorage;

        loadCachedUser().subscribeOn(Schedulers.io()).subscribe(new Action1<BackendUser>() {
            @Override
            public void call(BackendUser user) { }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                logger.error("Failed to login", throwable);
            }
        });
    }

    private Observable<BackendUser> loadCachedUser(){
        logger.debug("Stored user Login: " + getStoredAccount());

        if(getUser() != null) return Observable.from(getUser());
        else if(getStoredAccount() != null){
            logger.debug("Stored user Login: " + getStoredAccount());
            return guardedLogin(getStoredAccount());
        }
        else return Observable.empty();
    }

    /**
     * Return LocalCredentials representing logged in user credentials stored to disk.
     * @return locally stored credentials. Representing account name and access token
     */
    public LocalCredentials getStoredAccount(){
        return ObjectUtils.get1stOrNull(accountStorage.getAccounts());
    }

    private Observable<BackendUser> guardedLogin(LocalCredentials account){
        synchronized (AuthManager.class){
            switch (CURRENT_STATE){
                case LOGGED_IN: return Observable.from(getUser());                    // already logged in, return user
                case LOGGING_IN: return loginEventPublisher.asObservable();           // already loggin in, listen to result
                case LOGGED_OUT: return getUserFromAuthToken(account.getAuthToken()); // asked to login, we arnt, so login
                default: return Observable.error(new Exception("Invalid current State?!: " + CURRENT_STATE));
            }
        }
    }

    /**
     * Login using user authentication token
     * @param authToken authentication user for user.
     * @return Logged in user.
     */
    public Observable<BackendUser> getUserFromAuthToken(final String authToken){
        return Observable.create(new Observable.OnSubscribe<BackendUser>() {
            @Override
            public void call(Subscriber<? super BackendUser> subscriber) {
                try{
                    setLoginState(LOGGING_IN);

                    ValidCredentials validCredentials = getWebService().getUserFromAuthToken(authToken).toBlockingObservable().first();
                    if(validCredentials == null) throw new Exception("Null User Returned");

                    subscriber.onNext(setUser(validCredentials));
                }catch (Exception e){
                    setLoginState(LOGGED_OUT);
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * Login using user recovery token
     * @param recoveryToken recovery token for user.
     * @return Logged in user.
     */
    public Observable<BackendUser> getUserFromRecoveryToken(final String recoveryToken){
        return Observable.create(new Observable.OnSubscribe<BackendUser>() {
            @Override
            public void call(Subscriber<? super BackendUser> subscriber) {
                try{
                    setLoginState(LOGGING_IN);

                    ValidCredentials validCredentials = getWebService().getUserFromRecoveryToken(recoveryToken).toBlockingObservable().first();
                    if(validCredentials == null) throw new Exception("Null User Returned");

                    subscriber.onNext(setUser(validCredentials));
                }catch (Exception e){
                    setLoginState(LOGGED_OUT);
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * Returns currently logged in user if one exists.
     * @return Logged in user or null.
     */
    public BackendUser getUser() {
        return user;
    }

    /**
     * Log out current user if logged in.
     */
    public void logout(){
        List<LocalCredentials> accountList = accountStorage.getAccounts();
        if(accountList.size() == 1){
            String userName = accountList.get(0).getName();
            logger.debug("logout: " + userName);
            accountStorage.removeAccount(userName);
            user = null;
        }
        setLoginState(LoginState.LOGGED_OUT);
    }

    private BackendUser setUser(ValidCredentials returned){
        logger.debug("setUser: " + returned);
        user = BackendUser.from(returned);
        logger.debug("setUser: " + getUser());

        if(returned!=null) storeOrUpdateAccount(returned);

        fireLoginListeners();
        setLoginState(LOGGED_IN);

        return getUser();
    }

    private void storeOrUpdateAccount(Credentials validCredentials){
        logger.debug("storeOrUpdateAccount: " + validCredentials);

        String accountName = validCredentials.getEmailAddress();
        String recoveryToken = validCredentials.getRecoveryToken();
        String authToken = validCredentials.getAuthToken();

        if (!accountStorage.exists(accountName)) {
            LocalCredentials account = new LocalCredentials();
            account.setName(accountName);
            account.setAuthToken(authToken);
            account.setRecoveryToken(recoveryToken);

            accountStorage.addAcccount(account);
        } else {
            accountStorage.setAuthToken(accountName,authToken);

            if(recoveryToken!=null && recoveryToken.length()>0)
                accountStorage.setRecoveryToken(accountName, recoveryToken);
        }

    }

    private static PublicKey serverPublicKey = null;

    /**
     * Returns server public key. Queries server or local copy.
     * @return Server public key.
     */
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

    /**
     * Syncronously attempt to create user account
     * @param loginCreds Credentials used to create account.
     * @return Response of the operation
     */
    public SignUpResponse signUp(SignUpCredentials loginCreds){
        logger.debug("signUp(" + loginCreds + ")");
        try {
            setLoginState(LOGGING_IN);
            PublicKey key = Crypto.pubKeyFromBytes(getWebService().getPublicKey());

            loginCreds.encryptPassword(key);

            logger.debug("Login Creds: " + loginCreds);
            ServerResponse<ValidCredentials> response = ServerResponse.from(ValidCredentials.class, getWebService().userSignUp(loginCreds));

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

    /**
     * Asyncronously attempt to create user account
     * @param signInCreds Credentials used to create account.
     * @return Response of the operation
     */
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
            })
              .subscribeOn(Schedulers.io()).observeOn(config.observeOn());
        } catch (Exception e) {
            logger.error("Failed to signUp(" + signInCreds.getEmailAddress() + ")", e);
            return Observable.error(e);
        }
    }

    /**
     * Syncronously attempt to log into user account
     * @param loginCreds Credentials used to login.
     * @return Response of the operation
     */
    public SignInResponse login(final LoginCredentials loginCreds){
        logger.debug("login("+loginCreds+")");
        try{
            setLoginState(LOGGING_IN);
            if(!loginCreds.isEncrypted()){
                PublicKey key = Crypto.pubKeyFromBytes(getWebService().getPublicKey());

                loginCreds.encryptPassword(key);
            }

            logger.debug("Login Creds: " + loginCreds);
            ServerResponse<ValidCredentials> response = ServerResponse.from(ValidCredentials.class,getWebService().login(loginCreds));

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

    /**
     * Asyncronously attempt to log into user account
     * @param loginCreds Credentials used to login.
     * @return Response of the operation
     */
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
            })
              .subscribeOn(Schedulers.io()).observeOn(config.observeOn());

        }catch (Exception e){
            logger.error("Failed to SignIn("+loginCreds.getEmailAddress()+")",e);
            setLoginState(LOGGED_OUT);
            return Observable.error(e);
        }
    }

    private void setLoginState(@NotNull LoginState state){
        logger.debug("Changing State("+config.id+"): " + state);
        CURRENT_STATE = state;
    }

    /**
     * Update remote server with new user data.
     * @return status of update.
     */
    public Observable<Void> sendUserData(BackendUser backendUser){
        return getWebService().sendUserData(getUser().getAuthToken(),backendUser)
               .subscribeOn(config.subscribeOn()).observeOn(config.observeOn());
    }

    /**
     * Query server to return user data for the logged in user
     * @return updated BackendUser.
     */
    public Observable<Map<String,Object>> getUserData(BackendUser backendUser){
        return getWebService().getUserData(getUser().getAuthToken(), backendUser.getOwnerId() + "")
                .subscribeOn(config.subscribeOn()).observeOn(config.observeOn());
    }

    @Override
    protected Class<AuthWebService> getType() {
        return AuthWebService.class;
    }

    /**
     * Add loginListener to listen to login events
     * @param listener LoginListener to receive events.
     */
    public void addLoginListener(LoginListener listener){
        Subscription subscription = loginEventPublisher
                .subscribeOn(Schedulers.io())
                .subscribe(listener);
        listener.setSubscription(subscription);
        if(getUser()!=null){
            listener.onNext(getUser());
        }
    }

    /**
     * @hide
     */
    private void fireLoginListeners(){
        loginEventPublisher.onNext(getUser());
    }

}