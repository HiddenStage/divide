/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.divide.client;

import com.google.inject.Inject;
import io.divide.client.auth.AuthManager;
import io.divide.client.auth.SignInResponse;
import io.divide.client.auth.SignUpResponse;
import io.divide.client.auth.credentials.LocalCredentials;
import io.divide.client.auth.credentials.LoginCredentials;
import io.divide.client.auth.credentials.SignUpCredentials;
import io.divide.client.auth.credentials.ValidCredentials;
import io.divide.shared.logging.Logger;
import io.divide.shared.transitory.Credentials;
import rx.Observable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    /**
     * Convience method to access the authManager
     * @return authManager
     */

    private static AuthManager getAM(){
        if(authManager==null) throw new RuntimeException("Backend not initialized!");
        return authManager;
    }

    /**
     * Convience method to initialize BackendUser from ValidCredentials
     * @param credentials
     * @return
     */

    public static BackendUser from(ValidCredentials credentials){
        BackendUser beu = new BackendUser();
        beu.initFrom(credentials);
        return beu;
    }

    /**
     * @return Currently logged in user.
     */
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

    /**
     * Used to access the stored credentials.
     * @return Locally stored credentials for the logged in user.
     */
    public static LocalCredentials getStoredAccount(){
        return authManager.getStoredAccount();
    }

//    protected final Credentials getLoggedInUser(){
//        return getUser();
//    }

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

    /**
     * Login using an authtoken
     * @param token
     * @return BackendUser for the sent token.
     */
    public static BackendUser fromToken(String token){
        return getAM().getUserFromAuthToken(token).toBlockingObservable().first();
    }

    /**
     * Perform syncronously login attempt.
     * @param loginCredentials credentials used for login attempt.
     * @return login results.
     */
    public static SignInResponse signIn(LoginCredentials loginCredentials){
        return getAM().login(loginCredentials);
    }

    /**
     * Perform syncronously login attempt.
     * @param email user email address
     * @param password user password
     * @return login results.
     */
    public static SignInResponse signIn(String email, String password){
        return signIn(new LoginCredentials(email,password));
    }

    /**
     * Perform syncronously sign up attempt.
     * @param signUpCredentials credentials used for sign up attempt.
     * @return signup results.
     */
    public static SignUpResponse signUp(SignUpCredentials signUpCredentials){
        return getAM().signUp(signUpCredentials);
    }

    /**
     * Perform syncronously sign up attempt.
     * @param username user name user will be identified by.
     * @param email user email address
     * @param password user password
     * @return login results.
     */
    public static SignUpResponse signUp(String username, String email, String password){
        return signUp(new SignUpCredentials(username,email,password));
    }

    /**
     * Perform asyncronously login attempt.
     * @param email user email address
     * @param password user password
     * @return login results as observable.
     */
    public static Observable<BackendUser> logInInBackground(String email, String password){
        return getAM().loginASync(new LoginCredentials(email, password));
    }

    /**
     * Perform asyncronously sign up attempt.
     * @param username user name user will be identified by.
     * @param email user email address
     * @param password user password
     * @return login results as observable.
     */
    public static Observable<BackendUser> signUpInBackground(String username, String email, String password){
        return getAM().signUpASync(new SignUpCredentials(username, email, password));
    }

    public static void logout(){
        getAM().logout();
    }

    public void	requestPasswordReset(String email){
        throw new NotImplementedException();
    }
    public Observable<Boolean> requestPasswordResetInBackground(String email){
        throw new NotImplementedException();
    }

    /**
     * Synchronously sign up using credentials provided via constructor or setters.
     * @return boolean indicating sign up success
     */
    public boolean signUp(){
        SignUpCredentials creds = new SignUpCredentials(getUsername(),getEmailAddress(),getPassword());
        SignUpResponse response = getAM().signUp(creds);
        if(response.getStatus().isSuccess()){
            this.initFrom(response.get());
            return true;
        } else return false;
    }

    private void save(BackendUser user){
        getAM().sendUserData(user).subscribe();
    }

    private Observable<Void> saveASync(BackendUser user){
        return getAM().sendUserData(user);
    }

    /**
     *  Asynchronously saves this user object. Saving any data added via the put() method.
     *  put("1");
     *  put(1);
     *  etc.
     */
    public void save(){
        save(this);
    }

    /**
     *  Asynchronously saves this user object return an Observable to monitor. Saving any data added via the put() method.
     *  put("1");
     *  put(1);
     *  etc.
     *  @return Obserable to monitor operation.
     */
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
