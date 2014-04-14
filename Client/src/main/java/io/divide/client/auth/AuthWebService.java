package io.divide.client.auth;

import io.divide.client.auth.credentials.LoginCredentials;
import io.divide.client.auth.credentials.SignUpCredentials;
import io.divide.client.auth.credentials.ValidCredentials;
import io.divide.shared.web.transitory.Credentials;
import retrofit.client.Response;
import retrofit.http.*;
import rx.Observable;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 7/27/13
 * Time: 8:16 PM
 */
public interface AuthWebService {

    @POST("/auth")
    public Response userSignUp(@Body SignUpCredentials credentials);

    @POST("/auth")
    public Observable<ValidCredentials> userSignUpA(@Body SignUpCredentials credentials);

    @PUT("/auth")
    public Response login(@Body LoginCredentials credentials);
    @PUT("/auth")
    public Observable<ValidCredentials> loginA(@Body LoginCredentials credentials);

    @GET("/auth/key")
    public Observable<byte[]> getPublicKeyA();

    @GET("/auth/key")
    public byte[] getPublicKey();

    @GET("/auth/from/{token}")
    public Observable<ValidCredentials> getUserFromAuthToken(@Path("token")String authToken);

    @GET("/auth/recover/{token}")
    public Observable<ValidCredentials> getUserFromRecoveryToken(@Path("token")String authToken);

    @POST("/auth/user/data")
    public Observable<Void> sendUserData(@Body Credentials credentials);

    @GET("/auth/user/data")
    public Observable<ValidCredentials> getUserData();

}
