package io.divide.client;

import com.google.inject.Inject;
import io.divide.shared.logging.Logger;
import io.divide.client.auth.AuthManager;
import io.divide.client.auth.SignInResponse;
import io.divide.client.auth.SignUpResponse;
import io.divide.client.auth.credentials.LocalCredentials;
import io.divide.client.auth.credentials.LoginCredentials;
import io.divide.client.auth.credentials.SignUpCredentials;
import io.divide.client.auth.credentials.ValidCredentials;
import io.divide.client.data.Callback;
import io.divide.shared.web.transitory.Credentials;
import rx.Observable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/24/13
 * Time: 9:26 PM
 */
public final class BackendUser extends Credentials {

    private static Logger logger = Logger.getLogger(BackendUser.class);
    @Inject private static AuthManager authManager;

    private static final String ANONYMOUS_KEY = "anonymous_key";

    public BackendUser(){ }

    public BackendUser(String username, String email, String password){
        this();
        this.setUsername(username);
        this.setEmailAddress(email);
        this.setPassword(password);
    }

    private static AuthManager getAM(){
        if(authManager==null) throw new RuntimeException("Backend not initialized!");
        return authManager;
    }

//    private static Context getApp(){
//        if(app==null) throw new RuntimeException("Backend not initialized!");
//        return app;
//    }

    public static BackendUser from(ValidCredentials credentials){
        BackendUser beu = new BackendUser();
        beu.initFrom(credentials);
        return beu;
    }

    public static BackendUser getUser(){
        return getAM().getUser();
    }

//    public static ServerResponse<BackendUser> getAnonymousUser(){
//        String id = UserUtils.getDeviceIdUnique(Backend.get().app);
//        ServerResponse<BackendUser> response;
//
//        response = signIn(id, id);
//        if(!response.getStatus().isSuccess()){
//            response = signUp(id, id, id);
//            if(response.getStatus().isSuccess()){
//                BackendUser user = response.get();
//                user.put(ANONYMOUS_KEY,true);
//                user.saveASync();
//            }
//        }
//
//        return response;
//    }

    public static LocalCredentials getStoredAccount(){
        return authManager.getStoredAccount();
    }

    protected final Credentials getLoggedInUser(){
        return getUser();
    }

    @Override
    protected final boolean isSystemUser(){
        return false;
    }

    private void initFrom(Credentials credentials){
        logger.debug("initFrom: " + credentials);
        try {
            this.meta_data = credentials.getMetaData();
            this.user_data = credentials.getUserData();
        } catch (Exception e) {
            logger.error("Failed to init BackendUser from Credentials",e);
        }
    }

    public static BackendUser fromToken(String token){
        return getAM().getUserFromAuthToken(token).toBlockingObservable().first();
    }

    public static SignInResponse signIn(LoginCredentials loginCredentials){
        return getAM().login(loginCredentials);
    }

    public static SignInResponse signIn(String email, String password){
        return signIn(new LoginCredentials(email,password));
    }

    public static SignUpResponse signUp(SignUpCredentials signUpCredentials){
        return getAM().signUp(signUpCredentials);
    }

    public static SignUpResponse signUp(String username, String email, String password){
        return signUp(new SignUpCredentials(username,email,password));
    }

    public static Observable<BackendUser> logInInBackground(String email, String password){
        return getAM().loginASync(new LoginCredentials(email, password));
    }

    public static Observable<BackendUser> signUpInBackground(String username, String email, String password){
        return getAM().signUpASync(new SignUpCredentials(username, email, password));
    }

    public static void logout(){
        getAM().logout();
    }

    public void	requestPasswordReset(String email){
        throw new NotImplementedException();
    }
    public void	requestPasswordResetInBackground(String email, Callback<Boolean> callback){
        throw new NotImplementedException();
    }

    public Observable<BackendUser> signUpA(){
        SignUpCredentials creds = new SignUpCredentials(getUsername(),getEmailAddress(),getPassword());
        return getAM().signUpASync(creds);
    }

    public boolean signUp(){
        SignUpCredentials creds = new SignUpCredentials(getUsername(),getEmailAddress(),getPassword());
        SignUpResponse response = getAM().signUp(creds);
        if(response.getStatus().isSuccess()){
            this.initFrom(response.get());
            return true;
        } else return false;
    }

    private void save(BackendUser object){
        getAM().sendUserData(object).subscribe();
    }

    private Observable<Void> saveASync(BackendUser user){
        return getAM().sendUserData(user);
    }

    public void save(){
        save(this);
    }

    public Observable<Void> saveASync(){
        return saveASync(this);
    }

    @Override
    public String toString() {
        return "BackendUser{" +
                "emailAddress='" + getEmailAddress() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", userKey='" + getOwnerId() + '\'' +
                ", authToken='" + getAuthToken() + '\'' +
                ", createDate=" + getCreateDate() +
//                ", authTokenExpireDate=" + getAuthTokenExpireDate() +
                ", validation='" + getValidation() + '\'' +
                '}';
    }

}
