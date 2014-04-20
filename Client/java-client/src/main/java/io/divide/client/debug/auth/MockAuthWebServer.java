package io.divide.client.debug.auth;

import com.google.inject.Inject;
import io.divide.client.auth.AuthManager;
import io.divide.client.auth.AuthWebService;
import io.divide.client.auth.credentials.LoginCredentials;
import io.divide.client.auth.credentials.SignUpCredentials;
import io.divide.client.auth.credentials.ValidCredentials;
import io.divide.client.debug.GsonResponse;
import io.divide.shared.server.AuthServerLogic;
import io.divide.shared.server.DAO;
import io.divide.shared.server.KeyManager;
import io.divide.shared.util.ObjectUtils;
import io.divide.shared.util.ReflectionUtils;
import io.divide.shared.web.transitory.Credentials;
import io.divide.shared.web.transitory.TransientObject;
import io.divide.shared.web.transitory.query.Query;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.Path;
import rx.Observable;
import rx.Subscriber;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;

/**
 * Created by williamwebb on 4/6/14.
 */
public class MockAuthWebServer implements AuthWebService{

    private AuthManager authManager;
    private DAO dao;
    private AuthServerLogic<TransientObject> authServerLogic;

    @Inject
    public MockAuthWebServer(AuthManager authManager, DAO<TransientObject,TransientObject> dao, KeyManager keyManager) {
        this.authManager = authManager;
        this.dao = dao;
        this.authServerLogic = new AuthServerLogic<TransientObject>(dao,keyManager);
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
    public Observable<Void> sendUserData(@Header("Authorization") String authToken, final @Body Credentials credentials) {
        verifyAuthToken(authToken);
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    System.err.println("Saving: " + credentials.getUserData());
                    authServerLogic.recieveUserData(authManager.getUser(), credentials.getUserData());
                    subscriber.onNext(null);
                } catch (DAO.DAOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<ValidCredentials> getUserData(@Header("Authorization") String authToken) {
        verifyAuthToken(authToken);
        return Observable.create(new Observable.OnSubscribe<ValidCredentials>() {
            @Override
            public void call(Subscriber<? super ValidCredentials> subscriber) {
                try {
                    Collection<TransientObject> x = dao.get(Query.safeTable(Credentials.class), authManager.getUser().getObjectKey());
                    TransientObject to = ObjectUtils.get1stOrNull(x);
                    if(to!=null){
                        subscriber.onNext(to(ValidCredentials.class,to));
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
        if(token == null ||
           token.length() == 0 || //TODO token should be fixed length, verify against this also
           authManager == null ||
           authManager.getUser() == null ||
           authManager.getUser().getAuthToken() == null ||
          !authManager.getUser().getAuthToken().equals(token)){
            throw new RuntimeException("");
        }
    }

    public <T extends TransientObject> T to(Class<T> type, TransientObject from){
        try {
            Constructor<T> constructor;
            constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);

            T t = constructor.newInstance();
            Map meta = (Map) ReflectionUtils.getObjectField(from, TransientObject.META_DATA);
            Map user = (Map) ReflectionUtils.getObjectField(from,TransientObject.USER_DATA);

            ReflectionUtils.setObjectField(t, TransientObject.META_DATA, meta);
            ReflectionUtils.setObjectField(t, TransientObject.USER_DATA, user);

            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
