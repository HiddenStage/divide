package io.divide.client.auth;


import io.divide.client.auth.credentials.LoginCredentials;
import io.divide.client.auth.credentials.SignUpCredentials;
import io.divide.client.auth.credentials.ValidCredentials;
import retrofit.client.Response;
import retrofit.http.*;
import rx.Observable;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 7/27/13
 * Time: 8:16 PM
 *
 * Interface defining relationship with server.
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

    @POST("/auth/user/data/{userId}")
    public Observable<Void> sendUserData(@Header("Authorization") String authToken, @Path("userId") String userId, @Body Map<String,?> map);

    @GET("/auth/user/data/{userId}")
    public Observable<Map<String,Object>> getUserData(@Header("Authorization") String authToken, @Path("userId") String userId);

}
