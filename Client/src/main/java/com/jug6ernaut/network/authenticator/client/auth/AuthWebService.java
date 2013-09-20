package com.jug6ernaut.network.authenticator.client.auth;

import com.jug6ernaut.network.shared.web.transitory.Credentials;
import retrofit.Callback;
import retrofit.http.*;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 7/27/13
 * Time: 8:16 PM
 */
public interface AuthWebService {

    @POST("/auth")
    public Credentials userSignUp(@Body Credentials credentials);
    @POST("/auth")
    public void userSignUp(@Body Credentials credentials, Callback<Credentials> callback);

    @PUT("/auth")
    public Credentials login(@Body Credentials credentials);
    @PUT("/auth")
    public void login(@Body Credentials credentials,Callback<Credentials> callback);

    @GET("/auth/key")
    public byte[] getPublicKey();

    @GET("/auth/user/{token}")
    public Credentials getUser(@Path("token")String authToken);

    @GET("/auth/user/{token}")
    public void getUser(@Path("token")String authToken,Callback<Credentials> callback);
}
