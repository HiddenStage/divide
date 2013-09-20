package com.jug6ernaut.network.authenticator.client;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.auth.AuthUtils;
import com.jug6ernaut.network.authenticator.client.auth.AuthWebService;
import com.jug6ernaut.network.shared.util.Crypto;
import com.jug6ernaut.network.shared.web.transitory.Credentials;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
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
public class AuthManager {

    public static final String ACCOUNT_TYPE = "com.jug6ernaut.tactics";
    public static final String ACCOUNT_NAME = "Tactics";
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an Tactics account";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an Tactics account";

    private static Logger logger = Logger.getLogger(AuthManager.class);
    private static Logger retrologger = Logger.getLogger("Retrofit");
    private static AuthManager authManager;
    private AuthWebService authWebService;
    private BackendUser user;
    private AuthUtils authUtils;
    private Context context;
    private OkClient httpClient;
    private String url;

    private AuthManager(Context context, String url, OkClient httpClient) {
        context.registerReceiver(CONNECTION_RECIEVER, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        this.context = context;
        this.url = url;
        this.httpClient = httpClient;
        authUtils = AuthUtils.get(context,ACCOUNT_TYPE);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(httpClient)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String s) {
                        retrologger.debug(s);
                    }
                })
                .setServer(url)
                .build();
        authWebService = restAdapter.create(AuthWebService.class);

        List<Account> accounts = authUtils.getAccounts();

//        Set<String> accounts = authUtils.getUserName(ACCOUNT_TYPE);
//        String accountName = (accounts!=null && accounts.size()==1?(String)accounts.toArray()[0]:null);

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

    public static AuthManager init(Context context, String url, OkClient httpClient) {
        if (authManager == null){
            authManager = new AuthManager(context,url,httpClient);
        }
        else {
            logger.error("AuthManager already initialized");
        }
        return authManager;
    }

    private void getUserASync(String authToken) {
        try {
            authWebService.getUser(authToken,new Callback<Credentials>() {
                @Override
                public void success(final Credentials webUser, Response response) {
                    setUser(webUser);
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                if (retrofitError != null) logger.error("Failed to get User!",retrofitError);
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

//        Account account = authUtils.getAccount(ACCOUNT_TYPE,user.getEmailAddress());
//        AccountStorage.get(context).addAccount(account);

//        authUtils.addOrCreate(ACCOUNT_TYPE,user.getEmailAddress());
//         authUtils.setAccount(user.getEmailAddress());

        fireLoginListeners();
        return getUser();
    }

    protected
    PublicKey getServerKey(){
        logger.debug("getServerKey()");
        try {
            byte[] pubKey = authWebService.getPublicKey();
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
            byte[] pubKey = authWebService.getPublicKey();
            logger.debug("pubKey: " + String.valueOf(pubKey));
            PublicKey key = Crypto.pubKeyFromBytes(pubKey);

            Credentials loginCreds = new Credentials(email,username, password);
            loginCreds.encryptPassword(key);
            logger.debug("Login Creds: " + loginCreds);

            return setUser(authWebService.userSignUp(loginCreds));
        } catch (Exception e) {
            logger.error("Failed to signUp(" + username + ")", e);
        }

        return null;
    }

    public void signUpASync(final String email,final String username,final String password){
        logger.debug("signUpASync("+email+","+username+")");
        try {
            PublicKey key = Crypto.pubKeyFromBytes(authWebService.getPublicKey());

            Credentials loginCreds = new Credentials(email,username, password);
            loginCreds.encryptPassword(key);

            authWebService.userSignUp(loginCreds, new Callback<Credentials>() {
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
            PublicKey key = Crypto.pubKeyFromBytes(authWebService.getPublicKey());

            Credentials creds = new Credentials("",username, password);
            creds.encryptPassword(key);

            return setUser(authWebService.login(creds));
        }catch (Exception e){
            logger.error("Login Failure("+username+")",e);
            return null;
        }
    }

    public boolean loginASync(final String username,final String password){
        logger.debug("loginASync("+username+")");
        try{
            PublicKey key = Crypto.pubKeyFromBytes(authWebService.getPublicKey());

            Credentials loginCreds = new Credentials("",username, password);
            loginCreds.encryptPassword(key);

            authWebService.login(loginCreds, new Callback<Credentials>() {
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

    /*
     * Not actually used yet...but will be
     */
    private static BroadcastReceiver CONNECTION_RECIEVER = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

            if (activeNetInfo != null && activeNetInfo.getState() == NetworkInfo.State.CONNECTED) {
                logger.debug("Network " + activeNetInfo.getTypeName() + " connected");

            }

            if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                logger.debug("There's no network connectivity");

            }
        }
    };

}
