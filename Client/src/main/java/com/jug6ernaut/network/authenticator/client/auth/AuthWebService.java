package com.jug6ernaut.network.authenticator.client.auth;

import com.jug6ernaut.network.authenticator.client.auth.credentials.LoginCredentials;
import com.jug6ernaut.network.authenticator.client.auth.credentials.SignUpCredentials;
import com.jug6ernaut.network.authenticator.client.auth.credentials.ValidCredentials;
import com.jug6ernaut.network.shared.web.transitory.Credentials;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.*;

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
    public void userSignUp(@Body SignUpCredentials credentials, Callback<ValidCredentials> callback);

    @PUT("/auth")
    public Response login(@Body LoginCredentials credentials);
    @PUT("/auth")
    public void login(@Body LoginCredentials credentials,Callback<ValidCredentials> callback);

    @GET("/auth/key")
    public byte[] getPublicKey();

    @GET("/auth/user/{token}")
    public ValidCredentials getUser(@Path("token")String authToken);

    @GET("/auth/user/{token}")
    public void getUser(@Path("token")String authToken, Callback<ValidCredentials> callback);

    @POST("/auth/user/data")
    public void sendUserData(@Body Credentials credentials);

    @POST("/auth/user/data")
    public void sendUserData(@Body Credentials credentials, Callback<String> callback);

    @GET("/auth/user/data")
    public void getUserData(Callback<ValidCredentials> callback);

    @GET("/auth/user/data")
    public ValidCredentials getUserData();
}
