/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.divide.client.auth;

import com.google.inject.Inject;
import io.divide.client.GsonResponse;
import io.divide.client.auth.credentials.LoginCredentials;
import io.divide.client.auth.credentials.SignUpCredentials;
import io.divide.client.auth.credentials.ValidCredentials;
import io.divide.shared.logging.Logger;
import io.divide.shared.server.AuthServerLogic;
import io.divide.shared.server.DAO;
import io.divide.shared.server.KeyManager;
import io.divide.shared.transitory.Credentials;
import io.divide.shared.transitory.TransientObject;
import io.divide.shared.util.ObjectUtils;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.Path;
import rx.Observable;
import rx.Subscriber;

import java.util.Map;

import static io.divide.shared.util.DaoUtils.to;

public class MockAuthWebService implements AuthWebService{

    private static Logger logger = Logger.getLogger(MockAuthWebService.class);
    private AuthServerLogic<TransientObject> authServerLogic;
    private AuthManager authManager;

    @Inject
    public MockAuthWebService(DAO<TransientObject, TransientObject> dao, KeyManager keyManager) {
        this.authServerLogic = new AuthServerLogic<TransientObject>(dao,keyManager);
    }

    // must be done after initialization to prevent cyclic referencing
    public void setAuthManger(AuthManager authManager){
        this.authManager = authManager;
    }

    @Override
    public Response userSignUp(@Body SignUpCredentials credentials) {
        try {
            Credentials c = authServerLogic.userSignUp(credentials);

            return new GsonResponse("",200,"",null, c).build();
        } catch (DAO.DAOException e) {
            return new GsonResponse("",e.getStatusCode(),e.getMessage(), null, null).build();
        }
    }

    @Override
    public Observable<ValidCredentials> userSignUpA(@Body SignUpCredentials credentials) {
        try {
            return Observable.from(to(ValidCredentials.class, authServerLogic.userSignUp(credentials)));
        } catch (DAO.DAOException e) {
            return Observable.error(e);
        }
    }

    @Override
    public Response login(@Body LoginCredentials credentials) {
        try{
            Credentials dbCreds = authServerLogic.userSignIn(credentials);
            return new GsonResponse("",200,"",null, dbCreds).build();
        }catch(DAO.DAOException e) {
            return new GsonResponse("",e.getStatusCode(),e.getMessage(), null, null).build();
        }
    }

    @Override
    public Observable<ValidCredentials> loginA(@Body LoginCredentials credentials) {
        try {
            return Observable.from(to(ValidCredentials.class, authServerLogic.userSignIn(credentials)));
        } catch (DAO.DAOException e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable<byte[]> getPublicKeyA() {
        return Observable.from(authServerLogic.getPublicKey());
    }

    @Override
    public byte[] getPublicKey() {
        return authServerLogic.getPublicKey();
    }

    @Override
    public Observable<ValidCredentials> getUserFromAuthToken(@Path("token") String authToken) {
        try {
            return Observable.from(to(ValidCredentials.class, authServerLogic.getUserFromAuthToken(authToken)));
        } catch (DAO.DAOException e) {
            return Observable.error(e);
        }
    }

    @Override
    public Observable<ValidCredentials> getUserFromRecoveryToken(final @Path("token") String authToken) {
        return Observable.create(new Observable.OnSubscribe<ValidCredentials>() {
            @Override
            public void call(Subscriber<? super ValidCredentials> subscriber) {
                try {
                    subscriber.onNext(to(ValidCredentials.class, authServerLogic.getUserFromRecoveryToken(authToken)));
                } catch (DAO.DAOException e) {
                    System.out.println(e.getStatusCode() + " : " + e.getMessage());
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Void> sendUserData(@Header("Authorization") String authToken, @Path("userId") final String userId, @Body final Map<String, ?> data) {
        verifyAuthToken(authToken);
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    System.err.println("Saving: " + userId + ", " + data);
                    authServerLogic.recieveUserData(userId, data);
                    subscriber.onNext(null);
                } catch (DAO.DAOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<Map<String,Object>> getUserData(@Header("Authorization") final String authToken, final String userId) {
        verifyAuthToken(authToken);
        return Observable.create(new Observable.OnSubscribe<Map<String,Object>>() {
            @Override
            public void call(Subscriber<? super Map<String,Object>> subscriber) {
                try {
                    Credentials x = authServerLogic.getUserById(userId);
                    TransientObject to = ObjectUtils.get1stOrNull(x);
                    if(to!=null){
                        subscriber.onNext(authServerLogic.sendUserData(userId));
                    } else {
                        subscriber.onNext(null);
                    }
                } catch (DAO.DAOException e) {
                    subscriber.onError(e);
                }
            }
        });

    }

    public void verifyAuthToken(String token){
        System.out.println("MockAuthWebService: " + token);
        token = token.substring("CUSTOM ".length(),token.length());
        if(token == null ||
           token.length() == 0 || //TODO token should be fixed length, verify against this also
           authManager == null ||
           authManager.getUser() == null ||
           authManager.getUser().getAuthToken() == null ||
          !authManager.getUser().getAuthToken().equals(token)){
            throw new RuntimeException("");
        }
    }

}
